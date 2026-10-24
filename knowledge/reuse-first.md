# Reuse first — research open source before building anything

> The agent's first job is **not** to write code. It is to find out whether
> good code, a good pattern, or a good answer already exists — verify it —
> and only then build what is genuinely missing. Building from scratch is
> the last resort, not the default. This saves the most time exactly where
> AppFlow apps used to lose it: re-inventing UI, architecture, and plumbing
> that Google and the community already solved and tested.
>
> Checked 2026-10-22. Stars, activity, and versions below and in
> [open-source-references.md](open-source-references.md) go stale — re-check
> live before every app (see [MAINTENANCE.md](../MAINTENANCE.md)).

## The law in one line

**No new component, screen pattern, library, or subsystem is hand-built
until the reuse scan (§2) for it is recorded.** "I can write this myself"
is not a reason to skip the scan. Neither is "the library is faster to
write than to read" — that is usually false by the second bug.

## 1. The five reuse verdicts

Every candidate found gets exactly one verdict, recorded in
`docs/RESEARCH.md` (plan chat) or the phase record (later phases):

| Verdict | Meaning | When |
|---|---|---|
| **DEPEND** | Add as a versioned dependency (Gradle/pub), pinned, via the official artefact | Licence compatible (§3), actively maintained, does one job well, size/privacy cost acceptable |
| **ADAPT** | Copy a *pattern or small snippet* into the app, rewritten in the app's style, with attribution | Official samples (Apache-2.0) especially; the idea is proven but a dependency is too much |
| **STUDY** | Read it, learn the behaviour/structure, re-implement clean-room in original code | GPL/AGPL code, closed apps, or anything whose code may not be copied (§4) |
| **BUILD** | Write it in the app — the scan found nothing fit, or everything fit is worse than owning it | Record *why* the found options lost; "nothing found" must name what was searched |
| **REJECT** | Looked, and no — with a one-line reason that stays recorded | Stale, unlicensed, licence-incompatible, bloated, privacy-hostile, star-inflated (§2, step 5) |

Rejected candidates stay in the record so no later chat re-researches them.
That is the same anti-re-litigation rule the docs system already uses for
app options.

## 2. The reuse scan — 7 steps, time-boxed

Run once in the plan chat for the whole app (architecture, UI base, data,
ads/billing), then once per phase for that phase's capability only.
Time-box: ~30 minutes of searching per capability. If the scan is not done,
the phase is not ready to code.

1. **Name the capability in one sentence.** "Offline list→detail with
   search", not "the app". One sentence per search — vague searches return
   vague repos.
2. **Search in this order:** ① the curated catalogue
   ([open-source-references.md](open-source-references.md)) → ② official
   sources (`android/*`, `google*/*`, `flutter/*` on GitHub;
   developer.android.com samples) → ③ GitHub search by capability +
   `language:kotlin` (or `dart`), sorted by stars, then by recently updated
   → ④ Maven Central / pub.dev for the library form of the same idea.
3. **Shortlist 2–4 candidates max.** More is not research, it is hoarding.
4. **Verify each candidate** against the scorecard (§3). A candidate that
   fails licence or activity is REJECT on the spot — do not read its code
   "just to see".
5. **Distrust popularity theatre.** Stars can explode without quality
   (a repo can gain 100k+ stars in days; forks and issue counts can be
   farmed the same way — see the `morluto/rea` note in
   [open-source-references.md](open-source-references.md)). Weight in this
   order: licence → recent commits/releases → real tests/CI in the repo →
   who maintains it → issue quality → stars, last and least.
6. **Decide DEPEND / ADAPT / STUDY / BUILD / REJECT** per candidate and
   write the one-line reason. Pin exact versions for every DEPEND.
7. **Record it** in `docs/RESEARCH.md` § "Reuse map" (template already has
   the table) or the phase record's "Reused" line, and log every shipped
   licence in `docs/DECISIONS.md` § "Third-party licences".

## 3. Verification scorecard (apply to every candidate)

| Check | Pass looks like | Fail looks like |
|---|---|---|
| **Licence** | MIT / Apache-2.0 / BSD — compatible with the app's MIT *or* GPL choice; licence file present in repo | No licence file (= all rights reserved, **unusable**), or GPL/AGPL code headed for an MIT app |
| **Activity** | Commit or release in the last ~6 months; not archived | Archived, "maintenance mode", or silent for a year+ (official samples that are *complete* are the exception — note it) |
| **Provenance** | Official org, or a maintainer with a track record; releases on Maven Central/pub.dev | Random 0-star template, renamed repo, download links to ZIPs/executables on third-party sites |
| **Health** | Tests + CI in-repo, changelog, issues answered | No tests, issues ignored, one giant initial commit and nothing since |
| **Fit** | Does one job; small surface; works offline; no unexpected permissions/analytics | Needs a backend/account, bundles trackers, doubles APK size for one screen, forces its architecture on the whole app |
| **Cost** | APK/RAM cost known and acceptable for low-end devices | Unmeasured — measure before DEPEND (see [app-engineering.md](app-engineering.md) §7) |

**Licence traffic light:**

- 🟢 **MIT / Apache-2.0 / BSD-2/3** — DEPEND or ADAPT freely; keep notices
  (AboutLibraries in the catalogue generates the credits screen).
- 🟡 **GPL-3.0** — STUDY freely. Copy code **only** if the app itself is
  GPL-3.0 (a plan-gate licence decision). Never paste GPL code into an MIT
  app "just for one function".
- 🔴 **AGPL / no licence / proprietary / "source available"** — STUDY the
  *behaviour* only where lawful (§4), never copy code or assets. No
  licence file means no permission, however open the repo looks.

## 4. Reverse engineering — smooth, lawful, and useful

Two directions, both legitimate, both with hard edges.

### A. Study other apps → rebuild the pattern cleanly (clean-room)

For open-source apps (catalogue §6) this is simply reading code you are
allowed to read. For any app, the *behaviour* study loop is:

1. What user job does this feature/screen actually do? (one sentence)
2. Screen map: entry → states (loading/empty/error/success) → exit.
3. Data shape it must keep (fields, offline behaviour, sync if any).
4. Permissions it asks for — and the user-visible reason for each.
5. **Stop looking at their implementation.** Write the spec from steps
   1–4, then implement from the spec in the app's own style.
6. Log in `docs/RESEARCH.md`: what was studied (repo/app + date), what
   was taken (behaviour/pattern only), licence posture.

Never copy code, text, icons, or assets from a STUDY target. Never
decompile a closed app to clone it, bypass billing/ads/licence checks,
defeat DRM or access controls, or ship a lookalike — that breaks RULES
laws 5 and 10 and risks the one Play account. If a task needs one of
those, it is not an AppFlow task; stop.

### B. Inspect your OWN APK/AAB (do this every release)

This is where reverse-engineering tools earn their keep, on artefacts
you own:

- `bundletool` → real per-device download size from the AAB.
- APK Analyzer / `apkanalyzer` (Android SDK) → size breakdown, manifest
  and permission audit, DEX overview.
- JADX / Apktool (catalogue §7) on **your own** release APK → verify R8
  obfuscation actually ran, no debug code, no test ad-unit IDs, no
  secrets or staging URLs shipped.
- Keep each release's R8 `mapping.txt` privately (never in git) so your
  own crash reports stay de-obfuscatable.

### C. Agent-driven RE tools (e.g. REA) — WATCH, not default

Tools like `morluto/rea` (an MCP/CLI layer over Ghidra/JADX/Hopper,
checked 2026-10-22) can drive decompilers for an agent and return
evidence records. AppFlow posture: **do not adopt as a default.** It
needs a desktop (Node + JDK + providers), which the phone-first flow
does not have; its popularity signals are unverifiable (see
[open-source-references.md](open-source-references.md)); and for your
own apps you already hold the source. Re-evaluate only if a future app
genuinely needs binary analysis, and then: official source only
(`github.com/morluto/rea`, npm `rea-agents` — lookalike downloads have
carried malware), pinned version, dry-run setup, own artefacts only,
disposable environment. Full note in the catalogue.

## 5. Anti-patterns (each one costs a phase if it slips in)

- **Not-invented-here:** hand-rolling navigation, image loading, JSON,
  date/time, or a design system before the scan. These are solved.
- **Sample-shopping mid-phase:** mixing three samples' patterns because
  each looked nice. Pick ONE base pattern per app (catalogue §2/§3),
  adapt everything to it.
- **Star-shopping:** choosing by stars/forks without the §3 scorecard.
- **Dependency pile-up:** every DEPEND must remove more work than its
  size, update, and privacy cost adds. One DI library, one image
  library, one JSON library — never two of a kind.
- **Copying the whole sample app** (e.g. all of Now in Android) to get
  one feature. ADAPT the module/pattern; leave the rest.
- **Vendoring without a record:** pasted code with no source/licence
  line in DECISIONS.md is a defect, found in review or never.
- **Silent upgrades:** bumping a reused library "while I'm here" outside
  its own tested change (see maintenance-cadence.md).

## 6. What this changes day to day

- Plan chat: the reuse map is drafted *with* the options table, so the
  roadmap is priced on reuse, not on writing everything.
- Phase chats: the phase record opens with its "Reused" line (verdict +
  source per part). A part with no line has not been scanned.
- Reviews: "show me the reuse scan" is a valid review comment; "we'll
  just build it" without one is not an answer.
- Time saved goes to the things open source cannot do: the niche
  content, the local-language quality, the owner's verified facts.
