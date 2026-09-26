# 演示脚本与素材（`docs/demo/`）

> **用途**：把项目主指令 §七·6 要求的"一段项目演示"落成**可照做的分镜脚本** —— 每一步给出操作、命令、预期结果与对应截图编号。
> **录屏由作者按本脚本自行完成**（可以是录屏、GIF 或截图，**不能手机拍屏**）；AI 不代录、不代验收（`AGENTS.md` 协作规则 7）。
> **最后更新**：阶段 9 批 4（2026-09-26）。素材合计 **39 张**：stage6 15 张 + stage7 10 张 + stage8 5 张 + stage9 9 张。

---

## 一、准备（一次性）

```bash
# 后端（Git Bash）—— 先起后端，等控制台出现 Started BlogApplication 再起前端
cd "/d/code/Additional Full-stack Development of Personal blogs/backend" && ./mvnw spring-boot:run
```
```powershell
# 后端（Windows PowerShell）
cd "D:\code\Additional Full-stack Development of Personal blogs\backend"; .\mvnw.cmd spring-boot:run
```
```bash
# 前端（另开一个终端窗口）
cd "/d/code/Additional Full-stack Development of Personal blogs/frontend" && npm install --no-fund --no-audit && npm run dev
```

- **访问地址**：前端 http://localhost:5173 ｜ 写作台（隐藏入口）http://localhost:5173/studio ｜ 后端 http://localhost:8080 ｜ Swagger UI http://localhost:8080/swagger-ui/index.html
- **演示前建议**：数据库恢复种子状态（停服 → 删 `backend/data/blog.db*` → 重启，见 README 第五节，**会丢数据**）；本地数据用 `/about` → 「重置本地数据」清空
- **窗口建议**：桌面宽度 **≥ 1024px**（右侧固定目录才会显示）；另备一个终端窗口用于后端命令；窄屏效果用 DevTools 设备模式 375×667
- **数据说明**：演示中 `view_count` 会随访问增长，属预期行为；五元组 `12 / 8 / 23 / 0 / 0` 是种子状态

---

## 二、前端演示（6 步）

| # | 步骤 | 操作 | 预期 | 对应截图 |
|---|---|---|---|---|
| 1 | **首页列表加载** | 打开 http://localhost:5173/ → 点「浏览全部文章」→ 滚到列表底部 | 首页 hero + 「最新文章」卡片；列表页「共 12 篇文章」，滚动到底自动累积并显示「**已经到底了 · 共 12 篇**」 | `stage9-08-home.png`、`stage9-02-list-page.png`、`stage7-05-infinite-scroll-end.png` |
| 2 | **进入详情** | 点任意卡片（推荐第 6 篇，含代码块与正文配图） | 标题 / meta（阅读 · 点赞 · 评论）/ 封面 / Markdown 正文 / 代码高亮 / **右侧固定目录 + 滚动高亮** / 上下篇 / 评论区 | `stage9-06-desktop-toc.png`、`stage6-02-detail-code-highlight.png`、`stage6-03-detail-inline-image.png` |
| 3 | **搜索过滤** | 搜索框输入 `SQLite`（停 300ms 自动过滤）→ 点标签 chips 多选 → 切「同时包含 / 任一即可」→ 再输入一个不存在的词 | 关键词命中 3 篇；多标签 AND / OR 语义切换生效；无匹配时出现**空状态插画 + 「清除筛选」** | `stage9-03-empty-filter.png` |
| 4 | **点赞与评论** | 点赞按钮 → 评论框输入内容 → 「发表评论」（首次会弹昵称 / 邮箱弹窗，填一次即记住）→ 再删除刚发的评论 | 点赞数字有 pop 动画、按钮变「已点赞」；评论「已发表」提示 + 头部「评论 N」+1；删除走**行内二次确认**，删完回到「还没有评论」 | `stage7-07-comment-posted.png`、`stage7-08-comment-after-delete.png`、`stage8-02-comment-edit.png` |
| 5 | **深色模式切换** | 点页头右侧主题按钮循环三态 | 「亮色 → 暗色 → 跟随系统」；暗色下正文 / 次要文字 / 链接对比度均达 WCAG AA（详见审计报告 B 层 §5） | `stage9-04-dark-detail.png`、`stage6-04-detail-dark.png` |
| 6 | **刷新后数据保留** | 换主题 + 点赞 + 发表一条评论后按 F5 | 主题仍是暗色、点赞仍是「已点赞」、评论仍在；`/about` 的「本地数据」面板可看到本机 6 个 `blog:` 键 | `stage6-06-about-localdata-toast.png` |

---

## 三、后端演示（6 步）

> 命令以 Git Bash 为例；PowerShell 把 `curl` 换成 `curl.exe`、行内 JSON 用双引号转义即可。**Git Bash 的 `curl` 传中文会按 GBK 编码**（见 `docs/debug-log.md` 报错记录 4），中文参数建议改用 Swagger UI 或 `node -e "fetch(...)"`。

| # | 步骤 | 命令 / 操作 | 预期 | 对应截图 |
|---|---|---|---|---|
| 1 | **启动** | `cd backend && ./mvnw spring-boot:run`；另开窗口执行 `curl -s http://localhost:8080/api/health` | 控制台出现 `Tomcat started on port 8080` 与 `Started BlogApplication in X seconds`；接口返回 `{"code":0,"message":"ok","data":{"status":"UP",...}}`；浏览器打开 Swagger UI 可见 **5 个分组 / 20 个操作** | `stage9-09-swagger.png` |
| 2 | **创建文章** | `curl -s -X POST http://localhost:8080/api/articles -H "Content-Type: application/json" -d '{"title":"演示：新建一篇文章","content":"## 小标题\n\n这是演示用的正文。","status":"PUBLISHED","tags":["演示"]}'` | HTTP **201**，`data` 为新的 `ArticleSummary`（含 `id` / `summary` 自动取正文前 120 字 / `tags` 自动创建）；刷新前端列表可见新文章 | — |
| 3 | **列表查询** | `curl -s "http://localhost:8080/api/articles?size=5&page=1"`；再试 `?keyword=SQLite`、`?tags=Vue,前端&tagMode=or`、`?status=DRAFT` | 返回分页对象 `{items,page,size,total,totalPages}`；默认只含 `PUBLISHED`、按 `createdAt` 倒序；`keyword` / 多标签 / 状态过滤均生效 | — |
| 4 | **修改文章** | `curl -s -X PUT http://localhost:8080/api/articles/<id> -H "Content-Type: application/json" -d '{"title":"演示：标题已修改","content":"正文……","status":"DRAFT"}'` | HTTP 200，`data` 为更新后的 `ArticleSummary`；转 `DRAFT` 后该文章从默认列表消失（`?status=DRAFT` 仍可查到） | — |
| 5 | **删除文章** | `curl -s -X DELETE http://localhost:8080/api/articles/<id>` → 再 `curl -s http://localhost:8080/api/articles/<id>` | 删除返回 `data: null`；再查详情 → HTTP 404 / `code 40004`。**注意**：删除文章后**标签会保留**（既定行为） | — |
| 6 | **重启后数据保留** | 在后端窗口按 `Ctrl + C` 停服 → 重新 `./mvnw spring-boot:run` → 重新查询第 2 步创建的文章（或任意文章） | 数据仍在（单文件 SQLite：`backend/data/blog.db`，运行时伴随 `blog.db-wal` / `blog.db-shm`）；启动日志会再次打印 `Started BlogApplication` | — |

> **演示后清理**：把自己创建的文章删掉；如需恢复严格种子状态，按 README 第五节重置数据库（`article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`）。

---

## 四、可选加分项演示（按时间取舍）

| 项目 | 展示方式 | 截图 |
|---|---|---|
| 阅读进度条 + 回到顶部 | 详情页滚动到中部 / 底部 | `stage7-01-reading-progress-mid.png`、`stage7-02-bottom-back-to-top.png` |
| 窄屏目录折叠 | 设备模式 375 宽，展开「本页目录」并点条目 | `stage7-06-toc-panel-375.png`、`stage9-05-responsive-375.png` |
| 写作台（隐藏入口） | 打开 http://localhost:5173/studio：新建 / 编辑 / 删除 + 草稿 ↔ 发布 + 标签管理 | `stage8-04-studio.png` |
| 异常与降级 | 停掉后端 → 列表 / 详情显示错误态与「重试」；点赞弹 Toast | `stage6-08-error-list.png`、`stage6-09-error-detail.png`、`stage6-10-error-toast.png` |
| XSS 防护 | 正文 / 标题 / 标签 / 评论四类载荷一律按纯文本渲染 | `stage6-11-xss-as-text.png` |
| 404 与键盘可达 | 访问 `/no-such-page`；Tab 遍历页头看焦点环 | `stage6-07-404-page.png`、`stage6-14-focus-visible.png` |
| 响应式三档 | 375 / 768 / 1280 / 1920 | `stage9-05`、`stage9-07`、`stage9-06`、`stage7-09-responsive-1920.png` |
| 管线加载失败兜底 | 断网或资源缺失时正文区显示错误块（不可复现属正常） | `stage9-01-markdown-load-error.png` |

---

## 五、素材索引（39 张）

### 阶段 9（阶段 9 批 2–批 4，9 张）

| 文件 | 内容 |
|---|---|
| `stage9-01-markdown-load-error.png` | Markdown 管线加载失败的正文兜底（标题 + 说明 + 刷新按钮） |
| `stage9-02-list-page.png` | 文章列表页（搜索框、标签面板、卡片网格） |
| `stage9-03-empty-filter.png` | 筛选出 0 篇文章的空状态（插画 + 清除筛选） |
| `stage9-04-dark-detail.png` | 暗色详情页（含正文配图与点赞按钮） |
| `stage9-05-responsive-375.png` | 375×667 窄屏（汉堡菜单 + 单列卡片 + 暗色） |
| `stage9-06-desktop-toc.png` | 1280 桌面详情页（右侧固定目录 + 当前小节高亮） |
| `stage9-07-responsive-768.png` | 768×1024（桌面导航 + 「本页目录」折叠面板 + 代码高亮） |
| `stage9-08-home.png` | 首页（hero + 最新文章卡片） |
| `stage9-09-swagger.png` | Swagger UI（接口说明、错误码、标签分组） |

### 阶段 8（后端能力迭代，5 张）

| 文件 | 内容 |
|---|---|
| `stage8-01-adjacent-nav.png` | 详情页底部「上一篇 / 下一篇」 |
| `stage8-02-comment-edit.png` | 评论行内「编辑」表单 |
| `stage8-03-adjacent-nav-375.png` | 窄屏上下篇堆叠 |
| `stage8-04-studio.png` | 写作台 `/studio`（文章 CRUD + 标签管理） |
| `stage8-05-detail-dark.png` | 暗色详情页（阶段 8 新增 UI 复核） |

### 阶段 7（前端体验迭代，10 张）

| 文件 | 内容 |
|---|---|
| `stage7-01-reading-progress-mid.png` | 阅读进度条（页头下沿） |
| `stage7-02-bottom-back-to-top.png` | 页面底部「回到顶部」 |
| `stage7-03-dark-back-to-top.png` | 暗色下的回到顶部 |
| `stage7-04-detail-after-split.png` | 详情页拆包后的加载表现 |
| `stage7-05-infinite-scroll-end.png` | 无限滚动到底 |
| `stage7-06-toc-panel-375.png` | 窄屏「本页目录」折叠面板 |
| `stage7-07-comment-posted.png` | 评论发表成功提示 |
| `stage7-08-comment-after-delete.png` | 删除评论后回到空态 |
| `stage7-09-responsive-1920.png` | 1920 宽屏（亮色） |
| `stage7-10-dark-1920.png` | 1920 宽屏（暗色） |

### 阶段 6（全链路回归与异常演练，15 张）

| 文件 | 内容 |
|---|---|
| `stage6-01-list-empty-state.png` | 列表筛选空态 |
| `stage6-02-detail-code-highlight.png` | 代码块高亮 |
| `stage6-03-detail-inline-image.png` | 正文内联配图 |
| `stage6-04-detail-dark.png` | 暗色详情页 |
| `stage6-05-comments.png` | 评论区 |
| `stage6-06-about-localdata-toast.png` | `/about` 本地数据面板 + 重置 Toast |
| `stage6-07-404-page.png` | 404 页 |
| `stage6-08-error-list.png` | 后端停服时的列表错误态 |
| `stage6-09-error-detail.png` | 后端停服时的详情错误态 |
| `stage6-10-error-toast.png` | 后端停服时的 Toast |
| `stage6-11-xss-as-text.png` | XSS 载荷按纯文本渲染 |
| `stage6-12-responsive-375.png` | 375 窄屏 |
| `stage6-13-responsive-768.png` | 768 平板 |
| `stage6-14-focus-visible.png` | 键盘焦点环 |
| `stage6-15-global-error-toast.png` | 全局错误兜底 Toast |

---

## 六、注意事项

1. **录屏形态**：录屏 / GIF / 截图均可，**不能手机拍屏**；建议覆盖 §二 + §三 的 12 个步骤（这是主指令明确点名的演示点）；
2. **顺序建议**：先前端 6 步（一镜到底更像真实使用），再后端 6 步（终端 + Swagger 交替，重启保留单独收尾）；
3. **不要演示真实删除**：`/studio` 与 `DELETE` 接口都是**演示级无鉴权**，演示时只删自己刚建的文章；删除文章后标签会保留，属既定行为；
4. **演示前请重置数据**（数据库 + 本地数据），结束后如需回到种子状态同样按 README 第五节处理；
5. 本目录的 `.png` 均为真实浏览器截图（阶段 6 / 7 / 8 / 9 各批实测时归档），文件名前缀对应阶段号。

---

## 附录 A：后端演示逐步脚本（Swagger UI 版 · 录屏用）

> 用途：主指令 §七·6 要求后端演示 6 个点（**启动、创建文章、列表查询、修改、删除、重启后数据保留**）。本附录给出一份"照着念、照着点"的版本；命令行的等价做法见 §三 表格。
> 为什么用 Swagger UI：**Git Bash 的 `curl` 传中文会按 GBK 编码**（见 `docs/debug-log.md` 报错记录 4），Swagger 是表单提交、无此问题，且顺带展示了"API 文档"这个加分项。

### A.0 开录前 3 分钟准备

| 检查项 | 命令 / 操作 | 期望 |
|---|---|---|
| 后端在跑 | `curl.exe -s http://localhost:8080/api/health`（PowerShell）或浏览器打开该地址 | `{"code":0,...,"status":"UP"}` |
| 前端在跑 | 浏览器打开 http://localhost:5173/articles | 列表页「共 12 篇文章」 |
| 数据是种子状态 | 见 §五 或 `GET /api/tags` | `article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0` |
| 窗口摆放 | 终端 A（后端）＋ 浏览器标签 1（Swagger UI：`http://localhost:8080/swagger-ui/index.html`）＋ 标签 2（前端列表） | 浏览器缩放 100%，窗口 ≥1280 宽（Swagger 右侧不折叠） |
| 随手记 id | 便签 / 记事本 | 第 2 步拿到的 `id` 后面 PUT / DELETE 都要用 |

### A.1 第 1 步 · 启动（约 20 秒镜头）

```bash
cd "/d/code/Additional Full-stack Development of Personal blogs/backend" && ./mvnw spring-boot:run
```

镜头给到这三行（这是"启动了"的硬证据）：

1. `Starting BlogApplication using Java 26.0.2.1 with PID ...`
2. `Tomcat started on port 8080 (http) with context path '/'`
3. **`Started BlogApplication in X seconds`**

随后切浏览器：`/api/health` 显示 `code=0 / status=UP` → 再切 Swagger UI，滚动展示 **5 个分组（健康检查 / 文章 / 标签 / 评论 / 点赞）与 20 个操作**，顺带念一下顶部的统一约定（`{code,message,data}`、7 个错误码、分页 `page` 从 1 起、`size` 1–20、演示级无鉴权）。

### A.2 第 2 步 · 创建文章（POST /api/articles）

Swagger UI → **文章**分组 → `POST /api/articles` → **Try it out** → Request body 粘贴：

```json
{
  "title": "演示：新建一篇文章",
  "content": "## 小标题\n\n这是演示用的正文，用来验证创建接口。",
  "status": "PUBLISHED",
  "tags": ["演示"]
}
```

→ **Execute**。期望：**HTTP 201**，响应 `data` 里能看到 `id`（记下）、`summary`（留空时后端自动截正文前 120 字）、`tags: ["演示"]`、`viewCount: 0`。

**接着把镜头切到前端标签并刷新** → 新文章出现在列表顶部（前后端联通的直观证据），点进去能看到 Markdown 正文。

**（可选加分镜头）** 把 `title` 清空再 Execute → **400 / `code 40001`**，`data.fields.title = "标题不能为空"` —— 说明"字段级校验原因"。

### A.3 第 3 步 · 列表查询（GET /api/articles）

`GET /api/articles` → Try it out，依次改参数（每次 Execute 都能看到 `{items,page,size,total,totalPages}`）：

| 参数 | 期望 |
|---|---|
| （不带参数） | 默认 `page=1&size=10&status=PUBLISHED`，只含**已发布**、按 `createdAt` 倒序 |
| `size=5&page=2` | 第二页 5 条（共 12 篇） |
| `keyword=SQLite` | **3 篇**（只按**标题**模糊匹配） |
| `tags=Vue,前端&tagMode=and` | **2 篇**（同时包含） |
| `tags=Vue,前端&tagMode=or` | **5 篇**（任一即可） |
| `status=DRAFT` | 当前草稿（种子状态为 0 篇；第 4 步之后会变成 1 篇） |
| `size=999`（反例） | **400 / `code 40002`**（`size` 上限 20） |

### A.4 第 4 步 · 修改文章（PUT /api/articles/{id}，全量语义）

`PUT /api/articles/{id}`（填第 2 步的 id）→ Try it out → 请求体：

```json
{
  "title": "演示：标题已修改（已转草稿）",
  "content": "## 小标题\n\n这是演示用的正文，用来验证创建接口。",
  "status": "DRAFT",
  "tags": ["演示"]
}
```

→ Execute → **200** + 更新后的对象（标题已变、`status=DRAFT`）。然后：

- `GET /api/articles`（默认）→ **该文章不在列表里**；
- `GET /api/articles?status=DRAFT` → **能查到**；
- 前端标签刷新 → 列表里也看不到了（草稿不对外展示）；
- 但 `GET /api/articles/{id}`（详情）**仍能读到该草稿** —— 前端直接打开 `/articles/{id}` 也能看到正文（详情接口不按状态过滤，这点可选展示）。

> 提醒：`PUT` 是**全量更新**，`title` / `content` / `status` 必填；`tags` 省略或传 `[]` 都表示**清空标签**。

### A.5 第 5 步 · 重启后数据保留（**关键镜头，建议放在删除之前**）

1. 终端 A 按 **`Ctrl + C`** 停服（画面出现 `Stopping service [Tomcat]` 之类）；
2. 重新执行 `./mvnw spring-boot:run`，等 `Started BlogApplication in X seconds`；
3. 浏览器再查 `GET /api/articles/{id}`（或 `status=DRAFT` 的列表）→ **标题是改过的、状态仍是 DRAFT —— 数据还在**。

讲解点：数据落在**单文件 SQLite**（`backend/data/blog.db`，运行时伴随 `blog.db-wal` / `blog.db-shm`）；每次启动都会幂等执行 `schema.sql` + `data.sql`，不会重复插入（可打开 `backend/data/` 目录展示文件与时间戳）。

### A.6 第 6 步 · 删除文章（收尾）

- `DELETE /api/articles/{id}` → Execute → **200，`data: null`**；
- 再 `GET /api/articles/{id}` → **404 / `code 40004`**；
- 想在前端"看到少一条"：删除前先把它 `PUT` 回 `PUBLISHED` 再删；
- ⚠️ **删除文章不会删除标签**：`GET /api/tags` 里"演示"标签仍在，只是 `articleCount` 归 0。演示收尾可再 `DELETE /api/tags/{id}` 把标签也清掉。

### A.7 录完的收尾

```bash
# 只想清掉演示数据：在 Swagger 里删掉演示创建的文章（+ 标签）
# 想彻底回到种子状态：停服 → 删数据库三件套 → 重启（会丢数据，不可恢复）
cd "/d/code/Additional Full-stack Development of Personal blogs/backend" && rm -f data/blog.db data/blog.db-wal data/blog.db-shm
```

### A.8 录制中途常见问题

| 现象 | 原因 | 处理 |
|---|---|---|
| Swagger UI 打不开 / 连接被拒 | 后端没在运行 | 先启动后端，等 `Started BlogApplication` 再刷新 |
| 端口 8080 被占用（`Port 8080 was already in use`） | 已有一个实例在跑 | `netstat -ano \| grep ":8080 " \| grep -i listening` 取 PID → `taskkill //PID <pid> //F` |
| Git Bash 里 `curl` 带中文报 `40002` / 乱码 | curl 按 GBK 编码参数 | 改用 Swagger UI，或 PowerShell 的 `curl.exe`，或 `node -e` 的 `fetch` |
| 前端列表没变化 | 页面没刷新 | 手动 F5（列表页不会自动同步后端改动） |
| 想从头再录一遍 | 数据已被改 | 停服 → 删 `data/blog.db*` → 重启（约 2 秒恢复 12 篇种子文章） |
