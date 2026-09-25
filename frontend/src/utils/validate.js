// 评论表单校验（阶段 5 批 3）：数值与 docs/api-contract.md 第 4.9 节一致（昵称 1–30 字、内容 1–1000 字、邮箱可选但需合法）。
// 双层校验：这里做提交前的即时反馈；服务端仍会再校验一遍并返回 40001 + fields，
// 表单直接复用同一套字段名（authorName / authorEmail / content）把服务端原因回填到对应输入框。
export const COMMENT_LIMITS = {
  authorNameMax: 30,
  contentMax: 1000
}

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

/**
 * 校验评论表单，返回"字段名 → 错误文案"的对象；无错误时返回空对象。
 * @param {{authorName?: string, authorEmail?: string, content?: string}} form
 * @returns {Record<string, string>}
 */
export function validateCommentForm(form) {
  const errors = {}
  const name = (form.authorName || '').trim()
  const email = (form.authorEmail || '').trim()
  const content = (form.content || '').trim()

  if (name === '') {
    errors.authorName = '昵称不能为空'
  } else if (name.length > COMMENT_LIMITS.authorNameMax) {
    errors.authorName = `昵称不能超过 ${COMMENT_LIMITS.authorNameMax} 个字`
  }

  if (email !== '' && !EMAIL_PATTERN.test(email)) {
    errors.authorEmail = '邮箱格式不正确'
  }

  if (content === '') {
    errors.content = '评论内容不能为空'
  } else if (content.length > COMMENT_LIMITS.contentMax) {
    errors.content = `评论内容不能超过 ${COMMENT_LIMITS.contentMax} 个字`
  }

  return errors
}
