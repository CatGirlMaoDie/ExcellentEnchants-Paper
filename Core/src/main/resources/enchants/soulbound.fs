// Soulbound — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Soulbound"
max-level = 1
weight = "rare"
description = ["Protects from being dropped on death."]
supported-items = "BREAKABLE"
treasure = true
trades = ["DESERT_SPECIAL"]

def on-inventory-death(ctx) {
    if &ctx.event.getKeepInventory() {
        return false
    }

    stack = &ctx.item
    &ctx.event.getDrops().remove(&stack)
    &ctx.event.getItemsToKeep().add(&stack)
    true
}
