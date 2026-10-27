// Curse of Misfortune — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Curse of Misfortune"
max-level = 3
weight = "uncommon"
curse = true
description = ["%enchantment_trigger_chance%% chance to have no drops from blocks or mobs."]
supported-items = "TOOLS_WEAPONS"
treasure = true
trades = ["TAIGA_COMMON"]

priority = "highest"
trigger-chance = [base: 0, per-level: 7]
drop-xp = false

def on-mining(ctx) {
    if !&ctx.chance() {
        return false
    }

    &ctx.event.setDropItems(false)
    if !&drop-xp {
        &ctx.event.setExpToDrop(0)
    }
    true
}

def on-kill(ctx) {
    if !&ctx.chance() {
        return false
    }

    &ctx.event.getDrops().clear()
    if !&drop-xp {
        &ctx.event.setDroppedExp(0)
    }
    true
}
