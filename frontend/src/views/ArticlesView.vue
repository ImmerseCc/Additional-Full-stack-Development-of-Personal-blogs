<script setup>
// 文章列表页（阶段 4 批 2 建；阶段 5 批 2 扩展为「搜索 + 标签过滤」，模块四）。
// 数据一律来自 GET /api/articles（决策 R）；筛选与页码全部与地址栏同步
// （keyword / tags / tagMode / page），刷新与分享链接都能回到同一状态（决策 3）。
// 历史记录策略：筛选改动用 replace（避免每敲一个字就压一条历史），翻页用 push（可后退）。
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ArticleList from '@/components/ArticleList.vue'
import Pagination from '@/components/Pagination.vue'
import SearchInput from '@/components/SearchInput.vue'
import TagFilter from '@/components/TagFilter.vue'
import { fetchArticles } from '@/api/articles'
import { fetchTags } from '@/api/tags'

const PAGE_SIZE = 10
const DEFAULT_TAG_MODE = 'and'

const route = useRoute()
const router = useRouter()

const articles = ref([])
const total = ref(0)
const totalPages = ref(0)
const loading = ref(false)
const error = ref(null)

const tags = ref([])
const tagsError = ref(null)

// ---- 地址栏 → 状态（地址栏不可信：非法值一律按默认处理，交给后端只会换来 40002）----
function parseTags(raw) {
  if (typeof raw !== 'string') return []
  return raw
    .split(',')
    .map((name) => name.trim())
    .filter((name) => name !== '')
}

const keyword = computed(() => (typeof route.query.keyword === 'string' ? route.query.keyword : ''))
const selectedTags = computed(() => parseTags(route.query.tags))
const tagMode = computed(() => (route.query.tagMode === 'or' ? 'or' : DEFAULT_TAG_MODE))
const page = computed(() => {
  const raw = Number(route.query.page)
  return Number.isInteger(raw) && raw >= 1 ? raw : 1
})

const hasFilters = computed(() => keyword.value !== '' || selectedTags.value.length > 0)
const countText = computed(() =>
  hasFilters.value ? `筛选出 ${total.value} 篇文章` : `共 ${total.value} 篇文章`
)

const emptyText = computed(() => {
  if (hasFilters.value) return '没有匹配的文章'
  return page.value > 1 ? '这一页没有文章' : '还没有已发布的文章'
})
const emptyDescription = computed(() => {
  if (hasFilters.value) return '换个关键词，或减少标签条件后再试试'
  return page.value > 1 ? '该页码超出范围，可以回到第 1 页' : ''
})
const emptyActionText = computed(() => {
  if (hasFilters.value) return '清除筛选'
  return page.value > 1 ? '回到第 1 页' : ''
})

// 任一筛选或页码变化都会改变签名，从而触发重新加载
const querySignature = computed(() =>
  JSON.stringify({
    keyword: keyword.value,
    tags: selectedTags.value,
    tagMode: tagMode.value,
    page: page.value
  })
)

let requestId = 0

async function load() {
  const current = ++requestId
  loading.value = true
  error.value = null
  try {
    const data = await fetchArticles({
      page: page.value,
      size: PAGE_SIZE,
      keyword: keyword.value,
      tags: selectedTags.value,
      tagMode: tagMode.value
    })
    // 快速连点分页 / 连续改筛选时，丢弃"过期"的响应，避免旧数据覆盖新数据
    if (current !== requestId) return
    // 遗留 17：手输页码超过总页数时回退到最后一页（后端对越界页返回空数组，不会 404）
    if (data.totalPages > 0 && page.value > data.totalPages) {
      syncQuery({ page: data.totalPages }, { replace: true })
      return
    }
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

async function loadTags() {
  tagsError.value = null
  try {
    tags.value = await fetchTags()
  } catch (caught) {
    tags.value = []
    tagsError.value = caught
  }
}

watch(querySignature, load, { immediate: true })
loadTags()

// ---- 状态 → 地址栏 ----
function isSameQuery(query) {
  return ['keyword', 'tags', 'tagMode', 'page'].every(
    (key) => (route.query[key] ?? null) === (query[key] ?? null)
  )
}

function syncQuery(patch, { replace = true } = {}) {
  const next = {
    keyword: keyword.value,
    tags: selectedTags.value,
    tagMode: tagMode.value,
    page: page.value,
    ...patch
  }
  const query = {}
  if (next.keyword) query.keyword = next.keyword
  if (next.tags.length > 0) query.tags = next.tags.join(',')
  // 单标签时 and / or 等价，不进地址栏，保持链接干净
  if (next.tags.length > 1 && next.tagMode === 'or') query.tagMode = 'or'
  if (next.page > 1) query.page = String(next.page)

  if (isSameQuery(query)) return
  router[replace ? 'replace' : 'push']({ path: '/articles', query })
}

// ---- 交互 ----
function onKeyword(value) {
  syncQuery({ keyword: value, page: 1 })
}

function onTags(next) {
  syncQuery({ tags: next, page: 1 })
}

function onTagMode(mode) {
  syncQuery({ tagMode: mode, page: 1 })
}

function clearFilters() {
  syncQuery({ keyword: '', tags: [], tagMode: DEFAULT_TAG_MODE, page: 1 })
}

function goToPage(target) {
  syncQuery({ page: target }, { replace: false })
}

function onEmptyAction() {
  if (hasFilters.value) clearFilters()
  else goToPage(1)
}
</script>

<template>
  <section class="page">
    <header class="articles__head">
      <h1>文章列表</h1>
      <p v-if="!loading && !error" class="articles__count">{{ countText }}</p>
    </header>

    <div class="articles__search">
      <SearchInput :model-value="keyword" @update:model-value="onKeyword" />
    </div>

    <TagFilter
      v-if="tags.length > 0"
      :tags="tags"
      :selected="selectedTags"
      :mode="tagMode"
      @update:selected="onTags"
      @update:mode="onTagMode"
    />

    <p v-else-if="tagsError" class="articles__tags-error" role="alert">
      标签加载失败：{{ tagsError.message }}
      <button type="button" class="articles__tags-retry" @click="loadTags">重试</button>
    </p>

    <ArticleList
      :articles="articles"
      :loading="loading"
      :error="error"
      :skeleton-count="PAGE_SIZE"
      :empty-text="emptyText"
      :empty-description="emptyDescription"
      :empty-action-text="emptyActionText"
      error-text="文章列表加载失败"
      @retry="load"
      @empty-action="onEmptyAction"
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
  margin-bottom: 20px;
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

.articles__search {
  max-width: 420px;
  margin-bottom: 16px;
}

.articles__tags-error {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin: 0 0 24px;
  padding: 12px 16px;
  color: var(--color-muted);
  background-color: var(--color-bg-soft);
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-card);
  font-size: 14px;
}

.articles__tags-retry {
  padding: 4px 12px;
  color: var(--color-accent);
  background-color: transparent;
  border: 1px solid var(--color-border-strong);
  border-radius: var(--radius-pill);
  font-size: 13px;
  cursor: pointer;
}

.articles__tags-retry:hover {
  border-color: var(--color-accent);
}
</style>
