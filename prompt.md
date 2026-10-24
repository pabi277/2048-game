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
  compare, record deliberate native adaptations only.

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

- **Plan chat completed 2026-10-24.** Plan approved; repository creation
  authorized. The plan documents are committed to this branch (docs-only commit;
  read its sha with `git log -- docs/ROADMAP.md`). **No app code, no CI, no
  playbook snapshot yet** — those are phase 0.
- Phase 0 📋 not started · CI: none · APK: none · next step runs in a **new
  chat** and begins by committing the playbook snapshot (`templates/` +
  `knowledge/`) and the provenance line **before** any app code; the plan docs
  are already in place.
- Next: **phase 0 — Bootstrap + hello** — parts 0.1 snapshot + plan docs +
  LICENSE + README + `rule.md` + `prompt.md` · 0.2 Gradle skeleton + hello
  screen + one unit test + one screenshot test + lint · 0.3 CI + version pins +
  licence log
- Then **phase 0-D — UI reference site + tokens freeze** (owner approves in
  chat; no UI code before that)
- Open owner items: exit-gate answers (plan approval · repo visibility + licence
  · git author identity · working name + package ID); the reference screenshot
  for Phase 0-D; live ad units much later.

## How to continue

1. Read `docs/ROADMAP.md`, `docs/DECISIONS.md`, `docs/UI-REFERENCE.md` and the
   latest `docs/phases/` record — trust the repo, not memory.
2. Implement **phase 0**, all its parts, evidence-first, with the reuse map in
   `docs/RESEARCH.md` as the base.
3. End with: CI green (incl. the no-device quality gate) + the run's
   downloadable `app-progress-apk` artifact · `docs/phases/00-bootstrap.md` (say
   what the APK shows) · roadmap row status · this file updated · short report
   (run id, tip sha, APK, what is now visible) · **the next-chat prompt printed
   in chat** for phase 0-D · stop at the merge gate.
