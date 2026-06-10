package com.cocojenna.world.ruin;

import net.minecraft.util.RandomSource;

/** 遺跡九變體（設計書附錄 A）. */
public enum RuinVariant {
    DEFAULT,
    OVERGROWN,
    BURIED,
    VELVET,
    MOONSTONE,
    BLACK_MUD,
    CARDBOARD,
    CRYSTAL,
    RUINED;

    public static RuinVariant roll(RandomSource random, boolean catKingdom) {
        RuinVariant[] pool = catKingdom
                ? new RuinVariant[]{DEFAULT, VELVET, MOONSTONE, CRYSTAL, OVERGROWN}
                : new RuinVariant[]{DEFAULT, OVERGROWN, BURIED, BLACK_MUD, RUINED};
        return pool[random.nextInt(pool.length)];
    }
}
