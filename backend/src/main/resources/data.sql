-- ============================================================
-- 种子数据（幂等）：固定 ID + INSERT OR IGNORE，重复启动不会重复插入
-- 内容为本项目自己撰写的中文文章，不使用任何现成模板站点的文章或图片外链
-- 首批 3 篇用于验证建表与幂等；全量约 12 篇留到阶段 4/5 补齐
-- ============================================================

INSERT OR IGNORE INTO tag (id, name, created_at) VALUES
    (1, '项目日志', '2026-09-01T09:00:00'),
    (2, 'Vue', '2026-09-01T09:00:00'),
    (3, 'Spring Boot', '2026-09-01T09:00:00'),
    (4, 'SQLite', '2026-09-01T09:00:00');

INSERT OR IGNORE INTO article (id, title, summary, content, cover_url, status, view_count, created_at, updated_at) VALUES
    (1,
     '项目开篇：为什么手写一个博客，而不是套模板',
     '记录这个项目的起点：为什么选择从零手写前端与后端，而不是找一个现成的博客主题。',
     '## 起因

想要一个真正属于自己的博客，而不是把现成主题换个颜色。

## 两条底线

- 前端只用 JavaScript，页面与组件自己写，不引入任何 UI 组件库
- 后端只用 Java，接口按 RESTful 风格自己设计，数据库选轻量的 SQLite

## 技术栈

前端是 Vue 3 加 Vite，样式用原生 CSS 与 CSS 变量实现深浅色切换。后端是 Spring Boot 加 JdbcTemplate，入口类如下。

```java
@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
```

## 接下来

先把骨架搭起来，再一模块一模块地填内容，每一步都留下可验证的记录。',
     NULL, 'PUBLISHED', 0, '2026-09-03T09:00:00', '2026-09-03T09:00:00'),
    (2,
     '技术选型复盘：Vue 3 + Vite 与 Spring Boot + SQLite',
     '为什么前端选 Vue 3、后端选 Spring Boot、数据库选 SQLite，以及这些选择各自换来的代价。',
     '## 前端为什么是 Vue 3

组合式 API 把状态与副作用放在一起，写主题切换、搜索防抖、无限滚动这类交互很省代码。单文件组件把模板、脚本、样式收在一个文件里，正好对应本项目对 HTML 与 CSS 的要求。

放弃 TypeScript 不是因为它不好，而是项目约束明确要求前端使用 JavaScript。

## 后端为什么是 Spring Boot

内置 Tomcat，不需要额外中间件就能起一个 REST 服务，分层结构天然清晰：路由层、服务层、数据访问层各管一件事。

数据访问用 JdbcTemplate 而不是 ORM：SQL 显式写在数据访问层，遇到 SQLite 的兼容性问题时不用和框架搏斗。

## 数据库为什么是 SQLite

单文件、零安装、可以直接复制备份。个人博客读多写少，完全够用。

代价也要说清楚：SQLite 是单写者模型，并发写入会拿锁，所以连接池要限制为 1。

## 一个教训

选型时不写清楚「为什么」，三个月后自己也会忘。所以这份复盘本身就是项目的一部分。',
     NULL, 'PUBLISHED', 0, '2026-09-05T09:00:00', '2026-09-05T09:00:00'),
    (3,
     'SQLite 适合做博客数据库吗',
     'SQLite 是单写者模型，读多写少的个人博客完全够用，但需要处理连接与锁的细节。',
     '## 结论先说

够用，但要理解它的边界。

## 三个关键点

1. 单写者：同一时刻只允许一个写事务，写操作会串行执行
2. 外键默认关闭：必须在每个连接上执行 PRAGMA foreign_keys = ON，否则级联删除不生效
3. 锁等待：并发写入可能抛 SQLITE_BUSY，需要设置 busy_timeout 让写入排队而不是直接失败

## 本项目的做法

- 连接池最大连接数设为 1，从源头避免并发写冲突
- 通过连接初始化语句打开外键约束
- 建表脚本全部使用 CREATE TABLE IF NOT EXISTS，可重复执行

## 什么时候该换

当写入频繁、或者需要多实例同时写同一个库时，就该换 PostgreSQL 这类数据库了。个人博客暂时到不了那一步。',
     NULL, 'PUBLISHED', 0, '2026-09-07T09:00:00', '2026-09-07T09:00:00');

INSERT OR IGNORE INTO article_tag (article_id, tag_id) VALUES
    (1, 1),
    (2, 2),
    (2, 3),
    (3, 4),
    (3, 1);
