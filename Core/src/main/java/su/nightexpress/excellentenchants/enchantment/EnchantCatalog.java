package su.nightexpress.excellentenchants.enchantment;

import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import su.nightexpress.excellentenchants.EnchantsFiles;
import su.nightexpress.excellentenchants.EnchantsKeys;
import su.nightexpress.excellentenchants.EnchantsPlugin;
import su.nightexpress.excellentenchants.api.EnchantDefinition;
import su.nightexpress.excellentenchants.api.EnchantDistribution;
import su.nightexpress.excellentenchants.api.enchantment.CustomEnchantment;
import su.nightexpress.excellentenchants.api.item.ItemSetDefaults;
import su.nightexpress.excellentenchants.api.item.ItemSetRegistry;
import su.nightexpress.excellentenchants.api.wrapper.TradeType;
import su.nightexpress.excellentenchants.bridge.EnchantCatalogEntry;
import su.nightexpress.excellentenchants.manager.EnchantManager;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Version;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static su.nightexpress.excellentenchants.EnchantsPlaceholders.*;
import static su.nightexpress.excellentenchants.enchantment.EnchantCatalog.Weight.*;

@NullMarked
public enum EnchantCatalog implements EnchantCatalogEntry {

    ;

    public static void loadAll(Path dataDir, ItemSetRegistry itemSetRegistry,
                               BiConsumer<EnchantCatalog, IllegalStateException> onError) {
        Path enchantsDir = Path.of(dataDir.toString(), EnchantsFiles.DIR_ENCHANTS);
        Path disabledDir = Path.of(enchantsDir.toString(), EnchantsFiles.DIR_DISABLED);

        if (!Files.exists(disabledDir)) {
            try {
                Files.createDirectories(disabledDir);
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        Set<String> disabledEnchants = FileUtil.findYamlFiles(disabledDir.toString()).stream().map(
            FileUtil::getNameWithoutExtension).collect(Collectors.toSet());

        for (EnchantCatalog value : values()) {
            if (disabledEnchants.contains(value.getId())) {
                value.disabled = true;
            }
            else {
                try {
                    value.load(enchantsDir, itemSetRegistry);
                }
                catch (IllegalStateException exception) {
                    value.disabled = true;
                    onError.accept(value, exception);
                }
            }
        }
    }


    public static Stream<EnchantCatalog> stream() {
        return Stream.of(values());
    }


    public static Stream<EnchantCatalog> enabled() {
        return stream().filter(EnchantCatalog::isEnabled);
    }

    /*public static boolean isPresentAndEnabled(String enchantId) {
        return Enums.parse(enchantId, EnchantCatalog.class).filter(EnchantCatalog::isEnabled).isPresent();
    }*/

    private final String  id;
    private final boolean curse;
    private final boolean paperOnly;

    private final Supplier<EnchantDefinition>   initDefinition;
    private final Supplier<EnchantDistribution> initDistribution;
    private final EnchantFactory<?>             factory;

    private EnchantDefinition   definition;
    private EnchantDistribution distribution;
    private boolean             disabled;

    EnchantCatalog(Supplier<EnchantDefinition> initDefinition,
                   Supplier<EnchantDistribution> initDistribution,
                   EnchantFactory<?> factory) {
        this(initDefinition, initDistribution, factory, false);
    }

    EnchantCatalog(Supplier<EnchantDefinition> initDefinition,
                   Supplier<EnchantDistribution> initDistribution,
                   EnchantFactory<?> factory,
                   boolean curse) {
        this(initDefinition, initDistribution, factory, curse, false);
    }

    EnchantCatalog(Supplier<EnchantDefinition> initDefinition,
                   Supplier<EnchantDistribution> initDistribution,
                   EnchantFactory<?> factory,
                   boolean curse,
                   boolean paperOnly) {
        this.id = LowerCase.INTERNAL.apply(this.name());
        this.curse = curse;
        this.paperOnly = paperOnly;
        this.initDefinition = initDefinition;
        this.initDistribution = initDistribution;
        this.factory = factory;
    }

    public void load(Path enchantsDir, ItemSetRegistry itemSetRegistry) throws IllegalStateException {
        if (this.paperOnly && Version.isSpigot())
            throw new IllegalStateException("The enchantment is available for PaperMC only");

        Path file = Path.of(enchantsDir.toString(), FileConfig.withExtension(this.id));
        boolean exists = Files.exists(file);

        FileConfig config = FileConfig.load(file);
        EnchantDefinition definition = this.initDefinition.get();
        EnchantDistribution distribution = this.initDistribution.get();

        if (!exists) {
            config.set("Definition", definition);
            config.set("Distribution", distribution);
        }

        this.definition = EnchantDefinition.read(config, "Definition", itemSetRegistry);
        this.distribution = EnchantDistribution.read(config, "Distribution");

        config.saveChanges();
    }


    public CustomEnchantment createEnchantment(EnchantsPlugin plugin, EnchantManager manager, Path file,
                                               EnchantContext context) {
        return this.factory.create(plugin, manager, file, context);
    }

    @Override

    public String getId() {
        return this.id;
    }

    @Override

    public NamespacedKey getKey() {
        return EnchantsKeys.create(this.id);
    }

    @Override

    public EnchantDefinition getDefinition() {
        if (this.definition == null) throw new IllegalStateException("Definition is not yet initialized");

        return this.definition;
    }

    @Override

    public EnchantDistribution getDistribution() {
        if (this.distribution == null) throw new IllegalStateException("Distribution is not yet initialized");

        return this.distribution;
    }

    @Override
    public boolean isCurse() {
        return this.curse;
    }

    public boolean isPaperOnly() {
        return this.paperOnly;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public boolean isEnabled() {
        return !this.disabled;
    }

    static class Weight {

        static final int COMMON    = 10;
        static final int UNCOMMON  = 5;
        static final int RARE      = 2;
        static final int VERY_RARE = 1;

    }
}
