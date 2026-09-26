<script setup>
// 文章详情（阶段 4 批 3 实现，批 4 补目录 / 滚动高亮 / 进场动画；阶段 5 批 3 接入评论区）：
// 真实调用 GET /api/articles/{id}，把 Markdown 正文交给 MarkdownRenderer。
// 三种状态各自可辨：加载中（骨架）/ 出错（可重试）/ 文章不存在（404 文案，不重试而是引导回列表）。
// 决策 U：prev / next 契约里恒为 null（阶段 8 才实现），本批不渲染该区块。
// 决策 Y / Z：目录取"真实渲染出来的标题"（保证 id 与正文一致），桌面右侧固定栏；
//             窄屏（< 1024px）改为正文上方的「本页目录」折叠面板（阶段 7 批 4，决策 BK）；
//             当前小节高亮由 utils/scrollSpy.js 的 scroll + getBoundingClientRect 计算。
// 阶段 5 批 3：正文下方挂评论区；评论区自己取数与维护总数，只把总数回传给头部 meta 的「评论 N」。
// 阶段 5 批 4：正文下方再加点赞按钮（LikeButton），计数同样以后端返回为准并回传给 meta。
// 阶段 7 批 1：页头下沿挂阅读进度条（ReadingProgress），进度按页面滚动比例计算（滚到底 = 100%）。
// 阶段 7 批 2：Markdown 渲染管线改为**动态加载**（决策 BH），正文渲染期间用骨架兜底。
// 阶段 7 批 4：窄屏补「本页目录」折叠面板（原生 <details>），点条目后自动收起。
import { computed, defineAsyncComponent, nextTick, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import CommentSection from '@/components/CommentSection.vue'
import LikeButton from '@/components/LikeButton.vue'
import MarkdownBodySkeleton from '@/components/MarkdownBodySkeleton.vue'
import MarkdownLoadError from '@/components/MarkdownLoadError.vue'
import ReadingProgress from '@/components/ReadingProgress.vue'
import SkeletonBlock from '@/components/SkeletonBlock.vue'
import TableOfContents from '@/components/TableOfContents.vue'
import { fetchArticleDetail, postArticleView } from '@/api/articles'
import { formatDate } from '@/utils/date'
import { useScrollSpy } from '@/utils/scrollSpy'

// 决策 BH（阶段 7 批 2）：Markdown 管线（markdown-it + highlight.js + DOMPurify，约 280 kB）
// 由静态 import 改为动态加载 —— 详情页壳层（标题 / meta / 封面 / 骨架）不再等它下载完，
// 文章 404 与错误态更是完全不加载；取目录前用它确保正文已经渲染（见 load()）。
// 阶段 9 批 2（闭环遗留 32）：补 loadingComponent / errorComponent 两态。
// 演练实测：原先只靠 <Suspense> 兜加载态时，**加载期异常会被 Suspense 吞掉** ——
// 正文区静默空白、errorComponent 不渲染、只剩一条全局错误 Toast；改用异步组件自带的
// loading（骨架）/ error（MarkdownLoadError +「重新加载正文」）后，三种状态都有明确界面。
const loadMarkdownRenderer = () => import('@/components/MarkdownRenderer.vue')
const MarkdownRenderer = defineAsyncComponent({
  loader: loadMarkdownRenderer,
  loadingComponent: MarkdownBodySkeleton,
  errorComponent: MarkdownLoadError
})

const route = useRoute()

const article = ref(null)
const loading = ref(true)
const error = ref(null)
const headings = ref([])
const bodyRoot = ref(null)
// 窄屏「本页目录」折叠面板（阶段 7 批 4，决策 BK）：挂在 <details> 上，点条目后自动收起
const tocPanel = ref(null)

const { activeId, measure } = useScrollSpy(() => headings.value)

const notFound = computed(() => Boolean(error.value && error.value.isNotFound))
const updated = computed(
  () => article.value && article.value.updatedAt && article.value.updatedAt !== article.value.createdAt
)

// 评论区发表 / 删除后回传"当前总数"，让头部 meta 的「评论 N」立刻跟上
function onCommentTotalChange(total) {
  if (article.value) {
    article.value.commentCount = total
  }
}

// 点赞 / 取消点赞后回传计数，同步头部 meta 的「点赞 N」
function onLikeCountChange(count) {
  if (article.value) {
    article.value.likeCount = count
  }
}

function closeTocPanel() {
  if (tocPanel.value) tocPanel.value.open = false
}

// 目录直接从渲染结果里取：这样 id 与正文标题天然一致，不需要再把 Markdown 解析一遍
function collectHeadings() {
  const root = bodyRoot.value
  if (!root) return
  headings.value = [...root.querySelectorAll('h2[id], h3[id]')].map((element) => ({
    id: element.id,
    text: element.textContent.trim(),
    level: Number(element.tagName.slice(1))
  }))
}

async function load() {
  loading.value = true
  error.value = null
  try {
    const data = await fetchArticleDetail(route.params.id)
    article.value = data
    // 拿到真实标题后覆盖路由的通用标题，浏览器标签页与历史记录更好读
    document.title = `${data.title} · 个人博客`
    // 阅读数上报（阶段 8 批 6，决策 BU）：fire-and-forget，失败不影响阅读；
    // 成功后用接口返回值刷新显示（计数一律以后端为准）
    postArticleView(data.id)
      .then((view) => {
        if (article.value && article.value.id === data.id) {
          article.value.viewCount = view.viewCount
        }
      })
      .catch(() => {})
  } catch (caught) {
    error.value = caught
    article.value = null
    headings.value = []
  } finally {
    loading.value = false
  }

  // 必须先让 loading 置否、正文真正渲染出来，再去取目录；
  // 否则此时模板还停在加载态的骨架分支，bodyRoot 是 null，目录永远为空（批 4 实测踩到）
  await nextTick()
  // 正文现在是异步组件（决策 BH）：还要等管线 chunk 到位并完成渲染，否则读到的是骨架。
  // 加载失败 → 正文区由异步组件的 errorComponent（MarkdownLoadError）兜底；
  // 这里只是不去取目录，也不让整个 load() 抛错（否则会多一条全局错误 Toast）。
  try {
    await loadMarkdownRenderer()
  } catch (caught) {
    return
  }
  await nextTick()
  collectHeadings()
  measure()
}

onMounted(load)
</script>

<template>
  <section class="page">
    <div class="detail-layout" :class="{ 'detail-layout--toc': headings.length > 0 }">
      <div class="detail-layout__main">
        <div v-if="loading" class="detail-loading" aria-busy="true" aria-label="文章加载中">
          <SkeletonBlock height="34px" width="72%" />
          <SkeletonBlock height="14px" width="36%" />
          <SkeletonBlock height="14px" width="94%" />
          <SkeletonBlock height="14px" width="88%" />
          <SkeletonBlock height="14px" width="92%" />
          <SkeletonBlock height="14px" width="60%" />
          <SkeletonBlock height="150px" radius="var(--radius-card)" />
        </div>

        <div v-else-if="error" class="detail-state" role="alert">
          <p class="detail-state__title">{{ notFound ? '文章不存在' : '文章加载失败' }}</p>
          <p class="detail-state__detail">
            {{
              notFound
                ? `ID 为 ${route.params.id} 的文章可能已被删除，或者从未存在。`
                : error.message
            }}
          </p>
          <div class="detail-state__actions">
            <button v-if="!notFound" type="button" class="detail-state__button" @click="load">
              重试
            </button>
            <RouterLink class="detail-state__link" to="/articles">← 返回文章列表</RouterLink>
          </div>
        </div>

        <article v-else-if="article" class="detail">
          <header class="detail__head">
            <h1 class="detail__title">{{ article.title }}</h1>

            <p class="detail__meta">
              <time :datetime="article.createdAt">创建于 {{ formatDate(article.createdAt) }}</time>
              <template v-if="updated"> · 更新于 {{ formatDate(article.updatedAt) }}</template>
              <span> · 阅读 {{ article.viewCount }}</span>
              <span> · 点赞 {{ article.likeCount }}</span>
              <span> · 评论 {{ article.commentCount }}</span>
            </p>

            <ul v-if="article.tags && article.tags.length" class="detail__tags">
              <li v-for="tag in article.tags" :key="tag" class="detail__tag">{{ tag }}</li>
            </ul>
          </header>

          <img
            v-if="article.coverUrl"
            v-reveal="{ delay: 60 }"
            class="detail__cover"
            :src="article.coverUrl"
            alt=""
            loading="lazy"
            decoding="async"
          />

          <details v-if="headings.length" ref="tocPanel" class="detail__toc">
            <summary class="detail__toc-summary">
              本页目录
              <svg class="detail__toc-icon" viewBox="0 0 20 20" aria-hidden="true" focusable="false">
                <path
                  d="M6 8l4 4 4-4"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.7"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
            </summary>
            <TableOfContents
              :headings="headings"
              :active-id="activeId"
              :show-title="false"
              @navigate="closeTocPanel"
            />
          </details>

          <div ref="bodyRoot" v-reveal="{ delay: 120 }" class="detail__body">
            <MarkdownRenderer :source="article.content" />
          </div>

          <ReadingProgress />

          <div v-reveal class="detail__actions">
            <LikeButton
              :article-id="article.id"
              :initial-count="article.likeCount"
              @count-change="onLikeCountChange"
            />
          </div>

          <nav v-if="article.prev || article.next" v-reveal class="detail__adjacent" aria-label="相邻文章">
            <RouterLink
              v-if="article.prev"
              class="detail__adjacent-item"
              :to="`/articles/${article.prev.id}`"
            >
              <span class="detail__adjacent-label">← 上一篇</span>
              <span class="detail__adjacent-title">{{ article.prev.title }}</span>
            </RouterLink>
            <span v-else class="detail__adjacent-item detail__adjacent-item--empty" aria-hidden="true"></span>

            <RouterLink
              v-if="article.next"
              class="detail__adjacent-item detail__adjacent-item--next"
              :to="`/articles/${article.next.id}`"
            >
              <span class="detail__adjacent-label">下一篇 →</span>
              <span class="detail__adjacent-title">{{ article.next.title }}</span>
            </RouterLink>
            <span v-else class="detail__adjacent-item detail__adjacent-item--empty" aria-hidden="true"></span>
          </nav>

          <footer v-reveal class="detail__foot">
            <RouterLink to="/articles">← 返回文章列表</RouterLink>
          </footer>
        </article>

        <CommentSection v-if="article" :article-id="article.id" @total-change="onCommentTotalChange" />
      </div>

      <aside v-if="headings.length" class="detail-layout__aside">
        <TableOfContents :headings="headings" :active-id="activeId" />
      </aside>
    </div>
  </section>
</template>

<style scoped>
.detail-layout__aside {
  display: none;
}

/* 桌面：正文 + 右侧目录两栏；≥1024px 才显示目录（决策 Y） */
@media (min-width: 1024px) {
  .detail-layout--toc {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 220px;
    gap: 40px;
    align-items: start;
  }

  .detail-layout__aside {
    display: block;
    position: sticky;
    top: calc(var(--header-height) + 24px);
    max-height: calc(100vh - var(--header-height) - 48px);
    overflow-y: auto;
  }
}

.detail-loading {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.detail-state {
  padding: 40px 24px;
  text-align: center;
  color: var(--color-muted);
  background-color: var(--color-bg-soft);
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-card);
}

.detail-state__title {
  margin: 0 0 8px;
  color: var(--color-text);
  font-size: 20px;
}

.detail-state__detail {
  margin: 0 0 20px;
  font-size: 14px;
}

.detail-state__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.detail-state__button {
  padding: 8px 18px;
  color: var(--color-accent-contrast);
  background-color: var(--color-accent);
  border: 1px solid transparent;
  border-radius: var(--radius-pill);
  cursor: pointer;
  transition: background-color var(--duration-fast) ease;
}

.detail-state__button:hover {
  background-color: var(--color-accent-hover);
}

.detail-state__link {
  font-size: 15px;
}

.detail__title {
  margin: 0 0 12px;
  font-size: 32px;
  line-height: 1.3;
}

.detail__meta {
  margin: 0 0 16px;
  color: var(--color-muted);
  font-size: 14px;
}

.detail__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 0 0 24px;
  padding: 0;
  list-style: none;
}

.detail__tag {
  padding: 3px 12px;
  color: var(--color-accent);
  background-color: var(--color-accent-soft);
  border-radius: var(--radius-pill);
  font-size: 13px;
}

.detail__cover {
  display: block;
  width: 100%;
  margin-bottom: 28px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
}

/* 窄屏「本页目录」折叠面板（阶段 7 批 4，决策 BK）：桌面已有右侧固定目录，这里整体隐藏 */
.detail__toc {
  margin-bottom: 24px;
  background-color: var(--color-bg-soft);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
}

.detail__toc-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 12px 16px;
  font-size: 14px;
  cursor: pointer;
  list-style: none;
}

/* 收起浏览器默认的三角标记，改用右侧箭头 */
.detail__toc-summary::-webkit-details-marker {
  display: none;
}

.detail__toc-icon {
  flex: none;
  width: 18px;
  height: 18px;
  color: var(--color-muted);
  transition: transform var(--duration-fast) ease;
}

.detail__toc[open] .detail__toc-icon {
  transform: rotate(180deg);
}

.detail__toc .toc {
  padding: 4px 16px 14px;
}

@media (min-width: 1024px) {
  .detail__toc {
    display: none;
  }
}

/* 正文下方的互动区（阶段 5 批 4：点赞按钮） */
.detail__actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
}

.detail__foot {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid var(--color-border);
  font-size: 15px;
}

/* 上下篇导航（阶段 8 批 6）：两栏等宽，窄屏自动堆叠；缺一侧时占位保持对称 */
.detail__adjacent {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
  margin-top: 32px;
}

.detail__adjacent-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px 16px;
  background-color: var(--color-bg-soft);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
  transition: border-color var(--duration-fast) ease;
}

.detail__adjacent-item:hover {
  border-color: var(--color-accent);
}

.detail__adjacent-label {
  color: var(--color-muted);
  font-size: 12px;
}

.detail__adjacent-title {
  color: var(--color-text);
  font-size: 14px;
  line-height: 1.5;
}

.detail__adjacent-item--next {
  align-items: flex-end;
  text-align: right;
}

.detail__adjacent-item--empty {
  background-color: transparent;
  border-color: transparent;
}
</style>
