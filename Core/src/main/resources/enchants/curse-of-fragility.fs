// Curse of Fragility — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Curse of Fragility"
max-level = 1
weight = "common"
curse = true
description = ["Prevents an item from being grindstoned or anviled."]
supported-items = "BREAKABLE"
treasure = true
trades = ["TAIGA_COMMON"]

def on-click(ctx) {
    view = &ctx.event.getView()
    type = &view.getMenuType()

    if (&type == &view.getTopInventory().getType()) {
        // no-op: kept for readability
    }

    name = &type.name()
    if &name == "ANVIL" || &name == "GRINDSTONE" {
        &ctx.event.setCancelled(true)
        return true
    }

    false
}
