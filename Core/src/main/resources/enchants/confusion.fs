// Confusion — default enchantment script.
//
// This file is the single source of truth for the enchantment: it declares the
// definition/distribution metadata and implements the behaviour using Fluxon.
// Edit freely, then restart the server (or run /ee reload) to apply changes.

name = "Confusion"
max-level = 2
weight = "common"
description = ["%enchantment_trigger_chance%% chance to apply %enchantment_potion_type% %enchantment_potion_level% (%enchantment_potion_duration%s.) on hit."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["SNOW_COMMON"]

trigger-chance = [base: 0, per-level: 8]
potion-effect = [type: "NAUSEA", duration: [base: 10, per-level: 5, capacity: 30], amplifier: [base: 1, capacity: 1]]

def on-attack(ctx) {
    if !&ctx.chance() {
        return false
    }
    if !&ctx.effect(&ctx.victim) {
        return false
    }

    true
}
