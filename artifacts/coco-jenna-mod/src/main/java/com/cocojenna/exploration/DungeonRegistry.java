package com.cocojenna.exploration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** 區域地牢配置（設計書 3.2）. */
public final class DungeonRegistry {

    public record DungeonDef(String id, String region, String rewardKey) {}

    private static final Map<String, DungeonDef> DUNGEONS = new LinkedHashMap<>();

    static {
        reg("elder_cellar", "first_cry", "stardust_cloth");
        reg("weaver_workshop", "velvet_forest", "workshop_cloak");
        reg("moon_well", "moon_alley", "moonlight_ripple");
        reg("rust_sewer", "gear_town", "gear_schedule");
        reg("forgotten_confessional", "sleep_sanctuary", "silent_whisper");
        reg("saltwind_wreck", "blind_port", "abyss_anchor");
        reg("watchtower_vault", "dawn_highlands", "royal_glory");
        reg("wind_cavern", "howling_gorge", "storm_cloak");
        reg("ashura_trial", "phantom_maze", "sequence_badge");
        reg("tower_prison", "forgotten_tower", "redeem_velvet");
        reg("stardust_tomb", "stardust_desert", "stardust_step");
        reg("forgotten_vault", "forgotten_wastes", "dark_tide");
        reg("cardboard_depth", "cardboard_slums", "cheshire_grin");
        reg("moonlight_grotto", "moonlight_beach", "moonlight_clear");
        reg("catnip_mine", "catnip_highlands", "catnip_item");
    }

    private DungeonRegistry() {}

    private static void reg(String id, String region, String reward) {
        DUNGEONS.put(id, new DungeonDef(id, region, reward));
    }

    public static Optional<DungeonDef> get(String id) {
        return Optional.ofNullable(DUNGEONS.get(id));
    }

    public static int flag(String id) {
        int i = 0;
        for (String key : DUNGEONS.keySet()) {
            if (key.equals(id)) return 1 << i;
            i++;
        }
        return 0;
    }
}
