-- ============================================================
-- 种子数据（幂等）：固定 ID + INSERT OR IGNORE，重复启动不会重复插入
-- 内容为本项目自己撰写的中文文章，不使用任何现成模板站点的文章或图片外链
-- 阶段 1 批 4：首批 3 篇（用于验证建表与幂等）
-- 阶段 4 批 0：补齐为 12 篇，标签扩到 8 个；封面改用 frontend/public 下的本地 SVG
-- ============================================================

INSERT OR IGNORE INTO tag (id, name, created_at) VALUES
    (1, '项目日志', '2026-09-01T09:00:00'),
    (2, 'Vue', '2026-09-01T09:00:00'),
    (3, 'Spring Boot', '2026-09-01T09:00:00'),
    (4, 'SQLite', '2026-09-01T09:00:00'),
    (5, '前端', '2026-09-01T09:00:00'),
    (6, 'CSS', '2026-09-01T09:00:00'),
    (7, '后端', '2026-09-01T09:00:00'),
    (8, 'Markdown', '2026-09-01T09:00:00');

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
     '/images/covers/project-intro.svg', 'PUBLISHED', 0, '2026-09-03T09:00:00', '2026-09-03T09:00:00'),
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
     '/images/covers/stack-review.svg', 'PUBLISHED', 0, '2026-09-05T09:00:00', '2026-09-05T09:00:00'),
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
     '/images/covers/sqlite-notes.svg', 'PUBLISHED', 0, '2026-09-07T09:00:00', '2026-09-07T09:00:00'),
    (4,
     'Vue 3 组合式 API：把同一件事的代码放在一起',
     '组合式 API 按关注点组织代码。记录它在实际项目里的组织方式、什么时候该抽成独立函数，以及 script setup 的代价。',
     '## 从选项式到组合式

选项式按「类型」分块：数据放 data、方法放 methods、监听放 watch。组件一大，同一件事的代码就被拆到四个地方。

组合式按「关注点」分块：主题切换相关的 ref、computed、watch 可以写在一起，读代码时不用在文件里来回跳。

## 一个真实的小例子

```js
const theme = ref("system")

const resolved = computed(() =>
  theme.value === "system" ? (systemDark.value ? "dark" : "light") : theme.value
)

watch(resolved, (value) => {
  document.documentElement.setAttribute("data-theme", value)
})
```

## 什么时候该抽出去

组合式 API 让「抽逻辑」变得便宜，但不必因此把所有东西都抽走。我的判断标准是：同一段逻辑在两个以上组件里出现，或者它自带状态与生命周期，才值得独立成函数。

## 小结

script setup 省掉了一层 return，代价是调试时要记得它是编译期语法糖，不是在运行时存在的对象。',
     '/images/covers/composition-api.svg', 'PUBLISHED', 0, '2026-09-09T09:00:00', '2026-09-09T09:00:00'),
    (5,
     'CSS 变量做主题：亮色、暗色与跟随系统',
     '不引入任何 UI 组件库的前提下，用 CSS 变量实现亮 / 暗 / 跟随系统三态主题，并处理首屏防闪与无障碍。',
     '## 为什么不用 CSS-in-JS

项目的约束是不引入 UI 组件库与 CSS 框架，主题系统只能自己写。CSS 变量是最省事的一条路：改一处变量，整站颜色跟着变。

## 三处协作

1. index.html 里一段同步脚本，在首屏渲染前写好 data-theme，避免深色用户看到白屏闪烁；
2. :root 与 [data-theme="dark"] 两套令牌，语义化命名（--color-bg、--color-text），组件里只引语义名，不出现具体色值；
3. 一个 store 负责三态（亮 / 暗 / 跟随系统）与 localStorage 持久化。

## 跟随系统怎么做

用 matchMedia("(prefers-color-scheme: dark)") 读取系统偏好，并且要监听它的 change 事件——用户在系统设置里改主题时，页面应当立刻跟上。

## 别忘了无障碍

- 用 color-scheme 告诉浏览器当前是亮还是暗，滚动条与表单控件会跟着变；
- 尊重 prefers-reduced-motion，把动效时长令牌归零，而不是在每个组件里写一遍判断；
- 焦点样式统一交给 :focus-visible，鼠标点击时不显示刺眼的轮廓。',
     '/images/covers/css-theme.svg', 'PUBLISHED', 0, '2026-09-11T09:00:00', '2026-09-11T09:00:00'),
    (6,
     'markdown-it 加 DOMPurify：渲染内容的安全底线',
     'Markdown 渲染必须经过 sanitize 才能进页面。记录渲染流水线的顺序、白名单收窄与三个容易翻车的细节。',
     '## 危险在哪

Markdown 转成 HTML 之后要插进页面，如果直接 v-html，等于把任何 HTML 都执行一遍。博客正文目前只有作者能写，但评论一旦支持富文本，这就变成一条真实的攻击路径。

## 流水线

```js
const md = new MarkdownIt({ html: false, linkify: true, breaks: false })
const html = DOMPurify.sanitize(md.render(source))
```

三个关键决策：

- html: false：不允许 Markdown 里直接写原始 HTML，从源头上少一类输入；
- 先 render 再 sanitize：顺序不能反，否则白名单会误杀 markdown-it 生成的标签；
- 外链统一加 rel="noopener"，避免 window.opener 被利用。

## 白名单要收窄

DOMPurify 的默认白名单已经比较克制，但仍建议显式限制允许的标签与属性，尤其是 target、style 这类容易出问题的属性。

## 一句话结论

渲染别人的内容时，永远假设它是恶意的。',
     '/images/covers/markdown-safe.svg', 'PUBLISHED', 0, '2026-09-12T09:00:00', '2026-09-12T09:00:00'),
    (7,
     'JdbcTemplate 手写 SQL 的取舍',
     '只有 5 张表、还带多标签组合过滤的项目，为什么用 JdbcTemplate 而不是 ORM，以及手写 SQL 换来的具体代价。',
     '## 不用 ORM 的理由

项目的表只有 5 张，查询带多标签 AND / OR 组合过滤。用 JdbcTemplate 把 SQL 显式写出来，比让 ORM 生成一段难以预料的语句更好维护。

## 分层怎么写

- 数据访问层只做「执行 SQL 加映射结果」，不写业务判断；
- 服务层负责业务规则与事务边界；
- 路由层只管接参与包装响应。

## 分页查询的一个坑

SQLite 用 LIMIT 加 OFFSET，总数要单独查一次：

```sql
SELECT COUNT(*) FROM article WHERE status = ?;
SELECT id, title FROM article WHERE status = ? ORDER BY created_at DESC LIMIT ? OFFSET ?;
```

两条语句之间数据可能变化，所以返回的 total 只是近似值。演示项目可以接受；要严格一致就得把两条查询放进同一个事务。

## 代价

- 字段改名要同时改 SQL 与结果映射，编译器帮不上忙；
- 动态条件要靠字符串拼接，必须用参数占位符而不是把值拼进去。',
     '/images/covers/jdbctemplate.svg', 'PUBLISHED', 0, '2026-09-14T09:00:00', '2026-09-14T09:00:00'),
    (8,
     'RESTful 接口设计：统一返回体与错误码分段',
     '统一返回体、错误码分段与字段级校验原因：一套让前端只写一次解包逻辑、让表单能直接定位错误字段的接口约定。',
     '## 统一返回体

所有接口都返回同一个外壳：

```json
{ "code": 0, "message": "ok", "data": {} }
```

好处是前端只写一次解包逻辑。代价是 HTTP 状态码与业务码并存，容易混淆，所以约定要写死：先看 HTTP 是否 2xx，再看 code 是否为 0。

## 错误码怎么分段

| 段位 | 含义 |
|---|---|
| 0 | 成功 |
| 40001 到 40009 | 参数与资源类问题 |
| 50000 到 50001 | 服务端异常 |

分段的意义在于：前端可以按段位决定是「提示用户改输入」还是「提示稍后重试」。

## 参数校验要给出字段级原因

```json
{
  "code": 40001,
  "message": "参数校验失败",
  "data": { "fields": { "title": "标题不能为空" } }
}
```

表单可以直接把这些字段名映射到输入框下方的提示，不需要前端再猜哪一项不合格。

## 全局异常处理

在 @RestControllerAdvice 里把业务异常、校验异常、兜底异常分别映射成对应错误码，控制器里就不会出现一片 try / catch。',
     '/images/covers/rest-api.svg', 'PUBLISHED', 0, '2026-09-15T09:00:00', '2026-09-15T09:00:00'),
    (9,
     '分页的细节：page、size 与 totalPages',
     'page 从 1 开始、size 上限 20、totalPages 由后端算好——分页看着简单，边界情况却不少，逐条记下处理方式。',
     '## 参数约定

- page 从 1 开始，默认 1；
- size 默认 10，上限 20；
- 越界或非数字返回 40002，而不是静默纠正成合法值。

上限存在的意义是防止有人用 size=100000 把库拖死。

## 响应结构

```json
{ "items": [], "page": 1, "size": 10, "total": 42, "totalPages": 5 }
```

totalPages 由后端算好，前端就不用到处写取整运算。

## 三个容易忽略的边界

1. page 超过总页数时返回空数组，而不是 404；
2. size 为 0 属于非法参数，因为约定范围是 1 到 20；
3. 删除数据后再翻页，同一篇文章可能出现在两页，这是无状态分页的固有问题，不做特殊处理。

## 前端怎么用

分页组件只负责把目标页码抛出去，请求与状态交给视图；同时把 page 同步到地址栏查询串，刷新后能回到同一页。',
     '/images/covers/pagination.svg', 'PUBLISHED', 0, '2026-09-17T09:00:00', '2026-09-17T09:00:00'),
    (10,
     '外键与级联删除：SQLite 的默认值陷阱',
     'SQLite 的外键约束默认关闭，用连接池时更要小心。记录开启方式、级联删除的验证方法，以及 busy_timeout 的必要性。',
     '## 默认是关的

SQLite 的外键约束默认不生效，必须每个连接执行一次：

```sql
PRAGMA foreign_keys = ON;
```

用连接池时，这一句要放在连接初始化语句里，否则只有第一个连接是开着的。

## 建表时的写法

```sql
FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE
```

删文章时，评论、点赞、标签关联一起被清理，标签本身保留。

## 怎么验证它真的生效了

光看建表语句不算验证。做法是删掉一篇文章，再分别统计三张关联表的行数，全部归零才算通过。

## 另外两个 SQLite 的坑

- 单写者模型：需要 busy_timeout 让写入排队等待，而不是直接抛 SQLITE_BUSY；
- 模糊匹配用不上普通索引，数据量大了要考虑 FTS5。',
     '/images/covers/foreign-key.svg', 'PUBLISHED', 0, '2026-09-19T09:00:00', '2026-09-19T09:00:00'),
    (11,
     '前端路由：懒加载、页面过渡与 404 兜底',
     '路由懒加载、页面过渡、404 兜底与标题同步：四条让单页应用更像多页站点、又保留 SPA 体验的具体做法。',
     '## 懒加载

每个视图都用动态 import 声明，Vite 会把它们切成独立 chunk。首页不必等文章详情页的代码，首屏因此更快。

## 页面过渡

在路由视图外面套一层 Transition，配合 out-in 模式避免两个页面同时存在。动效时长走 CSS 变量，所以 prefers-reduced-motion 下自动失效，不需要额外写判断。

## 404 兜底

用星号通配捕获所有未匹配路径，渲染一个真正的 404 页面，而不是静默重定向到首页。用户输错地址时，他应该知道发生了什么。

## 标题同步

在路由的 afterEach 钩子里根据 meta.title 写 document.title。放在一处，视图组件就不必各自处理。

## 滚动位置

scrollBehavior 里优先返回浏览器保存的位置（前进后退时恢复原处），否则回到顶部。从详情页返回列表页时，这一点尤其重要。',
     '/images/covers/vue-router.svg', 'PUBLISHED', 0, '2026-09-21T09:00:00', '2026-09-21T09:00:00'),
    (12,
     '点赞与评论的幂等设计',
     '点赞按钮最容易被连点两次。用数据库唯一约束加接口幂等两道防线，把重复计数挡在数据之外。',
     '## 幂等是什么意思

同一个请求发一次和发十次，最终状态一样。点赞按钮最容易出问题：网络抖动时用户点两下，计数就多了一个。

## 两道防线

1. 数据库唯一约束：article_id 与 visitor_id 组合唯一，重复插入直接失败；
2. 接口幂等：再次点赞不报错，直接返回当前状态与总数。

```sql
INSERT OR IGNORE INTO like_record (article_id, visitor_id, created_at)
VALUES (?, ?, ?);
```

用 INSERT OR IGNORE 而不是先查后插，可以避开并发下的竞态。

## 访客标识

不做登录的前提下，用前端生成并存在 localStorage 里的 UUID 作为 visitorId。这是演示级方案：清掉本地存储就能重复点赞。

## 归属校验

取消点赞与删除评论都要求带上创建时的 visitorId，不匹配就返回资源不存在。注意响应里不能回传 visitorId，否则等于把别人的钥匙印在门上。',
     '/images/covers/idempotent-like.svg', 'PUBLISHED', 0, '2026-09-23T09:00:00', '2026-09-23T09:00:00');

INSERT OR IGNORE INTO article_tag (article_id, tag_id) VALUES
    (1, 1),
    (2, 2),
    (2, 3),
    (3, 4),
    (3, 1),
    (4, 2),
    (4, 5),
    (5, 6),
    (5, 5),
    (6, 8),
    (6, 5),
    (7, 3),
    (7, 4),
    (8, 3),
    (8, 7),
    (9, 7),
    (9, 3),
    (10, 4),
    (10, 7),
    (11, 2),
    (11, 5),
    (12, 7),
    (12, 1);

-- 已建库（阶段 1 到阶段 3 产生的 blog.db）的封面回填：
-- INSERT OR IGNORE 不会更新已存在的行，故对旧库仅在 cover_url 为空时补值（幂等，可重复执行）
UPDATE article SET cover_url = '/images/covers/project-intro.svg' WHERE id = 1 AND cover_url IS NULL;
UPDATE article SET cover_url = '/images/covers/stack-review.svg' WHERE id = 2 AND cover_url IS NULL;
UPDATE article SET cover_url = '/images/covers/sqlite-notes.svg' WHERE id = 3 AND cover_url IS NULL;

-- 阶段 5 批 0：正文配图回填（幂等）
-- 目的：给第 6 篇文章补一张本地 SVG 正文配图，用于肉眼复验 Markdown 正文图片懒加载
-- 仅在正文尚无本地配图时追加，重复启动不会重复追加
UPDATE article
SET content = content || char(10) || char(10) || '![Markdown 渲染流水线示意](/images/articles/markdown-pipeline.svg)'
WHERE id = 6 AND instr(content, '/images/articles/') = 0;
