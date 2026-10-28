# 更新日志 / Changelog

本项目是 [ExcellentEnchants](https://github.com/nulli0n/ExcellentEnchants-spigot)（NightExpress，GPL-3.0）的 Paper 原生重构版。
所有变更均以 **ExcellentEnchants-Paper** 自身的版本号发布，第一个公开版本为 `1.0.0`。

---

## v1.0.0 · 首个公开版本

首个可运行版本：在 Paper 1.21.11 上完成全流程验证（81 个附魔加载、数据目录生成、零 ERROR）。

### 核心重构

- **移除 NightCore 前置依赖**：原本需要外部 NightCore 插件才能运行的引擎代码，现已内置为 `nightcore-shim` 模块并重写为原生 Paper 实现。插件现在**单文件即可运行**，不再要求服务器安装任何前置。
- **全面迁移到 Paper 插件体系**：`plugin.yml` → `paper-plugin.yml`；附魔注册改用 Paper 的 `RegistryEvents / LifecycleEvents`，附魔条目直接注册进**原版附魔注册表**。
- **引擎与脚本重定位**：内置的 `su.nightexpress.nightcore` 与 `org.tabooproject.fluxon` 在打包时统一重定位到 `su.nightexpress.excellentenchants.libs.*`，避免与其它插件冲突。

### Fluxon 脚本系统（重点）

- 引入 **Fluxon** JVM 脚本引擎（v1.6.31），**全部 81 个默认附魔改写为 `.fs` 脚本**，Java 附魔实现类与 `EnchantCatalog` 枚举条目全部删除。
- 脚本分两部分：上半部分是**元数据**（名称、等级上限、权重、稀有度、适用物品、互斥、村民交易、战利品、诅咒标记等），插件据此自动生成附魔定义与获取途径；下半部分是**行为**。
- 支持 **21 个行为钩子**：`on-attack`、`on-defend`、`on-mining`、`on-kill`、`on-passive`、`on-shoot`、`on-launch`、`on-hit`、`on-projectile-damage`、`on-drop`、`on-interact`、`on-fishing`、`on-inventory-death`、`on-click`、`on-item-damage`、`on-protection`、`on-death`、`on-resurrect`、`on-block-change`、`on-move`、`on-place`。
- `ctx` 提供大量助手方法（`chance()`、`mod()`、`breakBlock()`、`explodeBlocks()`、`smelt()`、`addDrop()`、`spawn()`、`skullItem()`、`charges()`、`addTickedBlock()` 等），同时允许直接调用 Bukkit 方法。
- 复杂附魔全部由脚本实现，例如：砍树（BFS 搜索原木/树叶）、矿脉（6 方向扩张）、隧道（视线方向 3×3）、爆破挖掘、精准刷怪笼、丝绸箱子、自动补种、斩首（62 种生物头颅皮肤）、火焰行者（岩浆转岩浆块并定时恢复）。
- 完整语法与 API 见 [`docs/SCRIPTING.md`](SCRIPTING.md)。

### 版本支持

- 支持区间收敛为 **Minecraft 1.21 – 26.3**：
  - 移除 1.19.4 / 1.20.x 以及 26.4+ 的版本条目；
  - **低于 1.21 的服务端会被主动拒绝启动**（控制台明确报错并停用插件，避免半残运行）；
  - 高于 26.3 的版本以"尽力兼容"模式加载并给出警告。
- 修复版本探测从未被调用的问题（`Version.detect()` 缺失会让版本判定完全失效）。

### Folia 支持

- `paper-plugin.yml` 声明 `folia-supported: true`。
- 插件不再实例化 `BukkitScheduler`（Folia 上不存在），改为按运行环境选择 **区域调度器 / 实体调度器**。
- 周期任务按所属区域分发：被动附魔按**实体**调度、定时方块恢复按**方块位置**调度、箭矢轨迹粒子按**箭矢实体**调度。
- 修复 `entity.getScheduler().run(null, ...)` 传入 null 插件（Folia 上 NPE）的问题。

### 构建修复

- 补齐 `API` / `Core` 对内部模块 `nightcore-shim` 的依赖（移除 NightCore 后编译链断裂）。
- `maven-shade-plugin` 3.2.4 → **3.5.3**，并移除固定的 ASM 9.2 覆盖配置：旧版 shade 内置 ASM 9.2 无法处理 Java 21 字节码（`Unsupported class file major version 65`）。

### 启动与本地化修复

- `ConfigCodecs` / `Arguments` 注册表改为**惰性初始化**：Paper bootstrapper 阶段就会读写配置，原先会抛 `not initialized yet`。
- 新增 `Software.loadDefault()`：移除 NightCore 后服务端桥接实现（`PaperBridge`）从未被加载，导致 `Config` 静态初始化直接崩溃。
- `Version.detect()` 空安全：bootstrapper 阶段 `Bukkit.getServer()` 尚为 null。
- **中文语言包修复**：语言按 JVM 语言码查找（`zh` / `pt`），而文件名为 `messages_cn.yml` / `messages_br.yml`，导致中文服**静默回退英文**。已重命名为 `messages_zh.yml` / `messages_pt.yml`，并合并了 Core 与引擎同名的 `messages_zh.yml`（shade 后互相覆盖，丢失一半翻译键）。
- "未安装 tooltip 提供者"由 `ERROR` 降级为 `WARN`（PacketEvents / ProtocolLib 本就是可选）。

### 验证

- 真实 **Paper 1.21.11 build 132** 服务端启动测试：

  ```
  [ExcellentEnchants] Loaded 81 scripted enchantment(s).
  [ExcellentEnchants] Reloaded 81 enchantment script(s).
  [ExcellentEnchants] Loaded 81 enchantments.
  [ExcellentEnchants] Plugin loaded in 320 ms!
  ```

  生成 `config.yml`、`distribution.yml`、`engine.yml`、`item_types.yml`、`enchants/`（81 个脚本）、`lang/lang_zh.yml`、`menu/`，无任何 ERROR。

### 升级提示

- 本版本**不支持**从上游版本直接覆盖升级：依赖结构、配置结构与附魔定义方式均已改变。请备份后删除旧的数据目录，让插件重新生成。
- 附魔存储为附魔注册表条目，修改脚本数值是安全的；但**删除**某个附魔脚本会使已附魔物品上的该附魔失效。

---

## 后续计划

- 把 tooltip 封包处理改为线程安全快照，以彻底满足 Folia 的线程约束。
- 补充脚本热重载（`/enchants reload` 已可重载脚本，后续增加单文件热重载与语法错误定位）。
- 继续丰富 `ctx` 助手方法与脚本文档示例。
