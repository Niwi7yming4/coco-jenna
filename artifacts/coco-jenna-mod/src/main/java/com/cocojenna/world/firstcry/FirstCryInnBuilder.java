package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/** 訪客小屋 ×3 + 貓旅館 + 秘密通道（設計書 3.7、3.10）. */
public final class FirstCryInnBuilder {

    private FirstCryInnBuilder() {}

    public static void build(ServerLevel level) {
        buildGuestCottage(level, FirstCryLayout.GUEST_COTTAGE_1);
        buildGuestCottage(level, FirstCryLayout.GUEST_COTTAGE_2);
        buildGuestCottage(level, FirstCryLayout.GUEST_COTTAGE_3);
        buildCatHotel(level, FirstCryLayout.CAT_HOTEL);
        buildSecretPassage(level, FirstCryLayout.SECRET_PASSAGE);
    }

    private static void buildGuestCottage(ServerLevel level, BlockPos o) {
        FirstCryBuildHelper.hollowBox(level, o, 8, 3, 6,
                FirstCryBuildHelper.planks(), FirstCryBuildHelper.planks(), FirstCryBuildHelper.thatch());
        FirstCryBuildHelper.stripeWalls(level, o, 8, 3, 6);
        FirstCryBuildHelper.set(level, o.offset(3, 0, -1), ModBlocks.CAT_BED.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(2, 1, 3), ModBlocks.CAT_BED.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(5, 1, 3), ModBlocks.CAT_BED.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(4, 1, 2), ModBlocks.BLUEPRINT_TABLE.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(6, 1, 4), ModBlocks.PICTURE_BOOK_STAND.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(2, 2, 4), ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(6, 2, 3), ModBlocks.TOY_BOX.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(7, 2, 4), ModBlocks.CATNIP.get().defaultBlockState());
        FirstCryBuildHelper.doorGap(level, o.getX() + 3, o.getY() + 1, o.getZ() + 5,
                Direction.SOUTH, 2, 2);
    }

    private static void buildCatHotel(ServerLevel level, BlockPos o) {
        FirstCryBuildHelper.hollowBox(level, o, 12, 5, 8,
                ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState(),
                FirstCryBuildHelper.planks(), FirstCryBuildHelper.thatch());
        FirstCryBuildHelper.doorGap(level, o.getX() + 4, o.getY() + 1, o.getZ(),
                Direction.NORTH, 2, 3);
        FirstCryBuildHelper.doorGap(level, o.getX() + 6, o.getY() + 1, o.getZ(),
                Direction.NORTH, 2, 3);
        for (int x = 2; x <= 9; x += 2) {
            FirstCryBuildHelper.set(level, o.offset(x, 5, 3), Blocks.GLASS.defaultBlockState());
        }
        for (int x = 0; x < 12; x += 3) {
            FirstCryBuildHelper.set(level, o.offset(x, 3, 7), ModBlocks.CAT_CLIMB_PLATFORM.get().defaultBlockState());
            FirstCryBuildHelper.set(level, o.offset(x, 5, 7), ModBlocks.CAT_CLIMB_PLATFORM.get().defaultBlockState());
        }
        int bed = 0;
        for (int x = 1; x <= 10 && bed < 12; x += 2) {
            for (int z = 2; z <= 6 && bed < 12; z += 2) {
                FirstCryBuildHelper.set(level, o.offset(x, 1, z), ModBlocks.CAT_BED.get().defaultBlockState());
                bed++;
            }
        }
        for (int z = 2; z <= 6; z++) {
            FirstCryBuildHelper.set(level, o.offset(10, 1, z), ModBlocks.FOOD_BOWL.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, o.offset(2, 1, 6), ModBlocks.TOY_BOX.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(8, 1, 6), ModBlocks.TOY_BOX.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(9, 1, 6), ModBlocks.TOY_BOX.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(10, 1, 6), ModBlocks.TOY_BOX.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(4, 1, 1), ModBlocks.SCRATCHING_POST.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(7, 1, 1), ModBlocks.SCRATCHING_POST.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(5, 1, 1), ModBlocks.SCRATCHING_POST.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(5, 1, 4), ModBlocks.CAT_SCRATCH_BOARD.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(6, 1, 4), ModBlocks.CAT_SCRATCH_BOARD.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(6, 2, 4), ModBlocks.CAT_CLIMB_PLATFORM.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(1, 2, 2), ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(10, 2, 4), ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState());
        FirstCryBuildHelper.lootChest(level, o.offset(10, 1, 1), "hotel_lost_and_found");
    }

    private static void buildSecretPassage(ServerLevel level, BlockPos start) {
        for (int z = 0; z < 8; z++) {
            FirstCryBuildHelper.set(level, start.offset(0, 1, z), Blocks.AIR.defaultBlockState());
            FirstCryBuildHelper.set(level, start.offset(0, 2, z), Blocks.AIR.defaultBlockState());
            FirstCryBuildHelper.set(level, start.offset(-1, 1, z), ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState());
            FirstCryBuildHelper.set(level, start.offset(1, 1, z), ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, start.offset(0, 1, 0), ModBlocks.COTTON_CANDY_SHRUB.get().defaultBlockState());
        FirstCryBuildHelper.set(level, start.offset(0, 1, 7), ModBlocks.COTTON_CANDY_SHRUB.get().defaultBlockState());
        FirstCryBuildHelper.set(level, start.offset(0, 1, 4), ModBlocks.TOY_BOX.get().defaultBlockState());
        FirstCryBuildHelper.set(level, start.offset(0, 1, 3), ModBlocks.CATNIP.get().defaultBlockState());
        FirstCryBuildHelper.set(level, start.offset(-1, 2, 4), ModBlocks.NEON_MUSHROOM.get().defaultBlockState());
    }
}
