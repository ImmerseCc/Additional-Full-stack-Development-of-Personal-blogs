# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 7「功能迭代一」进行中 —— 批 0（开工基线）、批 1（阅读进度条 + 回到顶部）、批 2（详情页 chunk 拆包）、批 3（列表页无限滚动 + 骨架屏）、批 4（窄屏目录折叠入口）、批 5（遗留 29 / 30 修正）均已完成**；分批方案（批 0–批 6）与决策点 BF–BO 已由作者确认（"均同意，请继续"）。阶段 0–6 全部完成并入库，其中**阶段 5 / 阶段 6 均已由作者人工验收通过**。
> **当前进度：下一步 = 批 6（收尾：删除 `Pagination.vue` + 整阶段回归 + 文档同步 + 交付人工验收清单）**；批次安排与决策点见 §三 第 1 条。
> **运行状态（批 0 结束时）**：**前后端均在运行** —— 后端 8080（`Started BlogApplication in 1.701 seconds`，JVM PID 4200）、前端 5173（`VITE v8.3.0 ready in 349 ms`）；数据库为种子状态（`article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`，批 5 的评论演练已还原）。

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 v1.0，**已确认**；阶段 5 批 1 按作者确认补注 likes 的 `visitorId` 必填，**未改字段**）。
2. **进度**：阶段 0–6 **全部完成并入库，且阶段 5 / 阶段 6 均已由作者人工验收通过**；**阶段 7（功能迭代一）进行中 —— 批 0–批 5 已完成（开工基线 / 阅读进度条 + 回到顶部 / 详情页 chunk 拆包 / 列表页无限滚动 + 骨架屏 / 窄屏目录折叠入口 / 遗留 29·30 修正），下一步批 6（收尾 + 验收清单）**。**前端六个核心模块全部完成**；后端 13 个操作全部实现并实测；阶段 6 追加了接口回归脚本（`npm run smoke`）与前端全局错误兜底；**阶段 7 的批次安排与决策点见 §三 第 1 条**。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水，阶段 6 批 0–批 5 + 验收轮已记录）、`docs/collaboration-log.md`（关键提示词汇总 + 阶段索引 + 阶段记录，**阶段 6 记录含验收结论**）、`docs/debug-log.md`（**9 条**真实记录 + **15 条**观察项）；`docs/audit-report.md` 有**三块「阶段 6 预审计」**（批 1 契约 / 批 3 异常·安全 / 批 4 非功能·可访问性），正式审计（12 项检查清单）仍在阶段 9。
4. **环境事实**：Node 24.21.0 / npm 11.19.0；JDK 26 已验证可跑 Spring Boot 4.1.1；Maven 用 `mvnw`；**当前前后端均在运行（批 0 结束时）** —— 后端 8080、前端 5173；若发现端口已释放，按第八节命令重新启动（已给出**绝对路径版**）。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题；**阶段验收结论由作者给出，AI 不代签**；**每个阶段结束后作者会新开会话继续**，所以本文件必须足以让下一个会话无缝接手。
6. **本机已踩过的坑**（细节都在 `docs/debug-log.md`）：① Git Bash 的 `curl` 传中文按 **GBK** 编码 → 用 `node -e` 的 `fetch` 或 Swagger UI；② `wc -m` 按**字节**计数；③ 后台任务有 10 分钟上限，超时只杀包装进程、派生 JVM / node 继续存活占端口，但**派生进程也可能在更晚时候自行退出**；④ "拒绝连接"第一步永远是"确认服务是否真的在监听"；⑤ **内置浏览器合成点击偶发不送达**（评论「发表」/「删除」按钮各复现过）→ **拿到 ref 后优先 `press_key` + `Enter`**；⑥ 路由过渡中间帧读不到"带过渡的子树"（先截图再读结构）；⑦ 固定 ID 的幂等种子数据遇"历史行占用同 ID"会**静默错位**；⑧ 启动后端前先查 8080；⑨ **后台标签页里 `requestAnimationFrame` / `IntersectionObserver` 被节流**；⑩ **Toast 只活 2.6s** → 把"触发动作 + `page.wait_for`/截图"放进**同一批**调用；⑪ **临时 Java 程序在 Git Bash 控制台打印中文会乱码** → 只读数值型结果；⑫ **`page.wait_for` 的 text 条件按视口扫描**（屏外文案会超时）；⑬ **本机 Edge 无头模式不产出任何输出**（`--dump-dom` / `--enable-logging` / `--log-file` 全空）→ 控制台类检查只能走应用层替代证据；⑭ **浏览器面板 / 标签页会被回收**（阶段 6 出现过两次）→ `browser.create_tab` 重开；⑮ **删除文章后标签会保留**（契约 §四·6），清理测试痕迹需用临时 JDBC 程序删孤立标签。
7. **前端代码地图**（阶段 6 仅改过 `main.js`）：
   - `src/api/`：`http.js`（统一请求 / 超时 / 解包 / 错误归一）、`error.js`、`articles.js`（参数规范化）、`tags.js`、`comments.js`、`likes.js`
   - `src/utils/`：`date.js`、`markdown.js`、`reveal.js`、`scrollSpy.js`、`debounce.js`、`storage.js`、`visitor.js`、`validate.js`
   - `src/stores/`：`theme.js`、`toast.js`、`likes.js`、`myComments.js`
   - `src/components/`（18 个）：`AppHeader` / `AppFooter` / `ThemeToggle` / `ArticleCard` / `ArticleList` / `ArticleSkeleton` / `SkeletonBlock` / `Pagination` / `MarkdownRenderer` / `TableOfContents` / `EmptyState` / `SearchInput` / `TagFilter` / `CommentSection` / `CommentItem` / `CommentForm` / `LikeButton` / `ToastStack` / `LocalDataPanel`
   - `src/main.js`：**阶段 6 批 4 新增全局错误兜底**（`app.config.errorHandler` + `window.unhandledrejection` → `console.error` + error Toast「页面出现未预期的异常，请刷新或稍后重试」；`useToastStore(pinia)` 显式传入）
   - `scripts/smoke.mjs`：**接口层回归脚本**（阶段 6 批 1 新增，`npm run smoke`，零新增依赖、97 项断言、自动清理并复核数据还原）
   - 路由：`/`、`/articles`（四参数与地址栏同步，越界自动回退）、`/articles/:id`、`/about`（本地数据管理）、404 兜底
8. **写代码时的硬经验**（阶段 4 / 5 代价换来的）：
   ① **动画不能成为内容可见性的前提**；② **取渲染结果必须等目标分支真正挂载**（`finally` + `await nextTick()`）；③ **读写非响应式外部状态要么等一拍、要么用 `flush: 'post'` 的 watcher**；④ 契约的 `CommentVO` **不回传 `visitorId`**，归属靠本地账本 + 后端校验；⑤ 点赞 / 评论计数**以后端返回为准**。

---

## 一、当前阶段与**验收记录**

**阶段 6 已全部完成并入库，且已由作者人工验收通过（2026-09-25）。**

**阶段 6 验收记录（作者逐条确认）**

| # | 验收项 | 结论 |
|---|---|---|
| 1 | 按 README 启动前后端，首页正常显示 | ✅ |
| 2 | `npm run smoke` → `全部通过：97/97 项断言` | ✅ |
| 3 | 契约逐条复核结论（`docs/audit-report.md` →「阶段 6 预审计」） | ✅ |
| 4 | 六模块正常路径（搜索 / 过滤 / 空态 / 详情 / 评论 / 点赞 / 本地数据） | ✅ |
| 5 | 异常与边界（404 页、详情 404、非法 id、未知标签空态） | ✅ |
| 6 | 后端停服降级（整页错误态 + 重试、点赞 Toast、恢复） | ✅ |
| 7 | XSS 防护（载荷按纯文本渲染、零执行） | ✅ |
| 8 | 全局错误兜底（Console 触发 `Promise.reject` → 错误 Toast） | ✅ |
| 9 | 响应式 375 / 768 / 1920 无破版 | ✅ |
| 10 | 键盘可达性与焦点环 | ✅ |
| 11 | 暗色主题可读性 | ✅ |
| 12 | `npm run build` 正常、体积符合基线 | ✅ |
| 13 | 测试数据未污染（`article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`） | ✅ |

> 验收结论原文："**ok，验收通过**"；**本阶段无返工项**。阶段 0–5 的验收记录见 `docs/collaboration-log.md` 的阶段记录（阶段 5：14 条清单逐条确认）。

---

## 二、已完成

| 时间点 | 事项 | 产物与证据 |
|---|---|---|
| 阶段 0 / 0.5 | 需求确认与技术选型 / 协作日志规范 | Vue 3 + Vite + 原生 CSS 变量；Spring Boot 4.1.1 + JdbcTemplate + SQLite；0–9 阶段路线；双日志分工 |
| 阶段 1 批 1–4 | 目录骨架与占位文件 | 根目录 3 + docs 9 + frontend 12 + backend 15 文件；`git init`；契约与模型升级 v1.0；提交 `501065a` → `25e280e` |
| 阶段 2 批 0–4 | 后端业务实现（13 个操作） | 52 项接口实测；外键级联 / 点赞幂等 / `busy_timeout` 均实测；提交 `e9ab336` → `7174838` → `af2334c` |
| 阶段 3 批 0–4 | 前端模块一（导航 / 主题 / 404 / 移动端 / 过渡） | 提交 `8c5923d` → `7de72da` → `b257499` → `b1a517f` → `378e3b0` |
| 阶段 4 批 0–5 | 前端模块二 / 三（列表 / 详情 / Markdown / 目录 / 进场动画） | 12 篇种子 + 12 张封面；22 项浏览器实测 + 15 项管线断言；提交 `ec848f3` → `16f1726` → `929c3da` |
| 阶段 5 批 0–批 6 | 前端模块四 / 五 / 六 + 收尾 + **作者验收** | 提交 `d6167ea` → … → `65efbe0` → `3852fca`；14 条清单全通过 |
| 阶段 6 批 0 | 开工基线：文档更正 + 两端启动 + 构建 / 数据库基线 | 后端 1.544s、前端 211ms；构建 151 模块 / 241ms；**更正表 / 索引计数**；提交 `bad0853` → `ce87b7b` |
| 阶段 6 批 1 | 契约逐条复核 + 接口回归脚本 | `npm run smoke` **97/97**；17 条接口 13 条一致 / 4 条阶段 8 可选项；**发现 `40009` 不可达**；提交 `2c5b301` |
| 阶段 6 批 2 | 正常路径全链路回归（六模块） | 6 张截图；**作者已通过**；提交 `cbe44cf` |
| 阶段 6 批 3 | 异常 / 边界 / 空态演练 | XSS / 注入 / 停服降级 / 404 与 URL 篡改；5 张截图；数据全部还原；提交 `84b5cbc` |
| 阶段 6 批 4 | 非功能复核 + **决策 AX 代码落地** | 全局错误兜底（探针两条路径 6ms / 5ms 命中后删除）；375 / 768 / 1920；对比度量化；键盘焦点；构建 234ms；4 张截图；提交 `9f8d775` |
| 阶段 6 批 5 | 收尾：终测 + 遗留收口 + 文档同步 + 停服 | `npm run build` **207ms**、`npm run smoke` **97/97**；数据终核；**5173 / 8080 均已释放**；提交 `b3658b3`（13 条验收清单） |
| **阶段 6 验收** | **作者人工验收通过（13 条清单逐条确认）+ 本轮文档覆盖** | 验收结论写入 `docs/collaboration-log.md`；本文件整份覆盖为验收后快照；提交见 `git log` 最新一条 |

**关键决策记录（阶段 5 / 6，作者已确认）**

| 编号 | 决策 |
|---|---|
| AH | 阶段 6 定位：全链路回归 + 异常 / 空态演练 + 契约逐条复核（替代原"前后端对接 / 替换 mock"） |
| AI–AT | 阶段 5 的 13 条决策（不新增依赖 / 筛选同步地址栏 / 卡片标签可点 / 空结果插画 / 评论加载更多 / 归属账本 / 点赞以后端为准 / Toast 规格 / 重置入口 / 修复遗留 17·21 / 契约补注） |
| AU | 阶段 6 定位（同 AH，作者拍板） |
| AV | 阶段 6 以**验证 + 文档**为主，代码类加固单列 |
| AW | `frontend/scripts/smoke.mjs` 固化接口层回归 —— **批 1 落地，97/97 通过** |
| AX | 前端全局错误兜底（`app.config.errorHandler` + `unhandledrejection` → Toast）—— **批 4 落地并通过验收** |
| AY | 复核结论写入 `docs/audit-report.md` 的「阶段 6 预审计」—— **批 1 / 3 / 4 三块已落地** |
| AZ | 详情页 chunk 拆包留阶段 7；本阶段只测量（实测 297.51 kB / gzip 111.03 kB） |
| BA | Toast 截图留档 —— **批 2 / 批 3 落地**（成功 Toast + 异常 Toast） |
| BB | **不**给启动命令追加 `--enable-native-access` |
| BC | 批 0 不重置数据库；演练数据在批 5 前还原 |
| BD | 沿用"每批停下等确认" |
| BE | **`40009` 留到阶段 8 做标签管理接口时启用**；阶段 6 不改契约 / 代码 |

---

## 三、待确认 / 待执行

1. **阶段 7「功能迭代一」进行中（批 0–批 5 已完成）**：分批方案（批 0–批 6）与决策点 **BF–BO 已由作者确认（"均同意，请继续"）**；范围 = 阅读进度条、回到顶部、无限滚动 + 骨架屏（决策 S 的既定安排）、窄屏目录折叠入口（遗留 22）、详情页 chunk 拆包（决策 AZ）、遗留 29 / 30 修正；**纯前端改动：不改契约、不动后端、不新增依赖**；
   - 批 0 ✅ 开工基线：后端 `Started BlogApplication in 1.701 seconds`；前端 `VITE v8.3.0 ready in 349 ms`；构建 151 模块 / 252ms（`index 54.53 kB`、详情 chunk 297.51 kB）；`npm run smoke` **97/97 项断言**；数据 `12 / 8 / 23 / 0 / 0`；
   - 批 1 ✅ 阅读进度条 + 回到顶部（闭环遗留 23）：新增 `ReadingProgress.vue` / `BackToTop.vue`；浏览器实测 8 项（顶部 0% → 滚动 700px 时约 58% → 底部 100%；键盘 `Enter` 平滑回顶；暗色正常；列表页无进度条）；构建 249ms（`index 55.56 kB`、详情 chunk 298.37 kB）；**实测中修正进度口径**（"正文元素"改为"页面滚动比例"，见 `docs/debug-log.md` 观察项）；
   - 批 2 ✅ 详情页 chunk 拆包（决策 BH①，遗留 20 / 27 **部分闭环**）：`MarkdownRenderer` 改 `defineAsyncComponent` + `<Suspense>` 骨架兜底；详情 shell chunk **298.37 → 17.62 kB**（gzip 6.42 kB）、管线独立为 **281.34 kB**（gzip 104.55 kB）；构建 231ms；目录 / 高亮 / 滚动高亮 / 404 态实测无回归；`smoke` **97/97**；**代价**：首次引入 Suspense 使全站共享 chunk +2.4 kB gzip（63.56 → 70.36 kB）；**总数未降**，BH②（`hljs/lib/core` + 按需注册语言）经作者拍板**暂不做**（2026-09-25，选 A）；
   - 批 3 ✅ 列表页无限滚动 + 骨架屏（决策 BG，遗留 18 **仍未闭环**）：`ArticlesView` 改累积加载（`?page` = 已加载页数、深链累积、越界收敛、滚动 `replace`）+ `IntersectionObserver` 哨兵 + 可见的「加载更多」按钮 + `ArticleList` 的 `appending` 追加骨架；浏览器实测 6 项（首屏 10 篇 → 滚动 / 键盘各加载第 2 页 → 12 篇 +「已经到底了」+ `?page=2`；`?page=99` 33ms 收敛为 `?page=2`；筛选态正确；首页无回归）；构建 316ms（`ArticlesView` 8.63 kB）；`smoke` **97/97**；
   - 批 4 ✅ 窄屏目录折叠入口（决策 BK，闭环遗留 22）：`TableOfContents` 新增 `navigate` emit 与 `showTitle`；详情页正文上方加原生 `<details>`「本页目录」（≥1024px 隐藏）；浏览器实测 5 项（375 开合与点条目自动收起、768 面板显示 / 桌面无回归）；构建 214ms（详情 chunk 18.29 kB）；`smoke` **97/97**；**实测中修正"先滚动后收起"导致的目标标题偏移**（见 `docs/debug-log.md` 观察项）；
   - 批 5 ✅ 遗留 29 / 30 修正（决策 BM / BL，**两条均闭环**）：亮色主色 `#3b6ef5 → #3563e0`（含 `--color-accent-soft` 的 `rgba(53,99,224,.14)`），对比度 **4.44 → 5.23**（WCAG AA 4.5 达标；hover 6.46、暗色 6.68）；`CommentForm` 暴露 `clearStatus()`，`CommentSection` 删除评论后调用 → **发表 → 提示出现 → 删除 → 提示消失**（`state: absent` 断言 3ms 命中）端到端实测通过；数据零残留（`12 / 8 / 23 / 0 / 0`）；构建 254ms（详情 chunk 18.38 kB）；`smoke` **97/97**；
   - 推进顺序：**批 6** 收尾 + 验收清单（删 `Pagination.vue` / 整阶段回归 / 文档同步）；
   - 已拍板：`Pagination.vue` 已无引用，按作者选择**批 6 收尾时删除**（2026-09-25，选 A）；
   - 决策要点：**BG** 无限滚动＝累积加载、`?page` = 已加载页数、滚动时 `replace`；**BH** 拆包只改动态 import（不改高亮行为）；**BI** 回到顶部全站挂载；**BJ** 进度条为页头底部细条；**BK** 窄屏目录为折叠面板；**BL** 主色取 `#3563e0`（13 张 SVG 插画不改）；**BM** 用 `defineExpose({ clearStatus })` 清"评论已发表"；**BN** 不重置数据库；**BO** 每批停下等确认；
2. **遗留 29 已定方案（决策 BM，待批 5 落地）**：「评论已发表」提示在评论被删除后仍显示 —— 由 `CommentForm.vue` 暴露 `clearStatus()`，父级在删除成功后调用；
3. **可选未做项**（契约标为阶段 8 可选项）：SQLite WAL 模式、`view_count` 计数、`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`（上下篇）、标签管理接口（**落地时一并启用 `40009`**）；
4. **阶段 9 待办**：报错记录整理 + 前后端专项审计（`docs/audit-report.md` 的 12 项检查清单仍待逐项填结论）+ 交付文档与演示脚本。

---

## 四、逐批结果

### 阶段 5（批 0–批 6，作者已确认并整体验收通过）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 + 文档更正 + 正文配图 | 构建 123 模块 / 216ms；幂等复核；管线断言 9 项全绿 |
| 1 ✅ | 存储层 + 评论 / 点赞接入层 | **25 项真实用例 25/25** |
| 2 ✅ | 模块四 搜索与分类过滤 | 8 项浏览器实测 |
| 3 ✅ | 模块五 A 评论区 | 8 项浏览器实测 |
| 4 ✅ | 模块五 B 点赞 + Toast | 7 项浏览器实测 |
| 5 ✅ | 模块六 本地数据 + 一键重置 | 面板一致 + 重置 Toast + 自查缺陷修复 |
| 6 ✅ | 收尾 + **作者人工验收** | 构建终测 270ms；14 条清单**逐条通过** |

### 阶段 6（批 0–批 5，全部完成 × **作者验收通过**）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 | 后端 1.544s / 前端 211ms；构建 **151 模块 / 241ms**；更正表 / 索引计数 |
| 1 ✅ | 契约逐条复核 + 接口回归脚本 | `npm run smoke` **97/97**；17 条接口 13 条一致 / 4 条阶段 8 可选项；`40009` 不可达 |
| 2 ✅ | 正常路径全链路回归（六模块） | 六模块全绿 + **6 张截图**；作者已通过 |
| 3 ✅ | 异常 / 边界 / 空态演练 | XSS 零执行；注入 + 通配符安全；停服降级三态 + 4 秒恢复；5 张截图；数据还原 |
| 4 ✅ | 非功能复核 + 全局错误兜底 | 375 / 768 / 1920；对比度量化；键盘焦点；构建 234ms / `index` 54.53 kB；兜底 6ms / 5ms 命中；4 张截图 |
| 5 ✅ | 收尾 | 构建 **207ms**、`smoke` **97/97**；数据终核；文档同步；**停服并释放端口**；13 条验收清单 |
| 验收 ✅ | **作者人工验收** | **13 条清单逐条确认通过（2026-09-25），无返工项** |

### 阶段 7（批 0–批 6，**进行中**：批 0 ✅）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 | 后端 `Started BlogApplication in 1.701 seconds`；前端 `VITE v8.3.0 ready in 349 ms`；构建 **151 模块 / 252ms**（`index 54.53 kB` / 详情 chunk 297.51 kB，与阶段 6 收尾一致）；`npm run smoke` **97/97**；数据只读复核 `12 / 8 / 23 / 0 / 0`；文档更正 |
| 1 ✅ | 详情页 A：阅读进度条 + 回到顶部 | 新增 `ReadingProgress.vue` / `BackToTop.vue`（Teleport 到 body + rAF 范式）；**浏览器 8 项实测**（顶部 0% → 700px 时约 58% → 底部 100%；页头下沿 2px 细条；键盘 `Enter` 平滑回顶；暗色正常；列表页无进度条）；构建 **249ms** / `index` 55.56 kB；**实测中修正进度口径**（闭环遗留 23） |
| 2 ✅ | 详情页 B：chunk 拆包 | `MarkdownRenderer` 改 `defineAsyncComponent` + `<Suspense>` 骨架兜底（决策 BH①）；详情 shell chunk **298.37 → 17.62 kB**（gzip 6.42 kB）、管线独立为 **281.34 kB**（gzip 104.55 kB）；构建 231ms；目录 / 代码高亮 / 滚动高亮 / 404 态实测无回归；**代价**：首次引入 Suspense 使共享 chunk +2.4 kB gzip；**遗留 20 / 27 部分闭环 —— 总字节未降，BH② 待作者决策** |
| 3 ✅ | 列表页：无限滚动 + 骨架屏 | `ArticlesView` 累积加载（`?page` = 已加载页数、深链累积、越界收敛、滚动 `replace`）+ `IntersectionObserver` 哨兵 + 可见「加载更多」按钮 + `ArticleList` 的 `appending` 追加骨架；**浏览器 6 项实测**（10 篇 → 滚动与键盘各加载第 2 页 → 12 篇 +「已经到底了」+ `?page=2`；`?page=99` 33ms 收敛；筛选态正确；首页无回归）；构建 316ms / `ArticlesView` 8.63 kB；**遗留 18 仍未闭环**（追加骨架未抓到真机画面） |
| 4 ✅ | 窄屏目录折叠入口 | `TableOfContents` 加 `navigate` emit + `showTitle`；详情页正文上方加原生 `<details>`「本页目录」（≥1024px 隐藏，桌面仍用右侧固定目录）；**浏览器 5 项实测**（375 开合 + 点条目自动收起 / 768 面板显示 / 1280 无回归）；构建 214ms（详情 chunk 18.29 kB）；**实测中修正"先滚动后收起"导致的目标标题偏移**（闭环遗留 22） |
| 5 ✅ | 遗留 29 / 30 修正 | 亮色主色 `#3b6ef5 → #3563e0`（含 `--color-accent-soft` 的 rgba），对比度 **4.44 → 5.23**（WCAG AA 达标；hover 6.46 / 暗色 6.68）；`CommentForm` 暴露 `clearStatus()` + `CommentSection` 删除后调用 → **发表 → 提示出现 → 删除 → 提示消失**（`state: absent` 断言 3ms 命中）；数据零残留；构建 254ms（详情 chunk 18.38 kB）；`smoke` 97/97；**遗留 29 / 30 双双闭环** |
| 6 | 收尾 + 验收清单 | 待执行（验收结论由作者给出，AI 不代签） |

**阶段 7 决策记录（BF–BO，作者已确认）**

| 编号 | 决策 |
|---|---|
| BF | 阶段 7 范围 = 进度条 / 回到顶部 / 无限滚动 + 骨架屏 / 窄屏目录 / 详情页拆包 / 遗留 29·30，**纯前端改动** |
| BG | 无限滚动＝累积加载，`?page` = 已加载页数，滚动时 `replace` 更新（深链按需加载到该页，越界仍回退） |
| BH | 拆包只把 Markdown 管线改动态 `import` + 骨架兜底（不改高亮行为）；`manualChunks` 与按需注册语言作为后备 |
| BI | 回到顶部全站挂载（`App.vue`），约 1.5 屏后出现，键盘可达，reduced-motion 瞬时跳转 |
| BJ | 阅读进度条＝页头底部 2px 细条，reduced-motion 下无过渡 |
| BK | 窄屏目录＝正文上方「本页目录」折叠面板，点条目后自动收起 |
| BL | 亮色主色改 `#3563e0`（对比度 5.23）；13 张 SVG 插画内写死的同色**不改** |
| BM | 遗留 29 用 `CommentForm` 的 `defineExpose({ clearStatus })`，删除成功后由父级调用 |
| BN | 批 0 不重置数据库（沿用决策 BC） |
| BO | 沿用"每批停下等确认"，验收结论由作者签 |

---

## 五、遗留问题（阶段 6 收口后，共 30 条）

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1–7 | ~~已解决的历史项~~ | — | 后端首次启动、JDK 26 兼容性、依赖下载慢、契约与模型草案、路径含空格、`frontend/public/` 缺失、种子仅 3 篇 |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、`README.md`、Swagger 描述、关于页标注 |
| 9 | WAL 未启用（`busy_timeout=5000` 已生效） | 并发写收益有限 | 需要时在 `connection-init-sql` 追加 `PRAGMA journal_mode = WAL` |
| 10 | 后台任务的派生进程可能残留、也可能稍后自行退出 | 端口占用 / 需重启 dev server | 已流程化：`netstat` 取 PID + `taskkill` |
| 11–15 | ~~已解决的历史项~~ | — | native-access 警告、外键约束验证、过渡观感与触摸点按（作者已验收）、tag ID 冲突 |
| 16 | 前端自动化回归 | 浏览器层仍需人工 | 接口层已闭环（`npm run smoke`）；浏览器层由人工验收清单覆盖 |
| 17 | ~~`?page` 越界不回退~~ | — | **阶段 6 批 2 复验通过** |
| 18 | 骨架屏**出现时机**仍未能真机抓拍 | 首屏与追加骨架只能证明代码 / 外观正确，抓不到"出现的那一瞬间" | **阶段 7 批 3 已就位"追加骨架"（`appending` + `aria-busy`）但仍未抓到画面**：本机请求约 10ms，"滚动 + 截图放进同一批"拿到的只有"页面已变化"；留到演示时用真机或浏览器网络节流观察 |
| 19 | 详情页**没有上一篇 / 下一篇** | 少一条浏览路径 | 阶段 8 实现 `adjacent` 时一并渲染 |
| 20 | 详情页 chunk 拆包 | 首屏不再被管线阻塞；**完整浏览的总字节数不变** | **部分闭环（阶段 7 批 2）**：详情 shell 拆到 **17.62 kB**（gzip 6.42），管线独立为 `MarkdownRenderer` **281.34 kB**（gzip 104.55）；若要把管线本身也压小，需 BH② 的 `hljs/lib/core` + 按需注册语言（**待作者决策**） |
| 21 | ~~正文图片懒加载无真实内容可验证~~ | — | **阶段 6 批 2 肉眼复验通过** |
| 22 | ~~目录只在桌面显示，窄屏没有可展开入口~~ | — | **已闭环（阶段 7 批 4）**：窄屏（<1024px）正文上方提供「本页目录」折叠面板（原生 `<details>`），点条目后自动收起；桌面继续用右侧固定目录 |
| 23 | ~~未实现「阅读进度条 / 回到顶部」~~ | — | **已闭环（阶段 7 批 1）**：页头 2px 进度条 + 全站回到顶部按钮，浏览器 8 项实测通过 |
| 24 | ~~Toast 视觉截图未留档~~ | — | **阶段 6 已留档**（成功 + 异常各一张） |
| 25 | 重置本地数据后旧 `visitorId` 的点赞无法用接口删除 | 该赞仍计入总数 | 演示级语义（关于页已说明）；清理只能直接操作数据库 |
| 26 | 内置浏览器合成点击偶发不送达 | 验证需改用键盘 | 已流程化"优先键盘路径"（工具限制，非项目缺陷） |
| 27 | ~~`CommentSection` / `LikeButton` 随详情页 chunk 加载~~ | — | **已闭环（阶段 7 批 2）**：两者留在 17.62 kB 的详情 shell chunk 里，不再与管线同包 |
| 28 | ~~`40009` 不可达~~ | — | **已拍板（决策 BE）**：阶段 8 启用 |
| 29 | ~~「评论已发表」提示在评论被删除后仍显示~~ | — | **已闭环（阶段 7 批 5，决策 BM）**：`CommentForm` 暴露 `clearStatus()`，`CommentSection` 删除成功后调用；端到端实测「发表 → 提示出现（59ms）→ 删除 → 提示消失（absent 断言 3ms）」 |
| 30 | ~~亮色主色 `#3b6ef5` 对白底对比度 4.44，低于 WCAG AA 4.5~~ | — | **已闭环（阶段 7 批 5，决策 BL）**：改为 **`#3563e0`**（对白底 **5.23**，AA 达标），`--color-accent-soft` 同步改为 `rgba(53, 99, 224, .14)`；暗色主色与 13 张 SVG 插画按决策不变 |

> **未覆盖项（如实登记，非缺陷）**：`50000` / `50001` 未构造触发条件、未实测；浏览器控制台 warn 与网络层 4xx 未直接检查（本机 Edge 无头不可用）；`prefers-reduced-motion` 仅静态证据；并发与 `SQLITE_BUSY` 留阶段 9。

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK / Maven | `java 26.0.2.1`（已验证可跑 Spring Boot 4.1.1）；Maven 未安装，用 `mvnw` |
| Node / npm | `node v24.21.0`、`npm 11.19.0` |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9（阶段 6 **未新增依赖**） |
| 后端依赖 | Spring Boot 4.1.1、Spring 7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc 3.1.1、Jackson 3、HikariCP |
| 数据库 | `backend/data/blog.db`；**6 张表（5 张业务表 + `sqlite_sequence`）＋ 8 个索引（5 个显式 `idx_*` + 3 个自动索引）**；**收尾终核** `article=12`、`tag=8`、`article_tag=23`、`comment=0`、`like_record=0`；`journal_mode=delete`；`busy_timeout=5000` |
| 前端静态资源 | `frontend/public/favicon.svg` + `images/covers/*.svg`（12 张）+ `images/articles/markdown-pipeline.svg` |
| 演示素材 | `docs/demo/`：阶段 6 归档 **15 张**真实截图（01 空结果 / 02 代码高亮 / 03 正文配图 / 04 暗色 / 05 评论区 / 06 关于页 + 成功 Toast / 07 404 / 08 列表错误态 / 09 详情错误态 / 10 停服点赞 Toast / 11 XSS 纯文本 / 12 响应式 375 / 13 响应式 768 / 14 焦点环 / 15 全局错误 Toast） |
| 服务状态 | **前后端均已停止**（阶段 6 收尾 `taskkill`），**5173 / 8080 均已释放**（`curl` 返回 000）；开新阶段前按第八节命令启动 |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达 |
| Git 仓库 | 分支 `main`；阶段 6 提交链：`bad0853` → `ce87b7b` → `2c5b301` → `cbe44cf` → `84b5cbc` → `9f8d775` → `b3658b3`（13 条验收清单）；**验收记录提交见 `git log` 最新一条** |
| 前端构建基线 | **阶段 6 收尾实测**：`npm run build` → **207ms**；`index-*.js 54.53 kB / gzip 22.08 kB`、`_plugin-vue_export-helper 63.56 kB`、详情 chunk `297.51 kB / gzip 111.03 kB`、`AboutView 4.90 kB`、`ArticlesView 8.80 kB`、`index css 10.18 kB`、`ArticleDetailView css 11.85 kB` |
| 接口回归基线 | **阶段 6 收尾实测**：`npm run smoke` → `全部通过：97/97 项断言` |

---

## 七、后端接口清单（阶段 2 产物 · 契约 v1.0）

**已实现并实测通过（13 个操作）**：`GET /api/health`；`GET /api/articles`（分页 / `keyword` / `tags`+`tagMode` / `status`）；`GET /api/articles/{id}`；`POST /api/articles`；`PUT /api/articles/{id}`；`DELETE /api/articles/{id}`；`GET /api/tags`；`GET` / `POST /api/articles/{id}/comments`；`DELETE /api/comments/{id}`；`GET` / `POST` / `DELETE /api/articles/{id}/likes`。

**未实现（契约标为阶段 8 可选项）**：`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理 `POST` / `PUT` / `DELETE /api/tags`（**落地时一并启用 `40009`**，决策 BE）、`view_count` 计数。

**统一约定**：响应 `{code, message, data}`；错误码 `0` / `40001` / `40002` / `40004` / `40009`（**当前不可达**）/ `50000`（未构造触发）/ `50001`（未构造触发）；时间格式 `yyyy-MM-dd'T'HH:mm:ss`；分页 `page` 从 1 起、`size` 1–20（默认 10）。Swagger：5 个分组、13 条接口摘要。

**阶段 6 的复核结论**见 `docs/audit-report.md` →「阶段 6 预审计」（批 1 契约 / 批 3 异常·安全 / 批 4 非功能·可访问性）；可重复验证用 `npm run smoke`。

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
# 接口层回归（阶段 6 新增；需后端已启动）
cd "/d/code/Additional Full-stack Development of Personal blogs/frontend" && npm run smoke
```

访问地址：前端 http://localhost:5173 ｜ 后端 http://localhost:8080 ｜ Swagger UI http://localhost:8080/swagger-ui/index.html ｜ API 前缀 `/api`。

> 贴士：① 先起后端（等控制台出现 `Started BlogApplication`）再起前端，否则首屏请求会经代理拿到 502；② 后端只在运行期间可访问 `/api/*` 与 Swagger；③ 停服后仍有进程占端口：`netstat -ano | grep ":8080 " | grep -i listening` 取 PID 后 `taskkill //PID <pid> //F`（前端同理，端口换 5173）；④ 数据库重置＝停服 → 删 `backend/data/blog.db`（含 `-wal` / `-shm`）→ 重启（丢数据，不可恢复）；⑤ 本地数据重置＝`/about` 页面内「重置本地数据」按钮；⑥ Git Bash 的 `curl` 传中文会按 GBK 编码，请用 `node -e` 的 `fetch` 或 Swagger UI。
>
> **自验入口**：`/articles`（搜索 / 标签筛选 / 分页 / 空态）、`/articles?keyword=SQLite&tags=前端&tagMode=or`（组合筛选深链）、`/articles?page=99`（越界回退）、`/articles/abc`（非法 id 错误态）、`/articles/99999`（详情 404）、`/articles/6`（正文配图 + 评论区）、`/articles/1`（Java 代码高亮）、`/about`（本地数据面板 + 一键重置）、`/no-such-page`（404 页）。
