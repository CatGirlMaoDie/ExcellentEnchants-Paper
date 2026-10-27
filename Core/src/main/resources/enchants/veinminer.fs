// Veinminer — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Veinminer"
max-level = 3
weight = "rare"
description = ["Mines up to %amount% blocks of the ore vein at once."]
supported-items = "PICKAXE"
trades = ["PLAINS_SPECIAL"]

priority = "lowest"
disable-on-crouch = true
block-limit = [base: 4, per-level: 1, capacity: 16]
affected-blocks = ["COAL_ORE","DEEPSLATE_COAL_ORE","COPPER_ORE","DEEPSLATE_COPPER_ORE","DIAMOND_ORE","DEEPSLATE_DIAMOND_ORE","EMERALD_ORE","DEEPSLATE_EMERALD_ORE","GOLD_ORE","DEEPSLATE_GOLD_ORE","NETHER_GOLD_ORE","IRON_ORE","DEEPSLATE_IRON_ORE","LAPIS_ORE","DEEPSLATE_LAPIS_ORE","REDSTONE_ORE","DEEPSLATE_REDSTONE_ORE","NETHER_QUARTZ_ORE"]


def on-mining(ctx) {
    faces = &ctx.faces("UP,DOWN,EAST,WEST,SOUTH,NORTH")
    if &ctx.isBusy() {
        return false
    }

    player = &ctx.player
    tool = &ctx.item
    if &player == null || &tool == null {
        return false
    }
    if &disable-on-crouch && &player.isSneaking() {
        return false
    }

    block = &ctx.event.getBlock()
    name = &block.getType().name()

    affected = false
    for entry in &affected-blocks {
        if &name == &entry {
            affected = true
        }
    }
    if !&affected {
        return false
    }

    if &block.getDrops(&tool, &player).isEmpty() {
        return false
    }

    limit = static java.lang.Math.min(&ctx.asInt(&ctx.mod(&block-limit, &ctx.level)), 30)
    if &limit < 0 {
        return false
    }

    ores = []
    prepare = []

    for face in &faces {
        relative = &block.getRelative(&face)
        if &relative.getType() == &block.getType() {
            &prepare.add(&relative)
        }
    }

    guard = 0
    while &guard < 64 {
        guard += 1

        changed = false
        for ore in &prepare {
            if !&ores.contains(&ore) {
                &ores.add(&ore)
                changed = true
            }
        }
        if !&changed {
            break
        }
        if &ores.size() >= &limit {
            break
        }

        nearby = []
        for ore in &prepare {
            for face in &faces {
                relative = &ore.getRelative(&face)
                if &relative.getType() == &block.getType() && !&nearby.contains(&relative) {
                    &nearby.add(&relative)
                }
            }
        }

        &prepare.clear()
        for ore in &nearby {
            &prepare.add(&ore)
        }
    }

    for ore in &ores {
        if &ores.size() > &limit {
            break
        }
        &ctx.breakBlock(&ore)
    }

    true
}
