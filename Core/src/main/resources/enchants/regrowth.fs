// Regrowth — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Regrowth"
max-level = 4
weight = "rare"
description = ["Restores %amount%❤ every few seconds."]
supported-items = "CHESTPLATE_ELYTRA"
primary-items = "CHESTPLATE"
treasure = true
trades = ["JUNGLE_SPECIAL"]

period = 15
min-health = 0.5
max-health = 20
heal-amount = [base: 0.1, per-level: 0.1, capacity: 1]

def on-passive(ctx) {
    entity = &ctx.actor
    max-hp = &ctx.maxHealth(&entity)
    health = &entity.getHealth()

    if &health < &min-health || &health > &max-health {
        return false
    }
    if &health >= &max-hp {
        return false
    }

    &entity.setHealth(static java.lang.Math.min(&max-hp, &health + &ctx.mod(&heal-amount, &ctx.level)))

    if &ctx.visualEffects() {
        &ctx.particles(&entity, "HEART", 5)
    }

    true
}
