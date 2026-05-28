package gg.moonrise.scheduler.impl;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Convenience wrapper around Paper's global region scheduler.
 * <p>
 * Use this scheduler for global-region tasks such as plugin-wide timers,
 * broadcasts, command dispatch, and shared plugin state. It is not a safe
 * substitute for entity-owned or location-owned Bukkit access.
 */
public class SyncScheduler {

    private final JavaPlugin plugin;
    private final GlobalRegionScheduler scheduler;

    /**
     * Creates a wrapper for Paper's global region scheduler.
     *
     * @param plugin plugin instance used as the scheduler task owner
     * @param scheduler Paper global region scheduler
     */
    public SyncScheduler(JavaPlugin plugin, GlobalRegionScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    /**
     * Runs a task on the global region scheduler.
     *
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask run(Consumer<ScheduledTask> task) {
        return scheduler.run(
                plugin,
                task
        );
    }

    /**
     * Runs a task on the global region scheduler after a tick delay.
     *
     * @param task task to run
     * @param delayTicks delay in server ticks
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Consumer<ScheduledTask> task, long delayTicks) {
        return scheduler.runDelayed(
                plugin,
                task,
                delayTicks
        );
    }

    /**
     * Runs a task on the global region scheduler after a duration delay.
     * <p>
     * The duration is converted to ticks with {@code delay.toMillis() / 50}.
     *
     * @param task task to run
     * @param delay delay before running the task
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Consumer<ScheduledTask> task, Duration delay) {
        return runDelayed(
                task,
                delay.toMillis() / 50
        );
    }

    /**
     * Schedules a repeating task on the global region scheduler.
     *
     * @param task task to run
     * @param delayTicks initial delay in server ticks
     * @param periodTicks period between executions in server ticks
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Consumer<ScheduledTask> task, long delayTicks, long periodTicks) {
        return scheduler.runAtFixedRate(
                plugin,
                task,
                delayTicks,
                periodTicks
        );
    }

    /**
     * Schedules a repeating task on the global region scheduler.
     * <p>
     * The same duration is used for the initial delay and period, converted to
     * ticks with {@code delay.toMillis() / 50}.
     *
     * @param task task to run
     * @param delay initial delay and period
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Consumer<ScheduledTask> task, Duration delay) {
        return schedule(
                task,
                delay.toMillis() / 50,
                delay.toMillis() / 50
        );
    }

    /**
     * Executes a runnable on the global region scheduler.
     *
     * @param runnable runnable to execute
     */
    public void execute(Runnable runnable) {
        scheduler.execute(plugin, runnable);
    }

}
