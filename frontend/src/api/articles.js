// 文章接口（阶段 4 批 1）：字段名与语义严格按 docs/api-contract.md 第 2、3、4 节。
// 阶段 4 只用到"列表"与"详情"两条读接口；创建 / 更新 / 删除按契约属阶段 8（或隐藏入口），暂不实现。
import { request } from './http'
import { ApiError } from './error'

/**
 * 文章列表：GET /api/articles
 * @param {{page?: number, size?: number, keyword?: string, tags?: string, tagMode?: 'and'|'or', status?: 'PUBLISHED'|'DRAFT'|'ALL'}} [params]
 *   page 从 1 开始（默认 1）；size 1–20（默认 10）；tags 为逗号分隔的标签名（如 'Vue,前端'）；
 *   tagMode 默认 and；status 默认 PUBLISHED。
 * @returns {Promise<{items: object[], page: number, size: number, total: number, totalPages: number}>}
 */
export async function fetchArticles(params = {}) {
  return request('/articles', { query: params })
}

/**
 * 文章详情：GET /api/articles/{id}
 * @param {number|string} id 文章 ID
 * @returns {Promise<object>} ArticleDetail（含 Markdown 原文 content；prev / next 阶段 8 前恒为 null）
 */
export async function fetchArticleDetail(id) {
  const value = Number(id)
  if (!Number.isInteger(value) || value < 1) {
    throw new ApiError(`文章 ID 不合法：${id}`)
  }
  return request(`/articles/${value}`)
}
