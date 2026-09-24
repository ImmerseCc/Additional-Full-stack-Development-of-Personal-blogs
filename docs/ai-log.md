# AI 协作日志

> 本文件是《VibeCoding 大实战：个人博客全栈项目》的**逐轮协作流水日志**，只追加、不覆盖历史；若需更正，另起条目写明"更正"。
> 关键提示词汇总另见 `docs/collaboration-log.md`（成品视图），两处汇总表保持同步，如有不一致**以本文件为准**。
> **诚实性约定**：未发生的事写"未发生"；未运行的命令写"未运行，需我验证"；未收到反馈的结果写"待我验证"；不编造日期、不编造报错、不编造测试结果。

---

## 项目信息

| 项 | 内容 |
|---|---|
| 项目名称 | VibeCoding 大实战：个人博客全栈项目 |
| 目标 | 交付能运行的个人博客全栈项目（前端 + 后端同一仓库），并保留完整 AI 协作过程 |
| 前端 | Vue 3 + Vite + Vue Router 4 + Pinia（纯 JavaScript，不用 TypeScript）；样式为原生 CSS + CSS 变量主题 |
| 后端 | Java 21 字节码目标（本机 JDK 26）+ Spring Boot 4.1.1 + JdbcTemplate + Bean Validation |
| 数据库 | SQLite 单文件 `backend/data/blog.db`，5 张表：`article` / `tag` / `article_tag` / `comment` / `like_record` |
| 目录结构 | `docs/` + `frontend/` + `backend/`，根目录含 `README.md`、`.gitignore`、`AGENTS.md` |
| 启动命令 | 后端 `cd backend && ./mvnw spring-boot:run`；前端 `cd frontend && npm install && npm run dev` |
| 访问地址 | 前端 http://localhost:5173 ｜ 后端 http://localhost:8080（API 前缀 `/api`，前端经 Vite 代理） |
| 协作分工 | 用户：确认方向、运行验证、最终验收；AI：规划、写代码、改代码、给命令、做审计、整理文档 |
| 日志分工 | 本文件＝逐轮流水；`docs/collaboration-log.md`＝关键提示词汇总 + 阶段索引；`docs/debug-log.md`＝报错专档；`docs/audit-report.md`＝审计专档 |

---

## 阶段记录

### 阶段 0：需求确认与技术选型（未写代码）
- 我的提示词摘要：以"资深全栈开发搭子"身份完成个人博客全栈项目；前端必须 JavaScript，后端必须 Java，持久化只用轻量方案；禁止使用现成博客模板或整站主题；分阶段推进、每阶段等确认；固定回复格式（本阶段目标／需求理解／改动文件／代码或命令／验证步骤／协作记录块／待确认问题）；要求 ≥4 组关键提示词、≥2 次功能迭代、1 次真实报错记录、1 次前后端专项审计；禁止编造运行结果、禁止硬编码密钥、禁止危险删除。首轮只允许回复理解、选型、目录结构、路线、待确认问题、风险，不得写代码。
- AI 做了什么：1) 只读探测本机环境，发现 Node/npm/Maven 全部缺失、仅有 JDK 26、网络与安装工具（winget/scoop）可用；2) 查证 Spring Initializr 当前稳定版为 4.0.8 / 4.1.1，Java 选项为 17/21/25/27；3) 输出选型（Vue 3 + Vite + 原生 CSS 变量；Spring Boot 4.1.1 + JdbcTemplate + SQLite；mvnw 免装 Maven）；4) 输出 monorepo 目录结构与 0–9 阶段路线；5) 提出 11 个待确认问题与 9 条风险。
- 改动文件：无（本轮为规划，无文件改动）
- 验证命令与结果：未运行（本阶段无代码可验证）；环境探测命令为 `node -v` / `java -version` / `mvn -v` / `curl -I` 等只读命令，实测结果：Node/npm/Maven 缺失、JDK 26、四个依赖源 HTTP 200。
- 遗留问题：11 个待确认问题（框架、数据访问、JDK 方案、Node 安装授权、鉴权、点赞真相源、种子文章、站点信息、Git、演示录屏）；JDK 26 与 Boot 4.1.1 兼容性未实测。
- 下一步：阶段 1 接口契约与数据模型草案。

### 阶段 0.5：协作日志规范确认（问答轮，无文件改动）
- 我的提示词摘要：除开发搭子外兼任"AI 协作日志记录员"；四个触发场景（记录本阶段／记录报错／记录审计／完成改动后自动记录）；阶段记录、报错记录、审计记录、关键提示词汇总四套模板；只输出可追加片段、不输出整份文件；禁止编造报错、编造测试结果、把未运行说成已通过；本轮不得写代码。
- AI 做了什么：确认职责与五类触发条件；逐字对齐四套模板；列出 9 条禁止事项（不编造报错／不编造测试结果／不编造输出／不写空话"已完成"／不把计划写成事实／不伪造授权／不写敏感信息／不擅自输出整份日志／不替你验收）；确定 `docs/collaboration-log.md` 与 `docs/ai-log.md` 的分工；明确日志不写日期（模板无日期字段，需要时由用户提供）。
- 改动文件：无（本轮为规范确认，未创建任何文件）
- 验证命令与结果：未运行（无代码可验证）
- 遗留问题：`docs/ai-log.md` 当时尚未创建；阶段 0 的 11 个问题仍未确认；Node 未安装；JDK 26 兼容性未实测。
- 下一步：等作者确认后进入目录架构落地。

### 阶段 1：目录结构与占位文件（批 1：根目录文件）
- 我的提示词摘要：按给定目录架构逐步创建目录与占位文件，不得一次性生成全部业务代码；必须先出"创建计划"、确认后分 4 批（根目录 → docs → frontend → backend）执行，每批结束停下等确认；每个文件说明"路径 + 占位还是完整 + 验证命令"；禁止使用现成模板、禁止硬编码密钥、禁止编造运行结果、不要扫描整个仓库。确认结论：A1（沿用当前目录）／B1（同时建 `ai-log.md` 与 `collaboration-log.md`）／C1（`mvnw` 取自 start.spring.io 官方骨架）／D1（增加第 5 张关联表 `article_tag`）／E（锁定 Vue 3 + Vite + Spring Boot + JdbcTemplate + SQLite，并授权 AI 安装 Node）；种子文章先写 2–3 篇。
- AI 做了什么：输出 22 个目录 / 约 38 个文件的创建计划与 5 个决策点；执行批 1，创建 `README.md`（技术栈、启动命令、目录结构、功能清单含"待实现"标注、数据位置与重置方法、访问地址、文档索引、5 条风险）、`.gitignore`（六类规则，含 `!backend/.mvn/wrapper/maven-wrapper.jar` 反向规则与 `backend/data/*.db` 精确忽略）、`AGENTS.md`（硬约束、技术栈、唯一启动命令、后端分层职责、契约索引、协作规则、8 条禁止事项、环境风险）。
- 改动文件：`README.md`（新建，完整，213 行）、`.gitignore`（新建，完整，76 行）、`AGENTS.md`（新建，完整，156 行）
- 验证命令与结果：AI 执行 `ls -a; wc -l README.md .gitignore AGENTS.md`，输出显示目录内仅这 3 个文件，行数 213 / 76 / 156（共 445 行）。用户侧执行 `cd "/d/code/Additional Full-stack Development of Personal blogs" && ls -a` 后**确认批 1 通过**。
- 遗留问题：`git init` 当时未执行；Node 安装任务在后台运行中，结果待确认；README 中"文档索引"当时标注为"待创建（批 2）"。
- 下一步：批 2 创建 `docs/` 文档骨架。

### 阶段 1：目录结构与占位文件（批 2：docs 文档骨架）
- 我的提示词摘要：批 1 通过，要求"现在 git init，请继续"——即执行 Git 仓库初始化并继续批 2（创建 docs 下的文档骨架，契约与数据模型出草案等确认）。
- AI 做了什么：1) 执行 `git init` 并把默认分支改为 `main`；2) 创建 `docs/` 下 8 个文档与 `docs/demo/.gitkeep`：`main-prompt.md`（原文照录三轮指令）、`current-state.md`（进度快照）、`api-contract.md`（17 个接口 + 统一响应 + 7 个错误码 + 8 项待确认）、`data-model.md`（5 张表字段/约束/索引 + 幂等种子策略 + 5 项待确认）、`collaboration-log.md`（关键提示词汇总 + 阶段索引）、`ai-log.md`（本文件）、`debug-log.md`（模板 + 首条真实环境报错）、`audit-report.md`（模板 + 12 项审计清单）；3) 复核 Node 安装结果并实测 `node -v` / `npm -v`；4) 同步 README 与 current-state 的状态描述。
- 改动文件：`docs/main-prompt.md`、`docs/current-state.md`、`docs/api-contract.md`、`docs/data-model.md`、`docs/collaboration-log.md`、`docs/ai-log.md`、`docs/debug-log.md`、`docs/audit-report.md`、`docs/demo/.gitkeep`（全部新建）；同步修改 `README.md`、`docs/current-state.md`
- 验证命令与结果：AI 实测 `node -v`（输出 `v24.21.0`）、`npm -v`（输出 `11.19.0`）；`git add -A` 后 `git status --short` 显示已暂存文件列表。**用户侧验证命令未运行，需我验证**：`ls docs && node -v && npm -v`
- 遗留问题：接口契约与数据模型仍是草案，等作者确认；Git 首次提交未执行；README 部分区块（文档索引、风险清单）状态待下一轮同步；`docs/demo/` 目录当前为空（除 .gitkeep）。
- 下一步：批 3 创建 `frontend/` 结构与配置文件（写入前用 npm registry 核对依赖版本）。

### 阶段 1：目录结构与占位文件（批 3：frontend）【补记】
> **补记说明**：本条与下一条批 4 记录，是在阶段 2 批 1 期间应作者要求补齐的。内容来源为 `docs/collaboration-log.md`「五、阶段记录」中阶段 1 的已验证记录与当时的实测输出，**不含新编造的事实**。

- 我的提示词摘要：批 2 通过后继续批 3（创建 `frontend/` 结构与配置文件）；规则不变——先查证依赖版本再写文件、每个文件说明"路径 + 占位还是完整 + 验证命令"、禁止使用现成模板、禁止一次性生成全部业务代码。
- AI 做了什么：先用 `curl` 查 npm registry 实时版本（发现 vue-router 已 5.x、pinia 4.x、vite 8.x，并核对 peer 依赖的可选性），再创建 12 个文件：`index.html`（含主题防闪脚本）、`package.json`、`vite.config.js`（含 `/api` 代理与 IPv4 绑定）、`src/main.js`、`src/App.vue`、`src/router/index.js`、`src/views/HomeView.vue`、`src/styles/base.css`（亮 / 暗两套 CSS 变量）、`src/{stores,api,components,utils}/.gitkeep`。
- 改动文件：`frontend/` 下 12 个文件（8 个完整 + 4 个 `.gitkeep` 占位，全部新建）
- 验证命令与结果（实测输出）：`npm install --no-fund --no-audit` → `added 81 packages in 10m`；`npm run build` → `✓ 28 modules transformed`、`✓ built in 104ms`（首页被切成独立 chunk，路由懒加载生效）；dev server 冒烟 → `GET / → 200`、`<title>个人博客 · VibeCoding</title>`、`/src/main.js` 返回转译后 ESM、`/src/App.vue` 返回编译后 SFC；作者侧确认批 3 通过。
- 遗留问题：首次 `npm install` 耗时 10 分钟量级（依赖下载慢）；`frontend/public/` 静态资源目录尚未创建；封面图暂无本地资源。
- 下一步：批 4 创建 `backend/` 结构与配置。

### 阶段 1：目录结构与占位文件（批 4：backend）【补记】
> **补记说明**：同上，内容来源为 `docs/collaboration-log.md` 阶段 1 记录。

- 我的提示词摘要：继续批 4（创建 `backend/` 结构与配置，写入前核对依赖版本）；期间作者报告"在验证时，localhost 拒绝了我的链接"，按 AI 要求提供真实证据（dev server 输出、`netstat`、三条 `curl` 实测、"未开代理"）后，在三选一中确认"加 `host: '127.0.0.1'`"。
- AI 做了什么：1) 读 start.spring.io 官方骨架后发现 **Spring Boot 4 的 Web 起步依赖更名为 `spring-boot-starter-webmvc`**（Boot 3 为 `-web`），据此修正坐标；2) 用 `curl` 核对 `sqlite-jdbc 3.53.4.0`、`springdoc 3.1.1`（其父 POM 声明 Boot 4.1.0，与本项目同代）；3) 复制官方 `mvnw` 三件套；4) 创建 15 个文件：`pom.xml`、`BlogApplication.java`、`application.yml`、`schema.sql`（5 张表 + 索引）、`data.sql`（3 篇原创文章 + 4 标签 + 5 关联，固定 ID + `INSERT OR IGNORE` 幂等）、`data/.gitkeep` 与 6 个包 `.gitkeep`；5) 修复 Vite 只绑定 IPv6 的问题（详见报错记录 2），并清理被 kill 后残留的派生 JVM。
- 改动文件：`backend/pom.xml`、`backend/mvnw`、`backend/mvnw.cmd`、`backend/.mvn/wrapper/maven-wrapper.properties`、`backend/src/main/java/com/example/blog/BlogApplication.java`、6 个包 `.gitkeep`、`backend/src/main/resources/{application.yml,schema.sql,data.sql}`、`backend/data/.gitkeep`（共 15 个）；另修改 `frontend/vite.config.js` 一行（`host: '127.0.0.1'`）。
- 验证命令与结果（实测输出）：`./mvnw -B -ntp spring-boot:run` → 在 `Java 26.0.2.1` 上 `Spring Boot v4.1.1 / Spring v7.0.9 / Tomcat 11.0.24`，`Started BlogApplication in 1.815 seconds`；JDBC 只读检查 → 6 张表 + 7 个索引；`article=3`、`tag=4`、`article_tag=5`，**两次启动后数量一致**（幂等 + 持久化）；`curl /v3/api-docs → 200`、`curl /swagger-ui/index.html → 200`；作者侧确认批 4 通过。
- 遗留问题：外键级联删除未实测（已登记为 `current-state.md` 遗留项 12）；结束后台任务会残留派生 JVM；`--enable-native-access` 警告；`frontend/public/` 未创建。
- 下一步：阶段 2 后端五层业务实现。

### 阶段 2：后端业务实现（批 0：提交收尾 + git 状态描述修正）
- 我的提示词摘要：新会话开头要求先读 6 份文档（`AGENTS.md`、`README.md`、`docs/api-contract.md`、`docs/data-model.md`、`docs/collaboration-log.md`、`docs/current-state.md`）并明确"**先不要写代码**"，读完后用不超过 10 行总结现状；随后"分批列出需要的事项"；对阶段 2 分批方案确认"**第二项拆分**"（把原批 2 拆成 2a 读路径 / 2b 写路径）并下达"**执行批 0**"。
- AI 做了什么：1) 只读核对 git 现状，发现**第二次提交其实已经存在**（`25e280e`），而 `docs/current-state.md`、`docs/collaboration-log.md` 里"第二次提交未执行"的描述是过时错误（该描述本身也是被 `25e280e` 带进仓库的）；2) 确认工作区仅剩三份 docs 有改动，无未跟踪文件、无多余文件；3) 逐处修正过时描述，并补记阶段 2 的分批方案；4) 执行提交收尾（**本条目随该提交一起入库，提交号见 `git log` 最新一条**）。
- 改动文件：`docs/current-state.md`（修改：最后更新行、当前阶段与阶段 2 分批、待确认/待执行、环境事实 Git 行）、`docs/collaboration-log.md`（修改：最后更新行、阶段索引的阶段 2 行、阶段 1 验证结果补记 `25e280e`、阶段 1 遗留问题）、`docs/ai-log.md`（追加本条目）。三份均为**修改/追加**，无占位文件，无业务代码改动。
- 验证命令与结果（均为实测输出）：`git status --short -uall` → 提交前仅 `docs/ai-log.md`、`docs/collaboration-log.md`、`docs/current-state.md` 三个 `M`（无未跟踪文件）；`git log --oneline -3` → `25e280e feat: 完成 frontend/backend 骨架并修复 dev server IPv4 绑定`、`501065a chore: 初始化项目骨架（根目录文件 + docs 文档骨架）`；`git show --stat 25e280e` → 31 个文件变更（frontend 12 + backend 15 + README/debug-log/current-state）；`git show --stat 501065a` → 13 个文件（含 `.gitattributes`）；`git ls-files | grep -i gitattributes` → `.gitattributes` 已被跟踪；`git branch --show-current` → `main`。
- 遗留问题：`docs/ai-log.md` 的阶段流水停在批 2，**缺少批 3、批 4 与报错修复的逐轮记录**（细节另见 `docs/collaboration-log.md` 阶段 1 记录），是否补记待作者决定；阶段 1 其余遗留项见 `docs/current-state.md` 第五节。
- 下一步：阶段 2 批 1 —— `common`（`ApiResponse` / `ErrorCode` / `BizException` / `GlobalExceptionHandler`）+ `model`（4 实体 / 4 DTO / 6 VO）+ `config/JacksonConfig`，以 `./mvnw -B -ntp compile` 验证。

### 阶段 2：后端业务实现（批 1：common + model + config）
- 我的提示词摘要："批0完成，请继续"——按已确认的分批方案执行批 1（`common` + `model` + `config/JacksonConfig`）。
- AI 做了什么：1) 动手前先用 `javap` 核对 Spring Boot 4.1.1 实际携带的 **Jackson 3**（`tools.jackson.core:jackson-databind:3.1.5`）及其扩展点，确认 `org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer`、`tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer(DateTimeFormatter)`、`SimpleModule.addSerializer`、`MapperBuilder.addModule` 真实存在，不凭记忆写 API；2) 创建 19 个完整文件：`common` 4 个、`model` 14 个（4 实体 + 4 请求 DTO + 6 响应 VO）、`config/JacksonConfig` 1 个；3) 删除已被真实代码取代的两个占位文件 `common/.gitkeep`、`model/.gitkeep`；4) 编译 → 启动烟测 → 用 `curl` 实测新增的"接口不存在"处理器；5) 清理烟测遗留的孤儿 JVM（PID 1680）。
- 改动文件（全部新建，完整；另删 2 个占位）：
  - `backend/src/main/java/com/example/blog/common/`：`ErrorCode.java`、`ApiResponse.java`、`BizException.java`、`GlobalExceptionHandler.java`
  - `backend/src/main/java/com/example/blog/model/`：`Article.java`、`Tag.java`、`Comment.java`、`LikeRecord.java`、`ArticleCreateRequest.java`、`ArticleUpdateRequest.java`、`CommentCreateRequest.java`、`VisitorIdRequest.java`、`ArticleSummaryVO.java`、`ArticleDetailVO.java`、`CommentVO.java`、`TagVO.java`、`LikeStateVO.java`、`PageVO.java`
  - `backend/src/main/java/com/example/blog/config/JacksonConfig.java`
  - 删除：`backend/src/main/java/com/example/blog/common/.gitkeep`、`backend/src/main/java/com/example/blog/model/.gitkeep`
- 验证命令与结果（均为实测输出）：`./mvnw -B -ntp compile` → `Compiling 20 source files with javac [debug parameters release 21]`、`BUILD SUCCESS`（1.7–2.0 秒）；`./mvnw -B -ntp spring-boot:run` → `Tomcat initialized with port 8080`、`HikariPool-1 - Start completed`、`Started BlogApplication in 2.098 seconds`（Spring Boot v4.1.1 / Spring v7.0.9 / Tomcat 11.0.24，无 context 报错，说明 `JacksonConfig` 与 `GlobalExceptionHandler` 均被正常装配）；`curl -i http://localhost:8080/api/not-exist` → `HTTP/1.1 404` + `{"code":40004,"message":"接口不存在：/api/not-exist","data":null}`（统一返回体生效）；`curl -X POST http://localhost:8080/api/health` → 同样 404/40004（当前无对应控制器，故"方法不支持"分支未触发）；停止后 `netstat` 显示 PID 1680 仍监听 8080（复现已知项 10）→ `taskkill //PID 1680 //F` 后复查 `8080 已释放`。
- 遗留问题：`JacksonConfig` 的固定时间格式**尚未经 HTTP 验证**（本批没有返回时间的接口），计划批 2a 用 `curl /api/articles` 观察 `createdAt` 是否形如 `2026-09-03T09:00:00`（不省略秒位）；`HttpRequestMethodNotSupportedException`（405 → 40002）同样待批 2a 验证；本批改动尚未 git 提交。
- 下一步：阶段 2 批 2a —— 文章读路径：`ArticleRepository` / `TagRepository`（读方法）+ `ArticleService` / `TagService` + `HealthController` / `ArticleController`（GET 列表、GET 详情）/ `TagController`，用 `curl` 实测分页、关键词、标签过滤、详情与 40004。

### 阶段 2：后端业务实现（批 2a：文章读路径）
- 我的提示词摘要："批 1 通过，提交并继续"——先提交批 1，再按已确认的分批方案执行批 2a（health / 文章列表与详情 / 标签列表）。
- AI 做了什么：1) 提交批 1 改动（`92a5cc6`）；2) 写入 8 个文件（比原计划多 1 个，见下）；3) 删除已被真实代码取代的 3 个 `.gitkeep`；4) 编译 → 启动 → 13 项 curl 实测（分页、关键词、标签 and/or、状态、详情、404、参数非法、方法不支持、编码非法）；5) 实测中发现并定位一起真实报错（`curl` 中文参数按 GBK 编码导致查询串解码失败），在 `GlobalExceptionHandler` 补处理器后复测通过（详见报错记录 4）。
- 改动文件：
  - 新建（完整）：`backend/src/main/java/com/example/blog/common/TimeFormats.java`、`repository/ArticleRepository.java`、`repository/TagRepository.java`、`service/ArticleService.java`、`service/TagService.java`、`controller/HealthController.java`、`controller/ArticleController.java`、`controller/TagController.java`
  - **计划外新增说明**：`common/TimeFormats.java` 不在原 7 文件清单中。原因：时间格式 `yyyy-MM-dd'T'HH:mm:ss` 需要被 repository（解析 SQLite 文本）、service（写入）与 `config/JacksonConfig`（JSON 序列化）三处共用，散落三份会漂移，故收敛为唯一定义，并把 `JacksonConfig` 改为引用它（删掉其内部重复的 formatter）。
  - 修改：`config/JacksonConfig.java`（引用 `TimeFormats.DATE_TIME`）、`common/GlobalExceptionHandler.java`（新增查询串编码非法处理器；javadoc 与实际行为对齐）
  - 删除（占位已被真实代码取代）：`repository/.gitkeep`、`service/.gitkeep`、`controller/.gitkeep`
- 验证命令与结果（均为实测输出）：
  - `./mvnw -B -ntp compile` → `Compiling 28 source files with javac [debug parameters release 21]`、`BUILD SUCCESS`
  - `curl /api/health` → `{"code":0,"message":"ok","data":{"status":"UP","time":"2026-09-24T12:55:28"}}`
  - `curl "/api/articles?page=1&size=2"` → `total=3`、`totalPages=2`，且 **`"createdAt":"2026-09-07T09:00:00"` 带秒位 —— 批 1 遗留的"时间格式未经 HTTP 验证"由此验证通过**
  - `keyword=Vue → total=1`；`tags=Vue → total=1`；`tags=项目日志,Vue&tagMode=or → total=3`；`tags=项目日志,SQLite&tagMode=and → total=1`；`status=DRAFT → total=0`；`status=ALL → total=3`
  - `curl /api/tags` → 4 个标签，文章数 2 / 1 / 1 / 1（按文章数倒序）
  - `curl /api/articles/1` → 详情含 Markdown 正文，`prev` / `next` 为 `null`（阶段 8 实现）
  - `curl /api/articles/9999` → `HTTP 404` + `{"code":40004,"message":"文章不存在：id=9999","data":null}`
  - `curl "/api/articles?page=abc"` → `HTTP 400` + `40002`（参数格式错误：page）
  - `curl "/api/articles?size=100"` → `HTTP 400` + `40002`（size 需在 1-20 之间）
  - `curl -X POST /api/articles` → `HTTP 400` + `40002`（请求方法不支持：POST）——**批 1 遗留的"方法不支持分支未触发"由此验证通过**
  - GBK 编码查询串（报错记录 4 场景）→ 修复前 `50000`，修复后 `HTTP 400` + `40002`
  - 收尾：停止服务后按惯例清理派生 JVM（`taskkill //PID 36880 //F`），复查 `8080 已释放`
- 遗留问题：文章**写路径**（创建 / 更新 / 删除、标签自动创建与清空、外键级联删除实测）留待批 2b；本批改动**尚未 git 提交**。
- 下一步：阶段 2 批 2b —— 文章写路径（POST / PUT / DELETE + 标签维护 + 级联删除实测）。

### 阶段 2：后端业务实现（批 2b：文章写路径）
- 我的提示词摘要："批 2a 通过，提交并继续"——先提交批 2a，再按已确认的分批执行批 2b（创建 / 更新 / 删除 + 标签维护 + 级联删除实测）。
- AI 做了什么：1) 提交批 2a（`b22592d`）；2) 在既有文件上补齐写路径：`ArticleRepository` 增 `insert` / `update` / `deleteById` / `replaceTags`，`TagRepository` 增 `findByName` / `insert`，`TagService` 增 `resolveTagIds`（去重 + 名称长度校验 + 不存在则创建），`ArticleService` 增 `createArticle` / `updateArticle` / `deleteArticle` 及摘要截取、状态归一、可选字段归 null 等私有方法（写方法带 `@Transactional`），`ArticleController` 增 POST / PUT / DELETE；3) 编译 → 启动 → 16 项 curl + JDBC 取证实测；4) 停服、清理派生 JVM 与临时验证文件。
- 改动文件（全部为**修改**，无新增、无占位文件）：
  - `backend/src/main/java/com/example/blog/repository/ArticleRepository.java`（写方法 + 类注释更新）
  - `backend/src/main/java/com/example/blog/repository/TagRepository.java`（`findByName` / `insert`）
  - `backend/src/main/java/com/example/blog/service/TagService.java`（`resolveTagIds` + 标签名长度校验）
  - `backend/src/main/java/com/example/blog/service/ArticleService.java`（写路径方法，并注入 `TagService`）
  - `backend/src/main/java/com/example/blog/controller/ArticleController.java`（POST 201 / PUT / DELETE）
- 验证命令与结果（均为实测输出）：
  - `./mvnw -B -ntp compile` → `Compiling 28 source files`、`BUILD SUCCESS`
  - `POST /api/articles`（不传 summary、正文 185 字、标签含新标签）→ **HTTP 201**，`id=4`、`status:"PUBLISHED"`、`tags:["Vue","级联测试标签"]`
  - 摘要截取核对（临时程序 `target/SummaryCheck.java` 按码点）→ `summary 码点数 = 120`、`content 码点数 = 185`、`summary == 正文前 120 个字符 ? true`
  - `GET /api/tags` → 新标签"级联测试标签"已**自动创建**（`id=5`）
  - `PUT /api/articles/4`（`status=DRAFT`、`tags=["SQLite"]`）→ 200，标签被整体替换为 `["SQLite"]`；`createdAt` 保持 `2026-09-24T19:16:59` 不变，`updatedAt` 变为 `2026-09-24T19:17:01`
  - `PUT` **不传** `tags` 字段 → 响应 `"tags":[]`（清空语义生效）
  - 删除前 JDBC 取证 → `article=1, comment=1, like_record=1, article_tag=1`（评论与点赞由临时程序直接写库构造）
  - `DELETE /api/articles/4` → `HTTP 200` + `{"code":0,"message":"ok","data":null}`
  - 删除后 JDBC 取证 → `article=0, comment=0, like_record=0, article_tag=0` → **外键 `ON DELETE CASCADE` 真实生效（`current-state.md` 遗留项 12 由此关闭）**
  - 删除后 `GET /api/articles/4` → `404 + 40004`；`GET /api/tags` → 标签**仍保留**（"级联测试标签"计数归 0，符合契约"标签本身保留"）
  - 校验类：`POST` 标题为空 → `400 + 40001` + `data.fields:{"title":"标题不能为空"}`；`PUT` / `DELETE` 不存在的 id → `404 + 40004`
  - 持久化：新建文章 `id=5` → **重启后端后** `GET /api/articles/5` 仍为 `200` 且内容完整，列表 `total=4`
  - 收尾：`taskkill //PID 16732 //F` 清理派生 JVM、`8080 已释放`；删除 `target/` 下临时文件（`CascadeCheck.java`、`SummaryCheck.java`、`payload-*.json`、`cp.txt`）；`git status` 仅剩 5 个待提交的 Java 文件
- 遗留问题：数据库里留有本次实测产生的数据（文章 `id=5`「写路径实测：创建一篇文章」与标签 `id=5`「级联测试标签」，后者计数为 0）——保留还是重置由作者决定；本批改动**尚未 git 提交**。
- 下一步：阶段 2 批 3 —— 评论与点赞（列表 / 发表 / 删除、点赞幂等、visitorId 归属校验）。

### 阶段 2：后端业务实现（批 3：评论与点赞）
- 我的提示词摘要："批 2b 通过，提交并继续，前一项选 b"——先提交批 2b，再执行批 3（评论与点赞），并按作者选择用**接口**删除上个批次的实测文章 5。
- AI 做了什么：1) 提交批 2b（`5c210a3`）；2) 新增 7 个文件（含 1 个计划外文件 `common/PageParams.java`，理由见下），并修改 2 个既有文件；3) 编译 → 启动 → 19 项实测（评论 6 项 + 点赞 8 项 + 回归 5 项）；4) 执行 `DELETE /api/articles/5` 完成作者选定的清理；5) 停服、清理派生 JVM 与临时载荷文件。
- 改动文件：
  - 新建（完整）：`backend/src/main/java/com/example/blog/repository/CommentRepository.java`、`repository/LikeRepository.java`、`service/CommentService.java`、`service/LikeService.java`、`controller/CommentController.java`、`controller/LikeController.java`、`common/PageParams.java`
  - **计划外文件说明**：`common/PageParams.java` 把"分页参数解析 + 语义校验（page ≥ 1、size 1–20）"收敛为唯一实现，供文章列表与评论列表共用；否则评论列表会复制文章列表那 12 行校验与错误文案（审计项「重复代码」）。相应地 `ArticleService` 的分页校验改为复用它。
  - 修改：`backend/src/main/java/com/example/blog/repository/ArticleRepository.java`（新增 `existsById`）、`service/ArticleService.java`（分页校验改用 `PageParams`、新增 `requireArticleExists` 供评论 / 点赞共用）
- 验证命令与结果（均为实测输出）：
  - `./mvnw -B -ntp compile` → `Compiling 35 source files`、`BUILD SUCCESS`
  - 清理（作者选 b）：`DELETE /api/articles/5` → `200 + {"code":0,"data":null}`；再查 → `404 + 40004`；标签「级联测试标签」按契约保留（计数 0）
  - 评论发表：`POST /api/articles/1/comments` → **HTTP 201** + `{"id":2,"articleId":1,"authorName":"批3实测访客","content":"...","createdAt":"2026-09-24T19:30:05"}`（响应**不含 `authorEmail` 与 `visitorId`**）
  - 评论列表：`GET /api/articles/1/comments?page=1&size=5` → `total=1, totalPages=1`
  - 评论错误分支：`GET /api/articles/9999/comments` → `404 + 40004`；昵称为空 → `400 + 40001` + `data.fields:{"authorName":"昵称不能为空"}`
  - 评论归属校验：`DELETE /api/comments/2` 用**他人** visitorId → `404 + 40004`（不删）；用**本人** visitorId → `200`；删除后列表 `total=0`
  - 点赞：初始 `liked:false, likeCount:0` → 点赞 `true,1` → **重复点赞仍 `true,1`（幂等，计数未放大）** → 第二个访客点赞 `true,2` → 取消 `false,1` → **再次取消仍 `false,1`（幂等）** → 清理后 `false,0`
  - 点赞错误分支：缺 `visitorId` → `400 + 40001` + `fields.visitorId`；`GET /api/articles/9999/likes?...` → `404 + 40004`
  - 回归（`PageParams` 重构后）：文章列表 `total=3` 正常；`page=abc` → `400 + 40002`；`size=100` → `400 + 40002`；文章 1 收尾计数 `likeCount:0, commentCount:0`（未留测试数据）
  - 收尾：`taskkill //PID 30228 //F` 清理派生 JVM、`8080 已释放`；删除 `target/c-*.json` 临时载荷
- 遗留问题：本批 7 个新增 + 2 个修改的文件**尚未 git 提交**；阶段 2 仅剩批 4。
- 下一步：阶段 2 批 4 —— 收尾（springdoc 注解、异常兜底实测、`busy_timeout` 评估、三份日志与状态快照同步）。

### 阶段 2：后端业务实现（批 4：收尾 —— Swagger 注解、异常兜底、busy_timeout）
- 我的提示词摘要："停止后端，批 3 通过，提交并继续"——先停服并提交批 3，再执行阶段 2 的收尾批（批 4）。
- AI 做了什么：1) 停掉孤儿 JVM（PID 24680）并提交批 3（`4b1700b`）；2) 新增 `config/OpenApiConfig.java`（OpenAPI 的 info：标题 / 版本 / 说明）；3) 给 5 个 controller 补 `@Tag` / `@Operation`，点赞的 query 参数补 `@Parameter`；4) `application.yml` 的 JDBC URL 追加 `busy_timeout=5000`；5) `README.md` 常见问题表补一行（native-access 警告）；6) 编译 → 启动 → 验证 Swagger 元信息、异常兜底 4 项、`busy_timeout` 生效。
- 改动文件：
  - 新建（完整）：`backend/src/main/java/com/example/blog/config/OpenApiConfig.java`
  - 修改：`backend/src/main/java/com/example/blog/controller/HealthController.java`、`ArticleController.java`、`TagController.java`、`CommentController.java`、`LikeController.java`（`@Tag` / `@Operation` / `@Parameter`）、`backend/src/main/resources/application.yml`（`?busy_timeout=5000`）、`README.md`（常见问题排查新增一行）
- 验证命令与结果（均为实测输出）：
  - `./mvnw -B -ntp compile` → `Compiling 36 source files`、`BUILD SUCCESS`
  - `curl /v3/api-docs` → tag 分组 5 个（健康检查 / 文章 / 标签 / 评论 / 点赞）；`"summary"` **13 条**（＝已实现的 13 个操作，一条不缺）；`info.title = "个人博客后端 API"`、`info.version = "v1.0"`
  - `curl /swagger-ui/index.html` → `HTTP 200`
  - 异常兜底复测：非法 JSON → `400 + 40002`；未知路径 → `404 + 40004`；`POST /api/tags`（方法不支持）→ `400 + 40002`；空标题 → `400 + 40001` + `data.fields.title`
  - `busy_timeout` 生效核对（临时程序用与 `application.yml` **相同**的 JDBC URL 建连）→ `PRAGMA busy_timeout = 5000`（期望值）→ **遗留项 9 的 busy_timeout 部分关闭**；同程序的 `PRAGMA foreign_keys = 0` 属预期（外键由 Hikari 连接初始化语句开启，URL 直连不带）
  - 收尾：删除 `target/` 下临时文件（`BusyTimeoutCheck.java`、`cp.txt`）
- 遗留问题：本批改动**尚未 git 提交**；WAL 模式仍未启用（连接池为 1、单写者场景下收益有限，暂不做并在 `current-state.md` 说明）；后端**仍在运行中**（应作者验证 Swagger UI 需求，用不设超时的方式启动）。
- 下一步：**阶段 2（批 0–4）全部完成**，等待作者确认；之后进入阶段 3「前端模块一：全局导航与主题」。

### 阶段 3：前端模块一（批 0：开工基线）
- 我的提示词摘要：新会话开场要求"**先不要写代码**"，先读 6 份文件（`AGENTS.md`、`README.md`、`docs/api-contract.md`、`docs/data-model.md`、`docs/collaboration-log.md`、`docs/current-state.md`），读完后用不超过 10 行总结现状；随后"分批列出需要的事项"；对阶段 3 分批方案（批 0–4）与 8 个决策点逐条确认：**1 同意、2 增加（新增 404 页）、3 三态（主题：亮 / 暗 / 跟随系统）、4 同意、5 同意、6 与 7 同意、8 不拆**（沿用阶段 2 的"每批停下等确认"节奏），并下达"请继续"。
- AI 做了什么：1) 只读核对前端现状（`App.vue`、`HomeView.vue`、`router/index.js`、`styles/base.css`、`index.html`、`vite.config.js`、`package.json` 与 `src` 目录树），确认 `src/{components,stores,api,utils}` 目前为空目录、路由表仅 `/` + 兜底重定向、`@` 别名已在 `vite.config.js` 配好、`frontend/public/` 确实不存在；2) 核对 `index.html` 首屏防闪脚本（读 `blog:theme`，"无值＝跟随系统"，与批 1 的三态约定一致，预计无需改动）；3) 核对 git 基线与前端构建基线；4) 更正 `README.md` 顶部状态块（阶段 1 / Node 未装 / 首次提交待执行 / 契约仍是草案 → 阶段 3 批 0 / 后端 13 接口已实测 / JDK 26 已验证 / 最新提交 `af2334c` / 契约与模型 v1.0 已确认）；5) 同步 `docs/current-state.md`（最后更新行、接手说明进度、当前阶段与分批、新增决策 K–Q、待确认项改为 README 待同步清单、第四节改为分批计划表、环境事实补 Git 提交链与前端构建基线）。
- 改动文件：`README.md`（修改：顶部状态块）、`docs/current-state.md`（修改：最后更新行、零节第 2 条、一节、二节关键决策表新增 K–Q、三节、四节、六节 Git 行与新增构建基线行）、`docs/collaboration-log.md`（修改：最后更新行、阶段索引的阶段 3 行改为"进行中：批 0 ✅"）、`docs/ai-log.md`（追加本条目）。均为**修改/追加**，无占位文件，**无前端业务代码改动**。
- 验证命令与结果（均为实测输出）：`git status --short` → 开工时为**空**（工作区干净）；`git log --oneline -12` → 最新一条为 `af2334c docs: 阶段 2 收尾记录与状态快照（阶段 3 开工前）`；`cd frontend && npm run build` → `✓ 28 modules transformed.`、`dist/index.html 1.30 kB`、`dist/assets/index-BSjQAKIk.js 89.01 kB │ gzip: 34.74 kB`、`✓ built in 160ms`；改文档后 `git diff --stat` → `README.md | 11 +++---`、`docs/current-state.md | 45 +++---`（`2 files changed, 33 insertions(+), 23 deletions(-)`）；全部改动完成后 `git status --short` → 四个 `M`（`README.md`、`docs/current-state.md`、`docs/collaboration-log.md`、`docs/ai-log.md`），无未跟踪文件。
- 遗留问题：本批改动**尚未 git 提交**（等作者确认后再提交，沿用阶段 2 节奏）；`README.md` 仍有阶段 1 时代的过时描述（第四节前端模块表、"后端功能"状态列、"已知问题与风险"第 2/6 条），已登记在 `docs/current-state.md` 第三节，计划批 4 一并同步；后端保持停止状态（8080 未占用）。
- 下一步：阶段 3 批 1 —— 主题系统：新建 `frontend/src/stores/theme.js`（三态 + `localStorage['blog:theme']` + 监听 `prefers-color-scheme`）、`frontend/src/components/ThemeToggle.vue`；扩展 `frontend/src/styles/base.css`；`App.vue` 临时挂载切换按钮；核对 `index.html` 防闪脚本；验证用 `npm run build` + 浏览器实测（切换 / 刷新不丢 / 跟随系统）。

---

## 报错记录

### 报错记录 1：scoop 自更新失败（github.com 连接被重置）——环境类，非阻塞
- 报错原文：
  ```
  Updating Scoop...
  fatal: unable to access 'https://github.com/ScoopInstaller/Scoop/': Recv failure: Connection was reset
  Remove-Item : 找不到路径"D:\Scoop\apps\scoop\new"，因为该路径不存在。
      + CategoryInfo          : ObjectNotFound: (D:\Scoop\apps\scoop\new:String) [Remove-Item], ItemNotFoundException
      + FullyQualifiedErrorId : PathNotFound,Microsoft.PowerShell.Commands.RemoveItemCommand
  Scoop download failed. If this appears several times, try removing SCOOP_REPO by 'scoop config rm SCOOP_REPO'
  ```
  （原文中中文提示在终端里显示为乱码，此处按含义还原，英文行原样保留）
- 运行命令：`scoop install nodejs-lts`（Git Bash 调用 scoop shim，后台任务 ID `bash-yxv8yghd`）
- 相关代码或文件：无项目代码；涉及 Scoop 自身脚本 `D:\Scoop\apps\scoop\current\libexec\scoop-update.ps1:88`
- 定位过程：观察输出顺序——失败发生在安装开始前的 `Updating Scoop...`（Scoop 自更新）阶段；随后仍继续执行 `Installing 'nodejs-lts' (24.21.0) [64bit] from 'main' bucket`，并完成下载、哈希校验（`Checking hash of node-v24.21.0-win-x64.7z ... ok`）、解压、链接与 `'nodejs-lts' (24.21.0) was installed successfully!`；判定为**本机访问 github.com 被重置**导致的自更新失败，与目标软件安装本身无关。
- 修复方案：**未做修复**（无需修复，安装已成功）。若后续 Scoop 自更新持续失败且影响使用，可选方案：`scoop config rm SCOOP_REPO`、或 `scoop config aria2-enabled false` 关闭多线程下载。以上均**未执行**。
- 修复后验证：AI 实测 `node -v` → `v24.21.0`；`npm -v` → `11.19.0`（均成功）。
- 最终结果：**非阻塞**。Node.js LTS 24.21.0 安装成功，Scoop 自更新失败未处理（属环境层问题，不影响本项目）。本条是本项目**第一份真实报错记录**。

### 报错记录 2：浏览器访问 http://127.0.0.1:5173 被拒绝（Windows 下 Vite 只绑定 IPv6）
> 专档见 `docs/debug-log.md` 报错记录 2；本条为同步条目（**补记**：在阶段 2 批 1 期间应作者要求补齐，内容与原记录一致）。

- 报错原文：作者原话"在验证时，localhost 拒绝了我的链接"，随后补充"第一个打不开，另两个可以，未开代理"（附 dev server 输出：`VITE v8.3.0 ready in 220 ms`、`Local: http://localhost:5173/`）。
- 运行命令：作者终端 `cd frontend && npm run dev`；AI 诊断命令 `netstat -ano | grep ":5173"` 与三条 `curl`（分别测 `localhost` / `127.0.0.1` / `[::1]`）。
- 相关代码或文件：`frontend/vite.config.js` 的 `server` 块（原先未设置 `host`）。
- 定位过程：先只读探测排除"服务根本没运行"；再依据作者提供的 `TCP [::1]:5173 LISTENING 31236` 与 `127.0.0.1 -> 000` 判定 Vite 只绑定 IPv6 `::1`；同时排除代理因素（注册表 `ProxyEnable = 0x0`）。
- 修复方案：经作者在三选一中确认，修改 `frontend/vite.config.js` 新增 `host: '127.0.0.1'`（**不采用** `host: true`，避免把开发服务器暴露到局域网）。
- 修复后验证：实测 `http://127.0.0.1:5173/ → 200`、`http://localhost:5173/ → 200`、`http://[::1]:5173/ → 000`（预期变化）；AI 自起的测试实例已 kill，5174 无残留监听。
- 最终结果：**已修复**（AI 侧实测通过；作者浏览器复验未反馈，标记为"待我验证"）。

### 报错记录 3：`cd: backend: No such file or directory` + `curl: (7)` 连不上 8080（工作目录错误，非代码缺陷）
> 专档见 `docs/debug-log.md` 报错记录 3；本条为同步条目（**补记**，同上）。

- 报错原文：作者报告"第一项测试报错：`bash: cd: backend: No such file or directory`；第二项测试报错：同上；`curl: (7) Failed to connect to localhost:8080 after 2203 ms`"，并补充确认"当时终端在默认目录，这是我的问题"。
- 运行命令：作者按 AI 交付的验证命令执行 `cd backend && ./mvnw -B -ntp compile`（及启动后端命令）与 `curl -i http://localhost:8080/api/not-exist`。
- 相关代码或文件：与项目代码无关；问题在**终端所在的工作目录**（相对路径 `cd backend` 的前提是终端已在项目根目录）。
- 定位过程：AI 在 `$HOME`（`/c/Users/19032`）复现 `cd backend` → 得到与作者**完全一致**的报错；`curl (7)` 是 `&&` 短路导致后端从未启动的连带结果，非端口或代理问题。
- 修复方案：改用绝对路径（Git Bash `cd "/d/code/Additional Full-stack Development of Personal blogs/backend"`；PowerShell `cd "D:\code\Additional Full-stack Development of Personal blogs\backend"; .\mvnw.cmd ...`），并在 `README.md`《常见问题排查》表新增该现象一行。**无代码改动**。
- 修复后验证：AI 侧在非项目目录用绝对路径 → `BUILD SUCCESS`（1.074 秒）；作者侧复验 → 终端 A `Started BlogApplication in 1.663 seconds`（PID 29924）、终端 B curl 输出正常，终端 A 同步出现 `GlobalExceptionHandler : 接口不存在：api/not-exist`。
- 最终结果：**已修复并双方实测通过**。

### 报错记录 4：`curl` 传中文参数导致查询串解码失败 → 50000（工具 / 编码类，非项目缺陷）
> 专档见 `docs/debug-log.md` 报错记录 4；本条为同步条目。

- 报错原文：服务端日志 `org.apache.tomcat.util.http.InvalidParameterException: Character decoding failed. Parameter [tags] ... has been ignored`，`Caused by: java.nio.charset.MalformedInputException: Input length = 1`；接口响应 `{"code":50000,"message":"服务端未预期异常","data":null}`。
- 运行命令：`curl -s --get --data-urlencode "tags=项目日志,Vue" --data-urlencode "tagMode=or" http://localhost:8080/api/articles`
- 相关代码或文件：`common/GlobalExceptionHandler.java`（缺少"查询串解码失败"处理器，异常落到兜底 50000）；与 `ArticleRepository` / `ArticleService` 的过滤逻辑无关。
- 定位过程：把同样的标签改用预编码 UTF-8 百分号串请求 → `total=3`（说明过滤逻辑正确）；`curl -v` 显示实际发出的是 `%CF%EE%C4%BF%C8%D5%D6%BE`（**GBK** 编码的"项目日志"）；而同一行命令中 `printf '%s' "项目日志" | xxd` 得到的是合法 UTF-8（`e9a1 b9e7 9bae e697 a5e5 bf97`）→ 差异来自可执行文件类型：`/mingw64/bin/curl` 是 Windows CRT 程序（参数按系统 ANSI 代码页 CP936 转换），`/usr/bin/printf` 是 msys2 程序（UTF-8）。
- 修复方案：① 服务端新增 `InvalidParameterException` → `40002` 处理器，并把 javadoc 中"请求方法不支持（405）"与实际行为对齐；② 测试方法改为使用预编码 UTF-8 百分号串，或改用 PowerShell / 浏览器验证。
- 修复后验证：重跑同一条 GBK 命令 → `HTTP 400` + `{"code":40002,"message":"查询参数编码非法，请使用 UTF-8 百分号编码","data":null}`；预编码串复测 `or → total=3`、`and → total=1` 正常。
- 最终结果：**已处理**（服务端错误码更准确 + 测试方法已记录；作者侧复验未反馈，标记为"待我验证"）。

### 报错记录 5：浏览器打不开 http://localhost:8080/swagger-ui/index.html（后端未运行）
> 专档见 `docs/debug-log.md` 报错记录 5；本条为同步条目。

- 报错原文：作者原话"http://localhost:8080/swagger-ui/index.html 拒绝了我的链接"。
- 运行命令：作者浏览器访问该地址；AI 诊断 `netstat -ano | grep ":8080"`、`tasklist //FI "IMAGENAME eq java.exe"`、`curl -w "%{http_code}" http://localhost:8080/api/health`。
- 相关代码或文件：与项目代码无关；阶段 2 每批实测结束后 AI 都会停服并清理派生 JVM，故"访问时服务未运行"属常态。
- 定位过程：`netstat` 无 8080 监听、`tasklist` 无 java 进程、`curl` 返回 `HTTP 000`（退出码 7）→ 判定为"服务根本没启动"，排除代理 / IPv6 / 端口占用。
- 修复方案：重新启动后端（Swagger UI 只在运行期间可访问），并在 `README.md`《常见问题排查》新增该现象一行。
- 修复后验证：`GET /api/health → 200`、`GET /swagger-ui/index.html → 200`（`<title>Swagger UI</title>`）、`GET /v3/api-docs → 200`；`netstat` 显示 `0.0.0.0:8080 LISTENING`（PID 24680）。作者侧浏览器复验未反馈，标记为"待我验证"。
- 最终结果：**已修复**（AI 侧实测通过）。根因：后端未运行——"浏览器拒绝连接"在本项目已出现三次，排查第一步应始终是"确认服务是否在监听"。

---

## 审计记录

**未发生。** 截至阶段 1 结束，项目骨架与配置已全部跑通（前端 `npm run build` 通过、后端 `spring-boot:run` 启动成功、Swagger 可访问），但业务功能尚未实现，故专项审计未开始。按项目要求，审计安排在阶段 9 执行，模板与 12 项检查清单见 `docs/audit-report.md`。

---

## 关键提示词汇总

> 与 `docs/collaboration-log.md` 的汇总表**逐字同步**。项目要求"至少 4 组关键提示词"，当前**已发生 4 组**（编号 1、2、3、5）；编号 4（功能迭代）与 6（项目审计）待真实发生后补写，不编造。

| 编号 | 阶段 | 提示词摘要 | AI 做了什么 | 作用 |
|---|---|---|---|---|
| 1 | 项目启动 | 主指令：前端必须 JavaScript、后端必须 Java、持久化只用轻量方案（SQLite/文件）；**禁止使用现成博客模板或整站主题**；分阶段推进且每阶段等确认；固定回复格式（阶段目标/需求理解/改动文件/命令/验证/协作记录/待确认）；要求 ≥4 组关键提示词、≥2 次功能迭代、1 次真实报错记录、1 次前后端专项审计；禁止编造运行结果、禁止硬编码密钥、禁止危险删除 | 只读探测环境（Node/npm/Maven 缺失、仅有 JDK 26、依赖源可达）、查证 Spring Initializr 可用版本、给出选型（Vue 3 + Vite + 原生 CSS 变量；Spring Boot 4.1.1 + JdbcTemplate + SQLite；mvnw 免装 Maven）、monorepo 目录结构、0–9 阶段路线、11 个待确认问题、9 条风险 | **确定了技术栈与执行节奏**，并把"保留协作过程"变成硬约束 |
| 2 | 协作流程 | 兼任"AI 协作日志记录员"：五类触发场景；阶段/报错/审计/关键提示词四套模板；只输出可追加片段、不输出整份文件；禁止编造报错、编造测试结果、把未运行说成已通过 | 确认职责与触发条件、逐字对齐四套模板、列出 9 条禁止事项、确定 `ai-log.md`（流水）与 `collaboration-log.md`（汇总）分工、明确日志不写日期 | **让协作过程可交付、可审计**，避免"只交最后代码" |
| 3 | 架构落地 | 给出完整目录架构与分批规则：先出"创建计划"、确认后按 4 批执行、每批停下等确认；每文件说明"路径 + 占位还是完整 + 验证命令"；禁止一次性生成全部业务代码、禁止硬编码密钥、不要扫描整个仓库；确认 A1/B1/C1/D1/E 五个决策；随后"批1通过，现在 git init，请继续"；"契约全同意 + 模型全同意，另外项均同意" | 输出 22 目录 / 约 38 文件创建计划与 5 个决策点；完成批 1–4：根目录三文件、docs 九文件、frontend 十二文件、backend 十五文件；执行 `git init` 与首次提交 `501065a`；新增 `.gitattributes`；把契约与数据模型升级为 v1.0 已确认；安装 Node 24.21.0；实测前端 install/build/dev 与后端启动、建表、种子数据、Swagger | **把架构落到磁盘并全部实测通过**，确立"分批交付 + 每批可验证"的节奏 |
| 4 | 功能迭代 | 待发生（计划：前端阅读进度条、回到顶部按钮、无限滚动 + 骨架屏；后端分页增强、标题关键词搜索、统一错误返回格式） | 待发生 | 待发生 |
| 5 | 报错修复 | 作者报告"在验证时，localhost 拒绝了我的链接"，并按 AI 要求提供真实证据（dev server 启动输出、`netstat` 与三条 `curl` 实测结果、"未开代理"），随后在三选一中确认"加 `host: '127.0.0.1'`" | 先只读探测排除"服务根本没运行"；再依据 `[::1]:5173 LISTENING` + `127.0.0.1 -> 000` 判定 Vite 只绑定 IPv6；修改 `frontend/vite.config.js` 一行；实测 `127.0.0.1 → 200`、`localhost → 200`、`[::1] → 000`（预期）；把完整闭环写入 `docs/debug-log.md` 报错记录 2 | **修复了"浏览器打不开 127.0.0.1"的真实故障**，并沉淀一条可复用的排错记录（报错原文 → 定位 → 修复 → 验证） |
| 6 | 项目审计 | 待发生（计划在阶段 9，覆盖能否按 README 启动、6 个前端模块、6 个后端功能、响应式、深色对比度、输入校验、敏感信息、数据读写、重复代码、死代码、未使用依赖） | 待发生 | 待发生 |
