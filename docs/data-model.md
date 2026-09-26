# 数据模型（v1.0 · 已确认；阶段 8 批 1 附注 · 已确认）

> **状态：表结构未变（v1.0 的五张表继续沿用）。** 阶段 8 只做两处**非结构**改动（按决策 BS / BW / BU）：① `comment` **不新增 `updated_at` 列**；② `journal_mode` 切为 **WAL**（连接级设置，不落建表语句）；③ `view_count` **开始计数**（经 `POST /api/articles/{id}/views` 自增，不加列、不加索引）。
> 最后更新：阶段 1 批 2 起草 → v1.0 确认；**阶段 8 批 1 附注**（WAL / `view_count` 启用 / 评论不改结构），**已随契约 v1.1 一并确认（作者"批 1 通过，请继续"，2026-09-26）**。
> 决策依据：作者确认 **D1 —— 在 article / comment / like_record / tag 四张表之外，增加第 5 张关联表 `article_tag`**，用于支持"按标签多选过滤"。

## 确认记录

| # | 待确认项 | 作者结论 |
|---|---|---|
| 1 | 不做软删除（直接真删） | 同意 |
| 2 | 保留 `view_count` 字段，阶段 2 不实现计数 | 同意（**阶段 8 起开始计数**，见下表附注） |
| 3 | 无 admin / 作者表（不做登录） | 同意 |
| 4 | 评论无审核状态字段 | 同意 |
| 5 | 点赞 / 评论计数先用实时 `COUNT`（方案 A） | 同意（不变） |

**阶段 8 附注（决策 BS / BW / BU，作者"均同意，请继续"，2026-09-26）**

| # | 事项 | 结论 |
|---|---|---|
| 1 | 评论表是否加 `updated_at` | **不加**（决策 BS）—— `schema.sql` 为 `CREATE TABLE IF NOT EXISTS`，加列需迁移或重置数据库；评论"修改"就地生效，接口不回 `updatedAt` |
| 2 | `journal_mode` | **切 WAL**（决策 BW）：由 JDBC URL 追加 `journal_mode=WAL` 落地，**不写进 `schema.sql`**；运行期数据库目录会出现 `blog.db-wal` / `blog.db-shm`，重置口径与 `blog.db` 一致（一并删除） |
| 3 | `view_count` | **开始计数**（决策 BU）：`POST /api/articles/{id}/views` 自增 1，不做去重；无新列、无新索引 |
| 4 | `like_count` / `comment_count` 冗余列（方案 B） | 仍**不采用**，继续实时 `COUNT` |

> 文末「六、待作者确认的点」保留作为决策留档，内容与上表一致。

---

## 一、总体说明

| 项 | 约定 |
|---|---|
| 数据库 | SQLite 单文件：`backend/data/blog.db` |
| 建表方式 | 启动时执行 `schema.sql`（全部 `CREATE TABLE IF NOT EXISTS`，幂等） |
| 种子数据 | 启动时执行 `data.sql`（固定 ID + `INSERT OR IGNORE`，幂等） |
| 时间字段 | `TEXT`，存 ISO-8601 字符串 `2026-09-23T10:20:30`（SQLite 无原生日期类型） |
| 布尔字段 | 不使用（本项目用不到；有/无由记录是否存在表达） |
| 外键 | 必须开启 `PRAGMA foreign_keys = ON`（SQLite 默认关闭！），否则 `ON DELETE CASCADE` 不生效 |
| 命名 | 表名、字段名一律小写下划线 `snake_case` |

### 实体关系（文字版 ER）

```
article  1 ──< article_tag >── 1  tag
article  1 ──< comment
article  1 ──< like_record
```

---

## 二、表结构

### 2.1 `article`（文章）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键 |
| `title` | TEXT | NOT NULL | 标题，1–100 字 |
| `summary` | TEXT | NOT NULL DEFAULT '' | 摘要，0–200 字；留空由后端截取正文前 120 字 |
| `content` | TEXT | NOT NULL | 正文 Markdown 原文，1–50000 字 |
| `cover_url` | TEXT | NULL | 封面图地址（首批种子数据用本地 SVG 路径，不依赖外链图床） |
| `status` | TEXT | NOT NULL DEFAULT 'PUBLISHED'，CHECK(`status IN ('DRAFT','PUBLISHED')`) | 文章状态（后端加分项） |
| `view_count` | INTEGER | NOT NULL DEFAULT 0 | 阅读数（**阶段 8 起计数**：`POST /api/articles/{id}/views` 自增 1，不做去重） |
| `created_at` | TEXT | NOT NULL | 创建时间（ISO-8601） |
| `updated_at` | TEXT | NOT NULL | 更新时间（ISO-8601） |

索引：

- `idx_article_status_created`：`(status, created_at DESC)` —— 列表页"已发布 + 按时间倒序"的主查询路径
- `idx_article_title`：`(title)` —— 关键词搜索目前是 `LIKE '%kw%'`，**普通索引无法命中**，先保留；数据量变大再评估 SQLite FTS5（阶段 8）

### 2.2 `tag`（标签）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键 |
| `name` | TEXT | NOT NULL UNIQUE | 标签名，1–20 字，重复由数据库保证唯一 |
| `created_at` | TEXT | NOT NULL | 创建时间 |

### 2.3 `article_tag`（文章-标签关联，第 5 张表）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| `article_id` | INTEGER | NOT NULL，REFERENCES `article(id)` ON DELETE CASCADE | 文章 |
| `tag_id` | INTEGER | NOT NULL，REFERENCES `tag(id)` ON DELETE CASCADE | 标签 |

约束与索引：

- 复合主键：`PRIMARY KEY (article_id, tag_id)` —— 同一文章不能重复挂同一标签
- `idx_article_tag_tag`：`(tag_id)` —— 按标签反查文章（多选过滤的查询路径）

### 2.4 `comment`（评论）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键 |
| `article_id` | INTEGER | NOT NULL，REFERENCES `article(id)` ON DELETE CASCADE | 所属文章 |
| `author_name` | TEXT | NOT NULL | 昵称，1–30 字 |
| `author_email` | TEXT | NULL | 邮箱，可选；**只存不返回**（接口不回传该字段） |
| `content` | TEXT | NOT NULL | 评论内容，1–1000 字 |
| `visitor_id` | TEXT | NOT NULL | 访客标识（前端生成的 UUID），用于删除时的归属校验 |
| `created_at` | TEXT | NOT NULL | 创建时间 |

索引：

- `idx_comment_article_created`：`(article_id, created_at DESC)` —— 某文章评论列表

> 删除策略：本项目**不做软删除**（不加 `deleted_at`），删除即真删；这是演示项目的取舍，已在审计中记录。

### 2.5 `like_record`（点赞记录）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键 |
| `article_id` | INTEGER | NOT NULL，REFERENCES `article(id)` ON DELETE CASCADE | 所属文章 |
| `visitor_id` | TEXT | NOT NULL | 访客标识（前端 UUID） |
| `created_at` | TEXT | NOT NULL | 点赞时间 |

约束与索引：

- **UNIQUE (`article_id`, `visitor_id`)** —— 数据库层保证同一访客对同一文章只有一条记录，接口因此天然幂等
- `idx_like_record_article`：`(article_id)` —— 统计点赞数

---

## 三、统计字段策略（`likeCount` / `commentCount` / `viewCount`）

两种做法：

| 方案 | 实现 | 优点 | 缺点 |
|---|---|---|---|
| A（**继续采用**） | 查询时用子查询实时 `COUNT` | 无一致性问题、代码少 | 列表页每条都算一次，数据量大时慢 |
| B（**阶段 8 评估后仍不采用**） | `article` 加冗余列 `like_count` / `comment_count`，服务层维护 | 读取快 | 需要保证不漂移（并发/异常时易出错） |

结论：**`likeCount` / `commentCount` 继续用方案 A**（阶段 8 批 1 复核，仍不采用方案 B）。

`viewCount` 是 `article` 自带的列：**阶段 8 起**由 `POST /api/articles/{id}/views` 自增（`UPDATE article SET view_count = view_count + 1`），读路径直接返回该列；不按访客去重、不做额外并发优化（演示级；SQLite 单写者模型 + `busy_timeout=5000` 已足够）。

---

## 四、种子数据策略（`data.sql`）

- 使用**固定 ID** 显式插入，配合 `INSERT OR IGNORE`，保证重复启动不会重复插入；
- 首批只写 2–3 篇文章（作者确认）用于验证幂等与联通，全量约 12 篇留到阶段 4/5 补齐；
- 标签同样固定 ID；文章与标签的关联写 `INSERT OR IGNORE INTO article_tag (article_id, tag_id) VALUES (1, 1);`；
- 摘要与正文都是本项目自己撰写的中文内容，**不使用任何模板站点的文章或图片外链**；封面先用本地 SVG（`frontend/public/images/covers/*.svg`，批 3/批 4 补充）。

示例（幂等写法）：

```sql
INSERT OR IGNORE INTO article (id, title, summary, content, cover_url, status, created_at, updated_at)
VALUES (1, '项目开篇：为什么要手写一个博客', '……', '……', '/images/covers/intro.svg', 'PUBLISHED',
        '2026-09-01T09:00:00', '2026-09-01T09:00:00');
```

---

## 五、初始化与重置

1. **初始化**：后端启动时 Spring Boot 自动执行 `schema.sql` → `data.sql`（由 `application.yml` 的 `spring.sql.init.mode=always` 控制）；
2. **journal_mode**：**阶段 8 起为 WAL**（决策 BW）—— 由 JDBC URL 的 `journal_mode=WAL` 参数在连接建立时设置，**不写进 `schema.sql`**；该模式持久化在数据库文件中，重置后由连接参数再次生效。运行期数据库目录会出现 `blog.db-wal` / `blog.db-shm` 两个伴随文件，属正常现象；
3. **重置**：停止后端 → 删除 `backend/data/blog.db`、`blog.db-wal`、`blog.db-shm` → 重新启动（自动重建并写入种子数据）；
4. **注意**：重置会丢失通过接口创建的全部数据，属于不可恢复操作。

---

## 六、待作者确认的点

| # | 待确认项 | 我的建议 |
|---|---|---|
| 1 | 是否需要"软删除"（`deleted_at`） | 不需要，演示项目直接真删，如需再加 |
| 2 | `view_count`（阅读数）字段是否保留 | 保留字段但阶段 2 不实现计数，阶段 8 视时间决定 |
| 3 | `admin`/作者表是否需要 | 不需要（不做登录） |
| 4 | 评论是否需要审核状态字段 | 不需要（再做会显著增加复杂度） |
| 5 | 统计计数用方案 A 还是 B | 先用 A，阶段 8 再评估 B |
