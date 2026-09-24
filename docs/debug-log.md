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

## 待记录的观察项（尚未构成报错）

| 观察 | 说明 | 状态 |
|---|---|---|
| Git 换行符提示 | `git add` 提示 `warning: LF will be replaced by CRLF ...` | **已处理**：新增 `.gitattributes`（`* text=auto eol=lf`；`.cmd/.bat/.ps1` 用 CRLF；`mvnw`/`*.sh` 用 LF） |
| 项目路径含空格 | 工作目录为 `D:\code\Additional Full-stack Development of Personal blogs` | 未处理；目前未出现异常 |
| 终端中文乱码 | AI 侧 Git Bash 输出中文提示时出现乱码（如"关闭后请求"变成了乱码） | 未处理；控制台代码页问题，不是项目问题 |
| IPv6 地址不再可用 | 修复后 `http://[::1]:5173` 返回 000（预期） | 无需处理；用 `localhost` 或 `127.0.0.1` 均可 |

---

## 当前状态

- 已记录真实报错：**2 条**（报错记录 1：环境类、非阻塞、无需修复；报错记录 2：已修复并实测通过）
- 项目代码层面的报错：尚未发生（业务代码从阶段 2 开始写）
- 交付要求"至少 1 次真实报错或调试过程"：**已满足**（第 2 条包含完整闭环：报错原文 → 定位 → 修复 → 实测验证）
