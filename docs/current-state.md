# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 7「功能迭代一」批 0–批 7 已全部完成，并已由作者人工验收通过（13 条清单，2026-09-26）**；验收后作者提出一条体验修订（评论昵称不再每次填写），已在**批 7** 完成并复测。阶段 0–6 全部完成并入库，其中**阶段 5 / 阶段 6 已由作者人工验收通过**。
> **当前进度：等作者对"评论昵称收起"修订做目视确认；下一阶段 = 阶段 8（功能迭代二：后端能力）—— 等开工指令**。
> **运行状态**：**前后端均在运行** —— 后端 8080（`Started BlogApplication in 1.701 seconds`，JVM PID 4200）、前端 5173；数据库为种子状态（`article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`）。

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 v1.0，**已确认**；阶段 5 批 1 按作者确认补注 likes 的 `visitorId` 必填，**未改字段**）。
2. **进度**：阶段 0–7 **全部完成并入库**，其中**阶段 5 / 阶段 6 / 阶段 7 均已由作者人工验收通过**（阶段 7 于 2026-09-26 验收，附一条体验修订，已在批 7 完成）。**前端六个核心模块全部完成**；后端 13 个操作全部实现并实测；阶段 6 追加接口回归脚本（`npm run smoke`，97 项断言）与前端全局错误兜底；阶段 7 完成前端体验迭代（进度条 / 回到顶部 / 无限滚动 + 追加骨架 / 窄屏折叠目录 / 详情页拆包 / 遗留 29·30 / 评论昵称收起）。**下一阶段：阶段 8（后端能力）**。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水，阶段 7 批 0–批 6 已记录）、`docs/collaboration-log.md`（关键提示词汇总 + 阶段索引 + 阶段记录，**阶段 7 记录已写**）、`docs/debug-log.md`（**9 条**真实记录 + **17 条**观察项）；`docs/audit-report.md` 有三块「阶段 6 预审计」+ 新增「阶段 7 变更摘要」，正式审计（12 项检查清单）仍在阶段 9。
4. **环境事实**：Node 24.21.0 / npm 11.19.0；JDK 26 已验证可跑 Spring Boot 4.1.1；Maven 用 `mvnw`；**当前前后端均在运行**（后端 8080、前端 5173）；若发现端口已释放，按第八节命令重新启动（已给出**绝对路径版**）。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题；**阶段验收结论由作者给出，AI 不代签**；**每个阶段结束后作者会新开会话继续**，所以本文件必须足以让下一个会话无缝接手。
6. **本机已踩过的坑**（细节都在 `docs/debug-log.md`）：① Git Bash 的 `curl` 传中文按 **GBK** 编码 → 用 `node -e` 的 `fetch` 或 Swagger UI；② `wc -m` 按**字节**计数；③ 后台任务有 10 分钟上限，超时只杀包装进程、派生 JVM / node 继续存活占端口，但**派生进程也可能在更晚时候自行退出**；④ "拒绝连接"第一步永远是"确认服务是否真的在监听"；⑤ **内置浏览器合成点击偶发不送达** → **拿到 ref 后优先 `press_key` + `Enter`**；⑥ 路由过渡中间帧读不到"带过渡的子树"（先截图等一拍再读结构）；⑦ 固定 ID 的幂等种子数据遇"历史行占用同 ID"会**静默错位**；⑧ 启动后端前先查 8080；⑨ **后台标签页里 `requestAnimationFrame` / `IntersectionObserver` 被节流**；⑩ **Toast 只活 2.6s** → 把"触发动作 + `page.wait_for`/截图"放进**同一批**调用；⑪ **临时 Java 程序在 Git Bash 控制台打印中文会乱码** → 只读数值型结果；⑫ **`page.wait_for` 的 text 条件按视口扫描**（屏外文案会超时）；⑬ **本机 Edge 无头模式不产出任何输出**；⑭ **浏览器面板 / 标签页会被回收** → `browser.create_tab` 重开；⑮ **删除文章后标签会保留**（契约 §四·6），清理痕迹需临时 JDBC 程序；⑯ **触摸模拟下合成点击打不开 `<details>`**（阶段 7 批 4）→ 用 ref + `Enter`；⑰ **元素快照不收录 `<summary>` / 文本框**（`role: textbox` 查不到）→ 用 `click_if_interactive` 取 ref、`visual.click` + `visual.type_text` 输入。
7. **前端代码地图**（阶段 7 新增已标注）：
   - `src/api/`：`http.js`（统一请求 / 超时 / 解包 / 错误归一）、`error.js`、`articles.js`（参数规范化）、`tags.js`、`comments.js`、`likes.js`
   - `src/utils/`：`date.js`、`markdown.js`、`reveal.js`、`scrollSpy.js`、`debounce.js`、`storage.js`、`visitor.js`、`validate.js`
   - `src/stores/`：`theme.js`、`toast.js`、`likes.js`、`myComments.js`
   - `src/components/`（**19 个**，阶段 7 新增 `BackToTop` / `ReadingProgress`，删除 `Pagination`）：`AppHeader` / `AppFooter` / `ThemeToggle` / `ArticleCard` / `ArticleList`（**`appending` 追加骨架**） / `ArticleSkeleton` / `SkeletonBlock` / `MarkdownRenderer`（**改为异步加载**） / `TableOfContents`（**`navigate` emit + `showTitle`**） / `EmptyState` / `SearchInput` / `TagFilter` / `CommentSection`（**删除后清表单提示**） / `CommentItem` / `CommentForm`（**暴露 `clearStatus()`**） / `LikeButton` / `ToastStack` / `LocalDataPanel` / `ReadingProgress` / `BackToTop`
   - `src/views/ArticlesView.vue`：**阶段 7 批 3 改为累积加载 + 哨兵 + 「加载更多」兜底**；`ArticleDetailView.vue`：**批 1 挂进度条、批 2 异步管线 + `<Suspense>`、批 4 窄屏折叠目录**
   - `src/main.js`：阶段 6 批 4 的全局错误兜底（`app.config.errorHandler` + `window.unhandledrejection` → 错误 Toast）
   - `scripts/smoke.mjs`：接口层回归脚本（`npm run smoke`，零新增依赖、97 项断言）
   - 路由：`/`、`/articles`（四参数与地址栏同步；**`?page` = 已加载页数**）、`/articles/:id`、`/about`、404 兜底
8. **写代码时的硬经验**（阶段 4 / 5 / 7 代价换来的）：
   ① **动画不能成为内容可见性的前提**；② **取渲染结果必须等目标分支真正挂载**（`finally` + `await nextTick()`）；③ **读写非响应式外部状态要么等一拍、要么用 `flush: 'post'` 的 watcher**；④ 契约的 `CommentVO` **不回传 `visitorId`**，归属靠本地账本 + 后端校验；⑤ 点赞 / 评论计数**以后端返回为准**；⑥ **异步组件（`defineAsyncComponent`）要让"依赖 DOM 的后续计算"多等一拍**（阶段 7 批 2）；⑦ **会改变布局高度的收起动作要先于滚动执行**（阶段 7 批 4）。

---

## 一、阶段 7 验收记录（13 条，**作者已逐条确认通过**）

**验收结论（作者，2026-09-26）：13 条清单全部通过**；同时提出体验修订 —— **「评论部分不能每次评论都添加昵称」**，分两步落地：**批 7** 先把"记住昵称后收起输入框"做出来（默认显示「以 XXX 的身份评论 · 改昵称」）；**批 8** 按作者进一步要求，把**昵称 / 邮箱整体收进弹窗，只在第一次评论时弹出、平时不占位**（表单只留「评论内容 + 发表评论」，点「修改昵称 / 邮箱」可随时改）。两条修订均已完成并复测。上一阶段（阶段 6）的 13 条验收记录见 `docs/collaboration-log.md` 的阶段记录。

| # | 验收项 | 自测证据（AI 侧） |
|---|---|---|
| 1 | 按 README 启动前后端，首页正常显示 | 后端 `Started BlogApplication in 1.701 seconds`；前端 `VITE v8.3.0 ready in 349 ms`；首页截图正常 |
| 2 | `npm run smoke` → `全部通过：97/97 项断言` | 批 6 终测 97/97 |
| 3 | **阅读进度条**：页头下沿 2px 细条，随滚动推进，滚到底 100%（0% 时不可见） | 实测 0% → 58%（滚 700px）→ 100% |
| 4 | **回到顶部**：滚动超约 1.5 屏后右下角出现；点击 / 键盘 `Enter` 平滑回顶（reduced-motion 下瞬时） | 键盘路径实测回顶，按钮随即消失 |
| 5 | **无限滚动**：`/articles` 首屏 10 篇 → 滚到底自动补到 12 篇 +「已经到底了 · 共 12 篇」；`?page` 随之更新；`?page=99` 收敛为 `?page=2`；「加载更多」按钮键盘可用 | 滚动 / 键盘两条路径 + 越界收敛 33ms |
| 6 | **窄屏折叠目录**：窗口 <1024px 时正文上方出现「本页目录」，点条目跳转后自动收起；桌面仍是右侧固定目录 | 375 / 768 / 1280 三档实测 |
| 7 | **详情页 chunk 拆包**：`npm run build` 后详情 shell（≈18 kB）与 `MarkdownRenderer`（≈281 kB）分包；首屏不再被管线阻塞，404 态不加载管线 | 构建产物 + `grep` 复核 |
| 8 | **亮色主色对比度**：链接 / 主按钮 / chips = `#3563e0`，对白底 **5.23**（WCAG AA 达标） | node 按 WCAG 公式实算 |
| 9 | **评论提示联动**：发表评论 → 「评论已发表」；删除该评论后提示消失 | E2E：提示出现 59ms / 消失 absent 断言 3ms |
| 10 | 暗色主题可读性（含进度条、回到顶部按钮、折叠目录） | 1920 暗色截图 `stage7-10` |
| 11 | 响应式 375 / 768 / 1920 无破版 | 截图 `stage7-06`（375）、`stage7-09`（1920） |
| 12 | 键盘可达性与焦点环（目录面板、加载更多、回到顶部） | 键盘路径逐项实测 |
| 13 | 测试数据未污染（`article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`） | 临时 JDBC 直读，批 6 / 批 7 两次终核 |

> 验收结论原文："**验收均通过**"（并附评论昵称修订要求）。**两条修订均已落地**：见 §四「阶段 7」表的 7 ✅ / 8 ✅ 两行；阶段 0–5 的验收记录见 `docs/collaboration-log.md` 的阶段记录。

---

## 二、已完成

| 时间点 | 事项 | 产物与证据 |
|---|---|---|
| 阶段 0 / 0.5 | 需求确认与技术选型 / 协作日志规范 | Vue 3 + Vite + 原生 CSS 变量；Spring Boot 4.1.1 + JdbcTemplate + SQLite；0–9 阶段路线 |
| 阶段 1 批 1–4 | 目录骨架与占位文件 | 根目录 3 + docs 9 + frontend 12 + backend 15 文件；`git init`；契约与模型 v1.0；提交 `501065a` → `25e280e` |
| 阶段 2 批 0–4 | 后端业务实现（13 个操作） | 52 项接口实测；外键级联 / 点赞幂等 / `busy_timeout` 均实测；提交 `e9ab336` → `7174838` → `af2334c` |
| 阶段 3 批 0–4 | 前端模块一（导航 / 主题 / 404 / 移动端 / 过渡） | 提交 `8c5923d` → `7de72da` → `b257499` → `b1a517f` → `378e3b0` |
| 阶段 4 批 0–5 | 前端模块二 / 三（列表 / 详情 / Markdown / 目录 / 进场动画） | 12 篇种子 + 12 张封面；22 项浏览器实测 + 15 项管线断言；提交 `ec848f3` → `16f1726` → `929c3da` |
| 阶段 5 批 0–批 6 | 前端模块四 / 五 / 六 + 收尾 + **作者验收** | 提交 `d6167ea` → … → `65efbe0` → `3852fca`；14 条清单全通过 |
| 阶段 6 批 0–批 5 | 全链路回归 + 异常 / 空态演练 + 契约逐条复核 | `smoke` 97/97；15 张截图；**作者验收通过（13 条）**；提交 `bad0853` → … → `b3658b3` → `95defd1` |
| 阶段 7 批 0 | 开工基线 | 后端 1.701s / 前端 349ms；构建 151 模块 / 252ms；`smoke` 97/97；数据 `12 / 8 / 23 / 0 / 0`；提交 `85e5326` |
| 阶段 7 批 1 | 阅读进度条 + 回到顶部（闭环 23） | 新增 `ReadingProgress.vue` / `BackToTop.vue`；浏览器 8 项实测；提交 `bb49776` |
| 阶段 7 批 2 | 详情页 chunk 拆包（决策 BH①） | 详情 shell 298.37 → **17.62 kB**；管线独立 281.34 kB；提交 `fd6ba43` |
| 阶段 7 批 3 | 列表页无限滚动 + 追加骨架（决策 BG） | 累积加载 + 哨兵 + 「加载更多」；浏览器 6 项实测；提交 `f35008d` |
| 阶段 7 批 4 | 窄屏目录折叠入口（决策 BK，闭环 22） | 原生 `<details>` 面板；浏览器 5 项实测；提交 `3f1b786` |
| 阶段 7 批 5 | 遗留 29 / 30 修正（决策 BM / BL） | 主色对比度 4.44 → 5.23；评论提示联动 E2E；提交 `cc1f0b8` |
| 阶段 7 批 6 | 收尾：删除 `Pagination.vue` + 整阶段回归 + 文档覆盖 | 构建 255ms、`smoke` 97/97；1920 亮 / 暗截图；**13 条验收清单**；提交见 `git log` 最新一条 |

**阶段 7 决策记录（BF–BO，作者已确认）**

| 编号 | 决策 |
|---|---|
| BF | 阶段 7 范围 = 进度条 / 回到顶部 / 无限滚动 + 骨架屏 / 窄屏目录 / 详情页拆包 / 遗留 29·30，**纯前端改动** |
| BG | 无限滚动＝累积加载，`?page` = 已加载页数，滚动时 `replace` 更新（深链累积加载，越界仍收敛） |
| BH | 拆包只把 Markdown 管线改动态 `import` + 骨架兜底（不改高亮行为）；**BH②（`hljs/lib/core` 按需注册语言）经作者拍板暂不做**（2026-09-25，选 A） |
| BI | 回到顶部全站挂载（`App.vue`），约 1.5 屏后出现，键盘可达，reduced-motion 瞬时跳转 |
| BJ | 阅读进度条＝页头底部 2px 细条，reduced-motion 下无过渡 |
| BK | 窄屏目录＝正文上方「本页目录」折叠面板，点条目后自动收起 |
| BL | 亮色主色改 `#3563e0`（对比度 5.23）；13 张 SVG 插画内写死的同色**不改** |
| BM | 遗留 29 用 `CommentForm` 的 `defineExpose({ clearStatus })`，删除成功后由父级调用 |
| BN | 批 0 不重置数据库（沿用决策 BC） |
| BO | 沿用"每批停下等确认"，验收结论由作者签 |
| BP | 无引用的 `Pagination.vue` **批 6 删除**（作者选 A，2026-09-25） |

---

## 三、待确认 / 待执行

1. **阶段 7 已由作者验收通过（2026-09-26，13 条清单）**；验收后提出的修订（评论昵称收起）已在**批 7** 完成并复测，**待作者目视确认**；确认后进入阶段 8。
2. **阶段 8（功能迭代二：后端能力）候选范围**（契约标为可选项，落地前需先改契约）：
   - `GET` / `PUT /api/comments/{id}`（单条查询 / 修改，`visitorId` 校验）；
   - `GET /api/articles/{id}/adjacent`（上一篇 / 下一篇）+ **详情页渲染上下篇**（遗留 19）；
   - 标签管理 `POST` / `PUT` / `DELETE /api/tags`，**落地时一并启用 `40009`**（决策 BE）；
   - `view_count` 计数；SQLite WAL（`PRAGMA journal_mode = WAL`）；
   - 可选：BH② 的 `hljs/lib/core` + 按需注册语言（把管线 chunk 压小，注意未注册语言会静默失去高亮）。
3. **阶段 9 待办**：报错记录整理 + 前后端专项审计（`docs/audit-report.md` 的 12 项检查清单仍待逐项填结论）+ 交付文档与演示脚本。

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
| 2 ✅ | 正常路径全链路回归（六模块） | 六模块全绿 + **6 张截图** |
| 3 ✅ | 异常 / 边界 / 空态演练 | XSS 零执行；注入 + 通配符安全；停服降级三态 + 4 秒恢复；5 张截图；数据还原 |
| 4 ✅ | 非功能复核 + 全局错误兜底 | 375 / 768 / 1920；对比度量化；键盘焦点；构建 234ms；兜底 6ms / 5ms 命中；4 张截图 |
| 5 ✅ | 收尾 | 构建 **207ms**、`smoke` **97/97**；数据终核；文档同步；停服并释放端口；13 条验收清单 |
| 验收 ✅ | **作者人工验收** | **13 条清单逐条确认通过（2026-09-25），无返工项** |

### 阶段 7（批 0–批 6，**全部完成，待作者验收**）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 | 后端 `Started BlogApplication in 1.701 seconds`；前端 `VITE v8.3.0 ready in 349 ms`；构建 **151 模块 / 252ms**；`smoke` **97/97**；数据 `12 / 8 / 23 / 0 / 0` |
| 1 ✅ | 阅读进度条 + 回到顶部 | 新增 `ReadingProgress.vue` / `BackToTop.vue`（Teleport + rAF 范式）；**浏览器 8 项实测**；构建 249ms；**实测中修正进度口径**（闭环 23） |
| 2 ✅ | 详情页 chunk 拆包 | 详情 shell **298.37 → 17.62 kB**（gzip 6.42）、管线独立 **281.34 kB**（gzip 104.55）；目录 / 高亮 / 404 无回归；**代价**：Suspense 运行时使共享 chunk +2.4 kB gzip；**遗留 20 / 27 部分闭环** |
| 3 ✅ | 列表页无限滚动 + 骨架屏 | 累积加载 + 哨兵 + 「加载更多」 + `appending` 追加骨架；**浏览器 6 项实测**（含 `?page=99` 33ms 收敛）；构建 316ms；**遗留 18 仍未闭环**（未抓到骨架画面） |
| 4 ✅ | 窄屏目录折叠入口 | 原生 `<details>`「本页目录」（≥1024px 隐藏）；**浏览器 5 项实测**（375 开合与自动收起 / 768 / 1280）；构建 214ms；**实测中修正"先滚动后收起"的目标偏移**（闭环 22） |
| 5 ✅ | 遗留 29 / 30 修正 | 主色 `#3563e0`（对比度 **4.44 → 5.23**）；评论提示联动 E2E（出现 59ms / 消失 3ms）；数据零残留；构建 254ms（**双双闭环**） |
| 6 ✅ | 收尾 | 删除 `Pagination.vue`（零引用）；构建 **255ms**、`smoke` **97/97**；1920 亮 / 暗截图；文档覆盖 + **13 条验收清单** |
| 7 ✅ | **验收后修订：评论昵称收起** | `CommentForm` 新增 `editingName` / `nameInput`：记过昵称即收起输入框，默认显示「以 XXX 的身份评论 · 改昵称」，点「改昵称」展开并聚焦，**提交成功后自动收起**；**浏览器 7 项实测**（含"首次访问 / 清过本地数据后显示输入框"路径）；构建 **240ms**、`smoke` **97/97**；数据零残留 |
| 8 ✅ | **验收后修订二：昵称 / 邮箱改为首次评论弹窗** | 表单只留「评论内容 + 发表评论」，昵称 / 邮箱收进**原生 `<dialog>`**（首次评论时自动弹出并聚焦昵称，确认后自动接续发表；Esc / 取消不改值）；确认即写入 `blog:commentAuthor` / **新增 `blog:commentEmail`**，身份行「以 XXX 的身份评论 · 修改昵称 / 邮箱」可随时改；`LocalDataPanel` / `AboutView` / `README` 文案同步；**浏览器 6 项实测**；构建 **311ms**、`smoke` **97/97**；数据零残留 |
| 验收 ✅ | **作者人工验收** | **13 条清单逐条确认通过（2026-09-26）**，附一条体验修订要求（评论昵称）→ 已由批 7 完成并复测 |

---

## 五、遗留问题（阶段 7 收口后，共 32 条）

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1–7 | ~~已解决的历史项~~ | — | 后端首次启动、JDK 26 兼容性、依赖下载慢、契约与模型草案、路径含空格、`frontend/public/` 缺失、种子仅 3 篇 |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、`README.md`、Swagger 描述、关于页标注 |
| 9 | WAL 未启用（`busy_timeout=5000` 已生效） | 并发写收益有限 | 阶段 8 候选（`PRAGMA journal_mode = WAL`） |
| 10 | 后台任务的派生进程可能残留、也可能稍后自行退出 | 端口占用 / 需重启 dev server | 已流程化：`netstat` 取 PID + `taskkill` |
| 11–15 | ~~已解决的历史项~~ | — | native-access 警告、外键约束验证、过渡观感与触摸点按（作者已验收）、tag ID 冲突 |
| 16 | 前端自动化回归 | 浏览器层仍需人工 | 接口层已闭环（`npm run smoke`）；浏览器层由各阶段人工验收清单覆盖 |
| 17 | ~~`?page` 越界不回退~~ | — | **阶段 6 批 2 复验通过；阶段 7 批 3 语义升级后仍保持（`?page=99` 33ms 收敛为 `?page=2`）** |
| 18 | 骨架屏**出现时机**未抓到真机画面 | 首屏 / 追加骨架只能证明代码与外观正确 | **阶段 7 批 3 就位追加骨架后仍未抓到**（本机请求约 10ms）；留到演示时用真机或浏览器网络节流观察 |
| 19 | 详情页**没有上一篇 / 下一篇** | 少一条浏览路径 | 阶段 8 实现 `adjacent` 时一并渲染 |
| 20 | 详情页 chunk 拆包 | 首屏不再被管线阻塞；**完整浏览的总字节数不变** | **部分闭环（阶段 7 批 2）**：shell **17.62 kB**、管线 **281.34 kB**；要压小需 BH②（`hljs/lib/core` 按需注册），**作者已拍板暂不做** |
| 21 | ~~正文图片懒加载无真实内容可验证~~ | — | **阶段 6 批 2 肉眼复验通过** |
| 22 | ~~目录只在桌面显示，窄屏没有可展开入口~~ | — | **已闭环（阶段 7 批 4）**：窄屏「本页目录」折叠面板，点条目自动收起 |
| 23 | ~~未实现「阅读进度条 / 回到顶部」~~ | — | **已闭环（阶段 7 批 1）**：页头 2px 进度条 + 全站回到顶部按钮，浏览器 8 项实测 |
| 24 | ~~Toast 视觉截图未留档~~ | — | **阶段 6 已留档**（成功 + 异常各一张） |
| 25 | 重置本地数据后旧 `visitorId` 的点赞无法用接口删除 | 该赞仍计入总数 | 演示级语义（关于页已说明）；清理只能直接操作数据库 |
| 26 | 内置浏览器合成点击偶发不送达 | 验证需改用键盘 | 已流程化"优先键盘路径"（工具限制，非项目缺陷） |
| 27 | ~~`CommentSection` / `LikeButton` 随详情页 chunk 加载~~ | — | **已闭环（阶段 7 批 2）**：两者留在 17.62 kB 的详情 shell chunk 里，不再与管线同包 |
| 28 | ~~`40009` 不可达~~ | — | **已拍板（决策 BE）**：阶段 8 启用 |
| 29 | ~~「评论已发表」提示在评论被删除后仍显示~~ | — | **已闭环（阶段 7 批 5，决策 BM）**：`CommentForm` 暴露 `clearStatus()`，删除成功后由父级调用；E2E 实测通过 |
| 30 | ~~亮色主色对比度 4.44 低于 WCAG AA~~ | — | **已闭环（阶段 7 批 5，决策 BL）**：改 `#3563e0`（对白底 **5.23**），`--color-accent-soft` 同步改 |
| 31 | **触摸模拟下合成点击打不开 `<details>`**（阶段 7 批 4 观察） | 验证窄屏目录面板时点不动 | 工具限制，非项目缺陷；**规避**：`click_if_interactive` 取 ref 后走键盘 `Enter` |
| 32 | **详情页"异步管线加载失败"的表现未演练**（阶段 7 批 2 观察） | 极端网络下正文可能停在骨架 | dev 模式会走 Vite 报错覆盖层，与生产行为不一致；如实登记，阶段 9 审计时评估是否补 `errorComponent` |

> **未覆盖项（如实登记，非缺陷）**：`50000` / `50001` 未构造触发条件、未实测；浏览器控制台 warn 与网络层 4xx 未直接检查（本机 Edge 无头不可用）；`prefers-reduced-motion` 仅静态证据；并发与 `SQLITE_BUSY` 留阶段 9。

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK / Maven | `java 26.0.2.1`（已验证可跑 Spring Boot 4.1.1）；Maven 未安装，用 `mvnw` |
| Node / npm | `node v24.21.0`、`npm 11.19.0` |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9（**阶段 6 / 阶段 7 均未新增依赖**） |
| 后端依赖 | Spring Boot 4.1.1、Spring 7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc 3.1.1、Jackson 3、HikariCP |
| 数据库 | `backend/data/blog.db`；**6 张表（5 张业务表 + `sqlite_sequence`）＋ 8 个索引（5 个显式 `idx_*` + 3 个自动索引）**；**批 6 终核** `article=12`、`tag=8`、`article_tag=23`、`comment=0`、`like_record=0`；`journal_mode=delete`；`busy_timeout=5000`（应用连接由 JDBC URL 设定） |
| 前端静态资源 | `frontend/public/favicon.svg` + `images/covers/*.svg`（12 张）+ `images/articles/markdown-pipeline.svg` |
| 演示素材 | `docs/demo/`：阶段 6 归档 **15 张**（`stage6-01…15`）+ **阶段 7 归档 10 张**（`stage7-01` 进度条中间态 / `02` 底部 + 回到顶部 / `03` 暗色 / `04` 拆包后详情 / `05` 无限滚动到底 / `06` 窄屏折叠目录 / `07` 评论已发表 / `08` 删除后提示消失 / `09` 响应式 1920 / `10` 暗色 1920） |
| 服务状态 | **前后端均在运行**（后端 8080 / 前端 5173），批 6 收尾未停服；如需停服见第八节贴士 ③ |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达 |
| Git 仓库 | 分支 `main`；阶段 7 提交链：`85e5326`（批 0）→ `bb49776`（批 1）→ `fd6ba43`（批 2）→ `f35008d`（批 3）→ `3f1b786`（批 4）→ `cc1f0b8`（批 5）→ **批 6 提交见 `git log` 最新一条** |
| 前端构建基线 | **阶段 7 收尾实测**：`npm run build` → **255ms**；`index-*.js 55.57 kB / gzip 22.34 kB`、详情 shell `ArticleDetailView 18.38 kB / gzip 6.70 kB`、`MarkdownRenderer 281.34 kB / gzip 104.55 kB`、共享 chunk `_plugin-vue_export-helper 70.36 kB / gzip 27.28 kB`、`ArticlesView 8.63 kB`、`AboutView 4.89 kB` |
| 接口回归基线 | **阶段 7 收尾实测**：`npm run smoke` → `全部通过：97/97 项断言` |

---

## 七、后端接口清单（阶段 2 产物 · 契约 v1.0，阶段 7 未改动）

**已实现并实测通过（13 个操作）**：`GET /api/health`；`GET /api/articles`（分页 / `keyword` / `tags`+`tagMode` / `status`）；`GET /api/articles/{id}`；`POST /api/articles`；`PUT /api/articles/{id}`；`DELETE /api/articles/{id}`；`GET /api/tags`；`GET` / `POST /api/articles/{id}/comments`；`DELETE /api/comments/{id}`；`GET` / `POST` / `DELETE /api/articles/{id}/likes`。

**未实现（契约标为阶段 8 可选项）**：`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理 `POST` / `PUT` / `DELETE /api/tags`（**落地时一并启用 `40009`**，决策 BE）、`view_count` 计数。

**统一约定**：响应 `{code, message, data}`；错误码 `0` / `40001` / `40002` / `40004` / `40009`（**当前不可达**）/ `50000`（未构造触发）/ `50001`（未构造触发）；时间格式 `yyyy-MM-dd'T'HH:mm:ss`；分页 `page` 从 1 起、`size` 1–20（默认 10）。Swagger：5 个分组、13 条接口摘要。

**复核结论**见 `docs/audit-report.md`（「阶段 6 预审计」三块 + 「阶段 7 变更摘要」）；可重复验证用 `npm run smoke`。

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

访问地址：前端 http://localhost:5173 ｜ 后端 http://localhost:8080 ｜ Swagger UI http://localhost:8080/swagger-ui/index.html ｜ API 前缀 `/api`。

> 贴士：① 先起后端（等控制台出现 `Started BlogApplication`）再起前端，否则首屏请求会经代理拿到 502；② 后端只在运行期间可访问 `/api/*` 与 Swagger；③ 停服后仍有进程占端口：`netstat -ano | grep ":8080 " | grep -i listening` 取 PID 后 `taskkill //PID <pid> //F`（前端同理，端口换 5173）；④ 数据库重置＝停服 → 删 `backend/data/blog.db`（含 `-wal` / `-shm`）→ 重启（丢数据，不可恢复）；⑤ 本地数据重置＝`/about` 页面内「重置本地数据」按钮；⑥ Git Bash 的 `curl` 传中文会按 GBK 编码，请用 `node -e` 的 `fetch` 或 Swagger UI。
>
> **阶段 7 自验入口**：`/articles/1`（页头下沿进度条 → 滚到底 100%；右下角回到顶部按钮）、`/articles`（滚到底自动补到 12 篇 +「已经到底了 · 共 12 篇」）、`/articles?page=99`（收敛为 `?page=2`）、把窗口缩到 1024px 以下（正文上方「本页目录」折叠面板，点条目跳转后自动收起）、`/articles/1` 评论区（发表 → 删除 → 提示消失）。
>
> **回归自验入口**（阶段 6 沿用）：`/articles?keyword=SQLite&tags=前端&tagMode=or`（组合筛选深链）、`/articles/abc`（非法 id 错误态）、`/articles/99999`（详情 404）、`/articles/6`（正文配图 + 评论区）、`/about`（本地数据面板 + 一键重置）、`/no-such-page`（404 页）。
