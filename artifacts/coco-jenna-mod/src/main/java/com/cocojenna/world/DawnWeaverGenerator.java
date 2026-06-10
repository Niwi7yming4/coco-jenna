package com.cocojenna.world;

import com.cocojenna.exploration.ExplorationMarkers;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 黎明高地 — 三花子披風工坊（設計書 9.5）.
 */
public final class DawnWeaverGenerator {

    public static final int FLOOR_Y = 64;
    public static final BlockPos CENTER = new BlockPos(256, FLOOR_Y, -384);
    private static final BlockPos MARKER = new BlockPos(256, FLOOR_Y, -384);

    private DawnWeaverGenerator() {}

    public static void ensureWeaver(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (level.getBlockState(MARKER).is(ModBlocks.VELVET_BLOCK.get())) return;
        placeWorkshop(level);
    }

    private static void placeWorkshop(ServerLevel level) {
        int cx = CENTER.getX();
        int cz = CENTER.getZ();

        for (int x = -6; x <= 6; x++) {
            for (int z = -6; z <= 6; z++) {
                set(level, cx + x, FLOOR_Y, cz + z, ModBlocks.VELVET_BLOCK.get().defaultBlockState());
            }
        }

        for (int y = 1; y <= 3; y++) {
            for (int x = -5; x <= 5; x++) {
                set(level, cx + x, FLOOR_Y + y, cz - 5, Blocks.SPRUCE_PLANKS.defaultBlockState());
                set(level, cx + x, FLOOR_Y + y, cz + 5, Blocks.SPRUCE_PLANKS.defaultBlockState());
            }
            for (int z = -4; z <= 4; z++) {
                set(level, cx - 5, FLOOR_Y + y, cz + z, Blocks.SPRUCE_PLANKS.defaultBlockState());
                set(level, cx + 5, FLOOR_Y + y, cz + z, Blocks.SPRUCE_PLANKS.defaultBlockState());
            }
        }

        set(level, cx, FLOOR_Y + 1, cz + 5, Blocks.AIR.defaultBlockState());
        set(level, cx, FLOOR_Y + 2, cz + 5, Blocks.AIR.defaultBlockState());
        set(level, cx, FLOOR_Y, cz, Blocks.LOOM.defaultBlockState());
        set(level, cx - 2, FLOOR_Y, cz, ModBlocks.CAT_BED.get().defaultBlockState());

        var sanhua = ModEntities.SANHUA_WEAVER.get().create(level);
        if (sanhua != null) {
            sanhua.setPos(cx, FLOOR_Y + 1, cz + 2);
            sanhua.setPersistenceRequired();
            level.addFreshEntity(sanhua);
        }
        set(level, cx + 8, FLOOR_Y + 1, cz - 8, ModBlocks.PURE_LIGHT_TOWER.get().defaultBlockState());
        var alpha = ModEntities.ALPHA.get().create(level);
        if (alpha != null) {
            alpha.setPos(cx - 4, FLOOR_Y + 1, cz - 4);
            alpha.setPersistenceRequired();
            level.addFreshEntity(alpha);
        }
        BlackMudBossHelper.trySpawnBoss(level, ModEntities.PRIMAL_CHAOS.get(),
                new BlockPos(cx + 6, FLOOR_Y + 1, cz + 6));
        ExplorationMarkers.placeDawnHighlands(level, CENTER);
    }

    private static void set(Level level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }
}
