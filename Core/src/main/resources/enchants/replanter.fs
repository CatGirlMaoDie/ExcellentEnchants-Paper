// Replanter — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Replanter"
max-level = 1
weight = "very-rare"
description = ["Automatically replant crops on right click and when harvest."]
supported-items = "HOE"
trades = ["PLAINS_COMMON"]

priority = "highest"
replant-on-right-click = true
replant-on-plant-break = true

crops = [WHEAT_SEEDS: "WHEAT", BEETROOT_SEEDS: "BEETROOTS", MELON_SEEDS: "MELON_STEM",
         PUMPKIN_SEEDS: "PUMPKIN_STEM", POTATO: "POTATOES", CARROT: "CARROTS",
         NETHER_WART: "NETHER_WART"]

def take-seeds(player, material) {
    inventory = &player.getInventory()
    slot = &inventory.first(&material)
    if &slot < 0 {
        return false
    }

    seed = &inventory.getItem(&slot)
    if &seed == null || &seed.getType().isAir() {
        return false
    }

    &seed.setAmount(&seed.getAmount() - 1)
    true
}

def on-interact(ctx) {
    if !&replant-on-right-click {
        return false
    }

    player = &ctx.player
    if &player == null {
        return false
    }

    action = &ctx.event.getAction().name()
    if !(&action == "RIGHT_CLICK_BLOCK") {
        return false
    }

    offhand = &player.getInventory().getItemInOffHand()
    if !&offhand.getType().isAir() && &crops.containsKey(&offhand.getType()) {
        return false
    }

    ground = &ctx.event.getClickedBlock()
    if &ground == null {
        return false
    }

    ground-name = &ground.getType().name()
    if !(&ground-name == "FARMLAND" || &ground-name == "SOUL_SAND") {
        return false
    }

    plant = &ground.getRelative(&ctx.face("UP"))
    if !&plant.isEmpty() {
        return false
    }

    for entry in &crops.entrySet() {
        seed-name = &entry.getKey().name()
        is-wart = &seed-name == "NETHER_WART"

        allowed = if &is-wart { &ground-name == "SOUL_SAND" } else { &ground-name == "FARMLAND" }
        if !&allowed {
            continue
        }

        if take-seeds(&player, &entry.getKey()) {
            &plant.setType(&ctx.material(&entry.getValue()))
            &ctx.sound(&player, if &is-wart { "ITEM_NETHER_WART_PLANT" } else { "ITEM_CROP_PLANT" })
            &player.swingMainHand()
            break
        }
    }

    true
}

def on-mining(ctx) {
    if !&replant-on-plant-break {
        return false
    }

    player = &ctx.player
    if &player == null {
        return false
    }

    plant = &ctx.event.getBlock()
    data = &plant.getBlockData()
    if !(&data is org.bukkit.block.data.Ageable) {
        return false
    }

    material = &data.getPlacementMaterial()
    if !&crops.containsKey(&material) {
        return false
    }

    if take-seeds(&player, &material) {
        &ctx.setBlockLater(&plant, &data.getMaterial().name())
    }

    true
}
