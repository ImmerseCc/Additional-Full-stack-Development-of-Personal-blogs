<script setup>
// 文章详情（占位）：阶段 4 批 3 在此实现 Markdown 渲染、代码高亮与阅读进度条。
// 批 2 先建这个占位页，是为了让列表卡片的链接有落点，而不是掉进 404。
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <section class="page">
    <h1>文章详情</h1>
    <p class="note">占位（阶段 4 批 3 实现）：路由参数 id = {{ route.params.id }}。</p>
    <p class="note">批 3 将在这里渲染 Markdown 正文、代码高亮、图片懒加载与阅读进度条。</p>
    <p><RouterLink to="/articles">← 返回文章列表</RouterLink></p>
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
