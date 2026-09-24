<script setup>
import ThemeToggle from '@/components/ThemeToggle.vue'
import { useThemeStore } from '@/stores/theme'

// 根组件：批 2 会在这里挂载全局导航栏与页脚。
// 本批先把主题按钮临时放在右上角（决策 N 指定它最终位置是导航栏右侧），并启动主题 store 的监听。
const theme = useThemeStore()
theme.init()
</script>

<template>
  <div class="app-shell">
    <div class="theme-probe">
      <ThemeToggle />
    </div>
    <RouterView />
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
}

.theme-probe {
  display: flex;
  justify-content: flex-end;
  padding: 12px 20px;
}
</style>
