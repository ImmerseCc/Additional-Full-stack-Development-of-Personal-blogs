<script setup>
// Markdown 渲染组件（阶段 4 批 3）：把契约里的 Markdown 原文交给 utils/markdown.js，
// 得到已清洗、已高亮、标题带 id 的 HTML 后用 v-html 插入。
// 注意：v-html 生成的内容拿不到 scoped 的 data 属性，因此本组件用 :deep() 写正文样式。
import { computed, nextTick, onMounted, watch } from 'vue'
import { renderMarkdown } from '@/utils/markdown'

const props = defineProps({
  source: { type: String, default: '' }
})

// 渲染完成信号（阶段 9 验收后修订，报错记录 12）：
// 异步管线的"chunk 到位"与"正文挂载"不在同一个 tick，父级只等一个 nextTick 可能读到空的骨架，
// 于是首次打开时取不到标题、右侧目录永远不出现（不会自愈）。由本组件在内容真正进 DOM 后主动通知。
const emit = defineEmits(['rendered'])

const html = computed(() => renderMarkdown(props.source))

onMounted(() => emit('rendered'))
watch(
  () => props.source,
  () => nextTick(() => emit('rendered'))
)
</script>

<template>
  <div class="markdown" v-html="html"></div>
</template>

<style scoped>
.markdown {
  font-size: 16px;
  line-height: 1.8;
  overflow-wrap: break-word;
}

.markdown :deep(h2),
.markdown :deep(h3),
.markdown :deep(h4) {
  margin: 32px 0 12px;
  line-height: 1.35;
  scroll-margin-top: calc(var(--header-height) + 16px);
}

.markdown :deep(h2) {
  padding-bottom: 6px;
  font-size: 22px;
  border-bottom: 1px solid var(--color-border);
}

.markdown :deep(h3) {
  font-size: 18px;
}

.markdown :deep(h4) {
  font-size: 16px;
}

.markdown :deep(p) {
  margin: 14px 0;
}

.markdown :deep(ul),
.markdown :deep(ol) {
  margin: 14px 0;
  padding-left: 24px;
}

.markdown :deep(li) {
  margin: 6px 0;
}

.markdown :deep(blockquote) {
  margin: 16px 0;
  padding: 4px 16px;
  color: var(--color-muted);
  border-left: 3px solid var(--color-accent);
  background-color: var(--color-bg-soft);
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
}

.markdown :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: var(--radius-sm);
}

.markdown :deep(hr) {
  margin: 28px 0;
  border: 0;
  border-top: 1px solid var(--color-border);
}

.markdown :deep(table) {
  width: 100%;
  margin: 16px 0;
  border-collapse: collapse;
  font-size: 15px;
}

.markdown :deep(th),
.markdown :deep(td) {
  padding: 8px 12px;
  border: 1px solid var(--color-border);
  text-align: left;
}

.markdown :deep(th) {
  background-color: var(--color-bg-soft);
}

/* 行内代码：浅底 + 圆角，与代码块区分 */
.markdown :deep(code) {
  padding: 2px 6px;
  font-size: 0.92em;
  background-color: var(--color-bg-soft);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
}

.markdown :deep(pre) {
  margin: 18px 0;
  padding: 14px 16px;
  overflow-x: auto;
  background-color: var(--color-bg-soft);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
}

/* 代码块内部的 code 不再叠加行内样式 */
.markdown :deep(pre code) {
  padding: 0;
  font-size: 14px;
  line-height: 1.7;
  background: none;
  border: 0;
}

/* ---- 代码高亮配色（手写，决策 V）：语义色定义在 base.css 的 --hl-* 令牌里 ---- */
.markdown :deep(.hljs-comment),
.markdown :deep(.hljs-quote) {
  color: var(--hl-comment);
  font-style: italic;
}

.markdown :deep(.hljs-keyword),
.markdown :deep(.hljs-selector-tag),
.markdown :deep(.hljs-literal),
.markdown :deep(.hljs-doctag) {
  color: var(--hl-keyword);
}

.markdown :deep(.hljs-string),
.markdown :deep(.hljs-regexp),
.markdown :deep(.hljs-addition) {
  color: var(--hl-string);
}

.markdown :deep(.hljs-number),
.markdown :deep(.hljs-attr),
.markdown :deep(.hljs-attribute),
.markdown :deep(.hljs-selector-attr),
.markdown :deep(.hljs-selector-class),
.markdown :deep(.hljs-selector-id) {
  color: var(--hl-number);
}

.markdown :deep(.hljs-title),
.markdown :deep(.hljs-title.class_),
.markdown :deep(.hljs-title.function_),
.markdown :deep(.hljs-section) {
  color: var(--hl-function);
}

.markdown :deep(.hljs-type),
.markdown :deep(.hljs-built_in),
.markdown :deep(.hljs-symbol),
.markdown :deep(.hljs-bullet) {
  color: var(--hl-type);
}

.markdown :deep(.hljs-variable),
.markdown :deep(.hljs-template-variable),
.markdown :deep(.hljs-params),
.markdown :deep(.hljs-property) {
  color: var(--color-text);
}

.markdown :deep(.hljs-tag),
.markdown :deep(.hljs-name),
.markdown :deep(.hljs-selector-pseudo),
.markdown :deep(.hljs-meta) {
  color: var(--hl-meta);
}

.markdown :deep(.hljs-emphasis) {
  font-style: italic;
}

.markdown :deep(.hljs-strong) {
  font-weight: 600;
}
</style>
