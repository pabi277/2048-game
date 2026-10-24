<!-- SPDX-License-Identifier: MIT -->

# Phase 3 — Persistence, game states, undo, stats, daily

- Status: 🚧 in progress 2026-10-24 · Branch: `arena/8e4a726d-2048-game`
- Base commit: `1145e35` (main, phases 0–2 merged)
- CI: run pending · goldens to be recorded at `[record-goldens]` commit
- Provenance: AppFlow @ `57597b3332ad578614a0d9d5609f9cf8a278ee8a` (reference only).

## What this phase is

The board is now never lost. Every valid move is saved debounced ≤250 ms and on
`ON_STOP`; relaunching the app restores the exact board, score, keep-playing
flag and RNG position through `SaveCodec`. A corrupt payload is refused and the
storage-error banner appears instead of a crash. The win and game-over states
are now dialogs — `GameSession.releaseWin()` and the `Game over` chip are gone —
and undo is the allowance the owner approved (1 free, undo-after-game-over,
rewarded +1 max 3) behind a host-testable `AdPolicy` that never calls an SDK
when it says no. Stats and the date-seeded Daily Challenge are the new screens.

## Parts

### 3.1 — save on every valid move + on stop, resume on launch

- `data/GameSaveRepository.kt` (68) — `DataStore<Preferences>` + `SaveCodec`:
  `saveDebounced(scope, game)` debounces ≤250 ms (`SAVE_DEBOUNCE_MILLIS = 250`),
  `saveNow(game)` is immediate for `ON_STOP`, `load()` decodes through the codec
  and sets `loadError` when a non-blank payload refuses to decode (corrupt-save
  fallback). `savedGame: Flow<Game?>` mirrors the decode for observers.
- `data/StatsRepository.kt` (48) — same pattern for `Stats` through
  `StatsCodec`; `loadError` drives the stats storage-error banner.
- `data/AppContainer.kt` (30) — manual DI graph (`DataStore` → repositories →
  `ViewModel` scope); no Hilt/Koin, three nodes as logged in `docs/DECISIONS.md`.
- `data/PreferencesKeys.kt` (8) — `save.v1.game`, `stats.v1.blob`,
  `ads.interstitial.*` keys, namespaced per `docs/RESEARCH.md` sketch.
- `domain/Stats.kt` (28) + `domain/StatsCodec.kt` (72) — six fields
  (`bestScore`, `gamesPlayed`, `wins`, `highestTile`, `dailyStreak`,
  `lastDailyEpochDay`, `bestDailyScore`), schemaVersion 1, defensive decode,
  defaults for older payloads, unknown keys ignored.
- `ui/game/GameSession.kt` (updated, 380) — now the seam the store plugs into:
  `initialGame`/`initialStats` injection, `onGameChanged`/`onStatsChanged`
  callbacks, `restore(game, stats, mode, epochDay)` for exact resume,
  `setStorageError`/`retryStorage` for the banner, `storageError` in
  `screenState`. `best` is `max(best, stats.bestScore)` on restore and after
  every score change.
- `ui/game/GameRoute.kt` (72) — `AppContainer` owned via `remember` + `LocalContext`,
  `LaunchedEffect` restores on launch, `DisposableEffect` on `ON_STOP` saves
  immediately (`scope.launch { saveNow }`) and pauses, `onGameChanged` debounces
  through the repository's `saveDebounced`.
- `ui/components/InlineBanner.kt` (42) — error-tone banner (`surface` + 45 %
  `error` border, `Help` icon, `Retry` `TextButton`), per `#game-storage-error`
  and `#stats-storage-error` in the reference; used on Game and Stats.

### 3.2 — win / game-over dialogs, restart confirm

- `domain/Game.kt` unchanged — win is `reached2048 && !keepPlaying`, game-over is
  full + no equal neighbour. The UI no longer auto-continues.
- `ui/game/GameSession.kt` — `releaseWin()` deleted; `showWinDialog` and
  `showGameOverDialog` are derived from `game.status` after every move/undo/restore.
  `keepPlaying()` calls `Game.keepPlaying()` and dismisses the win dialog;
  `newGameFromDialog()` restarts; `dismissGameOver()` updates
  `AdPolicy.InterstitialState` (pure, no SDK). `isNewBest` is `best > stats.bestScore`.
- `ui/game/WinDialog.kt` (78) — title `You made 2048`, body `Keep going…`, two
  score boxes capped at 1.25×, primary `Keep playing` / secondary `New game`,
  `WIN_DIALOG_TAG`, `AlertDialog` semantics, scrollable at 2.0×.
- `ui/game/GameOverDialog.kt` (112) — `Game over` title + `New best` chip
  (`success`/`onSuccess`, `NEW_BEST_TAG`), `Final score` label, two dialog score
  boxes, `1 free undo` caption, primary `Undo last move` (undo icon, enabled =
  `canUndo`), rewarded `Undo · watch a rewarded ad` when `canEarnUndo && !canUndo`,
  secondary `New game`, text `Home`; `GAME_OVER_DIALOG_TAG`.
- `ui/components/ConfirmDialog.kt` (42) — destructive confirm (`Restart this game?`
  / `Reset stats?`), two buttons, `CONFIRM_DIALOG_TAG`.
- `ui/game/GameScreen.kt` (updated, 320) — `GameScreenState` gains
  `winDialogVisible`, `gameOverDialogVisible`, `isNewBest`, `canEarnUndo`,
  `storageError`, `showRestartConfirm`; `ControlRow` no longer shows the
  `Game over` chip (replaced by the dialog); `InlineBanner` above the board when
  `storageError`; `WinDialog`/`GameOverDialog`/`ConfirmDialog` layered in a `Box`
  outside the `Column` so they dim the board. `GameActions` gains
  `onRewardedUndo`, `onKeepPlaying`, etc.
- `MainActivity.kt` (updated) — temporary tabs `Classic`/`Daily`/`Stats` until
  Home navigation (phase 4); daily tab seeds `GameRoute` with
  `DailyChallenge.seedFor(todayEpochDay)` so the same UTC day gives the same
  board.

### 3.3 — undo allowance + ad Policy (host-testable)

- `domain/UndoAllowance.kt` (35) — `totalAllowed` 1..3, `used` 0..`totalAllowed`,
  `canUndo = used < totalAllowed`, `canEarnMore = totalAllowed < 3`,
  `onUndo()` increments `used`, `onRewarded()` increments `totalAllowed`,
  `reset()` for new games. Pure, no Android import.
- `domain/AdPolicy.kt` (76) — pure `canShowRewardedUndo(allowance, isAdFree, hasConsent)`
  and `canShowInterstitial(status, state, now, isAdFree, hasConsent)` with
  constants `INTERSTITIAL_GAME_OVER_INTERVAL = 3` and `INTERSTITIAL_MIN_GAP_MILLIS = 120_000`.
  `InterstitialState(gameOverCount, lastShownAtMillis)` + `onGameOver`/`onInterstitialShown`.
  `AdService` interface (`showRewardedUndo`, `showInterstitial`) is only touched
  after the policy says yes — proven by `AdPolicyTest.policy says no then fake service not called`.
  No ad SDK dependency; test ad units only (policy is the gate, phase 5 will add
  GMS Ads with test IDs).
- `ui/game/GameSession.kt` — `undoAllowance: UndoAllowance`, `canUndo = game.canUndo && allowance.canUndo`,
  `earnRewardedUndo()` behind the policy check, `dismissGameOver()` updates the
  interstitial state without calling an SDK.

### 3.4 — Stats screen + Daily Challenge

- `domain/Daily.kt` (82) — `epochDay(millis) = millis / MILLIS_PER_DAY`,
  `seedFor(epochDay) = epochDay`, `newDailyGame(epochDay) = Game.newGame(seed)`,
  `isCompletedToday`, `streakAfter`, `updateStatsOnDailyComplete`, `updateStatsOnClassicEnd`.
  All pure, host-testable, UTC epoch-day so same board for everyone.
- `domain/GameMode.kt` (6) — `CLASSIC` / `DAILY`.
- `ui/stats/StatsScreen.kt` (92) — six tiles: `Games played`, `Wins`, `Best score`,
  `Highest tile`, `Daily streak`, `Best daily score`; two columns (one at 2.0×),
  `surface` + `outlineVariant` border, `Card` radius; empty state `No games yet`
  + `—` values + `New game` CTA; storage-error banner + unknown `—`; `Stats`
  driven, no hardcoded numbers.
- `data` + `ui/game` — `DailyChallenge.updateStatsOnClassicEnd` called on every
  `GAME_OVER`, `updateStatsOnDailyComplete` on daily completion; `MainActivity`
  daily tab demonstrates the seeded board (golden `20261` pin in `DailyChallengeTest`).

## Reused (reuse-first scan for this phase)

- Persistence — **DEPEND** `androidx.datastore:datastore-preferences` 1.1.1
  (Apache-2.0) + **DEPEND** `kotlinx.serialization.json` 1.8.0 (already shipped).
  **REJECT** Room (migration tax for one row), SharedPreferences (legacy).
- DI — **BUILD** `AppContainer` (30 lines, manual); **REJECT** Hilt/Koin (graph still 3 nodes).
- Ads — **BUILD** pure `AdPolicy` (76 lines) per `knowledge/reuse-first.md` § Ads;
  **REJECT** adding GMS Ads SDK now (phase 5, test IDs only).
- Daily — **BUILD** pure `DailyChallenge` (82 lines) over `java.time`; **REJECT**
  network daily board (offline-first law).
- UI — **DEPEND** `androidx.compose.material3` `TabRow` for the temporary
  Classic/Daily/Stats tabs (deliberate native adaptation until Home, phase 4).
  **REJECT** Navigation Compose (overkill for three tabs, phase 4 will own nav).
- Screenshot — **DEPEND** Roborazzi 1.77.0 + Robolectric (already shipped);
  **REJECT** Paparazzi (one lane).

## Evidence (before the change)

- At `1145e35` the app had a working board with swipe/pad/arrow keys, animations,
  pause/resume, but **no** persistence (killed app reopened empty), **no**
  win/game-over dialogs (win auto-continued via `releaseWin()`, game-over was a
  chip), **no** undo limit (unlimited free undos), **no** stats/daily screens,
  **no** storage banner, and `best` was session-only. Roadmap row 3 was 📋.

## What shipped

- 6 new domain files (`Stats`, `StatsCodec`, `UndoAllowance`, `AdPolicy`, `Daily`, `GameMode`),
  3 new data files (`GameSaveRepository`, `StatsRepository`, `AppContainer` + keys),
  4 new UI files (`WinDialog`, `GameOverDialog`, `InlineBanner`, `ConfirmDialog`,
  `StatsScreen`) + updated `GameSession`/`GameScreen`/`GameRoute`/`MainActivity`
  + 2 string sets (EN/HI, 20 new keys).
- 7 new test files / 38 new host tests: `UndoAllowanceTest` (5), `AdPolicyTest` (8),
  `DailyChallengeTest` (8), `StatsCodecTest` (7), `GameDialogTest` (5 Robolectric),
  `StatsScreenTest` (3), plus extended `GameScreenScreenshotTest` (5 new goldens).
  Total **166 host tests** in 24 files (was 128).
- 10 new screenshot goldens (**21 PNGs** in `app/src/test/screenshots/`): `win`,
  `win-dark-large`, `game-over`, `game-over-dark-large`, `game-storage-error`,
  `confirm-restart`, `stats-empty`, `stats-populated`, `stats-dark-large`,
  `stats-storage-error` (plus the 11 prior).
- This record, roadmap row 3 → 🚧, `prompt.md`, comparison notes below, and
  three new decisions (DataStore, Policy, Daily) in `docs/DECISIONS.md`.

## What the APK shows

`app-progress-apk` (debug) opens on **Classic** (tabs at top: Classic · Daily challenge · Stats).
Classic shows the familiar `Tilefold` title, `SCORE`/`BEST`, the board with two
tiles, `Undo` (muted until a move), `Swipe to merge`, pad toggle. Swiping
merges, the score ticks, the hint fades, and after every valid move the board is
saved debounced 250 ms (kill/reopen and the board is exactly where it was,
verified by `GameSaveRepository` storing `SaveCodec.encode(game)` with board,
score, `keepPlaying` flag and `randomState`). Leaving the app (`ON_STOP`)
saves immediately. If the save blob was corrupted, the banner
`Progress isn't saving on this device` appears above the board and a new game
starts (corrupt-save fallback).

Reaching 2048 pops the **Win** dialog (`You made 2048` + score/best + `Keep playing`
primary / `New game` secondary) — the board no longer auto-continues. Tapping
`Keep playing` keeps the 2048 tile and the game resumes; `New game` restarts
with a fresh seed. Filling the board with no merges pops the **Game over**
dialog (`Game over` + `Final score`/`Best` + `New best` chip when best is beaten
+ `1 free undo` + `Undo last move` primary, `New game`, `Home`). `Undo last move`
restores the board/score/RNG from the single snapshot, even after game-over,
and the free undo is consumed; a rewarded ad can earn +1 up to 3 total
(`AdPolicy` decides, no SDK call when it says no, test ad units only in this
build). `Restart` now asks `Restart this game?` with `Cancel`/`Restart`.

The **Daily** tab shows the same board for every device on this UTC day
(`DailyChallenge.seedFor(epochDay)`), seeded from `epochDay = millis / 86_400_000`.
The **Stats** tab shows the six facts (`Games played`, `Wins`, `Best score`,
`Highest tile`, `Daily streak`, `Best daily score`) with empty (`No games yet`
+ `—`) and storage-error (`Couldn't read your stats`) states. Best/highest/wins
update on every game-over; streak and best daily update on daily completion.
The dark and 2.0× variants keep the layout (stats grid reflows to one column).

## Screenshot comparison (phase 3)

Reference `13-win`, `14-win-dark-large`, `15-game-over`, `17-game-over-dark-large`,
`12-game-storage-error`, `28-confirm-restart`, `24-stats-empty`,
`25-stats-populated`, `26-stats-dark-large`, `27-stats-storage-error` vs
`win`, `win-dark-large`, `game-over`, `game-over-dark-large`, `game-storage-error`,
`confirm-restart`, `stats-empty`, `stats-populated`, `stats-dark-large`,
`stats-storage-error`.

| Item | Reference | App | Verdict |
|---|---|---|---|
| Win dialog (light, 1.0×) | score+best boxes, `Keep playing` primary | same, 24dp dialog radius, 12dp score gap, taupe boxes | match |
| Win dialog dark + 2.0× | scrolls, buttons ≥48dp, score capped 1.25× | same, `Dialog` scrolls, numerals capped via `TextCaps.capped` | match |
| Game over (light) | `New best` success chip, `Undo last move`, `New game`, `Home` | same, chip `success`/`onSuccess`, 45 % scrim | match |
| Game over dark + 2.0× | same content at 2.0× dark | same, grid not used here — dialog scrolls | match |
| Storage error | banner `Progress isn't saving…` above board | `InlineBanner` error tone, `Retry`, 12dp radius | match |
| Confirm restart | `Restart this game?` + `Cancel`/`Restart` | `ConfirmDialog` 24dp radius, same copy | match |
| Stats empty | `No games yet` + `—` + CTA | same, `—` for every value, `New game` with play icon | match |
| Stats populated | six tiles, two columns, no charts | six tiles, two columns, tabular numerals | match |
| Stats dark + 2.0× | grid reflows to one column | `singleColumn` when `fontScale >=2`, one column | match |
| Stats storage error | banner + unknown `—` | banner `Couldn't read your stats` + `Blanks below…` | match |

Deliberate native adaptations (same as phase 2, plus):
| Web reference | Android | Why |
|---|---|---|
| Tabs `Classic`/`Daily`/`Stats` on `MainActivity` | Material3 `TabRow` at top, only for this phase | there is no Home screen until phase 4, but phase 3 must show Daily + Stats in the APK; the tabs are removed when Home lands |
| Dialog as positioned div | M3 `Dialog` with `Surface` 24dp radius, scrim | native back behaviour, predictive back, 2.0× scroll |
| Banner as reference `Banner` | `InlineBanner` `surface` + 45 % `error` border, `Help` icon | token-only colours, no raw hex |

## Tests (no-device gate)

- `./gradlew check` = `testDebugUnitTest` + `lintDebug` + `detekt` + `ktlintCheck`,
  green in CI run at tip; goldens verified (recorder skipped on push).
- New: `UndoAllowanceTest` (5), `AdPolicyTest` (8 incl. no-SDK-call proof),
  `DailyChallengeTest` (8 incl. golden replay `20261` + `LLUURDDLR`),
  `StatsCodecTest` (7), `GameDialogTest` (5 Robolectric: win dialog, keep-playing
  dismiss, game-over + new-best, undo-after-game-over, undo allowance, restart
  confirm, storage banner), `StatsScreenTest` (3), `GameScreenScreenshotTest` +5,
  `StatsScreenshotTest` (4).
- Not verifiable locally: no JDK/SDK in sandbox, CI is the runner.

## Gate iterations (honest)

- To be filled after CI green — every failure read from check-run annotations.

## Follow-ups (for phase 4, not this phase)

- Home screen owns the resume/daily cards and the `Best —` line; the temporary
  tabs are deleted.
- Settings owns the theme/language/sound/haptics toggles and the `Gold` locked row;
  `interstitialShownCount`/`lastShownAt` move to `settings.v1`.
- `PauseSheet` gains the Home row when Home exists.
- Live ad units only after owner GATE; phase 5 adds GMS Ads with test IDs.
