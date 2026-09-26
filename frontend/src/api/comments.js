// 评论接口（阶段 5 批 1；阶段 8 批 2 补单条查询 / 修改）：字段名与语义严格按 docs/api-contract.md 第 2.4、4.8–4.12 节。
// 契约要点：authorEmail 可选、只存不回传；修改 / 删除都必须带上创建时的 visitorId 做归属校验，不匹配按 40004 处理。
import { request } from './http'
import { ApiError } from './error'

function assertArticleId(id) {
  const value = Number(id)
  if (!Number.isInteger(value) || value < 1) {
    throw new ApiError(`文章 ID 不合法：${id}`)
  }
  return value
}

/**
 * 某篇文章的评论列表：GET /api/articles/{id}/comments
 * @param {number|string} articleId 文章 ID
 * @param {{page?: number, size?: number}} [params] page 从 1 开始；size 1–20（默认 10）
 * @returns {Promise<{items: object[], page: number, size: number, total: number, totalPages: number}>}
 *   items 为 CommentVO[]，按 createdAt 倒序
 */
export async function fetchComments(articleId, { page = 1, size = 10 } = {}) {
  return request(`/articles/${assertArticleId(articleId)}/comments`, { query: { page, size } })
}

/**
 * 发表评论：POST /api/articles/{id}/comments
 * @param {number|string} articleId 文章 ID
 * @param {{authorName: string, content: string, visitorId: string, authorEmail?: string}} payload
 *   authorEmail 留空时不提交该字段；校验失败抛 ApiError（code 40001，fields 给出字段级原因）
 * @returns {Promise<object>} 创建出来的 CommentVO
 */
export async function createComment(articleId, { authorName, content, visitorId, authorEmail }) {
  const body = { authorName, content, visitorId }
  if (authorEmail) {
    body.authorEmail = authorEmail
  }
  return request(`/articles/${assertArticleId(articleId)}/comments`, { method: 'POST', body })
}

/**
 * 删除评论：DELETE /api/comments/{id}
 * @param {number|string} commentId 评论 ID
 * @param {string} visitorId 创建该评论时使用的访客标识（不匹配抛 40004）
 * @returns {Promise<null>}
 */
export async function deleteComment(commentId, visitorId) {
  const value = Number(commentId)
  if (!Number.isInteger(value) || value < 1) {
    throw new ApiError(`评论 ID 不合法：${commentId}`)
  }
  return request(`/comments/${value}`, { method: 'DELETE', body: { visitorId } })
}

/**
 * 修改评论：PUT /api/comments/{id}（阶段 8 批 2 新增接口）
 * 只改 content（1–1000 字）；visitorId 须与创建时一致，不匹配抛 40004。
 * @param {number|string} commentId 评论 ID
 * @param {string} content 新内容
 * @param {string} visitorId 创建该评论时使用的访客标识
 * @returns {Promise<object>} 更新后的 CommentVO
 */
export async function updateComment(commentId, content, visitorId) {
  const value = Number(commentId)
  if (!Number.isInteger(value) || value < 1) {
    throw new ApiError(`评论 ID 不合法：${commentId}`)
  }
  return request(`/comments/${value}`, { method: 'PUT', body: { content, visitorId } })
}
