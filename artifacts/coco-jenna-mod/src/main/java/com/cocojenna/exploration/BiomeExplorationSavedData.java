package com.cocojenna.exploration;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class BiomeExplorationSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_biome_exploration";
    private final Set<String> seededBiomes = new HashSet<>();

    public static BiomeExplorationSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                BiomeExplorationSavedData::load, BiomeExplorationSavedData::new, DATA_NAME);
    }

    private BiomeExplorationSavedData() {}

    private static BiomeExplorationSavedData load(CompoundTag tag) {
        BiomeExplorationSavedData data = new BiomeExplorationSavedData();
        if (tag.contains("seeded")) {
            for (var t : tag.getList("seeded", 8)) {
                data.seededBiomes.add(t.getAsString());
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (String s : seededBiomes) {
            list.add(StringTag.valueOf(s));
        }
        tag.put("seeded", list);
        return tag;
    }

    public boolean isSeeded(String biomeKey) {
        return seededBiomes.contains(biomeKey);
    }

    public void markSeeded(String biomeKey) {
        if (seededBiomes.add(biomeKey)) {
            setDirty();
        }
    }
}
