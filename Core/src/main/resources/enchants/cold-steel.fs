// Cold Steel — default enchantment script.
//
// This file is the single source of truth for the enchantment: it declares the
// definition/distribution metadata and implements the behaviour using Fluxon.
// Edit freely, then restart the server (or run /ee reload) to apply changes.

name = "Cold Steel"
max-level = 3
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to apply %enchantment_potion_type% %enchantment_potion_level% (%enchantment_potion_duration%s.) on attacker."]
supported-items = "CHESTPLATE_ELYTRA"
primary-items = "CHESTPLATE"
trades = ["SNOW_COMMON"]

trigger-chance = [base: 10, per-level: 5]
potion-effect = [type: "MINING_FATIGUE", duration: [base: 4, per-level: 1, capacity: 10]]

def on-defend(ctx) {
    if !&ctx.chance() {
        return false
    }
    if !&ctx.effect(&ctx.victim) {
        return false
    }

    true
}
