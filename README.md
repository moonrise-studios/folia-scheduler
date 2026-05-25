# Folia Scheduler

Small convenience wrapper around Paper's Folia scheduler APIs. It gives plugin
code one static entrypoint, `Scheduler`, for choosing the correct execution
owner: entity, location, global region, or async.

This library does not make unsafe Bukkit access safe. It makes the intended
scheduler choice shorter and more consistent.

## Requirements

- Java 21
- Paper API `1.21.8-R0.1-SNAPSHOT`
- A Paper/Folia runtime with `io.papermc.paper.threadedregions.scheduler`

## Dependency

Add the Moonrise repository to your Gradle build.

```diff
repositories {
    mavenCentral()
+   maven("https://repo.moonrise.gg/repository/maven-releases")
+   maven("https://repo.moonrise.gg/repository/maven-snapshots")
}
```

Choose one runtime strategy for your plugin: load the dependency at runtime with
Paper, or shade and relocate it into your plugin jar. `compileOnly` by itself is
not enough at runtime.

### Paper Runtime Dependency Loader

Use `compileOnly` when your plugin loads the scheduler artifact through Paper's
runtime dependency loader.

```diff
dependencies {
+   compileOnly("gg.moonrise.scheduler:folia-scheduler:1.0.0")
+   // Or, while testing unreleased changes:
+   // compileOnly("gg.moonrise.scheduler:folia-scheduler:1.0.0-SNAPSHOT")
}
```

Add the loader to `paper-plugin.yml`.

```diff
name: ExamplePlugin
version: '1.0.0'
main: com.example.myplugin.MyPlugin
api-version: '1.21'
+ loader: com.example.myplugin.MyPluginLoader
```

Then add the scheduler artifact to the plugin classpath from that loader.

```diff
package com.example.myplugin;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
+import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
+import org.eclipse.aether.artifact.DefaultArtifact;
+import org.eclipse.aether.graph.Dependency;
+import org.eclipse.aether.repository.RemoteRepository;

public final class MyPluginLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
+       MavenLibraryResolver resolver = new MavenLibraryResolver();
+       resolver.addRepository(new RemoteRepository.Builder(
+               "moonrise-releases",
+               "default",
+               "https://repo.moonrise.gg/repository/maven-releases"
+       ).build());
+       resolver.addRepository(new RemoteRepository.Builder(
+               "moonrise-snapshots",
+               "default",
+               "https://repo.moonrise.gg/repository/maven-snapshots"
+       ).build());
+       resolver.addDependency(new Dependency(
+               new DefaultArtifact("gg.moonrise.scheduler:folia-scheduler:1.0.0"),
+               null
+       ));
+
+       classpathBuilder.addLibrary(resolver);
    }
}
```

### Shaded Dependency

Use `implementation` when your plugin bundles the scheduler into its own jar.
Shade and relocate the scheduler package so your bundled copy cannot collide
with another plugin's copy.

```diff
plugins {
+   id("com.gradleup.shadow") version("9.2.2")
}

dependencies {
+   implementation("gg.moonrise.scheduler:folia-scheduler:1.0.0")
}

tasks.shadowJar {
+   relocate("gg.moonrise.scheduler", "com.example.myplugin.libs.foliascheduler")
}
```

Package and distribute the Shadow jar, not the plain jar.

## Initialize

Call `Scheduler.init(...)` once during plugin startup, before any code uses
`Scheduler.entity(...)`, `Scheduler.location()`, `Scheduler.sync()`, or
`Scheduler.async()`.

```java
package com.example.myplugin;

import gg.moonrise.scheduler.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Scheduler.init(this);
        // Register commands, listeners, services, and menus after init.
    }
}
```

Calling a scheduler entrypoint before initialization throws an
`IllegalStateException`.

## Choosing A Scheduler

Use the most specific owner for the state you are touching.

| Entrypoint | Use for | Avoid using for |
| --- | --- | --- |
| `Scheduler.entity(entity)` | Player/entity messages, inventory changes, teleports, entity mutation, entity removal, async callback handoff back to a player | Global server state or block/chunk work not owned by that entity |
| `Scheduler.location()` | Block, chunk, world, region, and location-owned state | Player inventory/message work when you already have the player |
| `Scheduler.sync()` | Global-region tasks, plugin-wide timers, broadcasts, command dispatch, and state that is not tied to a specific entity or location | Entity or world reads/writes that have a better owner |
| `Scheduler.async()` | Storage, file IO, network calls, serialization, CPU work, and other non-Bukkit background work | Bukkit world, entity, inventory, or block access |

The safest default is: if you have a `Player` or `Entity`, use
`Scheduler.entity(...)`; if you have a `Location`, `Block`, or `Chunk`, use
`Scheduler.location()`; use `Scheduler.sync()` only for truly global work.

## Entity-Owned Work

Entity scheduling is the right choice for player-facing async callbacks. Load
data off-thread, then return to the player's scheduler before messaging or
mutating the player.

```java
import gg.moonrise.scheduler.Scheduler;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public void sendLoadedMessage(Player player, CompletableFuture<String> messageFuture) {
    messageFuture.thenAccept(message ->
            Scheduler.entity(player).run(task ->
                    player.sendRichMessage(message)
            )
    );
}
```

Use the same owner for simple player inventory or message work.

```java
import gg.moonrise.scheduler.Scheduler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public void giveReward(Player player, ItemStack reward) {
    Scheduler.entity(player).run(task -> {
        player.getInventory().addItem(reward);
        player.sendRichMessage("<green>Reward claimed.");
    });
}
```

For delayed player work, use `runDelayed(...)`.

```java
import gg.moonrise.scheduler.Scheduler;
import org.bukkit.entity.Player;

import java.time.Duration;

public void remindLater(Player player) {
    Scheduler.entity(player).runDelayed(
            task -> player.sendRichMessage("<yellow>Reminder."),
            Duration.ofSeconds(5)
    );
}
```

Entity removal should also be entity-owned.

```java
import gg.moonrise.scheduler.Scheduler;
import org.bukkit.World;
import org.bukkit.entity.Mob;

public void removeMobs(World world) {
    for (Mob mob : world.getEntitiesByClass(Mob.class)) {
        Scheduler.entity(mob).run(task -> mob.remove());
    }
}
```

## Location-Owned Work

Use the location scheduler for block, chunk, and region-owned state.

```java
import gg.moonrise.scheduler.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;

public void inspectSpawn(Location location) {
    Scheduler.location().run(location, task -> {
        if (location.getBlock().isEmpty()) {
            location.getBlock().setType(Material.LIGHT);
        }
    });
}
```

Chunk coordinates and `Chunk` objects are supported too, using the same
location scheduler entrypoint.

## Global-Region Work

Use `Scheduler.sync()` for work that belongs to the global region instead of an
entity or location.

```java
import gg.moonrise.scheduler.Scheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public void broadcastReload() {
    Scheduler.sync().run(task ->
            Bukkit.broadcast(Component.text("Configuration reloaded."))
    );
}
```

Delayed and repeating global tasks are useful for plugin-wide timers. Keep the
returned `ScheduledTask` if you need to cancel a repeating task later.

```java
import gg.moonrise.scheduler.Scheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.time.Duration;

public void clearMessageLater() {
    Scheduler.sync().runDelayed(
            task -> Bukkit.broadcast(Component.text("Timer finished.")),
            Duration.ofSeconds(10)
    );
}
```

## Async Work

Use `Scheduler.async()` for work that does not touch Bukkit world, entity,
inventory, or block state. Return to an entity, location, or global scheduler
before interacting with Bukkit state again.

```java
import gg.moonrise.scheduler.Scheduler;
import org.bukkit.entity.Player;

public void saveSettings(Player player, Settings settings) {
    Scheduler.async().run(task -> {
        settings.saveToDisk();

        Scheduler.entity(player).run(doneTask ->
                player.sendRichMessage("<green>Settings saved.")
        );
    });
}
```

Async delayed and repeating tasks also use `Duration` or explicit `TimeUnit`.

```java
import gg.moonrise.scheduler.Scheduler;

import java.time.Duration;

public void scheduleCacheFlush() {
    Scheduler.async().schedule(
            task -> cache.saveToDisk(),
            Duration.ofMinutes(5)
    );
}
```

## Timing Rules

- Numeric delays and periods on `Scheduler.entity(...)`, `Scheduler.location()`,
  and `Scheduler.sync()` are ticks.
- `Duration` overloads on `Scheduler.entity(...)`, `Scheduler.location()`, and
  `Scheduler.sync()` convert milliseconds to ticks with `duration.toMillis() / 50`.
- `Scheduler.async()` delegates to Paper's async scheduler and uses
  `Duration`/`TimeUnit` timing instead of Bukkit ticks.
- A zero-delay or immediate `run(...)` should be used when you only need to
  cross back to the correct scheduler owner.

## Practical Rules

- Initialize once in `onEnable()` before registering code that may schedule work.
- Prefer entity-owned tasks for anything involving a `Player` or other `Entity`.
- Prefer location-owned tasks for block, chunk, region, or world-position work.
- Keep Bukkit API access out of async tasks unless the specific API is documented
  as async-safe.
- Keep `ScheduledTask` handles for repeating or long-delay tasks that need
  lifecycle cancellation.
- Use retired callbacks for entity tasks when a missed execution requires
  cleanup, rollback, or state invalidation.
