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
    return savedPosition || { top: 0 }
  }
})

router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} · 个人博客` : BASE_TITLE
})

export default router
