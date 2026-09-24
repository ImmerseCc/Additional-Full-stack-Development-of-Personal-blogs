# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：阶段 2 批 4（收尾）完成 —— Swagger 注解与 info 元信息、异常兜底 4 项复测、`busy_timeout=5000` 生效核对；**阶段 2（批 0–4）全部完成**，待作者确认；改动尚未 git 提交。

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 v1.0，**已确认**，实现时不得擅自改字段）。
2. **进度**：阶段 1（目录骨架）已完成并全部实测通过；**当前应从阶段 2「后端业务实现」开始**。不要重复创建已存在的骨架文件。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水）、`docs/collaboration-log.md`（关键提示词汇总 + 阶段索引 + 阶段记录）、`docs/debug-log.md`（报错专档）；审计记录在 `docs/audit-report.md`。
4. **环境事实**：Node 24.21.0 / npm 11.19.0 已安装；JDK 26 **已验证**可跑 Spring Boot 4.1.1；Maven 未安装但 `mvnw` 可用（依赖已缓存于 `.m2`，后端启动约 2 秒）。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题。

---

## 一、当前阶段

**阶段 1「目录结构与占位文件」：全部完成 ✅**（批 1 → 批 2 → 批 3 → 批 4，作者均已确认通过）
**阶段 2「后端业务实现」：全部完成 ✅**（批 0 → 批 1 → 批 2a → 批 2b → 批 3 → 批 4，累计 52 项接口实测，待作者确认）
**下一阶段：阶段 3「前端模块一：全局导航与主题」——尚未开始。**

阶段 2 分批（作者已确认，其中"原批 2"按作者要求拆成 2a / 2b）：
批 0 提交收尾 ✅ → 批 1 `common` + `model` + `config` ✅ → 批 2a 文章读路径 ✅ → 批 2b 文章写路径（含级联删除实测）✅ → 批 3 评论 + 点赞 ✅ → 批 4 收尾（Swagger 注解、异常兜底、`busy_timeout`）✅。每批结束停下等作者确认。

---

## 二、已完成

| 时间点 | 事项 | 产物与证据 |
|---|---|---|
| 阶段 0 | 需求确认与技术选型（未写代码） | 选型：Vue 3 + Vite、原生 CSS 变量、Spring Boot 4.1.1 + JdbcTemplate + SQLite；0–9 阶段路线 |
| 阶段 0.5 | 协作日志规范确认 | `docs/ai-log.md`（流水）+ `docs/collaboration-log.md`（汇总），四套模板与禁止事项 |
| 阶段 1 批 1 | 根目录 3 个文件 | `README.md`、`.gitignore`、`AGENTS.md`；作者确认通过 |
| 阶段 1 批 2 | docs 文档骨架 + `git init` + Node 安装 | 9 个文件；`git init`（分支 main）；`node v24.21.0` / `npm 11.19.0` 实测 |
| 阶段 1 批 2 收尾 | 契约与数据模型确认 + `.gitattributes` + 首次提交 | 两份文档升级为 v1.0 已确认；提交 `501065a` |
| 阶段 1 批 3 | frontend 12 个文件 | `npm install`（81 包）、`npm run build`（28 模块 / 104ms）、dev server 冒烟（HTTP 200 + SFC 编译）全部实测通过；作者确认通过 |
| 报错修复 | 浏览器 `127.0.0.1` 被拒绝 | 根因：Vite 只绑定 IPv6；修复：`vite.config.js` 加 `host: '127.0.0.1'`；实测 127.0.0.1 与 localhost 均 200 |
| 阶段 1 批 4 | backend 15 个文件 | `pom.xml`、`BlogApplication.java`、`application.yml`、`schema.sql`（5 张表）、`data.sql`（3 篇文章 + 4 标签）、`mvnw` 三件套、7 个 `.gitkeep` |
| 阶段 1 批 4 验证 | 后端启动与数据链路实测 | `Java 26.0.2.1` 上 `Spring Boot v4.1.1 / Spring v7.0.9 / Tomcat 11.0.24` 启动成功（1.815 秒）；`backend/data/blog.db` 生成；JDBC 查询确认 6 张表 + 7 个索引；种子数据 article=3、tag=4、article_tag=5，**两次启动后数量不变**（幂等 + 持久化）；`/v3/api-docs` 与 `/swagger-ui/index.html` 均 200 |
| 阶段 1 收尾 | 作者确认批 4 通过；阶段记录与状态快照落盘 | `docs/collaboration-log.md` 新增「五、阶段记录」；本文件更新至阶段 2 开工前状态 |
| 阶段 2 批 0 | 提交收尾 + git 状态描述修正 | 核对 git 现状（发现"第二次提交"其实已存在）→ 修正两处过时描述 → 提交 `e9ab336` |
| 阶段 2 批 1 | `common` + `model` + `config` 共 19 个文件（另删 2 个 `.gitkeep`） | `./mvnw -B -ntp compile` 通过（20 源文件）；启动 `Started BlogApplication in 2.098 seconds`；`GET /api/not-exist` → `404 + 40004`；作者侧复验通过；提交 `92a5cc6` |
| 阶段 2 批 2a | 文章读路径：8 个文件（另删 3 个 `.gitkeep`） | 13 项 curl 实测全部通过；批 1 遗留的时间格式（`createdAt":"2026-09-07T09:00:00"`）与方法不支持分支验证通过；处理报错记录 4（`InvalidParameterException → 40002`） |

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

---

## 三、待确认 / 待执行

1. **阶段 2 批 4（收尾）已完成（待作者确认）**：新增 `config/OpenApiConfig.java`、5 个 controller 补 `@Tag` / `@Operation` / `@Parameter`、JDBC URL 追加 `busy_timeout=5000`、`README.md` 补一行排查；实测 `/v3/api-docs` 含 5 个分组与 **13 条接口摘要**、`/swagger-ui/index.html → 200`、异常兜底 4 项正确、`PRAGMA busy_timeout = 5000` 生效；**阶段 2（批 0–4）至此全部完成**，下一步是**阶段 3「前端模块一：全局导航与主题」**；本批改动**尚未 git 提交**；
2. **Git 提交链**：`501065a`（批 1 + 批 2 骨架）→ `25e280e`（frontend / backend 骨架 + Vite IPv4 修复）→ `e9ab336`（阶段 1 收尾文档 + 修正 git 状态描述）→ `92a5cc6`（阶段 2 批 1：common / model / config）→ `b22592d`（阶段 2 批 2a：文章读路径）→ `5c210a3`（阶段 2 批 2b：文章写路径）→ `4b1700b`（阶段 2 批 3：评论与点赞）；
3. **每个阶段结束后作者会新开会话**：新会话请按"第零节 新会话接手说明"操作；
4. ~~`docs/ai-log.md` 缺少批 3、批 4 与报错修复的逐轮记录~~ **已补齐**：应作者要求补记批 3 / 批 4 阶段记录（标为【补记】，来源为 `docs/collaboration-log.md` 阶段 1 已记录内容），并把报错记录 2、3 同步进 `docs/ai-log.md`；

---

## 四、下一步（阶段 2）

| 层 / 内容 | 计划 |
|---|---|
| `common` | `ApiResponse`（统一返回 `{code,message,data}`）、`ErrorCode`（0 / 40001 / 40002 / 40004 / 40009 / 50000 / 50001）、`BizException`、`GlobalExceptionHandler` |
| `model` | 实体（Article / Tag / Comment / LikeRecord）、请求 DTO（带 Bean Validation 注解）、响应 VO（ArticleSummary / ArticleDetail / CommentVO / TagVO / LikeStateVO / PageVO） |
| `repository` | JdbcTemplate + 手写 SQL；含分页、关键词、标签多选（`article_tag` 联表）、计数子查询 |
| `service` | 业务规则与事务：文章 CRUD、标签自动创建/清空、评论归属校验（visitorId）、点赞幂等 |
| `controller` | 按契约实现：`GET /api/health`、文章列表（分页/关键词/标签/状态）、详情、创建、修改、删除、`GET /api/tags`、评论列表/创建/删除、点赞查询/点赞/取消；补 springdoc 注解 |
| 验证方式 | 每个接口给可复制的 `curl` 命令；执行"创建 → 列表 → 详情 → 修改 → 删除 → 重启后查询"完整链路；重点实测**外键级联删除**是否真的生效 |

---

## 五、遗留问题

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1 | ~~后端首次启动结果未知~~ **已解决** | — | 已实测：启动成功、建表成功、种子数据写入成功、接口 200 |
| 2 | ~~JDK 26 兼容性未实测~~ **已解决** | — | 实测可用：`Starting BlogApplication using Java 26.0.2.1`；无需改装 Temurin 21 |
| 3 | 依赖下载慢（首次约 10 分钟量级） | 影响验证耗时 | 依赖已缓存到 `.m2` 与 `frontend/node_modules`，后续启动 2 秒级 |
| 4 | 接口契约与数据模型已确认（v1.0） | — | 阶段 2 按契约实现 |
| 5 | 项目目录路径含空格 | 目前低风险 | 出现工具异常时再评估迁移 |
| 6 | `frontend/public/`（封面图等静态资源目录）未创建 | 阶段 4 文章封面暂无本地图片 | 需要时向作者申请新增；当前种子文章 `cover_url` 均为 NULL，前端可用渐变占位 |
| 7 | 种子文章仅 3 篇 | 演示内容偏少 | 全量约 12 篇在阶段 4/5 补齐 |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、README 中明确标注 |
| 9 | ~~SQLite 的 `busy_timeout` 与 WAL 未设置~~ **部分解决** | — | `busy_timeout=5000` 已通过 JDBC URL 设置并**实测生效**（阶段 2 批 4：`PRAGMA busy_timeout = 5000`）；**WAL 仍未启用**——连接池为 1、单写者场景下收益有限，暂不做，需要时可在 `connection-init-sql` 追加 `PRAGMA journal_mode = WAL` |
| 10 | 结束后台任务会留下派生的 JVM 孤儿进程占住 8080 | 下次启动报端口占用 | 已实测并清理（`taskkill //PID`）；另注意：**后台任务有 10 分钟上限**，超时只杀包装进程、派生 JVM 仍存活（阶段 2 批 3 实测）；后续停后端请用 `TaskStop` 或按 PID 结束 |
| 11 | ~~JDK 26 提示 `WARNING: A restricted method in java.lang.System has been called`~~ **已处理** | — | 结论：**仅是警告**，不写进标准启动命令（保持命令简洁）；已在 `README.md`《常见问题排查》补一行说明，并给出可选参数 `--enable-native-access=ALL-UNNAMED` |
| 12 | ~~外键约束是否在应用连接上真正打开未验证~~ **已解决** | — | 阶段 2 批 2b 实测：给文章直接写入 1 条评论 + 1 条点赞后调用 `DELETE /api/articles/4` → `comment`、`like_record`、`article_tag` 计数全部归 0，`ON DELETE CASCADE` **真实生效**（Hikari 的 `connection-init-sql: PRAGMA foreign_keys = ON` 有效） |

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK | `java 26.0.2.1`，`JAVA_HOME=D:\Java\jdk-26.0.2.1`；**已验证可运行 Spring Boot 4.1.1** |
| Maven | 本机未安装；项目使用 `mvnw`（wrapper 3.3.4 / only-script / 已自动下载 Maven 3.9.16 到 `.m2`） |
| Node / npm | `node v24.21.0`、`npm 11.19.0`（实测） |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9 |
| 后端依赖 | Spring Boot 4.1.1（Web 起步依赖为 **spring-boot-starter-webmvc**）、Spring v7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc-openapi-starter-webmvc-ui 3.1.1 |
| 数据库 | `backend/data/blog.db`（61440 字节），6 张表 + 7 个索引；article=3、tag=4、article_tag=5；重启后数据不变 |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达（见报错记录 1） |
| Git 仓库 | 分支 `main`；提交链：`501065a`（批 1 + 批 2）→ `25e280e`（批 3 + 批 4 + Vite IPv4 修复）→ 阶段 1 收尾文档（阶段 2 批 0 提交） |
