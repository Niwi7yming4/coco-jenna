package com.cocojenna.endgame;

import com.cocojenna.init.ModDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 貓之國維度世界級進度（終局、初代守護者）— 不依賴單一玩家 BondData.
 */
public class CatKingdomWorldData extends SavedData {

    private static final String DATA_NAME = "cocojenna_cat_kingdom_world";

    private boolean afterRain;
    private boolean kingdomUnlocked;
    @Nullable
    private UUID firstGuardianUuid;
    private int firstDawnVotes;

    public static CatKingdomWorldData get(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            throw new IllegalArgumentException("CatKingdomWorldData only for cat kingdom dimension");
        }
        return level.getDataStorage().computeIfAbsent(
                CatKingdomWorldData::load,
                CatKingdomWorldData::new,
                DATA_NAME);
    }

    public static CatKingdomWorldData getIfPresent(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return null;
        return level.getDataStorage().get(CatKingdomWorldData::load, DATA_NAME);
    }

    public boolean isAfterRain() { return afterRain; }
    public boolean isKingdomUnlocked() { return kingdomUnlocked; }
    @Nullable public UUID getFirstGuardianUuid() { return firstGuardianUuid; }

    /** 第一位達成「第一次黎明」的玩家成為初代守護者，為全伺服器解鎖終局. */
    public void registerFirstDawnCandidate(ServerPlayer player) {
        if (afterRain) return;
        if (firstGuardianUuid == null) {
            firstGuardianUuid = player.getUUID();
            kingdomUnlocked = true;
            afterRain = true;
            setDirty();
            return;
        }
        firstDawnVotes++;
        setDirty();
    }

    public void setAfterRain(boolean value) {
        afterRain = value;
        if (value) kingdomUnlocked = true;
        setDirty();
    }

    public static CatKingdomWorldData load(CompoundTag tag) {
        CatKingdomWorldData data = new CatKingdomWorldData();
        data.afterRain = tag.getBoolean("afterRain");
        data.kingdomUnlocked = tag.getBoolean("kingdomUnlocked");
        if (tag.hasUUID("firstGuardian")) {
            data.firstGuardianUuid = tag.getUUID("firstGuardian");
        }
        data.firstDawnVotes = tag.getInt("firstDawnVotes");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("afterRain", afterRain);
        tag.putBoolean("kingdomUnlocked", kingdomUnlocked);
        if (firstGuardianUuid != null) {
            tag.putUUID("firstGuardian", firstGuardianUuid);
        }
        tag.putInt("firstDawnVotes", firstDawnVotes);
        return tag;
    }
}
