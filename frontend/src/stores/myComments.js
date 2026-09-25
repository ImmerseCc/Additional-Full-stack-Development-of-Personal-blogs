// 本机"我发过的评论"账本（阶段 5 批 3，决策 8）：
// 契约的 CommentVO 不回传 visitorId（隐私设计，见 api-contract.md 第 2.4 节），
// 因此前端无法从接口判断"哪条评论是我发的"，只能在本地记下自己创建过的评论 ID，
// 用来决定是否显示删除入口。真正的归属校验始终在后端（visitorId 不匹配一律 40004）。
// 这份数据同时会被阶段 5 的"一键重置"清空，所以状态放在 store 里，重置后调用 reload() 重新对齐。
import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { readJson, writeJson } from '@/utils/storage'

export const MY_COMMENTS_STORAGE_KEY = 'myComments'
const MAX_ENTRIES = 200

function normalize(raw) {
  if (!Array.isArray(raw)) return []
  return raw
    .filter((entry) => entry && Number.isInteger(entry.id) && entry.id > 0)
    .map((entry) => ({
      id: entry.id,
      articleId: Number.isInteger(entry.articleId) ? entry.articleId : null,
      createdAt: typeof entry.createdAt === 'string' ? entry.createdAt : ''
    }))
    .slice(0, MAX_ENTRIES)
}

export const useMyCommentsStore = defineStore('myComments', () => {
  const entries = ref(normalize(readJson(MY_COMMENTS_STORAGE_KEY, [])))

  const count = computed(() => entries.value.length)

  function persist() {
    writeJson(MY_COMMENTS_STORAGE_KEY, entries.value)
  }

  function remember(comment) {
    if (!comment || !Number.isInteger(comment.id)) return
    entries.value = [
      {
        id: comment.id,
        articleId: Number.isInteger(comment.articleId) ? comment.articleId : null,
        createdAt: typeof comment.createdAt === 'string' ? comment.createdAt : ''
      },
      ...entries.value.filter((entry) => entry.id !== comment.id)
    ].slice(0, MAX_ENTRIES)
    persist()
  }

  function forget(commentId) {
    const id = Number(commentId)
    const next = entries.value.filter((entry) => entry.id !== id)
    if (next.length !== entries.value.length) {
      entries.value = next
      persist()
    }
  }

  function isMine(commentId) {
    const id = Number(commentId)
    return entries.value.some((entry) => entry.id === id)
  }

  // 本地数据被清空（如"一键重置"）后，让内存状态与存储重新对齐
  function reload() {
    entries.value = normalize(readJson(MY_COMMENTS_STORAGE_KEY, []))
  }

  return { entries, count, remember, forget, isMine, reload }
})
