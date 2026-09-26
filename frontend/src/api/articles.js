// 文章接口（阶段 4 批 1）：字段名与语义严格按 docs/api-contract.md 第 2、3、4 节。
// 阶段 4 只用到"列表"与"详情"两条读接口；创建 / 更新 / 删除按契约属阶段 8（或隐藏入口），暂不实现。
import { request } from './http'
import { ApiError } from './error'

// 参数规范化（阶段 5 批 1）：契约里 tags 是逗号分隔的字符串，前端筛选器传数组更自然，统一在这里收敛。
// 空值交给 http.js 的 buildQuery 跳过，所以这里只负责「数组转串」与「剔除纯空白关键词」。
function normalizeParams(params) {
  const normalized = { ...params }
  if (Array.isArray(normalized.tags)) {
    const names = normalized.tags
      .filter((name) => typeof name === 'string' && name.trim() !== '')
      .map((name) => name.trim())
    if (names.length === 0) {
      delete normalized.tags
    } else {
      normalized.tags = names.join(',')
    }
  }
  if (typeof normalized.keyword === 'string') {
    const keyword = normalized.keyword.trim()
    if (keyword === '') {
      delete normalized.keyword
    } else {
      normalized.keyword = keyword
    }
  }
  return normalized
}

/**
 * 文章列表：GET /api/articles
 * @param {{page?: number, size?: number, keyword?: string, tags?: string|string[], tagMode?: 'and'|'or', status?: 'PUBLISHED'|'DRAFT'|'ALL'}} [params]
 *   page 从 1 开始（默认 1）；size 1–20（默认 10）；tags 可传 'Vue,前端' 或 ['Vue', '前端']（数组自动拼串）；
 *   tagMode 默认 and；status 默认 PUBLISHED。
 * @returns {Promise<{items: object[], page: number, size: number, total: number, totalPages: number}>}
 */
export async function fetchArticles(params = {}) {
  return request('/articles', { query: normalizeParams(params) })
}

/**
 * 文章详情：GET /api/articles/{id}
 * @param {number|string} id 文章 ID
 * @returns {Promise<object>} ArticleDetail（含 Markdown 原文 content 与 viewCount；
 *   prev / next 自阶段 8 起为真实相邻文章（只含已发布），首 / 尾篇对应项为 null）
 */
export async function fetchArticleDetail(id) {
  const value = Number(id)
  if (!Number.isInteger(value) || value < 1) {
    throw new ApiError(`文章 ID 不合法：${id}`)
  }
  return request(`/articles/${value}`)
}

/**
 * 阅读数 +1：POST /api/articles/{id}/views（阶段 8 批 5 新增接口，决策 BU）
 * 每次调用都 +1、不按访客去重（演示级语义）；GET 详情本身无副作用。
 * @param {number|string} id 文章 ID
 * @returns {Promise<{viewCount: number}>} 自增后的当前阅读数
 */
export async function postArticleView(id) {
  const value = Number(id)
  if (!Number.isInteger(value) || value < 1) {
    throw new ApiError(`文章 ID 不合法：${id}`)
  }
  return request(`/articles/${value}/views`, { method: 'POST' })
}

/** 校验文章 ID（写作台的写接口共用）。 */
function assertArticleId(id) {
  const value = Number(id)
  if (!Number.isInteger(value) || value < 1) {
    throw new ApiError(`文章 ID 不合法：${id}`)
  }
  return value
}

/**
 * 创建文章：POST /api/articles（阶段 8 批 7，写作台 /studio）
 * @param {{title: string, content: string, summary?: string, coverUrl?: string, status?: 'PUBLISHED'|'DRAFT', tags?: string[]}} payload
 *   summary / coverUrl 留空不提交；tags 最多 5 个（不存在的标签由后端自动创建）
 * @returns {Promise<object>} 创建出来的 ArticleSummary
 */
export async function createArticle(payload) {
  const body = { title: payload.title, content: payload.content, status: payload.status || 'PUBLISHED' }
  if (payload.summary) body.summary = payload.summary
  if (payload.coverUrl) body.coverUrl = payload.coverUrl
  if (Array.isArray(payload.tags) && payload.tags.length > 0) body.tags = payload.tags
  return request('/articles', { method: 'POST', body })
}

/**
 * 更新文章：PUT /api/articles/{id}（**全量语义**：title / content / status 必填；
 * tags 传空数组表示清空标签）
 * @returns {Promise<object>} 更新后的 ArticleSummary
 */
export async function updateArticle(id, payload) {
  const body = {
    title: payload.title,
    content: payload.content,
    status: payload.status || 'PUBLISHED',
    tags: Array.isArray(payload.tags) ? payload.tags : []
  }
  if (payload.summary) body.summary = payload.summary
  if (payload.coverUrl) body.coverUrl = payload.coverUrl
  return request(`/articles/${assertArticleId(id)}`, { method: 'PUT', body })
}

/**
 * 删除文章：DELETE /api/articles/{id}（级联删除评论与点赞；标签本身保留）
 * @returns {Promise<null>}
 */
export async function deleteArticle(id) {
  return request(`/articles/${assertArticleId(id)}`, { method: 'DELETE' })
}
