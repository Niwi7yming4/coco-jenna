package com.cocojenna.world;

import com.cocojenna.entity.UndercatHubNpcEntity;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;

/** 走私貓薄荷農場 POI. */
public final class CatnipFarmGenerator {

    private CatnipFarmGenerator() {}

    public static void build(ServerLevel level, BlockPos center) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        CardboardSlumsGenerator.fillPad(level, cx, cy, cz, 20, Blocks.DIRT);
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                level.setBlock(new BlockPos(cx + dx, cy + 1, cz + dz), Blocks.GRASS.defaultBlockState(), 2);
            }
        }
        level.setBlock(new BlockPos(cx, cy + 8, cz), Blocks.GLOWSTONE.defaultBlockState(), 2);
        for (int i = -2; i <= 2; i++) {
            level.setBlock(new BlockPos(cx + i, cy + 1, cz + 10),
                    ModBlocks.STARLIGHT_MARBLE.get().defaultBlockState(), 2);
        }
        CardboardSlumsGenerator.spawnNpc(level, UndercatHubNpcEntity.Role.GREENPAW, cx - 4, cy + 1, cz - 6);
        var dragon = ModEntities.CATNIP_DRAGON.get().create(level);
        if (dragon != null) {
            dragon.setPos(cx + 6, cy + 1, cz + 4);
            dragon.finalizeSpawn(level, level.getCurrentDifficultyAt(center), MobSpawnType.STRUCTURE, null, null);
            level.addFreshEntity(dragon);
        }
        level.setBlock(new BlockPos(cx - 8, cy + 1, cz), ModBlocks.UNDERCAT_WAYSTONE.get().defaultBlockState(), 2);
    }
}
