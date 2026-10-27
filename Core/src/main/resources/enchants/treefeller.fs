// Treefeller — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Treefeller"
max-level = 1
weight = "rare"
description = ["Cuts down an entire tree."]
supported-items = "AXE"
trades = ["TAIGA_SPECIAL"]

priority = "lowest"
disable-on-crouch = true
block-limit = [base: 16, per-level: 8, capacity: 180]


def is-log(material) {
    &ctx.isLog(&material)
}

def is-leaves(material) {
    &ctx.isLeaves(&material)
}

def on-mining(ctx) {
    sides = &ctx.faces("UP,DOWN,EAST,WEST,SOUTH,NORTH,NORTH_EAST,NORTH_WEST,SOUTH_EAST,SOUTH_WEST")
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
    if !is-log(&block.getType()) {
        return false
    }

    limit = &ctx.asInt(&ctx.mod(&block-limit, &ctx.level))
    found = []
    lookup = [&block]

    guard = 0
    while &guard < 200 {
        guard += 1
        if &found.size() >= &limit {
            break
        }

        changed = false
        for entry in &lookup {
            if !&found.contains(&entry) {
                &found.add(&entry)
                changed = true
            }
        }
        if !&changed {
            break
        }

        connected = []
        for entry in &lookup {
            leaves = is-leaves(&entry.getType())
            for face in &sides {
                other = &entry.getRelative(&face)
                type = &other.getType()
                if !(is-log(&type) || is-leaves(&type)) {
                    continue
                }
                if &leaves && is-leaves(&type) {
                    continue
                }
                if !&connected.contains(&other) {
                    &connected.add(&other)
                }
            }
        }

        &lookup.clear()
        for entry in &connected {
            &lookup.add(&entry)
        }
    }

    for log in &found {
        if &tool.getAmount() <= 0 {
            break
        }
        if !is-log(&log.getType()) {
            continue
        }
        if &log.equals(&block) {
            continue
        }
        &ctx.breakBlock(&log)
    }

    true
}
