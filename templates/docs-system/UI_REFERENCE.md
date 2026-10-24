<!-- SPDX-License-Identifier: MIT -->

# UI reference — [APP]

> The design specification for this app's UI: a disposable, phone-shaped
> UI-only website, owner-approved before any app UI code
> (`knowledge/ui-reference-site.md` in the frozen snapshot; stacks in
> `knowledge/web-ui-references.md`). The reference's code is never copied
> into the app — only tokens, measurements, and the component inventory
> cross over.

## Reference

- Stack (DECIDE-ONCE, one only): [A Konsta+Tailwind+Vite (default) /
  B daisyUI plain HTML / C MUI core — with date + DECISIONS.md entry]
- Location: `design/reference/` (disposable; never shipped, never in the APK)
- Preview: [how the owner viewed it — live preview / artifact, date]

## Approval

- YYYY-MM-DD — owner approved [all screens / with listed changes] in chat.
- Changes after approval (direction changes re-open the gate):
  - YYYY-MM-DD — [change] — [approved by]

## Screens (one row per screen AND state)

| Screen / state | Reference page | Target composable(s) | Status |
|---|---|---|---|
| [Home — content] | | | 📋 |
| [Home — loading / empty / error / dark / large-font] | | | 📋 |

## Tokens (frozen on approval — app theme is generated from these)

- Colours (hex): seed/primary [ ] · surface [ ] · background [ ] ·
  error [ ] · text primary/secondary [ ] · dark scheme checked [ ]
- Type scale → Material 3 roles: [role: size/weight → typography role]
- Spacing: 4/8dp scale · screen padding [ ] · gaps [ ] · row height [ ]
- Shape/elevation: radii [ ] · elevation levels [ ]
- Icons: [one set] · Font: [family, available web + Android]

## Component inventory (= the app's Compose checklist)

- [ ] [button variants / cards / list rows / app bar / nav / chips /
      dialogs / sheets — one line each, token roles noted]

## Deliberate native adaptations (planned differences, not drift)

- [web pattern] → [Android pattern] — [why, date]

## Screenshot comparison (per UI phase)

- Phase [N] — [screen]: reference vs app screenshot reviewed in phase
  record · differences fixed: [ ] · deliberate adaptations above: [ ]
