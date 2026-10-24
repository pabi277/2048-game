<!-- SPDX-License-Identifier: MIT -->

# Research — Tilefold (working name)

> Evidence-backed. Every claim links its source and its check date. Rejected
> options stay listed so nobody re-litigates them. Rules: this repo's `rule.md`.
> Plan chat evidence cut-off: **2026-10-24**. Nothing here is a promise of
> demand or revenue.

## Niche verdict line

`Demand: real at category level (multiple 1M–10M+ Play listings on 2026-10-24, two
updated within the last 60 days) but crowded and feature-saturated; staleness:
mixed (one 10K+ no-ads incumbent last updated 2025-07-23, the category leaders
updated 2026-08/2026-10); complaints: intrusive/forced/mid-game ads confirmed on
4 of 6 inspected listings, willingness to pay for no ads confirmed on 2, privacy
distrust alleged on 1, undo-after-game-over requested on 1 — "lost progress" and
"janky sliding" were NOT confirmed in the accessible review sample (named gap,
§ Evidence gaps); owner advantage: restraint (no banner, no mid-game ad), Hindi +
English UI, small offline APK for low-end devices, and a deterministic seeded
engine that makes every rule testable; decision: build direction A (§ Chosen
direction) under the pre-written kill test, with the 1-star review audit re-run
from device installs in Phase 0/1 before store text is written.`

## Search log

| # | Query (exact) | Country / language | Date | Result-set note |
|---|---|---|---|---|
| 1 | `2048 puzzle` | IN / en | 2026-10-24 | Top 20 dominated by 2048-adjacent variants: 2248 (4.8), `2048 Merge Games - M2 Blocks` (4.8, 10M+), `Number Charm: 2048 Games` (4.6, 10M+), `2048` Ketchapp (4.3, 1M+), `2048` PixelProse (4.7, 100K+) |
| 2 | `merge number puzzle` | IN / en | 2026-10-24 | Confirms the same crowding plus more drop/tap variants (`Merge Number Puzzle` 4.2, `2248` 4.8/4.9, `Merge Hexa` 4.5) — the sliding-tile classic is a minority of results |
| 3 | `2048 game` | IN / en | 2026-10-24 | Same result family as #1 (listings re-checked individually, table below) |

Method: searched Play web with `hl=en&gl=IN`, then opened each relevant listing's
detail page (`showAllReviews=true`) on the same day. Ratings, review counts,
downloads and "Updated on" dates are read from the listing pages; ad/IAP labels
and Data-Safety summaries are developer-declared to Play (not independently
audited).

## Listings inspected (top relevant results, 2026-10-24)

| Listing | Developer | Rating / reviews | Downloads | Updated on | Ads / IAP declared | Data-safety highlights (developer-declared) |
|---|---|---|---|---|---|---|
| [2048](https://play.google.com/store/apps/details?id=com.ketchapp.play2048) | Ketchapp (Ubisoft Mobile Games) | 4.3 / 8.35K | 1M+ | 2026-09-29 | Contains ads · In-app purchases | Shares Location, Financial info + 3 others; collects Location, Personal info + 4 others; "Data isn't encrypted" |
| [Number Charm: 2048 Games](https://play.google.com/store/apps/details?id=com.dupuz.game2048.yellow) | HDuo Fun Games | 4.6 / 56.5K | 10M+ | 2026-10-13 | Contains ads · In-app purchases | Collects Personal info, Financial info + 3 others |
| [2048](https://play.google.com/store/apps/details?id=fr.pixelprose.game2048) | PixelProse SARL | 4.7 / 576 | 100K+ | 2026-02-16 | No ads label; monetisation via third-party SDK (reviewer allegation, see below) | "No data shared", "No data collected" |
| [Join Blocks 2048 Merge Puzzle](https://play.google.com/store/apps/details?id=com.steptwo.joinblocks) | Step Two Studio | 3.9 / 234 | 10K+ | 2026-08-30 | In-app purchases | Shares Device or other IDs; collects App activity, App info and performance, Device or other IDs |
| [2048 Puzzle Challenge](https://play.google.com/store/apps/details?id=com.arceapps.a2048) | ArceApps | 4.3 / 86 | 10K+ | **2025-07-23** (≈15 months stale) | **No ads** declared | "No data shared", "No data collected" |
| [2048 - Number Puzzle Game](https://play.google.com/store/apps/details?id=com.jrechach.game2048) | Repi Apps | No rating yet | 10+ | 2026-10-19 | No ads label | Collects Location, App activity + 2 others; "Data can't be deleted" |
| [2048 Merge Games - M2 Blocks](https://play.google.com/store/apps/details?id=merge.blocks.drop.number.puzzle.games) | Guru Puzzle Game | 4.8 / 354K | 10M+ | 2026-08-26 | Contains ads · In-app purchases | — (different mechanic; excluded from the sliding-tile comparison) |

Excluded as **not the same product**: M2 Blocks, 2248/X2 family, Merge Hexa,
`Number Match: 2048 Puzzle`, `Match the Number - 2048 Game` (tap/connect/drop
mechanics, not 4×4 sliding-merge).

## Review signals

Play's web detail page exposes only the ~3 "most helpful" reviews to an
unauthenticated fetch. What follows is what was actually readable on
2026-10-24, quoted verbatim where relevant, plus the listing's review count.

| Theme | Verified? | Evidence (listing, quote) |
|---|---|---|
| Intrusive / forced / mid-game ads | **Yes — 4 listings** | Ketchapp: "game has forced fullscreen advertisements. There are plenty of games that do not have forced advertisements." · "enjoyable until ads started appearing mid game when you're trying to play. uninstalled." · HDuo: "Slowly added unforced ads, then recently made them unskippable…" · "I understand the app needs to make money but the ads are so annoying and make it unplayable" · Step Two: "way to many ads..you'll get a 5 star if you decrease them some..I'd like to finish a game before an ad pops up." |
| Willingness to pay for no ads | **Yes — 2 listings** | Ketchapp: "Nice except for all the ads. Wish I could pay for it to be ad-free" · HDuO: "Aesthetic could be better but still good enough. Worth paying for no ads as it's pretty cheap." |
| Privacy distrust of monetisation SDKs | **Allegation, unverified** | PixelProse reviews claim a third-party SDK turns the phone into a "residential proxy or web scraping node" and that a share prompt appears "every time you play"; the listing declares no data collected. Developer text and reviewer claim contradict each other — recorded as a signal about *trust fragility*, not as fact. |
| Undo after game over | **Yes — 1 listing** | ArceApps: "Please, enable the undo last move option even after game over. That's why it's called 'undo last move'…" |
| Ad-free as a selling point | **Yes — 1 listing** | ArceApps: "10/10, love it, NO ADS!!!! Thank you for not lying about having no ads…" and "went looking for a 2048 game with no ads…" |
| Lost progress / board | **Not verified** | No readable review in the sample said progress was lost. Two listings advertise auto-save (PixelProse "Auto-Save: Never lose your progress"; Repi "Continue your latest saved game"), which is a *feature claim*, not a complaint. Treat as an unproven hypothesis until the fuller review audit. |
| Ugly / janky sliding | **Not verified** | Several listings advertise "60 FPS"/"zero latency"; no readable complaint described jank. Do not build the store text on this claim. |
| Feature saturation | **Yes** | Repi Apps (10+ downloads, 2026-10-19) already lists undo, resume, 3×3–6×6, stats, themes, arrow controls — i.e. the "differentiators" are copyable in a weekend. Our edge cannot be a feature checklist. |

### Method limits (named, not hidden)

- 10 recent 1-star reviews per listing were **not retrievable**: Play's review
  pagination requires an authenticated/POST path that a plain page fetch cannot
  reach. Observable sample ≈ 3 reviews per listing.
- No app was installed on a device in this chat (plan-only session), so
  `knowledge/niche-method.md` step 6 (observe ad placement in-app) is **not yet
  done**. It is scheduled as Phase 0/1 owner-or-agent work, before store text.
- Ratings/review counts shift; every number above is a 2026-10-24 snapshot.

## Market classification

Crowded-but-bad-in-one-specific-way. Category demand is proven (10M+ installs on
two listings); the quality gap is narrow and behavioural — ad interruptions,
unclear privacy, and a stale no-ads incumbent. A new entrant cannot win on
features (they are commodity) and must not win on brand (the name "2048" is not
ours to use). It can win on **restraint that is visible and verifiable**
(no banner, no mid-game interruption, hard interstitial cap, offline-only
storage), plus local-language UI and a daily seeded board.

## Differentiation options compared (presented 2026-10-24)

| Option | Scope | Risks | Verdict |
|---|---|---|---|
| **A — Quiet 2048 + Daily** | Respect-first ads (no banner, no mid-game ad, capped post-game interstitial, rewarded undo/theme, ad-free IAP) + resume-anywhere + 1 free undo + date-seeded Daily Challenge + EN/HI | Rewarded fill may be thin on a tiny app; more screens to screenshot-test; daily mode must avoid streak guilt | **CHOSEN by owner 2026-10-24** |
| B — Minimal respectful | Respectful ads + resume + 1 free undo; daily mode/themes/Hindi in v2 | Indistinguishable from Repi's brand-new app; weak retention hook | Not chosen |
| C — Zero-ads craft | No ad SDK at all in v1, no IAP, pure quality + HI | No revenue path; head-on with a loved no-ads incumbent | Not chosen |
| D — Daily-first | Seeded daily board as the headline product; classic secondary | Unproven habit loop, no community features, different product | Not chosen |

### Owner's answer and the 3-week reconciliation (recorded 2026-10-24)

Owner chose **A**, **EN + HI**, **rewarded-first monetization as proposed**, and a
**3-week** budget. My option-B/C note said 3 weeks "forces B/C scope"; the owner
kept A. Resolution (agent decision, reversible on objection — see
`docs/DECISIONS.md`): keep every A differentiator, cut **breadth** instead —
one board size (4×4), three themes (System/Light/Dark) plus **one** milestone
theme, no achievements/leaderboards/cloud/multiplayer/sharing/analytics. Adding
breadth back later is a normal scope change; it does not need a gate, but it
does need a re-plan.

## Reuse map (knowledge/reuse-first.md §2 — scanned 2026-10-24)

| Capability | Candidate (link, checked 2026-10-24) | Licence | Activity | Verdict | Why |
|---|---|---|---|---|---|
| **Game engine** (pure Kotlin, deterministic, seeded spawn) | No published Kotlin library exists that is deterministic + seeded + host-testable; searches: `2048 language:kotlin`, `2048 compose`, `2048 language:kotlin in:name,description` (top 8 by stars) | — | — | **BUILD** | Engine correctness *is* the product; seeded RNG and host tests are requirements no candidate meets. Small, fully testable, no dependency risk. |
| Engine behaviour reference | [gabrielecirulli/2048](https://github.com/gabrielecirulli/2048) | **MIT** (verified via GitHub licence API) | 13,446★ · last push **2024-10-24** · 17,607 forks · not archived | **STUDY** (JS: merge order, spawn 90/10, win/keep-playing, game-over rule) | Licence permits more, but it is JavaScript and untested for our rules; behaviour is what we need. |
| Engine + animation reference | [alexjlockwood/compose-multiplatform-2048](https://github.com/alexjlockwood/compose-multiplatform-2048) | **MIT** | 471★ · last push **2024-10-21** · **no test sources in the tree** | **STUDY** (domain shape, `GridTileMovement`, animated tile placement) | Stale (>6 months) and untested → not a base; MIT means individual patterns may be ADAPTed with attribution if the clean-room rewrite needs them. |
| Engine model reference | [andstatus/game2048](https://github.com/andstatus/game2048) | **Apache-2.0** | 359★ · last push **2026-07-19** · active | **STUDY** (board/history/undo modelling, JSON save) | Built on KorGE, so its UI and lifecycle do not transfer; its `model/` shape is worth reading before writing ours. |
| Swipe gestures | Compose `Modifier.pointerInput` + `detectDragGestures` (AndroidX) | Apache-2.0 | androidx/androidx active, pushed 2026-10-24 | **BUILD** (thin gesture adapter over the platform API) | One job, ~40 lines, fully testable via a pure `SwipeAxis` mapper; a third-party gesture library would add size for nothing. |
| Board animations | `androidx.compose.animation` (AndroidX) + pattern from alexjlockwood | Apache-2.0 / MIT | active | **DEPEND** platform lib; **ADAPT** pattern | Tile motion is state-driven (offset + scale per tile), which Compose animates natively. [airbnb/lottie-android](https://github.com/airbnb/lottie-android) **REJECT** — asset-driven, size cost, wrong tool. |
| Architecture base | [android/architecture-samples](https://github.com/android/architecture-samples) | Apache-2.0 | 45,871★ · pushed 2026-10-21 | **ADAPT** (single-activity, UDF, `StateFlow`, one ViewModel per screen) | Right size for a 3-week app. [android/nowinandroid](https://github.com/android/nowinandroid) **REJECT** for this app — modularisation tax a tiny game never repays. |
| DI | [google/dagger](https://github.com/google/dagger) (Hilt) / [InsertKoinIO/koin](https://github.com/InsertKoinIO/koin) | Apache-2.0 | koin: 10,022★ · pushed 2026-10-22 | **REJECT** (both) → **BUILD** a ~30-line `AppContainer` | Graph is 3 nodes (DataStore → repository → one ViewModel). Codegen/runtime DI would be pure overhead; revisit if the graph exceeds ~5 nodes. Logged as a reversible agent decision. |
| UI base (ONE sample) | [android/compose-samples](https://github.com/android/compose-samples) → **Jetsnack** | Apache-2.0 | 23,534★ · pushed 2026-10-23 | **ADAPT one** (M3 surfaces, custom layouts, rounded cards, dialog/sheet patterns) | The board is a grid of rounded surfaces inside a card-like frame — Jetsnack's shape. JetLagged stays STUDY for animation ideas only. |
| Persistence: settings + save | DataStore Preferences (AndroidX) | Apache-2.0 | active | **DEPEND** | Key-value is exactly the need (save blob + settings). Room **REJECT** (schema/migration tax for one row); SharedPreferences **REJECT** (legacy path, not for new code). |
| Persistence: board encoding | [Kotlin/kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | Apache-2.0 | active | **DEPEND (one JSON library)** | Exported board/save blobs must survive schema changes; one library only, no Moshi. |
| Screenshot tests | [takahirom/roborazzi](https://github.com/takahirom/roborazzi) | Apache-2.0 | 1,069★ · pushed 2026-10-22 | **DEPEND** (+ Robolectric) | Works with Robolectric on the JVM, so screenshot + Compose-semantics tests share one lane. [cashapp/paparazzi](https://github.com/cashapp/paparazzi) **REJECT** — pick one, not both. |
| Test libs | [google/truth](https://github.com/google/truth), [cashapp/turbine](https://github.com/cashapp/turbine), `kotlinx-coroutines-test` | Apache-2.0 | all pushed 2026-10-19/20 | **DEPEND** | Assertions + Flow/ViewModel testing without a device. [mockk/mockk](https://github.com/mockk/mockk) **REJECT** — hand-written fakes are enough, one fewer dependency. |
| Static analysis | [detekt/detekt](https://github.com/detekt/detekt) + [ktlint/ktlint](https://github.com/ktlint/ktlint) | Apache-2.0 / MIT | pushed 2026-10-23 / 2026-10-22 | **DEPEND (CI only)** | Catches the mistakes agents repeat; no runtime cost. |
| Credits screen | [mikepenz/AboutLibraries](https://github.com/mikepenz/AboutLibraries) | Apache-2.0 | 4,480★ · pushed 2026-10-18 | **DEPEND** | Generates the licence screen from the real dependency graph — required notices, zero hand-maintenance. |
| Icons | [lucide-icons/lucide](https://github.com/lucide-icons/lucide) | **ISC** (licence file read 2026-10-24) | 25,247★ · pushed 2026-10-23 | **ADAPT** ~8 icon paths into Compose `ImageVector`s, attribution kept | Keeps ONE icon set across the web reference and the app. [DevSrSouza/compose-icons](https://github.com/DevSrSouza/compose-icons) **REJECT** (last push 2024-09, stale); `lucide-compose` micro-repos **REJECT** (1★, no provenance). |
| Fonts | Roboto (Apache-2.0), self-hosted on web via fontsource | Apache-2.0 | — | **DEPEND/ADAPT** | Roboto is the Android system family → identical text on both sides with **zero** APK cost. |
| Ads (only if Phase 5 approved) | [googleads/googleads-mobile-android-examples](https://github.com/googleads/googleads-mobile-android-examples) + Google Mobile Ads SDK | Apache-2.0 / proprietary SDK | 1,885★ · pushed 2026-10-20 | **ADAPT** sample patterns; **DEPEND** SDK (test IDs until the owner's live-unit GATE) | Rewarded-first, consent-gated, Policy-isolated so "no ad call" is unit-testable. |
| Billing (ad-free IAP) | [googlesamples/play-billing-samples](https://github.com/googlesamples/play-billing-samples) + Play Billing Library | Apache-2.0 / proprietary SDK | 34★ · pushed 2026-10-05 | **STUDY/ADAPT with care** + **DEPEND** library | Samples historically lag library versions; verify every call against current Billing docs at Phase 5. |
| UI reference site stack | Vite + Tailwind + Konsta UI (Material theme) + Lucide | MIT / MIT / MIT / ISC | all active per `knowledge/web-ui-references.md` (checked 2026-10-24) | **DEPEND** (disposable, `design/reference/` only, never shipped) | Option A default: app-shaped Material screens that map ~1:1 to Compose. |

### Studied clean-room (reuse-first §4A)

| Target | Date | Behaviour taken | Not taken |
|---|---|---|---|
| gabrielecirulli/2048 (MIT) | 2026-10-24 | merge-order semantics (slide outward-in, one merge per tile per move), 90/10 spawn, win-at-2048 + keep-playing, game-over = no empty cell **and** no equal orthogonal neighbour | JS code, CSS, tile palette, fonts, branding |
| alexjlockwood/compose-multiplatform-2048 (MIT) | 2026-10-24 | domain vocabulary and animated tile-placement idea | its UI, colours, assets, code structure |
| andstatus/game2048 (Apache-2.0) | 2026-10-24 | history/undo modelling and JSON-save thinking | KorGE UI, its code |
| Ketchapp / HDuo / PixelProse / Step Two / ArceApps / Repi listings | 2026-10-24 | what users complain about and what features are commodity | any text, icon, screenshot, name or design |

## Chosen direction

**A — Quiet 2048 + Daily** (owner decision, 2026-10-24): 4×4 sliding-merge
puzzle; no banner ever; no ad during play; interstitial only *after* the game-over
dialog is dismissed with a hard cap; rewarded undo (+1, max 3 total per game) and
one rewarded theme unlock; one free undo per game; board auto-saved on every
valid move and resumed on launch; date-seeded Daily Challenge; English + Hindi
UI; one-time ad-free purchase. Budget: **3 weeks** (inside law 3 — not a waiver).

## In v1 / not in v1 (scope lock for 3 weeks)

**In:** one board size (4×4) · score + best (persisted) · win at 2048 + keep
playing · game-over detection · 1 free undo (+rewarded +1, max 3) · auto-save /
resume · Daily Challenge (date-seeded, local streak + best) · Stats (best, games,
highest tile, wins, daily streak) · Settings (sound, haptics, theme, language) ·
three themes (System/Light/Dark) + one milestone theme · EN + HI strings ·
rewarded ads + capped post-game interstitial + ad-free IAP · AboutLibraries
credits · privacy policy link.

**Out (v2+):** other board sizes, leaderboards, cloud save, multiplayer,
achievements, sharing cards, analytics, accounts, notifications, multiple
unlockable themes, Hindi store listing (in-app only at launch).

## Rejected, and why

- **Option B, C, D** — see the comparison table; A was chosen 2026-10-24.
- **Using "2048" as Play title / icon branding / package name** — Cirulli's MIT
  covers code, not the name; Play's Deceptive Behavior policy forbids
  impersonating apps/brands and requires titles that reflect the app without
  false affiliation ([policy read 2026-10-24](https://support.google.com/googleplay/android-developer/answer/9888077?hl=en)).
  Working name only in this repo ("Tilefold"); the official title is decided at
  publishing time.
- **Room, SharedPreferences, Moshi, Hilt, Koin, Paparazzi, mockk, Lottie,
  compose-icons, lucide-compose micro-repos** — reasons in the reuse map.
- **Cloning the reference screenshot's exact design, palette, icons or text** —
  behaviour and layout hierarchy only; tiles are our own cream→peach→gold ramp
  (values in `docs/UI-REFERENCE.md`).
- **Network features of any kind** (leaderboards, cloud save, remote daily
  boards) — no backend (law 13); daily seeds derive from the date locally.
- **Analytics SDKs** — no evidence of need; Data-Safety cost without a decision
  it would inform at this scale.

## Kill test (pre-written 2026-10-24, before any build or launch)

Measured on **day 29** after public release, on numbers read from Play Console
and the app's own crash reports (no user-level data):

**Kill if ANY of:**
1. Organic installs < **200/week** averaged over weeks 2–4, with the listing live
   and **zero** paid user acquisition.
2. D7 retention < **8%** (requires ≥300 installs; below that, treat as
   directional).
3. Crash-free sessions < **99.0%**.
4. Play rating < **4.0** with ≥30 ratings, **and** ad-related complaints ≥20% of
   the 1–2★ reviews.
5. Ad-free purchase conversion < **0.5%** of 28-day active users (only if
   Phase 5 shipped).

**Iterate once (≤1 week) only if** a single verified, fixable blocker explains a
miss (e.g. a save bug that lost boards, or one crashed screen). Otherwise
ship-next. Never rewrite these thresholds after seeing results.

**Daily-mode sub-test:** if <10% of D7-retained users open the daily board in
week 2, drop Daily Challenge in v2 instead of expanding it.

**Sample honesty:** below ~300 installs these numbers are directional; the
decision then weighs installs + ratings + review themes, and says so in writing.

## Data schema sketch (plan-level; no code in this chat)

Persisted locally in DataStore, keys namespaced, `schemaVersion` int, corrupt or
older payloads decoded defensively (fall back to an empty save; never crash):

- `save.v1`: `mode` (CLASSIC|DAILY) · `cells[16]` (exponent 0 = empty, 1 = 2,
  2 = 4 … 11 = 2048) · `score` · `moveCount` · `won` (2048 reached) ·
  `keepPlaying` · `rngSeed` + `rngCounter` (deterministic spawn resume) ·
  `undoSlot` (previous board+score+seed state, one entry) · `undoUsed` ·
  `dailyEpochDay` (DAILY only) · `savedAt`.
- `stats.v1`: `bestScore` · `gamesPlayed` · `wins` · `highestTile` ·
  `dailyStreak` · `lastDailyEpochDay` · `bestDailyScore`.
- `settings.v1`: `sound` · `haptics` · `theme` (SYSTEM|LIGHT|DARK|GOLD) ·
  `language` (en|hi) · `dpadVisible` · `adFreeCache` · `adsConsent` ·
  `interstitialShownCount` · `lastInterstitialAt`.
- Write rule: persist on **every valid move** (debounced ≤250 ms) **and** on
  `ON_STOP`; a resume must never show a board older than the last move.

## Evidence gaps / open items (not asked as questions — researched later)

- [ ] Full 1-star audit (10 per listing) — needs authenticated Play access or
      device installs; do before store text (Phase 6).
- [ ] In-app ad-placement observation (niche-method step 6) — install the top
      three listings on a device, log placements; Phase 0/1.
- [ ] Owner's reference screenshot — not yet provided in this chat; layout
      described in the brief only, so every visual value in
      `docs/UI-REFERENCE.md` is a **proposal** until Phase 0 approval.
- [ ] Play Console target-API requirement re-check on release day (law 6).

## Sources checked (2026-10-24)

- Play listing pages listed in § Listings inspected (rating, reviews, downloads,
  updated date, ads/IAP labels, data-safety summaries) — one fetch each, `hl=en&gl=IN`.
- `https://github.com/gabrielecirulli/2048` — licence API: MIT; 13,446★; pushed
  2024-10-24; forks 17,607.
- `https://github.com/alexjlockwood/compose-multiplatform-2048` — MIT; 471★;
  pushed 2024-10-21; tree has no test sources.
- `https://github.com/andstatus/game2048` — Apache-2.0; 359★; pushed 2026-07-19.
- `https://github.com/takahirom/roborazzi` (Apache-2.0, 1,069★, 2026-10-22),
  `https://github.com/cashapp/paparazzi` (Apache-2.0, 2,637★, 2026-10-23).
- `https://github.com/mikepenz/AboutLibraries` (Apache-2.0, 4,480★, 2026-10-18),
  `https://github.com/InsertKoinIO/koin` (Apache-2.0, 10,022★, 2026-10-22),
  `https://github.com/detekt/detekt` (Apache-2.0, 2026-10-23),
  `https://github.com/ktlint/ktlint` (MIT, 2026-10-22).
- `https://github.com/lucide-icons/lucide` — ISC licence text read via GitHub
  licence API; 25,247★; pushed 2026-10-23.
- `https://github.com/android/compose-samples` / `architecture-samples` /
  `androidx/androidx` — Apache-2.0, active.
- AppFlow playbook `57597b3332ad578614a0d9d5609f9cf8a278ee8a`:
  PLAN.md, START.md, RULES.md (target API 36 as of 2026-10-10), PROMPTS.md,
  `templates/rule-app.md`, `templates/prompt-app.md`, `templates/readme-app.md`,
  `templates/docs-system/*`, `knowledge/niche-method.md`, `reuse-first.md`,
  `open-source-references.md`, `app-engineering.md`, `ui-reference-site.md`,
  `web-ui-references.md`, `fast-quality-no-device.md`.
- Play Deceptive Behavior policy —
  `https://support.google.com/googleplay/android-developer/answer/9888077?hl=en`
  (impersonation/brand-mimicry prohibition).
