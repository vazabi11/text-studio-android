# Text Studio changelog

## 1.2.1 (versionCode 4) — Prepared source
- Remove all trailing descriptions and shortcut hints from File / Edit / View command menus. Keep command names, touch targets and toggle checkmarks.
- Use independent Text Studio branding. No Microsoft assets imported.
- Retain existing external file MIME registration and URI handling unchanged.
- Android build and device acceptance pending.

## 1.2 (versionCode 3) — Prepared source
- Mobile-first Notepad-inspired File / Edit / View menus and 48dp quick actions.
- Light / dark / system theme; compact line/column, zoom, EOL and encoding status.
- Android clipboard commands, selection, date/time, numeric Go to line, font family and zoom, spellcheck option (platform dependent), optional details/status.
- Retains V1.1 external content URI handling, MIME registration, Save As fallback, Markdown preview and encoding conversion.
- Single-document editor; multi-tab session, recent files, printing and AI functions not included in this revision.
- No Android build or device acceptance yet.

## 1.1 (versionCode 2) — Build passed — GitHub Actions #22; device acceptance not confirmed
- Android ACTION_VIEW / ACTION_EDIT registration for supported text MIME types and content URIs.
- Initial launch and singleTop external file delivery after editor readiness.
- Read-only URI grants use Save As; writable grants permit ordinary Save subject to provider permissions.
- External open checks unsaved changes and pending dialogs; 5 MiB limit retained.
- Save As advertises a text MIME type based on the file extension.
- Retains V1.0 Java share lambda fix, SDK setup workflow V1.3, application ID and existing UI.
- No broad storage permission, binary catch-all, or file:// handler. Generic MIME or legacy providers may require in-app Open.

## 1.0
- Initial editor; GitHub Actions runs #16 and #17 succeeded at 477c33d.
