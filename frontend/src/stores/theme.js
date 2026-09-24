import { computed, ref, watch } from 'vue'
import { defineStore } from 'pinia'

// 主题三态（决策 M）：亮 / 暗 / 跟随系统。
// localStorage 里存的是「用户选择」（含 system），<html data-theme> 存的是「实际生效值」（light / dark）；
// index.html 的首屏防闪脚本按同一键名与同一语义读取，故刷新与首屏都不会闪烁。
export const THEME_STORAGE_KEY = 'blog:theme'
export const THEME_MODES = ['light', 'dark', 'system']
export const THEME_LABELS = {
  light: '亮色',
  dark: '暗色',
  system: '跟随系统'
}

const DARK_QUERY = '(prefers-color-scheme: dark)'

function readStoredMode() {
  try {
    const saved = localStorage.getItem(THEME_STORAGE_KEY)
    return THEME_MODES.includes(saved) ? saved : 'system'
  } catch (error) {
    return 'system'
  }
}

export const useThemeStore = defineStore('theme', () => {
  const mode = ref(readStoredMode())
  const systemDark = ref(window.matchMedia(DARK_QUERY).matches)

  const isDark = computed(() =>
    mode.value === 'system' ? systemDark.value : mode.value === 'dark'
  )
  const resolvedMode = computed(() => (isDark.value ? 'dark' : 'light'))
  const nextMode = computed(
    () => THEME_MODES[(THEME_MODES.indexOf(mode.value) + 1) % THEME_MODES.length]
  )

  let initialized = false

  function applyToDocument() {
    document.documentElement.setAttribute('data-theme', resolvedMode.value)
  }

  function persist() {
    try {
      localStorage.setItem(THEME_STORAGE_KEY, mode.value)
    } catch (error) {
      // 存储不可用（隐私模式 / 禁用存储）时退化为仅当前会话生效
    }
  }

  function setMode(next) {
    if (THEME_MODES.includes(next)) {
      mode.value = next
    }
  }

  function cycleMode() {
    setMode(nextMode.value)
  }

  function init() {
    if (initialized) {
      return
    }
    initialized = true

    watch(mode, persist)
    watch([mode, systemDark], applyToDocument, { immediate: true })
    window.matchMedia(DARK_QUERY).addEventListener('change', (event) => {
      systemDark.value = event.matches
    })
  }

  return { mode, systemDark, isDark, resolvedMode, nextMode, setMode, cycleMode, init }
})
