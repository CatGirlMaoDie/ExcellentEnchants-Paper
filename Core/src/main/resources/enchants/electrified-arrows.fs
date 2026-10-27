// Electrified Arrows — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Electrified Arrows"
max-level = 3
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance for an arrow to strike lightning with %damage%❤ extra damage."]
supported-items = "BOW_CROSSBOW"
trades = ["PLAINS_COMMON"]

arrow-effects = "ELECTRIC_SPARK"
trigger-chance = [base: 0, per-level: 5]
damage-amount = [base: 1.25, per-level: 0.25]

def on-shoot(ctx) {
    true
}

def on-hit(ctx) {
    if !&ctx.chance() {
        return
    }
    if &ctx.event.getHitEntity() != null || &ctx.event.getHitBlock() == null {
        return
    }

    block = &ctx.event.getHitBlock()
    &block.getWorld().strikeLightningEffect(&block.getLocation())

    if &ctx.visualEffects() {
        &ctx.particles(&block.getLocation(), "ELECTRIC_SPARK", 120, 0.75)
    }
}

def on-projectile-damage(ctx) {
    if !&ctx.chance() {
        return
    }

    block = &ctx.victim.getLocation().getBlock().getRelative(&ctx.face("DOWN"))
    &block.getWorld().strikeLightningEffect(&block.getLocation())
    &ctx.event.setDamage(&ctx.event.getDamage() + &ctx.mod(&damage-amount, &ctx.level))
}
