<p align="center">
  <img src="https://nightexpressdev.com/excellentenchants/banner.png">
</p>

<p align="center">
<b>ExcellentEnchants</b> is a lightweight and modern enchantments plugin that adds <b>80+ vanilla-like</b>, fully customizable enchantments to your server.
</p>

<p align="center">
<b>ExcellentEnchants</b> does <b>not</b> modify loot tables, villager trades, or enchanting tables - enchantments are seamlessly integrated into your server in the same way as vanilla or datapack enchantments.
</p>

<p align="center">
<b>ExcellentEnchants</b> leaves <b>no permanent</b> impact on your server after uninstallation, as if the plugin had never been installed - no errors, bugs, or crashes. Ever.
</p>

<p align="center">
<a href="https://nightexpressdev.com/excellentenchants/enchantments/list/"><b>Click here to view all new enchantments!</b></a>
</p>

<p align="center">
<img src="https://nightexpressdev.com/excellentenchants/img/gif/enchants_ghast.gif">
<img src="https://nightexpressdev.com/excellentenchants/img/gif/enchants_flamewalker.gif">
</p>
<p align="center">
<img src="https://nightexpressdev.com/excellentenchants/img/gif/enchants_thunder.gif">
<img src="https://nightexpressdev.com/excellentenchants/img/gif/enchants_tunnel.gif">
</p>

## Features

### Integration & Compatibility
- [**Seamless Integration**](https://nightexpressdev.com/excellentenchants/enchantments/integration/). Fully compatible with vanilla mechanics, commands, and other plugins.
- **Anvil & Grindstone Support**. Combine or remove custom enchantments just like vanilla ones.
- **Enchanted Books Support**. Access custom enchanted books directly from the Creative menu.
- [**PlaceholderAPI**](https://nightexpressdev.com/excellentenchants/placeholders/papi/) support.

<p align="center">
<img src="https://nightexpressdev.com/excellentenchants/img/gif/anvil.gif">
<img src="https://nightexpressdev.com/excellentenchants/img/gif/books.gif">
</p>

### Enchantment Distribution
- [**Enchanting Table**](https://nightexpressdev.com/excellentenchants/enchantments/distribution/) & **Villager Trades**. Obtain custom enchantments through standard progression.
- [**Random Loot**](https://nightexpressdev.com/excellentenchants/enchantments/distribution/). Find enchanted items in dungeon chests.
- [**Fishing**](https://nightexpressdev.com/excellentenchants/enchantments/distribution/). Fish enchanted books with custom enchantments.
- [**Mob Equipment**](https://nightexpressdev.com/excellentenchants/enchantments/distribution/). Mobs can spawn with custom-enchanted gear.

<p align="center">
<img src="https://nightexpressdev.com/excellentenchants/img/gif/enchanting.gif">
<img src="https://nightexpressdev.com/excellentenchants/img/gif/loot.gif">
</p>

### Customization & Control
- **Highly Customizable**. Modify attributes of any enchantment.
- **Overpowering Enchantments**. Allow enchantments to scale beyond max levels.
- **Exclusives**. Define incompatible enchantments per enchantment.
- [**Disable Enchantments**](https://nightexpressdev.com/excellentenchants/enchantments/disabling/). Disable enchantments globally or per world.

### Item & Equipment Support
- **Axes Support**. Apply sword enchantments to axes.
- **Crossbows Support**. Apply bow enchantments to crossbows by default.
- **Elytra Support**. Apply chestplate enchantments to elytras by default.
- [**Item Lists**](https://nightexpressdev.com/excellentenchants/enchantments/item-sets/). Create custom primary & supported item lists for enchantments.

### User Interface & Presentation
- **Enchantments GUI**. Customizable GUI for browsing all custom enchantments.
- **Colored Tooltips**. Customize tooltip colors for custom enchantments.
- [**Description Tooltip**](https://nightexpressdev.com/excellentenchants/features/tooltips/). Display enchantment summaries in item tooltips.
- **Visual Effects**. Enchantments include particles and sound effects.

### Advanced Mechanics
- [**Enchant Charges**](https://nightexpressdev.com/excellentenchants/features/charges/). Introduce charge-based mechanics for enchantments.
- [**New Curses**](https://nightexpressdev.com/excellentenchants/enchantments/list/). Additional curse enchantments beyond vanilla.

## Building

The project is a multi-module Maven build:

```
mvn clean package
```

The API version is defined by the `paper.api.version` property in the root `pom.xml` (default `26.1.2.build.51-beta`, which requires **JDK 25**).
To build against a Java 21 compatible Paper API, override the property:

```
mvn clean package -Dpaper.api.version=1.21.11-R0.1-SNAPSHOT
```

### Module layout

| Module | Description |
|:--|:--|
| `nightcore-shim` | The engine sources (configuration, language, command framework, GUI framework, text/particle helpers) previously shipped by the *NightCore* library, bundled and adapted to the native Paper API. |
| `API` | Public API of the plugin. |
| `Core` | The plugin implementation; shades the engine into `su.nightexpress.excellentenchants.libs.nightcore` and the Fluxon runtime into `su.nightexpress.excellentenchants.libs.fluxon`. |
| `tooltip-packetevents` / `tooltip-protocollib` | Optional tooltip providers. |
| `compat-mythicmobs` | Optional MythicMobs compatibility. |

## Scripting (Fluxon)

Enchantments can be defined and implemented with [Fluxon](https://github.com/TabooLib/fluxon) scripts - no Java code required.

* Scripts live in `plugins/ExcellentEnchants/enchants/*.fs`.
* A script declares the enchantment metadata (name, max level, weight, item sets, distribution, components) **and** its behaviour through hook functions such as `on-attack(ctx)`, `on-defend(ctx)`, `on-mining(ctx)`, `on-kill(ctx)` and `on-passive(ctx)`.
* **All 81 default enchantments are shipped as Fluxon scripts** (`Core/src/main/resources/enchants/*.fs`): every metadata entry, distribution, component and behaviour lives in the script files - there are no Java enchantment implementations left. They are extracted on first startup, so they can be read, copied and modified as templates.
* Adding a new `.fs` file adds a new enchantment; `/ee reload` hot-reloads script behaviour (metadata changes require a restart).

See [docs/SCRIPTING.md](docs/SCRIPTING.md) for the full reference.

```fluxon
name = "Venom"
max-level = 2
weight = "uncommon"
supported-items = "SWORDS_AXES"
primary-items = "SWORD"

trigger-chance = [base: 5, per-level: 5]
potion-effect = [type: "POISON", duration: [base: 2, per-level: 1, capacity: 10]]

def on-attack(ctx) {
    if !&ctx.chance() { return false }
    if !&ctx.effect(&ctx.victim) { return false }
    true
}
```

## Requirements

**ExcellentEnchants** is a standalone **Paper** plugin. It does **not** require any additional plugin to be installed on your server: the engine code that used to live in the external *NightCore* library is now bundled inside the plugin itself and implemented on top of the native Paper API.

**Supported range: Minecraft 1.21 - 26.3.**

| **Server Version** | **Paper** | **Folia** | **Java Version** |
|:------------------:|:---------:|:---------:|:----------------:|
|       26.3         |    Yes    |    Yes    |        25        |
|       26.2         |    Yes    |    Yes    |        25        |
|      26.1.x        |    Yes    |    Yes    |        25        |
|      1.21.11       |    Yes    |    Yes    |        21        |
|    1.21.9 - 1.21.10|    Yes    |    Yes    |        21        |
|      1.21.8        |    Yes    |    Yes    |        21        |
|   1.21 - 1.21.7    |    Yes    |    Yes    |        21        |

- **Folia:** supported. All scheduling goes through the global region, region and entity schedulers
  (no `BukkitScheduler` usage), so chunk/entity work always runs on the owning region thread.
- **Older versions (1.20.x and below) are no longer supported.** The plugin will refuse to start on them and disable itself.
- Versions newer than **26.3** are loaded in "best effort" mode: the plugin enables, but some features may not work properly.
- Anything not listed in the compatibility table is **NOT** supported.
- Make sure to check out all known issues and incompatibilities [here](https://nightexpressdev.com/excellentenchants/common-issues/).

**Optional Plugins:**
- [PacketEvents](https://spigotmc.org/resources/80279/) - For enchantment descriptions in item tooltip (you need only one).
- [ProtocolLib](https://ci.dmulloy2.net/job/ProtocolLib/) - For enchantment descriptions in item tooltip (you need only one).

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for the list of changes of this fork (Paper native rewrite, Fluxon scripting, Folia support, ...).

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)** - see the [LICENSE](LICENSE) file for the full text.

It is a derivative work of [ExcellentEnchants](https://github.com/nulli0n/ExcellentEnchants-spigot) by
[NightExpress](https://nightexpressdev.com/), which is also licensed under GPL-3.0. Because the license is
copyleft, any modified or redistributed version must stay under GPL-3.0 and ship its source code.

## Links
- [**Website**](https://nightexpressdev.com/) - Check out my other plugins.
- [**Wiki**](https://nightexpressdev.com/excellentenchants/) - Learn how to configure the plugin.
- [**Discord**](https://discord.gg/EwNFGsnGaW) - Get support, ask questions.
- [**Github**](https://github.com/CatGirlMaoDie/ExcellentEnchants-Paper) - Check out the source code.
- [**Donate**](https://ko-fi.com/nightexpress) - Support me and my work.
