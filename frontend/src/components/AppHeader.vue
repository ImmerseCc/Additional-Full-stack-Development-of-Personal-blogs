<script setup>
import { useRoute } from 'vue-router'
import ThemeToggle from '@/components/ThemeToggle.vue'

// 全局导航（决策 K）：首页 / 文章列表 / 关于，右侧固定主题按钮（决策 N）。
// 当前页高亮以 aria-current="page" 为准（无障碍可读），不完全依赖 RouterLink 的默认类名。
// 移动端（< 768px）的折叠菜单与滚动样式变化在批 3 实现，本批先保证窄屏布局不溢出。
const NAV_ITEMS = [
  { to: '/', label: '首页' },
  { to: '/articles', label: '文章列表' },
  { to: '/about', label: '关于' }
]

const route = useRoute()

function isActive(to) {
  return to === '/' ? route.path === '/' : route.path.startsWith(to)
}
</script>

<template>
  <header class="app-header">
    <div class="app-header__inner">
      <RouterLink to="/" class="brand">
        个人博客<span class="brand__suffix"> · VibeCoding</span>
      </RouterLink>
      <nav class="app-nav" aria-label="主导航">
        <RouterLink
          v-for="item in NAV_ITEMS"
          :key="item.to"
          :to="item.to"
          class="app-nav__link"
          :class="{ 'app-nav__link--active': isActive(item.to) }"
          :aria-current="isActive(item.to) ? 'page' : undefined"
        >
          {{ item.label }}
        </RouterLink>
      </nav>
      <ThemeToggle />
    </div>
  </header>
</template>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 20;
  border-bottom: 1px solid var(--color-border);
  background-color: var(--color-bg-elevated);
  transition: var(--transition-theme);
}

.app-header__inner {
  display: flex;
  align-items: center;
  gap: 12px;
  max-width: var(--content-max);
  min-height: var(--header-height);
  margin: 0 auto;
  padding: 8px 20px;
}

.brand {
  font-size: 1.05rem;
  font-weight: 600;
  color: var(--color-text);
  text-decoration: none;
  white-space: nowrap;
}

.app-nav {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}

.app-nav__link {
  padding: 6px 10px;
  border-radius: var(--radius-sm);
  color: var(--color-muted);
  text-decoration: none;
  white-space: nowrap;
  transition:
    background-color var(--duration-fast) ease,
    color var(--duration-fast) ease;
}

.app-nav__link:hover {
  background-color: var(--color-bg-soft);
  color: var(--color-text);
}

.app-nav__link--active {
  background-color: var(--color-accent-soft);
  color: var(--color-accent);
  font-weight: 600;
}

@media (max-width: 767px) {
  .brand__suffix {
    display: none;
  }

  .app-header__inner {
    gap: 6px;
    padding: 8px 12px;
  }

  .app-nav__link {
    padding: 6px 8px;
    font-size: 0.92rem;
  }
}
</style>
