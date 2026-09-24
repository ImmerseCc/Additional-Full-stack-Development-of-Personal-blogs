# 协作记录（关键提示词与阶段索引）

> **与 `docs/ai-log.md` 的关系**：`ai-log.md` 是逐轮流水日志（唯一真相来源）；本文件是**成品视图**——把关键提示词汇总成可直接用于交付/答辩的表格，并给出阶段索引、报错/审计索引与阶段记录。
> 两处的"关键提示词汇总"表保持同步；如出现不一致，**以 `docs/ai-log.md` 为准**。
> 最后更新：阶段 2 批 0（提交收尾）完成；阶段 2 批 1 待开工。

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
| 2 | 后端业务实现（6 项基础 API + 统一返回 + Swagger） | 进行中：批 0 ✅（提交收尾）／批 1 ✅ ／批 2a ✅（待作者确认）／批 2b–4 待开工 | `ai-log.md` → 阶段 2 |
| 3 | 前端搭建（模块一：导航与主题） | 未开始 | — |
| 4 | 前端模块二 / 三（列表、详情、Markdown、TOC） | 未开始 | — |
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
