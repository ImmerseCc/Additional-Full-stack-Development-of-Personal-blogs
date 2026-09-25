<script setup>
// 文章卡片（阶段 4 批 2 建；阶段 5 批 2 让标签可点）：列表页与首页共用。
// 整卡可点：标题链接用 ::after 覆盖整卡（点击范围大）；标签链接用 z-index 抬到覆盖层之上，
// 点标签 = 跳到 /articles?tags=标签名 做过滤（模块四）。
import { formatDate } from '@/utils/date'

defineProps({
  article: { type: Object, required: true }
})

const detailPath = (article) => `/articles/${article.id}`
const filterByTag = (tag) => ({ path: '/articles', query: { tags: tag } })
</script>

<template>
  <article class="card">
    <div class="card__cover">
      <img v-if="article.coverUrl" :src="article.coverUrl" alt="" loading="lazy" decoding="async" />
      <div v-else class="card__cover-fallback" aria-hidden="true"></div>
    </div>

    <div class="card__body">
      <h3 class="card__title">
        <RouterLink :to="detailPath(article)">{{ article.title }}</RouterLink>
      </h3>

      <p v-if="article.summary" class="card__summary">{{ article.summary }}</p>

      <ul v-if="article.tags && article.tags.length" class="card__tags">
        <li v-for="tag in article.tags" :key="tag">
          <RouterLink class="card__tag" :to="filterByTag(tag)" :aria-label="`按标签「${tag}」筛选文章`">
            {{ tag }}
          </RouterLink>
        </li>
      </ul>

      <div class="card__meta">
        <time :datetime="article.createdAt">{{ formatDate(article.createdAt) }}</time>
        <span class="card__stats">
          <span>点赞 {{ article.likeCount }}</span>
          <span>评论 {{ article.commentCount }}</span>
        </span>
      </div>
    </div>
  </article>
</template>

<style scoped>
.card {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  background-color: var(--color-bg-elevated);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-sm);
  transition:
    transform var(--duration-base) var(--ease-out),
    box-shadow var(--duration-base) var(--ease-out),
    border-color var(--duration-base) var(--ease-out);
}

/* 悬停只在支持悬停的设备上生效，避免触屏"悬停残留" */
@media (hover: hover) {
  .card:hover {
    transform: translateY(-3px);
    border-color: var(--color-border-strong);
    box-shadow: var(--shadow-md);
  }

  .card:hover .card__cover img {
    transform: scale(1.04);
  }
}

/* 键盘走到标题链接时，同样给出整卡的视觉反馈 */
.card:focus-within {
  border-color: var(--color-accent);
  box-shadow: var(--shadow-md);
}

.card__cover {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  background-color: var(--color-bg-soft);
}

.card__cover img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--duration-slow) var(--ease-out);
}

/* 契约允许 coverUrl 为空：用主题色渐变占位，不依赖任何外链图片 */
.card__cover-fallback {
  width: 100%;
  height: 100%;
  background-image: linear-gradient(135deg, var(--color-accent-soft), var(--color-bg-soft) 70%);
}

.card__body {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 10px;
  padding: 16px 18px 18px;
}

.card__title {
  margin: 0;
  font-size: 18px;
  line-height: 1.4;
}

.card__title a {
  color: var(--color-text);
  text-decoration: none;
}

/* 覆盖整卡的可点区域：inset 相对最近的定位祖先（.card）计算 */
.card__title a::after {
  content: '';
  position: absolute;
  inset: 0;
}

.card__title a:hover {
  color: var(--color-accent);
}

.card__summary {
  flex: 1;
  margin: 0;
  color: var(--color-muted);
  font-size: 14px;
}

.card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 0;
  padding: 0;
  list-style: none;
}

/* 标签链接要浮在标题链接的整卡覆盖层（::after 的 inset:0）之上，否则点不到（阶段 5 批 2） */
.card__tag {
  position: relative;
  z-index: 1;
  display: inline-flex;
  padding: 2px 10px;
  color: var(--color-accent);
  background-color: var(--color-accent-soft);
  border: 1px solid transparent;
  border-radius: var(--radius-pill);
  font-size: 12px;
  text-decoration: none;
  transition:
    color var(--duration-fast) ease,
    background-color var(--duration-fast) ease,
    border-color var(--duration-fast) ease;
}

.card__tag:hover {
  color: var(--color-accent-contrast);
  background-color: var(--color-accent);
  border-color: var(--color-accent);
}

.card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: var(--color-muted);
  font-size: 13px;
}

.card__stats {
  display: flex;
  gap: 12px;
}
</style>
