<script setup>
// 文章列表容器（阶段 4 批 2）：统一处理四种状态（加载中 / 出错 / 空结果 / 有数据），
// 列表页与首页都只关心"拿数据 + 传进来"，空态与错误态文案不重复写两遍。
import ArticleCard from './ArticleCard.vue'
import ArticleSkeleton from './ArticleSkeleton.vue'

defineProps({
  articles: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  // ApiError 实例；有值即进入错误态
  error: { type: Object, default: null },
  skeletonCount: { type: Number, default: 6 },
  emptyText: { type: String, default: '暂时没有文章' },
  errorText: { type: String, default: '文章加载失败' }
})

const emit = defineEmits(['retry'])
</script>

<template>
  <div class="article-list">
    <ul v-if="loading" class="article-list__grid" aria-busy="true">
      <li v-for="n in skeletonCount" :key="n">
        <ArticleSkeleton />
      </li>
    </ul>

    <div v-else-if="error" class="article-list__state" role="alert">
      <p class="article-list__state-title">{{ errorText }}</p>
      <p class="article-list__state-detail">{{ error.message }}</p>
      <button type="button" class="article-list__retry" @click="emit('retry')">重试</button>
    </div>

    <p v-else-if="articles.length === 0" class="article-list__state">{{ emptyText }}</p>

    <ul v-else class="article-list__grid">
      <li v-for="(article, index) in articles" :key="article.id" v-reveal="{ delay: Math.min(index, 6) * 40 }">
        <ArticleCard :article="article" />
      </li>
    </ul>
  </div>
</template>

<style scoped>
.article-list__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.article-list__state {
  margin: 0;
  padding: 32px 24px;
  text-align: center;
  color: var(--color-muted);
  background-color: var(--color-bg-soft);
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-card);
}

.article-list__state-title {
  margin: 0 0 6px;
  color: var(--color-text);
  font-size: 16px;
}

.article-list__state-detail {
  margin: 0 0 16px;
  font-size: 14px;
}

.article-list__retry {
  padding: 8px 18px;
  color: var(--color-accent-contrast);
  background-color: var(--color-accent);
  border: 1px solid transparent;
  border-radius: var(--radius-pill);
  cursor: pointer;
  transition: background-color var(--duration-fast) ease;
}

.article-list__retry:hover {
  background-color: var(--color-accent-hover);
}
</style>
