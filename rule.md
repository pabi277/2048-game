<!-- SPDX-License-Identifier: MIT -->

# Tilefold (working name) — rules

> The operating manual for THIS repository — **self-contained**: follow it even
> if no other repo is reachable. Derived from AppFlow @
> `57597b3332ad578614a0d9d5609f9cf8a278ee8a` (provenance only; the text below is
> authoritative for this app). Owner answers and waivers live in
> `docs/DECISIONS.md`; the living state lives in `prompt.md`.

## 1. Identity

- App: **Tilefold** (working name; the official Play title is decided at
  publishing time and is not "2048") · package: **`io.github.pabi277.tilefold`**
  (literal confirmed at the first commit; permanent once published) ·
  license: **MIT**
- Git author: **GitHub noreply identity of the account owning this repository**
  (exact literal read from the account and verified with `git var` before the
  first commit; never a personal address, never AppFlow's addresses) · session
  branch only
- Time budget: **3 weeks** (owner decision 2026-10-24; within law 3, so **no
  waiver** — see `docs/DECISIONS.md` § Law waivers)
- Product: an offline 4×4 sliding-merge number puzzle (score, best, win at 2048
  with keep-playing, game over, one-free-undo + resume-anywhere, date-seeded
  Daily Challenge, EN+HI, three themes + one milestone theme, respectful ads +
  ad-free purchase).

## 2. Branching and the merge gate (law)

- Work only on the assigned session branch. Never create/switch branches, push
  `main`, force-push, or `git reset --hard`.
- Commit and push the session branch freely. Before committing, verify
  author/committer identity with `git var` and inspect the commit before
  pushing. Never use an agent's personal email/account.
- The agent never opens a PR, merges, pushes `main`, tags, releases, publishes,
  deploys, or submits to Play without my explicit command in chat. Green CI is
  evidence, not authorization. "Open a PR" does not mean "merge".
- One phase at a time; never a second PR while one is open.

## 3. Questions — ask less, gate always

**GATE — always ask; never act without an explicit yes:**
repo creation · app name + package ID (permanent once published) · git author
identity · license if unfixed · PR · merge · `main` push · release · publish ·
deploy · Play submission · money in any form (including live ad units and paid
SDKs) · legal/tax/store-account actions · tester personal data · waiving a law of
this file · changing an answer recorded in `docs/DECISIONS.md`.
→ One Summary batch per gate moment, ≤ 4 questions, each naming its gate.

**DECIDE-ONCE — asked once, in the plan chat, recorded in `docs/DECISIONS.md`
with the date:**
languages/locales · monetization model · scope cuts · time budget · design
direction. All five are answered (2026-10-24).
→ Never re-ask a recorded answer — that is a defect, not caution. Changing one
is a GATE.

**DEFAULT — never ask:** researchable facts (research, cite source + date) ·
reversible implementation choices (log under Agent decisions) · anything already
answered in chat or docs · taste with an obvious default.

## 4. Work cycle — one phase per chat

1. Verify state first: `git status`, `git log`, PRs, CI runs. Trust the
   repository, not memory.
2. Evidence before hypothesis: `file:line` reads against the current tree. No fix
   on a guess.
3. Reuse scan first, then implement the current phase only (2–4 parts; one whole
   capability). Before building any part, check whether a verified library,
   official sample or studied pattern already does it (frozen
   `knowledge/reuse-first.md`; verdicts DEPEND/ADAPT/STUDY/BUILD/REJECT) and open
   the phase record with its "Reused" line. Its "Done means" cell in
   `docs/ROADMAP.md` is the acceptance test. Nothing outside it.
4. Tests + CI green, **without device tests**: the no-device quality gate (frozen
   `knowledge/fast-quality-no-device.md` §1 — unit + Robolectric + screenshot +
   lint on the JVM) is part of "Done means"; a physical device never gates a
   phase. CI is the executor of record. The run must upload the downloadable
   **`app-progress-apk`** artifact — every phase must be visible to me as a
   sideloadable build.
5. Docs ride with the code commit: `docs/phases/NN-<slug>.md` record (including
   what the APK shows), roadmap status, `docs/DECISIONS.md` if anything new, and
   `prompt.md` updated.
6. Report (run id, tip sha, APK artifact name, what is now visible), then
   **print the next-chat prompt IN CHAT** — one fully-filled copyable block from
   `templates/next-chat-prompt.md` for the next phase (parts, "Done means",
   decisions never to re-ask, CI/APK facts). Then **stop at the merge gate** (§2).

## 5. Invariants — do not break

- **The engine is pure Kotlin, deterministic and host-testable.** No Android
  imports in `domain/`; no `java.util.Random`, no wall clock, no I/O inside the
  engine; every board is reproducible from `(seed, moves)`. A rule that exists
  only in the UI layer is a bug.
- **Rules are the product.** Slide/merge semantics: all tiles move in the swipe
  direction, a tile merges at most once per move, merges resolve from the
  leading edge inward, score increases by the merged value, one new tile spawns
  after a *valid* move only (90% 2 / 10% 4), win at 2048 with keep-playing,
  game over only when no empty cell **and** no equal orthogonal neighbour.
- **A board is never lost.** Persist on every valid move and on stop; corrupt or
  older payloads decode defensively; destructive migration is a data-loss bug.
  Resume must show the board at the last move, never an older one.
- **Offline-first core**; no backend, login, analytics or notifications without
  recorded evidence of need. Daily challenge derives from a date seed locally.
- **UI follows the approved reference**: no app UI code before the reference site
  is owner-approved and tokens frozen in `docs/UI-REFERENCE.md` (frozen
  `knowledge/ui-reference-site.md`); screens are copied from the reference and
  screenshot-compared; differences only as recorded deliberate native
  adaptations; visual changes go reference-first, then screen — never reverse.
  No screen may define its own colour, spacing or type value.
- **Ads:** rewarded-first, never during play, **no banner**, interstitial only
  after the game-over dialog is dismissed (≤1 per 3 game-overs, ≥120 s apart),
  never before consent, never for ad-free users; the ad-free purchase is always
  offered; test ad units only until live units are explicitly authorized (GATE);
  the ad `Policy` is pure Kotlin and unit-tested so "policy says no ⇒ no SDK
  call" is provable.
- **Accessibility is a screen state**: 48dp targets, TalkBack labels/order, font
  scale 2.0 survival, reduced-motion variant, and Hindi long-text screens.
- No secrets, real ad IDs, account IDs, or tester personal data in git or chat.
- Agent-invented facts are never shipped; content claims are the owner's to
  verify. The store listing may only describe features that exist in the build.
- The privacy policy and Data Safety answers stay truthful to the shipped build.
  Licences of vendored code are recorded in `docs/DECISIONS.md`.
- **Naming:** "2048" is not our Play title, icon branding or package name, and
  no listing text may imply affiliation with Gabriele Cirulli or another
  developer (Play Deceptive Behavior policy).
- Reuse stays lawful: only licence-compatible code is depended on or adapted
  (MIT/Apache/BSD/ISC into any app; GPL code only into a GPL app); studied
  targets are re-implemented clean-room — behaviour and patterns, never copied
  code, text or assets; never bypass billing, ads, DRM, licence checks or access
  controls. One library per job.
- Kill fast: the pre-written kill test in `docs/RESEARCH.md` decides
  ship-next vs iterate. Never rewrite the test after seeing results.

## 6. Definition of done (any change)

CI green (incl. the no-device quality gate) · tests · docs updated ·
`prompt.md` current · next-chat prompt printed in chat · `app-progress-apk`
uploaded · short report — then stop. A release additionally needs every item on
`templates/play-checklist.md` green and my explicit command.
