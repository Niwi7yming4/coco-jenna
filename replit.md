# 貓之國的記憶 (Coco & Jenna: Memories of the Cat Kingdom)

Minecraft 1.20.1 Forge mod featuring two cat companions (Coco & Jenna), a three-track growth system, the Cat Kingdom dimension, 50+ items/weapons, and a post-game "First Dawn" endgame event.

## Run & Operate

- **Build the mod:** `cd artifacts/coco-jenna-mod && ./gradlew build` (requires Forge MDK environment)
- **Run in dev:** `cd artifacts/coco-jenna-mod && ./gradlew runClient`
- **Run tests:** `cd artifacts/coco-jenna-mod && ./gradlew runGameTestServer`
- Mod ID: `cocojenna` | Forge: `47.2.0` | MC: `1.20.1` | Java: 17

## Where things live

```
artifacts/coco-jenna-mod/
├── build.gradle / gradle.properties         # Forge MDK build config
├── src/main/java/com/cocojenna/
│   ├── CocoJennaMod.java                    # Mod entry point
│   ├── init/      ModItems, ModBlocks, ModEntities, ModSounds, ModEffects, ModBiomes, ModDimensions
│   ├── entity/    CocoEntity, JennaEntity, + 8 NPC/boss entities + 7 AI Goals
│   ├── item/      29 item classes (DaikataItem, RyokatanaItem, SealOrbItem, ...)
│   ├── block/     17 block classes (DistillerBlock, CatKingdomPortalBlock, ...)
│   ├── capability/ BondData.java (source-of-truth for all bond/growth data) + ModCapabilities
│   ├── network/   ModNetwork + 4 packet classes
│   ├── event/     ModEventHandler, AttributeRegistrationHandler
│   └── client/    MemoryBookScreen, FirstDawnRenderer, MindSyncRenderer
└── src/main/resources/
    ├── META-INF/mods.toml
    ├── pack.mcmeta
    ├── assets/cocojenna/
    │   ├── lang/   zh_tw.json (primary), en_us.json
    │   ├── sounds.json
    │   ├── models/ (129 item+block models)
    │   └── blockstates/ (18 blockstates)
    └── data/cocojenna/
        ├── dimension/ + dimension_type/   (Cat Kingdom)
        ├── recipes/   (38 crafting recipes)
        ├── loot_tables/  (8 entity+block tables)
        ├── advancements/ (8 advancements)
        └── tags/      (5 item+block tags)
```

## Architecture decisions

- **BondData** (`capability/BondData.java`) is the single source of truth for all growth tracking: Emotion (0–100), Independence (0–100), Awakening (0–50), Sister Bond, memory shards, and last-interact timestamps for both Coco and Jenna.
- **Capability registration** uses Forge 47.x `RegisterCapabilitiesEvent` (fired on mod event bus) — not the deprecated `@CapabilityInject` pattern from 1.19.
- **Network** uses `SimpleChannel` with 4 packets: `SyncBondDataPacket`, `OpenMemoryBookPacket`, `TriggerFirstDawnPacket`, `MindSyncViewPacket`.
- **Dimension** (Cat Kingdom) is defined via data pack JSONs under `data/cocojenna/dimension/` + `dimension_type/`. `ModDimensions` only holds the `ResourceKey<Level>`.
- **Entity attributes** registered via `AttributeRegistrationHandler` on `EntityAttributeCreationEvent` (MOD bus).

## Product

- **Two cat companions:** Coco (black cat, protective) and Jenna (tortoiseshell, playful) follow and interact with the player.
- **Three-track growth:** Emotion / Independence / Awakening — each cat grows independently across 38+ items and interactions.
- **Sister Bond system:** Shared bond mechanic unlocking cooperative abilities and unique dialogue.
- **50+ items:** 21 legendary Daikatana + 50 Ryokatana swords, cat foods, seal orbs, memory shards, tools, gear.
- **Cat Kingdom dimension:** 7 biomes, portal-based entry, unique mob ecology.
- **Combat → Seal Orb:** Defeat enemies, collect their essence into seal orbs for progression.
- **First Dawn endgame:** Triggered by defeating the Shadow Claw boss; unlocks post-game content and sequences.

## User preferences

_Populate as you build._

## Gotchas

- **Never call `ModCapabilities.register()` from `commonSetup`** — capability registration must happen via `RegisterCapabilitiesEvent` on the mod event bus (already wired in `CocoJennaMod.java`).
- **Mod event bus vs Forge event bus:** `AttributeRegistrationHandler` uses `Bus.MOD`. `ModEventHandler` uses `Bus.FORGE` (default).
- **Gradle build requires Forge MDK setup** — the project cannot be built directly in Replit's environment; it needs a Java 17 + Forge MDK environment.
- **Textures are placeholder** — all `assets/cocojenna/textures/` directories exist but actual `.png` texture files need to be provided by an artist before the mod is visually complete.
- **Dimension worldgen** uses `minecraft:fixed` biome source pointing to `cocojenna:velvet_forest`; proper multi-biome generation requires a custom `BiomeSource`.

## Pointers

- `BondData.java` — all growth data fields and NBT serialization
- `ModItems.java` — complete registry of all 60+ items including spawn eggs
- `DaikataItem.java` — Skill enum with 20 skills, right-click charge mechanic
- `ModEventHandler.java` — daily tick, hurt notification, death, shard pickup, First Dawn trigger
