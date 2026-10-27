// Confusing Arrows — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Confusing Arrows"
max-level = 3
weight = "common"
description = ["%enchantment_trigger_chance%% chance for arrow to have %enchantment_potion_type% %enchantment_potion_level% (%enchantment_potion_duration%s.)"]
supported-items = "BOW_CROSSBOW"
trades = ["SWAMP_COMMON"]

arrow-effects = [particle: "ENTITY_EFFECT", color: "200,100,100"]
trigger-chance = [base: 15, per-level: 5]
potion-effect = [type: "NAUSEA", duration: [base: 6, per-level: 1]]

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
