package com.cocojenna.world;

import com.cocojenna.entity.UndercatHubNpcEntity;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;

/** 走私碼頭 POI. */
public final class SmugglerDockGenerator {

    private SmugglerDockGenerator() {}

    public static void build(ServerLevel level, BlockPos center) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        CardboardSlumsGenerator.fillPad(level, cx, cy, cz, 22, Blocks.OAK_PLANKS);
        for (int dx = -18; dx <= 18; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                level.setBlock(new BlockPos(cx + dx, cy - 1, cz + dz), Blocks.WATER.defaultBlockState(), 2);
            }
        }
        for (int i = -3; i <= 3; i++) {
            level.setBlock(new BlockPos(cx + i * 4, cy, cz - 8),
                    Blocks.OAK_SLAB.defaultBlockState(), 2);
        }
        for (int i = 0; i < 6; i++) {
            level.setBlock(new BlockPos(cx - 15 + i * 5, cy + 1, cz - 12),
                    ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState(), 2);
        }
        level.setBlock(new BlockPos(cx, cy + 1, cz - 10), Blocks.CHEST.defaultBlockState(), 2);
        CardboardSlumsGenerator.spawnNpc(level, UndercatHubNpcEntity.Role.ONE_EYE, cx, cy + 1, cz - 11);
        level.setBlock(new BlockPos(cx + 8, cy + 1, cz),
                ModBlocks.UNDERCAT_WAYSTONE.get().defaultBlockState(), 2);
        for (int i = 0; i < 4; i++) {
            var leech = ModEntities.BLIND_WATER_LEECH.get().create(level);
            if (leech != null) {
                leech.setPos(cx + 10 + i * 3, cy, cz + 4);
                leech.finalizeSpawn(level, level.getCurrentDifficultyAt(center), MobSpawnType.STRUCTURE, null, null);
                level.addFreshEntity(leech);
            }
        }
    }
}
