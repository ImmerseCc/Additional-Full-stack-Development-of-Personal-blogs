<script setup>
// 关于（占位）：阶段 3 只放静态说明，不调用后端接口（决策 O）；前后端联调演练留阶段 6。
</script>

<template>
  <section class="page">
    <h1>关于本站</h1>
    <p>
      这是一个从零手写的个人博客全栈项目，用于学习与实战演练：<strong>未使用任何现成博客模板或整站主题</strong>，
      所有页面、组件与接口均为自行实现。
    </p>
    <ul>
      <li>前端：Vue 3（Composition API + <code>&lt;script setup&gt;</code>）+ Vite + Vue Router + Pinia，纯 JavaScript，原生 CSS 变量主题</li>
      <li>后端：Spring Boot 4.1.1 + JdbcTemplate + SQLite，统一返回体 <code>{ code, message, data }</code></li>
      <li>接口契约与数据模型：见仓库 <code>docs/api-contract.md</code>、<code>docs/data-model.md</code></li>
    </ul>
    <p class="note">本页阶段 3 暂不调用后端接口，接口联调在阶段 6 进行。</p>
  </section>
</template>

<style scoped>
.page p {
  max-width: 68ch;
}

.page ul {
  max-width: 68ch;
  padding-left: 20px;
  color: var(--color-muted);
}

.note {
  margin-top: 16px;
  padding: 12px 16px;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-card);
  color: var(--color-accent);
}
</style>
