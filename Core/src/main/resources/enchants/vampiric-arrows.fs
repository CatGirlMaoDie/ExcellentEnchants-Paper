// Vampiric Arrows — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Vampiric Arrows"
max-level = 3
weight = "rare"
description = ["%enchantment_trigger_chance%% chance to restore %amount%❤ on arrow hit."]
supported-items = "BOW_CROSSBOW"
trades = ["SWAMP_SPECIAL"]

priority = "highest"
arrow-effects = [particle: "DUST", color: "255,0,0"]
trigger-chance = [base: 20, per-level: 5]
heal-amount = [base: 1, per-level: 0.5, capacity: 5]

def on-shoot(ctx) {
    &ctx.event.getProjectile() is org.bukkit.entity.Arrow
}

def on-projectile-damage(ctx) {
    if !&ctx.chance() {
        return
    }

    shooter = &ctx.actor
    if &shooter.isDead() || &shooter.getHealth() <= 0.0 {
        return
    }

    max-health = &ctx.maxHealth(&shooter)
    health = &shooter.getHealth()
    if &health >= &max-health {
        return
    }

    &shooter.setHealth(static java.lang.Math.min(&max-health, &health + &ctx.mod(&heal-amount, &ctx.level)))

    if &ctx.visualEffects() {
        &ctx.particles(&shooter, "HEART", 5)
    }
}
