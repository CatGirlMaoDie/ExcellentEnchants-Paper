// Sniper — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Sniper"
max-level = 2
weight = "common"
description = ["Increases projectile speed by %amount%%"]
supported-items = "BOW_CROSSBOW"
trades = ["PLAINS_SPECIAL"]

speed-modifier = [base: 1, per-level: 0.2, capacity: 3]

def on-shoot(ctx) {
    projectile = &ctx.event.getProjectile()
    factor = &ctx.mod(&speed-modifier, &ctx.level)
    &projectile.setVelocity(&projectile.getVelocity().multiply(&factor))
    true
}
