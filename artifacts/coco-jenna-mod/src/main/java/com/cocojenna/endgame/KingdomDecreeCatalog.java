package com.cocojenna.endgame;

import net.minecraft.network.chat.Component;

import java.util.*;

/** 王國法令目錄 — 經濟／文化／社會／特別 共 20 條. */
public final class KingdomDecreeCatalog {

    public enum Category { ECONOMIC, CULTURAL, SOCIAL, SPECIAL }

    public record DecreeDef(String id, Category category, int shardCost, int repCost,
                            long durationTicks, Set<String> conflicts, boolean throneRequired) {
        public Component name() {
            return Component.translatable("decree.cocojenna." + id + ".name");
        }
        public Component desc() {
            return Component.translatable("decree.cocojenna." + id + ".desc");
        }
    }

    private static final Map<String, DecreeDef> BY_ID = new LinkedHashMap<>();

    static {
        reg("peace", Category.SOCIAL, 0, 0, 168000, Set.of(), false);
        reg("harvest_boost", Category.ECONOMIC, 3, 5, 168000, Set.of("famine_relief"), false);
        reg("trade_fair", Category.ECONOMIC, 5, 8, 168000, Set.of(), false);
        reg("festival_night", Category.CULTURAL, 4, 6, 168000, Set.of("curfew"), false);
        reg("curfew", Category.SOCIAL, 2, 4, 168000, Set.of("festival_night"), false);
        reg("garden_bloom", Category.CULTURAL, 3, 5, 168000, Set.of(), false);
        reg("scholar_hour", Category.CULTURAL, 4, 7, 168000, Set.of(), false);
        reg("furball_play", Category.SOCIAL, 2, 3, 168000, Set.of(), false);
        reg("builder_rush", Category.ECONOMIC, 6, 10, 168000, Set.of("rest_day"), false);
        reg("rest_day", Category.SOCIAL, 1, 2, 168000, Set.of("builder_rush"), false);
        reg("memory_archive", Category.CULTURAL, 5, 8, 168000, Set.of(), false);
        reg("chef_praise", Category.CULTURAL, 4, 6, 168000, Set.of(), false);
        reg("star_gazing", Category.CULTURAL, 3, 5, 168000, Set.of(), false);
        reg("open_market", Category.ECONOMIC, 5, 9, 168000, Set.of("isolation"), false);
        reg("immigration_decree", Category.SOCIAL, 10, 20, 168000, Set.of(), false);
        reg("music_decree", Category.CULTURAL, 2, 5, 168000, Set.of(), false);
        reg("isolation", Category.SOCIAL, 4, 12, 168000, Set.of("open_market", "festival_night"), false);
        reg("famine_relief", Category.ECONOMIC, 6, 6, 168000, Set.of("harvest_boost"), false);
        reg("royal_parade", Category.SPECIAL, 8, 15, 168000, Set.of(), true);
        reg("twin_blessing", Category.SPECIAL, 10, 20, 168000, Set.of(), true);
        reg("eternal_spring", Category.SPECIAL, 12, 25, 168000, Set.of("curfew"), true);
        reg("picture_week", Category.CULTURAL, 3, 4, 168000, Set.of(), false);
    }

    private static void reg(String id, Category cat, int shards, int rep, long dur,
                            Set<String> conflicts, boolean throne) {
        BY_ID.put(id, new DecreeDef(id, cat, shards, rep, dur, conflicts, throne));
    }

    public static DecreeDef get(String id) {
        return BY_ID.getOrDefault(id, BY_ID.get("peace"));
    }

    public static List<DecreeDef> all() {
        return List.copyOf(BY_ID.values());
    }

    private KingdomDecreeCatalog() {}
}
