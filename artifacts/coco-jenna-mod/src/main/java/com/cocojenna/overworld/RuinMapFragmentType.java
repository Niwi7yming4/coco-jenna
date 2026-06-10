package com.cocojenna.overworld;

import net.minecraft.util.RandomSource;

/** 六種主世界遺跡地圖碎片（設計書 主世界再多點 §5.1）. */
public enum RuinMapFragmentType {
    WAR_RUIN(OverworldRuinType.WAR_RUIN),
    FORGOTTEN_ALTAR(OverworldRuinType.FORGOTTEN_ALTAR),
    MUD_FARM(OverworldRuinType.MUD_FARM),
    SMUGGLER_OUTPOST(OverworldRuinType.SMUGGLER_OUTPOST),
    POLLUTED_TEMPLE(OverworldRuinType.POLLUTED_TEMPLE),
    MOON_SEAL(OverworldRuinType.MOON_SEAL);

    public final OverworldRuinType ruin;

    RuinMapFragmentType(OverworldRuinType ruin) {
        this.ruin = ruin;
    }

    public int bit() {
        return 1 << ordinal();
    }

    public static RuinMapFragmentType forRuin(OverworldRuinType type) {
        for (RuinMapFragmentType t : values()) {
            if (t.ruin == type) return t;
        }
        return WAR_RUIN;
    }

    public static RuinMapFragmentType random(RandomSource random) {
        return values()[random.nextInt(values().length)];
    }
}
