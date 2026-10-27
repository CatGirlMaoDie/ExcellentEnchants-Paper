// Smelter — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Smelter"
max-level = 5
weight = "uncommon"
description = ["Smelts mined blocks with %enchantment_trigger_chance%% chance."]
supported-items = "TOOL"
primary-items = "MINING_TOOLS"
trades = ["DESERT_COMMON"]

priority = "low"
trigger-chance = [base: 15, per-level: 5]
disable-on-crouch = true
exempted-items = ["COBBLESTONE"]

def on-drop(ctx) {
    player = &ctx.player
    if &disable-on-crouch && &player != null && &player.isSneaking() {
        return false
    }
    if &ctx.event.getBlockState() is org.bukkit.block.Container {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    smelted = false
    drops = &ctx.event.getItems()
    iterator = &drops.iterator()

    while &iterator.hasNext() {
        drop = &iterator.next()
        stack = &drop.getItemStack()
        name = &stack.getType().name()

        exempt = false
        for entry in &exempted-items {
            if &name == &entry {
                exempt = true
            }
        }
        if &exempt {
            continue
        }

        result = &ctx.smelt(&stack)
        if &result == null {
            continue
        }

        &ctx.addDrop(&result)
        &iterator.remove()
        smelted = true
    }

    if &smelted && &ctx.visualEffects() {
        &ctx.particles(&ctx.event.getBlock().getLocation(), "FLAME", 20, 0.25)
        &ctx.sound(&ctx.event.getBlock(), "BLOCK_LAVA_EXTINGUISH")
    }

    &smelted
}
