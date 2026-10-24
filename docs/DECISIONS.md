<!-- SPDX-License-Identifier: MIT -->

# Decisions — Tilefold (working name)

> The anti-re-ask register (`rule.md` §3). Every question is answered here ONCE.
> Later chats read this file before asking anything. Re-asking a recorded answer
> is a defect. Changing an owner answer is a GATE.

## Owner decisions

- **2026-10-24 — Plan-chat scope (PLAN.md stage 1)** — plan-only session, no
  repository/branch/commit/app code until the exit gate is answered; pinned
  playbook `57597b3332ad578614a0d9d5609f9cf8a278ee8a`.
- **2026-10-24 — App/direction** — a 2048-style 4×4 sliding-merge puzzle for
  Android, following the owner's brief (behaviour and layout only; no copying of
  the reference app's branding, assets or exact design).
- **2026-10-24 — Differentiation (batch A)** — **Option A "Quiet 2048 +
  Daily"**: no banner ever; no ad during play; interstitial only after the
  game-over dialog is dismissed, hard-capped; rewarded undo (+1, max 3) and one
  rewarded theme unlock; one free undo per game; auto-save/resume on every move;
  date-seeded Daily Challenge.
- **2026-10-24 — Languages (batch A)** — **English + Hindi** in-app strings at
  launch. Play listing text in English at first.
- **2026-10-24 — Monetization (batch A)** — **Rewarded-first, as proposed**:
  rewarded undo / extra theme; interstitial ONLY on the game-over screen after
  dismissal with a strict frequency cap; **no banner**; **no ad during play**;
  one-time ad-free purchase always offered. Test ad units only until the owner
  authorizes live units (separate GATE).
- **2026-10-24 — Time budget (batch A)** — **3 weeks.** This is *within* RULES
  law 3 (≤4 weeks), so it is **not** a law waiver; there is no waiver to record.
- **2026-10-24 — Exit gate (batch B): plan** — **APPROVED**, including the
  3-week scope lock in `docs/RESEARCH.md` § "In v1 / not in v1".
- **2026-10-24 — Exit gate (batch B): repository** — **authorized: public
  repository, MIT licence.** Creation and the playbook snapshot happen in the
  **Phase 0 chat**, not in the plan chat (PLAN.md stage 1 creates nothing).
- **2026-10-24 — Exit gate (batch B): git author identity** — the **GitHub
  noreply address of the account that owns the new repository** (not a personal
  address; AppFlow's addresses are never copied into this repo). The exact
  literal is read from the GitHub account and verified with `git var` before the
  first commit in Phase 0 — a factual read, not a re-ask of this decision.
- **2026-10-24 — Owner instruction: commit the plan documents** — commit the
  plan file set (`README.md`, `rule.md`, `prompt.md`, `docs/`) on the current
  session branch as a **docs-only** commit. No app code, no CI, no playbook
  snapshot, no push, no PR.
- **2026-10-24 — Exit gate (batch B): working name + package ID** — **Tilefold**
  · package **`io.github.pabi277.tilefold`** (owner selected
  `io.github.<owner>.tilefold`; `<owner>` resolved to the GitHub account that
  owns this repository, `pabi277`). The literal is confirmed at the first
  Phase 0 commit — free to change until the app is published, **permanent after
  that**. The official Play title remains unset and will not be "2048".

- **2026-10-24 — Docs committed before the playbook snapshot** — START.md
  §2/§2.5 puts the frozen `templates/` + `knowledge/` snapshot and its provenance
  line *before* the plan docs. The owner asked for the docs commit now, so the
  order is inverted **here only**: phase 0.1 still commits the snapshot and the
  required provenance line before any app code, and the README states the pin
  without claiming a snapshot that does not exist yet. Reversible on objection;
  no law or invariant is waived.
## Law waivers (owner-approved exceptions to this repo's `rule.md`)

- None. The 3-week budget is not a waiver (law 3 allows up to 4 weeks).

## Agent decisions (reversible — object if wrong)

- **2026-10-24 — Resolving "direction A + 3 weeks"** — the batch-A option text
  for 3 weeks said such a budget "forces B/C scope", but the owner chose A. I
  kept **every A differentiator** and cut **breadth** instead: one board size
  (4×4); three themes (System/Light/Dark) + one milestone theme; no
  achievements, leaderboards, cloud save, multiplayer, sharing cards, analytics,
  accounts or notifications. Reversible on objection — adding breadth back needs
  a re-plan, not a re-ask of batch A.
- **2026-10-24 — Working name** — **Tilefold** (placeholders: repo `tilefold`).
  Chosen because "2048" must not be our Play title or package branding
  (Cirulli's MIT covers code, not the name; Play's Deceptive Behavior policy
  forbids brand mimicry). Final working name + package ID are exit-gate items.
- **2026-10-24 — UI reference stack** — **Option A: Vite + Tailwind + Konsta UI
  (Material theme) + Lucide**, the recorded plan default (no research reason to
  choose B/C). Disposable, `design/reference/` only, never shipped.
- **2026-10-24 — Icons** — one set: **Lucide (ISC)** on both sides; web via the
  `lucide` package, Android via ~8 adapted `ImageVector` paths with attribution.
  Fallback (Material Icons + glyph table) only if adaptation proves error-prone;
  a switch would be logged here, not done silently.
- **2026-10-24 — Font** — **Roboto** on both sides (Android system family,
  self-hosted on web via fontsource): identical metrics, zero APK cost.
- **2026-10-24 — Architecture** — single-activity, one `app` module, clean
  packages (`ui/`, `domain/`, `data/`), one ViewModel per screen, `StateFlow` +
  UDF, adapted from `android/architecture-samples` (Apache-2.0).
- **2026-10-24 — DI** — **no DI framework**; a small manual `AppContainer`
  (≡3 graph nodes). Hilt/Koin rejected in the reuse map for this size;
  revisit above ~5 nodes. Recorded deviation from the catalogue default
  (which prefers Hilt, or Koin for tiny apps).
- **2026-10-24 — Screenshot tests** — **Roborazzi + Robolectric** (one lane for
  screenshots and Compose semantics tests); Paparazzi rejected (pick one).
- **2026-10-24 — Persistence** — **DataStore Preferences** for save/stats/
  settings + **kotlinx.serialization** for the save blob (schemaVersion,
  defensive decode). Room and SharedPreferences rejected.
- **2026-10-24 — Engine RNG** — engine-owned seeded PRNG (no
  `java.util.Random`, no `System.currentTimeMillis`) so boards are reproducible
  in tests and the daily board is reproducible from a date seed.
- **2026-10-24 — Daily seed convention** — seed = the **UTC epoch-day** of the
  launch date, so "same board for everyone" is true across time zones; the UI
  says when the next daily board appears. Reversible.
- **2026-10-24 — Undo rules** — 1 free undo per game; rewarded +1 up to 3 total;
  undo is allowed on the game-over dialog (verified user request in research);
  undo restores board, score and RNG position so the future stays deterministic.
- **2026-10-24 — Interstitial cap** — only after the game-over dialog is
  dismissed; at most 1 per 3 game-overs and ≥120 s apart; never for ad-free
  users, never before consent, never on win/keep-playing.
- **2026-10-24 — SDK levels** — `minSdk 24`, `targetSdk 36` (RULES law 6; the
  playbook's policy checkpoint of 2026-10-10 says API 36 — re-check on release
  day). `compileSdk` follows the pinned AGP at Phase 0.
- **2026-10-24 — Version pins** — no library version is written into planning
  docs; every DEPEND is pinned at Phase 0 from its official artefact and logged
  in § Third-party licences below.
- **2026-10-24 — Sound** — small original/generated cues only (merge, win,
  button tick); no third-party audio assets, so no new licence surface. If a cue
  cannot be generated cleanly, the feature ships silent rather than borrowed.

## Third-party licences (everything shipped, per reuse-first §3)

Planned at plan time; **versions are pinned and confirmed at Phase 0**, and this
table is updated from the real dependency graph (`AboutLibraries`) with the
shipped licence of each entry.

- Planned — Kotlin / AndroidX (Compose, DataStore, Navigation, Lifecycle,
  Activity) — Apache-2.0 — DEPEND (platform).
- Planned — kotlinx.serialization — Apache-2.0 — DEPEND (save encoding).
- Planned — Roborazzi — Apache-2.0 — DEPEND (test only).
- Planned — Robolectric — Apache-2.0 — DEPEND (test only).
- Planned — Truth, Turbine, kotlinx-coroutines-test — Apache-2.0 — DEPEND (test only).
- Planned — detekt (Apache-2.0) / ktlint (MIT) — DEPEND (CI only).
- Planned — AboutLibraries — Apache-2.0 — DEPEND (credits screen).
- Planned — Lucide icons (adapted paths) — ISC — ADAPT, notice in credits.
- Planned — Roboto — Apache-2.0 — DEPEND/ADAPT (font).
- Planned — Google Mobile Ads SDK — proprietary SDK, Play policy-bound —
  DEPEND (Phase 5 only, test IDs until the live-unit GATE).
- Planned — Play Billing Library — proprietary SDK — DEPEND (Phase 5 only).

## Open (not plan-chat questions — later gates or factual reads)

- [ ] Read the exact GitHub-noreply literal from the owning account and verify
      it with `git var` before the first Phase 0 commit (decision already made).
- [ ] Confirm `io.github.pabi277.tilefold` as the literal package ID at the first
      commit (permanent once published).
- [ ] Phase 0-D: owner approves the UI reference site in chat → tokens freeze.
- [ ] Later GATE items, each in its own phase: live AdMob unit IDs, Play signing
      secrets, tester list, Play submission — never in chat or git.
- Note: the plan chat created **no** repository, branch, commit or code; this
  file set is the input to the Phase 0 chat.
