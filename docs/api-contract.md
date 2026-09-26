# 接口契约（v1.1 · 已确认）

> **状态：已确认（作者回复"批 1 通过，请继续"，2026-09-26）。** v1.1 按阶段 8 决策 BR–BX 升级：第 10 / 11 / 16 / 17 条由"阶段 8 可选"转为正式条目，新增第 18 条（阅读数）与 `viewCount` 字段。后端与前端按本文件实现；实现期间如需变更字段，必须先改本文件并重新确认。
> 契约是前后端唯一事实来源：任何改动先改本文件，再改后端，再改前端。
> 最后更新：阶段 1 批 2 起草 → v1.0 确认；**阶段 5 批 1 补注** likes 的 `visitorId` 必填；**阶段 8 批 1 升级为 v1.1**（评论单条查询 / 修改、相邻文章、标签管理 + `40009`、阅读数 `POST /views` 与 `viewCount` 字段；**未改任何已确认字段的语义**）。

## 确认记录

| # | 待确认项 | 作者结论 |
|---|---|---|
| 1 | 多标签过滤语义 `tagMode` | 采用建议：默认 `and`，保留 `or` 可选 |
| 2 | 分页 `size` 上限 20 | 同意 |
| 3 | 更新文章用 `PUT` 全量 | 同意 |
| 4 | 增加 `GET /api/health` | 同意 |
| 5 | 允许匿名评论 | 同意 |
| 6 | 点赞去重用前端生成的 `visitorId` | 同意 |
| 7 | 关键词先只搜标题 | 同意 |
| 8 | 保留"标签管理"接口为阶段 8 可选 | 同意（阶段 8 已转正式，见下表） |

**阶段 8 确认记录（v1.1，决策 BR–BX，作者"均同意，请继续"，2026-09-26）**

| # | 事项 | 结论 |
|---|---|---|
| 1 | 第 10 / 11 条（评论单条查询 / 修改） | 转正式；修改仅改 `content`，`visitorId` 须与创建时一致，不匹配返回 `40004`；**不加 `updatedAt` 字段、不改表结构**（决策 BS） |
| 2 | 第 16 条（上一篇 / 下一篇） | 转正式；**只取 `PUBLISHED`**、`ORDER BY created_at, id`；`GET /api/articles/{id}` 的 `prev` / `next` **同步实装**（前端零额外请求）（决策 BT） |
| 3 | 第 17 条（标签管理） | 转正式；**启用 `40009`**（标签名重复，创建 / 改名均适用）（决策 BE） |
| 4 | 第 18 条（阅读数，新增） | `POST /api/articles/{id}/views` 自增；`viewCount` 进 `ArticleSummary` / `ArticleDetail`；GET 保持无副作用（决策 BU） |
| 5 | WAL | `journal_mode=WAL` 由 JDBC URL 落地（决策 BW）；**接口层无变化**，记录于 `docs/data-model.md` |
| 6 | 搜索范围 | 保持只搜标题，不扩展到摘要（决策 BV） |
| 7 | 前端管理入口 | 隐藏路由 `/studio`（文章 CRUD + 草稿切换 + 标签管理），演示级无鉴权（决策 BR） |

> 文末「六、待作者确认的点」保留作为决策留档；阶段 8 的决策以本表与 `docs/current-state.md` §二 为准。

---

## 一、通用约定

| 项 | 约定 |
|---|---|
| 基础路径 | 所有接口以 `/api` 开头 |
| 前端调用方式 | 前端代码里写 `/api/...`，由 Vite 开发代理转发到 `http://localhost:8080`（规避 CORS） |
| 请求体格式 | `Content-Type: application/json; charset=utf-8` |
| 响应格式 | 统一为 `{ "code": 0, "message": "ok", "data": ... }` |
| JSON 字段命名 | 小驼峰 `camelCase`（如 `coverUrl`、`createdAt`） |
| 时间格式 | ISO-8601 字符串，本地时区无偏移，例：`2026-09-23T10:20:30` |
| ID 类型 | 整数（SQLite `INTEGER PRIMARY KEY`） |
| 分页参数 | `page`（从 1 开始，默认 1）、`size`（默认 10，范围 1–20） |
| 分页响应 | `{ "items": [...], "page": 1, "size": 10, "total": 42, "totalPages": 5 }` |
| 空数据 | `data` 为 `null` 或空数组，不省略字段 |
| 排序 | 列表默认按 `createdAt` 倒序（最新在前） |

### 成功与错误响应示例

```json
{ "code": 0, "message": "ok", "data": { "id": 1 } }
```

```json
{
  "code": 40001,
  "message": "参数校验失败",
  "data": { "fields": { "title": "标题不能为空", "content": "内容长度需在 1-50000 之间" } }
}
```

### 错误码表

| code | 对应 HTTP 状态 | 含义 |
|---|---|---|
| `0` | 200 / 201 | 成功 |
| `40001` | 400 | 参数校验失败（`data.fields` 给出字段级原因） |
| `40002` | 400 | 参数格式错误（如 `page=abc`、非法 JSON） |
| `40004` | 404 | 资源不存在（文章/评论 ID 不存在） |
| `40009` | 409 | 资源冲突（如标签名重复） |
| `50000` | 500 | 服务端未预期异常 |
| `50001` | 500 | 数据库操作失败 |

HTTP 状态码与业务 `code` 同时返回：前端先看 HTTP 是否 2xx，再看 `code` 是否为 0。

---

## 二、数据结构

### 2.1 `TagVO`

```json
{ "id": 3, "name": "Vue", "articleCount": 5 }
```

### 2.2 `ArticleSummary`（列表项）

```json
{
  "id": 1,
  "title": "Vue 3 组合式 API 实践笔记",
  "summary": "记录组合式 API 在实际项目中的组织方式……",
  "coverUrl": "/images/covers/vue-basics.svg",
  "status": "PUBLISHED",
  "tags": ["Vue", "前端"],
  "likeCount": 3,
  "commentCount": 2,
  "viewCount": 42,
  "createdAt": "2026-09-23T10:20:30",
  "updatedAt": "2026-09-23T10:20:30"
}
```

> `viewCount` 为**阶段 8 新增字段**（决策 BU）：由 `POST /api/articles/{id}/views` 自增，不做按访客去重；列表与详情均返回。

### 2.3 `ArticleDetail`（详情）

`ArticleSummary` 的全部字段，外加：

```json
{
  "content": "# 标题\n\n正文 Markdown 原文……",
  "prev": { "id": 2, "title": "上一篇标题" },
  "next": { "id": 4, "title": "下一篇标题" }
}
```

> `prev` / `next` **阶段 8 起实装**（决策 BT）：只把 `PUBLISHED` 纳入相邻链、排序 `ORDER BY created_at, id`；首篇的 `prev` 与尾篇的 `next` 为 `null`。独立接口 `GET /api/articles/{id}/adjacent` 提供同一语义（详情接口直接带出，前端无需额外请求）。

### 2.4 `CommentVO`

```json
{
  "id": 12,
  "articleId": 1,
  "authorName": "小明",
  "content": "写得很清楚，收藏了。",
  "createdAt": "2026-09-23T11:00:00"
}
```

> `authorEmail` 仅用于服务端保存与校验，**不回传前端**（隐私考虑，见 `data-model.md`）。

### 2.5 `LikeStateVO`

```json
{ "articleId": 1, "liked": true, "likeCount": 4 }
```

---

## 三、接口清单

| # | 方法 | 路径 | 说明 | 实现阶段 |
|---|---|---|---|---|
| 1 | GET | `/api/health` | 健康检查，用于确认后端已启动 | 阶段 2 |
| 2 | GET | `/api/articles` | 文章列表（分页 / 关键词 / 标签 / 状态） | 阶段 2 |
| 3 | GET | `/api/articles/{id}` | 文章详情（含 `prev` / `next`） | 阶段 2（`prev` / `next` 阶段 8 实装） |
| 4 | POST | `/api/articles` | 创建文章 | 阶段 2 |
| 5 | PUT | `/api/articles/{id}` | 更新文章 | 阶段 2 |
| 6 | DELETE | `/api/articles/{id}` | 删除文章（级联删除评论与点赞） | 阶段 2 |
| 7 | GET | `/api/tags` | 标签列表（含每个标签的文章数） | 阶段 2 |
| 8 | GET | `/api/articles/{id}/comments` | 某文章的评论列表（分页） | 阶段 2 |
| 9 | POST | `/api/articles/{id}/comments` | 发表评论 | 阶段 2 |
| 10 | GET | `/api/comments/{id}` | 单条评论 | 阶段 8 |
| 11 | PUT | `/api/comments/{id}` | 修改评论（仅 `content`，`visitorId` 归属校验） | 阶段 8 |
| 12 | DELETE | `/api/comments/{id}` | 删除评论 | 阶段 2 |
| 13 | GET | `/api/articles/{id}/likes` | 查询点赞状态与总数 | 阶段 2 |
| 14 | POST | `/api/articles/{id}/likes` | 点赞（幂等） | 阶段 2 |
| 15 | DELETE | `/api/articles/{id}/likes` | 取消点赞（幂等） | 阶段 2 |
| 16 | GET | `/api/articles/{id}/adjacent` | 上一篇 / 下一篇（只含 `PUBLISHED`） | 阶段 8 |
| 17 | POST/PUT/DELETE | `/api/tags`、`/api/tags/{id}` | 标签管理（重名 → `40009`） | 阶段 8 |
| 18 | POST | `/api/articles/{id}/views` | 阅读数 +1（无去重，演示级） | 阶段 8 |

---

## 四、接口明细

### 1. 健康检查

- **方法 / 路径**：`GET /api/health`
- **请求参数**：无
- **响应**：

```json
{ "code": 0, "message": "ok", "data": { "status": "UP", "time": "2026-09-23T10:20:30" } }
```

### 2. 文章列表

- **方法 / 路径**：`GET /api/articles`
- **查询参数**：

| 参数 | 类型 | 必填 | 默认 | 说明 |
|---|---|---|---|---|
| `page` | int | 否 | 1 | 页码，从 1 开始 |
| `size` | int | 否 | 10 | 每页条数，1–20 |
| `keyword` | string | 否 | - | 标题关键词，模糊匹配（`LIKE %kw%`） |
| `tags` | string | 否 | - | 标签名，逗号分隔可多选，例：`tags=Vue,前端` |
| `tagMode` | string | 否 | `and` | 多标签组合语义：`and`（同时包含）/ `or`（任一） |
| `status` | string | 否 | `PUBLISHED` | `PUBLISHED` / `DRAFT` / `ALL` |

- **响应 `data`**：分页对象，`items` 为 `ArticleSummary[]`
- **错误**：`40002`（page/size 非数字或越界）

### 3. 文章详情

- **方法 / 路径**：`GET /api/articles/{id}`
- **响应 `data`**：`ArticleDetail`
- **错误**：`40004`（文章不存在）

### 4. 创建文章

- **方法 / 路径**：`POST /api/articles`
- **请求体**：

```json
{
  "title": "必填，1-100 字",
  "summary": "可选，0-200 字，留空时由后端截取正文前 120 字",
  "content": "必填，1-50000 字，Markdown 原文",
  "coverUrl": "可选，封面图地址",
  "status": "可选，PUBLISHED（默认）或 DRAFT",
  "tags": ["可选，最多 5 个标签名，不存在的标签自动创建"]
}
```

- **响应**：HTTP 201，`data` 为创建的 `ArticleSummary`
- **错误**：`40001`、`40009`

### 5. 更新文章

- **方法 / 路径**：`PUT /api/articles/{id}`（**全量更新**语义）
- **请求体**：字段同创建接口；`title`、`content`、`status` 必填；`tags` 省略表示"清空标签"，传 `[]` 同样表示清空
- **响应 `data`**：更新后的 `ArticleSummary`
- **错误**：`40001`、`40004`、`40009`

### 6. 删除文章

- **方法 / 路径**：`DELETE /api/articles/{id}`
- **行为**：级联删除该文章的评论、点赞记录、标签关联（标签本身保留）
- **响应**：`data` 为 `null`
- **错误**：`40004`

### 7. 标签列表

- **方法 / 路径**：`GET /api/tags`
- **响应 `data`**：`TagVO[]`，按文章数倒序
- **用途**：前端模块四的标签多选过滤器

### 8. 评论列表

- **方法 / 路径**：`GET /api/articles/{id}/comments`
- **查询参数**：`page`、`size`（同通用约定）
- **响应 `data`**：分页对象，`items` 为 `CommentVO[]`，按 `createdAt` 倒序
- **错误**：`40004`（文章不存在）

### 9. 发表评论

- **方法 / 路径**：`POST /api/articles/{id}/comments`
- **请求体**：

```json
{
  "authorName": "必填，1-30 字",
  "authorEmail": "可选，需符合邮箱格式，仅服务端保存不回传",
  "content": "必填，1-1000 字",
  "visitorId": "必填，8-64 位，前端生成的访客标识"
}
```

- **响应**：HTTP 201，`data` 为 `CommentVO`
- **错误**：`40001`、`40004`

### 10–11. 单条评论查询 / 修改（阶段 8 转正式）

- **方法 / 路径**：`GET /api/comments/{id}`、`PUT /api/comments/{id}`
- `GET`：返回 `CommentVO`；**错误**：`40004`（评论不存在）
- `PUT`：请求体 `{ "content": "1-1000 字", "visitorId": "必填，须与创建时一致" }`
  - 校验不通过返回 `40001`（字段级原因）；`visitorId` 不匹配或评论不存在返回 `40004`
  - **只改 `content`**：`authorName` / `authorEmail` / `visitorId` / `createdAt` 均不变；**不新增 `updatedAt` 字段**（决策 BS）
  - 前端只对本机账本（`blog:myComments`）内的评论显示「编辑」入口

### 12. 删除评论

- **方法 / 路径**：`DELETE /api/comments/{id}`
- **请求体**：`{ "visitorId": "必填，须与创建时一致" }`
- **响应**：`data` 为 `null`
- **错误**：`40004`（评论不存在或 `visitorId` 不匹配）

> 说明：本项目不做登录，评论删除采用"访客标识匹配"作为最轻量的归属校验；这是**演示级**方案，公网部署需替换为真正的鉴权。

### 13–15. 点赞

- `GET /api/articles/{id}/likes?visitorId=xxx`：返回 `LikeStateVO`（前端初始化点赞按钮状态）；`visitorId` **必填**（缺失或不符合 8–64 位格式返回 `40001`，阶段 5 批 1 实测确认）
- `POST /api/articles/{id}/likes`，请求体 `{ "visitorId": "..." }`：**幂等**，已点赞再次调用仍返回 `liked: true` 与当前总数
- `DELETE /api/articles/{id}/likes`，请求体 `{ "visitorId": "..." }`：**幂等**，未点赞时调用返回 `liked: false`
- **唯一约束**：`like_record(article_id, visitor_id)` 唯一，数据库层面防止重复计数
- **错误**：`40001`（visitorId 缺失或格式不符）、`40004`（文章不存在）

### 16. 上一篇 / 下一篇（阶段 8 转正式）

- **方法 / 路径**：`GET /api/articles/{id}/adjacent`
- **响应**：`data: { "prev": {id,title}|null, "next": {id,title}|null }`
- **口径（决策 BT）**：**只把 `PUBLISHED` 纳入相邻链**（草稿不参与、也不会被返回）；排序 `ORDER BY created_at, id`（`id` 兜底同秒）；首篇的 `prev`、尾篇的 `next` 为 `null`
- **错误**：`40004`（文章不存在）
- **同步实装**：`GET /api/articles/{id}` 的 `prev` / `next` 字段返回同样结果（前端直接使用，无需额外请求）

### 17. 标签管理（阶段 8 转正式）

- `POST /api/tags`：`{ "name": "1-20 字" }` → HTTP 201，`data` 为 `TagVO`（新建时 `articleCount` 为 0）
- `PUT /api/tags/{id}`：`{ "name": "1-20 字" }` → HTTP 200，`data` 为更新后的 `TagVO`
- `DELETE /api/tags/{id}`：删除标签并解除所有文章关联（`article_tag` 级联删除）→ `data: null`
- **错误**：`40001`（name 缺失或超长）、`40004`（标签不存在）、**`40009`（标签名重复 —— 创建 / 改名均适用；阶段 8 起启用）**

### 18. 阅读数（阶段 8 新增）

- **方法 / 路径**：`POST /api/articles/{id}/views`
- **请求体**：无
- **行为**：`view_count` 自增 1（**不按访客去重**：每次调用都 +1，演示级语义）
- **响应**：`data: { "viewCount": 43 }`（自增后的值）
- **错误**：`40004`（文章不存在）
- **展示**：`viewCount` 同时进 `ArticleSummary` 与 `ArticleDetail`；前端进入详情页后调用本接口并显示「阅读 N」
- **说明**：`GET /api/articles/{id}` **保持无副作用**（决策 BU），计数只经本接口发生

---

## 五、前端模块与接口的对应关系

| 前端模块 | 使用接口 |
|---|---|
| 一、全局导航与主题 | 无（纯前端） |
| 二、文章列表 | `GET /api/articles`、`GET /api/tags` |
| 三、文章详情 | `GET /api/articles/{id}`（含 `prev` / `next`）、`POST /api/articles/{id}/views`、`GET /api/articles/{id}/adjacent` |
| 四、搜索与过滤 | `GET /api/articles?keyword=&tags=`、`GET /api/tags` |
| 五、评论与点赞 | `GET/POST/DELETE /api/articles/{id}/comments`、`GET/PUT/DELETE /api/comments/{id}`、`GET/POST/DELETE /api/articles/{id}/likes` |
| 六、本地持久化 | 无（localStorage：主题、`visitorId`、已点赞文章 ID 集合） |
| 文章管理（阶段 8 隐藏入口 `/studio`，决策 BR） | `POST/PUT/DELETE /api/articles`、`GET/POST/PUT/DELETE /api/tags` |

**双份数据的分工（重要）**：点赞计数、评论与阅读数的**唯一事实来源是后端**；`localStorage` 只存"本机偏好"（主题）与"本机状态"（`visitorId`、我赞过哪些文章 ID），用于界面初始化，不作为计数依据。

---

## 六、待作者确认的点

| # | 待确认项 | 我的建议 |
|---|---|---|
| 1 | 多标签过滤语义 `tagMode`：`and` / `or` | 默认 `and`（同时包含所选标签），保留 `or` 可选 |
| 2 | 分页 `size` 上限 | 20（避免一次拉太多） |
| 3 | 更新文章用 `PUT` 全量还是 `PATCH` 局部 | `PUT` 全量（语义清晰、前端表单正好全量提交） |
| 4 | 是否需要 `/api/health` | 需要（用于验证"后端已启动"，无需引入 actuator） |
| 5 | 是否允许匿名评论 | 允许（仅要求 `authorName` + `content`） |
| 6 | 点赞去重方式 | 前端生成 UUID 存 localStorage 作为 `visitorId` |
| 7 | 关键词搜索范围 | 先只搜标题（符合项目要求），阶段 8 再评估是否扩到摘要 |
| 8 | 是否保留"标签管理"接口（第 17 条） | 保留为阶段 8 可选项 |
