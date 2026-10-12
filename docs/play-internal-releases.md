# Publish to Google Play internal testing

The **Publish Android to Play internal testing** workflow builds a signed APK and AAB,
uploads the AAB and R8 mapping to `com.charan.yourday` on the `internal` track, then
checks the committed track and bundle SHA-256 through the Google Play Developer API.
The workflow fails if the release is a draft, on a different track, or contains a different bundle.

## Run a release

In GitHub, open **Actions > Publish Android to Play internal testing > Run workflow**,
select `master`, and enter a version name such as `0.0.2`. Alternatively, push a version
tag such as `v0.0.2`; this also creates a GitHub release with the APK and AAB.

```sh
gh workflow run android-release.yml --ref master -f version_name=0.0.2
```

The version code exceeds the codes returned by Play for bundles, APKs and all tracks,
with the workflow run number as a lower bound. Releases are serialized and running
uploads are never canceled by a subsequent release. Local builds retain their default
version unless `-PversionCode` and `-PversionName` are supplied.

The Actions summary reports the verified version code and hash. Run artifacts include
the signed APK, AAB, mapping and `play-release-verification.json`. Play may take a few
minutes to make a completed release installable by testers.

## Repository secrets

| Secret | Purpose |
| --- | --- |
| `SERVICE_ACCOUNT_JSON` | Raw Google Play service-account key JSON |
| `KEYSTORE_BASE64` | Base64-encoded Your Day upload keystore |
| `KEY_STORE_PASSWORD` or `KEYSTORE_PASSWORD` | Upload keystore password |
| `KEY_ALIAS` | Upload key alias |
| `KEY_PASSWORD` | Upload key password |
| `GOOGLE_SERVICES_JSON_BASE64` | Base64 Firebase config including `com.charan.yourday` |
| `API_KEY` | Weather API key |
| `TODOIST_CLIENT_ID` | Todoist OAuth client ID |
| `TODOIST_CLIENT_SECRET` | Todoist OAuth client secret |

Enable the Google Play Android Developer API for the service account's Cloud project.
In Play Console **Users and permissions**, grant that service account access to
Your Day, including viewing app information and releasing to testing tracks.

```sh
gh secret set SERVICE_ACCOUNT_JSON --repo sai0x20charan/YourDay < /path/to/service-account.json
```

For a new listing, upload the first signed AAB in Play Console and finish Play App
Signing and the initial release setup before using the API. The signing certificate
must match the upload key registered for Your Day. A draft app may require its first
internal rollout through Play Console. The workflow requires a completed release
and does not silently leave a draft as a successful publication.

The workflow publishes to internal testing only. Internal testers must be on the
listing's tester list and use its opt-in link. Store metadata and production rollout
are managed in Play Console separately.
