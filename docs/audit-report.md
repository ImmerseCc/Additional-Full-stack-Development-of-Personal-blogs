# 项目审计记录

> **当前状态：正式审计已开始（阶段 9 进行中）。** 第 1 次审计分两层执行：**A 静态层（阶段 9 批 1，已完成 —— 见文末「第 1 次审计 · A 静态层」）**；**B 运行时层（阶段 9 批 3，待执行）**。下方 12 项检查清单的「结论」列已按层回填，最终三类分流（已修复 / 未处理 / 需要我亲自验证）在批 3 后定稿。
> 审计必须基于**实际运行结果**与**逐文件阅读**，不用“应该没问题”下结论；不写“无 bug”，只写“检查了什么、没检查什么”。

---

## 记录模板（复制使用）

```markdown
## 项目审计记录
- 审计范围：
- 发现问题：
- 已修复：
- 未处理：
- 需要我亲自验证：
- 风险提示：
```

要求：
- **审计范围**至少覆盖下方 12 项检查清单；
- **需要我亲自验证**至少列 2 条，并给出具体可复制的验证步骤；
- 结果必须分成 **已修复 / 未处理 / 需要我亲自验证** 三类，不允许只写“已完成”。

---

## 审计检查清单（12 项，阶段 9 逐项填结论）

| # | 检查项 | 具体检查内容 | 结论 |
|---|---|---|---|
| 1 | 能否按 README 启动 | 严格按 README 的命令启动前后端，不额外执行 README 未写的步骤 | 待审计 |
| 2 | 前端 6 个核心模块 | 导航与主题、列表、详情、搜索过滤、评论点赞、本地持久化逐项验证可交互 | 待审计 |
| 3 | 后端 6 项最低功能 | 列表分页、详情、创建、修改、删除、重启后数据仍在 | 待审计 |
| 4 | 响应式 | 375px / 768px / 1280px 三档宽度下导航、列表、详情不破版 | 待审计 |
| 5 | 深色模式对比度 | 正文、次要文字、按钮、边框在深色下的对比度是否可读（重点查灰字发虚） | 待审计 |
| 6 | 输入校验 | 评论与文章表单的前端校验 + 后端 Bean Validation 双层校验是否一致 | **A 静态对账完成、差异已修复**（见 §5 与 §9）：邮箱口径已统一为同一条正则 + ≤100 字；文章表单补齐长度校验；**实测部分待批 3** |
| 7 | 敏感信息 | 仓库内是否出现密钥/Token/密码；`authorEmail` 是否意外回传前端 | **✅ 通过**（A 静态层 §1）：密钥类关键词 0 命中、无 `.env`/`.pem`/`.key`；`CommentVO` 为 5 字段 record，不含 `authorEmail` / `visitorId` |
| 8 | 数据保存读取稳定性 | 点赞/评论/主题刷新后是否保留；后端重启后数据是否仍在；SQLite 并发写入是否报 `SQLITE_BUSY` | 待审计（B 运行时层，批 3；重启不丢已有阶段 2 / 4 / 8 历史实测） |
| 9 | 重复代码 | 组件/工具函数是否有可合并的重复实现 | **已修复（批 2，见 §9）**：滚动+rAF 范式抽为 `frontend/src/utils/scrollFrame.js`；`blankToNull` 收敛为 `common/Texts.java` |
| 10 | 死代码 | 是否存在未被引用的组件、函数、样式、`.gitkeep` 残留 | **已修复（批 2，见 §9）**：删除 `config/.gitkeep` 与 `ApiError#isValidationError`；模块级与类级其余无死代码 |
| 11 | 未使用依赖 | `package.json` 与 `pom.xml` 中声明但未使用的依赖 | **✅ 通过**（A 静态层 §4）：前端 8 个、后端 5 个依赖全部有实际使用点 |
| 12 | 内存泄漏与危险操作 | `setInterval` / 事件监听 / `IntersectionObserver` 是否正确清理；是否存在危险删除逻辑 | **✅ 通过**（A 静态层 §6）：监听器 / 观察器 / rAF 清理齐全（清理逻辑现集中在 `utils/scrollFrame.js`）；`clearAll()` 只删 `blog:` 前缀、后端无 `DROP`/`TRUNCATE`；1 条观察项（theme store 监听常驻） |

---

## 审计方法（阶段 9 执行时使用）

1. **读代码**：逐文件阅读 `frontend/src/**` 与 `backend/src/main/java/**`，标记可疑点；
2. **跑命令**：按 README 启动前后端，用 `curl` 逐个调用接口（含错误分支：不存在的 ID、超长字段、非法 JSON）；
3. **浏览器实测**：DevTools 的 Network / Application / Performance 面板，检查请求、localStorage、监听器清理；
4. **静态检查**：搜索危险 API（`innerHTML`、`v-html` 未过滤、`eval`）、硬编码密钥、未使用导出；
5. **交叉验证**：README 描述与实现是否一致（启动命令、端口、数据位置、功能清单）。

---

## 历次审计

| 轮次 | 时间点 | 范围 | 结论摘要 | 记录位置 |
|---|---|---|---|---|
| 预审计 | 2026-09-25（阶段 6 批 1） | 契约 ↔ 实现逐条对账（17 条接口 + 通用约定 + 7 个错误码） | 13 条已实现且一致 / 4 条为阶段 8 可选项；**发现 `40009` 不可达**；接口回归 **97/97 通过** | 本文件 → 阶段 6 预审计 |
| 1 · A 静态层 | 2026-09-26（阶段 9 批 1） | 前端 48 / 后端 41 个源文件的静态层：敏感信息、重复代码、死代码、未使用依赖、内存泄漏与危险操作 + 第 6 项规则对账 | **第 7 / 11 / 12 项通过**；发现 2 处可合并重复、2 处死代码残留、3 处校验口径差异（**均低危**）→ 拟批 2 处理；B 运行时层待批 3 | 本文件 → 第 1 次审计 · A 静态层 |
| 1 · B 运行时层 | 待执行（阶段 9 批 3） | README 启动 / 前端六模块 / 后端六项 / 响应式 / 对比度 / 双层校验实测 / 数据稳定性 | 待审计 | 本文件 |

---

## 阶段 6 预审计（批 1 · 契约逐条复核，2026-09-25）

> **定位**：正式审计仍在**阶段 9**。本章是“预告式”复核 —— 只做「契约 ↔ 实现」逐条对账 + 可重复的接口回归，**不做**全量代码审计（上面 12 项检查清单仍待阶段 9 逐项填结论）。
> **方法**：① 逐行阅读 `backend/src/main/java/**` 的 controller / service / common；② 用本批新增的 `frontend/scripts/smoke.mjs`（Node 原生 `fetch`、**零新增依赖**）对**真实后端**跑 97 项断言；③ 脚本无法安全覆盖的行为（标签自动创建）用一次性调用 + JDBC 复核，并当场清理。
> **前置状态**：后端 8080 运行中；数据库为种子状态（`article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`）。

### 1. 契约 §三「接口清单」17 条逐条结论

| # | 契约条目 | 实现 | 结论与证据 |
|---|---|---|---|
| 1 | `GET /api/health` | 已实现 | ✅ 一致：HTTP 200 / `code=0` / `status=UP` / `time` 符合 `yyyy-MM-ddTHH:mm:ss` |
| 2 | `GET /api/articles` | 已实现 | ✅ 一致：分页 `page/size/total/totalPages`、默认只返回 `PUBLISHED`、`createdAt` 倒序；`keyword=SQLite` → 3 篇、`tags=Vue,前端` AND → 2 篇 / OR → 5 篇、`status=DRAFT` → 0 篇；6 组越界与非法取值（`page=0`、`size=0`、`size=999`、`page=abc`、`tagMode=xxx`、`status=xxx`）全部 `40002` |
| 3 | `GET /api/articles/{id}` | 已实现 | ✅ 一致：含 `content`；`prev`/`next` 恒为 `null`（契约已注明属阶段 8 能力）；不存在 → `40004` |
| 4 | `POST /api/articles` | 已实现 | ✅ 一致：HTTP 201；`summary` 留空 → 按**码点**截取正文前 120 字；标签自动创建 + 去重（`Vue,Vue,前端` → 2 个）；标题 101 字 / 空内容 / 6 个标签 / `status=ARCHIVED` / 标签名 21 字 → 均 `40001` 且带 `data.fields` |
| 5 | `PUT /api/articles/{id}` | 已实现 | ✅ 一致：全量更新；`tags: []` 与**省略 `tags`** 都表示清空；转 `DRAFT` 后从默认列表消失、`?status=DRAFT` 可查到；不存在 → `40004` |
| 6 | `DELETE /api/articles/{id}` | 已实现 | ✅ 一致：`data` 为 `null`；删除后详情 `40004`、重复删除 `40004`；**标签本身保留**（一次性验证：新建的标签在文章删除后仍在，`articleCount` 归 0） |
| 7 | `GET /api/tags` | 已实现 | ✅ 一致：8 个 `TagVO`（`id/name/articleCount`），按 `articleCount` 倒序，`articleCount` 合计 23 = `article_tag` 行数 |
| 8 | `GET /api/articles/{id}/comments` | 已实现 | ✅ 一致：分页对象完整、按 `createdAt` 倒序；文章不存在 → `40004` |
| 9 | `POST /api/articles/{id}/comments` | 已实现 | ✅ 一致：HTTP 201；`authorEmail` **只存不返回**（实测响应无该字段）、`visitorId` 同样不回传；空昵称 / 非法邮箱 / 1001 字内容 / 7 位 `visitorId` → `40001` 且带字段级原因 |
| 10 | `GET /api/comments/{id}` | **未实现** | ⏸ 契约标注“阶段 8（可选）”，与本批实测一致 |
| 11 | `PUT /api/comments/{id}` | **未实现** | ⏸ 同上 |
| 12 | `DELETE /api/comments/{id}` | 已实现 | ✅ 一致：`visitorId` 不匹配与评论不存在**同码同文案**（不泄露评论是否存在）→ `40004`；成功时 `data` 为 `null` |
| 13 | `GET /api/articles/{id}/likes` | 已实现 | ✅ 一致：返回 `LikeStateVO`；`visitorId` 必填（缺失 / 7 位 → `40001`，阶段 5 批 1 的契约补注生效） |
| 14 | `POST /api/articles/{id}/likes` | 已实现 | ✅ 一致：**幂等**（连点两次仍 `liked=true`、`likeCount=1`） |
| 15 | `DELETE /api/articles/{id}/likes` | 已实现 | ✅ 一致：**幂等**（未点赞时调用仍 `liked=false`、`likeCount=0`） |
| 16 | `GET /api/articles/{id}/adjacent` | **未实现** | ⏸ 契约标注“阶段 8（可选）” |
| 17 | 标签管理 `POST/PUT/DELETE /api/tags` | **未实现** | ⏸ 契约标注“阶段 8（可选）” |

**汇总：17 条中 13 条已实现且逐条一致，4 条为契约已标注的阶段 8 可选项。**

### 2. 契约 §一「通用约定」与错误码表

| 检查项 | 结论 |
|---|---|
| 响应体固定为 `{code, message, data}` 三字段 | ✅ 实测成功响应恰为 3 个键，`data` 不省略 |
| HTTP 状态码与业务 `code` 同时返回 | ✅ 13 组异常用例全部双检通过 |
| 空数据「`data` 为 `null` 或空数组」 | ✅ 删除接口 `data: null`；空列表 `items: []` 且 `total: 0` |
| 时间格式 `yyyy-MM-ddTHH:mm:ss` | ✅ 健康检查与文章字段均匹配 |
| 分页 `page` 从 1 起、`size` 1–20（默认 10） | ✅ 默认值与 4 组越界反例全部 `40002` |
| 列表排序 `createdAt` 倒序 | ✅ 逐项比较通过 |
| `0` 成功 | ✅ |
| `40001` 参数校验失败（含 `data.fields`） | ✅ 文章 / 评论 / 点赞三类共 9 组反例命中，字段级原因齐备 |
| `40002` 参数格式错误 | ✅ 越界、非数字、非法 `tagMode` / `status`、非法 JSON、方法不支持（`PATCH`）、查询串编码非法（历史报错记录 4） |
| `40004` 资源不存在 | ✅ 文章 / 评论 / 点赞共 9 组用例 + 未知路径（`/api/nope`） |
| `40009` 资源冲突 | ⚠️ **当前不可达**（见第 3 节问题 1） |
| `50000` 服务端未预期异常 | ⚠️ 兜底处理器已接线，**本批未构造触发条件，未实测** |
| `50001` 数据库操作失败 | ⚠️ 同上（`DataAccessException` → `50001`） |

### 3. 发现的问题

| # | 问题 | 级别 | 状态 |
|---|---|---|---|
| 1 | **`40009` 是死码**：契约错误码表、`ErrorCode.CONFLICT`、`OpenApiConfig` 的接口描述三处都提到它，但全仓库**没有任何抛出点**（`grep -rn "CONFLICT\|40009"` 只命中定义与文档两处）。契约 §四·4 的“标签不存在自动创建”由 find-or-create + 去重实现，因此“标签名重复”根本不会走到冲突分支；只有并发插入同名标签撞上 `tag.name` 的 UNIQUE 约束时，才会以 `DataAccessException` → **`50001`** 的形式暴露 | 契约 ↔ 实现偏差（低危：无功能影响，但文档承诺了一个不可达的错误码） | **未处理**（建议：阶段 8 做标签管理接口时启用 `40009`，或在契约中把它标注为“预留”） |
| 2 | 同一问题的文档侧表现：Swagger 的接口描述里列了 `40009`，调用方可能据此写出永远不会命中的分支 | 文档一致性（低危） | 未处理（随第 1 条一并解决） |

> 除以上 2 条外，本批**未发现**契约与实现的其它偏差。**这不代表没有问题**，只代表“契约所描述的 17 条接口 + 通用约定 + 错误码”这一范围已逐条对完。

### 4. 未覆盖 / 未验证（不谎报）

1. `50000` / `50001` 的触发链路未实测（需构造数据库故障或注入异常，本批未做）；
2. 并发场景未覆盖：同一访客并发点赞、并发写导致的 `SQLITE_BUSY`（对应审计清单第 8 项，留阶段 9）；
3. 契约 §四·10 / 11 / 16 / 17 未实现（阶段 8 可选项）；
4. 前端**没有**文章管理入口（`POST/PUT/DELETE /api/articles` 目前只被 smoke 脚本调用）—— 契约 §五 的既定安排，不算缺陷；
5. 浏览器层行为（渲染、交互、响应式、无障碍）不在本批范围，由批 2 / 批 4 覆盖。

### 5. 需要作者亲自验证（2 条，均可复制）

1. **接口回归脚本**（需后端已启动，期望最后一行 `全部通过：97/97 项断言`；脚本会自动清理它写入的评论 / 文章 / 点赞）：

   ```bash
   cd "/d/code/Additional Full-stack Development of Personal blogs/frontend" && npm run smoke
   ```
   ```powershell
   cd "D:\code\Additional Full-stack Development of Personal blogs\frontend"; npm run smoke
   ```

2. **`40009` 不可达的复核**（可选，在仓库根目录执行，期望只看到定义行与描述行、**没有任何 `throw`**）：

   ```bash
   grep -rn "CONFLICT\|40009" backend/src/main/java
   ```

### 6. 回归脚本说明（本批新增，可重复运行）

`frontend/scripts/smoke.mjs`（`npm run smoke`）：Node 原生 `fetch`、**零新增依赖**，覆盖 13 个已实现操作的正例 + 反例共 **97 项断言**，按契约章节分组输出；跑完自动清理测试数据并复核数据库回到种子状态。脚本刻意**不覆盖**“标签不存在自动创建”（会永久新增标签行，而删除标签的接口属阶段 8），该行为改用一次性调用 + JDBC 清理验证（已在第 1 节第 4、6 条与第 3 节记录）。

---

## 阶段 6 预审计（批 3 补充 · 异常 / 边界 / 安全复核，2026-09-25）

> 本章是批 1「契约逐条复核」的**补充**：把"异常路径、边界输入、安全载荷"也走一遍，结论只写实测到的，不写"应该没问题"。**正式审计仍在阶段 9**（12 项检查清单未动）。

### 1. 安全类复核（XSS / 注入 / 通配符）

| 检查项 | 实测结论 |
|---|---|
| 正文 Markdown 里的 `<script>` / `<img onerror>` / `<iframe>` | ✅ **全部以纯文本呈现**：不创建元素、不执行脚本、无弹窗（markdown-it `html:false` 转义 + DOMPurify 兜底两层） |
| Markdown 链接 `[x](javascript:alert(1))` | ✅ **未渲染为可点链接**（渲染为字面文本），`javascript:` 未进入 DOM 属性 |
| 文章标题 / 标签名中的 HTML | ✅ 只作为文本：`document.title` 与标签 chip 均按原文显示，不解析标签 |
| 评论内容中的 HTML / 脚本 | ✅ 按纯文本渲染（评论不走 Markdown 管道，直接文本插值） |
| 搜索关键字 SQL 注入（`' OR 1=1 --`、`UNION SELECT`） | ✅ 参数化占位符 + `LIKE ? ESCAPE '\'`：返回 200 / `code=0` / `total=0`，无 500、无越权穿透 |
| 搜索关键字通配符（`%`、`_`） | ✅ 被显式转义（`ArticleRepository#escapeLike`），不会退化成"匹配全部" |
| 路径参数非数字（`/articles/abc`） | ✅ 后端 `40002`；前端有专门错误态「文章 ID 不合法」，不泄露堆栈 |

### 2. 异常与降级演练（后端停服）

| 场景 | 实测表现 |
|---|---|
| 直连后端 / 经 Vite 代理 / 前端静态页 | `000` / `502` / `200`（前端页面不依赖后端即可打开） |
| 列表页、详情页 | 整页错误态：「无法连接后端服务（HTTP 502），请确认后端已在 http://localhost:8080 运行」+ **重试**按钮 |
| 评论提交 | **表单内行内红字**错误，输入内容保留、未被清空、未产生脏数据 |
| 点赞 | 右下角 **Toast** 同文案；按钮状态未被误改（无乐观更新） |
| `/about` 本地数据面板 | ✅ 后端不可用仍完全可用（本地数据与后端解耦） |
| 后端恢复 | 重启约 4 秒就绪，列表与详情自动恢复正常（无需清缓存） |

### 3. 边界与 URL 篡改

| 输入 | 实测表现 |
|---|---|
| `/no-such-page` | 404 页，`document.title` 同步为「页面不存在 · 个人博客」 |
| `/articles/99999`、`/0`、`/-1` | 详情 404「文章不存在」+ 返回文章列表 |
| `/articles/abc` | 错误态「文章 ID 不合法：abc」+ 重试 / 返回 |
| `?tags=不存在的标签` | 空态「筛选出 0 篇文章」+ 「清除筛选」，无报错 |
| `?page=-1&size=999` | **前端静默归一**（`page` 回第 1 页、`size` 用前端常量），非法值不会上送；接口层直接调用仍严格 `40002` |

### 4. 本批未覆盖（不谎报）

1. `50000` / `50001` 仍未构造触发条件（需注入异常或制造数据库故障），**未实测**；
2. 存储型 XSS 的"入库前过滤"未做——本项目的策略是**渲染侧防护**（写入保留原文、渲染时转义 / 消毒），这是既定设计，不是遗漏；
3. 并发与 `SQLITE_BUSY`、CSRF（无登录体系，暂不适用）不在本批范围，留阶段 9 评估。

### 5. 需要作者亲自验证（2 条，均可复制）

1. **停服降级**（先启动后端 → 打开 http://localhost:5173/articles/1 → 停掉后端 → 点「点赞」）：期望出现右下角 Toast「无法连接后端服务（HTTP 502）…」，且按钮仍显示「点赞 0」；
   停后端：`netstat -ano | grep ":8080 " | grep -i listening` 取 PID 后 `taskkill //PID <pid> //F`；重启见 `README.md` 第二节。
2. **XSS 复验**：打开 `docs/demo/stage6-11-xss-as-text.png` 查看渲染结果，或按 `docs/api-contract.md` 的 `POST /api/articles` 自建一篇含 `<script>` 的文章访问一次（记得删掉，删除后标签会保留，需按 `docs/current-state.md` 遗留 25 的方式清理）。

---

## 阶段 6 预审计（批 4 补充 · 非功能与可访问性复核，2026-09-25）

> 第三块补充：响应式、对比度、键盘可达性、构建体积，以及本批落地的**全局错误兜底**。同样只写实测到的。

### 1. 响应式（无破版判定：无横向溢出、断点行为符合设计）

| 视口 | 实测结论 |
|---|---|
| 375×667 | 页头折叠为汉堡菜单、标签面板折叠（「展开」）、卡片单列、正文与封面自适应；**无横向溢出** |
| 768×1024 | **断点边界正确**：桌面导航（无汉堡）、标签面板展开、双列卡片 |
| 1920×1080 | 内容居中（`--content-max: 1040px`）、详情页目录在右、代码高亮正常 |

### 2. 颜色对比度（WCAG 相对亮度公式计算，非目测）

| 组合 | 比值 | 阈值 | 结论 |
|---|---|---|---|
| 暗 · 正文 on 背景 | 15.20 | 4.5 | ✅ |
| 暗 · 正文 on 卡片 | 13.24 | 4.5 | ✅ |
| 暗 · 次要文字 on 背景 / 卡片 | 7.31 / 6.36 | 4.5 | ✅ |
| 暗 · 链接 on 背景 / 卡片 | 6.68 / 5.82 | 4.5 | ✅ |
| 暗 · 点赞色 on 卡片 | 6.38 | 3.0 | ✅ |
| 暗 · 按钮文字 on 主色 | 6.87 | 4.5 | ✅ |
| 亮 · 正文 on 背景 | 15.80 | 4.5 | ✅ |
| 亮 · 次要文字 on 背景 | 6.11 | 4.5 | ✅ |
| **亮 · 主色 `#3b6ef5` on 白底**（链接 / 主按钮白字同值） | **4.44** | 4.5 | ⚠️ **略低于 AA** → 遗留 30，候选修正 `#3563e0`(5.23) / `#2f5ed6`(5.69) |
| 暗 · 边框 / 强边框 | 1.37 / 1.63 | 3.0 | 记录在案：**装饰性分隔线不适用** WCAG 1.4.11 的 3:1（非交互组件） |

### 3. 键盘可达性与动效

- ✅ Tab 遍历页头，`:focus-visible` 焦点环（2px 主色 + 2px 偏移）可见并随焦点移动；
- ✅ `prefers-reduced-motion`：**静态证据** —— `base.css` 媒体查询把 `--duration-fast/base/slow` 归零、并对 `*` 强制 `animation/transition-duration: 0.01ms`、`scroll-behavior: auto`，全站动效均走这些令牌（阶段 5 已由作者验收过该可选项）。**未做**动态仿真（本机无可用通道）。

### 4. 全局错误兜底（批 4 代码改动）

- `frontend/src/main.js`：`app.config.errorHandler` + `window.unhandledrejection` → `console.error` + 一条 error Toast「页面出现未预期的异常，请刷新或稍后重试」；
- **实测**：临时探针 `?probe=error`（组件生命周期抛错）与 `?probe=reject`（未处理拒绝）**两条路径均命中**（6ms / 5ms），探针已删除；截图 `docs/demo/stage6-15-global-error-toast.png`；
- **成本**：`index` chunk 54.22 → **54.53 kB**（+0.31 kB，gzip +0.22 kB）；构建 234ms。

### 5. 本批未覆盖（不谎报）

1. **浏览器控制台 warn / 网络层 4xx 未直接检查**：本机 Edge 无头模式不产出任何输出（`--dump-dom` / `--enable-logging` / `--log-file` 全空，多次复现），该通道不可用；已改用应用层替代证据（首页 / 详情 / 关于三页在 1.5s 稳定窗口内**无全局错误 Toast**，即无未捕获异常）；
2. `prefers-reduced-motion` 未做动态仿真（同上，无可用通道）；
3. 真实移动端触摸点按仍以作者真机为准（阶段 3 已验收过一次）。

---

## 阶段 7 变更摘要（供阶段 9 正式审计参考）

> 本阶段为**纯前端**改动：未改 `docs/api-contract.md`、未改 `docs/data-model.md`、未动 `backend/` 任何文件、未新增任何依赖（前端依赖版本与阶段 6 完全一致）。

### 1. 代码变更清单

| 类型 | 文件 | 说明 |
|---|---|---|
| 新增 | `frontend/src/components/ReadingProgress.vue` | 阅读进度条（`role="progressbar"`，rAF 驱动，Teleport 到 body） |
| 新增 | `frontend/src/components/BackToTop.vue` | 回到顶部按钮（全站挂载，1.5 屏阈值，reduced-motion 瞬时跳转） |
| **删除** | `frontend/src/components/Pagination.vue` | 无限滚动取代分页后**零引用**（全仓 grep 确认；作者选 A 后批 6 删除） |
| 修改 | `frontend/src/views/ArticlesView.vue` | 分页 → 累积加载（`?page` = 已加载页数、跨页累积、越界收敛、滚动 `replace`）+ IO 哨兵 + 「加载更多」兜底 |
| 修改 | `frontend/src/views/ArticleDetailView.vue` | 挂进度条 / 异步管线 + `<Suspense>` / 窄屏折叠目录面板 |
| 修改 | `frontend/src/components/ArticleList.vue` | 新增 `appending`（追加骨架 + `aria-busy`） |
| 修改 | `frontend/src/components/TableOfContents.vue` | 新增 `navigate` emit、`showTitle`；**跳转改为"先收起、后滚动"** |
| 修改 | `frontend/src/components/CommentForm.vue` / `CommentSection.vue` | 暴露 / 调用 `clearStatus()`（删除评论后清提示） |
| 修改 | `frontend/src/App.vue` | 引入 `BackToTop` |
| 修改 | `frontend/src/styles/base.css` | 亮色主色 `#3b6ef5 → #3563e0`、`--color-accent-soft → rgba(53,99,224,.14)` |

### 2. 构建体积变化（`npm run build`，均为实测）

| 产物 | 阶段 6 收尾 | 阶段 7 收尾 |
|---|---|---|
| 详情页 shell | `ArticleDetailView` 297.51 kB（gzip 111.03） | **18.38 kB（gzip 6.70）** |
| Markdown 管线 | 与详情页同包 | **独立 chunk 281.34 kB（gzip 104.55）** |
| 共享 chunk | `_plugin-vue_export-helper` 63.56 kB（gzip 24.91） | **70.36 kB（gzip 27.28）**（+2.4 kB gzip，源自首次引入 `<Suspense>`） |
| 列表页 | `ArticlesView` 8.80 kB | 8.63 kB |
| 入口 | `index` 54.53 kB | 55.57 kB（新增两个组件的注册） |

### 3. 新增观察项（已记入 `docs/debug-log.md`）

- **合成点击打不开 `<details>`**（触摸模拟下坐标点击无效，`click_if_interactive` 只识别为 generic 角色）→ 工具限制，验证走键盘路径；
- **详情页"异步管线加载失败"的表现未演练** → dev 模式的报错覆盖层与生产行为不一致，如后续要加固，可在 `defineAsyncComponent` 上补 `errorComponent`。

### 4. 本次审计可复核项与未覆盖项

- **可复核**：`npm run smoke` 97/97；`npm run build` 产物分包（见上表）；对比度（`#3563e0` 对白底 5.23，node 按 WCAG 公式实算）；六个模块的浏览器走查记录（`docs/demo/stage7-01…10`）+ 各批 ai-log 条目。
- **未覆盖（如实登记）**：骨架屏出现时机（遗留 18，本机请求约 10 ms 抓不到）；异步 chunk 加载失败路径；`prefers-reduced-motion` 的动态仿真（仍为静态证据）；浏览器控制台 warn 与网络层 4xx（本机 Edge 无头不可用）—— 与阶段 6 的未覆盖范围一致，建议在阶段 9 的正式审计中统一处理。

---

## 阶段 8 变更摘要（供阶段 9 正式审计参考）

> 本阶段是**后端能力补全 + 前端最小接入 + 写作台**：契约升级为 **v1.1 已确认**（第 10 / 11 / 16 / 17 条转正、新增第 18 条、`viewCount` 字段）；表结构**零变更**；未新增任何依赖；新增 1 个前端路由（`/studio`）与 1 个视图。

### 1. 代码变更清单

| 类型 | 文件 | 说明 |
|---|---|---|
| 新增 | `backend/src/main/java/com/example/blog/model/{CommentUpdateRequest,AdjacentVO,AdjacentPairVO,ViewCountVO,TagSaveRequest}.java` | 评论修改 DTO、相邻文章 VO（顶层化）、阅读数 VO、标签 DTO |
| 修改 | `backend/src/main/java/com/example/blog/model/{ArticleSummaryVO,ArticleDetailVO}.java` | +`viewCount`（契约 §2.2 / §2.3） |
| 修改 | `backend/src/main/java/com/example/blog/repository/{CommentRepository,ArticleRepository,TagRepository}.java` | 单条评论读改、相邻查询（行值比较 `(created_at, id)`）、阅读数自增、标签管理写方法 |
| 修改 | `backend/src/main/java/com/example/blog/service/{CommentService,ArticleService,TagService}.java` | 归属校验 / 相邻带出 / 阅读数 / 标签管理（重名 `40009`） |
| 修改 | `backend/src/main/java/com/example/blog/controller/{CommentController,ArticleController,TagController}.java` | 新增 7 个操作（**20 个操作收口**） |
| 修改 | `backend/src/main/resources/application.yml` | JDBC URL 追加 `journal_mode=WAL` |
| 新增 | `frontend/src/views/StudioView.vue` | 写作台（隐藏入口 `/studio`）：文章 CRUD + 草稿切换 + 标签管理 |
| 修改 | `frontend/src/api/{articles,tags,comments}.js` | +7 个写接口封装（`postArticleView` / `createArticle` / `updateArticle` / `deleteArticle` / `createTag` / `renameTag` / `deleteTag` / `updateComment`） |
| 修改 | `frontend/src/router/index.js` | +`/studio` 隐藏路由（不进导航栏） |
| 修改 | `frontend/src/views/ArticleDetailView.vue` | 阅读数显示与上报（fire-and-forget + 返回值刷新）、上下篇导航 |
| 修改 | `frontend/src/components/{CommentItem,CommentSection}.vue` | 评论行内编辑（仅本机账本可见） |
| 修改 | `frontend/scripts/smoke.mjs` | 97 → **143 项断言**（新增 4 组用例；修正 1 项阶段 6 遗留的过时断言） |

### 2. 契约与实现一致性

- `docs/api-contract.md` **v1.1 已确认**（作者"批 1 通过"）；**§四 · 1–18 全部实现**；
- 错误码闭环：`40009`（标签重名）、`50000`（临时探针）、`50001` + `SQLITE_BUSY`（外部写者持锁 → 锁内写等待 **5.11s** 失败、锁内读 **3.6ms** 成功）——**全部拿到真实触发记录**（探针与临时程序均未提交）；
- 前端不按记忆写死字段名：smoke 的 `ARTICLE_KEYS` 已加入 `viewCount`，按契约断言。

### 3. 数据与运行环境

- 数据库：`journal_mode=wal`（伴生 `-wal` / `-shm`）；重置口径见 `docs/data-model.md` §五；
- 数据终核：`article=12 / tag=8 / article_tag=23 / comment=0 / like_record=0`（`view_count` 复位为 0）。

### 4. 本次审计可复核项与未覆盖项

- **可复核**：`npm run smoke` **143/143**；`npm run build` **258ms** 与分包（`StudioView 11.91 kB` 懒加载、详情 shell 22.92 kB）；浏览器 14 项实测记录（批 6 / 批 7）与截图 `docs/demo/stage8-01…05`；各批 ai-log 条目。
- **未覆盖（如实登记）**：浏览器控制台 warn 与网络层 4xx（本机 Edge 无头不可用）；`prefers-reduced-motion` 动态仿真；骨架屏出现时机（遗留 18）；详情页异步管线失败路径（遗留 32）—— 与阶段 6 / 7 的未覆盖范围一致，建议阶段 9 正式审计统一处理。

---

## 第 1 次审计 · A 静态层（阶段 9 批 1，2026-09-26）

> **范围**：`frontend/src/**`（48 文件 / 6 513 行）与 `backend/src/main/java/**`（41 个 `.java` / 2 519 行）的**静态层**检查 —— 敏感信息（第 7 项）、重复代码（第 9 项）、死代码（第 10 项）、未使用依赖（第 11 项）、内存泄漏与危险操作（第 12 项），外加第 6 项（输入校验）的**前后端规则对账**。
> **方法**：逐文件阅读 + 交叉引用计数（对每个前端模块 / 后端类统计导入与引用点）+ 关键词扫描（密钥、危险 API、`DROP`/`TRUNCATE`）+ 依赖—使用点对账 + CSS 类与变量的启发式引用检查。
> **声明**：本批**只读**，未改动任何业务代码；所有"通过"都写明**检查了什么**，不写"无 bug"。

### 1. 敏感信息（第 7 项）— ✅ 通过

| 检查 | 结果 |
|---|---|
| 密钥 / Token / 密码（`frontend/src`、`backend/src`、`pom.xml`、`vite.config.js`、`index.html`） | **0 命中**（`password\|secret\|token\|apikey` 均无匹配） |
| `.env` / `.pem` / `.key` 等凭据文件 | **不存在**（根目录 `ls -a` 复核） |
| `application.yml` | 只含端口、数据源（`./data/blog.db` + WAL）、初始化与日志级别，**无凭据** |
| `authorEmail` 是否回传前端 | **不回传**：`CommentVO`（`backend/src/main/java/com/example/blog/model/CommentVO.java:10-15`）是 5 字段 record（`id / articleId / authorName / content / createdAt`），**不含** `authorEmail` 与 `visitorId`；二者只出现在请求 DTO（`CommentCreateRequest.java:21`）与实体（`Comment.java:15`） |

### 2. 重复代码（第 9 项）— 发现 2 处（低危，拟批 2）

| # | 重复点 | 位置 | 说明 |
|---|---|---|---|
| 9-1 | "滚动 + `requestAnimationFrame` 合并"范式重复 | `frontend/src/utils/scrollSpy.js:38-51`、`frontend/src/components/BackToTop.vue:19-36`、`frontend/src/components/ReadingProgress.vue:23-38` | 三处均为「scroll/resize 监听 → 只登记一帧 → rAF 中计算 → 卸载时取消」；`ReadingProgress.vue:6` 注释自认"与 `utils/scrollSpy.js` 同一范式"。可抽 `utils/scrollFrame.js` |
| 9-2 | `blankToNull` 两个 service 各写一份 | `backend/.../service/ArticleService.java:197`、`backend/.../service/CommentService.java:104` | 逻辑相同（空白串归一为 `null`），可提到 `common`（如 `Texts.java`） |

> 其它疑似重复经核对**不成立**：`SkeletonBlock` 被 `ArticleSkeleton` 等复用（组合而非复制，25 处引用）；`escapeLike` 全仓只有一个定义（`ArticleRepository.java:247`）；标签"不存在则自动创建"由 `ArticleService` 委托 `TagService`（`ArticleService.java:27`），**没有**把重名 `40009` 逻辑复制两份；时间格式集中在 `common/TimeFormats.java`。

### 3. 死代码（第 10 项）— 发现 2 处残留（拟批 2）

| # | 残留 | 位置 | 依据 |
|---|---|---|---|
| 10-1 | `.gitkeep` 残留 | `backend/src/main/java/com/example/blog/config/.gitkeep` | 该包已有 `JacksonConfig.java` / `OpenApiConfig.java` 两个真实类 |
| 10-2 | 未被引用的 getter | `frontend/src/api/error.js:16`（`get isValidationError()`） | 全仓 `isValidationError` **仅 1 处命中**（定义本身）；对照 `isNotFound` 有 3 处使用（`CommentItem.vue:66,87`、`ArticleDetailView.vue:43`） |

**不含死代码的部分（已逐一核对）**：前端 38 个模块（20 组件 / 8 utils / 4 stores / 6 api）引用数全部 ≥ 2；后端 41 个类全部有引用（`BlogApplication`、5 个 controller、2 个 config 由框架发现，属正常）；各 `*.js` 导出函数均有使用点（含 `getVisitorId` 10 处、`useScrollSpy`、`revealDirective`、`fetchArticleDetail` 等）。

**另两处 `.gitkeep` 的处置**：`backend/data/.gitkeep` **必须保留**（保证目录存在，`blog.db` 被 gitignore）；`docs/demo/.gitkeep` 含目录说明文本（非空占位），批 4 新增 `docs/demo/README.md` 时一并决定去留。

**CSS 误报澄清**：工具曾标记 `.page-enter-active` / `.page-enter-from` / `.page-leave-active` / `.page-leave-to` 与 `--color-bg`，人工复核确认**均为误报** —— 前 4 个是 Vue `<Transition name="page">`（`App.vue:21`）运行时生成的类名；`--color-bg` 在 `frontend/src/styles/base.css:76`（`body` 背景）真实使用。**结论：未发现未使用的 CSS 类 / 变量**（启发式检查，局限见 §7）。

### 4. 未使用依赖（第 11 项）— ✅ 通过

| 侧 | 依赖 | 使用点 |
|---|---|---|
| 前端 | vue / vue-router / pinia | `main.js`、`router/index.js`、4 个 store |
| | markdown-it / highlight.js / dompurify | `frontend/src/utils/markdown.js:6-8` |
| | vite / @vitejs/plugin-vue（dev） | `vite.config.js:2-3` + `package.json` scripts |
| 后端 | `spring-boot-starter-webmvc` / `-jdbc` / `-validation` | controller 层 / `JdbcTemplate` / 各请求 DTO 的 `@NotBlank` 等 |
| | `sqlite-jdbc` / `springdoc-openapi-starter-webmvc-ui` | `application.yml` 驱动 + `config/OpenApiConfig.java` 与 20 个 `@Operation` |

> `pom.xml:60` 有显式说明：**不引入测试起步依赖**（当前无测试），避免"声明了却没用"——与审计口径一致。

### 5. 输入校验对账（第 6 项 · 静态部分）— 发现 3 处口径差异（低危）

| # | 差异 | 前端 | 后端 | 影响 |
|---|---|---|---|---|
| 6-1 | 邮箱格式 | `frontend/src/utils/validate.js:9` 正则**要求含点**（`a@b` 会被前端拦下） | `CommentCreateRequest.java:19` 用 `@Email`（较宽松，`a@b` 可通过） | 前端**更严**：接口侧可存入"前端认为非法"的邮箱；不影响数据安全 |
| 6-2 | 邮箱长度 | 未校验 | `@Size(max = 100)`（`CommentCreateRequest.java:20`） | 前端放行、后端 `40001` 拦下；表单按字段名回填原因（`CommentForm.vue:131`），体验可接受 |
| 6-3 | 文章表单 | `views/StudioView.vue:92-98` 只校验：标题非空且 ≤100、正文非空、标签 ≤5 | `ArticleCreateRequest` / `ArticleUpdateRequest` 另含 `summary ≤200`、`coverUrl ≤200`、`content ≤50000` | 前端更松、后端兜底；`StudioView.vue:115` 会把 `40001` 的 `data.fields` 汇总展示 |

**结论**：不存在"前端放过且后端静默接受"的方向；3 处均为可解释的松紧差异。评论表单三字段（昵称 ≤30 / 内容 ≤1000 / 必填）与后端**完全一致**（`validate.js:4-7` ↔ `CommentCreateRequest.java:15-24`）。

### 6. 内存泄漏与危险操作（第 12 项）— ✅ 通过

| 检查 | 结果 |
|---|---|
| 事件监听配对 | 6 组 `addEventListener` 全部有清理：`utils/scrollSpy.js:46-53`、`components/BackToTop.vue:28-36`、`components/ReadingProgress.vue:30-38`、`components/AppHeader.vue:77-87`（含 `matchMedia`）、`components/TagFilter.vue:30,35`、`api/http.js:48`（`{ once: true }` + `:81` 显式移除） |
| `IntersectionObserver` | 3 处 `disconnect()`：`utils/reveal.js:60`、`views/ArticlesView.vue:173,192` |
| 定时器 / 帧 | `setTimeout` 均有 `clearTimeout`（`stores/toast.js:18,38,53`、`LikeButton.vue:84-92`、`utils/debounce.js:13-22`）；`requestAnimationFrame` 均有 `cancelAnimationFrame`（`BackToTop.vue:19,34`、`scrollSpy.js:41,51`、`ReadingProgress.vue:26,36`）；`SearchInput.vue:47` 卸载时 `commit.cancel()` |
| 危险删除（前端） | **无** `localStorage.clear()`；`utils/storage.js:96-101` 的 `clearAll()` 只删 `blog:` 前缀键，不动同域其它数据 |
| 危险操作（后端） | 全部 `DELETE` 均为**参数化单行删除**（`ArticleRepository.java:205,210`、`CommentRepository.java:105`、`LikeRepository.java:71`、`TagRepository.java:98`）；全仓**无** `DROP TABLE` / `TRUNCATE` |
| 危险 API（前端） | 仅 `MarkdownRenderer.vue:16` 一处 `v-html`，输入是 `utils/markdown.js:97` 的 **DOMPurify 消毒输出**（阶段 6 批 3 已用四类载荷实测为纯文本） |

**观察项（非缺陷，登记）**：`frontend/src/stores/theme.js:70` 的 `matchMedia('change')` 监听在应用生命周期内**不解除** —— 单例 store、`init()` 只执行一次（`:62-66` 有 `initialized` 守卫），SPA 场景不构成泄漏；若将来支持 store 销毁需补移除。

### 7. 本批未覆盖 / 局限（如实登记）

1. **"未使用 CSS 类 / 变量"是启发式检查**（按类名字符串检索 `.vue` / `.js`）：动态拼接的类名可能漏判，不做 100% 反向断言；
2. **后端只查到"类级"引用**，未做"未使用方法 / 字段"级检查；
3. **未引入静态分析器**（无 ESLint / Checkstyle，本项目不为此新增依赖）；
4. 浏览器层行为、接口实测、对比度、响应式、并发与 `SQLITE_BUSY` **不在本批范围**，属批 3（B 运行时层）。

### 8. 结论分流（A 静态层）

- **已修复**：批 1 本批未改代码；下述"未处理"项**已在批 2 全部修复**（见 §9）。
- **未处理（拟批 2）**：9-1、9-2（重复实现）、10-1、10-2（死代码残留）、6-1 / 6-2 / 6-3（校验口径差异，至少统一邮箱口径）。→ **批 2 已全部修复，见 §9**。
- **需要我亲自验证（2 条，可复制）**：
  1. `ApiError#isValidationError` 是否真的无人引用（期望只看到定义行 `frontend/src/api/error.js:16`）：

     ```bash
     cd "/d/code/Additional Full-stack Development of Personal blogs" && grep -rn "isValidationError" frontend/src
     ```
     ```powershell
     cd "D:\code\Additional Full-stack Development of Personal blogs"; Get-ChildItem -Path frontend\src -Recurse -Include *.vue,*.js | Select-String -Pattern "isValidationError"
     ```
  2. `.gitkeep` 是否只剩 3 处（其中 `config/.gitkeep` 为残留）：

     ```bash
     cd "/d/code/Additional Full-stack Development of Personal blogs" && find . -name ".gitkeep" -not -path "./node_modules/*" -not -path "./frontend/node_modules/*" -not -path "./backend/target/*"
     ```
     ```powershell
     cd "D:\code\Additional Full-stack Development of Personal blogs"; Get-ChildItem -Recurse -Force -Filter ".gitkeep" | Where-Object { $_.FullName -notmatch "node_modules|target" } | Select-Object FullName
     ```
- **风险提示**：本层未发现高危问题；3 处校验差异均为"松紧不一致"而非"校验缺失"，后端始终是最终防线。
