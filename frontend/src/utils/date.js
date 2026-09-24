// 时间展示工具（阶段 4 批 2）：契约的时间是本地时区、无偏移的 ISO 串，例如 2026-09-23T10:20:30。
// 刻意不用 new Date()：对无时区后缀的串做解析容易踩时区解释的坑，直接按字符取日期部分最稳。
const DATE_PREFIX = /^(\d{4})-(\d{2})-(\d{2})/

/**
 * 把契约时间串格式化为 YYYY-MM-DD，供列表卡片与详情页展示。
 * @param {string|null|undefined} value 例如 '2026-09-23T10:20:30'
 * @returns {string} 例如 '2026-09-23'；无法识别时原样返回，空值返回空串
 */
export function formatDate(value) {
  if (!value) return ''
  const matched = DATE_PREFIX.exec(String(value))
  return matched ? `${matched[1]}-${matched[2]}-${matched[3]}` : String(value)
}
