<script setup>
// 骨架块原语（阶段 4 批 3）：把"流光"这一段样式收敛到一处，
// ArticleSkeleton（列表卡片）与详情页加载态都复用它，避免同一段动画 CSS 写两遍。
// 纯装饰：对屏幕阅读器隐藏。
// 动效时长走 CSS 变量，base.css 的 prefers-reduced-motion 规则会把动画时长归零。
import { computed } from 'vue'

const props = defineProps({
  // 固定高度；若给了 aspect 则忽略 height
  height: { type: String, default: '14px' },
  width: { type: String, default: '100%' },
  // 用宽高比撑开高度，例如 '16 / 9'
  aspect: { type: String, default: '' },
  radius: { type: String, default: 'var(--radius-sm)' }
})

const style = computed(() => ({
  width: props.width,
  height: props.aspect ? 'auto' : props.height,
  aspectRatio: props.aspect || undefined,
  borderRadius: props.radius
}))
</script>

<template>
  <div class="skeleton-block" :style="style" aria-hidden="true"></div>
</template>

<style scoped>
.skeleton-block {
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
