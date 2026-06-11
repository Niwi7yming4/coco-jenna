package com.cocojenna.exploration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** 區域傳說條目登錄（設計書 2.1）. */
public final class LoreRegistry {

    private static final List<LoreEntry> ENTRIES = new ArrayList<>();

    static {
        add(0, "velvet_dynasty", LoreEntry.LoreCarrier.TABLET, "velvet_forest", 0);
        add(1, "moon_first_king", LoreEntry.LoreCarrier.MURAL, "moon_alley", 0);
        add(2, "gear_steam_age", LoreEntry.LoreCarrier.TABLET, "gear_town", 0);
        add(3, "sanctuary_whisper", LoreEntry.LoreCarrier.MURAL, "sleep_sanctuary", 0);
        add(4, "blind_merchant_legend", LoreEntry.LoreCarrier.TABLET, "blind_port", 0);
        add(5, "dawn_watchtower", LoreEntry.LoreCarrier.MURAL, "dawn_highlands", 0);
        add(6, "gorge_storm_bell", LoreEntry.LoreCarrier.TABLET, "howling_gorge", 0);
        add(7, "maze_ashura_trial", LoreEntry.LoreCarrier.MURAL, "phantom_maze", 0);
        add(8, "tower_prisoners", LoreEntry.LoreCarrier.TABLET, "forgotten_tower", 0);
        add(9, "first_cry_elder", LoreEntry.LoreCarrier.TABLET, "first_cry", 0);
        add(10, "ironpaw_youth", LoreEntry.LoreCarrier.MEMORY_STONE, "central_plaza", 1);
        add(11, "black_mud_omen", LoreEntry.LoreCarrier.TABLET, "velvet_forest", 1);
        add(12, "saltwind_captain", LoreEntry.LoreCarrier.MURAL, "blind_port", 2);
        add(13, "royal_glory_past", LoreEntry.LoreCarrier.RELIC, "dawn_highlands", 2);
        add(14, "catnip_festival", LoreEntry.LoreCarrier.MURAL, "first_cry", 1);
        add(15, "map_ruins_hint", LoreEntry.LoreCarrier.MAP_PAGE, "central_plaza", 2);
        add(16, "rainbow_canyon", LoreEntry.LoreCarrier.TABLET, "rainbow_canyon", 1);
        add(17, "catnip_highlands", LoreEntry.LoreCarrier.TABLET, "catnip_highlands", 2);
        add(18, "cardboard_slums", LoreEntry.LoreCarrier.MURAL, "cardboard_slums", 2);
        add(19, "moonlight_beach", LoreEntry.LoreCarrier.TABLET, "moonlight_beach", 1);
        add(20, "stardust_desert", LoreEntry.LoreCarrier.TABLET, "stardust_desert", 3);
        add(21, "forgotten_wastes_depth", LoreEntry.LoreCarrier.MURAL, "forgotten_wastes", 3);
        add(22, "first_cry_story_north", LoreEntry.LoreCarrier.TABLET, "first_cry", 1);
        add(23, "first_cry_story_east", LoreEntry.LoreCarrier.TABLET, "first_cry", 2);
        add(24, "first_cry_story_south", LoreEntry.LoreCarrier.TABLET, "first_cry", 2);
        add(25, "first_cry_story_west", LoreEntry.LoreCarrier.TABLET, "first_cry", 3);
        add(26, "first_cry_canopy_secret", LoreEntry.LoreCarrier.TABLET, "first_cry", 3);
    }

    private LoreRegistry() {}

    private static void add(int id, String key, LoreEntry.LoreCarrier type, String region, int shardCost) {
        ENTRIES.add(new LoreEntry(id, key, type, region, shardCost));
    }

    public static List<LoreEntry> all() {
        return Collections.unmodifiableList(ENTRIES);
    }

    public static Optional<LoreEntry> byId(int id) {
        if (id < 0 || id >= ENTRIES.size()) return Optional.empty();
        return Optional.of(ENTRIES.get(id));
    }

    public static int regionEntryCount(String region) {
        int n = 0;
        for (LoreEntry e : ENTRIES) {
            if (e.region().equals(region)) n++;
        }
        return n;
    }

    public static int regionFlag(String region) {
        return switch (region) {
            case "first_cry" -> 1;
            case "velvet_forest" -> 2;
            case "moon_alley" -> 4;
            case "central_plaza" -> 8;
            case "gear_town" -> 16;
            case "sleep_sanctuary" -> 32;
            case "blind_port" -> 64;
            case "dawn_highlands" -> 128;
            case "howling_gorge" -> 256;
            case "phantom_maze" -> 512;
            case "forgotten_tower" -> 1024;
            case "rainbow_canyon" -> 2048;
            case "catnip_highlands" -> 4096;
            case "cardboard_slums" -> 8192;
            case "moonlight_beach" -> 16384;
            case "stardust_desert" -> 32768;
            case "forgotten_wastes" -> 65536;
            default -> 0;
        };
    }

    /** 區域傳說是否已全部解鎖. */
    public static boolean isRegionComplete(com.cocojenna.capability.BondData bond, String region) {
        for (LoreEntry e : ENTRIES) {
            if (e.region().equals(region) && !bond.hasLore(e.id())) return false;
        }
        return regionEntryCount(region) > 0;
    }
}
