<script setup>
import { computed } from 'vue'
import { THEME_LABELS, useThemeStore } from '@/stores/theme'

// 三态循环按钮：亮 → 暗 → 跟随系统。
// 阶段 3 批 2 会把它移进全局导航栏右侧（决策 N），本批先由 App.vue 临时挂载。
const theme = useThemeStore()

const ariaLabel = computed(
  () => `主题：${THEME_LABELS[theme.mode]}（点击切换为${THEME_LABELS[theme.nextMode]}）`
)
</script>

<template>
  <button
    type="button"
    class="theme-toggle"
    :aria-label="ariaLabel"
    :title="ariaLabel"
    @click="theme.cycleMode()"
  >
    <svg
      v-if="theme.mode === 'light'"
      class="theme-toggle__icon"
      viewBox="0 0 20 20"
      aria-hidden="true"
    >
      <circle cx="10" cy="10" r="4" fill="none" stroke="currentColor" stroke-width="1.6" />
      <path
        d="M10 1.5v2M10 16.5v2M1.5 10h2M16.5 10h2M4 4l1.4 1.4M14.6 14.6L16 16M16 4l-1.4 1.4M5.4 14.6L4 16"
        fill="none"
        stroke="currentColor"
        stroke-width="1.6"
        stroke-linecap="round"
      />
    </svg>
    <svg
      v-else-if="theme.mode === 'dark'"
      class="theme-toggle__icon"
      viewBox="0 0 20 20"
      aria-hidden="true"
    >
      <path
        d="M16 12.2A6.8 6.8 0 0 1 7.8 4a6.8 6.8 0 1 0 8.2 8.2Z"
        fill="none"
        stroke="currentColor"
        stroke-width="1.6"
        stroke-linejoin="round"
      />
    </svg>
    <svg v-else class="theme-toggle__icon" viewBox="0 0 20 20" aria-hidden="true">
      <rect
        x="2.5"
        y="3.5"
        width="15"
        height="10"
        rx="1.5"
        fill="none"
        stroke="currentColor"
        stroke-width="1.6"
      />
      <path
        d="M7 16.5h6"
        fill="none"
        stroke="currentColor"
        stroke-width="1.6"
        stroke-linecap="round"
      />
    </svg>
    <span class="visually-hidden" aria-live="polite">{{ THEME_LABELS[theme.mode] }}</span>
  </button>
</template>

<style scoped>
.theme-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  padding: 0;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-pill);
  background-color: var(--color-bg-soft);
  color: var(--color-text);
  cursor: pointer;
  transition:
    background-color var(--duration-fast) ease,
    border-color var(--duration-fast) ease,
    color var(--duration-fast) ease;
}

.theme-toggle:hover {
  border-color: var(--color-accent);
  color: var(--color-accent);
}

.theme-toggle__icon {
  width: 18px;
  height: 18px;
}
</style>
