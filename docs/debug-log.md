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

## 待记录的观察项（尚未构成报错）

| 观察 | 说明 | 状态 |
|---|---|---|
| Git 换行符提示 | `git add` 时提示 `warning: LF will be replaced by CRLF the next time Git touches it`（本机 `core.autocrlf` 默认行为） | 未处理；不影响功能。如需统一换行，可加 `.gitattributes`（需作者同意新增文件） |
| 项目路径含空格 | 工作目录为 `D:\code\Additional Full-stack Development of Personal blogs` | 未处理；目前未出现异常 |

---

## 当前状态

- 已记录真实报错：**1 条**（报错记录 1，环境类、非阻塞）
- 项目代码层面的报错：**尚未发生**（业务代码还没开始写，阶段 2 起才可能产生）
- 交付要求"至少 1 次真实报错或调试过程"：**已满足**（若后续出现代码类报错，继续按模板追加）
