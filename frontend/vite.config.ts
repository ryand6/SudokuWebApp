import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  define: {
    // Polyfill Node global
    global: "window",
  },
  plugins: [react(), tailwindcss()],
  server: {
    proxy: {
      "/ws": {
          target: "http://localhost:8080",
          changeOrigin: true,
          ws: true
      },
      "/api/": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/login": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/logout": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/csrf": {
        target: "http://localhost:8080",
        changeOrigin: true,
      }
    }
  }
})
