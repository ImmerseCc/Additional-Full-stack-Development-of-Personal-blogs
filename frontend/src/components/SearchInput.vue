<script setup>
// 搜索框（阶段 5 批 2，模块四）：输入即过滤 + 防抖；清空与回车立即生效（不等防抖）。
// 只负责"输入 → 抛出值"，筛选语义与地址栏同步交给列表页。
import { onBeforeUnmount, ref, watch } from 'vue'
import { debounce } from '@/utils/debounce'

const props = defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: '搜索文章标题' },
  wait: { type: Number, default: 300 }
})

const emit = defineEmits(['update:modelValue'])

const text = ref(props.modelValue)
const inputId = `search-${Math.random().toString(36).slice(2, 8)}`

const commit = debounce((value) => emit('update:modelValue', value), props.wait)

// 外部改动（浏览器前进 / 后退、"清除筛选"、点卡片标签）同步进输入框；
// 比较时用 trim 后的值，避免用户正在输入的首尾空白被外部同步吃掉。
watch(
  () => props.modelValue,
  (value) => {
    if (value !== text.value.trim()) {
      text.value = value
    }
  }
)

function onInput(event) {
  text.value = event.target.value
  commit(text.value.trim())
}

function flush() {
  commit.cancel()
  emit('update:modelValue', text.value.trim())
}

function clear() {
  commit.cancel()
  text.value = ''
  emit('update:modelValue', '')
}

onBeforeUnmount(() => commit.cancel())
</script>

<template>
  <div class="search">
    <label class="visually-hidden" :for="inputId">按标题搜索文章</label>
    <input
      :id="inputId"
      class="input search__input"
      type="search"
      :value="text"
      :placeholder="placeholder"
      autocomplete="off"
      spellcheck="false"
      enterkeyhint="search"
      @input="onInput"
      @keydown.enter.prevent="flush"
    />
    <button v-if="text" type="button" class="search__clear" aria-label="清除搜索词" @click="clear">
      <svg viewBox="0 0 16 16" width="14" height="14" aria-hidden="true" focusable="false">
        <path
          d="M4 4l8 8M12 4l-8 8"
          fill="none"
          stroke="currentColor"
          stroke-width="1.8"
          stroke-linecap="round"
        />
      </svg>
    </button>
  </div>
</template>

<style scoped>
.search {
  position: relative;
}

/* 给自绘的清空按钮留出位置 */
.search__input {
  padding-right: 40px;
  border-radius: var(--radius-pill);
}

/* 关掉浏览器自带的搜索清除按钮，避免出现两个"×" */
.search__input::-webkit-search-cancel-button {
  display: none;
}

.search__clear {
  position: absolute;
  top: 50%;
  right: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  padding: 0;
  color: var(--color-muted);
  background-color: transparent;
  border: 1px solid transparent;
  border-radius: var(--radius-pill);
  cursor: pointer;
  transform: translateY(-50%);
  transition:
    color var(--duration-fast) ease,
    background-color var(--duration-fast) ease;
}

.search__clear:hover {
  color: var(--color-text);
  background-color: var(--color-bg-soft);
}
</style>
