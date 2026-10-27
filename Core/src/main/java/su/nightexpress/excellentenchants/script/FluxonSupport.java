package su.nightexpress.excellentenchants.script;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import org.tabooproject.fluxon.Fluxon;
import org.tabooproject.fluxon.compiler.CompilationContext;
import org.tabooproject.fluxon.parser.ParsedScript;
import org.tabooproject.fluxon.runtime.Environment;
import org.tabooproject.fluxon.runtime.FluxonRuntime;
import org.tabooproject.fluxon.runtime.Function;
import org.tabooproject.fluxon.runtime.FunctionContext;
import org.tabooproject.fluxon.runtime.FunctionContextPool;

/**
 * Thin helper layer around the bundled Fluxon scripting runtime.
 * <p>
 * All enchant scripts share the single JVM wide {@link FluxonRuntime} instance, but each script is
 * evaluated in its own {@link Environment}, so scripts can not leak variables between each other.
 */
public final class FluxonSupport {

    private FluxonSupport() {

    }

    @NonNull
    public static FluxonRuntime runtime() {
        return FluxonRuntime.getInstance();
    }

    @NonNull
    public static Environment newEnvironment() {
        return runtime().newEnvironment();
    }

    /**
     * Reads and evaluates a script file. The returned environment owns the root variables and the
     * functions defined by the script.
     */
    @NonNull
    public static Environment evaluate(@NonNull Path file) throws IOException {
        String source = Files.readString(file, StandardCharsets.UTF_8);
        return evaluate(source, file.getFileName().toString());
    }

    @NonNull
    public static Environment evaluate(@NonNull String source, @NonNull String fileName) {
        Environment env = newEnvironment();
        CompilationContext context = new CompilationContext(source, fileName);
        // Enchant scripts are trusted server-side files: they may access Bukkit objects directly.
        context.setAllowReflectionAccess(true);
        context.setAllowJavaConstruction(true);
        context.setAllowInvalidReference(true);

        ParsedScript script = Fluxon.parse(context, env);
        script.eval(env);
        return env;
    }

    /**
     * Invokes a function defined by a script.
     */
    @Nullable
    public static Object call(@NonNull Environment env, @NonNull String name, @NonNull Object... args) {
        Function function = env.getFunctionOrNull(name);
        if (function == null) return null;

        FunctionContextPool pool = FunctionContextPool.local();
        try (FunctionContext<?> context = pool.borrow(function, null, args, env)) {
            function.call(context);
            return context.getReturnRef();
        }
    }

    public static boolean hasFunction(@NonNull Environment env, @NonNull String name) {
        return env.getFunctionOrNull(name) != null;
    }
}
