package com.cocojenna.world;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/** 從 datapack NBT 放置 12 生物群系大型地標；失敗時回退程序生成. */
public final class BiomeDatapackStructurePlacer {

    private static final int CHANCE = 1;
    private static final String PREFIX = "biome_landmarks/";

    private BiomeDatapackStructurePlacer() {}

    public static void decorateChunk(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        long seed = level.getSeed() ^ (chunk.getPos().toLong() * 47L);
        if (Math.floorMod(seed, 100) >= CHANCE) return;

        int x = chunk.getPos().getMinBlockX() + 8 + (int) (Math.abs(seed >> 8) % 8);
        int z = chunk.getPos().getMinBlockZ() + 8 + (int) (Math.abs(seed >> 12) % 8);
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
        Holder<Biome> biome = level.getBiome(surface);
        ResourceLocation id = templateFor(biome);
        if (id == null) return;

        if (!tryPlaceNbt(level, surface, id)) {
            BiomeLargeStructurePlacer.placeLarge(level, biome, surface);
        }
        chunk.setUnsaved(true);
    }

    private static boolean tryPlaceNbt(ServerLevel level, BlockPos origin, ResourceLocation id) {
        try {
            StructureTemplate template = level.getStructureManager().getOrCreate(id);
            Vec3i size = template.getSize();
            if (size.equals(Vec3i.ZERO)) return false;
            StructurePlaceSettings settings = new StructurePlaceSettings();
            template.placeInWorld(level, origin, origin, settings, level.random, Block.UPDATE_ALL);
            return true;
        } catch (Exception e) {
            CocoJennaMod.LOGGER.warn("Failed to place biome structure {}: {}", id, e.toString());
            return false;
        }
    }

    private static ResourceLocation templateFor(Holder<Biome> biome) {
        String name = null;
        if (biome.is(ModBiomes.VELVET_FOREST)) name = "velvet_forest";
        else if (biome.is(ModBiomes.MOON_ALLEY)) name = "moon_alley";
        else if (biome.is(ModBiomes.FIRST_CRY_PLAINS)) name = "first_cry_plains";
        else if (biome.is(ModBiomes.HOWLING_GORGE)) name = "howling_gorge";
        else if (biome.is(ModBiomes.BLIND_WATER_RIVER)) name = "blind_water_river";
        else if (biome.is(ModBiomes.DAWN_HIGHLANDS)) name = "dawn_highlands";
        else if (biome.is(ModBiomes.FORGOTTEN_WASTES)) name = "forgotten_wastes";
        else if (biome.is(ModBiomes.CARDBOARD_SLUMS)) name = "cardboard_slums";
        else if (biome.is(ModBiomes.MOONLIGHT_BEACH)) name = "moonlight_beach";
        else if (biome.is(ModBiomes.RAINBOW_CANYON)) name = "rainbow_canyon";
        else if (biome.is(ModBiomes.CATNIP_HIGHLANDS)) name = "catnip_highlands";
        else if (biome.is(ModBiomes.STARDUST_DESERT)) name = "stardust_desert";
        return name == null ? null : new ResourceLocation(CocoJennaMod.MOD_ID, PREFIX + name);
    }
}
