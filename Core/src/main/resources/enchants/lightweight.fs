// Lightweight — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Lightweight"
max-level = 1
weight = "common"
description = ["You can safely jump on turtle eggs, farmlands and big dripleaf."]
supported-items = "BOOTS"
trades = ["SNOW_COMMON"]

def on-block-change(ctx) {
    block = &ctx.event.getBlock()
    origin = &block.getType().name()
    target = &ctx.event.getTo().name()

    if &origin == "FARMLAND" && &target == "DIRT" {
        &ctx.event.setCancelled(true)
        return true
    }

    if &origin == "BIG_DRIPLEAF" && &target == "BIG_DRIPLEAF" && !&ctx.actor.isSneaking() {
        &ctx.event.setCancelled(true)
        return true
    }

    false
}

def on-interact(ctx) {
    if !(&ctx.event.getAction().name() == "PHYSICAL") {
        return false
    }

    block = &ctx.event.getClickedBlock()
    if &block == null {
        return false
    }

    if &block.getType().name() == "TURTLE_EGG" {
        &ctx.event.setCancelled(true)
        return true
    }

    false
}
