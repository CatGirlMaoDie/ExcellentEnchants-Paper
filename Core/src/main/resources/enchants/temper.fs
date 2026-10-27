// Temper — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Temper"
max-level = 5
weight = "very-rare"
description = ["Inflicts %amount%% more damage for each %radius%❤ missing."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["TAIGA_COMMON"]

priority = "lowest"
damage-amount = [base: 0, per-level: 5, capacity: 100]
damage-step = [base: 0.5]

def on-attack(ctx) {
    attacker = &ctx.actor
    max-health = &ctx.maxHealth(&attacker)
    health = &attacker.getHealth()
    if &health >= &max-health {
        return false
    }

    missing = &max-health - &health
    step = &ctx.mod(&damage-step, &ctx.level)
    if &step <= 0 || &missing < &step {
        return false
    }

    steps = static java.lang.Math.floor(&missing / &step)
    if &steps <= 0 {
        return false
    }

    percent = 1.0 + (&ctx.mod(&damage-amount, &ctx.level) * &steps / 100.0)
    &ctx.event.setDamage(&ctx.event.getDamage() * &percent)
    true
}
