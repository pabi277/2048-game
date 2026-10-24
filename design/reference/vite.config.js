// SPDX-License-Identifier: MIT
//
// Disposable UI reference for Tilefold. It is a design specification, never
// shipped: nothing in `app/` imports it, and it is excluded from the APK.
//
// The dev server must be reachable through Arena's HTTPS preview proxy, hence
// 0.0.0.0, no host allow-list, and HMR over the proxied wss port.
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    host: '0.0.0.0',
    port: 5180,
    strictPort: true,
    allowedHosts: true,
    hmr: { protocol: 'wss', clientPort: 443 },
  },
  preview: { host: '0.0.0.0', port: 5180, allowedHosts: true },
});
