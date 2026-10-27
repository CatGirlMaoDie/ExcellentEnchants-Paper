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
arrow-effects = "ELECTRIC_SPARK"                               // 箭矢轨迹粒子（可以是字符串或 [particle: "...", color: "r,g,b"]）
charges = [max: [base: 50], consume: 1, recharge: 1, fuel: "ENDER_PEARL"]   // 充能系统
damage-bonus = "multiplier"                                    // 伤害减免类型：normal / multiplier
place-in-containers = false                                    // 是否允许放入容器

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
| `on-attack(ctx)` | 用附魔武器攻击实体 | `true` = 已生效 |
| `on-defend(ctx)` | 穿着附魔护甲被攻击 | `true` = 已生效 |
| `on-mining(ctx)` | 用附魔工具挖掘方块 | `true` = 已生效 |
| `on-kill(ctx)` | 用附魔武器击杀实体 | `true` = 已生效 |
| `on-passive(ctx)` | 被动触发（按 `period` 周期） | `true` = 已生效 |
| `on-shoot(ctx)` | 用附魔弓射箭 | `true` = 已生效 |
| `on-launch(ctx)` | 投掷附魔三叉戟 | `true` = 已生效 |
| `on-hit(ctx)` | 箭/三叉戟命中方块或实体 | 无（`ctx.projectile` 为投射物） |
| `on-projectile-damage(ctx)` | 箭/三叉戟造成伤害 | 无（`ctx.victim` 为受击者） |
| `on-drop(ctx)` | 方块掉落物生成（时运/精准采集后） | `true` = 已生效 |
| `on-interact(ctx)` | 与方块交互 / 左击方块 | `true` = 已生效 |
| `on-fishing(ctx)` | 钓鱼状态变化 | `true` = 已生效 |
| `on-inventory-death(ctx)` | 玩家死亡掉落物品 | `true` = 已生效 |
| `on-click(ctx)` | 在容器界面点击 | `true` = 已生效 |
| `on-item-damage(ctx)` | 物品耐久减少 | `true` = 已生效 |
| `on-protection(ctx)` | 受到伤害（可改伤害减免） | `true` = 已生效 |
| `on-death(ctx)` | 实体死亡（附魔持有者） | `true` = 已生效 |
| `on-resurrect(ctx)` | 不死图腾触发 | `true` = 已生效 |
| `on-block-change(ctx)` | 实体改变方块（踩踏等） | `true` = 已生效 |
| `on-move(ctx)` | 玩家移动 | `true` = 已生效 |
| `on-place(ctx)` | 放置附魔方块 | 无 |

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
| `ctx.effectArrow(arrow)` | 给箭矢附加 `potion-effect` 组件 |
| `ctx.mod(map[, level])` | 计算脚本中声明的 `[base: .., per-level: .., capacity: ..]` 数值 |
| `ctx.asInt(x)` / `ctx.asFloat(x)` | 数值类型转换 |
| `ctx.charges()` / `ctx.consumeCharge()` / `ctx.outOfCharges()` | 充能系统 |
| `ctx.give(item)` / `ctx.giveTo(player, item)` / `ctx.hasSpace(player, item)` | 给予物品 |
| `ctx.breakBlock(block)` | 以"忙碌状态"破坏方块（不会再次触发附魔） |
| `ctx.explodeBlocks(block, power)` | 生成挖掘爆炸（不伤害实体） |
| `ctx.explode(location, power, fire, blocks, items)` | 生成可配置爆炸 |
| `ctx.explodeLivingOnly(location, power, effects)` | 只伤害生物的爆炸 |
| `ctx.cook(item)` / `ctx.smelt(item)` / `ctx.isCookable(item)` | 烧炼结果 |
| `ctx.addDrop(item)` | 向当前方块掉落事件追加掉落物 |
| `ctx.autoReel()` | 自动收杆 |
| `ctx.spawn(type, location)` / `ctx.entityType(name)` | 生成实体 |
| `ctx.launchProjectile(type)` | 发射投射物（如 `ENDER_PEARL`） |
| `ctx.material(name)` / `ctx.item(name)` / `ctx.itemOf(material)` | 材质/物品构造 |
| `ctx.face(name)` / `ctx.faces("UP,DOWN,...")` | 方块朝向常量 |
| `ctx.isLog(type)` / `ctx.isLeaves(type)` | 原木/树叶判断 |
| `ctx.maxHealth(entity)` | 实体最大生命值 |
| `ctx.disablePickup(arrow)` | 禁止拾取箭矢 |
| `ctx.setBlockLater(block, material)` | 下一 tick 替换方块 |
| `ctx.addTickedBlock(block, from, to, life)` | 定时恢复方块（火焰行者） |
| `ctx.formBlock(block, material)` | 触发方块形成事件并应用 |
| `ctx.removeAllEnchants(item)` | 清除物品所有附魔 |
| `ctx.skullItem(material, texture, name, owner)` / `ctx.headMaterial(type)` | 头颅物品 |
| `ctx.isMythicMob(e)` / `ctx.spawnReason(e)` / `ctx.spawnEgg(type)` | MythicMobs / 生成信息 |
| `ctx.setPdcString(item, key, value)` / `ctx.getPdcString(item, key)` | 持久化数据 |
| `ctx.delayVelocity(entity, factor)` | 下一 tick 修改速度 |
| `ctx.lastTwoTargetBlocks(distance)` | 准星路径上的方块 |

> 脚本运行在**开启反射访问**的编译上下文中，因此也可以直接访问 Bukkit 对象的任意方法：
> `&ctx.victim.setFireTicks(100)`、`&ctx.player.getInventory().addItem(...)` 等。

> ⚠️ 插件类会被 shade 重定位，因此脚本中**不要**直接引用 `su.nightexpress.*` 的类名；
> 枚举/常量请使用上表提供的辅助方法（如 `ctx.face("UP")`、`ctx.material("LAVA")`），
> 或直接比较 `.name()` 字符串。类型判断（`is org.bukkit.entity.Player`）不受影响。

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
