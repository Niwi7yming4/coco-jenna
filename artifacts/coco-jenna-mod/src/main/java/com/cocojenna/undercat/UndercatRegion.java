package com.cocojenna.undercat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

/** 地下貓域主要區域與傳送座標. */
public enum UndercatRegion {
    CARDBOARD_SLUMS(0, 64, 0, 2),
    SMUGGLER_DOCK(400, 63, 0, 2),
    CATNIP_FARM(800, 64, 0, 2),
    SCRATCH_ARENA(0, 64, 400, 2),
    SILENT_LIBRARY(0, 64, -400, 2),
    SERVANT_CAMP(-250, 64, 200, 1);

    public final BlockPos center;
    public final int chunkRadius;

    UndercatRegion(int x, int y, int z, int chunkRadius) {
        this.center = new BlockPos(x, y, z);
        this.chunkRadius = chunkRadius;
    }

    public String buildKey() {
        return name();
    }

    public boolean overlaps(ChunkPos chunk) {
        ChunkPos centerChunk = new ChunkPos(center);
        return Math.abs(chunk.x - centerChunk.x) <= chunkRadius
                && Math.abs(chunk.z - centerChunk.z) <= chunkRadius;
    }
}
