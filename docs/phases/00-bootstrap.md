<!-- SPDX-License-Identifier: MIT -->

# Phase 00 — bootstrap + hello

- Status: ✅ · Date: 2026-10-24 · Branch: `arena/52619694-2048-game`
- CI: run [40064397149](https://github.com/pabi277/2048-game/actions/runs/40064397149)
  green at head sha `bdd578901bd7b56beb2012e2aba10f1b2399f6f3`
  · Provenance: AppFlow @ `57597b3332ad578614a0d9d5609f9cf8a278ee8a` (reference only)
- **APK:** artifact `app-progress-apk` from run 40064397149 (10,837,751 B) —
  *shows: a single cream "Tilefold" screen with the tagline "Slide. Merge.
  Breathe.", a taupe "Phase 0 · foundation build" chip and the principles line,
  rendered from the seed theme. Nothing else exists yet — no board, no tiles.*

## Parts

- 0.1 — playbook snapshot + plan docs + `LICENSE` (MIT) + README + `rule.md` +
  `prompt.md` — **done**, in two commits: `2a55172` (frozen `templates/` +
  `knowledge/` + provenance line) after the plan-docs merge `2b3bb7d`
- 0.2 — Gradle/AGP skeleton, one `app` module, single activity, hello screen in
  the seed theme, host unit test, Robolectric + Roborazzi screenshot test,
  Android Lint + detekt + ktlint — **done** (`89970a1` and its fixes)
- 0.3 — `ci-build.yml` from `templates/ci-build.yml` with secret names only,
  every DEPEND pinned from its official artefact, licences logged — **done**
  (`docs/DECISIONS.md` § Third-party licences)

## Reused (reuse-first scan for this phase)

- 0.1 snapshot — **ADAPT** — AppFlow @ `57597b3` (MIT): copied as a frozen
  snapshot, never edited; provenance line in `README.md`.
- 0.2 build skeleton — **ADAPT** — `android/compose-samples` Jetsnack
  (Apache-2.0, checked 2026-10-24): single-module Compose setup, JVM 17,
  `libs.versions.toml`, `packaging.resources.excludes`, `enableEdgeToEdge`.
  Version numbers were read from that sample and from
  `android/nowinandroid` (Apache-2.0), not invented.
- 0.2 Gradle wrapper — **ADAPT** — wrapper jar/scripts from the same official
  sample (Gradle wrapper files are the standard bootstrap of any Gradle
  project); distribution pinned to Gradle 8.14.3.
- 0.2 theme — **ADAPT** — Material 3 `lightColorScheme` from one seed, per
  `knowledge/app-engineering.md` §3.1. `material-color-utilities` is **not**
  depended on in phase 0: the palette is still a proposal, so hand-derived
  values are enough and one fewer dependency ships. Revisit at 0-D freeze.
- 0.2 tests — **DEPEND** — Roborazzi 1.77.0 + Robolectric 4.16 (Apache-2.0),
  Truth 1.4.4; Paparazzi and mockk stay rejected (one of each per job).
- 0.2 static analysis — **DEPEND** — detekt 1.23.8 (Apache-2.0) +
  ktlint-gradle 14.2.0 (MIT) with ktlint engine 1.8.0.
- 0.2 CI — **ADAPT** — `templates/ci-build.yml` (MIT), with four local changes:
  the SDK step (the template's `android-actions/setup-android@v3` fails on the
  current runner image — run 40061789029), `check` as the gate command,
  screenshot/report artifacts, and failure annotations.
- 0.2 CI diagnostics — **BUILD** — Gradle/detekt/ktlint/test annotations
  (~60 lines of workflow YAML). Nothing found to reuse: the log bundle and
  artifact downloads are unreachable from this environment, so the workflow
  surfaces the same information through check-run annotations.

## Evidence (before the change)

- START.md §2/§2.5 (snapshot and plan docs before any app code) and §4
  (pre-code checklist) — read at playbook `57597b3`.
- `templates/ci-build.yml`, `templates/rule-app.md`, `templates/prompt-app.md`,
  `templates/readme-app.md`, `knowledge/fast-quality-no-device.md` §1–§2.
- Run 40061789029 step 4 — `Set up Android SDK` failed; step list read from the
  Actions API, log bundle unreachable (blob EOF).
- Run 40063396863 → 40063585285 — `ContrastTest` reported exactly 1.0 for every
  colour pair; cause: Compose packs a colour as `(argb shl 32) or colorspaceId`,
  so bits 16/8/0 are the colour-space id, not the channels (`Contrast.kt`).
- Run 40063984300 — `testReleaseUnitTest` failed while `testDebugUnitTest`
  passed: the release variant has no `ui-test-manifest` (debug-only), so the
  Compose host activity is absent.

## What shipped

- `app/` module: `MainActivity` (single activity, edge-to-edge), `HelloScreen`
  with light/dark/large-font previews, `TilefoldTheme` +
  `SeedColors` (provisional tokens from `docs/UI-REFERENCE.md`) + `Contrast`
  (WCAG maths used by tests and, later, the a11y pass).
- EN + HI string resources for the hello screen (`values/`, `values-hi/`).
- Manifest with **no permissions**; backup/extraction rules included.
- `config/detekt/detekt.yml` (deviations only), `.editorconfig`
  (ktlint official, Compose naming allowed, 120 cols).
- `.github/workflows/ci-build.yml`: no-device gate (`check`), debug APK
  artifact `app-progress-apk`, screenshot artifact `app-screenshots`, reports,
  failure annotations, and the untouched signed-release lane for `main`
  (secret **names** only: `ANDROID_KEYSTORE_BASE64`,
  `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`).
- Versions pinned (read from the artefacts/samples, logged in
  `docs/DECISIONS.md`): AGP 8.13.0 · Kotlin 2.2.20 · Gradle 8.14.3 ·
  Compose BOM 2025.09.01 · Roborazzi 1.77.0 · Robolectric 4.16 · detekt 1.23.8 ·
  ktlint-gradle 14.2.0 / ktlint 1.8.0 · Truth 1.4.4 · Turbine 1.2.0 ·
  coroutines-test 1.10.1 · kotlinx-serialization-json 1.8.0 ·
  compileSdk 36 / targetSdk 36 / minSdk 24.

## Tests (no-device gate — knowledge/fast-quality-no-device.md §1)

- `./gradlew check` in CI = `testDebugUnitTest` + `lintDebug` + `detekt` +
  `ktlintCheck` (main and test source sets), all green in run 40064397149.
- Unit: `ContrastTest` — 7 tests; proves black/white = 21:1, a published WCAG
  pair (#767676 on white = 4.54:1), symmetry, and that the provisional token
  pairs meet AA (seed/on-seed 4.66:1 is the tightest).
- Screenshot: `HelloScreenScreenshotTest` — Robolectric + Roborazzi renders
  light and dark; CI asserts PNGs exist (16 files) and uploads
  `app-screenshots` (7,763 B). **These are renders, not pixel-diff goldens** —
  goldens require the frozen tokens and arrive with phase 0-D.
- Static: Android Lint (`abortOnError`, warnings not yet errors) + detekt
  (`buildUponDefaultConfig`, `maxIssues: 0`) + ktlint official style.
- Instrumented/emulator: none, by design. Physical device: not needed for this
  phase; the owner's sideload look is a look, not a gate.
- **Not verifiable from this environment:** artifact byte contents — the blob
  storage host refuses connections here (`app-progress-apk` size 10,837,751 B
  and `app-screenshots` 7,763 B are read from the Actions API). The owner
  downloads them from the run page.

## UI reference comparison (UI phases only)

Not applicable yet: phase 0-D is the reference itself. The hello screen uses the
**proposed** seed and tokens, and is expected to change when 0-D freezes them.

## Next-chat prompt

- [x] Printed IN CHAT at phase end (from `templates/next-chat-prompt.md`, fully
      filled for phase 0-D) — the owner copies the next chat's start from the
      chat; `prompt.md` updated in this commit as backup only.

## Follow-ups

- Phase 0-D builds the reference site and freezes tokens; the hello screen's
  seed is then re-checked against the frozen palette (expected small changes).
- Screenshot **goldens** (fail on visual diff) start at 0-D, once the reference
  is approved; until then screenshots are evidence, not a contract.
- `warningsAsErrors` for lint is deliberately off in phase 0; turn it on in the
  release-prep phase once the UI surface is stable.
- `actions/setup-java@v4` is deprecated (warning in run 40064397149); move to
  v5 in a later CI hygiene change, after confirming the Gradle cache behaviour.
