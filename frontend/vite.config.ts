/// <reference types="vite/client" />

import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    define: {
      global: 'window',
    },

    plugins: [react(), tailwindcss()],

    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
      },
    },

    server: {
      proxy: {
        '/ws': {
          target: env.VITE_API_BASE_URL,
          changeOrigin: true,
          ws: true,
        },

        '/api/': {
          target: env.VITE_API_BASE_URL,
          changeOrigin: true,
        },

        '/login': {
          target: env.VITE_API_BASE_URL,
          changeOrigin: true,
        },

        '/logout': {
          target: env.VITE_API_BASE_URL,
          changeOrigin: true,
        },

        '/csrf': {
          target: env.VITE_API_BASE_URL,
          changeOrigin: true,
        },
      },
    },
  }
})
