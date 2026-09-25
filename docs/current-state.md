# 当前状态

> 本文件是项目进度的**唯一实时快照**，每个阶段/批次结束后同步更新；**每个新会话开始时，请先读本文件**。
> 最后更新：**阶段 4「前端模块二 / 三：文章列表与文章详情」全部完成（批 0–批 5）并已入库**。阶段 0–3 亦全部完成并入库。**下一阶段：阶段 5（前端模块四 / 五 / 六：搜索与分类过滤、评论与点赞、本地持久化与重置）——等作者开工指令。**

---

## 零、新会话接手说明（给下一个会话的 AI）

1. **先读三个文件**：`AGENTS.md`（项目总纲与禁止事项）、`docs/current-state.md`（本文件）、`docs/api-contract.md`（接口契约 v1.0，**已确认**，实现时不得擅自改字段）。
2. **进度**：阶段 0、0.5、1、2、3、4 全部完成并入库。**前端六个核心模块中，模块一（导航与主题）、模块二（文章列表）、模块三（文章详情）已完成**；剩余 **模块四（搜索与分类过滤）、模块五（评论与互动）、模块六（本地持久化与模拟）属阶段 5**。后端 13 个操作已全部实现并实测（见第七节）。种子数据 12 篇文章 / 8 个标签。
3. **日志三件套**：`docs/ai-log.md`（逐轮流水）、`docs/collaboration-log.md`（关键提示词汇总 + 阶段索引 + 阶段记录）、`docs/debug-log.md`（报错专档，**8 条**真实记录 + 9 条观察项）；审计记录在 `docs/audit-report.md`（阶段 9 才做）。
4. **环境事实**：Node 24.21.0 / npm 11.19.0；JDK 26 已验证可跑 Spring Boot 4.1.1；Maven 用 `mvnw`；**后端与前端 dev server 当前均处于停止状态**，需要时用第八节命令启动。
5. **协作纪律**：分批交付、每批结束停下等确认；未运行的命令写"未运行，需我验证"；不编造运行结果；禁止使用现成博客模板或整站主题。
6. **本机已踩过的坑（细节都在 `docs/debug-log.md`）**：① Git Bash 里 `curl` 传中文参数会按 **GBK** 编码 → 改用 `node -e` 的 `fetch` 或 Swagger UI；② `wc -m` 在本机按**字节**计数；③ AI 后台任务有 **10 分钟上限**，超时只杀包装进程、**派生 JVM / node 会继续存活**占住端口（`taskkill //PID <pid> //F` 清理）；④ "拒绝连接"第一步永远是"确认服务是否真的在监听"；⑤ 内置浏览器**合成点击在部分元素上不送达**（主题按钮点不动 → 键盘 `Tab` + `Enter`；分页 / 重试 / 卡片链接 / 目录链接点按有效）；⑥ **路由过渡中间帧读不到"带过渡的子树"**（先截图再读结构）；⑦ 固定 ID 的幂等种子数据遇"历史行占用同 ID"会**静默错位**；⑧ **启动后端前先查 8080**，代理返回 502 通常就是"后端不在监听"；⑨ **后台标签页里 `requestAnimationFrame` 不触发、`IntersectionObserver` 首次投递被大幅推迟**（`setTimeout` 仍可用）→ 凡"等一帧/等可见"的逻辑都用 `setTimeout` 或先截图强制渲染。
7. **前端代码地图**：
   - `src/api/`：`http.js`（`request()` + 超时 8s + 解包 + 错误归一，502/503/504 有可操作文案）、`error.js`（`ApiError` + `isNotFound` / `isValidationError`）、`articles.js`、`tags.js`；**阶段 5 用到评论 / 点赞时再建 `comments.js` / `likes.js`**（决策 AB）。
   - `src/utils/`：`date.js`、`markdown.js`（`renderMarkdown()`：markdown-it `html:false` + `highlight.js/lib/common` + DOMPurify；标题补 id、外链 target+rel、图片 lazy）、`reveal.js`（全局指令 `v-reveal`）、`scrollSpy.js`（`useScrollSpy(getHeadings, { offset })`）。
   - `src/components/`：`ArticleList`（四态容器）、`ArticleCard`、`ArticleSkeleton`、`Pagination`、`SkeletonBlock`、`MarkdownRenderer`、`TableOfContents`、`AppHeader`、`AppFooter`、`ThemeToggle`。
   - 路由：`/`、`/articles`（页码与 `?page=N` 同步）、`/articles/:id`（两栏 + 目录 + 动态标题）、`/about`、404 兜底。
   - `base.css` 令牌：主题三态色、`--hl-*`（代码高亮亮/暗两套）、`.reveal` / `.is-revealed`。
8. **写代码时的两条硬经验（批 4 代价换来的，见报错记录 8）**：① **动画不能成为内容可见性的前提**（`v-reveal` 对"挂载时已在视口内"的元素是立即显现的，不依赖观察器）；② **取渲染结果（DOM 内容/尺寸）必须等目标分支真正挂载**（放到 `finally` 之后再 `await nextTick()`）。

---

## 一、当前阶段

**阶段 4「前端模块二 / 三：文章列表与文章详情」：全部完成 ✅ 并已入库（批 0 – 批 5）。**
**下一阶段：阶段 5「前端模块四 / 五 / 六」**——搜索与分类过滤、评论与点赞、本地持久化与一键重置；届时按惯例先出分批方案与决策点，等作者确认后开工。

阶段 4 分批（作者已同意方案与 10 个决策点）：批 0 开工基线 ✅ → 批 1 `src/api/` 接入层 ✅ → 批 2 文章列表 + 首页门面 ✅ → 批 3 文章详情 + Markdown 渲染 ✅ → 批 4 加分项（目录 / 滚动高亮 / 进场动画 / 图片懒加载）✅ → 批 5 收尾与文档 ✅。

**阶段 4 验收要点**：首页门面、文章列表（卡片 / 骨架屏 / 空态 / 错误态 + 重试 / 分页 / 深链）、文章详情（Markdown 渲染 / 代码高亮亮暗两套 / 404 与错误分流 / 动态标题）、右侧目录与滚动高亮（≥1024px）、滚动进场动画、三态主题下八处页面均正常。

---

## 二、已完成

| 时间点 | 事项 | 产物与证据 |
|---|---|---|
| 阶段 0 | 需求确认与技术选型 | Vue 3 + Vite + 原生 CSS 变量；Spring Boot 4.1.1 + JdbcTemplate + SQLite；0–9 阶段路线 |
| 阶段 0.5 | 协作日志规范确认 | `ai-log.md`（流水）+ `collaboration-log.md`（汇总），四套模板与禁止事项 |
| 阶段 1 批 1–4 | 目录骨架与占位文件落地 | 根目录 3 + docs 9 + frontend 12 + backend 15 文件；`git init`；契约与模型升级 v1.0；提交 `501065a` → `25e280e` |
| 报错修复 | 浏览器 `127.0.0.1:5173` 被拒绝 | 根因 Vite 只绑 IPv6；加 `host: '127.0.0.1'`（报错记录 2） |
| 阶段 2 批 0–4 | 后端业务实现（13 个操作） | 52 项接口实测；外键级联 / 点赞幂等 / `busy_timeout` 均实测；提交 `e9ab336` → `7174838` → `af2334c` |
| 阶段 3 批 0–4 | 前端模块一（导航 / 主题 / 404 / 移动端 / 过渡） | 见第四节；提交 `8c5923d` → `7de72da` → `b257499` → `b1a517f` → `378e3b0` |
| 阶段 4 批 0 | 开工基线：三项申请落地 + 种子数据 + 数据库重建 | `frontend/public/`（favicon + 12 张自绘封面）；`data.sql` 扩为 12 篇 + 8 标签 + 23 关联 + 封面回填；重置重建并实测幂等；提交 `ec848f3`。**修掉 1 个真实数据问题**（报错记录 6） |
| 阶段 4 批 1 | `src/api/` 接口访问层 | 4 个模块；**14 项真实调用用例全通过**；提交 `b6f7701`。**记录 1 个真实启动故障**（报错记录 7） |
| 阶段 4 批 2 | 文章列表 + 首页门面 | 5 个组件 + 2 个视图重写；**10 项浏览器实测**；提交 `af913f0` + 文档 `bc89da3` |
| 阶段 4 批 3 | 文章详情 + Markdown 渲染 | `utils/markdown.js` + `MarkdownRenderer` + `SkeletonBlock`；**15 项管线断言 + 6 项页面实测**；提交 `44b0d46` |
| 阶段 4 批 4 | 加分项：目录 + 滚动高亮 + 进场动画 + 图片懒加载 | `TableOfContents` + `utils/reveal.js` + `utils/scrollSpy.js`；**6 项浏览器实测**；提交 `16f1726`。**自查并修复 2 个真实缺陷**（报错记录 8） |
| 阶段 4 批 5 | 收尾：终验 + 文档同步（本次） | `npm run build` **123 模块 / 200ms**；浏览器终验 5 项（首页 / 深链第 2 页 / 路由 404 / 窄屏列表 / 三态主题沿用前批结论）；`README.md` 功能清单与加分项状态更新；本文件整份覆盖；`collaboration-log.md` 补阶段 4 记录 |

**关键决策记录（作者已确认）**

| 编号 | 决策 |
|---|---|
| A1–Q | 阶段 0–3 的历史决策（根目录沿用、双日志、`mvnw` 取自官方骨架、第 5 张关联表、技术栈锁定、契约与模型全通过、IPv4 绑定、2a/2b 拆分、文章 5 只删文章、native-access 只写 README、菜单三项、新增 404 页、主题三态、主题按钮位置、关于页纯前端、不新增子目录、批 2/批 3 不拆） |
| R | 阶段 4 三项开工申请全部同意：新增 `frontend/public/`、种子补到 12 篇、**直接联调真实后端**（不先写 mock） |
| S | 列表先用**分页控件**；"无限滚动 + 骨架屏"整体留阶段 7 |
| T | 首页＝简介 + 最新 3 篇 + 入口；完整列表在 `/articles` |
| U | 详情页 `prev` / `next` 阶段 4 **不渲染**（契约恒为 `null`） |
| V | 代码高亮**手写**亮 / 暗两套配色，不引官方主题 |
| W | 阶段 4 **不新增任何前端依赖** |
| X | 标题锚点 `id` 自己写 markdown-it 核心规则（中文标题 `section-<序号>` 兜底） |
| Y | 目录桌面（≥1024px）固定栏，窄屏隐藏 |
| Z | 目录高亮用 `scroll` + `getBoundingClientRect`；进场动画用 `IntersectionObserver`；复用逻辑放 `src/utils/` |
| AA | 种子 tag ID 冲突采用**标准重置路径**解决（作者确认） |
| AB | 批 1 只建 4 个 api 模块；`comments.js` / `likes.js` 推迟到阶段 5（避免死代码） |
| AC | 批 2 先加 `/articles/:id` 路由 + 占位详情页，避免卡片链接掉进 404 |
| AD | 批 3 骨架流光收敛为 `SkeletonBlock` 原语并复用 |
| AE | 详情页 404 只给"返回列表"，不给"重试" |
| AF | **动画不得成为内容可见性的前提**：视口内元素挂载即显现，只有视口外元素才交给观察器 |
| AG | 目录内容从**真实渲染结果**里取（`h2[id], h3[id]`），保证目录 id 与正文一致 |

---

## 三、待确认 / 待执行

1. **阶段 4 已整体交付**：批 0–批 5 全部入库（提交链见第六节）；**等作者开工指令后进入阶段 5**；
2. **阶段 5 开工前建议先出分批方案**（按惯例）：预计涉及「模块四 搜索与分类过滤（实时搜索 + 防抖、多选标签 + `tagMode`、空结果动画）」「模块五 评论与点赞（表单校验、点赞动画、Toast）」「模块六 本地持久化（`blog:visitorId`、已点赞集合、页面内一键重置）」，以及**新增 `src/api/comments.js` / `likes.js`**、可能新增 `src/stores/`（点赞 / Toast 等跨组件状态）；
3. **阶段 6 的定位需要作者确认**：契约与阶段索引里"阶段 6＝前后端对接（真实数据替换 mock）"，但**阶段 4 起就已是直接联调真实后端**（决策 R）——建议阶段 6 改为"全链路回归 + 异常 / 空态演练 + 契约逐条复核"，请作者拍板；
4. **可选未做项**：SQLite WAL 模式；契约标为阶段 8 的可选项（`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理、`view_count` 计数）。

---

## 四、阶段批结果

### 阶段 3 逐批结果（作者已确认）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线 | 状态块更正；`npm run build` 28 模块 / 160ms；提交 `8c5923d` |
| 1 ✅ | 主题系统（三态） | `stores/theme.js` + `ThemeToggle.vue` + `base.css` 令牌；31 模块 / 131ms；三态循环 + 刷新不丢 |
| 2 ✅ | 导航栏 + 路由 + 404 | `AppHeader` + `NotFoundView` + 占位视图与路由；39 模块且四视图独立 chunk |
| 3 ✅ | 汉堡菜单 + 滚动样式 | 375×667 折叠 / 键盘开合 / Esc / 遮罩 / 滚动后页头模糊；桌面无回归 |
| 4 ✅ | 路由过渡 + 收尾 | `AppFooter` + `<Transition>`；140ms / 主包 106.33 kB；提交 `378e3b0` |

### 阶段 4 逐批结果（作者已确认全部批次）

| 批 | 内容 | 结果与证据 |
|---|---|---|
| 0 ✅ | 开工基线（`ec848f3`） | 13 个静态资源（favicon + 12 张封面，实拍全部正常）；种子 3→12 篇 + 8 标签 + 23 关联；数据库重置重建 + 幂等复验；41 模块 |
| 1 ✅ | `src/api/` 接入层（`b6f7701`） | 4 个模块；14 项真实用例 14/14（含 40004 / 40002 / 超时 / 取消）；41 模块 / 126ms |
| 2 ✅ | 文章列表 + 首页门面（`af913f0`） | 5 组件 + 2 视图；10 项浏览器实测（卡片 / 分页 / 深链 / 空态 / 错误态 + 重试 / 375 / 暗色 / 悬停 / 骨架屏） |
| 3 ✅ | 文章详情 + Markdown（`44b0d46`） | 15 项管线断言（含 XSS 两层防护）+ 6 项页面实测；119 模块；详情 chunk 283.79 kB（懒加载） |
| 4 ✅ | 加分项（`16f1726`） | 目录 + 滚动高亮 + 进场动画 + 图片懒加载；6 项浏览器实测；123 模块。**自查修复 2 个真实缺陷**（报错记录 8） |
| 5 ✅ | 收尾（本次） | `npm run build` **123 模块 / 200ms**；终端验：首页（hero + 3 篇）、`/articles?page=2` 深链（2 张 + 第 2 页高亮 + 下一页禁用）、路由 404（`/no-such-page` → 「404 页面不存在」+ 返回首页 + 标题正确）、375px 列表（单列 + 汉堡菜单）、接口侧 `total=12` / 标签 8 个 / 详情 id=7 正文 503 字；`README.md` 功能清单与加分项状态更新 |

---

## 五、遗留问题

| # | 问题 | 影响 | 处理计划 |
|---|---|---|---|
| 1–7 | ~~已解决的历史项~~ | — | 后端首次启动、JDK 26 兼容性、依赖下载慢、契约与模型草案、路径含空格、`frontend/public/` 缺失、种子仅 3 篇（阶段 4 批 0 补齐 12 篇）——均已闭环 |
| 8 | 后端未做鉴权（演示项目） | 公网部署有风险 | 已在 `application.yml`、`AGENTS.md`、`README.md`、Swagger 描述中标注 |
| 9 | **WAL 未启用**（`busy_timeout=5000` 已生效） | 并发写场景收益有限，当前连接池为 1 | 需要时在 `connection-init-sql` 追加 `PRAGMA journal_mode = WAL` |
| 10 | 结束后台任务会留下派生进程（JVM / node），或启动前 8080 已有别的实例 | 端口占用 / 前端经代理拿到 502 | 已流程化：`netstat -ano \| grep ":端口" \| grep -i listening` + `taskkill //PID <pid> //F`；阶段 4 复现 5 次以上 |
| 11–15 | ~~已解决的历史项~~ | — | native-access 警告、外键约束验证、过渡观感与触摸点按（作者已验收）、tag ID 冲突（重置重建）——均已闭环 |
| 16 | **前端暂无自动化回归**（靠临时页 + 人工浏览器验证，页均删除） | 每批仍需人工跑一遍 | 阶段 5 继续沿用；如阶段 9 有余量再考虑固化成脚本 |
| 17 | 列表页 `?page` 超过总页数时只显示空态，不回退最后一页 | 手输极端页码体验一般 | 阶段 5 或阶段 7 视余量决定 |
| 18 | 骨架屏**出现时机**未能真机抓拍（本地请求约 10ms） | 只能证明外观正确 | 阶段 7 做无限滚动时一并观察 |
| 19 | 详情页**没有上一篇 / 下一篇**（契约 `prev` / `next` 恒为 `null`） | 少一条浏览路径 | 阶段 8 实现 `adjacent` 时一并渲染 |
| 20 | 详情页 chunk 286.22 kB（gzip 106.66 kB，含 markdown-it + hljs + dompurify） | 首次进详情页多下 ~107 kB（gzip） | 预期取舍（决策 W）；余量允许时可改 `hljs/lib/core` + 按需注册语言 |
| 21 | **Markdown 正文图片懒加载没有真实内容可验证**（12 篇种子文章都不含图片） | 该加分项只能靠构造样例断言（已通过） | 阶段 5 / 8 若有余量，给某篇文章补一张本地 SVG 配图（需改 `data.sql` + 幂等 `UPDATE` 或重置库）即可肉眼复验 |
| 22 | 目录只在桌面显示，窄屏没有可展开的入口 | 手机上无法跳转章节 | 决策 Y 的既定取舍；阶段 7 体验迭代可加"折叠式目录按钮" |
| 23 | 阶段 4 未实现「阅读进度条 / 回到顶部」（属模块三与阶段 7 的体验项） | 详情页少两项体验增强 | 阶段索引已把它们放在**阶段 7（功能迭代一）**；如需提前请作者指示 |

---

## 六、环境事实（实测，非推测）

| 项 | 实测结果 |
|---|---|
| 操作系统 | Windows，Git Bash（`git version 2.55.0.windows.5`） |
| JDK / Maven | `java 26.0.2.1`（已验证可跑 Spring Boot 4.1.1）；Maven 未安装，用 `mvnw`（wrapper 3.3.4 / Maven 3.9.16 已缓存） |
| Node / npm | `node v24.21.0`、`npm 11.19.0` |
| 前端依赖 | vue 3.5.43、vue-router 5.3.1、pinia 4.0.3、markdown-it 15.0.2、highlight.js 11.12.0、dompurify 3.4.15、vite 8.3.0、@vitejs/plugin-vue 6.0.9（决策 W：**不新增依赖**） |
| 后端依赖 | Spring Boot 4.1.1（`spring-boot-starter-webmvc`）、Spring 7.0.9、Tomcat 11.0.24、sqlite-jdbc 3.53.4.0、springdoc 3.1.1、Jackson 3 |
| 数据库 | `backend/data/blog.db`；6 张表 + 7 个索引；`article=12`、`tag=8`、`article_tag=23`；封面 12/12 非空；二次启动幂等；`busy_timeout=5000` |
| 前端静态资源 | `frontend/public/favicon.svg` + `images/covers/*.svg`（12 张，652–1438 字节） |
| 服务状态 | **后端与前端 dev server 均已停止**（阶段 4 收尾后复查 `5173/8080 均已释放`） |
| 网络 | `start.spring.io`、`registry.npmjs.org`、`repo.maven.apache.org`、`repo1.maven.org` 可达；`github.com` 不可达 |
| Git 仓库 | 分支 `main`；提交链：`501065a` → `25e280e` → `e9ab336` → `92a5cc6` → `b22592d` → `5c210a3` → `4b1700b` → `7174838` → `af2334c` → `8c5923d` → `7de72da` → `b257499` → `b1a517f` → `378e3b0` → `550f576` → `ec848f3`（批 0）→ `b6f7701`（批 1）→ `af913f0`（批 2）→ `bc89da3` → `44b0d46`（批 3）→ `9eb671a` → `16f1726`（批 4）→ 阶段 4 收尾文档（见 `git log` 最新一条） |
| 前端构建基线 | 阶段 4 收尾实测：`npm run build` → `✓ 123 modules transformed` / `✓ built in 200ms`；主包 `index-*.js 108.60 kB / gzip 42.49 kB`、`index css 7.10 kB`、详情 chunk `ArticleDetailView-*.js 286.22 kB / gzip 106.66 kB` + `6.25 kB` CSS、列表共享 chunk `ArticleList-*.js 2.94 kB`、`date-*.js 2.52 kB`、四个视图各自独立 chunk |
| 前端页面结构 | 四条正式路由 + 404 兜底；页头 / 主内容 / 页脚三段式；主题键 `blog:theme`；列表页码与 `?page=N` 同步；详情页两栏（正文 + 目录，≥1024px）、动态标题、滚动高亮；全局指令 `v-reveal`；10 个组件 |

---

## 七、后端接口清单（阶段 2 产物 · 契约 v1.0）

**已实现并实测通过（13 个操作）**：`GET /api/health`；`GET /api/articles`（分页 / `keyword` / `tags`+`tagMode` / `status`）；`GET /api/articles/{id}`；`POST /api/articles`；`PUT /api/articles/{id}`；`DELETE /api/articles/{id}`；`GET /api/tags`；`GET` / `POST /api/articles/{id}/comments`；`DELETE /api/comments/{id}`；`GET` / `POST` / `DELETE /api/articles/{id}/likes`。

**未实现（契约标为阶段 8 可选项）**：`GET` / `PUT /api/comments/{id}`、`GET /api/articles/{id}/adjacent`、标签管理 `POST` / `PUT` / `DELETE /api/tags`、`view_count` 计数。

**统一约定**：响应 `{code, message, data}`；错误码 `0` / `40001` / `40002` / `40004` / `40009` / `50000` / `50001`；时间格式 `yyyy-MM-dd'T'HH:mm:ss`；分页 `page` 从 1 起、`size` 1–20（默认 10）。Swagger：5 个分组、13 条接口摘要。

**阶段 5 会用到的接口（已实现，前端待接入）**：`GET /api/articles/{id}/comments`、`POST /api/articles/{id}/comments`、`DELETE /api/comments/{id}`（需 `visitorId`）、`GET/POST/DELETE /api/articles/{id}/likes`（幂等，需 `visitorId`）。

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

访问地址：前端 http://localhost:5173 ｜ 后端 http://localhost:8080 ｜ Swagger UI http://localhost:8080/swagger-ui/index.html ｜ API 前缀 `/api`。

> 贴士：① 路径含空格，`cd` 必须加引号；② 后端只在运行期间可访问 `/api/*` 与 Swagger；③ **启动后端前先查 8080**，端口被占先确认是不是已有实例；④ 停服后仍有进程占端口就用 `taskkill //PID <pid> //F`；⑤ 数据库重置＝停服 → 删 `blog.db` / `-wal` / `-shm` → 重启（丢数据，不可恢复）；⑥ 主题等本地数据重置＝DevTools → Local Storage 删 `blog:` 前缀键（阶段 5 会提供页面内重置按钮）；⑦ Git Bash 的 `curl` 传中文会按 GBK 编码，请用 `node -e` 的 `fetch` 或 Swagger UI；⑧ 代理返回 502 时先确认后端在不在；⑨ 阶段 4 成品可这样自验：http://localhost:5173 （首页）、`/articles` （列表 + 分页）、`/articles?page=2` （深链）、`/articles/7` （详情 + 目录，窗口 ≥1024px 可见右侧目录）、`/articles/999999` （文章不存在）、`/no-such-page` （404 页）。
