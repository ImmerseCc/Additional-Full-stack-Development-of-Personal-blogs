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

## 待记录的观察项（尚未构成报错）

| 观察 | 说明 | 状态 |
|---|---|---|
| Git 换行符提示 | `git add` 提示 `warning: LF will be replaced by CRLF ...` | **已处理**：新增 `.gitattributes`（`* text=auto eol=lf`；`.cmd/.bat/.ps1` 用 CRLF；`mvnw`/`*.sh` 用 LF） |
| 项目路径含空格 | 工作目录为 `D:\code\Additional Full-stack Development of Personal blogs` | 未处理；目前未出现异常 |
| 终端中文乱码 | AI 侧 Git Bash 输出中文提示时出现乱码（如"关闭后请求"变成了乱码） | 未处理；控制台代码页问题，不是项目问题 |
| IPv6 地址不再可用 | 修复后 `http://[::1]:5173` 返回 000（预期） | 无需处理；用 `localhost` 或 `127.0.0.1` 均可 |
| Git Bash 的 `wc -m` 按**字节**计数 | locale 自检：`printf '%s' '中文测试' \| wc -m` 输出 12（字节）而不是 4（字符） | 已绕开：中文长度校验改为按**码点**核对的临时程序（阶段 2 批 2b 实测"摘要截取 120 字"时使用，验证后已删除） |
| 后台任务有 10 分钟上限，超时只杀包装进程 | 供作者验证 Swagger UI 而启动的后端后台任务在 600 秒后超时被终止，但派生 JVM（PID 24680）**继续存活并正常服务**（`/api/health` 与 `/swagger-ui/index.html` 均 200） | 无需处理；再次印证遗留项 10。后续要给作者长时间演示，应让后端跑在**作者自己的终端**里（或让 AI 用不设超时的方式启动），停服务时按 PID `taskkill` |
| 前端 dev server 同样会残留派生的 node 进程 | 阶段 3 验证中四次复现：`TaskStop` 杀掉包装进程后，node 仍监听 `127.0.0.1:5173`（PID 18788 / 29044 / 34536 / 26488） | 已处理流程化：每次验证结束用 `netstat -ano \| grep ":5173" \| grep -i listening` 取 PID 后 `taskkill //PID <pid> //F`，并在日志中记录 "5173/8080 均已释放" |
| 内置浏览器在**触摸仿真**下合成点击不送达页面 | 阶段 3 批 3 用移动设备模式（`responsive` 375×667，`mobile: true, touch: true`）验证时，点击汉堡按钮与主题按钮均无任何事件，但工具仍返回 `outcome: clicked`；键盘 `Tab`/`Enter` 路径正常 | 无需处理（工具限制，非项目缺陷）。**后续遇到移动端交互验证**：① 优先用键盘路径；② 需要点按时切回桌面设备模式，或交由作者在真实浏览器 / 真机点按验收 |

---

## 当前状态

- 已记录真实报错：**5 条**（1：环境类、非阻塞、无需修复；2：已修复并实测通过；3：使用 / 环境类，已修复并**双方**实测通过；4：工具 / 编码类，已处理；5：使用类——后端未运行，已修复，作者侧复验待反馈）
- 项目代码层面的报错：**1 条已处理**（报错记录 4 暴露的"查询串解码失败被兜底成 50000"属项目代码改进项，已在 `GlobalExceptionHandler` 修正为 40002）
- 交付要求"至少 1 次真实报错或调试过程"：**已满足**（第 2、3、4、5 条均包含完整闭环：报错原文 → 定位 → 修复 → 实测验证）
