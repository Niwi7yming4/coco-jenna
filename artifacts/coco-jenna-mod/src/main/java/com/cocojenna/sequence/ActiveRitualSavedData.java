package com.cocojenna.sequence;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.*;

/** 進行中的晉升儀式協作狀態 */
public class ActiveRitualSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_active_ritual";

    public static class RitualState {
        public UUID promoter;
        public int blessCount;
        public int resonanceSlots;
        public int guardianKills;
        public final Set<UUID> helpers = new HashSet<>();
    }

    private RitualState active;

    public static ActiveRitualSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ActiveRitualSavedData::load, ActiveRitualSavedData::new, DATA_NAME);
    }

    @Nullable
    public RitualState getActive() { return active; }

    public RitualState start(UUID promoter) {
        active = new RitualState();
        active.promoter = promoter;
        setDirty();
        return active;
    }

    public void clear() {
        active = null;
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        if (active == null) return tag;
        tag.putUUID("promoter", active.promoter);
        tag.putInt("bless", active.blessCount);
        tag.putInt("resonance", active.resonanceSlots);
        tag.putInt("guardKills", active.guardianKills);
        ListTag helpers = new ListTag();
        active.helpers.forEach(u -> helpers.add(net.minecraft.nbt.StringTag.valueOf(u.toString())));
        tag.put("helpers", helpers);
        return tag;
    }

    public static ActiveRitualSavedData load(CompoundTag tag) {
        ActiveRitualSavedData data = new ActiveRitualSavedData();
        if (tag.isEmpty() || !tag.hasUUID("promoter")) return data;
        data.active = new RitualState();
        data.active.promoter = tag.getUUID("promoter");
        data.active.blessCount = tag.getInt("bless");
        data.active.resonanceSlots = tag.getInt("resonance");
        data.active.guardianKills = tag.getInt("guardKills");
        if (tag.contains("helpers")) {
            for (Tag t : tag.getList("helpers", Tag.TAG_STRING)) {
                data.active.helpers.add(UUID.fromString(t.getAsString()));
            }
        }
        return data;
    }
}
