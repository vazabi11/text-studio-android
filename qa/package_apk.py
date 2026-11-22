"""Verify the built debug APK, then package it using actual version metadata."""
import hashlib
import os
import re
import shutil
import subprocess
from pathlib import Path


def one(pattern, text, label):
    values = re.findall(pattern, text, re.MULTILINE)
    if len(values) != 1:
        raise SystemExit(f"Cannot verify {label}: expected one value, got {len(values)}")
    return values[0]


def package():
    gradle = Path("app/build.gradle").read_text(encoding="utf-8")
    expected = (
        one(r"^\s*versionName\s+'([^']+)'\s*$", gradle, "versionName"),
        one(r"^\s*versionCode\s+(\d+)\s*$", gradle, "versionCode"),
        one(r"^\s*applicationId\s+'([^']+)'\s*$", gradle, "applicationId"),
    )
    apk = Path("app/build/outputs/apk/debug/app-debug.apk")
    if not apk.is_file() or apk.stat().st_size == 0:
        raise SystemExit("Missing APK")
    tools = Path(os.environ["ANDROID_HOME"]) / "build-tools/34.0.0"
    cert = subprocess.check_output([str(tools / "apksigner"), "verify", "--verbose", "--print-certs", str(apk)], text=True, stderr=subprocess.STDOUT)
    print(cert)
    fingerprints = re.findall(r"^Signer #\d+ certificate SHA-256 digest: ([0-9a-fA-F]{64})\s*$", cert, re.MULTILINE)
    if not fingerprints:
        raise SystemExit("No signer certificate fingerprint reported")
    badging = subprocess.check_output([str(tools / "aapt"), "dump", "badging", str(apk)], text=True)
    metadata = one(r"^package: (.+)$", badging, "package line")
    def field(name):
        return one(r"(?:^|\s)" + name + r"='([^']+)'", metadata, name)
    actual = (field("versionName"), field("versionCode"), field("name"))
    if actual != expected or not re.fullmatch(r"[0-9]+(?:\.[0-9]+){2}", actual[0]):
        raise SystemExit(f"APK metadata mismatch/unsafe version: {actual}, expected {expected}")
    if not re.search(r"^application-debuggable\s*$", badging, re.MULTILINE):
        raise SystemExit("Expected a debug APK")
    version, code, app_id = actual
    name = "TextStudio-v" + version
    dest = Path("dist") / (name + ".apk")
    dest.parent.mkdir(exist_ok=True)
    if dest.exists():
        raise SystemExit("Destination already exists: refusing overwrite")
    shutil.copyfile(apk, dest)
    sha = hashlib.sha256(dest.read_bytes()).hexdigest()
    summary = (f"## Text Studio APK\n- File: `{dest.name}`\n- Version: `{version}` (code `{code}`)\n"
               f"- Application ID: `{app_id}`\n- Build type: Debug / testing only\n"
               f"- Commit: `{os.environ.get('GITHUB_SHA', 'unknown')}`\n- SHA-256: `{sha}`\n"
               f"- Certificate SHA-256: `{', '.join(fingerprints)}`\n"
               "- Signing continuity and device acceptance: not verified\n")
    with open(os.environ["GITHUB_OUTPUT"], "a", encoding="utf-8") as out:
        out.write(f"artifact_name={name}\napk_path={dest.as_posix()}\napk_sha256={sha}\n")
    with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as out:
        out.write(summary)
    print(summary)


if __name__ == "__main__":
    package()
