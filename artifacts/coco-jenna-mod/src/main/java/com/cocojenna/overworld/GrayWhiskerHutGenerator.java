package com.cocojenna.overworld;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/** 隱居貓賢者灰鬚的小屋（主世界滲透核心結構）. */
public final class GrayWhiskerHutGenerator {

    private GrayWhiskerHutGenerator() {}

    public static BlockPos ensureHut(ServerLevel level, BlockPos near) {
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(level);
        if (data.isHutPlaced()) {
            return data.hutCenter();
        }

        int angle = level.random.nextInt(360);
        double dist = 280 + level.random.nextInt(220);
        int x = near.getX() + (int) (Math.cos(Math.toRadians(angle)) * dist);
        int z = near.getZ() + (int) (Math.sin(Math.toRadians(angle)) * dist);
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        BlockPos center = new BlockPos(x, y, z);

        buildHut(level, center);
        BlockPos portal = center.offset(4, 0, 0);
        data.setHut(center, portal);
        spawnGrayWhisker(level, center.offset(0, 1, 0));
        return center;
    }

    private static void buildHut(ServerLevel level, BlockPos center) {
        BlockState plank = Blocks.SPRUCE_PLANKS.defaultBlockState();
        BlockState log = Blocks.SPRUCE_LOG.defaultBlockState();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos floor = center.offset(dx, 0, dz);
                level.setBlock(floor, plank, 2);
            }
        }
        for (int dy = 1; dy <= 3; dy++) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    boolean wall = Math.abs(dx) == 3 || Math.abs(dz) == 3;
                    if (!wall) continue;
                    BlockPos p = center.offset(dx, dy, dz);
                    if (dy == 2 && dz == 3 && Math.abs(dx) <= 1) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    } else {
                        level.setBlock(p, log, 2);
                    }
                }
            }
        }
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                level.setBlock(center.offset(dx, 4, dz), plank, 2);
            }
        }
        level.setBlock(center.offset(0, 1, 0), Blocks.CAMPFIRE.defaultBlockState(), 2);
        level.setBlock(center.offset(-2, 1, -2), Blocks.CHEST.defaultBlockState(), 2);
        level.setBlock(center.offset(2, 2, -3), ModBlocks.CATNIP.get().defaultBlockState(), 2);
        level.setBlock(center.offset(-2, 2, -3), ModBlocks.CATNIP.get().defaultBlockState(), 2);

        BlockPos frame = center.offset(4, 1, 0);
        for (int h = 0; h < 4; h++) {
            level.setBlock(frame.offset(0, h, 0), ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState(), 2);
            level.setBlock(frame.offset(1, h, 0), ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState(), 2);
        }

        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(level);
        data.putTrace(center.offset(0, 2, -3), OverworldTraceType.CAT_GRAFFITI);
        data.putTrace(center.offset(2, 2, -3), OverworldTraceType.CAT_GRAFFITI);
    }

    private static void spawnGrayWhisker(ServerLevel level, BlockPos pos) {
        var entity = ModEntities.GRAY_WHISKER.get().create(level);
        if (entity != null) {
            entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            level.addFreshEntity(entity);
        }
    }
}
