<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import ThemeToggle from '@/components/ThemeToggle.vue'

// 全局导航（决策 K）：首页 / 文章列表 / 关于，右侧固定主题按钮（决策 N）。
// 当前页高亮以 aria-current="page" 为准；窄屏（< 768px）折叠为汉堡菜单：
// aria-expanded / aria-controls 标注状态，Esc、遮罩点击、路由切换都会收起，
// 打开时焦点进入菜单、关闭后焦点回到汉堡按钮。滚动超过阈值时页头切换为"浮起"样式。
const NAV_ITEMS = [
  { to: '/', label: '首页' },
  { to: '/articles', label: '文章列表' },
  { to: '/about', label: '关于' }
]

const MOBILE_QUERY = '(max-width: 767px)'
const SCROLL_THRESHOLD = 4

const route = useRoute()
const isMenuOpen = ref(false)
const isScrolled = ref(false)
const menuButton = ref(null)
const navPanel = ref(null)

let mobileQuery = null

function isActive(to) {
  return to === '/' ? route.path === '/' : route.path.startsWith(to)
}

function openMenu() {
  isMenuOpen.value = true
  nextTick(() => navPanel.value?.querySelector('a')?.focus())
}

function closeMenu(restoreFocus = false) {
  if (!isMenuOpen.value) {
    return
  }
  isMenuOpen.value = false
  if (restoreFocus) {
    nextTick(() => menuButton.value?.focus())
  }
}

function toggleMenu() {
  if (isMenuOpen.value) {
    closeMenu(true)
  } else {
    openMenu()
  }
}

function onKeydown(event) {
  if (event.key === 'Escape') {
    closeMenu(true)
  }
}

function onScroll() {
  isScrolled.value = window.scrollY > SCROLL_THRESHOLD
}

function onViewportChange(event) {
  // 从窄屏切回宽屏时收起菜单，避免回到窄屏后菜单仍是展开态
  if (!event.matches) {
    closeMenu(false)
  }
}

watch(
  () => route.fullPath,
  () => closeMenu(false)
)

onMounted(() => {
  window.addEventListener('keydown', onKeydown)
  window.addEventListener('scroll', onScroll, { passive: true })
  onScroll()
  mobileQuery = window.matchMedia(MOBILE_QUERY)
  mobileQuery.addEventListener('change', onViewportChange)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', onKeydown)
  window.removeEventListener('scroll', onScroll)
  mobileQuery?.removeEventListener('change', onViewportChange)
})
</script>

<template>
  <header class="app-header" :class="{ 'app-header--scrolled': isScrolled }">
    <div class="app-header__inner">
      <RouterLink to="/" class="brand">
        个人博客<span class="brand__suffix"> · VibeCoding</span>
      </RouterLink>

      <button
        ref="menuButton"
        type="button"
        class="menu-button"
        :aria-expanded="isMenuOpen ? 'true' : 'false'"
        aria-controls="app-nav"
        :aria-label="isMenuOpen ? '关闭菜单' : '打开菜单'"
        @click="toggleMenu()"
      >
        <svg
          v-if="!isMenuOpen"
          class="menu-button__icon"
          viewBox="0 0 20 20"
          aria-hidden="true"
        >
          <path
            d="M3 6h14M3 10h14M3 14h14"
            fill="none"
            stroke="currentColor"
            stroke-width="1.7"
            stroke-linecap="round"
          />
        </svg>
        <svg v-else class="menu-button__icon" viewBox="0 0 20 20" aria-hidden="true">
          <path
            d="M5.5 5.5l9 9M14.5 5.5l-9 9"
            fill="none"
            stroke="currentColor"
            stroke-width="1.7"
            stroke-linecap="round"
          />
        </svg>
      </button>

      <nav
        id="app-nav"
        ref="navPanel"
        class="app-nav"
        :class="{ 'app-nav--open': isMenuOpen }"
        aria-label="主导航"
      >
        <RouterLink
          v-for="item in NAV_ITEMS"
          :key="item.to"
          :to="item.to"
          class="app-nav__link"
          :class="{ 'app-nav__link--active': isActive(item.to) }"
          :aria-current="isActive(item.to) ? 'page' : undefined"
          @click="closeMenu(true)"
        >
          {{ item.label }}
        </RouterLink>
      </nav>

      <ThemeToggle />
    </div>
  </header>

  <div
    v-if="isMenuOpen"
    class="app-header__scrim"
    aria-hidden="true"
    @click="closeMenu(true)"
  ></div>
</template>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 20;
  border-bottom: 1px solid var(--color-border);
  background-color: var(--color-bg-elevated);
  transition:
    var(--transition-theme),
    box-shadow var(--duration-base) ease;
}

/* 滚动后的"浮起"样式：半透明底 + 模糊 + 阴影，并隐藏分隔线 */
.app-header--scrolled {
  border-bottom-color: transparent;
  background-color: var(--color-bg-header);
  backdrop-filter: blur(8px);
  box-shadow: var(--shadow-sm);
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

.menu-button {
  display: none;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  padding: 0;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-pill);
  background-color: var(--color-bg-soft);
  color: var(--color-text);
  cursor: pointer;
  transition:
    background-color var(--duration-fast) ease,
    border-color var(--duration-fast) ease,
    color var(--duration-fast) ease;
}

.menu-button:hover {
  border-color: var(--color-accent);
  color: var(--color-accent);
}

.menu-button__icon {
  width: 18px;
  height: 18px;
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

/* 折叠菜单打开时铺在页头下方的遮罩（点击即收起；位于 header 之外，避免被 backdrop-filter 影响定位） */
.app-header__scrim {
  position: fixed;
  top: var(--header-height);
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 10;
  background-color: rgba(15, 20, 25, 0.45);
}

@media (max-width: 767px) {
  .brand__suffix {
    display: none;
  }

  .app-header__inner {
    gap: 6px;
    padding: 8px 12px;
  }

  .menu-button {
    display: inline-flex;
    margin-left: auto;
  }

  .app-nav {
    position: absolute;
    top: 100%;
    right: 0;
    left: 0;
    flex-direction: column;
    align-items: stretch;
    gap: 2px;
    margin: 0;
    padding: 8px 12px 12px;
    border-bottom: 1px solid var(--color-border);
    background-color: var(--color-bg-elevated);
    box-shadow: var(--shadow-md);
    opacity: 0;
    visibility: hidden;
    transform: translateY(-6px);
    transition:
      opacity var(--duration-fast) ease,
      transform var(--duration-fast) ease,
      visibility var(--duration-fast) linear;
  }

  .app-nav--open {
    opacity: 1;
    visibility: visible;
    transform: none;
  }

  .app-nav__link {
    padding: 10px 12px;
    font-size: 0.95rem;
  }
}
</style>
