package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/** 農牧區（設計書 3.8）. */
public final class FirstCryFarmBuilder {

    private FirstCryFarmBuilder() {}

    public static void build(ServerLevel level) {
        BlockPos field = FirstCryLayout.FARM;
        for (int qx = 0; qx < 3; qx++) {
            for (int qz = 0; qz < 3; qz++) {
                for (int dx = 0; dx < 5; dx++) {
                    for (int dz = 0; dz < 5; dz++) {
                        BlockPos p = field.offset(qx * 6 + dx, 0, qz * 6 + dz);
                        FirstCryBuildHelper.set(level, p.below(), ModBlocks.STARDUST_SOIL.get().defaultBlockState());
                        FirstCryBuildHelper.set(level, p, Blocks.FARMLAND.defaultBlockState());
                        FirstCryBuildHelper.set(level, p.above(), ModBlocks.CATNIP.get().defaultBlockState());
                    }
                }
            }
        }
        for (int qx = 0; qx < 3; qx++) {
            for (int dz = 0; dz < 15; dz++) {
                FirstCryBuildHelper.set(level, field.offset(qx * 6 + 5, 0, dz), FirstCryBuildHelper.brick());
                if (dz % 2 == 0) {
                    FirstCryBuildHelper.set(level, field.offset(qx * 6 + 5, 0, dz + 1), FirstCryBuildHelper.brick());
                }
            }
        }
        BlockPos scarecrow = field.offset(7, 0, 7);
        FirstCryBuildHelper.set(level, scarecrow, ModBlocks.SCRATCHING_POST.get().defaultBlockState());
        FirstCryBuildHelper.set(level, scarecrow.above(), FirstCryBuildHelper.wool());
        FirstCryBuildHelper.set(level, scarecrow.above(2), ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        BlockPos pond = field.offset(0, 0, 18);
        for (int dx = 0; dx < 10; dx++) {
            for (int dz = 0; dz < 8; dz++) {
                boolean edge = dx == 0 || dx == 9 || dz == 0 || dz == 7;
                BlockPos p = pond.offset(dx, 0, dz);
                if (edge) {
                    FirstCryBuildHelper.set(level, p, ModBlocks.SALT_BLOCK.get().defaultBlockState());
                } else {
                    FirstCryBuildHelper.set(level, p.below(), ModBlocks.STARDUST_SOIL.get().defaultBlockState());
                    FirstCryBuildHelper.set(level, p, Blocks.WATER.defaultBlockState());
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            FirstCryBuildHelper.set(level, pond.offset(2 + i * 2, 0, -1),
                    ModBlocks.FOOD_BOWL.get().defaultBlockState());
        }
        for (int z = 2; z <= 5; z++) {
            FirstCryBuildHelper.set(level, pond.offset(4, 0, z), FirstCryBuildHelper.planks());
            FirstCryBuildHelper.set(level, pond.offset(5, 0, z), FirstCryBuildHelper.planks());
        }
        FirstCryBuildHelper.set(level, field.offset(14, 1, 2), ModBlocks.AROMA_DISTILLER.get().defaultBlockState());
        BlockPos shed = field.offset(16, 0, 0);
        FirstCryBuildHelper.hollowBox(level, shed, 6, 3, 5,
                FirstCryBuildHelper.planks(), FirstCryBuildHelper.planks(), FirstCryBuildHelper.thatch());
        FirstCryBuildHelper.set(level, shed.offset(2, 1, 2), ModBlocks.BLUEPRINT_TABLE.get().defaultBlockState());
        FirstCryBuildHelper.set(level, shed.offset(4, 1, 1), ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState());
        FirstCryBuildHelper.set(level, shed.offset(1, 1, 3), ModBlocks.CAT_BED.get().defaultBlockState());
        FirstCryBuildHelper.doorGap(level, shed.getX() + 2, shed.getY() + 1, shed.getZ(),
                Direction.NORTH, 2, 2);
    }
}
