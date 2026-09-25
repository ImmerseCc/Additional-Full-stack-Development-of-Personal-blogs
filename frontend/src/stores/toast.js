// 轻量 Toast（阶段 5 批 4）：最多同时显示 3 条，默认 2.6s 自动消失，也可手动关闭。
// 只存在内存里、不落 localStorage —— 提示属于"当下发生的事"，刷新后不该复现。
// 定时器集中管理：被挤掉的（超过上限）与手动关闭的都会 clearTimeout，避免悬挂回调。
import { ref } from 'vue'
import { defineStore } from 'pinia'

export const TOAST_MAX_VISIBLE = 3
export const TOAST_DEFAULT_DURATION = 2600

export const useToastStore = defineStore('toast', () => {
  const items = ref([])
  const timers = new Map()
  let nextId = 0

  function dismiss(id) {
    const timer = timers.get(id)
    if (timer) {
      clearTimeout(timer)
      timers.delete(id)
    }
    items.value = items.value.filter((item) => item.id !== id)
  }

  /**
   * 弹出一条提示。
   * @param {string} message 文案（为空则忽略）
   * @param {{type?: 'info'|'success'|'error', duration?: number}} [options] duration 为 0 表示不自动消失
   * @returns {number|null} 提示 ID
   */
  function push(message, { type = 'info', duration = TOAST_DEFAULT_DURATION } = {}) {
    if (!message) return null
    const id = ++nextId
    const next = [...items.value, { id, message, type }]
    while (next.length > TOAST_MAX_VISIBLE) {
      const dropped = next.shift()
      const timer = timers.get(dropped.id)
      if (timer) {
        clearTimeout(timer)
        timers.delete(dropped.id)
      }
    }
    items.value = next
    if (duration > 0) {
      timers.set(
        id,
        setTimeout(() => dismiss(id), duration)
      )
    }
    return id
  }

  function clear() {
    for (const timer of timers.values()) clearTimeout(timer)
    timers.clear()
    items.value = []
  }

  return { items, push, dismiss, clear }
})
