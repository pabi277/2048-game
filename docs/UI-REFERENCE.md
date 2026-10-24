<!-- SPDX-License-Identifier: MIT -->

# UI reference — Tilefold (working name)

> The design specification for this app's UI: a disposable, phone-shaped
> UI-only website, owner-approved **before any app UI code**
> (`knowledge/ui-reference-site.md`; stack catalogue:
> `knowledge/web-ui-references.md`). The reference's code is never copied into
> the app — only tokens, measurements and the component inventory cross over.
>
> **Status: drafted in the plan chat 2026-10-24. NOT built, NOT approved.**
> Phase 0 builds the site; the owner approves it in chat; only then are the
> tokens below frozen. Every value here is a **proposal** and may change during
> Phase 0 review. The owner's reference screenshot has not been supplied yet —
> layout facts below come only from the brief (cream background, big title, two
> taupe SCORE/BEST boxes, home and restart buttons above a rounded board, tiles
> ramping cream → peach/orange → red-orange → gold, text flipping dark → white).

## Reference

- Stack (DECIDE-ONCE, one only): **Option A — Vite + Tailwind CSS + Konsta UI
  (Material theme) + Lucide icons**, chosen 2026-10-24 (owner accepted the plan
  default; see `docs/DECISIONS.md`). Rationale: app-shaped Material screens that
  map ~1:1 to Compose; builds and previews in seconds.
- Location: `design/reference/` in the app repo (disposable; never shipped,
  never in the APK, never inside `app/`).
- Phone frame: fixed 390 × 844 CSS px, one column, app navigation; no hover
  states, no landing-page patterns.
- Preview: Phase 0 presents the live preview **and** static screenshots of every
  screen/state in chat for approval.

## Approval

- **Pending.** Planned gate: owner reviews every screen/state in chat during
  Phase 0, requests corrections (unlimited rounds), then approves explicitly.
- On approval, record: date · stack · screens/state coverage · token freeze
  commit sha — and log the design decision in `docs/DECISIONS.md`.
- After approval, a change to the **design direction** (stack, visual language,
  palette family) re-opens this gate (DECIDE-ONCE change → GATE class). Small
  per-screen fixes update the reference first, then the screen — never reverse.

## Screens × states (every row will be a page/state in the reference site)

Required state vocabulary: **content** · **loading** (local only: brief
skeleton/splash) · **empty** (nothing saved yet) · **error** (storage read/write
failure) · **dark** · **large-font** (Android font scale 2.0 equivalent, and a
Hindi long-text run where text length changes). "Ads" states exist only after
Phase 5 ships and are shown in the reference as *slots*, never as real ad art.

| # | Screen / state | What it shows | Target composable(s) | Status |
|---|---|---|---|---|
| 1 | **Home — content (no saved game)** | Big title, one-line subtitle, primary "New game", secondary "Daily challenge", text links "How to play · Stats · Settings", best score line | `HomeScreen`, `PrimaryButton`, `SecondaryButton`, `TextLinkRow`, `BestScoreLine` | 📋 |
| 2 | **Home — content (game in progress)** | Same, with "Continue" as primary (score + highest tile + mode chip) and "New game" demoted to a text action with a confirm dialog | `HomeScreen`, `ResumeCard`, `ConfirmDialog` | 📋 |
| 3 | **Home — daily completed today** | Daily card showing today's score, streak chip, "Played — come back tomorrow" disabled state | `DailyCard`, `StreakChip` | 📋 |
| 4 | **Home — empty/never played** | Best score shown as "—", stats teaser hidden | `HomeScreen` (empty variant) | 📋 |
| 5 | **Home — storage error** | Inline non-blocking banner: "Couldn't read your saved game" + Retry; New game still works | `InlineBanner` (error tone) | 📋 |
| 6 | **Home — loading** | 1-frame splash: app mark + board skeleton | `SplashScreen`, `BoardSkeleton` | 📋 |
| 7 | **Game — new game (playing)** | Title row (small), SCORE + BEST boxes (taupe), home + restart icon buttons above the rounded board, 4×4 board, undo + direction-pad fallback toggle below, "swipe to merge" hint that fades after first move | `GameScreen`, `ScoreBox`×2, `BoardFrame`, `TileGrid`, `IconButtonRow`, `DirectionPad`, `UndoButton` | 📋 |
| 8 | **Game — mid-game (playing)** | Same; score ticking, mixed tile values incl. 64/128/256, one merge animating | `GameScreen`, `Tile` (animated) | 📋 |
| 9 | **Game — paused** | Board dimmed under a bottom sheet: Resume / Restart / Home; auto-shown when the app backgrounds | `PauseSheet` | 📋 |
| 10 | **Game — dark** | Full dark palette, tile ramp dark variant, same hierarchy | `GameScreen` (dark) | 📋 |
| 11 | **Game — large font** | 2.0× font scale: score boxes wrap to two lines, board keeps square aspect, buttons keep ≥48dp | `GameScreen` (fontScale) | 📋 |
| 12 | **Game — storage error** | Banner above the board: "Progress isn't saving on this device" + Retry; play continues | `InlineBanner` | 📋 |
| 13 | **Win (2048 reached)** | Dialog over the board: "You made 2048" + score + best + "Keep playing" (primary) / "New game" (secondary) | `WinDialog` | 📋 |
| 14 | **Win — dark / large font** | Same dialog on dark; scrollable when text grows | `WinDialog` | 📋 |
| 15 | **Game over** | Dialog over the board: final score, best, "New best" chip if beaten, "Undo last move" (1 free, then rewarded), "New game", "Home" | `GameOverDialog`, `NewBestChip`, `RewardedUndoButton` | 📋 |
| 16 | **Game over — after dismissal (ad slot, Phase 5 only)** | What the user sees *after* closing the dialog: normal Home/board, interstitial only if the cap allows; reference shows the suppressed case by default | `AdSlotPlaceholder` (reference only) | 📋 |
| 17 | **Game over — dark / large font** | Dialog variants | `GameOverDialog` | 📋 |
| 18 | **How to play** | Numbered rules (slide, merge, 2 spawns 90/10, score = merge sum, win at 2048, keep playing, game over rule), scoring example, undo rules, controls (swipe + arrows + keyboard on tablets) | `HowToPlayScreen`, `NumberedRule`, `ExampleRow` | 📋 |
| 19 | **How to play — dark / large font** | Scrollable long text; Hindi run for length testing | `HowToPlayScreen` | 📋 |
| 20 | **Settings — content** | Sections: Sound (toggle), Haptics (toggle), Theme (System/Light/Dark/Gold-locked), Language (English/हिन्दी), Controls (show direction pad), Progress (Reset stats — destructive confirm), Ads ("Remove ads" purchase row or "Ad-free — thank you"), About (version, Licences, Privacy policy) | `SettingsScreen`, `SettingsRow`, `SwitchRow`, `SegmentedChoice`, `LockedThemeRow`, `PurchaseRow` | 📋 |
| 21 | **Settings — ad-free active** | Purchase row replaced by an "Ad-free" state; rewarded entries hidden | `SettingsScreen` | 📋 |
| 22 | **Settings — purchase unavailable / no ad fill** | Disabled purchase row with reason ("Store unavailable — try later"); rewarded button hidden with a quiet note | `SettingsScreen` (error/empty variants) | 📋 |
| 23 | **Settings — dark / large font** | Rows grow, no truncation of Hindi labels | `SettingsScreen` | 📋 |
| 24 | **Stats — empty** | "No games yet", CTA "New game"; best/highest tile as "—" | `StatsScreen` (empty) | 📋 |
| 25 | **Stats — populated** | Games played, wins, best score, highest tile, daily streak, best daily score; no charts | `StatsScreen`, `StatTile` | 📋 |
| 26 | **Stats — dark / large font** | Grid reflows to one column at 2.0× | `StatsScreen` | 📋 |
| 27 | **Stats — storage error** | Banner + zeros treated as unknown, not as real zeros | `InlineBanner` | 📋 |

Not screens, but part of the reference: the **confirm dialogs** (restart, reset
stats) and the **language switch** showing the same Home screen in Hindi.

## Tokens — PROPOSED, to be frozen only on owner approval in Phase 0

Colour intent (from the brief): cream ground, taupe score boxes, rounded board,
tiles ramping pale cream → peach → orange → red-orange → gold, numeral colour
flipping dark → white as tiles darken. Values below are our own ramp, **not**
taken from any competitor.

### Colour — light theme

| Role | Hex | Notes |
|---|---|---|
| Background | `#FAF6EF` | cream page ground |
| Surface / card | `#FFFFFF` | dialogs, sheets |
| Score box (taupe) | `#D9CDB9` | label `#5C4F3F`, value `#2C241C` |
| Board frame | `#E6DCCB` | rounded 16dp |
| Empty cell | `#D3C7B2` | |
| Primary (accent) | `#B4552F` | terracotta; buttons, focus |
| On primary | `#FFF8F0` | |
| Text primary | `#2C241C` | |
| Text secondary | `#6B5C4A` | |
| Divider | `#E3D9C8` | |
| Error | `#9C3B32` | banner/destructive |
| Success / new best | `#3F7A4B` | chip |

Tile ramp (background / numeral):

| Value | Tile bg | Text | | Value | Tile bg | Text |
|---|---|---|---|---|---|---|
| 2 | `#F4EBDD` | `#6B5C4A` | | 128 | `#DC5F35` | `#FFFFFF` |
| 4 | `#F1E2C6` | `#6B5C4A` | | 256 | `#C74A2C` | `#FFFFFF` |
| 8 | `#F2CD9E` | `#7A4A22` | | 512 | `#B03B2A` | `#FFFFFF` |
| 16 | `#F0B87B` | `#6E3F1C` | | 1024 | `#C89434` | `#FFF9E8` |
| 32 | `#EC9A5C` | `#FFFFFF` | | 2048 | `#A97A1F` | `#FFF6DE` |
| 64 | `#E57B45` | `#FFFFFF` | | >2048 | `#6E4E12` | `#FFF6DE` |

### Colour — dark theme (derived, contrast-checked at build time)

| Role | Hex |
|---|---|
| Background | `#161311` |
| Surface / card | `#241F1B` |
| Score box | `#3A322A` (label `#C9BBA8`, value `#F3ECE2`) |
| Board frame | `#2B2520` |
| Empty cell | `#3A322A` |
| Primary | `#E08A5E` |
| Text primary / secondary | `#F3ECE2` / `#B7A794` |
| Tile 2 / 4 | `#3E362E` / `#4A3F33` with `#EFE4D4` |
| Tile 8 / 16 / 32 | `#8A5A32` / `#A2663A` / `#B87244` with `#FFF6EC` |
| Tile 64 / 128 / 256 / 512 | `#C97F4B` / `#D2653B` / `#BC4E2E` / `#9E3D28` with `#FFFFFF` |
| Tile 1024 / 2048 | `#C39A46` / `#A8822E` with `#FFF6DE` |

Gold milestone theme: palette derived from tile-2048 gold (`#A97A1F`) as seed on
a near-black ground; identical layout and type scale.

### Type scale (Material 3 roles; sizes in sp, Android font scaling respected)

| Role | Size / weight / tracking | Mapped M3 role |
|---|---|---|
| App title (Home) | 40 / 700 / -0.5 | `displaySmall` |
| Screen title | 24 / 600 | `headlineSmall` |
| Dialog title | 22 / 600 | `headlineSmall` |
| Score value | 26 / 700, tabular | `headlineMedium` |
| Score label | 12 / 600 / +0.08em, uppercase | `labelSmall` |
| Tile numeral | 32 / 700 for 1–2 digits; 26 / 700 for 3; 21 / 700 for 4; 18 / 700 for 5+ | `titleLarge` variants |
| Body | 16 / 400, line-height 1.5 | `bodyLarge` |
| Secondary/list label | 14 / 400 | `bodyMedium` |
| Button label | 16 / 600 | `labelLarge` |
| Caption/legal | 12 / 400 | `bodySmall` |

Font: **Roboto** (Android system family; self-host on web via fontsource) —
identical text on both sides, zero APK cost. Tabular figures for score/tiles so
numbers do not jitter.

### Spacing, shape, elevation

- Spacing scale: **4 / 8 only** (4, 8, 12, 16, 24, 32 as multiples).
- Screen padding: 24dp horizontal; 16dp top/bottom safe-area aware.
- Score-box gap: 12dp; board→controls gap: 16dp; control gap: 12dp.
- Board: inner padding 10dp; tile gap 8dp; board corner 16dp; tile corner 8dp;
  score box corner 12dp; buttons corner 12dp; sheets 24dp top corners;
  dialogs 24dp.
- Elevation: 0 for the board and score boxes (flat on cream), 2dp for the
  direction pad, dialog/sheet as M3 defaults. No shadows on tiles.
- Touch targets ≥48dp; icon buttons 48dp; direction-pad keys 56dp.

## Component inventory (= the app's Compose checklist)

- [ ] `PrimaryButton` (filled, accent, 48dp min height)
- [ ] `SecondaryButton` / `TextLink` (quiet actions)
- [ ] `IconButton` variants (home, restart, undo, settings, help, stats)
- [ ] `ScoreBox` (label + tabular value, taupe surface)
- [ ] `BoardFrame` (rounded, padded, holds the grid)
- [ ] `TileGrid` + `Tile` (value → ramp, text colour flip, slide/merge/spawn
      animations, reduced-motion variant)
- [ ] `DirectionPad` (4 keys, optional per settings)
- [ ] `StreakChip` / `NewBestChip` / `ModeChip`
- [ ] `ResumeCard`, `DailyCard`, `StatTile`
- [ ] `SettingsRow` (label, description, trail), `SwitchRow`, `SegmentedChoice`,
      `LockedThemeRow`, `PurchaseRow`
- [ ] `InlineBanner` (error/info), `ConfirmDialog`, `PauseSheet`, `WinDialog`,
      `GameOverDialog`
- [ ] `BoardSkeleton` (loading), `EmptyState` (stats)

## Icons (one set, both sides)

**Lucide (ISC)** — web via `lucide` package; app via 8 icon paths adapted into
Compose `ImageVector`s, attribution kept in the licences screen. Planned set:
home, rotate-ccw (restart), undo-2, settings, circle-help, bar-chart-3, volume-2,
smartphone (haptics), palette, chevron-right, lock, x, sun/moon (theme).
If the ImageVector adaptation proves error-prone, fall back to Material Icons
with a 1:1 glyph table — recorded as an agent decision, not a silent swap.

## Deliberate native adaptations (planned differences, not drift)

| Web reference | Android | Why |
|---|---|---|
| CSS transitions on tiles | Compose `Animatable` per tile + `AnimatedVisibility` for spawns; respects "remove animations" accessibility setting | native motion model and a11y |
| Hover/active CSS states | Material ripple + state layers | Material 3 behaviour over pixel-identical CSS |
| Fixed 390×844 frame | Edge-to-edge, insets-driven padding, square board from available width | device variety, notches, gesture bar |
| `:focus` outlines | TalkBack order, content descriptions ("tile 128, row 2 column 3"), 48dp targets | accessibility is a screen state |
| Web font loading | system Roboto | zero APK cost, identical metrics |
| Dialog as a div overlay | M3 `AlertDialog` + `ModalBottomSheet`, back-gesture dismissal | native back behaviour, predictive back |
| Board drawn with CSS grid | Compose layout with per-tile offsets | 60fps on low-end devices, no re-layout per frame |

## Web → Compose mapping status

The mapping table is complete for Phase 2 (Game) and Phase 3 (dialogs, Stats)
and Phase 4 (Home, How to play, Settings). An unmapped screen is not ready to
code. Screenshot comparison is the acceptance test: each UI phase reviews the
Paparazzi/Roborazzi shots beside the reference screenshots and either fixes the
difference or records it here.

## Screenshot comparison (per UI phase)

- Phase 2 — Game: reference vs app screenshot reviewed in the phase record ·
  differences fixed: — · deliberate adaptations above: —
- Phase 3 — Win / Game-over / Stats / Resume: to fill
- Phase 4 — Home / How to play / Settings / Hindi: to fill
