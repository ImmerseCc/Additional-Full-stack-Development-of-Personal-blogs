<script setup>
// Markdown 管线加载失败兜底（阶段 9 批 2，闭环遗留 32）。
// 触发时机：动态 import 失败（离线、发布后资源被替换、浏览器缓存到旧版 chunk）。
// 说明：Vue 3.5 的 defineAsyncComponent 只把 error 传给 errorComponent（retry / fail / attempts
// 已移除，见 node_modules/@vue/runtime-core …… AsyncComponentWrapper 的实现），
// 所以这里不做"原地重新 import"（那条路在模块图里已被标记为失败），
// 直接给最可靠的动作：刷新页面 —— 重新取 index.html 与新的 chunk。
defineProps({
  error: { type: Error, default: null }
})

function refresh() {
  window.location.reload()
}
</script>

<template>
  <div class="md-error" role="alert">
    <p class="md-error__title">正文渲染模块加载失败</p>
    <p class="md-error__hint">通常是网络中断，或浏览器缓存了旧版本资源。刷新后可重新加载。</p>
    <button type="button" class="btn btn--ghost" @click="refresh">刷新页面</button>
  </div>
</template>

<style scoped>
.md-error {
  padding: 22px 20px;
  color: var(--color-text);
  background-color: var(--color-bg-soft);
  border: 1px dashed var(--color-border-strong);
  border-radius: var(--radius-card);
  text-align: center;
}

.md-error__title {
  margin: 0 0 6px;
  font-weight: 600;
}

.md-error__hint {
  margin: 0 0 14px;
  color: var(--color-muted);
  font-size: 13px;
}
</style>
