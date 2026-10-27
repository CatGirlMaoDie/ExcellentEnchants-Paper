// Vampire — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Vampire"
max-level = 3
weight = "rare"
description = ["%enchantment_trigger_chance%% chance to heal for %amount%❤ on hit."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["SAVANNA_COMMON"]

priority = "monitor"
heal-amount = [base: 0.25, per-level: 0.25, capacity: 10]
heal-multiplier = false

def on-attack(ctx) {
    attacker = &ctx.actor
    if !&ctx.chance() {
        return false
    }

    max-health = &ctx.maxHealth(&attacker)
    health = &attacker.getHealth()
    if &health >= &max-health {
        return false
    }

    amount = &ctx.mod(&heal-amount, &ctx.level)
    heal = if &heal-multiplier { &ctx.event.getFinalDamage() * &amount } else { &amount }
    &attacker.setHealth(static java.lang.Math.min(&max-health, &health + &heal))

    if &ctx.visualEffects() {
        &ctx.particles(&attacker, "HEART", 5)
    }

    true
}
