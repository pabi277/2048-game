# Fast and good — quality without device tests, builds without waiting

> Two promises, kept honestly: **(1)** an app can be proven *good enough*
> phase by phase with **no physical-device test at all** — CI does the
> proving; **(2)** it can be built *fast* — reuse first, one module, JVM
> tests, cached CI. The one thing this file never claims: that a real
> device is *never* needed. It is needed exactly once, briefly, before
> production, for the short list in §3 that no CI can prove. Everything
> else that used to wait for a phone in someone's hand now waits for a
> green CI run instead.
>
> Builds on: [reuse-first.md](reuse-first.md) (don't build what exists),
> [app-engineering.md](app-engineering.md) §§2–7 (how it is built),
> `templates/ci-build.yml` (the machine that proves it). Checked 2026-10-22.

## 1. The no-device quality gate (every phase, in CI)

A phase is **done** when this gate is green — not when someone "tried it
on a phone". The downloadable `app-progress-apk` remains for the owner's
eyes (PLAN.md §5); it is a *look*, not a test the phase waits for.

| Risk (what used to need a device) | Proved without a device by | In CI |
|---|---|---|
| Logic is wrong | Host unit tests: domain, ViewModels (Turbine), repositories with fakes, Policy objects | `testDebugUnitTest` — every push |
| A screen looks wrong / regressed | **Screenshot test per screen & key state** (Paparazzi or Roborazzi): pixel diff fails the build with an image the owner reviews from the CI artifact | JVM — every push, seconds, no emulator |
| Screen states missing (loading/empty/error) | The four states are part of the screen contract (app-engineering §2.2); each gets its own screenshot + preview | JVM — every push |
| Framework/Android behaviour (Room, DataStore, WorkManager, navigation) | **Robolectric** tests on the host JVM; Room migration tests from in-memory DB; Compose semantics tests under Robolectric/Roborazzi | JVM — every push |
| Static defects, API misuse, a11y slips | Android Lint + detekt + ktlint (incl. accessibility checks: content descriptions, touch targets) | Every push |
| Crashes from leaks | LeakCanary in debug builds, exercised by the test suite | Every push |
| Fat/slow/bloated build | APK/AAB size budget: bundletool download-size + apkanalyzer breakdown; a size jump beyond the recorded budget fails or must be justified in the phase record | Release lane |
| Secrets/test IDs/debug code shipped | Own-APK inspection of the *release* artefact (reuse-first §4B): R8 ran, no test ad-unit IDs, no staging URLs, manifest/permissions match the docs | Release lane |
| Dependency rots or sneaks in a tracker | Licence + dependency inventory diff (AboutLibraries data); any new DEPEND needs its reuse verdict first (reuse-first §3) | Every push |

Rules that make the gate trustworthy:

- **Screenshot tests are the UI contract.** Approve a visual change by
  approving the new reference images (a deliberate diff), never by
  deleting the test. A red screenshot test is a failing test.
- **No emulator in the per-phase lane.** Emulator/instrumented runs are
  slow and flaky; they live only in the release lane (§3) or a manual
  workflow dispatch. JVM (unit + Robolectric + screenshot) is the gate.
- **Tests ride with the part.** A part that ships code without its row
  of this table is not done (PLAN.md §5 "Done means" still rules).

## 2. Fast to build — the speed rules

Speed comes from *not doing work*, in this order:

1. **Reuse first** ([reuse-first.md](reuse-first.md)) — the fastest code
   is the library/sample already written, tested, and sized. This is
   worth more than every tip below combined.
2. **One `app` module** with clean packages until a *measured* build or
   boundary problem pays for modules (app-engineering §2.4). Every extra
   module is configuration time on every build, forever.
3. **KSP, never KAPT**, for Hilt/DI and Room codegen; pin the
   Gradle/AGP/Kotlin/JDK set CI proved green (app-engineering §1).
4. **Gradle speed properties** in the app (verify each against the
   pinned AGP before enabling — configuration cache especially):
   `org.gradle.parallel=true`, `org.gradle.caching=true`,
   `kotlin.incremental=true`, `android.nonTransitiveRClass=true`,
   a sane `org.gradle.jvmargs`. Never run `clean` locally or in CI
   "to be safe" — that deletes the cache that makes you fast.
5. **Two CI lanes:** the per-push *fast lane* (unit + screenshot + lint +
   debug APK — what `templates/ci-build.yml` already aims at) and the
   *release lane* on `main`/dispatch only (signed AAB/APK, R8, size and
   own-APK inspection, optional emulator smoke). Release work never
   taxes a phase push.
6. **Debug for phases, release for `main`** (already the template's
   shape). R8/minify/shrink only where they ship.
7. **Small dependency diet:** one library per job, question every
   kilobyte on low-end devices (app-engineering §7). Fewer dependencies
   = faster configuration, smaller APK, fewer Data Safety disclosures.
8. **JVM everything:** screenshot/Robolectric/unit on the host JVM;
   emulators and devices are the slowest possible test runners (§1).

## 3. The honest remainder — one short device pass, once, before production

No CI lane can truthfully prove these. They are **not** per-phase tests;
they are a single ~15-minute owner pass on the signed release candidate
(sideload the CI artefact), recorded in the phase-6 record:

- AdMob consent flow and one rewarded/interstitial placement rendering
  on a real device (test IDs), with Policy-off state verified.
- Play Billing: one real purchase **and restore** of the ad-free product
  (licence-tester account), entitlement surviving reinstall.
- Runtime permissions (camera/files/notifications as applicable) granted
  and denied, on the owner's actual device/OEM skin.
- Core offline flow in airplane mode, once, end to end.

If any of these fails, that is a bug-fix chat, not a redesign. If an app
has *no* ads, billing, or sensitive permissions, its list genuinely is
just the airplane-mode line — and for such an app, "good enough without
device tests" is fully true until that one pass.

## 4. What "good enough" means (definition, so nobody moves it)

An AppFlow v1 is good enough to submit when **all** are true:

- [ ] No-device gate (§1) green on the exact release commit.
- [ ] Every screen: four states present, screenshot-tested, a11y lint clean.
- [ ] Core flow works offline (repository/airplane-equivalent tests,
      plus the §3 pass for the release candidate).
- [ ] Release artefact inspected (size, manifest, own-APK pass) and
      within its recorded size budget.
- [ ] Licences logged (DECISIONS.md), Data Safety/privacy truthful to
      the shipped build, play/admob checklists green.
- [ ] §3 remainder pass recorded for the features the app actually has.

Anything beyond this is polish, and polish after launch beats polish
before it (kill-fast law, RULES rule 4).
