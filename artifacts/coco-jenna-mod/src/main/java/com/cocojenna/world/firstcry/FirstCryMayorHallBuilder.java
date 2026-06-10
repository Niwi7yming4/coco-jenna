package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/** 村長議事廳 20×12 雙層（設計書 3.2）. */
public final class FirstCryMayorHallBuilder {

    private FirstCryMayorHallBuilder() {}

    public static void build(ServerLevel level) {
        BlockPos o = FirstCryLayout.MAYOR_HALL;
        FirstCryBuildHelper.hollowBox(level, o, 20, 4, 12,
                FirstCryBuildHelper.planks(), FirstCryBuildHelper.planks(), FirstCryBuildHelper.thatch());
        FirstCryBuildHelper.stripeWalls(level, o, 20, 4, 12);
        for (int x = 4; x <= 15; x++) {
            FirstCryBuildHelper.set(level, o.offset(x, 1, 6), ModBlocks.BLUEPRINT_TABLE.get().defaultBlockState());
            FirstCryBuildHelper.set(level, o.offset(x, 1, 5), ModBlocks.CAT_BED.get().defaultBlockState());
            FirstCryBuildHelper.set(level, o.offset(x, 1, 7), ModBlocks.CAT_BED.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, o.offset(16, 1, 6), ModBlocks.DECREE_PEDESTAL.get().defaultBlockState());
        FirstCryBuildHelper.fillRect(level, o.getX() + 14, o.getY(), o.getZ() + 4,
                o.getX() + 17, o.getY(), o.getZ() + 8, ModBlocks.VELVET_CARPET.get().defaultBlockState());
        for (int dy = 1; dy <= 3; dy++) {
            FirstCryBuildHelper.set(level, o.offset(1, dy, 3), ModBlocks.MURAL_FRAGMENT.get().defaultBlockState());
        }
        for (int x = 5; x <= 15; x += 2) {
            FirstCryBuildHelper.set(level, o.offset(x, 4, 6), ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        }
        FirstCryBuildHelper.doorGap(level, o.getX() + 8, o.getY() + 1, o.getZ() + 11,
                Direction.SOUTH, 4, 3);
        FirstCryBuildHelper.set(level, o.offset(8, 3, 11), ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(0, 2, 6), ModBlocks.VELVET_TREE_LOG.get().defaultBlockState());
        FirstCryBuildHelper.set(level, o.offset(0, 3, 6), ModBlocks.VELVET_TREE_LOG.get().defaultBlockState());
        FirstCryBuildHelper.catDoor(level, o.offset(1, 1, 6), Direction.WEST);
        for (int x = 5; x <= 14; x += 2) {
            FirstCryBuildHelper.set(level, o.offset(x, 5, 0), ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState());
        }
        for (int dy = 1; dy <= 4; dy++) {
            FirstCryBuildHelper.set(level, o.offset(18, dy, 6), Blocks.LADDER.defaultBlockState()
                    .setValue(net.minecraft.world.level.block.LadderBlock.FACING, Direction.WEST));
        }
        BlockPos u = o.offset(13, 5, 2);
        FirstCryBuildHelper.hollowBox(level, u, 6, 3, 7,
                ModBlocks.STARLIGHT_MARBLE.get().defaultBlockState(),
                FirstCryBuildHelper.planks(), FirstCryBuildHelper.thatch());
        FirstCryBuildHelper.set(level, u.offset(2, 1, 3), ModBlocks.CAT_BED.get().defaultBlockState());
        FirstCryBuildHelper.set(level, u.offset(4, 1, 3), ModBlocks.CAT_BED.get().defaultBlockState());
        FirstCryBuildHelper.set(level, u.offset(1, 1, 4), ModBlocks.PICTURE_BOOK_STAND.get().defaultBlockState());
        FirstCryBuildHelper.set(level, u.offset(4, 1, 5), ModBlocks.PICTURE_BOOK_STAND.get().defaultBlockState());
        FirstCryBuildHelper.fillRect(level, u.getX() + 5, u.getY() + 1, u.getZ(),
                u.getX() + 6, u.getY() + 2, u.getZ() + 6, ModBlocks.ROPE_NET.get().defaultBlockState());
        FirstCryBuildHelper.set(level, u.offset(1, 2, 5), ModBlocks.SUSPICIOUS_WALL.get().defaultBlockState());
        FirstCryBuildHelper.tablet(level, u.offset(2, 3, 4), 9);
        FirstCryBuildHelper.lootChest(level, u.offset(3, 3, 4), "mayor_secret");
    }
}
