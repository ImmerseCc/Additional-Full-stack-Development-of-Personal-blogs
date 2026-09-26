// 标签接口（阶段 4 批 1；阶段 8 批 7 补管理接口）：字段名与语义按 docs/api-contract.md 第 2.1、4.7、4.17 节。
import { request } from './http'
import { ApiError } from './error'

/**
 * 标签列表：GET /api/tags
 * @returns {Promise<Array<{id: number, name: string, articleCount: number}>>} 按文章数倒序
 */
export async function fetchTags() {
  return request('/tags')
}

function assertTagId(id) {
  const value = Number(id)
  if (!Number.isInteger(value) || value < 1) {
    throw new ApiError(`标签 ID 不合法：${id}`)
  }
  return value
}

/**
 * 新建标签：POST /api/tags
 * @param {string} name 名称 1–20 字；重名抛 40009（资源冲突）
 * @returns {Promise<{id: number, name: string, articleCount: number}>}
 */
export async function createTag(name) {
  return request('/tags', { method: 'POST', body: { name } })
}

/**
 * 标签改名：PUT /api/tags/{id}
 * @param {number|string} id 标签 ID
 * @param {string} name 新名称（重名抛 40009、不存在抛 40004）
 */
export async function renameTag(id, name) {
  return request(`/tags/${assertTagId(id)}`, { method: 'PUT', body: { name } })
}

/**
 * 删除标签：DELETE /api/tags/{id}（解除所有文章关联；标签不存在抛 40004）
 * @returns {Promise<null>}
 */
export async function deleteTag(id) {
  return request(`/tags/${assertTagId(id)}`, { method: 'DELETE' })
}
