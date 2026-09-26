// 接口错误对象（阶段 4 批 1）：把三类失败统一成一种可读形态，视图层只判断这一个类型。
// 三类失败：
//   1. HTTP 层（如 500）—— status 有值、code 可能为 null；
//   2. 业务错误码（如 40001 / 40004 / 40009）—— code 有值，fields 可能带字段级原因；
//   3. 网络失败 / 超时 / 主动取消 —— code 与 status 均为空。
export class ApiError extends Error {
  constructor(message, { code = null, status = 0, fields = null } = {}) {
    super(message)
    this.name = 'ApiError'
    this.code = code
    this.status = status
    this.fields = fields
  }

  // 资源不存在：列表 / 详情页据此显示"文章不存在"而不是通用错误
  get isNotFound() {
    return this.code === 40004
  }
}
