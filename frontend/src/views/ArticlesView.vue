<script setup>
// 文章列表页（阶段 4 批 2 建；阶段 5 批 2 扩展为「搜索 + 标签过滤」，模块四）。
// 数据一律来自 GET /api/articles（决策 R）；筛选与页码全部与地址栏同步
// （keyword / tags / tagMode / page），刷新与分享链接都能回到同一状态（决策 3）。
// 历史记录策略：筛选改动与滚动加载一律 replace（避免每敲一个字 / 每滚一屏就压一条历史）。
//
// 阶段 7 批 3（决策 S / BG）：分页控件改为**无限滚动**——
//   · 列表改为累积加载，?page 的语义变成"已加载到第 N 页"；
//   · 深链 ?page=3 会按顺序把前 3 页都取回来；页数越界仍收敛到最后一页（遗留 17 的行为不变）；
//   · 底部保留「加载更多」按钮：键盘 / 读屏用户不必依赖滚动，环境不支持 IntersectionObserver 时也靠它兜底。
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ArticleList from '@/components/ArticleList.vue'
import SearchInput from '@/components/SearchInput.vue'
import TagFilter from '@/components/TagFilter.vue'
import { fetchArticles } from '@/api/articles'
import { fetchTags } from '@/api/tags'

const PAGE_SIZE = 10
const DEFAULT_TAG_MODE = 'and'
// 哨兵提前 240px 触发，滚动到底前就开始取下一页
const SENTINEL_MARGIN = '240px 0px'

const route = useRoute()
const router = useRouter()

const articles = ref([])
const total = ref(0)
// 已加载页数，与地址栏的 ?page 同义
const loadedPages = ref(1)
const initialized = ref(false)
const loading = ref(false)
const loadingMore = ref(false)
const error = ref(null)
const moreError = ref(null)

const tags = ref([])
const tagsError = ref(null)

const sentinel = ref(null)
let observer = null
let requestId = 0
// 当前列表对应的"筛选条件"签名：用于识别"只是页码变了"的地址栏变化，避免重复取数
let renderedFilter = ''

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
const hasMore = computed(() => articles.value.length < total.value)
const showMoreArea = computed(() => !loading.value && !error.value && articles.value.length > 0)
const countText = computed(() =>
  hasFilters.value ? `筛选出 ${total.value} 篇文章` : `共 ${total.value} 篇文章`
)

const emptyText = computed(() => (hasFilters.value ? '没有匹配的文章' : '还没有已发布的文章'))
const emptyDescription = computed(() =>
  hasFilters.value ? '换个关键词，或减少标签条件后再试试' : ''
)
const emptyActionText = computed(() => (hasFilters.value ? '清除筛选' : ''))

// 任一筛选或页码变化都会改变签名，从而触发重新加载
const filterSignature = computed(() =>
  JSON.stringify({ keyword: keyword.value, tags: selectedTags.value, tagMode: tagMode.value })
)
const querySignature = computed(
  () => JSON.stringify({ ...JSON.parse(filterSignature.value), page: page.value })
)

async function load(targetPage) {
  const target = Math.max(1, Number(targetPage) || 1)

  // 页码回到"已加载的最后一页"（越界收敛写回地址栏）且筛选没变时，无事可做
  if (
    initialized.value &&
    target === loadedPages.value &&
    renderedFilter === filterSignature.value &&
    !error.value &&
    !moreError.value
  ) {
    return
  }

  // 首次加载 / 改筛选 / 手改地址栏往回退：从第 1 页重新累积；否则在已有列表后面追加
  const rebuild = target <= loadedPages.value || articles.value.length === 0

  const current = ++requestId
  if (rebuild) {
    loading.value = true
    error.value = null
  } else {
    loadingMore.value = true
  }
  moreError.value = null

  const collected = rebuild ? [] : articles.value.slice()
  let cursor = rebuild ? 1 : loadedPages.value + 1
  let limit = target
  let totalCount = total.value

  try {
    while (cursor <= limit) {
      const data = await fetchArticles({
        page: cursor,
        size: PAGE_SIZE,
        keyword: keyword.value,
        tags: selectedTags.value,
        tagMode: tagMode.value
      })
      // 快速连点 / 连续改筛选时丢弃"过期"的响应，避免旧数据覆盖新数据
      if (current !== requestId) return
      totalCount = data.total
      // 遗留 17：页数超过实际总页数时收敛到最后一页（后端对越界页返回空数组，不会 404）
      limit = Math.min(limit, Math.max(1, data.totalPages))
      collected.push(...data.items)
      cursor += 1
    }

    articles.value = collected
    total.value = totalCount
    loadedPages.value = limit
    initialized.value = true
    renderedFilter = filterSignature.value

    // 地址栏与"已加载页数"保持一致：滚动追加与越界收敛都走 replace
    if (page.value !== limit) syncQuery({ page: limit }, { replace: true })
  } catch (caught) {
    if (current !== requestId) return
    if (rebuild) {
      error.value = caught
      articles.value = []
      total.value = 0
      loadedPages.value = 1
    } else {
      moreError.value = caught
    }
  } finally {
    if (current === requestId) {
      loading.value = false
      loadingMore.value = false
    }
  }
}

// 首屏 / 整页错误态的「重试」：从第 1 页重来
function retryLoad() {
  load(1)
}

function loadMore() {
  if (loading.value || loadingMore.value || !hasMore.value) return
  load(loadedPages.value + 1)
}

// 哨兵随"还有更多"区域挂载 / 卸载；每次追加后再观察一次，
// 这样"新内容还没把哨兵顶出视口"时会继续往下取，直到不再相交或没有更多
function observeSentinel() {
  if (!observer) return
  observer.disconnect()
  if (sentinel.value) observer.observe(sentinel.value)
}

watch(sentinel, observeSentinel, { flush: 'post' })
watch(loadedPages, observeSentinel, { flush: 'post' })

onMounted(() => {
  if (typeof IntersectionObserver === 'undefined') return
  observer = new IntersectionObserver(
    (entries) => {
      if (entries.some((entry) => entry.isIntersecting)) loadMore()
    },
    { rootMargin: SENTINEL_MARGIN }
  )
  observeSentinel()
})

onBeforeUnmount(() => {
  observer?.disconnect()
  observer = null
})

async function loadTags() {
  tagsError.value = null
  try {
    tags.value = await fetchTags()
  } catch (caught) {
    tags.value = []
    tagsError.value = caught
  }
}

watch(querySignature, () => load(page.value), { immediate: true })
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

function onEmptyAction() {
  if (hasFilters.value) clearFilters()
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
      :appending="loadingMore"
      :skeleton-count="PAGE_SIZE"
      :empty-text="emptyText"
      :empty-description="emptyDescription"
      :empty-action-text="emptyActionText"
      error-text="文章列表加载失败"
      @retry="retryLoad"
      @empty-action="onEmptyAction"
    />

    <div v-if="showMoreArea" class="articles__more">
      <p v-if="moreError" class="articles__more-error" role="alert">
        加载更多失败：{{ moreError.message }}
      </p>

      <button v-if="hasMore || loadingMore" type="button" class="btn btn--ghost" @click="loadMore">
        {{ loadingMore ? '正在加载…' : '加载更多' }}
      </button>

      <p v-else class="articles__more-end">已经到底了 · 共 {{ total }} 篇</p>

      <span class="visually-hidden" role="status">{{ loadingMore ? '正在加载更多文章' : '' }}</span>

      <div ref="sentinel" class="articles__sentinel" aria-hidden="true"></div>
    </div>
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

/* ---- 无限滚动的底部区域（阶段 7 批 3）---- */
.articles__more {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-top: 28px;
}

.articles__more-error {
  margin: 0;
  color: var(--color-like);
  font-size: 14px;
}

.articles__more-end {
  margin: 0;
  color: var(--color-muted);
  font-size: 14px;
}

/* 观测哨兵：1px 高、不占视觉空间，只用于 IntersectionObserver 定位 */
.articles__sentinel {
  width: 100%;
  height: 1px;
}
</style>
