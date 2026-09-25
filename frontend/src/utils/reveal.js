// 滚动进场动画（阶段 4 批 4，决策 Z）：用 IntersectionObserver 给元素加一次性的"显现"效果。
// 以全局指令形式提供（在 main.js 注册 `v-reveal`），用法：
//   <li v-reveal>                  立刻参与
//   <li v-reveal="{ delay: 80 }">  延迟 80ms（列表里按 index 做错落感）
//
// 两条**可见性优先**的约定（踩过坑后加的，见 docs/debug-log.md 观察项）：
//   1. 挂载时就在视口内的元素**不等 IntersectionObserver**，直接排一次显现——
//      否则在标签页被节流/未渲染时，首屏内容会一直停在 opacity:0（看起来像页面坏了）；
//   2. 系统开启"减少动态效果"或浏览器不支持 IntersectionObserver 时，不添加初始隐藏态。
// 只有"挂载时在视口外"的元素才交给 IntersectionObserver，用于滚动到附近时再显现。
const REVEAL_CLASS = 'reveal'
const REVEALED_CLASS = 'is-revealed'
const REDUCED_MOTION_QUERY = '(prefers-reduced-motion: reduce)'
// 视口内元素延迟一瞬再显现，让 CSS 过渡有机会播出来（不是 rAF：rAF 在后台标签页不触发）
const IMMEDIATE_DELAY = 20

function prefersReducedMotion() {
  return typeof window !== 'undefined' && window.matchMedia(REDUCED_MOTION_QUERY).matches
}

function isInViewport(el) {
  const rect = el.getBoundingClientRect()
  return rect.top < window.innerHeight && rect.bottom > 0
}

export const revealDirective = {
  mounted(el, binding) {
    if (prefersReducedMotion() || typeof IntersectionObserver === 'undefined') return

    const delay = Number(binding.value && binding.value.delay) || 0
    if (delay > 0) el.style.setProperty('--reveal-delay', `${delay}ms`)
    el.classList.add(REVEAL_CLASS)

    if (isInViewport(el)) {
      el.__revealTimer = window.setTimeout(() => {
        delete el.__revealTimer
        el.classList.add(REVEALED_CLASS)
      }, IMMEDIATE_DELAY)
      return
    }

    const observer = new IntersectionObserver(
      (entries, self) => {
        for (const entry of entries) {
          if (!entry.isIntersecting) continue
          entry.target.classList.add(REVEALED_CLASS)
          self.unobserve(entry.target) // 只播一次，来回滚动不重复
        }
      },
      // 元素露出一部分、且不是刚贴到视口下边缘时就触发
      { rootMargin: '0px 0px -8% 0px', threshold: 0.04 }
    )

    observer.observe(el)
    el.__revealObserver = observer
  },

  unmounted(el) {
    if (el.__revealTimer) window.clearTimeout(el.__revealTimer)
    el.__revealObserver?.disconnect()
    delete el.__revealTimer
    delete el.__revealObserver
  }
}
