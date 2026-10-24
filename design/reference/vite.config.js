// SPDX-License-Identifier: MIT
//
// Disposable UI reference for Tilefold. It is a design specification, never
// shipped: nothing in `app/` imports it, and it is excluded from the APK.
//
// Two ways to serve it:
//   * plain `npm run dev`       — localhost (Termux, laptop): normal HMR.
//   * `TILEFOLD_PREVIEW=1 npm run dev` — behind Arena's HTTPS preview proxy,
//     which needs HMR over the proxied wss port.
// Both bind 0.0.0.0 so a phone on the same network can open the LAN address.
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

const behindProxy = process.env.TILEFOLD_PREVIEW === '1';

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    host: '0.0.0.0',
    port: 5180,
    strictPort: true,
    // The proxy forwards a host we do not know in advance; local use is
    // unaffected because `allowedHosts: true` only widens what is accepted.
    allowedHosts: true,
    hmr: behindProxy ? { protocol: 'wss', clientPort: 443 } : undefined,
  },
  preview: { host: '0.0.0.0', port: 5180, allowedHosts: true },
});
