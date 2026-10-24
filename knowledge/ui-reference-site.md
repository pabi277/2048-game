# UI reference site — design it on the web, build it in the app

> Web UI is where agents are strongest and correction is cheapest; app
> UI is where correction costs a CI build, a download, and a sideload.
> So AppFlow designs the UI **first as a disposable UI-only website**,
> gets it owner-approved there, freezes the design tokens, and only
> then builds Compose screens against it. The website is a *design
> specification*, never shipped code: no backend, no real data, no
> attempt to make it a real product.
>
> Stacks: [web-ui-references.md](web-ui-references.md) (default: Option
> A). Engineering it maps to: [app-engineering.md](app-engineering.md)
> §3. Reuse rules: [reuse-first.md](reuse-first.md).

## 1. When it happens

- **Plan chat:** no site is built (stage 1 creates nothing). The plan
  names the reference stack (DECIDE-ONCE design direction), lists every
  screen + its states, and drafts `docs/UI-REFERENCE.md` from
  `templates/docs-system/UI_REFERENCE.md`. The roadmap's Phase 0 carries
  the site as a part.
- **Phase 0 (bootstrap):** the site is built, owner-approved, and the
  tokens frozen — **before any app UI code beyond the hello screen**.
  No Phase 1+ UI work starts against an unapproved reference.

## 2. Build rules for the reference site

1. **Phone-shaped from minute one.** Fixed phone viewport
   (~390×844 CSS px, one column, app navigation). A desktop-width or
   landing-page layout is a failed reference, however beautiful —
   delete and redo, don't "adapt later".
2. **One page per app screen, every state a page/state of its own:**
   content · loading (skeleton) · empty · error/offline · dark mode ·
   large-font. The happy screen alone is half a design.
3. **Real-shaped fake data:** long local-language names/text, big
   numbers, missing images, 0/1/many list items. Lorem ipsum hides the
   exact problems (overflow, truncation) that ruin real screens.
4. **App patterns, not web patterns:** bottom navigation / top app bar,
   FAB only if the app will have one, sheets/dialogs as the app will
   show them. No hover states, no mouse thinking, no hero sections,
   no pricing/marketing blocks.
5. **One stack, one theme** ([web-ui-references.md](web-ui-references.md)
   §7). The agent builds it fast (static Vite/plain HTML — it must
   preview in seconds, e.g. as a live preview or static artifact the
   owner opens on their phone).
6. **Disposable by design:** it lives in the app repo under
   `design/reference/` (never inside `app/`, never in the APK, licence
   of its code is the app's own). Nothing in the shipped app may depend
   on it at build or runtime.

## 3. Approval gate (owner, in chat)

1. Agent presents every screen/state (screenshots or the live preview)
   in chat. Owner corrects **here** — colour, spacing, hierarchy,
   wording placeholders — as many rounds as needed; rounds are cheap
   now and expensive later.
2. Owner approves explicitly (or approves with listed changes). Record
   date + stack + what was approved in `docs/UI-REFERENCE.md` and the
   design decision in `docs/DECISIONS.md`.
3. Changing the design *direction* (stack, visual language) after this
   restarts this gate — it is a DECIDE-ONCE change (GATE class). Small
   per-screen fixes during app phases update the reference first, then
   the screen — never the reverse.

## 4. Tokens freeze (the bridge to Compose)

On approval, extract and record in `docs/UI-REFERENCE.md`:

- **Colours:** seed/primary, surface, background, error, text levels —
  as hex; the app's Material 3 theme is *generated from the seed*
  (app-engineering §3.1), dark scheme derived and checked, not invented.
- **Type scale:** each role (headline/body/label…) with size/weight —
  mapped to Material 3 typography roles, respecting Android font
  scaling rather than fixed px.
- **Spacing:** 4/8dp scale only; screen padding, gaps, list row height.
- **Shape & elevation:** corner radii per component kind; elevation
  levels used.
- **Components inventory:** every component used (button variants,
  cards, list rows, app bar, nav, chips, dialogs, sheets…) — this list
  becomes the app's Compose component checklist.
- **Icons & font:** the one icon set and font family, chosen for
  availability on both sides (web-ui-references §5).

If it is not in the tokens, it does not go into the app "because it
looked nice once". New visual values enter via a tokens update first.

## 5. Building the app against it (Phases 1+)

1. **Mapping table first** (`docs/UI-REFERENCE.md`): one row per screen
   — reference page/state → target composable(s) → token roles used →
   status. An unmapped screen is not ready to code.
2. **Screen-by-screen copy, not inspiration.** Layout, spacing,
   hierarchy and component choices follow the reference; behaviour,
   state (UDF/ViewModel) and data follow app-engineering §§2–4.
3. **Screenshot comparison is the acceptance test:** the screen's
   Paparazzi/Roborazzi shots (fast-quality-no-device §1) are reviewed
   side-by-side with the reference screenshots in the phase record.
   Differences are either fixed or listed as **deliberate native
   adaptations** (see §6) in `docs/UI-REFERENCE.md`. "Looks close" in
   prose is not evidence.
4. The reference's code is **never copied into the app** — different
   material, different code. What crosses over is only: tokens,
   layout measurements, component inventory, and icon/font choices.

## 6. Deliberate native adaptations (expected, recorded, not "fixed")

Web and Android are different materials; these differences are planned,
not drift. Record each in `docs/UI-REFERENCE.md`:

- Native navigation: back gesture/behaviour, bottom-nav behaviour,
  system bars/insets, edge-to-edge handling.
- Material 3 component behaviour (ripple, states, dialogs, pickers)
  over pixel-identical CSS look.
- Touch targets ≥48dp, TalkBack order/descriptions, font-scale
  survival — even where the reference (mouse-sized) differs.
- Platform performance: heavy web blur/shadow/gradient effects
  simplified to what renders smoothly on low-end devices.

Expect ~90% visual transfer (layout, colour, hierarchy, spacing) and
judge the result against that — not against pixel identity.

## 7. Definition of done (reference step)

Approved screens+states presented in chat · owner approval recorded
with date · tokens frozen in `docs/UI-REFERENCE.md` · mapping table
complete for the phases being built · reference committed under
`design/reference/` (Phase 0 commit) · **then** app UI coding may start.
