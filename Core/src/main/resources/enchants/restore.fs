// Restore — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Restore"
max-level = 3
weight = "rare"
description = ["%enchantment_trigger_chance%% chance to save item from breaking back to %amount%%"]
supported-items = "BREAKABLE"
trades = ["DESERT_COMMON"]

priority = "monitor"
restore-amount = [base: 15, per-level: 5, capacity: 100]

def on-item-damage(ctx) {
    if !&ctx.chance() {
        return false
    }

    item = &ctx.item
    meta = &item.getItemMeta()
    if !(&meta is org.bukkit.inventory.meta.Damageable) {
        return false
    }

    damage = &ctx.event.getDamage()
    max-durability = &item.getType().getMaxDurability()
    if &meta.getDamage() + &damage < &max-durability {
        return false
    }

    &ctx.event.setCancelled(true)

    percent = 100.0 - &ctx.mod(&restore-amount, &ctx.level)
    &meta.setDamage(&ctx.asInt(&max-durability * (&percent / 100.0)))
    &meta.removeEnchant(&ctx.enchantment.getBukkitEnchantment())
    &item.setItemMeta(&meta)

    if &ctx.visualEffects() {
        &ctx.sound(&ctx.actor, "ITEM_TOTEM_USE")
    }

    true
}
