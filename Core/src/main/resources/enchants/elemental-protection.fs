// Elemental Protection — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Elemental Protection"
max-level = 4
weight = "common"
description = ["Reduces potion and elemental damage by %amount%%."]
supported-items = "ARMOR"
trades = ["SWAMP_COMMON"]

damage-bonus = "multiplier"
protection-amount = [base: 0, per-level: 5, capacity: 25]
protection-capacity = 80

def on-protection(ctx) {
    type = &ctx.event.getDamageSource().getDamageType().name()
    if !(&type == "WITHER" || &type == "MAGIC" || &type == "FREEZE" || &type == "LIGHTNING_BOLT") {
        return false
    }

    amount = &ctx.mod(&protection-amount, &ctx.level)
    if &amount <= 0.0 {
        return false
    }

    &ctx.damageBonus.addPenalty(&amount, &protection-capacity)
    true
}
