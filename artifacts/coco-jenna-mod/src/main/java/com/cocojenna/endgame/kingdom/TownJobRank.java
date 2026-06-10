package com.cocojenna.endgame.kingdom;

import com.cocojenna.capability.BondData;
import net.minecraft.network.chat.Component;

/** 第十章：NPC 職階. */
public enum TownJobRank {
    SCRAPPER("拾荒者", 10, 0, 0, 0),
    CRAFTSMAN("工匠貓", 8, 50, 0, 0),
    GARDENER("園丁貓", 6, 0, 1, 0),
    FISHER("漁夫貓", 5, 0, 2, 0),
    CHEF("廚師貓", 4, 0, 3, 1),
    ARCHITECT("建築師貓", 3, 0, 4, 10),
    SCHOLAR("學者貓", 3, 0, 5, 0),
    PERFORMER("藝人貓", 4, 0, 6, 0),
    SHADOW_GUARD("暗影衛", 4, 0, 7, 80),
    MERCHANT("商貓", 3, 0, 8, 0);

    public final String zh;
    public final int cap;
    /** 齒輪鎮聲望需求（0=無）. */
    public final int gearRepReq;
    /** 建築解鎖旗標（0=無, 1=廚房, 2=moon alley, ...）. */
    public final int buildingFlag;
    /** 王城舊部聲望需求. */
    public final int royalRepReq;

    TownJobRank(String zh, int cap, int gearRepReq, int buildingFlag, int royalRepReq) {
        this.zh = zh;
        this.cap = cap;
        this.gearRepReq = gearRepReq;
        this.buildingFlag = buildingFlag;
        this.royalRepReq = royalRepReq;
    }

    public Component displayName() {
        return Component.translatable("kingdom.cocojenna.job." + name().toLowerCase());
    }

    public boolean meetsRequirements(BondData bond) {
        if (gearRepReq > 0 && bond.getReputation("gear_town") < gearRepReq) return false;
        if (royalRepReq > 0 && bond.getReputation("royal") < royalRepReq) return false;
        return switch (buildingFlag) {
            case 1 -> bond.isBuildingPlaced("cat_kitchen") || bond.getBuildingProgress("cat_kitchen") > 0;
            case 2 -> bond.hasPeaceScene("afterrain_moon_alley");
            case 3 -> bond.isBuildingPlaced("cat_kitchen");
            case 4 -> bond.getPlacedBuildingCount() >= 10;
            case 5 -> bond.isBuildingPlaced("cat_library");
            case 6 -> bond.isBuildingPlaced("open_air_theater");
            case 7 -> bond.getReputation("royal") >= 80;
            case 8 -> bond.isBuildingPlaced("market_square");
            default -> true;
        };
    }
}
