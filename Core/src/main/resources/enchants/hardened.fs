// Hardened — default enchantment script.
//
// This file is the single source of truth for the enchantment: it declares the
// definition/distribution metadata and implements the behaviour using Fluxon.
// Edit freely, then restart the server (or run /ee reload) to apply changes.

name = "Hardened"
max-level = 2
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to get %enchantment_potion_type% %enchantment_potion_level% (%enchantment_potion_duration%s.) when damaged."]
supported-items = "CHESTPLATE_ELYTRA"
primary-items = "CHESTPLATE"
trades = ["PLAINS_COMMON"]

trigger-chance = [base: 4, per-level: 2]
potion-effect = [type: "RESISTANCE", duration: [base: 3, per-level: 1, capacity: 10]]

def on-defend(ctx) {
    if !&ctx.chance() {
        return false
    }
    if !&ctx.effect(&ctx.actor) {
        return false
    }

    true
}
