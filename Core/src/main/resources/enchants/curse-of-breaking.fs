// Curse of Breaking — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Curse of Breaking"
max-level = 3
weight = "common"
curse = true
description = ["%enchantment_trigger_chance%% chance to consume extra %amount% durability points."]
supported-items = "BREAKABLE"
treasure = true
trades = ["SAVANNA_COMMON"]

durability-amount = [base: 0, per-level: 1, capacity: 5]

def on-item-damage(ctx) {
    if &ctx.event.getDamage() <= 0 {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    amount = &ctx.asInt(&ctx.mod(&durability-amount, &ctx.level))
    if &amount <= 0 {
        return false
    }

    &ctx.event.setDamage(&ctx.event.getDamage() + &amount)
    true
}
