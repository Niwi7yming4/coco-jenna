package com.cocojenna.blackmud;

import com.cocojenna.init.ModDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** NPC 腐蝕階段 1–4（設計書 8.3）. */
public class NpcCorrosionSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_npc_corrosion";
    private final Map<UUID, Integer> stageByEntity = new HashMap<>();

    public static NpcCorrosionSavedData get(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            ServerLevel ck = level.getServer().getLevel(ModDimensions.CAT_KINGDOM);
            if (ck != null) level = ck;
        }
        return level.getDataStorage().computeIfAbsent(
                NpcCorrosionSavedData::load,
                NpcCorrosionSavedData::new,
                DATA_NAME);
    }

    public int getStage(UUID entityId) {
        return stageByEntity.getOrDefault(entityId, 0);
    }

    public void setStage(UUID entityId, int stage) {
        int clamped = Math.max(0, Math.min(4, stage));
        if (clamped == 0) {
            stageByEntity.remove(entityId);
        } else {
            stageByEntity.put(entityId, clamped);
        }
        setDirty();
    }

    public static NpcCorrosionSavedData load(CompoundTag tag) {
        NpcCorrosionSavedData data = new NpcCorrosionSavedData();
        ListTag list = tag.getList("entities", Tag.TAG_COMPOUND);
        for (Tag entry : list) {
            CompoundTag c = (CompoundTag) entry;
            data.stageByEntity.put(c.getUUID("id"), c.getInt("stage"));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        stageByEntity.forEach((id, stage) -> {
            CompoundTag c = new CompoundTag();
            c.putUUID("id", id);
            c.putInt("stage", stage);
            list.add(c);
        });
        tag.put("entities", list);
        return tag;
    }
}
