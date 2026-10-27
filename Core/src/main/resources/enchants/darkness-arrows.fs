// Darkness Arrows — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Darkness Arrows"
max-level = 3
weight = "common"
description = ["%enchantment_trigger_chance%% chance for arrow to have %enchantment_potion_type% %enchantment_potion_level% (%enchantment_potion_duration%s.)"]
supported-items = "BOW_CROSSBOW"
trades = ["SNOW_COMMON"]

arrow-effects = "ASH"
trigger-chance = [base: 10, per-level: 5]
potion-effect = [type: "DARKNESS", duration: [base: 2.5, per-level: 0.5]]

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
