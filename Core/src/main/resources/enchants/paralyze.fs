// Paralyze — default enchantment script.
//
// This file is the single source of truth for the enchantment: it declares the
// definition/distribution metadata and implements the behaviour using Fluxon.
// Edit freely, then restart the server (or run /ee reload) to apply changes.

name = "Paralyze"
max-level = 5
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to apply %enchantment_potion_type% %enchantment_potion_level% (%enchantment_potion_duration%s.) on hit."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["TAIGA_COMMON"]

trigger-chance = [base: 10, per-level: 2]
potion-effect = [type: "MINING_FATIGUE", duration: [base: 3, per-level: 1, capacity: 10]]

def on-attack(ctx) {
    if !&ctx.chance() {
        return false
    }
    if !&ctx.effect(&ctx.victim) {
        return false
    }

    if &ctx.visualEffects() {
        &ctx.particles(&ctx.victim, "ENCHANTED_HIT", 30)
    }

    true
}
