<!-- SPDX-License-Identifier: MIT -->

# Phase 1 — Pure-Kotlin engine + tests

- Status: ✅ complete 2026-10-24 · **awaiting the owner's merge gate**
- Date: 2026-10-24 · Branch: `arena/6f3f2907-2048-game`
- CI: run [40076597395](https://github.com/pabi277/2048-game/actions/runs/40076597395)
  green at **`20a9648`** (`No-device gate, debug build, progress APK` ✅,
  `UI reference tokens match the doc` ✅). Artifacts:
  **`app-progress-apk` 10,891,006 B** (was 10,849,239 B) ·
  `app-screenshots` 253,477 B (the same 5 goldens, unchanged) ·
  `quality-gate-reports` 66,822 B.
- Provenance: AppFlow @ `57597b3332ad578614a0d9d5609f9cf8a278ee8a` (reference
  only) · base commit `d32324c` (phase 0-D merged).

## What this phase is

The game, as a library that knows nothing about Android. Every rule the player
will ever feel — a tile merges once, `2 2 2 2` becomes `4 4`, a 4 appears one
time in ten, 2048 wins, a full board with no equal neighbour is over, undo puts
the *future* back — is decided here, in pure Kotlin, seeded and reproducible
(`rule.md` §5; roadmap row 1). Phase 2 paints it; it never re-implements it.

## Parts

### 1.1 — board, slide/merge, spawner

- `app/.../domain/Board.kt` — immutable 16-cell board (`0` = empty), row-major,
  `valueAt`/`row`/`column`/`emptyCells`/`isFull`/`highestTile`, a validating
  `of`/`rows` factory (a tile value must be a power of two ≥ 2), and the stable
  one-line form (`2,4,8,16/…`) the golden tests compare against.
- `app/.../domain/Moves.kt` — `Moves.apply(board, direction)` returns a sealed
  `MoveOutcome`: `Moved(board, scoreDelta, mergedValues)` or `NoMove`.
  - Each line is **read in movement order** (leading edge first) so one merge
    scan serves all four directions.
  - The scan compacts, then merges adjacent equal pairs from the leading edge
    once each: `2 2 2 2 → 4 4 0 0`, `4 4 8 0 → 8 8 0 0`, `2 2 4 0 → 4 4 0 0`.
  - A direction that changes nothing returns the `NoMove` singleton — the
    caller must not spawn for it, so an invalid swipe is free.
  - `hasAnyMove` is the four-direction any.
- `app/.../domain/EngineRandom.kt` — `xorshift64*` (Marsaglia xorshift, Vigna's
  multiply), seeded from a `Long`, with the **cursor exposed as `state`** so a
  save or an undo restores the exact future; seed `0` maps to a fixed non-zero
  state because zero is a fixed point of the algorithm. `Spawner.spawn` draws the
  **value first** (4 when `nextInt(10) >= 9`, else 2) **then the cell**, so a
  stored state always reproduces the same tile in the same place.

### 1.2 — score, win, keep playing, game over, undo

- `app/.../domain/Game.kt` — `GameSnapshot(board, score, randomState,
  reached2048, keepPlaying, moves)` and `Game`, which is never mutated: every
  call returns a new instance (or `this` when nothing changed).
- Score = sum of the merged values of the move; the snapshot keeps it.
- Win at **2048** (`reached2048` set from the board's highest tile) → `WON`;
  while the dialog is owed, `move` returns `this` (a refusal costs no spawn and
  no RNG draw); `keepPlaying()` clears the owed dialog.
- Game over = board full **and** no equal orthogonal neighbour
  (`isGameOver`), derived in `status`, never stored.
- Undo = one snapshot (`board`, `score`, `randomState`) plus the `moves`
  counter — the free-undo limit itself is a phase-3 policy, not an engine rule.

### 1.3 — save codec + the test suite

- `app/.../domain/SaveCodec.kt` — kotlinx.serialization JSON with
  `schemaVersion` (current **1**), board, score, RNG state, flags, moves and the
  nested `previous` snapshot, so the free undo survives a process kill.
  `decode` returns `null` for: blank input, unparseable JSON, an unknown
  *future* version, a wrong cell count, a non-power-of-two cell, a negative
  score/moves or a zero RNG state. Nothing is repaired silently — a lost board
  must surface as "new game + storage banner" (phase 3), never a half-board.
  Unknown keys are ignored, and every field has a default, so an older payload
  still decodes.
- Host suite, 62 engine tests (the 4 preview tests are in 1.4; all run in
  `testDebugUnitTest`, no device):
  - `MovesTest` (12) — every direction by hand, including `2 2 2 2 → 4 4 0 0`,
    "a merged tile does not merge again", `NoMove` for a packed board;
  - `EngineRandomTest` (12) — sequence pinned for seed 1, state restoration,
    seed 0 mapping, bounds, spawn only into empty cells, spawn refused on a
    full board, value-before-cell order, and the **90 / 10 spawn distribution**
    measured over 20,000 spawns (CI enforces an 8.5–11.5 % band on fours);
  - `GameTest` (13) — two opening tiles, score/moves/spawn accounting, an
    invalid move costing nothing, undo restoring board + score + RNG, reaching
    2048 blocking input until keep-playing, the dead and the alive full board;
  - `ReplayTest` (5) — **golden boards**: seed `20261024` + `LLUURDDLR` →
    `0,2,0,2/0,0,0,0/0,0,0,2/0,2,4,8`, score 20, RNG `937781803555920346`;
    seed `7` + `UDUDLRLR` → `0,0,0,4/0,0,0,2/0,2,0,2/0,0,8,2`, score 20;
  - `EnginePropertyTest` (8) — over **300 generated boards** × 4 directions: no
    tile lost, one tile removed per merge, score delta = sum of merged values,
    every merged value was twice a tile already present, no gaps towards the
    leading edge, `hasAnyMove` agrees with the four directions;
  - `SaveCodecTest` (12) — round trip incl. undo, a reload continuing with the
    same future tile, and every refusal above.

### 1.4 — debug-only `EnginePreview`

- `app/.../ui/EnginePreview.kt` — a deliberately **undesigned** screen: a 4×4
  grid of plain numerals, `Score / Moves / status`, four arrow buttons, Undo and
  New game. It uses only Material theme defaults; no colour, spacing or type
  value from the frozen reference appears in it, so it cannot pre-empt a token.
- `MainActivity` shows it **only when `BuildConfig.DEBUG`** is set (the build now
  generates `BuildConfig`); the release build still starts on the phase-0 hello
  screen.
- `EnginePreviewTest` (4, Robolectric) — the seeded opening board renders, a
  `↓` click moves the two tiles to the bottom row and reads `Moves 1`, Undo
  restores `Moves 0`, and New game stays playable.

## Engine semantics in one place

| Question | Answer (code) |
|---|---|
| Empty cell | `0` (`Board`) |
| Merge order | from the leading edge inward (`Moves.mergeLine`) |
| Merges per tile per move | at most one |
| `2 2 2 2` moved left | `4 4 0 0`, +8 |
| Invalid swipe | `NoMove`; no spawn, no RNG draw, same `Game` instance |
| Spawn | value first (`nextInt(10) >= 9` → 4), then a uniform empty cell |
| PRNG | `xorshift64*`, `state` serialised; seed 0 → fixed non-zero state |
| Score | sum of merged values |
| Win | highest tile ≥ 2048 → `WON` until `keepPlaying()` |
| Game over | full board **and** no equal orthogonal neighbour |
| Undo | one snapshot: board + score + RNG state (+ moves) |
| Save | `schemaVersion: 1`, JSON, defensive `decode → null` |

## Reused (reuse-first scan for this phase)

- Engine — **BUILD**, as recorded in `docs/RESEARCH.md`. Searched again for a
  drop-in pure-Kotlin 2048 core: `gabrielecirulli/2048` (MIT — **STUDY**: its
  behaviour is the reference for merge order and the 90/10 spawn, but it is
  JavaScript and its CSS/palette is out of bounds), `alexjlockwood/
  compose-multiplatform-2048` (MIT — **STUDY**: same design space, no engine
  tests, multipliers/state handling differ), `andstatus/game2048`
  (Apache-2.0 — **STUDY**: rules only; its engine is bound to KorGE). None is
  small enough, testable enough or licence-clean enough to adopt; a 480-line
  engine with 66 tests is the cheaper, reviewable path.
- PRNG — **BUILD** (`xorshift64*` is a published algorithm, not a library).
  Searched: `kotlin.random.Random` (REJECT: not serialisable, no stable sequence
  contract across versions), `java.util.Random` (REJECT: platform class in
  `domain/`, state format is an implementation detail).
- Serialisation — **DEPEND** `kotlinx-serialization-json` 1.8.0 (Apache-2.0),
  already in the phase-0 dependency graph; no new licence shipped this phase.
- Preview UI — **DEPEND** Compose Material 3 (already shipped) with theme
  defaults only; no new dependency, no design work.
- **No licence was added or removed in phase 1.** The shipped set is unchanged.

## Evidence (before the change)

- Phase 0-D record (`docs/phases/00d-ui-reference.md`) and its run
  40070687033: the app had 5 main Kotlin files (activity, hello screen, theme,
  tokens, contrast helper) and **no `domain/` package at all** — the rules did
  not exist in code before this phase.
- `docs/ROADMAP.md` row 1 was 📋; `docs/DECISIONS.md` already fixed the engine
  constraints (own seeded PRNG, undo restores the RNG position, save is
  DataStore + kotlinx.serialization) — this phase implemented them, it did not
  re-decide them.

## What shipped

- `app/src/main/java/io/github/pabi277/tilefold/domain/` — `Board.kt` (106),
  `Moves.kt` (123), `EngineRandom.kt` (90, incl. `Spawner`), `Game.kt` (160),
  `SaveCodec.kt` (106). No Android import anywhere in `domain/`.
- `app/src/main/java/io/github/pabi277/tilefold/ui/EnginePreview.kt` (187) and
  the `BuildConfig.DEBUG` switch in `MainActivity.kt`; `buildConfig = true` in
  `app/build.gradle.kts`.
- 66 host tests in 7 new files; no new screenshot golden was recorded (the
  preview is deliberately not a designed screen, so there is nothing to pin).
- This record, roadmap row 1 → ✅, `prompt.md`, and four line-level decisions
  in `docs/DECISIONS.md` (PRNG/save, `save.v1` refusals, refused input is free,
  debug-only preview).

## What the APK shows

`app-progress-apk` (10,891,006 B) is the **debug** build, so on launch it shows
the **EnginePreview**: the title line, `Score 0 · Moves 0 · PLAYING`, a 4×4 grid
of plain numerals starting from seed `20261024` (`0,0,0,0/0,0,0,0/0,2,0,2/…`),
four arrow buttons and Undo / New game. Pressing `←` slides the tiles to the
left edge and a new 2 or 4 appears; `↑`/`↓` do the same down the columns; `Undo`
puts the board, the score **and** the next tile back exactly as they were. The
board is not styled: the frozen reference is the phase-2 target and this screen
must not pre-empt it. The release build still opens the phase-0 hello screen.

## Tests (no-device gate — `knowledge/fast-quality-no-device.md` §1)

- `./gradlew check` = `testDebugUnitTest` + `lintDebug` + `detekt` +
  `ktlintCheck`, green in run 40076597395 at `20a9648`; the `ui-reference`
  token-parity job stayed green (no token was touched).
- 85 host tests total: 66 new engine/preview tests above + the 19 phase-0/0-D
  theme and screenshot tests, whose 5 goldens are unchanged.
- **Not verifiable from this environment:** no JDK or Android SDK exists in the
  sandbox, so nothing was compiled or run locally — CI is the compiler and the
  runner. Artifact sizes are read from the Actions API (the blob host refuses
  connections here). The golden boards were produced by a Python mirror of the
  Kotlin rules and then **confirmed by CI**, which is why the three wrong
  hand-written expectations could be fixed from annotations rather than guessed
  at again.

## Gate iterations (recorded honestly)

Three rounds, each failure read from check-run annotations:

1. **run at `54bedae`** — detekt: `ReturnCount` in
   `SaveDto.toSnapshot` (5 > 4) and `NestedBlockDepth` in one property test. Fix:
   one `isImpossible()` validation point (4 conditions) and an extracted
   `assertNoGaps` helper. No rule was suppressed and no threshold was raised.
2. **`9efd97b`** — three *test* failures, all in hand-computed expectations:
   a right-move column check asserted the wrong column, one expected row had a
   `16` in the wrong cell, and a property asserted that every valid move merges
   (a pure slide is valid and scores 0). The engine output was right in all
   three; the tests were corrected, and the pure-slide case is now documented in
   the assertion's comment.
3. **`20a9648`** — green. Detekt and ktlint were clean on the engine and test
   files from the first push; only the three items above changed.

## Follow-ups (for phase 2, not this phase)

- Phase 2 must consume `Moves`/`Game` unchanged; if a screen needs a rule it
  does not find, the fix belongs in `domain/` with a test, never in the UI.
- The preview stays as the debug echo of the engine until the real `GameScreen`
  replaces it; delete it (and the `BuildConfig.DEBUG` branch) then, not before.
- `moves` is persisted but no screen shows it yet; phase 3 may drop it from the
  UI entirely without a schema bump.
- `actions/setup-java@v4` deprecation (carried since phase 0) still applies.
