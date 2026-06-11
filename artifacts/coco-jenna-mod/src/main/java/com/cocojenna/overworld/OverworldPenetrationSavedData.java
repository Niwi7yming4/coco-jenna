package com.cocojenna.overworld;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** 主世界滲透痕跡與灰鬚小屋位置（世界級 SavedData）. */
public class OverworldPenetrationSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_overworld_penetration";

    private final Long2ByteOpenHashMap traceByPos = new Long2ByteOpenHashMap();
    private final Long2ByteOpenHashMap ruinByPos = new Long2ByteOpenHashMap();
    private final Set<Long> seededChunks = new HashSet<>();
    private final Set<Long> ruinSeededChunks = new HashSet<>();
    private final Set<Long> leakCores = new HashSet<>();
    private final Long2ByteOpenHashMap fusionByPos = new Long2ByteOpenHashMap();
    private final Set<Long> resonanceSites = new HashSet<>();
    private final Set<UUID> gatheringBlessed = new HashSet<>();
    private BlockPos gatheringCenter = null;
    private long gatheringStartTick = 0L;
    private boolean hutPlaced;
    private boolean starterOutpostPlaced;
    private BlockPos hutCenter = BlockPos.ZERO;
    private BlockPos hutPortalFrame = BlockPos.ZERO;

    public static OverworldPenetrationSavedData get(ServerLevel level) {
        if (!level.dimension().equals(Level.OVERWORLD)) {
            throw new IllegalArgumentException("Overworld penetration data is overworld-only");
        }
        return level.getDataStorage().computeIfAbsent(
                OverworldPenetrationSavedData::load,
                OverworldPenetrationSavedData::new,
                DATA_NAME);
    }

    public OverworldPenetrationSavedData() {
        traceByPos.defaultReturnValue((byte) -1);
        ruinByPos.defaultReturnValue((byte) -1);
        fusionByPos.defaultReturnValue((byte) -1);
    }

    public boolean isChunkSeeded(long chunkKey) {
        return seededChunks.contains(chunkKey);
    }

    public void markChunkSeeded(long chunkKey) {
        seededChunks.add(chunkKey);
        setDirty();
    }

    public void putTrace(BlockPos pos, OverworldTraceType type) {
        traceByPos.put(pos.asLong(), (byte) type.ordinal());
        setDirty();
    }

    public OverworldTraceType getTrace(BlockPos pos) {
        byte v = traceByPos.get(pos.asLong());
        if (v < 0 || v >= OverworldTraceType.values().length) return null;
        return OverworldTraceType.values()[v];
    }

    public void removeTrace(BlockPos pos) {
        traceByPos.remove(pos.asLong());
        setDirty();
    }

    public boolean isHutPlaced() { return hutPlaced; }

    public BlockPos hutCenter() { return hutCenter; }

    public BlockPos hutPortalFrame() { return hutPortalFrame; }

    public void setHut(BlockPos center, BlockPos portalFrame) {
        hutPlaced = true;
        hutCenter = center;
        hutPortalFrame = portalFrame;
        setDirty();
    }

    public boolean isRuinChunkSeeded(long chunkKey) {
        return ruinSeededChunks.contains(chunkKey);
    }

    public void markRuinChunkSeeded(long chunkKey) {
        ruinSeededChunks.add(chunkKey);
        setDirty();
    }

    public void putRuin(BlockPos center, OverworldRuinType type) {
        ruinByPos.put(center.asLong(), (byte) type.ordinal());
        setDirty();
    }

    public OverworldRuinType getRuin(BlockPos pos) {
        byte v = ruinByPos.get(pos.asLong());
        if (v < 0 || v >= OverworldRuinType.values().length) return null;
        return OverworldRuinType.values()[v];
    }

    public OverworldRuinType findRuinNear(BlockPos pos, int radius) {
        for (var entry : ruinByPos.long2ByteEntrySet()) {
            BlockPos p = BlockPos.of(entry.getLongKey());
            if (p.distSqr(pos) <= radius * radius) {
                byte v = entry.getByteValue();
                if (v >= 0 && v < OverworldRuinType.values().length) {
                    return OverworldRuinType.values()[v];
                }
            }
        }
        return null;
    }

    /** 最近遺跡距離（格）；無遺跡時回傳 {@link Integer#MAX_VALUE}. */
    public int nearestRuinDistance(BlockPos pos) {
        int best = Integer.MAX_VALUE;
        for (long key : ruinByPos.keySet()) {
            int dist = (int) Math.sqrt(pos.distSqr(BlockPos.of(key)));
            if (dist < best) best = dist;
        }
        return best;
    }

    public boolean canPlaceRuin(BlockPos center, OverworldRuinType type) {
        int nearest = nearestRuinDistance(center);
        if (nearest == Integer.MAX_VALUE) return true;
        return nearest >= type.tier().minSpacing;
    }

    public boolean isStarterOutpostPlaced() { return starterOutpostPlaced; }

    public void setStarterOutpostPlaced(boolean v) {
        starterOutpostPlaced = v;
        setDirty();
    }

    public void markLeakCore(BlockPos pos) {
        leakCores.add(pos.asLong());
        setDirty();
    }

    public boolean isLeakCore(BlockPos pos) {
        return leakCores.contains(pos.asLong());
    }

    public void removeLeakCore(BlockPos pos) {
        leakCores.remove(pos.asLong());
        setDirty();
    }

    public void putFusionBuilding(BlockPos pos, FusionBuildingType type) {
        fusionByPos.put(pos.asLong(), (byte) type.ordinal());
        setDirty();
    }

    public BlockPos findFusionBuilding(FusionBuildingType type, BlockPos near, int radius) {
        for (var entry : fusionByPos.long2ByteEntrySet()) {
            if (entry.getByteValue() != type.ordinal()) continue;
            BlockPos p = BlockPos.of(entry.getLongKey());
            if (p.distSqr(near) <= (long) radius * radius) return p;
        }
        return null;
    }

    public void putResonanceSite(BlockPos pos) {
        resonanceSites.add(pos.asLong());
        setDirty();
    }

    public void removeResonanceSite(BlockPos pos) {
        resonanceSites.remove(pos.asLong());
        setDirty();
    }

    public BlockPos findResonanceNear(BlockPos near, int radius) {
        for (long key : resonanceSites) {
            BlockPos p = BlockPos.of(key);
            if (p.distSqr(near) <= (long) radius * radius) return p;
        }
        return null;
    }

    public boolean hasActiveGathering() {
        return gatheringCenter != null;
    }

    public BlockPos gatheringCenter() {
        return gatheringCenter;
    }

    public long gatheringStartTick() {
        return gatheringStartTick;
    }

    public void startGathering(BlockPos center, long gameTime) {
        gatheringCenter = center;
        gatheringStartTick = gameTime;
        gatheringBlessed.clear();
        setDirty();
    }

    public void endGathering() {
        gatheringCenter = null;
        gatheringBlessed.clear();
        setDirty();
    }

    public boolean isGatheringBlessed(UUID player) {
        return gatheringBlessed.contains(player);
    }

    public void markGatheringBlessed(UUID player) {
        gatheringBlessed.add(player);
        setDirty();
    }

    public static OverworldPenetrationSavedData load(CompoundTag tag) {
        OverworldPenetrationSavedData data = new OverworldPenetrationSavedData();
        data.hutPlaced = tag.getBoolean("hutPlaced");
        data.starterOutpostPlaced = tag.getBoolean("starterOutpost");
        if (tag.contains("hutCenter")) data.hutCenter = BlockPos.of(tag.getLong("hutCenter"));
        if (tag.contains("hutPortal")) data.hutPortalFrame = BlockPos.of(tag.getLong("hutPortal"));
        if (tag.contains("seededChunks")) {
            for (Tag t : tag.getList("seededChunks", Tag.TAG_LONG)) {
                data.seededChunks.add(((net.minecraft.nbt.LongTag) t).getAsLong());
            }
        }
        if (tag.contains("traces")) {
            for (Tag t : tag.getList("traces", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                data.traceByPos.put(c.getLong("pos"), c.getByte("type"));
            }
        }
        if (tag.contains("ruins")) {
            for (Tag t : tag.getList("ruins", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                data.ruinByPos.put(c.getLong("pos"), c.getByte("type"));
            }
        }
        if (tag.contains("ruinChunks")) {
            for (Tag t : tag.getList("ruinChunks", Tag.TAG_LONG)) {
                data.ruinSeededChunks.add(((net.minecraft.nbt.LongTag) t).getAsLong());
            }
        }
        if (tag.contains("leakCores")) {
            for (Tag t : tag.getList("leakCores", Tag.TAG_LONG)) {
                data.leakCores.add(((net.minecraft.nbt.LongTag) t).getAsLong());
            }
        }
        if (tag.contains("fusion")) {
            for (Tag t : tag.getList("fusion", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                data.fusionByPos.put(c.getLong("pos"), c.getByte("type"));
            }
        }
        if (tag.contains("resonance")) {
            for (Tag t : tag.getList("resonance", Tag.TAG_LONG)) {
                data.resonanceSites.add(((net.minecraft.nbt.LongTag) t).getAsLong());
            }
        }
        if (tag.contains("gatheringCenter")) {
            data.gatheringCenter = BlockPos.of(tag.getLong("gatheringCenter"));
            data.gatheringStartTick = tag.getLong("gatheringTick");
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("hutPlaced", hutPlaced);
        tag.putBoolean("starterOutpost", starterOutpostPlaced);
        tag.putLong("hutCenter", hutCenter.asLong());
        tag.putLong("hutPortal", hutPortalFrame.asLong());
        ListTag chunks = new ListTag();
        seededChunks.forEach(c -> chunks.add(net.minecraft.nbt.LongTag.valueOf(c)));
        tag.put("seededChunks", chunks);
        ListTag traces = new ListTag();
        traceByPos.long2ByteEntrySet().forEach(e -> {
            CompoundTag c = new CompoundTag();
            c.putLong("pos", e.getLongKey());
            c.putByte("type", e.getByteValue());
            traces.add(c);
        });
        tag.put("traces", traces);
        ListTag ruins = new ListTag();
        ruinByPos.long2ByteEntrySet().forEach(e -> {
            CompoundTag c = new CompoundTag();
            c.putLong("pos", e.getLongKey());
            c.putByte("type", e.getByteValue());
            ruins.add(c);
        });
        tag.put("ruins", ruins);
        ListTag ruinChunks = new ListTag();
        ruinSeededChunks.forEach(c -> ruinChunks.add(net.minecraft.nbt.LongTag.valueOf(c)));
        tag.put("ruinChunks", ruinChunks);
        ListTag leaks = new ListTag();
        leakCores.forEach(c -> leaks.add(net.minecraft.nbt.LongTag.valueOf(c)));
        tag.put("leakCores", leaks);
        ListTag fusion = new ListTag();
        fusionByPos.long2ByteEntrySet().forEach(e -> {
            CompoundTag c = new CompoundTag();
            c.putLong("pos", e.getLongKey());
            c.putByte("type", e.getByteValue());
            fusion.add(c);
        });
        tag.put("fusion", fusion);
        ListTag resonance = new ListTag();
        resonanceSites.forEach(c -> resonance.add(net.minecraft.nbt.LongTag.valueOf(c)));
        tag.put("resonance", resonance);
        if (gatheringCenter != null) {
            tag.putLong("gatheringCenter", gatheringCenter.asLong());
            tag.putLong("gatheringTick", gatheringStartTick);
        }
        return tag;
    }
}
