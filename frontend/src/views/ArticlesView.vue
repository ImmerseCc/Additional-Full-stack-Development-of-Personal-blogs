<script setup>
// 文章列表页（阶段 4 批 2）：真实调用 GET /api/articles（决策 R：直接联调后端）。
// 页码与地址栏同步（?page=N，第 1 页不带参数），刷新与分享链接都能回到同一页。
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ArticleList from '@/components/ArticleList.vue'
import Pagination from '@/components/Pagination.vue'
import { fetchArticles } from '@/api/articles'

const PAGE_SIZE = 10

const route = useRoute()
const router = useRouter()

const articles = ref([])
const total = ref(0)
const totalPages = ref(0)
const loading = ref(false)
const error = ref(null)

// 地址栏里的 page 不可信：非法值一律按第 1 页处理（交给后端只会换来 40002）
const page = computed(() => {
  const raw = Number(route.query.page)
  return Number.isInteger(raw) && raw >= 1 ? raw : 1
})

let requestId = 0

async function load() {
  const current = ++requestId
  loading.value = true
  error.value = null
  try {
    const data = await fetchArticles({ page: page.value, size: PAGE_SIZE })
    // 快速连点分页时，丢弃"过期"的响应，避免旧数据覆盖新数据
    if (current !== requestId) return
    articles.value = data.items
    total.value = data.total
    totalPages.value = data.totalPages
  } catch (caught) {
    if (current !== requestId) return
    error.value = caught
    articles.value = []
    total.value = 0
    totalPages.value = 0
  } finally {
    if (current === requestId) loading.value = false
  }
}

watch(page, load, { immediate: true })

function goToPage(target) {
  router.push({ path: '/articles', query: target > 1 ? { page: String(target) } : {} })
}
</script>

<template>
  <section class="page">
    <header class="articles__head">
      <h1>文章列表</h1>
      <p v-if="!loading && !error" class="articles__count">共 {{ total }} 篇文章</p>
    </header>

    <ArticleList
      :articles="articles"
      :loading="loading"
      :error="error"
      :skeleton-count="PAGE_SIZE"
      empty-text="这一页没有文章"
      error-text="文章列表加载失败"
      @retry="load"
    />

    <Pagination :page="page" :total-pages="totalPages" @change="goToPage" />
  </section>
</template>

<style scoped>
.articles__head {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 24px;
}

.articles__head h1 {
  margin: 0;
  font-size: 28px;
}

.articles__count {
  margin: 0;
  color: var(--color-muted);
  font-size: 14px;
}
</style>
