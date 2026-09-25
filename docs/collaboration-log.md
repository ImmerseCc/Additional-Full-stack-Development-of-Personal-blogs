# 协作记录（关键提示词与阶段索引）

> **与 `docs/ai-log.md` 的关系**：`ai-log.md` 是逐轮流水日志（唯一真相来源）；本文件是**成品视图**——把关键提示词汇总成可直接用于交付/答辩的表格，并给出阶段索引、报错/审计索引与阶段记录。
> 两处的"关键提示词汇总"表保持同步；如出现不一致，**以 `docs/ai-log.md` 为准**。
> 最后更新：**阶段 7（功能迭代一）已开工 —— 分批方案（批 0–批 6）与决策点 BF–BO 经作者确认（"均同意，请继续"），批 0（开工基线）已完成**。阶段 0–5 均已全部完成并入库（阶段 3 的点按路径与过渡观感由作者验收；**阶段 5 已由作者人工验收通过，14 条验收清单逐条确认**）。**阶段 6 已开工：定位由作者拍板为"全链路回归 + 异常 / 空态演练 + 契约逐条复核"（决策 AU），批 0–批 5 已全部完成；**该阶段已由作者人工验收通过（13 条清单逐条确认，2026-09-25）**，完整记录见下方「五、阶段记录」。**

---

## 一、关键提示词汇总

> 项目要求至少 4 组关键提示词。当前**已发生 5 组**（编号 1、2、3、4、5）；编号 6（项目审计）会在真实发生后补写，**不提前编造内容**。

| 编号 | 阶段 | 提示词摘要 | AI 做了什么 | 作用 |
|---|---|---|---|---|
| 1 | 项目启动 | 主指令：前端必须 JavaScript、后端必须 Java、持久化只用轻量方案（SQLite/文件）；**禁止使用现成博客模板或整站主题**；分阶段推进且每阶段等确认；固定回复格式（阶段目标/需求理解/改动文件/命令/验证/协作记录/待确认）；要求 ≥4 组关键提示词、≥2 次功能迭代、1 次真实报错记录、1 次前后端专项审计；禁止编造运行结果、禁止硬编码密钥、禁止危险删除 | 只读探测环境（Node/npm/Maven 缺失、仅有 JDK 26、依赖源可达）、查证 Spring Initializr 可用版本、给出选型（Vue 3 + Vite + 原生 CSS 变量；Spring Boot 4.1.1 + JdbcTemplate + SQLite；mvnw 免装 Maven）、monorepo 目录结构、0–9 阶段路线、11 个待确认问题、9 条风险 | **确定了技术栈与执行节奏**，并把"保留协作过程"变成硬约束 |
| 2 | 协作流程 | 兼任"AI 协作日志记录员"：五类触发场景；阶段/报错/审计/关键提示词四套模板；只输出可追加片段、不输出整份文件；禁止编造报错、编造测试结果、把未运行说成已通过 | 确认职责与触发条件、逐字对齐四套模板、列出 9 条禁止事项、确定 `ai-log.md`（流水）与 `collaboration-log.md`（汇总）分工、明确日志不写日期 | **让协作过程可交付、可审计**，避免"只交最后代码" |
| 3 | 架构落地 | 给出完整目录架构与分批规则：先出"创建计划"、确认后按 4 批执行、每批停下等确认；每文件说明"路径 + 占位还是完整 + 验证命令"；禁止一次性生成全部业务代码、禁止硬编码密钥、不要扫描整个仓库；确认 A1/B1/C1/D1/E 五个决策；随后"批1通过，现在 git init，请继续"；"契约全同意 + 模型全同意，另外项均同意" | 输出 22 目录 / 约 38 文件创建计划与 5 个决策点；完成批 1–4：根目录三文件、docs 九文件、frontend 十二文件、backend 十五文件；执行 `git init` 与首次提交 `501065a`；新增 `.gitattributes`；把契约与数据模型升级为 v1.0 已确认；安装 Node 24.21.0；实测前端 install/build/dev 与后端启动、建表、种子数据、Swagger | **把架构落到磁盘并全部实测通过**，确立"分批交付 + 每批可验证"的节奏 |
| 4 | 功能迭代 | "**分批列出需要的事项**" → AI 给出阶段 5 分批方案与 14 个决策点，作者"**均同意，请继续**"；此后逐批"批 N 通过，请继续"；期间一次"**补充问题指你提出的**"（后台标签页进场动画观察项）与"**你提出的三条均确认**"（契约补注 / 重置语义 / 暂未引用模块） | 阶段 5 分 7 批交付模块四 / 五 / 六：搜索防抖 + 多选标签 + 空结果动画；评论（字段级校验 / 归属删除 / 加载更多）；点赞（**以后端为准** + 数字动画）+ Toast；本地数据面板 + 一键重置。累计 **25 项接口用例 + 31 项浏览器实测**，顺手关闭遗留 17 / 21，自查并修复 1 个缺陷（报错记录 9） | **把"功能迭代"从计划变成真实交付**：六个前端模块全部落地，且每批都留下可复现的实测证据 |
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
| 4 | 前端模块二 / 三（列表、详情、Markdown、TOC） | **已完成**：批 0 ✅ ／ 批 1 ✅ ／ 批 2 ✅ ／ 批 3 ✅ ／ 批 4 ✅ ／ 批 5 ✅（**作者已确认全部批次**） | 本文件 → 五、阶段记录；`ai-log.md` → 阶段 4 |
| 5 | 前端模块四 / 五 / 六（搜索过滤、评论点赞、本地持久化） | **已完成**：批 0 ✅ ／ 批 1 ✅ ／ 批 2 ✅ ／ 批 3 ✅ ／ 批 4 ✅ ／ 批 5 ✅ ／ 批 6 ✅（**作者已人工验收通过，14 条验收清单逐条确认**） | 本文件 → 五、阶段记录；`ai-log.md` → 阶段 5 |
| 6 | 全链路回归 + 异常 / 空态演练 + 契约逐条复核（**定位已由作者拍板**，替代原"前后端对接（真实数据替换 mock）"） | **已完成（批 0–批 5）**：批 0 ✅（开工基线）／ 批 1 ✅（契约逐条复核 + `frontend/scripts/smoke.mjs`，**97 项断言全通过**）／ 批 2 ✅（正常路径全链路回归，六模块，**作者已通过**）／ 批 3 ✅（异常 / 边界 / 安全演练）／ 批 4 ✅（非功能复核 + **决策 AX 全局错误兜底**）／ 批 5 ✅（收尾 + 13 条验收清单）—— **作者已人工验收通过（13 条清单逐条确认，2026-09-25）** | 本文件 → 五、阶段记录；`ai-log.md` → 阶段 6 批 0–批 5 |
| 7 | 功能迭代一（前端体验：进度条、回到顶部、无限滚动 + 骨架屏、窄屏目录折叠、详情页拆包、遗留 29 / 30 修正） | **进行中**：批 0–批 4 ✅（开工基线 / 阅读进度条 + 回到顶部 / 详情页 chunk 拆包 / 列表页无限滚动 + 骨架屏 / 窄屏目录折叠入口）／ 批 5–批 6 待推进 | 本文件 → 五、阶段记录（阶段收尾时补写）；分批方案与决策点见 `docs/current-state.md` §三 |
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
| 8 | 批 4 自查：`v-reveal` 把首屏内容永久藏在 `opacity:0`；详情页目录（TOC）永远为空 | 前端自查类（本批新代码引入） | **均已修复并浏览器复验**（视口内元素改为挂载即显现；取目录挪到 `finally` + `nextTick` 之后） | `docs/debug-log.md` 报错记录 8 |

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

### 阶段 4：前端模块二 / 三（文章列表与文章详情，批 0–批 5，实测通过 · 作者已确认入库）

- **我的提示词摘要**：新会话开场要求"**先不要写代码**"，先读 6 份文件后用不超过 10 行总结现状；随后"**遗留问题 1 已验收完毕，请继续，分批列出需要的事项**"；AI 给出阶段 4 分批方案（批 0–5）+ 3 项开工申请 + 10 个决策点，作者回复"**三项申请均同意；决策点 6 不必新增（依赖）；其他决策均同意**"；期间就"种子 tag ID 冲突"作者选择"**重置数据库**"；之后逐批确认："批 0 通过，请继续" / "确认决策 AB，批 1 通过，请继续" / "批 2 通过" / "批 3 通过" / "批 4 通过，请继续" / 最后"**批 5 通过，提交**"，并要求按阶段收尾惯例执行两件事：**追加本文件的阶段记录**、**覆盖 `docs/current-state.md` 的完整内容**（作者约定每个阶段结束后新开会话）。
- **AI 做了什么（按批）**：
  1. **批 0（开工基线）**：新建 `frontend/public/favicon.svg` + 12 张**自绘封面 SVG**（自绘图元、无 `<text>`、无外部引用，实拍 13 张全部正常渲染）；`data.sql` 由 3 篇扩为 **12 篇 + 8 标签 + 23 条关联**并加封面回填；`index.html` 引用 favicon；**按标准路径重置数据库**并复验幂等。过程中**发现并修复 1 个真实数据问题**（固定 tag ID 与阶段 2 残留行冲突导致「前端」标签未建成，报错记录 6）。
  2. **批 1（`src/api/` 接入层）**：`http.js`（统一 `/api` + 查询串 URI 编码 + 8s 超时 + 外部 signal 转发 + `{code,message,data}` 解包 + 五类失败归一）、`error.js`（`ApiError` + `isNotFound` / `isValidationError`）、`articles.js`、`tags.js`；**14 项真实调用用例 14/14 通过**（正例 8 + 异常 6）。期间记录 1 个真实启动故障（8080 被非本会话实例占用，报错记录 7）。
  3. **批 2（文章列表 + 首页门面）**：`ArticleCard` / `ArticleList`（四态容器）/ `ArticleSkeleton` / `Pagination` + `utils/date.js`；重写 `ArticlesView`（页码与 `?page=N` 双向同步、请求序号守卫丢弃过期响应）与 `HomeView`（决策 T）；先加 `/articles/:id` 路由 + 占位详情页（决策 AC）；**10 项浏览器实测通过**。
  4. **批 3（文章详情 + Markdown）**：`utils/markdown.js`（markdown-it `html:false` + `highlight.js/lib/common` + DOMPurify；核心规则补标题 id；外链 target+rel；未知语言转义不高亮）、`MarkdownRenderer`、`SkeletonBlock`（骨架原语，决策 AD）；`base.css` 追加 `--hl-*` 亮/暗配色（决策 V）；重写 `ArticleDetailView`（加载骨架 / 404 只给返回 / 错误可重试，决策 AE）；**15 项管线断言 + 6 项页面实测**。首轮 3 条断言 FAIL 经排查是**断言写错**（`html:false` 下原始标签被转义而非删除），修正后全绿。
  5. **批 4（加分项）**：`TableOfContents`（桌面固定栏 / 窄屏隐藏，决策 Y）、`utils/scrollSpy.js`（scroll + `getBoundingClientRect`）、`utils/reveal.js`（全局指令 `v-reveal`）、`markdown.js` 图片懒加载规则；列表卡片错落进场。**自查出并修复 2 个真实缺陷**（报错记录 8）：进场动画把首屏内容永久藏在 `opacity:0`（改为视口内元素挂载即显现）、详情页目录永远为空（取目录挪到 `finally` + `nextTick` 之后）。
  6. **批 5（收尾）**：构建终测 + 接口侧终验 + 浏览器终验（首页 / 深链第 2 页 / 路由 404 / 窄屏列表）+ `README.md` 功能清单与加分项状态更新 + 本文件阶段记录 + `current-state.md` 整份覆盖。
- **改动文件**：
  - 新建（完整）：`frontend/public/favicon.svg`、`frontend/public/images/covers/*.svg`（12 张）、`frontend/src/api/{http,error,articles,tags}.js`、`frontend/src/utils/{date,markdown,reveal,scrollSpy}.js`、`frontend/src/components/{ArticleCard,ArticleList,ArticleSkeleton,Pagination,SkeletonBlock,MarkdownRenderer,TableOfContents}.vue`
  - 修改（完整）：`frontend/src/{main.js,router/index.js,App.vue}`（App.vue 未变，路由改了）、`frontend/src/views/{HomeView,ArticlesView,ArticleDetailView}.vue`、`frontend/src/styles/base.css`、`frontend/index.html`、`backend/src/main/resources/data.sql`
  - 删除：`frontend/src/{api,utils}/.gitkeep`
  - 文档：`README.md`、`docs/{current-state,api-contract（未改字段）,collaboration-log,ai-log,debug-log}.md`
- **验证命令与结果**（均为实测输出，明细见 `ai-log.md` 各批与 `current-state.md` 第四节）：
  - 逐批 `npm run build`：41 → 55 → 119 → 123 模块（`✓ built in` 126 / 156 / 169 / 200 ms），详情页 chunk 286.22 kB 全在懒加载链上
  - 数据库与种子：`article=12`、`tag=8`、`article_tag=23`，封面 12/12，二次启动幂等
  - 接口层 14 项；Markdown 管线 15 项（含两层 XSS 防护与 DOMPurify 直接施压）；浏览器实测累计 **22 项**（列表 10 + 详情 6 + 加分项 6）+ 收尾终验 5 项
  - 作者侧：批 0–批 5 全部回复通过
- **遗留问题**：见 `docs/current-state.md` 第五节（共 23 条，其中 8 / 9 / 10 / 16–23 仍未闭环且不阻塞）。阶段 4 新增的未闭环项：无自动化回归（16）、`?page` 越界不回退（17）、骨架屏出现时机无法抓拍（18）、详情页无上一篇/下一篇（19）、详情 chunk 偏大（20）、Markdown 图片懒加载无真实内容可验证（21）、窄屏无目录入口（22）、阅读进度条与回到顶部留阶段 7（23）。
- **下一步**：**阶段 5 —— 前端模块四 / 五 / 六**：搜索与分类过滤（实时搜索 + 防抖、多选标签 + `tagMode`、空结果动画）、评论与点赞（表单校验、点赞动画、Toast）、本地持久化与一键重置（`blog:visitorId`、已点赞集合）；需新增 `src/api/comments.js` / `likes.js`。另需作者拍板：阶段 6 原定为"前后端对接（真实数据替换 mock）"，而阶段 4 起已直接联调真实后端（决策 R），建议改为"全链路回归 + 异常 / 空态演练 + 契约逐条复核"。

### 阶段 5：前端模块四 / 五 / 六（搜索与分类过滤、评论与点赞、本地持久化，批 0–批 6，实测通过 · **作者已人工验收通过**）

- **我的提示词摘要**：新会话开场要求"**先不要写代码**"，先读 6 份文件后用不超过 10 行总结现状；随后"**分批列出需要的事项**"；对 AI 给出的分批方案（批 0–6）+ 3 项开工申请 + 14 个决策点回复"**均同意，请继续**"；期间一次"**补充问题指你提出的**"（后台标签页进场动画观察项）与"**你提出的三条均确认**"（契约补注 / 重置语义 / 暂未引用模块）；此后逐批下达"批 N 通过，请继续"；最后明确"**批 6 我需要人工测试本阶段所有增设改动**"——**本阶段验收由作者自己完成，AI 不代签**。
- **AI 做了什么（按批）**：
  1. **批 0（开工基线）**：更正 `AGENTS.md` §2/§9 与 `README.md` 的过时条目（`Vue Router 4 → 5`、Node 已安装、JDK 26 已实测）；新增 `frontend/public/images/articles/markdown-pipeline.svg`（自绘单行 SVG）并给 `data.sql` 追加**幂等**正文配图回填（**关闭遗留 21**）；实测后端两次启动幂等（配图不重复追加）+ 真实种子内容过 `renderMarkdown()` 断言 9 项全绿；提交 `d6167ea`。
  2. **批 1（存储层 + 接入层）**：`utils/storage.js`（`blog:` 前缀统一封装、存储不可用静默降级）、`utils/visitor.js`（`crypto.randomUUID` + 兜底、**不做模块级缓存**）、`api/comments.js`、`api/likes.js`；`api/articles.js` 补参数规范化（数组 `join(',')`、`keyword` 去空白）；**25 项真实用例 25/25**；契约补注（likes 的 `visitorId` 必填）与观察项单独提交 `b12bcd4`；提交 `3a4a7c7`。
  3. **批 2（模块四）**：`utils/debounce.js`、`components/{SearchInput,TagFilter,EmptyState}.vue`；重写 `views/ArticlesView.vue`（四参数与地址栏同步、**改筛选 `replace` / 翻页 `push`**、**越界回退关闭遗留 17**）；`ArticleList` 空态接入 `EmptyState`；`ArticleCard` 标签改为可点链接（`z-index` 抬到覆盖层之上）；`base.css` 补通用控件类 `.btn / .input / .chip`；**8 项浏览器实测**；提交 `bf78030`。
  4. **批 3（模块五 A）**：`utils/validate.js`、`stores/myComments.js`（**决策 8** 本地账本）、`components/{CommentForm,CommentItem,CommentSection}.vue`；`utils/date.js` 增 `formatDateTime`；详情页挂评论区并回传总数；**8 项浏览器实测**（含"他人评论无删除入口"、真删复核、加载更多 11 条）；提交 `5979b18`。
  5. **批 4（模块五 B）**：`stores/{toast,likes}.js`、`components/{ToastStack,LikeButton}.vue`；`App.vue` 挂 Toast；`base.css` 增语义色 `--color-like`；详情页互动区 + meta 联动；**7 项浏览器实测**（含**停后端触发异常 Toast**、暗色配色、刷新后状态保持）；提交 `a8d499f`。
  6. **批 5（模块六）**：`components/LocalDataPanel.vue` + 重写 `views/AboutView.vue`；一键重置 = `clearAll()` + 各 store 归默认 + Toast 反馈；**实测中自查出 1 个缺陷**（"当前占用的键"读在主题默认值落盘之前，报错记录 9）→ 用 `await nextTick()` + `flush: 'post'` watcher 修复并复验；测试遗留的点赞记录用临时 JDBC 程序清理（验证后删除）；提交 `2726566`。
  7. **批 6（收尾）**：`npm run build` 终测 **270ms**；四份文档同步（`README.md` 功能清单 / 本地数据说明 / 文档索引 / 已知问题，`collaboration-log.md` 阶段索引与关键提示词 #4，`debug-log.md` 报错记录 9 与观察项，本文件）+ `current-state.md` 整份覆盖（含**14 条人工验收清单**）；**明确不做验收代签**。
- **改动文件**：
  - 新建（**完整**，22 个）：`frontend/src/utils/{storage,visitor,debounce,validate}.js`、`frontend/src/api/{comments,likes}.js`、`frontend/src/stores/{toast,likes,myComments}.js`、`frontend/src/components/{SearchInput,TagFilter,EmptyState,CommentForm,CommentItem,CommentSection,ToastStack,LikeButton,LocalDataPanel}.vue`、`frontend/public/images/articles/markdown-pipeline.svg`
  - 修改（**完整**）：`frontend/src/api/articles.js`、`frontend/src/utils/date.js`、`frontend/src/styles/base.css`、`frontend/src/App.vue`、`frontend/src/views/{ArticlesView,ArticleDetailView,AboutView}.vue`、`frontend/src/components/{ArticleList,ArticleCard}.vue`、`backend/src/main/resources/data.sql`
  - 根文档：`AGENTS.md`、`README.md`、`docs/{api-contract,debug-log,collaboration-log,ai-log,current-state}.md`
- **验证命令与结果**（均为实测输出，明细见 `ai-log.md` 各批）：
  - 逐批 `npm run build`：123 模块 / 216ms → 131 模块 / 236ms → 273ms → 290ms → 283ms → 终测 **270ms**；详情 chunk 297.51 kB、`AboutView 4.90 kB`、各视图独立 chunk
  - 接口层 **25 项**（列表参数规范化 / 评论增删 / 点赞三态幂等 / `40001`·`40002`·`40004` 分支 / `clearAll`）
  - 浏览器 **31 项**（列表 8 + 评论 8 + 点赞 7 + 本地数据 5 + 配图与幂等 3）
  - 数据侧：多次复核 `article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`（**测试数据全部还原**）
  - 作者侧：批 0–批 5 全部逐批回复通过；**批 6 交付的 14 条验收清单经作者人工逐条验收通过（阶段 5 整体验收通过）**
- **遗留问题**：见 `docs/current-state.md` 第五节 —— **关闭遗留 17（`?page` 越界回退）与遗留 21（正文图片懒加载无真实内容）**；新增 24–27 四条（Toast 截图未留档、重置后旧点赞无法自助撤销、合成点击偶发不送达、详情页 chunk 再增）。
- **下一步**：**阶段 6 开工**（作者按惯例新开会话继续，届时先出分批方案与决策点）；阶段 6 的定位（AI 建议改为"全链路回归 + 异常 / 空态演练 + 契约逐条复核"）**仍待作者拍板**。

### 阶段 6：全链路回归 + 异常 / 空态演练 + 契约逐条复核（批 0–批 5，已完成 × **作者已人工验收通过**，2026-09-25）

- **验收结论（作者，2026-09-25）**：交付的 **13 条验收清单逐条通过** —— 作者结论"**ok,验收通过**"；本阶段**无返工项**。验收后作者按惯例要求："**记录本阶段，并更新 `docs/current-state.md`**"（即本记录与 `current-state.md` 的本轮覆盖）。

- **我的提示词摘要**：
  - 新会话开场要求"**先不要写代码**"，先读 6 份文件后用不超过 10 行总结现状；
  - 随后"**请继续，分批列出需要的事项**"：AI 给出**定位建议（替代原"前后端对接 / 替换 mock"）+ 6 批方案 + 10 个决策点（AU–BD）**，作者回复"**均同意，请继续**"；
  - 逐批指令："**开始批 2**"、"**遗留28阶段 8 处理，批2通过，请继续**"（拍板决策 BE + 批 2 验收通过）、两次"**请继续**"（批 3、批 4）；
  - 阶段收尾按惯例要求：**追加本文件的阶段记录 + 覆盖 `docs/current-state.md` 的完整内容**。
  - **说明**：本阶段**未新增关键提示词编号**（`#6` 仍预留给阶段 9 的项目审计）；阶段 6 的提示词模式与 `#4` 同类（分批授权 + 逐批确认）。
- **AI 做了什么（按批）**：
  1. **批 0（开工基线）**：只读探测（git / netstat / 目录）；启动两端并采集基线；`npm run build` 151 模块 / 241ms；数据库只读复核并**更正一处文档计数不准**（原记"6 张表 + 7 个索引"，实测为 **6 张表（5 业务表 + `sqlite_sequence`）＋ 8 个索引（5 显式 `idx_*` + 3 个自动索引）**）；提交 `bad0853`；另把"后台任务超时后派生进程仍存活"的复现补记入库（`ce87b7b`）。
  2. **批 1（契约逐条复核 + 接口回归脚本）**：逐行读后端 controller / service / common + DTO，与契约 v1.0 对账 —— **17 条接口中 13 条已实现且一致、4 条为契约已标注的阶段 8 可选项**；新增 `frontend/scripts/smoke.mjs`（`npm run smoke`，Node 原生 `fetch`、**零新增依赖**、**97 项断言**、自动清理测试数据、跑完复核数据库还原）；**发现 `40009` 不可达**；`docs/audit-report.md` 新增「阶段 6 预审计」章节；提交 `2c5b301`。
  3. **批 2（正常路径全链路回归）**：真实浏览器逐模块走查 —— 导航与三态主题（含刷新保持）、列表（深链 `?page=2`、越界回退 `?page=99 → 2`）、搜索与过滤（防抖 3 篇 / AND 2 篇 / OR 5 篇 / 空态 + 清除筛选 / 卡片标签跳转）、详情（Java 代码高亮、正文配图、目录 scroll-spy）、评论（发表 → 归属删除 + 行内确认 → 空态，接口侧 `total=1 → 0`）、点赞（`likeCount=1 → 0` + 刷新保持）、本地数据（面板 5 键 → 一键重置 + Toast「清理 5 项」）；**6 张截图**归档；作者验收通过；提交 `cbe44cf`。
  4. **批 3（异常 / 边界 / 空态演练）**：**XSS 四类载荷**（标题 / 正文 / 标签 / 评论）一律纯文本、零执行、`javascript:` 链接未渲染为链接；**SQL 注入与 `%` `_` 通配**被参数化 + `LIKE ? ESCAPE '\'` 挡住（无 500）；`40009` 现场复核仍不可达；**后端停服降级** —— 列表 / 详情整页错误态 + 重试、评论行内红字、点赞 Toast、`/about` 不受影响、重启 4 秒恢复；404 页 / 详情 404 / 非法 id / 未知标签 / 非法分页各有明确表现；**5 张截图**；演练数据（含 2 个孤立标签）全部清理；提交 `84b5cbc`。
  5. **批 4（非功能与体验复核 + 决策 AX）**：`frontend/src/main.js` 加**全局错误兜底**（`app.config.errorHandler` + `window.unhandledrejection` → `console.error` + error Toast），用临时探针验证两条路径（6ms / 5ms 命中）后**删除探针**；响应式 375 / 768 / 1920 无破版（含断点边界）；**WCAG 对比度量化**（暗色全部达标；亮色主色 4.44 略低于 AA → **遗留 30**）；键盘焦点环可见；构建 234ms / `index` chunk 54.53 kB（+0.31 kB）；**4 张截图**；提交 `9f8d775`。
  6. **批 5（收尾）**：终测 `npm run build` → 207ms、`npm run smoke` → **97/97**；数据终核 `12 / 8 / 23 / 0 / 0`；**遗留 1–30 逐条收口**；文档同步 + `current-state.md` 整份覆盖（含 **13 条人工验收清单**）；**停服并释放 5173 / 8080**；**不代签**（验收结论由作者给出）。
- **改动文件**：新增 `frontend/scripts/smoke.mjs`（完整）、`docs/demo/stage6-01…15-*.png`（**15 张真实浏览器截图**）；修改 `frontend/package.json`（加 `smoke` 脚本）、`frontend/src/main.js`（全局错误兜底）；文档 `README.md`、`docs/{current-state,api-contract（未改字段）,audit-report,collaboration-log,ai-log,debug-log}.md`；临时程序 `backend/target/tmp-check/*.java`（gitignore 内，不提交）。**未新增任何依赖**。
- **验证命令与结果**（均为实测输出）：
  - `npm run smoke` → **`全部通过：97/97 项断言`**（含数据还原 5 项）
  - `npm run build` → 逐批 241ms → 261ms → **234ms** → 收尾 **207ms**；`index` chunk 54.22 → **54.53 kB**（+0.31 kB）
  - 后端 `Started BlogApplication in 1.544 seconds`（Java 26.0.2.1 / Boot 4.1.1 / Tomcat 11.0.24）；前端 `VITE v8.3.0 ready in 211 ms`
  - 数据侧：全程多次复核，收尾为 `article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`（临时 JDBC 直读）
  - 停服：**5173 / 8080 均已释放**
- **遗留问题**：见 `docs/current-state.md` 第五节（共 30 条）。本阶段**闭环 4 条**：17（越界回退复验）、21（正文配图复验）、24（Toast 截图）、28（`40009` 拍板 → 决策 BE）；**新增 1 条**：30（亮色主色对比度 4.44，建议阶段 7 修正）；另有 2 处**未覆盖项**如实登记（控制台 warn / 网络层 4xx 未直接检查；`prefers-reduced-motion` 仅静态证据）。
- **下一步**：**阶段 6 已由作者人工验收通过（2026-09-25）**；**下一步进入阶段 7（功能迭代一）** —— 作者按惯例新开会话后，AI 先出分批方案与决策点，范围建议：阅读进度条、回到顶部、无限滚动 + 骨架屏、窄屏目录折叠入口、详情页 chunk 拆包（决策 AZ），以及遗留 29 / 30 的体验修正。
