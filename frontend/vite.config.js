import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 开发服务器端口 5173；/api 开头的请求代理到后端 8080，规避 CORS（契约见 docs/api-contract.md）
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    // 显式绑定 IPv4：Windows 下不写 host 时 Vite 可能只监听 IPv6 ::1，
    // 导致浏览器访问 http://127.0.0.1:5173 被拒绝（见 docs/debug-log.md 报错记录 2）
    host: '127.0.0.1',
    port: 5173,
    strictPort: false,
    open: false,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: false
  }
})
