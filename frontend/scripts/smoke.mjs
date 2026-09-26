#!/usr/bin/env node
/**
 * 接口层回归脚本（阶段 6 批 1 · 决策 AW）
 *
 * 用途：把 `docs/api-contract.md`（v1.1）里"已实现"的操作固化成可重复运行的用例（阶段 8 批 2 起含评论单条查询 / 修改），
 * 覆盖正常路径 + 错误分支（40001 / 40002 / 40004），跑完自动把测试数据清理干净。
 *
 * 用法（零新增依赖，只用 Node 原生 fetch）：
 *   npm run smoke                      # 默认直连 http://localhost:8080
 *   node scripts/smoke.mjs http://127.0.0.1:5173   # 也可走 Vite 代理
 *
 * 前置条件：后端已启动（`cd backend && ./mvnw spring-boot:run`），数据库为种子状态。
 * 退出码：0 = 全部通过；1 = 有用例失败（失败清单见输出末尾）。
 */

const BASE = `${(process.argv[2] || process.env.API_BASE || 'http://localhost:8080').replace(/\/+$/, '')}/api`;

const VISITOR = 'smoke-visitor-000000000001'; // 8-64 位字母/数字/下划线/连字符
const OTHER_VISITOR = 'smoke-visitor-000000000002';
const TIME_RE = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/;

const ARTICLE_KEYS = ['id', 'title', 'summary', 'coverUrl', 'status', 'tags',
    'likeCount', 'commentCount', 'createdAt', 'updatedAt'];
const COMMENT_KEYS = ['id', 'articleId', 'authorName', 'content', 'createdAt'];
const PAGE_KEYS = ['items', 'page', 'size', 'total', 'totalPages'];

let pass = 0;
const failures = [];

function group(title) {
    console.log(`\n${title}`);
}

function check(label, cond, detail = '') {
    if (cond) {
        pass += 1;
        console.log(`  \u2713 ${label}`);
    } else {
        failures.push(detail ? `${label} —— ${detail}` : label);
        console.log(`  \u2717 ${label}${detail ? ` —— ${detail}` : ''}`);
    }
}

async function api(method, path, { body, raw } = {}) {
    const init = { method, headers: {} };
    if (body !== undefined) {
        init.headers['Content-Type'] = 'application/json; charset=utf-8';
        init.body = raw ? body : JSON.stringify(body);
    }
    let res;
    try {
        res = await fetch(BASE + path, init);
    } catch (err) {
        throw new Error(`请求发送失败 ${method} ${path}：${err.message}（后端是否已在 ${BASE} 运行？）`);
    }
    const text = await res.text();
    let json = null;
    try { json = JSON.parse(text); } catch { /* 非 JSON 响应，保留 text 供排查 */ }
    return { status: res.status, json, text };
}

const okBody = (r) => r.json !== null && r.json.code === 0 && r.json.message === 'ok';
const hasAll = (obj, keys) => obj !== null && typeof obj === 'object'
    && keys.every((k) => Object.prototype.hasOwnProperty.call(obj, k));
const short = (s) => (s.length > 90 ? `${s.slice(0, 90)}…` : s);

/** 错误响应断言：HTTP 状态 + 业务 code 双检（契约 §一「HTTP 状态码与业务 code 同时返回」）。 */
function checkError(label, r, httpStatus, code) {
    check(`${label} → HTTP ${httpStatus} / code ${code}`, r.status === httpStatus && r.json?.code === code,
        `实际 HTTP ${r.status} / code ${r.json?.code} / body ${short(r.text)}`);
}

const created = { articleId: null, commentId: null, likeArticleId: 1 };

async function main() {
    console.log(`接口回归：${BASE}\n访客标识：${VISITOR}`);

    // ── §四·1 健康检查 ──────────────────────────────────────────────
    group('§四·1 健康检查 GET /api/health');
    {
        const r = await api('GET', '/health');
        check('HTTP 200 且 code=0', r.status === 200 && okBody(r));
        check('data.status = UP', r.json?.data?.status === 'UP');
        check('data.time 为 yyyy-MM-ddTHH:mm:ss（契约 §一时间格式）', TIME_RE.test(r.json?.data?.time ?? ''),
            `实际 ${r.json?.data?.time}`);
    }

    // ── §四·2 文章列表 ──────────────────────────────────────────────
    group('§四·2 文章列表 GET /api/articles');
    {
        const r = await api('GET', '/articles');
        check('HTTP 200 且 code=0', r.status === 200 && okBody(r));
        const p = r.json?.data;
        check('分页对象含 items/page/size/total/totalPages（契约 §一）', hasAll(p, PAGE_KEYS));
        check('默认 page=1 / size=10', p?.page === 1 && p?.size === 10);
        check('种子数据 total=12、totalPages=2', p?.total === 12 && p?.totalPages === 2,
            `实际 total=${p?.total} totalPages=${p?.totalPages}`);
        check('默认只返回已发布（items 全为 PUBLISHED）', p?.items?.every((it) => it.status === 'PUBLISHED'));
        check('列表项字段与 ArticleSummary 一致（契约 §2.2）', p?.items?.[0] && hasAll(p.items[0], ARTICLE_KEYS));
        check('likeCount / commentCount 为数字', typeof p?.items?.[0]?.likeCount === 'number'
            && typeof p?.items?.[0]?.commentCount === 'number');
        check('tags 为字符串数组', Array.isArray(p?.items?.[0]?.tags)
            && p.items[0].tags.every((t) => typeof t === 'string'));
        check('默认按 createdAt 倒序', (p?.items ?? []).every((it, i, arr) => i === 0 || arr[i - 1].createdAt >= it.createdAt));
        check('createdAt 为契约时间格式', TIME_RE.test(p?.items?.[0]?.createdAt ?? ''), `实际 ${p?.items?.[0]?.createdAt}`);

        const p20 = await api('GET', '/articles?size=20');
        check('size=20 时一次取回全部 12 条', p20.json?.data?.items?.length === 12 && p20.json?.data?.totalPages === 1);

        const p2 = await api('GET', '/articles?page=2&size=5');
        check('page=2&size=5 → 第 2 页 5 条（total 12）', p2.json?.data?.items?.length === 5
            && p2.json?.data?.page === 2 && p2.json?.data?.total === 12);

        const kw = await api('GET', '/articles?keyword=SQLite');
        check('keyword=SQLite → 3 篇（标题 LIKE 匹配）', kw.json?.data?.total === 3, `实际 ${kw.json?.data?.total}`);

        const kwNone = await api('GET', '/articles?keyword=%E4%B8%8D%E5%AD%98%E5%9C%A8%E7%9A%84%E5%85%B3%E9%94%AE%E8%AF%8D');
        check('关键词无命中 → items 空数组且 total=0（不省略字段）', Array.isArray(kwNone.json?.data?.items)
            && kwNone.json.data.items.length === 0 && kwNone.json.data.total === 0);

        const and = await api('GET', '/articles?tags=Vue,%E5%89%8D%E7%AB%AF');
        check('tags=Vue,前端（默认 tagMode=and）→ 2 篇', and.json?.data?.total === 2, `实际 ${and.json?.data?.total}`);

        const or = await api('GET', '/articles?tags=Vue,%E5%89%8D%E7%AB%AF&tagMode=or');
        check('tagMode=or → 5 篇', or.json?.data?.total === 5, `实际 ${or.json?.data?.total}`);

        const all = await api('GET', '/articles?status=ALL&size=20');
        check('status=ALL → 12 篇（等同默认，本次无草稿）', all.json?.data?.total === 12);

        const draft = await api('GET', '/articles?status=DRAFT');
        check('status=DRAFT → 0 篇且 items=[]', draft.json?.data?.total === 0 && draft.json?.data?.items?.length === 0);

        checkError('page=0 越界', await api('GET', '/articles?page=0'), 400, 40002);
        checkError('size=999 越界（上限 20）', await api('GET', '/articles?size=999'), 400, 40002);
        checkError('size=0 越界', await api('GET', '/articles?size=0'), 400, 40002);
        checkError('page=abc 非数字', await api('GET', '/articles?page=abc'), 400, 40002);
        checkError('tagMode=xxx 非法取值', await api('GET', '/articles?tagMode=xxx'), 400, 40002);
        checkError('status=xxx 非法取值', await api('GET', '/articles?status=xxx'), 400, 40002);
    }

    // ── §四·3 文章详情 ──────────────────────────────────────────────
    group('§四·3 文章详情 GET /api/articles/{id}');
    {
        const r = await api('GET', '/articles/1');
        check('HTTP 200 且 code=0', r.status === 200 && okBody(r));
        check('含 ArticleDetail 的 content / prev / next（契约 §2.3）', hasAll(r.json?.data, [...ARTICLE_KEYS, 'content', 'prev', 'next']));
        check('content 非空', typeof r.json?.data?.content === 'string' && r.json.data.content.length > 0);
        check('prev / next 已实装（文章 1 为最早一篇：prev=null、next 非空）',
            r.json?.data?.prev === null && r.json?.data?.next?.id != null,
            `实际 prev=${JSON.stringify(r.json?.data?.prev)} next=${JSON.stringify(r.json?.data?.next)}`);
        checkError('不存在的 id=99999', await api('GET', '/articles/99999'), 404, 40004);
    }

    // ── §四·7 标签列表 ──────────────────────────────────────────────
    group('§四·7 标签列表 GET /api/tags');
    {
        const r = await api('GET', '/tags');
        check('HTTP 200 且 code=0', r.status === 200 && okBody(r));
        const tags = r.json?.data;
        check('返回 8 个标签（TagVO：id/name/articleCount）', Array.isArray(tags) && tags.length === 8
            && tags.every((t) => hasAll(t, ['id', 'name', 'articleCount'])), `实际 ${tags?.length}`);
        check('按 articleCount 倒序', Array.isArray(tags)
            && tags.every((t, i) => i === 0 || tags[i - 1].articleCount >= t.articleCount));
        const sum = (tags ?? []).reduce((acc, t) => acc + t.articleCount, 0);
        check('articleCount 合计 = 23（article_tag 关联数）', sum === 23, `实际 ${sum}`);
    }

    // ── §四·8 / 9 / 12 评论 ────────────────────────────────────────
    group('§四·8/9/12 评论：列表 / 发表 / 删除');
    {
        const empty = await api('GET', '/articles/1/comments');
        check('列表 HTTP 200 且分页对象完整', empty.status === 200 && okBody(empty) && hasAll(empty.json?.data, PAGE_KEYS));
        check('测试前该文章评论数为 0（种子状态）', empty.json?.data?.total === 0);

        checkError('评论列表：文章不存在 id=99999', await api('GET', '/articles/99999/comments'), 404, 40004);

        const post = await api('POST', '/articles/1/comments', {
            body: { authorName: '冒烟测试', authorEmail: 'smoke@example.com', content: '接口回归脚本写入的临时评论。', visitorId: VISITOR },
        });
        check('发表成功返回 HTTP 201 且 code=0', post.status === 201 && okBody(post));
        const c = post.json?.data;
        created.commentId = c?.id ?? null;
        check('返回 CommentVO 字段（契约 §2.4）', hasAll(c, COMMENT_KEYS));
        check('authorEmail 不回传（隐私约定）', c !== null && !Object.prototype.hasOwnProperty.call(c, 'authorEmail'));
        check('visitorId 不回传（阶段 5 批 1 补注）', c !== null && !Object.prototype.hasOwnProperty.call(c, 'visitorId'));

        const after = await api('GET', '/articles/1/comments');
        check('发表后列表 total=1 且首条为刚写入的内容', after.json?.data?.total === 1
            && after.json?.data?.items?.[0]?.id === created.commentId);

        checkError('空昵称', await api('POST', '/articles/1/comments', {
            body: { authorName: '  ', content: 'x', visitorId: VISITOR },
        }), 400, 40001);
        const badEmail = await api('POST', '/articles/1/comments', {
            body: { authorName: '甲', authorEmail: 'not-an-email', content: 'x', visitorId: VISITOR },
        });
        checkError('非法邮箱', badEmail, 400, 40001);
        check('40001 带字段级原因 data.fields.authorEmail', typeof badEmail.json?.data?.fields?.authorEmail === 'string');
        const longContent = await api('POST', '/articles/1/comments', {
            body: { authorName: '甲', content: 'a'.repeat(1001), visitorId: VISITOR },
        });
        checkError('评论超长（1001 字）', longContent, 400, 40001);
        check('40001 带字段级原因 data.fields.content', typeof longContent.json?.data?.fields?.content === 'string');
        checkError('visitorId 过短（7 位）', await api('POST', '/articles/1/comments', {
            body: { authorName: '甲', content: 'x', visitorId: 'abc1234' },
        }), 400, 40001);
        checkError('文章不存在时发表评论', await api('POST', '/articles/99999/comments', {
            body: { authorName: '甲', content: 'x', visitorId: VISITOR },
        }), 404, 40004);

        checkError('删除评论：visitorId 不匹配（契约 §四·12）', await api('DELETE', `/comments/${created.commentId}`, {
            body: { visitorId: OTHER_VISITOR },
        }), 404, 40004);

        const del = await api('DELETE', `/comments/${created.commentId}`, { body: { visitorId: VISITOR } });
        check('删除成功 HTTP 200、code=0、data 为 null', del.status === 200 && okBody(del) && del.json.data === null);
        if (okBody(del)) created.commentId = null;
        checkError('重复删除（已不存在）', await api('DELETE', `/comments/${created.commentId ?? 1}`, {
            body: { visitorId: VISITOR },
        }), 404, 40004);

        const restored = await api('GET', '/articles/1/comments');
        check('删除后评论数回到 0', restored.json?.data?.total === 0);
    }

    // ── §四·10 / 11 单条评论查询 / 修改（阶段 8 批 2）─────────────
    group('§四·10/11 评论：单条查询 / 修改');
    {
        const post = await api('POST', '/articles/1/comments', {
            body: { authorName: '冒烟测试', content: '单条查询与修改的临时评论。', visitorId: VISITOR },
        });
        const cid = post.json?.data?.id ?? null;
        created.commentId = cid;

        const g = await api('GET', `/comments/${cid}`);
        check('单条查询 HTTP 200 且字段与 CommentVO 一致', g.status === 200 && okBody(g) && hasAll(g.json?.data, COMMENT_KEYS));
        check('单条查询不回传 authorEmail / visitorId', g.json?.data !== null
            && !Object.prototype.hasOwnProperty.call(g.json.data, 'authorEmail')
            && !Object.prototype.hasOwnProperty.call(g.json.data, 'visitorId'));

        checkError('单条查询：不存在 id=99999', await api('GET', '/comments/99999'), 404, 40004);

        checkError('修改：visitorId 不匹配', await api('PUT', `/comments/${cid}`, {
            body: { content: 'attempt', visitorId: OTHER_VISITOR },
        }), 404, 40004);

        const emptyContent = await api('PUT', `/comments/${cid}`, { body: { content: '', visitorId: VISITOR } });
        checkError('修改：内容为空', emptyContent, 400, 40001);
        check('40001 带字段级原因 data.fields.content', typeof emptyContent.json?.data?.fields?.content === 'string');

        checkError('修改：内容超长（1001 字）', await api('PUT', `/comments/${cid}`, {
            body: { content: 'a'.repeat(1001), visitorId: VISITOR },
        }), 400, 40001);

        checkError('修改：评论不存在 id=99999', await api('PUT', '/comments/99999', {
            body: { content: 'x', visitorId: VISITOR },
        }), 404, 40004);

        const upd = await api('PUT', `/comments/${cid}`, { body: { content: '修改后的内容（smoke）。', visitorId: VISITOR } });
        check('修改成功 HTTP 200 且 code=0', upd.status === 200 && okBody(upd));
        check('content 已更新、昵称与创建时间不变', upd.json?.data?.content === '修改后的内容（smoke）。'
            && upd.json?.data?.authorName === g.json?.data?.authorName
            && upd.json?.data?.createdAt === g.json?.data?.createdAt,
            `实际 ${JSON.stringify(upd.json?.data)}`);
        check('修改不影响 id / articleId', upd.json?.data?.id === cid && upd.json?.data?.articleId === 1);

        const again = await api('GET', `/comments/${cid}`);
        check('再次查询读到新内容', again.json?.data?.content === '修改后的内容（smoke）。');

        const del = await api('DELETE', `/comments/${cid}`, { body: { visitorId: VISITOR } });
        check('清理该评论成功', del.status === 200 && okBody(del) && del.json.data === null);
        if (okBody(del)) created.commentId = null;

        const afterDel = await api('GET', '/articles/1/comments');
        check('评论数回到 0', afterDel.json?.data?.total === 0);
        checkError('单条查询：已删除', await api('GET', `/comments/${cid}`), 404, 40004);
    }

    // ── §四·16 相邻文章（阶段 8 批 3）─────────────────────────────
    group('§四·16 相邻文章：GET /api/articles/{id}/adjacent');
    {
        const list = await api('GET', '/articles?size=20');
        const items = list.json?.data?.items ?? [];
        const newest = items[0];
        const oldest = items[items.length - 1];

        const oldAdj = await api('GET', `/articles/${oldest.id}/adjacent`);
        check('最早一篇：prev=null、next 非空', oldAdj.status === 200 && okBody(oldAdj)
            && oldAdj.json?.data?.prev === null && oldAdj.json?.data?.next?.id != null,
            `实际 ${JSON.stringify(oldAdj.json?.data)}`);
        check('相邻项为 {id,title} 两字段（契约 §四·16）',
            hasAll(oldAdj.json?.data?.next, ['id', 'title']) && Object.keys(oldAdj.json.data.next).length === 2);

        const newAdj = await api('GET', `/articles/${newest.id}/adjacent`);
        check('最新一篇：next=null、prev 非空', okBody(newAdj)
            && newAdj.json?.data?.next === null && newAdj.json?.data?.prev?.id != null,
            `实际 ${JSON.stringify(newAdj.json?.data)}`);

        const detail = await api('GET', `/articles/${newest.id}`);
        check('详情接口带出与 /adjacent 一致的 prev / next（决策 BT）',
            detail.json?.data?.prev?.id === newAdj.json?.data?.prev?.id && detail.json?.data?.next === null);

        checkError('文章不存在 id=99999', await api('GET', '/articles/99999/adjacent'), 404, 40004);

        const draft = await api('POST', '/articles', {
            body: { title: '【冒烟测试】相邻链草稿', content: 'draft', status: 'DRAFT' },
        });
        created.articleId = draft.json?.data?.id ?? null;
        const withDraft = await api('GET', `/articles/${newest.id}/adjacent`);
        check('草稿不进入相邻链（最新一篇的 next 仍为 null）', withDraft.json?.data?.next === null);
        const delDraft = await api('DELETE', `/articles/${draft.json?.data?.id}`);
        check('清理草稿文章', okBody(delDraft));
        if (okBody(delDraft)) created.articleId = null;

        const tmp = await api('POST', '/articles', {
            body: { title: '【冒烟测试】相邻链已发布', content: 'tmp', status: 'PUBLISHED' },
        });
        created.articleId = tmp.json?.data?.id ?? null;
        const adjAfterTmp = await api('GET', `/articles/${newest.id}/adjacent`);
        check('插入新发布文章后：原最新一篇的 next 指向它', adjAfterTmp.json?.data?.next?.id === tmp.json?.data?.id,
            `实际 ${JSON.stringify(adjAfterTmp.json?.data?.next)}`);
        const tmpAdj = await api('GET', `/articles/${tmp.json?.data?.id}/adjacent`);
        check('新文章：prev 指向原最新一篇、next=null',
            tmpAdj.json?.data?.prev?.id === newest.id && tmpAdj.json?.data?.next === null,
            `实际 ${JSON.stringify(tmpAdj.json?.data)}`);

        const delTmp = await api('DELETE', `/articles/${tmp.json?.data?.id}`);
        check('清理临时发布文章', okBody(delTmp));
        if (okBody(delTmp)) created.articleId = null;
        const backToNormal = await api('GET', `/articles/${newest.id}/adjacent`);
        check('清理后最新一篇的 next 回到 null', backToNormal.json?.data?.next === null);
    }

    // ── §四·13–15 点赞 ────────────────────────────────────────────
    group('§四·13–15 点赞：状态 / 点赞 / 取消（两端幂等）');
    {
        const init = await api('GET', `/articles/1/likes?visitorId=${VISITOR}`);
        check('查询状态 HTTP 200，liked=false / likeCount=0', init.status === 200 && okBody(init)
            && init.json?.data?.liked === false && init.json?.data?.likeCount === 0);
        check('LikeStateVO 字段（articleId/liked/likeCount，契约 §2.5）',
            hasAll(init.json?.data, ['articleId', 'liked', 'likeCount']));

        checkError('visitorId 缺失（阶段 5 批 1 补注：必填）', await api('GET', '/articles/1/likes'), 400, 40001);
        checkError('visitorId 格式不符（7 位）', await api('GET', '/articles/1/likes?visitorId=abc1234'), 400, 40001);
        checkError('文章不存在 id=99999', await api('GET', `/articles/99999/likes?visitorId=${VISITOR}`), 404, 40004);

        const like1 = await api('POST', '/articles/1/likes', { body: { visitorId: VISITOR } });
        check('点赞成功 liked=true / likeCount=1', okBody(like1) && like1.json?.data?.liked === true
            && like1.json?.data?.likeCount === 1, `实际 count=${like1.json?.data?.likeCount}`);

        const like2 = await api('POST', '/articles/1/likes', { body: { visitorId: VISITOR } });
        check('重复点赞幂等：仍 liked=true / likeCount=1', okBody(like2) && like2.json?.data?.liked === true
            && like2.json?.data?.likeCount === 1, `实际 count=${like2.json?.data?.likeCount}`);

        const other = await api('GET', `/articles/1/likes?visitorId=${OTHER_VISITOR}`);
        check('另一访客看到 liked=false 但总数仍为 1（计数以后端为准）',
            other.json?.data?.liked === false && other.json?.data?.likeCount === 1);

        const unlike1 = await api('DELETE', '/articles/1/likes', { body: { visitorId: VISITOR } });
        check('取消点赞 liked=false / likeCount=0', okBody(unlike1) && unlike1.json?.data?.liked === false
            && unlike1.json?.data?.likeCount === 0, `实际 count=${unlike1.json?.data?.likeCount}`);

        const unlike2 = await api('DELETE', '/articles/1/likes', { body: { visitorId: VISITOR } });
        check('重复取消幂等：仍 liked=false / likeCount=0', okBody(unlike2) && unlike2.json?.data?.liked === false
            && unlike2.json?.data?.likeCount === 0);

        checkError('点赞：文章不存在', await api('POST', '/articles/99999/likes', { body: { visitorId: VISITOR } }), 404, 40004);
    }

    // ── §四·4 / 5 / 6 文章写路径 ────────────────────────────────────
    group('§四·4/5/6 文章：创建 / 更新 / 删除');
    {
        const longContent = `这是一段用于验证摘要自动截取的正文。${'内容'.repeat(80)}`;
        const create = await api('POST', '/articles', {
            body: { title: '【冒烟测试】临时文章', content: longContent, tags: ['Vue', 'Vue', '前端'] },
        });
        check('创建返回 HTTP 201 且 code=0', create.status === 201 && okBody(create));
        const a = create.json?.data;
        created.articleId = a?.id ?? null;
        check('返回 ArticleSummary 字段完整', hasAll(a, ARTICLE_KEYS));
        check('status 省略 → 默认 PUBLISHED', a?.status === 'PUBLISHED');
        check('tags 去重（Vue,Vue,前端 → 2 个）', Array.isArray(a?.tags) && a.tags.length === 2, `实际 ${JSON.stringify(a?.tags)}`);
        check('summary 留空 → 后端截取正文前 120 字',
            typeof a?.summary === 'string' && [...a.summary].length === 120 && longContent.startsWith(a.summary),
            `实际长度 ${a?.summary ? [...a.summary].length : 'n/a'}`);

        const detail = await api('GET', `/articles/${created.articleId}`);
        check('详情 content 与写入一致，且 status=PUBLISHED',
            detail.json?.data?.content === longContent && detail.json?.data?.status === 'PUBLISHED');

        const upd1 = await api('PUT', `/articles/${created.articleId}`, {
            body: { title: '【冒烟测试】已更新', content: '更新后的正文。', status: 'DRAFT', tags: [] },
        });
        check('PUT 全量更新成功（HTTP 200）', upd1.status === 200 && okBody(upd1));
        check('tags=[] → 清空标签，status=DRAFT 生效',
            upd1.json?.data?.tags?.length === 0 && upd1.json?.data?.status === 'DRAFT');

        const draftList = await api('GET', '/articles?status=DRAFT&size=20');
        check('status=DRAFT 能查到刚转草稿的文章', draftList.json?.data?.items?.some((it) => it.id === created.articleId));
        const pubList = await api('GET', '/articles?size=20');
        check('默认列表（PUBLISHED）不再包含草稿文章', !pubList.json?.data?.items?.some((it) => it.id === created.articleId));

        const upd2 = await api('PUT', `/articles/${created.articleId}`, {
            body: { title: '【冒烟测试】省略 tags', content: '正文。', status: 'PUBLISHED', tags: ['前端'] },
        });
        check('再次 PUT 挂上 1 个标签', upd2.json?.data?.tags?.length === 1);

        const upd3 = await api('PUT', `/articles/${created.articleId}`, {
            body: { title: '【冒烟测试】省略 tags', content: '正文。', status: 'PUBLISHED' },
        });
        check('PUT 省略 tags → 同样表示清空（契约 §四·5）', upd3.json?.data?.tags?.length === 0);

        const badTitle = await api('POST', '/articles', { body: { title: 'x'.repeat(101), content: '内容' } });
        checkError('标题超长（101 字）', badTitle, 400, 40001);
        check('40001 带字段级原因 data.fields.title', typeof badTitle.json?.data?.fields?.title === 'string');
        checkError('内容为空', await api('POST', '/articles', { body: { title: '标题', content: '' } }), 400, 40001);
        checkError('标签超过 5 个', await api('POST', '/articles', {
            body: { title: '标题', content: '内容', tags: ['a', 'b', 'c', 'd', 'e', 'f'] },
        }), 400, 40001);
        checkError('status 非法取值', await api('POST', '/articles', {
            body: { title: '标题', content: '内容', status: 'ARCHIVED' },
        }), 400, 40001);
        checkError('标签名超长（21 字，service 层语义校验）', await api('POST', '/articles', {
            body: { title: '标题', content: '内容', tags: ['x'.repeat(21)] },
        }), 400, 40001);
        checkError('更新不存在的文章', await api('PUT', '/articles/99999', {
            body: { title: '标题', content: '内容', status: 'PUBLISHED' },
        }), 404, 40004);

        const del = await api('DELETE', `/articles/${created.articleId}`);
        check('删除成功 HTTP 200、code=0、data 为 null', del.status === 200 && okBody(del) && del.json.data === null);
        if (okBody(del)) created.articleId = null;
        checkError('删除后详情不存在', await api('GET', `/articles/${created.articleId ?? 99999}`), 404, 40004);
        checkError('重复删除', await api('DELETE', '/articles/99999'), 404, 40004);
    }

    // ── 通用约定（契约 §一）─────────────────────────────────────────
    group('通用约定：未知路径 / 方法不支持 / 非法 JSON / 统一返回体');
    {
        checkError('未知路径 /api/nope', await api('GET', '/nope'), 404, 40004);
        checkError('方法不支持（PATCH /api/articles/1）', await api('PATCH', '/articles/1'), 400, 40002);
        checkError('非法 JSON 请求体', await api('POST', '/articles', { body: '{ 不是 JSON', raw: true }), 400, 40002);
        const health = await api('GET', '/health');
        check('成功响应固定为 {code, message, data} 三字段', hasAll(health.json, ['code', 'message', 'data'])
            && Object.keys(health.json).length === 3);
    }

    // ── 收尾：数据还原复核 ──────────────────────────────────────────
    group('收尾复核：测试数据已全部清理（数据库回到种子状态）');
    {
        const all = await api('GET', '/articles?status=ALL&size=20');
        check('文章总数回到 12', all.json?.data?.total === 12, `实际 ${all.json?.data?.total}`);
        const draft = await api('GET', '/articles?status=DRAFT');
        check('草稿数为 0', draft.json?.data?.total === 0);
        const comments = await api('GET', '/articles/1/comments');
        check('评论数回到 0', comments.json?.data?.total === 0);
        const likes = await api('GET', `/articles/1/likes?visitorId=${VISITOR}`);
        check('点赞数为 0 且本访客未点赞', likes.json?.data?.likeCount === 0 && likes.json?.data?.liked === false);
        const tags = await api('GET', '/tags');
        check('标签仍为 8 个、关联合计 23', tags.json?.data?.length === 8
            && tags.json.data.reduce((acc, t) => acc + t.articleCount, 0) === 23);
    }
}

/** 无论用例是否中途失败，都尝试清理本脚本创建的临时数据。 */
async function cleanup() {
    try {
        if (created.commentId !== null) {
            await api('DELETE', `/comments/${created.commentId}`, { body: { visitorId: VISITOR } });
            console.log(`\n[清理] 已删除遗留评论 id=${created.commentId}`);
        }
        if (created.articleId !== null) {
            await api('DELETE', `/articles/${created.articleId}`);
            console.log(`[清理] 已删除遗留文章 id=${created.articleId}`);
        }
        const state = await api('GET', `/articles/${created.likeArticleId}/likes?visitorId=${VISITOR}`);
        if (state.json?.data?.liked === true) {
            await api('DELETE', `/articles/${created.likeArticleId}/likes`, { body: { visitorId: VISITOR } });
            console.log('[清理] 已取消遗留点赞');
        }
    } catch (err) {
        console.error(`[清理] 失败：${err.message}`);
    }
}

try {
    await main();
} catch (err) {
    failures.push(`脚本中断：${err.message}`);
    console.error(`\n✗ 脚本中断：${err.message}`);
} finally {
    await cleanup();
}

const total = pass + failures.length;
console.log(`\n${'-'.repeat(60)}`);
console.log(failures.length === 0
    ? `全部通过：${pass}/${total} 项断言`
    : `失败 ${failures.length} 项 / 共 ${total} 项断言：\n- ${failures.join('\n- ')}`);
console.log(`契约未覆盖：标签管理接口、POST /api/articles/{id}/views（契约 §四·17/18，阶段 8 批 4–批 5 落地）`);
process.exit(failures.length === 0 ? 0 : 1);
