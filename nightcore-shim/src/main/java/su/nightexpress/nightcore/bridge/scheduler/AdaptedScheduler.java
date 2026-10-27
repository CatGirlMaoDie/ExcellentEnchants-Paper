package su.nightexpress.nightcore.bridge.scheduler;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface AdaptedScheduler {

    /**
     * Server wide scheduler instance, set by the plugin on enable.
     * <p>
     * Kept in a static holder so that static utilities (that have no plugin reference) can schedule
     * work in a Folia compatible way.
     */
    Holder HOLDER = new Holder();

    final class Holder {
        private volatile AdaptedScheduler instance;

        private Holder() {

        }

        @Nullable
        public AdaptedScheduler get() {
            return this.instance;
        }

        public void set(@Nullable AdaptedScheduler instance) {
            this.instance = instance;
        }
    }

    /**
     * @return the server wide scheduler, or {@code null} when the plugin is not enabled yet.
     */
    @Nullable
    static AdaptedScheduler instance() {
        return HOLDER.get();
    }

    static void setInstance(@Nullable AdaptedScheduler scheduler) {
        HOLDER.set(scheduler);
    }

    void cancelTasks();

    @NonNull
    AdaptedTask runTask(@NonNull Runnable runnable);

    @Nullable
    AdaptedTask runTask(@NonNull Entity entity, @NonNull Runnable runnable);

    @NonNull
    AdaptedTask runTask(@NonNull Location location, @NonNull Runnable runnable);

    @NonNull
    AdaptedTask runTask(@NonNull Chunk chunk, @NonNull Runnable runnable);

    @NonNull
    AdaptedTask runTaskAsync(@NonNull Runnable runnable);

    @NonNull
    AdaptedTask runTaskLater(@NonNull Runnable runnable, long delay);

    @NonNull
    AdaptedTask runTaskLaterAsync(@NonNull Runnable runnable, long delay);

    @NonNull
    AdaptedTask runTaskTimer(@NonNull Runnable runnable, long delay, long period);

    @NonNull
    AdaptedTask runTaskTimerAsync(@NonNull Runnable runnable, long delay, long period);
}
