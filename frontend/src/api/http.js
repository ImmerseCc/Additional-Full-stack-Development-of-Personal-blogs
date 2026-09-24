// 接口访问层（阶段 4 批 1）：统一发起请求、统一解包、统一报错。
// 约定来自 docs/api-contract.md v1.0：
//   - 所有请求以 /api 开头，开发期由 Vite 代理转发到 http://localhost:8080（见 vite.config.js）；
//   - 响应统一为 { code, message, data }，HTTP 2xx 且 code === 0 才算成功；
//   - 失败一律抛出 ApiError：HTTP 层错误、业务错误码、网络失败、超时都归一成同一种错误对象。
import { ApiError } from './error'

const BASE_URL = '/api'
const DEFAULT_TIMEOUT = 8000

// 拼查询串：跳过 undefined / null / 空串；键与值都做 URI 编码（标签名是中文，不能像 Git Bash 的 curl 那样按 GBK 发出）
function buildQuery(query) {
  if (!query) return ''
  const parts = []
  for (const [key, value] of Object.entries(query)) {
    if (value === undefined || value === null || value === '') continue
    parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
  }
  return parts.length > 0 ? `?${parts.join('&')}` : ''
}

function toApiError(payload, status) {
  const message = (payload && payload.message) || `请求失败（HTTP ${status}）`
  const fields = payload && payload.data && payload.data.fields ? payload.data.fields : null
  return new ApiError(message, { code: payload ? payload.code : null, status, fields })
}

/**
 * 发起一次接口请求并返回解包后的 data。
 * @param {string} path 以 / 开头的路径（不含 /api 前缀）
 * @param {{method?: string, body?: unknown, query?: object, signal?: AbortSignal, timeout?: number}} [options]
 * @returns {Promise<unknown>} 契约里 data 字段的内容（对象 / 数组 / null）
 */
export async function request(path, options = {}) {
  const { method = 'GET', body, query, signal, timeout = DEFAULT_TIMEOUT } = options

  const controller = new AbortController()
  const timer = timeout > 0 ? setTimeout(() => controller.abort(), timeout) : null
  const forwardAbort = () => controller.abort()
  if (signal) {
    if (signal.aborted) controller.abort()
    else signal.addEventListener('abort', forwardAbort, { once: true })
  }

  try {
    const response = await fetch(`${BASE_URL}${path}${buildQuery(query)}`, {
      method,
      headers: body === undefined ? undefined : { 'Content-Type': 'application/json; charset=utf-8' },
      body: body === undefined ? undefined : JSON.stringify(body),
      signal: controller.signal
    })

    const text = await response.text()
    let payload = null
    if (text) {
      try {
        payload = JSON.parse(text)
      } catch {
        throw new ApiError('服务端返回的不是合法 JSON', { status: response.status })
      }
    }

    if (!response.ok) throw toApiError(payload, response.status)
    if (!payload || payload.code !== 0) throw toApiError(payload, response.status)

    return payload.data
  } catch (error) {
    if (error instanceof ApiError) throw error
    if (error.name === 'AbortError') {
      throw new ApiError(signal && signal.aborted ? '请求已取消' : `请求超时（${timeout}ms）`)
    }
    throw new ApiError('无法连接后端服务，请确认后端已启动（/api 代理指向 http://localhost:8080）')
  } finally {
    if (timer) clearTimeout(timer)
    if (signal) signal.removeEventListener('abort', forwardAbort)
  }
}
