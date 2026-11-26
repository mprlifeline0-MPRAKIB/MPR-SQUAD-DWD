# Red Team Review — MPR SQUAD DWD

Review date: 2026-11-26
Scope: current repository contents on `main` only.
Review type: preliminary static review. No APK was available, so this is not a runtime penetration test, device test, or certification.

## Executive result

**Release decision: BLOCKED.** The current app is a UI scaffold. The six home cards open placeholder text; there is no implemented business workflow, persistent database, PDF export, backup/restore, reminder, account isolation, or app-sharing feature. These missing features cannot yet be penetration-tested.

## Findings

### RT-001 — Critical / release blocker: core features are not implemented
Evidence: `MainActivity.kt` displays a placeholder message after selecting a module. Room dependencies are declared but no entities, DAO, database, repositories, or migrations exist.
Risk: users cannot create or preserve work records; totals and reports do not exist.
Required remediation: implement each module and persistence, then add functional and data-integrity tests.

### RT-002 — High / release blocker: personal-account isolation is not implemented
Evidence: no personal-account data model or dashboard/PDF query filtering exists.
Risk: the requirement that personal accounts be excluded from general totals and PDFs is not enforceable or testable yet.
Required remediation: separate account types and enforce exclusion in repository/query/report layers, with automated regression tests.

### RT-003 — High / release blocker: backup, restore and PDF surfaces are absent
Evidence: no backup/export/restore or PDF implementation exists.
Risk: data loss and unintended disclosure cannot be assessed.
Required remediation: implement validated backup format, restore confirmation/rollback, and contextual PDF generation; test malformed and tampered backups and privacy boundaries.

### RT-004 — Medium: exit confirmation previously did not exit
Evidence: the “Yes” button only dismissed the dialog.
Status: fixed in the current branch by calling Activity.finish() from the confirmation action. Must still be verified in a device test.

### RT-005 — Low: no unnecessary Android permissions in current manifest
Evidence: current manifest declares no dangerous permissions, exported launcher activity only.
Status: positive observation for this scaffold. Reassess when file sharing, notifications, image selection, or backup features are added. Prefer Android Sharesheet and Storage Access Framework rather than broad storage permissions.

### RT-006 — Medium: public repository exposure
Evidence: repository visibility is public.
Risk: any committed personal portrait/contact details, backups, tokens, signing keys, or real user data would be publicly accessible.
Required remediation: keep secrets and real records out of Git; review branding assets and contact information before committing.

## Security controls added / planned

- Add GitHub CodeQL static analysis workflow for Kotlin/Java.
- Keep workflow permissions least-privilege.
- Build from source in CI; do not treat a successful compile as proof of feature completeness or security.
- Before release: dependency review, static analysis, unit/instrumentation tests, offline and persistence tests, backup tamper tests, privacy isolation tests, and physical Android device validation.

## Test status

- Source inspection: completed for current scaffold files.
- Runtime APK analysis: not run (no APK artifact available).
- Physical Android test: not run.
- Penetration test: not run.
- CodeQL: workflow added; result pending a GitHub Actions run.

## Exit criteria

Do not label the app complete or release-ready until all release blockers are fixed, automated tests pass, CodeQL is clean or findings are triaged, and the APK is installed and exercised on a physical Android phone.
