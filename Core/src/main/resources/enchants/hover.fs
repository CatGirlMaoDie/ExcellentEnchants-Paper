// Hover — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Hover"
max-level = 3
weight = "common"
description = ["%enchantment_trigger_chance%% chance for arrow to have %enchantment_potion_type% %enchantment_potion_level% (%enchantment_potion_duration%s.)"]
supported-items = "BOW_CROSSBOW"
trades = ["PLAINS_SPECIAL"]

arrow-effects = "BUBBLE_POP"
trigger-chance = [base: 4, per-level: 3]
potion-effect = [type: "LEVITATION", duration: [base: 4, per-level: 1, capacity: 10]]

def on-shoot(ctx) {
    arrow = &ctx.event.getProjectile()
    if !(&arrow is org.bukkit.entity.Arrow) {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    &ctx.disablePickup(&arrow)
    &ctx.effectArrow(&arrow)
}
