<script setup>
// 骨架屏卡片（阶段 4 批 2，批 3 起改用 SkeletonBlock 原语）：与 ArticleCard 同构，仅用于列表加载态。
// 纯装饰，对屏幕阅读器隐藏（列表容器用 aria-busy 播报加载状态）。
import SkeletonBlock from './SkeletonBlock.vue'
</script>

<template>
  <div class="skeleton" aria-hidden="true">
    <SkeletonBlock aspect="16 / 9" radius="0" />
    <div class="skeleton__body">
      <SkeletonBlock height="18px" width="80%" />
      <SkeletonBlock />
      <SkeletonBlock width="60%" />
      <div class="skeleton__foot">
        <SkeletonBlock height="12px" width="72px" />
        <SkeletonBlock height="12px" width="72px" />
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

.skeleton__body {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px 18px 18px;
}

.skeleton__foot {
  display: flex;
  justify-content: space-between;
  margin-top: 4px;
}
</style>
