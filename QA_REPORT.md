# Text Studio 1.2.1 — QA report

Prepared source; base GitHub main 601c0186704e41498763d3c32cc2dd32cb3612b8 (V1.1). No Android build or real device test.

Removed 24 trailing descriptions/keyboard hints. Node syntax and manifest XML parse passed; manifest unchanged. Chromium simulated NativeText bridge 29/29 passed. Tests:
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

No Microsoft fonts, logos or screenshots are bundled in this source tree. System font stack and project vector artwork retained. This is not legal clearance.

Open with: ACTION_VIEW / ACTION_EDIT, CATEGORY_DEFAULT, content URI and text/* plus specific structured-text MIME types. Real chooser/default association depends on sender MIME and Android resolver. Generic octet-stream and file:// not registered. Device acceptance, signing, update compatibility and actual file provider grants remain unverified.
