// Curse of Drowned — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Curse of Drowned"
max-level = 3
weight = "uncommon"
curse = true
description = ["%enchantment_trigger_chance%% chance to fish up a Drowned Zombie."]
supported-items = "FISHING_ROD"
treasure = true
trades = ["SWAMP_COMMON"]

priority = "lowest"

def on-fishing(ctx) {
    if !&ctx.isFishingState("CAUGHT_FISH") {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    hook = &ctx.event.getHook()
    drowned = &ctx.spawn("DROWNED", &hook.getLocation())
    &hook.setHookedEntity(&drowned)
    &hook.pullHookedEntity()
    &ctx.event.setCancelled(true)

    if &ctx.visualEffects() {
        &ctx.particles(&hook.getLocation(), "UNDERWATER", 50, 0.75)
        &ctx.sound(&ctx.actor, "ENTITY_DROWNED_AMBIENT")
    }

    true
}
