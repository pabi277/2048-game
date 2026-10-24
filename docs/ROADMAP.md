<!-- SPDX-License-Identifier: MIT -->

# Roadmap — Tilefold (working name)

Bootstrapped from AppFlow @ 57597b3332ad578614a0d9d5609f9cf8a278ee8a (planned; snapshot
commit happens in Phase 0 — nothing has been created yet)

> One phase = one chat (`rule.md` §4). A phase is a decent chunk: one whole
> capability, 2–4 parts listed in its `docs/phases/NN-<slug>.md` record.
> Status: 📋 planned · 🚧 in progress · ✅ done · ❌ cancelled (keep the row).
> **"Done means" always includes**: CI green on the session branch, the
> no-device quality gate (`knowledge/fast-quality-no-device.md` §1 — unit +
> Robolectric + screenshot + lint on the JVM), and the run's downloadable
> **`app-progress-apk`** artifact that installs and shows that phase's
> increment. A physical device never gates a phase.
>
> **Budget: 3 weeks** (owner decision 2026-10-24; inside law 3 — not a waiver).
> The scope lock in `docs/RESEARCH.md` § "In v1 / not in v1" is what makes that
> possible: one board size, three themes + one unlockable, no achievements,
> leaderboards, cloud, multiplayer, sharing or analytics.

| # | Phase (2–4 parts) | Parts | Done means | Status |
|---|---|---|---|---|
| **0** | **Bootstrap + hello** | 0.1 playbook snapshot (templates/ + knowledge/ + provenance line) then plan docs + `LICENSE` + `README` + `rule.md` + `prompt.md`, committed **before any app code** · 0.2 minimal Gradle/AGP skeleton: one `app` module, single activity, hello screen in the theme generated from the frozen seed, one host unit test, one Roborazzi screenshot test, lint/detekt wired · 0.3 `ci-build.yml` from `templates/ci-build.yml` (secret **names** only) + version pins read from official artefacts + licences logged in `DECISIONS.md` | CI green with the no-device gate; `app-progress-apk` uploads; APK opens on the hello screen; snapshot + plan docs committed before any feature code | ✅ (run 40064397149, `docs/phases/00-bootstrap.md`) |
| **0-D** | **UI reference site + tokens freeze** | 0-D.1 phone-shaped Vite+Tailwind+Konsta+Lucide prototype under `design/reference/` covering every screen/state in `docs/UI-REFERENCE.md` (27 rows + the restart/reset confirms + a Hindi Home run = 30 states) · 0-D.2 owner review rounds **in chat** (colour, spacing, hierarchy, Hindi length) · 0-D.3 freeze tokens + web→Compose mapping in `docs/UI-REFERENCE.md`, point the app theme at the frozen tokens, switch the screenshot tests to goldens | Every screen/state presented; owner approves explicitly in chat; tokens + mapping frozen; reference committed under `design/reference/`; **no later UI phase may start before this row is ✅** | ✅ (run 40073640384, `docs/phases/00d-ui-reference.md`; tokens frozen 2026-10-24) |
| **1** | **Pure-Kotlin engine + tests** | 1.1 board model, slide/merge for all 4 directions (one merge per tile per move, outward-in order), spawn 90% 2 / 10% 4 via an engine-owned seeded PRNG · 1.2 score = sum of merges, win at 2048, keep-playing, game-over = no empty cell and no equal orthogonal neighbour, undo snapshot semantics · 1.3 save codec (schemaVersion, defensive decode) + deterministic replay tests (same seed ⇒ same board), golden-board tests, property tests (no tile loss, score delta equals merged sums) · 1.4 debug-only `EnginePreview` screen (plain numerals, no design) so the APK shows tiles moving | Engine is pure Kotlin (no Android imports), seeded and reproducible; host test suite green incl. golden + property tests; debug-only preview moves tiles with arrow buttons; `app-progress-apk` shows a working 4×4 board moving | 📋 |
| **2** | **Board UI + swipe + animation** | 2.1 `GameScreen` built from the frozen reference: title row, SCORE/BEST boxes, home + restart buttons above the rounded board, 4×4 `TileGrid` with the value→ramp tokens and numeral flip · 2.2 gestures: horizontal/vertical swipe with a pure, unit-tested `SwipeAxis` mapper + direction-pad fallback + hardware-keyboard arrows; invalid moves do nothing (no spawn) · 2.3 motion: slide, merge-pop, spawn-scale, score tick, reduced-motion variant; pause sheet + auto-pause on background; screenshot tests for new/mid-game/paused/dark/2.0× font | Swiping and the pad both move tiles on the approved layout; animations smooth and state-driven; pause/resume works; screenshot comparisons recorded in the phase record; APK shows the real game screen | 📋 |
| **3** | **Persistence, game states, undo, stats, daily** | 3.1 save on every valid move (debounced ≤250 ms) + on stop; resume on launch with the exact board, score, win/keep-playing flag and RNG position; corrupt-save fallback + storage-error banner · 3.2 win dialog with "Keep playing", game-over dialog with final score / best / "New best", restart + home confirms · 3.3 undo: 1 free per game, undo-after-game-over allowed, rewarded +1 (max 3) behind an ad `Policy` that is host-testable — no ad call when policy says no · 3.4 Stats screen (best, games, highest tile, wins, daily streak) + Daily Challenge (date-seeded 4×4 board, same for everyone, local streak + best daily score) | Kill/reopen the app and the board is exactly where it was; win, keep-playing, game-over and undo all proven by Robolectric/unit tests + screenshots; daily board reproducible from a fixed date seed in tests; APK shows resume, win/game-over dialogs, undo and a daily game | 📋 |
| **4** | **Home, How to play, Settings, themes, EN+HI, sound & haptics** | 4.1 Home (continue / new / daily cards, empty + error states) and How to play, per reference · 4.2 Settings: sound, haptics, theme (System/Light/Dark + one milestone-unlocked gold theme), language (English/हिन्दी), direction-pad toggle, reset stats confirm, licences + privacy links · 4.3 full string externalisation EN + HI with a parity test (no missing/unused keys), Hindi long-text screenshots · 4.4 sound + haptics: one merge cue, one win cue, one button tick, all original/generated assets or none, gated by settings, silenced when the device is on vibrate | Every string localized and parity-tested; theme + language switches take effect without restart; sound/haptics respect settings; screenshots for every new screen/state incl. Hindi at 2.0× font; APK shows the complete app minus ads | 📋 |
| **5** | **Ads + ad-free purchase (only if approved in the plan batch — it was)** | 5.1 ads `Policy` (pure Kotlin): may show rewarded undo, may show interstitial only on the game-over screen after dismissal, cap 1 per 3 game-overs and ≥120 s apart, never when ad-free, never before consent; unit tests prove "policy says no ⇒ no SDK call" · 5.2 Google Mobile Ads integration with **test IDs only**, consent flow, rewarded undo (+1, max 3) and one rewarded theme unlock; graceful no-fill states · 5.3 Play Billing: one non-consumable ad-free product, purchase + restore, entitlement cached in DataStore and surviving reinstall (restore path verified on the release candidate) | Policy tests green incl. ad-free/consent/cap cases; test-ID ads render in the gallery state; no banner and no in-play interstitial anywhere in the build; purchase + restore work; Data-Safety/privacy text updated; APK shows the rewarded + purchase flows (test IDs) | 📋 |
| **6** | **Polish, release prep, store listing draft** | 6.1 a11y pass (TalkBack labels/order, 48dp targets, font scale 2.0 everywhere, reduced motion), empty/error audit, AboutLibraries credits screen · 6.2 performance + size: cold start, jank check, bundletool download size within the recorded budget, R8/own-APK inspection (no test IDs, no debug code, no secrets) · 6.3 release hygiene: version code/name read back from the artefact, target API re-checked on release day, privacy policy + Data Safety truthful, Play checklist and AdMob checklist green, store text + screenshot shot-list drafted from verified features only, owner-verified | No-device gate green on the exact release commit; size within budget; own-APK inspection clean; privacy/Data-Safety match the shipped build; listing draft contains no invented claims; **stops before submission** — release needs the owner's explicit command | 📋 |

## Held / cancelled (keep visible)

- *(nothing yet)*

## Dependencies between rows

- **0 → 0-D**: 0-D needs CI plus a hello screen whose theme consumes the frozen
  seed. 0-D must be ✅ before Phase 2 paints a single tile.
- **1 → 2**: Phase 2 consumes the engine only; it must not re-implement rules in
  the UI layer.
- **3 → 5**: the ad `Policy` needs the game-over and undo state machines from
  Phase 3.
- **5 → 6**: release text must describe what Phase 5 actually shipped.

## Phase-level data/schema notes (no code here)

- `save.v1`, `stats.v1`, `settings.v1` shapes are sketched in
  `docs/RESEARCH.md` § Data schema sketch. Any field added later bumps
  `schemaVersion` and needs a decode test — a lost board is a data-loss bug.
- Colour tokens are the only source of colour values; no screen may introduce a
  hex value. Tile ramp lookup is a single pure function with its own unit test
  (every value 2…2048+ maps to exactly one token pair).
