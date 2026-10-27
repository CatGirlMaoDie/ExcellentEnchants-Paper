package su.nightexpress.nightcore.util;

import java.util.logging.Logger;

import org.jspecify.annotations.NonNull;

/**
 * Minimal logging facade used by the bundled engine sources instead of a global plugin instance.
 */
public final class ShimLog {

    private static final Logger LOGGER = Logger.getLogger("ExcellentEnchants");

    private ShimLog() {

    }

    public static void info(@NonNull String msg) {
        LOGGER.info(msg);
    }

    public static void warn(@NonNull String msg) {
        LOGGER.warning(msg);
    }

    public static void error(@NonNull String msg) {
        LOGGER.severe(msg);
    }

    @NonNull
    public static Logger logger() {
        return LOGGER;
    }
}
