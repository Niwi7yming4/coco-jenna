package com.cocojenna.exploration;

/** 傳說載體條目（設計書第二章）. */
public record LoreEntry(
        int id,
        String key,
        LoreCarrier type,
        String region,
        int shardCost
) {
    public enum LoreCarrier { TABLET, MURAL, MEMORY_STONE, RELIC, MAP_PAGE }
}
