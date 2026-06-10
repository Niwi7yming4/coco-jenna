package com.cocojenna.world;

import com.cocojenna.entity.UndercatHubNpcEntity;
import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/** 貓奴營地 POI. */
public final class ServantCampGenerator {

    private ServantCampGenerator() {}

    public static void build(ServerLevel level, BlockPos center) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                level.setBlock(new BlockPos(cx + dx, cy - 1, cz + dz), Blocks.STONE_BRICKS.defaultBlockState(), 2);
            }
        }
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (Math.abs(dx) == 5 || Math.abs(dz) == 4) {
                    level.setBlock(new BlockPos(cx + dx, cy, cz + dz), Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 2);
                    level.setBlock(new BlockPos(cx + dx, cy + 1, cz + dz), Blocks.STONE_BRICKS.defaultBlockState(), 2);
                }
            }
        }
        CardboardSlumsGenerator.spawnNpc(level, UndercatHubNpcEntity.Role.HEAD_SERVANT, cx, cy + 1, cz);
        level.setBlock(new BlockPos(cx + 6, cy + 1, cz), ModBlocks.UNDERCAT_WAYSTONE.get().defaultBlockState(), 2);
    }
}
