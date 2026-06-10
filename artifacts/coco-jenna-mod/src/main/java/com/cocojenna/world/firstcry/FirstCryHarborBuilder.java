package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/** 初啼港碼頭（設計書 3.6）. */
public final class FirstCryHarborBuilder {

    private FirstCryHarborBuilder() {}

    public static void build(ServerLevel level) {
        BlockPos dock = FirstCryLayout.HARBOR;
        for (int x = 0; x < 15; x++) {
            for (int z = 0; z < 6; z++) {
                FirstCryBuildHelper.set(level, dock.offset(x, 0, z), FirstCryBuildHelper.planks());
            }
        }
        for (int x = -1; x < 16; x += 4) {
            FirstCryBuildHelper.set(level, dock.offset(x, 1, 0), ModBlocks.VELVET_TREE_LOG.get().defaultBlockState());
            FirstCryBuildHelper.set(level, dock.offset(x, 1, 5), ModBlocks.ROPE_NET.get().defaultBlockState());
        }
        int[][] berths = {{2, 5}, {6, 5}, {10, 5}, {13, 5}};
        for (int[] b : berths) {
            FirstCryBuildHelper.set(level, dock.offset(b[0], 1, b[1]),
                    ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState());
            FirstCryBuildHelper.set(level, dock.offset(b[0], 0, b[1] + 1), Blocks.OAK_STAIRS.defaultBlockState());
        }
        BlockPos cabin = dock.offset(5, 0, -6);
        FirstCryBuildHelper.hollowBox(level, cabin, 5, 3, 5,
                FirstCryBuildHelper.planks(), FirstCryBuildHelper.planks(), FirstCryBuildHelper.thatch());
        FirstCryBuildHelper.set(level, cabin.offset(2, 1, 2), ModBlocks.BLUEPRINT_TABLE.get().defaultBlockState());
        FirstCryBuildHelper.set(level, cabin.offset(1, 1, 2), ModBlocks.CAT_BED.get().defaultBlockState());
        FirstCryBuildHelper.set(level, cabin.offset(3, 1, 1), ModBlocks.FOOD_BOWL.get().defaultBlockState());
        FirstCryBuildHelper.set(level, cabin.offset(2, 2, 0), ModBlocks.MURAL_FRAGMENT.get().defaultBlockState());
        FirstCryBuildHelper.lootChest(level, cabin.offset(1, 1, 1), "harbor_supply");
        for (int z = -10; z <= 6; z++) {
            for (int w = -1; w <= 1; w++) {
                FirstCryBuildHelper.set(level, dock.offset(7 + w, 0, z), FirstCryBuildHelper.brick());
            }
            if (z % 4 == 0) {
                FirstCryBuildHelper.set(level, dock.offset(7, 1, z),
                        ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState());
            }
        }
        for (int i = 0; i < 4; i++) {
            FirstCryBuildHelper.set(level, dock.offset(3 + i * 3, 1, -4),
                    ModBlocks.COTTON_CANDY_SHRUB.get().defaultBlockState());
            FirstCryBuildHelper.set(level, dock.offset(4 + i * 3, 1, -3),
                    ModBlocks.HIBISCUS_FLOWER.get().defaultBlockState());
        }
        for (int x = -20; x <= 20; x++) {
            for (int z = 30; z <= 44; z++) {
                FirstCryBuildHelper.set(level, x, FirstCryLayout.Y - 1, z, Blocks.WATER.defaultBlockState());
            }
        }
    }
}
