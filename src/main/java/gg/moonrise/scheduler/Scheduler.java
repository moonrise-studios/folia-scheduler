package gg.moonrise.scheduler;

import gg.moonrise.scheduler.impl.AsyncScheduler;
import gg.moonrise.scheduler.impl.EntityScheduler;
import gg.moonrise.scheduler.impl.LocationScheduler;
import gg.moonrise.scheduler.impl.SyncScheduler;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Static entrypoint for the Folia scheduler wrapper.
 * <p>
 * Initialize this class once with {@link #init(JavaPlugin)} during plugin
 * startup, then choose the scheduler that owns the state being touched:
 * entity-owned work through {@link #entity(Entity)}, region-owned work through
 * {@link #location()}, global-region work through {@link #sync()}, and
 * background work through {@link #async()}.
 * <p>
 * This wrapper only shortens Paper/Folia scheduler calls. It does not make
 * unsafe Bukkit API access safe on the wrong scheduler.
 */
public class Scheduler {

    private static JavaPlugin INSTANCE;

    private static SyncScheduler SYNC_SCHEDULER;
    private static AsyncScheduler ASYNC_SCHEDULER;
    private static LocationScheduler LOCATION_SCHEDULER;

    /**
     * Creates a scheduler entrypoint instance.
     * <p>
     * The scheduler API is intended to be used through static methods; this
     * constructor exists for source and binary compatibility with the implicit
     * public constructor exposed by earlier versions.
     */
    public Scheduler() {
    }

    /**
     * Initializes the shared schedulers for a plugin.
     * <p>
     * Call this once during plugin startup before using any other method on this
     * class.
     *
     * @param plugin plugin instance used as the scheduler task owner
     */
    public static void init(JavaPlugin plugin) {
        INSTANCE = plugin;
        SYNC_SCHEDULER = new SyncScheduler(plugin, plugin.getServer().getGlobalRegionScheduler());
        ASYNC_SCHEDULER = new AsyncScheduler(plugin, plugin.getServer().getAsyncScheduler());
        LOCATION_SCHEDULER = new LocationScheduler(plugin, plugin.getServer().getRegionScheduler());
    }

    /**
     * Returns a scheduler bound to a specific entity.
     * <p>
     * Use this for player/entity messages, inventory mutation, teleports,
     * entity mutation, entity removal, and async callback handoff back to an
     * entity. If called before {@link #init(JavaPlugin)}, this method throws an
     * {@link IllegalStateException}.
     *
     * @param entity entity that owns the scheduled work
     * @param <T> entity type
     * @return scheduler wrapper for the entity
     */
    public static <T extends Entity> EntityScheduler entity(T entity) {
        if (INSTANCE == null) {
            throw new IllegalStateException("Scheduler has not been initialized. Call Scheduler.init(...) before using Scheduler.entity().");
        }
        return new EntityScheduler(INSTANCE, entity);
    }

    /**
     * Returns the global-region scheduler wrapper.
     * <p>
     * Use this for plugin-wide timers, broadcasts, command dispatch, and global
     * state that is not owned by one entity or region. Do not use it as a
     * substitute for entity-owned or location-owned Bukkit access. If called
     * before {@link #init(JavaPlugin)}, this method throws an
     * {@link IllegalStateException}.
     *
     * @return global-region scheduler wrapper
     */
    public static SyncScheduler sync() {
        if (SYNC_SCHEDULER == null) {
            throw new IllegalStateException("Scheduler has not been initialized. Call Scheduler.init(...) before using Scheduler.sync().");
        }
        return SYNC_SCHEDULER;
    }

    /**
     * Returns the asynchronous scheduler wrapper.
     * <p>
     * Use this for storage, file IO, network calls, serialization, CPU work, and
     * other background work that does not touch Bukkit world, entity, inventory,
     * or block state directly. If called before {@link #init(JavaPlugin)}, this
     * method throws an {@link IllegalStateException}.
     *
     * @return async scheduler wrapper
     */
    public static AsyncScheduler async() {
        if (ASYNC_SCHEDULER == null) {
            throw new IllegalStateException("Scheduler has not been initialized. Call Scheduler.init(...) before using Scheduler.async().");
        }
        return ASYNC_SCHEDULER;
    }

    /**
     * Returns the region scheduler wrapper for location- and chunk-owned work.
     * <p>
     * Use this for block, chunk, world, region, and location-owned state. If
     * called before {@link #init(JavaPlugin)}, this method throws an
     * {@link IllegalStateException}.
     *
     * @return location/region scheduler wrapper
     */
    public static LocationScheduler location() {
        if (LOCATION_SCHEDULER == null) {
            throw new IllegalStateException("Scheduler has not been initialized. Call Scheduler.init(...) before using Scheduler.location().");
        }
        return LOCATION_SCHEDULER;
    }
}
