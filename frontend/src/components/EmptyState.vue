<script setup>
// 空结果状态（阶段 5 批 2，模块四）：插画 + 文案 + 可选操作按钮，列表页与后续评论区共用。
// 插画是内联 SVG（不引外部图片），颜色全部走主题令牌，亮 / 暗两套自动跟随；
// 入场动画只用 CSS，prefers-reduced-motion 下由 base.css 的全局规则自动失效。
defineProps({
  title: { type: String, default: '没有找到内容' },
  description: { type: String, default: '' },
  actionText: { type: String, default: '' }
})

const emit = defineEmits(['action'])
</script>

<template>
  <div class="empty">
    <svg class="empty__art" viewBox="0 0 120 96" aria-hidden="true" focusable="false">
      <rect class="empty__paper" x="24" y="12" width="68" height="60" rx="8" />
      <path class="empty__line" d="M38 30h40M38 42h30M38 54h20" />
      <circle class="empty__lens" cx="76" cy="64" r="17" />
      <path class="empty__handle" d="M88 76l12 12" />
    </svg>
    <p class="empty__title">{{ title }}</p>
    <p v-if="description" class="empty__description">{{ description }}</p>
    <button v-if="actionText" type="button" class="btn empty__action" @click="emit('action')">
      {{ actionText }}
    </button>
  </div>
</template>

<style scoped>
.empty {
  padding: 40px 24px;
  color: var(--color-muted);
  text-align: center;
  background-color: var(--color-bg-soft);
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-card);
  animation: empty-in var(--duration-slow) var(--ease-out) both;
}

.empty__art {
  width: 120px;
  height: 96px;
  margin-bottom: 4px;
}

.empty__paper {
  fill: var(--color-bg-elevated);
  stroke: var(--color-border-strong);
  stroke-width: 2;
}

.empty__line {
  fill: none;
  stroke: var(--color-border-strong);
  stroke-width: 3;
  stroke-linecap: round;
}

.empty__lens {
  fill: var(--color-accent-soft);
  stroke: var(--color-accent);
  stroke-width: 2.5;
}

.empty__handle {
  fill: none;
  stroke: var(--color-accent);
  stroke-width: 3.5;
  stroke-linecap: round;
}

.empty__title {
  margin: 0 0 6px;
  color: var(--color-text);
  font-size: 16px;
}

.empty__description {
  margin: 0 0 16px;
  font-size: 14px;
}

.empty__description:last-child {
  margin-bottom: 0;
}

@keyframes empty-in {
  from {
    opacity: 0;
    transform: translateY(8px);
  }

  to {
    opacity: 1;
    transform: none;
  }
}
</style>
