<script setup>
// 标签筛选（阶段 5 批 2，模块四）：多选 chips + and / or 组合语义切换。
// 桌面直接铺开（当前 8 个标签够用）；窄屏折叠为「展开 / 收起」面板，避免挤压列表（决策 4）。
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const props = defineProps({
  tags: { type: Array, default: () => [] },
  selected: { type: Array, default: () => [] },
  mode: { type: String, default: 'and' }
})

const emit = defineEmits(['update:selected', 'update:mode'])

const NARROW_QUERY = '(max-width: 767px)'

const expanded = ref(false)
const isNarrow = ref(false)
let media = null

function onMediaChange(event) {
  isNarrow.value = event.matches
  if (!event.matches) {
    expanded.value = false
  }
}

onMounted(() => {
  media = window.matchMedia(NARROW_QUERY)
  isNarrow.value = media.matches
  media.addEventListener('change', onMediaChange)
})

onBeforeUnmount(() => {
  if (media) {
    media.removeEventListener('change', onMediaChange)
  }
})

// 窄屏且未展开时收起面板；宽屏始终铺开
const collapsed = computed(() => isNarrow.value && !expanded.value)
const summary = computed(() =>
  props.selected.length > 0 ? `标签筛选（已选 ${props.selected.length} 个）` : '标签筛选'
)

function toggle(name) {
  const next = props.selected.includes(name)
    ? props.selected.filter((item) => item !== name)
    : [...props.selected, name]
  emit('update:selected', next)
}
</script>

<template>
  <div class="tag-filter">
    <div class="tag-filter__head">
      <h2 class="tag-filter__title">{{ summary }}</h2>
      <div class="tag-filter__actions">
        <button v-if="selected.length > 0" type="button" class="tag-filter__ghost" @click="emit('update:selected', [])">
          清除标签
        </button>
        <button
          v-if="isNarrow"
          type="button"
          class="tag-filter__ghost"
          :aria-expanded="expanded ? 'true' : 'false'"
          aria-controls="tag-filter-panel"
          @click="expanded = !expanded"
        >
          {{ expanded ? '收起' : '展开' }}
        </button>
      </div>
    </div>

    <div v-show="!collapsed" id="tag-filter-panel" class="tag-filter__body">
      <ul class="tag-filter__list">
        <li v-for="tag in tags" :key="tag.id">
          <button
            type="button"
            class="chip"
            :aria-pressed="selected.includes(tag.name) ? 'true' : 'false'"
            @click="toggle(tag.name)"
          >
            {{ tag.name }}
            <span class="chip__count">{{ tag.articleCount }}</span>
          </button>
        </li>
      </ul>

      <div v-if="selected.length > 1" class="tag-filter__mode" role="group" aria-label="多标签组合方式">
        <span>组合方式</span>
        <button
          type="button"
          class="tag-filter__mode-button"
          :aria-pressed="mode === 'and' ? 'true' : 'false'"
          @click="emit('update:mode', 'and')"
        >
          同时包含
        </button>
        <button
          type="button"
          class="tag-filter__mode-button"
          :aria-pressed="mode === 'or' ? 'true' : 'false'"
          @click="emit('update:mode', 'or')"
        >
          任一即可
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tag-filter {
  margin-bottom: 24px;
  padding: 16px 18px;
  background-color: var(--color-bg-soft);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
}

.tag-filter__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.tag-filter__title {
  margin: 0;
  color: var(--color-muted);
  font-size: 14px;
  font-weight: 600;
}

.tag-filter__actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tag-filter__ghost {
  padding: 4px 10px;
  color: var(--color-accent);
  background-color: transparent;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-pill);
  font-size: 13px;
  cursor: pointer;
  transition: border-color var(--duration-fast) ease;
}

.tag-filter__ghost:hover {
  border-color: var(--color-accent);
}

.tag-filter__body {
  margin-top: 12px;
}

.tag-filter__list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.tag-filter__mode {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  color: var(--color-muted);
  font-size: 13px;
}

.tag-filter__mode-button {
  padding: 3px 12px;
  color: var(--color-muted);
  background-color: var(--color-bg-elevated);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-pill);
  font-size: 13px;
  cursor: pointer;
  transition:
    color var(--duration-fast) ease,
    border-color var(--duration-fast) ease,
    background-color var(--duration-fast) ease;
}

.tag-filter__mode-button[aria-pressed='true'] {
  color: var(--color-accent);
  background-color: var(--color-accent-soft);
  border-color: var(--color-accent);
}
</style>
