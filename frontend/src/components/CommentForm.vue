<script setup>
// 评论表单（阶段 5 批 3，模块五）：评论内容 + 发表。
// 校验走 utils/validate.js（与服务端契约同数值）；服务端返回的 40001 字段级原因按同一套字段名回填。
// 阶段 7 批 5（决策 BM）：向父级暴露 clearStatus()，评论被删除后由父级清掉「评论已发表」提示（闭环遗留 29）。
// 阶段 7 批 8（作者验收后修订二）：**昵称 / 邮箱从表单里挪进原生 <dialog> 弹窗，只在第一次评论时弹一次**，
//   之后记在本机（blog:commentAuthor / blog:commentEmail）自动带上，平时表单里不再占位；
//   想改就点身份行上的「修改」（同一个弹窗）。邮箱只在服务端保存、不回传、不公开。
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
const EMAIL_STORAGE_KEY = 'commentEmail'
const uid = Math.random().toString(36).slice(2, 8)

const authorName = ref(readRaw(AUTHOR_STORAGE_KEY) || '')
const authorEmail = ref(readRaw(EMAIL_STORAGE_KEY) || '')
const content = ref('')
const errors = ref({})
const submitting = ref(false)
const serverError = ref(null)
const status = ref('')

// 弹窗里的草稿：点「取消」不会动已记住的值
const draftName = ref('')
const draftEmail = ref('')
const dialogError = ref('')
const dialogRef = ref(null)
const nameInput = ref(null)
// 弹窗确认后是否继续发表（就是"第一次评论"这条路径）；Esc / 取消都会把它清掉
let resumeSubmit = false

const contentLength = computed(() => content.value.length)
const hiddenFieldError = computed(() => errors.value.authorName || errors.value.authorEmail || '')

function clearStatus() {
  if (status.value) status.value = ''
}

// 评论已在别处删掉时，「评论已发表」就不再是事实了 —— 由 CommentSection 在删除成功后调用
defineExpose({ clearStatus })

function openAuthorDialog() {
  const dialog = dialogRef.value
  if (!dialog || dialog.open) return
  draftName.value = authorName.value
  draftEmail.value = authorEmail.value
  dialogError.value = ''
  dialog.showModal()
  nextTick(() => nameInput.value?.focus())
}

function closeAuthorDialog() {
  dialogRef.value?.close()
}

// 原生 Esc、点「取消」都会走到这里：不改动任何值，也不再继续发表
function onDialogClose() {
  resumeSubmit = false
}

function confirmAuthor() {
  const name = draftName.value.trim()
  const email = draftEmail.value.trim()
  // 复用与服务端同源的校验，只关心昵称 / 邮箱两项（内容项传占位值绕过）
  const checked = validateCommentForm({ authorName: name, authorEmail: email, content: 'x' })
  if (checked.authorName) {
    dialogError.value = checked.authorName
    nextTick(() => nameInput.value?.focus())
    return
  }
  if (checked.authorEmail) {
    dialogError.value = checked.authorEmail
    return
  }

  authorName.value = name
  authorEmail.value = email
  // 先落盘：即便随后接口失败，也不用再问一次
  writeRaw(AUTHOR_STORAGE_KEY, name)
  writeRaw(EMAIL_STORAGE_KEY, email)

  const continueSubmit = resumeSubmit
  resumeSubmit = false
  dialogRef.value?.close()
  if (continueSubmit) submit()
}

async function submit() {
  if (submitting.value) return
  serverError.value = null
  status.value = ''

  // 第一次评论（本机还没有昵称）：先弹窗收集昵称 / 邮箱，确认后自动接着发表
  if (!authorName.value.trim()) {
    resumeSubmit = true
    openAuthorDialog()
    return
  }

  const localErrors = validateCommentForm({
    authorName: authorName.value,
    authorEmail: authorEmail.value,
    content: content.value
  })
  errors.value = localErrors
  if (Object.keys(localErrors).length > 0) return

  submitting.value = true
  try {
    const created = await createComment(props.articleId, {
      authorName: authorName.value.trim(),
      authorEmail: authorEmail.value.trim(),
      content: content.value.trim(),
      visitorId: getVisitorId()
    })
    content.value = ''
    errors.value = {}
    status.value = '评论已发表'
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

    <p v-if="hiddenFieldError" class="comment-form__error" role="alert">{{ hiddenFieldError }}</p>
    <p v-if="serverError" class="comment-form__error" role="alert">提交失败：{{ serverError.message }}</p>
    <p class="comment-form__status" role="status">{{ status }}</p>

    <div class="comment-form__actions">
      <p class="comment-form__identity">
        <template v-if="authorName">
          以 <strong>{{ authorName }}</strong> 的身份评论
          <button type="button" class="comment-form__identity-edit" @click="openAuthorDialog">
            修改昵称 / 邮箱
          </button>
        </template>
        <template v-else>首次评论需要填一次昵称，之后会自动记住</template>
      </p>

      <button type="submit" class="btn" :disabled="submitting">
        {{ submitting ? '提交中…' : '发表评论' }}
      </button>
    </div>
  </form>

  <dialog ref="dialogRef" class="author-dialog" aria-labelledby="author-dialog-title" @close="onDialogClose">
    <form method="dialog" class="author-dialog__form" @submit.prevent="confirmAuthor">
      <h2 id="author-dialog-title" class="author-dialog__title">留下你的称呼</h2>
      <p class="author-dialog__hint">
        只在第一次评论时问这一次，之后存在本机、自动带上（想改随时可以点「修改昵称 / 邮箱」）。
      </p>

      <div class="comment-form__field">
        <label class="comment-form__label" :for="`comment-author-${uid}`">
          昵称 <span class="comment-form__required" aria-hidden="true">*</span>
        </label>
        <input
          :id="`comment-author-${uid}`"
          ref="nameInput"
          v-model="draftName"
          class="input"
          type="text"
          :maxlength="COMMENT_LIMITS.authorNameMax"
          autocomplete="nickname"
        />
      </div>

      <div class="comment-form__field">
        <label class="comment-form__label" :for="`comment-email-${uid}`">
          邮箱 <span class="comment-form__hint">选填，仅服务端保存、不会公开</span>
        </label>
        <input
          :id="`comment-email-${uid}`"
          v-model="draftEmail"
          class="input"
          type="email"
          autocomplete="email"
        />
      </div>

      <p v-if="dialogError" class="comment-form__error" role="alert">{{ dialogError }}</p>

      <div class="author-dialog__actions">
        <button type="button" class="btn btn--ghost" @click="closeAuthorDialog">取消</button>
        <button type="submit" class="btn">确定并发表</button>
      </div>
    </form>
  </dialog>
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
  /* 错误提示动画（阶段 9 批 2，审计追加项）：每次出现淡入 + 轻微下移；
     时长走令牌，prefers-reduced-motion 下由 base.css 的全局规则自动归零 */
  animation: comment-error-in var(--duration-fast) var(--ease-out) both;
}

@keyframes comment-error-in {
  from {
    opacity: 0;
    transform: translateY(-2px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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

/* 身份行 + 发表按钮同一行：身份在左、按钮在右（阶段 7 批 8 起昵称/邮箱不再占表单位置） */
.comment-form__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 10px 16px;
}

.comment-form__identity {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
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

/* ---- 首次评论的昵称 / 邮箱弹窗（原生 <dialog>，自动获得焦点陷阱与 Esc 关闭）---- */
.author-dialog {
  width: min(420px, calc(100vw - 32px));
  padding: 0;
  color: var(--color-text);
  background-color: var(--color-bg-elevated);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-md);
}

.author-dialog::backdrop {
  background-color: rgba(15, 20, 25, 0.45);
}

.author-dialog__form {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 22px;
}

.author-dialog__title {
  margin: 0;
  font-size: 18px;
}

.author-dialog__hint {
  margin: 0;
  color: var(--color-muted);
  font-size: 13px;
}

.author-dialog__actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
