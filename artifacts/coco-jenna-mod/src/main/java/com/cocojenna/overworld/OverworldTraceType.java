package com.cocojenna.overworld;

/** 主世界滲透痕跡類型. */
public enum OverworldTraceType {
    MOON_PAW(40),
    BLACK_MUD_RESIDUE(25),
    FORGOTTEN_TOY(15),
    CAT_GRAFFITI(12),
    MOONSTONE_SHARD(8);

    private final int weight;

    OverworldTraceType(int weight) { this.weight = weight; }

    public int weight() { return weight; }

    public static OverworldTraceType roll(net.minecraft.util.RandomSource random) {
        int total = 0;
        for (OverworldTraceType t : values()) total += t.weight;
        int roll = random.nextInt(total);
        for (OverworldTraceType t : values()) {
            roll -= t.weight;
            if (roll < 0) return t;
        }
        return MOON_PAW;
    }
}
