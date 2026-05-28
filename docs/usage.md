# Usage Recipes

Use the most specific scheduler owner for the state you are about to touch.
These recipes are intended as quick lookup examples for agents and plugin
authors.

| Situation | Use | Reason |
| --- | --- | --- |
| Continue after async database work and message a player | `Scheduler.entity(player).run(...)` | Player messages and inventory operations are entity-owned. |
| Give or remove items from a player inventory | `Scheduler.entity(player).run(...)` | The player owns the inventory interaction. |
| Teleport, remove, or mutate an entity | `Scheduler.entity(entity).run(...)` | Entity state must run on that entity scheduler. |
| Change a block at a known location | `Scheduler.location().run(location, ...)` | Block and world state are owned by the target region. |
| Work with a chunk by coordinates | `Scheduler.location().run(world, chunkX, chunkZ, ...)` | Chunk state is region-owned. |
| Run a plugin-wide timer or broadcast | `Scheduler.sync().schedule(...)` | Global tasks belong on the global region scheduler. |
| Dispatch a command from plugin code | `Scheduler.sync().run(...)` | Command dispatch is global server work. |
| Load files, call a web API, or write to a database | `Scheduler.async().run(...)` | Blocking and CPU work should not run on region/entity schedulers. |
| Return from async work to Bukkit world state | `Scheduler.location()` or `Scheduler.entity(...)` | Choose the owner of the Bukkit state touched by the callback. |

## Common Patterns

### Async Load, Then Player Callback

```java
CompletableFuture<String> messageFuture = loadMessageAsync(player.getUniqueId());

messageFuture.thenAccept(message ->
        Scheduler.entity(player).run(task ->
                player.sendRichMessage(message)
        )
);
```

### Block Mutation

```java
Scheduler.location().run(location, task -> {
    location.getBlock().setType(Material.DIAMOND_BLOCK);
});
```

### Plugin-Wide Repeating Task

```java
Scheduler.sync().schedule(
        task -> getServer().broadcast(Component.text("Server heartbeat")),
        Duration.ofMinutes(1)
);
```

### Background Work

```java
Scheduler.async().run(task -> {
    savePlayerData(playerId, data);
});
```

## Pitfalls

- `Scheduler.sync()` is the global region scheduler, not a substitute for
  entity-owned or location-owned Bukkit access.
- `Scheduler.async()` must not touch Bukkit world, entity, inventory, or block
  state directly.
- `Duration` overloads on entity, location, and sync schedulers are converted to
  ticks by truncating milliseconds divided by 50.
- `compileOnly` is not enough at runtime unless Paper's runtime dependency
  loader adds the artifact to the plugin classpath.

