// Jumping — default enchantment script.
//
// This file is the single source of truth for the enchantment: it declares the
// definition/distribution metadata and implements the behaviour using Fluxon.
// Edit freely, then restart the server (or run /ee reload) to apply changes.

name = "Jumping"
max-level = 2
weight = "rare"
description = ["Grants permanent %enchantment_potion_type% %enchantment_potion_level% effect."]
supported-items = "BOOTS"
trades = ["PLAINS_COMMON"]

potion-effect = [type: "JUMP_BOOST", permanent: true]
period = 5

def on-passive(ctx) {
    if !&ctx.chance() {
        return false
    }
    if !&ctx.effect(&ctx.actor) {
        return false
    }

    true
}
