// Wisdom — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Wisdom"
max-level = 5
weight = "uncommon"
description = ["Mobs drops x%modifier% more XP."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["DESERT_COMMON"]

xp-modifier = [base: 1, per-level: 0.5, capacity: 3]

def on-kill(ctx) {
    modifier = &ctx.mod(&xp-modifier, &ctx.level)
    exp = &ctx.event.getDroppedExp()
    &ctx.event.setDroppedExp(&ctx.asInt(static java.lang.Math.ceil(&exp * &modifier)))
    true
}
