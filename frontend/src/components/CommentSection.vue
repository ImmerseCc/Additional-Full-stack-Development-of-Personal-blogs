<script setup>
// 评论区（阶段 5 批 3，模块五）：列表（按契约倒序）+ 加载更多 + 发表表单 + 删除。
// 数据全部来自后端（契约 4.8 / 4.9 / 4.12 节）；本地只记"我发过的评论 ID"用于决定是否显示删除入口。
// 总数通过 total-change 回传给详情页头部 meta 的「评论 N」，避免两处各算一份。
// 阶段 7 批 5（决策 BM）：删除成功后顺手清掉表单里的「评论已发表」提示（闭环遗留 29）。
import { computed, onMounted, ref, watch } from 'vue'
import CommentForm from './CommentForm.vue'
import CommentItem from './CommentItem.vue'
import EmptyState from './EmptyState.vue'
import SkeletonBlock from './SkeletonBlock.vue'
import { fetchComments } from '@/api/comments'
import { useMyCommentsStore } from '@/stores/myComments'

const PAGE_SIZE = 10

const props = defineProps({
  articleId: { type: [Number, String], required: true }
})

const emit = defineEmits(['total-change'])

const myComments = useMyCommentsStore()

const comments = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(true)
const loadingMore = ref(false)
const error = ref(null)
// 表单实例：删除评论后调用它的 clearStatus()
const formRef = ref(null)

const hasMore = computed(() => comments.value.length < total.value)

function publishTotal() {
  emit('total-change', total.value)
}

async function load() {
  loading.value = true
  error.value = null
  try {
    const data = await fetchComments(props.articleId, { page: 1, size: PAGE_SIZE })
    comments.value = data.items
    total.value = data.total
    page.value = data.page
    publishTotal()
  } catch (caught) {
    error.value = caught
    comments.value = []
    total.value = 0
    publishTotal()
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  if (loadingMore.value || !hasMore.value) return
  loadingMore.value = true
  error.value = null
  try {
    const data = await fetchComments(props.articleId, { page: page.value + 1, size: PAGE_SIZE })
    page.value = data.page
    // 去重：极端情况下（并发新增 / 删除导致分页漂移）同一条评论可能出现在两页里
    const known = new Set(comments.value.map((item) => item.id))
    comments.value = [...comments.value, ...data.items.filter((item) => !known.has(item.id))]
    total.value = data.total
    publishTotal()
  } catch (caught) {
    error.value = caught
  } finally {
    loadingMore.value = false
  }
}

function onSubmitted(comment) {
  myComments.remember(comment)
  comments.value = [comment, ...comments.value]
  total.value += 1
  publishTotal()
}

function onDeleted(commentId) {
  myComments.forget(commentId)
  const before = comments.value.length
  comments.value = comments.value.filter((item) => item.id !== commentId)
  if (comments.value.length !== before) {
    total.value = Math.max(0, total.value - 1)
  }
  // 评论已经删了，表单下方的「评论已发表」就不再是事实 —— 一并清掉（阶段 7 批 5，决策 BM）
  formRef.value?.clearStatus()
  publishTotal()
}

onMounted(load)
// 同一路由参数变化时（理论上详情页会因 :key 重建组件）仍留一道保险
watch(() => props.articleId, load)
</script>

<template>
  <section class="comments" :aria-busy="loading ? 'true' : 'false'">
    <h2 class="comments__title">
      评论
      <span v-if="total > 0" class="comments__count">{{ total }}</span>
    </h2>

    <CommentForm ref="formRef" :article-id="articleId" @submitted="onSubmitted" />

    <div v-if="loading" class="comments__skeleton" aria-hidden="true">
      <SkeletonBlock height="16px" width="28%" />
      <SkeletonBlock height="14px" width="92%" />
      <SkeletonBlock height="14px" width="70%" />
    </div>

    <div v-else-if="error && comments.length === 0" class="comments__state" role="alert">
      <p class="comments__state-text">评论加载失败：{{ error.message }}</p>
      <button type="button" class="btn btn--ghost" @click="load">重试</button>
    </div>

    <EmptyState v-else-if="comments.length === 0" title="还没有评论" description="欢迎留下第一条评论" />

    <template v-else>
      <ul class="comments__list">
        <li v-for="comment in comments" :key="comment.id">
          <CommentItem :comment="comment" :can-delete="myComments.isMine(comment.id)" @deleted="onDeleted" />
        </li>
      </ul>

      <p v-if="error" class="comments__error" role="alert">加载更多失败：{{ error.message }}</p>

      <div v-if="hasMore" class="comments__more">
        <button type="button" class="btn btn--ghost" :disabled="loadingMore" @click="loadMore">
          {{ loadingMore ? '加载中…' : `加载更多（还有 ${total - comments.length} 条）` }}
        </button>
      </div>
      <p v-else class="comments__end">已显示全部 {{ total }} 条评论</p>
    </template>
  </section>
</template>

<style scoped>
.comments {
  margin-top: 48px;
  padding-top: 28px;
  border-top: 1px solid var(--color-border);
}

.comments__title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 20px;
  font-size: 22px;
}

.comments__count {
  padding: 1px 10px;
  color: var(--color-accent);
  background-color: var(--color-accent-soft);
  border-radius: var(--radius-pill);
  font-size: 14px;
  font-weight: 600;
}

.comments__skeleton {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px 0;
}

.comments__state {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 16px 20px;
  color: var(--color-muted);
  background-color: var(--color-bg-soft);
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-card);
  font-size: 14px;
}

.comments__state-text {
  margin: 0;
}

.comments__list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.comments__error {
  margin: 12px 0 0;
  color: var(--color-muted);
  font-size: 13px;
}

.comments__more {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.comments__end {
  margin: 20px 0 0;
  color: var(--color-muted);
  font-size: 13px;
  text-align: center;
}
</style>
