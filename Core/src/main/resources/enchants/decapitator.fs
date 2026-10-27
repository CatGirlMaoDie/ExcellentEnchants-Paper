// Decapitator — default enchantment script.
//
// Metadata declares the enchantment, the handler(s) below implement its behaviour.
// See docs/SCRIPTING.md for the full reference.

name = "Decapitator"
max-level = 2
weight = "rare"
description = ["%enchantment_trigger_chance%% chance to obtain player's or mob's head."]
supported-items = "SWORDS_AXES"
primary-items = "SWORD"
treasure = true
trades = ["SNOW_SPECIAL"]

priority = "normal"
trigger-chance = [base: 1, per-level: 2]
ignore-mythic-mobs = true
ignored-entities = ["ENDER_DRAGON", "WITHER", "WITHER_SKELETON"]
head-name = "%type%'s Head"
head-textures = [
    AXOLOTL: "5c167410409336acc58e6433ffa8b7f86a8786e35ec7300b9062340281d4691c", BAT: "3820a10db222f69ac2215d7d10dca47eeafa215553764a2b81bafd479e7933d1", BEE: "cce9edbbc5fdc0d8487ac72eab239d2cacfe408d74288d6384b044111ba4de0f",
    BLAZE: "b20657e24b56e1b2f8fc219da1de788c0c24f36388b1a409d0cd2d8dba44aa3b", CAT: "d0dba942c06b77a2828e3f66a1faec5e8643e9ea61a81a4523279739ed82d", CAVE_SPIDER: "eccc4a32d45d74e8b14ef1ffd55cd5f381a06d4999081d52eaea12e13293e209",
    CHICKEN: "1638469a599ceef7207537603248a9ab11ff591fd378bea4735b346a7fae893", COD: "7892d7dd6aadf35f86da27fb63da4edda211df96d2829f691462a4fb1cab0", COW: "b667c0e107be79d7679bfe89bbc57c6bf198ecb529a3295fcfdfd2f24408dca3",
    DOLPHIN: "8e9688b950d880b55b7aa2cfcd76e5a0fa94aac6d16f78e833f7443ea29fed3", DONKEY: "63a976c047f412ebc5cb197131ebef30c004c0faf49d8dd4105fca1207edaff3", DROWNED: "c84df79c49104b198cdad6d99fd0d0bcf1531c92d4ab6269e40b7d3cbbb8e98c",
    ELDER_GUARDIAN: "1c797482a14bfcb877257cb2cff1b6e6a8b8413336ffb4c29a6139278b436b", ENDERMAN: "c09f1de6135f4bea781c5a8e0d61095f833ee2685d8154ecea814ee6d328a5c6", ENDERMITE: "1730127e3ac7677122422df0028d9e7368bd157738c8c3cddecc502e896be01c",
    EVOKER: "806ac02fd9dac966b7e5806736b6feb90e2f3b0577969e673291b8307c1ef8e5", FOX: "d8954a42e69e0881ae6d24d4281459c144a0d5a968aed35d6d3d73a3c65d26a", GHAST: "64ab8a22e7687cc4c78f3b6ff5b1eb04917b51cd3cd7dbce36171160b3c77ced",
    GOAT: "457a0d538fa08a7affe312903468861720f9fa34e86d44b89dcec5639265f03", GUARDIAN: "a0bf34a71e7715b6ba52d5dd1bae5cb85f773dc9b0d457b4bfc5f9dd3cc7c94", HOGLIN: "9bb9bc0f01dbd762a08d9e77c08069ed7c95364aa30ca1072208561b730e8d75",
    HORSE: "a996399fff9cbcfb7ba677dd0c2d104229d1cc2307a6f075a882da4694ef80ae", HUSK: "d674c63c8db5f4ca628d69a3b1f8a36e29d8fd775e1a6bdb6cabb4be4db121", ILLUSIONER: "512512e7d016a2343a7bff1a4cd15357ab851579f1389bd4e3a24cbeb88b",
    IRON_GOLEM: "a9ceb73d97cf5dc32e333dbef7af25f39e42033d684649075ba4681af2a3c01b", LLAMA: "9f7d90b305aa64313c8d4404d8d652a96eba8a754b67f4347dcccdd5a6a63398", MAGMA_CUBE: "38957d5023c937c4c41aa2412d43410bda23cf79a9f6ab36b76fef2d7c429",
    MULE: "a0486a742e7dda0bae61ce2f55fa13527f1c3b334c57c034bb4cf132fb5f5f", MOOSHROOM: "45603d539f666fdf0f7a0fe20b81dfef3abe6c51da34b9525a5348432c5523b2", OCELOT: "5657cd5c2989ff97570fec4ddcdc6926a68a3393250c1be1f0b114a1db1",
    PANDA: "8018a1771d69c11b8dad42cd310375ba2d827932b25ef357f7e572c1bd0f9", PARROT: "a4ba8d66fecb1992e94b8687d6ab4a5320ab7594ac194a2615ed4df818edbc3", PHANTOM: "7e95153ec23284b283f00d19d29756f244313a061b70ac03b97d236ee57bd982",
    PIG: "fa305e321e87ec91421ecccf7cfef10703fb77f62658d6b998f117fcf34cd0b2", PILLAGER: "18e57841607f449e76b7c820fcbd1913ec1b80c4ac81728874db230f5df2b3b", POLAR_BEAR: "d46d23f04846369fa2a3702c10f759101af7bfe8419966429533cd81a11d2b",
    PUFFERFISH: "6df8c316962949ba3be445c94ebf714108252d46459b66110f4bc14e0e1b59dc", RABBIT: "ffecc6b5e6ea5ced74c46e7627be3f0826327fba26386c6cc7863372e9bc", RAVAGER: "cd20bf52ec390a0799299184fc678bf84cf732bb1bd78fd1c4b441858f0235a8",
    SALMON: "8aeb21a25e46806ce8537fbd6668281cf176ceafe95af90e94a5fd84924878", SHEEP: "a723893df4cfb9c7240fc47b560ccf6ddeb19da9183d33083f2c71f46dad290a", SILVERFISH: "da91dab8391af5fda54acd2c0b18fbd819b865e1a8f1d623813fa761e924540",
    SLIME: "a5acd8b24f7389a40404348f4344eec2235d4ca718453be9803b60b71a125891", SNOW_GOLEM: "8e8d206f61e6de8a79d0cb0bcd98aced464cbfefc921b4160a25282163112a", SPIDER: "cd541541daaff50896cd258bdbdd4cf80c3ba816735726078bfe393927e57f1",
    SQUID: "d8705624daa2956aa45956c81bab5f4fdb2c74a596051e24192039aea3a8b8", STRAY: "9e391c6e535f7aa5a2b6ee6d137f59f2d7c60def88853ba611ceb2d16a7e7c73", STRIDER: "125851a86ee1c54c94fc5bed017823dfb3ba08eddbcab2a914ef45b596c1603",
    TRADER_LLAMA: "8424780b3c5c5351cf49fb5bf41fcb289491df6c430683c84d7846188db4f84d", TROPICAL_FISH: "d6dd5e6addb56acbc694ea4ba5923b1b25688178feffa72290299e2505c97281", TURTLE: "8fa552139966c5fac1b98061ce23fc0ddef058c163142dd6d1c768cd2da207c2",
    VEX: "5e7330c7d5cd8a0a55ab9e95321535ac7ae30fe837c37ea9e53bea7ba2de86b", VILLAGER: "a36e9841794a37eb99524925668b47a62b5cb72e096a9f8f95e106804ae13e1b", VINDICATOR: "6deaec344ab095b48cead7527f7dee61b063ff791f76a8fa76642c8676e2173",
    WANDERING_TRADER: "ee011aac817259f2b48da3e5ef266094703866608b3d7d1754432bf249cd2234", WITCH: "8aa986a6e1c2d88ff198ab2c3259e8d2674cb83a6d206f883bad2c8ada819", WOLF: "28d408842e76a5a454dc1c7e9ac5c1a8ac3f4ad34d6973b5275491dff8c5c251",
    ZOGLIN: "e67e18602e03035ad68967ce090235d8996663fb9ea47578d3a7ebbc42a5ccf9", ZOMBIFIED_PIGLIN: "7eabaecc5fae5a8a49c8863ff4831aaa284198f1a2398890c765e0a8de18da8c", ZOMBIE_HORSE: "d22950f2d3efddb18de86f8f55ac518dce73f12a6e0f8636d551d8eb480ceec",
    ZOMBIE_VILLAGER: "b2b393be2dc2973d41a834e19dd6b73b866782d684a097ebfe99cb390194f", SKELETON_HORSE: "47effce35132c86ff72bcae77dfbb1d22587e94df3cbc2570ed17cf8973a"
]

def on-kill(ctx) {
    if !&ctx.chance() {
        return false
    }

    victim = &ctx.victim
    type = &victim.getType().name()

    for ignored in &ignored-entities {
        if &type == &ignored {
            return false
        }
    }
    if &ignore-mythic-mobs && &ctx.isMythicMob(&victim) {
        return false
    }

    material = &ctx.headMaterial(&type)
    owner = if &victim is org.bukkit.entity.Player { &victim } else { null }
    texture = if &owner == null { &head-textures[&type] } else { null }

    if &owner == null && &texture == null && &material == "PLAYER_HEAD" {
        return false
    }

    display = &head-name.replace("%type%", &type)
    head = &ctx.skullItem(&material, &texture, &display, &owner)

    &victim.getWorld().dropItemNaturally(&victim.getLocation(), &head)

    if &ctx.visualEffects() {
        &ctx.particles(&victim, "BLOCK", 30, 0.25)
    }

    true
}
