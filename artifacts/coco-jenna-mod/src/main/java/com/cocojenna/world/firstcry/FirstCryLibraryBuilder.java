package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/** 記憶圖書館 18×10（設計書 3.2）. */
public final class FirstCryLibraryBuilder {

    private FirstCryLibraryBuilder() {}

    public static void build(ServerLevel level) {
        BlockPos o = FirstCryLayout.LIBRARY;
        for (int x = 0; x < 18; x++) {
            for (int z = 0; z < 10; z++) {
                boolean edge = x == 0 || x == 17 || z == 0 || z == 9;
                BlockPos p = o.offset(x, 0, z);
                FirstCryBuildHelper.set(level, p, (x + z) % 2 == 0
                        ? ModBlocks.REINFORCED_CARDBOARD.get().defaultBlockState()
                        : ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState());
                if (edge) {
                    for (int dy = 1; dy <= 5; dy++) {
                        FirstCryBuildHelper.set(level, p.above(dy), (x + z + dy) % 2 == 0
                                ? ModBlocks.REINFORCED_CARDBOARD.get().defaultBlockState()
                                : ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState());
                    }
                }
            }
        }
        for (int x = 1; x < 17; x++) {
            for (int z = 1; z < 9; z++) {
                FirstCryBuildHelper.set(level, o.offset(x, 6, z), ModBlocks.TAPE_TEMPLE.get().defaultBlockState());
            }
        }
        for (int x = 2; x <= 15; x += 3) {
            for (int dy = 2; dy <= 4; dy++) {
                FirstCryBuildHelper.set(level, o.offset(x, dy, 0), ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
            }
        }
        for (int x = 1; x < 17; x++) {
            for (int dy = 1; dy <= 4; dy++) {
                if (x % 3 != 0) {
                    FirstCryBuildHelper.set(level, o.offset(x, dy, 1), ModBlocks.PICTURE_BOOK_STAND.get().defaultBlockState());
                    FirstCryBuildHelper.set(level, o.offset(x, dy, 8), ModBlocks.PICTURE_BOOK_STAND.get().defaultBlockState());
                } else if (dy == 2) {
                    FirstCryBuildHelper.tablet(level, o.offset(x, dy, 1), 14);
                    FirstCryBuildHelper.tablet(level, o.offset(x, dy, 8), 14);
                }
            }
        }
        int[][] read = {{3, 4}, {7, 4}, {11, 4}, {15, 4}};
        for (int[] r : read) {
            FirstCryBuildHelper.set(level, o.offset(r[0], 1, r[1]), ModBlocks.BLUEPRINT_TABLE.get().defaultBlockState());
            FirstCryBuildHelper.set(level, o.offset(r[0], 1, r[1] + 1), ModBlocks.CAT_BED.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, o.offset(2, 1, 7), ModBlocks.SOCKETING_TABLE.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(3, 1, 7), ModBlocks.AROMA_DISTILLER.get().defaultBlockState());
        for (int x = 5; x <= 13; x += 4) {
            FirstCryBuildHelper.set(level, o.offset(x, 5, 5), ModBlocks.ROPE_NET.get().defaultBlockState());
        }
        for (int x = 2; x < 16; x += 2) {
            FirstCryBuildHelper.set(level, o.offset(x, 4, 2), ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState());
        }
        FirstCryBuildHelper.doorGap(level, o.getX() + 8, o.getY() + 1, o.getZ() + 9,
                Direction.SOUTH, 2, 2);
        FirstCryBuildHelper.set(level, o.offset(15, 2, 8), ModBlocks.SUSPICIOUS_WALL.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(16, 1, 7), ModBlocks.SHADOW_CRYSTAL_BLOCK.get().defaultBlockState());
        FirstCryBuildHelper.lootChest(level, o.offset(16, 1, 6), "forbidden_books");
    }
}
