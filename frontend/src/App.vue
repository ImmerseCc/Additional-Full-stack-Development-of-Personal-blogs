<script setup>
import BackToTop from '@/components/BackToTop.vue'
import AppFooter from '@/components/AppFooter.vue'
import AppHeader from '@/components/AppHeader.vue'
import ToastStack from '@/components/ToastStack.vue'
import { useThemeStore } from '@/stores/theme'

// 根组件：页头（导航 + 主题按钮）+ 路由内容（带过渡）+ 页脚 + 全局 Toast（阶段 5 批 4）
// + 回到顶部按钮（阶段 7 批 1，全站可用）。
// 路由过渡类 .page-* 定义在 src/styles/base.css，动效时长走 CSS 变量，
// 因此 prefers-reduced-motion 下自动禁用（变量被归零）。
const theme = useThemeStore()
theme.init()
</script>

<template>
  <div class="app-shell">
    <AppHeader />
    <main class="app-main">
      <RouterView v-slot="{ Component, route }">
        <Transition name="page" mode="out-in">
          <component :is="Component" :key="route.path" />
        </Transition>
      </RouterView>
    </main>
    <AppFooter />
    <ToastStack />
    <BackToTop />
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
</style>
