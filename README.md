<!-- SPDX-License-Identifier: MIT -->

# Tilefold (working name)

Playbook pin: AppFlow @ `57597b3332ad578614a0d9d5609f9cf8a278ee8a` (pinned 2026-10-24).
The frozen `templates/` + `knowledge/` snapshot and the required provenance line
are added in phase 0.1, before any app code.

An offline 4×4 sliding-merge number puzzle for Android: slide tiles, merge equal
numbers, race to 2048 without losing your board or your train of thought.

> **Status: plan approved 2026-10-24, not built.** This repository currently
> holds the plan documents and the app's operating manual only. Gradle skeleton,
> CI, the playbook snapshot and all app code arrive in phase 0. Nothing below
> claims a shipped feature.

## What it does (v1 scope — planned)

- 4×4 board; swipe or use the direction pad / keyboard arrows to slide every
  tile; equal tiles merge and double (2+2=4 … 1024+1024=2048); one new tile per
  valid move (2 with 90% chance, 4 with 10%).
- Score = sum of merges; best score persists across sessions; win at the 2048
  tile with a **keep playing** option; game over only when no empty cell and no
  adjacent equal tiles remain.
- **Resume anywhere**: the board is saved on every valid move and restored on
  launch.
- **One free undo per game** (an additional undo can be earned by watching a
  rewarded ad; up to three per game), including on the game-over screen.
- **Daily Challenge**: one date-seeded board per day, the same for everyone, with
  a local streak and a separate best score.
- English and हिन्दी UI; system/light/dark plus one unlockable theme; sound and
  haptics you can switch off; stats (best score, games played, highest tile,
  wins, daily streak).
- Fully offline: scores, boards and settings stay on the device.

## Screenshots

Not available yet — screenshots will be added from the reference site in Phase 0
and from the build in later phases. They will show only features present in the
build they came from.

## Privacy and ads

- Privacy policy: to be published in the release phase (URL added here then).
- Data Safety: reflects the exact shipped build and every included SDK; with ads
  enabled this includes the Google Mobile Ads SDK (Phase 5).
- Ads: planned as rewarded (optional undo / one theme) plus an interstitial that
  can appear **only after the game-over dialog is dismissed**, capped at one per
  three game-overs and never during play. **No banner.** If the ad phase is not
  shipped, this section will say "No ads".
- Ad-free purchase: planned one-time non-consumable product that removes all ads
  and is restorable from the store; shipped in Phase 5 and described here when it exists.
- Accounts and backend: none planned; no login, no cloud save, no analytics.

## Build and test

Requirements: JDK 17, Android SDK, and the checked-in Gradle wrapper.

```sh
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

Release artifacts are built by GitHub Actions. Signing credentials live in
protected Actions secrets, never in this repository. See
`.github/workflows/ci-build.yml` for the actual workflow.

## Permissions and data

Planned: internet access (only if the ads phase ships, required by the ad SDK;
no other network use) and vibration (haptics). No storage, location, camera,
contacts or notification permissions. All game data is local; uninstalling the
app removes it. `<confirm against the shipped manifest before release>`.

## License and credits

- **App license:** MIT.
- **Third-party code and assets:** generated credits screen (AboutLibraries) plus
  the Lucide icon notice (ISC) and the Roboto font notice (Apache-2.0). Full list
  in `docs/DECISIONS.md` § Third-party licences.

## Store listing

- Google Play: not published yet (link added when the listing is live).
- Support contact: added before release.
- Target API: 36 at plan time (re-check the live requirement on release day)

No bracketed placeholder ships in a public release. Never add signing keys,
service-account JSON, account IDs, private tester data or real AdMob IDs to this
repository.

## Plan and provenance

- Plan documents: `docs/RESEARCH.md` (evidence, reuse map, pre-written kill
  test), `docs/UI-REFERENCE.md` (screens, states, proposed tokens),
  `docs/ROADMAP.md` (phases), `docs/DECISIONS.md` (dated decision register).
- Operating manual: `rule.md` · continuation prompt: `prompt.md`.
- Playbook provenance: AppFlow @ `57597b3…`, pinned as a frozen snapshot; this
  repository does not depend on it at build or run time.
