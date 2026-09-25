<script setup>
// 本地数据管理（阶段 5 批 5，模块六）：把浏览器里存了什么摊开给用户看，并提供一键重置。
// 设计要点：
//  - 概览只读 localStorage 的 `blog:` 前缀键（一律经 utils/storage.js），不发任何后端请求；
//  - 重置 = 清空全部 `blog:` 键 + 把内存里的 store 拉回默认（主题回"跟随系统"、点赞与评论账本清空），
//    并弹 Toast 告知；访客标识不做缓存，清掉后下次用到会重新生成；
//  - 二次确认用行内展开，不用 window.confirm（与评论删除保持一致）。
import { computed, nextTick, ref, watch } from 'vue'
import { clearAll, listKeys, readRaw } from '@/utils/storage'
import { THEME_LABELS, useThemeStore } from '@/stores/theme'
import { useLikesStore } from '@/stores/likes'
import { useMyCommentsStore } from '@/stores/myComments'
import { useToastStore } from '@/stores/toast'
import { VISITOR_STORAGE_KEY } from '@/utils/visitor'

const AUTHOR_STORAGE_KEY = 'commentAuthor'
// 阶段 7 批 8：首次评论弹窗里填的邮箱也记在本机（仅本地保存，服务端只存不返）
const EMAIL_STORAGE_KEY = 'commentEmail'

const theme = useThemeStore()
const likes = useLikesStore()
const myComments = useMyCommentsStore()
const toast = useToastStore()

const confirming = ref(false)
const snapshot = ref(buildSnapshot())

function buildSnapshot() {
  return {
    keys: listKeys(),
    visitorId: readRaw(VISITOR_STORAGE_KEY),
    authorName: readRaw(AUTHOR_STORAGE_KEY) || '',
    authorEmail: readRaw(EMAIL_STORAGE_KEY) || ''
  }
}

function maskVisitorId(value) {
  if (!value) return '尚未生成'
  return value.length > 14 ? `${value.slice(0, 8)}…${value.slice(-4)}` : value
}

const rows = computed(() => [
  { label: '主题偏好', value: THEME_LABELS[theme.mode] || theme.mode, key: 'theme' },
  { label: '访客标识', value: maskVisitorId(snapshot.value.visitorId), key: 'visitorId' },
  { label: '已点赞文章', value: `${likes.count} 篇`, key: 'likedArticles' },
  { label: '我发过的评论', value: `${myComments.count} 条`, key: 'myComments' },
  { label: '评论昵称记忆', value: snapshot.value.authorName || '未记录', key: 'commentAuthor' },
  { label: '评论邮箱记忆', value: snapshot.value.authorEmail || '未记录', key: 'commentEmail' }
])

const keyList = computed(() => (snapshot.value.keys.length ? snapshot.value.keys.join('、') : '（无）'))

// 键列表与访客标识直接读 localStorage（不是响应式数据）：在"会写键"的操作（主题 / 点赞 / 评论账本）变化后重建。
// flush: 'post' 保证主题 store 的 watch 已经落盘，读到的列表才是准的。
function refresh() {
  snapshot.value = buildSnapshot()
}

watch([() => theme.mode, () => likes.count, () => myComments.count], refresh, { flush: 'post' })

async function reset() {
  const removed = clearAll()
  // 内存状态同步回默认：主题按默认值重新落盘（跟随系统），两个账本清空
  theme.setMode('system')
  likes.reload()
  myComments.reload()
  // 主题 store 的 watch 在下一个 tick 才把默认值写回存储，等一拍再取快照，键列表才准确
  await nextTick()
  refresh()
  confirming.value = false
  toast.push(`本地数据已重置（清理 ${removed} 项）`, { type: 'success' })
}
</script>

<template>
  <section class="local-data" aria-labelledby="local-data-title">
    <h2 id="local-data-title" class="local-data__title">本地数据</h2>
    <p class="local-data__lead">
      下面这些只存在你这台浏览器里：用来记住主题偏好、标识访客身份，以及判断哪些评论和点赞是你自己的。
      它们不会上传到服务端 —— 服务端保存的是文章正文、评论内容与点赞记录本身。
    </p>

    <dl class="local-data__list">
      <div v-for="row in rows" :key="row.key" class="local-data__row">
        <dt class="local-data__label">
          {{ row.label }}
          <code class="local-data__key">blog:{{ row.key }}</code>
        </dt>
        <dd class="local-data__value">{{ row.value }}</dd>
      </div>
    </dl>

    <p class="local-data__keys">当前占用的键：{{ keyList }}</p>

    <div class="local-data__actions">
      <template v-if="!confirming">
        <button type="button" class="btn btn--ghost" @click="confirming = true">重置本地数据</button>
        <span class="local-data__hint">
          重置后主题回到「跟随系统」，访客标识会重新生成；服务端的文章、评论与点赞记录不受影响。
        </span>
      </template>
      <template v-else>
        <span class="local-data__confirm">确定清空全部本地数据？</span>
        <button type="button" class="btn" @click="reset">确认重置</button>
        <button type="button" class="btn btn--ghost" @click="confirming = false">取消</button>
      </template>
    </div>
  </section>
</template>

<style scoped>
.local-data {
  margin-top: 40px;
  padding-top: 28px;
  border-top: 1px solid var(--color-border);
}

.local-data__title {
  margin: 0 0 12px;
  font-size: 22px;
}

.local-data__lead {
  max-width: 68ch;
  margin: 0 0 20px;
  color: var(--color-muted);
  font-size: 15px;
}

.local-data__list {
  margin: 0 0 16px;
  padding: 0;
}

.local-data__row {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px 16px;
  padding: 10px 0;
  border-bottom: 1px solid var(--color-border);
}

.local-data__label {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 8px;
  color: var(--color-text);
  font-size: 14px;
}

.local-data__key {
  color: var(--color-muted);
  font-size: 12px;
}

.local-data__value {
  margin: 0;
  color: var(--color-accent);
  font-size: 14px;
  font-variant-numeric: tabular-nums;
}

.local-data__keys {
  margin: 0 0 20px;
  color: var(--color-muted);
  font-size: 13px;
  overflow-wrap: anywhere;
}

.local-data__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.local-data__confirm {
  color: var(--color-text);
  font-size: 14px;
}

.local-data__hint {
  flex: 1;
  min-width: 240px;
  color: var(--color-muted);
  font-size: 13px;
}
</style>
