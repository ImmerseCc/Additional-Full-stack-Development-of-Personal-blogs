# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 6「全链路回归 + 异常 / 空态演练 + 契约逐条复核」已全部完成（批 0–批 5）**，**等待作者按第一节的 13 条清单人工验收**。阶段 0–5 亦全部完成并入库，其中**阶段 5 已经作者人工验收通过（14 条清单逐条确认）**。
> **收尾状态**：前后端均已停服，端口 5173 / 8080 已释放；数据库为种子状态（`article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`）。

---

## 一、当前阶段与**人工验收清单**

**阶段 6 已完成并交付验收**。前三批（0–3）以验证为主，**批 4 有一次业务代码改动**（`frontend/src/main.js` 全局错误兜底），清单已标注。

| # | 验收项 | 怎么验 | 期望结果 |
|---|---|---|---|
| 1 | **能按 README 启动** | 第八节两条命令（先后端后前端） | 后端控制台出现 `Started BlogApplication`；前端 `Local: http://127.0.0.1:5173/`；首页显示文章卡片 |
| 2 | **接口回归脚本** | `cd frontend && npm run smoke` | 末行 `全部通过：97/97 项断言`；跑完数据库回到种子状态 |
| 3 | **契约逐条复核结论** | 打开 `docs/audit-report.md` →「阶段 6 预审计」 | 17 条接口：13 条一致、4 条阶段 8 可选项；`40009` 标注为"当前不可达" |
| 4 | **六模块正常路径**（批 2，截图 01–06） | `/articles`（搜索 `SQLite`、标签 `前端`+`Vue` 与「任一即可」、空态清除筛选）；`/articles/6`（目录、正文配图）；`/articles/1`（代码高亮）；详情页发一条评论再删除；点赞再取消；`/about` 一键重置 | 与 `docs/demo/stage6-01…06-*.png` 一致 |
| 5 | **异常与边界**（批 3，截图 07–11） | `/no-such-page`、`/articles/99999`、`/articles/abc`、`/articles?tags=不存在的标签` | 分别是 404 页、详情 404、非法 ID 错误态、空态 |
| 6 | **后端停服降级** | 打开 `/articles` 后停掉后端（`taskkill`），再刷新或点赞 | 列表/详情整页错误态 + 重试；点赞出现右下角 Toast；重启后端后 4 秒内恢复 |
| 7 | **XSS 防护**（截图 11） | 看 `docs/demo/stage6-11-xss-as-text.png` | `<script>` / `<img onerror>` / `javascript:` 链接均为**纯文本**，无执行 |
| 8 | **本次代码改动：全局错误兜底**（批 4） | 打开站点 → F12 → Console 执行 `Promise.reject(new Error('test'))` | 右下角出现红色 Toast「页面出现未预期的异常，请刷新或稍后重试」 |
| 9 | **响应式**（截图 12、13） | 浏览器缩放到 375px / 768px / 1920px | 375：汉堡菜单 + 标签折叠 + 单列；768：桌面导航 + 标签展开 + 双列；1920：内容居中 + 详情目录在右 |
| 10 | **键盘可达性**（截图 14） | 页面按 Tab | 焦点环清晰可见并随焦点移动 |
| 11 | **暗色主题**（截图 04） | 点右上角主题按钮切到暗色 | 正文/次要文字/代码高亮可读；按钮文案随状态变化 |
| 12 | **构建体积** | `cd frontend && npm run build` | 成功，约 200–260ms；`index-*.js` ≈ **54.5 kB**、详情 chunk ≈ 297.5 kB |
| 13 | **测试数据未污染** | 首页显示「共 12 篇文章」；或 `/api/articles?status=ALL` | `article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0` |

> **验收结论请由您给出**（AGENTS.md 协作规则 7：AI 不代签）。若某项不通过，请把现象与复现步骤发我，我按"报错先记录再修复"流程处理。

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
| 阶段 6 批 1 | 契约逐条复核 + 接口回归脚本 | `npm run smoke` **97/97**；17 条接口 13 条一致 / 4 条阶段 8 可选项；**发现 `40009` 不可达**；审计报告新增预审计；提交 `2c5b301` |
| 阶段 6 批 2 | 正常路径全链路回归（六模块） | 6 张截图；**作者已通过**；提交 `cbe44cf` |
| 阶段 6 批 3 | 异常 / 边界 / 空态演练 | XSS / 注入 / 停服降级 / 404 与 URL 篡改；5 张截图；数据全部还原；提交 `84b5cbc` |
| 阶段 6 批 4 | 非功能复核 + **决策 AX 代码落地** | 全局错误兜底（探针两条路径 6ms / 5ms 命中后删除）；375 / 768 / 1920；对比度量化；键盘焦点；构建 234ms；4 张截图；提交 `9f8d775` |
| **阶段 6 批 5** | 收尾：终测 + 遗留收口 + 文档同步 + 停服 | `npm run build` **207ms**、`npm run smoke` **97/97**；数据终核 `12 / 8 / 23 / 0 / 0`；**5173 / 8080 均已释放**；交付 13 条验收清单 |

**关键决策记录（阶段 5 / 6，作者已确认）**

| 编号 | 决策 |
|---|---|
| AH | 阶段 6 定位：全链路回归 + 异常 / 空态演练 + 契约逐条复核（替代原"前后端对接 / 替换 mock"） |
| AI–AT | 阶段 5 的 13 条决策（不新增依赖 / 筛选同步地址栏 / 卡片标签可点 / 空结果插画 / 评论加载更多 / 归属账本 / 点赞以后端为准 / Toast 规格 / 重置入口 / 修复遗留 17·21 / 契约补注） |
| AU | 阶段 6 定位（同 AH，作者拍板） |
| AV | 阶段 6 以**验证 + 文档**为主，代码类加固单列 |
| AW | `frontend/scripts/smoke.mjs` 固化接口层回归 —— **批 1 落地，97/97 通过** |
| AX | 前端全局错误兜底（`app.config.errorHandler` + `unhandledrejection` → Toast）—— **批 4 落地并实测** |
| AY | 复核结论写入 `docs/audit-report.md` 的「阶段 6 预审计」—— **批 1 / 3 / 4 三块已落地** |
| AZ | 详情页 chunk 拆包留阶段 7；本阶段只测量（实测 297.51 kB / gzip 111.03 kB） |
| BA | Toast 截图留档 —— **批 2 / 批 3 落地**（成功 Toast + 异常 Toast） |
| BB | **不**给启动命令追加 `--enable-native-access` |
| BC | 批 0 不重置数据库；演练数据在批 5 前还原（批 3 已按此清理） |
| BD | 沿用"每批停下等确认" |
| BE | **`40009` 留到阶段 8 做标签管理接口时启用**；本阶段不改契约 / 代码 |

---

## 三、待确认 / 待执行

1. **阶段 6 验收**：作者按第一节 13 条清单验收（当前进度）；
2. **阶段 7 开工前的待办**（AI 建议的下一阶段范围，**待作者拍板**）：阅读进度条、回到顶部、无限滚动 + 骨架屏（决策 S 的既定安排）、窄屏目录折叠入口、详情页 chunk 拆包（决策 AZ）、遗留 29（评论「已发表」提示）与遗留 30（亮色主色对比度）的体验修正；
3. **可选未做项**（契约标为阶段 8 可选项）：SQLite WAL 模式、`view_count` 计数、`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`（上下篇）、标签管理接口（含启用 `40009`）；
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

### 阶段 6（批 0–批 5，全部完成 · 等待作者验收）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 | 后端 1.544s / 前端 211ms；构建 **151 模块 / 241ms**；更正表 / 索引计数 |
| 1 ✅ | 契约逐条复核 + 接口回归脚本 | `npm run smoke` **97/97**；17 条接口 13 条一致 / 4 条阶段 8 可选项；`40009` 不可达 |
| 2 ✅ | 正常路径全链路回归（六模块） | 六模块全绿 + **6 张截图**；**作者已通过** |
| 3 ✅ | 异常 / 边界 / 空态演练 | XSS 零执行；注入 + 通配符安全；停服降级三态 + 4 秒恢复；5 张截图；数据还原 |
| 4 ✅ | 非功能复核 + 全局错误兜底 | 375 / 768 / 1920；对比度量化；键盘焦点；构建 234ms / `index` 54.53 kB；兜底 6ms / 5ms 命中；4 张截图 |
| 5 ✅ | 收尾 | 构建 **207ms**、`smoke` **97/97**；数据终核；文档同步；**停服并释放端口**；13 条验收清单 |

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
| 18 | 骨架屏**出现时机**未能真机抓拍 | 只能证明外观正确 | 阶段 7 做无限滚动时一并观察 |
| 19 | 详情页**没有上一篇 / 下一篇** | 少一条浏览路径 | 阶段 8 实现 `adjacent` 时一并渲染 |
| 20 | 详情页 chunk 297.51 kB（gzip 111.03 kB） | 首次进详情页多下 ~111 kB | **阶段 7** 拆包（可改 `hljs/lib/core` + 按需注册语言） |
| 21 | ~~正文图片懒加载无真实内容可验证~~ | — | **阶段 6 批 2 肉眼复验通过** |
| 22 | 目录只在桌面显示，窄屏没有可展开入口 | 手机上无法跳转章节 | 决策 Y 的既定取舍；阶段 7 可加"折叠式目录按钮" |
| 23 | 未实现「阅读进度条 / 回到顶部」 | 详情页少两项体验增强 | **阶段 7** |
| 24 | ~~Toast 视觉截图未留档~~ | — | **阶段 6 批 2 / 批 3 已留档**（成功 + 异常各一张） |
| 25 | 重置本地数据后旧 `visitorId` 的点赞无法用接口删除 | 该赞仍计入总数 | 演示级语义（关于页已说明）；清理只能直接操作数据库 |
| 26 | 内置浏览器合成点击偶发不送达 | 验证需改用键盘 | 已流程化"优先键盘路径"（工具限制，非项目缺陷） |
| 27 | `CommentSection` / `LikeButton` 随详情页 chunk 加载 | 详情页首屏体积再增 | 与第 20 条同一取舍（阶段 7 一并评估） |
| 28 | ~~`40009` 不可达~~ | — | **已拍板（决策 BE）**：阶段 8 启用 |
| 29 | 「评论已发表」提示在评论被删除后仍显示 | 轻微体验瑕疵 | **待作者判断**；若需修正，可在删除成功后一并清掉（阶段 7） |
| 30 | **亮色主色 `#3b6ef5` 对白底对比度 4.44，略低于 WCAG AA 的 4.5**（链接与主按钮白字同值） | 轻微可读性风险（大字号不受限） | 建议**阶段 7** 改为 `#3563e0`(5.23) 或 `#2f5ed6`(5.69)，并复核链接 / 主按钮 / 焦点环观感 |

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
| 服务状态 | **前后端均已停止**：收尾时 `taskkill` 停掉后端（PID 40772）与前端 dev server（PID 2272），**5173 / 8080 均已释放**（`curl` 均返回 000） |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达 |
| Git 仓库 | 分支 `main`；阶段 6 提交链：`bad0853`（批 0）→ `ce87b7b` → `2c5b301`（批 1）→ `cbe44cf`（批 2）→ `84b5cbc`（批 3）→ `9f8d775`（批 4）；**批 5 文档提交见 `git log` 最新一条** |
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
