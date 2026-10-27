// Curse of Mediocrity — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Curse of Mediocrity"
max-level = 3
weight = "uncommon"
curse = true
description = ["%enchantment_trigger_chance%% chance to disenchant item drops."]
supported-items = "TOOLS_WEAPONS"
primary-items = "ALL_WEAPON"
treasure = true
trades = ["SNOW_COMMON"]

priority = "highest"
trigger-chance = [base: 0, per-level: 15]

def on-drop(ctx) {
    if !&ctx.chance() {
        return false
    }

    for drop in &ctx.event.getItems() {
        stack = &drop.getItemStack()
        &ctx.removeAllEnchants(&stack)
        &drop.setItemStack(&stack)
    }

    true
}

def on-kill(ctx) {
    if !&ctx.chance() {
        return false
    }

    for stack in &ctx.event.getDrops() {
        meta = &stack.getItemMeta()
        for enchant in &meta.getEnchants().keySet() {
            &meta.removeEnchant(&enchant)
        }
        &stack.setItemMeta(&meta)
    }

    true
}
