<script setup>
// 单条评论（阶段 5 批 3，模块五；阶段 8 批 6 补"编辑"入口）：
// 昵称 / 时间 / 内容；只有"本机发过的评论"才显示编辑与删除入口
// （决策 8 / BS：契约不回传 visitorId，归属靠本地账本显示、靠后端校验）。
// 删除用行内二次确认、编辑用行内表单，都不用 window.confirm —— 阻塞式弹窗样式不受主题控制。
import { nextTick, ref } from 'vue'
import { deleteComment, updateComment } from '@/api/comments'
import { formatDateTime } from '@/utils/date'
import { getVisitorId } from '@/utils/visitor'
import { COMMENT_LIMITS } from '@/utils/validate'

const props = defineProps({
  comment: { type: Object, required: true },
  canDelete: { type: Boolean, default: false },
  canEdit: { type: Boolean, default: false }
})

const emit = defineEmits(['deleted', 'updated'])

const confirming = ref(false)
const deleting = ref(false)
const error = ref(null)

// 行内编辑（阶段 8 批 6）：editing 为真时用 textarea 替换正文段落
const editing = ref(false)
const saving = ref(false)
const editContent = ref('')
const editInput = ref(null)
const editError = ref(null)

async function startEdit() {
  if (editing.value) return
  editContent.value = props.comment.content
  editError.value = null
  error.value = null
  confirming.value = false
  editing.value = true
  await nextTick()
  editInput.value?.focus()
}

function cancelEdit() {
  if (saving.value) return
  editing.value = false
  editError.value = null
}

async function saveEdit() {
  if (saving.value) return
  const content = editContent.value.trim()
  if (content === '') {
    editError.value = '评论内容不能为空'
    return
  }
  if (content.length > COMMENT_LIMITS.contentMax) {
    editError.value = `评论内容不能超过 ${COMMENT_LIMITS.contentMax} 个字`
    return
  }
  saving.value = true
  editError.value = null
  try {
    const updated = await updateComment(props.comment.id, content, getVisitorId())
    editing.value = false
    emit('updated', updated)
  } catch (caught) {
    if (caught && caught.isNotFound) {
      // 评论已不存在，或本机 visitorId 不匹配：回退只读并提示
      editing.value = false
      error.value = caught
    } else {
      editError.value = caught.message
    }
  } finally {
    saving.value = false
  }
}

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

    <p v-if="!editing" class="comment__content">{{ comment.content }}</p>

    <form v-else class="comment__edit" @submit.prevent="saveEdit">
      <label class="visually-hidden" :for="`comment-edit-${comment.id}`">修改评论内容</label>
      <textarea
        :id="`comment-edit-${comment.id}`"
        ref="editInput"
        v-model="editContent"
        class="input comment__edit-input"
        rows="3"
        :maxlength="COMMENT_LIMITS.contentMax"
        :disabled="saving"
      ></textarea>
      <p v-if="editError" class="comment__error" role="alert">{{ editError }}</p>
      <div class="comment__edit-actions">
        <button type="submit" class="btn" :disabled="saving">
          {{ saving ? '保存中…' : '保存修改' }}
        </button>
        <button type="button" class="comment__action" :disabled="saving" @click="cancelEdit">取消</button>
      </div>
    </form>

    <div v-if="(canDelete || canEdit) && !editing" class="comment__actions">
      <template v-if="!confirming">
        <button v-if="canEdit" type="button" class="comment__action" @click="startEdit">编辑</button>
        <button v-if="canDelete" type="button" class="comment__action" @click="confirming = true">删除</button>
      </template>
      <template v-else>
        <span class="comment__confirm">确定删除这条评论？</span>
        <button type="button" class="comment__action comment__action--danger" :disabled="deleting" @click="remove">
          {{ deleting ? '删除中…' : '确认删除' }}
        </button>
        <button type="button" class="comment__action" :disabled="deleting" @click="confirming = false">取消</button>
      </template>
    </div>

    <p v-if="error && !editing" class="comment__error" role="alert">操作失败：{{ error.message }}</p>
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

/* 行内编辑（阶段 8 批 6） */
.comment__edit {
  margin: 0;
}

.comment__edit-input {
  width: 100%;
  min-height: 76px;
  resize: vertical;
}

.comment__edit-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}
</style>
