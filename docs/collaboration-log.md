# 协作记录（关键提示词与阶段索引）

> **与 `docs/ai-log.md` 的关系**：`ai-log.md` 是逐轮流水日志（唯一真相来源）；本文件是**成品视图**——把关键提示词汇总成可直接用于交付/答辩的表格，并给出阶段索引、报错/审计索引与阶段记录。
> 两处的"关键提示词汇总"表保持同步；如出现不一致，**以 `docs/ai-log.md` 为准**。
> 最后更新：阶段 0–3 均已全部完成并入库（阶段 3 的点按路径与过渡观感已由作者验收）；**阶段 4「前端模块二 / 三：文章列表与文章详情」批 0–批 2 已入库（`ec848f3`、`b6f7701`、`af913f0` + 文档 `bc89da3`），批 3（文章详情 + Markdown 渲染）已完成且 14 项管线断言 + 6 项页面实测通过、改动尚未提交**，下一步批 4（TOC + 滚动高亮 + 进场动画 + 图片懒加载）。

---

## 一、关键提示词汇总

> 项目要求至少 4 组关键提示词。当前**已发生 4 组**（编号 1、2、3、5）；编号 4（功能迭代）与 6（项目审计）会在真实发生后补写，**不提前编造内容**。

| 编号 | 阶段 | 提示词摘要 | AI 做了什么 | 作用 |
|---|---|---|---|---|
| 1 | 项目启动 | 主指令：前端必须 JavaScript、后端必须 Java、持久化只用轻量方案（SQLite/文件）；**禁止使用现成博客模板或整站主题**；分阶段推进且每阶段等确认；固定回复格式（阶段目标/需求理解/改动文件/命令/验证/协作记录/待确认）；要求 ≥4 组关键提示词、≥2 次功能迭代、1 次真实报错记录、1 次前后端专项审计；禁止编造运行结果、禁止硬编码密钥、禁止危险删除 | 只读探测环境（Node/npm/Maven 缺失、仅有 JDK 26、依赖源可达）、查证 Spring Initializr 可用版本、给出选型（Vue 3 + Vite + 原生 CSS 变量；Spring Boot 4.1.1 + JdbcTemplate + SQLite；mvnw 免装 Maven）、monorepo 目录结构、0–9 阶段路线、11 个待确认问题、9 条风险 | **确定了技术栈与执行节奏**，并把"保留协作过程"变成硬约束 |
| 2 | 协作流程 | 兼任"AI 协作日志记录员"：五类触发场景；阶段/报错/审计/关键提示词四套模板；只输出可追加片段、不输出整份文件；禁止编造报错、编造测试结果、把未运行说成已通过 | 确认职责与触发条件、逐字对齐四套模板、列出 9 条禁止事项、确定 `ai-log.md`（流水）与 `collaboration-log.md`（汇总）分工、明确日志不写日期 | **让协作过程可交付、可审计**，避免"只交最后代码" |
| 3 | 架构落地 | 给出完整目录架构与分批规则：先出"创建计划"、确认后按 4 批执行、每批停下等确认；每文件说明"路径 + 占位还是完整 + 验证命令"；禁止一次性生成全部业务代码、禁止硬编码密钥、不要扫描整个仓库；确认 A1/B1/C1/D1/E 五个决策；随后"批1通过，现在 git init，请继续"；"契约全同意 + 模型全同意，另外项均同意" | 输出 22 目录 / 约 38 文件创建计划与 5 个决策点；完成批 1–4：根目录三文件、docs 九文件、frontend 十二文件、backend 十五文件；执行 `git init` 与首次提交 `501065a`；新增 `.gitattributes`；把契约与数据模型升级为 v1.0 已确认；安装 Node 24.21.0；实测前端 install/build/dev 与后端启动、建表、种子数据、Swagger | **把架构落到磁盘并全部实测通过**，确立"分批交付 + 每批可验证"的节奏 |
| 4 | 功能迭代 | 待发生（计划：前端阅读进度条、回到顶部按钮、无限滚动 + 骨架屏；后端分页增强、标题关键词搜索、统一错误返回格式） | 待发生 | 待发生 |
| 5 | 报错修复 | 作者报告"在验证时，localhost 拒绝了我的链接"，并按 AI 要求提供真实证据（dev server 启动输出、`netstat` 与三条 `curl` 实测结果、"未开代理"），随后在三选一中确认"加 `host: '127.0.0.1'`" | 先只读探测排除"服务根本没运行"；再依据 `[::1]:5173 LISTENING` + `127.0.0.1 -> 000` 判定 Vite 只绑定 IPv6；修改 `frontend/vite.config.js` 一行；实测 `127.0.0.1 → 200`、`localhost → 200`、`[::1] → 000`（预期）；把完整闭环写入 `docs/debug-log.md` 报错记录 2 | **修复了"浏览器打不开 127.0.0.1"的真实故障**，并沉淀一条可复用的排错记录（报错原文 → 定位 → 修复 → 验证） |
| 6 | 项目审计 | 待发生（计划在阶段 9，覆盖能否按 README 启动、6 个前端模块、6 个后端功能、响应式、深色对比度、输入校验、敏感信息、数据读写、重复代码、死代码、未使用依赖） | 待发生 | 待发生 |

---

## 二、阶段索引

| 阶段 | 名称 | 状态 | 记录位置 |
|---|---|---|---|
| 0 | 需求确认与技术选型 | 已完成（未写代码） | `ai-log.md` → 阶段 0 |
| 0.5 | 协作日志规范确认 | 已完成（问答轮） | `ai-log.md` → 阶段 0.5 |
| 1 | 目录结构与占位文件 | **已完成**：批 1 ✅ ／ 批 2 ✅ ／ 批 3 ✅ ／ 批 4 ✅（作者全部确认通过） | 本文件 → 五、阶段记录；`ai-log.md` → 阶段 1 |
| 2 | 后端业务实现（6 项基础 API + 统一返回 + Swagger） | **已完成**：批 0 ✅ ／批 1 ✅ ／批 2a ✅ ／批 2b ✅ ／批 3 ✅ ／批 4 ✅（待作者确认） | 本文件 → 五、阶段记录；`ai-log.md` → 阶段 2 |
| 3 | 前端搭建（模块一：导航与主题） | **已完成**：批 0 ✅ ／ 批 1 ✅ ／ 批 2 ✅ ／ 批 3 ✅ ／ 批 4 ✅（**作者已确认**） | 本文件 → 五、阶段记录；`ai-log.md` → 阶段 3 |
| 4 | 前端模块二 / 三（列表、详情、Markdown、TOC） | **进行中**：批 0 ✅（开工基线：`frontend/public/` 封面与 favicon、种子补到 12 篇、数据库重置重建）；批 1–5 未开始 | 本文件 → 五、阶段记录（阶段 4 收尾时补写）；`ai-log.md` → 阶段 4 |
| 5 | 前端模块四 / 五 / 六（搜索过滤、评论点赞、本地持久化） | 未开始 | — |
| 6 | 前后端对接（真实数据替换 mock） | 未开始 | — |
| 7 | 功能迭代一（前端体验：进度条、回到顶部、无限滚动 + 骨架屏） | 未开始 | — |
| 8 | 功能迭代二（后端能力：搜索、分页、草稿状态、评论 CRUD、统一错误、Swagger 完善） | 未开始 | — |
| 9 | 报错记录整理 + 前后端专项审计 + 交付文档与演示脚本 | 未开始 | — |

---

## 三、报错记录索引

| 编号 | 简述 | 类型 | 状态 | 详细记录位置 |
|---|---|---|---|---|
| 1 | scoop 自更新失败（`github.com` 连接被重置），Node 仍安装成功 | 环境类（非项目代码） | 已定位 · 非阻塞 · 无需修复 | `docs/debug-log.md` 报错记录 1 |
| 2 | 浏览器访问 `http://127.0.0.1:5173` 被拒绝（Windows 下 Vite 只绑定 IPv6） | 前端配置类（真实故障） | **已修复并实测通过** | `docs/debug-log.md` 报错记录 2 |
| 3 | 终端报 `cd: backend: No such file or directory` + `curl: (7)` 连不上 8080 | 使用 / 环境类（非代码缺陷） | **已修复并双方实测通过** | `docs/debug-log.md` 报错记录 3 |
| 4 | `curl` 传中文参数导致查询串解码失败 → 50000（GBK 参数编码） | 工具 / 编码类（非项目缺陷） | **已处理**（服务端改判 40002 + 测试方法已记录） | `docs/debug-log.md` 报错记录 4 |
| 5 | 浏览器打不开 `http://localhost:8080/swagger-ui/index.html`（后端未运行） | 使用类（非代码缺陷） | **已修复**（AI 侧实测 200，作者侧待复验） | `docs/debug-log.md` 报错记录 5 |
| 6 | 种子数据固定 tag ID 与历史残留行冲突（`id=5` 被阶段 2 测试标签占用）→ 「前端」标签未建成、4 条文章关联错位 | 数据类（项目种子文件 + 历史数据） | **已修复**（按标准路径重置数据库，复验幂等通过） | `docs/debug-log.md` 报错记录 6 |
| 7 | 后端启动失败：`APPLICATION FAILED TO START … Port 8080 was already in use`（8080 上已有一个非本会话启动的实例） | 进程 / 环境类（非代码缺陷） | **已恢复**（确认端口空闲后重启，14 项接口层用例随后全通过） | `docs/debug-log.md` 报错记录 7 |

---

## 四、审计记录索引

| 审计轮次 | 状态 | 详细记录位置 |
|---|---|---|
| 第 1 次（前端 + 后端专项审计，阶段 9） | 未发生 | `docs/audit-report.md`（当前仅有模板与 12 项检查清单） |

---

## 五、阶段记录

### 阶段 0：需求确认与技术选型（摘要）
- 我的提示词摘要：首轮主指令，要求先不写代码，只给理解、选型理由、目录结构、分阶段路线、待确认问题与风险点。
- AI 做了什么：只读探测本机环境（Node/npm/Maven 缺失、只有 JDK 26、四个依赖源可达）；查证 Spring Initializr 稳定版与 Java 选项；给出选型、目录结构、0–9 阶段路线、11 个待确认问题、9 条风险。
- 改动文件：无（本轮为规划，无文件改动）
- 验证命令与结果：未运行（本阶段无代码）；环境探测为只读命令，结果见 `docs/current-state.md` 环境事实表。
- 遗留问题：11 个待确认问题；JDK 26 与 Boot 4.1.1 兼容性未实测（后于批 4 验证通过）。
- 下一步：阶段 1 接口契约与数据模型草案。

### 阶段 0.5：协作日志规范确认（摘要）
- 我的提示词摘要：兼任"AI 协作日志记录员"，明确四套模板、五类触发场景、只输出可追加片段、禁止编造报错与测试结果。
- AI 做了什么：确认职责与触发条件、逐字对齐模板、列 9 条禁止事项、确定双日志分工。
- 改动文件：无（本轮为规范确认）
- 验证命令与结果：未运行（无代码可验证）
- 遗留问题：`docs/ai-log.md` 当时尚未创建（后于批 2 创建）。
- 下一步：阶段 1 目录架构落地。

### 阶段 1：目录结构与占位文件（批 1–4，作者已确认通过）

- **我的提示词摘要**：
  - 架构与分批指令：按给定目录架构（根目录 3 文件 + `docs/` + `frontend/` + `backend/`）创建目录与占位文件；**不得擅自增删顶层目录**；先出"创建计划"再动手；分 4 批执行、每批结束停下等确认；每个文件说明"路径 + 占位还是完整 + 验证命令"；**禁止一次性生成全部业务代码、禁止使用现成博客模板、禁止硬编码密钥、禁止编造运行结果、不要扫描整个仓库**。
  - 决策确认：A1 沿用当前目录／B1 同时维护 `ai-log.md` 与 `collaboration-log.md`／C1 `mvnw` 取自 start.spring.io 官方骨架／D1 增加第 5 张关联表 `article_tag`／E 锁定 Vue 3 + Vite + Spring Boot + JdbcTemplate + SQLite 并授权 AI 安装 Node；种子文章先 2–3 篇；后续追加确认：契约 8 项与模型 5 项全通过、允许新增 `.gitattributes`、执行首次提交；报错修复时确认采用 `host: '127.0.0.1'`。
- **AI 做了什么**：
  1. **批 1（根目录）**：创建 `README.md`（技术栈 / 启动命令 / 目录结构 / 功能清单含"待实现"标注 / 数据位置与重置 / 访问地址 / 文档索引 / 风险清单）、`.gitignore`（六类规则，含 `!maven-wrapper.jar` 反向规则与 `backend/data/*.db` 精确忽略）、`AGENTS.md`（硬约束、后端分层职责、契约索引、协作规则、8 条禁止事项）。
  2. **批 2（docs）**：创建 `main-prompt.md`（原文照录三轮指令）、`api-contract.md`（17 接口 + 统一响应 + 7 错误码 + 8 项待确认）、`data-model.md`（5 张表 + 索引 + 幂等种子策略 + 5 项待确认）、`current-state.md`、`debug-log.md`、`audit-report.md`（12 项审计清单）、`demo/.gitkeep`、`ai-log.md`、`collaboration-log.md`；执行 `git init`（分支 `main`）；安装并实测 Node.js。
  3. **批 2 收尾**：把两份草案升级为 **v1.0 已确认**并写入确认记录表；新增 `.gitattributes`；完成首次提交 `501065a`。
  4. **批 3（frontend）**：用 `curl` 查 npm registry 实时版本（发现 vue-router 已 5.x、pinia 4.x、vite 8.x，并核对 peer 依赖的可选性），创建 12 个文件（`index.html` 含主题防闪脚本、`package.json`、`vite.config.js` 含 `/api` 代理与 IPv4 绑定、`main.js`、`App.vue`、`router/index.js`、`HomeView.vue`、`base.css` 含亮暗 CSS 变量、4 个 `.gitkeep`）。
  5. **报错修复（2 起）**：scoop 自更新失败（`github.com` 不可达，非阻塞）；浏览器 `127.0.0.1` 被拒绝（Vite 只绑 IPv6，改一行配置修复）。
  6. **批 4（backend）**：读 start.spring.io 官方骨架后发现 **Spring Boot 4 的 Web 起步依赖更名为 `spring-boot-starter-webmvc`**（Boot 3 为 `-web`），据此修正坐标；核对 `sqlite-jdbc 3.53.4.0`、`springdoc 3.1.1`（父 POM 声明 Boot 4.1.0）；复制官方 `mvnw` 三件套；创建 15 个文件（`pom.xml`、`BlogApplication.java`、`application.yml`、`schema.sql`（5 张表 + 索引）、`data.sql`（3 篇原创文章 + 4 标签 + 5 关联，固定 ID + `INSERT OR IGNORE` 幂等）、`data/.gitkeep` + 6 个包 `.gitkeep`）。
  7. **批 4 验证与收尾**：跑通后端启动与数据链路；清理被 kill 后残留的派生 JVM 孤儿进程；同步 `docs/current-state.md`。
- **改动文件**：
  - 根目录：`README.md`、`.gitignore`、`AGENTS.md`、`.gitattributes`
  - `docs/`：`main-prompt.md`、`current-state.md`、`api-contract.md`、`data-model.md`、`collaboration-log.md`、`ai-log.md`、`debug-log.md`、`audit-report.md`、`demo/.gitkeep`
  - `frontend/`：`index.html`、`package.json`、`vite.config.js`、`src/main.js`、`src/App.vue`、`src/router/index.js`、`src/views/HomeView.vue`、`src/styles/base.css`、`src/{stores,api,components,utils}/.gitkeep`
  - `backend/`：`pom.xml`、`mvnw`、`mvnw.cmd`、`.mvn/wrapper/maven-wrapper.properties`、`src/main/java/com/example/blog/BlogApplication.java`、6 个包 `.gitkeep`、`src/main/resources/{application.yml,schema.sql,data.sql}`、`data/.gitkeep`
- **验证命令与结果**（全部为实测输出）：
  - `cd frontend && npm install --no-fund --no-audit` → `added 81 packages in 10m`
  - `npm run build` → `✓ 28 modules transformed`、`✓ built in 104ms`（首页被切成独立 chunk，路由懒加载生效）
  - dev server 冒烟 → `GET / → 200`、`<title>个人博客 · VibeCoding</title>`、`/src/main.js` 返回转译后 ESM、`/src/App.vue` 返回编译后 SFC
  - 修复后 → `http://127.0.0.1:5173/ → 200`、`http://localhost:5173/ → 200`、`[::1] → 000`（预期）
  - `cd backend && ./mvnw -B -ntp spring-boot:run` → `Java 26.0.2.1` 上 `Spring Boot v4.1.1 / Spring v7.0.9 / Tomcat 11.0.24`，`Started BlogApplication in 1.815 seconds`
  - JDBC 只读检查 → 6 张表 + 7 个索引；`article=3`、`tag=4`、`article_tag=5`；**两次启动后数量一致**（幂等 + 持久化）
  - `curl /v3/api-docs → 200`、`curl /swagger-ui/index.html → 200`
  - `git commit` → `501065a chore: 初始化项目骨架（根目录文件 + docs 文档骨架）`
  - `git commit` → `25e280e feat: 完成 frontend/backend 骨架并修复 dev server IPv4 绑定`（批 3、批 4 与 IPv4 修复合并入库，31 个文件）
  - 作者侧：批 1、批 2、批 3、批 4 均已回复"通过"
- **遗留问题**：见 `docs/current-state.md` 第五节。已解决：JDK 26 兼容性、Node 安装、后端启动与建表验证、第二次 git 提交（`25e280e`）、`docs/ai-log.md` 缺失的批 3/批 4 逐轮记录与报错记录 2/3（已按本文件记录补记）。未处理：`frontend/public/` 目录、全量种子文章（约 12 篇）、外键级联删除的实测、结束后台任务会残留派生 JVM、`--enable-native-access` 警告。
- **下一步**：阶段 2 —— 后端五层业务实现（`common` / `model` / `repository` / `service` / `controller`），按 `docs/api-contract.md` v1.0 实现 health、文章 CRUD 与分页、标签、评论、点赞，并补 Swagger 注解与示例。

### 阶段 2：后端业务实现（批 0–4，全部实测通过 · 作者已确认）

- **我的提示词摘要**：
  - 新会话开场要求"先不要写代码"，只读 6 份文档后用不超过 10 行总结现状；随后"分批列出需要的事项"；确认把原批 2 拆成 **2a 读路径 / 2b 写路径**；之后逐批下达"批 N 通过，提交并继续"；阶段结束时要求"记录本阶段，并覆盖 `docs/current-state.md` 的完整内容"。
  - 期间作者报告并配合提供证据的真实故障 2 起：`cd: backend: No such file or directory`（终端目录错误）、浏览器打不开 Swagger UI（后端未启动）；另外要求补齐 `ai-log.md` 缺失的历史记录（批 3 / 批 4 阶段记录、报错记录 2 / 3）。
- **AI 做了什么**：
  1. **批 0（提交收尾）**：核对 git 现状，发现"第二次提交其实已存在"（`25e280e`），修正 `current-state.md`、`collaboration-log.md` 的过时描述，提交 `e9ab336`。
  2. **批 1（`common` + `model` + `config`）**：先用 `javap` 核实 Boot 4.1.1 实际携带 **Jackson 3**（`tools.jackson.*`）与可用扩展点，再写 19 个文件（错误码 / 统一返回 / 业务异常 / 全局异常处理；4 实体 + 4 请求 DTO + 6 响应 VO；`JacksonConfig`），删 2 个占位。提交 `92a5cc6`。
  3. **批 2a（文章读路径）**：新增 `ArticleRepository` / `TagRepository` / `ArticleService` / `TagService` / `HealthController` / `ArticleController` / `TagController` 与计划外的 `common/TimeFormats.java`（时间格式收敛为唯一定义）；13 项 curl 实测；实测中定位真实报错（mingw64 版 `curl` 把中文参数按 GBK 编码 → 服务端 UTF-8 解码失败 → 50000），给 `GlobalExceptionHandler` 补 `InvalidParameterException → 40002` 后复测通过。提交 `b22592d`。
  4. **批 2b（文章写路径）**：补齐 insert / update / delete / 标签自动创建与清空 / 事务；用临时 JDBC 程序构造数据后实测 **外键级联删除真实生效**（遗留项 12 关闭）、重启后数据仍在（持久化）、摘要按码点截取 120 字。提交 `5c210a3`。
  5. **批 3（评论与点赞）**：新增 `CommentRepository` / `LikeRepository` / `CommentService` / `LikeService` / `CommentController` / `LikeController` 与计划外的 `common/PageParams.java`（消除分页校验重复代码）；19 项实测，含点赞 / 取消**两端幂等**、visitorId 归属校验；按作者选择用接口删除上批实测文章 5。提交 `4b1700b`。
  6. **批 4（收尾）**：新增 `OpenApiConfig`，给 5 个 controller 补 `@Tag` / `@Operation` / `@Parameter`；JDBC URL 追加 `busy_timeout=5000` 并实测生效；`README.md` 常见问题表补 native-access 一行；复测异常兜底 4 项。提交 `7174838`。
- **改动文件**：新增 35 个 Java 文件、修改 7 个 Java 文件（另修改 `backend/src/main/resources/application.yml`、`README.md`）；`docs/` 下四份文档多轮同步。
- **验证命令与结果**（均有真实输出留档，详见 `ai-log.md` 各批）：`./mvnw -B -ntp compile` 每批 `BUILD SUCCESS`（最终 36 个源文件）；接口实测累计 **13 + 16 + 19 + 4 = 52 项**全部通过；级联删除、点赞幂等、时间格式 `2026-09-07T09:00:00`、未知路径 → 40004、方法不支持 → 40002、`PARAM` 校验 → 40001、`busy_timeout = 5000` 均实测通过。
- **遗留问题**：见 `docs/current-state.md` 第五节。已解决：JDK 26 兼容性、Node 安装、第二次 git 提交、外键级联、契约相关遗留项 9（busy_timeout）与 12；剩余：WAL 未启用、`frontend/public/` 未创建、种子文章仅 3 篇、无鉴权（演示项目）、路径含空格。
- **下一步**：阶段 3 —— 前端模块一（响应式导航、汉堡菜单、深浅色主题切换、当前页高亮、路由过渡）。

### 阶段 3：前端模块一（全局导航与主题，批 0–批 4，实测通过 · 作者已确认入库）

- **我的提示词摘要**：新会话开场要求"**先不要写代码**"，先读 6 份文件后用不超过 10 行总结现状；随后"分批列出需要的事项"；对阶段 3 分批方案（批 0–4）与 8 个决策点逐条确认（**1 同意 / 2 增加 404 页 / 3 三态主题 / 4 同意 / 5 同意 / 6 与 7 同意 / 8 不拆**）；之后逐批下达"批 N 通过，提交并继续"。
- **AI 做了什么**：
  1. **批 0（开工基线）**：更正 `README.md` 顶部状态块（阶段 1 / Node 未装 / 首次提交待执行 / 契约仍是草案等过时描述），同步 `current-state.md`、`collaboration-log.md`，追加 `ai-log.md` 条目；`npm run build` 基线 28 模块 / 160ms；提交 `8c5923d`。
  2. **批 1（主题系统）**：新建 `stores/theme.js`（三态 + `localStorage['blog:theme']` + `matchMedia` 监听）、`components/ThemeToggle.vue`（内联 SVG 图标 + `aria-label` + live region）；`base.css` 扩展为完整主题令牌（语义色 / 阴影 / 尺寸 / 动效变量 / `color-scheme` / `:focus-visible` / `.visually-hidden` / reduced-motion 归零）；`App.vue` 临时挂载并初始化 store；删除 2 个 `.gitkeep`；提交 `7de72da`。
  3. **批 2（导航栏 + 路由 + 404）**：新建 `AppHeader.vue`（品牌 + 首页 / 文章列表 / 关于 + 右侧主题按钮，`aria-current="page"` 高亮）与 `NotFoundView.vue`（完整 404）；`ArticlesView.vue` / `AboutView.vue` 为占位页；`router/index.js` 新增三条路由并在 `afterEach` 同步 `document.title`；`App.vue` 改三段式布局；`base.css` 加 `.page`；提交 `b257499`。
  4. **批 3（移动端汉堡菜单 + 滚动样式变化）**：`AppHeader.vue` 重写（`aria-expanded` / `aria-controls`、Esc 关闭、遮罩点击、焦点进出、路由与断点变化自动收起、滚动超阈值后页头半透明 + `backdrop-filter`）；`base.css` 加 `--color-bg-header`；遮罩放在 `<header>` 之外以规避 `backdrop-filter` 成为 fixed 定位包含块；提交 `b1a517f`。
  5. **批 4（路由过渡 + 收尾）**：新建 `AppFooter.vue`（页脚从 `App.vue` 拆出）；`App.vue` 加 `<RouterView v-slot>` + `<Transition name="page" mode="out-in">`；`base.css` 加 `.page-*` 过渡类（时长走令牌，故 reduced-motion 下自动失效）；同步 `README.md`（前端模块表、后端功能表、已知问题）与 `docs/*`（`current-state.md` 整份覆盖、`ai-log.md`、`debug-log.md` 观察项、本文件）。
- **改动文件**：新建（完整）`frontend/src/stores/theme.js`、`components/{ThemeToggle,AppHeader,AppFooter}.vue`、`views/NotFoundView.vue`；新建（占位）`views/{ArticlesView,AboutView}.vue`；修改（完整）`frontend/src/{App.vue,router/index.js,styles/base.css}`；删除 2 个 `.gitkeep`；`README.md` 与 `docs/{current-state,ai-log,collaboration-log,debug-log}.md` 多轮同步。
- **验证命令与结果**（均有真实输出留档，详见 `ai-log.md` 阶段 3 各批）：每批 `npm run build` 通过（28 → 31 → 39 模块，最终 `✓ built in 142ms`、`index css 6.46 kB`、主包 `106.33 kB / gzip 41.62 kB`，四个视图各自独立 chunk）；浏览器实测覆盖：三态主题循环与刷新不丢、四路径标题与当前页高亮、404 页、375×667 折叠与键盘开合、Esc 关闭、滚动后页头半透明模糊、桌面视口无回归、路由过渡中间帧与稳定态。
- **遗留问题**：见 `docs/current-state.md` 第五节；新增 13（过渡观感与 reduced-motion 禁用待作者目视）与 14（触摸点按路径待真机补验）；第 10 条"派生进程残留"在阶段 3 又复现 4 次（node 占 5173）。
- **下一步**：阶段 4 —— 前端模块二 / 三：文章列表（卡片、分页或无限滚动、骨架屏、悬停动效）与文章详情（Markdown 渲染、代码高亮、阅读进度条、TOC、图片懒加载）；**开工前需向作者申请** `frontend/public/` 目录与补齐全量种子文章（约 12 篇）。
- **阶段收尾**（作者确认后）：批 0–3 依次提交 `8c5923d` / `7de72da` / `b257499` / `b1a517f`，批 4（含本文档更新）提交 `378e3b0`；本文件的阶段记录与 `current-state.md` 的整份覆盖随此后的一次文档提交入库（提交号见 `git log` 最新一条）。
