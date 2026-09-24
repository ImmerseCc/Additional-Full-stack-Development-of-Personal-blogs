<script setup>
// 首页（阶段 4 批 2，决策 T）：站点门面 = 一句话介绍 + 最新 3 篇卡片 + "查看全部"入口。
// 与列表页共用 ArticleList / ArticleCard，只是数据条数与文案不同。
import { onMounted, ref } from 'vue'
import ArticleList from '@/components/ArticleList.vue'
import { fetchArticles } from '@/api/articles'

const LATEST_COUNT = 3

const articles = ref([])
const loading = ref(false)
const error = ref(null)

async function load() {
  loading.value = true
  error.value = null
  try {
    const data = await fetchArticles({ size: LATEST_COUNT })
    articles.value = data.items
  } catch (caught) {
    error.value = caught
    articles.value = []
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="page">
    <header class="hero">
      <p class="hero__eyebrow">Vue 3 + Spring Boot + SQLite</p>
      <h1 class="hero__title">个人博客 · VibeCoding</h1>
      <p class="hero__desc">
        从零手写的个人博客：前端不引入任何 UI 组件库与 CSS 框架，后端用 JdbcTemplate 手写 SQL，
        数据落在单文件 SQLite 里。这里记录实现过程、取舍与踩过的坑。
      </p>
      <div class="hero__actions">
        <RouterLink class="hero__button" to="/articles">浏览全部文章</RouterLink>
        <RouterLink class="hero__link" to="/about">关于这个项目 →</RouterLink>
      </div>
    </header>

    <section class="latest">
      <div class="latest__head">
        <h2 class="latest__title">最新文章</h2>
        <RouterLink class="latest__more" to="/articles">查看全部 →</RouterLink>
      </div>

      <ArticleList
        :articles="articles"
        :loading="loading"
        :error="error"
        :skeleton-count="LATEST_COUNT"
        empty-text="还没有已发布的文章"
        error-text="最新文章加载失败"
        @retry="load"
      />
    </section>
  </section>
</template>

<style scoped>
.hero {
  padding: 8px 0 32px;
  border-bottom: 1px solid var(--color-border);
}

.hero__eyebrow {
  margin: 0 0 8px;
  color: var(--color-accent);
  font-size: 13px;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.hero__title {
  margin: 0 0 12px;
  font-size: 34px;
  line-height: 1.25;
}

.hero__desc {
  max-width: 620px;
  margin: 0 0 20px;
  color: var(--color-muted);
}

.hero__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
}

.hero__button {
  padding: 9px 20px;
  color: var(--color-accent-contrast);
  background-color: var(--color-accent);
  border-radius: var(--radius-pill);
  text-decoration: none;
  transition: background-color var(--duration-fast) ease;
}

.hero__button:hover {
  color: var(--color-accent-contrast);
  background-color: var(--color-accent-hover);
}

.hero__link {
  font-size: 15px;
}

.latest {
  margin-top: 32px;
}

.latest__head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 20px;
}

.latest__title {
  margin: 0;
  font-size: 20px;
}

.latest__more {
  font-size: 14px;
}
</style>
