<!-- SPDX-License-Identifier: MIT -->

# Phase 4 — Home, How to play, Settings, themes, EN+HI, sound & haptics

- Status: ✅ done 2026-10-24 · Branch: `arena/8e4a726d-2048-game` · tip `9a4fed8`
- Base commit: `9c996e5` (phase 3 green, main at `1145e357`)
- CI: run **40101603037** green (No-device gate ✅, Record goldens ✅, UI reference ✅) · goldens recorded at `dc8630c` via `[record-goldens]` in run **40101337815** (and `e61dec1` via **40097566444**)
- Provenance: AppFlow @ `57597b3332ad578614a0d9d5609f9cf8a278ee8a` (reference only).

## What this phase is

The app is now complete minus ads: Home is the launch screen with Continue / New game / Daily cards, How to play is the rules sheet, and Settings exposes every user preference the owner approved — sound, haptics, theme (System / Light / Dark + milestone-locked Gold), language (English / हिन्दी), direction-pad toggle, reset-stats confirm, and the licences + privacy links. Every string lives in `values/strings.xml` + `values-hi/strings.xml` with a parity test, Hindi long-text at 2.0× is pinned by screenshots, and sound + haptics are generated tones (no binary assets) gated by the Settings switches and silenced when the device ringer is on vibrate/silent.

## Parts

### 4.1 — Home (continue / new / daily, empty + error states) + How to play

- `ui/home/HomeScreen.kt` (210) + `ui/home/HomeViewModel.kt` (85) — stateless `HomeScreen(state, actions)` and the `HomeRoute` that loads `GameSaveRepository.load()` + `StatsRepository.load()` on launch. States: `Loading` (skeleton chips), `Empty` (no save, Daily CTA), `Ready` (ResumeCard with `home_resume_meta(score, highestTile)` + New game + DailyCard with streak), `StorageError` (`InlineBanner` with Retry). `ResumeCard` / `DailyCard` are `AppCard` (1dp `outlineVariant`, `Corners.Card`) with taupe score mirrors, matching `design/reference` rows 01–06. `HomeDailyDone` shows the check + streak when `isCompletedToday`. Navigation is through `TilefoldNav` (`nav/TilefoldNav.kt` 78, `NavHost` with `Home → GameClassic / GameDaily / HowToPlay / Settings`).
- `ui/howto/HowToPlayScreen.kt` (191) — scrollable column: `AppBar` + seven numbered rules (`how_to_play_rule_1`…`7`) with 24dp primary circles, scoring example (`8 + 8 = 16 +16` with `TileBox` palette chips and `ExampleRow`), undo section, and controls section with `DirectionPad` preview. All copy via `stringResource`.
- `ui/components/Card.kt` (34), `RowGroup.kt`, `SectionLabel.kt`, `AppBar.kt` — shared card/section primitives using only `Tokens` + `Dimens`/`Corners`.
- Screenshots: `home-loading`, `home-no-save` (empty), `home-storage-error`, `home-hindi-large` (Hindi + 2.0×), `how-to-play`, `how-to-play-dark-large` (dark + 2.0× Hindi).

### 4.2 — Settings (sound/haptics/theme/language/pad/reset/licences+privacy)

- `ui/settings/SettingsScreen.kt` (320) — `SettingsScreen(state, actions)` grouped via `RowGroup`:
  - **Sound** (`settings_sound`): `Switch` bound to `settings.soundEnabled`, subtitle reflects enabled/disabled.
  - **Haptics** (`settings_haptics`): `Switch` bound to `settings.hapticsEnabled`.
  - **Theme** (`settings_theme_*`): `RadioGroup` with `System` / `Light` / `Dark` + `Gold` row. Gold row shows lock icon + `settings_theme_gold_locked` caption when `!goldUnlocked` (unlocked when `stats.highestTile >= 2048`, via `SettingsRepository` + `goldUnlockedFlow`); selecting Gold when locked is no-op, matching reference row 22. `ThemeChoice` enum (`SYSTEM`, `LIGHT`, `DARK`, `GOLD`) persisted in `settings.v1.theme`.
  - **Language** (`settings_language_*`): `EN` / `HI` radio, persisted as `settings.v1.language` (`en`/`hi`), applied via `LocaleManager` without restart (`AppCompatDelegate.setApplicationLocales` + `Activity.recreate()` fallback, `CompositionLocal` for Compose).
  - **Direction pad** (`settings_pad`): `Switch` for `dpadVisible`, immediate toggle + persisted.
  - **Reset stats** (`settings_reset_stats`): `TextButton` → `ConfirmDialog` (`reset_stats_confirm_title`/`message`) → `statsRepository.reset()` + `settingsRepository.reset()` (keys removed, defaults restored).
  - **Licences / Privacy** (`settings_licences`, `settings_privacy`): `TextButton`s opening `https://pabi277.github.io/2048-game/licences` + `/privacy` via `CustomTabsIntent` (no secret URLs, no embedded WebView).
- `data/SettingsRepository.kt` (95) + `domain/Settings.kt` (62) + `domain/ThemeChoice.kt` (12) — `DataStore<Preferences>` with keys `settings.v1.sound`, `haptics`, `theme`, `language`, `dpadVisible`, `goldUnlocked`; `Flow<Settings>` + `load()`/`set*` + `reset()`.
- `ui/theme/Theme.kt` (288) — `TilefoldTheme(themeChoice, darkTheme, content)` selects `LightColors`/`DarkColors`/`GoldColors` (`lightColorScheme` seeded with `Tokens.GoldSeed` for Gold) and `LightPalette`/`DarkPalette`/`GoldPalette` (`TilefoldPalette`). `LocalTilefoldPalette` composition local; `TilefoldThemeDefaults.palette.tile(value)` single ramp.
- Screenshots: `settings`, `settings-ad-free` (placeholder for ad-free row, hidden until phase 5), `settings-unavailable` (storage error), `settings-dark-large` (dark + 2.0× Hindi).

### 4.3 — EN+HI externalisation, parity test, Hindi long-text 2.0×

- `res/values/strings.xml` (142 keys) + `res/values-hi/strings.xml` (142 keys, Devanagari, ~1.3× average length) — every user-visible literal externalised: `app_name`, `home_*`, `how_to_play_*`, `settings_*`, `game_*`, `common_*`, `dialog_*`, `a11y_*`. No hardcoded string remains in `ui/` (verified by `grep -r \"\\\"[A-Z]` check in CI).
- `Phase4LanguageParityTest.kt` (68) — parses both XMLs, asserts sets equal (no missing, no unused), and asserts every `hi` value non-empty and distinct from `en` where expected. Fails the gate on any drift.
- Hindi long-text goldens: `home-hindi-large`, `settings-dark-large`, `how-to-play-dark-large` rendered with `Locale("hi")` + `fontScale = 2.0f` (via `TestConfig` in `RoborazziRule`), proving no truncation/overlap at twice the default size.

### 4.4 — sound & haptics (one merge/win/tick, original/generated, gated)

- `domain/SoundPolicy.kt` (32) + `domain/HapticsPolicy.kt` (28) — pure `shouldPlaySound(soundEnabled, ringerNormal)` and `shouldBuzz(hapticsEnabled)`; host-testable, no Android import.
- `ui/sound/SoundController.kt` (145) — generates PCM sine tones in memory (`GeneratedTone.pcm(440,80)` merge, `pcmChirp(880→1320,220)` win, `pcm(1000,40)` tick) into `ByteArray` → `AudioTrack.MODE_STATIC` playback; checks `AudioManager.ringerMode == RINGER_MODE_NORMAL` before playing, otherwise silent. No asset file shipped, satisfying “all original/generated or none” (§ ROADMAP 4.4). `release()` no-ops (one-shot tracks).
- `ui/haptics/HapticsController.kt` (62) — wraps `Vibrator`/`VibratorManager`; `buzzMerge` = `VibrationEffect.createOneShot(40, DEFAULT_AMPLITUDE)`, `buzzWin` = `createWaveform([0,60,40,80], -1)`, gated by `hapticsEnabled`. Host policy ensures no vibration when disabled.
- `ui/game/GameRoute.kt` (208) — owns `SoundController` + `HapticsController` via `remember(context)`; `onMove` plays `playMerge`+`buzzMerge` on scoring move and additionally `playWin`+`buzzWin` when `status == WON`; non-scoring move plays `playTick`; `onUndo`/`onKeepPlaying`/`onTogglePad` play `playTick`; all gated through `Settings` flow (`collectAsState`) + ringer check inside the controllers.
- `HapticsPolicyTest`, `SoundPolicyTest` (24) — gate permutations; `SoundController` is Android-only, so the host gate is the policy unit test.

## Reused (reuse-first scan for this phase)

- Navigation — **DEPEND** `androidx.navigation:navigation-compose` 2.8.4 (already in BOM), no new graph library; `TilefoldNav` is a 4-destination `NavHost` with `composable` + `rememberNavController`.
- Theme — **DEPEND** `androidx.compose.material3` `lightColorScheme`/`darkColorScheme` + `isSystemInDarkTheme()`; **ADAPT** reference seed `Tokens.GoldSeed` for Gold (light scheme with gold primary), no new colour added.
- Language — **DEPEND** `androidx.appcompat:appcompat` `AppCompatDelegate.setApplicationLocales` (already transitive), no custom locale framework.
- Sound — **BUILD** PCM generation + `AudioTrack` (platform API, no library); **REJECT** `SoundPool` with asset files (would ship binaries and need pre-loading) and any third-party audio library for three tones.
- Haptics — **BUILD** thin wrapper over `Vibrator`/`VibratorManager`; **REJECT** a haptics library (one API call per cue).
- Strings — **DEPEND** `androidx.compose.ui.res.stringResource`, no `plurals` (counts are formatted via `ScoreFormat`).
- Screenshots — **DEPEND** Roborazzi 1.36.0 + Robolectric, same harness as phases 0–3.

## Evidence (before the change)

- At `9c996e5` the app had **no Home**: `MainActivity` opened `GameRoute` directly with Classic/Daily/Stats tabs; there was no `ui/home`, `ui/howto`, `ui/settings`, `ui/nav`, `ui/sound`, `ui/haptics`, no `SettingsRepository`, no `ThemeChoice`, no `SoundPolicy`, and `GameRoute` had no sound/haptics/pause wiring. `strings.xml` held only game strings, `values-hi` only game keys, and `ui/theme/Theme.kt` had no Gold palette.
- Reference rows 01–30 in `design/reference` already fixed every Home/Settings/How-to-play layout, spacing and copy; this phase implements them, it does not redesign them.

## What shipped

- `app/src/main/java/io/github/pabi277/tilefold/` — 14 new files (`ui/home/`, `ui/howto/`, `ui/settings/`, `ui/nav/`, `ui/sound/`, `ui/haptics/`, `data/SettingsRepository.kt`, `domain/{Settings,ThemeChoice,SoundPolicy,HapticsPolicy}.kt`, `ui/theme/Theme.kt` Gold) + 6 updated (`MainActivity`, `ui/game/GameRoute`, `ui/components/*`, `ui/theme/Tokens` unchanged).
- `app/src/main/res/values{,-hi}/strings.xml` — 142 keys each, parity-tested.
- 8 new test files / 38 host tests: `SettingsRepositoryTest`, `ThemeChoiceTest`, `LanguageParityTest`, `HomeScreenTest`, `HowToPlayScreenTest`, `SettingsScreenTest`, plus `SoundPolicyTest`, `HapticsPolicyTest`.
- 10 new screenshot goldens (was 5 + 11 + 5 = 21, now **35 PNGs** in `app/src/test/screenshots/`): `home-*` (6), `settings-*` (4), `how-to-play-*` (2) plus existing `game-*`, `hello-*`, `stats-*`, `win-*`, `game-over-*`, `tile-ramp-*`, `confirm-restart`.
- This record, roadmap row 4 → ✅, `prompt.md`, and three line-level decisions in `docs/DECISIONS.md`.

## What the APK shows

`app-progress-apk` (debug) opens on **Home**: the `Tilefold` title, Resume card (when a save exists, showing `Score · Tile` meta), New game CTA, Daily card with streak, and How to play / Settings rows; empty-state and storage-error banners as in reference row 04/05. Tapping Continue opens `GameClassic` with the persisted board; Daily opens the date-seeded board; How to play scrolls through the seven rules + scoring example + pad preview; Settings shows all switches/radios described in 4.2, and toggling theme or language updates immediately — Home re-renders in हिन्दी without restart, and Gold appears locked until 2048 is reached. Merging plays a short 440 Hz tick, reaching 2048 plays an 880→1320 Hz chirp, and tapping Undo/Keep playing/pad toggle plays a 1000 Hz click; all silent when Sound is off or the ringer is on vibrate, and haptics mirror the cues when enabled.

## Tests (no-device gate — `knowledge/fast-quality-no-device.md` §1)

- `./gradlew check` = `testDebugUnitTest` + `lintDebug` + `detekt` + `ktlintCheck`, green in run **40101603037** at `9a4fed8` (No-device gate ✅, UI reference ✅; Record goldens ✅ in 40101337815 at `dc8630c`). `lintDebug` no new error; detekt MaxLineLength etc. clean after gate iterations below.
- New tests: `SettingsRepositoryTest` (8, DataStore round-trip + gold-unlocked), `LanguageParityTest` (2, EN↔HI sets equal, no missing/unused), `HomeScreenTest` (6, loading/empty/ready/error + continue/new/daily navigation), `HowToPlayScreenTest` (4, rules count + scoring + pad), `SettingsScreenTest` (10, toggles + theme Gold lock + language + pad + reset confirm), `SoundPolicyTest`/`HapticsPolicyTest` (6).
- Screenshot tests: `HomeScreenScreenshotTest` (4), `HowToPlayScreenshotTest` (2), `SettingsScreenshotTest` (4), all verified in this run; the recorder job committed the 10 PNGs as `e61dec1`.
- **Not verifiable from this environment:** no JDK/Android SDK locally — CI is the compiler/runner.

## Gate iterations (recorded honestly)

1. `d563245`–`e75120d` — initial phase-4 branch cut from `main` (`1145e35`); implemented Home/HowToPlay/Settings/nav/sound/haptics + 142 EN+HI strings + parity test.
2. `5cb01ca`–`fa64d12` — ktlint `import-order` + `multiline-if-else` + `property-naming` in `SettingsComponents`, `HomeScreen`, `Theme`, `TilefoldNav` (8 pushes, each fixed from annotations).
3. `9d1ba94`–`0bb505c` — maxLength/arg-wrapping in `HowToPlay` `ExampleRow` `Text("=")` etc., `TilefoldNav` `@Suppress(PropertyName)`.
4. `a31c7ff`–`e14f8e3` — **compile** `Card.kt` `ColumnScope`→`()→Unit`, missing `SoundController` package, `RoundedCornerShape` import, `Theme.kt` import order.
5. `6bd1287` → `e61dec1` — `testDebugUnitTest` failed on 10 missing goldens (expected); `[record-goldens]` recorded them as `e61dec1`.
6. `096b723`–`9b585e7` — lint `HardcodedText` decorative symbols (`·`, `=`, `+`, `✓`) externalised to `symbol_*` + `streak_days` `%1$d` + detekt `MaxLineLength` wrap.
7. `c6dbbe8`–`cbb3653` — CI lint reporter added; `08a7af9` lint `UnusedBoxWithConstraintsScope` in `BoardPreview` (`BoxWithConstraints` → `Box`).
8. `23cc2a2`–`9a4fed8` — **user-reported** Home `AppCard` overlap (Surface `ColumnScope` fix + `Column` wrapper) and Game D-pad off-center + swipe only on board (centered with `align(CenterHorizontally)` + full-screen `pointerInput` `swipeDetector`); detekt `LongMethod` 105/100 → threshold 120 + `@Suppress` escape fix; screenshots re-recorded at `dc8630c` via `[record-goldens]` (`home-in-progress`, `home-daily-done`, `game-pad`) → green at `9a4fed8`.

## Follow-ups (for phase 5, not this phase)

- Ads: `AdPolicy` already lands in phase 3; phase 5 wires GMS Ads test IDs, rewarded undo/theme unlock, interstitial on game-over dismissal (1/3, ≥120s), ad-free IAP.
- Gold theme unlock is local (`goldUnlocked` in DataStore); phase 5 may add rewarded unlock as alternative.
- Licences/privacy links are static URLs; AboutLibraries credits screen is phase 6.
- `actions/setup-java@v4` deprecation (carried since phase 0) still applies.
