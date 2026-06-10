package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** 聖樹、環樹廣場、樹洞、樹冠秘密平台（設計書 3.1）. */
public final class FirstCrySacredTreeBuilder {

    private static final int[][] NEON = {
            {3, 5, 2}, {4, 12, -3}, {-3, 8, 2}, {2, 15, -2},
            {-4, 10, -1}, {3, 3, -3}, {-2, 14, 3}, {4, 7, 1}
    };
    private static final int[] STORY_LORE = {22, 23, 24, 25};

    private FirstCrySacredTreeBuilder() {}

    public static void build(ServerLevel level) {
        BlockPos center = FirstCryLayout.SACRED_TREE;
        buildTrunk(level, center);
        buildCanopy(level, center);
        buildTreeHole(level, center);
        buildPlaza(level, center);
        buildCanopySecret(level, center);
        FirstCryBuildHelper.set(level, FirstCryLayout.offset(0, 52, 0),
                ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());
    }

    private static void buildTrunk(ServerLevel level, BlockPos c) {
        BlockState log = ModBlocks.VELVET_TREE_LOG.get().defaultBlockState();
        int[][] roots = {{-4, -2}, {4, -2}, {-3, 3}, {3, 3}};
        for (int[] r : roots) {
            FirstCryBuildHelper.fillDisk(level, c.offset(r[0], -1, r[1]), 2,
                    ModBlocks.STARDUST_SOIL.get().defaultBlockState());
            FirstCryBuildHelper.set(level, c.offset(r[0], 0, r[1]), ModBlocks.CAT_BED.get().defaultBlockState());
        }
        for (int dy = 0; dy <= 18; dy++) {
            int rad = dy < 8 ? 2 : (dy < 15 ? 2 : 1);
            if (dy > 12) rad = 1;
            for (int dx = -rad; dx <= rad; dx++) {
                for (int dz = -rad; dz <= rad; dz++) {
                    if (dx * dx + dz * dz > rad * rad) continue;
                    boolean channel = dx == 0 && dz == 0 && dy > 0 && dy < 17;
                    if (!channel) {
                        FirstCryBuildHelper.set(level, c.offset(dx, dy, dz), log);
                    }
                }
            }
        }
        for (int[] n : NEON) {
            FirstCryBuildHelper.set(level, c.offset(n[0], n[1], n[2]),
                    ModBlocks.NEON_MUSHROOM.get().defaultBlockState());
        }
        int[][] vines = {{1, 20, 2}, {-1, 22, 2}, {2, 24, 1}, {-2, 21, -1},
                {0, 23, 3}, {1, 19, -2}, {-1, 25, 0}, {2, 22, -2}};
        for (int[] v : vines) {
            int len = 3 + level.random.nextInt(3);
            for (int dy = 0; dy < len; dy++) {
                FirstCryBuildHelper.set(level, c.offset(v[0], v[1] + dy, v[2]),
                        ModBlocks.VELVET_VINE.get().defaultBlockState());
            }
        }
    }

    private static void buildCanopy(ServerLevel level, BlockPos c) {
        BlockState leaf = ModBlocks.VELVET_TREE_LEAVES.get().defaultBlockState();
        for (int dy = 18; dy <= 35; dy++) {
            int r = dy < 26 ? 6 : (dy < 32 ? 5 : 3);
            FirstCryBuildHelper.fillDisk(level, c.offset(0, dy, 0), r, leaf);
        }
    }

    private static void buildTreeHole(ServerLevel level, BlockPos c) {
        BlockPos base = c.below(2);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy < 3; dy++) {
                    FirstCryBuildHelper.set(level, base.offset(dx, dy, dz), Blocks.AIR.defaultBlockState());
                }
            }
        }
        FirstCryBuildHelper.set(level, base, ModBlocks.VELVET_CARPET.get().defaultBlockState());
        FirstCryBuildHelper.set(level, base.above(), ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());
        FirstCryBuildHelper.set(level, c.below(2), ModBlocks.UNDERCAT_TREE_HOLE.get().defaultBlockState());
        int[][] murals = {{-1, 1, -1}, {1, 1, -1}, {-1, 1, 1}, {1, 1, 1}};
        for (int[] m : murals) {
            FirstCryBuildHelper.set(level, base.offset(m[0], m[1], m[2]),
                    ModBlocks.MURAL_FRAGMENT.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, c.offset(0, 1, -2),
                ModBlocks.ANCIENT_STONE_TABLET.get().defaultBlockState());
    }

    private static void buildPlaza(ServerLevel level, BlockPos c) {
        FirstCryBuildHelper.fillDisk(level, c, 12, FirstCryBuildHelper.grass());
        for (int a = 0; a < 8; a++) {
            FirstCryBuildHelper.radialPath(level, c, a * Math.PI / 4, 12, 3, FirstCryBuildHelper.brick());
        }
        for (int w = 0; w < 8; w++) {
            FirstCryBuildHelper.scatterWedgePlants(level, c, 11, w, 5);
            for (int i = 0; i < 3; i++) {
                double ang = w * Math.PI / 4 + i * 0.15;
                int px = (int) (Math.cos(ang) * 9);
                int pz = (int) (Math.sin(ang) * 9);
                FirstCryBuildHelper.set(level, c.offset(px, 0, pz),
                        ModBlocks.COTTON_CANDY_SHRUB.get().defaultBlockState());
            }
        }
        FirstCryBuildHelper.lowerSlabRing(level, c, 6);
        int[][] beds = {{3, 5}, {-4, 4}, {5, -3}, {-3, -5}, {0, 7}, {-6, 0},
                {4, -6}, {-2, 6}, {6, 2}, {-6, -2}};
        for (int[] b : beds) {
            FirstCryBuildHelper.set(level, c.offset(b[0], 0, b[1]), ModBlocks.CAT_BED.get().defaultBlockState());
        }
        for (int i = 0; i < 6; i++) {
            double ang = i * Math.PI / 3 + 0.2;
            int px = (int) (Math.cos(ang) * 5);
            int pz = (int) (Math.sin(ang) * 5);
            FirstCryBuildHelper.set(level, c.offset(px, 0, pz), ModBlocks.TOY_BOX.get().defaultBlockState());
        }
        for (int i = 0; i < 4; i++) {
            double ang = i * Math.PI / 2 + 0.4;
            int px = (int) (Math.cos(ang) * 4);
            int pz = (int) (Math.sin(ang) * 4);
            FirstCryBuildHelper.set(level, c.offset(px, 0, pz), ModBlocks.SCRATCHING_POST.get().defaultBlockState());
        }
        for (int a = 0; a < 16; a++) {
            if (a % 5 != 0) continue;
            double ang = a * Math.PI * 2 / 16;
            int px = (int) Math.round(Math.cos(ang) * 6);
            int pz = (int) Math.round(Math.sin(ang) * 6);
            FirstCryBuildHelper.set(level, c.offset(px, 0, pz),
                    ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState());
        }
        Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (int i = 0; i < dirs.length; i++) {
            FirstCryBuildHelper.tablet(level, c.relative(dirs[i], 11), STORY_LORE[i]);
        }
    }

    private static void buildCanopySecret(ServerLevel level, BlockPos c) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                FirstCryBuildHelper.set(level, c.offset(dx, 25, dz), FirstCryBuildHelper.planks());
            }
        }
        FirstCryBuildHelper.tablet(level, c.offset(0, 26, -2), 26);
        FirstCryBuildHelper.set(level, c.offset(0, 24, 3), ModBlocks.VELVET_VINE.get().defaultBlockState());
    }

    public static void buildBlackMudRuin(ServerLevel level) {
        BlockPos ruin = FirstCryLayout.BLACK_MUD_RUIN;
        FirstCryBuildHelper.hollowBox(level, ruin, 7, 3, 5,
                ModBlocks.REINFORCED_CARDBOARD.get().defaultBlockState(),
                ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState(),
                FirstCryBuildHelper.thatch());
        FirstCryBuildHelper.doorGap(level, ruin.getX() + 3, ruin.getY() + 1, ruin.getZ() + 4,
                Direction.SOUTH, 2, 2);
        FirstCryBuildHelper.set(level, ruin.offset(1, 0, 1), ModBlocks.BLACK_MUD.get().defaultBlockState());
        FirstCryBuildHelper.set(level, ruin.offset(1, 1, 1), ModBlocks.BLACK_MUD.get().defaultBlockState());
        FirstCryBuildHelper.set(level, ruin.offset(5, 1, 1), ModBlocks.SHADOW_CRYSTAL_BLOCK.get().defaultBlockState());
        FirstCryBuildHelper.set(level, ruin.offset(3, 1, 1),
                ModBlocks.ANCIENT_STONE_TABLET.get().defaultBlockState()
                        .setValue(com.cocojenna.block.AncientStoneTabletBlock.LORE, 11));
        FirstCryBuildHelper.lootChest(level, ruin.offset(3, 1, 3), "black_mud_clue");
    }
}
