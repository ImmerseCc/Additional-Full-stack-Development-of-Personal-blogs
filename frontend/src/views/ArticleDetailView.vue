<script setup>
// 文章详情（阶段 4 批 3）：真实调用 GET /api/articles/{id}，把 Markdown 正文交给 MarkdownRenderer。
// 三种状态各自可辨：加载中（骨架）/ 出错（可重试）/ 文章不存在（404 文案，不重试而是引导回列表）。
// 决策 U：prev / next 契约里恒为 null（阶段 8 才实现），本批不渲染该区块。
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'
import SkeletonBlock from '@/components/SkeletonBlock.vue'
import { fetchArticleDetail } from '@/api/articles'
import { formatDate } from '@/utils/date'

const route = useRoute()

const article = ref(null)
const loading = ref(true)
const error = ref(null)

const notFound = computed(() => Boolean(error.value && error.value.isNotFound))
const updated = computed(
  () => article.value && article.value.updatedAt && article.value.updatedAt !== article.value.createdAt
)

async function load() {
  loading.value = true
  error.value = null
  try {
    const data = await fetchArticleDetail(route.params.id)
    article.value = data
    // 拿到真实标题后覆盖路由的通用标题，浏览器标签页与历史记录更好读
    document.title = `${data.title} · 个人博客`
  } catch (caught) {
    error.value = caught
    article.value = null
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="page">
    <div v-if="loading" class="detail-loading" aria-busy="true" aria-label="文章加载中">
      <SkeletonBlock height="34px" width="72%" />
      <SkeletonBlock height="14px" width="36%" />
      <SkeletonBlock height="14px" width="94%" />
      <SkeletonBlock height="14px" width="88%" />
      <SkeletonBlock height="14px" width="92%" />
      <SkeletonBlock height="14px" width="60%" />
      <SkeletonBlock height="150px" radius="var(--radius-card)" />
    </div>

    <div v-else-if="error" class="detail-state" role="alert">
      <p class="detail-state__title">{{ notFound ? '文章不存在' : '文章加载失败' }}</p>
      <p class="detail-state__detail">
        {{
          notFound
            ? `ID 为 ${route.params.id} 的文章可能已被删除，或者从未存在。`
            : error.message
        }}
      </p>
      <div class="detail-state__actions">
        <button v-if="!notFound" type="button" class="detail-state__button" @click="load">重试</button>
        <RouterLink class="detail-state__link" to="/articles">← 返回文章列表</RouterLink>
      </div>
    </div>

    <article v-else-if="article" class="detail">
      <header class="detail__head">
        <h1 class="detail__title">{{ article.title }}</h1>

        <p class="detail__meta">
          <time :datetime="article.createdAt">创建于 {{ formatDate(article.createdAt) }}</time>
          <template v-if="updated"> · 更新于 {{ formatDate(article.updatedAt) }}</template>
          <span> · 点赞 {{ article.likeCount }}</span>
          <span> · 评论 {{ article.commentCount }}</span>
        </p>

        <ul v-if="article.tags && article.tags.length" class="detail__tags">
          <li v-for="tag in article.tags" :key="tag" class="detail__tag">{{ tag }}</li>
        </ul>
      </header>

      <img
        v-if="article.coverUrl"
        class="detail__cover"
        :src="article.coverUrl"
        alt=""
        loading="lazy"
        decoding="async"
      />

      <MarkdownRenderer class="detail__body" :source="article.content" />

      <footer class="detail__foot">
        <RouterLink to="/articles">← 返回文章列表</RouterLink>
      </footer>
    </article>
  </section>
</template>

<style scoped>
.detail-loading {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.detail-state {
  padding: 40px 24px;
  text-align: center;
  color: var(--color-muted);
  background-color: var(--color-bg-soft);
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-card);
}

.detail-state__title {
  margin: 0 0 8px;
  color: var(--color-text);
  font-size: 20px;
}

.detail-state__detail {
  margin: 0 0 20px;
  font-size: 14px;
}

.detail-state__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.detail-state__button {
  padding: 8px 18px;
  color: var(--color-accent-contrast);
  background-color: var(--color-accent);
  border: 1px solid transparent;
  border-radius: var(--radius-pill);
  cursor: pointer;
  transition: background-color var(--duration-fast) ease;
}

.detail-state__button:hover {
  background-color: var(--color-accent-hover);
}

.detail-state__link {
  font-size: 15px;
}

.detail__title {
  margin: 0 0 12px;
  font-size: 32px;
  line-height: 1.3;
}

.detail__meta {
  margin: 0 0 16px;
  color: var(--color-muted);
  font-size: 14px;
}

.detail__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 0 0 24px;
  padding: 0;
  list-style: none;
}

.detail__tag {
  padding: 3px 12px;
  color: var(--color-accent);
  background-color: var(--color-accent-soft);
  border-radius: var(--radius-pill);
  font-size: 13px;
}

.detail__cover {
  display: block;
  width: 100%;
  margin-bottom: 28px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
}

.detail__foot {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid var(--color-border);
  font-size: 15px;
}
</style>
