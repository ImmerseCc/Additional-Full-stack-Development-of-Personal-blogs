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
