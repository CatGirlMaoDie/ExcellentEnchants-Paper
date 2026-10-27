// Village Defender — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Village Defender"
max-level = 5
weight = "common"
description = ["Inflicts %amount%❤ more damage to all pillagers."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["PLAINS_COMMON"]

priority = "low"
damage-multiplier = false
damage-amount = [base: 0.5, per-level: 0.5]

def on-attack(ctx) {
    victim = &ctx.victim
    if !(&victim is org.bukkit.entity.Illager) {
        return false
    }

    damage = &ctx.event.getDamage()
    extra = &ctx.mod(&damage-amount, &ctx.level)
    &ctx.event.setDamage(if &damage-multiplier { &damage * &extra } else { &damage + &extra })

    if &ctx.visualEffects() {
        &ctx.particles(&victim, "ANGRY_VILLAGER", 30, 0.25)
    }

    true
}
