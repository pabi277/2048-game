<!-- SPDX-License-Identifier: MIT -->

# Tilefold (working name) — continuation prompt

> **Self-contained handoff. Paste this file as the first message of the next
> chat, then give one command** (e.g. `start phase 0` or a bug report). This
> repo is self-contained: `rule.md` holds the full rules, `docs/` the plan and
> decisions, THIS FILE the living state. AppFlow is provenance only — nothing
> here depends on it. Update this file at the end of every phase, in the same
> commit as the phase record.

## Identity (fixed at the exit gate — answered 2026-10-24)

- App: **Tilefold** (working name) · package: **`io.github.pabi277.tilefold`** ·
  license: **MIT**
- Official Play title: **Not set** — and it will not be "2048" (recorded in the
  portfolio register only when confirmed in Play Console)
- Provenance: bootstrapped from AppFlow @
  `57597b3332ad578614a0d9d5609f9cf8a278ee8a` (reference only — this repo is
  self-contained)
- Git author: **GitHub noreply identity of the repo owner** (literal verified
  with `git var` before the first commit) · session branch: assigned per chat ·
  never push `main`
- Budget: **3 weeks** from Phase 0 (owner decision 2026-10-24; no law waiver)

## Rules digest (full rules: `./rule.md` — self-contained)

- Work one **phase** per chat (a phase = 2–4 parts, one whole capability). No
  work outside the current phase's "Done means".
- Never create/switch branches, push `main`, force-push, or `reset --hard`.
- No PR, merge, release, publish, deploy, or Play submission without the owner's
  explicit command in chat. Green CI is not authorization.
- **Ask less, gate always** (`rule.md` §3): GATE items are asked once, in one
  Summary batch. DECIDE-ONCE answers live in `docs/DECISIONS.md` and are **never
  re-asked**. Everything else is researched or decided-and-logged.
- Evidence before hypothesis (`file:line`). CI green required, with the
  downloadable `app-progress-apk` artifact uploaded. Docs ride with the code
  commit: phase record, roadmap status, `prompt.md`.
- **Reuse first**: scan before building any part; record
  DEPEND/ADAPT/STUDY/BUILD/REJECT in the phase record; one UI base, one library
  per job, shipped licences logged in `docs/DECISIONS.md`.
- **UI from the approved reference**: no UI coding before Phase 0-D is approved
  and tokens are frozen in `docs/UI-REFERENCE.md`; copy screens, screenshot-
  compare, record deliberate native adaptations only. The reference lives in
  `design/reference/` (disposable, never shipped); its tokens are the app's
  tokens, guarded by `npm run verify` and `TokensSyncTest`.

## Decisions that must never be re-asked (full log: `docs/DECISIONS.md`)

- Direction: **A — Quiet 2048 + Daily** (no banner, no in-play ad, interstitial
  only after game-over dismissal with a hard cap, rewarded undo/theme, ad-free
  purchase, auto-save/resume, daily seeded challenge).
- Languages: **English + Hindi** in-app; English Play listing at first.
- Monetization: **rewarded-first as proposed**; test ad IDs until the owner's
  live-unit GATE.
- Time budget: **3 weeks** (inside law 3 — no waiver).
- Design stack: **Option A** (Vite + Tailwind + Konsta + Lucide); font Roboto;
  icons Lucide both sides.
- Engine: pure Kotlin, seeded PRNG, deterministic; UI never re-implements rules.
- Persistence: DataStore + kotlinx.serialization; save every valid move.
- Screenshot gate: Roborazzi + Robolectric; DI: manual `AppContainer`.
- SDKs: `minSdk 24`, `targetSdk 36` (re-check on release day).
- Exit gate (2026-10-24): **plan approved** · repo **public · MIT** authorized ·
  git author = GitHub noreply of the repo owner · working name **Tilefold**,
  package **`io.github.pabi277.tilefold`** (literal confirmed at first commit;
  permanent after publishing) · Play title unset ("2048" is not ours to use).

## State (update at every phase end)

- **Phase 0 ✅ 2026-10-24** — snapshot `2a55172`, skeleton `89970a1`, CI green in
  run 40064397149 at `bdd5789`. Artifacts: `app-progress-apk` (10,837,751 B),
  `app-screenshots` (16 PNGs), `quality-gate-reports`. Record:
  `docs/phases/00-bootstrap.md`.
- **Phase 0-D ✅ 2026-10-24 — UI reference approved, tokens frozen, merged.**
  - 0-D.1 ✅ — disposable Vite + Tailwind + Konsta + Lucide reference under
    `design/reference/`: **30 states** (the 27 doc rows + restart confirm +
    reset-stats confirm + Hindi Home) + a tokens page, plus 32 archived renders
    in `design/reference/screenshots/`. Live preview: `npm run dev` on port 5180.
    CI run 40067703773 green at `f34a181` (app gate + new `ui-reference` job).
  - 0-D.2 ✅ — round 1 (owner: **approved with changes**): Home gained a
    decorative board still, the non-square tile bug was fixed (tiles now fill
    their cell), then the owner approved in chat and authorized the merge.
  - 0-D.3 ✅ — tokens, mapping, contrast record and approval table
    are in `docs/UI-REFERENCE.md`; the app theme now reads
    `app/.../ui/theme/Tokens.kt` (single token source, `SeedColors.kt` deleted);
    `TokensSyncTest` proves doc ↔ app; screenshot tests are **goldens** in
    `app/src/test/screenshots/` verified by `testDebugUnitTest` (a visual diff
    fails the build). Tokens approved + frozen in chat on 2026-10-24
    (`e0393be`), then merged to `main`.
    Gate green in run 40070687033 at `e0d0c95`: `app-progress-apk` 10,849,239 B,
    `app-screenshots` 253,477 B (5 goldens), `quality-gate-reports` 56,093 B.
- **Phase 1 ✅ 2026-10-24 — pure-Kotlin engine + tests (awaiting the merge
  gate).** Gate green in run 40076597395 at `20a9648`: `app-progress-apk`
  10,891,006 B, `app-screenshots` 253,477 B (same 5 goldens), `quality-gate-
  reports` 66,822 B. Record: `docs/phases/01-engine.md`.
  - 1.1 ✅ — `domain/Board.kt` (immutable 16 cells), `domain/Moves.kt`
    (`Moved`/`NoMove`, one merge per tile, leading-edge scan), `domain/
    EngineRandom.kt` (`xorshift64*` with the cursor as the saved state +
    `Spawner`: value first, then cell).
  - 1.2 ✅ — `domain/Game.kt`: score = merged sum, win at 2048 + keep playing,
    game over = full + no equal orthogonal neighbour, one-snapshot undo
    (board + score + RNG), refused input is free (same instance, no draw).
  - 1.3 ✅ — `domain/SaveCodec.kt` (`schemaVersion` 1, defensive
    `decode → null`, nested undo); 66 new host tests: per-direction merge
    cases, 90/10 spawn over 20,000 spawns, **golden replays** (seed
    `20261024` + `LLUURDDLR`, seed `7` + `UDUDLRLR`), 300-board property
    tests, save round-trip and refusal cases.
  - 1.4 ✅ — `ui/EnginePreview.kt`, plain numerals + four arrows, shown only
    behind `BuildConfig.DEBUG`; the release build still opens the hello screen.
- Facts read back from the repo/CI (not memory): AGP 8.13.0, Kotlin 2.2.20,
  Gradle 8.14.3, Compose BOM 2025.09.01, minSdk 24 / targetSdk 36 /
  compileSdk 36; gate command `./gradlew check` = debug unit tests + lint +
  detekt + ktlint; reference stack Vite 8.3.5 / Tailwind 4.3.3 / Konsta 5.5.0 /
  lucide-react 1.61.0 (ISC, web only).
- **Tokens frozen 2026-10-24** (freeze commit `e0393be`, approved in chat):
  the values in `docs/UI-REFERENCE.md` = `design/reference/src/design.js` =
  `app/.../ui/theme/Tokens.kt`, guarded by `npm run verify` + `TokensSyncTest`.
  Visual changes go reference → doc → `Tokens.kt` → screen, never the reverse.
- Next: **phase 2 — board UI + swipe + animation** (parts 2.1–2.3), built from
  the frozen reference; the engine is consumed, never re-implemented.
- Open owner items: the reference screenshot is no longer needed (the reference
  is ours and approved); live ad units only in phase 5 and only after a GATE.

## How to continue

1. Read `docs/ROADMAP.md`, `docs/DECISIONS.md`, `docs/UI-REFERENCE.md` and the
   latest `docs/phases/` record — trust the repo, not memory.
2. Implement **phase 2** (board UI + swipe + animation), all its parts,
   evidence-first, against `docs/UI-REFERENCE.md` and
   `design/reference/screenshots/`; the engine in `domain/` is the only rules
   source (the UI never re-implements a merge).
3. End with: CI green (incl. the no-device quality gate) + the run's
   downloadable `app-progress-apk` artifact · `docs/phases/02-board-ui.md` (say
   what the APK shows, and record where the Compose screens differ
   deliberately from the reference) · roadmap row status · this file updated ·
   short report (run id, tip sha, APK, what is now visible) · **the next-chat
   prompt printed in chat** for phase 3 · stop at the merge gate.
