package su.nightexpress.excellentenchants.script;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentenchants.EnchantsKeys;
import su.nightexpress.excellentenchants.EnchantsPlugin;
import su.nightexpress.excellentenchants.api.EnchantDefinition;
import su.nightexpress.excellentenchants.api.EnchantDistribution;
import su.nightexpress.excellentenchants.api.item.ItemSetDefaults;
import su.nightexpress.excellentenchants.api.wrapper.TradeType;
import su.nightexpress.excellentenchants.bridge.EnchantCatalogEntry;
import su.nightexpress.excellentenchants.enchantment.EnchantContext;
import su.nightexpress.excellentenchants.manager.EnchantManager;
import su.nightexpress.nightcore.util.Enums;

/**
 * Catalog entry backed by a Fluxon enchantment script.
 */
public class ScriptEnchantEntry implements EnchantCatalogEntry {

    private EnchantScript script;
    private final String        id;

    private EnchantDefinition   definition;
    private EnchantDistribution distribution;
    private boolean             disabled;

    public ScriptEnchantEntry(@NonNull EnchantScript script) {
        this.script = script;
        this.id = script.getId();
    }

    /**
     * Builds the enchantment definition and distribution from the script metadata.
     */
    public void load() throws IllegalStateException {
        this.definition = this.createDefinition();
        this.distribution = this.createDistribution();
    }

    @NonNull
    private EnchantDefinition createDefinition() {
        String name = this.script.metaString("name");
        if (name == null) name = capitalize(this.id);

        int maxLevel = this.script.metaInt("max-level", 1);

        EnchantDefinition.Builder builder = EnchantDefinition.builder(name, maxLevel);

        Object description = this.script.meta("description");
        List<String> lines = readStringList(description);
        if (!lines.isEmpty()) builder.description(lines);

        builder.weight(this.readWeight());

        ItemSetDefaults supported = readItemSet(this.script.meta("supported-items"));
        if (supported == null) supported = readItemSet(this.script.meta("items"));
        if (supported == null) {
            throw new IllegalStateException("Script '" + this.id + "' has no 'supported-items' (or 'items') declared.");
        }

        ItemSetDefaults primary = readItemSet(this.script.meta("primary-items"));
        builder.supportedItems(supported);
        if (primary != null) builder.primaryItems(primary);

        int anvilCost = this.script.metaInt("anvil-cost", -1);
        if (anvilCost >= 0) builder.anvilCost(anvilCost);

        List<String> exclusives = readStringList(this.script.meta("exclusive-with"));
        if (!exclusives.isEmpty()) {
            Set<NamespacedKey> keys = new HashSet<>();
            exclusives.forEach(key -> keys.add(EnchantsKeys.create(key)));
            builder.exclusives(keys);
        }

        return builder.build();
    }

    private int readWeight() {
        Object weight = this.script.meta("weight");
        if (weight instanceof Number number) return number.intValue();
        if (weight != null) {
            return switch (String.valueOf(weight).toLowerCase(Locale.ROOT)) {
                case "common" -> 10;
                case "uncommon" -> 5;
                case "rare" -> 2;
                case "very-rare", "very_rare" -> 1;
                default -> 5;
            };
        }

        return 5;
    }

    @NonNull
    private EnchantDistribution createDistribution() {
        boolean treasure = this.script.metaBoolean("treasure", false);

        Set<TradeType> trades = new HashSet<>();
        for (String raw : readStringList(this.script.meta("trades"))) {
            Enums.parse(raw, TradeType.class).ifPresent(trades::add);
        }

        if (treasure) {
            return new EnchantDistribution(trades, true, this.script.metaBoolean("discoverable", false), true,
                false, this.script.metaBoolean("random-loot", true), false);
        }

        return new EnchantDistribution(trades, false, this.script.metaBoolean("discoverable", true),
            this.script.metaBoolean("tradable", true), this.script.metaBoolean("mob-equipment", true),
            this.script.metaBoolean("random-loot", true), this.script.metaBoolean("traded-equipment", true));
    }

    @NonNull
    public ScriptEnchantment createEnchantment(@NonNull EnchantsPlugin plugin,
                                               @NonNull EnchantManager manager,
                                               @NonNull Path file,
                                               @NonNull EnchantContext context) {
        EnchantScript script = this.script;

        if (script.hasHandler(ScriptEnchantment.HOOK_LAUNCH) || script.hasHandler("projectile:trident")) {
            return new ScriptTridentEnchantment(plugin, manager, file, context, script);
        }

        if (script.hasHandler(ScriptEnchantment.HOOK_HIT) || script.hasHandler(
            ScriptEnchantment.HOOK_PROJECTILE_DAMAGE)) {
            return new ScriptArrowEnchantment(plugin, manager, file, context, script);
        }

        return new ScriptEnchantment(plugin, manager, file, context, script);
    }

    @Override
    @NonNull
    public String getId() {
        return this.id;
    }

    @Override
    @NonNull
    public NamespacedKey getKey() {
        return EnchantsKeys.create(this.id);
    }

    @Override
    @NonNull
    public EnchantDefinition getDefinition() {
        if (this.definition == null) throw new IllegalStateException("Definition is not yet initialized");
        return this.definition;
    }

    @Override
    @NonNull
    public EnchantDistribution getDistribution() {
        if (this.distribution == null) throw new IllegalStateException("Distribution is not yet initialized");
        return this.distribution;
    }

    @Override
    public boolean isCurse() {
        return this.script.metaBoolean("curse", false);
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isEnabled() {
        return !this.disabled;
    }

    @NonNull
    public EnchantScript getScript() {
        return this.script;
    }

    @NonNull
    public Path getFile() {
        return this.script.getFile();
    }

    /**
     * Swaps in a freshly parsed script (behaviour hot reload).
     */
    public void replaceScript(@NonNull EnchantScript script) {
        this.script = script;
    }

    // ------------------------------------------------------------------------------------------------------------
    // Util
    // ------------------------------------------------------------------------------------------------------------

    @Nullable
    private static ItemSetDefaults readItemSet(@Nullable Object raw) {
        if (raw == null) return null;

        List<String> names = readStringList(raw);
        if (names.isEmpty()) return null;

        // Multiple item sets are not supported by the API: the first one wins.
        return Enums.parse(names.get(0), ItemSetDefaults.class).orElse(null);
    }

    @NonNull
    private static List<String> readStringList(@Nullable Object raw) {
        List<String> result = new ArrayList<>();
        if (raw == null) return result;

        if (raw instanceof Iterable<?> iterable) {
            for (Object element : iterable) {
                if (element != null) result.add(String.valueOf(element).trim());
            }
            return result;
        }

        String value = String.valueOf(raw);
        for (String part : value.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) result.add(trimmed);
        }

        return result;
    }

    @NonNull
    private static String capitalize(@NonNull String id) {
        String[] parts = id.replace('-', '_').split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (!builder.isEmpty()) builder.append(' ');
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }

        return builder.toString();
    }
}
