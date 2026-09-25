import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useToastStore } from './stores/toast'
import { revealDirective } from './utils/reveal'
import './styles/base.css'

const app = createApp(App)
const pinia = createPinia()

// 全局指令：滚动进场动画（实现见 utils/reveal.js）
app.directive('reveal', revealDirective)

app.use(pinia)
app.use(router)

// 全局错误兜底（阶段 6 批 4 · 决策 AX）：组件渲染 / 生命周期 / 侦听器里未捕获的异常，
// 以及没有 catch 的 Promise 拒绝，都收敛成一条 Toast —— 避免"页面静静地坏掉"、用户只看到白屏。
// 错误照旧写进 console，不吞异常；Toast 只给用户看得到的提示，技术细节留在控制台。
const toast = useToastStore(pinia)
const UNEXPECTED_ERROR_MESSAGE = '页面出现未预期的异常，请刷新或稍后重试'

app.config.errorHandler = (error, _instance, info) => {
  console.error(`[全局错误] ${info}`, error)
  toast.push(UNEXPECTED_ERROR_MESSAGE, { type: 'error' })
}

window.addEventListener('unhandledrejection', (event) => {
  console.error('[未处理的 Promise 拒绝]', event.reason)
  toast.push(UNEXPECTED_ERROR_MESSAGE, { type: 'error' })
})

app.mount('#app')
