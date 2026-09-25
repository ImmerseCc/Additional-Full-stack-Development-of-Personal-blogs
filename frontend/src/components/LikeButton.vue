<script setup>
// 点赞按钮（阶段 5 批 4，模块五）：计数**以后端返回为准**（契约第 5 节），本地只记"我赞过哪些"。
// 交互细节：请求期间禁用按钮（防连点）；成功后用返回值覆盖状态与计数；失败保留原状态并弹 Toast。
// 契约保证 POST / DELETE 幂等，所以重试不会造成重复计数；打开文章时先查一次真实状态并对齐本地账本。
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { fetchLikeState, likeArticle, unlikeArticle } from '@/api/likes'
import { useLikesStore } from '@/stores/likes'
import { useToastStore } from '@/stores/toast'
import { getVisitorId } from '@/utils/visitor'

const props = defineProps({
  articleId: { type: [Number, String], required: true },
  initialCount: { type: Number, default: 0 }
})

const emit = defineEmits(['count-change'])

const likes = useLikesStore()
const toast = useToastStore()

const liked = ref(false)
const count = ref(Number(props.initialCount) || 0)
const pending = ref(false)
const ready = ref(false)
const bump = ref(false)

let bumpTimer = null

function apply(state) {
  if (!state) return
  liked.value = Boolean(state.liked)
  count.value = Number(state.likeCount) || 0
  if (liked.value) likes.mark(props.articleId)
  else likes.unmark(props.articleId)
  emit('count-change', count.value)
}

async function sync() {
  try {
    apply(await fetchLikeState(props.articleId, getVisitorId()))
  } catch (error) {
    // 拉取点赞状态失败不打断阅读：保持当前显示（计数用详情接口给的值），不弹提示
  } finally {
    ready.value = true
  }
}

async function toggle() {
  if (pending.value || !ready.value) return
  pending.value = true
  try {
    const state = liked.value
      ? await unlikeArticle(props.articleId, getVisitorId())
      : await likeArticle(props.articleId, getVisitorId())
    apply(state)
  } catch (error) {
    toast.push(error && error.message ? error.message : '点赞失败，请稍后重试', { type: 'error' })
  } finally {
    pending.value = false
  }
}

onMounted(sync)

watch(
  () => props.articleId,
  () => {
    ready.value = false
    sync()
  }
)

// 详情接口后到的计数：同步显示（点赞按钮自己的返回值才是权威）
watch(
  () => props.initialCount,
  (value) => {
    count.value = Number(value) || 0
  }
)

// 计数变化时让数字"弹"一下（动效时长极短，reduced-motion 下由全局规则归零）
watch(count, () => {
  bump.value = true
  if (bumpTimer) clearTimeout(bumpTimer)
  bumpTimer = setTimeout(() => {
    bump.value = false
    bumpTimer = null
  }, 420)
})

onBeforeUnmount(() => {
  if (bumpTimer) clearTimeout(bumpTimer)
})
</script>

<template>
  <div class="like">
    <button
      type="button"
      class="like__button"
      :class="{ 'like__button--active': liked, 'like__button--bump': bump }"
      :aria-pressed="liked ? 'true' : 'false'"
      :disabled="pending || !ready"
      @click="toggle"
    >
      <svg class="like__icon" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true" focusable="false">
        <path
          d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
        />
      </svg>
      <span>{{ liked ? '已点赞' : '点赞' }}</span>
      <span class="like__count" :class="{ 'like__count--bump': bump }">{{ count }}</span>
    </button>
    <span class="visually-hidden" role="status">{{ liked ? `已点赞，当前 ${count} 个赞` : `未点赞，当前 ${count} 个赞` }}</span>
  </div>
</template>

<style scoped>
.like {
  display: flex;
  align-items: center;
  gap: 10px;
}

.like__button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  color: var(--color-muted);
  background-color: var(--color-bg-elevated);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-pill);
  font-size: 14px;
  cursor: pointer;
  transition:
    color var(--duration-fast) ease,
    border-color var(--duration-fast) ease,
    background-color var(--duration-fast) ease;
}

.like__button:hover:not(:disabled) {
  color: var(--color-like);
  border-color: var(--color-like);
}

.like__button:disabled {
  opacity: 0.7;
  cursor: progress;
}

.like__button--active {
  color: var(--color-like);
  border-color: var(--color-like);
  background-color: color-mix(in srgb, var(--color-like) 12%, transparent);
}

.like__icon path {
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linejoin: round;
  transition: fill var(--duration-fast) ease;
}

.like__button--active .like__icon path {
  fill: currentColor;
}

.like__count {
  font-variant-numeric: tabular-nums;
  font-weight: 600;
}

.like__button--bump .like__icon {
  animation: like-pulse 0.4s var(--ease-out);
}

.like__count--bump {
  animation: like-pop 0.4s var(--ease-out);
}

@keyframes like-pulse {
  0% {
    transform: scale(1);
  }

  45% {
    transform: scale(1.25);
  }

  100% {
    transform: scale(1);
  }
}

@keyframes like-pop {
  0% {
    transform: translateY(0);
  }

  45% {
    transform: translateY(-4px);
  }

  100% {
    transform: translateY(0);
  }
}
</style>
