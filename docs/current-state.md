# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 4「前端模块二 / 三：文章列表与文章详情」批 2（文章列表 + 首页门面）已完成并入库（`af913f0`）**；批 0、批 1 亦已入库（`ec848f3` / `b6f7701`）；阶段 0–3 全部完成并入库。**下一步：批 3（文章详情 + Markdown 渲染）——等作者开工指令。**

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 v1.0，**已确认**，实现时不得擅自改字段）。
2. **进度**：阶段 0、0.5、1、2、3 已完成并入库；**阶段 4 批 0、批 1、批 2 已入库（`ec848f3`、`b6f7701`、`af913f0`）**，**当前应从批 3「文章详情 / Markdown 渲染」开始**（等作者指令）；后端 13 个操作已全部实现并实测（见第七节清单）；前端已有导航栏 / 汉堡菜单 / 三态主题 / 404 / 路由过渡 / 页脚 / 接口访问层 / **文章列表（卡片 + 骨架屏 + 空态 + 错误态 + 分页）+ 首页门面**；**唯一剩下的占位页是 `ArticleDetailView.vue`（批 3 填内容）**；种子数据为 **12 篇文章 / 8 个标签**，封面为 `frontend/public/images/covers/*.svg`。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水）、`docs/collaboration-log.md`（关键提示词汇总 + 阶段索引 + 阶段记录）、`docs/debug-log.md`（报错专档，已有 **7 条**真实记录 + 9 条观察项）；审计记录在 `docs/audit-report.md`（阶段 9 才做）。
4. **环境事实**：Node 24.21.0 / npm 11.19.0 已安装；JDK 26 **已验证**可跑 Spring Boot 4.1.1；Maven 未安装但 `mvnw` 可用（依赖已缓存，后端启动约 2 秒）；**后端与前端 dev server 当前均处于停止状态**，需要时用第八节的标准命令启动。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题。
6. **本机已踩过的坑（细节都在 `docs/debug-log.md`）**：① Git Bash 里给 `curl` 传中文参数会被按 **GBK** 编码 → 改用预编码 UTF-8 百分号串、`node -e` 的 `fetch`，或用 Swagger UI / PowerShell `Invoke-RestMethod`；② `wc -m` 在本机按**字节**计数（要数中文长度得按码点算）；③ AI 的后台任务有 **10 分钟上限**，超时只杀包装进程、**派生 JVM / node 会继续存活**并占住 8080 / 5173（用 `taskkill //PID <pid> //F` 清理，阶段 3 复现 4 次、阶段 4 已复现 3 次）；④ 浏览器提示"拒绝连接"时，排查第一步永远是"确认服务是否真的在监听"；⑤ **内置浏览器的合成点击在部分元素上不送达**（主题按钮在移动仿真与桌面模式下都点不动，键盘 `Tab` + `Enter` 立刻成功；分页按钮与重试按钮的点按是有效的）→ 交互验证优先键盘路径，移动端点按交作者真机；⑥ **路由过渡中间帧读不到"带过渡的子树"**（`page.text.snapshot` / `page.elements.snapshot` 只会读到页头，`wait_for` 文本条件会一直超时，但同期截图正常）→ 先截图确认画面再读结构；⑦ **固定 ID 的幂等种子数据遇到"历史遗留行占用同一 ID"会静默错位**（`INSERT OR IGNORE` 是整行跳过，不是更新），见报错记录 6；⑧ **启动后端前先查 8080**：端口被占不等于程序坏了（见报错记录 7），前端经代理拿到 **502** 通常就是"后端不在监听"。
7. **前端代码地图（阶段 4 现状）**：
   - `src/api/`：`http.js`（`request()` 统一入口 + 超时 + 解包 + 错误归一，502/503/504 有可操作文案）、`error.js`（`ApiError` + `isNotFound` / `isValidationError`）、`articles.js`（`fetchArticles` / `fetchArticleDetail`）、`tags.js`（`fetchTags`）；**写接口的 `comments.js` / `likes.js` 等阶段 5 用到时再建**（决策 AB，避免死代码）。
   - `src/components/`：`ArticleList.vue`（四态容器：加载 / 出错 + 重试 / 空结果 / 有数据）、`ArticleCard.vue`（封面 + 标题 + 摘要 + 标签 + 日期 + 点赞评论数，整卡可点）、`ArticleSkeleton.vue`（骨架 + 流光）、`Pagination.vue`；导航与主题组件仍为阶段 3 产物。
   - `src/utils/date.js`：契约 ISO 串 → `YYYY-MM-DD`（刻意不用 `new Date()`，避免时区解释坑）。
   - 路由：`/`、`/articles`、`/articles/:id`（当前是占位页）、`/about` + 404 兜底；列表页页码与地址栏 `?page=N` 双向同步（第 1 页不带参数）。

---

## 一、当前阶段

**阶段 4「前端模块二 / 三：文章列表与文章详情」：进行中 —— 批 0 ✅、批 1 ✅、批 2 ✅（均已入库），等作者开工指令后进入批 3。**
阶段 0–3 全部完成并入库（阶段 3 的点按路径与过渡观感已由作者验收，见第五节第 13、14 条）。

阶段 4 分批（作者已同意方案与 10 个决策点，见第二节决策 R–AA）：
批 0 开工基线 ✅ → 批 1 API 访问层 ✅ → 批 2 文章列表（卡片 / 悬停动效 / 骨架屏 / 空态与错误态 / 分页控件 / 首页门面）✅ → 批 3 文章详情（Markdown 渲染 + 代码高亮 + 错误分流）→ 批 4 加分项（TOC + 滚动高亮 + IntersectionObserver 进场 + 图片懒加载）→ 批 5 收尾与文档。

---

## 二、已完成

| 时间点 | 事项 | 产物与证据 |
|---|---|---|
| 阶段 0 | 需求确认与技术选型（未写代码） | 选型：Vue 3 + Vite、原生 CSS 变量、Spring Boot 4.1.1 + JdbcTemplate + SQLite；0–9 阶段路线 |
| 阶段 0.5 | 协作日志规范确认 | `ai-log.md`（流水）+ `collaboration-log.md`（汇总），四套模板与禁止事项 |
| 阶段 1 批 1–4 | 目录骨架、docs、frontend、backend 全部落地 | 根目录 3 文件 + docs 9 文件 + frontend 12 文件 + backend 15 文件；`git init`；契约与模型升级 v1.0；提交 `501065a` → `25e280e` |
| 报错修复 | 浏览器 `http://127.0.0.1:5173` 被拒绝 | 根因：Vite 只绑定 IPv6；修复：`vite.config.js` 加 `host: '127.0.0.1'`（报错记录 2） |
| 阶段 2 批 0–4 | 后端业务实现（13 个操作） | 累计 52 项接口实测通过；外键级联、点赞幂等、`busy_timeout` 均实测；提交 `e9ab336` → `7174838` → `af2334c` |
| 阶段 3 批 0 | 开工基线（文档更正） | `README.md` 状态块 + `current-state.md` 同步；`npm run build` 基线 28 模块 / 160ms；提交 `8c5923d` |
| 阶段 3 批 1 | 主题系统（三态） | 新建 `stores/theme.js`、`components/ThemeToggle.vue`；扩展 `styles/base.css`；删 2 个 `.gitkeep`。浏览器实测三态循环 + 刷新不丢；提交 `7de72da` |
| 阶段 3 批 2 | 导航栏 + 路由目标 + 404 | 新建 `components/AppHeader.vue`、`views/NotFoundView.vue`（完整）与 `views/{ArticlesView,AboutView}.vue`（占位）；路由加 `/articles`、`/about`、`/:pathMatch(.*)*` 与 `document.title` 同步；各视图独立 chunk；提交 `b257499` |
| 阶段 3 批 3 | 移动端汉堡菜单 + 滚动样式变化 | `AppHeader.vue` 重写（`aria-expanded` / `aria-controls`、Esc、焦点进出、遮罩、`backdrop-filter` 页头）+ `--color-bg-header`。375×667 实测折叠 / 键盘开合 / Esc / 滚动模糊，桌面无回归；提交 `b1a517f` |
| 阶段 3 批 4 | 路由过渡 + 页脚拆分 + 文档同步（阶段 3 收尾） | 新建 `components/AppFooter.vue`；`App.vue` 加 `<Transition name="page" mode="out-in">`；`base.css` 加 `.page-*`；`README.md` 与四份 docs 同步；提交 `378e3b0` |
| 阶段 4 批 0 | 开工基线：三项申请落地 + 种子数据 + 数据库重建 | 新建 `frontend/public/`（favicon + 12 张自绘封面 SVG，共 13 个文件）；`data.sql` 由 3 篇扩为 **12 篇 + 8 标签 + 23 条关联**并加封面回填；`index.html` 引用 favicon；数据库按标准路径重置重建，接口实测 `total=12` / `tags=8` / 封面 12/12 / 二次启动幂等；`npm run build` 基线 **41 模块 / 137ms**；提交 `ec848f3`（20 文件）。**修掉 1 个真实数据问题**（报错记录 6） |
| 阶段 4 批 1 | `src/api/` 接口访问层 | 新建（完整）`src/api/http.js`、`error.js`、`articles.js`、`tags.js`；删除 `src/api/.gitkeep`；**14 项真实调用用例全通过**（经 Vite 代理打到后端）；`npm run build` 41 模块 / 126ms；提交 `b6f7701`（10 文件）。**期间遇到并记录 1 个真实启动故障**（报错记录 7） |
| 阶段 4 批 2 | 文章列表 + 首页门面 | 新建（完整）`src/utils/date.js`、`src/components/{ArticleCard,ArticleSkeleton,ArticleList,Pagination}.vue`；新建（**占位**）`src/views/ArticleDetailView.vue` + 路由 `/articles/:id`；重写（完整）`src/views/{ArticlesView,HomeView}.vue`；`http.js` 补 502 文案；删除 `src/utils/.gitkeep`；`npm run build` **55 模块 / 156ms**；**10 项浏览器实测通过**（首页、列表、翻页、深链、空态、错误态 + 重试、移动端、暗色、悬停、骨架屏）；提交 `af913f0`（16 文件，+796/−63） |

**关键决策记录（作者已确认）**

| 编号 | 决策 |
|---|---|
| A1 | 项目根目录沿用当前目录，接受名称与"连字符"要求的差异 |
| B1 | 同时维护 `docs/ai-log.md` 与 `docs/collaboration-log.md` |
| C1 | `mvnw` 三件套取自 start.spring.io 官方骨架 |
| D1 | 数据库含第 5 张关联表 `article_tag` |
| E | 技术栈锁定 Vue 3 + Vite + Spring Boot + JdbcTemplate + SQLite；授权 AI 安装 Node |
| F | 契约 8 项 + 数据模型 5 项全部按建议通过；允许新增 `.gitattributes`；执行首次 git 提交 |
| G | 前端 dev server 显式绑定 IPv4（`host: '127.0.0.1'`），不采用 `host: true`（避免暴露到局域网） |
| H | 阶段 2 分批方案：把"原批 2"拆成 **2a 读路径 / 2b 写路径** |
| I | 阶段 2 实测产生的文章 5：**只删文章、保留标签**（选 b，已用接口删除） |
| J | `--enable-native-access` 只写进 README 排查说明，不进标准启动命令 |
| K | 阶段 3 菜单项锁定三项：首页 `/`、文章列表 `/articles`、关于 `/about`；后两者阶段 3 只做**占位页** |
| L | 阶段 3 **新增 404 页**（`NotFoundView.vue`），替换"非匹配路径一律重定向首页"的兜底 |
| M | 主题切换采用**三态**：亮 / 暗 / 跟随系统（`blog:theme` 存 `light` / `dark` / `system`） |
| N | 主题按钮固定在导航栏右侧（桌面与移动端同一位置，不藏进汉堡菜单） |
| O | 关于页阶段 3 **不调用后端**，保持纯前端（前后端对接演练留阶段 6） |
| P | 阶段 3 不新增 `frontend/public/`，不新增 `src/` 子目录（复用逻辑放 `src/utils/`） |
| Q | 阶段 3 批 2（导航结构）/ 批 3（移动端交互）**不拆分**，沿用阶段 2 的"每批停下等确认"节奏 |
| R | 阶段 4 三项开工申请**全部同意**：①新增 `frontend/public/`（favicon + 封面 SVG）；②种子文章补到约 12 篇；③阶段 4 **直接联调真实后端**（不先写 mock，阶段 6 转为全链路回归 + 异常/空态演练） |
| S | 列表翻页阶段 4 先用**分页控件**（`size=10`，契约默认），"无限滚动 + 骨架屏"整体留阶段 7 迭代 |
| T | **首页定位**＝站点简介 + 最新 3–5 篇卡片 + "查看全部文章"入口；完整列表在 `/articles`（两处复用同一列表组件） |
| U | 详情页 `prev` / `next`（契约里恒为 `null`，阶段 8 才实现）阶段 4 **不渲染**该区块 |
| V | 代码高亮**手写**亮 / 暗两套极简配色（CSS 变量），**不引入** highlight.js 官方主题 CSS |
| W | 阶段 4 **不新增任何前端依赖**（现成 markdown-it 15.0.2 / highlight.js 11.12.0 / dompurify 3.4.15 已够用） |
| X | Markdown 标题锚点 `id` **自己写 markdown-it renderer 规则**生成（中文标题用序号兜底），不引插件 |
| Y | TOC 版式：桌面（≥1024px）右侧固定栏，窄屏隐藏 |
| Z | 滚动实现：TOC 当前小节高亮用 `scroll` + `getBoundingClientRect`，进场动画用 `IntersectionObserver`；复用逻辑仍放 `src/utils/`，**不新增 `src/` 子目录**（延续决策 P） |
| AA | 种子数据暴露的 tag ID 冲突（`id=5` 被阶段 2 残留标签占用）**采用标准重置路径解决**：停服 → 删 `blog.db` → 重启自动重建（作者确认，属不可恢复操作，见报错记录 6） |
| AB | 批 1 **只建 `http.js` / `error.js` / `articles.js` / `tags.js`**；`comments.js` / `likes.js` 推迟到阶段 5（用到时再建），理由是避免死代码（阶段 9 审计含"死代码"检查项） |
| AC | 批 2 就**先加 `/articles/:id` 路由 + 占位详情页**，让列表卡片的链接有落点而不是掉进 404；正文渲染留批 3（沿用阶段 3 建占位页的既有做法） |

---

## 三、待确认 / 待执行

1. **阶段 4 前三个批均已入库**：批 0 `ec848f3`、批 1 `b6f7701`、批 2 `af913f0`；**等作者开工指令后进入批 3**；
2. **批 3–5 为纯实现**：详情 + Markdown → 加分项（TOC / 动效 / 懒加载）→ 收尾文档；无需再申请新目录（`src/api/`、`src/utils/`、`src/components/` 均已存在）；
3. **无待作者补验项**：第 13、14 条（过渡观感、触摸点按路径）已由作者验收（第五节）；
4. **可选未做项**（不阻塞）：SQLite WAL 模式（连接池为 1，收益有限）；契约标为阶段 8 的可选项（`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理 `POST` / `PUT` / `DELETE /api/tags`、`view_count` 计数）。

---

## 四、阶段批结果

### 阶段 3 逐批结果（作者已确认全部批次）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 批 0 ✅ | 开工基线 | `README.md` 顶部状态块更正；本文件同步；`npm run build` 基线 28 模块 / 160ms；提交 `8c5923d` |
| 批 1 ✅ | 主题系统（三态） | 新建（完整）`src/stores/theme.js`、`src/components/ThemeToggle.vue`；修改（完整）`src/styles/base.css`、`src/App.vue`；删除 2 个 `.gitkeep`。实测 31 模块 / 131ms；浏览器验证三态循环 + 刷新不丢 |
| 批 2 ✅ | 导航栏 + 路由 + 404 | 新建（完整）`AppHeader.vue`、`NotFoundView.vue`；新建（占位）`ArticlesView.vue`、`AboutView.vue`；修改 `router/index.js`、`App.vue`、`base.css`。实测 39 模块且四视图独立 chunk；四路径标题 + 高亮 + 404 + 深色主题 |
| 批 3 ✅ | 汉堡菜单 + 滚动样式 | 修改（完整）`AppHeader.vue`、`base.css`。实测 CSS 6.10 kB；375×667 折叠 / 键盘开合 / Esc / 遮罩 / 滚动后页头半透明模糊；桌面视口无回归（点按路径待补验） |
| 批 4 ✅ | 路由过渡 + 收尾 | 新建（完整）`AppFooter.vue`；修改（完整）`App.vue`、`base.css`；`README.md` + `docs/*` 同步。实测 140ms / CSS 6.46 kB / 主包 106.33 kB（Transition 运行时首次进包）；截图捕获过渡中间帧与稳定态；提交 `378e3b0` |

**验收要点**：导航栏（含滚动样式变化）、汉堡菜单（含键盘可达）、主题三态 + 首屏防闪、当前页高亮、路由过渡、404 页。

### 阶段 4 逐批结果（进行中）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 批 0 ✅ | 开工基线（已入库 `ec848f3`） | 新建（完整）`frontend/public/favicon.svg` + `images/covers/*.svg`（12 张自绘 SVG，390–1438 字节，结构校验无 `<text>` / 无外链 / 无 CSS 变量，**内置浏览器实拍 13 张全部正常渲染**）；修改（完整）`backend/src/main/resources/data.sql`（3→12 篇 + 8 标签 + 23 关联 + 封面回填）、`frontend/index.html`（新增 favicon 引用一行）；数据库按标准路径重置重建；`npm run build` 基线 41 模块 / 137ms，`dist/` 确认包含 13 个 SVG |
| 批 0 实测明细 | 种子与数据库 | 重置后：`GET /api/tags` → 8 个（`1=项目日志(3) 2=Vue(3) 3=Spring Boot(4) 4=SQLite(3) 5=前端(4) 6=CSS(1) 7=后端(4) 8=Markdown(1)`）；`GET /api/articles?size=20&status=ALL` → `total=12`、封面 12/12 非空、无重复 id；`?tags=前端` → 4 篇；`?size=5&page=2` → 5 条（`ids=7,6,5,4,3`）；**二次重启数量不变 = 幂等**；契约长度核对：12 篇的 title / summary / content 全部在 1–100 / 0–200 / 1–50000 内 |
| 批 1 ✅ | `src/api/` 接口访问层（已入库 `b6f7701`） | 新建（完整）`src/api/http.js`、`error.js`、`articles.js`、`tags.js`；删除 `src/api/.gitkeep`。**14 项真实调用用例 14/14 通过**（临时自测页经 Vite 代理 `/api` → 8080，验证后已删除）：正例 8 项（列表默认参数 `total=12`/3 条、中文标签 `tags=前端` 4 篇、多标签 `or`、多标签 `and` 2 篇且两篇都含 Vue+前端、`keyword=SQLite`、详情 id=1、标签 8 个、`size=5&page=3` 2 条）；异常 6 项（详情 999999 → `code=40004`/HTTP 404、`page=abc` → `40002`/400、`timeout=1ms` → "请求超时（1ms）"、未知路径 → `40004`、非法 ID 前置校验、外部 signal → "请求已取消"）。`npm run build` → 41 模块 / 126ms |
| 批 1 期间故障 | 后端启动失败 | `APPLICATION FAILED TO START … Port 8080 was already in use`（8080 上已有一个 22:01:20 启动、非本会话启动的实例）；确认端口空闲后重启自己的实例完成验证；完整闭环见 `debug-log.md` 报错记录 7 |
| 批 2 ✅ | 文章列表 + 首页门面（已入库 `af913f0`） | 新建（完整）`utils/date.js`、`components/{ArticleCard,ArticleSkeleton,ArticleList,Pagination}.vue`；新建（占位）`views/ArticleDetailView.vue` + 路由 `/articles/:id`；重写（完整）`views/{ArticlesView,HomeView}.vue`；`api/http.js` 补 502 文案；删除 `utils/.gitkeep`；`npm run build` **55 模块 / 156ms**（首页与列表页共享 `articles-*.js` 4.93 kB + `articles-*.css` 3.98 kB）；提交 16 文件（+796/−63） |
| 批 2 实测明细 | 浏览器 10 项（桌面 1280×800 + iPhone SE 375×667） | ① 首页：hero 简介 + 3 张最新卡片（封面/标题/摘要/标签/日期/点赞评论数均来自后端）；② `/articles`：`共 12 篇文章` + 10 张卡片 + 分页（当前页禁用上一页）；③ 点「第 2 页」→ URL `?page=2`、2 张卡片、下一页禁用；④ **深链刷新** `?page=2` → 仍 2 张卡片；⑤ `?page=99` → 空态「这一页没有文章」+ 分页可用；⑥ **错误态与重试**：停后端 → `文章列表加载失败 / 无法连接后端服务（HTTP 502），请确认后端已在 http://localhost:8080 运行` + `role="alert"` + 重试按钮；重启后端后点「重试」→ 卡片与分页恢复；⑦ 375×667 单列卡片 + 汉堡菜单无溢出；⑧ 暗色主题卡片配色正确；⑨ 悬停：卡片上浮 + 阴影加强 + 标题变主题蓝（截图对比）；⑩ 骨架屏在亮 / 暗两套主题下并排渲染正确（临时页，验证后删除） |
| 批 3 | 文章详情（Markdown 渲染 + 代码高亮 + 404 与错误分流；阅读进度条归批 4） | 未开始 |
| 批 4 | 加分项（TOC + 滚动高亮 + IntersectionObserver 进场动画 + 图片懒加载） | 未开始 |
| 批 5 | 收尾（构建实测 + 浏览器实拍 + README/docs 同步 + 提交） | 未开始 |

---

## 五、遗留问题

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1 | ~~后端首次启动结果未知~~ **已解决** | — | 阶段 1 批 4 实测：启动、建表、种子数据、接口全部正常 |
| 2 | ~~JDK 26 兼容性未实测~~ **已解决** | — | 实测可用：`Starting BlogApplication using Java 26.0.2.1` |
| 3 | 依赖下载慢（首次约 10 分钟量级） | 影响验证耗时 | 依赖已缓存，后续启动 2 秒级 |
| 4 | ~~接口契约与数据模型是草案~~ **已解决** | — | v1.0 已确认，阶段 2 全部按契约实现 |
| 5 | 项目目录路径含空格 | 目前低风险 | 出现工具异常时再评估迁移 |
| 6 | ~~`frontend/public/`（封面图等静态资源目录）未创建~~ **已解决** | — | 阶段 4 批 0 新建：`public/favicon.svg` + `public/images/covers/*.svg`（12 张），生产构建已验证会复制进 `dist/` |
| 7 | ~~种子文章仅 3 篇~~ **已解决** | — | 阶段 4 批 0 补齐为 **12 篇 + 8 标签 + 23 条关联**，`INSERT OR IGNORE` 幂等（二次启动实测数量不变） |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、`README.md`、Swagger 描述中标注 |
| 9 | ~~SQLite 的 `busy_timeout` 未设置~~ **部分解决** | — | `busy_timeout=5000` 已生效；**WAL 仍未启用**，需要时在 `connection-init-sql` 追加 `PRAGMA journal_mode = WAL` |
| 10 | 结束后台任务会留下派生进程（JVM / node），或**启动前 8080 已有别的实例** | 下次启动报端口占用 / 前端经代理拿到 502 | 阶段 3 复现 4 次；阶段 4 已复现 3 次（批 0 两次 JVM/node 残留，批 1 一次 8080 被非本会话实例占用，见报错记录 7）；统一流程：`netstat -ano \| grep ":端口" \| grep -i listening` 取 PID 后 `taskkill //PID <pid> //F` |
| 11 | ~~JDK 26 提示 native-access 警告~~ **已处理** | — | 结论：仅警告，不写进标准启动命令；`README.md`《常见问题排查》已说明 |
| 12 | ~~外键约束是否在应用连接上真正打开未验证~~ **已解决** | — | 批 2b 实测：删除文章后 `comment` / `like_record` / `article_tag` 计数全部归 0 |
| 13 | ~~过渡动画的**观感**（时长 / 缓动）与 `prefers-reduced-motion` 禁用未由 AI 实测~~ **作者已验收** | — | 作者在阶段 4 开工时回复"遗留问题已验收完毕"，AI 按本条与第 14 条（仅有的两项"待作者补验"）理解为验收通过；如作者指的是其他条目，请指出，AI 再更正本节 |
| 14 | ~~触摸点按路径未由 AI 实测（工具限制）~~ **作者已验收** | — | 同上；工具限制记录保留在 `debug-log.md` 观察项（后续移动端验证仍建议键盘路径优先） |
| 15 | 种子数据的固定 tag ID 与历史残留行冲突（`id=5`） | **已解决** | 阶段 4 批 0 发现并重置重建，详见 `debug-log.md` 报错记录 6；**教训**：扩写固定 ID 的种子后必须核对真实数据 |
| 16 | 前端接口层与页面暂无自动化回归（靠临时自测页 / 临时预览页 + 人工浏览器验证，页均删除） | 后续每批仍需人工跑一遍 | 保留做法：需要时再建临时页（经 Vite 代理真实打后端），验证后删除；如阶段 9 有余量再考虑固化 |
| 17 | 列表页 `?page` 超过总页数时只显示空态，不会回退到最后一页 | 手输极端页码时体验一般 | 阶段 4 批 5 或阶段 7 视余量决定是否加"越界回退"；当前不做（空态 + 上一页可用） |
| 18 | 骨架屏的**出现时机**未能在真机抓拍（本地请求约 10ms，一闪而过） | 只能证明骨架屏渲染正确，不能证明"加载期间显示多久" | 骨架屏外观已在亮 / 暗两套主题下独立验证（批 2 实测 ⑩）；真实加载时长在阶段 7 做无限滚动时可一并观察 |

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK | `java 26.0.2.1`，`JAVA_HOME=D:\Java\jdk-26.0.2.1`；**已验证可运行 Spring Boot 4.1.1** |
| Maven | 本机未安装；项目使用 `mvnw`（wrapper 3.3.4 / only-script / Maven 3.9.16 已下载到 `.m2`） |
| Node / npm | `node v24.21.0`、`npm 11.19.0` |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9（阶段 4 决策 W：**不新增依赖**） |
| 后端依赖 | Spring Boot 4.1.1（Web 起步依赖为 **spring-boot-starter-webmvc**）、Spring 7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc-openapi-starter-webmvc-ui 3.1.1、**Jackson 3**（`tools.jackson.core:jackson-databind:3.1.5`） |
| 数据库 | `backend/data/blog.db`（阶段 4 批 0 按标准路径重置重建）；6 张表 + 7 个索引；`article=12`、`tag=8`、`article_tag=23`；12 篇 `cover_url` 全部非空；二次启动数量一致（幂等）；JDBC URL 已带 `busy_timeout=5000` |
| 前端静态资源 | `frontend/public/favicon.svg`（390 字节）+ `frontend/public/images/covers/*.svg`（12 张，652–1438 字节）；`index.html` 已引用 favicon；`npm run build` 会把它们复制进 `dist/`（已实测） |
| 前端接口层 | `frontend/src/api/`：`http.js`（`request()` 统一入口 + `ApiError` 抛出，502/503/504 有可操作文案）、`error.js`（`ApiError` + `isNotFound` / `isValidationError`）、`articles.js`（`fetchArticles` / `fetchArticleDetail`）、`tags.js`（`fetchTags`）；默认超时 8s；`src/api/.gitkeep` 已删除 |
| 服务状态 | **后端与前端 dev server 均已停止**（批 2 验证后复查 `5173/8080 均已释放`） |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达（报错记录 1） |
| Git 仓库 | 分支 `main`；提交链：`501065a` → `25e280e` → `e9ab336` → `92a5cc6` → `b22592d` → `5c210a3` → `4b1700b` → `7174838` → `af2334c` → `8c5923d` → `7de72da` → `b257499` → `b1a517f` → `378e3b0` → `550f576` → `ec848f3`（批 0）→ `b6f7701`（批 1）→ `af913f0`（批 2）；**工作区干净** |
| 前端构建基线 | 阶段 4 批 2 实测：`npm run build` → `✓ 55 modules transformed` / `✓ built in 156ms`（提交后回归 148ms）；共享 chunk `articles-*.js 4.93 kB` + `articles-*.css`；主包 `index-*.js 106.74 kB / gzip 41.76 kB`、`index css 6.46 kB`、`dist/index.html 1.36 kB`；五个视图各自独立 chunk |
| 前端页面结构 | 四条正式路由（`/`、`/articles`、`/articles/:id`、`/about`）+ 404 兜底；页头 / 主内容 / 页脚三段式；主题键 `blog:theme`；列表页页码与 `?page=N` 同步；组件：`ArticleList`（四态容器）、`ArticleCard`、`ArticleSkeleton`、`Pagination`、`AppHeader`、`AppFooter`、`ThemeToggle` |

---

## 七、后端接口清单（阶段 2 产物 · 契约 v1.0）

**已实现并实测通过（13 个操作）**

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/health` | 健康检查（status + 服务器时间） |
| GET | `/api/articles` | 文章列表：分页 / `keyword` / `tags`+`tagMode=and·or` / `status=PUBLISHED·DRAFT·ALL` |
| GET | `/api/articles/{id}` | 文章详情（含 Markdown 正文；`prev` / `next` 恒为 `null`） |
| POST | `/api/articles` | 创建文章（201；标签不存在自动创建；`summary` 留空取正文前 120 字） |
| PUT | `/api/articles/{id}` | 更新文章（PUT 全量；`tags` 省略或 `[]` = 清空） |
| DELETE | `/api/articles/{id}` | 删除文章（级联清理评论 / 点赞 / 标签关联，标签本身保留） |
| GET | `/api/tags` | 标签列表（含文章数，按数量倒序） |
| GET | `/api/articles/{id}/comments` | 评论列表（分页，创建时间倒序） |
| POST | `/api/articles/{id}/comments` | 发表评论（201；`authorEmail` 与 `visitorId` 不回传） |
| DELETE | `/api/comments/{id}` | 删除评论（请求体带 `visitorId`；不存在或不匹配 → 40004） |
| GET | `/api/articles/{id}/likes?visitorId=` | 点赞状态与总数 |
| POST | `/api/articles/{id}/likes` | 点赞（幂等） |
| DELETE | `/api/articles/{id}/likes` | 取消点赞（幂等） |

**未实现（契约标为阶段 8 可选项）**：`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理 `POST` / `PUT` / `DELETE /api/tags`、`view_count` 计数。

**统一约定**：响应 `{code, message, data}`；错误码 `0` / `40001` / `40002` / `40004` / `40009` / `50000` / `50001`；时间格式 `yyyy-MM-dd'T'HH:mm:ss`；分页 `page` 从 1 起、`size` 1–20（默认 10）。

Swagger 文档：5 个分组（健康检查 / 文章 / 标签 / 评论 / 点赞）、13 条接口摘要，含统一返回与错误码说明。

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

访问地址：前端 http://localhost:5173 ｜ 后端 http://localhost:8080 ｜ Swagger UI http://localhost:8080/swagger-ui/index.html ｜ API 前缀 `/api`（前端经 Vite 代理）。

> 贴士：① 路径含空格，`cd` 时**必须加引号**；② 后端只在**运行期间**才能访问 `/api/*` 与 Swagger UI（报错记录 5 的教训）；③ **启动后端前先查 8080**——`netstat -ano | grep ":8080" | grep -i listening`，若已有实例在跑就是"端口被占"而不是"程序坏了"（报错记录 7）；确实要换端口再改 `application.yml` 的 `server.port` 与 `vite.config.js` 的代理 target；④ 停服务后如仍有进程占端口：`netstat -ano | grep ":5173" | grep -i listening`（或 `:8080`）查到 PID 后 `taskkill //PID <pid> //F`；⑤ 数据库重置＝停服 → 删除 `backend/data/blog.db`、`blog.db-wal`、`blog.db-shm` → 重启（会丢数据，属不可恢复操作；阶段 4 批 0 用过一次）；⑥ 主题等本地数据重置＝DevTools → Application → Local Storage → 删除 `blog:` 前缀的键（阶段 5 会提供页面内重置按钮）；⑦ Git Bash 里的 `curl` 传中文会被按 GBK 编码，验证中文查询参数请用 `node -e` 的 `fetch`、Swagger UI 或 PowerShell；⑧ 前端经代理请求后端返回 **502** 时，先去确认后端是否还在监听；⑨ 列表页第 2 页可直接访问 http://localhost:5173/articles?page=2 验证深链。
