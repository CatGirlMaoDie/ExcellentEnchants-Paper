// Seasoned Angler — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Seasoned Angler"
max-level = 3
weight = "uncommon"
description = ["Increases amount of XP gained from fishing by %amount%%."]
supported-items = "FISHING_ROD"
trades = ["PLAINS_COMMON"]

xp-modifier = [base: 0, per-level: 50, capacity: 300]

def on-fishing(ctx) {
    if !&ctx.isFishingState("CAUGHT_FISH") {
        return false
    }

    exp = &ctx.event.getExpToDrop()
    if &exp <= 0 {
        return false
    }

    percent = &ctx.mod(&xp-modifier, &ctx.level)
    modified = static java.lang.Math.ceil(&exp * (1.0 + &percent / 100.0))
    &ctx.event.setExpToDrop(&ctx.asInt(&modified))
    true
}
