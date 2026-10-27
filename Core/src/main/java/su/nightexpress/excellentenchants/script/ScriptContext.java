package su.nightexpress.excellentenchants.script;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.FireworkEffect;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentenchants.api.damage.DamageBonus;

/**
 * Context object passed to every enchantment script handler.
 * <p>
 * Scripts receive it as the first (and only) handler argument:
 * <pre>
 * def on-attack(ctx) {
 *     if &ctx.chance() { &ctx.effect(&ctx.victim) }
 * }
 * </pre>
 * Because scripts are evaluated with reflective member access enabled, the raw Bukkit event and
 * entities are always reachable as well, e.g. {@code &ctx.event.setDamage(10.0)}.
 */
public final class ScriptContext {

    private final ScriptEnchantment enchantment;
    private final String            hook;
    private final int               level;
    private final LivingEntity      actor;
    private final LivingEntity      victim;
    private final ItemStack         item;
    private final Event             event;
    private final Projectile        projectile;
    private final DamageBonus       damageBonus;

    public ScriptContext(@NonNull ScriptEnchantment enchantment,
                         @NonNull String hook,
                         int level,
                         @NonNull LivingEntity actor,
                         @Nullable LivingEntity victim,
                         @Nullable ItemStack item,
                         @Nullable Event event) {
        this(enchantment, hook, level, actor, victim, item, event, null, null);
    }

    public ScriptContext(@NonNull ScriptEnchantment enchantment,
                         @NonNull String hook,
                         int level,
                         @NonNull LivingEntity actor,
                         @Nullable LivingEntity victim,
                         @Nullable ItemStack item,
                         @Nullable Event event,
                         @Nullable Projectile projectile,
                         @Nullable DamageBonus damageBonus) {
        this.enchantment = enchantment;
        this.hook = hook;
        this.level = level;
        this.actor = actor;
        this.victim = victim;
        this.item = item;
        this.event = event;
        this.projectile = projectile;
        this.damageBonus = damageBonus;
    }

    // ------------------------------------------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------------------------------------------

    @NonNull
    public ScriptEnchantment getEnchantment() {
        return this.enchantment;
    }

    @NonNull
    public String getHook() {
        return this.hook;
    }

    public int getLevel() {
        return this.level;
    }

    @NonNull
    public LivingEntity getActor() {
        return this.actor;
    }

    @Nullable
    public Player getPlayer() {
        return this.actor instanceof Player player ? player : null;
    }

    @Nullable
    public LivingEntity getVictim() {
        return this.victim;
    }

    @Nullable
    public ItemStack getItem() {
        return this.item;
    }

    @Nullable
    public Event getEvent() {
        return this.event;
    }

    @Nullable
    public Projectile getProjectile() {
        return this.projectile;
    }

    @Nullable
    public DamageBonus getDamageBonus() {
        return this.damageBonus;
    }

    @NonNull
    public World getWorld() {
        return this.actor.getWorld();
    }

    @NonNull
    public Location getLocation() {
        return this.actor.getLocation();
    }

    @NonNull
    public LivingEntity getTarget() {
        return this.victim != null ? this.victim : this.actor;
    }

    public boolean isPlayerActor() {
        return this.actor instanceof Player;
    }

    // ------------------------------------------------------------------------------------------------------------
    // Enchantment helpers
    // ------------------------------------------------------------------------------------------------------------

    public boolean chance() {
        return this.enchantment.testTriggerChance(this.level);
    }

    public boolean chance(double percent) {
        return ThreadLocalRandom.current().nextDouble(100D) < percent;
    }

    /**
     * Numeric coercion helpers (Fluxon numbers are doubles by default).
     */
    public int asInt(@Nullable Object value) {
        return value instanceof Number number ? number.intValue() : 0;
    }

    public float asFloat(@Nullable Object value) {
        return value instanceof Number number ? number.floatValue() : 0F;
    }

    public int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, Math.max(min + 1, max + 1));
    }

    /**
     * Applies the potion effect configured for this enchantment in its script components.
     */
    public boolean effect(@Nullable LivingEntity target) {
        if (target == null) return false;

        return this.enchantment.addPotionEffect(target, this.level);
    }

    public boolean effect() {
        return this.effect(this.getTarget());
    }

    /**
     * Applies the enchantment potion effect to a projectile (arrow), which transfers it on hit.
     */
    public boolean effectArrow(@Nullable Arrow arrow) {
        if (arrow == null) return false;

        return this.enchantment.addPotionEffect(arrow, this.level);
    }

    public boolean visualEffects() {
        return this.enchantment.hasVisualEffects();
    }

    /**
     * Evaluates a modifier declared in the script, e.g. {@code [base: 1, per-level: 0.25]}.
     */
    public double mod(@Nullable Map<?, ?> modifier, int level) {
        if (modifier == null) return 0D;

        Map<?, ?> map    = modifier;
        double   base     = asDouble(map.get("base"), 0D);
        double   perLevel = asDouble(map.get("per-level"), 0D);
        double   capacity = asDouble(map.get("capacity"), 0D);
        String   mode     = String.valueOf(map.get("mode") == null ? "addictive" : map.get("mode"));

        double value = mode.equalsIgnoreCase("multiplier") ? base * Math.max(1, level) : base + (perLevel * level);
        return capacity > 0 ? Math.min(capacity, value) : value;
    }

    public double mod(@Nullable Map<?, ?> modifier) {
        return this.mod(modifier, this.level);
    }

    // ------------------------------------------------------------------------------------------------------------
    // Charges
    // ------------------------------------------------------------------------------------------------------------

    public int charges() {
        return this.item == null ? 0 : this.enchantment.getCharges(this.item);
    }

    public boolean outOfCharges() {
        return this.item != null && this.enchantment.isOutOfCharges(this.item);
    }

    public void consumeCharge() {
        if (this.item != null) this.enchantment.consumeCharges(this.item, this.level);
    }

    public void restoreCharge() {
        if (this.item != null) this.enchantment.restoreCharges(this.item, this.level);
    }

    // ------------------------------------------------------------------------------------------------------------
    // Generic Bukkit helpers
    // ------------------------------------------------------------------------------------------------------------

    public boolean addEffect(@Nullable LivingEntity target, @NonNull String effect, int durationTicks, int amplifier) {
        if (target == null) return false;

        PotionEffectType type = resolveEffect(effect);
        if (type == null) return false;

        return target.addPotionEffect(new PotionEffect(type, durationTicks, amplifier, false, true));
    }

    public boolean removeEffect(@Nullable LivingEntity target, @NonNull String effect) {
        if (target == null) return false;

        PotionEffectType type = resolveEffect(effect);
        if (type == null) return false;

        target.removePotionEffect(type);
        return true;
    }

    public void particles(@Nullable LivingEntity target, @NonNull String particle, int count) {
        if (target == null) return;
        this.particles(target.getEyeLocation(), particle, count);
    }

    public void particles(@NonNull Location location, @NonNull String particle, int count) {
        this.particles(location, particle, count, 0.25D);
    }

    public void particles(@NonNull Location location, @NonNull String particle, int count, double spread) {
        Particle type = resolveParticle(particle);
        if (type == null) return;

        World world = location.getWorld();
        if (world == null) return;

        world.spawnParticle(type, location, count, spread, spread, spread, 0.1D);
    }

    public void sound(@Nullable Entity target, @NonNull String sound) {
        if (target == null) return;

        Sound type = resolveSound(sound);
        if (type == null) return;

        target.getWorld().playSound(target.getLocation(), type, 1F, 1F);
    }

    public void sound(@Nullable Entity target, @NonNull String sound, float volume, float pitch) {
        if (target == null) return;

        Sound type = resolveSound(sound);
        if (type == null) return;

        target.getWorld().playSound(target.getLocation(), type, volume, pitch);
    }

    public void msg(@Nullable Entity target, @NonNull String text) {
        if (target == null) return;

        target.sendMessage(text);
    }

    public void damage(@Nullable LivingEntity target, double amount) {
        if (target == null) return;

        target.damage(amount, this.actor);
    }

    public void heal(@Nullable LivingEntity target, double amount) {
        if (target == null) return;

        Attribute attribute = Attribute.MAX_HEALTH;
        double max = target.getAttribute(attribute) != null ? target.getAttribute(attribute).getValue() : 20D;

        target.setHealth(Math.min(max, target.getHealth() + amount));
    }

    public void ignite(@Nullable LivingEntity target, int seconds) {
        if (target == null) return;

        target.setFireTicks(seconds * 20);
    }

    public void freeze(@Nullable LivingEntity target, int ticks) {
        if (target == null) return;

        target.setFreezeTicks(ticks);
    }

    public void drop(@NonNull ItemStack item) {
        this.actor.getWorld().dropItemNaturally(this.actor.getLocation(), item);
    }

    public void dropAt(@NonNull Location location, @NonNull ItemStack item) {
        World world = location.getWorld();
        if (world != null) world.dropItemNaturally(location, item);
    }

    /**
     * Gives the item to the acting player, dropping leftovers on the ground.
     */
    public boolean give(@NonNull ItemStack item) {
        Player player = this.getPlayer();
        if (player == null) return false;

        return this.giveTo(player, item);
    }

    /**
     * Gives the item to the given player, dropping leftovers on the ground.
     */
    public boolean giveTo(@NonNull Player player, @NonNull ItemStack item) {
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item.clone());
        leftovers.values().forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
        return true;
    }

    public boolean hasSpace(@NonNull Player player, @NonNull ItemStack item) {
        if (item.getType().isAir()) return false;

        int stackSize = item.getType().getMaxStackSize();
        for (ItemStack content : player.getInventory().getStorageContents()) {
            if (content == null || content.getType().isAir()) return true;
            if (content.isSimilar(item) && content.getAmount() < stackSize) return true;
        }

        return false;
    }

    /**
     * Breaks the block without triggering this plugin's own enchantment logic again.
     */
    public void breakBlock(@NonNull Block block) {
        su.nightexpress.excellentenchants.EnchantsUtils.safeBusyBreak(this.getPlayer(), block);
    }

    /**
     * Breaks the block and collects its drops into the given location list.
     */
    public void breakBlock(@NonNull Block block, @NonNull List<ItemStack> drops) {
        Player player = this.getPlayer();
        if (player == null) return;

        su.nightexpress.excellentenchants.EnchantsUtils.safeBusyBreak(player, block);
    }

    /**
     * Creates an enchantment explosion (respects the plugin's explosion handling and protection plugins).
     */
    public boolean explode(@NonNull Location location, float power, boolean fire, boolean destroyBlocks,
                           boolean damageItems) {
        return this.enchantment.getManager().createExplosion(this.actor, location, power, fire, destroyBlocks,
            explosion -> {
                if (!damageItems) explosion.setOnDamage(damageEvent -> {
                    Entity hit = damageEvent.getEntity();
                    if (hit instanceof Item || hit instanceof org.bukkit.entity.ItemFrame) {
                        damageEvent.setCancelled(true);
                    }
                });
            });
    }

    @NonNull
    public List<Block> blocksAround(@NonNull Block center, int radius) {
        java.util.List<Block> result = new java.util.ArrayList<>();
        World world = center.getWorld();
        if (world == null) return result;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    result.add(center.getRelative(x, y, z));
                }
            }
        }

        return result;
    }

    public void strikeLightning(@NonNull Location location, boolean effectOnly) {
        World world = location.getWorld();
        if (world == null) return;

        if (effectOnly) world.strikeLightningEffect(location);
        else world.strikeLightning(location);
    }

    @Nullable
    public Entity spawn(@NonNull String entityType, @NonNull Location location) {
        org.bukkit.entity.EntityType type = resolveEntityType(entityType);
        if (type == null) return null;

        World world = location.getWorld();
        if (world == null) return null;

        return world.spawnEntity(location, type);
    }

    @Nullable
    public LivingEntity spawnLiving(@NonNull String entityType, @NonNull Location location) {
        Entity entity = this.spawn(entityType, location);
        return entity instanceof LivingEntity living ? living : null;
    }

    /**
     * Drops of a caught item entity (fishing).
     */
    @Nullable
    public ItemStack caughtItem() {
        if (this.event instanceof org.bukkit.event.player.PlayerFishEvent fishEvent) {
            if (fishEvent.getCaught() instanceof Item item) {
                return item.getItemStack();
            }
        }

        return null;
    }

    public boolean isMythicMob(@Nullable LivingEntity entity) {
        return entity != null && su.nightexpress.excellentenchants.EnchantsUtils.isMythicMob(entity);
    }

    /**
     * Spawn reason name of the given entity (may be {@code null}).
     */
    @Nullable
    public String spawnReason(@Nullable LivingEntity entity) {
        if (entity == null) return null;

        org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason = this.enchantment.getManager().getSpawnReason(
            entity);
        return reason == null ? null : reason.name();
    }

    /**
     * Spawn egg item of the given entity type, or {@code null} when the entity has no egg.
     */
    @Nullable
    public ItemStack spawnEgg(@NonNull String entityType) {
        EntityType type = resolveEntityType(entityType);
        if (type == null) return null;

        Material material = org.bukkit.Bukkit.getItemFactory().getSpawnEgg(type);
        return material == null ? null : new ItemStack(material);
    }

    /**
     * Spawns a random firework rocket with the given power.
     */
    @Nullable
    public Firework launchFirework(@NonNull Location location, double power) {
        World world = location.getWorld();
        if (world == null) return null;

        org.bukkit.entity.Entity entity = world.spawnEntity(location,
            org.bukkit.entity.EntityType.FIREWORK_ROCKET);
        if (!(entity instanceof org.bukkit.entity.Firework firework)) return null;

        org.bukkit.inventory.meta.FireworkMeta meta = firework.getFireworkMeta();
        org.bukkit.FireworkEffect.Type type = org.bukkit.FireworkEffect.Type.values()[ThreadLocalRandom.current()
            .nextInt(org.bukkit.FireworkEffect.Type.values().length)];
        org.bukkit.Color color = org.bukkit.Color.fromBGR(ThreadLocalRandom.current().nextInt(256),
            ThreadLocalRandom.current().nextInt(256), ThreadLocalRandom.current().nextInt(256));
        org.bukkit.Color fade = org.bukkit.Color.fromBGR(ThreadLocalRandom.current().nextInt(256),
            ThreadLocalRandom.current().nextInt(256), ThreadLocalRandom.current().nextInt(256));

        meta.addEffect(org.bukkit.FireworkEffect.builder()
            .flicker(ThreadLocalRandom.current().nextBoolean())
            .withColor(color)
            .withFade(fade)
            .with(type)
            .trail(ThreadLocalRandom.current().nextBoolean())
            .build());
        meta.setPower((int) power);
        firework.setFireworkMeta(meta);

        return firework;
    }

    /**
     * Multiplies the given entity velocity on the next tick (used for knockback reduction).
     */
    public void delayVelocity(@Nullable LivingEntity target, double factor) {
        if (target == null) return;

        this.enchantment.getPlugin().runTask(target, () -> target.setVelocity(target.getVelocity()
            .multiply(factor)));
    }

    /**
     * Returns the smelting/cooking result of the given item, or the item itself when there is no recipe.
     */
    @NonNull
    public ItemStack cook(@NonNull ItemStack item) {
        for (java.util.Iterator<org.bukkit.inventory.Recipe> iterator = org.bukkit.Bukkit.getServer()
            .recipeIterator(); iterator.hasNext(); ) {
            org.bukkit.inventory.Recipe recipe = iterator.next();
            if (recipe instanceof org.bukkit.inventory.CookingRecipe<?> cooking && cooking.getInputChoice().test(item)) {
                ItemStack result = cooking.getResult().clone();
                result.setAmount(item.getAmount());
                return result;
            }
        }

        return item;
    }

    /**
     * Returns the furnace smelting result of the given item, or {@code null} when there is no recipe.
     */
    @Nullable
    public ItemStack smelt(@NonNull ItemStack item) {
        for (java.util.Iterator<org.bukkit.inventory.Recipe> iterator = org.bukkit.Bukkit.getServer()
            .recipeIterator(); iterator.hasNext(); ) {
            org.bukkit.inventory.Recipe recipe = iterator.next();
            if (recipe instanceof org.bukkit.inventory.FurnaceRecipe furnace
                && furnace.getInputChoice().test(item)) {
                ItemStack result = furnace.getResult().clone();
                result.setAmount(item.getAmount());
                return result;
            }
        }

        return null;
    }

    /**
     * Adds an extra drop to the current block drop event (keeps vanilla/plugin drop handling intact).
     */
    public void addDrop(@NonNull ItemStack item) {
        if (this.event instanceof org.bukkit.event.block.BlockDropItemEvent dropEvent) {
            su.nightexpress.excellentenchants.EnchantsUtils.populateResource(dropEvent, item);
        }
    }

    /**
     * Creates an explosion that breaks the surrounding blocks through the plugin's "busy break" routine,
     * so no enchantment is applied to the exploded blocks (used by Blast Mining).
     */
    public boolean explodeBlocks(@NonNull Block origin, float power) {
        Player player = this.getPlayer();
        if (player == null) return false;

        return this.enchantment.getManager().createExplosion(player, origin.getLocation(), power, false, true,
            explosion -> {
                explosion.setOnDamage(damageEvent -> damageEvent.setCancelled(true));
                explosion.setOnExplode(explodeEvent -> {
                    java.util.List<Block> blocks = explodeEvent.blockList();
                    blocks.forEach(block -> {
                        if (block.getLocation().equals(origin.getLocation())) return;
                        su.nightexpress.excellentenchants.EnchantsUtils.safeBusyBreak(player, block);
                    });
                    blocks.clear();
                });
            });
    }

    /**
     * Blocks between the player's eyes and the given distance, skipping air/water/lava.
     */
    @NonNull
    public List<Block> lastTwoTargetBlocks(int maxDistance) {
        Player player = this.getPlayer();
        if (player == null) return java.util.Collections.emptyList();

        java.util.Set<Material> transparent = java.util.EnumSet.of(Material.AIR, Material.WATER, Material.LAVA);
        return player.getLastTwoTargetBlocks(transparent, maxDistance);
    }

    /**
     * Creates an explosion that only damages living entities (used by Kamikadze).
     */
    public boolean explodeLivingOnly(@NonNull Location location, float power, boolean effects) {
        return this.enchantment.getManager().createExplosion(this.actor, location, power, false, false,
            explosion -> {
                if (effects) {
                    explosion.setOnExplode(explodeEvent -> {
                        this.particles(this.actor.getEyeLocation(), "SMOKE", 60, 0.5);
                        this.particles(this.actor.getEyeLocation(), "LAVA", 100, 1.25);
                    });
                }
                explosion.setOnDamage(damageEvent -> {
                    if (!(damageEvent.getEntity() instanceof LivingEntity)) {
                        damageEvent.setCancelled(true);
                    }
                });
            });
    }

    /**
     * Replaces the block type on the next tick (used by Replanter).
     */
    public void setBlockLater(@NonNull Block block, @NonNull String materialName) {
        Material material = Material.matchMaterial(materialName.toUpperCase().replace(' ', '_'));
        if (material == null) return;

        this.enchantment.getPlugin().runTask(block.getLocation(), () -> block.setType(material));
    }

    /**
     * Removes every enchantment from the given item.
     */
    public void removeAllEnchants(@NonNull ItemStack item) {
        su.nightexpress.excellentenchants.EnchantsUtils.removeAll(item);
    }

    /**
     * Head material used by Decapitator for the given entity type.
     */
    @NonNull
    public String headMaterial(@NonNull String entityType) {
        return switch (entityType.toUpperCase()) {
            case "WITHER_SKELETON", "WITHER" -> "WITHER_SKELETON_SKULL";
            case "ZOMBIE", "GIANT" -> "ZOMBIE_HEAD";
            case "SKELETON" -> "SKELETON_SKULL";
            case "CREEPER" -> "CREEPER_HEAD";
            case "ENDER_DRAGON" -> "DRAGON_HEAD";
            default -> "PLAYER_HEAD";
        };
    }

    /**
     * Creates a skull item, optionally applying a base64 skin texture or the given player skin.
     */
    @NonNull
    public ItemStack skullItem(@NonNull String materialName, @Nullable String texture, @Nullable String name,
                               @Nullable Player skinOwner) {
        Material material = Material.matchMaterial(materialName.toUpperCase().replace(' ', '_'));
        if (material == null) material = Material.PLAYER_HEAD;

        ItemStack stack = new ItemStack(material);
        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack;

        if (name != null) meta.setDisplayName(name);

        if (meta instanceof org.bukkit.inventory.meta.SkullMeta skullMeta) {
            if (skinOwner != null) {
                skullMeta.setOwningPlayer(skinOwner);
            }
            else if (texture != null) {
                try {
                    org.bukkit.profile.PlayerProfile profile = org.bukkit.Bukkit.createPlayerProfile(java.util.UUID
                        .randomUUID());
                    org.bukkit.profile.PlayerTextures textures = profile.getTextures();
                    textures.setSkin(new java.net.URL("http://textures.minecraft.net/texture/" + texture));
                    profile.setTextures(textures);
                    skullMeta.setOwnerProfile(profile);
                }
                catch (Exception exception) {
                    // Invalid texture, keep the plain skull.
                }
            }
        }

        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Stores a string in the item's persistent data container.
     */
    public void setPdcString(@NonNull ItemStack item, @NonNull String key, @NonNull String value) {
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey("excellentenchants", key.toLowerCase()
            .replace(' ', '_')), org.bukkit.persistence.PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    @Nullable
    public String getPdcString(@NonNull ItemStack item, @NonNull String key) {
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        return meta.getPersistentDataContainer().get(new org.bukkit.NamespacedKey("excellentenchants", key
            .toLowerCase().replace(' ', '_')), org.bukkit.persistence.PersistentDataType.STRING);
    }

    /**
     * Schedules a temporary block transformation handled by the plugin's ticked block manager.
     */
    public void addTickedBlock(@NonNull Block block, @NonNull String origin, @NonNull String target, int lifeTime) {
        Material originType = Material.matchMaterial(origin.toUpperCase().replace(' ', '_'));
        Material targetType = Material.matchMaterial(target.toUpperCase().replace(' ', '_'));
        if (originType == null || targetType == null) return;

        this.enchantment.getManager().addTickedBlock(block, originType, targetType, lifeTime);
    }

    /**
     * Fires a block-form event for the given block/material and applies it when not cancelled.
     */
    public boolean formBlock(@NonNull Block block, @NonNull String material) {
        Material type = Material.matchMaterial(material.toUpperCase().replace(' ', '_'));
        if (type == null) return false;

        org.bukkit.block.data.BlockData data = type.createBlockData();
        org.bukkit.block.BlockState state = data.createBlockState();
        org.bukkit.event.block.EntityBlockFormEvent formEvent = new org.bukkit.event.block.EntityBlockFormEvent(
            this.actor, block, state);
        org.bukkit.Bukkit.getPluginManager().callEvent(formEvent);
        if (formEvent.isCancelled()) return false;

        block.setBlockData(state.getBlockData());
        return true;
    }

    public boolean isBusy() {
        return su.nightexpress.excellentenchants.EnchantsUtils.isBusy();
    }

    public boolean isCookable(@NonNull ItemStack item) {
        return !this.cook(item).isSimilar(item);
    }

    /**
     * Reels the fishing hook in on the next tick (auto reel behaviour).
     */
    public boolean autoReel() {
        if (!(this.event instanceof org.bukkit.event.player.PlayerFishEvent fishEvent)) return false;

        Player player = fishEvent.getPlayer();
        org.bukkit.inventory.EquipmentSlot slot = su.nightexpress.excellentenchants.EnchantsUtils.getItemHand(
            player, Material.FISHING_ROD);
        if (slot == null) return false;

        this.enchantment.getPlugin().runTask(player, () -> {
            if (fishEvent.isCancelled()) return;
            if (!fishEvent.getHook().isValid()) return;

            player.swingHand(slot);
            fishEvent.getHook().retrieve(slot);
        });

        return true;
    }

    public boolean isFishingState(@NonNull String state) {
        if (this.event instanceof org.bukkit.event.player.PlayerFishEvent fishEvent) {
            return fishEvent.getState().name().equalsIgnoreCase(state);
        }

        return false;
    }

    @NonNull
    public Vector velocity(@NonNull Entity entity) {
        return entity.getVelocity();
    }

    // ------------------------------------------------------------------------------------------------------------
    // Util
    // ------------------------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------------------------
    // Bukkit constant helpers (scripts can not reference relocated class constants directly)
    // ------------------------------------------------------------------------------------------------------------

    @Nullable
    public BlockFace face(@NonNull String name) {
        try {
            return org.bukkit.block.BlockFace.valueOf(name.toUpperCase());
        }
        catch (IllegalArgumentException exception) {
            return null;
        }
    }

    @NonNull
    public List<org.bukkit.block.BlockFace> faces(@NonNull String csv) {
        java.util.List<org.bukkit.block.BlockFace> list = new java.util.ArrayList<>();
        for (String part : csv.split(",")) {
            org.bukkit.block.BlockFace face = this.face(part.trim());
            if (face != null) list.add(face);
        }

        return list;
    }

    @Nullable
    public Material material(@NonNull String name) {
        return Material.matchMaterial(name.toUpperCase().replace(' ', '_'));
    }

    @NonNull
    public ItemStack item(@NonNull String material) {
        Material type = this.material(material);
        return new ItemStack(type == null ? Material.STONE : type);
    }

    @NonNull
    public ItemStack itemOf(@Nullable Material material) {
        return new ItemStack(material == null ? Material.STONE : material);
    }

    @Nullable
    public EntityType entityType(@NonNull String name) {
        return resolveEntityType(name);
    }

    @Nullable
    public Particle particle(@NonNull String name) {
        return resolveParticle(name);
    }

    /**
     * Launches a projectile of the given entity type from the acting entity.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public Projectile launchProjectile(@NonNull String entityTypeName) {
        EntityType type = resolveEntityType(entityTypeName);
        if (type == null) return null;

        Class<?> clazz = type.getEntityClass();
        if (clazz == null || !Projectile.class.isAssignableFrom(clazz)) return null;

        return this.actor.launchProjectile((Class<? extends Projectile>) clazz);
    }

    @NonNull
    public PotionEffect potionEffect(@NonNull String type, int durationTicks, int amplifier) {
        PotionEffectType effectType = resolveEffect(type);
        return new PotionEffect(effectType == null ? PotionEffectType.SPEED : effectType, durationTicks, amplifier,
            false, true);
    }

    @Nullable
    public Enchantment enchantment(@NonNull String name) {
        try {
            return org.bukkit.enchantments.Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(name.toLowerCase()
                .replace(' ', '_')));
        }
        catch (Exception exception) {
            return null;
        }
    }

    public boolean hasEnchantment(@Nullable ItemStack item, @NonNull String name) {
        if (item == null) return false;

        org.bukkit.enchantments.Enchantment enchantment = this.enchantment(name);
        return enchantment != null && item.containsEnchantment(enchantment);
    }

    public double maxHealth(@NonNull LivingEntity entity) {
        Attribute attribute = Attribute.MAX_HEALTH;
        return entity.getAttribute(attribute) != null ? entity.getAttribute(attribute).getValue() : 20D;
    }

    public boolean isLog(@Nullable Material material) {
        return material != null && org.bukkit.Tag.LOGS.isTagged(material);
    }

    public boolean isLeaves(@Nullable Material material) {
        return material != null && org.bukkit.Tag.LEAVES.isTagged(material);
    }

    public void disablePickup(@Nullable AbstractArrow arrow) {
        if (arrow != null) arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
    }

    @NonNull
    public Set<Object> emptySet() {
        return new java.util.HashSet<>();
    }

    private static double asDouble(@Nullable Object value, double def) {
        return value instanceof Number number ? number.doubleValue() : def;
    }

    @Nullable
    private static PotionEffectType resolveEffect(@NonNull String name) {
        PotionEffectType type = PotionEffectType.getByName(name.toUpperCase().replace(' ', '_'));
        if (type != null) return type;

        return org.bukkit.Registry.EFFECT.get(org.bukkit.NamespacedKey.minecraft(name.toLowerCase().replace(' ',
            '_')));
    }

    @Nullable
    private static Particle resolveParticle(@NonNull String name) {
        String key = name.toUpperCase().replace(' ', '_').replace('-', '_');
        try {
            return Particle.valueOf(key);
        }
        catch (IllegalArgumentException exception) {
            return null;
        }
    }

    @Nullable
    private static Sound resolveSound(@NonNull String name) {
        String key = name.toLowerCase().replace('_', '.').replace(' ', '.');
        Sound sound = org.bukkit.Registry.SOUNDS.get(org.bukkit.NamespacedKey.minecraft(key));
        if (sound != null) return sound;

        return org.bukkit.Registry.SOUNDS.get(org.bukkit.NamespacedKey.minecraft(name.toLowerCase().replace(' ',
            '_')));
    }

    @Nullable
    private static EntityType resolveEntityType(@NonNull String name) {
        String key = name.toUpperCase().replace(' ', '_').replace('-', '_');
        try {
            return EntityType.valueOf(key);
        }
        catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
