# Text Studio changelog

## 1.1 (versionCode 2) — Prepared source, not built
- Android ACTION_VIEW / ACTION_EDIT registration for supported text MIME types and content URIs.
- Initial launch and singleTop external file delivery after editor readiness.
- Read-only URI grants use Save As; writable grants permit ordinary Save subject to provider permissions.
- External open checks unsaved changes and pending dialogs; 5 MiB limit retained.
- Save As advertises a text MIME type based on the file extension.
- Retains V1.0 Java share lambda fix, SDK setup workflow V1.3, application ID and existing UI.
- No broad storage permission, binary catch-all, or file:// handler. Generic MIME or legacy providers may require in-app Open.

## 1.0
- Initial editor; GitHub Actions runs #16 and #17 succeeded at 477c33d.
