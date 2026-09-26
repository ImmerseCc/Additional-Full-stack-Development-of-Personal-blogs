<script setup>
// 正文管线加载中的骨架（阶段 9 批 2，闭环遗留 32）：原先是 <Suspense #fallback> 里的模板片段，
// 改为异步组件的 loadingComponent —— 与 errorComponent 走同一套机制。
// 起因：演练实测 <Suspense> 会吞掉加载期异常（正文区静默空白），故不再用它兜加载态。
import SkeletonBlock from '@/components/SkeletonBlock.vue'
</script>

<template>
  <div class="md-skeleton" aria-busy="true" aria-label="正文加载中">
    <SkeletonBlock height="14px" width="92%" />
    <SkeletonBlock height="14px" width="86%" />
    <SkeletonBlock height="14px" width="90%" />
    <SkeletonBlock height="14px" width="58%" />
    <SkeletonBlock height="150px" radius="var(--radius-card)" />
  </div>
</template>

<style scoped>
.md-skeleton {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
</style>
