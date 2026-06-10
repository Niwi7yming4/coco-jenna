package com.cocojenna.world;

import com.cocojenna.capability.BondData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** 貓之國全域建設進度 — 多人共享（紀念碑 + 藍圖）. */
public class KingdomBuildSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_kingdom_build";
    private int monumentShardRecord = 0;
    private int monumentHeightBuilt = 0;
    private boolean primalCoreAnchored = false;
    private final Map<String, Integer> buildingProgress = new HashMap<>();
    private final Set<String> buildingsPlaced = new HashSet<>();

    public static KingdomBuildSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                KingdomBuildSavedData::load, KingdomBuildSavedData::new, DATA_NAME);
    }

    public KingdomBuildSavedData() {}

    public int monumentShardRecord() { return monumentShardRecord; }
    public int monumentHeightBuilt() { return monumentHeightBuilt; }

    public int getBuildingProgress(String id) {
        return buildingProgress.getOrDefault(id, 0);
    }

    public void setBuildingProgress(String id, int prog) {
        buildingProgress.put(id, Math.max(0, prog));
        setDirty();
    }

    public boolean isBuildingPlaced(String id) {
        return buildingsPlaced.contains(id);
    }

    public void markBuildingPlaced(String id) {
        buildingsPlaced.add(id);
        setDirty();
    }

    public void updateMonumentShards(int shards) {
        if (shards > monumentShardRecord) {
            monumentShardRecord = shards;
            setDirty();
        }
    }

    public void setMonumentHeightBuilt(int h) {
        monumentHeightBuilt = h;
        setDirty();
    }

    public boolean isPrimalCoreAnchored() { return primalCoreAnchored; }

    public void setPrimalCoreAnchored(boolean v) {
        primalCoreAnchored = v;
        setDirty();
    }

    /** 將世界共享進度寫入玩家 BondData（供 GUI 顯示）. */
    public void syncToBond(BondData bond) {
        for (var e : buildingProgress.entrySet()) {
            bond.setBuildingProgress(e.getKey(), e.getValue());
        }
        for (String id : buildingsPlaced) {
            bond.setBuildingPlaced(id);
        }
    }

    /** 從較高的進度合併（多人貢獻取 max）. */
    public void mergeProgress(String id, int prog) {
        int next = Math.max(getBuildingProgress(id), prog);
        if (next != getBuildingProgress(id)) {
            setBuildingProgress(id, next);
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("monumentShards", monumentShardRecord);
        tag.putInt("monumentHeight", monumentHeightBuilt);
        tag.putBoolean("primalCoreAnchored", primalCoreAnchored);
        ListTag builds = new ListTag();
        for (var e : buildingProgress.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putString("id", e.getKey());
            c.putInt("prog", e.getValue());
            builds.add(c);
        }
        tag.put("buildingProgress", builds);
        ListTag placed = new ListTag();
        for (String id : buildingsPlaced) {
            placed.add(StringTag.valueOf(id));
        }
        tag.put("buildingsPlaced", placed);
        return tag;
    }

    public static KingdomBuildSavedData load(CompoundTag tag) {
        KingdomBuildSavedData d = new KingdomBuildSavedData();
        d.monumentShardRecord = tag.getInt("monumentShards");
        d.monumentHeightBuilt = tag.getInt("monumentHeight");
        d.primalCoreAnchored = tag.getBoolean("primalCoreAnchored");
        if (tag.contains("buildingProgress")) {
            for (Tag t : tag.getList("buildingProgress", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                d.buildingProgress.put(c.getString("id"), c.getInt("prog"));
            }
        }
        if (tag.contains("buildingsPlaced")) {
            for (Tag t : tag.getList("buildingsPlaced", Tag.TAG_STRING)) {
                d.buildingsPlaced.add(t.getAsString());
            }
        }
        return d;
    }
}
