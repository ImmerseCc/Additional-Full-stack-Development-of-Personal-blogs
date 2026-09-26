# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 9（报错记录整理 + 前后端专项审计 + 交付文档与演示脚本）已开工 —— 批 0 开工基线完成（2026-09-26）**；此前**阶段 8 批 0–批 8 已由作者人工验收通过**（13 条清单，2026-09-26），阶段 0–7 全部完成并入库（其中阶段 5 / 6 / 7 亦已验收）。
> **当前阶段：阶段 9 进行中 —— 批 1（正式审计 A · 静态层）待开工**；分批方案与决策 BY–CG 见 §三 / §二。
> **运行状态**：**前后端均在运行** —— 后端 8080、前端 5173；数据库为种子状态（`article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`，`view_count` 已复位为 0，`journal_mode=wal`）。

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 **v1.1 已确认**（阶段 8 批 1，作者复核通过）；阶段 5 批 1 补注 likes 的 `visitorId` 必填）。
2. **进度**：阶段 0–8 **全部完成并入库**（阶段 5 / 6 / 7 / 8 均已由作者人工验收通过）；**阶段 9 已开工，批 0 开工基线完成（2026-09-26）**。**前端六个核心模块 + 写作台 `/studio` 全部完成**；后端**契约 §四 · 1–18 全部实现（20 个操作）**；接口回归脚本 `npm run smoke` **143 项断言**（未覆盖项清零）；**`journal_mode=wal`**。**下一步：批 1（正式审计 A · 静态层，只读）**。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水，阶段 8 批 0–批 8 + 收尾验收记录已录入）、`docs/collaboration-log.md`（关键提示词 + 阶段索引 + 阶段记录，**阶段 8 记录已含验收结论**）、`docs/debug-log.md`（**9 条**真实记录 + **19 条**观察项）；`docs/audit-report.md` 有三块「阶段 6 预审计」+「阶段 7 变更摘要」+「阶段 8 变更摘要」，正式审计（12 项清单）在阶段 9。
4. **环境事实**：Node 24.21.0 / npm 11.19.0；JDK 26 已验证可跑 Spring Boot 4.1.1；Maven 用 `mvnw`；**当前前后端均在运行**；若端口已释放，按第八节命令重新启动（已给出**绝对路径版**）。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题；**阶段验收结论由作者给出，AI 不代签**；**每个阶段结束后作者会新开会话继续**，所以本文件必须足以让下一个会话无缝接手。
6. **本机已踩过的坑**（细节都在 `docs/debug-log.md`）：① Git Bash 的 `curl` 传中文按 **GBK** 编码 → 用 `node -e` 的 `fetch` 或 Swagger UI；② `wc -m` 按**字节**计数；③ 后台任务有 10 分钟上限，超时只杀包装进程、派生 JVM / node 继续存活占端口；④ "拒绝连接"第一步永远是"确认服务是否真的在监听"；⑤ **合成点击偶发不送达** → 拿到 ref 后优先 `press_key` + `Enter`；⑥ 路由过渡中间帧读不到"带过渡的子树"；⑦ 固定 ID 的幂等种子数据遇"历史行占用同 ID"会**静默错位**；⑧ 启动后端前先查 8080；⑨ **后台标签页里 `requestAnimationFrame` / `IntersectionObserver` 被节流**；⑩ **Toast 只活 2.6s**；⑪ **临时 Java 程序在 Git Bash 控制台打印中文会乱码**；⑫ **`page.wait_for` 的 text 条件按视口扫描**；⑬ **本机 Edge 无头模式不产出任何输出**；⑭ **浏览器面板 / 标签页会被回收** → `browser.create_tab` 重开；⑮ **删除文章后标签会保留**；⑯ **触摸模拟下合成点击打不开 `<details>`**；⑰ **元素快照不收录 `<summary>` 与部分文本框** → 用坐标点击 + `visual.type_text`；写作台表单可用 `page.element.fill`；**⑱ Vite HMR 中间态：新标签首次打开主区为空 → `tab.reload` 即正常**；**⑲ `page.visual.snapshot` 偶发 `PAGE_NOT_READY` → 重试一次；跨文档沿用旧 `snapshotId` 会报 `STALE_SNAPSHOT`**。
7. **前端代码地图**（阶段 8 的变化已标注）：
   - `src/api/`：`http.js`（统一请求 / 超时 / 解包 / 错误归一）、`error.js`、`articles.js`（**+ `postArticleView` / `createArticle` / `updateArticle` / `deleteArticle`**）、`tags.js`（**+ `createTag` / `renameTag` / `deleteTag`**）、`comments.js`（**+ `updateComment`**）、`likes.js`
   - `src/utils/`：`date.js`、`markdown.js`、`reveal.js`、`scrollSpy.js`、`debounce.js`、`storage.js`、`visitor.js`、`validate.js`
   - `src/stores/`：`theme.js`、`toast.js`、`likes.js`、`myComments.js`
   - `src/components/`（19 个）：`AppHeader` / `AppFooter` / `ThemeToggle` / `ArticleCard` / `ArticleList` / `ArticleSkeleton` / `SkeletonBlock` / `MarkdownRenderer`（异步加载） / `TableOfContents` / `EmptyState` / `SearchInput` / `TagFilter` / `CommentForm` / **`CommentItem`（+ 行内「编辑」）** / `CommentSection`（+ `onUpdated` 就地替换） / `LikeButton` / `ToastStack` / `LocalDataPanel` / `ReadingProgress` / `BackToTop`
   - `src/views/`：`ArticlesView`、`ArticleDetailView`（**+ 阅读数显示与上报、上下篇导航**）、`AboutView`、`HomeView`、`NotFoundView`、**`StudioView`（阶段 8 批 7 新增：写作台）**
   - `scripts/smoke.mjs`：接口层回归（`npm run smoke`，**143 项断言**）
   - 路由：`/`、`/articles`、`/articles/:id`、`/about`、**`/studio`（隐藏入口，不进导航栏）**、404 兜底
   - **localStorage 键（全部 `blog:` 前缀）**：`theme` / `visitorId` / `likedArticles` / `myComments` / `commentAuthor` / `commentEmail`
8. **写代码时的硬经验**：① **动画不能成为内容可见性的前提**；② **取渲染结果必须等目标分支真正挂载**（`finally` + `await nextTick()`）；③ **读写非响应式外部状态要么等一拍、要么用 `flush: 'post'` 的 watcher**；④ 契约的 `CommentVO` **不回传 `visitorId`**，归属靠本地账本 + 后端校验；⑤ 点赞 / 评论 / **阅读数**计数**以后端返回为准**（阅读数上报后用返回值刷新）；⑥ **异步组件要让"依赖 DOM 的后续计算"多等一拍**；⑦ **会改变布局高度的收起动作要先于滚动执行**；⑧ **原生控件的优先序**：`<details>` / `<dialog>` 比手写浮层更省代码、天然可访问；⑨ **全量 PUT 的接口（改状态 / 改标签）必须先把详情取回来、把其余字段原样带回**。

---

## 一、阶段 9 验收清单（**待作者逐条确认**）

**阶段主题**：报错记录整理 + 前后端专项审计 + 交付文档与演示脚本（批 0–批 6 全部完成）。**验收结论由作者给出，AI 不代签**（AGENTS.md 协作规则 7）。上一阶段（阶段 8）的 13 条验收记录见 `docs/collaboration-log.md` 的阶段记录。

| # | 验收项 | 自测证据（AI 侧） |
|---|---|---|
| 1 | 按 README 启动前后端，页面与接口可用 | 批 3：后端 `Started BlogApplication in 1.923 seconds`、前端 `VITE v8.3.0 ready in 342 ms`；`/api/health` 直连与经代理均 **200** |
| 2 | `npm run smoke` 全绿 | 批 6 终测：**全部通过：145/145 项断言** |
| 3 | `npm run build` 通过 | 批 6 终测：`✓ built in 300ms`（详情 shell 23.12 kB / 管线 281.34 kB / 共享 chunk 65.48 kB） |
| 4 | **审计报告 12 项检查清单逐项有结论** | `docs/audit-report.md`：A 静态层（§1–§9）+ B 运行时层（§1–§9），结论列已全部填满，含误报澄清、局限登记与三类分流 |
| 5 | **静态层发现的问题已修复**（批 2 共 9 项） | 抽查：`frontend/src/utils/scrollFrame.js` 存在且被三处复用；`backend/.../common/Texts.java` 存在；`grep -rn "isValidationError" frontend/src` 无命中；`config/.gitkeep` 已删 |
| 6 | **邮箱口径前后端一致**（`a@b` 被拦下） | `smoke` 新增 2 条断言（`a@b` → 40001、102 字 → 40001）；前端 `utils/validate.js` 与后端 `CommentCreateRequest.EMAIL_PATTERN` 为同一条正则 |
| 7 | **遗留 32 闭环**：异步管线失败态有明确界面 | 演练记录见 `docs/debug-log.md` 报错记录 10；截图 `docs/demo/stage9-01-markdown-load-error.png`；可照做复现（临时改名 `MarkdownRenderer.vue` → 刷新详情页） |
| 8 | 深色模式可读（对比度达标） | 批 3 §5：12 组按 WCAG 实算**全部达标**（暗 正文 15.20 / 次要 7.31 / 链接 6.68；亮 正文 15.80 / 主色 5.23） |
| 9 | **并发与失败路径有真实触发记录** | 批 3 §7：锁内写等待 **5.085s → 50001**、锁内读 **4.8ms → 200**；`journal_mode=wal`、`busy_timeout=5000` |
| 10 | **报错记录整理完毕** | `docs/debug-log.md`：**10 条真实报错** + 索引表 + 观察项表；记录 10 为阶段 9 演练新发现并已修复 |
| 11 | **关键提示词与主指令归档** | `docs/collaboration-log.md` §一 **6 组**（含 #6 项目审计，全部为真实发生）；`docs/main-prompt.md` 已含「四、阶段 9 的交付指令（原文照录）」 |
| 12 | **演示脚本与素材交付** | `docs/demo/README.md`（12 个演示点：操作 / 命令 / 预期 / 截图编号）+ **39 张**素材；**录屏待作者按脚本完成** |
| 13 | 数据未被污染（含 `view_count` 复位） | 批 6 终核（临时 JDBC 直读）：`article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`、**`view_count` 非零行 = 0**、6 表 8 索引、`journal_mode=wal` |

> **说明**：以上 13 条为 AI 侧自测证据，**待作者逐条验收**；验收通过后由 AI 按惯例补写 `docs/collaboration-log.md` 的阶段 9 阶段记录，并整份覆盖本文件。

---

## 二、已完成

| 时间点 | 事项 | 产物与证据 |
|---|---|---|
| 阶段 0 / 0.5 | 需求确认与技术选型 / 协作日志规范 | Vue 3 + Vite + 原生 CSS 变量；Spring Boot 4.1.1 + JdbcTemplate + SQLite；0–9 阶段路线 |
| 阶段 1 批 1–4 | 目录骨架与占位文件 | 根目录 3 + docs 9 + frontend 12 + backend 15 文件；`git init`；契约与模型 v1.0；提交 `501065a` → `25e280e` |
| 阶段 2 批 0–4 | 后端业务实现（13 个操作） | 52 项接口实测；外键级联 / 点赞幂等 / `busy_timeout` 均实测；提交 `e9ab336` → `7174838` → `af2334c` |
| 阶段 3 批 0–4 | 前端模块一（导航 / 主题 / 404 / 移动端 / 过渡） | 提交 `8c5923d` → `7de72da` → `b257499` → `b1a517f` → `378e3b0` |
| 阶段 4 批 0–5 | 前端模块二 / 三（列表 / 详情 / Markdown / 目录 / 进场动画） | 12 篇种子 + 12 张封面；22 项浏览器实测 + 15 项管线断言；提交 `ec848f3` → `16f1726` → `929c3da` |
| 阶段 5 批 0–批 6 | 前端模块四 / 五 / 六 + 收尾 + **作者验收** | 提交 `d6167ea` → … → `3852fca`；14 条清单全通过 |
| 阶段 6 批 0–批 5 | 全链路回归 + 异常 / 空态演练 + 契约逐条复核 | `smoke` 97/97；15 张截图；**作者验收通过（13 条）**；提交 `bad0853` → … → `9f8d775` |
| 阶段 7 批 0–批 8 | 前端体验迭代 + 两条验收后评论体验修订 | 提交 `85e5326` → … → `1904279`；**13 条清单 + 二次确认通过** |
| 阶段 8 批 0–批 8 | **后端能力补全 + 前端接入 + 写作台** | 契约 v1.1；**契约 §四 · 1–18 全部实现（20 个操作）**；`smoke` 97 → **143**；`journal_mode=wal`；截图 `stage8-01…05`；提交 `ada2788` → … → `8dbc5b5` → `2e53303`；**已由作者人工验收通过（13 条，2026-09-26）** |
| 阶段 9 批 0 | **开工基线 + 功能达标自检** | 基线 `npm run build` **274ms**、`npm run smoke` **143/143**、数据 `12 / 8 / 23 / 0 / 0`、6 表 8 索引、`journal_mode=wal`；功能自检结论**全部达标**（3 处差异见 §三）；提交见 `git log` 最新一条 |

**阶段 8 决策记录（BR–BX，作者已确认"均同意，请继续"，2026-09-26）**

| 编号 | 决策 |
|---|---|
| BR | 阶段 8 纳入**极简管理入口**：隐藏路由 `/studio`（文章新建 / 编辑 / 删除 + `PUBLISHED/DRAFT` 切换 + 标签管理）；正文用 Markdown 文本框，不做富文本编辑器、不做登录（演示级无鉴权，页面标注） |
| BS | 评论"修改"**不改表、不改 VO**，编辑就地生效；前端只对本地账本 `blog:myComments` 内的评论显示「编辑」 |
| BT | 相邻文章只取 `PUBLISHED`、`ORDER BY created_at, id`；**详情接口直接带出 `prev` / `next`**（前端零额外请求），`/adjacent` 独立接口同时实现（由 smoke 覆盖） |
| BU | 阅读数新增 `POST /api/articles/{id}/views`（GET 保持无副作用）+ `viewCount` 进 `ArticleSummaryVO` / `ArticleDetailVO`；前端进入详情后上报、显示「阅读 N」 |
| BV | 搜索保持**只匹配标题**，不扩展到摘要 |
| BW | WAL 由 JDBC URL 追加 `journal_mode=WAL` 落地（与 `busy_timeout=5000` 同行）；文档写明 `-wal` / `-shm` 口径 |
| BX | 顺带闭环"未覆盖项"：`50000` / `50001` 与 `SQLITE_BUSY` 演练纳入批 5 |

> 阶段 5 / 6 / 7 的决策记录（含 BF–BQ）见 `docs/collaboration-log.md` 的阶段记录与 `docs/ai-log.md` 各批条目。

**阶段 9 决策记录（BY–CG，作者"均同意，请继续"，2026-09-26）**

| 编号 | 决策 |
|---|---|
| BY | 阶段 9 只做审计 / 整理 / 交付文档与演示脚本，**不新增业务功能**；审计发现的**低成本问题**（死代码、未使用依赖、重复实现、文档不一致）在本阶段修复，**涉及行为变更的**先记录等拍板 |
| BZ | 审计三段式：批 1 静态层（**只读出清单**）→ 批 2 修复 → 批 3 运行时层；**审计过程不改业务代码**，修复独立成批便于追溯 |
| CA | `docs/audit-report.md` 新增「第 1 次审计」正式章节：12 项清单逐项填结论，结果分**已修复 / 未处理 / 需要我亲自验证**三类（"需要我亲自验证" ≥ 2 条含可复制步骤）；阶段 6 / 7 / 8 三份变更摘要保留为附录 |
| CB | 演示交付 = **演示脚本（`docs/demo/README.md`）+ 分步截图**；**录屏由作者按脚本自行录制**（AI 不代录、不代验收） |
| CC | 遗留 32 纳入批 2（补 `errorComponent` 并演练失败态）；遗留 18 **不强行闭环**，改为在演示脚本中注明"由真机 / 网络节流观察" |
| CD | 遗留 20（详情 chunk）/ 25（重置后旧点赞不可自助撤销）/ 33（本机记住评论邮箱）保持演示级语义，**不改代码**，仅在审计报告中登记 |
| CE | 审计真实发生后补写**关键提示词 #6（项目审计）**并与汇总表同步；本阶段指令原文追加进 `docs/main-prompt.md` 末尾 |
| CF | 阶段 9 收尾**保持前后端运行**（便于作者录屏）+ 数据复位为种子状态（`12 / 8 / 23 / 0 / 0`） |
| CG | 每批一次提交 + 收尾文档提交；**不 push**（本机 `github.com` 不可达） |

**批 0 追加确认（作者"2 不必了，其他均同意"，2026-09-26）**：① **重启持久化演练不做**；② 功能达标自检的 3 处差异中，评论错误提示动画 / Toast `warning` 类型**纳入批 2**，虚拟列表保持不做、GitHub 托管只登记。

---

## 三、阶段 9 分批方案与进度（**进行中**）

**定位（决策 BY）**：报错记录整理 + 前后端专项审计 + 交付文档与演示脚本；**不新增业务功能**，审计发现的低成本问题在本阶段修复，涉及行为变更的先记录等拍板。

| 批 | 内容 | 状态 |
|---|---|---|
| 0 | 开工基线：只读核对 + 两端确认 + 基线采集 + 文档更正 | ✅ 完成（2026-09-26） |
| 1 | **正式审计 A · 静态层（只读）**：逐文件阅读前端 48 / 后端 41 个源文件；敏感信息、死代码、重复代码、未使用依赖、内存泄漏与危险操作（审计清单 **7 / 9 / 10 / 11 / 12** + 第 6 项规则对账） | ✅ 完成（2026-09-26）：第 7 / 11 / 12 项通过；发现 2 处重复 + 2 处死代码残留 + 3 处校验口径差异（均低危） |
| 2 | **审计问题修复**：批 1 清单 7 项 + **评论错误提示动画** + **Toast `warning` 类型** + 遗留 32 | ✅ 完成（2026-09-26）：9 项全部修复；**遗留 32 闭环**（三态复验 + 截图 `stage9-01`）；`smoke` 143 → **145** |
| 3 | **正式审计 B · 运行时层**：严格按 README 启动 / 前端六模块 / 后端六项 / 响应式 375·768·1280 / 深色对比度 / 前后端双层校验实测 / 数据稳定性（含 `SQLITE_BUSY`） | ✅ 完成（2026-09-26）：12 项清单全部填满 —— **第 1 / 2 / 3 / 4 / 5 / 6 / 8 项通过**；并发演练 锁内写 **5.085s → 50001**、锁内读 **4.8ms → 200**；新增截图 `stage9-02…07`（共 6 张） |
| 4 | **演示脚本与素材**：`docs/demo/README.md`（覆盖主指令 §七·6 的 12 个演示点）+ 按脚本补齐 `stage9-*` 截图；录屏由作者按脚本自行完成 | ✅ 完成（2026-09-26）：脚本含"操作 + 命令 + 预期 + 截图编号"+ 39 张素材索引；新增 `stage9-08-home` / `stage9-09-swagger`；删除失效的 `docs/demo/.gitkeep` |
| 5 | **交付文档整理**：关键提示词 **#6（项目审计）** + `debug-log` 报错记录整理 + `audit-report` 定稿 + `ai-log` 流水 + README / AGENTS 索引同步 + 本阶段指令原文追加进 `main-prompt.md` | ✅ 完成（2026-09-26）：#6 已补写并与 §一 汇总表同步；`debug-log` 新增 **10 条索引表**；`audit-report` 头部与 12 项清单定稿；`main-prompt.md` 追加「四、阶段 9 的交付指令（原文照录）」；README / AGENTS 文档索引刷新 |
| 6 | **收尾与验收清单**：终测 `build` / `smoke` + 数据终核（含 `view_count` 复位为种子状态）+ 交付物总表 + 13 条验收清单（**不代签**） | ✅ 完成（2026-09-26）：`build` **300ms**、`smoke` **145/145**、`view_count` 复位（`reset rows = 3`、非零行 **0**）、数据 `12 / 8 / 23 / 0 / 0`；**13 条验收清单已交付，待作者确认** |

**作者批复补充约定（2026-09-26）**

1. **重启持久化演练不做**（沿用阶段 2 / 4 / 8 的历史实测证据）；
2. 功能达标自检发现的 3 处差异处置：**评论错误提示动画**与 **Toast `warning` 类型**纳入批 2 修复；**虚拟列表**保持"主动不做"（理由见 `README.md`）、**GitHub 托管**受本机网络限制只登记（`docs/debug-log.md` 报错记录 1）；
3. 决策 **BY–CG 均同意**（见 §二）。

---

## 四、逐批结果

### 阶段 9（批 0–批 2 完成，**进行中** —— 批 3 待开工）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 + 功能达标自检 | 只读核对：git 工作区干净 · HEAD `486ebea` · 分支 `main`；8080（PID 28052）/ 5173（PID 5124）均在监听；Node 24.21.0 / npm 11.19.0 / JDK 26.0.2.1。基线（实测）：`npm run build` **274ms**、`npm run smoke` **143/143**、数据库 `12 / 8 / 23 / 0 / 0`、**6 表 8 索引**、`journal_mode=wal`、`ARTICLE_STATUS PUBLISHED = 12`。功能自检：前端 6 模块 + 加分项、后端 6 项 + 7 项加分项逐条达标；3 处差异见 §三 补充约定 |
| 1 ✅ | 正式审计 A · 静态层（**只读**） | 前端 48 / 后端 41 个源文件逐文件核对：**第 7 / 11 / 12 项通过**（敏感信息 0 命中、依赖全有使用点、监听器与观察器清理齐全）；发现 **2 处重复**（滚动+rAF 范式 ×3、`blankToNull` ×2）、**2 处死代码残留**（`config/.gitkeep`、`ApiError#isValidationError`）、**3 处校验口径差异**（邮箱正则 / 邮箱长度 / 文章表单长度）；报告写入 `docs/audit-report.md`（新增整章 + 12 项清单回填） |
| 2 ✅ | 审计问题修复（9 项） | ① 抽 `utils/scrollFrame.js` 消除三处重复；② 新增 `common/Texts.java`；③ 删 `config/.gitkeep`；④ 删 `isValidationError`；⑤⑥ 邮箱口径前后端统一为同一条正则 + ≤100 字、文章表单补齐 `LIMITS`；⑦ 评论错误提示加淡入动画；⑧ Toast 补 `warning` 类型（写作台「转草稿」真实使用）；⑨ **遗留 32 闭环**：`defineAsyncComponent` 补 loading / error 两态、去掉会吞异常的 `<Suspense>`，三态浏览器复验 + 截图 `docs/demo/stage9-01-markdown-load-error.png`。`smoke` **143 → 145/145**；构建 **371ms**（共享 chunk 70.44 → 65.48 kB） |
| 3 ✅ | 正式审计 B · 运行时层 | **README 从零启动**：后端 `Started in 1.923s`、前端 `VITE ready in 342ms`；前端六模块浏览器走查（无限滚动累积 12 篇、空态「筛选出 0 篇文章」、评论「校验→发表→归属删除」、点赞「刷新后仍保持」、`/about` 6 个 `blog:` 键）；后端六项（列表 11 字段与契约一致、`keyword=SQLite → 3`、`tags OR → 5`、详情 `prev/next`）；响应式 375 / 768 / 1280；对比度 12 组全部达标；并发演练 锁内写 **5.085s → 50001** / 锁内读 **4.8ms → 200**；截图 `stage9-02…07`（6 张） |
| 4 ✅ | 演示脚本与素材 | 新增 `docs/demo/README.md`（12 个演示点：操作 + 命令 + 预期 + 截图编号；含 39 张素材索引与注意事项）；补齐 `stage9-08-home` / `stage9-09-swagger`；删除失效的 `docs/demo/.gitkeep`；**录屏由作者按下达脚本自行完成** |
| 5 ✅ | 交付文档整理 | **关键提示词 #6（项目审计）** 补写并与 `collaboration-log.md` §一 汇总表同步（编号 6 从"待发生"转为真实记录）；`debug-log.md` 新增 **10 条索引表** + 核对计数（10 条真实报错）；`audit-report.md` 头部与 12 项清单定稿；`main-prompt.md` 追加「**四、阶段 9 的交付指令（原文照录）**」（今昔对照：开工指令 / 功能自检清单 / 批复 / 逐批确认）；README 与 AGENTS 文档索引刷新 |
| 6 ✅ | 收尾与验收清单 | 终测 `npm run build` → **300ms**、`npm run smoke` → **145/145**；`view_count` 复位（`reset rows = 3`、非零行 **0**），`DbCheck` 终核 `12 / 8 / 23 / 0 / 0` + 6 表 8 索引 + `journal_mode=wal`；**13 条验收清单**写入 §一（含可复现的遗留 32 演练方式与并发命令）；**不代签 —— 验收结论待作者给出** |

### 阶段 5（批 0–批 6，作者已确认并整体验收通过）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0–5 | 开工基线 → 存储层 → 模块四 → 模块五 A/B → 模块六 | 25 项接口用例 + 31 项浏览器实测；面板一致 + 重置 Toast + 自查缺陷修复 |
| 6 ✅ | 收尾 + **作者人工验收** | 构建终测 270ms；14 条清单**逐条通过** |

### 阶段 6（批 0–批 5，全部完成 × **作者验收通过**）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0–4 | 开工基线 → 契约复核 + `smoke` 97 项 → 全链路回归 → 异常演练 → 非功能复核 + 全局错误兜底 | 六模块全绿；XSS 零执行；停服降级三态；对比度量化；15 张截图 |
| 5 ✅ | 收尾 | 构建 **207ms**、`smoke` **97/97**；13 条验收清单 |
| 验收 ✅ | **作者人工验收** | **13 条清单逐条确认通过（2026-09-25），无返工项** |

### 阶段 7（批 0–批 8，**全部完成 × 作者验收通过**）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0–6 | 开工基线 → 阅读进度条 + 回到顶部 → 详情页拆包 → 无限滚动 → 窄屏目录 → 遗留 29·30 修正 → 收尾 | 详情 shell 298.37 → 17.62 kB；构建 255ms；`smoke` 97/97；13 条验收清单 |
| 7–8 | 验收后修订：评论昵称收起 → 昵称 / 邮箱首次弹窗 | 浏览器 7 + 6 项实测；构建 240 / 311ms |
| 验收 ✅ | **作者人工验收** | **13 条清单逐条确认通过（2026-09-26）**；两条修订后二次确认"ok，验收通过" |

### 阶段 8（批 0–批 8，**全部完成 × 作者验收通过**）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 | 构建 **153 模块 / 229ms**；`smoke` **97/97**；数据 `12 / 8 / 23 / 0 / 0` |
| 1 ✅ | 契约 v1.1 + 数据模型同步（**纯文档**） | 第 10 / 11 / 16 / 17 条转正 + **新增第 18 条**；**表结构零变更**；**已由作者复核通过** |
| 2 ✅ | 后端：评论单条查询 / 修改 | `GET` / `PUT /api/comments/{id}`；`smoke` **112/112**（+15）；手动 9 步 |
| 3 ✅ | 后端：相邻文章 + 详情 `prev` / `next` | `GET /api/articles/{id}/adjacent`（行值比较 `(created_at, id)`）；`smoke` **123/123**（+11） |
| 4 ✅ | 后端：标签管理 + 启用 `40009` | `POST` / `PUT` / `DELETE /api/tags`；**闭环遗留 28**；`smoke` **137/137**（+14） |
| 5 ✅ | 后端：阅读数 + WAL + 未覆盖项演练 | `POST /views` + `viewCount`；**`journal_mode=wal`**；**`40009` / `50000` / `50001` / `SQLITE_BUSY` 真实触发留档**；`smoke` **143/143** |
| 6 ✅ | 前端最小接入：上下篇 / 阅读数 / 评论行内编辑 | 浏览器 6 项实测（含跳转、编辑接口复核、375px）；构建 242ms；截图 `stage8-01…03` |
| 7 ✅ | 写作台 `/studio`（隐藏入口，决策 BR） | `StudioView` **11.91 kB** 独立 chunk；浏览器 8 步实测（文章 CRUD + 草稿切换 + 标签三操作）；截图 `stage8-04` |
| 8 ✅ | 收尾与回归 | 终测构建 **258ms** / `smoke` **143/143**；**遗留 9 / 19 / 28 收口、未覆盖项清零**；文档覆盖 + **13 条验收清单**；截图 `stage8-05`（暗色） |
| 验收 ✅ | **作者人工验收** | **13 条清单逐条确认通过（2026-09-26），无返工项**；随后按要求完成本文件本轮的整份覆盖与协作日志补写 |

---

## 五、遗留问题（阶段 8 收口后，共 33 条）

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1–7 | ~~已解决的历史项~~ | — | 后端首次启动、JDK 26 兼容性、依赖下载慢、契约与模型草案、路径含空格、`frontend/public/` 缺失、种子仅 3 篇 |
| 8 | 后端未做鉴权（演示项目；**写作台同理**） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、`README.md`、Swagger 描述、关于页与写作台页内标注 |
| 9 | ~~WAL 未启用~~ | — | **已闭环（阶段 8 批 5）**：JDBC URL 加 `journal_mode=WAL`，`PRAGMA journal_mode` 实测 `wal`，`-wal` / `-shm` 伴生文件出现 |
| 10 | 后台任务的派生进程可能残留、也可能稍后自行退出 | 端口占用 / 需重启 dev server | 已流程化：`netstat` 取 PID + `taskkill`（阶段 8 又复现：包装超时后派生 JVM 仍正常服务） |
| 11–15 | ~~已解决的历史项~~ | — | native-access 警告、外键约束验证、过渡观感与触摸点按（作者已验收）、tag ID 冲突 |
| 16 | 前端自动化回归 | 浏览器层仍需人工 | 接口层已闭环（`npm run smoke`，143 项）；浏览器层由各阶段人工验收清单覆盖 |
| 17 | ~~`?page` 越界不回退~~ | — | 阶段 6 复验、阶段 7 批 3 语义升级后仍保持（`?page=99` 33ms 收敛） |
| 18 | 骨架屏**出现时机**未抓到真机画面 | 首屏 / 追加骨架只能证明代码与外观正确 | 本机请求约 10ms；留到演示时用真机或浏览器网络节流观察（可选收尾项，见 §三） |
| 19 | ~~详情页没有上一篇 / 下一篇~~ | — | **已闭环（阶段 8 批 3 后端 + 批 6 前端）**：`/adjacent` + 详情带出 `prev` / `next`，导航可点击跳转（截图 `stage8-01` / `stage8-03`） |
| 20 | 详情页 chunk 拆包 | 首屏不再被管线阻塞；**完整浏览的总字节数不变** | **部分闭环**：shell **17.62 → 22.92 kB**（阶段 8 新增上下篇 / 行内编辑 / 阅读数）、管线 281.34 kB；要压小需 BH②（**作者已拍板暂不做**） |
| 21 | ~~正文图片懒加载无真实内容可验证~~ | — | 阶段 6 批 2 肉眼复验通过 |
| 22 | ~~目录只在桌面显示~~ | — | 已闭环（阶段 7 批 4）：窄屏「本页目录」折叠面板 |
| 23 | ~~未实现「阅读进度条 / 回到顶部」~~ | — | 已闭环（阶段 7 批 1） |
| 24 | ~~Toast 视觉截图未留档~~ | — | 阶段 6 已留档（成功 + 异常各一张） |
| 25 | 重置本地数据后旧 `visitorId` 的点赞无法用接口删除 | 该赞仍计入总数 | 演示级语义（关于页已说明）；清理只能直接操作数据库 |
| 26 | 内置浏览器合成点击偶发不送达 | 验证需改用键盘 | 已流程化"优先键盘路径"（工具限制，非项目缺陷） |
| 27 | ~~`CommentSection` / `LikeButton` 随详情页 chunk 加载~~ | — | 已闭环（阶段 7 批 2） |
| 28 | ~~`40009` 不可达~~ | — | **已闭环（阶段 8 批 4）**：标签管理上线后重名冲突稳定触发 409 / `40009`（smoke 断言 + 手动实测） |
| 29 | ~~「评论已发表」提示在删除后仍显示~~ | — | 已闭环（阶段 7 批 5，决策 BM） |
| 30 | ~~亮色主色对比度 4.44 低于 WCAG AA~~ | — | 已闭环（阶段 7 批 5，决策 BL）：`#3563e0`（对白底 **5.23**） |
| 31 | **触摸模拟下合成点击打不开 `<details>`** | 验证窄屏目录面板时点不动 | 工具限制，非项目缺陷；**规避**：`click_if_interactive` 取 ref 后走键盘 `Enter` |
| 32 | ~~详情页"异步管线加载失败"的表现未演练~~ | — | **已闭环（阶段 9 批 2）**：`defineAsyncComponent` 补 `loadingComponent` / `errorComponent` 两态（**去掉会吞异常的 `<Suspense>`**），三态浏览器复验通过 + 截图 `docs/demo/stage9-01-markdown-load-error.png`；演练方法与根因见 `docs/debug-log.md` 报错记录 10 |
| 33 | **本机记住了评论邮箱**（`blog:commentEmail`） | 共享电脑上前次使用者的邮箱会自动带上 | 演示级取舍：`/about` 可查看与一键清除；若要更保守，可在弹窗里加"不记住邮箱"开关 |

> **未覆盖项（如实登记，非缺陷）**：浏览器控制台 warn 与网络层 4xx 未直接检查（本机 Edge 无头不可用）；`prefers-reduced-motion` 仅静态证据。**阶段 8 批 5 已闭环**：`40009` / `50000` / `50001` / `SQLITE_BUSY` 均拿到真实触发记录（临时探针 + 外部写者持锁演练，探针验证后已删除）。
>
> **阶段 8 新增观察项（工具限制，非项目缺陷）**：Vite HMR 中间态（新标签首次打开主区为空 → `reload` 即正常）；`page.visual.snapshot` 偶发 `PAGE_NOT_READY`（重试即成功）。详见 `docs/debug-log.md` 观察项表。

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK / Maven | `java 26.0.2.1`（已验证可跑 Spring Boot 4.1.1）；Maven 未安装，用 `mvnw` |
| Node / npm | `node v24.21.0`、`npm 11.19.0` |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9（**阶段 6 / 7 / 8 均未新增依赖**） |
| 后端依赖 | Spring Boot 4.1.1、Spring 7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc 3.1.1、Jackson 3、HikariCP |
| 数据库 | `backend/data/blog.db`；**6 张表（5 张业务表 + `sqlite_sequence`）＋ 8 个索引（5 个显式 `idx_*` + 3 个自动索引）**；**阶段 9 批 6 终核** `article=12`、`tag=8`、`article_tag=23`、`comment=0`、`like_record=0`、**`view_count` 非零行 = 0**（`ResetViewCount` 复位 `reset rows = 3`）；**`journal_mode=wal`（阶段 8 批 5 起，伴随 `-wal` / `-shm` 文件）**；`busy_timeout=5000`（应用连接由 JDBC URL 设定） |
| 前端静态资源 | `frontend/public/favicon.svg` + `images/covers/*.svg`（12 张）+ `images/articles/markdown-pipeline.svg` |
| 演示素材 | `docs/demo/`：**39 张**（阶段 6 **15** + 阶段 7 **10** + 阶段 8 **5** + 阶段 9 **9**：`01` 管线失败态 / `02` 列表页 / `03` 空态筛选 / `04` 暗色详情 / `05` 375 窄屏 / `06` 1280 桌面+侧栏目录 / `07` 768 折叠目录 / `08` 首页 / `09` Swagger UI）；**并含演示脚本 `docs/demo/README.md`**（阶段 9 批 4，覆盖 12 个演示点） |
| 服务状态 | **前后端均在运行**（后端 8080 PID 39716 / 前端 5173 PID 38320）；**阶段 9 批 6 复核**：按 README 从零启动成功、健康检查 `UP`、`smoke` 145/145；如需停服见第八节贴士 ③ |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达 |
| Git 仓库 | 分支 `main`；**阶段 7 提交链**：`85e5326` → … → `1904279` → `9c9d2ba`（阶段收尾）；**阶段 8 提交链**：`ada2788`（批 0）→ `5531129`（批 1）→ `19f0f07`（批 2）→ `cbb3ed9`（批 3）→ `fdf84a1`（批 4）→ `de376db`（批 5）→ `a56e0f2`（批 6）→ `8dbc5b5`（批 7）→ `2e53303`（批 8 收尾）→ `486ebea`（阶段记录定稿）；**阶段 9 提交链**：`97b3ee2`（批 0）→ `d611e9e`（批 1）→ `cc2699d`（批 2）→ `79a3e79`（批 3）→ `2dcd930`（批 4）→ `e365eda`（批 5）→ 批 6 见 `git log` 最新一条 |
| 前端构建基线 | **阶段 9 批 6 终测**：`npm run build` → **300ms**；`index-*.js 57.15 kB / gzip 22.95 kB`、详情 shell `ArticleDetailView 23.12 kB`、`MarkdownRenderer 281.34 kB / gzip 104.55 kB`、共享 chunk **65.48 kB / gzip 25.68 kB**（批 2 为 371ms、批 0 基线 274ms） |
| 接口回归基线 | **阶段 9 批 6 终测**：`npm run smoke` → `全部通过：145/145 项断言`（跑完数据回种子状态）；Swagger 操作数实测 **20** |

---

## 七、后端接口清单（契约 v1.1 · 阶段 8 收口）

**契约 §四 · 1–18 全部实现（20 个操作）**：

- 健康检查：`GET /api/health`
- 文章：`GET /api/articles`（分页 / `keyword` / `tags`+`tagMode` / `status`）、`GET` / `POST` / `PUT` / `DELETE /api/articles/{id}`、**`GET /api/articles/{id}/adjacent`**（阶段 8 批 3）、**`POST /api/articles/{id}/views`**（阶段 8 批 5）
- 标签：`GET /api/tags`、**`POST` / `PUT` / `DELETE /api/tags[/{id}]`**（阶段 8 批 4，重名 `40009`）
- 评论：`GET` / `POST /api/articles/{id}/comments`、`DELETE /api/comments/{id}`、**`GET` / `PUT /api/comments/{id}`**（阶段 8 批 2）
- 点赞：`GET` / `POST` / `DELETE /api/articles/{id}/likes`（两端幂等）

**统一约定**：响应 `{code, message, data}`；错误码 `0` / `40001` / `40002` / `40004` / `40009` / `50000` / `50001`（**全部可触发，`40009` / `50000` / `50001` 有真实记录**）；时间格式 `yyyy-MM-dd'T'HH:mm:ss`；分页 `page` 从 1 起、`size` 1–20（默认 10）。Swagger：5 个分组、20 条接口摘要。

**复核结论**见 `docs/audit-report.md`（「阶段 6 预审计」+「阶段 7 变更摘要」+「阶段 8 变更摘要」）；可重复验证用 `npm run smoke`（**143 项断言**）。

---

## 八、如何启动与验证（标准命令 · 绝对路径版）

> 项目路径含空格，`cd` 后面**必须加引号**；Git Bash 用 `/d/code/...` 写法，PowerShell 用 `D:\code\...` 写法。以下命令均已实测跑通。

```bash
# 后端（Git Bash，端口 8080）
cd "/d/code/Additional Full-stack Development of Personal blogs/backend" && ./mvnw -B -ntp spring-boot:run
```
```powershell
# 后端（Windows PowerShell，端口 8080）
cd "D:\code\Additional Full-stack Development of Personal blogs\backend"; .\mvnw.cmd -B -ntp spring-boot:run
```
```bash
# 前端（Git Bash，端口 5173；另开一个终端窗口）
cd "/d/code/Additional Full-stack Development of Personal blogs/frontend" && npm install --no-fund --no-audit && npm run dev
```
```powershell
# 前端（Windows PowerShell，端口 5173；另开一个窗口）
cd "D:\code\Additional Full-stack Development of Personal blogs\frontend"; npm install --no-fund --no-audit; npm run dev
```
```bash
# 接口层回归（需后端已启动）
cd "/d/code/Additional Full-stack Development of Personal blogs/frontend" && npm run smoke
```

访问地址：前端 http://localhost:5173 ｜ **写作台（隐藏入口）http://localhost:5173/studio** ｜ 后端 http://localhost:8080 ｜ Swagger UI http://localhost:8080/swagger-ui/index.html ｜ API 前缀 `/api`。

> 贴士：① 先起后端（等控制台出现 `Started BlogApplication`）再起前端，否则首屏请求会经代理拿到 502；② 后端只在运行期间可访问 `/api/*` 与 Swagger；③ 停服后仍有进程占端口：`netstat -ano | grep ":8080 " | grep -i listening` 取 PID 后 `taskkill //PID <pid> //F`（前端同理，端口换 5173）；④ 数据库重置＝停服 → 删 `backend/data/blog.db`（含 `-wal` / `-shm`）→ 重启（丢数据，不可恢复）；⑤ 本地数据重置＝`/about` 页面内「重置本地数据」按钮；⑥ Git Bash 的 `curl` 传中文会按 GBK 编码，请用 `node -e` 的 `fetch` 或 Swagger UI。
>
> **阶段 8 自验入口**：`/articles/2`（页底**上下篇卡片**可点击跳转；meta 有「阅读 N」，刷新会 +1）、`/articles/2` 评论区（发表 → 点「编辑」→ 行内修改 → 保存/取消）、`/studio`（隐藏入口：新建/编辑/删除文章、草稿 ↔ 发布、标签新建/改名/删除）、`curl -s -X POST http://localhost:8080/api/articles/1/views`（阅读数 +1）。
>
> **回归自验入口**（阶段 6 / 7 沿用）：`/articles?keyword=SQLite&tags=前端&tagMode=or`（组合筛选深链）、`/articles/abc`（非法 id 错误态）、`/articles/99999`（详情 404）、`/articles/6`（正文配图 + 评论区）、`/about`（本地数据面板 + 一键重置）、`/no-such-page`（404 页）、`/articles?page=99`（收敛为 `?page=2`）。
