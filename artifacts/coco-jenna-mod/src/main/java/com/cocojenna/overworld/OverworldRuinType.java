package com.cocojenna.overworld;

import net.minecraft.util.RandomSource;

/** 主世界貓之國遺跡類型（設計書 主世界再多點 §2）. */
public enum OverworldRuinType {
    WAR_RUIN(28, RuinTier.SMALL),
    FORGOTTEN_ALTAR(16, RuinTier.SMALL),
    MUD_FARM(20, RuinTier.MEDIUM),
    SMUGGLER_OUTPOST(16, RuinTier.MEDIUM),
    CAT_BAR(12, RuinTier.MEDIUM),
    POLLUTED_TEMPLE(8, RuinTier.LARGE),
    /** 極稀有；僅透過 {@link OverworldRuinGenerator} 獨立機率生成. */
    MOON_SEAL(0, RuinTier.LARGE);

    public enum RuinTier {
        SMALL(200, 400),
        MEDIUM(800, 1200),
        LARGE(2000, 3000);

        public final int minSpacing;
        public final int maxSpacing;

        RuinTier(int minSpacing, int maxSpacing) {
            this.minSpacing = minSpacing;
            this.maxSpacing = maxSpacing;
        }
    }

    private final int weight;
    private final RuinTier tier;

    OverworldRuinType(int weight, RuinTier tier) {
        this.weight = weight;
        this.tier = tier;
    }

    public RuinTier tier() {
        return tier;
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
