---
name: Coco & Jenna Mod Architecture
description: Key architectural decisions for the cocojenna Forge 1.20.1 mod.
---

## Project location

`artifacts/coco-jenna-mod/` — standalone Gradle project (not part of the pnpm workspace).

## Key decisions

**BondData is the single source of truth** for all player-cat relationship data:
- Fields: cocoEmotion, jennaEmotion, cocoIndependence, jennaIndependence, cocoAwakening, jennaAwakening, sisterBond, memoryShardsTotal, lastInteractCoco, lastInteractJenna, endgameUnlocked
- Stored as player Capability, serialized to NBT, synced via SyncBondDataPacket on login and state changes
- Preserved across death via `PlayerEvent.Clone`

**Why:** All 50+ items, 20+ entity interactions, and event handlers need to read/write this data. Single class prevents desync.

**Dimension (Cat Kingdom):** Defined entirely via data pack JSONs in `data/cocojenna/dimension/` + `dimension_type/`. `ModDimensions.java` only holds the `ResourceKey<Level>` constant. No code-side dimension registration needed in Forge 47.x.

**Entity attributes:** Registered via `AttributeRegistrationHandler` listening to `EntityAttributeCreationEvent` on the MOD bus (`@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)`).

**Network:** 4 packets on `SimpleChannel "cocojenna:main"`:
1. `SyncBondDataPacket` — server→client, sends full NBT BondData
2. `OpenMemoryBookPacket` — server→client, opens Memory Book GUI
3. `TriggerFirstDawnPacket` — server→nearby clients, plays First Dawn event
4. `MindSyncViewPacket` — server→client, triggers camera switch

**Texture status:** All model JSONs reference textures at `cocojenna:item/<name>` and `cocojenna:block/<name>` but actual PNG files are NOT included — need an artist.
