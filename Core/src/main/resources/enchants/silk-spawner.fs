// Silk Spawner — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Silk Spawner"
max-level = 1
weight = "very-rare"
description = ["%enchantment_trigger_chance%% chance to mine spawner."]
supported-items = "MINING_TOOLS"
primary-items = "PICKAXE"
treasure = true
trades = ["JUNGLE_SPECIAL"]

priority = "low"
trigger-chance = [base: 0, per-level: 25]
place-in-containers = true

def on-mining(ctx) {
    block = &ctx.event.getBlock()
    state = &block.getState()
    if !(&state is org.bukkit.block.CreatureSpawner) {
        return false
    }
    if !&ctx.chance() {
        return false
    }
    if &state.getSpawnedType() == null {
        return false
    }

    &ctx.event.setExpToDrop(0)
    &ctx.event.setDropItems(true)

    item = &ctx.item("SPAWNER")
    meta = &item.getItemMeta()
    if &meta == null {
        return false
    }

    spawner-item = &meta.getBlockState()
    transfer-settings(&state, &spawner-item)
    &spawner-item.update(true)
    &meta.setBlockState(&spawner-item)
    &item.setItemMeta(&meta)

    &ctx.enchantment.getManager().setBlockEnchant(&item, &ctx.enchantment)

    world = &block.getWorld()
    location = &block.getLocation().add(0.5, 0.5, 0.5)
    &world.dropItemNaturally(&location, &item)
    true
}

def on-place(ctx) {
    block = &ctx.event.getBlock()
    if !(&block.getType().name() == "SPAWNER") {
        return
    }

    meta = &ctx.item.getItemMeta()
    if !(&meta is org.bukkit.inventory.meta.BlockStateMeta) {
        return
    }

    spawner-item = &meta.getBlockState()
    if !(&spawner-item is org.bukkit.block.CreatureSpawner) {
        return
    }

    spawner-block = &block.getState()
    transfer-settings(&spawner-item, &spawner-block)
    &spawner-block.update(true)
}

def transfer-settings(from, to) {
    &to.setPotentialSpawns(&ctx.emptySet())

    if &from.getPotentialSpawns().isEmpty() {
        &to.setSpawnedType(&from.getSpawnedType())
    }
    else {
        for spawn in &from.getPotentialSpawns() {
            &to.addPotentialSpawn(&spawn)
        }
    }

    &to.setMinSpawnDelay(&from.getMinSpawnDelay())
    &to.setMaxSpawnDelay(&from.getMaxSpawnDelay())
    &to.setMaxNearbyEntities(&from.getMaxNearbyEntities())
    &to.setRequiredPlayerRange(&from.getRequiredPlayerRange())
    &to.setSpawnCount(&from.getSpawnCount())
    &to.setSpawnRange(&from.getSpawnRange())
}
