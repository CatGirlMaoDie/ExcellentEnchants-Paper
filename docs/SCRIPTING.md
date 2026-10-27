# Fluxon 附魔脚本

ExcellentEnchants 使用 [Fluxon](https://github.com/TabooLib/fluxon) 脚本语言来定义与实现附魔。
每一个 `.fs` 脚本既是附魔的**配置**（名称、等级、权重、适配物品、掉落分布、概率/药水/周期组件），
也是附魔的**行为实现**（命中、受击、挖掘、击杀、被动触发等回调）。

内置的默认附魔中，已经有一批被完全改写为脚本（见 `Core/src/main/resources/enchants/`），
其余附魔仍由 Java 实现，可以作为迁移参考。

## 目录结构

```
plugins/ExcellentEnchants/
├── enchants/
│   ├── venom.fs              # 脚本附魔
│   ├── haste.fs
│   ├── poison-arrows.fs      # 自定义附魔（无需写 Java）
│   └── _disabled_/           # 放在这里的脚本会被禁用
└── config.yml
```

* 插件首次启动时会把 jar 内置的默认脚本释放到 `enchants/`（已存在则不覆盖）。
* 在 `enchants/` 中新增 `.fs` 文件即可添加新附魔，**无需编译任何 Java 代码**。
* 文件名（去掉 `.fs`）就是附魔 ID；也可以在脚本中用 `id = "..."` 覆盖。
* 附魔 ID 会用于注册 Minecraft 附魔（`excellentenchants:<id>`），因此包含 ID 的改动需要重启服务器。

## 脚本结构

```fluxon
// —— 元数据（顶层变量，插件在加载脚本时读取）——
name = "Venom"                       // 显示名
max-level = 2                        // 最大等级
weight = "uncommon"                  // common / uncommon / rare / very-rare，或直接写数字
description = ["命中时有几率使目标中毒。"]
supported-items = "SWORDS_AXES"      // 可用物品集（ItemSetDefaults 枚举名）
primary-items = "SWORD"              // 主要物品集（附魔台产出）
trades = ["SWAMP_COMMON"]            // 村民交易分布（TradeType 枚举名）
treasure = false                     // 是否为宝藏附魔
curse = false                        // 是否为诅咒
priority = "normal"                  // 事件优先级 lowest/low/normal/high/highest

// —— 组件（可选）——
trigger-chance = [base: 5, per-level: 5]                       // 触发几率（0-100）
potion-effect = [type: "POISON",                               // 药水效果组件
                 duration: [base: 2, per-level: 1, capacity: 10],
                 amplifier: [base: 0, per-level: 0]]
period = 5                                                     // 被动触发间隔（秒）

// —— 行为（可选，定义了哪个回调就实现哪个）——
def on-attack(ctx) {
    if !&ctx.chance() { return false }
    if !&ctx.effect(&ctx.victim) { return false }
    true
}
```

### 支持的回调

| 回调 | 触发时机 | 返回值 |
|:--|:--|:--|
| `on-attack(ctx)` | 玩家用附魔武器攻击实体 | `true` = 已生效 |
| `on-defend(ctx)` | 玩家穿着附魔护甲被攻击 | `true` = 已生效 |
| `on-mining(ctx)` | 玩家用附魔工具挖掘方块 | `true` = 已生效 |
| `on-kill(ctx)` | 玩家用附魔武器击杀实体 | `true` = 已生效 |
| `on-passive(ctx)` | 被动触发（按 `period` 周期） | `true` = 已生效 |

### 上下文对象 `ctx`

| 成员 | 说明 |
|:--|:--|
| `ctx.actor` | 拥有附魔物品的实体（攻击者 / 穿戴者 / 挖掘者） |
| `ctx.player` | 若 `actor` 是玩家则返回该玩家，否则为 `null` |
| `ctx.victim` | 对手实体（受击者 / 被击杀者 / 攻击者），可能为 `null` |
| `ctx.target` | `victim` 优先，否则为 `actor` |
| `ctx.item` | 触发事件的物品 |
| `ctx.event` | 原始 Bukkit 事件对象 |
| `ctx.level` | 附魔等级 |
| `ctx.hook` | 当前回调名，如 `on-attack` |

### 辅助方法

| 方法 | 说明 |
|:--|:--|
| `ctx.chance()` | 按 `trigger-chance` 组件与当前等级掷骰 |
| `ctx.chance(percent)` | 按自定义百分比掷骰 |
| `ctx.effect(target)` / `ctx.effect()` | 施加 `potion-effect` 组件配置的药水效果 |
| `ctx.addEffect(target, "POISON", ticks, amplifier)` | 施加任意药水效果 |
| `ctx.removeEffect(target, "POISON")` | 移除药水效果 |
| `ctx.particles(target, "FLAME", 30)` | 播放粒子 |
| `ctx.sound(target, "ENTITY_PLAYER_ATTACK_CRIT")` | 播放音效 |
| `ctx.msg(target, "text")` | 发送消息 |
| `ctx.damage(target, amount)` | 造成伤害（以 `actor` 为攻击者） |
| `ctx.heal(target, amount)` | 治疗 |
| `ctx.ignite(target, seconds)` | 点燃 |
| `ctx.freeze(target, ticks)` | 冻结 |
| `ctx.drop(item)` | 在 `actor` 处掉落物品 |
| `ctx.random(min, max)` | 随机整数 |
| `ctx.visualEffects()` | 是否开启了视觉特效 |

> 脚本运行在**开启反射访问**的编译上下文中，因此也可以直接访问 Bukkit 对象的任意方法：
> `&ctx.victim.setFireTicks(100)`、`&ctx.player.getInventory().addItem(...)` 等。

## 自定义附魔示例

`enchants/lightning-strike.fs`：

```fluxon
name = "Lightning Strike"
max-level = 3
weight = "rare"
description = ["有几率召唤闪电劈中目标。"]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["PLAINS_SPECIAL"]

trigger-chance = [base: 5, per-level: 5]

def on-attack(ctx) {
    if !&ctx.chance() { return false }

    world = &ctx.victim.getWorld()
    world.strikeLightningEffect(&ctx.victim.getLocation())
    &ctx.damage(&ctx.victim, 4.0)
    &ctx.sound(&ctx.victim, "ENTITY_LIGHTNING_BOLT_THUNDER")

    true
}
```

重启服务器（或在控制台执行 `/ee reload` 以刷新行为）后生效。

## 重载说明

* `/ee reload`：重新读取脚本**行为**（`def` 回调）与常规配置。
* 修改 **元数据**（`name`、`max-level`、物品集、分布等）需要**重启服务器**，
  因为这些数据在服务器 bootstrap 阶段注册进了 Minecraft 附魔注册表。
