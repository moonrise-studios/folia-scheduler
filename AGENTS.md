# Agent Guide

This repository is a small Java 21 library that wraps Paper/Folia scheduler APIs
behind one static entrypoint: `gg.moonrise.scheduler.Scheduler`.

## Core Model

- Call `Scheduler.init(JavaPlugin)` once during plugin startup before using any
  scheduler entrypoint.
- This library chooses a Paper/Folia execution owner. It does not make unsafe
  Bukkit access safe.
- Use the most specific owner for the state being touched:
  - `Scheduler.entity(entity)` for player/entity messages, inventory mutation,
    teleports, entity mutation, and async callback handoff back to an entity.
  - `Scheduler.location()` for block, chunk, world, region, and location-owned
    work.
  - `Scheduler.sync()` for global-region work such as plugin timers,
    broadcasts, command dispatch, and plugin-wide state.
  - `Scheduler.async()` for storage, file IO, network calls, serialization, and
    CPU work that does not touch Bukkit world/entity/inventory/block state.

## API Conventions

- `long` delay and period parameters on entity, location, and sync schedulers
  are server ticks.
- `Duration` overloads on entity, location, and sync schedulers convert
  milliseconds to ticks with `duration.toMillis() / 50`.
- `AsyncScheduler` uses Paper's async scheduler and passes explicit
  `TimeUnit`s; `Duration` overloads use milliseconds.
- Entity scheduler `retired` callbacks are optional Paper/Folia callbacks used
  when entity-owned work cannot run because the entity scheduler is retired.
- Entrypoints throw `IllegalStateException` if used before `Scheduler.init(...)`.

## Editing Guidance

- Keep the public surface compact and close to the underlying Paper/Folia API.
- Do not hide scheduler ownership decisions behind generic "run sync" helpers.
- Preserve existing package names, artifact coordinates, and Java 21 target.
- Add examples when behavior is operator- or integrator-visible.
- Prefer explicit owner names in documentation: entity, location/region,
  global region, and async.

## Verification

Use the Gradle wrapper from the repository root:

```bash
./gradlew build
./gradlew javadoc
```

