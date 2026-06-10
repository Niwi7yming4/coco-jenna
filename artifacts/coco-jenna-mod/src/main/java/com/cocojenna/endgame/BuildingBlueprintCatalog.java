package com.cocojenna.endgame;

import net.minecraft.network.chat.Component;

import java.util.*;

/** 貓咪建築大師 — 藍圖與 BOM（設計書第七章完整清單）. */
public final class BuildingBlueprintCatalog {

    public record Blueprint(String id, int requiredProgress, Map<String, Integer> bom, int forgeLevelGain) {
        public Blueprint(String id, int requiredProgress, Map<String, Integer> bom) {
            this(id, requiredProgress, bom, 0);
        }

        public Component name() {
            return Component.translatable("building.cocojenna." + id);
        }
    }

    private static final Map<String, Blueprint> BY_ID = new LinkedHashMap<>();

    static {
        // ── 設計書 7.1 可建造建築 ──────────────────────────────────────
        reg("small_cat_house", 40, Map.of("velvet_planks", 30, "woven_wool", 20));
        regForge("ironpaw_forge_upgrade", 80, Map.of("iron_ingot", 50, "stardust_brick", 30), 1);
        regForge("ironpaw_forge_master", 100, Map.of("iron_ingot", 80, "purr_crystal", 10, "precision_gear", 20), 1);
        reg("sanbana_sewing", 70, Map.of("woven_wool", 40, "velvet_fur", 20));
        reg("distiller_station", 30, Map.of("iron_ingot", 10, "glass", 5));
        reg("pure_light_tower_build", 90, Map.of("moonstone_brick", 20, "purr_crystal", 3));
        reg("memory_lighthouse_build", 100, Map.of("memory_shard", 30, "stardust_brick", 40));
        reg("cat_paradise", 85, Map.of("velvet_planks", 50, "cat_scratch_board", 30));
        reg("mine_entrance", 50, Map.of("oak_planks", 20, "iron_ingot", 10));

        // ── 雨後 DLC 擴充建築 ───────────────────────────────────────────
        reg("cat_library", 100, Map.of("book", 50, "velvet_fur", 20));
        reg("open_air_theater", 80, Map.of("moonstone_brick", 48, "velvet_block", 16));
        reg("hot_spring", 90, Map.of("salt_crystal", 24, "stardust_soil", 32));
        reg("floating_platform", 60, Map.of("velvet_fur", 64, "moonstone", 12));
        reg("market_square", 70, Map.of("moonstone_brick", 32, "hibiscus_flower", 16));
        reg("cat_school", 85, Map.of("moonstone", 20, "catnip", 32, "velvet_fur", 24));
        reg("stargazer_tower", 110, Map.of("moonstone", 48, "purr_crystal", 16));
        reg("puree_fountain", 50, Map.of("spore_fruit", 16, "velvet_grass", 32));
        reg("festival_stage", 75, Map.of("moonstone_brick", 40, "neon_mushroom", 12));
        reg("memory_monument_tier2", 120, Map.of("memory_shard", 10, "moonstone", 32));
        reg("memory_theater", 95, Map.of("memory_clay", 16, "memory_shard", 8, "moonstone_brick", 24));
    }

    private static void reg(String id, int req, Map<String, Integer> bom) {
        BY_ID.put(id, new Blueprint(id, req, bom));
    }

    private static void regForge(String id, int req, Map<String, Integer> bom, int forgeGain) {
        BY_ID.put(id, new Blueprint(id, req, bom, forgeGain));
    }

    public static Blueprint get(String id) { return BY_ID.get(id); }
    public static List<Blueprint> all() { return List.copyOf(BY_ID.values()); }

    private BuildingBlueprintCatalog() {}
}
