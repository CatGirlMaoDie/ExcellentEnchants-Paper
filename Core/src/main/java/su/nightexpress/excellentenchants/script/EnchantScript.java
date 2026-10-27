package su.nightexpress.excellentenchants.script;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import org.tabooproject.fluxon.runtime.Environment;

/**
 * A parsed enchantment script: the evaluated Fluxon environment plus the metadata declared by the
 * script and the set of hook handlers it implements.
 */
public final class EnchantScript {

    private final Path                file;
    private final String              id;
    private final Environment         environment;
    private final Set<String>         handlers;
    private final Map<String, Object> meta;

    public EnchantScript(@NonNull Path file,
                         @NonNull String id,
                         @NonNull Environment environment,
                         @NonNull Set<String> handlers,
                         @NonNull Map<String, Object> meta) {
        this.file = file;
        this.id = id;
        this.environment = environment;
        this.handlers = handlers;
        this.meta = meta;
    }

    @NonNull
    public Path getFile() {
        return this.file;
    }

    @NonNull
    public String getId() {
        return this.id;
    }

    @NonNull
    public Environment getEnvironment() {
        return this.environment;
    }

    @NonNull
    public Set<String> getHandlers() {
        return this.handlers;
    }

    public boolean hasHandler(@NonNull String hook) {
        return this.handlers.contains(hook);
    }

    @NonNull
    public Map<String, Object> getMeta() {
        return this.meta;
    }

    @Nullable
    public Object meta(@NonNull String key) {
        return this.meta.get(key);
    }

    public boolean metaBoolean(@NonNull String key, boolean def) {
        Object value = this.meta.get(key);
        return value instanceof Boolean bool ? bool : def;
    }

    public int metaInt(@NonNull String key, int def) {
        Object value = this.meta.get(key);
        if (value instanceof Number number) return number.intValue();

        return def;
    }

    @Nullable
    public String metaString(@NonNull String key) {
        Object value = this.meta.get(key);
        return value == null ? null : String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public Map<String, Object> metaMap(@NonNull String key) {
        Object value = this.meta.get(key);
        return value instanceof Map<?, ?> map ? (Map<String, Object>) map : null;
    }
}
