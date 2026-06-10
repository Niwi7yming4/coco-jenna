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

/** 中型地標 NBT（貓村莊、九命神社、王宮翼等，設計書 §3.4）. */
public final class BiomeMediumStructurePlacer {

    private static final int CHANCE = 2;
    private static final String PREFIX = "biome_landmarks/medium/";

    private BiomeMediumStructurePlacer() {}

    public static void decorateChunk(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        long seed = level.getSeed() ^ (chunk.getPos().toLong() * 53L);
        if (Math.floorMod(seed, 100) >= CHANCE) return;

        int x = chunk.getPos().getMinBlockX() + 4 + (int) (Math.abs(seed >> 6) % 8);
        int z = chunk.getPos().getMinBlockZ() + 4 + (int) (Math.abs(seed >> 10) % 8);
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
        Holder<Biome> biome = level.getBiome(surface);
        ResourceLocation id = templateFor(biome, seed);
        if (id == null) return;

        if (!tryPlaceNbt(level, surface, id)) {
            placeProceduralFallback(level, biome, surface, id.getPath());
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
            CocoJennaMod.LOGGER.warn("Failed to place medium structure {}: {}", id, e.toString());
            return false;
        }
    }

    private static ResourceLocation templateFor(Holder<Biome> biome, long seed) {
        String name;
        if (biome.is(ModBiomes.VELVET_FOREST) || biome.is(ModBiomes.FIRST_CRY_PLAINS)) {
            name = "cat_village";
        } else if (biome.is(ModBiomes.DAWN_HIGHLANDS) || biome.is(ModBiomes.MOONLIGHT_BEACH)) {
            name = "velvet_palace_wing";
        } else if (biome.is(ModBiomes.CATNIP_HIGHLANDS) || biome.is(ModBiomes.FORGOTTEN_WASTES)) {
            name = "nine_lives_shrine";
        } else if (biome.is(ModBiomes.CARDBOARD_SLUMS)) {
            name = "cat_village";
        } else if (biome.is(ModBiomes.RAINBOW_CANYON)) {
            name = "nine_lives_shrine";
        } else if (biome.is(ModBiomes.MOON_ALLEY)) {
            name = "velvet_palace_wing";
        } else if (biome.is(ModBiomes.HOWLING_GORGE) || biome.is(ModBiomes.BLIND_WATER_RIVER)) {
            name = Math.floorMod(seed, 2) == 0 ? "cat_village" : "nine_lives_shrine";
        } else if (biome.is(ModBiomes.STARDUST_DESERT)) {
            name = "nine_lives_shrine";
        } else {
            return null;
        }
        return new ResourceLocation(CocoJennaMod.MOD_ID, PREFIX + name);
    }

    private static void placeProceduralFallback(ServerLevel level, Holder<Biome> biome, BlockPos o, String path) {
        if (path.endsWith("cat_village")) {
            BiomeLargeStructurePlacer.placeLarge(level, biome, o);
        } else if (path.endsWith("nine_lives_shrine")) {
            for (int i = 0; i < 9; i++) {
                int dx = (i % 3) - 1;
                int dz = (i / 3) - 1;
                level.setBlock(o.offset(dx * 2, 1, dz * 2),
                        net.minecraft.world.level.block.Blocks.CANDLE.defaultBlockState(), 2);
            }
            level.setBlock(o.above(2), com.cocojenna.init.ModBlocks.CAT_SCRATCH_BOARD.get().defaultBlockState(), 2);
        } else {
            BiomeLargeStructurePlacer.placeLarge(level, biome, o);
        }
    }
}
