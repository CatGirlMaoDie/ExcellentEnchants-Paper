// Cutter — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Cutter"
max-level = 3
weight = "rare"
description = ["%enchantment_trigger_chance%% chance to throw away enemy''s armor and damage it for %damage%%."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["PLAINS_COMMON"]

durability-reduction = [base: 0, per-level: 0.01, capacity: 1]
allow-players = true
allow-mobs = true
ignore-mythic-mobs = false

def on-attack(ctx) {
    victim = &ctx.victim
    equipment = &victim.getEquipment()
    if &equipment == null {
        return false
    }

    is-player = &victim is org.bukkit.entity.Player
    if &is-player && !&allow-players {
        return false
    }
    if !&is-player && !&allow-mobs {
        return false
    }
    if !&ignore-mythic-mobs && &ctx.isMythicMob(&victim) {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    armor = &equipment.getArmorContents()
    if &armor.length == 0 {
        return false
    }

    index = &ctx.random(0, &armor.length - 1)
    piece = &armor[&index]
    if &piece == null || &piece.getType().isAir() || &piece.getType().getMaxDurability() == 0 {
        return false
    }

    meta = &piece.getItemMeta()
    if !(&meta is org.bukkit.inventory.meta.Damageable) {
        return false
    }

    &meta.setDamage(&ctx.asInt(&piece.getType().getMaxDurability() * &ctx.mod(&durability-reduction,
        &ctx.level)))
    &piece.setItemMeta(&meta)

    &armor[&index] = null
    &equipment.setArmorContents(&armor)

    drop = &victim.getWorld().dropItemNaturally(&victim.getLocation(), &piece)
    &drop.setPickupDelay(50)

    if &ctx.visualEffects() {
        &ctx.particles(&victim, "ITEM", 30, 0.25)
        &ctx.sound(&victim, "ENTITY_ITEM_BREAK")
    }

    true
}
