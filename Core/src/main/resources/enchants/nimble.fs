// Nimble — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Nimble"
max-level = 1
weight = "rare"
description = ["Moves all entity's loot directly to your inventory."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["JUNGLE_COMMON"]

priority = "monitor"
ignore-players = false

def on-kill(ctx) {
    if &ignore-players && (&ctx.victim is org.bukkit.entity.Player) {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    player = &ctx.actor
    drops = &ctx.event.getDrops()
    for item in &drops {
        &ctx.giveTo(&player, &item)
    }
    &drops.clear()
    true
}
