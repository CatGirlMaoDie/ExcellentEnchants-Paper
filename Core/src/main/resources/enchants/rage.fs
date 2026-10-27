// Rage — default enchantment script.
//
// This file is the single source of truth for the enchantment: it declares the
// definition/distribution metadata and implements the behaviour using Fluxon.
// Edit freely, then restart the server (or run /ee reload) to apply changes.

name = "Rage"
max-level = 2
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to get %enchantment_potion_type% %enchantment_potion_level% (%enchantment_potion_duration%s.) on hit."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["DESERT_COMMON"]

trigger-chance = [base: 6, per-level: 2]
potion-effect = [type: "STRENGTH", duration: [base: 3, per-level: 1, capacity: 10]]

def on-attack(ctx) {
    if !&ctx.chance() {
        return false
    }
    if !&ctx.effect(&ctx.actor) {
        return false
    }

    if &ctx.visualEffects() {
        &ctx.particles(&ctx.actor, "LAVA", 30)
    }

    true
}
