# Text Studio 1.2.2 — QA report

Prepared source. Parent: PR #1 head 415c2ea10718cde81c859ff419e5023f02becefc (V1.2.1, successful PR run #23). Main at preparation: 601c0186704e41498763d3c32cc2dd32cb3612b8. All 16 files in previous full app source ZIP matched PR Git blob SHA; two unrelated tracked fix/ files are preserved by overlay and excluded from the app package.

Browser QA using Chromium simulated NativeText bridge, 38/38 passed:
- PASS: no trailing command descriptions
- PASS: startup and no JS errors
- PASS: light theme
- PASS: no horizontal overflow at 412dp
- PASS: dirty indicator
- PASS: immediate undo
- PASS: redo
- PASS: find count
- PASS: replace all
- PASS: go line
- PASS: select all
- PASS: copy bridge
- PASS: cut
- PASS: paste
- PASS: time/date insert
- PASS: zoom
- PASS: reset zoom
- PASS: read-only
- PASS: wrap
- PASS: status hidden
- PASS: details
- PASS: external open
- PASS: accept open
- PASS: native overwrite request
- PASS: save acknowledgement
- PASS: native save-as fallback
- PASS: dark mode
- PASS: compact keyboard viewport
- PASS: no runtime errors
- PASS: about opens from settings
- PASS: about dynamic metadata
- PASS: about no GitHub or links
- PASS: about own brand
- PASS: about compact viewport
- PASS: about close
- PASS: about native back
- PASS: about guards external open
- PASS: no runtime errors after about

Node JS syntax and workflow YAML parsed. Packaging helper tested against mock apksigner/aapt output: correct name and bytes, no overwrite, mismatched ID rejected, missing debug flag rejected, failed signer rejected (5/5). This is not real APK verification. Actual Android compile, APK signing/certificate continuity, permissions and device acceptance remain pending. No additional dependencies or signing changes; AndroidManifest unchanged.

Standard: MAS-STD-009-ID V1.2 / Hybrid Policy V1.4. About omits GitHub, reads version/code from Android package metadata; APK name TextStudio-v1.2.2.apk; debug/testing remains in CI summary.
