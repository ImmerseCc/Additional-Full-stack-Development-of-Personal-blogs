<script setup>
// 写作台（阶段 8 批 7，决策 BR）：隐藏入口 /studio，不出现在导航栏。
// 极简：文章新建 / 编辑 / 删除 + PUBLISHED/DRAFT 切换 + 标签管理。
// 演示级：不做登录、不做富文本，正文就是 Markdown 文本框；正文渲染仍由详情页的管线完成。
// 公网部署前必须补鉴权（与后端 API 同源风险，见 README「已知问题」）。
import { computed, onMounted, ref } from 'vue'
import EmptyState from '@/components/EmptyState.vue'
import {
  createArticle,
  deleteArticle,
  fetchArticleDetail,
  fetchArticles,
  updateArticle
} from '@/api/articles'
import { createTag, deleteTag, fetchTags, renameTag } from '@/api/tags'
import { useToastStore } from '@/stores/toast'
import { formatDateTime } from '@/utils/date'

const MAX_TAGS = 5

const toast = useToastStore()

// ── 文章区 ──────────────────────────────────────────────────────
const articles = ref([])
const articlesTotal = ref(0)
const articlesLoading = ref(true)
const articlesError = ref(null)
const confirmingDeleteId = ref(null)
const deletingId = ref(null)
const togglingId = ref(null)

// 表单：null = 收起；否则 { id: null|number, ... }（id 为空表示新建）
const form = ref(null)
const formLoading = ref(false)
const saving = ref(false)
const formError = ref(null)
const formMode = computed(() => (form.value && form.value.id ? 'edit' : 'create'))

function openCreate() {
  formError.value = null
  formLoading.value = false
  form.value = { id: null, title: '', summary: '', coverUrl: '', status: 'PUBLISHED', tagsText: '', content: '' }
}

async function openEdit(article) {
  formError.value = null
  formLoading.value = true
  confirmingDeleteId.value = null
  form.value = {
    id: article.id,
    title: article.title,
    summary: article.summary || '',
    coverUrl: article.coverUrl || '',
    status: article.status,
    tagsText: (article.tags || []).join(', '),
    content: ''
  }
  try {
    // 列表接口不带正文，编辑前先取详情；取回前禁用保存，避免把空正文写回去
    const detail = await fetchArticleDetail(article.id)
    if (form.value && form.value.id === article.id) {
      form.value.content = detail.content
    }
  } catch (caught) {
    formError.value = `正文加载失败：${caught.message}`
  } finally {
    formLoading.value = false
  }
}

function closeForm() {
  form.value = null
  formError.value = null
  formLoading.value = false
}

function parseTags(text) {
  return [...new Set(text.split(',').map((name) => name.trim()).filter(Boolean))]
}

async function saveForm() {
  if (saving.value || formLoading.value || !form.value) return
  const payload = {
    title: form.value.title.trim(),
    summary: form.value.summary.trim(),
    coverUrl: form.value.coverUrl.trim(),
    status: form.value.status,
    tags: parseTags(form.value.tagsText),
    content: form.value.content
  }
  // 轻量客户端校验（服务端仍会再校验一遍，字段级原因由 40001 返回）
  // 阶段 9 批 2（审计 6-3）：补齐与后端 DTO 相同的长度上限（ArticleCreateRequest / ArticleUpdateRequest）
  const LIMITS = { title: 100, summary: 200, coverUrl: 200, content: 50000 }
  const errors = []
  if (payload.title === '') errors.push('标题不能为空')
  else if (payload.title.length > LIMITS.title) errors.push(`标题不能超过 ${LIMITS.title} 字`)
  if (payload.content.trim() === '') errors.push('正文不能为空')
  else if (payload.content.length > LIMITS.content) errors.push(`正文不能超过 ${LIMITS.content} 字`)
  if (payload.summary.length > LIMITS.summary) errors.push(`摘要不能超过 ${LIMITS.summary} 字`)
  if (payload.coverUrl.length > LIMITS.coverUrl) errors.push(`封面地址不能超过 ${LIMITS.coverUrl} 字`)
  if (payload.tags.length > MAX_TAGS) errors.push(`标签最多 ${MAX_TAGS} 个`)
  if (errors.length > 0) {
    formError.value = errors.join('；')
    return
  }

  saving.value = true
  formError.value = null
  try {
    if (formMode.value === 'edit') {
      await updateArticle(form.value.id, payload)
      toast.push('文章已更新', { type: 'success' })
    } else {
      await createArticle(payload)
      toast.push('文章已创建', { type: 'success' })
    }
    closeForm()
    await loadArticles()
  } catch (caught) {
    const fieldReasons = caught.fields ? Object.values(caught.fields).join('；') : ''
    formError.value = fieldReasons || caught.message
  } finally {
    saving.value = false
  }
}

/** 发布 / 转草稿：PUT 是全量语义，先取详情把正文与其余字段原样带回 */
async function toggleStatus(article) {
  if (togglingId.value) return
  togglingId.value = article.id
  try {
    const detail = await fetchArticleDetail(article.id)
    const nextStatus = article.status === 'PUBLISHED' ? 'DRAFT' : 'PUBLISHED'
    await updateArticle(article.id, {
      title: detail.title,
      summary: detail.summary || '',
      coverUrl: detail.coverUrl || '',
      status: nextStatus,
      tags: detail.tags || [],
      content: detail.content
    })
    // 转草稿会让文章从前台列表消失，用 warning 类型提示（阶段 9 批 2：Toast 补齐 warning）
    if (nextStatus === 'PUBLISHED') {
      toast.push('已发布', { type: 'success' })
    } else {
      toast.push('已转为草稿，前台列表不再展示', { type: 'warning' })
    }
    await loadArticles()
  } catch (caught) {
    toast.push(`操作失败：${caught.message}`, { type: 'error' })
  } finally {
    togglingId.value = null
  }
}

async function removeArticle(id) {
  if (deletingId.value) return
  deletingId.value = id
  try {
    await deleteArticle(id)
    toast.push('文章已删除', { type: 'success' })
    confirmingDeleteId.value = null
    if (form.value && form.value.id === id) closeForm()
    await loadArticles()
  } catch (caught) {
    toast.push(`删除失败：${caught.message}`, { type: 'error' })
  } finally {
    deletingId.value = null
  }
}

async function loadArticles() {
  articlesLoading.value = true
  articlesError.value = null
  try {
    const data = await fetchArticles({ status: 'ALL', page: 1, size: 20 })
    articles.value = data.items
    articlesTotal.value = data.total
  } catch (caught) {
    articlesError.value = caught
    articles.value = []
    articlesTotal.value = 0
  } finally {
    articlesLoading.value = false
  }
}

// ── 标签区 ──────────────────────────────────────────────────────
const tags = ref([])
const tagsLoading = ref(true)
const tagsError = ref(null)
const newTagName = ref('')
const creatingTag = ref(false)
const editingTagId = ref(null)
const editingTagName = ref('')
const tagBusyId = ref(null)
const confirmingTagDeleteId = ref(null)

async function loadTags() {
  tagsLoading.value = true
  tagsError.value = null
  try {
    tags.value = await fetchTags()
  } catch (caught) {
    tagsError.value = caught
    tags.value = []
  } finally {
    tagsLoading.value = false
  }
}

async function submitNewTag() {
  const name = newTagName.value.trim()
  if (creatingTag.value || name === '') return
  creatingTag.value = true
  try {
    await createTag(name)
    toast.push(`标签「${name}」已创建`, { type: 'success' })
    newTagName.value = ''
    await loadTags()
  } catch (caught) {
    toast.push(`创建失败：${caught.message}`, { type: 'error' })
  } finally {
    creatingTag.value = false
  }
}

function startRename(tag) {
  confirmingTagDeleteId.value = null
  editingTagId.value = tag.id
  editingTagName.value = tag.name
}

function cancelRename() {
  editingTagId.value = null
  editingTagName.value = ''
}

async function submitRename(tag) {
  const name = editingTagName.value.trim()
  if (tagBusyId.value || name === '') return
  tagBusyId.value = tag.id
  try {
    await renameTag(tag.id, name)
    toast.push('标签已改名', { type: 'success' })
    cancelRename()
    await loadTags()
  } catch (caught) {
    toast.push(`改名失败：${caught.message}`, { type: 'error' })
  } finally {
    tagBusyId.value = null
  }
}

async function removeTag(tag) {
  if (tagBusyId.value) return
  tagBusyId.value = tag.id
  try {
    await deleteTag(tag.id)
    toast.push(`标签「${tag.name}」已删除`, { type: 'success' })
    confirmingTagDeleteId.value = null
    if (editingTagId.value === tag.id) cancelRename()
    await loadTags()
  } catch (caught) {
    toast.push(`删除失败：${caught.message}`, { type: 'error' })
  } finally {
    tagBusyId.value = null
  }
}

onMounted(() => {
  loadArticles()
  loadTags()
})
</script>

<template>
  <section class="page studio">
    <header class="studio__head">
      <h1 class="studio__title">写作台</h1>
      <p class="studio__hint">
        隐藏入口（不在导航栏）。演示级：<strong>无登录、无鉴权</strong>，请勿部署到公网；
        正文为 Markdown 原文，保存后由详情页渲染。
      </p>
    </header>

    <!-- 文章管理 -->
    <section class="studio__section" aria-labelledby="studio-articles-title">
      <div class="studio__section-head">
        <h2 id="studio-articles-title" class="studio__section-title">
          文章
          <span v-if="articlesTotal > 0" class="studio__count">{{ articlesTotal }}</span>
        </h2>
        <div class="studio__section-actions">
          <button type="button" class="btn" @click="openCreate">新建文章</button>
          <button type="button" class="btn btn--ghost" :disabled="articlesLoading" @click="loadArticles">刷新</button>
        </div>
      </div>

      <form v-if="form" class="studio-form" @submit.prevent="saveForm">
        <h3 class="studio-form__title">
          {{ formMode === 'edit' ? `编辑文章 #${form.id}` : '新建文章' }}
        </h3>
        <div class="studio-form__grid">
          <label class="studio-form__field studio-form__field--wide">
            <span class="studio-form__label">标题 <span class="studio-form__required">*</span></span>
            <input v-model="form.title" class="input" maxlength="100" />
          </label>
          <label class="studio-form__field">
            <span class="studio-form__label">状态</span>
            <select v-model="form.status" class="input">
              <option value="PUBLISHED">已发布</option>
              <option value="DRAFT">草稿</option>
            </select>
          </label>
          <label class="studio-form__field">
            <span class="studio-form__label">标签（逗号分隔，最多 {{ MAX_TAGS }} 个）</span>
            <input v-model="form.tagsText" class="input" placeholder="Vue, 前端" />
          </label>
          <label class="studio-form__field studio-form__field--wide">
            <span class="studio-form__label">摘要（留空时后端取正文前 120 字）</span>
            <input v-model="form.summary" class="input" maxlength="200" />
          </label>
          <label class="studio-form__field studio-form__field--wide">
            <span class="studio-form__label">封面图地址（可选）</span>
            <input v-model="form.coverUrl" class="input" placeholder="/images/covers/xxx.svg" />
          </label>
          <label class="studio-form__field studio-form__field--wide">
            <span class="studio-form__label">
              正文（Markdown 原文）<span class="studio-form__required">*</span>
            </span>
            <textarea
              v-model="form.content"
              class="input studio-form__content"
              rows="12"
              :placeholder="formLoading ? '正文加载中…' : '# 标题'"
            ></textarea>
          </label>
        </div>
        <p v-if="formError" class="studio-form__error" role="alert">{{ formError }}</p>
        <div class="studio-form__actions">
          <button type="submit" class="btn" :disabled="saving || formLoading">
            {{ saving ? '保存中…' : formLoading ? '加载中…' : '保存' }}
          </button>
          <button type="button" class="btn btn--ghost" :disabled="saving" @click="closeForm">取消</button>
        </div>
      </form>

      <p v-if="articlesLoading" class="studio__state" aria-busy="true">文章加载中…</p>
      <div v-else-if="articlesError" class="studio__state" role="alert">
        <span>文章加载失败：{{ articlesError.message }}</span>
        <button type="button" class="btn btn--ghost" @click="loadArticles">重试</button>
      </div>
      <EmptyState v-else-if="articles.length === 0" title="还没有文章" description="点「新建文章」写第一篇" />
      <ul v-else class="studio-list">
        <li v-for="item in articles" :key="item.id" class="studio-list__row">
          <div class="studio-list__main">
            <span class="studio-badge" :class="`studio-badge--${item.status.toLowerCase()}`">
              {{ item.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </span>
            <span class="studio-list__title">{{ item.title }}</span>
            <span class="studio-list__meta">更新于 {{ formatDateTime(item.updatedAt) }}</span>
          </div>
          <div class="studio-list__actions">
            <button type="button" class="studio-action" @click="openEdit(item)">编辑</button>
            <button
              type="button"
              class="studio-action"
              :disabled="togglingId === item.id"
              @click="toggleStatus(item)"
            >
              {{ togglingId === item.id ? '处理中…' : item.status === 'PUBLISHED' ? '转草稿' : '发布' }}
            </button>
            <template v-if="confirmingDeleteId !== item.id">
              <button type="button" class="studio-action" @click="confirmingDeleteId = item.id">删除</button>
            </template>
            <template v-else>
              <span class="studio-list__confirm">确认删除？</span>
              <button
                type="button"
                class="studio-action studio-action--danger"
                :disabled="deletingId === item.id"
                @click="removeArticle(item.id)"
              >
                {{ deletingId === item.id ? '删除中…' : '确认删除' }}
              </button>
              <button type="button" class="studio-action" @click="confirmingDeleteId = null">取消</button>
            </template>
          </div>
        </li>
      </ul>
      <p v-if="articlesTotal > articles.length" class="studio__note">
        仅显示前 {{ articles.length }} 篇（共 {{ articlesTotal }} 篇）。
      </p>
    </section>

    <!-- 标签管理 -->
    <section class="studio__section" aria-labelledby="studio-tags-title">
      <div class="studio__section-head">
        <h2 id="studio-tags-title" class="studio__section-title">
          标签
          <span v-if="tags.length > 0" class="studio__count">{{ tags.length }}</span>
        </h2>
        <div class="studio__section-actions">
          <label class="visually-hidden" for="studio-new-tag">新标签名</label>
          <input
            id="studio-new-tag"
            v-model="newTagName"
            class="input studio-tags__input"
            maxlength="20"
            placeholder="新标签名（1-20 字）"
            @keyup.enter="submitNewTag"
          />
          <button type="button" class="btn" :disabled="creatingTag || newTagName.trim() === ''" @click="submitNewTag">
            {{ creatingTag ? '创建中…' : '新建标签' }}
          </button>
        </div>
      </div>

      <p v-if="tagsLoading" class="studio__state" aria-busy="true">标签加载中…</p>
      <div v-else-if="tagsError" class="studio__state" role="alert">
        <span>标签加载失败：{{ tagsError.message }}</span>
        <button type="button" class="btn btn--ghost" @click="loadTags">重试</button>
      </div>
      <ul v-else class="studio-list">
        <li v-for="tag in tags" :key="tag.id" class="studio-list__row">
          <template v-if="editingTagId === tag.id">
            <input
              v-model="editingTagName"
              class="input studio-tags__input"
              maxlength="20"
              :aria-label="`修改标签「${tag.name}」`"
              @keyup.enter="submitRename(tag)"
            />
            <div class="studio-list__actions">
              <button type="button" class="studio-action" :disabled="tagBusyId === tag.id" @click="submitRename(tag)">
                {{ tagBusyId === tag.id ? '保存中…' : '保存' }}
              </button>
              <button type="button" class="studio-action" @click="cancelRename">取消</button>
            </div>
          </template>
          <template v-else>
            <div class="studio-list__main">
              <span class="studio-list__title">{{ tag.name }}</span>
              <span class="studio-list__meta">{{ tag.articleCount }} 篇文章</span>
            </div>
            <div class="studio-list__actions">
              <button type="button" class="studio-action" @click="startRename(tag)">改名</button>
              <template v-if="confirmingTagDeleteId !== tag.id">
                <button type="button" class="studio-action" @click="confirmingTagDeleteId = tag.id">删除</button>
              </template>
              <template v-else>
                <span class="studio-list__confirm">确认删除？</span>
                <button
                  type="button"
                  class="studio-action studio-action--danger"
                  :disabled="tagBusyId === tag.id"
                  @click="removeTag(tag)"
                >
                  {{ tagBusyId === tag.id ? '删除中…' : '确认删除' }}
                </button>
                <button type="button" class="studio-action" @click="confirmingTagDeleteId = null">取消</button>
              </template>
            </div>
          </template>
        </li>
      </ul>
    </section>
  </section>
</template>

<style scoped>
.studio__head {
  margin-bottom: 28px;
}

.studio__title {
  margin: 0 0 8px;
  font-size: 30px;
}

.studio__hint {
  margin: 0;
  color: var(--color-muted);
  font-size: 14px;
  line-height: 1.7;
}

.studio__section {
  margin-bottom: 40px;
  padding-top: 24px;
  border-top: 1px solid var(--color-border);
}

.studio__section-head {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.studio__section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 20px;
}

.studio__count {
  padding: 1px 10px;
  color: var(--color-accent);
  background-color: var(--color-accent-soft);
  border-radius: var(--radius-pill);
  font-size: 13px;
  font-weight: 600;
}

.studio__section-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.studio__state {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin: 0;
  padding: 14px 18px;
  color: var(--color-muted);
  background-color: var(--color-bg-soft);
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-card);
  font-size: 14px;
}

.studio__note {
  margin: 12px 0 0;
  color: var(--color-muted);
  font-size: 13px;
}

/* 编辑表单 */
.studio-form {
  margin-bottom: 20px;
  padding: 18px 20px;
  background-color: var(--color-bg-soft);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
}

.studio-form__title {
  margin: 0 0 14px;
  font-size: 16px;
}

.studio-form__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 14px;
}

.studio-form__field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.studio-form__field--wide {
  grid-column: 1 / -1;
}

.studio-form__label {
  color: var(--color-muted);
  font-size: 13px;
}

.studio-form__required {
  color: #d1242f;
}

[data-theme='dark'] .studio-form__required {
  color: #ff7b72;
}

.studio-form__content {
  min-height: 220px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
  font-size: 13px;
  line-height: 1.7;
  resize: vertical;
}

.studio-form__error {
  margin: 12px 0 0;
  color: #d1242f;
  font-size: 13px;
}

[data-theme='dark'] .studio-form__error {
  color: #ff7b72;
}

.studio-form__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

/* 列表 */
.studio-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.studio-list__row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 12px 4px;
  border-bottom: 1px solid var(--color-border);
}

.studio-list__main {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.studio-list__title {
  color: var(--color-text);
  font-size: 15px;
  overflow-wrap: anywhere;
}

.studio-list__meta {
  color: var(--color-muted);
  font-size: 12px;
}

.studio-list__confirm {
  color: var(--color-muted);
  font-size: 13px;
}

.studio-list__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.studio-action {
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

.studio-action:hover {
  color: var(--color-accent);
  border-color: var(--color-accent);
}

.studio-action--danger:hover {
  color: #d1242f;
  border-color: #d1242f;
}

[data-theme='dark'] .studio-action--danger:hover {
  color: #ff7b72;
  border-color: #ff7b72;
}

.studio-action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 状态徽标 */
.studio-badge {
  flex: none;
  padding: 1px 8px;
  border-radius: var(--radius-pill);
  font-size: 12px;
}

.studio-badge--published {
  color: #1a7f37;
  background-color: rgba(26, 127, 55, 0.12);
}

[data-theme='dark'] .studio-badge--published {
  color: #7ee2a8;
  background-color: rgba(126, 226, 168, 0.16);
}

.studio-badge--draft {
  color: var(--color-muted);
  background-color: var(--color-bg-soft);
  border: 1px solid var(--color-border);
}

.studio-tags__input {
  width: 200px;
}
</style>
