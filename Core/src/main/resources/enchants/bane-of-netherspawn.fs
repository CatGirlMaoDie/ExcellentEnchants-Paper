// Bane of Netherspawn — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Bane of Netherspawn"
max-level = 5
weight = "common"
description = ["Inflicts %damage%❤ more damage to nether mobs."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["PLAINS_COMMON"]

priority = "low"
damage-multiplier = false
damage-amount = [base: 0.75, per-level: 0.25]
nether-mobs = ["BLAZE", "MAGMA_CUBE", "WITHER_SKELETON", "GHAST", "WITHER", "PIGLIN", "PIGLIN_BRUTE", "ZOGLIN", "HOGLIN", "STRIDER", "ZOMBIFIED_PIGLIN"]

def on-attack(ctx) {
    type = &ctx.victim.getType().name()
    matched = false
    for mob in &nether-mobs {
        if &type == &mob {
            matched = true
        }
    }
    if !&matched {
        return false
    }

    damage = &ctx.event.getDamage()
    extra = &ctx.mod(&damage-amount, &ctx.level)
    &ctx.event.setDamage(if &damage-multiplier { &damage * &extra } else { &damage + &extra })

    if &ctx.visualEffects() {
        &ctx.particles(&ctx.victim, "SMOKE", 20, 0.25)
        &ctx.particles(&ctx.victim, "LAVA", 5, 0.25)
    }

    true
}
