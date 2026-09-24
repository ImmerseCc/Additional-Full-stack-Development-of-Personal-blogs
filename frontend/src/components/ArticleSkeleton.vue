<script setup>
// 骨架屏卡片（阶段 4 批 2）：与 ArticleCard 同构，仅用于加载态占位。
// 纯装饰，对屏幕阅读器隐藏（列表容器用 aria-busy 播报加载状态）。
// 动效时长走 CSS 变量 + base.css 的 prefers-reduced-motion 全局规则，减少动态效果时自动失效。
</script>

<template>
  <div class="skeleton" aria-hidden="true">
    <div class="skeleton__cover"></div>
    <div class="skeleton__body">
      <div class="skeleton__line skeleton__line--title"></div>
      <div class="skeleton__line"></div>
      <div class="skeleton__line skeleton__line--short"></div>
      <div class="skeleton__foot">
        <div class="skeleton__line skeleton__line--meta"></div>
        <div class="skeleton__line skeleton__line--meta"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.skeleton {
  height: 100%;
  overflow: hidden;
  background-color: var(--color-bg-elevated);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
}

.skeleton__cover {
  aspect-ratio: 16 / 9;
}

.skeleton__body {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px 18px 18px;
}

.skeleton__line {
  height: 12px;
  border-radius: var(--radius-sm);
}

.skeleton__line--title {
  height: 18px;
  width: 80%;
}

.skeleton__line--short {
  width: 60%;
}

.skeleton__line--meta {
  width: 72px;
  height: 12px;
}

.skeleton__foot {
  display: flex;
  justify-content: space-between;
  margin-top: 4px;
}

/* 流光：从右往左扫过，两种主题下都用同一组语义色，故无需分支 */
.skeleton__cover,
.skeleton__line {
  background-image: linear-gradient(
    90deg,
    var(--color-bg-soft) 0%,
    var(--color-accent-soft) 50%,
    var(--color-bg-soft) 100%
  );
  background-size: 200% 100%;
  animation: skeleton-shimmer 1.4s var(--ease-out) infinite;
}

@keyframes skeleton-shimmer {
  from {
    background-position: 200% 0;
  }

  to {
    background-position: -200% 0;
  }
}
</style>
