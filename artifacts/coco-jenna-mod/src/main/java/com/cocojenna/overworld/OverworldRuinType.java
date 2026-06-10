package com.cocojenna.overworld;

import net.minecraft.util.RandomSource;

/** 主世界貓之國遺跡類型（設計書 主世界再多點 §2）. */
public enum OverworldRuinType {
    WAR_RUIN(28),
    FORGOTTEN_ALTAR(16),
    MUD_FARM(20),
    SMUGGLER_OUTPOST(16),
    CAT_BAR(12),
    POLLUTED_TEMPLE(8),
    /** 極稀有；僅透過 {@link OverworldRuinGenerator} 獨立機率生成. */
    MOON_SEAL(0);

    private final int weight;

    OverworldRuinType(int weight) {
        this.weight = weight;
    }

    public static OverworldRuinType roll(RandomSource random) {
        int total = 0;
        for (OverworldRuinType t : values()) {
            if (t.weight > 0) total += t.weight;
        }
        int roll = random.nextInt(total);
        for (OverworldRuinType t : values()) {
            if (t.weight <= 0) continue;
            roll -= t.weight;
            if (roll < 0) return t;
        }
        return WAR_RUIN;
    }
}
