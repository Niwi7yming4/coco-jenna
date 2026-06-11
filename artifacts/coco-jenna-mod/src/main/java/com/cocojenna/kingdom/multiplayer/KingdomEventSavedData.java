package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.init.ModDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** 全服事件 + 共享祭典時鐘 */
public class KingdomEventSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_kingdom_events";

    private long nextPurgeEventDay = 30;
    private int purgeContribution;
    private boolean purgeActive;
    private long purgeEndDay;
    private int worldFestivalPhase;
    private long worldFestivalDay;
    private boolean purgeFailedShopClosed;
    private int purgeGoal = 5000;

    public static KingdomEventSavedData get(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            throw new IllegalArgumentException("events only cat kingdom");
        }
        return level.getDataStorage().computeIfAbsent(
                KingdomEventSavedData::load, KingdomEventSavedData::new, DATA_NAME);
    }

    public long getNextPurgeEventDay() { return nextPurgeEventDay; }
    public int getPurgeContribution() { return purgeContribution; }
    public boolean isPurgeActive() { return purgeActive; }
    public int getWorldFestivalPhase() { return worldFestivalPhase; }
    public boolean isPurgeFailedShopClosed() { return purgeFailedShopClosed; }

    public void addPurgeContribution(int n) {
        if (!purgeActive) return;
        purgeContribution += n;
        setDirty();
    }

    /** 單人伺服器降低全服淨化門檻（設計書 Phase 6 單人友善）. */
    public static int purgeContributionGoal() {
        return 5000;
    }

    public int purgeContributionGoalForLevel(ServerLevel level) {
        int online = level.getServer().getPlayerCount();
        return online <= 1 ? 2500 : purgeContributionGoal();
    }

    public void tickDay(ServerLevel level, long day) {
        if (!purgeActive && day >= nextPurgeEventDay) {
            purgeActive = true;
            purgeContribution = 0;
            purgeEndDay = day + 3;
            purgeGoal = purgeContributionGoalForLevel(level);
        }
        if (purgeActive && day >= purgeEndDay) {
            if (purgeContribution >= purgeGoal) {
                purgeActive = false;
                nextPurgeEventDay = day + 30;
                purgeFailedShopClosed = false;
            } else {
                purgeActive = false;
                purgeFailedShopClosed = true;
                nextPurgeEventDay = day + 30;
            }
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLong("nextPurge", nextPurgeEventDay);
        tag.putInt("purgeContrib", purgeContribution);
        tag.putBoolean("purgeActive", purgeActive);
        tag.putLong("purgeEnd", purgeEndDay);
        tag.putInt("festPhase", worldFestivalPhase);
        tag.putLong("festDay", worldFestivalDay);
        tag.putBoolean("shopClosed", purgeFailedShopClosed);
        tag.putInt("purgeGoal", purgeGoal);
        return tag;
    }

    public static KingdomEventSavedData load(CompoundTag tag) {
        KingdomEventSavedData data = new KingdomEventSavedData();
        data.nextPurgeEventDay = tag.getLong("nextPurge");
        data.purgeContribution = tag.getInt("purgeContrib");
        data.purgeActive = tag.getBoolean("purgeActive");
        data.purgeEndDay = tag.getLong("purgeEnd");
        data.worldFestivalPhase = tag.getInt("festPhase");
        data.worldFestivalDay = tag.getLong("festDay");
        data.purgeFailedShopClosed = tag.getBoolean("shopClosed");
        data.purgeGoal = tag.contains("purgeGoal") ? tag.getInt("purgeGoal") : purgeContributionGoal();
        return data;
    }
}
