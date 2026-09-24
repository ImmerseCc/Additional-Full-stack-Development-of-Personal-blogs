<script setup>
import { useRoute } from 'vue-router'

// 404 页（决策 L）：替换原先"未知路径一律重定向首页"的兜底，明确告知并给出回首页入口。
const route = useRoute()
</script>

<template>
  <section class="page not-found">
    <p class="not-found__code">404</p>
    <h1>页面不存在</h1>
    <p class="not-found__hint">
      没有找到 <code>{{ route.fullPath }}</code> 对应的页面，可能是链接已过期或地址输入有误。
    </p>
    <RouterLink class="not-found__link" to="/">返回首页</RouterLink>
  </section>
</template>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}

.not-found__code {
  margin: 0;
  font-size: 3.5rem;
  font-weight: 700;
  line-height: 1;
  color: var(--color-accent);
}

.not-found h1 {
  margin: 8px 0 0;
}

.not-found__hint {
  max-width: 60ch;
  color: var(--color-muted);
}

.not-found__hint code {
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  background-color: var(--color-bg-soft);
  border: 1px solid var(--color-border);
}

.not-found__link {
  margin-top: 12px;
  padding: 8px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-pill);
  background-color: var(--color-bg-soft);
  color: var(--color-accent);
  text-decoration: none;
  transition:
    background-color var(--duration-fast) ease,
    border-color var(--duration-fast) ease;
}

.not-found__link:hover {
  border-color: var(--color-accent);
}
</style>
