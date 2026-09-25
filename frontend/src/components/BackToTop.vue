<script setup>
// 回到顶部（阶段 7 批 1，决策 BI）：全站挂载，滚动超过约一屏半后出现在右下角。
// 滚动监听同样走"事件登记一帧 + rAF 计算"的范式；系统开启"减少动态效果"时改为瞬时跳转。
// Teleport 到 body，让固定定位不受父级层叠上下文影响（同 ToastStack）。
import { onBeforeUnmount, onMounted, ref } from 'vue'

const APPEAR_RATIO = 1.5

const visible = ref(false)
let frame = 0

function measure() {
  frame = 0
  visible.value = window.scrollY > window.innerHeight * APPEAR_RATIO
}

function schedule() {
  if (frame) return
  frame = window.requestAnimationFrame(measure)
}

function toTop() {
  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  window.scrollTo({ top: 0, behavior: reduceMotion ? 'auto' : 'smooth' })
}

onMounted(() => {
  window.addEventListener('scroll', schedule, { passive: true })
  window.addEventListener('resize', schedule)
  measure()
})

onBeforeUnmount(() => {
  if (frame) window.cancelAnimationFrame(frame)
  window.removeEventListener('scroll', schedule)
  window.removeEventListener('resize', schedule)
})
</script>

<template>
  <Teleport to="body">
    <Transition name="to-top">
      <button
        v-if="visible"
        type="button"
        class="back-to-top"
        aria-label="回到顶部"
        @click="toTop"
      >
        <svg viewBox="0 0 20 20" width="18" height="18" aria-hidden="true" focusable="false">
          <path
            d="M10 15.5V5M5 10l5-5 5 5"
            fill="none"
            stroke="currentColor"
            stroke-width="1.8"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>
    </Transition>
  </Teleport>
</template>

<style scoped>
.back-to-top {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 30;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  padding: 0;
  color: var(--color-text);
  background-color: var(--color-bg-elevated);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-pill);
  box-shadow: var(--shadow-md);
  cursor: pointer;
  transition:
    var(--transition-theme),
    box-shadow var(--duration-fast) ease;
}

.back-to-top:hover {
  color: var(--color-accent);
  border-color: var(--color-accent);
}

/* 进出场：淡入 + 轻微上移；时长走令牌，prefers-reduced-motion 下自动归零 */
.to-top-enter-active,
.to-top-leave-active {
  transition:
    opacity var(--duration-base) var(--ease-out),
    transform var(--duration-base) var(--ease-out);
}

.to-top-enter-from,
.to-top-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
</style>
