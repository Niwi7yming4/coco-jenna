package com.cocojenna.overworld;

/** 主世界文化融合建築（設計書 §7.3）. */
public enum FusionBuildingType {
    EMBASSY(1),
    TWIN_STATUE(2),
    CATNIP_EXCHANGE(4);

    public final int bit;

    FusionBuildingType(int bit) {
        this.bit = bit;
    }
}
