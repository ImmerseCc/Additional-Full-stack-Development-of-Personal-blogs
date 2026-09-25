import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { revealDirective } from './utils/reveal'
import './styles/base.css'

const app = createApp(App)

// 全局指令：滚动进场动画（实现见 utils/reveal.js）
app.directive('reveal', revealDirective)

app.use(createPinia())
app.use(router)
app.mount('#app')
