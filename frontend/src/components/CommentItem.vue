<script setup>
// 单条评论（阶段 5 批 3，模块五）：昵称 / 时间 / 内容。
// 只有"本机发过的评论"才显示删除入口（决策 8：契约不回传 visitorId，归属靠本地账本显示、靠后端校验）。
// 删除用行内二次确认，不用 window.confirm —— 阻塞式弹窗样式不受主题控制，也与页面观感割裂。
import { ref } from 'vue'
import { deleteComment } from '@/api/comments'
import { formatDateTime } from '@/utils/date'
import { getVisitorId } from '@/utils/visitor'

const props = defineProps({
  comment: { type: Object, required: true },
  canDelete: { type: Boolean, default: false }
})

const emit = defineEmits(['deleted'])

const confirming = ref(false)
const deleting = ref(false)
const error = ref(null)

async function remove() {
  if (deleting.value) return
  deleting.value = true
  error.value = null
  try {
    await deleteComment(props.comment.id, getVisitorId())
    emit('deleted', props.comment.id)
  } catch (caught) {
    // 40004 = 评论不存在，或 visitorId 不匹配；两种情况本机都不应再显示删除入口，按"已删除"处理
    if (caught && caught.isNotFound) {
      emit('deleted', props.comment.id)
    } else {
      error.value = caught
    }
  } finally {
    deleting.value = false
    confirming.value = false
  }
}
</script>

<template>
  <li class="comment">
    <div class="comment__head">
      <span class="comment__author">{{ comment.authorName }}</span>
      <time class="comment__time" :datetime="comment.createdAt">{{ formatDateTime(comment.createdAt) }}</time>
    </div>

    <p class="comment__content">{{ comment.content }}</p>

    <div v-if="canDelete" class="comment__actions">
      <template v-if="!confirming">
        <button type="button" class="comment__action" @click="confirming = true">删除</button>
      </template>
      <template v-else>
        <span class="comment__confirm">确定删除这条评论？</span>
        <button type="button" class="comment__action comment__action--danger" :disabled="deleting" @click="remove">
          {{ deleting ? '删除中…' : '确认删除' }}
        </button>
        <button type="button" class="comment__action" :disabled="deleting" @click="confirming = false">取消</button>
      </template>
    </div>

    <p v-if="error" class="comment__error" role="alert">删除失败：{{ error.message }}</p>
  </li>
</template>

<style scoped>
.comment {
  padding: 16px 0;
  border-bottom: 1px solid var(--color-border);
}

.comment__head {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 6px;
}

.comment__author {
  color: var(--color-text);
  font-size: 14px;
  font-weight: 600;
}

.comment__time {
  color: var(--color-muted);
  font-size: 12px;
}

.comment__content {
  margin: 0;
  color: var(--color-text);
  font-size: 15px;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.comment__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
  font-size: 13px;
}

.comment__confirm {
  color: var(--color-muted);
}

.comment__action {
  padding: 2px 10px;
  color: var(--color-muted);
  background-color: transparent;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-pill);
  font-size: 13px;
  cursor: pointer;
  transition:
    color var(--duration-fast) ease,
    border-color var(--duration-fast) ease;
}

.comment__action:hover {
  color: var(--color-accent);
  border-color: var(--color-accent);
}

.comment__action--danger:hover {
  color: #d1242f;
  border-color: #d1242f;
}

[data-theme='dark'] .comment__action--danger:hover {
  color: #ff7b72;
  border-color: #ff7b72;
}

.comment__action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.comment__error {
  margin: 8px 0 0;
  color: #d1242f;
  font-size: 13px;
}

[data-theme='dark'] .comment__error {
  color: #ff7b72;
}
</style>
