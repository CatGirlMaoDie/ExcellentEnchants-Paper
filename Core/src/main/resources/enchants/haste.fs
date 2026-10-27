// Haste — default enchantment script.
//
// This file is the single source of truth for the enchantment: it declares the
// definition/distribution metadata and implements the behaviour using Fluxon.
// Edit freely, then restart the server (or run /ee reload) to apply changes.

name = "Haste"
max-level = 3
weight = "rare"
description = ["Grants %enchantment_potion_type% %enchantment_potion_level% effect when mining blocks."]
supported-items = "TOOL"
primary-items = "MINING_TOOLS"
trades = ["SAVANNA_COMMON"]

potion-effect = [type: "HASTE", duration: [base: 3, per-level: 1, capacity: 10]]

def on-mining(ctx) {
    if !&ctx.chance() {
        return false
    }
    if !&ctx.effect(&ctx.actor) {
        return false
    }

    true
}
