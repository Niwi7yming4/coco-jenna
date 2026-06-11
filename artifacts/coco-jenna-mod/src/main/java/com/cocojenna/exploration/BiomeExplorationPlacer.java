package com.cocojenna.exploration;

import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;

/** 12 生態域首次生成時放置傳說石碑與怪貓（設計書 貓之國再深化 §3）. */
public final class BiomeExplorationPlacer {

    private BiomeExplorationPlacer() {}

    /** @return true 若本 chunk 剛完成生態域首次探索 seed（微 POI 應降權或跳過） */
    public static boolean trySeedChunk(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return false;
        int cx = chunk.getPos().getMinBlockX() + 8;
        int cz = chunk.getPos().getMinBlockZ() + 8;
        int cy = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG, cx, cz);
        BlockPos center = new BlockPos(cx, cy, cz);
        Holder<Biome> biome = level.getBiome(center);
        if (!biome.is(ModTags.CAT_KINGDOM_BIOMES)) return false;

        BiomeExplorationSavedData data = BiomeExplorationSavedData.get(level);
        String key = biome.unwrapKey().map(k -> k.location().getPath()).orElse("");
        if (key.isEmpty() || data.isSeeded(key)) return false;
        data.markSeeded(key);
        placeForBiome(level, biome, center);
        return true;
    }

    private static void placeForBiome(ServerLevel level, Holder<Biome> biome, BlockPos center) {
        if (biome.is(ModBiomes.VELVET_FOREST)) {
            ExplorationMarkers.placeVelvetForest(level, center);
        } else if (biome.is(ModBiomes.MOON_ALLEY)) {
            ExplorationMarkers.placeMoonAlley(level, center);
        } else if (biome.is(ModBiomes.FIRST_CRY_PLAINS)) {
            ExplorationMarkers.placeFirstCryPlains(level, center);
        } else if (biome.is(ModBiomes.HOWLING_GORGE)) {
            ExplorationMarkers.placeHowlingGorge(level, center);
        } else if (biome.is(ModBiomes.BLIND_WATER_RIVER)) {
            ExplorationMarkers.placeBlindWaterRiver(level, center);
        } else if (biome.is(ModBiomes.DAWN_HIGHLANDS)) {
            ExplorationMarkers.placeDawnHighlands(level, center);
        } else if (biome.is(ModBiomes.FORGOTTEN_WASTES)) {
            ExplorationMarkers.placeForgottenWastes(level, center);
        } else if (biome.is(ModBiomes.CARDBOARD_SLUMS)) {
            ExplorationMarkers.placeCardboardSlums(level, center);
        } else if (biome.is(ModBiomes.MOONLIGHT_BEACH)) {
            ExplorationMarkers.placeMoonlightBeach(level, center);
        } else if (biome.is(ModBiomes.RAINBOW_CANYON)) {
            ExplorationMarkers.placeRainbowCanyon(level, center);
        } else if (biome.is(ModBiomes.CATNIP_HIGHLANDS)) {
            ExplorationMarkers.placeCatnipHighlands(level, center);
        } else if (biome.is(ModBiomes.STARDUST_DESERT)) {
            ExplorationMarkers.placeStardustDesert(level, center);
        }
    }
}
