// Dragonfire Arrows — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Dragonfire Arrows"
max-level = 3
weight = "rare"
description = ["%enchantment_trigger_chance%% chance for arrow to have Dragonfire Effect (R=%radius%, %duration%s)."]
supported-items = "BOW_CROSSBOW"
trades = ["SWAMP_SPECIAL"]

arrow-effects = "DRAGON_BREATH"
trigger-chance = [base: 4, per-level: 3]
cloud-duration = [base: 40, per-level: 20, capacity: 1200]
cloud-radius = [base: 0, per-level: 1, capacity: 5]

def on-shoot(ctx) {
    true
}

def on-hit(ctx) {
    if !&ctx.chance() {
        return
    }
    if &ctx.event.getHitEntity() != null {
        return
    }

    cloud = create-cloud(&ctx, &ctx.projectile.getLocation())
}

def on-projectile-damage(ctx) {
    if !&ctx.chance() {
        return
    }

    cloud = create-cloud(&ctx, &ctx.victim.getLocation())
}

def create-cloud(ctx, location) {
    world = &location.getWorld()
    cloud = &ctx.spawn("AREA_EFFECT_CLOUD", &location)

    &cloud.clearCustomEffects()
    &cloud.setSource(&ctx.actor)
    &cloud.setParticle(&ctx.particle("DRAGON_BREATH"), 1.0)
    &cloud.setRadius(&ctx.asFloat(&ctx.mod(&cloud-radius, &ctx.level)))
    &cloud.setDuration(&ctx.asInt(&ctx.mod(&cloud-duration, &ctx.level)))
    &cloud.setRadiusPerTick((7.0 - &cloud.getRadius()) / &cloud.getDuration())
    &cloud.addCustomEffect(&ctx.potionEffect("INSTANT_DAMAGE", 1, 1), true)

    &cloud
}
