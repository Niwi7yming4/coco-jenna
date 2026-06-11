package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.init.ModDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class KingdomLeaderboardSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_kingdom_leaderboard";

    public enum Board { ARENA, CATNIP, MEMORY, PURIFY, SOLO_WOLF }

    private final Map<Board, Map<UUID, Integer>> scores = new EnumMap<>(Board.class);

    public KingdomLeaderboardSavedData() {
        for (Board b : Board.values()) scores.put(b, new HashMap<>());
    }

    public static KingdomLeaderboardSavedData get(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            throw new IllegalArgumentException("leaderboard only cat kingdom");
        }
        return level.getDataStorage().computeIfAbsent(
                KingdomLeaderboardSavedData::load, KingdomLeaderboardSavedData::new, DATA_NAME);
    }

    public void addScore(Board board, UUID player, int delta) {
        scores.get(board).merge(player, delta, Integer::sum);
        setDirty();
    }

    public List<Map.Entry<UUID, Integer>> top(Board board, int n) {
        return scores.get(board).entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(n).toList();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        for (Board b : Board.values()) {
            ListTag list = new ListTag();
            scores.get(b).forEach((uuid, score) -> {
                CompoundTag c = new CompoundTag();
                c.putUUID("id", uuid);
                c.putInt("score", score);
                list.add(c);
            });
            tag.put(b.name(), list);
        }
        return tag;
    }

    public static KingdomLeaderboardSavedData load(CompoundTag tag) {
        KingdomLeaderboardSavedData data = new KingdomLeaderboardSavedData();
        for (Board b : Board.values()) {
            if (!tag.contains(b.name())) continue;
            for (Tag t : tag.getList(b.name(), Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                data.scores.get(b).put(c.getUUID("id"), c.getInt("score"));
            }
        }
        return data;
    }
}
