package com.cocojenna.exploration;

/** 五層探索密度間距（設計書 貓之國再深化 §2）. */
public final class ExplorationTierConfig {

    public enum Tier {
        MICRO(48, 80),
        SMALL(120, 200),
        MEDIUM(280, 400),
        LARGE(600, 900),
        MEGA(1200, 1800);

        public final int minSpacing;
        public final int maxSpacing;

        Tier(int minSpacing, int maxSpacing) {
            this.minSpacing = minSpacing;
            this.maxSpacing = maxSpacing;
        }
    }

    private ExplorationTierConfig() {}

    public static Tier tierForRuinPriority(int priority) {
        if (priority <= 1) return Tier.MEGA;
        if (priority <= 3) return Tier.LARGE;
        if (priority <= 6) return Tier.MEDIUM;
        if (priority <= 12) return Tier.SMALL;
        return Tier.MICRO;
    }

    public static int spacingBlocks(Tier tier, long seed) {
        int range = tier.maxSpacing - tier.minSpacing;
        return tier.minSpacing + (int) (Math.abs(seed) % Math.max(1, range));
    }
}
