// 访客标识（阶段 5 批 1）：契约要求 visitorId 为 8–64 位字符串，由前端生成并持久化。
// 用途：点赞去重（like_record 的唯一约束）与评论 / 取消点赞的归属校验。
// 这是演示级方案 —— 清掉本地存储就等于「换一个人」；公网部署须替换为真正的鉴权。
// 每次调用都直接读存储（不做模块级缓存），这样用户在 DevTools 里改过 / 清过之后行为依然自洽。
import { readRaw, writeRaw } from './storage'

export const VISITOR_STORAGE_KEY = 'visitorId'

// 与后端校验保持一致：8–64 位，字母 / 数字 / 连字符 / 下划线
const VISITOR_PATTERN = /^[A-Za-z0-9_-]{8,64}$/

function createVisitorId() {
  const webCrypto = globalThis.crypto
  if (webCrypto && typeof webCrypto.randomUUID === 'function') {
    return webCrypto.randomUUID()
  }
  // 兜底：不支持 crypto.randomUUID 的环境（老浏览器 / 非安全上下文）用时间戳加随机数，仍满足长度与字符集
  return `v-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`
}

/**
 * 取得本机的访客标识：已存在且合法就直接用，否则生成一个新的并持久化。
 * @returns {string} 8–64 位的 visitorId
 */
export function getVisitorId() {
  const stored = readRaw(VISITOR_STORAGE_KEY)
  if (stored && VISITOR_PATTERN.test(stored)) {
    return stored
  }
  const created = createVisitorId()
  writeRaw(VISITOR_STORAGE_KEY, created)
  return created
}
