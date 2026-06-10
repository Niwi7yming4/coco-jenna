package com.cocojenna.world;

import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

/** 棒棒糖樹 — 白色樹幹 + 彩色羊毛樹冠（設計書 §2.1 大型植被）. */
public final class LollipopTreePlacer {

    private static final int CHANCE = 2;
    private static final net.minecraft.world.level.block.state.BlockState[] CANOPY = {
            Blocks.RED_WOOL.defaultBlockState(),
            Blocks.ORANGE_WOOL.defaultBlockState(),
            Blocks.YELLOW_WOOL.defaultBlockState(),
            Blocks.LIME_WOOL.defaultBlockState(),
            Blocks.LIGHT_BLUE_WOOL.defaultBlockState(),
            Blocks.PINK_WOOL.defaultBlockState()
    };

    private LollipopTreePlacer() {}

    public static void decorateChunk(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        long seed = level.getSeed() ^ (chunk.getPos().toLong() * 61L);
        if (Math.floorMod(seed, 100) >= CHANCE) return;

        int x = chunk.getPos().getMinBlockX() + (int) (Math.abs(seed) % 16);
        int z = chunk.getPos().getMinBlockZ() + (int) (Math.abs(seed >> 5) % 16);
        BlockPos base = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
        Holder<Biome> biome = level.getBiome(base);
        if (!biome.is(ModBiomes.VELVET_FOREST) && !biome.is(ModBiomes.FIRST_CRY_PLAINS)
                && !biome.is(ModBiomes.DAWN_HIGHLANDS)) {
            return;
        }

        int trunkH = 3 + (int) (Math.abs(seed >> 8) % 3);
        for (int y = 0; y < trunkH; y++) {
            level.setBlock(base.above(y), Blocks.WHITE_CONCRETE.defaultBlockState(), 2);
        }
        var canopyState = CANOPY[(int) (Math.abs(seed >> 12) % CANOPY.length)];
        BlockPos top = base.above(trunkH);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy <= 1; dy++) {
                    if (Math.abs(dx) + Math.abs(dz) + dy > 2) continue;
                    level.setBlock(top.offset(dx, dy, dz), canopyState, 2);
                }
            }
        }
        chunk.setUnsaved(true);
    }
}
