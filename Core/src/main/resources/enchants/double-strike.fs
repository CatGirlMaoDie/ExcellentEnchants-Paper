// Double Strike — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Double Strike"
max-level = 2
weight = "very-rare"
description = ["%enchantment_trigger_chance%% chance to inflict double damage."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["TAIGA_COMMON"]

priority = "low"

def on-attack(ctx) {
    if !&ctx.chance() {
        return false
    }

    &ctx.event.setDamage(&ctx.event.getDamage() * 2.0)

    if &ctx.visualEffects() {
        &ctx.particles(&ctx.victim, "EXPLOSION", 15, 0.25)
        &ctx.sound(&ctx.victim, "ENTITY_GENERIC_EXPLODE")
    }

    true
}
