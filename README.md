# VitaCare

**VitaCare** is an Android app that helps people find doctors by specialty, check
live hospital bed and ICU availability, and reach volunteer blood donors.
Doctors verify a licence, publish a practice profile, and appear in the patient
directory.

---

## Features

- **Doctor directory** — browse every registered doctor, or filter by specialty
  (cardiology, orthopaedics, neurology, nephrology), with search across name,
  qualification and focus area.
- **Hospital availability** — live bed and ICU counts, sortable by name, beds
  free or ICU free, with a status pill that distinguishes "no beds" from
  "not reported".
- **Blood Link** — join the volunteer donor register, or search it by blood
  group and area and call a donor directly.
- **Two account types** — patients and doctors, with role-aware sign-in and
  separate dashboards.

## Tech stack

- Java 11, Android SDK 35 (minSdk 26)
- Firebase Authentication and Realtime Database
- Material 3 (`com.google.android.material`)
- AndroidX: AppCompat, ConstraintLayout, RecyclerView, Lifecycle (ViewModel +
  LiveData), View Binding

---

## Setting up

```bash
./gradlew assembleDebug      # build
./gradlew testDebugUnitTest  # unit tests
./gradlew lintDebug          # static analysis
```

### Two things you must configure

**1. The Realtime Database URL.**

The committed `app/google-services.json` has no `firebase_url` entry, so
`FirebaseDatabase.getInstance()` cannot infer an endpoint and throws at runtime —
which would take out every data-backed screen. The URL is therefore supplied
explicitly by `R.string.firebase_database_url` in
`app/src/main/res/values/config.xml`.

The committed value is the default `us-central1` endpoint derived from the
project id. **If the database was created in another region, change it** — the
Firebase console shows the real host, e.g.
`https://<project>-default-rtdb.asia-southeast1.firebasedatabase.app`.

The durable fix is to re-download `google-services.json` from the Firebase
console after the Realtime Database exists; the regenerated file carries
`firebase_url` and the override becomes redundant.

**2. Database security rules.**

A fresh Realtime Database denies all reads and writes, so **every data screen
will show "You don't have access" until rules are published.** The rules the app
expects are in [`database.rules.json`](database.rules.json), commented
throughout.

Publish them via the console — Realtime Database → Rules → paste → Publish — or
with the CLI:

```bash
firebase deploy --only database
```

In short: `users/<uid>` is private to that uid; `docs/`, `hospitals/` and
`blood_donors/` are readable by any signed-in user; a doctor may write only
their own `docs/<specialty>/<uid>` record and only if their role is `doctor`;
the donor register is append-only so nobody can edit or delete someone else's
listing; and the licence register permits only a single-record equality query on
`license`, never a bulk read.

The licence check in `DoctorVerificationActivity` is a convenience check that
stops honest users registering with a typo. **It is not an authorisation
boundary.** Because verification necessarily happens before the doctor has an
account, that query cannot require auth — so a licence number can still be
guessed one attempt at a time. Closing that properly needs a Cloud Function that
takes the three fields and returns a boolean, or a reordered flow where the user
registers first and verifies while signed in. Both are app changes; the rules
alone cannot fix it.

### Sample data

[`seed-data.json`](seed-data.json) has a handful of hospitals, doctors, licence
records and donors, so the screens have something to show.

Import **one node at a time** from the console (Data → select node → ⋮ → Import
JSON). Importing the whole file at the root replaces the entire database,
including real accounts under `users/`.

It deliberately includes awkward records worth keeping: a hospital with no bed
counts at all (must read as "not reported", never as zero) and one whose
`available_beds` is the string `"N/A"` (the record that used to crash sorting).
To test the doctor flow, verify with `Ayesha Rahman` /
`ayesha.rahman@example.com` / `BMDC001`.

### Known constraints

- `applicationId` is `com.example.vitacare_app_250` and cannot change without
  re-registering the app in the Firebase console — `google-services.json`
  matches on package name. For the same reason the debug build carries no
  `applicationIdSuffix`.
- Release builds are unsigned; add a signing config before distributing.

---

## Architecture

```
core/       Application, Result<T>, AppError            — cross-cutting types
data/
  model/    Doctor, Hospital, BloodDonor, Role,
            Specialty, BloodGroup                       — typed, self-validating
  repo/     Auth / Doctor / Hospital / Donor            — all Firebase access
  FirebaseProvider, DbPaths                             — SDK entry point, node names
session/    SessionManager                              — who is signed in
ui/
  base/     BaseActivity                                — insets, toolbar, feedback
  common/   StateView, FormField, InfoRow, …            — shared UI pieces
  auth/ patient/ doctor/ directory/ hospital/ blood/    — one package per flow
util/       Validators, Texts                           — pure, unit-tested
```

Three conventions hold throughout:

- **Screens never touch Firebase.** Everything goes through a repository, which
  returns `Result<T>` (a value or a classified `AppError`) or a `LiveData`.
- **Lists observe `LiveQuery`,** which attaches its database listener in
  `onActive` and removes it in `onInactive`. Nothing listens while no screen is
  watching.
- **Nothing is hardcoded in a layout.** Colours, type, shape and spacing resolve
  through theme attributes (`?attr/colorPrimary`, `@dimen/space_lg`), so light
  and dark mode both work and the design is adjustable in one place.
