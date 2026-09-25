# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 6「全链路回归 + 异常 / 空态演练 + 契约逐条复核」进行中 —— 批 0 ✅、批 1 ✅、批 2 ✅（作者已通过）、批 3 ✅**。阶段 0–5 全部完成并入库，其中**阶段 5 已经作者人工验收通过（14 条清单逐条确认）**。

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 v1.0，**已确认**；阶段 5 批 1 按作者确认补注了 likes 的 `visitorId` 必填，**未改字段**）。
2. **进度**：阶段 0、0.5、1、2、3、4、5 全部完成并入库（阶段 5 已通过作者人工验收）；**阶段 6 已开工：批 0（基线）✅、批 1（契约 17 条逐条复核 + `npm run smoke` 97 项断言）✅、批 2（六模块全链路回归，作者已通过）✅、批 3（异常 / 边界 / 安全演练）✅；批 4–批 5 待执行**。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水，阶段 5 批 0–批 6 与**阶段 6 批 0–批 3** 已记录）、`docs/collaboration-log.md`（关键提示词汇总 + 阶段索引 + 阶段记录）、`docs/debug-log.md`（**9 条**真实记录 + **14 条**观察项）；`docs/audit-report.md` 已有**「阶段 6 预审计」（批 1 契约复核 + 批 3 异常 / 安全复核）**（正式审计仍在阶段 9）。
4. **环境事实**：Node 24.21.0 / npm 11.19.0；JDK 26 已验证可跑 Spring Boot 4.1.1；Maven 用 `mvnw`；**当前后端 8080（JVM PID 40772）与前端 dev server 5173（node PID 2272）均在运行**（后端与前端在批 3 演练中重启过；批 0 启动的前端 node 进程此后自行退出，已在 `debug-log.md` 记录）。需要时用第八节命令重启（已给出**绝对路径版**）。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题；**阶段验收结论由作者给出，AI 不代签**；**每个阶段结束后作者会开一个新会话继续**，所以本文件必须足以让下一个会话无缝接手。
6. **本机已踩过的坑**（细节都在 `docs/debug-log.md`）：① Git Bash 的 `curl` 传中文按 **GBK** 编码 → 用 `node -e` 的 `fetch` 或 Swagger UI；② `wc -m` 按**字节**计数；③ 后台任务有 10 分钟上限，超时只杀包装进程、派生 JVM / node 会继续存活占端口，但**派生进程也可能在更晚时候自行退出**（批 3 实测：前端 node 约 30 分钟后消失，需重启 dev server）；④ "拒绝连接"第一步永远是"确认服务是否真的在监听"；⑤ **内置浏览器合成点击偶发不送达**（阶段 5 复现 1 次；**阶段 6 批 2 复现 2 次**：评论「发表」与「删除」按钮 —— 改用 `press_key` + `Enter` 立即成功，**拿到 ref 后优先键盘路径**）；⑥ 路由过渡中间帧读不到"带过渡的子树"（先截图再读结构）；⑦ 固定 ID 的幂等种子数据遇"历史行占用同 ID"会**静默错位**；⑧ 启动后端前先查 8080；⑨ **后台标签页里 `requestAnimationFrame` / `IntersectionObserver` 被节流**（进场动画不推进，切到可见即恢复）；⑩ **Toast 只活 2.6s，截图常晚于窗口** → 把"点击 + `page.wait_for`"放进同一批调用；⑪ **临时 Java 程序在 Git Bash 控制台打印中文会乱码** → 只读数值型结果，或给 `java` 追加 `-Dstdout.encoding=UTF-8`；⑫ **`page.wait_for` 的 text 条件按视口扫描**：目标文案在屏幕外时会超时（先用 `page.text.snapshot` 复核再判定）。
7. **前端代码地图**（阶段 6 至今**未改动业务代码**）：
   - `src/api/`：`http.js`（统一请求 / 超时 / 解包 / 错误归一）、`error.js`、`articles.js`（参数规范化：数组 `join(',')` + `keyword` 去空白）、`tags.js`、`comments.js`、`likes.js`
   - `src/utils/`：`date.js`、`markdown.js`、`reveal.js`、`scrollSpy.js`、`debounce.js`、`storage.js`（`blog:` 前缀读写 / 清空）、`visitor.js`、`validate.js`
   - `src/stores/`：`theme.js`、`toast.js`、`likes.js`、`myComments.js`
   - `src/components/`（18 个）：`AppHeader` / `AppFooter` / `ThemeToggle` / `ArticleCard` / `ArticleList` / `ArticleSkeleton` / `SkeletonBlock` / `Pagination` / `MarkdownRenderer` / `TableOfContents` / `EmptyState` / `SearchInput` / `TagFilter` / `CommentSection` / `CommentItem` / `CommentForm` / `LikeButton` / `ToastStack` / `LocalDataPanel`
   - `scripts/smoke.mjs`：**接口层回归脚本**（批 1 新增，`npm run smoke`，Node 原生 `fetch`、零新增依赖、97 项断言、自动清理测试数据）
   - 路由：`/`、`/articles`（四参数与地址栏同步，越界自动回退）、`/articles/:id`（正文 + 互动区 + 评论区 + 桌面目录）、`/about`（项目说明 + **本地数据管理**）、404 兜底
8. **写代码时的硬经验**（阶段 4 / 5 代价换来的）：
   ① **动画不能成为内容可见性的前提**；② **取渲染结果必须等目标分支真正挂载**（放 `finally` 之后并 `await nextTick()`）；③ **读写非响应式外部状态（localStorage、DOM 尺寸）要么等一拍、要么用 `flush: 'post'` 的 watcher 驱动**；④ 契约的 `CommentVO` **不回传 `visitorId`**，"哪条评论是我的"靠本地账本 `blog:myComments` 显示、归属校验仍在后端；⑤ 点赞 / 评论计数**以后端返回为准**，本地只存"我赞过哪些"。

---

## 一、当前阶段

**阶段 6「全链路回归 + 异常 / 空态演练 + 契约逐条复核」：进行中 —— 批 0 ✅、批 1 ✅、批 2 ✅（作者已通过）、批 3 ✅。**

- **批 1 结论**：契约 §三的 **17 条接口**逐条对账 —— **13 条已实现且行为一致**，**4 条为契约已标注的阶段 8 可选项**；通用约定与 `0` / `40001` / `40002` / `40004` 全部实测命中；**发现 `40009` 不可达**（**已拍板：决策 BE —— 阶段 8 做标签管理接口时启用**）。交付 `npm run smoke`（**97 项断言全通过**）。
- **批 2 结论**：**六个前端模块的正常路径全链路全部通过**（真实浏览器 + 接口侧双向复核），6 张截图入 `docs/demo/`；**作者已验收通过**。
- **批 3 结论**：**异常 / 边界 / 安全三层全部实测** —— XSS 四类载荷（标题 / 正文 / 标签 / 评论）一律纯文本、零执行；SQL 注入与 `%` `_` 通配被参数化 + `ESCAPE` 挡住（无 500）；`javascript:` 链接未渲染为链接；**后端停服**下列表 / 详情降级为整页错误态 + 重试、评论为行内错误、点赞为 Toast、`/about` 不受影响，重启后自动恢复；404 页 / 详情 404 / 非法 id / 未知标签 / 非法分页各有明确表现。5 张截图入 `docs/demo/`；演练数据全部清理（含 2 个孤立标签）。
- **下一批：批 4 —— 非功能与体验复核**：响应式多视口（375 / 768 / 1024 / 1280 / 1920）、三态主题与暗色对比度、键盘可达性与焦点可见、`prefers-reduced-motion`、浏览器控制台零报错、构建体积与 chunk 复核；并按**决策 AX** 落地前端全局错误兜底（`app.config.errorHandler` + `unhandledrejection` → Toast）。

**阶段 5 验收记录（历史留档，作者逐条确认，2026-09-25）**

| # | 验收点 | 结论 |
|---|---|---|
| 1 | 搜索 + 防抖（`?keyword=SQLite` → 3 篇） | ✅ |
| 2 | 标签多选 + `同时包含 / 任一即可`（2 篇 / 5 篇） | ✅ |
| 3 | 空结果动画 + 「清除筛选」 | ✅ |
| 4 | 深链 `?page=2` 与越界回退 `?page=99 → 2` | ✅ |
| 5 | 卡片标签可点跳过滤 | ✅ |
| 6 | 评论发表与字段级校验 | ✅ |
| 7 | 评论归属与删除（仅本人可见 + 行内确认） | ✅ |
| 8 | 评论「加载更多」 | ✅ |
| 9 | 点赞 + 数字动画 + 刷新保持 | ✅ |
| 10 | 异常 Toast（后端不可用时） | ✅ |
| 11 | 本地数据面板（含实时更新） | ✅ |
| 12 | 一键重置 | ✅ |
| 13 | 响应式（375 / 768 / 1280）与三态主题 | ✅ |
| 14 | 减少动态效果（可选项） | ✅ |

---

## 二、已完成

| 时间点 | 事项 | 产物与证据 |
|---|---|---|
| 阶段 0 / 0.5 | 需求确认与技术选型 / 协作日志规范 | Vue 3 + Vite + 原生 CSS 变量；Spring Boot 4.1.1 + JdbcTemplate + SQLite；0–9 阶段路线；双日志分工 |
| 阶段 1 批 1–4 | 目录骨架与占位文件 | 根目录 3 + docs 9 + frontend 12 + backend 15 文件；`git init`；契约与模型升级 v1.0；提交 `501065a` → `25e280e` |
| 阶段 2 批 0–4 | 后端业务实现（13 个操作） | 52 项接口实测；外键级联 / 点赞幂等 / `busy_timeout` 均实测；提交 `e9ab336` → `7174838` → `af2334c` |
| 阶段 3 批 0–4 | 前端模块一（导航 / 主题 / 404 / 移动端 / 过渡） | 提交 `8c5923d` → `7de72da` → `b257499` → `b1a517f` → `378e3b0` |
| 阶段 4 批 0–5 | 前端模块二 / 三（列表 / 详情 / Markdown / 目录 / 进场动画） | 12 篇种子 + 12 张封面；22 项浏览器实测 + 15 项管线断言；提交 `ec848f3` → `16f1726` → 收尾 `929c3da` |
| 阶段 5 批 0–批 6 | 前端模块四 / 五 / 六 + 阶段收尾 + 作者验收 | 提交 `d6167ea` → `3a4a7c7` → `b12bcd4` → `bf78030` → `5979b18` → `a8d499f` → `2726566` → `65efbe0` → `3852fca`；14 条清单全通过 |
| 阶段 6 批 0 | 开工基线：文档更正 + 两端启动 + 构建 / 数据库基线 | 后端 `Started BlogApplication in 1.544 seconds`；前端 `VITE v8.3.0 ready in 211 ms`；构建 151 模块 / 241ms；更正表 / 索引计数；提交 `bad0853` → `ce87b7b` |
| 阶段 6 批 1 | 契约逐条复核 + 接口回归脚本 | `npm run smoke` **97/97 断言通过**；17 条接口 13 条一致 / 4 条阶段 8 可选项；**发现 `40009` 不可达**；`docs/audit-report.md` 阶段 6 预审计；提交 `2c5b301` |
| 阶段 6 批 2 | 正常路径全链路回归（六模块） | 深链 / 越界回退 / AND-OR / 空态 / 代码高亮 / 目录 scroll-spy / 评论归属删除 / 点赞刷新保持 / 一键重置 + Toast；**6 张截图**；提交 `cbe44cf`；**作者已通过** |
| **阶段 6 批 3** | 异常 / 边界 / 空态演练 | XSS 四类载荷零执行；SQL 注入 / 通配符被参数化 + `ESCAPE` 挡住；停服降级三态（整页错误态 / 行内错误 / Toast）+ 自动恢复；404 与 URL 篡改各有明确表现；**5 张截图**；演练数据（含 2 个孤立标签）全部清理 |

**关键决策记录（阶段 5 / 6，作者已确认）**

| 编号 | 决策 |
|---|---|
| AH | 阶段 6 定位：采用"全链路回归 + 异常 / 空态演练 + 契约逐条复核"，替代原"前后端对接（真实数据替换 mock）" |
| AI | 阶段 5 **不新增任何前端依赖**，防抖 / UUID / Toast 全部手写（延续决策 W） |
| AJ | 筛选状态同步地址栏（`keyword / tags / tagMode`）；改筛选用 `replace`、翻页用 `push`；筛选变化归第 1 页 |
| AK | 标签过滤器：桌面铺开、≤767px 折叠为可展开面板；`tagMode` 仅在选中 ≥2 个标签时写进地址栏 |
| AL | 卡片标签可点（跳 `/articles?tags=X`），用 `z-index` 抬到整卡覆盖层之上 |
| AM | 空结果：内联 SVG 插画 + CSS 动画 + 「清除筛选」，不新增图片资源 |
| AN | 评论分页用「加载更多」（复用同一 `Pagination` 会带来两套分页语义） |
| AO | 评论删除入口靠本地账本 `blog:myComments` 显示（契约不含 `visitorId`），**不改契约** |
| AP | 点赞不做乐观 +1：请求期间禁用，成功后以后端返回值覆盖；卡片不加点赞按钮 |
| AQ | Toast：`Teleport to body` + `aria-live` + 最多 3 条 + 2.6s 自动消失，只存内存 |
| AR | 重置入口放在 `/about`（不新增路由）；重置 = 清空全部 `blog:` 键 + store 归默认 |
| AS | 顺手修复遗留 17（`?page` 越界回退）与遗留 21（补本地 SVG 正文配图） |
| AT | 契约补注（likes 的 `visitorId` 必填）只改表述、不改字段 |
| AU | 阶段 6 定位：采用"全链路回归 + 异常 / 空态演练 + 契约逐条复核" |
| AV | 阶段 6 以**验证 + 文档**为主；代码类加固单列（AX / BA / AZ 分别处理） |
| AW | 新增 `frontend/scripts/smoke.mjs` 固化接口层回归 —— **批 1 落地，97/97 通过** |
| AX | 新增前端全局错误兜底（`app.config.errorHandler` + `unhandledrejection` → Toast）—— **批 4 落地** |
| AY | 复核结论写入 `docs/audit-report.md` 的「阶段 6 预审计」章节 —— **批 1 + 批 3 已落地** |
| AZ | 详情页 chunk 拆包（遗留 20 / 27）**留阶段 7**；本阶段只测量并给出方案 |
| BA | Toast 截图留档（遗留 24）—— **批 2 落地**：`docs/demo/stage6-06-about-localdata-toast.png`（异常 Toast 已在批 3 补拍：`stage6-10-error-toast.png`） |
| BB | **不**给启动命令追加 `--enable-native-access`（保持标准命令简洁） |
| BC | 批 0 **不**重置数据库；批 1–4 演练产生的数据在批 5 还原（**批 3 已按此清理**） |
| BD | 阶段 6 沿用"每批停下等确认"的节奏 |
| **BE** | **`40009`（资源冲突）的处理方式：留到阶段 8 做标签管理接口时启用**；本阶段**不改契约、不改代码**，仅在文档中标记为"当前不可达"（作者 2026-09-25 拍板） |

---

## 三、待确认 / 待执行

1. **批 4 待执行**：非功能与体验复核（响应式 375 / 768 / 1024 / 1280 / 1920、三态主题与暗色对比度、键盘可达性与焦点可见、`prefers-reduced-motion`、控制台零报错、构建体积与 chunk 复核）+ **决策 AX** 前端全局错误兜底；
2. **批 5 待执行**：遗留收口 + 四份文档同步 + 交付人工验收清单（**验收结论由作者给出**）；
3. **可选未做项**（契约标为阶段 8 可选项）：SQLite WAL 模式、`view_count` 计数、`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`（上下篇）、标签管理接口（含启用 `40009`）；
4. **阶段 7 排期项**：阅读进度条、回到顶部、无限滚动 + 骨架屏（决策 S 的既定安排）、窄屏目录折叠入口、详情页 chunk 拆包（决策 AZ）、遗留 29（评论「已发表」提示）的体验修正。

---

## 四、逐批结果

### 阶段 5（批 0–批 6，作者已确认并整体验收通过）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 + 文档更正 + 正文配图 | `npm run build` 123 模块 / 216ms；后端两次启动幂等复核；真实种子内容管线断言 9 项全绿 |
| 1 ✅ | 存储层 + 评论 / 点赞接入层 | **25 项真实用例 25/25** |
| 2 ✅ | 模块四 搜索与分类过滤 | 8 项浏览器实测（防抖、or 语义、空态、卡片标签跳转、越界回退） |
| 3 ✅ | 模块五 A 评论区 | 8 项浏览器实测（字段级校验、归属删除、加载更多） |
| 4 ✅ | 模块五 B 点赞 + Toast | 7 项浏览器实测（含异常 Toast、暗色） |
| 5 ✅ | 模块六 本地数据 + 一键重置 | 面板与真实数据一致；重置 Toast；自查缺陷（报错记录 9）修复复验 |
| 6 ✅ | 收尾 + **作者人工验收** | 构建终测 **270ms**；14 条验收清单**逐条通过** |

### 阶段 6（进行中）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 | 后端 1.544s 启动 / 前端 211ms；构建 **151 模块 / 241ms**；数据库计数复核；**更正表 / 索引计数** |
| 1 ✅ | 契约逐条复核 + 接口回归脚本 | `npm run smoke` **97/97**；17 条接口 13 条一致 / 4 条阶段 8 可选项；`40009` 不可达；审计报告新增预审计章节 |
| 2 ✅ | 正常路径全链路回归（六模块） | 深链 / 越界回退 / AND-OR / 空态 / 代码高亮 / 目录 scroll-spy / 评论归属删除 / 点赞刷新保持 / 一键重置 + Toast；**6 张截图**；**作者已通过** |
| 3 ✅ | 异常 / 边界 / 空态演练 | XSS 四类载荷纯文本零执行；注入 + 通配符安全；停服降级三态（列表 / 详情整页错误态、评论行内错误、点赞 Toast）+ 4 秒恢复；404 / 详情 404 / 非法 id / 未知标签 / 非法分页各有明确表现；**5 张截图**；数据全部还原 |
| 4–5 | 非功能与体验复核 → 收尾 | 待执行 |

---

## 五、遗留问题

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1–7 | ~~已解决的历史项~~ | — | 后端首次启动、JDK 26 兼容性、依赖下载慢、契约与模型草案、路径含空格、`frontend/public/` 缺失、种子仅 3 篇 |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、`README.md`、Swagger 描述、关于页中标注 |
| 9 | WAL 未启用（`busy_timeout=5000` 已生效；批 0 实测 `journal_mode=delete`） | 并发写场景收益有限 | 需要时在 `connection-init-sql` 追加 `PRAGMA journal_mode = WAL` |
| 10 | 结束后台任务会留下派生进程（JVM / node），但**也可能稍后自行退出** | 端口占用 / 需重启 dev server | 已流程化：`netstat` 取 PID + `taskkill //PID <pid> //F`；批 3 实测前端 node 约 30 分钟后消失，重启即可 |
| 11–15 | ~~已解决的历史项~~ | — | native-access 警告、外键约束验证、过渡观感与触摸点按（作者已验收）、tag ID 冲突 |
| 16 | 前端自动化回归 | 浏览器层仍需人工 | **接口层已闭环（批 1）**：`npm run smoke`；浏览器层由批 2 / 批 3 / 批 4 覆盖，阶段 9 视余量再评估 |
| 17 | ~~`?page` 越界不回退~~ | — | **已修复**，批 2 真实浏览器复验通过 |
| 18 | 骨架屏**出现时机**未能真机抓拍 | 只能证明外观正确 | 阶段 7 做无限滚动时一并观察（批 4 再试一次） |
| 19 | 详情页**没有上一篇 / 下一篇**（契约 `prev` / `next` 恒为 `null`） | 少一条浏览路径 | 阶段 8 实现 `adjacent` 时一并渲染 |
| 20 | 详情页 chunk 297.51 kB（gzip 111.03 kB） | 首次进详情页多下 ~111 kB（gzip） | 阶段 6 只测量（决策 AZ）；拆包留阶段 7 |
| 21 | ~~Markdown 正文图片懒加载无真实内容可验证~~ | — | **已闭环**（批 2 再次肉眼确认） |
| 22 | 目录只在桌面显示，窄屏没有可展开的入口 | 手机上无法跳转章节 | 决策 Y 的既定取舍；阶段 7 可加"折叠式目录按钮" |
| 23 | 未实现「阅读进度条 / 回到顶部」 | 详情页少两项体验增强 | **阶段 7（功能迭代一）** |
| 24 | ~~Toast 视觉截图未留档~~ | — | **已闭环（批 2 + 批 3）**：`stage6-06-about-localdata-toast.png`（成功 Toast）、`stage6-10-error-toast.png`（异常 Toast） |
| 25 | 重置本地数据后，旧 `visitorId` 的点赞记录无法用接口删除 | 该赞仍计入总数，无法自助撤销 | 演示级语义（关于页已说明）；清理只能直接操作数据库 |
| 26 | 内置浏览器**合成点击偶发不送达** | 验证需改用键盘 | 批 2 复现 2 次；已流程化"优先键盘路径"，写入 `debug-log.md` |
| 27 | `CommentSection` / `LikeButton` 随详情页 chunk 加载 | 详情页首屏体积再增 | 与第 20 条同一取舍（决策 AZ） |
| 28 | ~~`40009`（资源冲突）不可达~~ | — | **已拍板（决策 BE）**：**阶段 8** 做标签管理接口时启用；本阶段不改契约 / 代码，文档标记为"当前不可达" |
| 29 | **「评论已发表」提示在评论被删除后仍显示**（批 2 观察） | 轻微体验瑕疵；未验证是否会自动消失 | **待作者判断**；若需修正，可在删除成功后一并清掉（阶段 7 体验迭代范围） |

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK / Maven | `java 26.0.2.1`（已验证可跑 Spring Boot 4.1.1）；Maven 未安装，用 `mvnw`（wrapper 3.3.4 / Maven 3.9.16 已缓存） |
| Node / npm | `node v24.21.0`、`npm 11.19.0` |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9（阶段 6 批 0–批 3 **未新增依赖**） |
| 后端依赖 | Spring Boot 4.1.1（`spring-boot-starter-webmvc`）、Spring 7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc 3.1.1、Jackson 3、HikariCP |
| 数据库 | `backend/data/blog.db`；**6 张表（5 张业务表 + `sqlite_sequence`）＋ 8 个索引（5 个显式 `idx_*` + 3 个 SQLite 自动索引）**；**批 3 收尾复核** `article=12`、`tag=8`、`article_tag=23`、`comment=0`、`like_record=0`；封面 12/12、正文配图 1；`journal_mode=delete`（未启 WAL）；`busy_timeout=5000` |
| 前端静态资源 | `frontend/public/favicon.svg` + `images/covers/*.svg`（12 张）+ `images/articles/markdown-pipeline.svg`（1 张正文配图） |
| 演示素材 | `docs/demo/`：阶段 6 已归档 **11 张**真实浏览器截图（`stage6-01` 空结果插画、`02` 代码高亮、`03` 正文配图、`04` 暗色、`05` 评论区、`06` 关于页 + 成功 Toast、`07` 404 页、`08` 列表错误态、`09` 详情错误态、`10` 停服点赞 Toast、`11` XSS 载荷按纯文本渲染） |
| 服务状态 | **后端 8080（JVM PID 40772）与前端 dev server 5173（node PID 2272）均在运行**；批 3 演练中后端停服 → 重启（4 秒就绪）、前端重启一次 |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达 |
| Git 仓库 | 分支 `main`；阶段 6 提交链：`bad0853`（批 0）→ `ce87b7b`（超时补记）→ `2c5b301`（批 1）→ `cbe44cf`（批 2）；**批 3 改动待提交** |
| 前端构建基线 | **阶段 6 批 0 实测**：`npm run build` → 151 模块 / `✓ built in 241ms`；详情 chunk `297.51 kB / gzip 111.03 kB`、`index-*.js 54.22 kB / gzip 21.86 kB` |
| 接口回归基线 | **阶段 6 批 1 实测**：`npm run smoke` → `全部通过：97/97 项断言`（约 1.3s） |

---

## 七、后端接口清单（阶段 2 产物 · 契约 v1.0）

**已实现并实测通过（13 个操作）**：`GET /api/health`；`GET /api/articles`（分页 / `keyword` / `tags`+`tagMode` / `status`）；`GET /api/articles/{id}`；`POST /api/articles`；`PUT /api/articles/{id}`；`DELETE /api/articles/{id}`；`GET /api/tags`；`GET` / `POST /api/articles/{id}/comments`；`DELETE /api/comments/{id}`；`GET` / `POST` / `DELETE /api/articles/{id}/likes`。

**未实现（契约标为阶段 8 可选项）**：`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理 `POST` / `PUT` / `DELETE /api/tags`（**阶段 8 落地时一并启用 `40009`**，决策 BE）、`view_count` 计数。

**统一约定**：响应 `{code, message, data}`；错误码 `0` / `40001` / `40002` / `40004` / `40009`（**当前不可达**）/ `50000`（未构造触发）/ `50001`（未构造触发）；时间格式 `yyyy-MM-dd'T'HH:mm:ss`；分页 `page` 从 1 起、`size` 1–20（默认 10）。Swagger：5 个分组、13 条接口摘要。

**阶段 6 的复核结论**见 `docs/audit-report.md` →「阶段 6 预审计」（批 1 契约复核 + 批 3 异常 / 安全复核）；可重复验证用 `npm run smoke`。

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
# 接口层回归（阶段 6 批 1 新增；需后端已启动）
cd "/d/code/Additional Full-stack Development of Personal blogs/frontend" && npm run smoke
```

访问地址：前端 http://localhost:5173 ｜ 后端 http://localhost:8080 ｜ Swagger UI http://localhost:8080/swagger-ui/index.html ｜ API 前缀 `/api`。

> 贴士：① 先起后端（等控制台出现 `Started BlogApplication`）再起前端，否则首屏请求会经代理拿到 502；② 后端只在运行期间可访问 `/api/*` 与 Swagger；③ 停服后仍有进程占端口：`netstat -ano | grep ":8080 " | grep -i listening` 取 PID 后 `taskkill //PID <pid> //F`；④ 数据库重置＝停服 → 删 `backend/data/blog.db`（含 `-wal` / `-shm`）→ 重启（丢数据，不可恢复）；⑤ 本地数据重置＝`/about` 页面内「重置本地数据」按钮；⑥ Git Bash 的 `curl` 传中文会按 GBK 编码，请用 `node -e` 的 `fetch` 或 Swagger UI。
>
> **自验入口**：`/articles`（搜索 / 标签筛选 / 分页 / 空态）、`/articles?keyword=SQLite&tags=前端&tagMode=or`（组合筛选深链）、`/articles?page=99`（越界回退）、`/articles/abc`（非法 id 错误态）、`/articles/99999`（详情 404）、`/articles/6`（正文配图 + 评论区）、`/about`（本地数据面板 + 一键重置）、`/no-such-page`（404 页）。
