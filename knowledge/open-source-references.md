# Open-source references — the verified catalogue

> Curated starting points for the reuse scan in
> [reuse-first.md](reuse-first.md). **Checked live on GitHub, 2026-10-22**
> (stars/activity/licence read that day). This is a *starting* list, not
> a whitelist: every entry still passes the reuse-first §3 scorecard at
> the moment of use, and versions are always pinned from the official
> artefact (Maven Central / pub.dev / GitHub release), never copied from
> a blog post. Re-check quarterly ([MAINTENANCE.md](../MAINTENANCE.md)).
>
> Default stack is **Kotlin + Jetpack Compose** (verdict §8). Flutter is
> the recorded exception path (§9). "Study" targets are for the
> clean-room loop in reuse-first §4A — mind the licence column.

## 1. How to read this catalogue

- **Verdict** = the usual reuse-first verdict for an AppFlow-sized app.
- Stars are shown only as a rough maturity hint — they are the *weakest*
  signal (reuse-first §2 step 5). Licence + activity decide.
- If a link is dead, archived, or silent when you check: REJECT it,
  note the date, and move on. Do not resurrect it from a fork without
  re-running the full scorecard.

## 2. Architecture & bootstrap (Phase 0)

| Reference | Licence · activity (2026-10-22) | Verdict | Use for |
|---|---|---|---|
| [android/nowinandroid](https://github.com/android/nowinandroid) | Apache-2.0 · 21.9k★ · pushed daily | **ADAPT** (patterns/modules) | The reference offline-first Compose app: modularisation, Room as source of truth, Hilt, screenshot tests, its UI catalog app. Too big to copy whole — mine per feature |
| [android/architecture-samples](https://github.com/android/architecture-samples) | Apache-2.0 · 45.9k★ · active | **ADAPT** | Smaller architecture patterns when Now in Android is overkill — closer to a 4-week app |
| [android/platform-samples](https://github.com/android/platform-samples) | Apache-2.0 · 1.8k★ · active | **ADAPT** | One official sample per platform API (notifications, widgets, permissions…) when a phase needs exactly one |
| [android/testing-samples](https://github.com/android/testing-samples) | Apache-2.0 · 9.3k★ | **ADAPT** | Test patterns that pair with `templates/ci-build.yml` |
| Official guidance: [architecture recommendations](https://developer.android.com/topic/architecture/recommendations) | — | **STUDY** | Layered UI/data layers, repository, UDF, `StateFlow` + `collectAsStateWithLifecycle`, single-activity. The rules the samples implement |

**DI (pick ONE, record it):** [google/dagger](https://github.com/google/dagger)
(Hilt; Apache-2.0; what Now in Android uses) by default, or
[InsertKoinIO/koin](https://github.com/InsertKoinIO/koin) (Apache-2.0,
lighter) when Hilt's codegen is the heavier choice for a tiny app.

## 3. UI — right first time (Phases 1–3)

The time-saver is **one coherent base, copied as a pattern**, plus
screenshot tests so CI sees the UI before the owner does
([app-engineering.md](app-engineering.md) §3).

| Reference | Licence · activity | Verdict | Use for |
|---|---|---|---|
| [android/compose-samples](https://github.com/android/compose-samples) | Apache-2.0 · 23.5k★ · active | **ADAPT one** | **JetNews** = list→detail reading · **Jetchat** = messaging UI · **Jetsnack** = Material 3 custom layouts/cards · **Reply** = adaptive phone/tablet/foldable · **JetLagged** = custom graphics. Pick the one whose shape matches the app; adapt everything to it |
| [material-foundation/material-color-utilities](https://github.com/material-foundation/material-color-utilities) | Apache-2.0 · 2.3k★ · active | **DEPEND/ADAPT** | Material You colour: generate the whole theme from one seed colour instead of hand-picking (and re-correcting) palettes |
| [cashapp/paparazzi](https://github.com/cashapp/paparazzi) | Apache-2.0 · 2.6k★ · active | **DEPEND** | Screenshot tests without a device — renders screens in CI |
| [takahirom/roborazzi](https://github.com/takahirom/roborazzi) | Apache-2.0 · 1.1k★ · active | **DEPEND** (alternative) | Screenshot testing with Robolectric; pick Paparazzi *or* Roborazzi, not both |
| [coil-kt/coil](https://github.com/coil-kt/coil) | Apache-2.0 · 11.9k★ · active | **DEPEND** | Compose image loading — only if the app actually loads images |
| [airbnb/lottie-android](https://github.com/airbnb/lottie-android) | Apache-2.0 · 35.8k★ | **DEPEND, sparingly** | One polished animation instead of hand-tuned motion; costs APK size, so one per app, not five |

## 4. Data, network, quality (only when proven needed — offline-first)

| Reference | Licence | Verdict | Use for |
|---|---|---|---|
| Room + DataStore (via §2 patterns) | Apache-2.0 (AndroidX) | **DEPEND** | Local source of truth; no separate catalogue repo needed |
| [square/retrofit](https://github.com/square/retrofit) + [square/okhttp](https://github.com/square/okhttp) | Apache-2.0 | **DEPEND** | HTTP layer — only when a backend/API is proven necessary (Rule 13) |
| [Kotlin/kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) (or Moshi) | Apache-2.0 | **DEPEND (one)** | JSON for the network/cache layer |
| [detekt/detekt](https://github.com/detekt/detekt) + [ktlint/ktlint](https://github.com/ktlint/ktlint) | Apache-2.0 / MIT | **DEPEND (CI)** | Static checks + formatting in CI — catches the mistakes agents repeat, automatically |
| [square/leakcanary](https://github.com/square/leakcanary) | Apache-2.0 | **DEPEND (debug only)** | Memory-leak detection before it becomes a 1-star review |
| [cashapp/turbine](https://github.com/cashapp/turbine) · [google/truth](https://github.com/google/truth) · [mockk/mockk](https://github.com/mockk/mockk) | Apache-2.0 | **DEPEND (tests)** | Flow assertions, fluent assertions, Kotlin mocking |

## 5. Ads, billing, polish, release (Phases 4–6)

| Reference | Licence · note | Verdict | Use for |
|---|---|---|---|
| [googleads/googleads-mobile-android-examples](https://github.com/googleads/googleads-mobile-android-examples) | Apache-2.0 · active | **ADAPT** | Live AdMob format samples (rewarded/interstitial). Test IDs only until the owner GATE for live units ([admob-checklist.md](../templates/admob-checklist.md)) |
| [googlesamples/play-billing-samples](https://github.com/googlesamples/play-billing-samples) | Apache-2.0 · **historically lags new Billing Library versions** | **STUDY/ADAPT with care** | Billing *patterns* (purchase/restore flow shape). Always verify against the current Billing documentation before copying any call |
| [mikepenz/AboutLibraries](https://github.com/mikepenz/AboutLibraries) | Apache-2.0 · active | **DEPEND** | Auto-generates the open-source licences/credits screen from actual dependencies |
| [google/bundletool](https://github.com/google/bundletool) | Apache-2.0 | **DEPEND (tool)** | AAB → per-device APKs; real download-size checks (reuse-first §4B) |

## 6. Study targets — finished apps to learn from (clean-room, §4A)

Small, real, finished apps — shaped like AppFlow's utilities. **Read,
learn the behaviour, re-implement. Licence decides what may be copied.**

| App | Licence | Study for |
|---|---|---|
| [FossifyOrg](https://github.com/FossifyOrg) suite — Gallery, Calendar, Notes, File-Manager, Messages… | **GPL-3.0** | Simple ad-free Material You utilities: exactly AppFlow's app shape, settings, theming, widgets. STUDY unless the app itself is GPL-3.0 |
| [AntennaPod/AntennaPod](https://github.com/AntennaPod/AntennaPod) | **GPL-3.0** | Mature offline download/playback/queue data handling |
| [TeamNewPipe/NewPipe](https://github.com/TeamNewPipe/NewPipe) | **GPL-3.0** | Extractor/pattern architecture at scale — study the structure, it is far bigger than a 4-week app |
| [android/nowinandroid](https://github.com/android/nowinandroid) (§2) | Apache-2.0 | The one study target whose code may also be ADAPTED |

## 7. Inspect your own build (reuse-first §4B)

| Tool | Licence | Use on YOUR artefacts |
|---|---|---|
| APK Analyzer / `apkanalyzer` (Android SDK, built-in) | SDK terms | Size breakdown, manifest/permissions audit |
| [skylot/jadx](https://github.com/skylot/jadx) | Apache-2.0 · 51.2k★ | Decompile **your own** APK: verify R8, no debug/test IDs/secrets shipped |
| [iBotPeaches/Apktool](https://github.com/iBotPeaches/Apktool) | Apache-2.0 · 26.5k★ | Resource/manifest inspection of **your own** APK |
| [google/bundletool](https://github.com/google/bundletool) (§5) | Apache-2.0 | Per-device size truth from the AAB |

## 8. Stack verdict — Kotlin + Compose (default), Flutter (exception)

| Factor | Kotlin + Compose | Flutter |
|---|---|---|
| Fit to AppFlow | Android/Play only; existing Gradle `ci-build.yml`; day-one Material You / target-API parity | Cross-platform — value only if iOS is genuinely required |
| Size/RAM | Smaller APK (no bundled engine), lighter on low-RAM devices | Engine bundled in every APK; near-native speed, higher baseline |
| Platform APIs | Direct (AdMob, Billing, WorkManager native-first) | Via plugins/platform channels |
| Rule | **Default for every app** | DECIDE-ONCE exception, recorded in `docs/DECISIONS.md` with the iOS reason |

## 9. Flutter exception path (only when §8 says so)

[flutter/flutter](https://github.com/flutter/flutter) (BSD-3) ·
[VeryGoodOpenSource/very_good_cli](https://github.com/VeryGoodOpenSource/very_good_cli)
(MIT; the trustworthy starter generator) ·
[VGVentures/news_toolkit](https://github.com/VGVentures/news_toolkit) (BSD-3; full-app study piece) ·
[felangel/bloc](https://github.com/felangel/bloc) (MIT) *or*
[rrousselGit/riverpod](https://github.com/rrousselGit/riverpod) (MIT) for state ·
[googleads/googleads-mobile-flutter](https://github.com/googleads/googleads-mobile-flutter) (Apache-2.0).
⚠️ `flutter/samples` shows no clear repo-level licence on GitHub — check
per-sample before any ADAPT.

## 10. Watch list & avoid list

**WATCH (re-check quarterly, do not depend on yet):**

- [`morluto/rea`](https://github.com/morluto/rea) (MIT; MCP/CLI over
  Ghidra/JADX/Hopper for agent-driven reverse engineering). Checked
  2026-10-22: real and actively released (npm `rea-agents` 6.7.0), but
  6 months old, desktop-only (breaks phone-first), and its popularity
  signals are unverifiable — it went from <10k stars (6 Oct) to ~363k
  (22 Oct), growth no quality judgement can rest on; lookalike
  downloads have carried malware, so official sources only. Posture and
  trial conditions: [reuse-first.md §4C](reuse-first.md). 

**AVOID (REJECT on sight, record why):**

- Archived or "maintenance mode" repos (e.g. old View-based Material
  Components samples for a new Compose app; `android/sunflower`;
  `facebook/screenshot-tests-for-android`) — complete samples may still
  be STUDY material if noted, never a base.
- Unlicensed "starter templates" from search (no licence file, near-zero
  activity) — legally unusable *and* unmaintained; official §2/§3
  sources beat them on both.
- The original SimpleMobileTools suite (sold to an ad company) — the
  maintained successor is Fossify (§6).
- Any repo whose install path is a third-party ZIP/executable or a
  `curl | bash` from anywhere but the project's own official repo.
- GPL/AGPL code pasted into an MIT-licensed app (reuse-first §3).

## 11. Keeping this file honest

- Quarterly: re-open every link; record archived/silent/licence changes
  with the check date; replace, don't patch around, dead entries.
- Per app: the plan chat re-verifies only the entries that app will
  actually DEPEND/ADAPT on (scorecard, reuse-first §3) and pins versions
  in the app's DECISIONS.md — this catalogue never pins versions for you.
- New candidates enter this file only after passing the scorecard in a
  real app or a recorded trial — not on announcement-day enthusiasm.
