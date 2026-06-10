package com.cocojenna.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** 初啼村主支線進度（與教程 FirstCryQuestManager 分離）. */
public class FirstCryProgress extends SavedData {

    private static final String NAME = "cocojenna_first_cry_progress";

    private int callingStage;
    private int blackMudStage;
    private boolean sacredTreeBlessed;
    private boolean harborUnlocked;
    private boolean blackMudPurified;
    private int harborTravelMask;

    public static FirstCryProgress get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FirstCryProgress::load, FirstCryProgress::new, NAME);
    }

    private FirstCryProgress() {}

    private static FirstCryProgress load(CompoundTag tag) {
        FirstCryProgress p = new FirstCryProgress();
        p.callingStage = tag.getInt("callingStage");
        p.blackMudStage = tag.getInt("blackMudStage");
        p.sacredTreeBlessed = tag.getBoolean("sacredTreeBlessed");
        p.harborUnlocked = tag.getBoolean("harborUnlocked");
        p.blackMudPurified = tag.getBoolean("blackMudPurified");
        p.harborTravelMask = tag.getInt("harborTravelMask");
        return p;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("callingStage", callingStage);
        tag.putInt("blackMudStage", blackMudStage);
        tag.putBoolean("sacredTreeBlessed", sacredTreeBlessed);
        tag.putBoolean("harborUnlocked", harborUnlocked);
        tag.putBoolean("blackMudPurified", blackMudPurified);
        tag.putInt("harborTravelMask", harborTravelMask);
        return tag;
    }

    public int getCallingStage() { return callingStage; }
    public void setCallingStage(int stage) { callingStage = stage; setDirty(); }
    public int getBlackMudStage() { return blackMudStage; }
    public void setBlackMudStage(int stage) { blackMudStage = stage; setDirty(); }
    public boolean isSacredTreeBlessed() { return sacredTreeBlessed; }
    public void setSacredTreeBlessed(boolean v) { sacredTreeBlessed = v; setDirty(); }
    public boolean isHarborUnlocked() { return harborUnlocked; }
    public void setHarborUnlocked(boolean v) { harborUnlocked = v; setDirty(); }
    public boolean isBlackMudPurified() { return blackMudPurified; }
    public void setBlackMudPurified(boolean v) { blackMudPurified = v; setDirty(); }
    public int getHarborTravelMask() { return harborTravelMask; }
    public void unlockHarbor(int bit) { harborTravelMask |= (1 << bit); setDirty(); }
    public boolean isHarborBitUnlocked(int bit) { return (harborTravelMask & (1 << bit)) != 0; }
}
