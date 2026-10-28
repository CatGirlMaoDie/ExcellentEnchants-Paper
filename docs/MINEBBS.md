# ExcellentEnchants-Paper

> 81 个原版风格附魔 · 无需任何前置 · 全脚本化 · Paper 1.21 – 26.3 & Folia

---

## 📖 简介

**ExcellentEnchants-Paper** 是经典附魔插件 ExcellentEnchants 的 **Paper 原生重构版**。

它保留了原版插件全部 81 个"原汁原味"的自定义附魔——不修改战利品表、不篡改村民交易、不劫持附魔台，附魔会像原版附魔一样自然地融入你的服务器；同时把整个引擎重写在了 **原生 Paper API** 之上，并引入 **Fluxon 脚本系统**，让服主可以像写配置一样写附魔逻辑。

**一句话概括：不用装前置、不锁版本、附魔内容全都能自己改。**

---

## ✨ 与上游版本的差异

| 对比项 | 上游 ExcellentEnchants | 本版本 ExcellentEnchants-Paper |
|:--|:--|:--|
| 运行前置 | **必须安装 NightCore** | **无需任何前置**，丢进 plugins 即可 |
| 引擎实现 | 依赖外部 NightCore 库 | NightCore 引擎已内置并重构为原生 Paper 实现 |
| 附魔实现 | 硬编码在 Java 类中 | **81 个附魔全部是 Fluxon 脚本**，可直接编辑 |
| 版本支持 | 跟随上游 | **专注 1.21 – 26.3**，1.20 及更早会主动拒绝启动 |
| Folia | 不支持 | **支持 Folia**，全链路区域/实体线程调度 |
| 开源协议 | GPL-3.0 | GPL-3.0（保持开源，可自由改） |

### 为什么"脚本化"很重要

所有附魔的行为都写在 `plugins/ExcellentEnchants/enchants/*.fs` 里，**不是编译进 jar 的黑盒**：

- 想改数值？改脚本里的 `base / per-level / capacity`。
- 想加附魔？复制一个 `.fs` 文件，改个名字就是新附魔。
- 想看某个附魔怎么实现的？直接读脚本，和读配置一样。
- 想删掉某个附魔？删掉对应文件（或把它从 `_index.txt` 移除）。

服主不再需要"等作者更新版本"才能改一个数值。

---

## 🎯 功能特性

### 无缝整合
- **完全原版化**：与命令、数据包、其他插件共存，不劫持原版机制
- **铁砧 / 砂轮**：像原版附魔一样随意合并、剥离（支持等级合并规则）
- **附魔书**：创造模式物品栏可直接取用自定义附魔书
- **PlaceholderAPI** 变量支持

### 获取途径
- **附魔台**与**村民交易**：随正常游戏进程自然获得
- **战利品箱**：地牢、遗迹宝箱随机出现已附魔装备
- **钓鱼**：可钓到带自定义附魔的附魔书
- **怪物装备**：怪物生成时即穿戴自定义附魔装备

### 自定义与控制
- **属性全可调**：每个附魔的触发概率、数值曲线、等级上限、权重、稀有度
- **超限等级**：允许附魔突破等级上限继续叠加
- **互斥规则**：为每个附魔单独设置不兼容附魔
- **全局 / 单世界禁用**：按需关闭某个附魔
- **物品列表**：自定义每个附魔的"适用物品"与"主手物品"集合（支持标签，如 `SWORDS_AXES`）

### 装备兼容
- **斧头**可继承剑类附魔
- **弩**默认继承弓类附魔
- **鞘翅**默认继承胸甲类附魔

### 界面与表现
- **附魔 GUI**：`/enchants list` 浏览全部自定义附魔，界面可自定义
- **彩色 Tooltip**：自定义附魔名称颜色
- **描述 Tooltip**：鼠标悬停即可看到附魔说明（需 PacketEvents 或 ProtocolLib，二选一）
- **粒子与音效**：每个附魔都配有对应视觉反馈

### 进阶机制
- **充能系统**：给附魔加"次数"概念，可用指定材料（燃料）补充，铁砧可充能
- **新增诅咒**：原版之外的更多诅咒附魔（死亡诅咒、脆弱诅咒、平庸诅咒等）

---

## 📜 附魔列表（81 个）

**⚔️ 剑 / 斧（23）**
下界生物克星、致盲、混乱、治愈、死亡诅咒、割裂、斩首、双重打击、力竭、冰霜、灵巧、麻痹、愤怒、火箭、横扫、淬火、节俭、雷鸣、吸血、毒素、村庄守护者、睿智、凋零

**🏹 弓 / 弩（15）**
轰炸、混乱之箭、黑暗之箭、龙息之箭、充能之箭、爆裂之箭、恶魂、悬停、滞留、剧毒之箭、狙击、吸血之箭、凋零之箭、末影弓、照明弹

**⛏ 镐（3）**
爆破挖掘、幸运矿工、矿脉采矿

**🔨 挖掘工具（3）**
隧道、丝绸箱子、精准刷怪笼

**🪓 斧（1）** 砍树 ｜ **🌾 锄（1）** 自动补种

**🧰 通用工具（4）**
玻璃破坏、急速、熔炼、远程拾取

**🪖 头盔（3）**
夜视、饱食、水下呼吸

**🥋 胸甲 / 鞘翅（8）**
冷钢、黑暗斗篷、龙心、火焰护盾、硬化、冰霜护盾、神风、再生

**👖 护腿（1）** 击退抵抗 ｜ **👢 靴子（5）** 火焰行者、跳跃、轻盈、回弹、速度

**🛡 通用护甲（1）** 元素防护

**🎣 钓鱼竿（6）**
自动收杆、双倍收获、河神、老练钓手、求生者、溺亡诅咒

**💀 诅咒 / 特殊（7）**
破损诅咒、脆弱诅咒（可损坏物品）、平庸诅咒、厄运诅咒（工具与武器通用）、死亡诅咒、溺亡诅咒、灵魂绑定

**🔱 三叉戟（1）** 炼狱

> 每个附魔的触发概率、数值曲线、等级上限、获取途径都写在 `enchants/<附魔id>.fs` 脚本里，可自由修改；游戏内用 `/enchants list` 可查看全部附魔与说明。

---

## 🖥 支持版本

| 服务端版本 | Paper | Folia | Java |
|:--|:--:|:--:|:--:|
| 26.3 / 26.2 / 26.1.x | ✅ | ✅ | 25 |
| 1.21.11 | ✅ | ✅ | 21 |
| 1.21.9 – 1.21.10 | ✅ | ✅ | 21 |
| 1.21.8 | ✅ | ✅ | 21 |
| 1.21 – 1.21.7 | ✅ | ✅ | 21 |

- **1.20.x 及更早版本不再支持**：插件启动时会检测版本并主动停用自身，避免半残运行。
- 比 26.3 更新的版本会以"尽力兼容"模式加载（可用，但可能有个别特性异常）。
- **Folia**：全部调度均走全局区域 / 区域 / 实体调度器，没有使用 `BukkitScheduler`。

---

## 📦 安装

1. 确认服务端为 **Paper 1.21+**（或 Folia）。
2. 把 `ExcellentEnchants-Paper-x.x.x.jar` 放入 `plugins/` 目录。
3. 启动服务器，插件会自动释放默认配置、语言文件与 **81 个附魔脚本**。
4. （可选）按需安装 PacketEvents 或 ProtocolLib，用于显示附魔描述。

**无需安装 NightCore 或任何其他前置。**

---

## 📁 生成的目录结构

```
plugins/ExcellentEnchants/
├── config.yml              # 主配置：附魔台/交易/战利品/钓鱼/充能/Tooltip 等开关
├── lang/
│   ├── lang_zh.yml     # 简体中文（已内置）
│   ├── messages_en.yml     # 英文
│   └── ...                 # 日文 / 西班牙文 / 波兰文 / 葡萄牙文
└── enchants/
    ├── _index.txt          # 脚本清单（决定加载哪些附魔）
    ├── treefeller.fs       # 砍树：BFS 搜索原木与树叶
    ├── veinminer.fs        # 矿脉：6 方向扩张
    ├── tunnel.fs           # 隧道：按视线方向 3x3 挖掘
    ├── decapitator.fs      # 斩首：62 种生物头颅皮肤
    └── ...                 # 共 81 个
```

---

## 🔧 脚本自定义示例

一个完整的附魔脚本长这样（`enchants/venom.fs` 简化版）：

```fluxon
name = "Venom"
max-level = 3
weight = "uncommon"
description = ["Poisons the target on hit."]
supported-items = "SWORDS_AXES"

trigger-chance = [base: 10, per-level: 5]
potion-effect = [type: "POISON", duration: [base: 3, per-level: 1, capacity: 10]]

def on-attack(ctx) {
    if !&ctx.chance() { return false }
    if !&ctx.effect(&ctx.victim) { return false }
    true
}
```

- 上半部分是**元数据**（名称、等级、权重、适用物品、稀有度、交易、战利品…），插件据此自动生成附魔条目与获取途径。
- 下半部分是**行为**，共支持 21 个钩子：`on-attack`、`on-defend`、`on-mining`、`on-kill`、`on-passive`、`on-shoot`、`on-launch`、`on-hit`、`on-projectile-damage`、`on-drop`、`on-interact`、`on-fishing`、`on-inventory-death`、`on-click`、`on-item-damage`、`on-protection`、`on-death`、`on-resurrect`、`on-block-change`、`on-move`、`on-place`。
- `ctx` 提供大量开箱即用的助手方法（`chance()`、`mod()`、`breakBlock()`、`smelt()`、`explodeBlocks()`、`spawn()`、`skullItem()`、`charges()` 等），也允许直接调用 Bukkit 方法。
- 完整语法与 API 见仓库 `docs/SCRIPTING.md`。

---

## ⌨️ 命令与权限

主命令：`/enchants`（别名 `/eenchants`、`/excellentenchants`）

| 命令 | 说明 | 权限 |
|:--|:--|:--|
| `/enchants reload` | 重载配置与全部脚本 | `excellentenchants.command.reload` |
| `/enchants list [玩家]` | 打开附魔列表 GUI | `excellentenchants.command.list` |
| `/enchants book <附魔> [等级] [玩家]` | 给予附魔书（`-charged` 附带充能） | `excellentenchants.command.book` |
| `/enchants randombook [玩家]` | 给予随机附魔书（`-custom` 仅自定义） | `excellentenchants.command.randombook` |
| `/enchants enchant <附魔> <等级> [玩家] [槽位]` | 给手中物品附魔 | `excellentenchants.command.enchant` |
| `/enchants disenchant <附魔> [玩家] [槽位]` | 移除附魔 | `excellentenchants.command.disenchant` |
| `/enchants givefuel <附魔> [数量] [玩家]` | 获得充能材料 | `excellentenchants.command.givefuel` |

---

## ❓ 常见问题

**Q：需要装什么前置？**
A：不需要。PacketEvents / ProtocolLib 仅用于"物品提示框显示附魔描述"，是可选的（二选一即可）；PlaceholderAPI、MythicMobs 也都是可选联动。

**Q：能从上游版本直接替换升级吗？**
A：不建议直接覆盖。本版本移除了 NightCore 依赖，配置结构与附魔定义方式均已改变，建议备份后删除旧目录让插件重新生成。

**Q：改脚本会破坏存档数据吗？**
A：附魔以附魔注册表条目形式存储，修改脚本的**数值**是安全的；但**删除**某个附魔脚本会导致已附魔物品上的该附魔失效，请谨慎操作并做好备份。

**Q：支持 1.20 吗？**
A：不支持，插件会主动拒绝启动。1.20 及更早请使用上游原版。

**Q：可以商用/二次分发吗？**
A：可以，但必须遵守 **GPL-3.0**：修改或再分发时必须同样开源并保留协议。

---

## 📜 开源协议与致谢

本项目基于 **GNU General Public License v3.0 (GPL-3.0)** 开源。

它是 [NightExpress](https://nightexpressdev.com/) 的 [ExcellentEnchants](https://github.com/nulli0n/ExcellentEnchants-spigot) 的衍生作品（同样采用 GPL-3.0）。感谢原作者提供优秀的附魔设计与美术资源；本版本仅对引擎实现、依赖结构与附魔编写方式进行了重写。

- **源码 / 反馈**：https://github.com/CatGirlMaoDie/ExcellentEnchants-Paper
- **上游项目**：https://github.com/nulli0n/ExcellentEnchants-spigot
- **脚本手写文档**：仓库 `docs/SCRIPTING.md`

---

## 🏷 推荐标签

Paper · Folia · 1.21 · 附魔 · 脚本 · 开源 · 无需前置
