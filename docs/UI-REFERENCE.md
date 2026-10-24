<!-- SPDX-License-Identifier: MIT -->

# UI reference — Tilefold (working name)

> The design specification for this app's UI: a disposable, phone-shaped
> UI-only website, owner-approved **before any app UI code**
> (`knowledge/ui-reference-site.md`; stack catalogue:
> `knowledge/web-ui-references.md`). The reference's code is never copied into
> the app — only tokens, measurements and the component inventory cross over.
>
> **Status: reference site BUILT and PRESENTED for review (phase 0-D,
> 2026-10-24). Tokens are NOT frozen yet — they freeze only on the owner's
> explicit approval in chat.** Every value below is what the site actually
> renders (`design/reference/src/design.js`); `npm run verify` fails if this
> file and the site disagree, and if any tile numeral loses contrast.

## Reference

- Stack (DECIDE-ONCE, one only): **Option A — Vite + Tailwind CSS + Konsta UI
  (Material theme) + Lucide icons**, chosen 2026-10-24 (`docs/DECISIONS.md`).
- Location: `design/reference/` in this repo (disposable; never shipped, never
  inside `app/`, never in the APK, never imported by Gradle).
- Phone frame: fixed **390 × 844** surface (plus a 10px bezel drawn by the
  harness), one column, app navigation, no hover states, no landing-page
  patterns. 1 CSS px here = **1 dp** in the app, so every measurement below is
  readable as an Android value.
- **30 review states** = the 27 required rows + restart confirm + reset-stats
  confirm + Home in Hindi, plus a tokens/contrast page.
- Run it: `cd design/reference && npm ci && npm run dev` → `http://localhost:5180`
  (states: `#/game-mid`, `#/settings`, … · gallery: `/?gallery=1` · tokens:
  `#/tokens`). No build step is needed for review; `npm run build` only makes an
  optional static copy and is not part of CI's app gate.
- Archived renders of every state: `design/reference/screenshots/` (30 phone
  PNGs at 2×, one tokens page, one gallery sheet) — these are the images phase 2+
  compares their Compose screenshots against.
- Copy: English source + a Hindi run of the same screens, so length, wrapping
  and line height are reviewed in Devanagari and not guessed. Hindi here is a
  **length probe**: the reviewed string set lands in phase 4.

## Approval

| Item | Value |
|---|---|
| Presented for review | **2026-10-24** (phase 0-D), 30 states + tokens page |
| Owner decision | **pending** — no freeze has happened |
| Corrections received | round 1 open |
| Freeze commit | — (filled when the owner approves) |

On approval this table gets the date, the stack, the coverage statement and the
freeze commit sha, and the decision is logged in `docs/DECISIONS.md`. After
approval, changing the **design direction** (stack, visual language, palette
family) re-opens this gate; per-screen fixes update the reference first, then
the screen — never the reverse.

## Screens × states (every row is a reference page)

Required state vocabulary: **content** · **loading** (local only: brief
skeleton/splash) · **empty** · **error** (storage read/write failure) · **dark** ·
**large-font** (Android font scale 2.0 equivalent, and a Hindi long-text run).
"Ads" states exist only after Phase 5 ships and appear in the reference as
*slots*, never as real ad art.

Status: 🎨 = rendered in the reference, awaiting approval · ✅ = approved.

| # | Screen / state | What it shows | Reference page | Target composable(s) | Status |
|---|---|---|---|---|---|
| 1 | **Home — content (no saved game)** | Big title, one-line subtitle, a **decorative still of the board** (what the game looks like, not a button), primary "New game", secondary "Daily challenge", text links "How to play · Stats · Settings", best score line | `#home-no-save` | `HomeScreen`, `BoardPreview`, `PrimaryButton`, `SecondaryButton`, `TextLinkRow`, `BestScoreLine` | 🎨 |
| 2 | **Home — content (game in progress)** | Same, with "Continue" as primary (score + highest tile + mode chip) and "New game" demoted to a text action with a confirm dialog | `#home-in-progress` | `HomeScreen`, `ResumeCard`, `ConfirmDialog` | 🎨 |
| 3 | **Home — daily completed today** | Daily card showing today's score, streak chip, "Played — come back tomorrow" disabled state | `#home-daily-done` | `DailyCard`, `StreakChip` | 🎨 |
| 4 | **Home — empty/never played** | Best score shown as "—", stats teaser hidden | `#home-empty` | `HomeScreen` (empty variant) | 🎨 |
| 5 | **Home — storage error** | Inline non-blocking banner: "Couldn't read your saved game" + Retry; New game still works | `#home-storage-error` | `InlineBanner` (error tone) | 🎨 |
| 6 | **Home — loading** | 1-frame splash: app mark + board skeleton | `#home-loading` | `SplashScreen`, `BoardSkeleton` | 🎨 |
| 7 | **Game — new game (playing)** | Title row (small), SCORE + BEST boxes (taupe), home + restart icon buttons above the rounded board, 4×4 board, undo + direction-pad toggle below, "swipe to merge" hint that fades after first move | `#game-new` | `GameScreen`, `ScoreBox`×2, `BoardFrame`, `TileGrid`, `IconButtonRow`, `UndoButton` | 🎨 |
| 8 | **Game — mid-game (playing)** | Same; score ticking, mixed tile values incl. 64/128/256, one merge animating | `#game-mid` | `GameScreen`, `Tile` (animated) | 🎨 |
| 9 | **Game — paused** | Board dimmed under a bottom sheet: Resume / Restart / Home; auto-shown when the app backgrounds | `#game-paused` | `PauseSheet` | 🎨 |
| 10 | **Game — dark** | Full dark palette, tile ramp dark variant, same hierarchy | `#game-dark` | `GameScreen` (dark) | 🎨 |
| 11 | **Game — large font** | 2.0× font scale: score boxes keep their numerals on one line, board keeps square aspect, buttons keep ≥48dp | `#game-large-font` | `GameScreen` (fontScale) | 🎨 |
| 12 | **Game — storage error** | Banner above the board: "Progress isn't saving on this device" + Retry; play continues | `#game-storage-error` | `InlineBanner` | 🎨 |
| 13 | **Win (2048 reached)** | Dialog over the board: "You made 2048" + score + best + "Keep playing" (primary) / "New game" (secondary) | `#win` | `WinDialog` | 🎨 |
| 14 | **Win — dark / large font** | Same dialog on dark; scrollable when text grows | `#win-dark-large` | `WinDialog` | 🎨 |
| 15 | **Game over** | Dialog over the board: final score, best, "New best" chip if beaten, "Undo last move" (1 free, then rewarded), "New game", "Home" | `#game-over` | `GameOverDialog`, `NewBestChip`, `RewardedUndoButton` | 🎨 |
| 16 | **Game over — after dismissal (ad slot, Phase 5 only)** | What the user sees *after* closing the dialog: normal Home/board, interstitial only if the cap allows; reference shows the suppressed case by default | `#game-over-dismissed` | `AdSlotPlaceholder` (reference only) | 🎨 |
| 17 | **Game over — dark / large font** | Dialog variants | `#game-over-dark-large` | `GameOverDialog` | 🎨 |
| 18 | **How to play** | Numbered rules (slide, merge, one merge per move, 90/10 spawn, score = merge sum, win at 2048 + keep playing, game-over rule), scoring example, undo rules, controls (swipe + pad + arrow keys) | `#how-to-play` | `HowToPlayScreen`, `NumberedRule`, `ExampleRow` | 🎨 |
| 19 | **How to play — dark / large font** | Scrollable long text; Hindi run for length testing | `#how-to-play-dark-large` | `HowToPlayScreen` | 🎨 |
| 20 | **Settings — content** | Sections: Play (Sound, Haptics, direction pad), Appearance (Theme System/Light/Dark + Gold locked, Language English/हिन्दी), Progress (Reset stats — destructive confirm), Ads ("Remove ads" purchase row), About (version, Licences, Privacy policy) | `#settings` | `SettingsScreen`, `SettingsRow`, `SwitchRow`, `SegmentedChoice`, `LockedThemeRow`, `PurchaseRow` | 🎨 |
| 21 | **Settings — ad-free active** | Purchase row replaced by an "Ad-free" state; rewarded entries hidden | `#settings-ad-free` | `SettingsScreen` | 🎨 |
| 22 | **Settings — purchase unavailable / no ad fill** | Disabled purchase row with reason ("Store unavailable — try later"); rewarded button hidden with a quiet note | `#settings-unavailable` | `SettingsScreen` (error/empty variants) | 🎨 |
| 23 | **Settings — dark / large font** | Rows grow, no truncation of Hindi labels | `#settings-dark-large` | `SettingsScreen` | 🎨 |
| 24 | **Stats — empty** | "No games yet", CTA "New game"; best/highest tile as "—" | `#stats-empty` | `StatsScreen` (empty) | 🎨 |
| 25 | **Stats — populated** | Games played, wins, best score, highest tile, daily streak, best daily score; no charts | `#stats-populated` | `StatsScreen`, `StatTile` | 🎨 |
| 26 | **Stats — dark / large font** | Grid reflows to one column at 2.0× | `#stats-dark-large` | `StatsScreen` | 🎨 |
| 27 | **Stats — storage error** | Banner + zeros treated as unknown, not as real zeros | `#stats-storage-error` | `InlineBanner` | 🎨 |
| + | **Confirm — restart game** | Destructive confirm from the game screen / Home "New game" | `#confirm-restart` | `ConfirmDialog` | 🎨 |
| + | **Confirm — reset stats** | Spells out exactly what is erased | `#confirm-reset-stats` | `ConfirmDialog` | 🎨 |
| + | **Home — Hindi** | The same Home screen in Hindi: link row, button widths, tagline length | `#home-hindi` | `HomeScreen` (hi) | 🎨 |

## Tokens (frozen on approval — the site and the app read these exact values)

Every value below is what the reference renders and what
`design/reference/scripts/check-tokens.mjs` asserts against this file.
No screen may introduce a value that is not here.

### Colour — light theme

| Role | Token | Hex |
|---|---|---|
| Background | `bg` | `#FAF6EF` |
| Surface / card / dialog | `surface` | `#FFFFFF` |
| Surface variant (segmented track, quiet buttons) | `surfaceVariant` | `#F1E9DC` |
| Score box (taupe) | `scoreBox` | `#D9CDB9` |
| Score box label | `scoreLabel` | `#5C4F3F` |
| Score box value | `scoreValue` | `#2C241C` |
| Board frame | `boardFrame` | `#E6DCCB` |
| Empty cell | `emptyCell` | `#D3C7B2` |
| Primary / accent (terracotta) | `accent` | `#B4552F` |
| On primary | `onAccent` | `#FFF8F0` |
| Text primary | `textPrimary` | `#2C241C` |
| Text secondary | `textSecondary` | `#6B5C4A` |
| Divider | `divider` | `#E3D9C8` |
| Outline variant (card borders) | `outlineVariant` | `#EFE6D8` |
| Error (banner, destructive row) | `error` | `#9C3B32` |
| On error | `onError` | `#FFF8F0` |
| Success / new best | `success` | `#3F7A4B` |
| On success | `onSuccess` | `#FFFFFF` |
| Skeleton base / shine | `skeleton` | `#EFE6D8` |
| Skeleton shine | `skeletonShine` | `#F8F2E8` |

### Colour — dark theme

| Role | Token | Hex |
|---|---|---|
| Background | `bg` | `#161311` |
| Surface / card / dialog | `surface` | `#241F1B` |
| Surface variant | `surfaceVariant` | `#2E2823` |
| Score box | `scoreBox` | `#3A322A` |
| Score box label | `scoreLabel` | `#C9BBA8` |
| Score box value | `scoreValue` | `#F3ECE2` |
| Board frame | `boardFrame` | `#2B2520` |
| Empty cell | `emptyCell` | `#3A322A` |
| Primary / accent | `accent` | `#E08A5E` |
| On primary | `onAccent` | `#1B1613` |
| Text primary | `textPrimary` | `#F3ECE2` |
| Text secondary | `textSecondary` | `#B7A794` |
| Divider | `divider` | `#43392F` |
| Outline variant | `outlineVariant` | `#332C25` |
| Error | `error` | `#E98A7B` |
| On error | `onError` | `#1B1613` |
| Success / new best | `success` | `#6FAF7C` |
| On success | `onSuccess` | `#12100E` |
| Skeleton base / shine | `skeleton` | `#2E2823` |
| Skeleton shine | `skeletonShine` | `#3A322A` |

### Tile ramp — light

The numeral colour flips from warm ink to white as the tiles darken; 1024 is the
gold step and takes dark ink again. `> 2048` is the `super` step.

| Value | Background | Numeral | Contrast |
|---|---|---|---|
| 2 | `#F4EBDD` | `#6B5C4A` | 5.46 |
| 4 | `#F1E2C6` | `#6B5C4A` | 5.05 |
| 8 | `#F2CD9E` | `#7A4A22` | 4.95 |
| 16 | `#F0B87B` | `#6E3F1C` | 4.96 |
| 32 | `#EC9A5C` | `#5A3318` | 4.86 |
| 64 | `#E57B45` | `#5A3318` | 3.77 |
| 128 | `#DC5F35` | `#FFFFFF` | 3.66 |
| 256 | `#C74A2C` | `#FFFFFF` | 4.73 |
| 512 | `#B03B2A` | `#FFFFFF` | 5.99 |
| 1024 | `#C89434` | `#2C241C` | 5.63 |
| 2048 | `#A97A1F` | `#FFF6DE` | 3.55 |
| > 2048 | `#6E4E12` | `#FFF6DE` | 7.06 |

### Tile ramp — dark

| Value | Background | Numeral | Contrast |
|---|---|---|---|
| 2 | `#3E362E` | `#EFE4D4` | 9.43 |
| 4 | `#4A3F33` | `#EFE4D4` | 8.16 |
| 8 | `#8A5A32` | `#FFF6EC` | 5.47 |
| 16 | `#A2663A` | `#FFF6EC` | 4.37 |
| 32 | `#B87244` | `#FFF6EC` | 3.55 |
| 64 | `#C97F4B` | `#FFFFFF` | 3.17 |
| 128 | `#D2653B` | `#FFFFFF` | 3.71 |
| 256 | `#BC4E2E` | `#FFFFFF` | 4.92 |
| 512 | `#9E3D28` | `#FFFFFF` | 6.66 |
| 1024 | `#C39A46` | `#241F1B` | 6.24 |
| 2048 | `#A8822E` | `#FFF6DE` | 3.30 |
| > 2048 | `#7A5A16` | `#FFF6DE` | 5.90 |

### Contrast record (asserted by `npm run verify`, computed from the hex values)

- Every tile numeral ≥ **3.0:1** (large bold text); the worst pairs are tile 64
  dark **3.17:1** and tile 2048 dark **3.30:1**. Nothing sits below 3.0.
- Body-size pairs ≥ **4.5:1**: text/bg 14.16 light · 15.77 dark; secondary/bg
  5.99 · 7.90; score label on score box 5.06 · 6.68; score value on score box
  9.72 · 10.73; accent button text 4.66 · 6.80; error text on surface 6.81 ·
  6.52.
- Gold milestone theme: **seed `#A97A1F`**, identical layout and type; the full
  scheme is derived in phase 4 where the theme ships.

### Type scale (Material 3 roles; sizes in dp × the system font scale)

| Role | Size / line / weight / tracking | Mapped M3 role |
|---|---|---|
| App title (Home) | 40 / 48 / 700 / -0.5 | `displaySmall` |
| Screen title | 24 / 32 / 600 / 0 | `headlineSmall` |
| Dialog title | 22 / 28 / 600 / 0 | `headlineSmall` |
| Score value | 26 / 32 / 700 / 0, tabular | `headlineMedium` |
| Score label | 12 / 16 / 600 / +0.08em, uppercase | `labelSmall` |
| Body | 16 / 24 / 400 | `bodyLarge` |
| Secondary / list label | 14 / 20 / 400 | `bodyMedium` |
| Button label | 16 / 20 / 600 | `labelLarge` |
| Caption / legal | 12 / 16 / 400 | `bodySmall` |
| Tile numeral | 32 / 32 for 1–2 digits · 26 for 3 · 21 for 4 · 18 for 5+ | `titleLarge` variants |

**Numeral scaling caps (deliberate, so 2.0× never breaks a layout):** tile
numerals grow only to **1.25×**; in-screen score values to **1.5×**; score
values inside dialogs to **1.25×**. All other text scales fully with the system
font setting.

Font: **Roboto** (the Android system family; self-hosted on web via fontsource)
with **Noto Sans Devanagari** for Hindi runs — identical metrics on both sides,
zero APK cost. Tabular figures for every score and numeral so values do not
jitter.

### Spacing, shape, elevation, motion

- Spacing scale: **4 / 8 only** → 4 / 8 / 12 / 16 / 24 / 32 dp. Nothing in
  between, on either side.
- Screen padding: 24dp horizontal; 16dp top/bottom, safe-area aware.
- Score-box gap 12dp; board→controls gap 16dp; control gap 8dp between
  same-row actions, 12dp between distinct controls.
- Board: inner padding 10dp, tile gap 8dp, square (390 − 48 = 342dp wide at 1×),
  corner 16dp; tile corner 8dp; score box corner 12dp; buttons 12dp; chips
  fully rounded; cards 16dp; dialogs 24dp, max width 320dp; sheets 24dp top
  corners.
- Elevation: 0 on board and score boxes (flat on cream), 2dp on the direction
  pad, Material defaults for dialogs/sheets. **No shadow on tiles.**
- Touch targets ≥48dp everywhere; icon buttons 48dp; direction-pad keys 56dp.
- Motion: slide 110ms, merge pop 140ms, spawn 130ms, score tick 220ms; all
  suppressed by the reduced-motion setting (phase 2 implements it).

## Component inventory (= the app's Compose checklist)

- [ ] `PrimaryButton` (filled accent, 48dp min height) — Home, dialogs, sheets
- [ ] `SecondaryButton` (surface-variant fill) — Home, dialogs, sheets
- [ ] `TextAction` / `TextLinkRow` (quiet accent or muted actions)
- [ ] `IconButton` (home, restart, undo, settings, help, stats, direction pad)
- [ ] `ScoreBox` (uppercase label + tabular value on taupe)
- [ ] `BoardFrame` (rounded, 10dp padded, holds the grid)
- [ ] `TileGrid` + `Tile` (value → ramp, numeral flip, slide/merge/spawn
      animations, reduced-motion variant)
- [ ] `DirectionPad` (4 keys, optional per settings) + pad toggle button
- [ ] `Chip` (neutral / accent / success / outline) — streak, new best, mode,
      swipe hint
- [ ] `ResumeCard`, `DailyCard`, `StatTile`, `Card`, `SectionLabel`
- [ ] `SettingsRow` (title, subtitle, trail), `ToggleRow`, `Switch`,
      `SegmentedChoice`, `LockedThemeRow`, `PurchaseRow`, `RowGroup`
- [ ] `InlineBanner` (error tone, Retry action), `ConfirmDialog`, `PauseSheet`,
      `WinDialog`, `GameOverDialog`
- [ ] `BoardPreview` (decorative non-interactive board still on Home: fixed
      4×4 sample grid, tile colours from the ramp, numeral size derived from the
      cell size — must be `contentDescription = null`, not focusable)
- [ ] `BoardSkeleton` + `Skeleton` (loading), `EmptyState` (stats)

## Icons (one set, both sides)

**Lucide (ISC)** — web via the `lucide-react` package; the app adapts the same
glyphs into Compose `ImageVector`s, with attribution kept in the licences screen.
The set the reference actually uses: `house`, `rotate-ccw` (restart), `undo-2`,
`settings`, `circle-help`, `chart-column` (stats), `volume-2`, `smartphone`
(haptics), `palette`, `sun-moon` (theme/language), `lock`, `chevron-right`,
`x` (close), `play`, and the four `arrow-*` direction keys.
If the ImageVector adaptation proves error-prone, fall back to Material Icons
with a 1:1 glyph table — recorded as an agent decision, not a silent swap.

## Deliberate native adaptations (planned differences, not drift)

| Web reference | Android | Why |
|---|---|---|
| CSS transitions on tiles | Compose `Animatable` per tile + `AnimatedVisibility` for spawns; respects "remove animations" | native motion model and a11y |
| Hover / active CSS states | Material ripple + state layers | Material 3 behaviour over pixel-identical CSS |
| Fixed 390 × 844 frame | Edge-to-edge, insets-driven padding, square board from available width | device variety, notches, gesture bar |
| `:focus` outlines | TalkBack order, content descriptions ("tile 128, row 2 column 3"), 48dp targets | accessibility is a screen state |
| Web font loading (Roboto + Noto Sans Devanagari) | system Roboto; Hindi uses the device's Devanagari fallback | zero APK cost, identical Latin metrics |
| Dialog as a positioned div | M3 `AlertDialog` + `ModalBottomSheet`, back-gesture dismissal | native back behaviour, predictive back |
| Board drawn with CSS grid | Compose layout with per-tile offsets | 60fps on low-end devices, no re-layout per frame |
| Tile numerals capped at 1.25× / dialog score at 1.25× / in-screen score at 1.5× | same caps applied through `sp` values that stop growing past the cap | 2.0× font must break no board or dialog layout |
| 1 CSS px = 1 dp | 1 dp everywhere, `sp` for text | keeps the measurements in this file directly usable |
| Decorative board still on Home | same still, `contentDescription = null`, skipped by TalkBack | decoration is never an accessibility or navigation target |
| Tiles are square cells filled edge to edge | `Modifier.fillMaxSize()` inside an `aspectRatio(1f)` cell; the numeral is centred and grows only to its cap | a tile that is not square reads as a progress bar, not a tile (found and fixed in 0-D review round 1) |

## Web → Compose mapping (complete — an unmapped screen is not ready to code)

Every row below is implemented in the reference and has a Compose target. Token
roles are the only source of colour/space/type in the app.

| Reference page | Compose entry point | Token roles used | Phase |
|---|---|---|---|
| `#home-no-save`, `#home-empty`, `#home-hindi` | `HomeScreen` (+ `HomeState.NoGame`, `BoardPreview`) | bg, textPrimary, textSecondary, accent, onAccent, surfaceVariant, boardFrame, emptyCell, tile ramp, minTarget | 4 |
| `#home-in-progress` | `HomeScreen` (+ `ResumeCard`), `ConfirmDialog` | surface, outlineVariant, accent, chip colours | 4 |
| `#home-daily-done` | `HomeScreen` (+ `DailyCard`, `Chip` accent) | accent, onAccent, surface, divider | 4 |
| `#home-storage-error`, `#stats-storage-error` | `HomeScreen` / `StatsScreen` + `InlineBanner` | error, surface, outlineVariant, textSecondary | 4 / 3 |
| `#home-loading` | `SplashScreen` + `BoardSkeleton` | skeleton, skeletonShine, boardFrame, radius.tile | 4 |
| `#game-new`, `#game-mid`, `#game-dark`, `#game-large-font` | `GameScreen` (+ `ScoreBox`, `BoardFrame`, `TileGrid`, `Tile`, `UndoButton`, `IconButtonRow`) | full light/dark palette, tile ramp 2…>2048, type roles, spacing scale | 2 |
| `#game-paused` | `GameScreen` + `PauseSheet` | scrim, surface, sheet radius, accent | 2 |
| `#game-storage-error` | `GameScreen` + `InlineBanner` | error, surface, outlineVariant | 2 |
| `#win`, `#win-dark-large` | `WinDialog` | surface, accent, scoreBox/scoreLabel/scoreValue, dialog radius | 3 |
| `#game-over`, `#game-over-dark-large` | `GameOverDialog` (+ `NewBestChip`, `RewardedUndoButton`) | success/onSuccess chip, accent, surface, error | 3 |
| `#game-over-dismissed` | `GameScreen` (state) + `AdSlotPlaceholder` in the reference only | — (no ad art in the design) | 5 |
| `#how-to-play`, `#how-to-play-dark-large` | `HowToPlayScreen` (+ `NumberedRule`, `ExampleRow`, `Card`) | textPrimary/secondary, accent, surface, outlineVariant, tile ramp | 4 |
| `#settings` | `SettingsScreen` (+ `SectionLabel`, `RowGroup`, `SettingsRow`, `ToggleRow`, `SegmentedChoice`, `LockedThemeRow`) | all role tokens, divider, outlineVariant | 4 |
| `#settings-ad-free`, `#settings-unavailable` | `SettingsScreen` (ad states) | success, textSecondary, error | 4 / 5 |
| `#stats-empty`, `#stats-populated`, `#stats-dark-large` | `StatsScreen` (+ `StatTile`, `EmptyState`) | surface, outlineVariant, textPrimary/Secondary, scoreValue | 3 |
| `#confirm-restart`, `#confirm-reset-stats` | `ConfirmDialog` | surface, scrim, error, accent, dialog radius | 3 / 4 |
| `#tokens` | `ui/theme/` (`TilefoldTheme`, `TileColors`, `Spacing`, `Shapes`) | the whole table above | 0-D freeze |

## Screenshot comparison (the acceptance test for every UI phase)

Reference renders live in `design/reference/screenshots/` at 2×; app screenshots
come from the Roborazzi lane. Side-by-side review happens in each UI phase's
record, and a difference is either fixed or listed under
**deliberate native adaptations** above. "Looks close" in prose is not evidence.

- Phase 2 — Game (`#game-new`, `#game-mid`, `#game-dark`, `#game-large-font`):
  to fill
- Phase 3 — Win / Game-over / Stats / dialogs: to fill
- Phase 4 — Home / How to play / Settings / Hindi: to fill

## What may cross over (guardrail)

Allowed: the token values above, the layout measurements, the component
inventory, the icon and font choices, and the copy (as string resources).
Forbidden: importing, copying or translating any file under
`design/reference/` into `app/`; shipping any part of the site in the APK;
letting the app's build depend on Node, Vite or npm. The reference is
disposable and never a runtime dependency.
