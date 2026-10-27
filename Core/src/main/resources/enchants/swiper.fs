// Swiper — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Swiper"
max-level = 3
weight = "rare"
description = ["%enchantment_trigger_chance%% chance to steal %amount% XP from players."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
trades = ["SWAMP_COMMON"]

priority = "normal"
xp-amount = [base: 0, per-level: 1, capacity: 10]

def on-attack(ctx) {
    attacker = &ctx.player
    if &attacker == null {
        return false
    }
    victim = &ctx.victim
    if !(&victim is org.bukkit.entity.Player) {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    total = &victim.getTotalExperience()
    if &total <= 0 {
        return false
    }

    amount = &ctx.mod(&xp-amount, &ctx.level)
    if &amount > &total {
        amount = &total
    }

    &victim.giveExp(0 - &amount)
    &attacker.giveExp(&amount)
    true
}
