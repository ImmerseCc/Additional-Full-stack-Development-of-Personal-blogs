// Markdown 渲染管线（阶段 4 批 3）：markdown-it 渲染 → DOMPurify 清洗 → 交给 v-html。
// 三条约定（决策 V / X 与 docs/api-contract.md）：
//   1. html: false —— 不允许正文里写原始 HTML，从源头少一类注入；
//   2. 先 render 再 sanitize，顺序不能反，否则白名单会误杀 markdown-it 自己生成的标签；
//   3. 标题统一补 id（供阶段 4 批 4 的目录跳转），纯中文标题用序号兜底。
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js/lib/common'
import DOMPurify from 'dompurify'

// 代码高亮：只对明确标注且 highlight.js 认识的语言着色。
// 返回空串表示"交给 markdown-it 自己转义"，避免对未知语言瞎猜导致高亮错乱。
function highlightCode(code, language) {
  if (!language || !hljs.getLanguage(language)) return ''
  try {
    return hljs.highlight(code, { language, ignoreIllegals: true }).value
  } catch {
    return ''
  }
}

const md = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: false,
  highlight: highlightCode
})

// 只保留 ASCII 字母、数字、空格与连字符；中文等字符会被剔除，随后由序号兜底
const UNSAFE_SLUG_CHARS = /[^a-z0-9\s-]/g

function slugify(text) {
  return String(text)
    .toLowerCase()
    .replace(/\s+/g, '-')
    .replace(UNSAFE_SLUG_CHARS, '')
    .replace(/-{2,}/g, '-')
    .replace(/^-+|-+$/g, '')
}

// 标题 id：纯中文标题 → section-<序号>；同名标题追加 -2 / -3 … 保证文档内唯一
md.core.ruler.push('heading_ids', (state) => {
  const used = new Map()
  let headingIndex = 0
  const tokens = state.tokens
  for (let i = 0; i < tokens.length; i += 1) {
    if (tokens[i].type !== 'heading_open') continue
    headingIndex += 1
    const inline = tokens[i + 1]
    const text = inline && inline.type === 'inline' ? inline.content : ''
    const base = slugify(text) || `section-${headingIndex}`
    const count = (used.get(base) || 0) + 1
    used.set(base, count)
    tokens[i].attrSet('id', count === 1 ? base : `${base}-${count}`)
  }
})

// 外链统一走新标签并带 rel="noopener noreferrer"（避免 window.opener 被利用）
const defaultLinkOpen =
  md.renderer.rules.link_open ||
  ((tokens, idx, options, env, self) => self.renderToken(tokens, idx, options))

md.renderer.rules.link_open = (tokens, idx, options, env, self) => {
  const href = tokens[idx].attrGet('href') || ''
  if (/^https?:\/\//i.test(href)) {
    tokens[idx].attrSet('target', '_blank')
    tokens[idx].attrSet('rel', 'noopener noreferrer')
  }
  return defaultLinkOpen(tokens, idx, options, env, self)
}

// 正文图片懒加载（阶段 4 批 4）：交给浏览器原生 loading="lazy"，不额外写 IntersectionObserver
const defaultImage =
  md.renderer.rules.image ||
  ((tokens, idx, options, env, self) => self.renderToken(tokens, idx, options))

md.renderer.rules.image = (tokens, idx, options, env, self) => {
  tokens[idx].attrSet('loading', 'lazy')
  tokens[idx].attrSet('decoding', 'async')
  return defaultImage(tokens, idx, options, env, self)
}

// id 供目录跳转，class 是代码高亮所需，loading / decoding 是图片懒加载所需；style 与被禁标签属于白名单外
const SANITIZE_OPTIONS = {
  USE_PROFILES: { html: true },
  ADD_ATTR: ['id', 'target', 'rel', 'class', 'loading', 'decoding'],
  FORBID_TAGS: ['style', 'iframe', 'form', 'input', 'button', 'script'],
  FORBID_ATTR: ['style', 'onerror', 'onload', 'onclick']
}

/**
 * 把 Markdown 原文渲染成可安全插入页面的 HTML。
 * @param {string} source 契约里 ArticleDetail.content
 * @returns {string} 已清洗的 HTML（标题带 id，代码块已高亮）
 */
export function renderMarkdown(source) {
  if (!source) return ''
  return DOMPurify.sanitize(md.render(String(source)), SANITIZE_OPTIONS)
}
