// Survivalist — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Survivalist"
max-level = 1
weight = "rare"
description = ["Automatically cooks fish if what is caught is raw."]
supported-items = "FISHING_ROD"
treasure = true
trades = ["SNOW_SPECIAL"]

def on-fishing(ctx) {
    if !&ctx.isFishingState("CAUGHT_FISH") {
        return false
    }

    drop = &ctx.event.getCaught()
    if !(&drop is org.bukkit.entity.Item) {
        return false
    }

    stack = &drop.getItemStack()
    if !&ctx.isCookable(&stack) {
        return false
    }

    &drop.setItemStack(&ctx.cook(&stack))
    false
}
