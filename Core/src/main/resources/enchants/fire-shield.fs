// Fire Shield — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Fire Shield"
max-level = 4
weight = "rare"
description = ["%enchantment_trigger_chance%% chance to ignite attackers for %duration%s."]
supported-items = "CHESTPLATE_ELYTRA"
primary-items = "CHESTPLATE"
trades = ["DESERT_COMMON"]

fire-duration = [base: 4, per-level: 1, capacity: 10]
fire-resistance = true

def on-defend(ctx) {
    if !&ctx.chance() {
        return false
    }

    ticks = &ctx.asInt(&ctx.mod(&fire-duration, &ctx.level) * 20.0)
    victim = &ctx.actor
    damager = &ctx.victim

    if &fire-resistance {
        &victim.addPotionEffect(&ctx.potionEffect("FIRE_RESISTANCE",
            &ticks, 0))
    }

    if &damager.getFireTicks() >= &ticks {
        return false
    }

    if &ctx.visualEffects() {
        &ctx.particles(&victim, "FLAME", 35, 0.5)
        &ctx.sound(&victim, "ITEM_FIRECHARGE_USE")
    }

    &damager.setFireTicks(&ticks)
    true
}
