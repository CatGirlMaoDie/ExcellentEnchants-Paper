// Double Catch — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Double Catch"
max-level = 3
weight = "rare"
description = ["Increases amount of caught item by x2 with %enchantment_trigger_chance%% chance."]
supported-items = "FISHING_ROD"
trades = ["PLAINS_COMMON"]

priority = "highest"

def on-fishing(ctx) {
    if !&ctx.isFishingState("CAUGHT_FISH") {
        return false
    }

    caught = &ctx.event.getCaught()
    if !(&caught is org.bukkit.entity.Item) {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    stack = &caught.getItemStack()
    amount = &stack.getAmount() * 2
    max = &stack.getMaxStackSize()
    &stack.setAmount(if &amount > &max { &max } else { &amount })
    &caught.setItemStack(&stack)
    true
}
