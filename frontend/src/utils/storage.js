// 本地存储工具（阶段 5 批 1）：统一 blog: 前缀、统一 try/catch 降级、统一 JSON 读写。
// 约定（见 docs/api-contract.md 第 5 节）：localStorage 只存「本机偏好」与「本机状态」，
// 点赞计数与评论的唯一事实来源始终是后端，本地数据只用于界面初始化与归属校验。
// 存储不可用（隐私模式 / 禁用存储）时全部静默降级：读返回空、写返回 false，绝不抛错中断页面。
export const STORAGE_PREFIX = 'blog:'

/**
 * 读取原始字符串。
 * @param {string} key 不含前缀的键名，例如 'theme'
 * @returns {string|null} 不存在或存储不可用时返回 null
 */
export function readRaw(key) {
  try {
    return localStorage.getItem(STORAGE_PREFIX + key)
  } catch (error) {
    return null
  }
}

/**
 * 写入原始字符串。
 * @returns {boolean} 是否写入成功
 */
export function writeRaw(key, value) {
  try {
    localStorage.setItem(STORAGE_PREFIX + key, String(value))
    return true
  } catch (error) {
    return false
  }
}

/**
 * 删除单个键。
 * @returns {boolean} 是否删除成功
 */
export function removeRaw(key) {
  try {
    localStorage.removeItem(STORAGE_PREFIX + key)
    return true
  } catch (error) {
    return false
  }
}

/**
 * 读取并解析 JSON。
 * @param {string} key 不含前缀的键名
 * @param {unknown} fallback 解析失败时的回退值
 */
export function readJson(key, fallback = null) {
  const raw = readRaw(key)
  if (raw === null) return fallback
  try {
    return JSON.parse(raw)
  } catch (error) {
    return fallback
  }
}

/**
 * 序列化并写入 JSON。
 * @returns {boolean} 是否写入成功
 */
export function writeJson(key, value) {
  let raw
  try {
    raw = JSON.stringify(value)
  } catch (error) {
    return false
  }
  return raw === undefined ? false : writeRaw(key, raw)
}

/**
 * 列出本应用（blog: 前缀）占用的全部键名（不含前缀），供本地数据一览与重置使用。
 * @returns {string[]} 键名数组；存储不可用时返回空数组
 */
export function listKeys() {
  try {
    const keys = []
    for (let index = 0; index < localStorage.length; index += 1) {
      const full = localStorage.key(index)
      if (full && full.startsWith(STORAGE_PREFIX)) keys.push(full.slice(STORAGE_PREFIX.length))
    }
    return keys
  } catch (error) {
    return []
  }
}

/**
 * 清空本应用写入的全部本地数据（只删 blog: 前缀，不动同一域名下的其它键）。
 * @returns {number} 实际删除的键数量
 */
export function clearAll() {
  let removed = 0
  for (const key of listKeys()) {
    if (removeRaw(key)) removed += 1
  }
  return removed
}
