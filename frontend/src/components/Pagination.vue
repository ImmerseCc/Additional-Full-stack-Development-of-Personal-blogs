<script setup>
// 分页控件（阶段 4 批 2，决策 S：阶段 4 先用分页控件，无限滚动留阶段 7）。
// 页码过多时收成"首页 … 当前±1 … 末页"；只有一页时整块不渲染。
import { computed } from 'vue'

const props = defineProps({
  page: { type: Number, required: true },
  totalPages: { type: Number, default: 0 }
})

const emit = defineEmits(['change'])

const MAX_FULL = 7

// 生成页码序列：数字表示页码，'gap' 表示省略号
const items = computed(() => {
  const total = props.totalPages
  if (total <= MAX_FULL) {
    return Array.from({ length: total }, (_, index) => index + 1)
  }

  const wanted = new Set([1, total, props.page - 1, props.page, props.page + 1])
  const pages = [...wanted].filter((value) => value >= 1 && value <= total).sort((a, b) => a - b)

  const result = []
  let previous = 0
  for (const value of pages) {
    if (previous && value - previous > 1) result.push('gap')
    result.push(value)
    previous = value
  }
  return result
})

function go(target) {
  if (target < 1 || target > props.totalPages || target === props.page) return
  emit('change', target)
}
</script>

<template>
  <nav v-if="totalPages > 1" class="pagination" aria-label="文章分页">
    <button
      type="button"
      class="pagination__button"
      :disabled="page <= 1"
      aria-label="上一页"
      @click="go(page - 1)"
    >
      上一页
    </button>

    <ul class="pagination__pages">
      <li v-for="(item, index) in items" :key="`${item}-${index}`">
        <span v-if="item === 'gap'" class="pagination__gap" aria-hidden="true">…</span>
        <button
          v-else
          type="button"
          class="pagination__button pagination__button--page"
          :class="{ 'is-current': item === page }"
          :aria-current="item === page ? 'page' : undefined"
          :aria-label="`第 ${item} 页`"
          @click="go(item)"
        >
          {{ item }}
        </button>
      </li>
    </ul>

    <button
      type="button"
      class="pagination__button"
      :disabled="page >= totalPages"
      aria-label="下一页"
      @click="go(page + 1)"
    >
      下一页
    </button>
  </nav>
</template>

<style scoped>
.pagination {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 28px;
}

.pagination__pages {
  display: flex;
  gap: 6px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.pagination__button {
  min-width: 38px;
  padding: 6px 12px;
  color: var(--color-text);
  background-color: var(--color-bg-elevated);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition:
    background-color var(--duration-fast) ease,
    border-color var(--duration-fast) ease,
    color var(--duration-fast) ease;
}

.pagination__button:hover:not(:disabled) {
  border-color: var(--color-border-strong);
  background-color: var(--color-bg-soft);
}

.pagination__button:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.pagination__button--page.is-current {
  color: var(--color-accent-contrast);
  background-color: var(--color-accent);
  border-color: var(--color-accent);
}

.pagination__gap {
  display: inline-block;
  min-width: 24px;
  padding: 6px 0;
  color: var(--color-muted);
  text-align: center;
}
</style>
