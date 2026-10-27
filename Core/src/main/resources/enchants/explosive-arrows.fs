// Explosive Arrows — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Explosive Arrows"
max-level = 3
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to shoot an explosive arrow."]
supported-items = "BOW_CROSSBOW"
trades = ["TAIGA_COMMON"]

arrow-effects = "SMOKE"
trigger-chance = [base: 3, per-level: 2]
fire-spread = true
damage-blocks = false
damage-items = false
power = [base: 1, per-level: 1, capacity: 5]

def on-shoot(ctx) {
    true
}

def on-hit(ctx) {
    if !&ctx.chance() {
        return
    }

    &ctx.explode(&ctx.projectile.getLocation(), &ctx.asFloat(&ctx.mod(&power, &ctx.level)), &fire-spread,
                 &damage-blocks, &damage-items)
}
