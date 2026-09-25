# AGENTS.md — 项目总纲

本文件是《VibeCoding 大实战：个人博客全栈项目》的**总纲**，供 AI 助手与开发者共同遵守。
任何临时做法与本文件冲突时，以本文件为准；需要变更规则时，**先改本文件，再改代码**。

---

## 1. 项目定位与硬约束

一个从零手写的个人博客全栈项目：**Vue 3 前端 + Spring Boot 后端 + SQLite 持久化**，前后端在同一个仓库内。

硬约束（来自项目主指令，不可违背）：

1. **禁止**使用任何现成博客模板或整站主题，所有页面、组件、接口必须自行实现；
2. 前端必须使用 **JavaScript**（可配合 HTML/CSS），**不引入 TypeScript**；
3. 后端必须使用 **Java**；
4. 数据持久化只能用**轻量方案**（SQLite / 文件存储），不得引入需要独立部署的大型数据库或中间件；
5. 不一次性生成全部业务代码，按批次推进，每批结束停下等确认。

---

## 2. 技术栈与版本

| 层 | 选型 |
|---|---|
| 前端语言 | JavaScript (ES2020+)、HTML、CSS |
| 前端框架 | Vue 3（Composition API + `<script setup>`） |
| 构建 / 开发服务器 | Vite，开发端口 `5173` |
| 路由 / 状态 | Vue Router 5 / Pinia 4（与 `frontend/package.json` 实测版本一致） |
| 样式 | 原生 CSS + CSS 变量主题系统（**不用** UI 组件库、**不用** CSS 框架） |
| Markdown | markdown-it + highlight.js + DOMPurify |
| 后端语言 | Java 21 字节码目标（本机 JDK 26，**已实测可运行 Spring Boot 4.1.1**） |
| 后端框架 | Spring Boot 4.1.1（Web / JDBC / Validation） |
| 数据访问 | JdbcTemplate + 手写 SQL（**不用** ORM） |
| 数据库 | SQLite：`backend/data/blog.db` |
| 构建 | 后端 Maven Wrapper（`mvnw`）/ 前端 npm |

依赖版本一律使用**当前稳定版**，并在写入配置文件前用官方源核对（npm registry / Maven Central），不凭记忆写版本号。

---

## 3. 启动命令（唯一标准写法）

```bash
# 后端（Git Bash / macOS / Linux）
cd backend && ./mvnw spring-boot:run
```

```powershell
# 后端（Windows PowerShell / CMD）
cd backend; .\mvnw.cmd spring-boot:run
```

```bash
# 前端
cd frontend && npm install && npm run dev
```

访问地址：前端 http://localhost:5173 ｜ 后端 http://localhost:8080 ｜ API 前缀 `/api`（前端通过 Vite 代理转发）。

---

## 4. 目录结构与分层约定

```
docs/      文档：主指令、当前状态、接口契约、数据模型、协作日志、调试记录、审计报告、演示素材
frontend/  Vue 3 前端：src/{router,stores,api,components,views,utils,styles}
backend/   Spring Boot 后端：src/main/java/com/example/blog/{controller,service,repository,model,config,common}
```

**后端分层职责（不得越界）**

| 包 | 职责 | 禁止 |
|---|---|---|
| `controller` | 接收请求参数、调用 service、包装统一响应 | 写 SQL、写业务规则 |
| `service` | 业务规则、参数语义校验、事务边界 | 直接依赖 `HttpServletRequest` |
| `repository` | 只用 JdbcTemplate 执行 SQL、结果映射 | 写业务判断 |
| `model` | 实体 / 请求 DTO / 响应 VO | 混入框架注解滥用 |
| `config` | OpenAPI、Web、数据源等配置类 | 写业务逻辑 |
| `common` | 统一返回体、错误码、业务异常、全局异常处理 | 依赖 controller |

---

## 5. 接口契约索引

- **唯一来源**：`docs/api-contract.md`（阶段 1 批 2 产出，**待用户确认**）。任何接口变更先改契约文档，再改后端，再改前端。
- **统一返回格式**：

```json
{ "code": 0, "message": "ok", "data": {} }
```

- **错误码段位**：`0` 成功；`4xxxx` 参数/校验类；`5xxxx` 服务端异常（具体码表见契约文档，待确认）。
- **资源划分**：`articles`（文章）、`comments`（评论）、`likes`（点赞）、`tags`（标签）。
- **规则**：前端不得按记忆写死字段名；契约未覆盖的接口视为不存在。

---

## 6. 协作规则

1. **分批推进**：每批结束停下等用户确认，禁止一次性产出全部业务代码；
2. **每次回复必须给出**：改动文件的完整相对路径、每个文件是"占位"还是"完整"、可复制的验证命令（含 Windows PowerShell 变体）；
3. **不编造运行结果**：未实际执行的命令必须写明「未运行，需验证」；
4. **日志双文件**：`docs/ai-log.md` 记录逐轮追加的协作流水；`docs/collaboration-log.md` 保存关键提示词汇总与阶段索引；
5. **报错先记录再修复**：格式见 `docs/debug-log.md`，须包含报错原文、运行命令、定位过程、修复方案、修复后验证、最终结果；
6. **当前状态**：随时以 `docs/current-state.md` 为准，阶段结束后同步更新；
7. **验收归用户**：最终启动、录屏、验收结论由用户给出，AI 不得代签。

---

## 7. 禁止事项

1. 使用现成博客模板 / 整站主题 / 整站复制代码；
2. 一次性生成全部业务代码；
3. 硬编码密钥、Token、密码，或把 `.env` 提交进仓库；
4. 编造运行结果、谎称未运行的测试通过；
5. 擅自增删顶层目录（`docs/`、`frontend/`、`backend/`）或改动既定目录架构；
6. 引入 TypeScript、UI 组件库（Element Plus / Ant Design 等）、CSS 框架（Tailwind / Bootstrap 等）；
7. 执行危险删除操作（`rm -rf`、删除数据库文件、`git reset --hard` 等），如需删除必须先向用户确认；
8. 引入重型中间件（Redis、MQ、集群数据库）或改变已确认的技术栈。

---

## 8. 数据与重置

- 后端数据：`backend/data/blog.db`（可能伴随 `-wal` / `-shm` 文件）；**重置**＝停止服务 → 删除这三个文件 → 重新启动，`schema.sql` + `data.sql` 会自动重建；删除即数据丢失，不可恢复。
- 前端数据：`localStorage`，键名前缀 `blog:`（阶段 5 落地）；**重置**＝页面内重置按钮（阶段 5 实现）或 DevTools 手动清除。
- 数据库文件与 `node_modules`、`target`、`dist` 均不得提交（见 `.gitignore`）。

---

## 9. 环境现状与已知风险

| 项 | 现状 | 影响 |
|---|---|---|
| JDK | 已装 26（`JAVA_HOME=D:\Java\jdk-26.0.2.1`） | 与 Spring Boot 4.1.1 兼容性**已实测**（`Starting BlogApplication using Java 26.0.2.1`），无需改用 Temurin 21 |
| Node.js / npm | 已装 **Node 24.21.0 / npm 11.19.0**（经 `scoop install nodejs-lts`） | 前端 install / build / dev 均已实测可用 |
| Maven | 未安装 | 使用 `mvnw` 包装器，无需安装 |
| 项目路径 | 含空格（`D:\code\Additional Full-stack Development of Personal blogs`） | 目前低风险，出现工具异常再评估迁移 |
| 网络 | npm registry / Maven Central / start.spring.io 均可访问（已实测 HTTP 200） | 依赖可正常下载 |

---

## 10. 文档索引

| 文件 | 作用 |
|---|---|
| `docs/main-prompt.md` | 项目主指令与协作日志指令原文 |
| `docs/current-state.md` | 当前阶段、已完成、下一步、遗留问题 |
| `docs/api-contract.md` | 前后端接口契约（唯一来源） |
| `docs/data-model.md` | 数据库表结构与字段约束 |
| `docs/collaboration-log.md` | 关键提示词汇总表 + 阶段索引 |
| `docs/ai-log.md` | 逐轮追加的协作流水日志 |
| `docs/debug-log.md` | 真实报错与修复记录 |
| `docs/audit-report.md` | 前后端专项审计记录 |
| `docs/demo/` | 演示截图 / 录屏素材 |
