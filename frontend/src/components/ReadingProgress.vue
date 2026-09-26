<script setup>
// 阅读进度条（阶段 7 批 1，决策 BJ）：贴在页头下沿的 2px 细条。
// 进度 = 页面滚动比例（scrollY / 可滚动高度），滚到页面底部为 100%；页面不可滚动时恒为 0。
// 这里刻意不以"正文元素"为准：种子文章里有正文高度小于视口的（如第 1 篇），
// 那种算法会让进度条在几十像素内从 0 跳到 100%，观感更像故障（批 1 实测发现后改为此口径）。
// 填充由 requestAnimationFrame 逐帧更新（滚动登记与 rAF 合并由 utils/scrollFrame.js 统一承担，
// 阶段 9 批 2 审计 9-1），因此不使用 CSS 过渡 —— prefers-reduced-motion 下表现完全一致。
// Teleport 到 body：路由过渡与页头的 backdrop-filter 会建立包含块 / 层叠上下文（同 ToastStack 的处理）。
import { computed, ref } from 'vue'
import { useScrollFrame } from '@/utils/scrollFrame'

const ratio = ref(0)
const percent = computed(() => Math.round(ratio.value * 100))

function measure() {
  const scrollable = document.documentElement.scrollHeight - window.innerHeight
  const value = scrollable > 0 ? window.scrollY / scrollable : 0
  ratio.value = Math.min(1, Math.max(0, value))
}

useScrollFrame(measure)
</script>

<template>
  <Teleport to="body">
    <div
      class="reading-progress"
      role="progressbar"
      aria-label="阅读进度"
      aria-valuemin="0"
      aria-valuemax="100"
      :aria-valuenow="percent"
    >
      <span class="reading-progress__bar" :style="{ transform: `scaleX(${ratio})` }"></span>
    </div>
  </Teleport>
</template>

<style scoped>
.reading-progress {
  position: fixed;
  top: var(--header-height);
  right: 0;
  left: 0;
  z-index: 21;
  height: 2px;
  pointer-events: none;
}

.reading-progress__bar {
  display: block;
  height: 100%;
  background-color: var(--color-accent);
  transform: scaleX(0);
  transform-origin: left center;
}
</style>
