// Silk Chest — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Silk Chest"
max-level = 1
weight = "very-rare"
description = ["Drop chests and saves all its content."]
supported-items = "MINING_TOOLS"
primary-items = "AXE"
trades = ["SAVANNA_COMMON"]

priority = "low"
drop-priority = "low"

def on-drop(ctx) {
    if !&ctx.chance() {
        return false
    }

    state = &ctx.event.getBlockState()
    if !(&state is org.bukkit.block.Chest) {
        return false
    }

    drops = &ctx.event.getItems()
    block-type = &state.getType()

    original = null
    for drop in &drops {
        stack = &drop.getItemStack()
        if &stack.getType() == &block-type && &stack.getAmount() == 1 {
            original = &drop
        }
    }
    if &original == null || &drops.size() <= 1 {
        return false
    }

    &drops.remove(&original)

    chest-item = &state.getBlockInventory()
    for drop in &drops {
        &chest-item.addItem(&drop.getItemStack())
    }
    &drops.clear()

    item = &ctx.itemOf(&block-type)
    meta = &item.getItemMeta()
    if !(&meta is org.bukkit.inventory.meta.BlockStateMeta) {
        return false
    }

    chest-state = &meta.getBlockState()
    contents = &chest-item.getContents()
    &chest-state.getBlockInventory().setContents(&contents)
    &chest-state.update(true)
    &meta.setBlockState(&chest-state)
    &item.setItemMeta(&meta)

    location = &state.getLocation()
    &location.getWorld().dropItemNaturally(&location, &item)
    &chest-item.clear()

    true
}

def on-place(ctx) {
    block = &ctx.event.getBlock()
    if !(&block.getType().name()::contains("CHEST")) {
        return
    }

    meta = &ctx.item.getItemMeta()
    if !(&meta is org.bukkit.inventory.meta.BlockStateMeta) {
        return
    }

    chest-item = &meta.getBlockState()
    if !(&chest-item is org.bukkit.block.Chest) {
        return
    }

    chest-block = &block.getState()
    &chest-block.getBlockInventory().setContents(&chest-item.getBlockInventory().getContents())
    &chest-block.update(true)
}
