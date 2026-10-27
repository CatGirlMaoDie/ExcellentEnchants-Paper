// Cure — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Cure"
max-level = 3
weight = "common"
description = ["%enchantment_trigger_chance%% chance to cure Zombified Piglins and Zombie Villagers on hit."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["SAVANNA_COMMON"]

priority = "monitor"

def on-attack(ctx) {
    victim = &ctx.victim
    player = &ctx.player
    if &player == null {
        return false
    }

    name = &victim.getType().name()
    if !(&name == "ZOMBIFIED_PIGLIN" || &name == "ZOMBIE_VILLAGER") {
        return false
    }
    if !&ctx.chance() {
        return false
    }
    if &ctx.event.getFinalDamage() < &victim.getHealth() {
        return false
    }

    &ctx.event.setCancelled(true)

    if &ctx.visualEffects() {
        &ctx.particles(&victim, "CLOUD", 30)
    }

    world = &victim.getWorld()
    location = &victim.getLocation()

    if &name == "ZOMBIFIED_PIGLIN" {
        &ctx.spawn("PIGLIN", &location)
        &victim.remove()
    }
    else {
        &victim.setConversionTime(1)
        &victim.setConversionPlayer(&player)
    }

    true
}
