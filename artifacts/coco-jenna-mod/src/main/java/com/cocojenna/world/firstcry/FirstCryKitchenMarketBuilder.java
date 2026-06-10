package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/** 貓廚房 + 公共食堂 + 貓薄荷市場（設計書 3.4）. */
public final class FirstCryKitchenMarketBuilder {

    private FirstCryKitchenMarketBuilder() {}

    public static void build(ServerLevel level) {
        buildKitchen(level, FirstCryLayout.KITCHEN);
        buildCanteen(level, FirstCryLayout.CANTEEN);
        buildCatnipMarket(level, FirstCryLayout.CATNIP_MARKET);
    }

    private static void buildKitchen(ServerLevel level, BlockPos o) {
        FirstCryBuildHelper.hollowBox(level, o, 10, 4, 8,
                FirstCryBuildHelper.planks(), FirstCryBuildHelper.planks(), FirstCryBuildHelper.thatch());
        for (int x = 0; x < 10; x++) {
            FirstCryBuildHelper.set(level, o.offset(x, 0, 0), ModBlocks.SALT_BLOCK.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, o.offset(9, 1, 4), ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(9, 2, 4), ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(9, 3, 4), ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState());
        for (int x = 2; x <= 7; x++) {
            FirstCryBuildHelper.set(level, o.offset(x, 5, 0), ModBlocks.COTTON_CANDY_SHRUB.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, o.offset(4, 1, 4), ModBlocks.CAT_KITCHEN.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(6, 1, 4), ModBlocks.AROMA_DISTILLER.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(2, 1, 3), ModBlocks.BLUEPRINT_TABLE.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(7, 1, 3), ModBlocks.BLUEPRINT_TABLE.get().defaultBlockState());
        for (int z = 2; z <= 6; z++) {
            FirstCryBuildHelper.set(level, o.offset(1, 1, z), ModBlocks.FOOD_BOWL.get().defaultBlockState());
        }
        for (int x = 2; x <= 7; x++) {
            FirstCryBuildHelper.set(level, o.offset(x, 2, 6), ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, o.offset(8, 1, 6), ModBlocks.CAT_BED.get().defaultBlockState());
        FirstCryBuildHelper.doorGap(level, o.getX() + 4, o.getY() + 1, o.getZ(),
                Direction.NORTH, 2, 2);
        FirstCryBuildHelper.lootChest(level, o.offset(1, 1, 1), "kitchen_ingredients");
    }

    private static void buildCanteen(ServerLevel level, BlockPos o) {
        for (int x = 0; x < 15; x++) {
            for (int z = 0; z < 10; z++) {
                FirstCryBuildHelper.set(level, o.offset(x, 0, z),
                        z < 5 ? FirstCryBuildHelper.planks() : FirstCryBuildHelper.grass());
            }
        }
        for (int x = 0; x < 10; x++) {
            for (int z = 0; z < 5; z++) {
                FirstCryBuildHelper.set(level, o.offset(x, 5, z), FirstCryBuildHelper.thatch());
            }
        }
        for (int row = 0; row < 2; row++) {
            for (int x = 1; x <= 8; x++) {
                FirstCryBuildHelper.fillRect(level, o.getX() + x, o.getY(), o.getZ() + 1 + row * 3,
                        o.getX() + x + 1, o.getY(), o.getZ() + 2 + row * 3, FirstCryBuildHelper.planks());
                FirstCryBuildHelper.set(level, o.offset(x, 1, 2 + row * 3), ModBlocks.CAT_BED.get().defaultBlockState());
                FirstCryBuildHelper.set(level, o.offset(x + 1, 1, 2 + row * 3), ModBlocks.CAT_BED.get().defaultBlockState());
            }
        }
        for (int x = 3; x <= 7; x += 2) {
            FirstCryBuildHelper.set(level, o.offset(x, 4, 3), ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        }
        for (int z = 5; z <= 9; z++) {
            FirstCryBuildHelper.set(level, o.offset(12, 3, z), ModBlocks.ROPE_NET.get().defaultBlockState());
            FirstCryBuildHelper.set(level, o.offset(12, 2, z), ModBlocks.VELVET_VINE.get().defaultBlockState());
        }
        for (int x = 11; x <= 13; x++) {
            FirstCryBuildHelper.set(level, o.offset(x, 1, 7), ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState());
        }
        FirstCryBuildHelper.waterPool(level, o.offset(12, 0, 8), 2, 2);
    }

    private static void buildCatnipMarket(ServerLevel level, BlockPos o) {
        int[][] pillars = {{0, 0}, {11, 0}, {0, 7}, {11, 7}};
        for (int[] p : pillars) {
            for (int dy = 0; dy < 4; dy++) {
                FirstCryBuildHelper.set(level, o.offset(p[0], dy, p[1]),
                        ModBlocks.VELVET_TREE_LOG.get().defaultBlockState());
            }
        }
        for (int x = 0; x <= 11; x++) {
            for (int z = 0; z <= 7; z++) {
                FirstCryBuildHelper.set(level, o.offset(x, 4, z), FirstCryBuildHelper.thatch());
            }
        }
        FirstCryBuildHelper.set(level, o.offset(5, 5, 3), ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());
        int[][] stalls = {{2, 2}, {8, 2}, {2, 5}, {8, 5}};
        BlockState[] goods = {
                ModBlocks.CATNIP.get().defaultBlockState(),
                ModBlocks.COTTON_CANDY_SHRUB.get().defaultBlockState(),
                ModBlocks.HIBISCUS_FLOWER.get().defaultBlockState(),
                ModBlocks.CATNIP.get().defaultBlockState()
        };
        for (int i = 0; i < stalls.length; i++) {
            int[] s = stalls[i];
            FirstCryBuildHelper.set(level, o.offset(s[0], 1, s[1]),
                    ModBlocks.CAT_CLIMB_PLATFORM.get().defaultBlockState());
            FirstCryBuildHelper.set(level, o.offset(s[0], 2, s[1]), goods[i]);
            FirstCryBuildHelper.tablet(level, o.offset(s[0], 0, s[1] - 1), 14);
        }
        FirstCryBuildHelper.set(level, o.offset(5, 0, 1), ModBlocks.CAT_BED.get().defaultBlockState());
    }
}
