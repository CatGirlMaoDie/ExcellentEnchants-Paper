// Infernus — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Infernus"
max-level = 3
weight = "common"
description = ["Launched trident will ignite the enemy for %time%s. on hit."]
supported-items = "TRIDENT"
trades = ["SAVANNA_COMMON"]

arrow-effects = "FLAME"
fire-ticks = [base: 60, per-level: 20, capacity: 120]

def on-launch(ctx) {
    &ctx.event.getEntity().setFireTicks(2147483647)
    true
}

def on-hit(ctx) {
    entity = &ctx.event.getHitEntity()
    if &entity == null {
        return
    }

    &entity.setFireTicks(&ctx.asInt(&ctx.mod(&fire-ticks, &ctx.level)))
}
