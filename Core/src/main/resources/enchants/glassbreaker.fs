// Glass Breaker — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Glass Breaker"
max-level = 1
weight = "common"
description = ["Breaks glass instantly"]
supported-items = "TOOL"
primary-items = "MINING_TOOLS"
trades = ["DESERT_COMMON"]

priority = "lowest"

def on-interact(ctx) {
    if !(&ctx.event.getAction().name() == "LEFT_CLICK_BLOCK") {
        return false
    }

    player = &ctx.player
    if &player == null {
        return false
    }

    block = &ctx.event.getClickedBlock()
    if &block == null {
        return false
    }

    name = &block.getType().name().toLowerCase()
    if !(&name::contains("glass")) {
        return false
    }

    if !&player.breakBlock(&block) {
        return false
    }

    &ctx.particles(&block.getLocation(), "BLOCK", 10, 0.5)
    return true
}
