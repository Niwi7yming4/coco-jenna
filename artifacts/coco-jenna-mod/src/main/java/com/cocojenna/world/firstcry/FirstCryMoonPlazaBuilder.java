package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** 月光祭壇廣場（設計書 3.5）. */
public final class FirstCryMoonPlazaBuilder {

    private FirstCryMoonPlazaBuilder() {}

    public static void build(ServerLevel level) {
        BlockPos c = FirstCryLayout.MOON_PLAZA;
        FirstCryBuildHelper.fillDisk(level, c, 15, ModBlocks.STARDUST_SOIL.get().defaultBlockState());
        buildTotemPattern(level, c);
        for (int tier = 0; tier < 3; tier++) {
            int r = 4 - tier;
            FirstCryBuildHelper.fillDisk(level, c.above(tier), r,
                    ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
        }
        FirstCryBuildHelper.set(level, c.above(2), ModBlocks.FULL_MOON_ALTAR.get().defaultBlockState());
        int[][] lamps = {{-4, 0}, {4, 0}, {0, 4}, {0, -4}};
        for (int[] l : lamps) {
            FirstCryBuildHelper.set(level, c.offset(l[0], 0, l[1]),
                    ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState());
        }
        for (int a = 0; a < 12; a++) {
            double ang = a * Math.PI * 2 / 12;
            int px = (int) Math.round(Math.cos(ang) * 13);
            int pz = (int) Math.round(Math.sin(ang) * 13);
            FirstCryBuildHelper.tablet(level, c.offset(px, 0, pz), 1);
        }
        int[][] guardians = {{-10, -10}, {10, -10}, {-10, 10}, {10, 10}};
        for (int[] g : guardians) {
            FirstCryBuildHelper.set(level, c.offset(g[0], 0, g[1]),
                    ModBlocks.ALTAR_FOUNDATION.get().defaultBlockState());
        }
        for (int i = 0; i < 6; i++) {
            double ang = i * Math.PI / 3;
            int px = (int) (Math.cos(ang) * 8);
            int pz = (int) (Math.sin(ang) * 8);
            FirstCryBuildHelper.set(level, c.offset(px, 0, pz), ModBlocks.CAT_BED.get().defaultBlockState());
        }
        BlockPos pavilion = c.offset(0, 0, 13);
        FirstCryBuildHelper.hollowBox(level, pavilion.offset(-3, 0, 0), 7, 3, 4,
                FirstCryBuildHelper.planks(), FirstCryBuildHelper.brick(), FirstCryBuildHelper.thatch());
        FirstCryBuildHelper.set(level, pavilion.offset(0, 1, 2), ModBlocks.DECREE_PEDESTAL.get().defaultBlockState());
        FirstCryBuildHelper.set(level, pavilion.offset(-1, 1, 2), ModBlocks.BLUEPRINT_TABLE.get().defaultBlockState());
        FirstCryBuildHelper.set(level, pavilion.offset(1, 1, 2), ModBlocks.SEAL_PEDESTAL.get().defaultBlockState());
        buildUndergroundChamber(level, c);
        FirstCryBuildHelper.set(level, FirstCryLayout.MOON_CHAMBER_WALL, ModBlocks.SUSPICIOUS_WALL.get().defaultBlockState());
    }

    private static void buildTotemPattern(Level level, BlockPos c) {
        for (int a = 0; a < 24; a++) {
            double ang = a * Math.PI * 2 / 24;
            int r = 6 + (a % 3);
            int px = (int) Math.round(Math.cos(ang) * r);
            int pz = (int) Math.round(Math.sin(ang) * r);
            BlockState s = a % 3 == 0 ? ModBlocks.SALT_BLOCK.get().defaultBlockState()
                    : ModBlocks.MOONSTONE_BRICK.get().defaultBlockState();
            if (level.getBlockState(c.offset(px, 0, pz)).is(ModBlocks.STARDUST_SOIL.get())) {
                FirstCryBuildHelper.set(level, c.offset(px, 0, pz), s);
            }
        }
    }

    private static void buildUndergroundChamber(ServerLevel level, BlockPos c) {
        BlockPos chamber = FirstCryLayout.MOON_CHAMBER_ALTAR;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = -2; dy <= 1; dy++) {
                    FirstCryBuildHelper.set(level, chamber.offset(dx, dy, dz), Blocks.AIR.defaultBlockState());
                }
            }
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                    FirstCryBuildHelper.set(level, chamber.offset(dx, 0, dz),
                            ModBlocks.MURAL_FRAGMENT.get().defaultBlockState());
                    FirstCryBuildHelper.set(level, chamber.offset(dx, 1, dz),
                            ModBlocks.MURAL_FRAGMENT.get().defaultBlockState());
                }
            }
        }
        FirstCryBuildHelper.set(level, chamber, ModBlocks.MOON_TRIAL_ALTAR.get().defaultBlockState());
        FirstCryBuildHelper.lootChest(level, chamber.above(), "moonlight_mural_reward");
    }
}
