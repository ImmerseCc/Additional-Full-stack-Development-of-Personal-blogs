# 报错与调试记录

> **本文件是报错记录专档**，与 `docs/ai-log.md` 的"报错记录"章节内容一致；如有修订两处同步更新。
> 记录规则（来自项目要求）：
> - 只记录**真实发生**的报错，禁止编造；作者提供的报错信息不完整时，先向作者索要补充（命令、完整堆栈、复现步骤），不靠猜补齐；
> - "修复方案"必须写清楚改了哪个文件、哪一段逻辑；
> - 结果若未反馈，一律写"待我验证"。

---

## 记录模板（复制使用）

```markdown
## 报错记录 X：问题简述
- 报错原文：
- 运行命令：
- 相关代码或文件：
- 定位过程：
- 修复方案：
- 修复后验证：
- 最终结果：
```

---

## 报错记录 1：scoop 自更新失败（github.com 连接被重置）——环境类，非阻塞

- **报错原文**：

  ```
  Updating Scoop...
  fatal: unable to access 'https://github.com/ScoopInstaller/Scoop/': Recv failure: Connection was reset
  Remove-Item : 找不到路径"D:\Scoop\apps\scoop\new"，因为该路径不存在。
      + CategoryInfo          : ObjectNotFound: (D:\Scoop\apps\scoop\new:String) [Remove-Item], ItemNotFoundException
      + FullyQualifiedErrorId : PathNotFound,Microsoft.PowerShell.Commands.RemoveItemCommand
  Scoop download failed. If this appears several times, try removing SCOOP_REPO by 'scoop config rm SCOOP_REPO'
  ```

  > 说明：终端中中文提示部分显示为乱码（控制台代码页问题），此处按含义还原；英文行原样保留。

- **运行命令**：`scoop install nodejs-lts`（在 Kimi Code 的 Git Bash 环境中调用 scoop shim；后台任务 ID `bash-yxv8yghd`，退出码 0）
- **相关代码或文件**：与项目代码无关；涉及 Scoop 自身脚本 `D:\Scoop\apps\scoop\current\libexec\scoop-update.ps1:88`
- **定位过程**：
  1. 观察输出顺序：失败出现在安装开始之前的 `Updating Scoop...`（Scoop 自更新）阶段，而不是安装阶段；
  2. 失败后流程继续，输出 `Installing 'nodejs-lts' (24.21.0) [64bit] from 'main' bucket`，随后 `Checking hash of node-v24.21.0-win-x64.7z ... ok.`、`Extracting ... done.`、`'nodejs-lts' (24.21.0) was installed successfully!`；
  3. 判断：本机访问 `github.com` 被重置（与 scoop 仓库源不可达一致），导致自更新失败；下载走的是 Node 官方分发（aria2 多线程），不受影响。
- **修复方案**：**未做修复**（无需修复，目标软件已安装成功）。若后续 Scoop 自更新持续失败并影响使用，可选：`scoop config rm SCOOP_REPO`、`scoop config aria2-enabled false`。以上命令**均未执行**。
- **修复后验证**：AI 实际执行 `node -v` → `v24.21.0`；`npm -v` → `11.19.0`（均输出成功）。
- **最终结果**：非阻塞。Node.js LTS 24.21.0 安装成功；Scoop 自更新失败保持未处理状态（环境层，不影响本项目构建与运行）。

---

## 报错记录 2：浏览器访问 http://127.0.0.1:5173 被拒绝（Windows 下 Vite 只绑定 IPv6）

- **报错原文**：作者原话"在验证时，localhost 拒绝了我的链接"，随后补充"第一个打不开，另两个可以，未开代理"。作者提供的开发服务器输出：

  ```
  VITE v8.3.0  ready in 220 ms

    ➜  Local:   http://localhost:5173/
    ➜  Network: use --host to expose
    ➜  press h + enter to show help
  ```

  （作者未提供浏览器错误码原文，如 `ERR_CONNECTION_REFUSED`）
- **运行命令**：作者终端 `cd frontend && npm run dev`；AI 侧诊断命令 `netstat -ano | grep ":5173"`、`curl -s -o /dev/null -w "%{http_code}" http://localhost:5173/`（以及 `127.0.0.1`、`[::1]` 两个地址）。
- **相关代码或文件**：`frontend/vite.config.js` 的 `server` 配置（`port: 5173`、`strictPort: false`，原先**未设置 `host`**）；`frontend/package.json` 的 `"dev": "vite"`。
- **定位过程**：
  1. AI 首次探测（作者第一次报错时）：`netstat -ano | grep LISTENING | grep :5173` 无输出、无 node 进程、三地址 curl 全为 `HTTP 000` → 判定"当时根本没有服务在监听"；
  2. 作者随后提供实测：`TCP [::1]:5173 [::]:0 LISTENING 31236`，`localhost -> 200`、`127.0.0.1 -> 000`、`[::1] -> 200`，浏览器侧"127.0.0.1 打不开，另外两个可以" → 判定为 **Vite 只绑定 IPv6 `::1`、未绑定 IPv4**；
  3. 排除代理因素：作者确认未开代理；注册表 `HKCU\...\Internet Settings` 显示 `ProxyEnable = 0x0`（系统代理关闭）。
- **修复方案**：经作者在三个选项中确认采用 `host: '127.0.0.1'`，修改 `frontend/vite.config.js` 的 `server` 块，新增一行 `host: '127.0.0.1'` 并加两行注释说明原因；**未采用** `host: true`（会监听所有网卡、把开发服务器暴露到 10.18.x.x 所在网络）。改动只此一行，其余配置未动。
- **修复后验证**（AI 实测，真实输出）：编辑配置文件后，作者终端内的 Vite 自动重启（进程号仍为 31236），`netstat` 显示 `TCP 127.0.0.1:5173 LISTENING 31236`；三地址实测 `http://127.0.0.1:5173/ -> 200`、`http://localhost:5173/ -> 200`、`http://[::1]:5173/ -> 000`（预期变化）；`curl http://127.0.0.1:5173/` 返回页面含 `<title>个人博客 · VibeCoding</title>`；AI 自起的测试实例（因 5173 被占用自动切到 5174）已 `kill`，`netstat` 确认 5174 无残留监听。
- **最终结果**：**已修复**（AI 侧实测通过；作者浏览器最终复验未反馈，标记为"待我验证"）。根因一句话：Windows 上 Vite 未显式设置 `host` 时可能只绑定 IPv6 `::1`，浏览器走 IPv4 时连接被拒绝；首次报错时还叠加了"服务未在运行"这一因素。

---

## 报错记录 3：`cd: backend: No such file or directory` + `curl: (7) Failed to connect to localhost:8080`（工作目录错误，非代码缺陷）

- **报错原文**（作者在阶段 2 批 1 的验证环节报告，逐字照录）：

  ```
  第一项测试报错：bash: cd: backend: No such file or directory
  第二项测试报错：bash: cd: backend: No such file or directory
  curl: (7) Failed to connect to localhost:8080 after 2203 ms: Could not connect to server
  ```

  > 作者已确认：当时终端位于**默认目录**（未先进入项目根目录），作者原话"这是我的问题"。
- **运行命令**：作者按 AI 交付的验证命令执行 `cd backend && ./mvnw -B -ntp compile`、启动后端的那条命令，以及 `curl -i http://localhost:8080/api/not-exist`
- **相关代码或文件**：与项目代码无关；问题出在**执行命令时终端所在的工作目录**（相对路径 `cd backend` 的前提是终端已在项目根目录）
- **定位过程**：
  1. 两条报错都以 `bash: cd:` 开头，说明是 Git Bash 在解释 `cd backend`；该写法是**相对路径**，仅当当前目录就是项目根目录时才成立；
  2. AI 在本机复现：先 `cd "$HOME"`（`/c/Users/19032`，即新开终端的默认位置），再执行 `cd backend` → 输出 `/usr/bin/bash: line 1: cd: backend: No such file or directory`，与作者报告的报错**完全一致**；
  3. `curl: (7)` 是**连带结果**：前一条命令因 `cd` 失败而中断（`&&` 短路），后端从未启动，因此 8080 无服务可连；不是端口占用、也不是代理问题；
  4. 排除项目侧原因：项目根目录确实存在且包含 `backend/`，同一编译命令在正确目录下已实测通过（阶段 2 批 1：`BUILD SUCCESS`）。
- **修复方案**：不涉及任何代码修改，改为**绝对路径**进入子目录（或在项目根目录下执行相对路径命令）：

  ```bash
  # Git Bash：路径含空格，必须加引号
  cd "/d/code/Additional Full-stack Development of Personal blogs/backend" && ./mvnw -B -ntp compile
  ```

  ```powershell
  # Windows PowerShell / CMD
  cd "D:\code\Additional Full-stack Development of Personal blogs\backend"; .\mvnw.cmd -B -ntp compile
  ```

  同时在 `README.md` 的《常见问题排查》表中新增一行，把"终端不在项目根目录"列为排查项，避免重复踩坑。
- **修复后验证**：
  1. AI 侧实测：在 `$HOME`（非项目目录）下执行绝对路径命令 → 输出 `/d/code/Additional Full-stack Development of Personal blogs/backend` 与 `BUILD SUCCESS`（`Total time: 1.074 s`）；
  2. 作者侧复验（作者提供的真实输出）：终端 A 用绝对路径启动 → `Tomcat initialized with port 8080`、`HikariPool-1 - Start completed`、`Started BlogApplication in 1.663 seconds`（PID 29924，Java 26.0.2.1）；终端 B 的 `curl.exe -i http://localhost:8080/api/not-exist` **输出正常**，同一时刻终端 A 出现 `GlobalExceptionHandler : 接口不存在：api/not-exist`（DEBUG 日志），证明请求确实到达后端并被统一异常处理捕获。
- **最终结果**：**已修复并双方实测通过**。根因一句话：终端不在项目根目录时，`cd backend` 必然失败，进而连带产生"后端未启动 + curl 连不上 8080"的现象；项目代码无缺陷。作者结论："当时终端在默认目录，这是我的问题"。

---

## 报错记录 4：`curl` 传中文参数导致查询串解码失败 → 50000（工具 / 编码类，非项目缺陷）

- **报错原文**（AI 在阶段 2 批 2a 实测时捕获，服务端日志原文节选）：

  ```
  ERROR c.e.blog.common.GlobalExceptionHandler : 服务端未预期异常

  org.apache.tomcat.util.http.InvalidParameterException: Character decoding failed.
  Parameter [tags] with value [��Ŀ��־,Vue] has been ignored. Note that the name and value
  quoted here may be corrupted due to the failed decoding.
      at org.apache.tomcat.util.http.Parameters.processParameters(Parameters.java:433)
      ...（51 行调用栈略）
  Caused by: java.nio.charset.MalformedInputException: Input length = 1
      at java.base/java.nio.charset.CoderResult.throwException(CoderResult.java:279)
  ```

  接口响应：`{"code":50000,"message":"服务端未预期异常","data":null}`（HTTP 500）
- **运行命令**：`curl -s --get --data-urlencode "tags=项目日志,Vue" --data-urlencode "tagMode=or" http://localhost:8080/api/articles`
- **相关代码或文件**：`backend/src/main/java/com/example/blog/common/GlobalExceptionHandler.java`（当时缺少针对"查询串解码失败"的处理器，异常落到兜底的 50000）；与 `ArticleRepository` / `ArticleService` 的过滤逻辑**无关**（见定位过程第 1 步）
- **定位过程**：
  1. 先排除服务端过滤逻辑：把同样的两个标签改用**预编码 UTF-8 百分号串**（纯 ASCII）请求 → `?tags=%E9%A1%B9%E7%9B%AE%E6%97%A5%E5%BF%97,Vue&tagMode=or` → 返回 `"total":3`，说明 SQL 与标签 and/or 逻辑正确；
  2. 用 `curl -v` 打印实际发出的请求行 → `GET /api/articles?tags=%CF%EE%C4%BF%C8%D5%D6%BE%2CVue&tagMode=or`，其中 `%CF%EE%C4%BF%C8%D5%D6%BE` 正是 **GBK 编码**的"项目日志"（项=CF EE、目=C4 BF、日=C8 D5、志=D6 BE），而 Tomcat 按 UTF-8 解码 → `MalformedInputException`；
  3. 用 `printf '%s' "项目日志" | xxd` 检查同一条命令里的字节 → `e9a1 b9e7 9bae e697 a5e5 bf97`（**合法 UTF-8**）。两者的差异来自可执行文件类型：`/usr/bin/printf` 是 msys2 运行时程序（参数按 UTF-8 传递），而 `which curl` → `/mingw64/bin/curl`（`PE32+ executable for MS Windows`），**命令行参数按系统 ANSI 代码页转换**（简体中文 Windows 为 CP936/GBK）；
  4. 因此结论是：这是**测试命令侧的编码问题**；浏览器与前端（fetch/axios）发出的中文参数一律是 UTF-8 百分号编码，不受影响。
- **修复方案**（两部分）：
  1. **服务端（代码改动）**：在 `GlobalExceptionHandler` 新增 `InvalidParameterException` 处理器，把"查询串编码非法"从兜底 `50000` 改为契约中的 `40002`（参数格式错误），消息为"查询参数编码非法，请使用 UTF-8 百分号编码"；同时把该类 javadoc 里"请求方法不支持（405）"的表述与实际行为（HTTP 400 + 40002）对齐；
  2. **测试方法（不改代码）**：本机 Git Bash 用 `curl` 测中文参数时，改用预编码 UTF-8 百分号串（如 `tags=%E9%A1%B9%E7%9B%AE%E6%97%A5%E5%BF%97,Vue`），或改用 PowerShell / 浏览器验证。
- **修复后验证**（AI 实测，真实输出）：重新编译并重启后，重跑同一条 GBK 命令 → `HTTP/1.1 400` + `{"code":40002,"message":"查询参数编码非法，请使用 UTF-8 百分号编码","data":null}`；预编码串复测 `tagMode=or → "total":3`、`tagMode=and → "total":1`，均符合预期。
- **最终结果**：**已处理**（错误码更准确 + 测试方法已沉淀）。根因一句话：mingw64 版 curl 会把命令行里的中文按系统 ANSI 代码页（GBK）做百分号编码，服务端按 UTF-8 解码必然失败。**作者侧复验未反馈，标记为"待我验证"。**

---

## 报错记录 5：浏览器打不开 http://localhost:8080/swagger-ui/index.html（后端未运行）

- **报错原文**：作者原话"http://localhost:8080/swagger-ui/index.html 拒绝了我的链接"（未提供浏览器错误码原文，如 `ERR_CONNECTION_REFUSED`）
- **运行命令**：作者在浏览器访问该地址；AI 侧诊断命令 `netstat -ano | grep ":8080"`、`tasklist //FI "IMAGENAME eq java.exe"`、`curl -o /dev/null -w "%{http_code}" http://localhost:8080/api/health`
- **相关代码或文件**：与项目代码无关。背景：阶段 2 每批实测结束后，AI 都会按惯例停掉后端并清理派生 JVM（避免占住 8080），因此"浏览器访问时服务并不在运行"属于常态
- **定位过程**：
  1. `netstat -ano | grep ":8080" | grep -i listening` → **无输出**：8080 上没有任何进程在监听；
  2. `tasklist //FI "IMAGENAME eq java.exe"` → `没有运行的任务匹配指定标准`：机器上没有任何 JVM 在跑；
  3. `curl http://localhost:8080/api/health` → `HTTP 000`（curl 退出码 7，连接被拒绝）→ 判定为"**服务根本没启动**"，而不是代理、IPv6 或端口占用问题；
  4. 交叉确认：阶段 2 批 3 实测收尾时执行过 `taskkill //PID 30228 //F`，此后 8080 空闲至今。
- **修复方案**：重新启动后端即可（Swagger UI 与 `/api/*` 只在后端运行期间可访问）：

  ```bash
  # Git Bash
  cd "/d/code/Additional Full-stack Development of Personal blogs/backend" && ./mvnw -B -ntp spring-boot:run
  ```

  ```powershell
  # Windows PowerShell / CMD
  cd "D:\code\Additional Full-stack Development of Personal blogs\backend"; .\mvnw.cmd -B -ntp spring-boot:run
  ```

  并在 `README.md`《常见问题排查》表中新增一行，把"后端地址打不开"直接指向"后端未启动"。
- **修复后验证**（AI 实测，真实输出）：重启后 `GET /api/health → 200`、`GET /swagger-ui/index.html → 200`（页面标题 `<title>Swagger UI</title>`）、`GET /v3/api-docs → 200`；`netstat` 显示 `0.0.0.0:8080 LISTENING`（PID 24680）。**作者侧浏览器复验未反馈，标记为"待我验证"。**
- **最终结果**：**已修复**（AI 侧实测通过）。根因一句话：**后端没在运行**。值得记一笔的是，"浏览器拒绝连接"在本项目已出现三次（报错记录 2 = Vite 只绑定 IPv6、报错记录 3 = 终端工作目录错误、本条 = 后端未启动），共同经验是：**排查顺序的第一步永远是"确认服务是否真的在监听"**。

---

## 报错记录 6：种子数据的固定 tag ID 与历史残留行冲突，导致「前端」标签未建成、文章关联错位（数据类，已通过重置数据库修复）

- **现象原文**：`data.sql` 与后端启动**都没有任何报错**。问题是在**接口实测**中发现的：`GET /api/tags` 里出现了阶段 2 的测试残留标签 `级联测试标签(4)`，而种子文件里新写的「前端」标签**根本不存在**；`GET /api/articles?size=20&status=ALL` 显示文章 4、5、6、11 的标签是 `级联测试标签`。
- **运行命令**：`cd backend && ./mvnw -B -ntp spring-boot:run`，随后用 `node -e`（内置 `fetch`）查 `/api/tags` 与 `/api/articles?size=20&status=ALL`（避免 Git Bash 的 `curl` 中文编码问题）
- **相关代码或文件**：`backend/src/main/resources/data.sql`（阶段 4 批 0 把标签从 4 个扩到 8 个，使用固定 ID + `INSERT OR IGNORE`）
- **定位过程**：
  1. `/api/tags` 输出 `id=3 Spring Boot(4)`、`id=5 级联测试标签(4)`、`id=6 CSS(1)`、`id=7 后端(4)`、`id=8 Markdown(1)` → 说明 id 6–8 是本次新建的，**id 5 被旧数据占住了**；
  2. 交叉核对 `docs/current-state.md` 环境事实：阶段 2 实测残留过一条「级联测试标签」（当时计数 0）→ 确认 `tag` 表里 `id=5` 的名字就是它；
  3. 根因：种子文件用**固定 ID** 写 `(5, '前端')`，而 SQLite 的 `INSERT OR IGNORE` 遇到主键冲突是**整行跳过**（不是更新），于是「前端」从未被创建；紧接着的 `INSERT OR IGNORE INTO article_tag (article_id, tag_id) VALUES (4,5) …` 虽然成功，却把这 4 条关联指向了那个残留标签 —— 数据被**静默错位**，全程零报错；
  4. 影响面判定：`blog.db` 已 gitignore，**全新克隆（任何人 clone 后首次启动）不受影响**（新库没有 id=5 的历史数据，种子会正确生成 8 个标签）；**只有本机开发库**受影响。
- **修复方案**：报请作者裁决后，采用 `AGENTS.md` 第 8 节的**标准重置路径**（作者确认为"重置数据库"）：停服 → 删除 `backend/data/blog.db`（当时无 `-wal` / `-shm`）→ 重启 → `schema.sql` + `data.sql` 自动重建。
- **修复后验证**（AI 实测，真实输出）：
  - `GET /api/tags` → 8 个：`3=Spring Boot(4) 5=前端(4) 7=后端(4) 4=SQLite(3) 2=Vue(3) 1=项目日志(3) 6=CSS(1) 8=Markdown(1)`，`id=5` 已是「前端」；
  - `GET /api/articles?size=20&status=ALL` → `total=12`、封面 12/12 非空、文章 4 / 5 / 6 / 11 的标签正确为 `前端`；
  - `GET /api/articles?tags=前端` → `total=4`、`ids=11,6,5,4`；
  - `GET /api/articles?size=5&page=2` → 5 条、`ids=7,6,5,4,3`；
  - **幂等复验**：再次停服 → 重启（`data.sql` 第二次执行）→ `tags=8`、`articles total=12`、封面 12/12、无重复 id；
  - 契约长度核对：12 篇的 `title` / `summary` / `content` 全部落在 1–100 / 0–200 / 1–50000 内。
- **最终结果**：**已修复**。一句话教训：**固定 ID 的幂等种子数据，遇到"历史遗留行占用同一个 ID"时会静默错位**——`INSERT OR IGNORE` 忽略冲突本就是设计如此，所以扩写种子后必须核对**真实数据**（本次正是靠接口实测、而不是靠"启动没报错"发现的）。

---

## 报错记录 7：`spring-boot:run` 启动失败——`Port 8080 was already in use`（进程 / 环境类，非代码缺陷）

- **报错原文**（后端启动日志，逐字摘录）：

  ```
  ***************************
  APPLICATION FAILED TO START
  ***************************

  Description:

  Web server failed to start. Port 8080 was already in use.

  Action:

  Identify and stop the process that's listening on port 8080 or configure this application to listen on another port.

  [ERROR] Failed to execute goal org.springframework.boot:spring-boot-maven-plugin:4.1.1:run (default-cli) on project blog-backend: Process terminated with exit code: 1
  ```

- **运行命令**：`cd backend && ./mvnw -B -ntp spring-boot:run`（阶段 4 批 1 联调验证前启动后端）
- **相关代码或文件**：与项目代码无关（端口与进程层面）。相关配置：`backend/src/main/resources/application.yml` 的 `server.port: 8080`、`frontend/vite.config.js` 的代理 target
- **定位过程**：
  1. 启动前按惯例查过端口（当时 `5173/8080 均已释放`），故失败原因不是"我没清理干净"；
  2. 失败后立刻取证：`netstat -ano | grep ":8080" | grep -i listening` → `LISTENING 23456`；`tasklist //FI "IMAGENAME eq java.exe"` → `17524` 与 `23456`；`curl http://localhost:8080/api/health → 200` —— **8080 上确有一个健康实例在服务**；
  3. 用 PowerShell 取进程启动时间：`17524 → 22:01:18`、`23456 → 22:01:20`（wrapper 与 fork 出的应用 JVM 相差 2 秒，正是 `spring-boot:run` 的典型形态），而**我这次启动是 22:04:21** → 判定"先到者占用端口，我的实例被顶掉"；
  4. `TaskList` 确认本会话只有一个活着的后台任务（前端 dev server），**没有遗留的后端任务** → 这两个 JVM **不是本会话启动的**（推测是作者在本机终端里自己起的验证实例）；AI 对它们**未执行任何 kill**；
  5. 约 1 分钟后它们自行消失（`netstat` 显示 8080 空闲、java 进程为 0），期间 AI 同样未执行停止命令 —— **来源与消失原因均未确认，按事实记录，不推测**；
  6. 附带现象：上面的实例消失后，前端经 Vite 代理的自测页**全部返回 `HTTP 502`**，前端 `ApiError` 如实报出 `status=502` —— 与报错记录 5 同源（"后端不在监听"），也侧面验证了错误归一化按预期工作。
- **修复方案**：确认 8080 已空闲后，重新启动自己的实例即可（**无任何代码改动**）：

  ```bash
  # Git Bash
  cd "/d/code/Additional Full-stack Development of Personal blogs/backend" && ./mvnw -B -ntp spring-boot:run
  ```

  ```powershell
  # Windows PowerShell / CMD
  cd "D:\code\Additional Full-stack Development of Personal blogs\backend"; .\mvnw.cmd -B -ntp spring-boot:run
  ```

- **修复后验证**（AI 实测，真实输出）：重启后 `GET http://localhost:8080/api/health → 200`，经 5173 代理 `GET /api/health → 200`；随后 14 项接口层用例 **14/14 通过**。
- **最终结果**：**已恢复**。两条可复用经验：① **启动后端前先查 8080**——"端口被占用"先确认是不是**已有实例在正常服务**，不要一上来就改端口；② 前端经代理拿 **502** 时，先怀疑后端不在监听（与报错记录 5 同一类判断）。

---

## 报错记录 8：批 4 自查发现并修复的两个前端缺陷（进场动画把首屏内容藏起来 / 详情页目录永远为空）

- **性质说明**：这两个缺陷都是批 4 新写的代码自己引入的，**没有对外报错、也没有让构建失败**——是浏览器实测把它们揪出来的。记录在此，因为它们属于"能编译、能跑、但用户看到的是坏页面"那一类，最值得留档。
- **缺陷 A：`v-reveal` 把首屏内容永久藏在 `opacity: 0`**
  - **现象**：详情页加载完成后，除标题与标签外，**封面、正文、返回链接全部不可见**（截图里页面下半部分是空白）；列表页同样看不到卡片。
  - **运行/复现命令**：`cd backend && ./mvnw -B -ntp spring-boot:run` + `cd frontend && npm run dev`，然后访问 `http://localhost:5173/articles/7`
  - **相关代码**：`frontend/src/utils/reveal.js`、`frontend/src/styles/base.css` 的 `.reveal` 规则
  - **定位过程**：① 截图显示内容缺失，但 DOM 里元素存在 → 判断是 CSS 隐藏态；② 读代码确认 `v-reveal` 会先加 `.reveal`（`opacity: 0`），等 `IntersectionObserver` 回调才加 `.is-revealed`；③ 用一个临时页把指令行为打出来：`[tick 1..6] #in-view revealed=false opacity=0`——**4 秒以上仍未投递**，而同一页里"被 `scrollIntoView` 滚进视口"的元素随后 `revealed=true`；④ 结论：后台标签页/未渲染场景下 IO 首次投递会被大幅推迟，而我把"内容可见性"挂在了这个回调上 —— **这是设计问题，不是环境问题**。
  - **修复方案**：`mounted` 里先用 `getBoundingClientRect()` 判断是否已在视口内——在视口内就**不交给观察器**，直接 20ms 后加 `.is-revealed`（用 `setTimeout` 而非 `requestAnimationFrame`，后者在后台标签页同样不触发）；只有"挂载时在视口外"的元素才用 `IntersectionObserver`（决策 AF）。
  - **修复后验证**：详情页封面 / 正文 / 返回链接全部正常显示（截图）；列表页 12 张卡片全部可见（截图）；下方卡片滚入视口后正常显现（截图）。
- **缺陷 B：详情页目录（TOC）永远不出现**
  - **现象**：右侧目录区一直为空，`/articles/7` 的 4 个小节一个都没列出（无控制台报错）。
  - **定位过程**：读 `ArticleDetailView.vue` 的 `load()`——`collectHeadings()` 写在 `try` 块里，执行时 `loading` 仍为 `true`；此时模板渲染的是**加载态骨架分支**，`bodyRoot` 还是 `null`，函数直接 `return`，而且**再没有第二次机会**。
  - **修复方案**：把"`await nextTick()` → 取目录 → 测一次高亮"整体移到 `finally` 之后（此时 `loading=false`、正文已真正渲染），并在错误分支里把 `headings` 清空。
  - **修复后验证**：目录列出 4 项（不用 ORM 的理由 / 分层怎么写 / 分页查询的一个坑 / 代价）；滚动时高亮从第 1 项跟随到第 4 项（滚到底时兜底高亮最后一节）；点「不用 ORM 的理由」精确滚到该标题并停在吸顶页头下方；375px 下目录整块隐藏。
- **最终结果**：**两个缺陷均已修复并在真实浏览器复验通过**。两条可复用经验：① **动画/装饰效果不能成为内容可见性的前提**——先保证"完全不跑动画也可见"，再叠加动画；② **取渲染结果（DOM 内容、尺寸）必须等目标分支真正挂载**，拿不准就把取值动作放到 `finally` 之后并 `await nextTick()`。

---

## 报错记录 9：阶段 5 批 5 自查发现的面板显示竞态（"当前占用的键"短暂不准）

- **性质说明**：与报错记录 8 同类 —— 能编译、能跑、主要功能也对，但界面上有一处显示与实际不符，是浏览器实测把它揪出来的。
- **现象**：点「确认重置」后，`/about` 的「本地数据」面板里「当前占用的键」显示"（无）"，而实际上主题 store 会在下一个 tick 把默认值写回 `blog:theme`（存储里确实有 1 个键）。
- **运行 / 复现命令**：`cd backend && ./mvnw -B -ntp spring-boot:run` + `cd frontend && npm run dev`，访问 `http://localhost:5173/about`，依次点「重置本地数据」→「确认重置」。
- **相关代码**：`frontend/src/components/LocalDataPanel.vue` 的 `reset()`
- **定位过程**：读代码确认 `clearAll()` → `theme.setMode('system')` → 立刻 `buildSnapshot()`；Vue 的 `watch` 回调默认在**下一个 tick** 才执行，取快照时主题的默认值还没落盘 —— 是**读写顺序**问题，不是存储问题。同一轮还发现：面板的键列表是一次性快照，用户在页面上点赞 / 换主题后不会自动更新。
- **修复方案**：① `reset()` 改为 `async`，在 `await nextTick()` 之后重建快照；② 新增 `flush: 'post'` 的 `watch([theme.mode, likes.count, myComments.count])` → 重建快照，让面板**实时反映**本机数据变化。
- **修复后验证**：点主题按钮（跟随系统 → 亮色）→ 面板「主题偏好」立即变「亮色」（无需刷新）；再重置 → 「当前占用的键」准确显示 `theme`、主题回「跟随系统」；服务端数据不受影响（`article 5 / 6` 的 `likeCount=0` 复核通过）。
- **最终结果**：**已修复并在真实浏览器复验通过**。可复用经验：**读写同一份"非响应式"外部状态（localStorage、DOM 尺寸等）时，要么等一拍（`await nextTick()`）再读，要么用 `flush: 'post'` 的 watcher 驱动更新**。

---

## 待记录的观察项（尚未构成报错）

| 观察 | 说明 | 状态 |
|---|---|---|
| Git 换行符提示 | `git add` 提示 `warning: LF will be replaced by CRLF ...` | **已处理**：新增 `.gitattributes`（`* text=auto eol=lf`；`.cmd/.bat/.ps1` 用 CRLF；`mvnw`/`*.sh` 用 LF） |
| 项目路径含空格 | 工作目录为 `D:\code\Additional Full-stack Development of Personal blogs` | 未处理；目前未出现异常 |
| 终端中文乱码 | AI 侧 Git Bash 输出中文提示时出现乱码（如"关闭后请求"变成了乱码） | 未处理；控制台代码页问题，不是项目问题。**阶段 6 批 0 复现**：用临时 Java 程序只读复核数据库时，中文标签同样打印为乱码（数值正常）→ 只读数值型结果，或给 `java` 追加 `-Dstdout.encoding=UTF-8` |
| IPv6 地址不再可用 | 修复后 `http://[::1]:5173` 返回 000（预期） | 无需处理；用 `localhost` 或 `127.0.0.1` 均可 |
| Git Bash 的 `wc -m` 按**字节**计数 | locale 自检：`printf '%s' '中文测试' \| wc -m` 输出 12（字节）而不是 4（字符） | 已绕开：中文长度校验改为按**码点**核对的临时程序（阶段 2 批 2b 实测"摘要截取 120 字"时使用，验证后已删除） |
| 后台任务有 10 分钟上限，超时只杀包装进程 | 供作者验证 Swagger UI 而启动的后端后台任务在 600 秒后超时被终止，但派生 JVM（PID 24680）**继续存活并正常服务**（`/api/health` 与 `/swagger-ui/index.html` 均 200）；**阶段 6 批 0 再次复现**：后端包装进程 600 秒超时被终止后，派生 JVM（PID 38900）仍监听 8080、`/api/health` 返回 200，前端派生 node（PID 36092）仍监听 127.0.0.1:5173 且页面 200 | 无需处理；再次印证遗留项 10。后续要给作者长时间演示，应让后端跑在**作者自己的终端**里（或让 AI 用不设超时的方式启动），停服务时按 PID `taskkill` |
| 前端 dev server 同样会残留派生的 node 进程 | 阶段 3 验证中四次复现：`TaskStop` 杀掉包装进程后，node 仍监听 `127.0.0.1:5173`（PID 18788 / 29044 / 34536 / 26488） | 已处理流程化：每次验证结束用 `netstat -ano \| grep ":5173" \| grep -i listening` 取 PID 后 `taskkill //PID <pid> //F`，并在日志中记录 "5173/8080 均已释放" |
| 内置浏览器的合成点击在部分元素上不送达 | 阶段 3 批 3 移动仿真（`responsive` 375×667，mobile + touch）下点汉堡按钮 / 主题按钮均无事件，但工具仍返回 `outcome: clicked`；**阶段 4 批 2 在桌面设备模式（desktop-1280，mobile: false）下点主题按钮同样不生效**，改用键盘 `Tab` + `Enter` 立刻成功（跟随系统 → 亮色 → 暗色）；同一批里列表页的分页按钮与错误态的「重试」按钮点按**是**生效的 | 无需处理（工具限制，非项目缺陷）。**验证交互时**：① 优先键盘路径；② 分页 / 重试这类普通按钮可先试 `page.element.click`，不生效就立刻换键盘；③ 移动端点按交作者真机验收 |
| 路由过渡中间帧读不到"带过渡的子树" | 阶段 4 批 2 用 `page.text.snapshot` / `page.elements.snapshot` 读页面时，若正好落在 `<Transition>` 的进出场中间帧，读到的 DOM 文字**只有页头**（卡片、分页、页脚全部读不到），`page.wait_for` 的文本条件也会一直超时（`WAIT_TIMEOUT`）；而同一时刻的**截图是正常的**（只是整体半透明） | 无需处理（采样时机问题，非项目缺陷）。**规避**：先 `page.visual.snapshot` 确认画面，再读结构；或等过渡结束（约 0.25s）后重读。批 2 曾据此误判"深链刷新后页面空白"，截图复核后确认正常 |
| 后台标签页里进场动画不推进 | 阶段 5 批 0 复验正文配图时实测：在**不可见**的标签页打开 `http://127.0.0.1:5173/articles/6`，页面结构已渲染、`document.title` 已更新为文章标题，但标题 / 封面 / 正文停在**半透明**（入场过渡未推进、`page.text.snapshot` 也读不到 main 里的文字）；`browser.switch_tab` 切到可见后**立刻恢复正常**。与报错记录 8 缺陷 A 同源（后台页 `requestAnimationFrame` / `IntersectionObserver` 被节流） | 无需处理（工具环境限制，非项目缺陷）。**规避**：验证动画 / 过渡类效果前先 `browser.switch_tab` 让标签页可见，或先截图强制渲染 |
| Toast 只活 2.6s，自动截图常晚于窗口 | 阶段 5 批 4 验证异常 Toast 时，两次"事后截图"都晚于 2.6s 自动消失（一轮模型往返 > 2.6s），只有 `page.wait_for` 在点击后 **108ms** 命中了完整文案 | 无需处理（采样时机问题，非项目缺陷）。**规避**：把"触发动作 + `page.wait_for`"放进**同一批**调用；需要截图留档时用 `duration: 0` 的长驻提示 |

---

## 当前状态

- 已记录真实报错：**9 条**（1：环境类、非阻塞、无需修复；2：已修复并实测通过；3：使用 / 环境类，已修复并**双方**实测通过；4：工具 / 编码类，已处理；5：使用类——后端未运行，已修复；6：**数据类——种子数据固定 tag ID 与历史残留行冲突，已按标准路径重置数据库修复并复验幂等**；7：**进程 / 环境类——8080 已被另一个实例占用导致后端启动失败，已恢复**；8：**前端自查类——批 4 的进场动画把首屏内容藏在 `opacity:0`、详情页目录永远为空，两个缺陷均已修复并复验**；9：**前端自查类（阶段 5 批 5）——本地数据面板"当前占用的键"读在主题默认值落盘之前，已用 `await nextTick()` + `flush: 'post'` watcher 修复并复验**）
- 项目代码层面的报错：**4 条已处理**（报错记录 4 暴露的"查询串解码失败被兜底成 50000"属项目代码改进项，已在 `GlobalExceptionHandler` 修正为 40002；报错记录 8 的 A / B 两个缺陷属该批新代码引入；报错记录 9 属阶段 5 批 5 新代码引入 —— 均已修复并复验）
- 交付要求"至少 1 次真实报错或调试过程"：**已满足**（第 2、3、4、5、6、7、8、9 条均包含完整闭环：报错原文 / 现象 → 定位 → 修复 → 实测验证）
