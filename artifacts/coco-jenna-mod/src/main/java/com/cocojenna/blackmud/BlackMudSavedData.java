package com.cocojenna.blackmud;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

/** 區塊黑泥腐化階段 0–4（設計書 黑泥NPC區域）. */
public class BlackMudSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_black_mud_corruption";
    private final Long2ByteOpenHashMap stageByChunk = new Long2ByteOpenHashMap();
    private final Set<String> defeatedBosses = new HashSet<>();
    private final Set<Long> protectedChunks = new HashSet<>();
    private long lastSpreadTick = 0L;
    private boolean initialSeeded = false;
    private long blindWaterRainUntil = 0L;
    private boolean afterRain = false;
    private boolean spreadCycleActive = false;
    private int spreadBatchCursor = 0;

    public static BlackMudSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                BlackMudSavedData::load,
                BlackMudSavedData::new,
                DATA_NAME);
    }

    public BlackMudSavedData() {
        stageByChunk.defaultReturnValue((byte) 0);
    }

    public int getStage(ChunkPos pos) {
        return stageByChunk.get(pos.toLong()) & 0xFF;
    }

    public void setStage(ChunkPos pos, int stage) {
        int clamped = Math.max(0, Math.min(4, stage));
        long key = pos.toLong();
        if (clamped == 0) {
            stageByChunk.remove(key);
        } else {
            stageByChunk.put(key, (byte) clamped);
        }
        setDirty();
    }

    public Long2ByteOpenHashMap stages() {
        return stageByChunk;
    }

    public long[] stageKeysSnapshot() {
        return stageByChunk.keySet().toLongArray();
    }

    public int stageCount() {
        return stageByChunk.size();
    }

    public boolean isSpreadCycleActive() { return spreadCycleActive; }

    public void beginSpreadCycle() {
        spreadCycleActive = true;
        spreadBatchCursor = 0;
        setDirty();
    }

    public void endSpreadCycle() {
        spreadCycleActive = false;
        spreadBatchCursor = 0;
        setDirty();
    }

    public int spreadBatchCursor() { return spreadBatchCursor; }

    public void setSpreadBatchCursor(int cursor) {
        spreadBatchCursor = cursor;
        setDirty();
    }

    public long lastSpreadTick() {
        return lastSpreadTick;
    }

    public void setLastSpreadTick(long tick) {
        lastSpreadTick = tick;
        setDirty();
    }

    public void markBossDefeated(String bossId) {
        defeatedBosses.add(bossId);
        setDirty();
    }

    public boolean isBossDefeated(String bossId) {
        return defeatedBosses.contains(bossId);
    }

    public void protectRadius(BlockPos center, int radius) {
        int chunkRadius = (radius + 15) / 16;
        ChunkPos cp = new ChunkPos(center);
        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                protectedChunks.add(new ChunkPos(cp.x + dx, cp.z + dz).toLong());
            }
        }
        setDirty();
    }

    public boolean isProtected(ChunkPos pos) {
        return protectedChunks.contains(pos.toLong());
    }

    public boolean isInitialSeeded() {
        return initialSeeded;
    }

    public void setInitialSeeded(boolean seeded) {
        initialSeeded = seeded;
        setDirty();
    }

    public long blindWaterRainUntil() {
        return blindWaterRainUntil;
    }

    public void setBlindWaterRainUntil(long tick) {
        blindWaterRainUntil = tick;
        setDirty();
    }

    public boolean isBlindWaterRainActive(long now) {
        return blindWaterRainUntil > 0 && now < blindWaterRainUntil;
    }

    public boolean isAfterRain() {
        return afterRain;
    }

    public void setAfterRain(boolean value) {
        afterRain = value;
        setDirty();
    }

    public void clearAllCorruption() {
        stageByChunk.clear();
        blindWaterRainUntil = 0L;
        spreadCycleActive = false;
        spreadBatchCursor = 0;
        setDirty();
    }

    public static BlackMudSavedData load(CompoundTag tag) {
        BlackMudSavedData data = new BlackMudSavedData();
        data.lastSpreadTick = tag.getLong("lastSpread");
        data.initialSeeded = tag.getBoolean("initialSeeded");
        data.blindWaterRainUntil = tag.getLong("blindWaterRainUntil");
        data.afterRain = tag.getBoolean("afterRain");
        data.spreadCycleActive = tag.getBoolean("spreadCycleActive");
        data.spreadBatchCursor = tag.getInt("spreadBatchCursor");
        if (tag.contains("defeatedBosses")) {
            for (Tag t : tag.getList("defeatedBosses", Tag.TAG_STRING)) {
                data.defeatedBosses.add(t.getAsString());
            }
        }
        if (tag.contains("protected")) {
            for (Tag t : tag.getList("protected", Tag.TAG_LONG)) {
                data.protectedChunks.add(((net.minecraft.nbt.LongTag) t).getAsLong());
            }
        }
        ListTag list = tag.getList("chunks", Tag.TAG_COMPOUND);
        for (Tag entry : list) {
            CompoundTag c = (CompoundTag) entry;
            data.stageByChunk.put(c.getLong("pos"), c.getByte("stage"));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLong("lastSpread", lastSpreadTick);
        tag.putBoolean("initialSeeded", initialSeeded);
        tag.putLong("blindWaterRainUntil", blindWaterRainUntil);
        tag.putBoolean("afterRain", afterRain);
        tag.putBoolean("spreadCycleActive", spreadCycleActive);
        tag.putInt("spreadBatchCursor", spreadBatchCursor);
        ListTag bosses = new ListTag();
        defeatedBosses.forEach(id -> bosses.add(net.minecraft.nbt.StringTag.valueOf(id)));
        tag.put("defeatedBosses", bosses);
        ListTag prot = new ListTag();
        protectedChunks.forEach(pos -> prot.add(net.minecraft.nbt.LongTag.valueOf(pos)));
        tag.put("protected", prot);
        ListTag list = new ListTag();
        stageByChunk.long2ByteEntrySet().forEach(entry -> {
            CompoundTag c = new CompoundTag();
            c.putLong("pos", entry.getLongKey());
            c.putByte("stage", entry.getByteValue());
            list.add(c);
        });
        tag.put("chunks", list);
        return tag;
    }
}
