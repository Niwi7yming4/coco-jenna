package com.cocojenna.blackmud;

import com.cocojenna.init.ModDimensions;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;

/** NPC 腐蝕階段 1–4（設計書 8.3）. */
public class NpcCorrosionSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_npc_corrosion";
    private final Object2IntOpenHashMap<UUID> stageByEntity = new Object2IntOpenHashMap<>();

    public NpcCorrosionSavedData() {
        stageByEntity.defaultReturnValue(0);
    }

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
        return stageByEntity.getInt(entityId);
    }

    public void setStage(UUID entityId, int stage) {
        int clamped = Math.max(0, Math.min(4, stage));
        if (clamped == 0) {
            stageByEntity.removeInt(entityId);
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
        for (var e : stageByEntity.object2IntEntrySet()) {
            CompoundTag c = new CompoundTag();
            c.putUUID("id", e.getKey());
            c.putInt("stage", e.getIntValue());
            list.add(c);
        }
        tag.put("entities", list);
        return tag;
    }
}
