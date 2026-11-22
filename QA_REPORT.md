# Text Studio 1.2.3 — QA report

Prepared source based on merged main c8e7bf82429caf30cb7b0757a1708939313850b3 (V1.2.2, run #27 success). All 17 archive files matched GitHub blob SHA before editing. Two unrelated tracked fix/ files remain untouched.

Chromium simulated NativeText bridge: 49/49 checks passed.
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
- PASS: settings close remains visible when scrolled
- PASS: settings X closes settings only
- PASS: main X clean exit
- PASS: File Exit clean exit
- PASS: main X cancel retains text
- PASS: File Exit cancel retains text
- PASS: Android back cancel
- PASS: explicit discard approval
- PASS: pending save blocks exit
- PASS: exit after save cancellation
- PASS: no runtime errors after exit checks

Native Activity lifecycle, actual Android build, signature and device acceptance are NOT tested. Existing workflow, manifest, application ID, signing configuration and APK naming helper unchanged. Exit closes Activity, not force-stop; dirty edits require explicit discard confirmation; pending saves block exit; opt-in draft is flushed on exit (real Android storage not tested).
