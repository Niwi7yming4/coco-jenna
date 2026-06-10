package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/** 良快刀商店 + 鐵匠舖 + 商店小巷（設計書 3.3）. */
public final class FirstCryMarketDistrictBuilder {

    private FirstCryMarketDistrictBuilder() {}

    public static void build(ServerLevel level) {
        buildRyokatanaShop(level, FirstCryLayout.RYOKATANA_SHOP);
        buildBlacksmith(level, FirstCryLayout.BLACKSMITH);
        buildShopAlley(level, FirstCryLayout.SHOP_ALLEY);
    }

    private static void buildRyokatanaShop(ServerLevel level, BlockPos o) {
        FirstCryBuildHelper.hollowBox(level, o, 12, 4, 8,
                ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState(),
                FirstCryBuildHelper.planks(), ModBlocks.TAPE_TEMPLE.get().defaultBlockState());
        for (int x = 1; x < 11; x++) {
            if (x % 3 == 0) {
                FirstCryBuildHelper.set(level, o.offset(x, 1, 1), ModBlocks.TAPE_BLOCK.get().defaultBlockState());
            }
        }
        FirstCryBuildHelper.doorGap(level, o.getX() + 4, o.getY() + 1, o.getZ(),
                Direction.NORTH, 4, 3);
        FirstCryBuildHelper.set(level, o.offset(5, 3, 0), ModBlocks.MURAL_FRAGMENT.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(2, 2, 0), ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(8, 2, 0), ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        FirstCryBuildHelper.fillRect(level, o.getX() + 2, o.getY() + 1, o.getZ() + 2,
                o.getX() + 4, o.getY() + 2, o.getZ() + 4, ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
        FirstCryBuildHelper.fillRect(level, o.getX() + 6, o.getY() + 1, o.getZ() + 2,
                o.getX() + 8, o.getY() + 2, o.getZ() + 4, ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
        for (int x = 2; x <= 10; x += 2) {
            FirstCryBuildHelper.set(level, o.offset(x, 1, 7), ModBlocks.RYOKATANA_SHOP_STAND.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, o.offset(6, 1, 6), ModBlocks.VELVET_CARPET.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(3, 1, 4), ModBlocks.CAT_SCRATCH_BOARD.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(8, 1, 4), ModBlocks.CAT_SCRATCH_BOARD.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(10, 1, 6), ModBlocks.CAT_BED.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(10, 1, 5), ModBlocks.TOY_BOX.get().defaultBlockState());
        FirstCryBuildHelper.lootChest(level, o.offset(1, 1, 6), "shop_ryokatana");
    }

    private static void buildBlacksmith(ServerLevel level, BlockPos o) {
        for (int x = 0; x < 14; x++) {
            for (int z = 0; z < 10; z++) {
                FirstCryBuildHelper.set(level, o.offset(x, -1, z), ModBlocks.SALT_BLOCK.get().defaultBlockState());
            }
        }
        FirstCryBuildHelper.hollowBox(level, o, 14, 5, 10,
                ModBlocks.REINFORCED_CARDBOARD.get().defaultBlockState(),
                FirstCryBuildHelper.brick(), FirstCryBuildHelper.thatch());
        FirstCryBuildHelper.fillRect(level, o.getX() + 5, o.getY() + 5, o.getZ() + 4,
                o.getX() + 7, o.getY() + 5, o.getZ() + 6, Blocks.GLASS.defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(6, 1, 5), ModBlocks.IRONPAW_FORGE.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(3, 1, 3), ModBlocks.SOCKETING_TABLE.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(9, 1, 3), ModBlocks.BLUEPRINT_TABLE.get().defaultBlockState());
        FirstCryBuildHelper.waterPool(level, o.offset(4, 0, 7), 3, 3);
        for (int x = 2; x <= 11; x += 3) {
            FirstCryBuildHelper.set(level, o.offset(x, 2, 8), ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, o.offset(11, 3, 2), ModBlocks.CAT_CLIMB_PLATFORM.get().defaultBlockState());
        FirstCryBuildHelper.doorGap(level, o.getX() + 6, o.getY() + 1, o.getZ(),
                Direction.NORTH, 2, 2);
        FirstCryBuildHelper.lootChest(level, o.offset(1, 1, 8), "forge_output");
    }

    private static void buildShopAlley(ServerLevel level, BlockPos start) {
        for (int z = 0; z < 10; z++) {
            for (int w = -1; w <= 1; w++) {
                FirstCryBuildHelper.set(level, start.offset(w, 0, z), FirstCryBuildHelper.brick());
            }
            FirstCryBuildHelper.set(level, start.offset(0, 3, z), ModBlocks.ROPE_NET.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, start.offset(0, 0, 4), ModBlocks.CAT_BED.get().defaultBlockState());
        FirstCryBuildHelper.set(level, start.offset(0, 0, 7), ModBlocks.CAT_BED.get().defaultBlockState());
        FirstCryBuildHelper.set(level, start.offset(2, 1, 9), ModBlocks.CATNIP.get().defaultBlockState());
        FirstCryBuildHelper.set(level, start.offset(-1, 1, 9), ModBlocks.COTTON_CANDY_SHRUB.get().defaultBlockState());
        FirstCryBuildHelper.set(level, start.offset(1, 1, 9), ModBlocks.HIBISCUS_FLOWER.get().defaultBlockState());
    }
}
