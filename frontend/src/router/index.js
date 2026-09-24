import { createRouter, createWebHistory } from 'vue-router'

// 路由表占位：目前只有首页（懒加载）与兜底重定向；
// 阶段 4/5 会补上文章详情页、关于页与 404 页，并加入路由过渡动画。
const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  }
})

export default router
