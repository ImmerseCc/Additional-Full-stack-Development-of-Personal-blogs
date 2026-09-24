// 标签接口（阶段 4 批 1）：字段名与语义按 docs/api-contract.md 第 2.1、4.7 节。
import { request } from './http'

/**
 * 标签列表：GET /api/tags
 * @returns {Promise<Array<{id: number, name: string, articleCount: number}>>} 按文章数倒序
 */
export async function fetchTags() {
  return request('/tags')
}
