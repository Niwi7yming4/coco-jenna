package com.cocojenna.memforge;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryForgeSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_memory_forge_rituals";
    private final Map<BlockPos, MemoryForgeRitual> rituals = new HashMap<>();

    public static MemoryForgeSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                MemoryForgeSavedData::load,
                MemoryForgeSavedData::new,
                DATA_NAME);
    }

    public MemoryForgeSavedData() {}

    public Map<BlockPos, MemoryForgeRitual> rituals() { return rituals; }

    public void add(MemoryForgeRitual ritual) {
        rituals.put(ritual.altarPos(), ritual);
        setDirty();
    }

    public void remove(BlockPos altar) {
        rituals.remove(altar);
        setDirty();
    }

    public MemoryForgeRitual get(BlockPos altar) {
        return rituals.get(altar);
    }

    public static MemoryForgeSavedData load(CompoundTag tag) {
        MemoryForgeSavedData data = new MemoryForgeSavedData();
        ListTag list = tag.getList("rituals", Tag.TAG_COMPOUND);
        for (Tag entry : list) {
            CompoundTag c = (CompoundTag) entry;
            BlockPos altar = BlockPos.of(c.getLong("altar"));
            BlockPos core = BlockPos.of(c.getLong("core"));
            UUID player = c.getUUID("player");
            MemoryForgeRecipe recipe = MemoryForgeRecipe.valueOf(c.getString("recipe"));
            long end = c.getLong("phaseEnd");
            MemoryForgeRitual ritual = new MemoryForgeRitual(altar, core, player, recipe, 0);
            ritual.setPhase(MemoryForgeRitual.Phase.valueOf(c.getString("phase")), end);
            if (c.getBoolean("catalyst")) ritual.markCatalystInjected();
            if (c.getBoolean("bonus")) ritual.markBonusApplied();
            if (c.getBoolean("penalty")) ritual.markPenaltyApplied();
            if (c.contains("blockHp")) {
                CompoundTag hpTag = c.getCompound("blockHp");
                for (String key : hpTag.getAllKeys()) {
                    ritual.blockHp().put(Long.parseLong(key), hpTag.getFloat(key));
                }
            }
            data.rituals.put(altar, ritual);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (MemoryForgeRitual r : rituals.values()) {
            CompoundTag c = new CompoundTag();
            c.putLong("altar", r.altarPos().asLong());
            c.putLong("core", r.corePos().asLong());
            c.putUUID("player", r.playerId());
            c.putString("recipe", r.recipe().name());
            c.putString("phase", r.phase().name());
            c.putLong("phaseEnd", r.phaseEndTick());
            c.putBoolean("catalyst", r.catalystInjected());
            c.putBoolean("bonus", r.bonusApplied());
            c.putBoolean("penalty", r.penaltyApplied());
            CompoundTag hpTag = new CompoundTag();
            r.blockHp().forEach((pos, hp) -> hpTag.putFloat(Long.toString(pos), hp));
            c.put("blockHp", hpTag);
            list.add(c);
        }
        tag.put("rituals", list);
        return tag;
    }
}
