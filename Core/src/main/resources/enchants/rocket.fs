// Rocket — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Rocket"
max-level = 3
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to launch your enemy into the space."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
treasure = true
trades = ["JUNGLE_COMMON"]

firework-power = [base: 1, per-level: 0.25, capacity: 3]

def on-attack(ctx) {
    if !&ctx.chance() {
        return false
    }

    victim = &ctx.victim
    if &victim.isInsideVehicle() {
        &victim.leaveVehicle()
    }

    firework = &ctx.launchFirework(&victim.getLocation(), &ctx.mod(&firework-power, &ctx.level))
    if &firework == null {
        return false
    }

    &firework.addPassenger(&victim)
    &ctx.sound(&victim, "ENTITY_FIREWORK_ROCKET_LAUNCH")
    true
}
