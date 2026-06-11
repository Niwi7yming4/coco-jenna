package com.cocojenna.world;

import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
        boolean biomeFirstSeed = com.cocojenna.exploration.BiomeExplorationPlacer.trySeedChunk(level, chunk);
        if (!biomeFirstSeed) {
            KingdomMicroMarkers.decorateChunk(level, chunk);
        }
        tryScatterVegetation(level, chunk);
        chunk.setUnsaved(true);
    }

    /** 低機率地表植被 patch（neon mushroom 等），不與生態域 seed chunk 的微 POI 重疊. */
    private static void tryScatterVegetation(ServerLevel level, LevelChunk chunk) {
        long seed = level.getSeed() ^ chunk.getPos().toLong() ^ 0x5EEDBEEFL;
        RandomSource random = RandomSource.create(seed);
        if (random.nextInt(100) >= 4) return;

        int lx = random.nextInt(16);
        int lz = random.nextInt(16);
        int wx = chunk.getPos().getMinBlockX() + lx;
        int wz = chunk.getPos().getMinBlockZ() + lz;
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, wx, wz);
        BlockPos surface = new BlockPos(wx, surfaceY, wz);
        BlockState top = chunk.getBlockState(new BlockPos(lx, surfaceY, lz));
        if (!top.is(Blocks.GRASS_BLOCK) && !BiomePaletteMixer.isModGrass(top)) return;
        if (!level.getBlockState(surface.above()).isAir()) return;

        Holder<Biome> biome = level.getBiome(surface);
        BlockState plant = pickVegetation(biome, random);
        if (plant != null) {
            level.setBlock(surface.above(), plant, 2);
        }
    }

    private static BlockState pickVegetation(Holder<Biome> biome, RandomSource random) {
        if (biome.is(ModBiomes.CARDBOARD_SLUMS) || biome.is(ModBiomes.MOON_ALLEY)) {
            return random.nextBoolean() ? ModBlocks.NEON_MUSHROOM.get().defaultBlockState() : null;
        }
        if (biome.is(ModBiomes.VELVET_FOREST) || biome.is(ModBiomes.FIRST_CRY_PLAINS)) {
            return random.nextInt(3) == 0 ? ModBlocks.CATNIP.get().defaultBlockState() : Blocks.FERN.defaultBlockState();
        }
        if (biome.is(ModBiomes.CATNIP_HIGHLANDS)) {
            return ModBlocks.CATNIP.get().defaultBlockState();
        }
        return random.nextInt(5) == 0 ? ModBlocks.NEON_MUSHROOM.get().defaultBlockState() : Blocks.GRASS.defaultBlockState();
    }

    private static boolean isReplaceableStone(BlockState state) {
        return state.is(Blocks.STONE) || state.is(Blocks.GRANITE) || state.is(Blocks.DIORITE)
                || state.is(Blocks.ANDESITE) || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.TUFF) || state.is(Blocks.GRAVEL);
    }

}
