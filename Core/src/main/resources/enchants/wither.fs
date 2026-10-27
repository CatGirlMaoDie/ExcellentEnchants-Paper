// Wither — default enchantment script.
//
// This file is the single source of truth for the enchantment: it declares the
// definition/distribution metadata and implements the behaviour using Fluxon.
// Edit freely, then restart the server (or run /ee reload) to apply changes.

name = "Wither"
max-level = 2
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to apply %enchantment_potion_type% %enchantment_potion_level% (%enchantment_potion_duration%s.) on hit."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["SNOW_COMMON"]

trigger-chance = [base: 4, per-level: 3]
potion-effect = [type: "WITHER", duration: [base: 4, per-level: 2, capacity: 10]]

def on-attack(ctx) {
    if !&ctx.chance() {
        return false
    }
    if !&ctx.effect(&ctx.victim) {
        return false
    }

    if &ctx.visualEffects() {
        &ctx.particles(&ctx.victim, "ASH", 30)
    }

    true
}
