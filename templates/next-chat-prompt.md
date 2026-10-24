<!-- SPDX-License-Identifier: MIT -->

# Next-chat prompt — printed IN CHAT at every phase end

> **Delivery rule (owner-facing):** when a phase completes, the agent
> prints the block below — fully filled, no placeholders left — as ONE
> copyable `text` block **in the chat**, right after the phase report.
> The owner copies it from the chat and pastes it as the first message
> of the next chat. The owner never has to open GitHub, a branch, or any
> file to get it.
>
> `prompt.md` in the repo is still updated in the same commit — it is
> the durable backup/archive if a chat is lost, **not** the delivery
> path. If the two ever disagree, the repo wins and the agent says so.
>
> Fill rules: read the facts (phase, CI run, APK, decisions) from the
> repository — never from memory (PLAN.md §2). Keep it self-contained:
> the next chat must be able to start from this block plus the repo
> alone. No secrets, account IDs, tester data, or real ad IDs — ever.

```text
Continue [APP] ([package id]) — phase [N+1] of [total]: "[phase title]".

Repo: [repo name/URL] · branch: [session branch] · never create/switch
branches, never push main, never force-push or reset --hard.
Provenance: AppFlow @ [sha] (reference only — this repo is self-contained;
obey THIS repo's rule.md).

Just completed (do not redo, do not re-ask):
- Phase [N] "[title]" ✅ — [one-line outcome].
- CI run [id] green · tip sha [sha] · app-progress-apk from that run
  shows: [what the owner sees after sideloading].
- Decisions already recorded in docs/DECISIONS.md — never re-ask them:
  [languages · monetization · time budget · license · git author ·
  any waivers, each with its date].

This chat — implement phase [N+1] ONLY (all its parts, nothing outside):
- [N+1].1 [part — observable outcome]
- [N+1].2 [part — observable outcome]
- [N+1].3 [part — observable outcome, if any]
Done means: [copy the "Done means" cell from docs/ROADMAP.md verbatim].
Its no-device quality gate must be green (frozen
knowledge/fast-quality-no-device.md §1): unit + screenshot + lint, CI green,
app-progress-apk uploaded.

How to work (full rules: rule.md):
1. Read docs/ROADMAP.md, docs/DECISIONS.md, the latest docs/phases/
   record, and docs/RESEARCH.md's reuse map — trust the repo, not memory.
2. Evidence before hypothesis: file:line reads against the current tree.
3. Reuse first: run this phase's reuse scan (frozen
   knowledge/reuse-first.md) before building any part; record
   DEPEND/ADAPT/STUDY/BUILD/REJECT per part; log shipped licences.
4. Ask less, gate always: GATE items (PR, merge, main push, release,
   publish, Play submission, money, legal, changing a recorded decision)
   only as one Summary batch; everything else researched or logged.
5. End this phase with ALL of: CI green + downloadable app-progress-apk ·
   docs/phases/[NN]-<slug>.md (incl. what the APK shows and the "Reused"
   lines) · roadmap row updated · prompt.md updated in the same commit ·
   short report (run id, tip sha, APK artifact, what is now visible) ·
   then print the NEXT phase's next-chat prompt in chat from
   templates/next-chat-prompt.md · stop at the merge gate. No PR, merge,
   release, or submission without my explicit command in chat.

Start: phase [N+1].
```

## Last phase (and bug-fix chats)

- After the **final** v1 phase, the block's "This chat" section instead
  says: `Release prep only, per docs/ROADMAP.md phase [N] and
  knowledge/fast-quality-no-device.md §3 — stop before any submission.`
- For a bug-fix chat, replace "This chat" with the bug: observed
  behaviour, where seen (APK/CI run), and "diagnose evidence-first;
  fix only this bug; same ending as step 5 above."
