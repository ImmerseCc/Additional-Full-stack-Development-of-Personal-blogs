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

---

## 审计记录

**未发生。** 项目尚未运行起来（业务代码未开始），按项目要求，专项审计安排在阶段 9 执行，模板与 12 项检查清单见 `docs/audit-report.md`。

---

## 关键提示词汇总

> 与 `docs/collaboration-log.md` 保持同步。项目要求"至少 4 组关键提示词"，当前已发生 3 组，第 4–6 组待真实发生后再补（不编造）。

| 编号 | 阶段 | 提示词摘要 | AI 做了什么 | 作用 |
|---|---|---|---|---|
| 1 | 项目启动 | 主指令：前端必须 JavaScript、后端必须 Java、持久化只用轻量方案；禁止现成模板/整站主题；分阶段推进每阶段等确认；固定回复格式；要求 ≥4 组关键提示词、≥2 次功能迭代、1 次真实报错、前后端专项审计；禁止编造结果与硬编码密钥 | 环境探测（Node/Maven 缺失、JDK 26）、技术选型（Vue 3 + Vite + 原生 CSS 变量；Spring Boot 4.1.1 + JdbcTemplate + SQLite）、monorepo 目录结构、0–9 阶段路线、11 个待确认问题、9 条风险 | 确定技术栈与执行节奏，把"过程留痕"变成硬约束 |
| 2 | 协作流程 | 兼任 AI 协作日志记录员：四套模板（阶段/报错/审计/关键提示词）、只输出可追加片段、禁止编造报错与测试结果、未运行写"未运行，需我验证" | 确认触发条件与模板、列 9 条禁止事项、确定 ai-log 与 collaboration-log 分工、明确不写日期 | 让协作过程可交付、可审计，避免"只交最后代码" |
| 3 | 架构落地 | 给定目录架构与分批规则：先出创建计划、确认后分 4 批执行、每批等确认；禁止一次性生成全部业务代码、禁止硬编码密钥、不要扫描仓库；随后"批1通过，现在 git init，请继续" | 输出 22 目录/38 文件创建计划与 5 个决策点；创建批 1 根目录文件、批 2 docs 文档骨架；执行 `git init`（分支 main）；安装并验证 Node.js 24.21.0 | 把架构落到磁盘，确立"分批交付 + 每批可验证"的节奏 |
| 4 | 功能迭代 | 待发生（计划：前端无限滚动 + 骨架屏 + 回到顶部；后端搜索/分页/统一错误格式） | 待发生 | 待发生 |
| 5 | 报错修复 | 待发生（作者尚未下达报错修复提示词；已发生 1 次真实环境报错，由 AI 在执行中捕获并记录） | 待发生 | 待发生 |
| 6 | 项目审计 | 待发生（计划在阶段 9） | 待发生 | 待发生 |
