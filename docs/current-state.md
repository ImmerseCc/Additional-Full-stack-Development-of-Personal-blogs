# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 3「前端模块一：全局导航与主题」全部完成（批 0–4，含浏览器实测）**；阶段 0–2 亦全部完成并实测通过。**下一步进入阶段 4「前端模块二 / 三：文章列表与详情」**。

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 v1.0，**已确认**，实现时不得擅自改字段）。
2. **进度**：阶段 0、0.5、1、2、3 已完成。**当前应从阶段 4「前端模块二 / 三：文章列表 + 文章详情」开始**；后端 13 个操作已全部实现并实测（见第七节清单）；前端已具备导航栏 / 汉堡菜单 / 三态主题 / 404 / 路由过渡 / 页脚，**文章列表与详情仍是占位页**（`HomeView.vue`、`ArticlesView.vue`、`AboutView.vue` 中的说明文字即待办）。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水）、`docs/collaboration-log.md`（关键提示词汇总 + 阶段索引 + 阶段记录）、`docs/debug-log.md`（报错专档，已有 5 条真实记录 + 8 条观察项）；审计记录在 `docs/audit-report.md`（阶段 9 才做）。
4. **环境事实**：Node 24.21.0 / npm 11.19.0 已安装；JDK 26 **已验证**可跑 Spring Boot 4.1.1；Maven 未安装但 `mvnw` 可用（依赖已缓存，后端启动约 2 秒）；**后端与前端 dev server 当前均处于停止状态**，需要时用第八节的标准命令启动。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题。
6. **本机已踩过的坑（细节都在 `docs/debug-log.md`）**：① Git Bash 里给 `curl` 传中文参数会被按 **GBK** 编码 → 改用预编码 UTF-8 百分号串，或用 Swagger UI / PowerShell `Invoke-RestMethod`；② `wc -m` 在本机按**字节**计数（要数中文长度得按码点算）；③ AI 的后台任务有 **10 分钟上限**，超时只杀包装进程、**派生 JVM / node 会继续存活**并占住 8080 / 5173（用 `taskkill //PID <pid> //F` 清理，阶段 3 期间已复现 4 次）；④ 浏览器提示"拒绝连接"时，排查第一步永远是"确认服务是否真的在监听"；⑤ **内置浏览器在触摸仿真（mobile + touch）下合成的鼠标点击不送达页面**，移动端交互验证请优先用键盘路径，点按路径交作者真机验收。

---

## 一、当前阶段

**阶段 3「前端模块一：全局导航与主题」：全部完成 ✅**（批 0 → 批 1 → 批 2 → 批 3 → 批 4，每批停下等作者确认；批 0–3 已提交入库）
**下一阶段：阶段 4「前端模块二 / 三：文章列表与文章详情」——尚未开始。**

阶段 3 分批：
批 0 开工基线 ✅ → 批 1 主题系统（Pinia 三态 + CSS 变量 + 首屏防闪）✅ → 批 2 导航栏 + 路由目标 + 404 页 ✅ → 批 3 移动端汉堡菜单 + 滚动样式变化 ✅ → 批 4 路由过渡 + 页脚拆分 + 文档同步 ✅。

---

## 二、已完成

| 时间点 | 事项 | 产物与证据 |
|---|---|---|
| 阶段 0 | 需求确认与技术选型（未写代码） | 选型：Vue 3 + Vite、原生 CSS 变量、Spring Boot 4.1.1 + JdbcTemplate + SQLite；0–9 阶段路线 |
| 阶段 0.5 | 协作日志规范确认 | `ai-log.md`（流水）+ `collaboration-log.md`（汇总），四套模板与禁止事项 |
| 阶段 1 批 1–4 | 目录骨架、docs、frontend、backend 全部落地 | 根目录 3 文件 + docs 9 文件 + frontend 12 文件 + backend 15 文件；`git init`；契约与模型升级 v1.0；提交 `501065a` → `25e280e` |
| 报错修复 | 浏览器 `http://127.0.0.1:5173` 被拒绝 | 根因：Vite 只绑定 IPv6；修复：`vite.config.js` 加 `host: '127.0.0.1'`（报错记录 2） |
| 阶段 2 批 0–4 | 后端业务实现（13 个操作） | 累计 52 项接口实测通过；外键级联、点赞幂等、`busy_timeout` 均实测；提交 `e9ab336` → `7174838` → `af2334c` |
| 阶段 3 批 0 | 开工基线（文档更正） | `README.md` 状态块 + 本文件同步；`npm run build` 基线 28 模块 / 160ms；提交 `8c5923d` |
| 阶段 3 批 1 | 主题系统（三态） | 新建 `stores/theme.js`、`components/ThemeToggle.vue`；扩展 `styles/base.css`；删 2 个 `.gitkeep`。浏览器实测三态循环 + 刷新不丢；提交 `7de72da` |
| 阶段 3 批 2 | 导航栏 + 路由目标 + 404 | 新建 `components/AppHeader.vue`、`views/NotFoundView.vue`（完整）与 `views/{ArticlesView,AboutView}.vue`（占位）；路由加 `/articles`、`/about`、`/:pathMatch(.*)*` 与 `document.title` 同步；各视图独立 chunk；提交 `b257499` |
| 阶段 3 批 3 | 移动端汉堡菜单 + 滚动样式变化 | `AppHeader.vue` 重写（`aria-expanded` / `aria-controls`、Esc、焦点进出、遮罩、`backdrop-filter` 页头）+ `--color-bg-header`。375×667 实测折叠 / 键盘开合 / Esc / 滚动模糊，桌面无回归；提交 `b1a517f` |
| 阶段 3 批 4 | 路由过渡 + 页脚拆分 + 文档同步 | 新建 `components/AppFooter.vue`；`App.vue` 加 `<Transition name="page" mode="out-in">`；`base.css` 加 `.page-*`；`README.md` 与四份 docs 同步 |

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
| H | 阶段 2 分批方案：把"原批 2"拆成 **2a 读路径 / 2b 写路径** |
| I | 阶段 2 实测产生的文章 5：**只删文章、保留标签**（选 b，已用接口删除） |
| J | `--enable-native-access` 只写进 README 排查说明，不进标准启动命令 |
| K | 阶段 3 菜单项锁定三项：首页 `/`、文章列表 `/articles`、关于 `/about`；后两者阶段 3 只做**占位页** |
| L | 阶段 3 **新增 404 页**（`NotFoundView.vue`），替换"非匹配路径一律重定向首页"的兜底 |
| M | 主题切换采用**三态**：亮 / 暗 / 跟随系统（`blog:theme` 存 `light` / `dark` / `system`） |
| N | 主题按钮固定在导航栏右侧（桌面与移动端同一位置，不藏进汉堡菜单） |
| O | 关于页阶段 3 **不调用后端**，保持纯前端（前后端对接演练留阶段 6） |
| P | 阶段 3 不新增 `frontend/public/`，不新增 `src/` 子目录（复用逻辑放 `src/utils/`） |
| Q | 阶段 3 批 2（导航结构）/ 批 3（移动端交互）**不拆分**，沿用阶段 2 的"每批停下等确认"节奏 |

---

## 三、待确认 / 待执行

1. **阶段 3 已完成，等待作者验收**：批 0–3 已提交，批 4（含文档同步）待作者确认后提交；
2. **验收建议**：作者按第八节启动前端，在三档宽度（375 / 768 / 1440）核对导航、汉堡菜单、主题三态、当前页高亮、路由过渡与 404 页后给出结论（**AI 不代签**）；
3. **需作者补验的点按路径**：触摸点按开合菜单、遮罩点击关闭、点链接后路由变化自动收起——内置浏览器在触摸仿真下合成点击不送达页面（见零节第 6 条 ⑤）；另建议在 DevTools → Rendering 里开启"减少动态效果"复核过渡动画是否被禁用；
4. **阶段 4 需先申请的资源**：`frontend/public/` 静态资源目录（封面图 / favicon），以及全量种子文章约 12 篇——按既定约定「需要时向作者申请」，等作者同意再新增。

---

## 四、阶段 3 逐批结果（作者已确认批 0–批 3）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 批 0 ✅ | 开工基线 | `README.md` 顶部状态块更正；本文件同步；`npm run build` 基线 28 模块 / 160ms；提交 `8c5923d` |
| 批 1 ✅ | 主题系统（三态） | 新建（完整）`src/stores/theme.js`、`src/components/ThemeToggle.vue`；修改（完整）`src/styles/base.css`、`src/App.vue`；删除 2 个 `.gitkeep`。实测 31 模块 / 131ms；浏览器验证三态循环 + 刷新不丢 |
| 批 2 ✅ | 导航栏 + 路由 + 404 | 新建（完整）`AppHeader.vue`、`NotFoundView.vue`；新建（占位）`ArticlesView.vue`、`AboutView.vue`；修改 `router/index.js`、`App.vue`、`base.css`。实测 39 模块且四视图独立 chunk；四路径标题 + 高亮 + 404 + 深色主题 |
| 批 3 ✅ | 汉堡菜单 + 滚动样式 | 修改（完整）`AppHeader.vue`、`base.css`。实测 CSS 6.10 kB；375×667 折叠 / 键盘开合 / Esc / 遮罩 / 滚动后页头半透明模糊；桌面视口无回归（点按路径待补验） |
| 批 4 ✅ | 路由过渡 + 收尾 | 新建（完整）`AppFooter.vue`；修改（完整）`App.vue`、`base.css`；`README.md` + `docs/*` 同步。实测 142ms / CSS 6.46 kB / 主包 106.33 kB（Transition 运行时首次进包）；截图捕获到过渡中间帧与稳定态 |

**验收要点**：导航栏（含滚动样式变化）、汉堡菜单（含键盘可达）、主题三态 + 首屏防闪、当前页高亮、路由过渡、404 页。

---

## 五、遗留问题

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1 | ~~后端首次启动结果未知~~ **已解决** | — | 阶段 1 批 4 实测：启动、建表、种子数据、接口全部正常 |
| 2 | ~~JDK 26 兼容性未实测~~ **已解决** | — | 实测可用：`Starting BlogApplication using Java 26.0.2.1` |
| 3 | 依赖下载慢（首次约 10 分钟量级） | 影响验证耗时 | 依赖已缓存，后续启动 2 秒级 |
| 4 | ~~接口契约与数据模型是草案~~ **已解决** | — | v1.0 已确认，阶段 2 全部按契约实现 |
| 5 | 项目目录路径含空格 | 目前低风险 | 出现工具异常时再评估迁移 |
| 6 | `frontend/public/`（封面图等静态资源目录）未创建 | 阶段 4 文章封面暂无本地图片 | 需向作者申请新增；当前种子文章 `cover_url` 均为 NULL，前端可用渐变占位 |
| 7 | 种子文章仅 3 篇 | 演示内容偏少 | 全量约 12 篇在阶段 4/5 补齐 |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、`README.md`、Swagger 描述中标注 |
| 9 | ~~SQLite 的 `busy_timeout` 未设置~~ **部分解决** | — | `busy_timeout=5000` 已生效；**WAL 仍未启用**，需要时在 `connection-init-sql` 追加 `PRAGMA journal_mode = WAL` |
| 10 | 结束后台任务会留下派生进程（JVM / node） | 下次启动报端口占用 | 阶段 3 又复现 4 次（5173 被 node 占住）；停服统一用 `netstat -ano \| grep ":端口" \| grep -i listening` + `taskkill //PID <pid> //F` |
| 11 | ~~JDK 26 提示 native-access 警告~~ **已处理** | — | 结论：仅警告，不写进标准启动命令；`README.md`《常见问题排查》已说明 |
| 12 | ~~外键约束是否在应用连接上真正打开未验证~~ **已解决** | — | 批 2b 实测：删除文章后 `comment` / `like_record` / `article_tag` 计数全部归 0 |
| 13 | 过渡动画的**观感**（时长 / 缓动）与 `prefers-reduced-motion` 禁用未由 AI 实测 | 可能不合口味 | 截图只能证明过渡存在；需作者目视评价，必要时调整 `--duration-base` / `--ease-out` |
| 14 | 触摸点按路径未由 AI 实测（工具限制） | 无法自动化回归 | 交作者真机 / 真实浏览器点按验收；已记入 `debug-log.md` 观察项 |

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK | `java 26.0.2.1`，`JAVA_HOME=D:\Java\jdk-26.0.2.1`；**已验证可运行 Spring Boot 4.1.1** |
| Maven | 本机未安装；项目使用 `mvnw`（wrapper 3.3.4 / only-script / Maven 3.9.16 已下载到 `.m2`） |
| Node / npm | `node v24.21.0`、`npm 11.19.0` |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9 |
| 后端依赖 | Spring Boot 4.1.1（Web 起步依赖为 **spring-boot-starter-webmvc**）、Spring 7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc-openapi-starter-webmvc-ui 3.1.1、**Jackson 3**（`tools.jackson.core:jackson-databind:3.1.5`） |
| 数据库 | `backend/data/blog.db`；6 张表 + 7 个索引；种子数据 `article=3`、`tag=4`（另有阶段 2 实测产生的「级联测试标签」，计数 0）、`article_tag=5`；JDBC URL 已带 `busy_timeout=5000` |
| 服务状态 | **后端与前端 dev server 均已停止**（阶段 3 批 4 收尾时确认 `5173/8080 均已释放`） |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达（报错记录 1） |
| Git 仓库 | 分支 `main`；提交链：`501065a` → `25e280e` → `e9ab336` → `92a5cc6` → `b22592d` → `5c210a3` → `4b1700b` → `7174838` → `af2334c`（阶段 2 收尾）→ `8c5923d`（批 0）→ `7de72da`（批 1）→ `b257499`（批 2）→ `b1a517f`（批 3）→ 阶段 3 收尾（本次提交） |
| 前端构建基线 | 阶段 3 批 4 实测：`npm run build` → `✓ built in 142ms`；`index css 6.46 kB`、主包 `index-*.js 106.33 kB / gzip 41.62 kB`；四个视图各自独立 chunk |
| 前端页面结构 | 三条正式路由（`/`、`/articles`、`/about`）+ 404 兜底；页头 / 主内容 / 页脚三段式；主题键 `blog:theme` |

---

## 七、后端接口清单（阶段 2 产物 · 契约 v1.0）

**已实现并实测通过（13 个操作）**

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/health` | 健康检查（status + 服务器时间） |
| GET | `/api/articles` | 文章列表：分页 / `keyword` / `tags`+`tagMode=and·or` / `status=PUBLISHED·DRAFT·ALL` |
| GET | `/api/articles/{id}` | 文章详情（含 Markdown 正文；`prev` / `next` 恒为 `null`） |
| POST | `/api/articles` | 创建文章（201；标签不存在自动创建；`summary` 留空取正文前 120 字） |
| PUT | `/api/articles/{id}` | 更新文章（PUT 全量；`tags` 省略或 `[]` = 清空） |
| DELETE | `/api/articles/{id}` | 删除文章（级联清理评论 / 点赞 / 标签关联，标签本身保留） |
| GET | `/api/tags` | 标签列表（含文章数，按数量倒序） |
| GET | `/api/articles/{id}/comments` | 评论列表（分页，创建时间倒序） |
| POST | `/api/articles/{id}/comments` | 发表评论（201；`authorEmail` 与 `visitorId` 不回传） |
| DELETE | `/api/comments/{id}` | 删除评论（请求体带 `visitorId`；不存在或不匹配 → 40004） |
| GET | `/api/articles/{id}/likes?visitorId=` | 点赞状态与总数 |
| POST | `/api/articles/{id}/likes` | 点赞（幂等） |
| DELETE | `/api/articles/{id}/likes` | 取消点赞（幂等） |

**未实现（契约标为阶段 8 可选项）**：`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理 `POST` / `PUT` / `DELETE /api/tags`、`view_count` 计数。

**统一约定**：响应 `{code, message, data}`；错误码 `0` / `40001` / `40002` / `40004` / `40009` / `50000` / `50001`；时间格式 `yyyy-MM-dd'T'HH:mm:ss`；分页 `page` 从 1 起、`size` 1–20（默认 10）。

Swagger 文档：5 个分组（健康检查 / 文章 / 标签 / 评论 / 点赞）、13 条接口摘要，含统一返回与错误码说明。

---

## 八、如何启动与验证（标准命令）

```bash
# 后端（Git Bash）
cd backend && ./mvnw -B -ntp spring-boot:run
```
```powershell
# 后端（Windows PowerShell / CMD）
cd backend; .\mvnw.cmd -B -ntp spring-boot:run
```
```bash
# 前端
cd frontend && npm install && npm run dev
```

访问地址：前端 http://localhost:5173 ｜ 后端 http://localhost:8080 ｜ Swagger UI http://localhost:8080/swagger-ui/index.html ｜ API 前缀 `/api`（前端经 Vite 代理）。

> 贴士：① 路径含空格，`cd` 时**必须加引号**；② 后端只在**运行期间**才能访问 `/api/*` 与 Swagger UI（报错记录 5 的教训）；③ 停服务后如仍有进程占端口：`netstat -ano | grep ":5173" | grep -i listening`（或 `:8080`）查到 PID 后 `taskkill //PID <pid> //F`；④ 数据库重置＝停服 → 删除 `backend/data/blog.db`、`blog.db-wal`、`blog.db-shm` → 重启（会丢数据，属不可恢复操作）；⑤ 主题等本地数据重置＝DevTools → Application → Local Storage → 删除 `blog:` 前缀的键（阶段 5 会提供页面内重置按钮）。
