# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 5「前端模块四 / 五 / 六：搜索与分类过滤、评论与点赞、本地持久化」全部完成（批 0–批 6）并已入库，且经作者人工验收通过（14 条验收清单逐条确认）**。阶段 0–4 亦全部完成并入库。**下一阶段：阶段 6 —— 等作者开工指令；其定位仍待作者拍板**（见第三节第 2 条）。

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 v1.0，**已确认**；阶段 5 批 1 按作者确认补注了 likes 的 `visitorId` 必填，**未改字段**）。
2. **进度**：阶段 0、0.5、1、2、3、4、5 全部完成并入库，**阶段 5 已经作者人工验收通过**。**前端六个核心模块全部完成**；后端 13 个操作全部实现并实测。种子数据 12 篇文章 / 8 个标签 / 第 6 篇含 1 张本地正文配图。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水，阶段 5 批 0–批 6 已补记）、`docs/collaboration-log.md`（关键提示词汇总 + 阶段索引 + 阶段记录）、`docs/debug-log.md`（**9 条**真实记录 + **10 条**观察项）；审计记录在 `docs/audit-report.md`（阶段 9 才做）。
4. **环境事实**：Node 24.21.0 / npm 11.19.0；JDK 26 已验证可跑 Spring Boot 4.1.1；Maven 用 `mvnw`；**后端与前端 dev server 当前均处于停止状态**，需要时用第八节命令启动（第八节已给出**绝对路径版**命令）。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题；**阶段验收结论由作者给出，AI 不代签**；**每个阶段结束后作者会新开一个会话来继续**，所以本文件必须足以让下一个会话无缝接手。
6. **本机已踩过的坑**（细节都在 `docs/debug-log.md`）：① Git Bash 的 `curl` 传中文按 **GBK** 编码 → 用 `node -e` 的 `fetch` 或 Swagger UI；② `wc -m` 按**字节**计数；③ 后台任务有 10 分钟上限，超时只杀包装进程、**派生 JVM / node 会继续存活**占端口；④ "拒绝连接"第一步永远是"确认服务是否真的在监听"；⑤ 内置浏览器**合成点击偶发不送达**（阶段 5 复现 1 次：点赞按钮首点未生效，重试成功）；⑥ 路由过渡中间帧读不到"带过渡的子树"（先截图再读结构）；⑦ 固定 ID 的幂等种子数据遇"历史行占用同 ID"会**静默错位**；⑧ 启动后端前先查 8080；⑨ **后台标签页里 `requestAnimationFrame` / `IntersectionObserver` 被节流**（进场动画不推进，切到可见即恢复）；⑩ **Toast 只活 2.6s，截图常晚于窗口** → 把"点击 + `page.wait_for`"放进同一批调用。
7. **前端代码地图**（阶段 5 之后）：
   - `src/api/`：`http.js`（统一请求 / 超时 / 解包 / 错误归一）、`error.js`、`articles.js`（含参数规范化：数组 `join(',')` + `keyword` 去空白）、`tags.js`、`comments.js`、`likes.js`
   - `src/utils/`：`date.js`（`formatDate` / `formatDateTime`）、`markdown.js`、`reveal.js`、`scrollSpy.js`、`debounce.js`、`storage.js`（`blog:` 前缀读写 / 清空）、`visitor.js`、`validate.js`
   - `src/stores/`：`theme.js`、`toast.js`、`likes.js`、`myComments.js`
   - `src/components/`（18 个）：`AppHeader` / `AppFooter` / `ThemeToggle` / `ArticleCard` / `ArticleList` / `ArticleSkeleton` / `SkeletonBlock` / `Pagination` / `MarkdownRenderer` / `TableOfContents` / `EmptyState` / `SearchInput` / `TagFilter` / `CommentSection` / `CommentItem` / `CommentForm` / `LikeButton` / `ToastStack` / `LocalDataPanel`
   - 路由：`/`、`/articles`（`keyword / tags / tagMode / page` 四参数与地址栏同步，越界自动回退）、`/articles/:id`（正文 + 互动区 + 评论区 + 桌面目录）、`/about`（项目说明 + **本地数据管理**）、404 兜底
   - `base.css` 令牌：主题三态语义色 + `--color-like` + `--hl-*` 两套高亮 + 通用控件类 `.btn / .btn--ghost / .input / .chip` + `.reveal`
8. **写代码时的硬经验**（阶段 4 / 5 代价换来的）：
   ① **动画不能成为内容可见性的前提**；② **取渲染结果必须等目标分支真正挂载**（放 `finally` 之后并 `await nextTick()`）；③ **读写非响应式外部状态（localStorage、DOM 尺寸）要么等一拍、要么用 `flush: 'post'` 的 watcher 驱动**；④ 契约的 `CommentVO` **不回传 `visitorId`**，"哪条评论是我的"靠本地账本 `blog:myComments` 显示、归属校验仍在后端；⑤ 点赞 / 评论计数**以后端返回为准**，本地只存"我赞过哪些"。

---

## 一、当前阶段

**阶段 5「前端模块四 / 五 / 六：搜索与分类过滤、评论与点赞、本地持久化」：全部完成（批 0–批 6）× 已入库 × 作者人工验收通过（14 条清单）。**

**下一阶段：阶段 6** —— 按惯例先出分批方案与决策点、等作者确认后开工；**其定位需要作者拍板**（见第三节第 2 条）。

**阶段 5 验收记录（作者逐条确认，2026-09-25）**

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
| **阶段 5 批 0** | 开工基线：文档更正 + 第 6 篇正文配图与幂等回填 | 关闭遗留 21；提交 `d6167ea` |
| **阶段 5 批 1** | 本地身份与存储层 + `src/api/` 评论 / 点赞接入层 | **25 项真实用例 25/25**；提交 `3a4a7c7` + 契约补注 `b12bcd4` |
| **阶段 5 批 2** | 模块四：搜索与分类过滤 | 8 项浏览器实测；关闭遗留 17；提交 `bf78030` |
| **阶段 5 批 3** | 模块五 A：评论区 | 8 项浏览器实测（含归属删除、加载更多）；提交 `5979b18` |
| **阶段 5 批 4** | 模块五 B：点赞 + Toast | 7 项浏览器实测（含异常 Toast、暗色）；提交 `a8d499f` |
| **阶段 5 批 5** | 模块六：本地数据面板 + 关于页 + 一键重置 | 实测含自查缺陷（报错记录 9）修复复验；提交 `2726566` |
| **阶段 5 批 6 + 验收** | 收尾：构建终测 + 文档同步 + 交付验收清单；**作者人工验收通过** | `npm run build` **270ms**；收尾文档提交 `65efbe0`；14 条清单全通过 |

**关键决策记录（阶段 5 新增，作者已确认）**

| 编号 | 决策 |
|---|---|
| AH | 阶段 6 定位：AI 建议改为"全链路回归 + 异常 / 空态演练 + 契约逐条复核"——**仍待作者拍板** |
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

---

## 三、待确认 / 待执行

1. **阶段 6 开工方式**：AI 先出分批方案 + 决策点，作者确认后开工（沿用每批停下等确认的节奏）；
2. **阶段 6 的定位**（**待作者拍板**）：原定为"前后端对接（真实数据替换 mock）"，但阶段 4 起已直接联调真实后端（决策 R），建议改为"全链路回归 + 异常 / 空态演练 + 契约逐条复核"；
3. **可选未做项**（契约标为阶段 8 可选项）：SQLite WAL 模式、`view_count` 计数、`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`（上下篇）、标签管理接口；
4. **阶段 7 排期项**：阅读进度条、回到顶部、无限滚动 + 骨架屏（决策 S 的既定安排）、窄屏目录折叠入口。

---

## 四、阶段 5 逐批结果（作者已确认批 0–批 5，并已整体验收通过）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 + 文档更正 + 正文配图 | `npm run build` 123 模块 / 216ms；后端两次启动幂等复核（`contentLen=578`、配图命中 1）；真实种子内容管线断言 9 项全绿；探针页验证后删除 |
| 1 ✅ | 存储层 + 评论 / 点赞接入层 | **25 项真实用例 25/25**（`tags` 数组 AND=2 / OR=5、`keyword` trim=3、`size=999→40002`、空昵称 / 非法邮箱字段级 40001、归属不符 40004、点赞两端幂等、`clearAll → removed=3`） |
| 2 ✅ | 模块四 搜索与分类过滤 | 8 项浏览器实测（防抖 → `?keyword=CSS`、or 语义 5 篇、空态清除筛选、卡片标签跳转、`?page=99 → ?page=2`） |
| 3 ✅ | 模块五 A 评论区 | 8 项浏览器实测（字段级校验、归属删除真删、加载更多 11 条、他人评论无删除入口） |
| 4 ✅ | 模块五 B 点赞 + Toast | 7 项浏览器实测（点赞 / 取消 / 刷新保持 / 异常 Toast 108ms 捕获 / 暗色） |
| 5 ✅ | 模块六 本地数据 + 一键重置 | 面板概览与真实数据一致；重置 Toast「清理 5 项」+ 主题回跟随系统；自查缺陷（报错记录 9）修复并复验；遗留点赞记录用临时 JDBC 清理 |
| 6 ✅ | 收尾 + **作者人工验收** | 构建终测 **270ms**；四份文档同步；14 条验收清单**逐条通过** |

---

## 五、遗留问题

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1–7 | ~~已解决的历史项~~ | — | 后端首次启动、JDK 26 兼容性、依赖下载慢、契约与模型草案、路径含空格、`frontend/public/` 缺失、种子仅 3 篇 |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、`README.md`、Swagger 描述、关于页中标注 |
| 9 | WAL 未启用（`busy_timeout=5000` 已生效） | 并发写场景收益有限 | 需要时在 `connection-init-sql` 追加 `PRAGMA journal_mode = WAL` |
| 10 | 结束后台任务会留下派生进程（JVM / node） | 端口占用 / 前端代理 502 | 已流程化：`netstat` 取 PID + `taskkill //PID <pid> //F`；阶段 5 每批收尾均执行并记录 |
| 11–15 | ~~已解决的历史项~~ | — | native-access 警告、外键约束验证、过渡观感与触摸点按（作者已验收）、tag ID 冲突 |
| 16 | **前端暂无自动化回归**（靠临时页 + 人工浏览器验证，页均删除） | 每批仍需人工跑一遍 | 阶段 5 继续沿用；如阶段 9 有余量再考虑固化成脚本 |
| 17 | ~~`?page` 越界不回退~~ | — | **已修复（阶段 5 批 2）**：越界自动 `replace` 到最后一页 |
| 18 | 骨架屏**出现时机**未能真机抓拍（本地请求约 10ms） | 只能证明外观正确 | 阶段 7 做无限滚动时一并观察 |
| 19 | 详情页**没有上一篇 / 下一篇**（契约 `prev` / `next` 恒为 `null`） | 少一条浏览路径 | 阶段 8 实现 `adjacent` 时一并渲染 |
| 20 | 详情页 chunk 297.51 kB（gzip 111.03 kB，含 markdown-it + hljs + dompurify + 评论 / 点赞逻辑） | 首次进详情页多下 ~111 kB（gzip） | 预期取舍（决策 W / AI）；余量允许时可改 `hljs/lib/core` + 按需注册语言 |
| 21 | ~~Markdown 正文图片懒加载无真实内容可验证~~ | — | **已闭环（阶段 5 批 0）**：第 6 篇补本地 SVG 配图，肉眼 + 断言双复验 |
| 22 | 目录只在桌面显示，窄屏没有可展开的入口 | 手机上无法跳转章节 | 决策 Y 的既定取舍；阶段 7 体验迭代可加"折叠式目录按钮" |
| 23 | 未实现「阅读进度条 / 回到顶部」 | 详情页少两项体验增强 | 阶段索引已放在**阶段 7（功能迭代一）** |
| 24 | **Toast 视觉截图未留档**（自动截图两次都晚于 2.6s 自动消失窗口） | 只有文本证据，无截图 | 需要时用"长驻 Toast（`duration: 0`）"补拍一张 |
| 25 | **重置本地数据后，旧 `visitorId` 的点赞记录无法用接口删除** | 该赞仍计入总数，且无法自助撤销 | 演示级语义（关于页已说明，作者验收时已确认）；测试期如需清理只能直接操作数据库（阶段 5 已用临时 JDBC 程序处理过一次） |
| 26 | 内置浏览器**合成点击偶发不送达**（阶段 5 在点赞按钮上复现 1 次，重试成功） | 验证需重试一次 | 工具限制；验证交互时优先键盘路径或重试 |
| 27 | 阶段 5 新增的 `CommentSection` / `LikeButton` 均随详情页 chunk 加载 | 详情页首屏体积再增 | 与第 20 条同一取舍；如需要可把评论区改为进入视口再懒加载 |

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK / Maven | `java 26.0.2.1`（已验证可跑 Spring Boot 4.1.1）；Maven 未安装，用 `mvnw`（wrapper 3.3.4 / Maven 3.9.16 已缓存） |
| Node / npm | `node v24.21.0`、`npm 11.19.0` |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9（**阶段 5 未新增任何依赖**） |
| 后端依赖 | Spring Boot 4.1.1（`spring-boot-starter-webmvc`）、Spring 7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc 3.1.1、Jackson 3 |
| 数据库 | `backend/data/blog.db`；6 张表 + 7 个索引；`article=12`、`tag=8`、`article_tag=23`、`comment=0`、`like_record=0`（阶段 5 测试数据已全部还原）；`busy_timeout=5000` |
| 前端静态资源 | `frontend/public/favicon.svg` + `images/covers/*.svg`（12 张）+ `images/articles/markdown-pipeline.svg`（1 张正文配图） |
| 服务状态 | **后端与前端 dev server 均已停止**（作者验收完成后复查 `5173/8080 均已释放`） |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达 |
| Git 仓库 | 分支 `main`；阶段 5 提交链：`d6167ea` → `3a4a7c7` → `b12bcd4` → `bf78030` → `5979b18` → `a8d499f` → `2726566` → `65efbe0`（阶段收尾文档）→ 本次「验收通过」记录提交（见 `git log` 最新一条） |
| 前端构建基线 | 阶段 5 收尾实测：`npm run build` → `✓ built in 270ms`；`index-*.js 54.22 kB / gzip 21.86 kB`、`_plugin-vue_export-helper-*.js 63.56 kB`、详情 chunk `297.51 kB / gzip 111.03 kB`、`AboutView 4.90 kB`、`ArticlesView 8.80 kB`、`index css 10.18 kB`、`ArticleDetailView css 11.85 kB`；各视图独立 chunk |

---

## 七、后端接口清单（阶段 2 产物 · 契约 v1.0）

**已实现并实测通过（13 个操作）**：`GET /api/health`；`GET /api/articles`（分页 / `keyword` / `tags`+`tagMode` / `status`）；`GET /api/articles/{id}`；`POST /api/articles`；`PUT /api/articles/{id}`；`DELETE /api/articles/{id}`；`GET /api/tags`；`GET` / `POST /api/articles/{id}/comments`；`DELETE /api/comments/{id}`；`GET` / `POST` / `DELETE /api/articles/{id}/likes`。

**未实现（契约标为阶段 8 可选项）**：`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理 `POST` / `PUT` / `DELETE /api/tags`、`view_count` 计数。

**统一约定**：响应 `{code, message, data}`；错误码 `0` / `40001` / `40002` / `40004` / `40009` / `50000` / `50001`；时间格式 `yyyy-MM-dd'T'HH:mm:ss`；分页 `page` 从 1 起、`size` 1–20（默认 10）。Swagger：5 个分组、13 条接口摘要。

**阶段 5 新增的前端调用**：`src/api/comments.js`（列表 / 新增 / 删除）、`src/api/likes.js`（状态 / 点赞 / 取消）—— 全部走真实后端，无 mock。

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

访问地址：前端 http://localhost:5173 ｜ 后端 http://localhost:8080 ｜ Swagger UI http://localhost:8080/swagger-ui/index.html ｜ API 前缀 `/api`。

> 贴士：① 先起后端（等控制台出现 `Started BlogApplication`）再起前端，否则首屏请求会经代理拿到 502；② 后端只在运行期间可访问 `/api/*` 与 Swagger；③ 停服后仍有进程占端口：`netstat -ano | grep ":8080 " | grep -i listening` 取 PID 后 `taskkill //PID <pid> //F`；④ 数据库重置＝停服 → 删 `backend/data/blog.db`（含 `-wal` / `-shm`）→ 重启（丢数据，不可恢复）；⑤ 本地数据重置＝`/about` 页面内「重置本地数据」按钮；⑥ Git Bash 的 `curl` 传中文会按 GBK 编码，请用 `node -e` 的 `fetch` 或 Swagger UI。
>
> **自验入口**：`/articles`（搜索 / 标签筛选 / 分页 / 空态）、`/articles?keyword=SQLite&tags=前端&tagMode=or`（组合筛选深链）、`/articles?page=99`（越界回退）、`/articles/6`（点赞 + 评论区 + 文末正文配图）、`/about`（本地数据面板 + 一键重置）、`/no-such-page`（404）。
