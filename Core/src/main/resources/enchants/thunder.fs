// Thunder — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Thunder"
max-level = 5
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to strike lightning with %damage%❤ extra damage."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["PLAINS_COMMON"]

priority = "low"
thunderstorm-only = false
damage-modifier = [base: 1.25, per-level: 0.25]

def on-attack(ctx) {
    victim = &ctx.victim
    world = &victim.getWorld()
    if &thunderstorm-only && !&world.isThundering() {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    location = &victim.getLocation()
    if &location.getBlock().getLightFromSky() != 15 {
        return false
    }

    &world.strikeLightningEffect(&location)

    if &ctx.visualEffects() {
        &ctx.particles(&location, "ELECTRIC_SPARK", 120, 0.75)
    }

    &ctx.event.setDamage(&ctx.event.getDamage() + &ctx.mod(&damage-modifier, &ctx.level))
    true
}
