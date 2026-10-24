<!-- SPDX-License-Identifier: MIT -->

# Phase 0-D — UI reference site + tokens freeze

- Status: 🚧 owner review open (built, presented, **not yet approved**)
- Date: 2026-10-24 · Branch: `arena/8c6acb31-2048-game`
- Provenance: AppFlow @ `57597b3332ad578614a0d9d5609f9cf8a278ee8a` (reference only)
- CI (reference site, part 0-D.1): run
  [40067703773](https://github.com/pabi277/2048-game/actions/runs/40067703773)
  green at `f34a181` — app no-device gate **and** the new
  `ui-reference tokens match the doc` job.
- CI (tokens + goldens, part 0-D.3): run
  [40070687033](https://github.com/pabi277/2048-game/actions/runs/40070687033)
  green at `e0d0c95`. Artifacts: **`app-progress-apk` 10,849,239 B**,
  `app-screenshots` 253,477 B (the 5 goldens + compare output), `quality-gate-reports`
  56,093 B. Goldens recorded by the `Record screenshot goldens` job in run
  [40070266477](https://github.com/pabi277/2048-game/actions/runs/40070266477)
  (commit `e1d5a09`).
- The bootstrap needed four guard-fix rounds (ktlint chain formatting, detekt
  loop jumps / return count / compound condition, then a Kotlin visibility error
  where the public palette class exposed the internal token type). Each round's
  failure is readable from the check-run annotations, which is why the workflow
  now also dumps the compiler section and a log tail — a fix for the next chat,
  not only for this one.

## What this phase is

The design specification for every screen in `docs/UI-REFERENCE.md`, built as a
disposable phone-shaped website, reviewed by the owner **in chat**, and only then
frozen as tokens. No app UI beyond the hello screen exists until this row is ✅
(`rule.md` §5; `knowledge/ui-reference-site.md` §1).

## Parts

### 0-D.1 — the reference site (`design/reference/`)

- Vite 8.3.5 + Tailwind 4.3.3 + Konsta UI 5.5.0 (Material theme) + Lucide
  (`lucide-react` 1.61.0), Roboto + Noto Sans Devanagari self-hosted via
  fontsource. Fixed **390 × 844** surface (1 CSS px = 1 dp), one column, app
  patterns only, no hover/marketing surfaces.
- **30 review states**: the 27 required rows, plus the restart confirm, the
  reset-stats confirm, the Hindi Home run, and a tokens/contrast page. Every
  state has its own URL (`#/game-mid`, `#/settings-dark-large`, …); `/?gallery=1`
  is the overview sheet; ← / → walk the states in order.
- `src/design.js` is the only place a colour, ramp pair, type role, spacing,
  radius or duration value exists. `src/ui.jsx` + `src/screens.jsx` contain no
  raw colour literal — enforced, not promised (see 0-D.3).
- Archived renders of all 30 states + tokens page + gallery:
  `design/reference/screenshots/` (4.5 MB, 32 PNGs at 2×). These are the images
  later UI phases compare their Compose screenshots against.
- Copy: English source plus a Hindi run of Home, Settings and How-to-play, so
  Devanagari length, wrapping and line height are reviewed rather than assumed.
  The Hindi is a **length probe**; the reviewed string set lands in phase 4.

### 0-D.2 — owner review rounds (in chat)

- Round 1 presented 2026-10-24 with the gallery, the individual states and the
  tokens page. Corrections from the owner are applied to the reference first,
  then to the doc and the app theme — never the reverse.
- Nothing is frozen before the owner's explicit approval in chat.

### 0-D.3 — tokens freeze + goldens

- `docs/UI-REFERENCE.md` now holds the **actual rendered values** (colour roles
  for both themes, the 12-step tile ramp per theme with computed contrast, the
  type scale, spacing/shape/elevation/motion, the component inventory, the icon
  set, the deliberate native adaptations and the complete web→Compose mapping).
- Two independent guards keep the three copies (reference, doc, app) equal:
  - `design/reference/scripts/check-tokens.mjs` (`npm run verify`, CI job
    `ui-reference`): doc ↔ reference site, contrast floors, state coverage, and
    "no raw colour literal in a screen file".
  - `app/src/test/.../theme/TokensSyncTest.kt` (host unit test): doc ↔ **app**
    tokens, plus the same contrast floors computed from the app's own values.
- The app theme (`ui/theme/Tokens.kt`, `Theme.kt`) now reads **only** these
  tokens: colour roles, tile ramp (`Tokens.tile(value, dark)` — one pure lookup),
  spacing (`Dimens`), corners (`Corners`), type roles and numeral caps
  (`TypeScale`). `SeedColors.kt` is gone.
- Screenshot tests are **goldens**: `captureRoboImage("src/test/screenshots/…")`
  and the test task runs in Roborazzi verify mode, so a visual difference fails
  `testDebugUnitTest`. Recording is deliberate
  (`./gradlew recordRoborazziDebug`, or the `record-screenshots` workflow
  dispatch), never automatic. Goldens: `hello-light`, `hello-dark`,
  `hello-large-font` (2.0× font scale, applied through `LocalDensity`),
  `tile-ramp-light`, `tile-ramp-dark`.
- The tile-ramp screenshots render the frozen ramp **from the app's tokens** in a
  test-source composable, so the Compose ramp is visually comparable with the
  reference's ramp before phase 2 paints a tile.

## Reused (reuse-first scan for this phase)

- 0-D.1 reference stack — **DEPEND** — Vite 8.3.5 (MIT), Tailwind 4.3.3 (MIT),
  Konsta UI 5.5.0 (MIT, Material theme), `lucide-react` 1.61.0 (**ISC**),
  React 19.3.1 (MIT), `@fontsource/roboto` 5.3.0 and
  `@fontsource/noto-sans-devanagari` 5.3.0 (OFL-1.1). The stack was already the
  recorded DECIDE-ONCE choice; versions were read from the npm registry on
  2026-10-24. **None of it ships in the APK** — the reference is disposable.
- 0-D.1 harness — **BUILD** — the review shell (state list, notes panel, gallery,
  tokens page) is ~200 lines that nothing off-the-shelf does for "every app state
  on one page". Searched: Storybook (REJECT: a build system for components, not
  a phone-frame specification, and it would publish the surface we are reviewing
  rather than a disposable page), Histoire (REJECT: Vue-first, same objection),
  plain `vite` + Tailwind (chosen). The device frame is drawn by the harness, not
  a library.
- 0-D.1 icons — **ADAPT** — Lucide (ISC): used as the `lucide-react` package on
  the web; the app will adapt the same glyphs into `ImageVector`s in phase 2,
  with attribution on the licences screen. No icon was hand-drawn.
- 0-D.1 fonts — **DEPEND (web only)** — Roboto + Noto Sans Devanagari, OFL-1.1.
  On Android both are system families: nothing is bundled into the APK, so the
  licence does not enter the shipped set.
- 0-D.3 goldens — **DEPEND** — Roborazzi 1.77.0 (Apache-2.0, already shipped for
  the screenshot lane; verify mode used instead of a new library) +
  Robolectric 4.16. No new dependency was added for the freeze.
- 0-D.3 app tokens — **BUILD** — the token module is ~330 lines of literals and
  the ramp lookup. Searched: `material-color-utilities` (REJECT again for this
  phase: the ramp and roles are hand-approved values, not generated tonal
  palettes; revisit only if the palette must be regenerated from a seed),
  `compose-multiplatform-2048` (STUDY only, MIT — its colour handling is not
  copied), Material 3 theme builders (nothing to depend on).
- 0-D.1 screenshots — **BUILD (dev-only tooling)** — `scripts/screenshots.mjs`
  over `playwright-core` (Apache-2.0) + `@sparticuz/chromium` (MIT), installed
  ad hoc and not part of the app or the gate. Searched: Percy/Chromatic (REJECT:
  SaaS, account, upload), BackstopJS (REJECT: extra config for 30 static pages),
  a Vite plugin (REJECT: no browser in the image). The archived PNGs are the
  deliverable; the script is how they are regenerated.
- 0-D.1 preview — **BUILD** — the live preview runs Vite bound to `0.0.0.0` with
  the sandbox's proxied host allowed and HMR over `wss`; no tunnelling service
  (ngrok/cloudflared) is depended on, because the platform preview already
  proxies the port.

## Evidence (before the change)

- `docs/UI-REFERENCE.md` @ `0b6bf95` — 27 required rows, tokens marked
  "PROPOSED, to be frozen only on owner approval".
- `app/.../ui/theme/SeedColors.kt` @ `0b6bf95` — provisional values, documented
  as expected to change at 0-D; `HelloScreenScreenshotTest` wrote PNGs into
  `build/roborazzi/` with the comment "renders, not goldens".
- Run 40064397149 (phase 0) — 16 PNGs, `app-screenshots` 7,763 B: renders only.
- Contrast of the *proposed* ramp, computed before any code was written:
  tiles 32 and 64 used white numerals (2.25:1 and 2.90:1 — unreadable) and tile
  1024 used `#FFF9E8` (2.58:1). These were corrected in the proposal **before**
  the reference was built: 32/64 now take `#5A3318` (4.86:1, 3.77:1) and 1024
  takes `#2C241C` (5.63:1). Recorded in the doc as rendered values.
- Dark ramp spot-check: dark tile 64 (white on `#C97F4B`) is the tightest
  pair at 3.17:1 — still above the 3.0:1 floor for large bold numerals.

## What shipped

- `design/reference/` — the whole disposable site + 32 archived PNGs +
  `scripts/check-tokens.mjs` + `scripts/screenshots.mjs` + its README with the
  stack, licences and the guardrail.
- `docs/UI-REFERENCE.md` — rewritten around the rendered values: colour tables,
  tile ramp + contrast record, type scale with numeral caps, spacing/shape/
  motion, component inventory, icons, deliberate native adaptations, the full
  web→Compose mapping, and the approval table (currently "pending").
- `app/.../ui/theme/Tokens.kt` — frozen token source of the app:
  `Tokens` (colour roles, ramp, seeds), `Dimens`, `Corners`, `TypeScale`.
- `app/.../ui/theme/Theme.kt` — M3 scheme, shapes and typography built only from
  tokens; `TilefoldPalette` + `LocalTilefoldPalette` carry the tile ramp and the
  score-box colours that Material has no slot for.
- `app/src/test/.../theme/TokensSyncTest.kt` — doc ↔ app parity + contrast.
- `app/src/test/.../TileRampScreenshotTest.kt` — golden evidence of the Compose
  ramp in both themes (test-source composable; never ships).
- `app/src/test/screenshots/*.png` — the goldens (recorded by CI, committed to
  this branch; see "Gate" below).
- CI: new `ui-reference` job (verify + build, non-gating for the app);
  goldens enforced in the test job; new `record-screenshots` job (non-`main`
  only) that re-records and commits goldens deliberately when asked by a
  `[record-goldens]` commit or a dispatch.

## What the APK shows

`app-progress-apk` from the phase 0-D run still shows **the single cream
"Tilefold" hello screen** — tagline, phase chip, principles line — but now
rendered from the **frozen token theme** (`Tokens.kt`/`Theme.kt`) rather than
the provisional `SeedColors`. There is still no board, no tiles and no
navigation: phase 1/2 bring those. The tile ramp and the score box are visible
only in the CI screenshot artifact (`app-screenshots`: `tile-ramp-light.png`,
`tile-ramp-dark.png`), on purpose — the reference site is the design, and the
app does not paint a tile before phase 2.

## Tests (no-device gate — `knowledge/fast-quality-no-device.md` §1)

- `./gradlew check` = `testDebugUnitTest` + `lintDebug` + `detekt` +
  `ktlintCheck`, green in run 40070687033 at `e0d0c95`.
- Unit: `ContrastTest` (7) + `TokensSyncTest` (7) — values, ramp coverage
  2…2048 + `super`, contrast floors 3.0:1 (numerals) and 4.5:1 (body), type
  roles, numeral caps, and the doc's spacing numbers.
- Screenshot: 5 goldens (hello light/dark/2.0×, tile ramp light/dark), verified
  in Roborazzi verify mode at 411×891dp/xxhdpi: a visual difference fails the
  test. The goldens were recorded by CI (`e1d5a09`) and pulled back through a git
  commit, since run artifacts are not downloadable here.
- Reference: `npm run verify` green (tokens ↔ doc, contrast, 30 states, no raw
  colours in screens) and `npm run build` green, in run 40067703773.
- **Not verifiable from this environment:** artifact bytes (the blob host
  refuses connections here) — APK/artifact sizes are read from the Actions API;
  the goldens were produced by CI and pulled back through a git commit, not by a
  download.

## Gate (the golden bootstrap, recorded honestly)

A pixel-diff contract needs goldens in git, and this environment cannot download
run artifacts. Sequence used, visible in the run history:

1. push with verify mode on → the test job fails because
   `app/src/test/screenshots/` is empty (the expected, honest state);
2. a commit whose message carries `[record-goldens]` (or a
   `workflow_dispatch` with `record_screenshots=true`) → `recordRoborazziDebug`
   writes the five goldens and the job commits them to this branch;
3. the push from that commit runs the normal lane in verify mode → green.

From then on, a visual change is approved by re-recording goldens deliberately
and reviewing the new images — never by deleting the test.

## Owner review (0-D.2)

- [x] Every screen/state presented in chat — 2026-10-24 (gallery + per-state
      renders + tokens page + the Compose ramp goldens).
- [ ] Owner corrections applied — round 1 open.
- [ ] **Owner approves explicitly in chat** — pending. Until these boxes are
      ticked, tokens are "proposed-frozen" (machine-checked) but **not
      owner-approved**, and no later UI phase may start.

## Next-chat prompt

- [ ] Printed IN CHAT at phase end for phase 1 (engine), from
      `templates/next-chat-prompt.md`.

## Follow-ups

- On approval: tick the three boxes above, set the approval row in
  `docs/UI-REFERENCE.md` (date · stack · coverage · freeze sha), flip the roadmap
  row to ✅, and log the design decision in `docs/DECISIONS.md`.
- Phase 2 must compare its Compose screenshots against
  `design/reference/screenshots/` and record the comparison here or in its own
  record — differences fixed or logged as deliberate native adaptations.
- `actions/setup-java@v4` deprecation (carried from phase 0) still applies.
- The reference site is not built for production and is not deployed anywhere;
  if the owner wants a permanent preview, that is a new decision (and a host).
