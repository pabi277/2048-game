# Web UI references — free open source for the UI reference site

> Verified starting points for building the disposable, phone-shaped
> **UI reference website** described in
> [ui-reference-site.md](ui-reference-site.md). **Checked live on GitHub,
> 2026-10-24** (stars/activity/licence read that day; re-check quarterly
> per [MAINTENANCE.md](../MAINTENANCE.md), and re-run the
> [reuse-first.md](reuse-first.md) §3 scorecard at the moment of use).
>
> "Reference can be many" is the rule: this catalogue stays wide on
> purpose. But **one app picks ONE primary stack** (§7), recorded as a
> DECIDE-ONCE design decision — mixing three kits in one reference site
> produces three visual languages, which is the disease this step exists
> to cure. Default if the plan chat has no reason to choose otherwise:
> **Option A (§7)**.

## 1. The trap this catalogue avoids

"Free web UI" is five different markets. Only the first three serve an
app reference; the last two actively harm it:

- ✅ Mobile-app UI kits (web tech, native look) · general component
  systems · copy-paste CSS block libraries.
- ❌ Landing-page/admin templates (hero sections, pricing tables,
  sidebars — the wrong skeleton for an app) · animation/showcase kits
  (landing-page glitter, motion everywhere, no app states).

## 2. Tier 1 — Mobile-app UI kits (best fit)

| Reference | Licence · activity (2026-10-24) | Use for |
|---|---|---|
| [konstaui/konsta](https://github.com/konstaui/konsta) | MIT · 4.3k★ · active | **Default pick.** Pixel-perfect mobile components with Tailwind CSS, Material Design + iOS themes, React/Vue/Svelte. Thinks in app screens (navbars, lists, cards, sheets); a Material-themed screen maps to Compose almost 1:1 |
| [ionic-team/ionic-framework](https://github.com/ionic-team/ionic-framework) | MIT · 52.7k★ · active | The mature giant: tabs, modals, lists, action sheets with native styling; Web Components core works with any framework. Heavier, battle-proven |
| [framework7io/framework7](https://github.com/framework7io/framework7) | MIT · 18.8k★ · active | Full mobile HTML framework built also as a prototyping tool; plain HTML/CSS/JS or React/Vue/Svelte; rich dialogs/popovers/lists |
| [material-components/material-web](https://github.com/material-components/material-web) | Apache-2.0 · 11.4k★ · active | Google's Material Web Components — semantically closest to Material 3 in Compose, so the web→Compose mapping table (§ ui-reference-site §5) nearly writes itself |

## 3. Tier 2 — General component systems (beautiful, needs discipline)

| Reference | Licence | Note |
|---|---|---|
| [shadcn-ui/ui](https://github.com/shadcn-ui/ui) | MIT · 126k★ | Most beautiful/customisable free option (copy-paste, you own the code, Tailwind). Risk: natural look is "modern web dashboard" — needs a strict phone frame + native adaptation pass |
| [mui/material-ui](https://github.com/mui/material-ui) | MIT (core) · 99k★ | Material Design in React; easiest *behaviour* translation to Material 3. Caution: MUI X advanced components are paid — core only. Heavier, React-only |
| [mantinedev/mantine](https://github.com/mantinedev/mantine) | MIT · 31.9k★ | Fully-featured, polished forms/hooks. Same web-feel caution |
| [heroui-inc/heroui](https://github.com/heroui-inc/heroui) (ex-NextUI) | Apache-2.0 · 31k★ | Beautiful modern React UI; valid alternative primary stack |
| [chakra-ui/chakra-ui](https://github.com/chakra-ui/chakra-ui) · [ant-design/ant-design](https://github.com/ant-design/ant-design) | MIT · 40.7k★ / 99.9k★ | Solid, but SaaS-desktop / enterprise data-dense flavour pulls a reference toward "admin panel" — usually the wrong direction for consumer apps |

## 4. Tier 3 — Fastest prototype fuel (copy-paste blocks)

| Reference | Licence · activity | Note |
|---|---|---|
| [saadeghi/daisyui](https://github.com/saadeghi/daisyui) | MIT · 42.7k★ · active | Tailwind plugin, semantic classes (`btn`, `card`), dozens of themes, zero JS. Fastest zero-to-good-looking path; any framework or plain HTML |
| [markmead/hyperui](https://github.com/markmead/hyperui) | MIT · 12.3k★ · active | Free Tailwind blocks, copy-paste, framework-agnostic — fill screens fast |
| [themesberg/flowbite](https://github.com/themesberg/flowbite) | MIT (core) · 9.4k★ | Stay on the free core; a paid Pro tier exists |
| [merakiuilabs/merakiui](https://github.com/merakiuilabs/merakiui) | MIT · 2.7k★ | Responsive Tailwind components incl. RTL support — relevant for RTL-language apps |
| ⚠️ [htmlstreamofficial/preline](https://github.com/htmlstreamofficial/preline) | licence unverified · 6.5k★ | Popular, but no clean licence verified from repo metadata — check the LICENSE file before ANY use, or REJECT |
| ⚠️ [mertJF/tailblocks](https://github.com/mertJF/tailblocks) | MIT · 8.9k★ · stale (2023) | Study/copy a block if useful; never depend on it |

## 5. Supporting cast

- **Base engine:** [tailwindlabs/tailwindcss](https://github.com/tailwindlabs/tailwindcss) (MIT) + [vitejs/vite](https://github.com/vitejs/vite) (MIT) — a reference site must build/preview in seconds.
- **Icons (pick one, keep parity with the app):** [lucide-icons/lucide](https://github.com/lucide-icons/lucide) (verify per-file licence), [tailwindlabs/heroicons](https://github.com/tailwindlabs/heroicons) (MIT), [iconify/iconify](https://github.com/iconify/iconify) (MIT; includes Material icon sets for web↔Compose parity).
- **Fonts:** [fontsource/fontsource](https://github.com/fontsource/fontsource) (MIT) — self-host the same family the app will ship.
- **Motion/data (sparingly):** [motiondivision/motion](https://github.com/motiondivision/motion) (MIT), [airbnb/lottie-web](https://github.com/airbnb/lottie-web) (MIT — the same Lottie file can ship in the app), [recharts/recharts](https://github.com/recharts/recharts) / [chartjs/Chart.js](https://github.com/chartjs/Chart.js) (MIT) only if the app has charts.
- **Phone presentation:** [marvelapp/devices.css](https://github.com/marvelapp/devices.css) (MIT, old, pure CSS) — device frames for showing screens phone-shaped; study-grade.
- **Component gallery (optional, large apps only):** [storybookjs/storybook](https://github.com/storybookjs/storybook) (MIT) — reference site as a living token/component catalogue. Heavy for a disposable prototype; skip by default.

## 6. Avoid for this purpose ❌

- Landing/admin templates (Cruip, CoreUI, Tailwind dashboards) — wrong
  skeleton; the app inherits marketing-page patterns.
- Animation-showcase kits (MagicUI, React Bits style) — motion
  everywhere, no app states; React Bits' licence did not verify cleanly.
- [twbs/bootstrap](https://github.com/twbs/bootstrap) default look (MIT)
  — fine framework, instantly generic/dated result; defeats the purpose.
- Paid traps in free clothing: Tailwind Plus (ex-Tailwind UI, paid),
  MUI X Pro, Flowbite Pro, "free" Figma kits with paid upsell. The
  tiers above are free-core clean.
- No verifiable licence file (Preline as flagged, MDBootstrap,
  Gluestack UI) — reuse-first §3: no licence = no permission = REJECT.

## 7. Stack options (plan chat picks ONE, records it DECIDE-ONCE)

| Option | Stack | Speed | Web→app transfer |
|---|---|---|---|
| **A · DEFAULT** | Vite + Tailwind + **Konsta UI (Material theme)** + Lucide | Fast | Highest — screens already app-shaped and Material; component mapping nearly mechanical |
| **B · Fastest** | Plain HTML + Tailwind + **daisyUI** + HyperUI blocks | Fastest (no JS framework; agent writes it in one pass) | Good — clean instantly, generic-web components need slightly more adaptation |
| **C · Closest semantics** | React + **MUI core** (or Material Web) | Medium (React build, heavier) | High on behaviour (Material states), plainer default beauty |

An app may *study* screens/blocks from other tiers (reuse-first STUDY),
but the reference site itself is built with the one chosen stack.
Switching stacks mid-design restarts the approval (§ ui-reference-site
§3) — it is a design-direction change, not a tweak.
