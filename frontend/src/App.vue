<script setup>
// 根组件（占位）：阶段 3 起在这里挂载全局导航栏、主题切换按钮、页脚与 Toast 容器
</script>

<template>
  <div class="app-shell">
    <RouterView />
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
}
</style>
