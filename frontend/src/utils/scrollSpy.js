// 目录滚动高亮（阶段 4 批 4，决策 Z）：用 scroll + getBoundingClientRect 判断"当前读到哪一节"。
// 这里刻意不用 IntersectionObserver——需要的是"最后一个越过页头线的小节"，
// 直接比较各标题的位置最直观，也便于在滚到底部时兜底高亮最后一节。
import { onBeforeUnmount, onMounted, ref } from 'vue'

/**
 * @param {() => Array<{id: string}>} getHeadings 返回当前目录项（组件里传 `() => headings.value`）
 * @param {{offset?: number}} [options] offset 为"页头线"距视口顶部的距离，默认 96（页头 64 + 余量）
 */
export function useScrollSpy(getHeadings, options = {}) {
  const offset = options.offset ?? 96
  const activeId = ref('')
  let frame = 0

  function measure() {
    frame = 0
    const headings = getHeadings()
    if (!headings.length) {
      activeId.value = ''
      return
    }

    let current = headings[0].id
    for (const heading of headings) {
      const element = document.getElementById(heading.id)
      if (!element) continue
      if (element.getBoundingClientRect().top - offset <= 0) current = heading.id
      else break
    }

    // 已经滚到底时高亮最后一节，否则最后一个小节可能永远高亮不到
    const scrolledToBottom =
      window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 4

    activeId.value = scrolledToBottom ? headings[headings.length - 1].id : current
  }

  // 滚动事件里只登记一帧，真正的计算放在 requestAnimationFrame 中
  function schedule() {
    if (frame) return
    frame = window.requestAnimationFrame(measure)
  }

  onMounted(() => {
    measure()
    window.addEventListener('scroll', schedule, { passive: true })
    window.addEventListener('resize', schedule)
  })

  onBeforeUnmount(() => {
    if (frame) window.cancelAnimationFrame(frame)
    window.removeEventListener('scroll', schedule)
    window.removeEventListener('resize', schedule)
  })

  return { activeId, measure }
}
