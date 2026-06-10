package com.cocojenna.world.ruin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** 追蹤已放置遺跡與皇陵種子 chunk. */
public class RuinMatrixSavedData extends SavedData {

    private static final String NAME = "cocojenna_ruin_matrix";

    public record PlacedRuin(BlockPos origin, String ruinId, String variant) {}

    private final Map<Long, PlacedRuin> placedRuinMeta = new HashMap<>();
    private final java.util.Set<Long> seededChunks = new java.util.HashSet<>();
    private boolean sleepingMausoleumPlaced;
    private int nbtFallbackCount;

    public static RuinMatrixSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                RuinMatrixSavedData::load, RuinMatrixSavedData::new, NAME);
    }

    private RuinMatrixSavedData() {}

    private static RuinMatrixSavedData load(CompoundTag tag) {
        RuinMatrixSavedData data = new RuinMatrixSavedData();
        if (tag.contains("seededChunks")) {
            for (Tag t : tag.getList("seededChunks", Tag.TAG_LONG)) {
                data.seededChunks.add(((net.minecraft.nbt.LongTag) t).getAsLong());
            }
        }
        if (tag.contains("placedRuins")) {
            for (Tag t : tag.getList("placedRuins", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                BlockPos origin = BlockPos.of(c.getLong("origin"));
                data.placedRuinMeta.put(origin.asLong(), new PlacedRuin(
                        origin,
                        c.getString("ruinId"),
                        c.getString("variant")));
            }
        }
        data.sleepingMausoleumPlaced = tag.getBoolean("sleepingMausoleum");
        data.nbtFallbackCount = tag.getInt("nbtFallbackCount");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag chunks = new ListTag();
        seededChunks.forEach(c -> chunks.add(net.minecraft.nbt.LongTag.valueOf(c)));
        tag.put("seededChunks", chunks);
        ListTag ruins = new ListTag();
        for (PlacedRuin r : placedRuinMeta.values()) {
            CompoundTag c = new CompoundTag();
            c.putLong("origin", r.origin().asLong());
            c.putString("ruinId", r.ruinId());
            c.putString("variant", r.variant());
            ruins.add(c);
        }
        tag.put("placedRuins", ruins);
        tag.putBoolean("sleepingMausoleum", sleepingMausoleumPlaced);
        tag.putInt("nbtFallbackCount", nbtFallbackCount);
        return tag;
    }

    public boolean isChunkSeeded(long chunkKey) {
        return seededChunks.contains(chunkKey);
    }

    public void markChunkSeeded(long chunkKey) {
        seededChunks.add(chunkKey);
        setDirty();
    }

    public boolean isRuinAt(BlockPos pos) {
        return placedRuinMeta.containsKey(pos.asLong());
    }

    public void registerPlacedRuin(BlockPos origin, String ruinId, String variant) {
        placedRuinMeta.put(origin.asLong(), new PlacedRuin(origin, ruinId, variant));
        setDirty();
    }

    public Optional<PlacedRuin> ruinAt(BlockPos center, int radius) {
        PlacedRuin best = null;
        double bestDist = radius * radius + 1;
        for (PlacedRuin r : placedRuinMeta.values()) {
            double d = r.origin().distSqr(center);
            if (d <= radius * radius && d < bestDist) {
                best = r;
                bestDist = d;
            }
        }
        return Optional.ofNullable(best);
    }

    /** 最近已登記遺跡原點（供頁爪指引）. */
    public BlockPos nearestRuinTo(BlockPos center, int maxRadius) {
        BlockPos best = null;
        double bestDist = maxRadius * (double) maxRadius + 1;
        for (PlacedRuin r : placedRuinMeta.values()) {
            if ("mausoleum".equals(r.ruinId())) continue;
            double d = r.origin().distSqr(center);
            if (d < bestDist) {
                best = r.origin();
                bestDist = d;
            }
        }
        return best;
    }

    public void recordNbtFallback() {
        nbtFallbackCount++;
        setDirty();
    }

    public int nbtFallbackCount() {
        return nbtFallbackCount;
    }

    public boolean isSleepingMausoleumPlaced() {
        return sleepingMausoleumPlaced;
    }

    public void setSleepingMausoleumPlaced() {
        sleepingMausoleumPlaced = true;
        setDirty();
    }
}
