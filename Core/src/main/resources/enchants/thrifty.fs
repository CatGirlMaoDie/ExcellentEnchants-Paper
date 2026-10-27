// Thrifty — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Thrifty"
max-level = 3
weight = "rare"
description = ["%enchantment_trigger_chance%% chance for mobs to drop spawn egg."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
treasure = true
trades = ["JUNGLE_SPECIAL"]

ignored-mobs = ["WITHER", "ENDER_DRAGON"]
ignored-spawn-reasons = ["SPAWNER_EGG", "SPAWNER", "DISPENSE_EGG"]
ignore-mythic-mobs = true

def on-kill(ctx) {
    if !&ctx.chance() {
        return false
    }

    victim = &ctx.victim
    type = &victim.getType().name()

    for ignored in &ignored-mobs {
        if &type == &ignored {
            return false
        }
    }

    if &ignore-mythic-mobs && &ctx.isMythicMob(&victim) {
        return false
    }

    reason = &ctx.spawnReason(&victim)
    if &reason != null {
        for ignored in &ignored-spawn-reasons {
            if &reason == &ignored {
                return false
            }
        }
    }

    egg = &ctx.spawnEgg(&type)
    if &egg == null {
        return false
    }

    &ctx.event.getDrops().add(&egg)
    true
}
