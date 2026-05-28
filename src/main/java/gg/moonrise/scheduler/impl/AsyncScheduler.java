package gg.moonrise.scheduler.impl;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Convenience wrapper around Paper's asynchronous scheduler.
 * <p>
 * Use this scheduler for storage, file IO, network calls, serialization, CPU
 * work, and other background work that does not directly touch Bukkit world,
 * entity, inventory, or block state.
 */
public class AsyncScheduler {

    private final JavaPlugin plugin;
    private final io.papermc.paper.threadedregions.scheduler.AsyncScheduler scheduler;

    /**
     * Creates a wrapper for Paper's async scheduler.
     *
     * @param plugin plugin instance used as the scheduler task owner
     * @param scheduler Paper async scheduler
     */
    public AsyncScheduler(JavaPlugin plugin, io.papermc.paper.threadedregions.scheduler.AsyncScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    /**
     * Schedules a repeating async task with no initial delay.
     *
     * @param task task to run
     * @param period period between executions
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Consumer<ScheduledTask> task, Duration period) {
        return schedule(
                task,
                0,
                period.toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Schedules a repeating async task.
     *
     * @param task task to run
     * @param delay initial delay
     * @param period period between executions
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Consumer<ScheduledTask> task, Duration delay, Duration period) {
        return schedule(
                task,
                delay.toMillis(),
                period.toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Schedules a repeating async task using an explicit time unit.
     *
     * @param task task to run
     * @param delay initial delay in {@code unit}
     * @param period period between executions in {@code unit}
     * @param unit time unit for delay and period
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Consumer<ScheduledTask> task, long delay, long period, TimeUnit unit) {
        return scheduler.runAtFixedRate(
                plugin,
                task,
                delay,
                period,
                unit
        );
    }

    /**
     * Runs an async task after a duration delay.
     *
     * @param task task to run
     * @param delay delay before running the task
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Consumer<ScheduledTask> task, Duration delay) {
        return scheduler.runDelayed(
                plugin,
                task,
                delay.toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Runs an async task after a delay using an explicit time unit.
     *
     * @param task task to run
     * @param delay delay in {@code unit}
     * @param unit time unit for the delay
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Consumer<ScheduledTask> task, long delay, TimeUnit unit) {
        return scheduler.runDelayed(
                plugin,
                task,
                delay,
                unit
        );
    }

    /**
     * Runs an async task immediately.
     *
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask run(Consumer<ScheduledTask> task) {
        return scheduler.runNow(plugin, task);
    }

}
