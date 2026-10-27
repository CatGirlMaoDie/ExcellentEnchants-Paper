// Ender Bow — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Ender Bow"
max-level = 1
weight = "very-rare"
description = ["Shoots ender pearls instead of arrows."]
supported-items = "BOW"
treasure = true
trades = ["PLAINS_SPECIAL"]

priority = "lowest"
charges = [max: [base: 50], consume: 1, recharge: 1, fuel: "ENDER_PEARL"]

def on-shoot(ctx) {
    arrow = &ctx.event.getProjectile()
    if !(&arrow is org.bukkit.entity.Projectile) {
        return false
    }

    pearl = &ctx.launchProjectile("ENDER_PEARL")
    &pearl.setVelocity(&arrow.getVelocity())
    &ctx.event.setProjectile(&pearl)
    true
}
