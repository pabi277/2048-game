// SPDX-License-Identifier: MIT
//
// Review harness. This file is *not* part of the design: it is the frame,
// state picker and notes panel that let the owner walk every screen/state in
// chat. Nothing here corresponds to a Composables in the app.
import { useEffect, useMemo, useState } from 'react';
import { panels, panelGroups } from './screens.jsx';
import { DesignProvider, PhoneFrame } from './ui.jsx';
import { color, goldSeed, radius, size, space, tile, type } from './design.js';
import { languageName } from './strings.js';

const TOKENS_ID = 'tokens';

function lum(hex) {
  const h = hex.replace('#', '');
  const channels = [0, 2, 4].map((i) => parseInt(h.slice(i, i + 2), 16) / 255);
  const lin = channels.map((v) => (v <= 0.03928 ? v / 12.92 : ((v + 0.055) / 1.055) ** 2.4));
  return 0.2126 * lin[0] + 0.7152 * lin[1] + 0.0722 * lin[2];
}
function contrast(a, b) {
  const [x, y] = [lum(a), lum(b)].sort((m, n) => n - m);
  return (x + 0.05) / (y + 0.05);
}

function PanelBody({ panel, langOverride }) {
  const lang = panel.lang ?? langOverride;
  const theme = panel.theme ?? 'light';
  const scale = panel.scale ?? 1;
  return (
    <DesignProvider theme={theme} scale={scale} lang={lang}>
      <div className="h-full">{panel.render()}</div>
    </DesignProvider>
  );
}

function Frame({ panel, langOverride }) {
  return (
    <PhoneFrame dark={(panel.theme ?? 'light') === 'dark'}>
      <PanelBody panel={panel} langOverride={langOverride} />
    </PhoneFrame>
  );
}

function Notes({ panel }) {
  return (
    <aside className="w-[300px] shrink-0 self-start rounded-2xl border border-white/10 bg-white/[0.03] p-4">
      <div className="text-[11px] uppercase tracking-[0.14em] text-white/40">
        State {panel.n} · {panel.group}
      </div>
      <div className="mt-1 text-[17px] font-semibold leading-snug text-white/90">{panel.title}</div>
      <div className="mt-3 text-[13px] leading-relaxed text-white/60">{panel.check}</div>
      <div className="mt-4 text-[11px] uppercase tracking-[0.14em] text-white/40">Target composables</div>
      <div className="mt-2 flex flex-wrap gap-1.5">
        {panel.uses.map((u) => (
          <span key={u} className="rounded-full border border-white/10 bg-white/[0.04] px-2 py-1 text-[11px] text-white/70">
            {u}
          </span>
        ))}
      </div>
      <div className="mt-4 space-y-1 text-[12px] text-white/45">
        <div>Theme · {panel.theme ?? 'light'}</div>
        <div>Font scale · {(panel.scale ?? 1).toFixed(1)}×</div>
        <div>Language · {languageName[panel.lang ?? 'en']}</div>
      </div>
    </aside>
  );
}

function Swatch({ name, hex, note }) {
  return (
    <div className="flex items-center gap-3">
      <div className="h-9 w-9 rounded-lg border border-white/10" style={{ background: hex }} />
      <div className="min-w-0">
        <div className="text-[13px] text-white/80">{name}</div>
        <div className="text-[11px] text-white/40">
          {hex}
          {note ? ` · ${note}` : ''}
        </div>
      </div>
    </div>
  );
}

function TokensPage() {
  const ramp = ['2', '4', '8', '16', '32', '64', '128', '256', '512', '1024', '2048', 'super'];
  const roleRows = [
    ['bg', 'Background'],
    ['surface', 'Surface / card'],
    ['surfaceVariant', 'Surface variant'],
    ['scoreBox', 'Score box'],
    ['boardFrame', 'Board frame'],
    ['emptyCell', 'Empty cell'],
    ['accent', 'Primary / accent'],
    ['textPrimary', 'Text primary'],
    ['textSecondary', 'Text secondary'],
    ['divider', 'Divider'],
    ['error', 'Error'],
    ['success', 'Success / new best'],
  ];
  return (
    <div className="mx-auto w-full max-w-[1100px] px-6 py-6">
      <h1 className="text-[22px] font-semibold text-white/90">Tokens — proposed, freeze on approval</h1>
      <p className="mt-1 max-w-[70ch] text-[13px] leading-relaxed text-white/55">
        The same table as <code className="text-white/70">docs/UI-REFERENCE.md</code>. Contrast is computed from the
        hex values (WCAG 2.2). Tile numerals are large bold text, so 3.0:1 is the floor; body-size text uses 4.5:1.
        Changes you ask for here are applied to the doc and to the Compose theme in the same commit.
      </p>

      <div className="mt-6 grid gap-6 lg:grid-cols-2">
        {['light', 'dark'].map((t) => (
          <div key={t} className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
            <div className="mb-3 text-[11px] uppercase tracking-[0.14em] text-white/40">{t} theme</div>
            <div className="grid gap-2 sm:grid-cols-2">
              {roleRows.map(([key, label]) => (
                <Swatch
                  key={key}
                  name={label}
                  hex={color[t][key]}
                  note={`${contrast(color[t][key], color[t].bg).toFixed(1)}:1 vs bg`}
                />
              ))}
            </div>
          </div>
        ))}
      </div>

      {['light', 'dark'].map((t) => (
        <div key={t} className="mt-6 rounded-2xl border border-white/10 bg-white/[0.03] p-4">
          <div className="mb-3 text-[11px] uppercase tracking-[0.14em] text-white/40">
            {t} tile ramp — numeral colour flips as tiles darken
          </div>
          <div className="grid grid-cols-3 gap-2 sm:grid-cols-6">
            {ramp.map((v) => {
              const [bg, fg] = tile[t][v];
              const ratio = contrast(bg, fg);
              return (
                <div key={v} className="rounded-xl p-2 text-center" style={{ background: bg, color: fg }}>
                  <div className="text-[20px] font-bold leading-tight" style={{ fontVariantNumeric: 'tabular-nums' }}>
                    {v === 'super' ? '4096' : v}
                  </div>
                  <div className="mt-1 text-[9px] opacity-80">{bg}</div>
                  <div className={`text-[9px] ${ratio >= 3 ? 'opacity-80' : 'font-bold opacity-100'}`}>
                    {ratio.toFixed(1)}:1
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      ))}

      <div className="mt-6 grid gap-6 lg:grid-cols-2">
        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
          <div className="mb-3 text-[11px] uppercase tracking-[0.14em] text-white/40">Type scale (1.0×)</div>
          <div className="space-y-2">
            {Object.entries(type).map(([role, v]) => (
              <div key={role} className="flex items-baseline justify-between gap-4 border-b border-white/5 pb-1">
                <span
                  className="text-white/85"
                  style={{
                    fontSize: v.size,
                    lineHeight: `${v.line}px`,
                    fontWeight: v.weight,
                    letterSpacing: v.tracking,
                  }}
                >
                  Tilefold 2048
                </span>
                <span className="shrink-0 text-[11px] text-white/40">
                  {role} · {v.size}/{v.line} · {v.weight}
                </span>
              </div>
            ))}
          </div>
        </div>
        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
          <div className="mb-3 text-[11px] uppercase tracking-[0.14em] text-white/40">Spacing, shape, touch</div>
          <ul className="space-y-2 text-[13px] text-white/65">
            <li>Spacing scale · {space.grid.join(' / ')} dp only</li>
            <li>Screen padding · {size.screenPadding} dp horizontal, {size.screenPaddingV} dp vertical</li>
            <li>
              Gaps · score {size.scoreGap} dp · board→controls {size.boardControlsGap} dp · control{' '}
              {size.controlGap} dp
            </li>
            <li>
              Radii · board {radius.board} · tile {radius.tile} · score box {radius.scoreBox} · button{' '}
              {radius.button} · dialog {radius.dialog} · sheet {radius.sheet}
            </li>
            <li>
              Touch targets · ≥{size.minTarget} dp everywhere · direction-pad key {size.dirPadKey} dp
            </li>
            <li>
              Board · padding {size.boardPadding} dp, tile gap {size.tileGap} dp, square (390 − 48 padding)
            </li>
            <li>Elevation · 0 on board and score boxes, 2 dp on the pad, Material defaults for dialogs</li>
            <li>
              Gold milestone seed · <span style={{ color: goldSeed }}>{goldSeed}</span> (scheme derived in phase 4)
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default function App() {
  const all = useMemo(() => [{ id: TOKENS_ID, n: 'T', group: 'Reference', title: 'Tokens & contrast', check: '', uses: [] }, ...panels], []);
  const [activeId, setActiveId] = useState(() => {
    const fromHash = window.location.hash.replace(/^#\/?/, '');
    return all.some((p) => p.id === fromHash) ? fromHash : panels[0].id;
  });
  const [lang, setLang] = useState('en');
  const [gallery, setGallery] = useState(() => new URLSearchParams(window.location.search).get('gallery') === '1');
  const active = all.find((p) => p.id === activeId) ?? panels[0];
  const index = all.findIndex((p) => p.id === active.id);

  useEffect(() => {
    const onHash = () => {
      const id = window.location.hash.replace(/^#\/?/, '');
      if (all.some((p) => p.id === id)) setActiveId(id);
    };
    window.addEventListener('hashchange', onHash);
    return () => window.removeEventListener('hashchange', onHash);
  }, [all]);

  useEffect(() => {
    if (activeId && activeId !== TOKENS_ID) window.history.replaceState(null, '', `#/${activeId}`);
  }, [activeId]);

  const select = (id) => {
    setActiveId(id);
    setGallery(false);
    window.scrollTo({ top: 0 });
  };

  const move = (delta) => {
    const next = (index + delta + all.length) % all.length;
    select(all[next].id);
  };

  useEffect(() => {
    const onKey = (e) => {
      if (gallery) return;
      if (e.key === 'ArrowRight') move(1);
      if (e.key === 'ArrowLeft') move(-1);
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  });

  return (
    <div className="min-h-full">
      <header className="sticky top-0 z-20 border-b border-white/10 bg-[#100e0c]/95 backdrop-blur">
        <div className="flex flex-wrap items-center gap-x-4 gap-y-2 px-5 py-3">
          <div>
            <div className="text-[13px] font-semibold uppercase tracking-[0.18em] text-white/80">Tilefold</div>
            <div className="text-[11px] text-white/40">UI reference · phase 0-D · 30 states + tokens</div>
          </div>

          <div className="ml-auto flex items-center gap-2">
            <div className="flex overflow-hidden rounded-lg border border-white/15">
              {['en', 'hi'].map((l) => (
                <button
                  key={l}
                  onClick={() => setLang(l)}
                  className={`px-3 py-1.5 text-[12px] ${lang === l ? 'bg-white/15 text-white' : 'text-white/50'}`}
                >
                  {languageName[l]}
                </button>
              ))}
            </div>
            <button
              onClick={() => setGallery((g) => !g)}
              className="rounded-lg border border-white/15 px-3 py-1.5 text-[12px] text-white/70"
            >
              {gallery ? 'Single view' : 'All states'}
            </button>
          </div>
        </div>

        <div className="flex items-center gap-2 px-5 pb-3 md:hidden">
          <select
            value={active.id}
            onChange={(e) => select(e.target.value)}
            className="w-full rounded-lg border border-white/15 bg-[#1a1714] px-3 py-2 text-[13px] text-white/85"
          >
            {all.map((p) => (
              <option key={p.id} value={p.id}>
                {p.n}. {p.title}
              </option>
            ))}
          </select>
        </div>
      </header>

      <div className="flex">
        <nav className="hidden w-[268px] shrink-0 border-r border-white/10 px-3 py-4 md:block">
          <div className="max-h-[calc(100vh-92px)] overflow-y-auto pr-1">
            {['Reference', ...panelGroups].map((group) => {
              const items = all.filter((p) => p.group === group);
              if (!items.length) return null;
              return (
                <div key={group} className="mb-4">
                  <div className="px-2 pb-1 text-[10px] uppercase tracking-[0.16em] text-white/35">{group}</div>
                  {items.map((p) => (
                    <button
                      key={p.id}
                      onClick={() => select(p.id)}
                      className={`mb-0.5 flex w-full items-baseline gap-2 rounded-lg px-2 py-1.5 text-left text-[12.5px] leading-snug ${
                        p.id === active.id && !gallery ? 'bg-white/12 text-white' : 'text-white/60 hover:bg-white/5'
                      }`}
                    >
                      <span className="w-5 shrink-0 text-white/35">{p.n}</span>
                      <span>{p.title}</span>
                    </button>
                  ))}
                </div>
              );
            })}
          </div>
        </nav>

        <main className="min-w-0 flex-1">
          {gallery ? (
            <div className="grid grid-cols-[repeat(auto-fill,minmax(210px,1fr))] gap-5 px-5 py-5">
              {panels.map((p) => (
                <div key={p.id} className="flex flex-col items-center">
                  <div
                    style={{ width: 410 * 0.5, height: 864 * 0.5 }}
                    className="overflow-hidden rounded-xl"
                    onClick={() => select(p.id)}
                  >
                    <div style={{ width: 410, height: 864, transform: 'scale(0.5)', transformOrigin: 'top left' }}>
                      <Frame panel={p} langOverride={lang} />
                    </div>
                  </div>
                  <button onClick={() => select(p.id)} className="mt-1.5 max-w-[200px] text-[11px] leading-tight text-white/55">
                    {p.n}. {p.title}
                  </button>
                </div>
              ))}
            </div>
          ) : active.id === TOKENS_ID ? (
            <TokensPage />
          ) : (
            <div className="flex flex-wrap items-start justify-center gap-8 px-5 py-6">
              <div className="flex flex-col items-center gap-3">
                <Frame panel={active} langOverride={lang} />
                <div className="flex items-center gap-3 text-[12px] text-white/45">
                  <button onClick={() => move(-1)} className="rounded-md border border-white/15 px-2 py-1">
                    ←
                  </button>
                  <span>
                    {active.n} / 30 · 390 × 844 · ← → keys walk the states
                  </span>
                  <button onClick={() => move(1)} className="rounded-md border border-white/15 px-2 py-1">
                    →
                  </button>
                </div>
              </div>
              <Notes panel={active} />
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
