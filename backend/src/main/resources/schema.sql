-- ============================================================
-- 建表脚本（幂等）：全部使用 CREATE TABLE IF NOT EXISTS，可重复执行
-- 表结构契约见 docs/data-model.md（v1.0 已确认）
-- 注意：外键约束需要每个连接执行 PRAGMA foreign_keys = ON 才会生效，
--      本项目在 application.yml 的 Hikari connection-init-sql 中设置
-- ============================================================

-- 1. 文章
CREATE TABLE IF NOT EXISTS article (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    title      TEXT    NOT NULL,
    summary    TEXT    NOT NULL DEFAULT '',
    content    TEXT    NOT NULL,
    cover_url  TEXT,
    status     TEXT    NOT NULL DEFAULT 'PUBLISHED'
                       CHECK (status IN ('DRAFT', 'PUBLISHED')),
    view_count INTEGER NOT NULL DEFAULT 0,
    created_at TEXT    NOT NULL,
    updated_at TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_article_status_created ON article (status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_article_title ON article (title);

-- 2. 标签
CREATE TABLE IF NOT EXISTS tag (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT NOT NULL UNIQUE,
    created_at TEXT NOT NULL
);

-- 3. 文章-标签关联（第 5 张表，用于标签多选过滤）
CREATE TABLE IF NOT EXISTS article_tag (
    article_id INTEGER NOT NULL REFERENCES article (id) ON DELETE CASCADE,
    tag_id     INTEGER NOT NULL REFERENCES tag (id) ON DELETE CASCADE,
    PRIMARY KEY (article_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_article_tag_tag ON article_tag (tag_id);

-- 4. 评论
CREATE TABLE IF NOT EXISTS comment (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    article_id   INTEGER NOT NULL REFERENCES article (id) ON DELETE CASCADE,
    author_name  TEXT    NOT NULL,
    author_email TEXT,
    content      TEXT    NOT NULL,
    visitor_id   TEXT    NOT NULL,
    created_at   TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_comment_article_created ON comment (article_id, created_at DESC);

-- 5. 点赞记录（(article_id, visitor_id) 唯一，接口天然幂等）
CREATE TABLE IF NOT EXISTS like_record (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    article_id INTEGER NOT NULL REFERENCES article (id) ON DELETE CASCADE,
    visitor_id TEXT    NOT NULL,
    created_at TEXT    NOT NULL,
    UNIQUE (article_id, visitor_id)
);

CREATE INDEX IF NOT EXISTS idx_like_record_article ON like_record (article_id);
