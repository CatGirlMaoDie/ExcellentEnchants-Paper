// Bomber — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Bomber"
max-level = 3
weight = "very-rare"
description = ["%enchantment_trigger_chance%% chance to shoot a TNT ignited for %time%s."]
supported-items = "BOW_CROSSBOW"
treasure = true
trades = ["DESERT_SPECIAL"]

priority = "lowest"
charges = [max: [base: 50, per-level: 10], consume: 1, recharge: 1, fuel: "TNT"]
fuse-ticks = [base: 40, per-level: 10, capacity: 200]

def on-shoot(ctx) {
    arrow = &ctx.event.getProjectile()
    if !(&arrow is org.bukkit.entity.Projectile) {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    fuse = static java.lang.Math.max(1, &ctx.asInt(&ctx.mod(&fuse-ticks, &ctx.level)))
    tnt = &ctx.spawn("TNTPrimed", &arrow.getLocation())
    &tnt.setVelocity(&arrow.getVelocity().multiply(&ctx.event.getForce() * 1.25))
    &tnt.setFuseTicks(&fuse)
    &tnt.setSource(&ctx.actor)
    &ctx.event.setProjectile(&tnt)
    true
}
