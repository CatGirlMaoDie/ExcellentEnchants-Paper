// River Master — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "River Master"
max-level = 5
weight = "common"
description = ["Increases casting distance."]
supported-items = "FISHING_ROD"
trades = ["PLAINS_COMMON"]

priority = "lowest"
velocity-mod = [base: 1, per-level: 0.25, capacity: 3]

def on-fishing(ctx) {
    if !&ctx.isFishingState("FISHING") {
        return false
    }

    hook = &ctx.event.getHook()
    factor = &ctx.mod(&velocity-mod, &ctx.level)
    &hook.setVelocity(&hook.getVelocity().multiply(&factor))
    true
}
