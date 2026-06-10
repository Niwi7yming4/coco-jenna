package com.cocojenna.memforge;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class DaikatanaRitualSavedData extends SavedData {

    private static final String ID = "cocojenna_daikatana_rituals";
    private final Map<BlockPos, DaikatanaRitual> rituals = new HashMap<>();

    public static DaikatanaRitualSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                DaikatanaRitualSavedData::load,
                DaikatanaRitualSavedData::new,
                ID);
    }

    public Map<BlockPos, DaikatanaRitual> rituals() { return rituals; }

    public void add(DaikatanaRitual ritual) {
        rituals.put(ritual.altarPos(), ritual);
        setDirty();
    }

    public void remove(BlockPos pos) {
        rituals.remove(pos);
        setDirty();
    }

    @Nullable
    public DaikatanaRitual get(BlockPos pos) {
        return rituals.get(pos);
    }

    public static DaikatanaRitualSavedData load(CompoundTag tag) {
        DaikatanaRitualSavedData data = new DaikatanaRitualSavedData();
        ListTag list = tag.getList("rituals", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            DaikatanaRitual ritual = DaikatanaRitual.load((CompoundTag) t);
            data.rituals.put(ritual.altarPos(), ritual);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (DaikatanaRitual r : rituals.values()) {
            list.add(r.save());
        }
        tag.put("rituals", list);
        return tag;
    }
}
