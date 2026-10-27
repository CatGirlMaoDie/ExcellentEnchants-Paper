// Telekinesis — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Telekinesis"
max-level = 1
weight = "very-rare"
description = ["Moves all blocks loot directly to your inventory."]
supported-items = "TOOL"
primary-items = "MINING_TOOLS"
treasure = true
trades = ["DESERT_SPECIAL"]

priority = "monitor"

def on-drop(ctx) {
    player = &ctx.player
    if &player == null {
        return false
    }

    moved = false
    drops = &ctx.event.getItems()
    iterator = &drops.iterator()
    while &iterator.hasNext() {
        drop = &iterator.next()
        stack = &drop.getItemStack()

        if &ctx.hasSpace(&player, &stack) {
            &ctx.giveTo(&player, &stack)
            &iterator.remove()
            moved = true
        }
    }

    &moved
}
