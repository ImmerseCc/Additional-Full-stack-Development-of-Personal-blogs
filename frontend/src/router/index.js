import { createRouter, createWebHistory } from 'vue-router'

const BASE_TITLE = '个人博客 · VibeCoding'

// 路由表（阶段 3 批 2）：三条正式路由 + 404 兜底（决策 K / L）。
// 阶段 4 会增加文章详情页（/articles/:id），复用同一套 meta.title → document.title 同步逻辑。
const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/articles',
    name: 'articles',
    component: () => import('@/views/ArticlesView.vue'),
    meta: { title: '文章列表' }
  },
  {
    // 详情页（阶段 4 批 2 建路由 + 占位页，批 3 实现内容）
    path: '/articles/:id',
    name: 'article-detail',
    component: () => import('@/views/ArticleDetailView.vue'),
    meta: { title: '文章详情' }
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('@/views/AboutView.vue'),
    meta: { title: '关于' }
  },
  {
    // 隐藏入口（阶段 8 批 7，决策 BR）：写作台，不在导航栏暴露；演示级无鉴权，页面内已标注
    path: '/studio',
    name: 'studio',
    component: () => import('@/views/StudioView.vue'),
    meta: { title: '写作台' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundView.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 浏览器前进 / 后退：恢复原位置
    if (savedPosition) return savedPosition
    // 同路径、仅查询串变化的导航不算"翻到新页面" —— 例如列表页滚动到底追加数据时，
    // 用 replace 同步 ?page=2；若照旧返回 { top: 0 }，会把正在向下浏览的用户直接弹回顶部
    // （阶段 9 验收后实测复现，见 docs/debug-log.md 报错记录 11）。
    // 返回 false 表示"不处理滚动"，保持当前位置。
    if (to.path === from.path) return false
    return { top: 0 }
  }
})

router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} · 个人博客` : BASE_TITLE
})

export default router
