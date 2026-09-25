// 点赞接口（阶段 5 批 1）：字段名与语义严格按 docs/api-contract.md 第 2.5、4.13–4.15 节。
// 契约要点：三个操作都要求 visitorId；点赞与取消点赞都是幂等的，前端可以放心做「重复点击保护 + 以后端返回为准」。
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
 * 查询点赞状态与总数：GET /api/articles/{id}/likes?visitorId=xxx
 * @param {number|string} articleId 文章 ID
 * @param {string} visitorId 本机访客标识
 * @returns {Promise<{articleId: number, liked: boolean, likeCount: number}>}
 */
export async function fetchLikeState(articleId, visitorId) {
  return request(`/articles/${assertArticleId(articleId)}/likes`, { query: { visitorId } })
}

/**
 * 点赞（幂等）：POST /api/articles/{id}/likes
 * @returns {Promise<{articleId: number, liked: boolean, likeCount: number}>} 已点赞时重复调用仍返回 liked: true
 */
export async function likeArticle(articleId, visitorId) {
  return request(`/articles/${assertArticleId(articleId)}/likes`, {
    method: 'POST',
    body: { visitorId }
  })
}

/**
 * 取消点赞（幂等）：DELETE /api/articles/{id}/likes
 * @returns {Promise<{articleId: number, liked: boolean, likeCount: number}>} 未点赞时调用返回 liked: false
 */
export async function unlikeArticle(articleId, visitorId) {
  return request(`/articles/${assertArticleId(articleId)}/likes`, {
    method: 'DELETE',
    body: { visitorId }
  })
}
