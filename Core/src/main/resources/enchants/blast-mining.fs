// Blast Mining — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Blast Mining"
max-level = 5
weight = "rare"
description = ["%enchantment_trigger_chance%% chance to mine blocks by explosion."]
supported-items = "PICKAXE"
trades = ["PLAINS_COMMON"]

priority = "lowest"
trigger-chance = [base: 0, per-level: 10]
explosion-power = [base: 3, per-level: 0.75, capacity: 8]
min-block-strength = 1.3

def on-mining(ctx) {
    if &ctx.isBusy() {
        return false
    }
    if !&ctx.chance() {
        return false
    }

    block = &ctx.event.getBlock()
    if &block.getType().getHardness() < &min-block-strength {
        return false
    }

    &ctx.explodeBlocks(&block, &ctx.asFloat(&ctx.mod(&explosion-power, &ctx.level)))
}
