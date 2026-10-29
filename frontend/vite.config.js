import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 22871,
    strictPort: true,
    proxy: {
      '/api': {
        target: 'http://localhost:22870',
        changeOrigin: true,
      },
    },
  },
})
