# MPR SQUAD™ DWD — Daily Work Details

Native Android application project (Kotlin + Jetpack Compose).

## Status
This repository is the initial project foundation, not a completed application or release APK. Features must be implemented and tested before release.

## Planned modules
- Duty records
- Bills and transport/meal expenses
- Expenses
- Person-based accounts (excluded from general dashboard totals and general PDFs)
- Location-based accounts
- Dashboard and date-filtered reports
- Office contract
- Stock inventory
- Settings and reminders
- Offline database, backup and restore
- Contextual PDF export and app sharing

## Build
The Android project will use Gradle and GitHub Actions. The APK produced by CI is a build artifact and must be installed and tested on a physical Android device before claiming release readiness.

## Privacy
This is a public repository. Do not commit real personal records, secrets, access tokens, private backups, signing keys, or credentials. Review portrait/contact information before publishing assets.

## Release checklist
- [ ] All modules implemented
- [ ] Offline persistence and restore tested
- [ ] PDF exports verified
- [ ] Person-account isolation verified
- [ ] Accessibility, crash and security tests passed
- [ ] APK installed and tested on Android phone
