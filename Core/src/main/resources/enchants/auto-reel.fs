// Auto Reel — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Auto Reel"
max-level = 1
weight = "very-rare"
description = ["Automatically reels in a hook on bite."]
supported-items = "FISHING_ROD"
treasure = true
trades = ["JUNGLE_SPECIAL"]

priority = "monitor"

def on-fishing(ctx) {
    if !&ctx.isFishingState("BITE") {
        return false
    }

    &ctx.autoReel()
}
