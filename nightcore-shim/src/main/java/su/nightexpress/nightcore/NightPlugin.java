package su.nightexpress.nightcore;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import su.nightexpress.nightcore.bridge.chat.UniversalChatEventHandler;
import su.nightexpress.nightcore.bridge.permission.PermissionNamespace;
import su.nightexpress.nightcore.bridge.scheduler.AdaptedScheduler;
import su.nightexpress.nightcore.bridge.scheduler.DefaultBukkitScheduler;
import su.nightexpress.nightcore.command.CommandManager;
import su.nightexpress.nightcore.command.api.NightPluginCommand;
import su.nightexpress.nightcore.commands.ArgumentRegistry;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.CommandProvider;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.configuration.codec.CodecRegistry;
import su.nightexpress.nightcore.configuration.codec.ConfigCodecs;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.language.LangManager;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangRegistry;
import su.nightexpress.nightcore.ui.menu.MenuRegistry;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Reflex;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.wrapper.UniPermission;

/**
 * Paper-native plugin base of the bundled engine.
 * <p>
 * Unlike the original library this class does not require a separate "NightCore" plugin to be
 * present on the server: all registries (commands, language, configuration, scheduler) are owned
 * by the plugin instance itself.
 */
public abstract class NightPlugin extends JavaPlugin implements NightCorePlugin {

    public static final String CONFIG_FILE = "config.yml";
    public static final String ENGINE_FILE = "engine.yml";

    protected final List<CommandProvider> commandProviders;

    protected AdaptedScheduler scheduler;

    protected NightCommand   rootCommand;
    protected List<Runnable> postLoaders;

    protected LangRegistry   langRegistry;
    protected LangManager    langManager;
    protected CommandManager commandManager;

    protected FileConfig    engineConf;
    protected FileConfig    config;
    protected PluginDetails details;

    protected NightPlugin() {
        this.commandProviders = new ArrayList<>();
    }

    @Override
    public void onEnable() {
        // Server version must be known before anything else (also used by the supported range check).
        Version.detect();

        // Command argument & codec registries must be available before any config/language is read.
        Arguments.init(new ArgumentRegistry());
        ConfigCodecs.init(new CodecRegistry());

        long loadTook = System.currentTimeMillis();

        this.scheduler = new DefaultBukkitScheduler(this);

        if (!this.checkVersion()) {
            this.getPluginManager().disablePlugin(this);
            return;
        }

        this.onStartup();
        this.loadManagers();

        this.info("Plugin loaded in " + (System.currentTimeMillis() - loadTook) + " ms!");
    }

    @Override
    public void onDisable() {
        this.unloadManagers();
        this.onShutdown();
    }

    protected boolean onInit() {
        return true;
    }

    protected void onStartup() {

    }

    protected void onShutdown() {

    }

    public void reload() {
        this.unloadManagers();
        this.loadManagers();
    }

    @NonNull
    public String getPlaceholderAPIIdentifier() {
        return LowerCase.INTERNAL.apply(this.getDescription().getName());
    }

    @Override
    @NonNull
    public Path dataPath() {
        return this.getDataFolder().toPath();
    }

    @Override
    @NonNull
    public final FileConfig getConfig() {
        return this.config;
    }

    @NonNull
    public FileConfig getEngineConfig() {
        return this.engineConf;
    }

    @NonNull
    @Deprecated
    public final FileConfig getLang() {
        return this.langManager.getConfig();
    }

    @NonNull
    @Override
    public PluginDetails getDetails() {
        if (this.details == null) throw new IllegalStateException("Plugin is not yet initialized!");

        return this.details;
    }

    @NonNull
    protected abstract PluginDetails getDefaultDetails();

    @Deprecated
    public void registerPermissions(@NonNull Class<?> clazz) {
        Reflex.getStaticFields(clazz, UniPermission.class, false).forEach(permission -> {
            if (this.getPluginManager().getPermission(permission.getName()) == null) {
                this.getPluginManager().addPermission(permission);
            }
        });
    }

    protected void setupRegistries() {
        this.langRegistry = new LangRegistry(this);
        this.langRegistry.setup();

        this.addRegistries();

        this.langRegistry.loadLocale();
    }

    protected void addRegistries() {

    }

    protected void setupConfig() {
        this.engineConf = FileConfig.loadOrExtract(this, ENGINE_FILE);
        this.config = FileConfig.loadOrExtract(this, CONFIG_FILE);

        // ---------- MIGRATION - START ----------
        if (this.config.contains("Plugin")) {
            PluginDetails details = PluginDetails.read(this, this.config, this.getDefaultDetails());
            details.write(this.engineConf, "");
            this.config.remove("Plugin");
        }
        // ---------- MIGRATION - END ----------

        this.details = PluginDetails.read(this, this.engineConf, this.getDefaultDetails());

        if (this.details.getConfigClass() != null) {
            this.config.initializeOptions(this.details.getConfigClass());
        }
    }

    protected void setupLanguage() {
        if (this.langRegistry.hasElements()) return; // Do not load if modern lang system present.

        this.langManager = new LangManager(this);
        this.langManager.setup();

        if (this.details.getLangClass() != null) {
            this.langManager.loadEntries(this.details.getLangClass());
        }
    }

    @Deprecated
    protected void setupPermissions() {
        Class<?> clazz = this.details.getPermissionsClass();
        if (clazz == null) return;

        this.registerPermissions(clazz);
    }

    private void registerCorePermissions() {
        PermissionNamespace namespace = this.getCorePermissions();
        if (namespace == null) return;

        Permission reloadPerm = namespace.create("reload");

        this.addCommandProvider(root -> root.branch(Commands.literal("reload")
            .permission(reloadPerm)
            .description(CoreLang.COMMAND_RELOAD_DESC)
            .executes((context, arguments) -> {
                this.doReload(context.getSender());
                return true;
            })
        ));

        this.registerPermissions(namespace);
    }

    @Override
    public void registerPermissions(@NonNull PermissionNamespace namespace) {
        namespace.register(this.getPluginManager());
    }

    protected boolean disableCommandManager() {
        return false;
    }

    protected void setupCommands() {
        if (this.disableCommandManager()) return;

        this.commandManager = new CommandManager(this);
        this.commandManager.setup();
    }

    protected void loadManagers() {
        this.postLoaders = new ArrayList<>(); // Initialize a list for post-loading code.

        this.setupConfig();         // Load configuration.
        this.setupRegistries();     // Load registries so the plugin modules can access them.
        this.setupLanguage();       // Load legacy language.
        this.setupPermissions();    // Register plugin permissions.
        this.setupCommands();       // Register plugin commands.
        this.registerCorePermissions();

        this.enable();              // Load the plugin.

        this.postLoad();            // Load some stuff that needs to be injected after the rest managers.

        if (this.rootCommand != null) {
            this.rootCommand.register();
        }
        else {
            this.registerCommands();
        }

        this.config.saveChanges();
        if (this.langManager != null) this.getLang().saveChanges();
        if (this.langRegistry != null) this.langRegistry.complete();
        this.engineConf.saveChanges();
    }

    protected void unloadManagers() {
        if (this.scheduler != null) {
            this.scheduler.cancelTasks(); // Stop all plugin tasks.
        }

        this.disable();

        MenuRegistry.closeAll();                 // Close all GUIs.
        HandlerList.unregisterAll(this);         // Unregister all plugin listeners.

        if (this.rootCommand != null) {
            this.rootCommand.unregister();
            this.rootCommand = null;
        }
        if (this.commandManager != null) this.commandManager.shutdown();
        if (this.langRegistry != null) this.langRegistry.shutdown();
        if (this.langManager != null) this.langManager.shutdown();
        this.details = null;                           // Reset so it will use default ones on config read.
    }

    protected void postLoad() {
        if (this.postLoaders == null) return;

        this.postLoaders.forEach(Runnable::run);
        this.postLoaders.clear();
        this.postLoaders = null; // Prevent from adding post-load code during runtime.
    }

    public void onPostLoad(@NonNull Runnable runnable) {
        if (this.postLoaders == null) {
            throw new IllegalStateException("Can't inject post loading code during runtime.");
        }
        this.postLoaders.add(runnable);
    }

    public void doReload(@NonNull CommandSender sender) {
        this.reload();
        CoreLang.PLUGIN_RELOADED.withPrefix(this).send(sender);
    }

    protected void registerCommands() {
        this.rootCommand = NightCommand.forPlugin(this, builder -> {
            this.commandProviders.forEach(provider -> provider.provideCommands(builder));
        });
        this.rootCommand.register();
    }

    public void addCommandProvider(CommandProvider provider) {
        this.commandProviders.add(provider);
    }

    @NonNull
    @Deprecated
    public final NightPluginCommand getBaseCommand() {
        return this.commandManager.getMainCommand();
    }

    @NonNull
    @Deprecated
    public final LangManager getLangManager() {
        return this.langManager;
    }

    @NonNull
    public final LangRegistry getLangRegistry() {
        return this.langRegistry;
    }

    @NonNull
    @Deprecated
    public final CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public void registerListener(@NonNull Listener listener) {
        this.getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void addChatHandler(@NonNull EventPriority priority, @NonNull UniversalChatEventHandler handler) {
        // Chat handling is not provided by the bundled engine anymore.
    }

    @Override
    public void removeChatHandler(@NonNull UniversalChatEventHandler handler) {
        // Chat handling is not provided by the bundled engine anymore.
    }

    public void registerLang(@NonNull Class<? extends LangContainer> clazz) {
        this.langRegistry.register(clazz);
    }

    @Override
    public void injectLang(@NonNull Class<? extends LangContainer> langClass) {
        this.langRegistry.inject(langClass);
    }

    @Override
    public void injectLang(@NonNull LangContainer langContainer) {
        this.langRegistry.inject(langContainer);
    }

    @Override
    public void extractResources(@NonNull String jarPath) {
        this.extractResources(jarPath, this.getDataFolder() + jarPath);
    }

    @Override
    public void extractResources(@NonNull String jarPath, @NonNull String targetPath) {
        File destination = new File(targetPath);
        if (destination.exists()) return;

        if (jarPath.startsWith("/")) {
            jarPath = jarPath.substring(1);
        }
        if (jarPath.endsWith("/")) {
            jarPath = jarPath.substring(0, jarPath.length() - 1);
        }

        FileUtil.extractResources(this.getFile(), jarPath, destination);
    }

    private boolean checkVersion() {
        String raw = Version.getRawVersion();

        // Versions below 1.21 are not supported at all - refuse to start.
        if (Version.isBelowMinimum()) {
            this.error("=".repeat(50));
            this.error("This plugin requires Minecraft " + Version.MIN_SUPPORTED.getLocalized() + " or newer!");
            this.error("Detected server version: " + raw);
            this.error("Disabling the plugin.");
            this.error("=".repeat(50));
            return false;
        }

        // Versions newer than the newest supported one run in a "best effort" mode.
        if (Version.isAboveMaximum()) {
            this.warn("=".repeat(50));
            this.warn("This server version (" + raw + ") is newer than the latest supported one ("
                + Version.MAX_SUPPORTED.getLocalized() + ").");
            this.warn("The plugin will be enabled, but some features may not work properly.");
            this.warn("=".repeat(50));
        }

        return true;
    }

    public PermissionNamespace getCorePermissions() {
        return null;
    }

    @Override
    @NonNull
    public PluginManager getPluginManager() {
        return this.getServer().getPluginManager();
    }

    @Override
    @NonNull
    public AdaptedScheduler scheduler() {
        return this.scheduler;
    }

    @Override
    public void runTask(@NonNull Runnable consumer) {
        this.scheduler.runTask(consumer);
    }

    @Override
    public void runTask(@NonNull Entity entity, @NonNull Runnable runnable) {
        this.scheduler.runTask(entity, runnable);
    }

    @Override
    public void runTask(@NonNull Location location, @NonNull Runnable runnable) {
        this.scheduler.runTask(location, runnable);
    }

    @Override
    public void runTask(@NonNull Chunk chunk, @NonNull Runnable runnable) {
        this.scheduler.runTask(chunk, runnable);
    }

    @Override
    public void runTaskAsync(@NonNull Runnable consumer) {
        this.scheduler.runTaskAsync(consumer);
    }

    @Override
    public void runTaskLater(@NonNull Runnable consumer, long delay) {
        this.scheduler.runTaskLater(consumer, delay);
    }

    @Override
    public void runTaskLaterAsync(@NonNull Runnable consumer, long delay) {
        this.scheduler.runTaskLaterAsync(consumer, delay);
    }

    @Override
    public void runTaskTimer(@NonNull Runnable consumer, long delay, long interval) {
        this.scheduler.runTaskTimer(consumer, delay, interval);
    }

    @Override
    public void runTaskTimerAsync(@NonNull Runnable consumer, long delay, long interval) {
        this.scheduler.runTaskTimerAsync(consumer, delay, interval);
    }
}
