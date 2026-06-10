package com.cocojenna.undercat;

import com.cocojenna.capability.BondData;
import net.minecraft.world.item.ItemStack;
/** 主世界／貓之國進入地下貓域的入口類型（批次 B）. */
public enum UndercatEntrance {
    TREE_HOLE(UndercatRegion.CARDBOARD_SLUMS, 0, 0, 0, 0, null, "undercat_entrance_tree"),
    BLIND_PORT_RIFT(UndercatRegion.SMUGGLER_DOCK, 1, 0, 0, 15, null, "undercat_entrance_blind"),
    GEAR_MINE_SHAFT(UndercatRegion.SCRATCH_ARENA, 2, 0, 0, 0, "gear_town", "undercat_entrance_gear"),
    LIGHTHOUSE_WELL(UndercatRegion.SILENT_LIBRARY, 3, 0, 0, 0, null, "undercat_entrance_lighthouse"),
    SANCTUARY_POOL(UndercatRegion.SERVANT_CAMP, 4, 0, 0, 0, null, "undercat_entrance_sanctuary");

    public final UndercatRegion destination;
    public final int minChapter;
    public final int minCocoEmotion;
    public final int minJennaEmotion;
    public final int minBlindPortRep;
    public final String minKingdomRepKey;
    public final String firstDialogueId;

    UndercatEntrance(UndercatRegion destination, int minChapter, int minCoco, int minJenna,
            int minBlindRep, String kingdomRepKey, String firstDialogueId) {
        this.destination = destination;
        this.minChapter = minChapter;
        this.minCocoEmotion = minCoco;
        this.minJennaEmotion = minJenna;
        this.minBlindPortRep = minBlindRep;
        this.minKingdomRepKey = kingdomRepKey;
        this.firstDialogueId = firstDialogueId;
    }

    public int flag() {
        return 1 << ordinal();
    }

    public boolean canUse(BondData bond) {
        if (bond.getUndercatChapter() < minChapter) return false;
        if (this == TREE_HOLE) {
            return bond.getCocoEmotion() >= 40 || bond.getJennaEmotion() >= 40;
        }
        if (bond.getCocoEmotion() < minCocoEmotion || bond.getJennaEmotion() < minJennaEmotion) {
            return false;
        }
        if (minBlindPortRep > 0 && bond.getReputation("blind_port") < minBlindPortRep) return false;
        if (minKingdomRepKey != null && bond.getReputation(minKingdomRepKey) < 20) return false;
        if (this == LIGHTHOUSE_WELL && bond.getMemoryShardsTotal() < 3 && bond.getUndercatChapter() < 4) {
            return false;
        }
        if (this == SANCTUARY_POOL && bond.getUndercatChapter() < 1) return false;
        return true;
    }

    public boolean needsHolyWater(BondData bond, ItemStack held) {
        return this == SANCTUARY_POOL
                && bond.getUndercatChapter() < 4
                && !held.is(com.cocojenna.init.ModItems.HOLY_WATER.get());
    }
}
