<script setup>
// 页脚（阶段 3 批 4 从 App.vue 拆出）：
// 站点说明 + 数据来源提示（点赞与评论以后端为唯一事实来源，本地只存偏好）。
</script>

<template>
  <footer class="app-footer">
    <p class="app-footer__line">个人博客 · 手写 Vue 3 + Spring Boot（学习项目，未使用任何现成模板）</p>
    <p class="app-footer__note">文章与评论数据来自后端 API，主题与访客标识存于浏览器本地。</p>
  </footer>
</template>

<style scoped>
.app-footer {
  padding: 20px;
  border-top: 1px solid var(--color-border);
  color: var(--color-muted);
  font-size: 0.9rem;
  text-align: center;
  transition: var(--transition-theme);
}

.app-footer__line,
.app-footer__note {
  margin: 0;
}

.app-footer__note {
  margin-top: 6px;
  font-size: 0.82rem;
  opacity: 0.85;
}
</style>
