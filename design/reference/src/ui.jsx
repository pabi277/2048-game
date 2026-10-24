// SPDX-License-Identifier: MIT
//
// Building blocks of the reference. Every value here comes from `design.js`;
// nothing is a one-off hex or size. This file is the *web* rendering of the
// component inventory that phase 2+ re-implements in Compose — the code itself
// never crosses over.
import { createContext, useContext, useMemo } from 'react';
import { Button, Segmented, SegmentedButton, Toggle } from 'konsta/react';
import {
  ArrowDown,
  ArrowLeft,
  ArrowRight,
  ArrowUp,
  ChartColumn,
  ChevronRight,
  CircleHelp,
  House,
  Lock,
  Palette,
  Play,
  RotateCcw,
  Settings as SettingsIcon,
  Smartphone,
  SunMoon,
  Undo2,
  Volume2,
  X,
} from 'lucide-react';
import {
  chrome,
  color,
  font,
  radius,
  scoreValueMaxScale,
  size,
  space,
  tileFontSizeFor,
  tileFor,
  type,
} from './design.js';
import { makeT } from './strings.js';

const DesignContext = createContext(null);
export const useDesign = () => useContext(DesignContext);

const rgbTriplet = (hex) => {
  const h = hex.replace('#', '');
  const n = h.length === 3 ? h.split('').map((x) => x + x).join('') : h;
  return `${parseInt(n.slice(0, 2), 16)} ${parseInt(n.slice(2, 4), 16)} ${parseInt(n.slice(4, 6), 16)}`;
};

/** Konsta reads its Material palette from these variables; so do our own parts. */
function cssVars(c, theme) {
  const pair = (name) => `rgb(${rgbTriplet(c[name])})`;
  const md = {
    primary: pair('accent'),
    'on-primary': pair('onAccent'),
    'primary-container': pair('scoreBox'),
    'on-primary-container': pair('scoreValue'),
    secondary: pair('textSecondary'),
    'on-secondary': pair('surface'),
    'secondary-container': pair('surfaceVariant'),
    'on-secondary-container': pair('textPrimary'),
    surface: pair('surface'),
    'on-surface': pair('textPrimary'),
    'surface-variant': pair('surfaceVariant'),
    'on-surface-variant': pair('textSecondary'),
    outline: pair('divider'),
    'outline-variant': pair('outlineVariant'),
    surface1: pair('surface'),
    surface2: pair('surfaceVariant'),
    surface3: pair('surfaceVariant'),
    surface4: pair('surfaceVariant'),
    surface5: pair('surfaceVariant'),
  };
  const vars = {};
  Object.entries(md).forEach(([k, v]) => {
    vars[`--k-color-md-light-${k}`] = v;
    vars[`--k-color-md-dark-${k}`] = v;
  });
  vars['--k-color-primary'] = md.primary;
  // App-level aliases so Tailwind arbitrary values stay readable.
  vars['--bg'] = c.bg;
  vars['--surface'] = c.surface;
  vars['--surface-variant'] = c.surfaceVariant;
  vars['--accent'] = c.accent;
  vars['--on-accent'] = c.onAccent;
  vars['--text'] = c.textPrimary;
  vars['--text-secondary'] = c.textSecondary;
  vars['--divider'] = c.divider;
  vars['--outline-variant'] = c.outlineVariant;
  vars['--error'] = c.error;
  vars['--on-error'] = c.onError;
  vars['--success'] = c.success;
  vars['--on-success'] = c.onSuccess;
  vars['--board'] = c.boardFrame;
  vars['--empty'] = c.emptyCell;
  vars['--score-box'] = c.scoreBox;
  vars['--score-label'] = c.scoreLabel;
  vars['--score-value'] = c.scoreValue;
  vars['--scrim'] = c.scrim;
  vars['--skeleton'] = c.skeleton;
  vars['--skeleton-shine'] = c.skeletonShine;
  vars['--font-app'] = font.family;
  return vars;
}

export function DesignProvider({ theme = 'light', scale = 1, lang = 'en', children }) {
  const value = useMemo(
    () => ({ c: color[theme], theme, scale, t: makeT(lang), lang }),
    [theme, scale, lang],
  );
  const vars = useMemo(() => cssVars(color[theme], theme), [theme]);
  return (
    <DesignContext.Provider value={value}>
      <div
        className={theme === 'dark' ? 'dark h-full w-full' : 'h-full w-full'}
        style={{ ...vars, fontFamily: font.family, color: 'var(--text)', background: 'var(--bg)' }}
      >
        {children}
      </div>
    </DesignContext.Provider>
  );
}

/** Text style from the frozen scale, multiplied by the Android font scale. */
export function tx(role, scale = 1, extra = {}) {
  const r = type[role];
  return {
    fontSize: `${r.size * scale}px`,
    lineHeight: `${r.line * scale}px`,
    fontWeight: r.weight,
    letterSpacing: r.tracking,
    ...extra,
  };
}

const tabular = { fontVariantNumeric: 'tabular-nums' };

/* ------------------------------------------------------------------ frame */

export function PhoneFrame({ children, overlay, dark }) {
  return (
    <div
      data-testid="phone-frame"
      className="relative shrink-0 overflow-hidden"
      style={{
        // 10 px bezel on each side: the surface inside is exactly 390 x 844.
        width: size.frameWidth + 20,
        height: size.frameHeight + 20,
        borderRadius: 44,
        border: `10px solid ${chrome.bezel}`,
        background: 'var(--bg)',
        // Contains position: fixed children (Konsta portals) inside the frame.
        transform: 'translateZ(0)',
        boxShadow: '0 24px 60px rgba(0,0,0,0.35)',
      }}
    >
      <div className="relative h-full w-full overflow-y-auto overflow-x-hidden" style={{ scrollbarWidth: 'thin' }}>
        {children}
      </div>
      {overlay}
      <span className="sr-only">{dark ? 'dark theme' : 'light theme'}</span>
    </div>
  );
}

export function Screen({ children, padded = true, className = '', style }) {
  return (
    <div
      className={`flex min-h-full flex-col ${className}`}
      style={{
        background: 'var(--bg)',
        paddingTop: padded ? size.screenPaddingV : 0,
        paddingBottom: padded ? size.screenPaddingV : 0,
        paddingLeft: padded ? size.screenPadding : 0,
        paddingRight: padded ? size.screenPadding : 0,
        ...style,
      }}
    >
      {children}
    </div>
  );
}

export function AppBar({ title, onBack = true, actions = null, subtitle }) {
  const { scale } = useDesign();
  return (
    <div className="flex items-center gap-2" style={{ minHeight: size.minTarget, marginBottom: 8 }}>
      {onBack ? <IconBtn icon={ArrowLeft} label="Back" /> : null}
      <div className="min-w-0 flex-1">
        <div style={tx('screenTitle', scale, { textWrap: 'balance' })}>{title}</div>
        {subtitle ? (
          <div style={tx('secondary', scale, { color: 'var(--text-secondary)' })}>{subtitle}</div>
        ) : null}
      </div>
      {actions}
    </div>
  );
}

export function IconBtn({ icon: Icon, label, onClick, tone = 'default', sizePx = 22 }) {
  const colour = tone === 'accent' ? 'var(--accent)' : tone === 'error' ? 'var(--error)' : 'var(--text)';
  return (
    <button
      type="button"
      aria-label={label}
      title={label}
      onClick={onClick}
      className="flex items-center justify-center"
      style={{
        width: size.minTarget,
        height: size.minTarget,
        borderRadius: radius.button,
        color: colour,
        background: 'transparent',
        border: 'none',
        cursor: 'pointer',
      }}
    >
      <Icon size={sizePx} strokeWidth={1.9} />
    </button>
  );
}

/* ---------------------------------------------------------------- controls */

export function PrimaryBtn({ children, icon: Icon, disabled, onClick, full = true }) {
  const { scale } = useDesign();
  return (
    <Button
      onClick={onClick}
      disabled={disabled}
      rounded={false}
      className="!m-0"
      colors={{
        fillBgMaterial: 'bg-[var(--accent)]',
        fillTextMaterial: 'text-[var(--on-accent)]',
        fillTouchRipple: 'touch-ripple-white',
      }}
      style={{
        height: Math.max(size.minTarget, 20 + type.button.line * scale),
        width: full ? '100%' : undefined,
        borderRadius: radius.button,
        paddingInline: 16,
        opacity: disabled ? 0.45 : 1,
        ...tx('button', scale),
        display: 'flex',
        gap: 8,
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      {Icon ? <Icon size={20} strokeWidth={2} /> : null}
      {children}
    </Button>
  );
}

export function SecondaryBtn({ children, icon: Icon, disabled, onClick, full = true }) {
  const { scale } = useDesign();
  return (
    <Button
      onClick={onClick}
      disabled={disabled}
      rounded={false}
      className="!m-0"
      colors={{
        fillBgMaterial: 'bg-[var(--surface-variant)]',
        fillTextMaterial: 'text-[var(--text)]',
        fillTouchRipple: 'touch-ripple-black',
      }}
      style={{
        height: Math.max(size.minTarget, 20 + type.button.line * scale),
        width: full ? '100%' : undefined,
        borderRadius: radius.button,
        paddingInline: 16,
        opacity: disabled ? 0.45 : 1,
        ...tx('button', scale),
        display: 'flex',
        gap: 8,
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      {Icon ? <Icon size={20} strokeWidth={2} /> : null}
      {children}
    </Button>
  );
}

export function TextAction({ children, icon: Icon, onClick, tone = 'accent', style }) {
  const { scale } = useDesign();
  const colour = tone === 'error' ? 'var(--error)' : tone === 'muted' ? 'var(--text-secondary)' : 'var(--accent)';
  return (
    <button
      type="button"
      onClick={onClick}
      className="flex items-center justify-center gap-2"
      style={{
        minHeight: size.minTarget,
        paddingInline: 8,
        background: 'transparent',
        border: 'none',
        cursor: 'pointer',
        color: colour,
        fontWeight: 600,
        ...tx('button', scale),
        ...style,
      }}
    >
      {Icon ? <Icon size={18} strokeWidth={2} /> : null}
      {children}
    </button>
  );
}

export function SettingsRow({ title, subtitle, trail, onClick, destructive, disabled, first }) {
  const { scale } = useDesign();
  return (
    <button
      type="button"
      onClick={disabled ? undefined : onClick}
      className="flex w-full items-center gap-3 text-left"
      style={{
        minHeight: Math.max(size.minTarget, 24 + type.secondary.line * scale),
        paddingTop: 8,
        paddingBottom: 8,
        paddingInline: 16,
        borderTop: first ? 'none' : '1px solid var(--divider)',
        background: 'transparent',
        border: 'none',
        borderTopColor: 'var(--divider)',
        cursor: disabled ? 'default' : 'pointer',
        opacity: disabled ? 0.55 : 1,
      }}
    >
      <span className="min-w-0 flex-1">
        <span
          style={tx('body', scale, {
            display: 'block',
            color: destructive ? 'var(--error)' : 'var(--text)',
            fontWeight: 500,
          })}
        >
          {title}
        </span>
        {subtitle ? (
          <span
            style={tx('secondary', scale, {
              display: 'block',
              color: destructive ? 'var(--error)' : 'var(--text-secondary)',

            })}
          >
            {subtitle}
          </span>
        ) : null}
      </span>
      {trail}
    </button>
  );
}

export function ToggleRow({ title, subtitle, checked, onClick, disabled }) {
  return (
    <SettingsRow
      title={title}
      subtitle={subtitle}
      disabled={disabled}
      onClick={onClick}
      trail={
        <Toggle
          checked={Boolean(checked)}
          disabled={disabled}
          onChange={onClick}
          colors={{
            bgMaterial: 'bg-[var(--score-box)]',
            checkedBgMaterial: 'bg-[var(--accent)]',
            thumbBgMaterial: 'bg-[var(--surface)]',
            checkedThumbBgMaterial: 'bg-[var(--on-accent)]',
          }}
        />
      }
    />
  );
}

export function RowGroup({ children, style }) {
  return (
    <div
      style={{
        background: 'var(--surface)',
        border: '1px solid var(--outline-variant)',
        borderRadius: radius.card,
        overflow: 'hidden',
        ...style,
      }}
    >
      {children}
    </div>
  );
}

export function Switch({ checked, disabled }) {
  return (
    <Toggle
      checked={Boolean(checked)}
      disabled={disabled}
      readOnly
      colors={{
        bgMaterial: 'bg-[var(--score-box)]',
        checkedBgMaterial: 'bg-[var(--accent)]',
        thumbBgMaterial: 'bg-[var(--surface)]',
        checkedThumbBgMaterial: 'bg-[var(--on-accent)]',
      }}
    />
  );
}

export function SegmentedChoice({ options, value, onChange }) {
  const { scale } = useDesign();
  return (
    <Segmented
      strong
      rounded={false}
      className="!m-0"
      colors={{
        strongBgMaterial: 'bg-[var(--surface-variant)]',
        strongHighlightBgMaterial: 'bg-[var(--accent)]',
      }}
      style={{ borderRadius: radius.button }}
    >
      {options.map((opt) => (
        <SegmentedButton
          key={opt.value}
          strong
          active={opt.value === value}
          onClick={() => onChange?.(opt.value)}
          className={opt.value === value ? '!bg-[var(--accent)] !text-[var(--on-accent)]' : '!text-[var(--text)]'}
          style={{ ...tx('secondary', scale, { fontWeight: 600 }), minHeight: size.minTarget - 8 }}
        >
          {opt.label}
        </SegmentedButton>
      ))}
    </Segmented>
  );
}

export function Chip({ children, tone = 'neutral', icon: Icon }) {
  const { scale } = useDesign();
  const map = {
    neutral: ['var(--surface-variant)', 'var(--text-secondary)'],
    accent: ['var(--accent)', 'var(--on-accent)'],
    success: ['var(--success)', 'var(--on-success)'],
    outline: ['transparent', 'var(--text-secondary)'],
  };
  const [bg, fg] = map[tone] ?? map.neutral;
  return (
    <span
      className="inline-flex items-center gap-1"
      style={{
        background: bg,
        color: fg,
        border: tone === 'outline' ? '1px solid var(--divider)' : 'none',
        borderRadius: radius.chip,
        padding: '4px 10px',
        ...tx('caption', scale, { fontWeight: 600 }),
        whiteSpace: 'nowrap',
      }}
    >
      {Icon ? <Icon size={12} strokeWidth={2.4} /> : null}
      {children}
    </span>
  );
}

/* ------------------------------------------------------------------ game */

export function ScoreBox({ label, value, scaleCap = scoreValueMaxScale }) {
  const { scale } = useDesign();
  return (
    <div
      className="flex flex-1 flex-col items-center"
      style={{
        background: 'var(--score-box)',
        borderRadius: radius.scoreBox,
        paddingTop: 8,
        paddingBottom: 8,
        paddingInline: 12,
        minWidth: 0,
      }}
    >
      <span
        style={tx('scoreLabel', scale, {
          color: 'var(--score-label)',
          textTransform: 'uppercase',
          letterSpacing: `${0.08 * scale}em`,
        })}
      >
        {label}
      </span>
      <span
        style={tx('scoreValue', Math.min(scale, scoreValueMaxScale), {
          color: 'var(--score-value)',
          ...tabular,
          textAlign: 'center',
          overflowWrap: 'anywhere',
        })}
      >
        {value}
      </span>
    </div>
  );
}

export function Tile({ value, pop = false, fontPx }) {
  const { theme, scale } = useDesign();
  const [bg, fg] = tileFor(value, theme);
  return (
    <div
      className="flex items-center justify-center"
      style={{
        // Fill the cell: the grid item is square, the tile must be square with
        // it. Without width/height here the numeral box collapses to text height
        // and the board renders as flat bars.
        width: '100%',
        height: '100%',
        boxSizing: 'border-box',
        background: bg,
        color: fg,
        borderRadius: radius.tile,
        fontWeight: 700,
        fontSize: `${fontPx ?? tileFontSizeFor(value, scale)}px`,
        lineHeight: 1,
        ...tabular,
        transform: pop ? 'scale(1.06)' : 'none',
        boxShadow: 'none',
      }}
    >
      {value}
    </div>
  );
}

export function Board({ grid, dim = false, popping = null }) {
  return (
    <div
      style={{
        background: 'var(--board)',
        borderRadius: radius.board,
        padding: size.boardPadding,
        display: 'grid',
        gridTemplateColumns: 'repeat(4, 1fr)',
        gridTemplateRows: 'repeat(4, 1fr)',
        gap: size.tileGap,
        aspectRatio: '1 / 1',
        width: '100%',
        opacity: dim ? 0.45 : 1,
      }}
    >
      {grid.flatMap((row, r) =>
        row.map((value, cIdx) => {
          const key = `${r}-${cIdx}`;
          return (
            <div
              key={key}
              style={{
                // Grid rows come from the square board + `1fr`, so the cell is
                // already square; the tile simply fills it.
                display: 'flex',
                background: value ? 'transparent' : 'var(--empty)',
                borderRadius: radius.tile,
              }}
            >
              {value ? <Tile value={value} pop={popping === key} /> : null}
            </div>
          );
        }),
      )}
    </div>
  );
}

export function DirPad({ enabled = true }) {
  const { scale } = useDesign();
  const key = (Icon, label) => (
    <button
      type="button"
      aria-label={label}
      style={{
        width: size.dirPadKey,
        height: size.dirPadKey,
        borderRadius: radius.button,
        background: 'var(--surface)',
        color: enabled ? 'var(--text)' : 'var(--text-secondary)',
        border: '1px solid var(--outline-variant)',
        boxShadow: size.elevation.dirPad ? '0 2px 4px rgba(0,0,0,0.10)' : 'none',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        cursor: 'pointer',
        ...tx('secondary', scale),
      }}
    >
      <Icon size={22} strokeWidth={2} />
    </button>
  );
  return (
    <div className="flex flex-col items-center" style={{ gap: 4 }}>
      <div>{key(ArrowUp, 'Up')}</div>
      <div className="flex" style={{ gap: 4 }}>
        {key(ArrowLeft, 'Left')}
        {key(ArrowDown, 'Down')}
        {key(ArrowRight, 'Right')}
      </div>
    </div>
  );
}

/**
 * A decorative still of the board, shown on Home so the first screen shows what
 * the game looks like. Non-interactive, aria-hidden; numerals are computed from
 * the cell size so a small board stays legible.
 */
export function BoardPreview({ px = 196 }) {
  const { theme } = useDesign();
  const gap = 6;
  const padding = 8;
  const cell = (px - padding * 2 - gap * 3) / 4;
  const grid = [
    [2, 4, 8, 16],
    [32, 64, 128, 256],
    [0, 2, 4, 0],
    [0, 0, 0, 8],
  ];
  return (
    <div
      aria-hidden="true"
      style={{
        width: px,
        background: 'var(--board)',
        borderRadius: radius.board,
        padding,
        display: 'grid',
        gridTemplateColumns: `repeat(4, ${cell}px)`,
        gridTemplateRows: `repeat(4, ${cell}px)`,
        gap,
      }}
    >
      {grid.flatMap((row, r) =>
        row.map((value, c) => {
          const [bg, fg] = value ? tileFor(value, theme) : ['var(--empty)', 'transparent'];
          return (
            <div
              key={`${r}-${c}`}
              style={{
                background: bg,
                color: fg,
                borderRadius: 5,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 700,
                fontSize: Math.round(cell * 0.44),
                lineHeight: 1,
                ...tabular,
              }}
            >
              {value || ''}
            </div>
          );
        }),
      )}
    </div>
  );
}

/* --------------------------------------------------------------- surfaces */

export function Card({ children, style, onClick }) {
  return (
    <div
      onClick={onClick}
      style={{
        background: 'var(--surface)',
        border: '1px solid var(--outline-variant)',
        borderRadius: radius.card,
        padding: 16,
        ...style,
      }}
    >
      {children}
    </div>
  );
}

export function SectionLabel({ children }) {
  const { scale } = useDesign();
  return (
    <div
      style={tx('scoreLabel', scale, {
        color: 'var(--text-secondary)',
        textTransform: 'uppercase',
        letterSpacing: `${0.08 * scale}em`,
        marginTop: 16,
        marginBottom: 8,
      })}
    >
      {children}
    </div>
  );
}

export function Banner({ message, detail, onRetry, retryLabel = 'Retry' }) {
  const { scale } = useDesign();
  return (
    <div
      role="status"
      className="flex items-start gap-3"
      style={{
        background: 'var(--surface)',
        border: '1px solid color-mix(in srgb, var(--error) 45%, transparent)',
        borderRadius: radius.banner,
        paddingTop: 12,
        paddingBottom: 12,
        paddingInline: 12,
      }}
    >
      <div style={{ color: 'var(--error)', marginTop: 2 }}>
        <CircleHelp size={18} strokeWidth={2.2} />
      </div>
      <div className="min-w-0 flex-1">
        <div style={tx('secondary', scale, { color: 'var(--error)', fontWeight: 600 })}>{message}</div>
        {detail ? <div style={tx('caption', scale, { color: 'var(--text-secondary)' })}>{detail}</div> : null}
      </div>
      {onRetry ? (
        <TextAction onClick={onRetry} style={tx('secondary', scale, { minHeight: 32, fontWeight: 700 })}>
          {retryLabel}
        </TextAction>
      ) : null}
    </div>
  );
}

export function StatTile({ label, value, wide = false }) {
  const { scale } = useDesign();
  return (
    <div
      style={{
        background: 'var(--surface)',
        border: '1px solid var(--outline-variant)',
        borderRadius: radius.card,
        padding: 16,
        gridColumn: wide ? '1 / -1' : undefined,
      }}
    >
      <div style={tx('secondary', scale, { color: 'var(--text-secondary)' })}>{label}</div>
      <div style={tx('scoreValue', scale, { ...tabular, marginTop: 4 })}>{value}</div>
    </div>
  );
}

export function Skeleton({ width = '100%', height = 16, style }) {
  return (
    <div
      aria-hidden
      style={{
        width,
        height,
        borderRadius: 8,
        background: 'linear-gradient(90deg, var(--skeleton), var(--skeleton-shine), var(--skeleton))',
        ...style,
      }}
    />
  );
}

/* ---------------------------------------------------------------- overlays */

export function Dialog({ title, children, actions }) {
  const { scale } = useDesign();
  return (
    <div
      className="absolute inset-0 flex items-center justify-center"
      style={{ background: 'var(--scrim)', padding: 24 }}
    >
      <div
        role="dialog"
        aria-modal="true"
        style={{
          background: 'var(--surface)',
          borderRadius: radius.dialog,
          padding: 24,
          width: '100%',
          maxWidth: size.dialogMaxWidth,
          boxShadow: '0 18px 40px rgba(0,0,0,0.28)',
          maxHeight: '78%',
          overflowY: 'auto',
        }}
      >
        <div style={tx('dialogTitle', scale, { marginBottom: 8 })}>{title}</div>
        <div style={{ ...tx('secondary', scale, { color: 'var(--text-secondary)' }) }}>{children}</div>
        <div className="flex flex-col" style={{ gap: 8, marginTop: 20 }}>
          {actions}
        </div>
      </div>
    </div>
  );
}

export function Sheet({ title, children, actions }) {
  const { scale } = useDesign();
  return (
    <div className="absolute inset-0 flex items-end" style={{ background: 'var(--scrim)' }}>
      <div
        role="dialog"
        aria-modal="true"
        style={{
          background: 'var(--surface)',
          borderTopLeftRadius: radius.sheet,
          borderTopRightRadius: radius.sheet,
          padding: 24,
          paddingBottom: 32,
          width: '100%',
        }}
      >
        <div className="mx-auto" style={{ width: 40, height: 4, borderRadius: 2, background: 'var(--divider)', marginBottom: 16 }} />
        <div style={tx('dialogTitle', scale, { marginBottom: 16 })}>{title}</div>
        <div className="flex flex-col" style={{ gap: 8 }}>
          {actions}
        </div>
        {children}
      </div>
    </div>
  );
}

/* Icon set shorthand, so screens stay readable. */
export const Icons = {
  home: House,
  restart: RotateCcw,
  undo: Undo2,
  settings: SettingsIcon,
  help: CircleHelp,
  stats: ChartColumn,
  sound: Volume2,
  haptics: Smartphone,
  theme: Palette,
  language: SunMoon,
  close: X,
  lock: Lock,
  chevron: ChevronRight,
  play: Play,
  up: ArrowUp,
  down: ArrowDown,
  left: ArrowLeft,
  right: ArrowRight,
};

export { space };
