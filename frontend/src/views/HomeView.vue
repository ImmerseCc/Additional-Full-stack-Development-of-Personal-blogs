<script setup>
// 首页（占位）：阶段 4 在这里实现文章卡片列表、分页/无限滚动、骨架屏与悬停动效
</script>

<template>
  <main class="placeholder">
    <h1>个人博客</h1>
    <p>前端骨架已就绪：Vue 3 + Vite + Vue Router + Pinia（纯 JavaScript）。</p>
    <ul class="checklist">
      <li>开发服务器：http://localhost:5173</li>
      <li>接口代理：/api → http://localhost:8080（见 vite.config.js）</li>
      <li>主题变量：src/styles/base.css（亮/暗两套 CSS 变量）</li>
    </ul>
    <p class="todo">文章列表待实现（阶段 4）</p>
  </main>
</template>

<style scoped>
.placeholder {
  max-width: 720px;
  margin: 0 auto;
  padding: 48px 20px;
}

.placeholder h1 {
  margin: 0 0 8px;
}

.placeholder p {
  color: var(--color-muted);
}

.checklist {
  padding-left: 20px;
  color: var(--color-muted);
}

.todo {
  margin-top: 24px;
  padding: 12px 16px;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-card);
  color: var(--color-accent);
}
</style>
