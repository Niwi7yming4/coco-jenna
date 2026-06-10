package com.cocojenna.world;

import com.cocojenna.exploration.ExplorationMarkers;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** 絨毛森林 — 悲嘆聚合體 Boss. */
public final class VelvetForestPoiGenerator {

    public static final BlockPos CENTER = new BlockPos(0, 64, -256);
    private static final BlockPos MARKER = new BlockPos(0, 64, -256);

    private VelvetForestPoiGenerator() {}

    public static void ensureForest(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (level.getBlockState(MARKER).is(ModBlocks.VELVET_TREE_LOG.get())) return;
        build(level);
    }

    private static void build(Level level) {
        int cx = CENTER.getX();
        int cz = CENTER.getZ();
        int y = CENTER.getY();
        for (int x = -14; x <= 14; x++) {
            for (int z = -14; z <= 14; z++) {
                if (x * x + z * z > 14 * 14) continue;
                BlockState ground = (x + z) % 4 == 0
                        ? ModBlocks.VELVET_GRASS.get().defaultBlockState()
                        : ModBlocks.STARDUST_SOIL.get().defaultBlockState();
                set(level, cx + x, y, cz + z, ground);
            }
        }

        ArchitectureBuilders.buildMushroomCottage(level, cx + 7, y, cz - 5);
        ArchitectureBuilders.buildThatchedCottage(level, new BlockPos(cx - 8, y, cz + 6));

        int[][] trees = {{4, -8}, {-6, -4}, {10, 4}, {-10, 8}, {0, 10}};
        for (int[] t : trees) {
            ArchitectureBuilders.buildVelvetTree(level, new BlockPos(cx + t[0], y, cz + t[1]), 5 + level.random.nextInt(3));
        }

        set(level, cx, y, cz, ModBlocks.BLACK_MUD.get().defaultBlockState());
        set(level, cx + 1, y, cz, ModBlocks.TOY_BOX.get().defaultBlockState());
        set(level, cx - 1, y + 1, cz, ModBlocks.NEON_MUSHROOM_POT.get().defaultBlockState());

        if (level instanceof ServerLevel server) {
            BlackMudBossHelper.trySpawnBoss(server, ModEntities.GRIEF_AMALGAM.get(), new BlockPos(cx, y + 1, cz));
            ExplorationMarkers.placeVelvetForest(server, CENTER);
        }
    }

    private static void set(Level level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }
}
