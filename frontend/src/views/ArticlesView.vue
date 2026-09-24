<script setup>
// 文章列表（占位）：阶段 4 实现卡片布局、分页 / 无限滚动、悬停动效与骨架屏，
// 数据来自契约里的 GET /api/articles；预计与首页共用同一列表组件。
</script>

<template>
  <section class="page">
    <h1>文章列表</h1>
    <p class="note">待实现（阶段 4）：卡片布局、分页 / 无限滚动、悬停动效、骨架屏与空结果提示。</p>
  </section>
</template>

<style scoped>
.note {
  margin-top: 12px;
  padding: 12px 16px;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-card);
  color: var(--color-accent);
}
</style>
