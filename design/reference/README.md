<!-- SPDX-License-Identifier: MIT -->

# Tilefold — UI reference (disposable)

A phone-shaped, UI-only prototype of every Tilefold screen and state. It is a
**design specification**, not product code: nothing in `app/` imports it, it is
never part of the APK, and no Gradle task reads this directory. The app takes
only the tokens, measurements and component inventory recorded in
[`docs/UI-REFERENCE.md`](../../docs/UI-REFERENCE.md).

## Run it

```bash
cd design/reference
npm ci          # installs Vite, Tailwind, Konsta, Lucide, fonts
npm run dev     # http://localhost:5180
```

- Every state has its own URL: `#/home-no-save`, `#/game-mid`, `#/settings`,
  `#/game-large-font`, … Use ← / → to walk them in order.
- `/?gallery=1` shows all 30 states on one sheet (the review overview).
- `#/tokens` renders the palette, tile ramp, type scale and computed contrast.
- English / हिन्दी switch in the header re-renders the same screens, so Hindi
  text length is reviewed rather than assumed.

## Verify the tokens (no dependencies, runs in CI)

```bash
npm run verify
```

`scripts/check-tokens.mjs` fails when `src/design.js` and the frozen tables in
`docs/UI-REFERENCE.md` disagree, when any tile numeral drops below 3.0:1 or any
body pair below 4.5:1, when a required screen/state row has no panel, or when a
raw colour literal appears in a screen file. It is the machine-checked half of
the phase 0-D freeze — the owner's approval is the other half.

## Re-render the archived screenshots (optional)

```bash
npm run shots   # writes ./screenshots/*.png (needs the dev server running)
```

Uses `playwright-core` + `@sparticuz/chromium` (dev-only). The committed PNGs in
`screenshots/` are the images later UI phases compare their Compose screenshots
against.

## Stack (one, recorded 2026-10-24)

Vite · Tailwind CSS 4 · Konsta UI (Material theme) · Lucide icons · Roboto +
Noto Sans Devanagari (self-hosted via fontsource).

## Licences

| Package | Version | Licence |
|---|---|---|
| vite | 8.3.5 | MIT |
| @vitejs/plugin-react | 6.1.2 | MIT |
| tailwindcss / @tailwindcss/vite | 4.3.3 | MIT |
| konsta | 5.5.0 | MIT |
| react / react-dom | 19.3.1 | MIT |
| lucide-react | 1.61.0 | **ISC** |
| @fontsource/roboto | 5.3.0 | OFL-1.1 (the font files) |
| @fontsource/noto-sans-devanagari | 5.3.0 | OFL-1.1 |
| playwright-core | 1.64.1 | Apache-2.0 (dev only) |
| @sparticuz/chromium | 153.0.0 | MIT (dev only) |

None of these ship in the app, so they add nothing to the APK. The one that will
cross over is **Lucide (ISC)** — its icon paths become Compose `ImageVector`s in
phase 2, with attribution on the licences screen.

## Guardrail

Reference code is never copied into `app/` — different material, different code.
Only tokens, measurements, the component inventory and the icon/font choices
cross over. Visual changes go reference-first, then screen.
