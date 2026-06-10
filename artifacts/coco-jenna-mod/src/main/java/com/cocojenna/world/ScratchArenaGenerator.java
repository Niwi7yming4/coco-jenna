package com.cocojenna.world;

import com.cocojenna.entity.UndercatHubNpcEntity;
import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/** 貓咪決鬥場 POI. */
public final class ScratchArenaGenerator {

    private ScratchArenaGenerator() {}

    public static void build(ServerLevel level, BlockPos center) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        for (int dx = -16; dx <= 16; dx++) {
            for (int dz = -16; dz <= 16; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= 16) {
                    level.setBlock(new BlockPos(cx + dx, cy - 1, cz + dz), Blocks.SAND.defaultBlockState(), 2);
                }
                if (dist >= 14 && dist <= 16) {
                    for (int y = 0; y <= 3; y++) {
                        level.setBlock(new BlockPos(cx + dx, cy + y, cz + dz),
                                ModBlocks.REINFORCED_CARDBOARD.get().defaultBlockState(), 2);
                    }
                }
            }
        }
        for (int i = -6; i <= 6; i += 3) {
            level.setBlock(new BlockPos(cx + i, cy + 4, cz - 14),
                    ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState(), 2);
        }
        CardboardSlumsGenerator.spawnNpc(level, UndercatHubNpcEntity.Role.SCARFACE, cx, cy + 1, cz - 10);
        level.setBlock(new BlockPos(cx, cy + 1, cz), ModBlocks.UNDERCAT_WAYSTONE.get().defaultBlockState(), 2);
    }
}
