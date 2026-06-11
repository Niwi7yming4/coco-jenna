package com.cocojenna.world;

import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** 設計書 §2.1：各生態域多方塊比例混合（位置 hash 取樣）. */
public final class BiomePaletteMixer {

    private BiomePaletteMixer() {}

    public static BlockState topBlock(Holder<Biome> biome, int wx, int wz, int surfaceY) {
        if (biome.is(ModBiomes.RAINBOW_CANYON)) {
            return com.cocojenna.kingdom.RainbowCanyonManager.stripeBlock(wx, wz, surfaceY);
        }
        int h = hash(wx, wz, surfaceY);
        if (biome.is(ModBiomes.VELVET_FOREST)) {
            return pick(h, 45, ModBlocks.VELVET_GRASS.get().defaultBlockState(),
                    30, Blocks.PINK_TERRACOTTA.defaultBlockState(),
                    15, Blocks.CHERRY_LEAVES.defaultBlockState(),
                    10, Blocks.PINK_CONCRETE.defaultBlockState());
        }
        if (biome.is(ModBiomes.FIRST_CRY_PLAINS)) {
            return pick(h, 40, ModBlocks.VELVET_GRASS.get().defaultBlockState(),
                    35, ModBlocks.STARDUST_SOIL.get().defaultBlockState(),
                    15, Blocks.PINK_TERRACOTTA.defaultBlockState(),
                    10, Blocks.WHITE_CONCRETE.defaultBlockState());
        }
        if (biome.is(ModBiomes.MOONLIGHT_BEACH)) {
            return pick(h, 50, Blocks.LIGHT_BLUE_CONCRETE_POWDER.defaultBlockState(),
                    30, Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState(),
                    10, Blocks.TURTLE_EGG.defaultBlockState(),
                    10, Blocks.GLOWSTONE.defaultBlockState());
        }
        if (biome.is(ModBiomes.CATNIP_HIGHLANDS)) {
            return pick(h, 50, Blocks.GREEN_CONCRETE.defaultBlockState(),
                    30, Blocks.LIME_TERRACOTTA.defaultBlockState(),
                    20, ModBlocks.CATNIP.get().defaultBlockState());
        }
        if (biome.is(ModBiomes.STARDUST_DESERT)) {
            return pick(h, 60, Blocks.BLACK_CONCRETE_POWDER.defaultBlockState(),
                    25, Blocks.GRAY_CONCRETE.defaultBlockState(),
                    15, Blocks.END_STONE.defaultBlockState());
        }
        if (biome.is(ModBiomes.DAWN_HIGHLANDS)) {
            return pick(h, 45, Blocks.GREEN_CONCRETE_POWDER.defaultBlockState(),
                    35, Blocks.LIME_CONCRETE.defaultBlockState(),
                    20, Blocks.GREEN_TERRACOTTA.defaultBlockState());
        }
        if (biome.is(ModBiomes.MOON_ALLEY)) {
            return pick(h, 50, ModBlocks.BLACK_MUD.get().defaultBlockState(),
                    30, Blocks.GRAY_CONCRETE.defaultBlockState(),
                    20, Blocks.STONE_BRICKS.defaultBlockState());
        }
        if (biome.is(ModBiomes.HOWLING_GORGE)) {
            return pick(h, 50, Blocks.COARSE_DIRT.defaultBlockState(),
                    30, Blocks.GRAVEL.defaultBlockState(),
                    20, Blocks.STONE.defaultBlockState());
        }
        if (biome.is(ModBiomes.BLIND_WATER_RIVER)) {
            return pick(h, 55, Blocks.LIGHT_BLUE_CONCRETE_POWDER.defaultBlockState(),
                    35, Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState(),
                    10, Blocks.CLAY.defaultBlockState());
        }
        if (biome.is(ModBiomes.FORGOTTEN_WASTES)) {
            return pick(h, 50, Blocks.BLACK_CONCRETE_POWDER.defaultBlockState(),
                    30, ModBlocks.BLACK_MUD.get().defaultBlockState(),
                    20, Blocks.GRAY_TERRACOTTA.defaultBlockState());
        }
        if (biome.is(ModBiomes.CARDBOARD_SLUMS)) {
            return pick(h, 45, Blocks.BROWN_WOOL.defaultBlockState(),
                    35, Blocks.TERRACOTTA.defaultBlockState(),
                    20, Blocks.COARSE_DIRT.defaultBlockState());
        }
        return Blocks.GRASS_BLOCK.defaultBlockState();
    }

    public static BlockState fillerBlock(Holder<Biome> biome, int wx, int wz) {
        int h = hash(wx, wz, -3);
        if (biome.is(ModBiomes.VELVET_FOREST) || biome.is(ModBiomes.FIRST_CRY_PLAINS)) {
            return pick(h, 55, ModBlocks.STARDUST_SOIL.get().defaultBlockState(),
                    45, Blocks.PINK_TERRACOTTA.defaultBlockState());
        }
        if (biome.is(ModBiomes.MOONLIGHT_BEACH)) {
            return Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState();
        }
        if (biome.is(ModBiomes.CATNIP_HIGHLANDS)) {
            return Blocks.GREEN_TERRACOTTA.defaultBlockState();
        }
        if (biome.is(ModBiomes.STARDUST_DESERT)) {
            return Blocks.BLACK_CONCRETE.defaultBlockState();
        }
        if (biome.is(ModBiomes.DAWN_HIGHLANDS)) {
            return Blocks.GREEN_TERRACOTTA.defaultBlockState();
        }
        if (biome.is(ModBiomes.MOON_ALLEY)) {
            return ModBlocks.BLACK_MUD.get().defaultBlockState();
        }
        if (biome.is(ModBiomes.BLIND_WATER_RIVER)) {
            return Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState();
        }
        if (biome.is(ModBiomes.FORGOTTEN_WASTES)) {
            return Blocks.BLACK_CONCRETE.defaultBlockState();
        }
        if (biome.is(ModBiomes.CARDBOARD_SLUMS)) {
            return Blocks.COARSE_DIRT.defaultBlockState();
        }
        if (biome.is(ModBiomes.RAINBOW_CANYON)) {
            return Blocks.YELLOW_TERRACOTTA.defaultBlockState();
        }
        return Blocks.DIRT.defaultBlockState();
    }

    private static int hash(int wx, int wz, int salt) {
        return Math.floorMod(wx * 31 + wz * 17 + salt * 13, 100);
    }

    private static BlockState pick(int h, int w0, BlockState s0, int w1, BlockState s1,
                                   int w2, BlockState s2, int w3, BlockState s3) {
        if (h < w0) return s0;
        if (h < w0 + w1) return s1;
        if (h < w0 + w1 + w2) return s2;
        return s3;
    }

    private static BlockState pick(int h, int w0, BlockState s0, int w1, BlockState s1, int w2, BlockState s2) {
        if (h < w0) return s0;
        if (h < w0 + w1) return s1;
        return s2;
    }

    private static BlockState pick(int h, int w0, BlockState s0, int w1, BlockState s1) {
        return h < w0 ? s0 : s1;
    }

    public static boolean isModGrass(BlockState state) {
        return state.is(ModBlocks.VELVET_GRASS.get()) || state.is(ModBlocks.STARDUST_SOIL.get());
    }
}
