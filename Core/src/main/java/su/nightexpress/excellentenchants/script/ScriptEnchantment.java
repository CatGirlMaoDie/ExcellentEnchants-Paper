package su.nightexpress.excellentenchants.script;

import java.nio.file.Path;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentenchants.EnchantsPlugin;
import su.nightexpress.excellentenchants.api.EnchantPriority;
import su.nightexpress.excellentenchants.api.Modifier;
import su.nightexpress.excellentenchants.api.damage.DamageBonus;
import su.nightexpress.excellentenchants.api.damage.DamageBonusType;
import su.nightexpress.excellentenchants.api.enchantment.component.EnchantComponent;
import su.nightexpress.excellentenchants.api.enchantment.meta.ArrowEffects;
import su.nightexpress.excellentenchants.api.enchantment.meta.Charges;
import su.nightexpress.excellentenchants.api.enchantment.meta.Period;
import su.nightexpress.excellentenchants.api.enchantment.meta.PotionEffects;
import su.nightexpress.excellentenchants.api.enchantment.meta.Probability;
import su.nightexpress.excellentenchants.api.enchantment.type.AttackEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.BlockChangeEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.BlockEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.BowEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.BlockDropEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.ContainerEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.DeathEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.DefendEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.DurabilityEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.FishingEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.InteractEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.InventoryEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.KillEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.MiningEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.MoveEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.PassiveEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.ProtectionEnchant;
import su.nightexpress.excellentenchants.api.enchantment.type.ResurrectEnchant;
import su.nightexpress.excellentenchants.enchantment.EnchantContext;
import su.nightexpress.excellentenchants.enchantment.GameEnchantment;
import su.nightexpress.excellentenchants.manager.EnchantManager;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Enums;
import su.nightexpress.nightcore.util.bukkit.NightItem;

/**
 * Enchantment implementation whose behaviour is provided by a Fluxon script.
 * <p>
 * Every hook delegates to the script handler of the same name. When a script does not define a
 * handler for a hook, the hook is a no-op.
 */
public class ScriptEnchantment extends GameEnchantment
    implements AttackEnchant, DefendEnchant, MiningEnchant, KillEnchant, PassiveEnchant, BowEnchant,
               BlockDropEnchant, InteractEnchant, FishingEnchant,
               InventoryEnchant, ContainerEnchant, DurabilityEnchant, ProtectionEnchant,
               DeathEnchant, ResurrectEnchant, BlockChangeEnchant, MoveEnchant, BlockEnchant {

    public static final String HOOK_ATTACK            = "on-attack";
    public static final String HOOK_DEFEND            = "on-defend";
    public static final String HOOK_MINING            = "on-mining";
    public static final String HOOK_KILL              = "on-kill";
    public static final String HOOK_PASSIVE           = "on-passive";
    public static final String HOOK_SHOOT             = "on-shoot";
    public static final String HOOK_LAUNCH            = "on-launch";
    public static final String HOOK_HIT               = "on-hit";
    public static final String HOOK_PROJECTILE_DAMAGE = "on-projectile-damage";
    public static final String HOOK_DROP              = "on-drop";
    public static final String HOOK_INTERACT          = "on-interact";
    public static final String HOOK_FISHING           = "on-fishing";
    public static final String HOOK_INVENTORY_DEATH   = "on-inventory-death";
    public static final String HOOK_CLICK             = "on-click";
    public static final String HOOK_ITEM_DAMAGE       = "on-item-damage";
    public static final String HOOK_PROTECTION        = "on-protection";
    public static final String HOOK_DEATH             = "on-death";
    public static final String HOOK_RESURRECT         = "on-resurrect";
    public static final String HOOK_BLOCK_CHANGE      = "on-block-change";
    public static final String HOOK_MOVE              = "on-move";
    public static final String HOOK_PLACE             = "on-place";

    private EnchantScript  script;
    private EnchantPriority priority;
    private DamageBonusType damageBonusType;

    public ScriptEnchantment(@NonNull EnchantsPlugin plugin,
                             @NonNull EnchantManager manager,
                             @NonNull Path file,
                             @NonNull EnchantContext context,
                             @NonNull EnchantScript script) {
        super(plugin, manager, file, context);
        this.script = script;
        this.loadMetadata();
        this.loadComponents();
    }

    private void loadMetadata() {
        this.loadPriority();

        this.damageBonusType = Enums.parse(this.script.metaString("damage-bonus"), DamageBonusType.class).orElse(
            DamageBonusType.NORMAL);
    }

    private void loadPriority() {
        this.priority = Enums.parse(this.script.metaString("priority"), EnchantPriority.class).orElse(
            EnchantPriority.NORMAL);
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

        Object arrowEffects = this.script.meta("arrow-effects");
        if (arrowEffects != null) {
            ArrowEffects effects = readArrowEffects(arrowEffects);
            if (effects != null) {
                this.addComponent(EnchantComponent.ARROW, effects);
            }
        }

        Map<String, Object> charges = this.script.metaMap("charges");
        if (charges != null && !charges.isEmpty()) {
            Charges chargeData = readCharges(charges);
            if (chargeData != null) {
                this.addComponent(EnchantComponent.CHARGES, chargeData);
            }
        }
    }

    @Nullable
    private static ArrowEffects readArrowEffects(@NonNull Object raw) {
        if (raw instanceof Map<?, ?> rawMap) {
            Map<String, Object> map = castMap(rawMap);
            Object particleRaw = map.get("particle");
            if (particleRaw == null) return null;

            Particle particle = parseParticle(String.valueOf(particleRaw));
            if (particle == null) return null;

            Object colorRaw = map.get("color");
            if (colorRaw == null) return ArrowEffects.basic(particle);

            org.bukkit.Color color = parseColor(String.valueOf(colorRaw));
            if (color == null) return ArrowEffects.basic(particle);

            return new ArrowEffects(su.nightexpress.nightcore.util.wrapper.UniParticle.of(particle, color));
        }

        Particle particle = parseParticle(String.valueOf(raw));
        return particle == null ? null : ArrowEffects.basic(particle);
    }

    @Nullable
    private static Color parseColor(@NonNull String raw) {
        String[] parts = raw.split(",");
        if (parts.length != 3) return null;

        try {
            return org.bukkit.Color.fromRGB(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[2].trim()));
        }
        catch (NumberFormatException exception) {
            return null;
        }
    }

    @Nullable
    private static Particle parseParticle(@NonNull String name) {
        try {
            return Particle.valueOf(name.toUpperCase().replace(' ', '_').replace('-', '_'));
        }
        catch (IllegalArgumentException exception) {
            return null;
        }
    }

    @NonNull
    private static Probability readProbability(@NonNull Map<String, Object> map) {
        String mode     = String.valueOf(map.getOrDefault("mode", "addictive"));
        double base     = asDouble(map.get("base"), 0D);
        double perLevel = asDouble(map.get("per-level"), 0D);

        return mode.equalsIgnoreCase("multiplier") ? Probability.multiplier(base, perLevel)
            : Probability.addictive(base, perLevel);
    }

    @Nullable
    private static Charges readCharges(@NonNull Map<String, Object> map) {
        Modifier max = readModifier(map.get("max"), 0D);
        int consume = (int) asDouble(map.get("consume"), 1D);
        int recharge = (int) asDouble(map.get("recharge"), 1D);

        String fuelName = map.get("fuel") == null ? null : String.valueOf(map.get("fuel"));
        if (fuelName == null) return null;

        Material material = Material.matchMaterial(fuelName.toUpperCase().replace(' ', '_'));
        if (material == null) return null;

        return Charges.custom(max, consume, recharge, NightItem.fromType(material));
    }

    @Nullable
    private static PotionEffects readPotionEffects(@NonNull Map<String, Object> map) {
        Object typeRaw = map.get("type");
        if (typeRaw == null) return null;

        PotionEffectType type = PotionEffectType.getByName(String.valueOf(typeRaw).toUpperCase().replace(' ', '_'));
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

            Modifier.Builder builder = mode.equalsIgnoreCase("multiplier") ? Modifier.multiplier(base)
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
    protected Boolean run(@NonNull String hook, @NonNull ScriptContext context) {
        if (!this.script.hasHandler(hook)) return null;

        Object result = FluxonSupport.call(this.script.getEnvironment(), hook, context);
        return result instanceof Boolean bool ? bool : Boolean.FALSE;
    }

    protected boolean runBoolean(@NonNull String hook, @NonNull ScriptContext context) {
        Boolean result = this.run(hook, context);
        return result != null && result;
    }

    @Override
    public boolean onAttack(@NonNull EntityDamageByEntityEvent event, @NonNull LivingEntity damager,
                            @NonNull LivingEntity victim, @NonNull ItemStack weapon, int level) {
        return this.runBoolean(HOOK_ATTACK, new ScriptContext(this, HOOK_ATTACK, level, damager, victim, weapon,
            event));
    }

    @Override
    public boolean onProtect(@NonNull EntityDamageByEntityEvent event, @NonNull LivingEntity damager,
                             @NonNull LivingEntity victim, @NonNull ItemStack weapon, int level) {
        return this.runBoolean(HOOK_DEFEND, new ScriptContext(this, HOOK_DEFEND, level, victim, damager, weapon,
            event));
    }

    @Override
    public boolean onBreak(@NonNull BlockBreakEvent event, @NonNull LivingEntity player, @NonNull ItemStack item,
                           int level) {
        return this.runBoolean(HOOK_MINING, new ScriptContext(this, HOOK_MINING, level, player, null, item, event));
    }

    @Override
    public boolean onKill(@NonNull EntityDeathEvent event, @NonNull LivingEntity entity, @NonNull Player killer,
                          @NonNull ItemStack weapon, int level) {
        return this.runBoolean(HOOK_KILL, new ScriptContext(this, HOOK_KILL, level, killer, entity, weapon, event));
    }

    @Override
    public boolean onTrigger(@NonNull LivingEntity entity, @NonNull ItemStack item, int level) {
        return this.runBoolean(HOOK_PASSIVE, new ScriptContext(this, HOOK_PASSIVE, level, entity, null, item, null));
    }

    @Override
    public boolean onShoot(@NonNull EntityShootBowEvent event, @NonNull LivingEntity shooter,
                           @NonNull ItemStack bow, int level) {
        return this.runBoolean(HOOK_SHOOT, new ScriptContext(this, HOOK_SHOOT, level, shooter, null, bow, event));
    }

    @Override
    public boolean onDrop(@NonNull BlockDropItemEvent event, @NonNull LivingEntity player, @NonNull ItemStack item,
                          int level) {
        return this.runBoolean(HOOK_DROP, new ScriptContext(this, HOOK_DROP, level, player, null, item, event));
    }

    @Override
    public boolean onInteract(@NonNull PlayerInteractEvent event, @NonNull LivingEntity entity,
                              @NonNull ItemStack item, int level) {
        return this.runBoolean(HOOK_INTERACT, new ScriptContext(this, HOOK_INTERACT, level, entity, null, item,
            event));
    }

    @Override
    public boolean onFishing(@NonNull PlayerFishEvent event, @NonNull ItemStack item, int level) {
        return this.runBoolean(HOOK_FISHING, new ScriptContext(this, HOOK_FISHING, level, event.getPlayer(), null,
            item, event));
    }

    @Override
    public boolean onDeath(@NonNull PlayerDeathEvent event, @NonNull Player player, @NonNull ItemStack itemStack,
                           int level) {
        return this.runBoolean(HOOK_INVENTORY_DEATH, new ScriptContext(this, HOOK_INVENTORY_DEATH, level, player,
            null, itemStack, event));
    }

    @Override
    public boolean onClick(@NonNull InventoryClickEvent event, @NonNull Player player,
                           @NonNull ItemStack itemStack, int level) {
        return this.runBoolean(HOOK_CLICK, new ScriptContext(this, HOOK_CLICK, level, player, null, itemStack,
            event));
    }

    @Override
    public boolean onItemDamage(@NonNull PlayerItemDamageEvent event, @NonNull Player player,
                                @NonNull ItemStack itemStack, int level) {
        return this.runBoolean(HOOK_ITEM_DAMAGE, new ScriptContext(this, HOOK_ITEM_DAMAGE, level, player, null,
            itemStack, event));
    }

    @Override
    public boolean onProtection(@NonNull EntityDamageEvent event, @NonNull DamageBonus damageBonus,
                                @NonNull LivingEntity entity, @NonNull ItemStack itemStack, int level) {
        return this.runBoolean(HOOK_PROTECTION, new ScriptContext(this, HOOK_PROTECTION, level, entity, null,
            itemStack, event, null, damageBonus));
    }

    @Override
    public boolean onDeath(@NonNull EntityDeathEvent event, @NonNull LivingEntity entity, @NonNull ItemStack item,
                           int level) {
        return this.runBoolean(HOOK_DEATH, new ScriptContext(this, HOOK_DEATH, level, entity, null, item, event));
    }

    @Override
    public boolean onResurrect(@NonNull EntityResurrectEvent event, @NonNull LivingEntity entity,
                               @NonNull ItemStack item, int level) {
        return this.runBoolean(HOOK_RESURRECT, new ScriptContext(this, HOOK_RESURRECT, level, entity, null, item,
            event));
    }

    @Override
    public boolean onBlockChange(@NonNull EntityChangeBlockEvent event, @NonNull LivingEntity entity,
                                 @NonNull ItemStack itemStack, int level) {
        return this.runBoolean(HOOK_BLOCK_CHANGE, new ScriptContext(this, HOOK_BLOCK_CHANGE, level, entity, null,
            itemStack, event));
    }

    @Override
    public void onPlace(@NonNull BlockPlaceEvent event, @NonNull Player player,
                        @NonNull Block block, @NonNull ItemStack itemStack) {
        this.run(HOOK_PLACE, new ScriptContext(this, HOOK_PLACE, 1, player, null, itemStack, event));
    }

    @Override
    public boolean canPlaceInContainers() {
        return this.script.metaBoolean("place-in-containers", false);
    }

    @Override
    public boolean onMove(@NonNull PlayerMoveEvent event, @NonNull Player player, @NonNull ItemStack itemStack,
                          int level) {
        return this.runBoolean(HOOK_MOVE, new ScriptContext(this, HOOK_MOVE, level, player, null, itemStack, event));
    }

    // ------------------------------------------------------------------------------------------------------------
    // Priorities
    // ------------------------------------------------------------------------------------------------------------

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

    @Override
    @NonNull
    public EnchantPriority getShootPriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getDropPriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getInteractPriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getFishingPriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getClickPriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getItemDamagePriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getProtectionPriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getDeathPriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getResurrectPriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public EnchantPriority getMovePriority() {
        return this.priority;
    }

    @Override
    @NonNull
    public DamageBonus getDamageBonus() {
        return new DamageBonus(this.damageBonusType);
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
    public EnchantsPlugin getPlugin() {
        return this.plugin;
    }

    @NonNull
    public EnchantManager getManager() {
        return this.manager;
    }

    @NonNull
    public EnchantPriority getPriority() {
        return this.priority;
    }

    @NonNull
    public EnchantScript getScript() {
        return this.script;
    }

    /**
     * Replaces the script of this enchantment, which allows hot-reloading its handlers.
     */
    public void updateScript(@NonNull EnchantScript script) {
        this.script = script;
        this.loadMetadata();
    }
}
