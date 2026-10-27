// Lingering — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Lingering"
max-level = 3
weight = "rare"
description = ["%enchantment_trigger_chance%% chance for tipped arrows to generate a lingering effect."]
supported-items = "BOW_CROSSBOW"
trades = ["SAVANNA_COMMON"]

trigger-chance = [base: 5, per-level: 5]

def on-shoot(ctx) {
    &ctx.event.getProjectile() is org.bukkit.entity.Arrow
}

def on-hit(ctx) {
    if &ctx.event.getHitEntity() != null {
        return
    }
    if !&ctx.chance() {
        return
    }

    arrow = &ctx.projectile
    location = &arrow.getLocation()

    cloud = &ctx.spawn("AREA_EFFECT_CLOUD", &location)
    if &cloud == null {
        return
    }

    &cloud.clearCustomEffects()
    &cloud.setSource(&ctx.actor)
    &cloud.setWaitTime(10)
    &cloud.setRadius(3.0)
    &cloud.setRadiusOnUse(-0.5)
    &cloud.setDuration(600)
    &cloud.setRadiusPerTick(0.0 - (&cloud.getRadius() / &cloud.getDuration()))

    if &arrow.getBasePotionType() != null {
        &cloud.setBasePotionType(&arrow.getBasePotionType())
    }

    if &arrow.hasCustomEffects() {
        for effect in &arrow.getCustomEffects() {
            &cloud.addCustomEffect(&effect, false)
        }
    }
}
