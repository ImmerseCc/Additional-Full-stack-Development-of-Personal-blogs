// 滚动 + requestAnimationFrame 合并（阶段 9 批 2，审计 A 静态层 9-1）。
// 把"scroll / resize 事件只登记一帧、真正的计算放进 rAF、卸载时取消并解绑"这套范式收敛到一处，
// 供目录高亮（utils/scrollSpy.js）、回到顶部（components/BackToTop.vue）、
// 阅读进度条（components/ReadingProgress.vue）共用；三处原先各写一份。
// 挂载时先立即跑一次 measure()（首屏可能已经处于滚动位置），卸载时自动 stop()。
import { onBeforeUnmount, onMounted } from 'vue'

/**
 * @param {() => void} measure 每帧最多执行一次的测量回调
 * @returns {{ schedule: () => void, measure: () => void }} schedule 供事件与外部触发，measure 供外部立即重算
 */
export function useScrollFrame(measure) {
  let frame = 0

  function schedule() {
    if (frame) return
    frame = window.requestAnimationFrame(() => {
      frame = 0
      measure()
    })
  }

  function stop() {
    if (frame) {
      window.cancelAnimationFrame(frame)
      frame = 0
    }
    window.removeEventListener('scroll', schedule)
    window.removeEventListener('resize', schedule)
  }

  onMounted(() => {
    measure()
    window.addEventListener('scroll', schedule, { passive: true })
    window.addEventListener('resize', schedule)
  })

  onBeforeUnmount(stop)

  return { schedule, measure }
}
