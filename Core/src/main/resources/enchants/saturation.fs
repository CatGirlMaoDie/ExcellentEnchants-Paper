// Saturation — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Saturation"
max-level = 2
weight = "rare"
description = ["Restores %amount% food points every few seconds."]
supported-items = "HELMET"
trades = ["SAVANNA_SPECIAL"]

period = 15
feed-amount = [base: 0, per-level: 1, capacity: 5]
max-food-level = 20

def on-passive(ctx) {
    player = &ctx.player
    if &player == null {
        return false
    }

    food = &player.getFoodLevel()
    if &food >= &max-food-level {
        return false
    }

    amount = &ctx.asInt(&ctx.mod(&feed-amount, &ctx.level))
    &player.setFoodLevel(static java.lang.Math.min(20, &food + &amount))
    true
}
