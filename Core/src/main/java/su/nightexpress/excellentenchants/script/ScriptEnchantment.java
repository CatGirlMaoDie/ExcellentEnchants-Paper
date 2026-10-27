package su.nightexpress.excellentenchants.script;

import java.nio.file.Path;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentenchants.EnchantsPlugin;
import su.nightexpress.excellentenchants.api.EnchantPriority;
import su.nightexpress.excellentenchants.api.Modifier;
import su.nightexpress.excellentenchants.api.enchantment.component.EnchantComponent;
import su.nightexpress.excellentenchants.api.enchantment.meta.Period;
import su.nightexpress.excellentenchants.api.enchantment.meta.PotionEffects;
import su.nightexpress.excellentenchants.api.enchantment.meta.Probability;
import su.nightexpress.excellentenchants.api.enchantment.type.AttackEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.DefendEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.KillEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.MiningEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.PassiveEnchant;
import su.nightexpress.excellentenchants.enchantment.EnchantContext;
import su.nightexpress.excellentenchants.enchantment.GameEnchantment;
import su.nightexpress.excellentenchants.manager.EnchantManager;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Enums;

/**
 * Enchantment implementation whose behaviour is provided by a Fluxon script.
 * <p>
 * Every hook delegates to the script handler of the same name ({@code on-attack}, {@code on-defend},
 * {@code on-mining}, {@code on-kill}, {@code on-passive}). When a script does not define a handler for a
 * hook, the hook is a no-op.
 */
public class ScriptEnchantment extends GameEnchantment
    implements AttackEnchant, DefendEnchant, MiningEnchant, KillEnchant, PassiveEnchant {

    public static final String HOOK_ATTACK  = "on-attack";
    public static final String HOOK_DEFEND  = "on-defend";
    public static final String HOOK_MINING  = "on-mining";
    public static final String HOOK_KILL    = "on-kill";
    public static final String HOOK_PASSIVE = "on-passive";

    private EnchantScript script;
    private final EnchantPriority priority;

    public ScriptEnchantment(@NonNull EnchantsPlugin plugin,
                             @NonNull EnchantManager manager,
                             @NonNull Path file,
                             @NonNull EnchantContext context,
                             @NonNull EnchantScript script) {
        super(plugin, manager, file, context);
        this.script = script;
        this.priority = Enums.parse(script.metaString("priority"), EnchantPriority.class).orElse(EnchantPriority.NORMAL);

        this.loadComponents();
    }

    private void loadComponents() {
        Map<String, Object> chance = this.script.metaMap("trigger-chance");
        if (chance != null && !chance.isEmpty()) {
            this.addComponent(EnchantComponent.PROBABILITY, readProbability(chance));
        }

        Map<String, Object> effect = this.script.metaMap("potion-effect");
        if (effect != null && !effect.isEmpty()) {
            PotionEffects potionEffects = readPotionEffects(effect);
            if (potionEffects != null) {
                this.addComponent(EnchantComponent.POTION_EFFECT, potionEffects);
            }
        }

        Object period = this.script.meta("period");
        if (period instanceof Number number) {
            this.addComponent(EnchantComponent.PERIODIC, Period.ofSeconds(number.intValue()));
        }
    }

    @NonNull
    private static Probability readProbability(@NonNull Map<String, Object> map) {
        String   mode     = String.valueOf(map.getOrDefault("mode", "addictive"));
        double   base     = asDouble(map.get("base"), 0D);
        double   perLevel = asDouble(map.get("per-level"), 0D);

        return mode.equalsIgnoreCase("multiplier") ? Probability.multiplier(base, perLevel)
            : Probability.addictive(base, perLevel);
    }

    @Nullable
    private static PotionEffects readPotionEffects(@NonNull Map<String, Object> map) {
        Object typeRaw = map.get("type");
        if (typeRaw == null) return null;

        org.bukkit.potion.PotionEffectType type = org.bukkit.potion.PotionEffectType.getByName(
            String.valueOf(typeRaw).toUpperCase().replace(' ', '_'));
        if (type == null) return null;

        boolean permanent = Boolean.parseBoolean(String.valueOf(map.getOrDefault("permanent", false)));

        Modifier duration = readModifier(map.get("duration"), 0D);
        Modifier amplifier = readModifier(map.get("amplifier"), 0D);

        return permanent ? PotionEffects.permanent(type, amplifier) : PotionEffects.temporal(type, duration,
            amplifier);
    }

    @NonNull
    private static Modifier readModifier(@Nullable Object raw, double def) {
        if (raw instanceof Map<?, ?> rawMap) {
            Map<String, Object> map = castMap(rawMap);
            double   base     = asDouble(map.get("base"), def);
            double   perLevel = asDouble(map.get("per-level"), 0D);
            double   capacity = asDouble(map.get("capacity"), 0D);
            String   mode     = String.valueOf(map.getOrDefault("mode", "addictive"));

            Modifier.Builder builder = mode.equalsIgnoreCase("multiplier")
                ? Modifier.multiplier(base)
                : Modifier.addictive(base);

            builder.perLevel(perLevel);
            if (capacity > 0) builder.capacity(capacity);

            return builder.build();
        }

        double base = asDouble(raw, def);
        return Modifier.addictive(base).build();
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private static Map<String, Object> castMap(@NonNull Map<?, ?> map) {
        return (Map<String, Object>) map;
    }

    private static double asDouble(@Nullable Object value, double def) {
        return value instanceof Number number ? number.doubleValue() : def;
    }

    // ------------------------------------------------------------------------------------------------------------
    // Script driven hooks
    // ------------------------------------------------------------------------------------------------------------

    @Nullable
    private Boolean run(@NonNull String hook, @NonNull ScriptContext context) {
        if (!this.script.hasHandler(hook)) return null;

        Object result = FluxonSupport.call(this.script.getEnvironment(), hook, context);
        return result instanceof Boolean bool ? bool : Boolean.FALSE;
    }

    @Override
    public boolean onAttack(@NonNull EntityDamageByEntityEvent event, @NonNull LivingEntity damager,
                            @NonNull LivingEntity victim, @NonNull ItemStack weapon, int level) {
        Boolean result = this.run(HOOK_ATTACK, new ScriptContext(this, HOOK_ATTACK, level, damager, victim, weapon,
            event));
        return result != null && result;
    }

    @Override
    public boolean onProtect(@NonNull EntityDamageByEntityEvent event, @NonNull LivingEntity damager,
                             @NonNull LivingEntity victim, @NonNull ItemStack weapon, int level) {
        Boolean result = this.run(HOOK_DEFEND, new ScriptContext(this, HOOK_DEFEND, level, victim, damager, weapon,
            event));
        return result != null && result;
    }

    @Override
    public boolean onBreak(@NonNull BlockBreakEvent event, @NonNull LivingEntity player, @NonNull ItemStack item,
                           int level) {
        Boolean result = this.run(HOOK_MINING, new ScriptContext(this, HOOK_MINING, level, player, null, item,
            event));
        return result != null && result;
    }

    @Override
    public boolean onKill(@NonNull EntityDeathEvent event, @NonNull LivingEntity entity, @NonNull Player killer,
                          @NonNull ItemStack weapon, int level) {
        Boolean result = this.run(HOOK_KILL, new ScriptContext(this, HOOK_KILL, level, killer, entity, weapon,
            event));
        return result != null && result;
    }

    @Override
    public boolean onTrigger(@NonNull LivingEntity entity, @NonNull ItemStack item, int level) {
        Boolean result = this.run(HOOK_PASSIVE, new ScriptContext(this, HOOK_PASSIVE, level, entity, null, item,
            null));
        return result != null && result;
    }

    @Override
    @NonNull
    public EnchantPriority getAttackPriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getProtectPriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getBreakPriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getKillPriority() {
        return this.priority;
    }

    // ------------------------------------------------------------------------------------------------------------
    // Script enchantments do not use YAML files.
    // ------------------------------------------------------------------------------------------------------------

    @Override
    public void load() {

    }

    @Override
    protected void loadAdditional(@NonNull FileConfig config) {

    }

    @NonNull
    public EnchantScript getScript() {
        return this.script;
    }

    /**
     * Replaces the script of this enchantment, which allows hot-reloading its handlers
     * (the enchantment definition itself is registered in the server registry and requires a restart).
     */
    public void updateScript(@NonNull EnchantScript script) {
        this.script = script;
    }
}
