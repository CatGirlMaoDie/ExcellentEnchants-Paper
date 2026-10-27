// Stopping Force — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Stopping Force"
max-level = 3
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to reduce knockback for %amount%%."]
supported-items = "LEGGINGS"
trades = ["PLAINS_COMMON"]

knockback-reduction = [base: 0.3, per-level: 0.2, capacity: 1]

def on-defend(ctx) {
    if !&ctx.chance() {
        return false
    }

    reduction = 1.0 - static java.lang.Math.max(0.0, &ctx.mod(&knockback-reduction, &ctx.level))
    &ctx.delayVelocity(&ctx.actor, &reduction)
    true
}
