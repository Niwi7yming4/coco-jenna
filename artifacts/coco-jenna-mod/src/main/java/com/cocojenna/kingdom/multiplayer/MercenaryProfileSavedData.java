package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 傭兵檔案（裝備快照 + 價格） */
public class MercenaryProfileSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_mercenary_profiles";
    private final Map<UUID, CompoundTag> profiles = new HashMap<>();

    public static MercenaryProfileSavedData get(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            throw new IllegalArgumentException("mercenary only cat kingdom");
        }
        return level.getDataStorage().computeIfAbsent(
                MercenaryProfileSavedData::load, MercenaryProfileSavedData::new, DATA_NAME);
    }

    public void saveProfile(ServerPlayer player, int price) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("price", price);
        tag.put("hand", player.getMainHandItem().save(new CompoundTag()));
        profiles.put(player.getUUID(), tag);
        ModCapabilities.getOrDefault(player).getMultiplayerSection().setMercenaryPrice(price);
        setDirty();
    }

    public CompoundTag getProfile(UUID owner) {
        return profiles.get(owner);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        profiles.forEach((uuid, prof) -> tag.put(uuid.toString(), prof));
        return tag;
    }

    public static MercenaryProfileSavedData load(CompoundTag tag) {
        MercenaryProfileSavedData data = new MercenaryProfileSavedData();
        for (String key : tag.getAllKeys()) {
            data.profiles.put(UUID.fromString(key), tag.getCompound(key));
        }
        return data;
    }
}
