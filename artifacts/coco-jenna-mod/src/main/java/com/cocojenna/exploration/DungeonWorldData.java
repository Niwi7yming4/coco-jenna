package com.cocojenna.exploration;

import com.cocojenna.world.DungeonGenerators;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;

/** 地牢世界狀態 — Boss 擊敗／不再重生（設計書 3.2）. */
public class DungeonWorldData extends SavedData {

    private static final String DATA_NAME = "cocojenna_dungeon_world";
    private static final int DUNGEON_COUNT = 10;

    private int bossesDefeated = 0;
    private final long[] bossAnchors = new long[DUNGEON_COUNT];

    public static DungeonWorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                DungeonWorldData::load,
                DungeonWorldData::new,
                DATA_NAME);
    }

    public DungeonWorldData() {}

    public void setBossAnchor(int dungeonIndex, BlockPos pos) {
        if (dungeonIndex >= 0 && dungeonIndex < DUNGEON_COUNT) {
            bossAnchors[dungeonIndex] = pos.asLong();
            setDirty();
        }
    }

    public BlockPos getBossAnchor(int dungeonIndex) {
        if (dungeonIndex < 0 || dungeonIndex >= DUNGEON_COUNT) return BlockPos.ZERO;
        return BlockPos.of(bossAnchors[dungeonIndex]);
    }

    public boolean isBossDefeated(int dungeonIndex) {
        if (dungeonIndex < 0 || dungeonIndex >= DUNGEON_COUNT) return false;
        return (bossesDefeated & (1 << dungeonIndex)) != 0;
    }

    public void markBossDefeated(ServerLevel level, int dungeonIndex) {
        if (dungeonIndex < 0 || dungeonIndex >= DUNGEON_COUNT) return;
        bossesDefeated |= (1 << dungeonIndex);
        despawnBossInRoom(level, dungeonIndex);
        setDirty();
    }

    public void markBossDefeated(ServerLevel level, String dungeonId) {
        markBossDefeated(level, DungeonGenerators.indexOf(dungeonId));
    }

    public void despawnBossInRoom(ServerLevel level, int dungeonIndex) {
        BlockPos anchor = getBossAnchor(dungeonIndex);
        if (anchor.equals(BlockPos.ZERO)) return;
        AABB box = new AABB(anchor).inflate(8, 4, 8);
        for (Mob mob : level.getEntitiesOfClass(Mob.class, box)) {
            if (!mob.isRemoved()) {
                mob.discard();
            }
        }
    }

    public static void syncBossStateForPlayer(ServerLevel level, net.minecraft.server.level.ServerPlayer player) {
        var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(player);
        DungeonWorldData data = get(level);
        for (int i = 0; i < DUNGEON_COUNT; i++) {
            String id = DungeonGenerators.idAt(i);
            int flag = DungeonRegistry.flag(id);
            if (flag != 0 && bond.hasDungeonCleared(flag) && !data.isBossDefeated(i)) {
                data.markBossDefeated(level, i);
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("bossesDefeated", bossesDefeated);
        long[] anchors = new long[DUNGEON_COUNT];
        System.arraycopy(bossAnchors, 0, anchors, 0, DUNGEON_COUNT);
        tag.putLongArray("bossAnchors", anchors);
        return tag;
    }

    public static DungeonWorldData load(CompoundTag tag) {
        DungeonWorldData data = new DungeonWorldData();
        data.bossesDefeated = tag.getInt("bossesDefeated");
        long[] anchors = tag.getLongArray("bossAnchors");
        for (int i = 0; i < Math.min(anchors.length, DUNGEON_COUNT); i++) {
            data.bossAnchors[i] = anchors[i];
        }
        return data;
    }
}
