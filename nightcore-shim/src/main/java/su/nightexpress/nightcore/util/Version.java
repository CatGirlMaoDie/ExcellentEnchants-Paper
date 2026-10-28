package su.nightexpress.nightcore.util;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;

import java.util.Comparator;
import java.util.stream.Stream;

public enum Version {

    // --- Supported range: Minecraft 1.21 - 26.3 -------------------------------------------------
    MC_1_21_0("1.21", 3953),
    MC_1_21("1.21.1", 3955),
    MC_1_21_2("1.21.2", 4080),
    MC_1_21_3("1.21.3", 4082),
    MC_1_21_4("1.21.4", 4189),
    MC_1_21_5("1.21.5", 4325),
    MC_1_21_6("1.21.6", 4435),
    MC_1_21_7("1.21.7", 4438),
    MC_1_21_8("1.21.8", 4440),
    MC_1_21_9("1.21.9", 4554),
    MC_1_21_10("1.21.10", 4556),
    MC_1_21_11("1.21.11", 4671),
    MC_26_1("26.1", 4786),
    MC_26_1_1("26.1.1", 4788),
    MC_26_1_2("26.1.2", 4790),
    MC_26_2("26.2", 4903),
    MC_26_3("26.3", 5023),

    /**
     * Server versions outside of the supported range (older than 1.21 or newer than 26.3).
     */
    UNKNOWN("Unknown", 10_000),
    ;

    /**
     * The oldest server version supported by the plugin.
     */
    public static final Version MIN_SUPPORTED = MC_1_21_0;

    /**
     * The newest server version supported by the plugin.
     */
    public static final Version MAX_SUPPORTED = MC_26_3;

    private static final boolean IS_PAPER = checkPaper();
    private static final boolean IS_FOLIA = checkFolia();

    private static Version current;
    private static String  rawVersion = "0";
    private final int      dataVersion;
    private final String   localized;

    Version(@NonNull String localized, int dataVersion) {
        this.localized = localized;
        this.dataVersion = dataVersion;
    }

    @NonNull
    public static Version detect() {
        // The server instance does not exist while a Paper bootstrapper runs.
        if (Bukkit.getServer() == null) {
            current = UNKNOWN;
            rawVersion = "";
            return current;
        }

        String bukkitVersion = isSpigot() ? Bukkit.getServer().getBukkitVersion() : Bukkit.getServer().getVersion();
        String exact = bukkitVersion.split("-")[0];
        rawVersion = exact;

        current = Stream.of(values()).sorted(Comparator.reverseOrder()).filter(version -> exact.equalsIgnoreCase(version
            .getLocalized())).findFirst().orElse(UNKNOWN);

        return current;
    }

    /**
     * Raw server version string as reported by the server (e.g. {@code 1.21.8}).
     */
    @NonNull
    public static String getRawVersion() {
        return rawVersion;
    }

    /**
     * Whether the server is running a version older than {@link #MIN_SUPPORTED}.
     */
    public static boolean isBelowMinimum() {
        if (rawVersion.isEmpty()) return false; // Not detected yet.

        return compare(rawVersion, MIN_SUPPORTED.getLocalized()) < 0;
    }

    /**
     * Whether the server is running a version newer than {@link #MAX_SUPPORTED}.
     */
    public static boolean isAboveMaximum() {
        if (rawVersion.isEmpty()) return false; // Not detected yet.

        return compare(rawVersion, MAX_SUPPORTED.getLocalized()) > 0;
    }

    /**
     * Compares two dot separated version strings numerically (e.g. {@code 1.21.8} with {@code 26.3}).
     */
    public static int compare(@NonNull String first, @NonNull String second) {
        String[] left = first.split("\\.");
        String[] right = second.split("\\.");
        int length = Math.max(left.length, right.length);

        for (int index = 0; index < length; index++) {
            int a = index < left.length ? toInt(left[index], 0) : 0;
            int b = index < right.length ? toInt(right[index], 0) : 0;

            if (a != b) return Integer.compare(a, b);
        }

        return 0;
    }

    private static int toInt(@NonNull String value, int def) {
        try {
            return Integer.parseInt(value.trim());
        }
        catch (NumberFormatException exception) {
            return def;
        }
    }

    @NonNull
    public static Version getCurrent() {
        if (current == null) throw new IllegalStateException("Version is not initialized!");

        return current;
    }

    @Deprecated
    public static boolean withDialogs() {
        return true;
    }

    public static boolean withCopperAge() {
        return isAtLeast(MC_1_21_9);
    }

    public static boolean isSpigot() {
        return !IS_PAPER;
    }

    public static boolean isPaper() {
        return IS_PAPER;
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    /**
     * Whether this entry is a known (detected) server version.
     */
    public boolean isKnown() {
        return this != UNKNOWN;
    }

    public int getDataVersion() {
        return this.dataVersion;
    }

    @NonNull
    public String getLocalized() {
        return this.localized;
    }

    public boolean isLower(@NonNull Version version) {
        return this.ordinal() < version.ordinal();
    }

    public boolean isHigher(@NonNull Version version) {
        return this.ordinal() > version.ordinal();
    }

    public static boolean isAtLeast(@NonNull Version version) {
        return version.isCurrent() || getCurrent().isHigher(version);
    }

    public static boolean isAbove(@NonNull Version version) {
        return getCurrent().isHigher(version);
    }

    public static boolean isBehind(@NonNull Version version) {
        return getCurrent().isLower(version);
    }

    public static boolean isBehindOrEqual(@NonNull Version version) {
        return getCurrent().isLower(version) || getCurrent() == version;
    }

    public boolean isCurrent() {
        return this == Version.getCurrent();
    }

    private static boolean checkPaper() {
        return Reflex.classExists("com.destroystokyo.paper.ParticleBuilder");
    }

    private static boolean checkFolia() {
        return Reflex.classExists("io.papermc.paper.threadedregions.RegionizedServer");
    }
}
