import importlib.util
from pathlib import Path
import unittest

spec = importlib.util.spec_from_file_location("play_release", Path(__file__).with_name("play_release.py"))
play = importlib.util.module_from_spec(spec)
spec.loader.exec_module(play)


class PlayReleaseTests(unittest.TestCase):
    def test_version_code_exceeds_bundles_apks_and_all_tracks(self):
        tracks = [{"releases": [{"versionCodes": ["42"]}]}]
        self.assertEqual(play.next_version_code([{"versionCode": 15}], [{"versionCode": 28}], tracks, 7), 43)

    def test_version_code_uses_run_number_as_floor(self):
        self.assertEqual(play.next_version_code([], [], [], 100), 100)

    def test_version_code_rejects_exhausted_range(self):
        with self.assertRaises(ValueError):
            play.next_version_code([{"versionCode": 2_100_000_000}], [], [], 1)

    def test_verification_accepts_exact_completed_bundle(self):
        track = {"track": "internal", "releases": [{"status": "completed", "versionCodes": ["43"]}]}
        play.verify_release(track, [{"versionCode": 43, "sha256": "abc"}], 43, "abc")

    def test_verification_rejects_draft_wrong_track_wrong_version_and_wrong_bundle(self):
        for track in [
            {"track": "internal", "releases": [{"status": "draft", "versionCodes": ["43"]}]},
            {"track": "production", "releases": [{"status": "completed", "versionCodes": ["43"]}]},
            {"track": "internal", "releases": [{"status": "completed", "versionCodes": ["42"]}]},
        ]:
            with self.subTest(track=track), self.assertRaises(ValueError):
                play.verify_release(track, [{"versionCode": 43, "sha256": "abc"}], 43, "abc")
        track = {"track": "internal", "releases": [{"status": "completed", "versionCodes": ["43"]}]}
        for bundles in [[], [{"versionCode": 43, "sha256": "wrong"}]]:
            with self.subTest(bundles=bundles), self.assertRaises(ValueError):
                play.verify_release(track, bundles, 43, "abc")


if __name__ == "__main__":
    unittest.main()
