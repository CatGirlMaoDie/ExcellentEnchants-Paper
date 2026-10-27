// Rebound — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Rebound"
max-level = 1
weight = "rare"
description = ["Effect of landing on a slime block."]
supported-items = "BOOTS"
treasure = true
trades = ["SWAMP_COMMON"]

priority = "lowest"
damage-bonus = "normal"
bounce-modifier = [base: 0.2, per-level: 0.1, capacity: 0.5]
bounce-limit = [base: 0.75, per-level: 0.15, capacity: 1.5]

def on-protection(ctx) {
    if !(&ctx.event.getDamageSource().getDamageType().name() == "FALL") {
        return false
    }

    entity = &ctx.actor
    if &entity is org.bukkit.entity.Player && &entity.isSneaking() {
        return false
    }

    &ctx.event.setCancelled(true)

    power = static java.lang.Math.min(&ctx.mod(&bounce-limit, &ctx.level),
        &ctx.event.getDamage() * &ctx.mod(&bounce-modifier, &ctx.level))

    if &ctx.visualEffects() {
        &ctx.particles(&entity.getLocation(), "ITEM_SLIME", 50, 0.45)
    }

    velocity = &entity.getVelocity()
    if &velocity.getY() < 0.0 {
        &entity.setVelocity(&velocity.setY(&power))
    }

    true
}
