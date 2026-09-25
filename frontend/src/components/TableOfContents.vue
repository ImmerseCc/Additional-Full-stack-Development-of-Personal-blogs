<script setup>
// 文章目录（阶段 4 批 4，决策 Y）：内容来自详情页真实渲染出来的 h2 / h3（带 id，见 utils/markdown.js）。
// 版式由父级控制：桌面右侧固定栏，窄屏整块隐藏。
// 高亮态由父级传入的 activeId 驱动（scrollSpy），本组件只负责渲染与跳转。
const props = defineProps({
  headings: { type: Array, default: () => [] },
  activeId: { type: String, default: '' }
})

// 用原生锚点跳转 + 手动滚动：保留 <a href="#id"> 的可访问性与中键打开等行为，
// 同时让"减少动态效果"的用户直接跳过去而不是滚动。
function goTo(id) {
  const target = document.getElementById(id)
  if (!target) return
  const reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  target.scrollIntoView({ behavior: reduce ? 'auto' : 'smooth', block: 'start' })
}
</script>

<template>
  <nav v-if="headings.length" class="toc" aria-label="文章目录">
    <p class="toc__title">目录</p>
    <ol class="toc__list">
      <li
        v-for="heading in headings"
        :key="heading.id"
        class="toc__item"
        :class="[`toc__item--h${heading.level}`, { 'is-active': heading.id === activeId }]"
      >
        <a
          class="toc__link"
          :href="`#${heading.id}`"
          :aria-current="heading.id === activeId ? 'location' : undefined"
          @click.prevent="goTo(heading.id)"
        >
          {{ heading.text }}
        </a>
      </li>
    </ol>
  </nav>
</template>

<style scoped>
.toc {
  font-size: 14px;
}

.toc__title {
  margin: 0 0 10px;
  color: var(--color-muted);
  font-size: 13px;
  letter-spacing: 0.04em;
}

.toc__list {
  margin: 0;
  padding: 0;
  list-style: none;
  border-left: 1px solid var(--color-border);
}

.toc__item {
  margin: 0;
}

/* h3 缩进一级，形成层级感 */
.toc__item--h3 .toc__link {
  padding-left: 24px;
  font-size: 13px;
}

.toc__link {
  display: block;
  padding: 5px 0 5px 12px;
  color: var(--color-muted);
  line-height: 1.45;
  text-decoration: none;
  border-left: 2px solid transparent;
  margin-left: -1px;
  transition:
    color var(--duration-fast) ease,
    border-color var(--duration-fast) ease;
}

.toc__link:hover {
  color: var(--color-accent);
}

.toc__item.is-active .toc__link {
  color: var(--color-accent);
  border-left-color: var(--color-accent);
  font-weight: 600;
}
</style>
