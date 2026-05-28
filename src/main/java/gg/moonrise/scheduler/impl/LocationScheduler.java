package gg.moonrise.scheduler.impl;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Convenience wrapper around Paper's region scheduler.
 * <p>
 * Use this scheduler for block, chunk, world, region, and location-owned state.
 * Prefer {@code Scheduler.entity(entity)} when the work is owned by a player or
 * other entity.
 */
public class LocationScheduler {

    private final JavaPlugin plugin;
    private final RegionScheduler scheduler;

    /**
     * Creates a wrapper for Paper's region scheduler.
     *
     * @param plugin plugin instance used as the scheduler task owner
     * @param scheduler Paper region scheduler
     */
    public LocationScheduler(JavaPlugin plugin, RegionScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    /**
     * Executes a runnable in the region that owns a location.
     *
     * @param location location whose region owns the work
     * @param task task to execute
     */
    public void executeLocation(Location location, Runnable task) {
        scheduler.execute(plugin, location, task);
    }

    /**
     * Executes a runnable in the region that owns chunk coordinates.
     *
     * @param world world containing the chunk
     * @param chunkX chunk x coordinate
     * @param chunkZ chunk z coordinate
     * @param task task to execute
     */
    public void executeChunk(World world, int chunkX, int chunkZ, Runnable task) {
        scheduler.execute(
            plugin,
            world,
            chunkX,
            chunkZ,
            task
        );
    }

    /**
     * Executes a runnable in the region that owns a chunk.
     *
     * @param chunk chunk whose region owns the work
     * @param task task to execute
     */
    public void executeChunk(Chunk chunk, Runnable task) {
        executeChunk(
            chunk.getWorld(),
            chunk.getX(),
            chunk.getZ(),
            task
        );
    }

    /**
     * Runs a scheduled task in the region that owns a location.
     *
     * @param location location whose region owns the work
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask run(Location location, Consumer<ScheduledTask> task) {
        return scheduler.run(
            plugin,
            location,
            task
        );
    }

    /**
     * Runs a scheduled task in the region that owns chunk coordinates.
     *
     * @param world world containing the chunk
     * @param chunkX chunk x coordinate
     * @param chunkZ chunk z coordinate
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask run(World world, int chunkX, int chunkZ, Consumer<ScheduledTask> task) {
        return scheduler.run(
            plugin,
            world,
            chunkX,
            chunkZ,
            task
        );
    }

    /**
     * Runs a scheduled task in the region that owns a chunk.
     *
     * @param chunk chunk whose region owns the work
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask run(Chunk chunk, Consumer<ScheduledTask> task) {
        return run(
                chunk.getWorld(),
                chunk.getX(),
                chunk.getZ(),
                task
        );
    }

    /**
     * Runs a delayed task in the region that owns a location.
     * <p>
     * The duration is converted to ticks with {@code delay.toMillis() / 50}.
     *
     * @param location location whose region owns the work
     * @param delay delay before running the task
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Location location, Duration delay, Consumer<ScheduledTask> task) {
        return runDelayed(
            location,
            delay.toMillis() / 50,
            task
        );
    }

    /**
     * Runs a delayed task in the region that owns a location.
     *
     * @param location location whose region owns the work
     * @param delay delay in server ticks
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Location location, long delay, Consumer<ScheduledTask> task) {
        return scheduler.runDelayed(
            plugin,
            location,
            task,
            delay
        );
    }

    /**
     * Runs a delayed task in the region that owns chunk coordinates.
     * <p>
     * The duration is converted to ticks with {@code delay.toMillis() / 50}.
     *
     * @param world world containing the chunk
     * @param chunkX chunk x coordinate
     * @param chunkZ chunk z coordinate
     * @param delay delay before running the task
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(World world, int chunkX, int chunkZ, Duration delay, Consumer<ScheduledTask> task) {
        return runDelayed(
                world,
                chunkX,
                chunkZ,
                delay.toMillis() / 50,
                task
        );
    }

    /**
     * Runs a delayed task in the region that owns chunk coordinates.
     *
     * @param world world containing the chunk
     * @param chunkX chunk x coordinate
     * @param chunkZ chunk z coordinate
     * @param delay delay in server ticks
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(World world, int chunkX, int chunkZ, long delay, Consumer<ScheduledTask> task) {
        return scheduler.runDelayed(
                plugin,
                world,
                chunkX,
                chunkZ,
                task,
                delay
        );
    }

    /**
     * Runs a delayed task in the region that owns a chunk.
     * <p>
     * The duration is converted to ticks with {@code delay.toMillis() / 50}.
     *
     * @param chunk chunk whose region owns the work
     * @param delay delay before running the task
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Chunk chunk, Duration delay, Consumer<ScheduledTask> task) {
        return runDelayed(
                chunk.getWorld(),
                chunk.getX(),
                chunk.getZ(),
                delay.toMillis() / 50,
                task
        );
    }

    /**
     * Runs a delayed task in the region that owns a chunk.
     *
     * @param chunk chunk whose region owns the work
     * @param delay delay in server ticks
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Chunk chunk, long delay, Consumer<ScheduledTask> task) {
        return runDelayed(
                chunk.getWorld(),
                chunk.getX(),
                chunk.getZ(),
                delay,
                task
        );
    }

    /**
     * Schedules a repeating task in the region that owns a location.
     * <p>
     * Durations are converted to ticks with {@code duration.toMillis() / 50}.
     *
     * @param location location whose region owns the work
     * @param delay initial delay
     * @param period period between executions
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Location location, Duration delay, Duration period, Consumer<ScheduledTask> task) {
        return schedule(
            location,
            delay.toMillis() / 50,
            period.toMillis() / 50,
            task
        );
    }

    /**
     * Schedules a repeating task in the region that owns a location.
     *
     * @param location location whose region owns the work
     * @param delay initial delay in server ticks
     * @param period period between executions in server ticks
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Location location, long delay, long period, Consumer<ScheduledTask> task) {
        return scheduler.runAtFixedRate(
            plugin,
            location,
            task,
            delay,
            period
        );
    }

    /**
     * Schedules a repeating task in the region that owns chunk coordinates.
     * <p>
     * Durations are converted to ticks with {@code duration.toMillis() / 50}.
     *
     * @param world world containing the chunk
     * @param chunkX chunk x coordinate
     * @param chunkZ chunk z coordinate
     * @param delay initial delay
     * @param period period between executions
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask schedule(World world, int chunkX, int chunkZ, Duration delay, Duration period, Consumer<ScheduledTask> task) {
        return schedule(
                world,
                chunkX,
                chunkZ,
                delay.toMillis() / 50,
                period.toMillis() / 50,
                task
        );
    }

    /**
     * Schedules a repeating task in the region that owns chunk coordinates.
     *
     * @param world world containing the chunk
     * @param chunkX chunk x coordinate
     * @param chunkZ chunk z coordinate
     * @param delay initial delay in server ticks
     * @param period period between executions in server ticks
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask schedule(World world, int chunkX, int chunkZ, long delay, long period, Consumer<ScheduledTask> task) {
        return scheduler.runAtFixedRate(
                plugin,
                world,
                chunkX,
                chunkZ,
                task,
                delay,
                period
        );
    }

    /**
     * Schedules a repeating task in the region that owns a chunk.
     * <p>
     * Durations are converted to ticks with {@code duration.toMillis() / 50}.
     *
     * @param chunk chunk whose region owns the work
     * @param delay initial delay
     * @param period period between executions
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Chunk chunk, Duration delay, Duration period, Consumer<ScheduledTask> task) {
        return schedule(
                chunk.getWorld(),
                chunk.getX(),
                chunk.getZ(),
                delay.toMillis() / 50,
                period.toMillis() / 50,
                task
        );
    }

    /**
     * Schedules a repeating task in the region that owns a chunk.
     *
     * @param chunk chunk whose region owns the work
     * @param delay initial delay in server ticks
     * @param period period between executions in server ticks
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Chunk chunk, long delay, long period, Consumer<ScheduledTask> task) {
        return schedule(
                chunk.getWorld(),
                chunk.getX(),
                chunk.getZ(),
                delay,
                period,
                task
        );
    }

}
