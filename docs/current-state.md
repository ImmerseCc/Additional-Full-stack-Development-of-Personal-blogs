# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 4「前端模块二 / 三：文章列表与文章详情」批 4（加分项：TOC + 滚动高亮 + 进场动画 + 图片懒加载）已完成**（批 0–批 3 已入库 `ec848f3` / `b6f7701` / `af913f0` / `44b0d46`；批 4 经 6 项浏览器实测通过，改动待作者确认后提交）；阶段 0–3 全部完成并入库。**下一步：批 5（阶段 4 收尾：构建实测 + 浏览器实拍 + README/docs 同步 + 提交）。**

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 v1.0，**已确认**，实现时不得擅自改字段）。
2. **进度**：阶段 0、0.5、1、2、3 已完成并入库；**阶段 4 批 0–批 3 已入库（`ec848f3`、`b6f7701`、`af913f0`、`44b0d46`），批 4 已完成但尚未提交**，**当前应从批 5「阶段 4 收尾」开始**；后端 13 个操作已全部实现并实测（见第七节清单）；前端**六个核心模块中的模块二 / 模块三已完成**（列表 + 详情 + Markdown + 代码高亮 + 目录），**无占位页**；种子数据为 **12 篇文章 / 8 个标签**。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水）、`docs/collaboration-log.md`（关键提示词汇总 + 阶段索引 + 阶段记录）、`docs/debug-log.md`（报错专档，已有 **8 条**真实记录 + 9 条观察项）；审计记录在 `docs/audit-report.md`（阶段 9 才做）。
4. **环境事实**：Node 24.21.0 / npm 11.19.0 已安装；JDK 26 **已验证**可跑 Spring Boot 4.1.1；Maven 未安装但 `mvnw` 可用；**后端与前端 dev server 当前均处于停止状态**，需要时用第八节的标准命令启动。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题。
6. **本机已踩过的坑（细节都在 `docs/debug-log.md`）**：① Git Bash 里给 `curl` 传中文参数会被按 **GBK** 编码 → 改用预编码 UTF-8 百分号串、`node -e` 的 `fetch`，或用 Swagger UI / PowerShell `Invoke-RestMethod`；② `wc -m` 在本机按**字节**计数（要数中文长度得按码点算）；③ AI 的后台任务有 **10 分钟上限**，超时只杀包装进程、**派生 JVM / node 会继续存活**并占住 8080 / 5173（用 `taskkill //PID <pid> //F` 清理，阶段 3 复现 4 次、阶段 4 复现 4 次以上）；④ 浏览器提示"拒绝连接"时，排查第一步永远是"确认服务是否真的在监听"；⑤ **内置浏览器的合成点击在部分元素上不送达**（主题按钮点不动，键盘 `Tab` + `Enter` 立刻成功；分页按钮、重试按钮、卡片链接、目录链接的点按是有效的）；⑥ **路由过渡中间帧读不到"带过渡的子树"**（`page.text.snapshot` / `page.elements.snapshot` 只读到页头，`wait_for` 文本条件会超时，但同期截图正常）→ 先截图确认画面再读结构；⑦ **固定 ID 的幂等种子数据遇到"历史遗留行占用同一 ID"会静默错位**（`INSERT OR IGNORE` 整行跳过）；⑧ **启动后端前先查 8080**：端口被占不等于程序坏了（报错记录 7），前端经代理拿到 **502** 通常就是"后端不在监听"；⑨ **后台标签页里 `requestAnimationFrame` 不触发、`IntersectionObserver` 首次投递会被大幅推迟**（`setTimeout` 仍可用）→ 验证涉及"等一帧/等可见"的逻辑时，优先改成 `setTimeout` 或直接用截图强制一次渲染，见报错记录 8。
7. **前端代码地图（阶段 4 现状）**：
   - `src/api/`：`http.js`（`request()` 统一入口 + 超时 + 解包 + 错误归一，502/503/504 有可操作文案）、`error.js`、`articles.js`、`tags.js`；**写接口的 `comments.js` / `likes.js` 等阶段 5 用到时再建**（决策 AB）。
   - `src/utils/`：`date.js`、`markdown.js`（`renderMarkdown()`：markdown-it `html:false` + `highlight.js/lib/common` + DOMPurify；标题补 id、外链 target+rel、图片 `loading="lazy"`）、`reveal.js`（全局指令 `v-reveal`，见第 8 条）、`scrollSpy.js`（`useScrollSpy(getHeadings, { offset })` → `{ activeId, measure }`）。
   - `src/components/`：`ArticleList.vue`（四态容器）、`ArticleCard.vue`（`<li>` 上带 `v-reveal` 错落进场）、`Pagination.vue`、`SkeletonBlock.vue`、`ArticleSkeleton.vue`、`MarkdownRenderer.vue`、`TableOfContents.vue`（桌面右侧固定栏，窄屏隐藏）。
   - 路由：`/`、`/articles`、`/articles/:id`、`/about` + 404 兜底；列表页码与 `?page=N` 双向同步；详情页动态标题 + 目录 + 滚动高亮。
   - 代码高亮配色：`base.css` 末尾 `--hl-*`（亮 / 暗各一套，决策 V）；`base.css` 另有 `.reveal` / `.is-revealed` 两态（决策 Z）。
8. **进场动画的正确用法（批 4 的血泪教训，见报错记录 8）**：`v-reveal` 只在"挂载时在视口外"的元素上等 `IntersectionObserver`；**在视口内的元素是挂载后立即显现的**（不依赖观察器）。写新页面时若要给元素加 `v-reveal`，务必保证"即使动画完全不生效，内容也可见"——动画只能作为增强，不能作为内容可见性的前提。

---

## 一、当前阶段

**阶段 4「前端模块二 / 三：文章列表与文章详情」：进行中 —— 批 0 ✅、批 1 ✅、批 2 ✅、批 3 ✅（已入库），批 4 ✅（待提交确认），等作者确认后进入批 5（阶段 4 收尾）。**
阶段 0–3 全部完成并入库（阶段 3 的点按路径与过渡观感已由作者验收，见第五节第 13、14 条）。

阶段 4 分批（作者已同意方案与 10 个决策点，见第二节决策 R–AA）：
批 0 开工基线 ✅ → 批 1 API 访问层 ✅ → 批 2 文章列表 ✅ → 批 3 文章详情（Markdown 渲染）✅ → 批 4 加分项（TOC + 滚动高亮 + 进场动画 + 图片懒加载）✅ → 批 5 收尾与文档。

---

## 二、已完成

| 时间点 | 事项 | 产物与证据 |
|---|---|---|
| 阶段 0 | 需求确认与技术选型（未写代码） | 选型：Vue 3 + Vite、原生 CSS 变量、Spring Boot 4.1.1 + JdbcTemplate + SQLite；0–9 阶段路线 |
| 阶段 0.5 | 协作日志规范确认 | `ai-log.md`（流水）+ `collaboration-log.md`（汇总），四套模板与禁止事项 |
| 阶段 1 批 1–4 | 目录骨架、docs、frontend、backend 全部落地 | 根目录 3 文件 + docs 9 文件 + frontend 12 文件 + backend 15 文件；`git init`；契约与模型升级 v1.0；提交 `501065a` → `25e280e` |
| 报错修复 | 浏览器 `http://127.0.0.1:5173` 被拒绝 | 根因：Vite 只绑定 IPv6；修复：`vite.config.js` 加 `host: '127.0.0.1'`（报错记录 2） |
| 阶段 2 批 0–4 | 后端业务实现（13 个操作） | 累计 52 项接口实测通过；外键级联、点赞幂等、`busy_timeout` 均实测；提交 `e9ab336` → `7174838` → `af2334c` |
| 阶段 3 批 0–4 | 前端模块一（导航 / 主题 / 404 / 移动端 / 过渡） | 见第四节阶段 3 逐批表；提交 `8c5923d` → `7de72da` → `b257499` → `b1a517f` → `378e3b0` |
| 阶段 4 批 0 | 开工基线：三项申请落地 + 种子数据 + 数据库重建 | 新建 `frontend/public/`（favicon + 12 张自绘封面 SVG）；`data.sql` 扩为 **12 篇 + 8 标签 + 23 条关联** + 封面回填；数据库按标准路径重置重建并实测幂等；提交 `ec848f3`（20 文件）。**修掉 1 个真实数据问题**（报错记录 6） |
| 阶段 4 批 1 | `src/api/` 接口访问层 | 新建（完整）`src/api/http.js`、`error.js`、`articles.js`、`tags.js`；**14 项真实调用用例全通过**；提交 `b6f7701`。**期间遇到并记录 1 个真实启动故障**（报错记录 7） |
| 阶段 4 批 2 | 文章列表 + 首页门面 | 新建（完整）`utils/date.js`、`components/{ArticleCard,ArticleSkeleton,ArticleList,Pagination}.vue` + 占位详情页与路由；重写 `views/{ArticlesView,HomeView}.vue`；**10 项浏览器实测通过**；提交 `af913f0`（16 文件）+ 文档 `bc89da3` |
| 阶段 4 批 3 | 文章详情 + Markdown 渲染 | 新建（完整）`utils/markdown.js`、`components/{MarkdownRenderer,SkeletonBlock}.vue`；`base.css` 追加 `--hl-*`；重写 `views/ArticleDetailView.vue`；**15 项管线断言 + 6 项页面实测通过**；提交 `44b0d46`（9 文件） |
| 阶段 4 批 4 | 加分项：TOC + 滚动高亮 + 进场动画 + 图片懒加载（本次） | 新建（完整）`components/TableOfContents.vue`、`utils/reveal.js`（全局指令 `v-reveal`）、`utils/scrollSpy.js`；`main.js` 注册指令；`base.css` 加 `.reveal` 两态；`markdown.js` 图片规则加 `loading="lazy"` + `decoding="async"`；`ArticleList.vue` 的 `<li>` 加错落进场；`ArticleDetailView.vue` 改为两栏（正文 + 右侧目录）、接入滚动高亮与进场。**6 项浏览器实测通过**；`npm run build` 123 模块 / 169ms。**自查出并修复 2 个真实缺陷**（报错记录 8） |

**关键决策记录（作者已确认）**

| 编号 | 决策 |
|---|---|
| A1 | 项目根目录沿用当前目录，接受名称与"连字符"要求的差异 |
| B1 | 同时维护 `docs/ai-log.md` 与 `docs/collaboration-log.md` |
| C1 | `mvnw` 三件套取自 start.spring.io 官方骨架 |
| D1 | 数据库含第 5 张关联表 `article_tag` |
| E | 技术栈锁定 Vue 3 + Vite + Spring Boot + JdbcTemplate + SQLite；授权 AI 安装 Node |
| F | 契约 8 项 + 数据模型 5 项全部按建议通过；允许新增 `.gitattributes`；执行首次 git 提交 |
| G | 前端 dev server 显式绑定 IPv4（`host: '127.0.0.1'`），不采用 `host: true` |
| H | 阶段 2 分批方案：把"原批 2"拆成 **2a 读路径 / 2b 写路径** |
| I | 阶段 2 实测产生的文章 5：**只删文章、保留标签**（选 b，已用接口删除） |
| J | `--enable-native-access` 只写进 README 排查说明，不进标准启动命令 |
| K | 阶段 3 菜单项锁定三项：首页 `/`、文章列表 `/articles`、关于 `/about` |
| L | 阶段 3 **新增 404 页**（`NotFoundView.vue`），替换"非匹配路径一律重定向首页"的兜底 |
| M | 主题切换采用**三态**：亮 / 暗 / 跟随系统（`blog:theme` 存 `light` / `dark` / `system`） |
| N | 主题按钮固定在导航栏右侧（桌面与移动端同一位置，不藏进汉堡菜单） |
| O | 关于页阶段 3 **不调用后端**，保持纯前端（前后端对接演练留阶段 6） |
| P | 阶段 3 不新增 `frontend/public/`，不新增 `src/` 子目录（复用逻辑放 `src/utils/`） |
| Q | 阶段 3 批 2 / 批 3 **不拆分**，沿用"每批停下等确认"节奏 |
| R | 阶段 4 三项开工申请**全部同意**：①新增 `frontend/public/`；②种子文章补到约 12 篇；③阶段 4 **直接联调真实后端** |
| S | 列表翻页阶段 4 先用**分页控件**（`size=10`），"无限滚动 + 骨架屏"整体留阶段 7 迭代 |
| T | **首页定位**＝站点简介 + 最新 3 篇卡片 + "查看全部文章"入口；完整列表在 `/articles` |
| U | 详情页 `prev` / `next`（契约里恒为 `null`）阶段 4 **不渲染**该区块 |
| V | 代码高亮**手写**亮 / 暗两套极简配色（CSS 变量），**不引入** highlight.js 官方主题 CSS |
| W | 阶段 4 **不新增任何前端依赖** |
| X | Markdown 标题锚点 `id` **自己写 markdown-it 核心规则**生成（纯中文标题用 `section-<序号>` 兜底，同名追加 `-2` / `-3`） |
| Y | TOC 版式：桌面（≥1024px）右侧固定栏，窄屏隐藏 |
| Z | 滚动实现：TOC 当前小节高亮用 `scroll` + `getBoundingClientRect`，进场动画用 `IntersectionObserver`；复用逻辑放 `src/utils/`，不新增子目录 |
| AA | 种子数据暴露的 tag ID 冲突（`id=5`）**采用标准重置路径解决**（作者确认，属不可恢复操作，见报错记录 6） |
| AB | 批 1 **只建 `http.js` / `error.js` / `articles.js` / `tags.js`**；`comments.js` / `likes.js` 推迟到阶段 5（避免死代码） |
| AC | 批 2 就**先加 `/articles/:id` 路由 + 占位详情页**，让卡片链接有落点而不是掉进 404 |
| AD | 批 3 把骨架"流光"样式收敛为 `SkeletonBlock.vue` 原语，`ArticleSkeleton.vue` 复用它 |
| AE | 详情页 404（`isNotFound`）**只给"返回列表"，不给"重试"**；其他错误才给重试按钮 |
| AF | 批 4 的 `v-reveal`：**在视口内的元素挂载后立即显现**（不等观察器），只有视口外的元素才用 `IntersectionObserver`；`prefers-reduced-motion` 或不支持 IO 时完全不添加隐藏态——**动画不得成为内容可见性的前提**（见报错记录 8） |
| AG | 批 4 的目录内容**从真实渲染结果里取**（`bodyRoot.querySelectorAll('h2[id], h3[id]')`），不再把 Markdown 解析第二遍，保证目录 id 与正文天然一致 |

---

## 三、待确认 / 待执行

1. **阶段 4 批 4 已就绪**：改动**尚未 git 提交**（等作者"批 4 通过，提交并继续"）；
2. **批 5 为阶段 4 收尾**：`npm run build` 终测 + 浏览器逐项实拍（首页 / 列表 / 翻页 / 详情 / 目录 / 404 / 错误态 / 三态主题 / 375px）+ `README.md` 功能清单与加分项状态更新 + `current-state.md` 整份覆盖 + `collaboration-log.md` 补阶段 4 记录；
3. **无待作者补验项**：第 13、14 条（过渡观感、触摸点按路径）已由作者验收（第五节）；
4. **可选未做项**（不阻塞）：SQLite WAL 模式；契约标为阶段 8 的可选项（`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理、`view_count` 计数）。

---

## 四、阶段批结果

### 阶段 3 逐批结果（作者已确认全部批次）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 批 0 ✅ | 开工基线 | `README.md` 顶部状态块更正；本文件同步；`npm run build` 基线 28 模块 / 160ms；提交 `8c5923d` |
| 批 1 ✅ | 主题系统（三态） | 新建（完整）`src/stores/theme.js`、`src/components/ThemeToggle.vue`；修改（完整）`src/styles/base.css`、`src/App.vue`；删除 2 个 `.gitkeep`。实测 31 模块 / 131ms；浏览器验证三态循环 + 刷新不丢 |
| 批 2 ✅ | 导航栏 + 路由 + 404 | 新建（完整）`AppHeader.vue`、`NotFoundView.vue`；新建（占位）`ArticlesView.vue`、`AboutView.vue`；修改 `router/index.js`、`App.vue`、`base.css`。实测 39 模块；四路径标题 + 高亮 + 404 + 深色主题 |
| 批 3 ✅ | 汉堡菜单 + 滚动样式 | 修改（完整）`AppHeader.vue`、`base.css`。实测 CSS 6.10 kB；375×667 折叠 / 键盘开合 / Esc / 遮罩 / 滚动后页头半透明模糊 |
| 批 4 ✅ | 路由过渡 + 收尾 | 新建（完整）`AppFooter.vue`；修改（完整）`App.vue`、`base.css`；实测 140ms / 主包 106.33 kB；提交 `378e3b0` |

### 阶段 4 逐批结果（进行中）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 批 0 ✅ | 开工基线（已入库 `ec848f3`） | 新建 `frontend/public/favicon.svg` + 12 张封面 SVG（**内置浏览器实拍 13 张全部正常渲染**）；`data.sql` 3→12 篇 + 8 标签 + 23 关联 + 封面回填；数据库重置重建；`npm run build` 41 模块 / 137ms |
| 批 0 实测明细 | 种子与数据库 | `GET /api/tags` → 8 个；`GET /api/articles?size=20&status=ALL` → `total=12`、封面 12/12、无重复 id；`?tags=前端` → 4 篇；**二次重启数量不变 = 幂等**；契约长度核对 12 篇全部合规 |
| 批 1 ✅ | `src/api/` 接口访问层（已入库 `b6f7701`） | 新建 `src/api/{http,error,articles,tags}.js`；删除 `.gitkeep`。**14 项真实调用用例 14/14 通过**（正例 8 + 异常 6）；`npm run build` 41 模块 / 126ms |
| 批 1 期间故障 | 后端启动失败 | `Port 8080 was already in use`（非本会话实例占用）；确认空闲后重启完成验证；见报错记录 7 |
| 批 2 ✅ | 文章列表 + 首页门面（已入库 `af913f0`） | 新建 `utils/date.js`、`components/{ArticleCard,ArticleSkeleton,ArticleList,Pagination}.vue` + 占位详情页与路由；重写 `views/{ArticlesView,HomeView}.vue`；`npm run build` 55 模块 / 156ms |
| 批 2 实测明细 | 浏览器 10 项（1280 + 375） | 首页 3 张卡片；列表 `共 12 篇文章` + 10 张 + 分页；点第 2 页 → `?page=2` + 2 张；深链刷新仍第 2 页；`?page=99` 空态；**错误态 + 重试恢复**；375 单列；暗色配色；悬停上浮 + 标题变主题蓝；骨架屏亮/暗正确 |
| 批 3 ✅ | 文章详情 + Markdown 渲染（已入库 `44b0d46`） | 新建 `utils/markdown.js`、`components/{MarkdownRenderer,SkeletonBlock}.vue`；`base.css` 追加 `--hl-*`；重写 `ArticleDetailView.vue`。`npm run build` **119 模块 / 169ms**；详情页独立 chunk **283.79 kB（gzip 105.72 kB）**，全在懒加载链上 |
| 批 3 实测明细 | Markdown 管线 15 项 + 页面 6 项 | 标题 id（英文 slug / 中文 `section-N` / 同名去重）；java 高亮；未知语言转义不高亮；原始 `<script>`、`<img onerror>` 被转义为文本；`javascript:` 链接不成 `<a>`；**DOMPurify 直接清洗掉 `onerror` / `javascript:` / `style`+`iframe`**；外链 target+rel、站内链接不加；表格与引用保留；未注入 `__xss`。页面：详情正常渲染 + 动态标题；`/articles/999999` → 「文章不存在」+ 返回（无重试）；停后端 → 错误态 + 重试恢复；亮/暗两套高亮配色；375 排版正常；骨架屏两套主题正确 |
| 批 4 ✅ | 加分项：TOC + 滚动高亮 + 进场动画 + 图片懒加载（待提交） | 新建（完整）`components/TableOfContents.vue`、`utils/reveal.js`、`utils/scrollSpy.js`；`main.js` 注册全局指令 `v-reveal`；`base.css` 加 `.reveal` / `.is-revealed` 两态（含 reduced-motion 兜底）；`utils/markdown.js` 的 `image` 规则加 `loading="lazy"` + `decoding="async"`（并放进 DOMPurify 白名单）；`components/ArticleList.vue` 的 `<li>` 加 `v-reveal="{ delay: … }"` 错落；`views/ArticleDetailView.vue` 改为两栏（正文 + 右侧目录）、接入滚动高亮与进场。`npm run build` **123 模块 / 169ms**（主包 108.32 kB、详情 chunk 286.22 kB） |
| 批 4 实测明细 | 浏览器 6 项（1280×800 + 375×667） | ① 详情页右侧目录列出 4 个小节（决策 Y）；② 目录随滚动高亮跟随（顶部高亮第 1 项；滚到底兜底高亮最后一节「代价」）；③ 点目录项精确跳转（标题停在吸顶页头下方，`scroll-margin-top` 生效）且高亮同步；④ 375px 下目录整块隐藏、正文单列；⑤ 列表页 12 张卡片全部可见（**不再卡在隐藏态**），下方卡片滚入视口后正常显现；⑥ 骨架屏原语与 Markdown 图片懒加载属性经临时页断言通过（`loading="lazy"` / `decoding="async"` 未被 DOMPurify 剥掉） |
| 批 4 自查缺陷 | 两个前端缺陷已修复｜见报错记录 8 | ① `v-reveal` 曾把首屏内容永久藏在 `opacity: 0`（后台标签页里 IO 首次投递被推迟）→ 改为"**视口内元素挂载即显现**"，只有视口外元素才等观察器；② 详情页目录一直为空（`collectHeadings()` 在 `loading` 仍为 `true`、模板还停在骨架分支时执行）→ 挪到 `finally` 之后 + `nextTick` |
| 批 5 | 阶段 4 收尾（构建终测 + 浏览器逐项实拍 + README/docs 同步 + 提交） | 未开始 |

---

## 五、遗留问题

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1 | ~~后端首次启动结果未知~~ **已解决** | — | 阶段 1 批 4 实测：启动、建表、种子数据、接口全部正常 |
| 2 | ~~JDK 26 兼容性未实测~~ **已解决** | — | 实测可用：`Starting BlogApplication using Java 26.0.2.1` |
| 3 | 依赖下载慢（首次约 10 分钟量级） | 影响验证耗时 | 依赖已缓存，后续启动 2 秒级 |
| 4 | ~~接口契约与数据模型是草案~~ **已解决** | — | v1.0 已确认，阶段 2 全部按契约实现 |
| 5 | 项目目录路径含空格 | 目前低风险 | 出现工具异常时再评估迁移 |
| 6 | ~~`frontend/public/` 未创建~~ **已解决** | — | 阶段 4 批 0 新建（favicon + 12 张封面），生产构建已验证复制进 `dist/` |
| 7 | ~~种子文章仅 3 篇~~ **已解决** | — | 阶段 4 批 0 补齐为 **12 篇 + 8 标签 + 23 条关联**，二次启动幂等 |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、`README.md`、Swagger 描述中标注 |
| 9 | ~~SQLite 的 `busy_timeout` 未设置~~ **部分解决** | — | `busy_timeout=5000` 已生效；**WAL 仍未启用** |
| 10 | 结束后台任务会留下派生进程（JVM / node），或启动前 8080 已有别的实例 | 端口占用 / 前端经代理拿到 502 | 阶段 3 复现 4 次、阶段 4 复现 4 次以上；统一流程：`netstat -ano \| grep ":端口" \| grep -i listening` 取 PID 后 `taskkill //PID <pid> //F` |
| 11 | ~~JDK 26 提示 native-access 警告~~ **已处理** | — | 结论：仅警告，不写进标准启动命令 |
| 12 | ~~外键约束是否真正打开未验证~~ **已解决** | — | 批 2b 实测：删文章后三张关联表计数归 0 |
| 13 | ~~过渡动画观感与 `prefers-reduced-motion` 禁用未实测~~ **作者已验收** | — | 作者开工时回复"遗留问题已验收完毕"，AI 按本条与第 14 条理解 |
| 14 | ~~触摸点按路径未由 AI 实测~~ **作者已验收** | — | 同上；工具限制记录保留在 `debug-log.md` 观察项 |
| 15 | ~~种子数据的固定 tag ID 与历史残留行冲突~~ **已解决** | — | 重置重建并复验；见报错记录 6 |
| 16 | 前端接口层与页面暂无自动化回归（靠临时页 + 人工浏览器验证，页均删除） | 后续每批仍需人工跑一遍 | 批 4 又用了一次临时页（含"后台标签页 rAF 不触发"的适配）；如阶段 9 有余量再考虑固化成脚本 |
| 17 | 列表页 `?page` 超过总页数时只显示空态，不回退最后一页 | 手输极端页码体验一般 | 批 5 或阶段 7 视余量决定 |
| 18 | 骨架屏的**出现时机**未能真机抓拍（本地请求约 10ms） | 只能证明外观正确 | 外观已验证（批 2 ⑩、批 3 ⑥）；真实时长在阶段 7 做无限滚动时观察 |
| 19 | 详情页**没有上一篇 / 下一篇**（契约 `prev` / `next` 恒为 `null`） | 少一条浏览路径 | 决策 U：阶段 8 实现 `adjacent` 时一并渲染 |
| 20 | 详情页 chunk 体积偏大（286.22 kB / gzip ≈107 kB） | 首次进详情页多下 ~107 kB（gzip） | 预期取舍（决策 W）；余量允许时可改 `hljs/lib/core` + 按需注册语言 |
| 21 | **Markdown 正文图片懒加载没有真实内容可验证**（12 篇种子文章都不含图片） | 该条加分项只能靠构造样例断言（已通过） | 阶段 5 / 8 若有余量，可给某篇文章补一张本地 SVG 配图（需改 `data.sql` + 幂等 `UPDATE` 或重置库），届时可肉眼复验 |
| 22 | 目录（TOC）只在桌面显示，窄屏没有可展开的入口 | 手机上无法跳转章节 | 决策 Y 的既定取舍；若阶段 7 做体验迭代可加"折叠式目录按钮" |

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK | `java 26.0.2.1`；**已验证可运行 Spring Boot 4.1.1** |
| Maven | 未安装；使用 `mvnw`（wrapper 3.3.4 / Maven 3.9.16 已缓存） |
| Node / npm | `node v24.21.0`、`npm 11.19.0` |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9（决策 W：**不新增依赖**） |
| 后端依赖 | Spring Boot 4.1.1（`spring-boot-starter-webmvc`）、Spring 7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc 3.1.1、**Jackson 3** |
| 数据库 | `backend/data/blog.db`；6 张表 + 7 个索引；`article=12`、`tag=8`、`article_tag=23`；封面 12/12 非空；二次启动幂等；`busy_timeout=5000` |
| 前端静态资源 | `frontend/public/favicon.svg` + `images/covers/*.svg`（12 张，652–1438 字节） |
| 前端接口层 | `src/api/`：`http.js`、`error.js`、`articles.js`、`tags.js`；默认超时 8s |
| 前端渲染层 | `src/utils/markdown.js`（`renderMarkdown()`，标题补 id、外链 target+rel、图片 lazy）、`src/utils/scrollSpy.js`（`useScrollSpy`）、`src/utils/reveal.js`（`v-reveal` 指令）、`src/components/{MarkdownRenderer,SkeletonBlock,TableOfContents}.vue` |
| 服务状态 | **后端与前端 dev server 均已停止**（批 4 验证后复查 `5173/8080 均已释放`） |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达 |
| Git 仓库 | 分支 `main`；提交链：`501065a` → `25e280e` → `e9ab336` → `92a5cc6` → `b22592d` → `5c210a3` → `4b1700b` → `7174838` → `af2334c` → `8c5923d` → `7de72da` → `b257499` → `b1a517f` → `378e3b0` → `550f576` → `ec848f3` → `b6f7701` → `af913f0` → `bc89da3` → `44b0d46` → `9eb671a`（批 3 文档）；**阶段 4 批 4 改动尚未提交**（`git status`：5 个 `M` + 3 个 `??`） |
| 前端构建基线 | 阶段 4 批 4 实测：`npm run build` → `✓ 123 modules transformed` / `✓ built in 169ms`（收尾复跑 203ms）；主包 `index-*.js 108.32 kB / gzip 42.40 kB`、`index css 7.10 kB`、详情 chunk `ArticleDetailView-*.js 286.22 kB / gzip 106.66 kB`、`ArticleDetailView-*.css 6.25 kB`、`dist/index.html 1.36 kB` |
| 前端页面结构 | 四条正式路由 + 404 兜底；页头 / 主内容 / 页脚三段式；主题键 `blog:theme`；列表页码与 `?page=N` 同步；详情页两栏（正文 + 目录，≥1024px）、动态标题、滚动高亮；全局指令 `v-reveal`；组件共 9 个（`ArticleList` / `ArticleCard` / `ArticleSkeleton` / `Pagination` / `SkeletonBlock` / `MarkdownRenderer` / `TableOfContents` / `AppHeader` / `AppFooter` / `ThemeToggle`） |

---

## 七、后端接口清单（阶段 2 产物 · 契约 v1.0）

**已实现并实测通过（13 个操作）**

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/health` | 健康检查 |
| GET | `/api/articles` | 文章列表：分页 / `keyword` / `tags`+`tagMode` / `status` |
| GET | `/api/articles/{id}` | 文章详情（`prev` / `next` 恒为 `null`） |
| POST | `/api/articles` | 创建文章（201；标签不存在自动创建） |
| PUT | `/api/articles/{id}` | 更新文章（PUT 全量） |
| DELETE | `/api/articles/{id}` | 删除文章（级联清理评论 / 点赞 / 标签关联） |
| GET | `/api/tags` | 标签列表（含文章数，倒序） |
| GET | `/api/articles/{id}/comments` | 评论列表（分页） |
| POST | `/api/articles/{id}/comments` | 发表评论（201） |
| DELETE | `/api/comments/{id}` | 删除评论（带 `visitorId` 归属校验） |
| GET | `/api/articles/{id}/likes?visitorId=` | 点赞状态与总数 |
| POST | `/api/articles/{id}/likes` | 点赞（幂等） |
| DELETE | `/api/articles/{id}/likes` | 取消点赞（幂等） |

**未实现（契约标为阶段 8 可选项）**：`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理、`view_count` 计数。

**统一约定**：响应 `{code, message, data}`；错误码 `0` / `40001` / `40002` / `40004` / `40009` / `50000` / `50001`；时间格式 `yyyy-MM-dd'T'HH:mm:ss`；分页 `page` 从 1 起、`size` 1–20。

---

## 八、如何启动与验证（标准命令）

```bash
# 后端（Git Bash）
cd backend && ./mvnw -B -ntp spring-boot:run
```
```powershell
# 后端（Windows PowerShell / CMD）
cd backend; .\mvnw.cmd -B -ntp spring-boot:run
```
```bash
# 前端
cd frontend && npm install && npm run dev
```

访问地址：前端 http://localhost:5173 ｜ 后端 http://localhost:8080 ｜ Swagger UI http://localhost:8080/swagger-ui/index.html ｜ API 前缀 `/api`。

> 贴士：① 路径含空格，`cd` 时**必须加引号**；② 后端只在**运行期间**才能访问 `/api/*` 与 Swagger UI；③ **启动后端前先查 8080**（`netstat -ano | grep ":8080" | grep -i listening`），端口被占先确认是不是已有实例在跑；④ 停服后如仍有进程占端口：查到 PID 后 `taskkill //PID <pid> //F`；⑤ 数据库重置＝停服 → 删 `backend/data/blog.db`、`-wal`、`-shm` → 重启（会丢数据）；⑥ 主题等本地数据重置＝DevTools → Application → Local Storage → 删除 `blog:` 前缀的键；⑦ Git Bash 的 `curl` 传中文会被按 GBK 编码，请用 `node -e` 的 `fetch` 或 Swagger UI；⑧ 前端经代理返回 **502** 时先确认后端是否还在监听；⑨ 可直接访问 http://localhost:5173/articles?page=2 （深链）、http://localhost:5173/articles/7 （详情 + 目录，≥1024px 窗口可见右侧目录）、http://localhost:5173/articles/999999 （文章不存在）逐项自验。
