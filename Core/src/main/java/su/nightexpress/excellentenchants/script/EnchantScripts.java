package su.nightexpress.excellentenchants.script;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import org.tabooproject.fluxon.runtime.Environment;

import su.nightexpress.excellentenchants.EnchantsFiles;

/**
 * Loads and holds all Fluxon based enchantment scripts.
 * <p>
 * Scripts are stored as {@code .fs} files inside the {@code enchants} directory of the plugin data
 * folder. Files placed into the {@code _disabled_} sub directory are ignored.
 */
public final class EnchantScripts {

    public static final String EXTENSION = ".fs";

    private static final Map<String, ScriptEnchantEntry> ENTRIES = new HashMap<>();

    private EnchantScripts() {

    }

    @NonNull
    public static Collection<ScriptEnchantEntry> all() {
        return ENTRIES.values();
    }

    @NonNull
    public static Stream<ScriptEnchantEntry> enabled() {
        return ENTRIES.values().stream().filter(ScriptEnchantEntry::isEnabled);
    }

    @Nullable
    public static ScriptEnchantEntry getById(@NonNull String id) {
        return ENTRIES.get(id.toLowerCase());
    }

    public static boolean isScripted(@NonNull String id) {
        return ENTRIES.containsKey(id.toLowerCase());
    }

    public static void clear() {
        ENTRIES.clear();
    }

    /**
     * Extracts the bundled default scripts into the plugin data folder and loads all scripts.
     *
     * @param dataDir    plugin data directory.
     * @param logger     logger used for reporting.
     * @param onError    callback invoked when a script could not be loaded.
     */
    public static void loadAll(@NonNull Path dataDir,
                               @NonNull Consumer<String> log,
                               @NonNull BiConsumer<String, Exception> onError) {
        Path enchantsDir = dataDir.resolve(EnchantsFiles.DIR_ENCHANTS.replace("/", ""));
        Path disabledDir = enchantsDir.resolve(EnchantsFiles.DIR_DISABLED);

        try {
            Files.createDirectories(enchantsDir);
            Files.createDirectories(disabledDir);
        }
        catch (IOException exception) {
            log.accept("Could not create enchantment script directories: " + exception.getMessage());
            return;
        }

        extractDefaults(enchantsDir, log);

        ENTRIES.clear();

        List<Path> files;
        try (Stream<Path> stream = Files.list(enchantsDir)) {
            files = stream.filter(path -> path.getFileName().toString().toLowerCase().endsWith(EXTENSION)).toList();
        }
        catch (IOException exception) {
            log.accept("Could not list enchantment scripts: " + exception.getMessage());
            return;
        }

        for (Path file : files) {
            try {
                ScriptEnchantEntry entry = loadScript(file);
                entry.load();

                if (ENTRIES.containsKey(entry.getId())) {
                    throw new IllegalStateException("Duplicate enchantment id '" + entry.getId() + "'.");
                }

                ENTRIES.put(entry.getId(), entry);
            }
            catch (Exception exception) {
                onError.accept(file.getFileName().toString(), exception);
            }
        }

        log.accept("Loaded " + ENTRIES.size() + " scripted enchantment(s).");
    }

    /**
     * Re-evaluates all script files and swaps the handlers of already registered enchantments.
     * <p>
     * Enchantment definitions/distributions are registered into the server registry during the server
     * bootstrap, so metadata changes require a server restart. Only the behaviour can be hot reloaded.
     *
     * @return amount of scripts reloaded.
     */
    public static int reloadBehaviour(@NonNull Consumer<String> log) {
        int reloaded = 0;

        for (ScriptEnchantEntry entry : new ArrayList<>(ENTRIES.values())) {
            Path file = entry.getFile();
            if (!Files.exists(file)) continue;

            try {
                ScriptEnchantEntry fresh = loadScript(file);
                entry.replaceScript(fresh.getScript());
                reloaded++;
            }
            catch (Exception exception) {
                log.accept("Could not reload enchantment script '" + file.getFileName() + "': " + exception
                    .getMessage());
            }
        }

        return reloaded;
    }

    @NonNull
    private static ScriptEnchantEntry loadScript(@NonNull Path file) throws IOException {
        Environment env = FluxonSupport.evaluate(file);

        Map<String, Object> meta = new LinkedHashMap<>();
        Set<String> handlers = new LinkedHashSet<>();

        for (String key : META_KEYS) {
            if (!env.hasRootVariable(key)) continue;
            meta.put(key, env.getRootVariable(key));
        }

        for (String hook : HOOKS) {
            if (FluxonSupport.hasFunction(env, hook)) {
                handlers.add(hook);
            }
        }

        String fileName = file.getFileName().toString();
        String id = String.valueOf(meta.getOrDefault("id", fileName.substring(0, fileName.length() - EXTENSION
            .length()))).toLowerCase();

        meta.putIfAbsent("id", id);

        EnchantScript script = new EnchantScript(file, id, env, handlers, meta);
        return new ScriptEnchantEntry(script);
    }

    private static final List<String> HOOKS = List.of(
        ScriptEnchantment.HOOK_ATTACK,
        ScriptEnchantment.HOOK_DEFEND,
        ScriptEnchantment.HOOK_MINING,
        ScriptEnchantment.HOOK_KILL,
        ScriptEnchantment.HOOK_PASSIVE
    );

    /**
     * Root variables read from a script as enchantment metadata.
     */
    private static final List<String> META_KEYS = List.of(
        "id", "name", "description", "max-level", "weight", "items", "supported-items", "primary-items",
        "exclusive-with", "anvil-cost", "curse", "priority", "treasure", "trades", "discoverable", "tradable",
        "mob-equipment", "random-loot", "traded-equipment", "trigger-chance", "potion-effect", "period"
    );

    /**
     * Copies bundled default scripts from the plugin jar into the data folder.
     */
    private static void extractDefaults(@NonNull Path enchantsDir, @NonNull Consumer<String> log) {
        List<String> defaults = new ArrayList<>();

        try (InputStream index = EnchantScripts.class.getResourceAsStream("/enchants/_index.txt")) {
            if (index == null) return;

            String content = new String(index.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            for (String line : content.split("\r?\n")) {
                String name = line.trim();
                if (!name.isEmpty() && name.endsWith(EXTENSION)) defaults.add(name);
            }
        }
        catch (IOException exception) {
            log.accept("Could not read the bundled enchantment script index: " + exception.getMessage());
            return;
        }

        for (String name : defaults) {
            Path target = enchantsDir.resolve(name);
            if (Files.exists(target)) continue;

            try (InputStream input = EnchantScripts.class.getResourceAsStream("/enchants/" + name)) {
                if (input == null) continue;

                Files.write(target, input.readAllBytes());
            }
            catch (IOException exception) {
                log.accept("Could not extract default enchantment script '" + name + "': " + exception
                    .getMessage());
            }
        }
    }
}
