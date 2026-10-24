// SPDX-License-Identifier: MIT
//
// Optional review tooling: renders every state of the reference to a PNG.
//
//   npm i -D playwright-core @sparticuz/chromium   # ad hoc; not in package.json
//   npm run dev &
//   node scripts/screenshots.mjs [baseUrl] [outDir]
//
// The site itself needs no browser and no build step beyond Vite — this script
// exists so the approved reference can be archived as images beside the phase
// record (later UI phases compare their Compose screenshots against them).
import chromium from '@sparticuz/chromium';
import { chromium as playwright } from 'playwright-core';
import { mkdir } from 'node:fs/promises';

const base = process.argv[2] ?? 'http://127.0.0.1:5180';
const out = process.argv[3] ?? 'screenshots';

const states = [
  [1, 'home-no-save'], [2, 'home-in-progress'], [3, 'home-daily-done'], [4, 'home-empty'],
  [5, 'home-storage-error'], [6, 'home-loading'], [7, 'game-new'], [8, 'game-mid'],
  [9, 'game-paused'], [10, 'game-dark'], [11, 'game-large-font'], [12, 'game-storage-error'],
  [13, 'win'], [14, 'win-dark-large'], [15, 'game-over'], [16, 'game-over-dismissed'],
  [17, 'game-over-dark-large'], [18, 'how-to-play'], [19, 'how-to-play-dark-large'],
  [20, 'settings'], [21, 'settings-ad-free'], [22, 'settings-unavailable'],
  [23, 'settings-dark-large'], [24, 'stats-empty'], [25, 'stats-populated'],
  [26, 'stats-dark-large'], [27, 'stats-storage-error'], [28, 'confirm-restart'],
  [29, 'confirm-reset-stats'], [30, 'home-hindi'],
];

await mkdir(out, { recursive: true });
const browser = await playwright.launch({
  executablePath: await chromium.executablePath(),
  args: chromium.args,
  headless: true,
});
const page = await browser.newPage({ viewport: { width: 1280, height: 1000 }, deviceScaleFactor: 2 });

for (const [n, id] of states) {
  await page.goto(`${base}/#/${id}`, { waitUntil: 'networkidle' });
  await page.evaluate(() => document.fonts.ready);
  await page.waitForTimeout(120);
  const frame = page.locator('[data-testid="phone-frame"]').first();
  await frame.screenshot({ path: `${out}/${String(n).padStart(2, '0')}-${id}.png` });
  process.stdout.write(`${n} ${id}\n`);
}

await page.goto(`${base}/#/tokens`, { waitUntil: 'networkidle' });
await page.evaluate(() => document.fonts.ready);
await page.screenshot({ path: `${out}/00-tokens.png`, fullPage: true });

await page.goto(`${base}/?gallery=1`, { waitUntil: 'networkidle' });
await page.evaluate(() => document.fonts.ready);
await page.waitForTimeout(200);
await page.screenshot({ path: `${out}/00-all-states.png`, fullPage: true });

await browser.close();
console.log('done');
