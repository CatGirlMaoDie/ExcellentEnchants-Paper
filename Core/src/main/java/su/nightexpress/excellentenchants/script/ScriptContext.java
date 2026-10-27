package su.nightexpress.excellentenchants.script;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Context object passed to every enchantment script handler.
 * <p>
 * Scripts receive it as the first (and only) handler argument:
 * <pre>
 * def on-attack(ctx) {
 *     if ctx.chance() { ctx.effect(ctx.victim) }
 * }
 * </pre>
 */
public final class ScriptContext {

    private final ScriptEnchantment enchantment;
    private final String            hook;
    private final int               level;
    private final LivingEntity      actor;
    private final LivingEntity      victim;
    private final ItemStack         item;
    private final Event             event;

    public ScriptContext(@NonNull ScriptEnchantment enchantment,
                         @NonNull String hook,
                         int level,
                         @NonNull LivingEntity actor,
                         @Nullable LivingEntity victim,
                         @Nullable ItemStack item,
                         @Nullable Event event) {
        this.enchantment = enchantment;
        this.hook = hook;
        this.level = level;
        this.actor = actor;
        this.victim = victim;
        this.item = item;
        this.event = event;
    }

    // ------------------------------------------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------------------------------------------

    @NonNull
    public ScriptEnchantment getEnchantment() {
        return this.enchantment;
    }

    /**
     * Current hook name, e.g. {@code attack}, {@code defend}, {@code mining}, {@code kill}, {@code passive}.
     */
    @NonNull
    public String getHook() {
        return this.hook;
    }

    public int getLevel() {
        return this.level;
    }

    /**
     * The entity that owns the enchanted item (attacker, wearer, miner, ...).
     */
    @NonNull
    public LivingEntity getActor() {
        return this.actor;
    }

    /**
     * The acting player, or {@code null} when the actor is not a player.
     */
    @Nullable
    public Player getPlayer() {
        return this.actor instanceof Player player ? player : null;
    }

    /**
     * The opponent entity (damage victim, dead entity, ...), may be {@code null}.
     */
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

    @NonNull
    public World getWorld() {
        return this.actor.getWorld();
    }

    @NonNull
    public Location getLocation() {
        return this.actor.getLocation();
    }

    /**
     * Convenience target: the victim when present, otherwise the actor.
     */
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

    /**
     * Rolls the enchantment trigger chance for the current level.
     */
    public boolean chance() {
        return this.enchantment.testTriggerChance(this.level);
    }

    /**
     * Rolls a custom chance (0-100).
     */
    public boolean chance(double percent) {
        return ThreadLocalRandom.current().nextDouble(100D) < percent;
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

    public boolean visualEffects() {
        return this.enchantment.hasVisualEffects();
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
        Particle type = resolveEnum(Particle.class, particle);
        if (type == null) return;

        World world = location.getWorld();
        if (world == null) return;

        world.spawnParticle(type, location, count, 0.25D, 0.25D, 0.25D, 0.1D);
    }

    public void sound(@Nullable LivingEntity target, @NonNull String sound) {
        if (target == null) return;

        Sound type = resolveSound(sound);
        if (type == null) return;

        target.getWorld().playSound(target.getLocation(), type, 1F, 1F);
    }

    public void msg(@Nullable LivingEntity target, @NonNull String text) {
        if (target == null) return;

        target.sendMessage(text);
    }

    public void damage(@Nullable LivingEntity target, double amount) {
        if (target == null) return;

        target.damage(amount, this.actor);
    }

    public void heal(@Nullable LivingEntity target, double amount) {
        if (target == null) return;

        double max = target.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH) != null
            ? target.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue() : 20D;

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

    // ------------------------------------------------------------------------------------------------------------
    // Util
    // ------------------------------------------------------------------------------------------------------------

    @Nullable
    private static PotionEffectType resolveEffect(@NonNull String name) {
        PotionEffectType type = PotionEffectType.getByName(name.toUpperCase().replace(' ', '_'));
        if (type != null) return type;

        return org.bukkit.Registry.EFFECT.get(org.bukkit.NamespacedKey.minecraft(name.toLowerCase().replace(' ', '_')));
    }

    @Nullable
    private static Sound resolveSound(@NonNull String name) {
        String key = name.toLowerCase().replace('_', '.').replace(' ', '.');
        Sound sound = org.bukkit.Registry.SOUNDS.get(org.bukkit.NamespacedKey.minecraft(key));
        if (sound != null) return sound;

        return org.bukkit.Registry.SOUNDS.get(org.bukkit.NamespacedKey.minecraft(name.toLowerCase().replace(' ', '_')));
    }

    @Nullable
    private static <T extends Enum<T>> T resolveEnum(@NonNull Class<T> clazz, @NonNull String name) {
        String normalized = name.toUpperCase().replace(' ', '_').replace('-', '_');
        try {
            return Enum.valueOf(clazz, normalized);
        }
        catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
