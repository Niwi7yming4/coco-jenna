package com.cocojenna.world;

import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

/** 12 生物群系專屬微型結構（設計書 貓之國再深化 §3.2–3.3）. */
public final class BiomeStructurePlacer {

    private static final int CHANCE = 3;

    private BiomeStructurePlacer() {}

    public static void decorateChunk(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        long seed = level.getSeed() ^ chunk.getPos().toLong();
        if (Math.floorMod(seed, 100) >= CHANCE) return;

        int x = chunk.getPos().getMinBlockX() + (int) (Math.abs(seed) % 16);
        int z = chunk.getPos().getMinBlockZ() + (int) (Math.abs(seed >> 4) % 16);
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
        Holder<Biome> biome = level.getBiome(surface);
        placeForBiome(level, biome, surface);
        chunk.setUnsaved(true);
    }

    private static void placeForBiome(ServerLevel level, Holder<Biome> biome, BlockPos o) {
        if (biome.is(ModBiomes.VELVET_FOREST)) {
            placeShrine(level, o, Blocks.PINK_WOOL.defaultBlockState());
        } else if (biome.is(ModBiomes.MOONLIGHT_BEACH)) {
            placeDock(level, o);
        } else if (biome.is(ModBiomes.RAINBOW_CANYON)) {
            placeColorAltar(level, o);
        } else if (biome.is(ModBiomes.CATNIP_HIGHLANDS)) {
            ArchitectureBuilders.fillDisk(level, o, 3, Blocks.GRASS_BLOCK.defaultBlockState());
            level.setBlock(o.above(), ModBlocks.CATNIP.get().defaultBlockState(), 2);
        } else if (biome.is(ModBiomes.CARDBOARD_SLUMS)) {
            placeBoxHouse(level, o);
        } else if (biome.is(ModBiomes.STARDUST_DESERT)) {
            level.setBlock(o, Blocks.OBSIDIAN.defaultBlockState(), 2);
            level.setBlock(o.above(), Blocks.END_ROD.defaultBlockState(), 2);
        } else if (biome.is(ModBiomes.FIRST_CRY_PLAINS)) {
            placeStrayTent(level, o);
        } else if (biome.is(ModBiomes.HOWLING_GORGE)) {
            level.setBlock(o.above(), Blocks.IRON_BARS.defaultBlockState(), 2);
            level.setBlock(o.above(2), Blocks.BELL.defaultBlockState(), 2);
        } else if (biome.is(ModBiomes.BLIND_WATER_RIVER)) {
            placeDock(level, o);
            level.setBlock(o.above(), Blocks.LANTERN.defaultBlockState(), 2);
        } else if (biome.is(ModBiomes.DAWN_HIGHLANDS)) {
            level.setBlock(o.above(), Blocks.POPPY.defaultBlockState(), 2);
            level.setBlock(o.above().east(), Blocks.DANDELION.defaultBlockState(), 2);
        } else if (biome.is(ModBiomes.FORGOTTEN_WASTES)) {
            level.setBlock(o, ModBlocks.BLACK_MUD.get().defaultBlockState(), 2);
            level.setBlock(o.above(), Blocks.CANDLE.defaultBlockState(), 2);
        } else if (biome.is(ModBiomes.MOON_ALLEY)) {
            placeMoonWell(level, o);
        }
    }

    private static void placeMoonWell(ServerLevel level, BlockPos o) {
        level.setBlock(o, Blocks.COBBLESTONE.defaultBlockState(), 2);
        level.setBlock(o.above(), Blocks.WATER_CAULDRON.defaultBlockState(), 2);
        level.setBlock(o.above(2), Blocks.LANTERN.defaultBlockState(), 2);
        level.setBlock(o.north(), Blocks.STONE_BRICKS.defaultBlockState(), 2);
        level.setBlock(o.south(), Blocks.STONE_BRICKS.defaultBlockState(), 2);
    }

    private static void placeShrine(ServerLevel level, BlockPos o, BlockState wool) {
        ArchitectureBuilders.fillDisk(level, o, 2, wool);
        level.setBlock(o.above(), ModBlocks.CAT_SCRATCH_BOARD.get().defaultBlockState(), 2);
    }

    private static void placeDock(ServerLevel level, BlockPos o) {
        for (int dx = -2; dx <= 2; dx++) {
            level.setBlock(o.offset(dx, 0, 0), Blocks.OAK_PLANKS.defaultBlockState(), 2);
            level.setBlock(o.offset(dx, -1, 0), Blocks.WATER.defaultBlockState(), 2);
        }
    }

    private static void placeColorAltar(ServerLevel level, BlockPos o) {
        BlockState[] colors = {
                Blocks.RED_TERRACOTTA.defaultBlockState(),
                Blocks.ORANGE_TERRACOTTA.defaultBlockState(),
                Blocks.YELLOW_TERRACOTTA.defaultBlockState()
        };
        for (int i = 0; i < 3; i++) {
            level.setBlock(o.offset(i - 1, 0, 0), colors[i], 2);
        }
        level.setBlock(o.above(), ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState(), 2);
    }

    private static void placeBoxHouse(ServerLevel level, BlockPos o) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy <= 2; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    boolean shell = Math.abs(dx) == 1 || Math.abs(dz) == 1 || dy == 2;
                    if (shell) {
                        level.setBlock(o.offset(dx, dy, dz), Blocks.BROWN_WOOL.defaultBlockState(), 2);
                    }
                }
            }
        }
        level.setBlock(o.offset(0, 0, 1), Blocks.AIR.defaultBlockState(), 2);
    }

    private static void placeStrayTent(ServerLevel level, BlockPos o) {
        for (int dx = -1; dx <= 1; dx++) {
            level.setBlock(o.offset(dx, 1, 0), Blocks.WHITE_WOOL.defaultBlockState(), 2);
        }
        level.setBlock(o, Blocks.CAMPFIRE.defaultBlockState(), 2);
    }
}
