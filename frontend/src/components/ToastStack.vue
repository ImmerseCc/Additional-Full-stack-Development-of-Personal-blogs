<script setup>
// Toast 容器（阶段 5 批 4）：Teleport 到 body，避免被 .app-header 的 backdrop-filter 之类
// 建立层叠上下文 / 包含块影响（页头与遮罩已用过 z-index 10 / 20，这里用 40 压在最上层）。
// aria-live="polite" 让屏幕阅读器播报新提示；每条也能手动关闭。
import { useToastStore } from '@/stores/toast'

const toast = useToastStore()
</script>

<template>
  <Teleport to="body">
    <TransitionGroup tag="ul" name="toast" class="toast-stack" aria-live="polite" aria-label="通知">
      <li v-for="item in toast.items" :key="item.id" class="toast" :class="`toast--${item.type}`">
        <span class="toast__message">{{ item.message }}</span>
        <button type="button" class="toast__close" aria-label="关闭通知" @click="toast.dismiss(item.id)">
          <svg viewBox="0 0 16 16" width="12" height="12" aria-hidden="true" focusable="false">
            <path d="M4 4l8 8M12 4l-8 8" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
          </svg>
        </button>
      </li>
    </TransitionGroup>
  </Teleport>
</template>

<style scoped>
.toast-stack {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 40;
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: min(320px, calc(100vw - 40px));
  margin: 0;
  padding: 0;
  list-style: none;
  pointer-events: none;
}

.toast {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 14px;
  color: var(--color-text);
  background-color: var(--color-bg-elevated);
  border: 1px solid var(--color-border);
  border-left: 3px solid var(--color-accent);
  border-radius: var(--radius-sm);
  box-shadow: var(--shadow-md);
  font-size: 14px;
  pointer-events: auto;
}

.toast--error {
  border-left-color: var(--color-like);
}

.toast--success {
  border-left-color: #2da44e;
}

/* 警告（阶段 9 批 2 新增，审计追加项）：需要注意但不构成失败的提示，如"已转为草稿" */
.toast--warning {
  border-left-color: #bf8700;
}

.toast__message {
  flex: 1;
  overflow-wrap: anywhere;
}

.toast__close {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  padding: 0;
  color: var(--color-muted);
  background-color: transparent;
  border: 1px solid transparent;
  border-radius: var(--radius-pill);
  cursor: pointer;
}

.toast__close:hover {
  color: var(--color-text);
  background-color: var(--color-bg-soft);
}

/* 进出场：位移 + 淡入淡出；动效时长走令牌，prefers-reduced-motion 下自动归零 */
.toast-enter-active,
.toast-leave-active {
  transition:
    opacity var(--duration-base) var(--ease-out),
    transform var(--duration-base) var(--ease-out);
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
</style>
