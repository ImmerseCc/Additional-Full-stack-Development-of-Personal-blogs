# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新。
> 最后更新：阶段 1 批 4 完成并实测通过（后端可启动、建表与种子数据写入、接口可访问均已验证）。

---

## 一、当前阶段

**阶段 1「目录结构与占位文件」**：批 1 ✅ → 批 2 ✅ → 批 3 ✅ → 批 4 ✅（全部实测通过）
**只差作者对批 4 的确认，即可进入阶段 2（后端业务实现）。**

---

## 二、已完成

| 时间点 | 事项 | 产物与证据 |
|---|---|---|
| 阶段 0 | 需求确认与技术选型（未写代码） | 选型：Vue 3 + Vite、原生 CSS 变量、Spring Boot 4.1.1 + JdbcTemplate + SQLite；0–9 阶段路线 |
| 阶段 0.5 | 协作日志规范确认 | `docs/ai-log.md`（流水）+ `docs/collaboration-log.md`（汇总），四套模板与禁止事项 |
| 阶段 1 批 1 | 根目录 3 个文件 | `README.md`、`.gitignore`、`AGENTS.md`；作者确认通过 |
| 阶段 1 批 2 | docs 文档骨架 + `git init` + Node 安装 | 9 个文件；`git init`（分支 main）；`node v24.21.0` / `npm 11.19.0` 实测 |
| 阶段 1 批 2 收尾 | 契约与数据模型确认 + `.gitattributes` + 首次提交 | 两份文档升级为 v1.0 已确认；提交 `501065a` |
| 阶段 1 批 3 | frontend 12 个文件 | `npm install`（81 包）、`npm run build`（28 模块，104ms）、dev server 冒烟（HTTP 200 + SFC 编译）全部实测通过；作者确认批 3 通过 |
| 报错修复 | 浏览器 127.0.0.1 被拒绝 | 根因：Vite 只绑定 IPv6；修复：`vite.config.js` 加 `host: '127.0.0.1'`；实测 127.0.0.1 与 localhost 均 200 |
| 阶段 1 批 4 | backend 15 个文件（`pom.xml`、`BlogApplication.java`、`application.yml`、`schema.sql`、`data.sql`、`mvnw` 三件套、7 个 `.gitkeep`） | 见下一行实测结果 |
| 阶段 1 批 4 验证 | 后端启动与数据链路实测 | `Java 26.0.2.1` 上 `Spring Boot v4.1.1 / Spring v7.0.9 / Tomcat 11.0.24` 启动成功（1.815 秒）；`backend/data/blog.db` 生成；JDBC 查询确认 6 张表（article / article_tag / comment / like_record / tag / sqlite_sequence）+ 7 个索引（4 个显式 + 3 个自动）全部建出；种子数据 article=3、tag=4、article_tag=5，重复启动后数量不变（幂等 + 持久化）；`/v3/api-docs` 与 `/swagger-ui/index.html` 均返回 200 |

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

1. **批 4 尚未获得作者确认**（实测已通过，等一句确认）；
2. **Git 第二次提交未执行**（批 3、批 4、两次报错修复的改动尚未提交）；
3. **阶段 2 未开始**（后端五层业务实现）。

---

## 四、下一步

| 阶段 | 内容 |
|---|---|
| 阶段 2 | 后端业务实现：`common`（ApiResponse / ErrorCode / BizException / GlobalExceptionHandler）、`model`（实体 / DTO / VO）、`repository`（JdbcTemplate）、`service`、`controller` 五层；按契约实现核心接口（health、文章 CRUD、列表分页、标签、评论、点赞）+ Swagger 完善 |
| 阶段 3 | 前端模块一：导航栏、汉堡菜单、主题切换、当前页高亮、路由过渡 |
| 阶段 4–5 | 前端模块二/三/四/五/六（列表、详情、搜索过滤、评论点赞、本地持久化） |
| 阶段 6 | 前后端对接（真实数据替换占位） |
| 阶段 7–8 | 两次功能迭代（前端体验 / 后端能力） |
| 阶段 9 | 专项审计 + 交付文档 + 演示脚本 |

---

## 五、遗留问题

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1 | ~~后端首次启动结果未知~~ **已解决** | — | 已实测：启动成功、建表成功、种子数据写入成功、接口 200 |
| 2 | ~~JDK 26 兼容性未实测~~ **已解决** | — | 实测可用：`Starting BlogApplication using Java 26.0.2.1`，Spring Boot v4.1.1 正常启动；无需改装 Temurin 21 |
| 3 | 依赖下载慢（首次约 10 分钟量级） | 影响验证耗时 | 依赖已缓存到 `C:\Users\19032\.m2` 与 `frontend/node_modules`，后续启动 2 秒级 |
| 4 | 接口契约与数据模型已确认（v1.0） | — | 阶段 2 按契约实现 |
| 5 | 项目目录路径含空格 | 目前低风险 | 出现工具异常时再评估迁移 |
| 6 | `frontend/public/`（封面图等静态资源目录）未创建 | 阶段 4 文章封面暂无本地图片 | 需要时向作者申请新增；当前种子文章 `cover_url` 均为 NULL，前端可用渐变占位 |
| 7 | 种子文章仅 3 篇 | 演示内容偏少 | 全量约 12 篇在阶段 4/5 补齐 |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、README 中明确标注 |
| 9 | SQLite 的 `busy_timeout` 与 WAL 未设置 | 极端并发下可能 `SQLITE_BUSY` | 连接池已限制为 1；阶段 2 视情况补充 PRAGMA 设置 |
| 10 | 结束后台任务会留下派生的 JVM 孤儿进程占住 8080 | 下次启动报端口占用 | 已实测并清理（`taskkill //PID`）；已写入 `docs/debug-log.md` 观察项；后续停后端请用 `TaskStop` 或按 PID 结束 |
| 11 | JDK 26 提示 `WARNING: A restricted method in java.lang.System has been called`（sqlite-jdbc 加载本地库） | 仅警告，不影响功能 | 可选：启动参数加 `--enable-native-access=ALL-UNNAMED`；阶段 2 评估是否写进 README |

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
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达（Scoop 自更新失败，见报错记录 1） |
| Git 仓库 | 分支 `main`，提交 `501065a`（批 1 + 批 2）；批 3、批 4 与报错修复的改动尚未提交 |
