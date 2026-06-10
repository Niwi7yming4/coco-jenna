package com.cocojenna.world.qin;

/** 皇陵六種風格. */
public enum MausoleumType {

    PAPER_HAREM(0, "paper_harem"),
    TERRACOTTA(1, "terracotta"),
    LIBRARY(2, "library"),
    TEA_GARDEN(3, "tea_garden"),
    OBSERVATORY(4, "observatory"),
    SLEEPING_CHAMBER(5, "sleeping_chamber");

    private final int bit;
    private final String id;

    MausoleumType(int bit, String id) {
        this.bit = bit;
        this.id = id;
    }

    public int bit() { return bit; }

    public String id() { return id; }

    public static MausoleumType roll(net.minecraft.util.RandomSource random, boolean allowSleeping) {
        MausoleumType[] pool = allowSleeping ? values() :
                new MausoleumType[]{PAPER_HAREM, TERRACOTTA, LIBRARY, TEA_GARDEN, OBSERVATORY};
        return pool[random.nextInt(pool.length)];
    }
}
