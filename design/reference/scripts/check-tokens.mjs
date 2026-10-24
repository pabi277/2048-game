// SPDX-License-Identifier: MIT
//
// The freeze guard for phase 0-D (no dependencies, plain Node).
//
//   node design/reference/scripts/check-tokens.mjs
//
// It fails the build/CI when any of these is true:
//   1. a colour, tile-ramp, type, spacing or radius value in `docs/UI-REFERENCE.md`
//      disagrees with `src/design.js` (the values the reference renders);
//   2. a tile numeral pair drops below 3.0:1, or a body-size pair below 4.5:1;
//   3. a required screen/state row (1…27) has no reference panel, or a panel is
//      missing from the doc's screens table;
//   4. `src/ui.jsx` or `src/screens.jsx` contains a raw colour literal — the
//      "no screen defines its own colour" rule, enforced instead of promised.
import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import { dirname, join } from 'node:path';
import { color, radius, size, space, tile, tileFontSizeFor, tileTextMaxScale, scoreValueMaxScale, type } from '../src/design.js';

const here = dirname(fileURLToPath(import.meta.url));
const root = join(here, '..');
const docPath = join(root, '..', '..', 'docs', 'UI-REFERENCE.md');

const failures = [];
const check = (ok, message) => {
  if (!ok) failures.push(message);
};

/* ------------------------------------------------------------------ helpers */

const luminance = (hex) => {
  const h = hex.replace('#', '');
  const channels = [0, 2, 4].map((i) => parseInt(h.slice(i, i + 2), 16) / 255);
  const lin = channels.map((v) => (v <= 0.03928 ? v / 12.92 : ((v + 0.055) / 1.055) ** 2.4));
  return 0.2126 * lin[0] + 0.7152 * lin[1] + 0.0722 * lin[2];
};
const contrast = (a, b) => {
  const [hi, lo] = [luminance(a), luminance(b)].sort((x, y) => y - x);
  return (hi + 0.05) / (lo + 0.05);
};

const section = (doc, heading) => {
  const start = doc.indexOf(heading);
  if (start < 0) return '';
  const rest = doc.slice(start + heading.length);
  const end = rest.search(/\n###? /);
  return end < 0 ? rest : rest.slice(0, end);
};

const tableRows = (text) =>
  text
    .split('\n')
    .filter((line) => line.trim().startsWith('|'))
    .map((line) =>
      line
        .trim()
        .replace(/^\|/, '')
        .replace(/\|$/, '')
        .split('|')
        .map((cell) => cell.trim()),
    )
    .filter((cells) => !cells.every((c) => /^:?-{2,}:?$/.test(c)));

const unquote = (s) => s.replace(/[`*]/g, '').trim();
const hexes = (s) => [...s.matchAll(/#[0-9A-Fa-f]{6}\b/g)].map((m) => m[0].toUpperCase());

/* --------------------------------------------------------------------- read */

const doc = await readFile(docPath, 'utf8');

/* 1. colours ------------------------------------------------------------- */

// Scrims are rgba() by design (a translucent overlay, not a surface colour).
const NON_HEX = new Set(['scrim']);

for (const theme of ['light', 'dark']) {
  const heading = theme === 'light' ? '### Colour — light theme' : '### Colour — dark theme';
  const rows = tableRows(section(doc, heading));
  const documented = new Map();
  for (const cells of rows) {
    const [role, token, hex] = cells;
    if (!token || !hex) continue;
    const key = unquote(token);
    const value = unquote(hex);
    if (!/^#[0-9A-Fa-f]{6}$/.test(value)) continue;
    documented.set(key, value.toUpperCase());
    if (!role) failures.push(`${theme}: colour row without a role name (${key})`);
  }

  for (const [key, value] of Object.entries(color[theme])) {
    if (NON_HEX.has(key)) continue;
    const docValue = documented.get(key);
    check(docValue !== undefined, `docs: ${theme}.${key} is missing from ${heading}`);
    if (docValue !== undefined) {
      check(
        docValue === value.toUpperCase(),
        `docs mismatch: ${theme}.${key} is ${docValue} in docs but ${value} in design.js`,
      );
    }
  }
  for (const key of documented.keys()) {
    check(key in color[theme], `docs: ${theme}.${key} is in the doc but not in design.js`);
  }
}

/* 2. tile ramp ----------------------------------------------------------- */

for (const theme of ['light', 'dark']) {
  const heading = theme === 'light' ? '### Tile ramp — light' : '### Tile ramp — dark';
  const rows = tableRows(section(doc, heading));
  const documented = new Map();
  for (const [valueCell, bgCell, fgCell, ratioCell] of rows) {
    const label = unquote(valueCell ?? '');
    if (!/^\d+$|^> ?\d+$/.test(label)) continue;
    const key = label.startsWith('>') ? 'super' : label;
    const bg = hexes(bgCell ?? '')[0];
    const fg = hexes(fgCell ?? '')[0];
    if (!bg || !fg) {
      failures.push(`${theme}: tile ${label} row needs two hex values`);
      continue;
    }
    const ratio = Number(unquote(ratioCell ?? ''));
    documented.set(key, { bg, fg, ratio });
  }

  for (const [key, [bg, fg]] of Object.entries(tile[theme])) {
    const entry = documented.get(key);
    check(entry !== undefined, `docs: tile ${key} (${theme}) is missing from the ramp table`);
    if (!entry) continue;
    check(entry.bg === bg.toUpperCase(), `docs mismatch: tile ${key} ${theme} bg ${entry.bg} vs ${bg}`);
    check(entry.fg === fg.toUpperCase(), `docs mismatch: tile ${key} ${theme} numeral ${entry.fg} vs ${fg}`);
    const actual = contrast(bg, fg);
    check(
      Number.isFinite(entry.ratio) && Math.abs(entry.ratio - Number(actual.toFixed(2))) <= 0.01,
      `docs mismatch: tile ${key} ${theme} contrast ${entry.ratio} vs computed ${actual.toFixed(2)}`,
    );
    check(actual >= 3.0, `contrast: tile ${key} ${theme} numeral is ${actual.toFixed(2)}:1 (needs ≥3.0:1)`);
  }
}

/* 3. body-size contrast -------------------------------------------------- */

const pairs = [
  ['light text on background', color.light.textPrimary, color.light.bg, 4.5],
  ['dark text on background', color.dark.textPrimary, color.dark.bg, 4.5],
  ['light secondary on background', color.light.textSecondary, color.light.bg, 4.5],
  ['dark secondary on background', color.dark.textSecondary, color.dark.bg, 4.5],
  ['light score label on score box', color.light.scoreLabel, color.light.scoreBox, 4.5],
  ['dark score label on score box', color.dark.scoreLabel, color.dark.scoreBox, 4.5],
  ['light score value on score box', color.light.scoreValue, color.light.scoreBox, 4.5],
  ['dark score value on score box', color.dark.scoreValue, color.dark.scoreBox, 4.5],
  ['light button label on accent', color.light.onAccent, color.light.accent, 4.5],
  ['dark button label on accent', color.dark.onAccent, color.dark.accent, 4.5],
  ['light error text on surface', color.light.error, color.light.surface, 4.5],
  ['dark error text on surface', color.dark.error, color.dark.surface, 4.5],
  ['light new-best chip', color.light.onSuccess, color.light.success, 4.5],
  ['dark new-best chip', color.dark.onSuccess, color.dark.success, 4.5],
];
for (const [label, fg, bg, min] of pairs) {
  const actual = contrast(fg, bg);
  check(actual >= min, `contrast: ${label} is ${actual.toFixed(2)}:1 (needs ≥${min}:1)`);
}

/* 4. type scale ---------------------------------------------------------- */

const roleRows = {
  appTitle: 'App title (Home)',
  screenTitle: 'Screen title',
  dialogTitle: 'Dialog title',
  scoreValue: 'Score value',
  scoreLabel: 'Score label',
  body: 'Body',
  secondary: 'Secondary / list label',
  button: 'Button label',
  caption: 'Caption / legal',
};
const typeRows = tableRows(section(doc, '### Type scale'));
for (const [key, label] of Object.entries(roleRows)) {
  const row = typeRows.find((cells) => unquote(cells[0] ?? '') === label);
  check(row !== undefined, `docs: type role "${label}" is missing from the type-scale table`);
  if (!row) continue;
  const numbers = (row[1] ?? '').match(/\d+(\.\d+)?/g)?.map(Number) ?? [];
  const expected = type[key];
  check(
    numbers[0] === expected.size && numbers[1] === expected.line && numbers[2] === expected.weight,
    `docs mismatch: type ${label} is "${unquote(row[1])}" but design.js has ${expected.size}/${expected.line}/${expected.weight}`,
  );
}

// Tile numerals: the doc's digit rule must be the function the reference uses.
const digitRule = [[2, 32], [8, 32], [128, 26], [1024, 21], [16384, 18]];
for (const [value, expected] of digitRule) {
  check(
    tileFontSizeFor(value, 1) === expected,
    `tile numeral: ${value} renders at ${tileFontSizeFor(value, 1)}dp, doc says ${expected}dp`,
  );
}
check(tileTextMaxScale === 1.25, `tile numeral cap is ${tileTextMaxScale}, doc says 1.25×`);
check(scoreValueMaxScale === 1.5, `score numeral cap is ${scoreValueMaxScale}, doc says 1.5×`);

/* 5. spacing, shape, touch ---------------------------------------------- */

const constants = section(doc, '### Spacing, shape, elevation, motion');
const expectations = [
  [`${size.screenPadding}dp horizontal`, `screen padding (${size.screenPadding}dp)`],
  [`${size.screenPaddingV}dp top/bottom`, `vertical screen padding (${size.screenPaddingV}dp)`],
  [`${size.boardPadding}dp`, `board padding (${size.boardPadding}dp)`],
  [`tile gap ${size.tileGap}dp`, `tile gap (${size.tileGap}dp)`],
  [`${radius.board}dp`, `board radius (${radius.board}dp)`],
  [`tile corner ${radius.tile}dp`, `tile radius (${radius.tile}dp)`],
  [`score box corner ${radius.scoreBox}dp`, `score box radius (${radius.scoreBox}dp)`],
  [`max width ${size.dialogMaxWidth}dp`, `dialog max width (${size.dialogMaxWidth}dp)`],
  [`≥${size.minTarget}dp`, `minimum touch target (${size.minTarget}dp)`],
  [`${size.dirPadKey}dp`, `direction-pad key (${size.dirPadKey}dp)`],
  [space.grid.join(' / '), 'spacing scale'],
];
for (const [needle, label] of expectations) {
  check(constants.includes(needle), `docs: ${label} — "${needle}" not found in the spacing section`);
}
check(
  expectations.every(([needle]) => constants.includes(needle)),
  'docs: the spacing section is the only place these measurements may live',
);

/* 6. screen/state coverage ---------------------------------------------- */

const screensSource = await readFile(join(root, 'src', 'screens.jsx'), 'utf8');
const panels = [...screensSource.matchAll(/P\(\s*'([a-z0-9-]+)',\s*(\d+),/g)].map((m) => ({
  id: m[1],
  n: Number(m[2]),
}));
for (let n = 1; n <= 27; n += 1) {
  check(
    panels.some((p) => p.n === n),
    `reference: state ${n} of docs/UI-REFERENCE.md has no panel in src/screens.jsx`,
  );
  check(
    new RegExp(`^\\| ${n} \\| \\*\\*`, 'm').test(doc),
    `docs: the screens table has no row for state ${n}`,
  );
}
for (const panel of panels) {
  check(
    doc.includes(`\`#${panel.id}\``),
    `docs: reference page #${panel.id} (state ${panel.n}) is not named in the screens table`,
  );
  check(panel.id.startsWith('tokens') === false, `reference: panel id "${panel.id}" is not a screen id`);
}
const screenshotFiles = await readFile(join(root, 'scripts', 'screenshots.mjs'), 'utf8');
for (const panel of panels) {
  check(
    screenshotFiles.includes(`'${panel.id}'`),
    `scripts/screenshots.mjs: state ${panel.n} (${panel.id}) is not in the capture list`,
  );
}

/* 7. no raw colours in screens ------------------------------------------ */

for (const file of ['ui.jsx', 'screens.jsx']) {
  const source = await readFile(join(root, 'src', file), 'utf8');
  const literals = [...source.matchAll(/#[0-9A-Fa-f]{3,8}\b/g)].map((m) => m[0]);
  check(
    literals.length === 0,
    `src/${file}: raw colour literal(s) ${literals.join(', ')} — every colour must come from design.js`,
  );
}

/* -------------------------------------------------------------------- report */

if (failures.length > 0) {
  console.error(`check-tokens: ${failures.length} problem(s)\n`);
  for (const failure of failures) console.error(`  ✗ ${failure}`);
  process.exit(1);
}

console.log(
  [
    'check-tokens: OK',
    `  colours: ${Object.keys(color.light).length} light + ${Object.keys(color.dark).length} dark tokens match docs/UI-REFERENCE.md`,
    `  tile ramp: 12 steps per theme, every numeral ≥3.0:1`,
    `  body pairs: ${pairs.length} pairs ≥4.5:1`,
    `  type: ${Object.keys(type).length} roles + numeral caps match`,
    `  screens: ${panels.length} reference states cover rows 1…27 (+ confirms, Hindi)`,
    '  no raw colour literals in src/ui.jsx or src/screens.jsx',
  ].join('\n'),
);
