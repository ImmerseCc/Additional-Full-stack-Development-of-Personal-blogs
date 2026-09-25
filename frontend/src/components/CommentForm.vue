<script setup>
// 评论表单（阶段 5 批 3，模块五）：昵称 / 邮箱（选填）/ 内容。
// 校验走 utils/validate.js（与服务端契约同数值）；服务端返回的 40001 字段级原因按同一套字段名回填。
// 昵称记在本地（blog:commentAuthor）免重复输入；邮箱不记忆（隐私，且契约里本来就只存不返）。
// 阶段 7 批 5（决策 BM）：向父级暴露 clearStatus()，评论被删除后由父级清掉「评论已发表」提示（闭环遗留 29）。
// 阶段 7 批 7（作者验收后修订）：**记过昵称就把输入框收起** —— 只留「以 XXX 的身份评论 · 改昵称」一行，
//   点「改昵称」才展开输入框；首次访问（本机无记录）或清过本地数据后才默认显示输入框。
import { computed, nextTick, ref } from 'vue'
import { createComment } from '@/api/comments'
import { readRaw, writeRaw } from '@/utils/storage'
import { getVisitorId } from '@/utils/visitor'
import { COMMENT_LIMITS, validateCommentForm } from '@/utils/validate'

const props = defineProps({
  articleId: { type: [Number, String], required: true }
})

const emit = defineEmits(['submitted'])

const AUTHOR_STORAGE_KEY = 'commentAuthor'
const uid = Math.random().toString(36).slice(2, 8)

const rememberedName = readRaw(AUTHOR_STORAGE_KEY) || ''
const authorName = ref(rememberedName)
const authorEmail = ref('')
const content = ref('')
const errors = ref({})
const submitting = ref(false)
const serverError = ref(null)
const status = ref('')
// false = 收起输入框（本机记过昵称）；true = 展开让用户填写 / 修改
const editingName = ref(rememberedName === '')
const nameInput = ref(null)

const contentLength = computed(() => content.value.length)

function clearStatus() {
  if (status.value) status.value = ''
}

// 评论已在别处删掉时，「评论已发表」就不再是事实了 —— 由 CommentSection 在删除成功后调用
defineExpose({ clearStatus })

async function startEditName() {
  editingName.value = true
  await nextTick()
  nameInput.value?.focus()
}

async function submit() {
  if (submitting.value) return
  serverError.value = null
  status.value = ''

  const localErrors = validateCommentForm({
    authorName: authorName.value,
    authorEmail: authorEmail.value,
    content: content.value
  })
  errors.value = localErrors
  if (Object.keys(localErrors).length > 0) return

  submitting.value = true
  try {
    const name = authorName.value.trim()
    const created = await createComment(props.articleId, {
      authorName: name,
      authorEmail: authorEmail.value.trim(),
      content: content.value.trim(),
      visitorId: getVisitorId()
    })
    writeRaw(AUTHOR_STORAGE_KEY, name)
    content.value = ''
    errors.value = {}
    status.value = '评论已发表'
    // 昵称已记下：收起输入框，回到「以 XXX 的身份评论」
    editingName.value = false
    emit('submitted', created)
  } catch (caught) {
    if (caught && caught.fields) {
      errors.value = { ...caught.fields }
    } else if (caught) {
      serverError.value = caught
    }
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <form class="comment-form" novalidate @submit.prevent="submit">
    <div class="comment-form__row">
      <div class="comment-form__field">
        <template v-if="editingName">
          <label class="comment-form__label" :for="`comment-author-${uid}`">
            昵称 <span class="comment-form__required" aria-hidden="true">*</span>
          </label>
          <input
            :id="`comment-author-${uid}`"
            ref="nameInput"
            v-model="authorName"
            class="input"
            type="text"
            :maxlength="COMMENT_LIMITS.authorNameMax"
            autocomplete="nickname"
            :aria-invalid="errors.authorName ? 'true' : 'false'"
            @input="clearStatus"
          />
          <p v-if="errors.authorName" class="comment-form__error">{{ errors.authorName }}</p>
        </template>

        <template v-else>
          <span class="comment-form__label">昵称</span>
          <p class="comment-form__identity">
            以 <strong>{{ authorName }}</strong> 的身份评论
            <button type="button" class="comment-form__identity-edit" @click="startEditName">
              改昵称
            </button>
          </p>
        </template>
      </div>

      <div class="comment-form__field">
        <label class="comment-form__label" :for="`comment-email-${uid}`">
          邮箱 <span class="comment-form__hint">选填，仅服务端保存、不会公开</span>
        </label>
        <input
          :id="`comment-email-${uid}`"
          v-model="authorEmail"
          class="input"
          type="email"
          autocomplete="email"
          :aria-invalid="errors.authorEmail ? 'true' : 'false'"
          @input="clearStatus"
        />
        <p v-if="errors.authorEmail" class="comment-form__error">{{ errors.authorEmail }}</p>
      </div>
    </div>

    <div class="comment-form__field">
      <label class="comment-form__label" :for="`comment-content-${uid}`">
        评论内容 <span class="comment-form__required" aria-hidden="true">*</span>
      </label>
      <textarea
        :id="`comment-content-${uid}`"
        v-model="content"
        class="input comment-form__textarea"
        rows="4"
        :maxlength="COMMENT_LIMITS.contentMax"
        :aria-invalid="errors.content ? 'true' : 'false'"
        @input="clearStatus"
      ></textarea>
      <div class="comment-form__foot">
        <span class="comment-form__counter">{{ contentLength }} / {{ COMMENT_LIMITS.contentMax }}</span>
        <p v-if="errors.content" class="comment-form__error">{{ errors.content }}</p>
      </div>
    </div>

    <p v-if="serverError" class="comment-form__error" role="alert">提交失败：{{ serverError.message }}</p>
    <p class="comment-form__status" role="status">{{ status }}</p>

    <div class="comment-form__actions">
      <button type="submit" class="btn" :disabled="submitting">
        {{ submitting ? '提交中…' : '发表评论' }}
      </button>
    </div>
  </form>
</template>

<style scoped>
.comment-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
  padding: 20px;
  background-color: var(--color-bg-soft);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
}

.comment-form__row {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

@media (min-width: 640px) {
  .comment-form__row {
    grid-template-columns: 1fr 1fr;
  }
}

.comment-form__field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.comment-form__label {
  color: var(--color-text);
  font-size: 14px;
  font-weight: 600;
}

.comment-form__required {
  color: var(--color-accent);
}

/* 记过昵称后的"身份行"（阶段 7 批 7）：min-height 与旁边的邮箱输入框对齐，两列不歪 */
.comment-form__identity {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  min-height: 42px;
  margin: 0;
  color: var(--color-muted);
  font-size: 14px;
}

.comment-form__identity strong {
  color: var(--color-text);
  font-weight: 600;
}

.comment-form__identity-edit {
  padding: 0;
  color: var(--color-accent);
  background: none;
  border: 0;
  font-size: 13px;
  text-decoration: underline;
  text-underline-offset: 2px;
  cursor: pointer;
}

.comment-form__hint {
  margin-left: 6px;
  color: var(--color-muted);
  font-size: 12px;
  font-weight: 400;
}

.comment-form__textarea {
  resize: vertical;
  min-height: 96px;
}

.comment-form__foot {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.comment-form__counter {
  color: var(--color-muted);
  font-size: 12px;
}

.comment-form__error {
  margin: 0;
  color: #d1242f;
  font-size: 13px;
}

[data-theme='dark'] .comment-form__error {
  color: #ff7b72;
}

.comment-form__status {
  margin: 0;
  min-height: 20px;
  color: var(--color-muted);
  font-size: 13px;
}

.comment-form__actions {
  display: flex;
  justify-content: flex-end;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
