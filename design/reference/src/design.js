// SPDX-License-Identifier: MIT
//
// THE design tokens for Tilefold — the only place colour, type, spacing, shape
// and motion values are written down on the web side.
//
// `scripts/check-tokens.mjs` asserts that this file and the frozen tables in
// `docs/UI-REFERENCE.md` agree, and that every tile numeral pair keeps its
// WCAG contrast. The Compose side reads the same values from
// `app/src/main/java/io/github/pabi277/tilefold/ui/theme/` — a value that is not
// here does not exist, and a screen may not invent one.

export const color = {
  light: {
    bg: '#FAF6EF',
    surface: '#FFFFFF',
    surfaceVariant: '#F1E9DC',
    scoreBox: '#D9CDB9',
    scoreLabel: '#5C4F3F',
    scoreValue: '#2C241C',
    boardFrame: '#E6DCCB',
    emptyCell: '#D3C7B2',
    accent: '#B4552F',
    onAccent: '#FFF8F0',
    textPrimary: '#2C241C',
    textSecondary: '#6B5C4A',
    divider: '#E3D9C8',
    outlineVariant: '#EFE6D8',
    error: '#9C3B32',
    onError: '#FFF8F0',
    success: '#3F7A4B',
    onSuccess: '#FFFFFF',
    skeleton: '#EFE6D8',
    skeletonShine: '#F8F2E8',
    scrim: 'rgba(44, 36, 28, 0.45)',
  },
  dark: {
    bg: '#161311',
    surface: '#241F1B',
    surfaceVariant: '#2E2823',
    scoreBox: '#3A322A',
    scoreLabel: '#C9BBA8',
    scoreValue: '#F3ECE2',
    boardFrame: '#2B2520',
    emptyCell: '#3A322A',
    accent: '#E08A5E',
    onAccent: '#1B1613',
    textPrimary: '#F3ECE2',
    textSecondary: '#B7A794',
    divider: '#43392F',
    outlineVariant: '#332C25',
    error: '#E98A7B',
    onError: '#1B1613',
    success: '#6FAF7C',
    onSuccess: '#12100E',
    skeleton: '#2E2823',
    skeletonShine: '#3A322A',
    scrim: 'rgba(0, 0, 0, 0.62)',
  },
};

// Tile ramp: value -> [background, numeral]. The numeral colour flips from the
// warm dark ink to white as the tile darkens, exactly as the brief describes.
export const tile = {
  light: {
    2: ['#F4EBDD', '#6B5C4A'],
    4: ['#F1E2C6', '#6B5C4A'],
    8: ['#F2CD9E', '#7A4A22'],
    16: ['#F0B87B', '#6E3F1C'],
    32: ['#EC9A5C', '#5A3318'],
    64: ['#E57B45', '#5A3318'],
    128: ['#DC5F35', '#FFFFFF'],
    256: ['#C74A2C', '#FFFFFF'],
    512: ['#B03B2A', '#FFFFFF'],
    1024: ['#C89434', '#2C241C'],
    2048: ['#A97A1F', '#FFF6DE'],
    super: ['#6E4E12', '#FFF6DE'],
  },
  dark: {
    2: ['#3E362E', '#EFE4D4'],
    4: ['#4A3F33', '#EFE4D4'],
    8: ['#8A5A32', '#FFF6EC'],
    16: ['#A2663A', '#FFF6EC'],
    32: ['#B87244', '#FFF6EC'],
    64: ['#C97F4B', '#FFFFFF'],
    128: ['#D2653B', '#FFFFFF'],
    256: ['#BC4E2E', '#FFFFFF'],
    512: ['#9E3D28', '#FFFFFF'],
    1024: ['#C39A46', '#241F1B'],
    2048: ['#A8822E', '#FFF6DE'],
    super: ['#7A5A16', '#FFF6DE'],
  },
};

export const tileFor = (value, theme) =>
  tile[theme][value > 2048 ? 'super' : String(value)] ?? tile[theme][2];

// Type scale, Material 3 roles. `line` is the line box in dp at 1.0x.
// Android ignores dp for text, so every screen multiplies these by the system
// font scale (`sp` behaviour); the reference does the same with its own scale.
export const type = {
  appTitle: { size: 40, line: 48, weight: 700, tracking: '-0.5px' },
  screenTitle: { size: 24, line: 32, weight: 600, tracking: '0' },
  dialogTitle: { size: 22, line: 28, weight: 600, tracking: '0' },
  scoreValue: { size: 26, line: 32, weight: 700, tracking: '0' },
  scoreLabel: { size: 12, line: 16, weight: 600, tracking: '0.08em' },
  body: { size: 16, line: 24, weight: 400, tracking: '0' },
  secondary: { size: 14, line: 20, weight: 400, tracking: '0' },
  button: { size: 16, line: 20, weight: 600, tracking: '0' },
  caption: { size: 12, line: 16, weight: 400, tracking: '0' },
};

// Tile numerals by digit count. They scale with the text scale only up to
// `tileTextMaxScale` (recorded native adaptation: the board keeps its square
// cells and legible numerals at 2.0x rather than overflowing).
export const tileText = { 1: 32, 2: 32, 3: 26, 4: 21, 5: 18 };
// Numerals inside fixed-size cells stop growing before the text does
// (deliberate native adaptation, recorded in docs/UI-REFERENCE.md).
export const tileTextMaxScale = 1.25;
export const scoreValueMaxScale = 1.5;

// 1 CSS px in the reference = 1 dp in the app (390 dp wide surface, the same
// width the screenshot tests render at). Nothing here is a web-only number.
export const space = { grid: [4, 8, 12, 16, 24, 32] };

/** Numeral size for a tile value at a given font scale (dp). */
export const tileFontSizeFor = (value, scale) => {
  const digits = String(value).length;
  const base = tileText[Math.min(digits, 5)] ?? tileText[5];
  return base * Math.min(scale, tileTextMaxScale);
};
export const radius = { board: 16, tile: 8, scoreBox: 12, button: 12, chip: 999, card: 16, banner: 12, dialog: 24, sheet: 24 };
export const size = {
  frameWidth: 390,
  frameHeight: 844,
  screenPadding: 24,
  screenPaddingV: 16,
  minTarget: 48,
  dirPadKey: 56,
  boardPadding: 10,
  tileGap: 8,
  scoreGap: 12,
  controlGap: 12,
  sameRowGap: 8,
  boardControlsGap: 16,
  dialogMaxWidth: 320,
  elevation: { board: 0, scoreBox: 0, dirPad: 2 },
};
export const duration = { slide: 110, pop: 140, spawn: 130, score: 220 };

// Face of the reference: Roboto (same family Android ships) with Noto Sans
// Devanagari for Hindi runs. Self-hosted so no network font is needed.
export const font = {
  family: "'Roboto', 'Noto Sans Devanagari', system-ui, -apple-system, 'Segoe UI', sans-serif",
  tabular: "'tnum' 1, 'lnum' 1",
};

// Harness chrome (the review page around the device). Not part of the app.
export const chrome = { bezel: '#17130F' };

// Gold milestone theme: only its seed is fixed at the freeze; the full scheme
// is derived in phase 4 where the theme ships.
export const goldSeed = '#A97A1F';
export const themeSeed = { light: color.light.accent, dark: color.dark.accent };
