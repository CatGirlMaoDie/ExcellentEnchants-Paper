// Flare — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Flare"
max-level = 1
weight = "uncommon"
description = ["%enchantment_trigger_chance%% chance to create a torch where arrow lands."]
supported-items = "BOW"
treasure = true
trades = ["SNOW_COMMON"]

priority = "high"
arrow-effects = "ELECTRIC_SPARK"
charges = [max: [base: 50], consume: 1, recharge: 1, fuel: "TORCH"]

def on-shoot(ctx) {
    &ctx.event.getProjectile() is org.bukkit.entity.Arrow
}

def on-hit(ctx) {
    block = &ctx.event.getHitBlock()
    if &block == null {
        return
    }

    face = &ctx.event.getHitBlockFace()
    if &face == null || &face.name() == "DOWN" {
        return
    }

    relative = &block.getRelative(&face)
    if !&relative.getType().isAir() {
        return
    }

    if &face.name() == "UP" {
        &relative.setType(&ctx.material("TORCH"))
    }
    else {
        &relative.setType(&ctx.material("WALL_TORCH"))
        data = &relative.getBlockData()
        &data.setFacing(&face)
        &relative.setBlockData(&data, true)
    }
}
