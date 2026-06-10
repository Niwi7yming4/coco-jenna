package com.cocojenna.gamble;

import com.cocojenna.init.ModDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 黑傑克賭局戰績與天氣賭注狀態. */
public class BlackjackGambleSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_blackjack";
    private final Map<UUID, Integer> totalWins = new HashMap<>();
    private long fateBlessingUntil;
    private long polarNightUntil;

    public static BlackjackGambleSavedData get(ServerLevel level) {
        ServerLevel store = level;
        if (!store.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            ServerLevel ck = level.getServer().getLevel(ModDimensions.CAT_KINGDOM);
            if (ck != null) store = ck;
        }
        return store.getDataStorage().computeIfAbsent(
                BlackjackGambleSavedData::load,
                BlackjackGambleSavedData::new,
                DATA_NAME);
    }

    public int wins(UUID player) {
        return totalWins.getOrDefault(player, 0);
    }

    public int addWin(UUID player) {
        int next = wins(player) + 1;
        totalWins.put(player, next);
        setDirty();
        return next;
    }

    public long fateBlessingUntil() { return fateBlessingUntil; }
    public long polarNightUntil() { return polarNightUntil; }

    public void setFateBlessingUntil(long tick) {
        fateBlessingUntil = tick;
        setDirty();
    }

    public void setPolarNightUntil(long tick) {
        polarNightUntil = tick;
        setDirty();
    }

    public boolean isFateBlessingActive(long now) {
        return fateBlessingUntil > now;
    }

    public boolean isPolarNightActive(long now) {
        return polarNightUntil > now;
    }

    public static BlackjackGambleSavedData load(CompoundTag tag) {
        BlackjackGambleSavedData data = new BlackjackGambleSavedData();
        data.fateBlessingUntil = tag.getLong("fateBlessingUntil");
        data.polarNightUntil = tag.getLong("polarNightUntil");
        ListTag wins = tag.getList("wins", Tag.TAG_COMPOUND);
        for (Tag entry : wins) {
            CompoundTag c = (CompoundTag) entry;
            data.totalWins.put(c.getUUID("player"), c.getInt("count"));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLong("fateBlessingUntil", fateBlessingUntil);
        tag.putLong("polarNightUntil", polarNightUntil);
        ListTag wins = new ListTag();
        totalWins.forEach((uuid, count) -> {
            CompoundTag c = new CompoundTag();
            c.putUUID("player", uuid);
            c.putInt("count", count);
            wins.add(c);
        });
        tag.put("wins", wins);
        return tag;
    }
}
