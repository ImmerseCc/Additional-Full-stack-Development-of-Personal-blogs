<script setup>
import AppHeader from '@/components/AppHeader.vue'
import { useThemeStore } from '@/stores/theme'

// 根组件：页头（导航 + 主题按钮）+ 路由内容 + 页脚。
// 路由过渡动画与页脚组件拆分在批 4 处理。
const theme = useThemeStore()
theme.init()
</script>

<template>
  <div class="app-shell">
    <AppHeader />
    <main class="app-main">
      <RouterView />
    </main>
    <footer class="app-footer">
      <p>个人博客 · 手写 Vue 3 + Spring Boot（学习项目，未使用任何现成模板）</p>
    </footer>
  </div>
</template>

<style scoped>
.app-shell {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-main {
  flex: 1;
}

.app-footer {
  padding: 20px;
  border-top: 1px solid var(--color-border);
  color: var(--color-muted);
  font-size: 0.9rem;
  text-align: center;
  transition: var(--transition-theme);
}

.app-footer p {
  margin: 0;
}
</style>
