package com.cocojenna.world;

import com.cocojenna.entity.UndercatHubNpcEntity;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;

/** 沉默圖書館 POI. */
public final class SilentLibraryGenerator {

    private SilentLibraryGenerator() {}

    public static void build(ServerLevel level, BlockPos center) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        for (int dx = -12; dx <= 12; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                for (int y = 0; y <= 5; y++) {
                    boolean wall = Math.abs(dx) == 12 || Math.abs(dz) == 8 || y == 0 || y == 5;
                    if (wall) {
                        level.setBlock(new BlockPos(cx + dx, cy + y, cz + dz),
                                ModBlocks.STARLIGHT_MARBLE.get().defaultBlockState(), 2);
                    } else if (y == 1 && Math.abs(dx) % 4 == 0) {
                        level.setBlock(new BlockPos(cx + dx, cy + y, cz + dz - 6),
                                Blocks.BOOKSHELF.defaultBlockState(), 2);
                    }
                }
            }
        }
        CardboardSlumsGenerator.spawnNpc(level, UndercatHubNpcEntity.Role.ABBESS, cx, cy + 1, cz - 4);
        var silenced = ModEntities.SILENCED_ONE.get().create(level);
        if (silenced != null) {
            silenced.setPos(cx, cy + 1, cz + 10);
            silenced.finalizeSpawn(level, level.getCurrentDifficultyAt(center), MobSpawnType.STRUCTURE, null, null);
            level.addFreshEntity(silenced);
        }
        level.setBlock(new BlockPos(cx, cy + 1, cz - 6), ModBlocks.UNDERCAT_WAYSTONE.get().defaultBlockState(), 2);
    }
}
