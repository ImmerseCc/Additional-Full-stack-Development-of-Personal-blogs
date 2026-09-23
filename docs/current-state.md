# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新。
> 最后更新：阶段 1 批 2（docs 文档骨架创建 + Node 安装验证 + git init）。

---

## 一、当前阶段

**阶段 1「目录结构与占位文件」**，进度：批 1 ✅ → 批 2 ✅（本轮）→ 批 3 ⬜ → 批 4 ⬜

---

## 二、已完成

| 时间点 | 事项 | 产物 |
|---|---|---|
| 阶段 0 | 需求确认与技术选型（未写代码） | 选型结论：Vue 3 + Vite（JS）、原生 CSS + CSS 变量、markdown-it + highlight.js + DOMPurify、Spring Boot 4.1.1 + JdbcTemplate + SQLite；目录结构与 0–9 阶段路线 |
| 阶段 0.5 | 协作日志规范确认 | 日志机制：`docs/ai-log.md`（流水）+ `docs/collaboration-log.md`（汇总），四套模板与禁止事项 |
| 阶段 1 批 1 | 根目录 3 个文件 | `README.md`（213 行）、`.gitignore`（76 行）、`AGENTS.md`（156 行）；用户已确认批 1 通过 |
| 阶段 1 批 1（附带） | `git init` 并切到 `main` 分支 | 本地 Git 仓库已建立，文件已 `git add` 暂存（尚未 commit） |
| 阶段 1 批 2 | docs 文档骨架（本轮） | `docs/` 下 8 个文档 + `docs/demo/.gitkeep` |
| 阶段 1 批 2（附带） | Node.js 安装与验证 | `scoop install nodejs-lts` 安装成功：`node v24.21.0`、`npm 11.19.0`（AI 实测输出） |

**关键决策记录（作者已确认）**

| 编号 | 决策 |
|---|---|
| A1 | 项目根目录沿用当前目录 `D:\code\Additional Full-stack Development of Personal blogs`，接受名称与"连字符"要求的差异 |
| B1 | 同时维护 `docs/ai-log.md`（逐轮流水）与 `docs/collaboration-log.md`（关键提示词汇总 + 阶段索引） |
| C1 | `mvnw` / `mvnw.cmd` / `.mvn/` 取自已验证可访问的 start.spring.io 官方骨架，不手写 |
| D1 | 数据库增加第 5 张关联表 `article_tag`（支持标签多选过滤） |
| E | 技术栈锁定 Vue 3 + Vite + Spring Boot + JdbcTemplate + SQLite；授权 AI 安装 Node.js |

---

## 三、进行中 / 待确认

1. **`docs/api-contract.md` 与 `docs/data-model.md` 为草案**，等作者逐条确认（两份文档末尾各有一张"待确认"表）；
2. **Git 首次提交尚未执行**（已 `git init`、已 `git add -A`，未 `git commit`）；
3. **批 3、批 4 未开始**（frontend / backend 目录尚未创建）。

---

## 四、下一步

| 批次 | 内容 |
|---|---|
| 批 3 | `frontend/`：`index.html`、`package.json`、`vite.config.js`、`src/main.js`、`src/App.vue`、`src/router/index.js`、`src/views/HomeView.vue`、`src/styles/base.css` 及 4 个 `.gitkeep`（写入前用 npm registry 核对依赖版本） |
| 批 4 | `backend/`：`pom.xml`、`mvnw` 三件套（取自 start.spring.io 官方骨架）、`BlogApplication.java`、`application.yml`、`schema.sql`、`data.sql`、`data/.gitkeep`、6 个包的 `.gitkeep` |
| 之后 | 阶段 2 后端业务实现 → 阶段 3 前端搭建 → ……（完整路线见 `docs/ai-log.md` 阶段 0 记录） |

---

## 五、遗留问题

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1 | ~~本机未安装 Node.js~~ **已解决** | — | 已安装 24.21.0 并实测 `node -v` / `npm -v` 成功；用户若在新终端找不到 `node`，重启终端即可（scoop 已写入 PATH） |
| 2 | 本机 JDK 为 26，Spring Boot 4.1.1 兼容性未实测 | 批 4 后端可能启动失败 | `pom.xml` 锁定 Java 21 字节码；若报 `Unsupported class file major version` 则安装 Temurin 21 |
| 3 | springdoc-openapi 与 Spring Boot 4.x 适配版本未核对 | Swagger 加分项可能受阻 | 批 4 写入 `pom.xml` 前用 curl 查 Maven Central；不适配则退回手写 `docs/openapi.yaml` |
| 4 | 前端依赖版本未最终锁定 | `package.json` 可能写过时版本 | 批 3 写入前用 `curl https://registry.npmjs.org/<pkg>/latest` 核对 |
| 5 | 接口契约与数据模型待作者确认 | 后端不能开工 | 等作者对两份草案的"待确认"表逐条回复 |
| 6 | 项目目录路径含空格 | 目前低风险 | 出现工具异常时再评估迁移 |
| 7 | README 部分区块（文档索引状态、风险清单第 1 条）在本轮同步中 | 文档可能短暂过时 | 本轮回复内一并同步完成 |
| 8 | 已有 1 条真实报错记录（环境类，非阻塞） | 交付要求已满足最低线 | 若阶段 2 起出现代码类报错，继续按模板追加 |
| 9 | `git add` 提示 LF 将被转为 CRLF | 跨平台换行不一致 | 可选：新增 `.gitattributes` 统一换行（**需作者同意新增文件**，暂不做） |

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash 可用（`git version 2.55.0.windows.5`） |
| JDK | `java 26.0.2.1`，`JAVA_HOME=D:\Java\jdk-26.0.2.1` |
| Maven / Gradle | 均未安装（后端将使用 `mvnw`，无需安装） |
| Node / npm | **已安装**：`node v24.21.0`、`npm 11.19.0`（AI 实测输出） |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`registry.npmmirror.com` 均实测 HTTP 200；`github.com` 不可达（Scoop 自更新失败，见 `docs/debug-log.md` 报错记录 1） |
| Git 仓库 | 已 `git init`，分支 `main`，文件已暂存，未提交 |
