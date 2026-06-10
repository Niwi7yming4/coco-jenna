package com.cocojenna.guardian;

import com.cocojenna.init.ModDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.UUID;

/** 守護者世界級離線追蹤（支援完全離線 7 日認領）. */
public class GuardianWorldData extends SavedData {

    private static final String DATA_NAME = "cocojenna_guardian";
    @Nullable
    private UUID guardianUuid;
    private long guardianLastLogoutTick;
    @Nullable
    private UUID pendingStripGuardian;

    public static GuardianWorldData get(MinecraftServer server) {
        ServerLevel level = server.getLevel(ModDimensions.CAT_KINGDOM);
        if (level == null) {
            level = server.overworld();
        }
        return level.getDataStorage().computeIfAbsent(
                GuardianWorldData::load,
                GuardianWorldData::new,
                DATA_NAME);
    }

    public GuardianWorldData() {}

    public boolean hasGuardian() {
        return guardianUuid != null;
    }

    @Nullable
    public UUID guardianUuid() {
        return guardianUuid;
    }

    public long guardianLastLogoutTick() {
        return guardianLastLogoutTick;
    }

    @Nullable
    public UUID pendingStripGuardian() {
        return pendingStripGuardian;
    }

    public void setGuardian(UUID uuid, long logoutTick) {
        guardianUuid = uuid;
        guardianLastLogoutTick = logoutTick;
        setDirty();
    }

    public void clearGuardian() {
        guardianUuid = null;
        setDirty();
    }

    public void setPendingStrip(UUID uuid) {
        pendingStripGuardian = uuid;
        setDirty();
    }

    public void clearPendingStrip() {
        pendingStripGuardian = null;
        setDirty();
    }

    public static GuardianWorldData load(CompoundTag tag) {
        GuardianWorldData data = new GuardianWorldData();
        if (tag.hasUUID("guardian")) {
            data.guardianUuid = tag.getUUID("guardian");
        }
        data.guardianLastLogoutTick = tag.getLong("guardianLastLogout");
        if (tag.hasUUID("pendingStrip")) {
            data.pendingStripGuardian = tag.getUUID("pendingStrip");
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        if (guardianUuid != null) {
            tag.putUUID("guardian", guardianUuid);
        }
        tag.putLong("guardianLastLogout", guardianLastLogoutTick);
        if (pendingStripGuardian != null) {
            tag.putUUID("pendingStrip", pendingStripGuardian);
        }
        return tag;
    }
}
