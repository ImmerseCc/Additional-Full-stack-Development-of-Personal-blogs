// 本机"我赞过哪些文章"账本（阶段 5 批 4，契约第 5 节）：
// 契约规定点赞计数的唯一事实来源是后端；本地这份只用于界面初始化与阶段 5 的"本地数据一览"，
// 绝不作为计数依据。每次打开文章时仍会向后端查一次真实状态（见 LikeButton），并把本地记录对齐。
import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { readJson, writeJson } from '@/utils/storage'

export const LIKED_STORAGE_KEY = 'likedArticles'

function normalize(raw) {
  if (!Array.isArray(raw)) return []
  return [...new Set(raw.filter((id) => Number.isInteger(id) && id > 0))]
}

export const useLikesStore = defineStore('likes', () => {
  const ids = ref(normalize(readJson(LIKED_STORAGE_KEY, [])))

  const count = computed(() => ids.value.length)

  function persist() {
    writeJson(LIKED_STORAGE_KEY, ids.value)
  }

  function has(articleId) {
    return ids.value.includes(Number(articleId))
  }

  function mark(articleId) {
    const id = Number(articleId)
    if (!Number.isInteger(id) || ids.value.includes(id)) return
    ids.value = [...ids.value, id]
    persist()
  }

  function unmark(articleId) {
    const id = Number(articleId)
    const next = ids.value.filter((item) => item !== id)
    if (next.length !== ids.value.length) {
      ids.value = next
      persist()
    }
  }

  // 本地数据被清空（如"一键重置"）后，让内存状态与存储重新对齐
  function reload() {
    ids.value = normalize(readJson(LIKED_STORAGE_KEY, []))
  }

  return { ids, count, has, mark, unmark, reload }
})
