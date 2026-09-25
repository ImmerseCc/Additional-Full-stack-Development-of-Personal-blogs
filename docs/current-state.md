# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 6「全链路回归 + 异常 / 空态演练 + 契约逐条复核」进行中 —— 批 0（开工基线）✅、批 1（契约逐条复核 + 接口回归脚本）✅、批 2（正常路径全链路回归）✅**。阶段 0–5 全部完成并入库，其中**阶段 5 已经作者人工验收通过（14 条清单逐条确认）**。

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 v1.0，**已确认**；阶段 5 批 1 按作者确认补注了 likes 的 `visitorId` 必填，**未改字段**）。
2. **进度**：阶段 0、0.5、1、2、3、4、5 全部完成并入库（阶段 5 已经作者人工验收通过）；**阶段 6 已开工：批 0 ✅、批 1 ✅（契约 17 条逐条复核，13 条一致 / 4 条阶段 8 可选项）、批 2 ✅（正常路径全链路回归，六模块全绿 + 6 张截图归档），批 3–批 5 待执行**。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水，阶段 5 批 0–批 6 与**阶段 6 批 0–批 2** 已记录）、`docs/collaboration-log.md`（关键提示词汇总 + 阶段索引 + 阶段记录）、`docs/debug-log.md`（**9 条**真实记录 + **13 条**观察项）；`docs/audit-report.md` 已有**「阶段 6 预审计」**章节（正式审计仍在阶段 9）。
4. **环境事实**：Node 24.21.0 / npm 11.19.0；JDK 26 已验证可跑 Spring Boot 4.1.1；Maven 用 `mvnw`；**批 0 启动的后端（8080）与前端 dev server（5173）仍在运行**（两个后台包装进程已到 10 分钟上限被回收，但派生进程继续服务），批 3 直接复用；需要时用第八节命令重启（已给出**绝对路径版**）。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题；**阶段验收结论由作者给出，AI 不代签**；**每个阶段结束后作者会开一个新会话继续**，所以本文件必须足以让下一个会话无缝接手。
6. **本机已踩过的坑**（细节都在 `docs/debug-log.md`）：① Git Bash 的 `curl` 传中文按 **GBK** 编码 → 用 `node -e` 的 `fetch` 或 Swagger UI；② `wc -m` 按**字节**计数；③ 后台任务有 10 分钟上限，超时只杀包装进程、**派生 JVM / node 会继续存活**占端口；④ "拒绝连接"第一步永远是"确认服务是否真的在监听"；⑤ **内置浏览器合成点击偶发不送达**（阶段 5 复现 1 次；**阶段 6 批 2 复现 2 次**：评论「发表」与「删除」按钮 —— 改用 `press_key` + `Enter` 立即成功，**拿到 ref 后优先键盘路径**）；⑥ 路由过渡中间帧读不到"带过渡的子树"（先截图再读结构）；⑦ 固定 ID 的幂等种子数据遇"历史行占用同 ID"会**静默错位**；⑧ 启动后端前先查 8080；⑨ **后台标签页里 `requestAnimationFrame` / `IntersectionObserver` 被节流**（进场动画不推进，切到可见即恢复）；⑩ **Toast 只活 2.6s，截图常晚于窗口** → 把"点击 + `page.wait_for`"放进同一批调用；⑪ **临时 Java 程序在 Git Bash 控制台打印中文会乱码**（控制台编码不匹配）→ 只读数值型结果，或给 `java` 追加 `-Dstdout.encoding=UTF-8`；⑫ **`page.wait_for` 的 text 条件按视口扫描**：目标文案在屏幕外时会超时（批 2 曾据此误判"筛选未生效"，用 `page.text.snapshot` 复核后确认已生效）。
7. **前端代码地图**（阶段 6 未改动业务代码）：
   - `src/api/`：`http.js`（统一请求 / 超时 / 解包 / 错误归一）、`error.js`、`articles.js`（含参数规范化：数组 `join(',')` + `keyword` 去空白）、`tags.js`、`comments.js`、`likes.js`
   - `src/utils/`：`date.js`（`formatDate` / `formatDateTime`）、`markdown.js`、`reveal.js`、`scrollSpy.js`、`debounce.js`、`storage.js`（`blog:` 前缀读写 / 清空）、`visitor.js`、`validate.js`
   - `src/stores/`：`theme.js`、`toast.js`、`likes.js`、`myComments.js`
   - `src/components/`（18 个）：`AppHeader` / `AppFooter` / `ThemeToggle` / `ArticleCard` / `ArticleList` / `ArticleSkeleton` / `SkeletonBlock` / `Pagination` / `MarkdownRenderer` / `TableOfContents` / `EmptyState` / `SearchInput` / `TagFilter` / `CommentSection` / `CommentItem` / `CommentForm` / `LikeButton` / `ToastStack` / `LocalDataPanel`
   - `scripts/smoke.mjs`：**接口层回归脚本**（阶段 6 批 1 新增，`npm run smoke`，Node 原生 `fetch`、零新增依赖、97 项断言、自动清理测试数据）
   - 路由：`/`、`/articles`（`keyword / tags / tagMode / page` 四参数与地址栏同步，越界自动回退）、`/articles/:id`（正文 + 互动区 + 评论区 + 桌面目录）、`/about`（项目说明 + **本地数据管理**）、404 兜底
   - `base.css` 令牌：主题三态语义色 + `--color-like` + `--hl-*` 两套高亮 + 通用控件类 `.btn / .btn--ghost / .input / .chip` + `.reveal`
8. **写代码时的硬经验**（阶段 4 / 5 代价换来的）：
   ① **动画不能成为内容可见性的前提**；② **取渲染结果必须等目标分支真正挂载**（放 `finally` 之后并 `await nextTick()`）；③ **读写非响应式外部状态（localStorage、DOM 尺寸）要么等一拍、要么用 `flush: 'post'` 的 watcher 驱动**；④ 契约的 `CommentVO` **不回传 `visitorId`**，"哪条评论是我的"靠本地账本 `blog:myComments` 显示、归属校验仍在后端；⑤ 点赞 / 评论计数**以后端返回为准**，本地只存"我赞过哪些"。

---

## 一、当前阶段

**阶段 6「全链路回归 + 异常 / 空态演练 + 契约逐条复核」：进行中 —— 批 0 ✅、批 1 ✅、批 2 ✅。**

- **批 1 结论**：契约 §三的 **17 条接口**逐条对账完毕 —— **13 条已实现且行为一致**，**4 条为契约已标注的阶段 8 可选项**；通用约定与 `0` / `40001` / `40002` / `40004` 全部实测命中；**发现 `40009` 不可达**（详见 `docs/audit-report.md` 阶段 6 预审计 §3、本文件遗留 28）。交付 `frontend/scripts/smoke.mjs`（`npm run smoke`，**97 项断言全通过**）。
- **批 2 结论**：**六个前端模块的正常路径全链路全部通过**（真实浏览器 + 接口侧双向复核）——导航与三态主题（含刷新保持）、列表（12 篇 + 深链 `?page=2` + 越界回退 `?page=99 → 2`）、搜索与过滤（防抖 `?keyword=SQLite` → 3 篇、标签 AND 2 篇 / OR 5 篇、空态 + 清除筛选、卡片标签跳转）、详情（Markdown / Java 代码高亮 / 正文配图 / 目录 scroll-spy）、评论（发表 → 归属删除 + 行内确认 → 空态，接口侧 `total=1 → 0`）、点赞（点赞 → 刷新保持 → 取消，接口侧 `likeCount=1 → 0`）、本地数据（面板 5 键真实值 → 一键重置 → Toast「清理 5 项」）。
- **下一批：批 3 —— 异常 / 边界 / 空态演练**（6 类错误码逐条触发、后端停服降级与 Toast、输入边界、XSS / SQL 注入样本、URL 篡改与未知路由）。

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
| 阶段 6 批 0 | 开工基线：文档更正 + 两端启动 + 构建 / 数据库基线复核 | 后端 `Started BlogApplication in 1.544 seconds`；前端 `VITE v8.3.0 ready in 211 ms`；构建 151 模块 / 241ms；数据库计数与预期一致；更正表 / 索引计数（见第六节）；提交 `bad0853` → `ce87b7b` |
| 阶段 6 批 1 | 契约逐条复核 + 接口回归脚本 | `frontend/scripts/smoke.mjs` **97/97 断言通过**；17 条接口 13 条一致 / 4 条阶段 8 可选项；**发现 `40009` 不可达**；`docs/audit-report.md` 阶段 6 预审计；提交 `2c5b301` |
| **阶段 6 批 2** | 正常路径全链路回归（六模块） | 真实浏览器逐模块走查（深链 / 越界回退 / AND-OR / 空态 / 代码高亮 / 目录 scroll-spy / 评论归属删除 / 点赞刷新保持 / 一键重置 + Toast）；**6 张截图归档 `docs/demo/`**；接口侧复核数据全部还原；业务代码零改动 |

**关键决策记录（阶段 5 / 6，作者已确认）**

| 编号 | 决策 |
|---|---|
| AH | 阶段 6 定位：**已拍板** —— 采用"全链路回归 + 异常 / 空态演练 + 契约逐条复核"，替代原"前后端对接（真实数据替换 mock）" |
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
| AT | 契约补注（likes 的 `visitorId` 必填）只改表述、不改字段（作者已确认） |
| AU | 阶段 6 定位：采用"全链路回归 + 异常 / 空态演练 + 契约逐条复核"（作者已确认） |
| AV | 阶段 6 以**验证 + 文档**为主；代码类加固单列（AX / BA / AZ 分别处理） |
| AW | 新增 `frontend/scripts/smoke.mjs` 固化接口层回归 —— **批 1 已落地并实测 97/97 通过** |
| AX | 新增前端全局错误兜底（`app.config.errorHandler` + `unhandledrejection` → Toast）—— 批 4 落地 |
| AY | 复核结论写入 `docs/audit-report.md` 的「阶段 6 预审计」章节 —— **批 1 已落地** |
| AZ | 详情页 chunk 拆包（遗留 20 / 27）**留阶段 7**；本阶段只测量并给出方案 |
| BA | Toast 截图留档（遗留 24）—— **批 2 已落地**：`docs/demo/stage6-06-about-localdata-toast.png`（「本地数据已重置（清理 5 项）」；异常 Toast 仍未单独截图） |
| BB | **不**给启动命令追加 `--enable-native-access`（保持标准命令简洁，延续阶段 1 决定） |
| BC | 批 0 **不**重置数据库；批 1–4 演练产生的数据在批 5 还原 |
| BD | 阶段 6 沿用"每批停下等确认"的节奏 |

---

## 三、待确认 / 待执行

1. **批 3 待执行**：异常 / 边界 / 空态演练（6 类错误码逐条触发、后端停服降级、输入边界、XSS / SQL 注入样本、URL 篡改与未知路由）；
2. **`40009` 的处理方式待定**（批 1 新发现，**作者尚未答复**）：① 阶段 8 做标签管理接口时启用；② 在契约中标注为"预留"。**建议 ①** —— 本阶段不动契约与代码；
3. **可选未做项**（契约标为阶段 8 可选项）：SQLite WAL 模式、`view_count` 计数、`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`（上下篇）、标签管理接口；
4. **阶段 7 排期项**：阅读进度条、回到顶部、无限滚动 + 骨架屏（决策 S 的既定安排）、窄屏目录折叠入口、详情页 chunk 拆包（决策 AZ）。

---

## 四、逐批结果

### 阶段 5（批 0–批 6，作者已确认并整体验收通过）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 + 文档更正 + 正文配图 | `npm run build` 123 模块 / 216ms；后端两次启动幂等复核（`contentLen=578`、配图命中 1）；真实种子内容管线断言 9 项全绿；探针页验证后删除 |
| 1 ✅ | 存储层 + 评论 / 点赞接入层 | **25 项真实用例 25/25**（`tags` 数组 AND=2 / OR=5、`keyword` trim=3、`size=999→40002`、空昵称 / 非法邮箱字段级 40001、归属不符 40004、点赞两端幂等、`clearAll → removed=3`） |
| 2 ✅ | 模块四 搜索与分类过滤 | 8 项浏览器实测（防抖 → `?keyword=CSS`、or 语义 5 篇、空态清除筛选、卡片标签跳转、`?page=99 → ?page=2`） |
| 3 ✅ | 模块五 A 评论区 | 8 项浏览器实测（字段级校验、归属删除真删、加载更多 11 条、他人评论无删除入口） |
| 4 ✅ | 模块五 B 点赞 + Toast | 7 项浏览器实测（点赞 / 取消 / 刷新保持 / 异常 Toast 108ms 捕获 / 暗色） |
| 5 ✅ | 模块六 本地数据 + 一键重置 | 面板概览与真实数据一致；重置 Toast「清理 5 项」+ 主题回跟随系统；自查缺陷（报错记录 9）修复并复验 |
| 6 ✅ | 收尾 + **作者人工验收** | 构建终测 **270ms**；四份文档同步；14 条验收清单**逐条通过** |

### 阶段 6（进行中）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线：文档更正 + 两端启动 + 构建 / 数据库基线 | 后端 `Started BlogApplication in 1.544 seconds`；前端 `VITE v8.3.0 ready in 211 ms`；`npm run build` **151 模块 / 241ms**；数据库 `article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`；**更正表 / 索引计数** |
| 1 ✅ | 契约逐条复核 + 接口回归脚本 | `npm run smoke` → **97/97 项断言通过**；17 条契约接口 **13 条一致 / 4 条阶段 8 可选项**；`40009` 不可达；`docs/audit-report.md` 阶段 6 预审计 |
| 2 ✅ | 正常路径全链路回归（六模块） | 导航 + 三态主题（刷新保持）、列表（12 篇 / 深链 / 越界回退）、搜索过滤（防抖 3 篇 / AND 2 篇 / OR 5 篇 / 空态清除筛选 / 卡片标签跳转）、详情（代码高亮 / 正文配图 / 目录 scroll-spy）、评论（发表 → 归属删除 → 空态，`total=1→0`）、点赞（`likeCount=1→0` + 刷新保持）、本地数据（面板 5 键 → 重置 + Toast）；**6 张截图入 `docs/demo/`**；数据还原复核通过 |
| 3–5 | 异常 / 空态演练 → 非功能复核 → 收尾 | 待执行 |

---

## 五、遗留问题

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1–7 | ~~已解决的历史项~~ | — | 后端首次启动、JDK 26 兼容性、依赖下载慢、契约与模型草案、路径含空格、`frontend/public/` 缺失、种子仅 3 篇 |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、`README.md`、Swagger 描述、关于页中标注 |
| 9 | WAL 未启用（`busy_timeout=5000` 已生效；批 0 实测 `journal_mode=delete`） | 并发写场景收益有限 | 需要时在 `connection-init-sql` 追加 `PRAGMA journal_mode = WAL` |
| 10 | 结束后台任务会留下派生进程（JVM / node） | 端口占用 / 前端代理 502 | 已流程化：`netstat` 取 PID + `taskkill //PID <pid> //F`；阶段 6 批 0 再次复现并记录 |
| 11–15 | ~~已解决的历史项~~ | — | native-access 警告、外键约束验证、过渡观感与触摸点按（作者已验收）、tag ID 冲突 |
| 16 | 前端自动化回归 | 浏览器层仍需人工 | **接口层已闭环（批 1）**：`npm run smoke` 可重复运行；浏览器层由批 2 / 批 4 覆盖，阶段 9 视余量再评估固化 |
| 17 | ~~`?page` 越界不回退~~ | — | **已修复（阶段 5 批 2）**，批 2 真实浏览器复验通过 |
| 18 | 骨架屏**出现时机**未能真机抓拍（本地请求约 10ms） | 只能证明外观正确 | 阶段 7 做无限滚动时一并观察 |
| 19 | 详情页**没有上一篇 / 下一篇**（契约 `prev` / `next` 恒为 `null`） | 少一条浏览路径 | 阶段 8 实现 `adjacent` 时一并渲染 |
| 20 | 详情页 chunk 297.51 kB（gzip 111.03 kB） | 首次进详情页多下 ~111 kB（gzip） | **阶段 6 只测量并给方案（决策 AZ）**；拆包留阶段 7，可改 `hljs/lib/core` + 按需注册语言 |
| 21 | ~~Markdown 正文图片懒加载无真实内容可验证~~ | — | **已闭环（阶段 5 批 0）**，批 2 再次肉眼确认（`/articles/6` 文末配图） |
| 22 | 目录只在桌面显示，窄屏没有可展开的入口 | 手机上无法跳转章节 | 决策 Y 的既定取舍；阶段 7 可加"折叠式目录按钮" |
| 23 | 未实现「阅读进度条 / 回到顶部」 | 详情页少两项体验增强 | 阶段索引已放在**阶段 7（功能迭代一）** |
| 24 | ~~Toast 视觉截图未留档~~ | — | **已闭环（阶段 6 批 2，决策 BA）**：`docs/demo/stage6-06-about-localdata-toast.png` 含「本地数据已重置（清理 5 项）」；**异常 Toast 仍未单独截图**（批 3 可补） |
| 25 | 重置本地数据后，旧 `visitorId` 的点赞记录无法用接口删除 | 该赞仍计入总数，无法自助撤销 | 演示级语义（关于页已说明，作者验收时已确认）；清理只能直接操作数据库 |
| 26 | 内置浏览器**合成点击偶发不送达** | 验证需改用键盘 | **阶段 6 批 2 复现 2 次**（评论「发表」/「删除」按钮）：改用 `press_key` + `Enter` 立即成功；已写入 `debug-log.md` 观察项 |
| 27 | `CommentSection` / `LikeButton` 随详情页 chunk 加载 | 详情页首屏体积再增 | 与第 20 条同一取舍（决策 AZ：阶段 7 一并评估） |
| 28 | **`40009`（资源冲突）不可达**：契约、`ErrorCode.CONFLICT`、Swagger 描述三处提及，但实现中无任何抛出点（批 1 发现） | 无功能影响；调用方可能按文档写出死分支 | **待作者拍板**：建议阶段 8 做标签管理接口时启用，或在契约中标注"预留"（见第三节第 2 条） |
| **29** | **「评论已发表」提示在评论被删除后仍显示**（批 2 观察：列表已回空态、表单已清空，该提示仍在） | 轻微体验瑕疵；未验证是否会自动消失 | **待作者判断**；若需修正，可在删除成功后一并清掉该提示（属阶段 7 的体验迭代范围） |

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK / Maven | `java 26.0.2.1`（已验证可跑 Spring Boot 4.1.1）；Maven 未安装，用 `mvnw`（wrapper 3.3.4 / Maven 3.9.16 已缓存） |
| Node / npm | `node v24.21.0`、`npm 11.19.0` |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9（阶段 6 批 0–批 2 **未新增依赖**） |
| 后端依赖 | Spring Boot 4.1.1（`spring-boot-starter-webmvc`）、Spring 7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc 3.1.1、Jackson 3、HikariCP |
| 数据库 | `backend/data/blog.db`；**6 张表（5 张业务表 + `sqlite_sequence`）＋ 8 个索引（5 个显式 `idx_*` + 3 个 SQLite 自动索引）**；**批 2 收尾复核** `article=12`、`tag=8`、`article_tag=23`、`comment=0`、`like_record=0`；封面 12/12、正文配图 1；`journal_mode=delete`（未启 WAL，遗留 9）；`busy_timeout=5000` |
| 前端静态资源 | `frontend/public/favicon.svg` + `images/covers/*.svg`（12 张）+ `images/articles/markdown-pipeline.svg`（1 张正文配图） |
| 演示素材 | `docs/demo/`：阶段 6 批 2 归档 6 张真实浏览器截图（`stage6-01-list-empty-state.png`、`stage6-02-detail-code-highlight.png`、`stage6-03-detail-inline-image.png`、`stage6-04-detail-dark.png`、`stage6-05-comments.png`、`stage6-06-about-localdata-toast.png`） |
| 服务状态 | **后端 8080（JVM PID 38900）与前端 dev server 5173（node PID 36092）仍在运行**（批 0 启动，批 1 / 批 2 复用；阶段收尾时按流程停服并释放端口） |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达 |
| Git 仓库 | 分支 `main`；阶段 6 提交链：`bad0853`（批 0）→ `ce87b7b`（超时复现补记）→ `2c5b301`（批 1）；**批 2 改动待提交**（提交后见 `git log` 最新一条） |
| 前端构建基线 | **阶段 6 批 0 实测**：`npm run build` → 151 模块 / `✓ built in 241ms`；`index-*.js 54.22 kB / gzip 21.86 kB`、`_plugin-vue_export-helper-*.js 63.56 kB`、详情 chunk `297.51 kB / gzip 111.03 kB`、`AboutView 4.90 kB`、`ArticlesView 8.80 kB`、`index css 10.18 kB`、`ArticleDetailView css 11.85 kB` |
| 接口回归基线 | **阶段 6 批 1 实测**：`npm run smoke` → `全部通过：97/97 项断言`（约 1.3s） |

---

## 七、后端接口清单（阶段 2 产物 · 契约 v1.0）

**已实现并实测通过（13 个操作）**：`GET /api/health`；`GET /api/articles`（分页 / `keyword` / `tags`+`tagMode` / `status`）；`GET /api/articles/{id}`；`POST /api/articles`；`PUT /api/articles/{id}`；`DELETE /api/articles/{id}`；`GET /api/tags`；`GET` / `POST /api/articles/{id}/comments`；`DELETE /api/comments/{id}`；`GET` / `POST` / `DELETE /api/articles/{id}/likes`。

**未实现（契约标为阶段 8 可选项）**：`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理 `POST` / `PUT` / `DELETE /api/tags`、`view_count` 计数。

**统一约定**：响应 `{code, message, data}`；错误码 `0` / `40001` / `40002` / `40004` / `40009`（**不可达**，遗留 28）/ `50000`（未构造触发）/ `50001`（未构造触发）；时间格式 `yyyy-MM-dd'T'HH:mm:ss`；分页 `page` 从 1 起、`size` 1–20（默认 10）。Swagger：5 个分组、13 条接口摘要。

**阶段 6 批 1 的逐条复核结论**见 `docs/audit-report.md` →「阶段 6 预审计」；可重复验证用 `npm run smoke`。

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
> **自验入口**：`/articles`（搜索 / 标签筛选 / 分页 / 空态）、`/articles?keyword=SQLite&tags=前端&tagMode=or`（组合筛选深链）、`/articles?page=99`（越界回退）、`/articles/6`（正文配图 + 评论区）、`/articles/1`（Java 代码高亮）、`/about`（本地数据面板 + 一键重置）、`/no-such-page`（404）。
