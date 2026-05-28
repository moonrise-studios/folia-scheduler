package gg.moonrise.scheduler.impl;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Convenience wrapper around Paper's entity scheduler.
 * <p>
 * Use this scheduler for work owned by a specific entity: player messages,
 * inventory mutation, teleports, entity mutation, entity removal, and async
 * callback handoff back to an entity.
 */
public class EntityScheduler {

    private final JavaPlugin plugin;
    private final Entity entity;

    /**
     * Creates a scheduler wrapper bound to one entity.
     *
     * @param plugin plugin instance used as the scheduler task owner
     * @param entity entity that owns scheduled work
     */
    public EntityScheduler(JavaPlugin plugin, Entity entity) {
        this.plugin = plugin;
        this.entity = entity;
    }

    /**
     * Executes a runnable on the entity scheduler after a tick delay.
     *
     * @param task task to execute
     * @param retired optional callback run by Paper when the entity scheduler is retired
     * @param delay delay in server ticks
     * @return {@code true} if Paper accepted the task
     */
    public boolean execute(Runnable task, Runnable retired, long delay) {
        return scheduler().execute(
                plugin,
                task,
                retired,
                delay
        );
    }

    /**
     * Executes a runnable on the entity scheduler after a duration delay.
     * <p>
     * The duration is converted to ticks with {@code delay.toMillis() / 50}.
     *
     * @param task task to execute
     * @param retired optional callback run by Paper when the entity scheduler is retired
     * @param delay delay before executing the task
     * @return {@code true} if Paper accepted the task
     */
    public boolean execute(Runnable task, Runnable retired, Duration delay) {
        return scheduler().execute(
                plugin,
                task,
                retired,
                delay.toMillis() / 50
        );
    }

    /**
     * Executes a runnable on the entity scheduler after a tick delay.
     *
     * @param task task to execute
     * @param delay delay in server ticks
     * @return {@code true} if Paper accepted the task
     */
    public boolean execute(Runnable task, long delay) {
        return execute(task, null, delay);
    }

    /**
     * Executes a runnable on the entity scheduler after a duration delay.
     * <p>
     * The duration is converted to ticks with {@code delay.toMillis() / 50}.
     *
     * @param task task to execute
     * @param delay delay before executing the task
     * @return {@code true} if Paper accepted the task
     */
    public boolean execute(Runnable task, Duration delay) {
        return execute(task, null, delay);
    }

    /**
     * Runs a scheduled task on the entity scheduler.
     *
     * @param task task to run
     * @param retired optional callback run by Paper when the entity scheduler is retired
     * @return scheduled task handle
     */
    public ScheduledTask run(Consumer<ScheduledTask> task, Runnable retired) {
        return scheduler().run(
                plugin,
                task,
                retired
        );
    }

    /**
     * Runs a scheduled task on the entity scheduler.
     *
     * @param task task to run
     * @return scheduled task handle
     */
    public ScheduledTask run(Consumer<ScheduledTask> task) {
        return run(task, null);
    }

    /**
     * Runs a scheduled task on the entity scheduler after a tick delay.
     *
     * @param task task to run
     * @param retired optional callback run by Paper when the entity scheduler is retired
     * @param delayTicks delay in server ticks
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Consumer<ScheduledTask> task, Runnable retired, long delayTicks) {
        return scheduler().runDelayed(
                plugin,
                task,
                retired,
                delayTicks
        );
    }

    /**
     * Runs a scheduled task on the entity scheduler after a tick delay.
     *
     * @param task task to run
     * @param delayTicks delay in server ticks
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Consumer<ScheduledTask> task, long delayTicks) {
        return runDelayed(task, null, delayTicks);
    }

    /**
     * Runs a scheduled task on the entity scheduler after a duration delay.
     * <p>
     * The duration is converted to ticks with {@code delay.toMillis() / 50}.
     *
     * @param task task to run
     * @param retired optional callback run by Paper when the entity scheduler is retired
     * @param delay delay before running the task
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Consumer<ScheduledTask> task, Runnable retired, Duration delay) {
        return scheduler().runDelayed(
                plugin,
                task,
                retired,
                delay.toMillis() / 50
        );
    }

    /**
     * Runs a scheduled task on the entity scheduler after a duration delay.
     * <p>
     * The duration is converted to ticks with {@code delay.toMillis() / 50}.
     *
     * @param task task to run
     * @param delay delay before running the task
     * @return scheduled task handle
     */
    public ScheduledTask runDelayed(Consumer<ScheduledTask> task, Duration delay) {
        return runDelayed(task, null, delay);
    }

    /**
     * Schedules a repeating task on the entity scheduler.
     *
     * @param task task to run
     * @param retired optional callback run by Paper when the entity scheduler is retired
     * @param delayTicks initial delay in server ticks
     * @param periodTicks period between executions in server ticks
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Consumer<ScheduledTask> task, Runnable retired, long delayTicks, long periodTicks) {
        return scheduler().runAtFixedRate(
                plugin,
                task,
                retired,
                delayTicks,
                periodTicks
        );
    }

    /**
     * Schedules a repeating task on the entity scheduler.
     *
     * @param task task to run
     * @param delayTicks initial delay in server ticks
     * @param periodTicks period between executions in server ticks
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Consumer<ScheduledTask> task, long delayTicks, long periodTicks) {
        return schedule(task, null, delayTicks, periodTicks);
    }

    /**
     * Schedules a repeating task on the entity scheduler.
     * <p>
     * Durations are converted to ticks with {@code duration.toMillis() / 50}.
     *
     * @param task task to run
     * @param retired optional callback run by Paper when the entity scheduler is retired
     * @param delay initial delay
     * @param period period between executions
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Consumer<ScheduledTask> task, Runnable retired, Duration delay, Duration period) {
        return scheduler().runAtFixedRate(
                plugin,
                task,
                retired,
                delay.toMillis() / 50,
                period.toMillis() / 50
        );
    }

    /**
     * Schedules a repeating task on the entity scheduler.
     * <p>
     * Durations are converted to ticks with {@code duration.toMillis() / 50}.
     *
     * @param task task to run
     * @param delay initial delay
     * @param period period between executions
     * @return scheduled task handle
     */
    public ScheduledTask schedule(Consumer<ScheduledTask> task, Duration delay, Duration period) {
        return schedule(task, null, delay, period);
    }

    /**
     * Returns the underlying Paper entity scheduler for this entity.
     *
     * @return Paper entity scheduler
     */
    public io.papermc.paper.threadedregions.scheduler.EntityScheduler scheduler() {
        return entity.getScheduler();
    }
}
