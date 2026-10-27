// Flame Walker — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Flame Walker"
max-level = 2
weight = "very-rare"
description = ["Ability to walk on lava, immunity to magma damage."]
supported-items = "BOOTS"
treasure = true
trades = ["DESERT_SPECIAL"]

radius = [base: 1, per-level: 1, capacity: 16]
block-decay = [base: 8, per-level: 1, capacity: 15]


def on-move(ctx) {
    faces = &ctx.faces("SOUTH,NORTH,EAST,WEST")
    to = &ctx.event.getTo()
    next-block = &to.getBlock().getRelative(&ctx.face("DOWN"))

    has-lava = false
    for face in &faces {
        if &next-block.getRelative(&face).getType().name() == "LAVA" {
            has-lava = true
        }
    }
    if !&has-lava {
        return false
    }

    radius = &ctx.asInt(&ctx.mod(&radius, &ctx.level))
    location = &ctx.actor.getLocation()
    world = &location.getWorld()
    center-x = &location.getBlockX()
    center-z = &location.getBlockZ()
    fixed-y = &location.getBlockY() - 1

    changed = false
    x = &center-x - &radius
    while &x <= &center-x + &radius {
        z = &center-z - &radius
        while &z <= &center-z + &radius {
            dx = &center-x - &x
            dz = &center-z - &z
            if &dx * &dx + &dz * &dz <= &radius * &radius {
                block = &world.getBlockAt(&x, &fixed-y, &z)
                if &block.getType().name() == "LAVA" {
                    if &block.getBlockData() is org.bukkit.block.data.Levelled {
                        if &block.getBlockData().getLevel() == 0 {
                            above = &block.getRelative(&ctx.face("UP"))
                            if &above.isEmpty() {
                                if &ctx.formBlock(&block, "MAGMA_BLOCK") {
                                    life = &ctx.random(1, static java.lang.Math.max(1,
                                        &ctx.asInt(&ctx.mod(&block-decay, &ctx.level))))
                                    &ctx.addTickedBlock(&block, "LAVA", "MAGMA_BLOCK", &life)
                                    changed = true
                                }
                            }
                        }
                    }
                }
            }
            z += 1
        }
        x += 1
    }

    &changed
}

def on-protection(ctx) {
    type = &ctx.event.getDamageSource().getDamageType().name()
    if !(&type == "HOT_FLOOR") {
        return false
    }

    &ctx.event.setCancelled(true)
    true
}
