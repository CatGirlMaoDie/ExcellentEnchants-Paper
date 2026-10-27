// Curse of Death — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Curse of Death"
max-level = 3
weight = "rare"
curse = true
description = ["When killing players, you have a chance of dying too."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
treasure = true
trades = ["DESERT_SPECIAL"]

priority = "monitor"

def on-kill(ctx) {
    if !(&ctx.victim is org.bukkit.entity.Player) {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    &ctx.actor.setHealth(0.0)
    true
}
