// Tunnel — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Tunnel"
max-level = 3
weight = "very-rare"
description = ["Mines multiple blocks at once in a certain shape."]
supported-items = "MINING_TOOLS"
primary-items = "PICKAXE"
trades = ["SAVANNA_SPECIAL"]

priority = "lowest"
disable-on-crouch = true

def on-mining(ctx) {
    if &ctx.isBusy() {
        return false
    }

    player = &ctx.player
    if &player == null {
        return false
    }
    if &disable-on-crouch && &player.isSneaking() {
        return false
    }

    item = &ctx.item
    if &item == null {
        return false
    }

    block = &ctx.event.getBlock()
    name = &block.getType().name()
    if &block.getType().isInteractable() && !(&name == "REDSTONE_ORE" || &name == "DEEPSLATE_REDSTONE_ORE") {
        return false
    }
    if &block.getDrops(&item).isEmpty() {
        return false
    }

    targets = &ctx.lastTwoTargetBlocks(10)
    if &targets.size() != 2 {
        return false
    }

    target = &targets[1]
    if !&target.getType().isOccluding() {
        return false
    }

    adjacent = &targets[0]
    dir = &target.getFace(&adjacent)
    is-z = &dir.name() == "EAST" || &dir.name() == "WEST"
    vertical = &dir.name() == "UP" || &dir.name() == "DOWN"

    count = 1
    if &ctx.level == 1 {
        count = 2
    }
    else if &ctx.level == 2 {
        count = 5
    }
    else if &ctx.level >= 3 {
        count = 9
    }

    offsets = [[0, 0], [0, -1], [-1, 0], [0, 1], [1, 0], [-1, -1], [-1, 1], [1, -1], [1, 1]]

    index = 0
    while &index < &count {
        offset = &offsets[&index]
        x-add = &offset[0]
        z-add = &offset[1]

        location = &block.getLocation()
        if &vertical {
            other = &location.clone().add(&x-add, 0, &z-add).getBlock()
        }
        else if &is-z {
            other = &location.clone().add(0, &z-add, &x-add).getBlock()
        }
        else {
            other = &location.clone().add(&x-add, &z-add, 0).getBlock()
        }

        index += 1

        if &other.equals(&block) {
            continue
        }
        if &other.getDrops(&item).isEmpty() {
            continue
        }
        if &other.isLiquid() {
            continue
        }

        other-name = &other.getType().name()
        if &other.getType().isInteractable() && !(&other-name == "REDSTONE_ORE" || &other-name == "DEEPSLATE_REDSTONE_ORE") {
            continue
        }
        if &other-name == "BEDROCK" || &other-name == "END_PORTAL" || &other-name == "END_PORTAL_FRAME" {
            continue
        }
        if &other-name == "OBSIDIAN" && !(&other-name == &name) {
            continue
        }

        &ctx.breakBlock(&other)
    }

    true
}
