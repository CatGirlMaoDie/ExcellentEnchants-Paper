// Lucky Miner — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Lucky Miner"
max-level = 3
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to gain %amount%% more XP from ores."]
supported-items = "PICKAXE"
trades = ["JUNGLE_COMMON"]

xp-modifier = [base: 1, per-level: 0.5, capacity: 3]

def on-mining(ctx) {
    if !&ctx.chance() {
        return false
    }

    modifier = &ctx.mod(&xp-modifier, &ctx.level)
    exp = &ctx.event.getExpToDrop()
    &ctx.event.setExpToDrop(&ctx.asInt(&exp * &modifier))
    true
}
