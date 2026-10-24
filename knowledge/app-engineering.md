# App engineering — the best-way build reference

> How an AppFlow app is built when it is built the easy, fast, correct
> way: reuse-first ([reuse-first.md](reuse-first.md)), one coherent UI
> base, offline-first data, CI that *sees* the app, and a release that
> is inspected before it is shipped. This file is the engineering half;
> [PLAN.md](../PLAN.md) is the process half, and
> [open-source-references.md](open-source-references.md) is the
> where-to-get-it half. Checked 2026-10-22; re-verify version-sensitive
> claims at use time.

## 1. The default stack (deviations are DECIDE-ONCE, recorded)

| Layer | Default | Why this one |
|---|---|---|
| Language / UI | Kotlin + Jetpack Compose, Material 3 | Verdict in open-source-references §8; matches `templates/ci-build.yml` |
| Architecture | Single-activity, UI → ViewModel → Repository → local source of truth (official guidance, catalogue §2) | The shape every official sample shares — code an agent (or a future chat) can navigate without a map |
| State | `StateFlow` + `collectAsStateWithLifecycle`; UDF; events go down as state, up as calls | One source of truth per screen; testable without the UI |
| DI | Hilt (default) or Koin — one, recorded | Catalogue §2 |
| Local data | Room (structured) + DataStore (key-value/settings) | Offline-first (Rule 13); static bundled assets for read-only content |
| Network | None by default; Retrofit/OkHttp + one JSON library *only* with recorded evidence of need | Every network layer is a privacy, Data Safety, and maintenance cost |
| Images | Coil, only if the app loads images | Catalogue §3 |
| Async | Coroutines + Flow; WorkManager only for proven deferrable work | No hand-rolled threading, ever |
| Build/CI | Gradle wrapper + `templates/ci-build.yml` (tests + `app-progress-apk` every phase; signed release on `main`) | CI is the PC (Rule 11) |

Pin every version at bootstrap from the official artefact and record
the set (with the Gradle/AGP/JDK combination that CI proved green) in
`docs/DECISIONS.md`. Never copy version numbers from an article —
including this playbook's older pages.

## 2. Architecture rules that prevent rework

1. **UI never touches a data source.** Composables and ViewModels talk
   to repositories only — even single-source ones. This is the line
   that makes later changes (assets → Room → API) a one-layer edit.
2. **One ViewModel per screen**, exposing one immutable UI-state type
   with explicit `loading / content / empty / error` — every screen
   ships all four states in its first version, not as a polish phase.
3. **Domain logic stays Android-free** where practical (pure Kotlin:
   calculators, parsers, validators, Policy objects), so it is
   unit-testable on the host and never needs an emulator in CI.
4. **Modules only when they pay.** A 4-week app starts as one `app`
   module with clean packages (`ui/`, `data/`, `domain/`). Split into
   Gradle modules only when build time or a Now-in-Android-style
   boundary is *measured* to matter — modularising a tiny app early is
   a tax, not a virtue.
5. **No premature backend, login, analytics, or permissions** (Rules
   13/5). Each needs recorded evidence in DECISIONS.md before it exists.

## 3. UI the first-try-right way

Most UI correction rounds come from inventing per-screen. Remove the
invention:

0. **Design on the web first.** The approved UI reference site and its
   frozen tokens (`docs/UI-REFERENCE.md`; workflow
   [ui-reference-site.md](ui-reference-site.md), stacks
   [web-ui-references.md](web-ui-references.md)) are the visual spec for
   every screen below. Screens are *copied* from it (layout, spacing,
   hierarchy), then screenshot-compared against it (§ ui-reference-site
   §5); deliberate native adaptations are recorded, not drifted into.
1. **One theme, generated.** Material 3 theme from a single seed colour
   — the seed frozen in the reference tokens (§0)
   (material-color-utilities, catalogue §3). Dynamic colour where the
   device offers it. Zero per-screen colour/font constants.
2. **One base sample, adapted.** The plan chat names the ONE
   compose-sample whose shape matches the app (JetNews / Jetchat /
   Jetsnack / Reply, catalogue §3) in the reuse map. Every screen is
   built to that pattern's spacing, typography, and component choices.
3. **Stateless composables by default.** State hoisted (`value` +
   `onValueChange`); screen state in the ViewModel; reusable components
   take plain state holders, not ViewModels.
4. **Every screen gets a `@Preview`** (light/dark, long text, empty)
   **and a screenshot test** (Paparazzi or Roborazzi, catalogue §3)
   running in CI. A UI change that shifts pixels fails CI with an image
   diff — the owner approves pixels from the artifact, not from prose.
   This is the single biggest saver of "fix the UI again" chats.
5. **Compose performance hygiene from day one:** stable `key`s in lazy
   lists, `remember` for expensive work, `derivedStateOf` for rapidly
   changing inputs, state reads deferred to the smallest composable,
   no backwards writes. Cheap now, painful to retrofit.
6. **Adaptive without heroics:** window-size classes + the Reply
   pattern for tablet/foldable; phones remain the primary target.
7. **Accessibility is a screen state, not a phase:** content
   descriptions, 48dp targets, font-scale survival, TalkBack order —
   checked in previews, listed in the phase record.

## 4. Data & offline

- **Bundled static content** (exam lists, catalogues, reference data)
  ships as versioned assets with a schema version; validate on load;
  never block first run on a download.
- **Room is the source of truth** for user data; UI observes Flow from
  the repository. Migrations are written and tested from the first
  schema change — destructive migration is a data-loss bug, not a
  shortcut.
- **Settings/flags** in DataStore (including the ad-free entitlement
  cache), never in ad-hoc files or SharedPreferences for new code.
- **Offline means offline:** airplane-mode is a CI/device test step for
  the core flow in every phase record, not a release-week discovery.

## 5. Ads & billing engineering (Rule 9, Phase 4)

- Core logic depends on a host-testable `Policy` interface, never on
  the ad SDK: *may show rewarded here? may show interstitial at this
  break? is the user ad-free?* Unit tests prove "Policy says no ⇒ no ad
  call" (monetization.md).
- Consent first: ads requested only in a consent state that permits it;
  test ad units until the owner's live-unit GATE; IDs via protected
  configuration, never in code/chat/git.
- Billing: one non-consumable ad-free product; purchase **and restore**
  tested on a real device before release; entitlement survives
  reinstall via Billing restore, cached locally. Patterns: catalogue
  §5, with the verify-live-docs warning that lives there.

## 6. Testing pyramid (what CI must prove, per phase)

| Layer | Covers | Where |
|---|---|---|
| Unit (host) | Domain logic, ViewModels (Turbine), repositories with fakes, Policy | `./gradlew testDebugUnitTest` in CI — every push |
| Screenshot | Every screen's key states (§3.4) | Paparazzi/Roborazzi in CI |
| Static | detekt + ktlint | CI gate, not a suggestion |
| Instrumented (sparingly) | One smoke path through the real app | CI emulator, release lane/manual dispatch only; too slow for every push |
| Manual device pass | ONLY what CI cannot prove: consent render, purchase+restore, runtime permissions, airplane-mode core flow | **Once, before production** — never a per-phase gate (`fast-quality-no-device.md` §3) |

A phase is not done on "it compiles": it is done when its "Done means"
is proven by a row of this table plus the downloadable
`app-progress-apk` (PLAN.md §5). The full no-device gate, the fast-build
rules, and the "good enough" definition live in
[fast-quality-no-device.md](fast-quality-no-device.md).

## 7. Performance on the devices that matter

Target the low end of the Indian market first; flagships forgive
everything, ₹8k phones forgive nothing.

- **APK/AAB size is a feature.** Check bundletool download size per
  release (§9); question every dependency's kilobytes
  (reuse-first §3, "Cost"); prefer AAB splits (already Play-default).
- **Cold start & jank:** Baseline Profiles for the startup path when
  the app has real screens; lazy layouts with stable keys (§3.5);
  LeakCanary in debug from Phase 1, not after a leak report.
- **Battery/network:** no polling, no wakelocks without a recorded
  reason; offline-first already removes most of this class.

## 8. Security & privacy by construction

- No secrets, real ad IDs, account IDs, or tester data in git or chat —
  enforced by review, helped by keeping all such values in protected
  config/Actions secrets from the first commit.
- Permission minimalism: request at the moment of use with the reason
  visible; every permission in the shipped build appears in the
  privacy policy and Data Safety answers (`templates/privacy-policy.md`,
  rejection-causes.md).
- SDK inventory: every DEPEND is a Data Safety disclosure. The reuse
  scan's privacy column (catalogue scorecard) is where a tracker-heavy
  library gets REJECTED — before it is ever in the build.
- Release build: R8/minify on, `mapping.txt` archived privately per
  release, own-APK inspection (reuse-first §4B) before any upload.

## 9. Release engineering (Phases 5–6, with the existing checklists)

1. CI green on the exact commit; signed AAB + APK artifacts from
   `main` only (`templates/ci-build.yml`).
2. Own-build inspection: size (bundletool), manifest/permissions,
   JADX pass on your own APK (no test IDs/debug/secrets).
3. Artifact truth: version code/name, target API (re-check the live
   requirement on release day), checksums — read back from the built
   artefact, never from memory (PLAN.md §2 culture).
4. `templates/play-checklist.md` + `templates/admob-checklist.md`
   green; store text from P6 rules (no invented features/claims).
5. Stop at the submission GATE. Green CI is evidence, not
   authorization (RULES.md).

## 10. The phase-by-phase shortcut map

| Phase | Build it the easy way |
|---|---|
| 0 Bootstrap | Official architecture pattern (§2), hello screen in the app's generated theme, CI + one unit + one screenshot test green before any feature |
| 1–3 Core | One adapted UI base (§3), repository-first data (§4), reuse scan per capability (reuse-first §2), screenshot diff in CI |
| 4 Money | Policy-isolated ads + billing patterns (§5), test IDs, restore tested on device |
| 5 Polish | Accessibility/font-scale pass, AboutLibraries credits, size pass (§7), empty/error state audit |
| 6 Release | §9 sequence; stop before submit |

If a faster way and this file disagree, the faster way wins only when
it is recorded as an agent decision with its evidence — that is how
this file gets better instead of bypassed.
