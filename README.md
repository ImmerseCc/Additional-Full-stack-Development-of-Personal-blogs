# VibeCoding 个人博客（全栈项目）

一个**从零手写**的个人博客全栈项目：Vue 3 前端 + Spring Boot 后端 + SQLite 持久化。
本项目**不使用任何现成博客模板或整站主题**，所有页面、组件、接口均按需求自行实现。

> **项目状态（持续更新）**
> - 当前阶段：**阶段 1「目录结构与占位文件」**，批 1（根目录文件）、批 2（docs 文档骨架）已完成，批 3–4 待开始
> - 业务功能：**全部未实现**，下方「功能清单」中状态为「待实现」的条目即未完成项
> - 环境：**Node.js 24.21.0 / npm 11.19.0 已安装**（经 `scoop install nodejs-lts`）；JDK 26 与 Spring Boot 4.1.1 的兼容性仍待实测
> - Git：已完成 `git init`（分支 `main`），首次提交待执行
> - 接口契约与数据模型目前是**草案**，待作者确认：`docs/api-contract.md`、`docs/data-model.md`

---

## 一、技术栈

| 层 | 选型 | 说明 |
|---|---|---|
| 前端语言 | JavaScript (ES2020+) + HTML + CSS | 不使用 TypeScript |
| 前端框架 | Vue 3（Composition API + `<script setup>`） | 单文件组件 `.vue` |
| 构建 / 开发服务器 | Vite | 开发端口 `5173` |
| 路由 / 状态管理 | Vue Router 4 / Pinia | 页面高亮、主题与点赞等跨组件状态 |
| 样式方案 | 原生 CSS + CSS 变量主题系统 | 不使用 UI 组件库、不使用 CSS 框架，全部手写 |
| Markdown 渲染 | markdown-it + highlight.js + DOMPurify | 代码高亮 + XSS 过滤 |
| 后端语言 | Java 21 字节码目标（本机 JDK 26，兼容性待验证） | — |
| 后端框架 | Spring Boot 4.1.1（Web / JDBC / Validation） | 内置 Tomcat，无需额外中间件 |
| 数据访问 | JdbcTemplate + 手写 SQL | 不使用 ORM |
| 数据库 | SQLite 单文件 | `backend/data/blog.db` |
| 构建工具 | 后端 Maven Wrapper（`mvnw`，免安装 Maven）/ 前端 npm | — |

**环境要求**

- JDK 21 或更高（本机已装 JDK 26：`D:\Java\jdk-26.0.2.1`）
- Node.js 20 LTS 或更高 + npm（本机已装 **Node 24.21.0 / npm 11.19.0**；若新开终端提示找不到 `node`，重启终端即可——Scoop 已把安装目录写入 PATH）
- 无需单独安装 Maven：后端使用自带的 `mvnw` 包装器

---

## 二、快速开始

### 后端（Spring Boot，端口 8080）

```bash
cd backend
./mvnw spring-boot:run          # Git Bash / macOS / Linux
```

```powershell
cd backend
.\mvnw.cmd spring-boot:run      # Windows PowerShell / CMD
```

首次运行 `mvnw` 会自动下载 Maven（需要网络）；启动成功后会自动创建 `backend/data/blog.db` 并执行 `schema.sql` / `data.sql` 初始化。

### 前端（Vite，端口 5173）

```bash
cd frontend
npm install
npm run dev
```

其它脚本：`npm run build`（生产构建）、`npm run preview`（预览构建产物）。

### 访问地址

| 服务 | 地址 |
|---|---|
| 前端页面 | http://localhost:5173 |
| 后端服务 | http://localhost:8080 |
| 后端 API 前缀 | http://localhost:8080/api |
| 前端如何调用后端 | Vite 开发代理 `/api` → `http://localhost:8080`（规避 CORS） |
| Swagger / OpenAPI | 待实现（阶段 2） |
| 数据库文件 | `backend/data/blog.db` |

### 常见问题排查

| 现象 | 原因 | 处理 |
|---|---|---|
| 终端报 `cd: backend: No such file or directory`（或找不到 `./mvnw`） | 当前终端**不在项目根目录**，`cd backend` 是相对路径 | 先用绝对路径进入项目再加引号（路径含空格）：Git Bash `cd "/d/code/Additional Full-stack Development of Personal blogs"`；PowerShell `cd "D:\code\Additional Full-stack Development of Personal blogs"`。完整记录见 `docs/debug-log.md` 报错记录 3 |
| 浏览器打不开 `http://127.0.0.1:5173`，但 `http://localhost:5173` 正常 | Windows 下 Vite 未显式设置 `host` 时可能只绑定 IPv6 `::1` | 本项目已在 `vite.config.js` 设置 `server.host = '127.0.0.1'` 规避；若仍出现，确认用的是最新配置并重启 dev server |
| `npm run dev` 报找不到命令 | 终端是在安装 Node 之前打开的，PATH 未刷新 | 关闭并重新打开终端（本项目已安装 Node 24.21.0） |
| 浏览器打不开 `http://localhost:8080/swagger-ui/index.html`（提示拒绝连接） | **后端没在运行**——Swagger UI 与 `/api/*` 只在后端运行期间可访问 | 先启动后端，等控制台出现 `Started BlogApplication` 再刷新页面；完整记录见 `docs/debug-log.md` 报错记录 5 |
| 前端请求 `/api/...` 报 502 / ECONNREFUSED | 后端没启动，或不是 8080 | 先启动后端，确认控制台出现 Tomcat 监听 8080 |
| 5173 被占用 | 其他程序占用端口 | 前端会自动换端口（看终端输出的 `Local:` 地址） |
| 8080 被占用 | 其他程序占用端口 | 修改 `backend/src/main/resources/application.yml` 的 `server.port`，同时改 `frontend/vite.config.js` 的代理 target |
| 启动日志出现 `WARNING: A restricted method in java.lang.System has been called` | JDK 24+ 对 sqlite-jdbc 加载本地库的限制提示，**仅警告**，不影响功能 | 无需处理；若想消除，可在启动命令追加 `--enable-native-access=ALL-UNNAMED`（本项目默认不加，保持标准启动命令简洁） |
| 后端启动报数据库错误 | 工作目录不对，或 `backend/data/` 不存在 | 必须在 `backend/` 目录下执行 `./mvnw spring-boot:run`；确认 `backend/data/` 存在（含 `.gitkeep`） |

> 前两条来自真实排错过程，完整记录（报错原文、定位、修复、验证）见 `docs/debug-log.md` 报错记录 2。

---

## 三、项目结构

```
.
├─ README.md                  # 本文件
├─ .gitignore
├─ AGENTS.md                  # 项目总纲（技术栈/命令/契约索引/协作与禁止事项）
├─ docs/                      # 文档：主指令、状态、契约、模型、日志、审计、演示素材
│  ├─ main-prompt.md
│  ├─ current-state.md
│  ├─ api-contract.md
│  ├─ data-model.md
│  ├─ collaboration-log.md
│  ├─ ai-log.md
│  ├─ debug-log.md
│  ├─ audit-report.md
│  └─ demo/
├─ frontend/                  # Vue 3 + Vite
│  ├─ index.html
│  ├─ package.json
│  ├─ vite.config.js
│  └─ src/
│     ├─ main.js
│     ├─ App.vue
│     ├─ router/
│     ├─ stores/
│     ├─ api/
│     ├─ components/
│     ├─ views/
│     ├─ utils/
│     └─ styles/
└─ backend/                   # Spring Boot + JdbcTemplate + SQLite
   ├─ pom.xml
   ├─ mvnw / mvnw.cmd / .mvn/
   ├─ data/                   # 运行时生成 blog.db（已 gitignore）
   └─ src/main/
      ├─ java/com/example/blog/
      │  ├─ BlogApplication.java
      │  ├─ controller/       # 路由层：接参 + 包装响应
      │  ├─ service/          # 服务层：业务规则与事务
      │  ├─ repository/       # 数据访问层：SQL / JdbcTemplate
      │  ├─ model/            # 实体、DTO、VO
      │  ├─ config/           # 配置类（OpenAPI、Web 等）
      │  └─ common/           # 统一返回、错误码、全局异常处理
      └─ resources/
         ├─ application.yml
         ├─ schema.sql        # 建表语句（幂等）
         └─ data.sql          # 种子数据（幂等）
```

---

## 四、功能清单

### 前端 6 个核心模块

| 模块 | 内容 | 状态 |
|---|---|---|
| 一、全局导航与主题 | 响应式导航栏、移动端汉堡菜单、深浅色切换、当前页高亮 | 待实现（阶段 3） |
| 二、文章列表展示 | 卡片布局、分页 / 无限滚动、悬停动效、骨架屏 | 待实现（阶段 4、7） |
| 三、文章详情页 | Markdown 渲染、阅读进度条、图片懒加载、代码高亮 | 待实现（阶段 4、7） |
| 四、搜索与分类过滤 | 实时搜索 + 防抖、多选标签过滤、空结果动画 | 待实现（阶段 5） |
| 五、评论与互动 | 评论表单校验、点赞数字动画、Toast 通知 | 待实现（阶段 5） |
| 六、数据持久化与模拟 | localStorage 主题/点赞、刷新不丢、一键重置 | 待实现（阶段 5） |

**前端加分项**：CSS 变量主题系统、跟随系统主题、路由过渡动画、文章目录自动生成 + 滚动监听、IntersectionObserver 滚动动画、图片懒加载、路由级代码分割 —— 状态：待实现（阶段 3–7）。
虚拟列表**不做**（博客数据量下属于过度工程，改用无限滚动 + 骨架屏）。

### 后端功能

| 功能 | 状态 |
|---|---|
| 文章列表（支持分页） | 待实现（阶段 2） |
| 文章详情 | 待实现（阶段 2） |
| 创建文章 | 待实现（阶段 2） |
| 修改文章 | 待实现（阶段 2） |
| 删除文章 | 待实现（阶段 2） |
| 数据持久化（重启不丢） | 待实现（阶段 2） |
| 标签 / 分类管理 | 待实现（阶段 2、8） |
| 标题关键词搜索 | 待实现（阶段 8） |
| 文章状态（草稿 / 已发布） | 待实现（阶段 8） |
| 统一错误返回格式 | 待实现（阶段 2） |
| 评论独立增删改查 | 待实现（阶段 2、8） |
| 接口输入校验 | 待实现（阶段 2） |
| Swagger / OpenAPI 文档 | 待实现（阶段 2） |

---

## 五、数据存储位置与重置方法

### 后端数据（SQLite）

- **位置**：`backend/data/blog.db`（同目录可能出现 `blog.db-wal`、`blog.db-shm` 临时文件）
- **重置方法**：
  1. 停止后端进程（`Ctrl + C`）；
  2. 删除 `backend/data/` 下的 `blog.db`、`blog.db-wal`、`blog.db-shm`；
  3. 重新执行 `./mvnw spring-boot:run`，启动时会用 `schema.sql` 重建表、用 `data.sql` 重新写入种子文章。
- **注意**：删除数据库文件会丢失通过接口创建的全部数据，属于不可恢复操作。

### 前端数据（localStorage）

- **位置**：浏览器 `localStorage`，键名规划以 `blog:` 为前缀（具体键名在阶段 5 落地）
- **重置方法**：
  1. 页面内「重置本地数据」按钮（阶段 5 实现，待实现）；
  2. 手动：浏览器 DevTools → Application → Local Storage → 删除 `blog:` 前缀的键。

---

## 六、文档索引

| 文档 | 作用 | 状态 |
|---|---|---|
| `docs/main-prompt.md` | 项目主指令与协作日志指令原文 | 已创建 |
| `docs/current-state.md` | 当前阶段、已完成、下一步、遗留问题 | 已创建 |
| `docs/api-contract.md` | 前后端接口契约（唯一来源） | 已创建（**草案，待确认**） |
| `docs/data-model.md` | 数据库表结构、字段、约束 | 已创建（**草案，待确认**） |
| `docs/collaboration-log.md` | 关键提示词汇总 + 阶段索引 | 已创建 |
| `docs/ai-log.md` | 逐轮追加的 AI 协作流水日志 | 已创建 |
| `docs/debug-log.md` | 真实报错与修复记录 | 已创建（含 1 条环境类真实报错） |
| `docs/audit-report.md` | 前后端专项审计记录 | 已创建（模板 + 12 项检查清单，审计待阶段 9） |
| `docs/demo/` | 演示截图 / 录屏存放目录 | 已创建（含 `.gitkeep`） |

---

## 七、已知问题与风险（随阶段更新）

1. **GitHub 当前不可达**：`github.com` 在本机网络下连接被重置（Scoop 自更新因此失败，见 `docs/debug-log.md` 报错记录 1），会影响从 GitHub 拉取依赖或推送仓库；如需把仓库推到 GitHub，需先解决网络问题；
2. **JDK 26 与 Spring Boot 4.1.1 兼容性未实测**：`pom.xml` 计划锁定 Java 21 字节码目标，若启动报 `Unsupported class file major version` 之类错误，改用 Temurin 21 LTS；
3. **项目目录路径含空格**（`D:\code\Additional Full-stack Development of Personal blogs`）：目前判定为低风险，若工具出现异常再考虑迁移；
4. **springdoc-openapi 与 Spring Boot 4.x 的适配版本尚未核对**：批 4 写入 `pom.xml` 前会用 curl 查询 Maven Central 确认，若不适配则退回手写 `docs/openapi.yaml`；
5. **前端依赖版本尚未最终锁定**：批 3 写入 `package.json` 前会用 npm registry 核对稳定版；
6. **接口契约与数据模型仍是草案**：确认后才会写进后端代码，见两份文档末尾的"待确认"表。

---

## 八、说明

- 本项目为学习与实战演练用途，后端接口默认不做鉴权，**如需部署到公网请自行增加鉴权与限流**（阶段 2 会在文档中明确标注）。
- 协作过程记录见 `docs/collaboration-log.md` 与 `docs/ai-log.md`。
