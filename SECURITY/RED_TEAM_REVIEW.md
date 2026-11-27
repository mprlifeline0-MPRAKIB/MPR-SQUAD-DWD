# Privacy and Release Check — MPR SQUAD DWD

Updated: 2026-11-27
Scope: personal, offline-first use by the owner, with APK sharing to other people.

## User's practical privacy requirement

The owner wants each person who installs a shared APK to use the app independently. Another install must not receive or see the owner's records. This is a data-separation requirement, not a request for enterprise-grade security or a formal penetration-test certification.

## Required implementation

1. Store operational records in the app's private on-device database (Room).
2. Do not sync records to a server or embed real records, backups, tokens, or signing keys in the APK or public Git repository.
3. A fresh install on another person's phone starts with an empty, separate local database. Sharing the APK shares the application only, not the owner's app data.
4. Keep backup/export as an explicit user action. Never silently include the owner's records when sharing the APK.
5. If import/restore is added, make it a deliberate action with a clear confirmation so the recipient imports only a backup they choose.
6. Keep personal-account transactions separate from work records; exclude them from work Dashboard totals and general work PDFs as requested.

## Proportionate checks before delivery

- Verify app data is stored in app-private local storage.
- Verify no cloud sync or automatic data upload exists.
- Verify APK sharing sends the APK only, not the database or backup.
- Verify a clean install starts with no pre-existing records.
- Verify personal transactions stay out of work totals and general work reports.
- Run Android build and basic app/data-flow tests; fix release-blocking defects.

## Out of scope unless later requested

- Formal penetration-test certification, enterprise threat modelling, complex account/login systems, and cloud security infrastructure.

## Current status

This document defines the practical privacy target. It does not claim that all checks have passed. The app still needs the remaining modules and the checks above before it can be called ready for everyday use.
