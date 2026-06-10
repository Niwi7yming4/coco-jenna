package com.cocojenna.world;

import com.cocojenna.block.MoonTrialAltarBlock;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** 初啼村後方月光三岔路微型結構. */
public final class MoonCrossroadsPlacer {

    private static final BlockPos ORIGIN = new BlockPos(0, FirstCryVillageGenerator.FLOOR_Y, -30);
    private static boolean placed;

    private MoonCrossroadsPlacer() {}

    public static void ensurePlaced(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (placed && level.getBlockState(ORIGIN).is(ModBlocks.MOON_TRIAL_ALTAR.get())) return;
        place(level);
        placed = true;
    }

    public static void place(ServerLevel level) {
        BlockPos o = ORIGIN;
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                level.setBlock(o.offset(dx, 0, dz), ModBlocks.MOONSTONE_BRICK.get().defaultBlockState(), 2);
            }
        }
        level.setBlock(o.above(), Blocks.GLOWSTONE.defaultBlockState(), 2);
        placeAltar(level, o.offset(-4, 1, 0), MoonTrialAltarBlock.ForcePath.RESONANCE);
        placeAltar(level, o.offset(0, 1, -2), MoonTrialAltarBlock.ForcePath.SHADOW);
        placeAltar(level, o.offset(4, 1, 0), MoonTrialAltarBlock.ForcePath.CHAOS);
        for (int i = -2; i <= 2; i++) {
            level.setBlock(o.offset(i, 1, 2), Blocks.LANTERN.defaultBlockState(), 2);
        }
    }

    private static void placeAltar(ServerLevel level, BlockPos pos, MoonTrialAltarBlock.ForcePath force) {
        BlockState state = ModBlocks.MOON_TRIAL_ALTAR.get().defaultBlockState()
                .setValue(MoonTrialAltarBlock.FORCE, force);
        level.setBlock(pos, state, 2);
        BlockState wool = switch (force) {
            case RESONANCE -> Blocks.YELLOW_WOOL.defaultBlockState();
            case SHADOW -> Blocks.PURPLE_WOOL.defaultBlockState();
            case CHAOS -> Blocks.PINK_WOOL.defaultBlockState();
        };
        level.setBlock(pos.above(), wool, 2);
    }
}
