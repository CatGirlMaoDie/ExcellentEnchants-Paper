// Ghast — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Ghast"
max-level = 1
weight = "very-rare"
description = ["Shoots fireballs instead of arrows."]
supported-items = "BOW_CROSSBOW"
treasure = true
trades = ["DESERT_COMMON"]

priority = "lowest"
fire-spread = true
yield = [base: 2, per-level: 0, capacity: 5]

def on-shoot(ctx) {
    arrow = &ctx.event.getProjectile()
    if !(&arrow is org.bukkit.entity.Projectile) {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    multishot = &ctx.item != null && &ctx.hasEnchantment(&ctx.item, "MULTISHOT")
    fireball = null

    if &multishot {
        fireball = &ctx.launchProjectile("SMALL_FIREBALL")
        &fireball.setVelocity(&arrow.getVelocity().normalize().multiply(0.5))
    }
    else {
        fireball = &ctx.launchProjectile("FIREBALL")
        &fireball.setDirection(&arrow.getVelocity())
    }

    &fireball.setIsIncendiary(&fire-spread)
    &fireball.setYield(&ctx.asFloat(&ctx.mod(&yield, &ctx.level)))
    &ctx.event.setProjectile(&fireball)
    true
}
