#!/usr/bin/env python3
"""Choose a new Play version code and verify the committed internal release."""

import argparse
from contextlib import contextmanager
import hashlib
import json
import os
from pathlib import Path
import sys

from google.oauth2 import service_account
from google.auth.transport.requests import AuthorizedSession


PACKAGE = "com.charan.yourday"
SCOPE = "https://www.googleapis.com/auth/androidpublisher"


def next_version_code(bundles, apks, tracks, minimum):
    codes = [int(item["versionCode"]) for item in bundles + apks]
    codes.extend(
        int(code)
        for track in tracks
        for release in track.get("releases", [])
        for code in release.get("versionCodes", [])
    )
    code = max(max(codes, default=0) + 1, minimum)
    if not 1 <= code <= 2_100_000_000:
        raise ValueError("Version code must be between 1 and 2100000000")
    return code


def verify_release(track, bundles, code, sha256):
    active = any(
        release.get("status") == "completed"
        and str(code) in release.get("versionCodes", [])
        for release in track.get("releases", [])
    )
    if track.get("track") != "internal" or not active:
        raise ValueError(f"Version {code} is not completed on the internal track")
    bundle = next((item for item in bundles if int(item["versionCode"]) == code), None)
    if bundle is None or bundle.get("sha256", "").lower() != sha256.lower():
        raise ValueError("Google Play's bundle hash does not match the built AAB")


class PlayClient:
    def __init__(self, credentials_file):
        credentials = service_account.Credentials.from_service_account_file(
            credentials_file, scopes=[SCOPE]
        )
        self.session = AuthorizedSession(credentials)
        self.base = f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE}/edits"

    def request(self, method, url, **kwargs):
        response = self.session.request(method, url, timeout=60, **kwargs)
        if not response.ok:
            try:
                message = response.json()["error"]["message"]
            except (ValueError, KeyError):
                message = response.reason
            raise RuntimeError(f"Google Play API {response.status_code}: {message}")
        return response.json() if response.content else {}

    @contextmanager
    def edit(self):
        edit = self.request("POST", self.base, json={})
        url = f"{self.base}/{edit['id']}"
        try:
            yield url
        finally:
            self.request("DELETE", url)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("command", choices=["prepare", "verify"])
    parser.add_argument("--service-account", required=True)
    parser.add_argument("--minimum-version-code", type=int, default=1)
    parser.add_argument("--version-code", type=int)
    parser.add_argument("--bundle", type=Path)
    parser.add_argument("--report", type=Path)
    args = parser.parse_args()
    if args.command == "verify" and (args.version_code is None or args.bundle is None):
        parser.error("verify requires --version-code and --bundle")

    client = PlayClient(args.service_account)
    with client.edit() as edit:
        bundles = client.request("GET", edit + "/bundles").get("bundles", [])
        if args.command == "prepare":
            apks = client.request("GET", edit + "/apks").get("apks", [])
            tracks = client.request("GET", edit + "/tracks").get("tracks", [])
            code = next_version_code(bundles, apks, tracks, args.minimum_version_code)
            print(f"Next version code for {PACKAGE}: {code}")
            if os.environ.get("GITHUB_OUTPUT"):
                with open(os.environ["GITHUB_OUTPUT"], "a") as output:
                    output.write(f"version_code={code}\n")
        else:
            track = client.request("GET", edit + "/tracks/internal")
            sha256 = hashlib.sha256(args.bundle.read_bytes()).hexdigest()
            verify_release(track, bundles, args.version_code, sha256)
            report = {"packageName": PACKAGE, "track": "internal", "versionCode": args.version_code,
                      "status": "completed", "sha256": sha256, "release": track}
            if args.report:
                args.report.write_text(json.dumps(report, indent=2) + "\n")
            print(f"Verified {PACKAGE} version {args.version_code} on internal, status completed, SHA-256 {sha256}")
            if os.environ.get("GITHUB_STEP_SUMMARY"):
                with open(os.environ["GITHUB_STEP_SUMMARY"], "a") as summary:
                    summary.write(f"## Verified Play internal release\n\nPackage: `{PACKAGE}`\n\n"
                                  f"Version code: `{args.version_code}`\n\nStatus: `completed`\n\n"
                                  f"Bundle SHA-256: `{sha256}`\n")


if __name__ == "__main__":
    try:
        main()
    except (RuntimeError, ValueError) as error:
        print(str(error), file=sys.stderr)
        sys.exit(1)
