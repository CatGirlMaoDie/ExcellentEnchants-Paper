// Kamikadze — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Kamikadze"
max-level = 3
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to explode on death."]
supported-items = "CHESTPLATE_ELYTRA"
primary-items = "CHESTPLATE"
trades = ["JUNGLE_COMMON"]

trigger-chance = [base: 0, per-level: 3]
apply-on-resurrect = true
explosion-power = [base: 1, per-level: 1, capacity: 5]

def on-death(ctx) {
    if !&ctx.chance() {
        return false
    }

    &ctx.explodeLivingOnly(&ctx.actor.getLocation(), &ctx.asFloat(&ctx.mod(&explosion-power, &ctx.level)),
        &ctx.visualEffects())
}

def on-resurrect(ctx) {
    if !&apply-on-resurrect {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    &ctx.explodeLivingOnly(&ctx.actor.getLocation(), &ctx.asFloat(&ctx.mod(&explosion-power, &ctx.level)),
        &ctx.visualEffects())
}
