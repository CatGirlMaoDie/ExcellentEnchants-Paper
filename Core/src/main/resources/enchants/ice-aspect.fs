// Ice Aspect — default enchantment script.
//
// This file is the single source of truth for the enchantment: it declares the
// definition/distribution metadata and implements the behaviour using Fluxon.
// Edit freely, then restart the server (or run /ee reload) to apply changes.

name = "Ice Aspect"
max-level = 3
weight = "common"
description = ["Freezes and applies %enchantment_potion_type% %enchantment_potion_level% (%enchantment_potion_duration%s.) on hit."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["SNOW_COMMON"]

trigger-chance = [base: 100, per-level: 0]
potion-effect = [type: "SLOWNESS", duration: [base: 3, per-level: 1, capacity: 10]]

def on-attack(ctx) {
    if !&ctx.chance() {
        return false
    }
    if !&ctx.effect(&ctx.victim) {
        return false
    }

    if &ctx.visualEffects() {
        &ctx.particles(&ctx.victim, "CLOUD", 30)
    }

    true
}
