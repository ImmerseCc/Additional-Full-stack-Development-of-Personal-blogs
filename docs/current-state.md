# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 3「前端模块一：全局导航与主题」进行中**——批 0（开工基线）、批 1（主题系统）、批 2（导航栏 + 路由目标 + 404）、批 3（移动端汉堡菜单 + 滚动样式变化）已完成，批 4 待做；阶段 0–2 全部完成并实测通过。

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 v1.0，**已确认**，实现时不得擅自改字段）。
2. **进度**：阶段 0、0.5、1、2 已完成；阶段 3「前端模块一：全局导航与主题」**批 0 已完成、批 1–4 待做**。后端 13 个操作已全部实现并实测（见第七节清单）；前端仍是骨架页（`App.vue` + `HomeView.vue`，`router/index.js` 只有 `/` 与兜底重定向）。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水）、`docs/collaboration-log.md`（关键提示词汇总 + 阶段索引 + 阶段记录）、`docs/debug-log.md`（报错专档，已有 5 条真实记录）；审计记录在 `docs/audit-report.md`（阶段 9 才做）。
4. **环境事实**：Node 24.21.0 / npm 11.19.0 已安装；JDK 26 **已验证**可跑 Spring Boot 4.1.1；Maven 未安装但 `mvnw` 可用（依赖已缓存，后端启动约 2 秒）；**后端当前处于停止状态**（阶段 2 收尾时已停并清理 JVM），需要时用第八节的标准命令启动。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题。
6. **本机已踩过的坑（细节都在 `docs/debug-log.md`）**：① Git Bash 里给 `curl` 传中文参数会被按 **GBK** 编码 → 改用预编码 UTF-8 百分号串，或用 Swagger UI / PowerShell `Invoke-RestMethod`；② `wc -m` 在本机按**字节**计数（要数中文长度得按码点算）；③ AI 的后台任务有 **10 分钟上限**，超时只杀包装进程、派生 JVM 会继续存活并占住 8080（用 `taskkill //PID <pid> //F` 清理）；④ 浏览器提示"拒绝连接"时，排查第一步永远是"确认服务是否真的在监听"。

---

## 一、当前阶段

**阶段 3「前端模块一：全局导航与主题」：进行中**（批 0 ✅、批 1 ✅、批 2 ✅、批 3 ✅；批 4 待做）
**上一阶段：阶段 2「后端业务实现」全部完成 ✅**（批 0 → 批 1 → 批 2a → 批 2b → 批 3 → 批 4；累计 52 项接口实测通过；作者已确认批 4）

阶段 3 分批（8 项决策见第二节「关键决策记录」K–Q，详细计划见第四节）：
批 0 开工基线 ✅ → 批 1 主题系统（Pinia 三态 + CSS 变量 + 首屏防闪）✅ → 批 2 导航栏 + 路由目标 + 404 页 ✅ → 批 3 移动端汉堡菜单 + 滚动样式变化（可访问性）✅ → 批 4 路由过渡 + 收尾（文档同步）。每批结束停下等作者确认后才提交。

---

## 二、已完成

| 时间点 | 事项 | 产物与证据 |
|---|---|---|
| 阶段 0 | 需求确认与技术选型（未写代码） | 选型：Vue 3 + Vite、原生 CSS 变量、Spring Boot 4.1.1 + JdbcTemplate + SQLite；0–9 阶段路线 |
| 阶段 0.5 | 协作日志规范确认 | `ai-log.md`（流水）+ `collaboration-log.md`（汇总），四套模板与禁止事项 |
| 阶段 1 批 1 | 根目录 3 个文件 | `README.md`、`.gitignore`、`AGENTS.md`；作者确认通过 |
| 阶段 1 批 2 | docs 文档骨架 + `git init` + Node 安装 | 9 个文件；分支 `main`；`node v24.21.0` / `npm 11.19.0` 实测 |
| 阶段 1 批 2 收尾 | 契约与数据模型确认 + `.gitattributes` + 首次提交 | 两份文档升级 v1.0 已确认；提交 `501065a` |
| 阶段 1 批 3 | frontend 12 个文件 | `npm install`（81 包）、`npm run build`（28 模块 / 104ms）、dev server 冒烟全部实测通过 |
| 报错修复 | 浏览器 `http://127.0.0.1:5173` 被拒绝 | 根因：Vite 只绑定 IPv6；修复：`vite.config.js` 加 `host: '127.0.0.1'`（报错记录 2） |
| 阶段 1 批 4 | backend 15 个文件 | `schema.sql`（5 张表）+ `data.sql`（3 篇种子）；`Started BlogApplication in 1.815 seconds`；6 张表 + 7 个索引 |
| 阶段 1 收尾 | 阶段记录与状态快照落盘 | `collaboration-log.md` 新增「五、阶段记录」；作者确认批 1–4 |
| 阶段 2 批 0 | 提交收尾 + 修正过时的 git 描述 | 提交 `e9ab336`（发现"第二次提交"其实已存在） |
| 阶段 2 批 1 | `common` + `model` + `config` 共 19 个文件 | `Compiling 20 source files`；启动 `2.098 seconds`；`GET /api/not-exist → 404 + 40004`；提交 `92a5cc6` |
| 阶段 2 批 2a | 文章读路径 8 个文件 | 13 项 curl 实测（时间格式带秒位 `2026-09-07T09:00:00`、方法不支持 → 40002）；提交 `b22592d` |
| 阶段 2 批 2b | 文章写路径（5 个文件修改） | 16 项实测；**外键级联删除真实生效**（遗留项 12 关闭）；重启后数据仍在；摘要 120 码点；提交 `5c210a3` |
| 阶段 2 批 3 | 评论 + 点赞 7 个文件 | 19 项实测（点赞 / 取消**两端幂等**、visitorId 归属校验、40001 / 40004）；提交 `4b1700b` |
| 阶段 2 批 4 | 收尾：Swagger 注解、异常兜底、`busy_timeout` | 5 个 tag 分组、13 条接口摘要、`/swagger-ui/index.html → 200`、异常兜底 4 项、`PRAGMA busy_timeout = 5000`；提交 `7174838` |

阶段 2 的真实报错共 **5 条**（1 环境类、2 前端配置类、3 与 5 使用类、4 工具 / 编码类），全部写入 `docs/debug-log.md`；其中第 4 条暴露的"查询串解码失败被兜底成 50000"属代码改进项，已在 `GlobalExceptionHandler` 修正为 40002。

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
| K | 阶段 3 菜单项锁定三项：首页 `/`、文章列表 `/articles`、关于 `/about`；后两者本阶段只做**占位页**（完整实现分别在阶段 4、阶段 6+） |
| L | 阶段 3 **新增 404 页**（`NotFoundView.vue`），替换当前"非匹配路径一律重定向首页"的兜底 |
| M | 主题切换采用**三态**：亮 / 暗 / 跟随系统（`blog:theme` 存 `light` / `dark` / `system`） |
| N | 主题按钮固定在导航栏右侧（桌面与移动端同一位置，不藏进汉堡菜单） |
| O | 关于页阶段 3 **不调用后端**，保持纯前端（前后端对接演练留阶段 6） |
| P | 阶段 3 不新增 `frontend/public/`，不新增 `src/` 子目录（复用逻辑放 `src/utils/`） |
| Q | 阶段 3 批 2（导航结构）/ 批 3（移动端交互）**不拆分**，沿用阶段 2 的"每批停下等确认"节奏 |

---

## 三、待确认 / 待执行

1. **阶段 3 进行中**：批 4 待做（见第四节计划）；每批结束停下等作者确认，确认后才提交；
2. **每个阶段结束后作者会新开会话**：新会话请按"第零节 新会话接手说明"操作；
3. **README 待同步项（批 4 一并处理）**：`README.md` 的「后端功能」状态列、第四节前端模块表、「已知问题与风险」第 2/6 条仍是阶段 1 的过时描述；
4. **需作者补验的点按路径**（批 3 遗留）：触摸点按开合菜单、遮罩点击关闭、点击链接后路由变化自动收起——内置浏览器在触摸仿真下合成点击不送达页面，只能由真机 / 真实浏览器验收；
5. **可选未做项**（不阻塞阶段 3）：SQLite WAL 模式（连接池为 1，收益有限）、`frontend/public/` 静态资源目录（阶段 4 需要封面图时再申请新增）、全量种子文章约 12 篇（阶段 4/5 补齐）。

---

## 四、阶段 3 分批计划（作者已确认 8 项决策）

| 批 | 内容 | 产物与验证 |
|---|---|---|
| 批 0 ✅ | 开工基线 | `README.md` 顶部状态块更正；本文件同步；`npm run build` 基线通过（28 模块 / 160ms）；`git status` 干净（`af2334c` → 提交 `8c5923d`） |
| 批 1 ✅ | 主题系统（三态） | 新建（完整）`src/stores/theme.js`、`src/components/ThemeToggle.vue`；修改（完整）`src/styles/base.css`、`src/App.vue`；删除 2 个 `.gitkeep`；`index.html` 核对通过未改。实测：`npm run build` → 31 模块 / 131ms；浏览器实测三态循环 + 刷新不丢（截图留档） |
| 批 2 ✅ | 导航栏 + 路由目标 + 404 | 新建（完整）`src/components/AppHeader.vue`、`src/views/NotFoundView.vue`；新建（占位）`src/views/ArticlesView.vue`、`src/views/AboutView.vue`；修改（完整）`src/router/index.js`（三条路由 + 404 + `document.title` 同步）、`src/App.vue`、`src/styles/base.css`（`.page`）。实测：`npm run build` → 39 模块 / 157ms 且各视图独立 chunk；浏览器实测四路径标题、当前页高亮、404 页、深色主题（截图留档） |
| 批 3 ✅ | 移动端汉堡菜单 + 滚动样式变化 | 修改（完整）`src/components/AppHeader.vue`（`aria-expanded` / `aria-controls`、Esc、路由切换自动收起、焦点进出、遮罩、滚动样式）、`src/styles/base.css`（`--color-bg-header`）。实测：`npm run build` → 169ms / CSS 6.10 kB；浏览器 375×667 实测折叠、键盘开启、Esc 关闭、滚动后页头半透明模糊（截图留档），桌面视口无回归。**点按路径需作者真机补验** |
| 批 4 | 路由过渡 + 收尾 | 改 `App.vue`（`<router-view v-slot>` + `<transition>`，尊重 `prefers-reduced-motion`）、`base.css`；可选 `AppFooter.vue`；同步 `README.md`（含第三节遗留项）、`docs/{current-state,ai-log,collaboration-log,debug-log}.md`；`npm run build` + 三档自测 |

**验收要点**：导航栏（含滚动样式变化）、汉堡菜单（含键盘可达）、主题三态 + 首屏防闪、当前页高亮、路由过渡。作者用浏览器验证 375px / 768px / 1440px 三档布局与主题切换后给出结论（AI 不代签）。

---

## 五、遗留问题

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1 | ~~后端首次启动结果未知~~ **已解决** | — | 阶段 1 批 4 实测：启动、建表、种子数据、接口全部正常 |
| 2 | ~~JDK 26 兼容性未实测~~ **已解决** | — | 实测可用：`Starting BlogApplication using Java 26.0.2.1` |
| 3 | 依赖下载慢（首次约 10 分钟量级） | 影响验证耗时 | 依赖已缓存，后续启动 2 秒级 |
| 4 | ~~接口契约与数据模型是草案~~ **已解决** | — | v1.0 已确认，阶段 2 全部按契约实现 |
| 5 | 项目目录路径含空格 | 目前低风险 | 出现工具异常时再评估迁移 |
| 6 | `frontend/public/`（封面图等静态资源目录）未创建 | 阶段 4 文章封面暂无本地图片 | 需要时向作者申请新增；当前种子文章 `cover_url` 均为 NULL，前端可用渐变占位 |
| 7 | 种子文章仅 3 篇 | 演示内容偏少 | 全量约 12 篇在阶段 4/5 补齐 |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、`README.md`、Swagger 描述中标注 |
| 9 | ~~SQLite 的 `busy_timeout` 未设置~~ **部分解决** | — | `busy_timeout=5000` 已随 JDBC URL 生效（批 4 实测 `PRAGMA busy_timeout = 5000`）；**WAL 仍未启用**，需要时在 `connection-init-sql` 追加 `PRAGMA journal_mode = WAL` |
| 10 | 结束后台任务会留下派生 JVM；后台任务还有 10 分钟上限 | 下次启动报端口占用 | 已实测（超时只杀包装进程、JVM 存活）；停后端用 `TaskStop` 或 `taskkill //PID <pid> //F`；已写入 `debug-log.md` 观察项 |
| 11 | ~~JDK 26 提示 native-access 警告~~ **已处理** | — | 结论：仅警告，不写进标准启动命令；`README.md`《常见问题排查》已说明 |
| 12 | ~~外键约束是否在应用连接上真正打开未验证~~ **已解决** | — | 批 2b 实测：删除文章后 `comment` / `like_record` / `article_tag` 计数全部归 0 |

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK | `java 26.0.2.1`，`JAVA_HOME=D:\Java\jdk-26.0.2.1`；**已验证可运行 Spring Boot 4.1.1** |
| Maven | 本机未安装；项目使用 `mvnw`（wrapper 3.3.4 / only-script / Maven 3.9.16 已下载到 `.m2`） |
| Node / npm | `node v24.21.0`、`npm 11.19.0` |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9 |
| 后端依赖 | Spring Boot 4.1.1（Web 起步依赖为 **spring-boot-starter-webmvc**）、Spring 7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc-openapi-starter-webmvc-ui 3.1.1、**Jackson 3**（`tools.jackson.core:jackson-databind:3.1.5`） |
| 数据库 | `backend/data/blog.db`；6 张表 + 7 个索引；种子数据 `article=3`、`tag=4`（另有阶段 2 实测产生的「级联测试标签」，计数 0）、`article_tag=5`；JDBC URL 已带 `busy_timeout=5000` |
| 后端状态 | **当前已停止**（阶段 2 收尾时停服并清理 JVM，`8080 已释放`） |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达（报错记录 1） |
| Git 仓库 | 分支 `main`；提交链：`501065a` → `25e280e` → `e9ab336` → `92a5cc6` → `b22592d` → `5c210a3` → `4b1700b` → `7174838`（阶段 2 批 4）→ `af2334c`（阶段 2 收尾文档）；工作区干净 |
| 前端构建基线 | 阶段 3 批 0 实测：`npm run build` → `28 modules transformed` / `built in 160ms`（dist 产物：index 1.30 kB、index js 89.01 kB / gzip 34.74 kB） |

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

> 贴士：① 路径含空格，`cd` 时**必须加引号**；② 后端只在**运行期间**才能访问 `/api/*` 与 Swagger UI（报错记录 5 的教训）；③ 停后端后如仍有进程占 8080：`netstat -ano | grep ":8080" | grep -i listening` 查到 PID 后 `taskkill //PID <pid> //F`；④ 数据库重置＝停服 → 删除 `backend/data/blog.db`、`blog.db-wal`、`blog.db-shm` → 重启（会丢数据，属不可恢复操作）。
