package com.cocojenna.world;

import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Custom biomes use overworld noise without matching surface rules, so columns default to stone.
 * This one-time pass per chunk applies biome-appropriate topsoil in the cat kingdom.
 */
public final class CatKingdomTerrainDecorator {

    private CatKingdomTerrainDecorator() {}

    public static void decorateChunk(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            return;
        }

        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();

        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int wx = minX + lx;
                int wz = minZ + lz;
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, wx, wz);
                BlockPos surfacePos = new BlockPos(wx, surfaceY, wz);
                Holder<Biome> biome = level.getBiome(surfacePos);

                BlockState top = BiomePaletteMixer.topBlock(biome, wx, wz, surfaceY);
                BlockState filler = BiomePaletteMixer.fillerBlock(biome, wx, wz);

                for (int dy = 0; dy <= 4; dy++) {
                    int y = surfaceY - dy;
                    if (y < level.getMinBuildHeight() + 1) {
                        break;
                    }
                    BlockPos local = new BlockPos(lx, y, lz);
                    BlockState current = chunk.getBlockState(local);
                    if (!isReplaceableStone(current)) {
                        continue;
                    }
                    if (dy == 0) {
                        chunk.setBlockState(local, top, false);
                    } else if (dy <= 3) {
                        chunk.setBlockState(local, filler, false);
                    } else {
                        chunk.setBlockState(local, Blocks.STONE.defaultBlockState(), false);
                    }
                }

                if (biome.is(ModBiomes.BLIND_WATER_RIVER) && surfaceY < 66) {
                    for (int y = surfaceY + 1; y <= 64; y++) {
                        BlockPos local = new BlockPos(lx, y, lz);
                        if (chunk.getBlockState(local).isAir()) {
                            chunk.setBlockState(local, Blocks.WATER.defaultBlockState(), false);
                        }
                    }
                }
            }
        }
        com.cocojenna.exploration.BiomeExplorationPlacer.trySeedChunk(level, chunk);
        chunk.setUnsaved(true);
    }

    private static boolean isReplaceableStone(BlockState state) {
        return state.is(Blocks.STONE) || state.is(Blocks.GRANITE) || state.is(Blocks.DIORITE)
                || state.is(Blocks.ANDESITE) || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.TUFF) || state.is(Blocks.GRAVEL);
    }

}
