<!-- SPDX-License-Identifier: MIT -->

# Phase 2 — Board UI + swipe + animation

- Status: ✅ complete 2026-10-24 · **merged to `main` in PR
  [#4](https://github.com/pabi277/2048-game/pull/4)** on the owner's explicit
  command (2026-10-24).
- Date: 2026-10-24 · Branch: `arena/6f3f2907-2048-game`
- Base commit: `c4c7ab5` (phase 1 merged) · goldens recorded by CI at `08e7467`
- CI: run [40079825296](https://github.com/pabi277/2048-game/actions/runs/40079825296)
  green at **`2bfbf3b`** (`No-device gate, debug build, progress APK` ✅,
  `UI reference tokens match the doc` ✅; the recorder job correctly skipped — a
  normal push never re-records). Artifacts: **`app-progress-apk` 10,966,802 B**
  (was 10,891,006 B) · `app-screenshots` 727,584 B (11 goldens, was 5 /
  253,477 B) · `quality-gate-reports` 76,798 B.
- Provenance: AppFlow @ `57597b3332ad578614a0d9d5609f9cf8a278ee8a` (reference
  only).

## What this phase is

The engine from phase 1, drawn. A player can swipe, tap the direction pad or use
arrow keys; the tiles slide where the engine says they slide, merge with a pop,
spawn with a small scale-in; the score ticks to its new value; the game pauses
when the app leaves the foreground and resumes on Resume or the back gesture.
Everything visible comes from the frozen reference (`docs/UI-REFERENCE.md`), and
nothing in the UI layer re-implements a rule: the screen animates a `MovePlan`
the engine produced.

## Parts

### 2.1 — `GameScreen` from the frozen reference

- `app/.../ui/game/GameScreen.kt` (341) — the screen itself, **stateless**:
  `GameScreen(state: GameScreenState, actions: GameActions, modifier,
  reducedMotion)`. `GameScreenState` carries everything the picture depends on
  (scene, score, best, status, canUndo, hintVisible, padVisible) and
  `GameActions` carries every interaction (`onMove`, `onUndo`, `onRestart`,
  `onHome`, `onTogglePad`). A fixed state therefore renders a fixed picture —
  that is what makes the six goldens deterministic and the Robolectric tests able
  to drive the *real* session.
- Layout, in reference order: 24sp title + 48dp home/restart `IconAction`s ·
  `ScoreRow` (two taupe boxes, 12dp gap, uppercase 12sp label with 0.08em
  tracking, 26sp bold tabular value) · `BoardFrame` (rounded 16dp, 10dp padding,
  8dp gaps, square) · `ControlRow` (Undo · hint or game-over chip · pad toggle) ·
  optional `DirectionPad` · 8dp tail.
- `app/.../ui/game/BoardView.kt` (261) — `BoardFrame`: 16 empty cells and the
  tiles on top, every tile positioned by `xOf`/`yOf(index, cell)` where
  `cell = (width − 2·BoardPadding − 3·TileGap) / 4`, and keyed by tile identity so
  a move slides one tile instead of blinking two.
- `app/.../ui/game/BoardScene.kt` (120) — the only engine → picture mapper:
  `still(board)` for a fresh game/undo, `after(scene, plan, nextBoard)` for a move
  (survivor keeps its key and takes the doubled value, the absorbed tile becomes a
  fading copy, the spawned tile gets a new key).
- `app/.../ui/game/GameSession.kt` (153) — the screen's state: the engine `Game`,
  the `BoardScene`, the hint/pad/pause flags, this session's best score, and the
  seven callbacks. Every rule call goes to `Moves`/`Game`; the session only
  decides what to draw and in which order.
- `app/.../ui/game/GameRoute.kt` (97) — `GameRoute` (session + lifecycle +
  back-handling) and `GameContent` (board + pause overlay, no lifecycle) so the
  tests draw exactly what the app draws.
- `app/.../ui/game/PauseSheet.kt` (136), `DirectionPad.kt` (107),
  `ui/components/Buttons.kt` (195), `ui/icons/TilefoldIcons.kt` (167),
  `ui/theme/TextCaps.kt` (30), `ui/game/ScoreFormat.kt` (16).
- `MainActivity` now shows `TilefoldTheme { GameRoute() }`; the phase-1
  `EnginePreview` and its 4 tests are deleted, and `buildConfig` is gone from
  `app/build.gradle.kts` (nothing reads `BuildConfig` any more).

### 2.2 — gestures

- `app/.../ui/game/SwipeAxis.kt` (44) — **pure** `direction(dx, dy, threshold)`:
  under the 24dp threshold the gesture is not a swipe (`null`), the dominant axis
  wins, an exact tie reads horizontal. `BoardView` accumulates the drag with
  `detectDragGestures` and reads it once on `onDragEnd`; the callback is held in
  `rememberUpdatedState` and the `pointerInput` is keyed on the threshold, so a
  running animation cannot cancel an in-flight drag.
- Direction pad — `DirectionPad` (56dp keys, 4dp gaps, 2dp elevation), toggled by
  the arrow `IconAction` in the control row.
- Hardware keyboard — the screen owns one `FocusRequester` (created with
  `remember`), requests focus in a `LaunchedEffect`, and maps
  `Key.DirectionUp/Down/Left/Right` on `KeyDown` via `directionFor`/`handleKey`.
  `.onPreviewKeyEvent` is applied *before* `.focusable()` so the handler sees the
  key first.
- An invalid move is free end to end: the engine returns the same `Game`
  (`MoveOutcome.NoMove`), `GameSession.move` returns `false` and changes nothing,
  so no tile spawns and no score is added.

### 2.3 — motion, pause, screenshots

- `app/.../ui/game/Motion.kt` (21) — the only place that decides *whether* the
  board animates; durations are `Durations` in `ui/theme/Tokens.kt`
  (`SLIDE_MS 110`, `POP_MS 140`, `SPAWN_MS 130`, `SCORE_MS 220`), asserted against
  `docs/UI-REFERENCE.md` by `TokensSyncTest`.
- Slide = an `Animatable` in dp per tile (interruptible: a tile caught mid-slide
  continues from where it is); merge pop = 1.0 → 1.06 → 1.0 over `POP_MS`; spawn
  = 0.6 → 1.0 over `SPAWN_MS`; absorbed tile fades over `SLIDE_MS` and is dropped
  by `GameRoute` when the slide is over; the score ticks with
  `animateIntAsState(tween(SCORE_MS))` in tabular figures.
- Reduced motion — `rememberReducedMotion()` reads the device setting
  (`ValueAnimator.areAnimatorsEnabled()` on API 26+, the animator duration scale
  below that); every duration becomes 0 and each `Animatable` snaps.
- Pause — `GameRoute` pauses on `Lifecycle.Event.ON_STOP` (background, call,
  screen off), `BackHandler` resumes, the pause sheet draws `Paused` + Resume +
  Restart + `pause_meta(score, highest tile)` over the dimmed board, and a paused
  session refuses moves.
- Six goldens — `GameScreenScreenshotTest`: `game-new`, `game-mid`, `game-dark`,
  `game-large-font`, `game-paused`, `game-pad`, at 411 × 891 dp / xxhdpi with
  `GraphicsMode.NATIVE` and reduced motion (so no animation is ever in flight when
  the shutter fires).

## Engine change this phase (inside `domain/`, with tests)

`Moves.plan(board, direction): MovePlan?` now returns the move's board **and** its
geometry — one `TileSlide(from, to)` per occupied cell (a merge emits two slides
onto the same destination, survivor first), `mergeDestinations`, `mergedValues`,
`scoreDelta`. `Moves.apply` is the same computation without the geometry and
`hasAnyMove` is the four-direction any. `MovePlanTest` (8) pins the geometry per
direction and asserts `plan.board == apply().board` for **800**
board/direction pairs. This is the rule "the UI never re-implements a merge" made
mechanical: the board can only draw what the engine returned.

## Reused (reuse-first scan for this phase)

- Swipe — **BUILD** (44 lines + 8 tests). Searched for a pointer-input helper:
  `androidx.compose.foundation.gestures.detectDragGestures` (**DEPEND**, already
  shipped) plus our own pure threshold mapper. `alexjlockwood/
  compose-multiplatform-2048` (MIT — **STUDY**: reads drags inline in the board
  composable, so its gesture rule is untestable) and `gabrielecirulli/2048`
  (**STUDY**: jQuery input manager, behaviour only) confirmed nothing small
  enough to adopt.
- Animation — **DEPEND** `androidx.compose.animation` (`Animatable`,
  `animateIntAsState`, `tween`, `graphicsLayer`), already on the classpath with
  the Compose BOM 2025.09.01. **REJECT** Lottie (a whole runtime for four
  transitions) and any physics-based spec (the reference's timings are explicit
  durations).
- Icons — **ADAPT** Lucide 1.61.0 (ISC): nine glyphs (`house`, `rotate-ccw`,
  `undo-2`, the four `arrow-*`, `play`, `x`) converted from the published 24×24
  `d` attributes through `PathParser`, tinted by the theme. **REJECT** Material
  Icons Extended (a megabyte of art for nine glyphs and a second icon language).
- UI base — **ADAPT** the Jetsnack sample's layout/state patterns (stateless
  screen + state holder), as recorded in `docs/RESEARCH.md`; no component library
  added.
- Fonts — the system Roboto already shipped in phase 0; nothing bundled.
- **One licence crosses into the app** (Lucide, ISC) and is logged in
  `docs/DECISIONS.md` § Third-party licences. The Gradle dependency graph is
  unchanged — the path data is vendored, not a package.

## Evidence (before the change)

- At `c4c7ab5` the app had **no** game screen: `MainActivity` opened the
  undersigned `EnginePreview`, there was no `ui/game/` package, no swipe, no
  motion, and the only screenshot goldens were the five phase-0/0-D images
  (`hello-*`, `tile-ramp-*`). Roadmap row 2 was 📋.
- The reference already fixed every number this phase uses (spacing 230–248 of
  `docs/UI-REFERENCE.md`, duration tokens at line 244), so nothing here is a new
  visual decision except the spawn start scale, which is now recorded in the
  adaptation table.

## What shipped

- 14 new files under `app/src/main/.../ui/` (icons, components, game), one new
  engine file, and the numbers above; `MainActivity` shows the real game.
- 10 new test files / 46 new host tests, plus one new `TokensSyncTest` case and
  the removed 4 preview tests: **128 host tests** in 17 files (was 85).
- Six new screenshot goldens (**11 PNGs** in `app/src/test/screenshots/`).
- This record, roadmap row 2 → ✅, `prompt.md`, the screenshot comparison in
  `docs/UI-REFERENCE.md`, and five line-level decisions + the Lucide licence in
  `docs/DECISIONS.md`.
- Hindi copy for the whole game screen in `values-hi/strings.xml` (20 keys, EN
  and HI in parity — the four direction labels are the pad's screen-reader
  names).

## What the APK shows

`app-progress-apk` is the **debug** build, so it opens straight on the game
screen of `07-game-new`: the `Tilefold` title with home and restart, the taupe
SCORE / BEST boxes (`0` / `0` on a first launch), the tan board with two opening
tiles, then `Undo` (muted), the `Swipe to merge` chip and the pad glyph. Swiping
any direction slides the tiles with the 110ms animation, merges pop, the spawned
tile scales in, the score ticks up and the chip disappears; the pad toggle opens
the 56dp four-way pad; arrow keys work with a keyboard attached; home or leaving
the app opens the pause sheet (dimmed board + `Paused` / `Resume` / `Restart`)
and Resume or back returns to the same board. Pressing back on the game itself
leaves the app (there is no Home screen until phase 4), and the release build
still opens the phase-0 hello screen.

## Screenshot comparison

Recorded in full in `docs/UI-REFERENCE.md` § Screenshot comparison (phase-2
row): reference `07`–`11` against `game-new`, `game-mid`, `game-paused`,
`game-dark`, `game-large-font` (+ `game-pad`, which the reference draws only as a
control glyph). Verified items and measurements (app images are 1233 px wide at
density 3, the reference frame is 820 px for 390 dp, so both are compared in dp):

| Item | Result |
|---|---|
| Title, score boxes, board frame, ramp, control row | geometry and colours match; the title's cap height measures 18.3dp in the app and 18.1dp in the reference |
| Pause sheet | **deliberate difference**: no Home row (there is no Home screen until phase 4, so the row would be a dead control) and the meta line quotes the board's real highest tile (1024); the reference's copy hard-codes 512 beside a 1024 tile |
| 2.0× font | score label 26 px → 52 px (2.0×); title 55 px → 83 px (1.51×) — Android 14's non-linear font scaling, which grows small text more than large text. The reference has no font scaling at all (it grows 1.95×), and the tile numerals stay capped at 1.25× in both. |
| Dark theme | board `#2b2723`, empty cell `#3a352f`, 62 % scrim — straight from `Tokens` |
| Dim on pause | board at `opacity 0.45` **and** the 45 % scrim, which is exactly what the reference does (`Board dim` + `var(--scrim)`) |

## Tests (no-device gate — `knowledge/fast-quality-no-device.md` §1)

- `./gradlew check` = `testDebugUnitTest` + `lintDebug` + `detekt` +
  `ktlintCheck`, green in run 40079825296 at `2bfbf3b`; the golden images are
  verified, not recorded, in this lane (the recorder job skipped itself on that
  push, which is the proof). `lintDebug` reports no new error; warnings stay
  non-fatal until release prep, as recorded.
- New tests: `MovePlanTest` (8, geometry + plan/apply agreement over 800 pairs),
  `BoardSceneTest` (6, identity, merges, spawns, a scripted game),
  `SwipeAxisTest` (8, threshold, dominant axis, tie, arrow keys),
  `MotionTest` (4), `TextCapsTest` (5), `GameScreenTest` (9, Robolectric: seeded
  opening board, swipe merge + score, undo, tap/sub-threshold drag costs nothing,
  pad plays, restart keeps best, home→pause→resume, paused restart,
  reduced-motion), `GameScreenScreenshotTest` (6) and one `TokensSyncTest` case
  for the motion durations.
- **Not verifiable from this environment:** there is still no JDK or Android SDK
  in the sandbox, so nothing was compiled or run locally — CI is the compiler and
  the runner, and every fix below was read from check-run annotations.

## Gate iterations (recorded honestly)

Seven pushes, every failure read from the check-run annotations:

1. `e3a2b4a` — detekt `LongMethod`: `GameScreen` 71 > 60 and `PauseSheet`
   70 > 60. Fix: extracted `ScoreRow`, and `PauseSheet`'s body into
   `SheetContent`. No rule suppressed.
2. `799a7dd` — ktlint (main): `property-naming` on the new `Durations` constants
   (→ `SLIDE_MS`…), `no-empty-first-line-in-class-body` (icons),
   `chain-method-continuation` twice (the key handler lambda and a trailing
   `graphicsLayer {}` in the tile modifier). Fix: handler hoisted into a `val`,
   `testTag` moved before the `graphicsLayer` trailing lambda.
3. `20bfc12` — ktlint (test): import order in two files, a chain continuation and
   a one-line body expression.
4. `b10bc43` — ktlint (main) again: a `.padding(...)` split over five lines
   inside the modifier chain. Fix: the call fits on one line at 120 columns.
5. `96a1be4` — **compile error**: `PathBuilder.getNodes()` is not public API in
   this Compose version (`Unresolved reference`). Fix: build the vectors from
   the published `d` strings through `PathParser().parsePathString(d).toNodes()`.
   This push also needed a CI fix: GitHub keeps only ~10 annotations per step and
   coloured `e: file:…` lines never matched `^e: `, so `b0f7ae4` collapsed each
   log's diagnostics into **one** ANSI-stripped, `%0A`-joined annotation.
6. `76329b6` — ktlint: the regenerated icons file again started its object body
   with a blank line.
7. `3632b01` — four *test* failures, all in hand-written expectations:
   `MovePlanTest.vertical plans use column destinations` expected the 4 in row 3
   to slide to row 2 (it stops at row 1: the merged 4 now occupies the top of the
   column and a tile may not pass a merged tile); `BoardSceneTest` compared cell
   *indices* with tile *keys*; `GameScreenTest.a tap or a tiny drag` used
   `swipeDown()`, which is a perfectly legal move on the opening board, and
   `the home icon pauses the game` asserted a pause sheet the test never drew.
   The engine was right in every case — one test was corrected, the others now
   use a tap + an 8 px drag and the real `GameContent` overlay.
8. `5ca158f` — the golden-recording commit: the gate ran **before** the goldens
   were committed (the recorder is a separate job), so `testDebugUnitTest`
   failed on six missing files and everything else — lint, detekt, ktlint, 122
   tests — passed. The recorder job then committed the six PNGs as `08e7467`
   ("Record screenshot goldens (deliberate re-record)") and that commit was fast-
   forwarded into the branch.

## Follow-ups (for phase 3, not this phase)

- The win/game-over dialogs replace `GameSession.releaseWin()` (auto-continue)
  and the `Game over` chip; both are marked as phase-3 placeholders in code.
- `best` is this session's maximum. Phase 3 owns persistence and must inject the
  stored best through the state holder — nothing is written to disk yet.
- Undo is unlimited in phase 2 (the engine's one snapshot); the "1 free + 1
  rewarded, max 3" policy and the ad `Policy` are phase-3 work.
- `PauseSheet` gains the Home row in phase 4, when there is a Home screen to
  return to.
- `action`/`GameScreenState` should gain `storageError` when the save codec lands
  (`#game-storage-error`).
- `actions/setup-java@v4` deprecation (carried since phase 0) still applies.
