<!-- SPDX-License-Identifier: MIT -->

# Phase <NN> — <slug>

- Status: ✅ / 🚧 / ❌ · Date: YYYY-MM-DD · Branch: <session-branch>
- CI: run <id> green (or: blocked at <step>) · Provenance: AppFlow @ <sha> (reference only)
- **APK:** artifact `app-progress-apk` from run <id> — *shows: [what the
  owner can see after sideloading it]*

## Parts

- <NN>.1 — [part] — [status]
- <NN>.2 — [part] — [status]
- <NN>.3 — [part] — [status]

## Reused (reuse-first scan for this phase)

- [part] — [DEPEND/ADAPT/STUDY/BUILD/REJECT] — [source + licence + pinned
  version, or why building it won]. A part with no line has not been scanned.

## Evidence (before the change)

[Exact `file:line` reads / device output / CI annotation that justified the
work. No fix on a guess.]

## What shipped

[Files and observable behavior. Claims that are read back from the repository
by the next change — versions, checksums, shas — are noted here as facts, not
guessed.]

## Tests (no-device gate — knowledge/fast-quality-no-device.md §1)

[What now proves it without a device: unit/Robolectric test names,
screenshot tests approved/updated, lint/detekt result, CI run id. A
physical device never gates a phase; the one release-time device pass
(§3 of that file) is recorded in the release phase record only.]

## UI reference comparison (UI phases only)

- [Reference screen/state vs app screenshot reviewed side-by-side;
  differences fixed, or listed in docs/UI-REFERENCE.md as deliberate
  native adaptations. Tokens used as frozen — no invented values.]

## Next-chat prompt

- [ ] Printed IN CHAT at phase end (from templates/next-chat-prompt.md,
      fully filled for phase <NN+1>) — owner copies the next chat's start
      from the chat; `prompt.md` updated in this commit as backup only.

## Follow-ups

- [New roadmap phases? New decision to log in DECISIONS.md? Nothing?]
