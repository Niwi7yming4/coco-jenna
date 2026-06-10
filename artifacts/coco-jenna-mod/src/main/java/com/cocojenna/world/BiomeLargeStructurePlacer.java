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

/** 12 生物群系大型地標（程序生成，補充微型結構）. */
public final class BiomeLargeStructurePlacer {

    private static final int CHANCE = 1;

    private BiomeLargeStructurePlacer() {}

    public static void decorateChunk(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        long seed = level.getSeed() ^ (chunk.getPos().toLong() * 31L);
        if (Math.floorMod(seed, 100) >= CHANCE) return;

        int x = chunk.getPos().getMinBlockX() + 8 + (int) (Math.abs(seed >> 8) % 8);
        int z = chunk.getPos().getMinBlockZ() + 8 + (int) (Math.abs(seed >> 12) % 8);
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
        Holder<Biome> biome = level.getBiome(surface);
        placeLarge(level, biome, surface);
        chunk.setUnsaved(true);
    }

    public static void placeLarge(ServerLevel level, Holder<Biome> biome, BlockPos o) {
        if (biome.is(ModBiomes.VELVET_FOREST)) {
            placeTower(level, o, Blocks.PINK_TERRACOTTA.defaultBlockState(), 12);
        } else if (biome.is(ModBiomes.MOONLIGHT_BEACH)) {
            placePier(level, o, 10);
        } else if (biome.is(ModBiomes.RAINBOW_CANYON)) {
            placeArch(level, o);
        } else if (biome.is(ModBiomes.CATNIP_HIGHLANDS)) {
            placeTower(level, o, Blocks.MOSS_BLOCK.defaultBlockState(), 10);
            level.setBlock(o.above(10), ModBlocks.CATNIP.get().defaultBlockState(), 2);
        } else if (biome.is(ModBiomes.CARDBOARD_SLUMS)) {
            placeBoxVillage(level, o);
        } else if (biome.is(ModBiomes.STARDUST_DESERT)) {
            placeObelisk(level, o);
        } else if (biome.is(ModBiomes.FIRST_CRY_PLAINS)) {
            placeCamp(level, o, 6);
        } else if (biome.is(ModBiomes.HOWLING_GORGE)) {
            placeBellTower(level, o);
        } else if (biome.is(ModBiomes.BLIND_WATER_RIVER)) {
            placePier(level, o, 8);
            level.setBlock(o.above(), Blocks.SEA_LANTERN.defaultBlockState(), 2);
        } else if (biome.is(ModBiomes.DAWN_HIGHLANDS)) {
            placeGarden(level, o);
        } else if (biome.is(ModBiomes.FORGOTTEN_WASTES)) {
            placeMudShrine(level, o);
        } else if (biome.is(ModBiomes.MOON_ALLEY)) {
            placeMoonGate(level, o);
        }
    }

    private static void placeTower(ServerLevel level, BlockPos o, BlockState wall, int height) {
        for (int dy = 1; dy <= height; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    boolean edge = Math.abs(dx) == 1 || Math.abs(dz) == 1 || dy == height;
                    if (edge) {
                        level.setBlock(o.offset(dx, dy, dz), wall, 2);
                    }
                }
            }
        }
    }

    private static void placePier(ServerLevel level, BlockPos o, int len) {
        for (int i = 0; i < len; i++) {
            level.setBlock(o.offset(i, 0, 0), Blocks.OAK_PLANKS.defaultBlockState(), 2);
            level.setBlock(o.offset(i, 1, 0), Blocks.OAK_FENCE.defaultBlockState(), 2);
            level.setBlock(o.offset(i, -1, 0), Blocks.WATER.defaultBlockState(), 2);
        }
    }

    private static void placeArch(ServerLevel level, BlockPos o) {
        BlockState[] colors = {
                Blocks.RED_TERRACOTTA.defaultBlockState(),
                Blocks.ORANGE_TERRACOTTA.defaultBlockState(),
                Blocks.YELLOW_TERRACOTTA.defaultBlockState(),
                Blocks.LIME_TERRACOTTA.defaultBlockState(),
                Blocks.CYAN_TERRACOTTA.defaultBlockState()
        };
        for (int i = 0; i < 5; i++) {
            level.setBlock(o.offset(i - 2, 0, 0), colors[i], 2);
            level.setBlock(o.offset(i - 2, 3, 0), colors[i], 2);
            if (i == 0 || i == 4) {
                level.setBlock(o.offset(i - 2, 1, 0), colors[i], 2);
                level.setBlock(o.offset(i - 2, 2, 0), colors[i], 2);
            }
        }
        level.setBlock(o.above(4), ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState(), 2);
    }

    private static void placeBoxVillage(ServerLevel level, BlockPos o) {
        for (int hx = -2; hx <= 2; hx += 2) {
            for (int hz = -2; hz <= 2; hz += 2) {
                placeBoxHouse(level, o.offset(hx, 0, hz));
            }
        }
    }

    private static void placeBoxHouse(ServerLevel level, BlockPos o) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy <= 3; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (Math.abs(dx) == 1 || Math.abs(dz) == 1 || dy == 3) {
                        level.setBlock(o.offset(dx, dy, dz), Blocks.BROWN_WOOL.defaultBlockState(), 2);
                    }
                }
            }
        }
        level.setBlock(o.offset(0, 0, 1), Blocks.AIR.defaultBlockState(), 2);
    }

    private static void placeObelisk(ServerLevel level, BlockPos o) {
        for (int dy = 1; dy <= 14; dy++) {
            level.setBlock(o.above(dy), Blocks.OBSIDIAN.defaultBlockState(), 2);
        }
        level.setBlock(o.above(15), Blocks.END_ROD.defaultBlockState(), 2);
        ArchitectureBuilders.fillDisk(level, o, 3, Blocks.SANDSTONE.defaultBlockState());
    }

    private static void placeCamp(ServerLevel level, BlockPos o, int tents) {
        for (int i = 0; i < tents; i++) {
            BlockPos t = o.offset(i * 4 - tents * 2, 0, 0);
            for (int dx = -1; dx <= 1; dx++) {
                level.setBlock(t.offset(dx, 1, 0), Blocks.WHITE_WOOL.defaultBlockState(), 2);
            }
            level.setBlock(t, Blocks.CAMPFIRE.defaultBlockState(), 2);
        }
    }

    private static void placeBellTower(ServerLevel level, BlockPos o) {
        placeTower(level, o, Blocks.STONE_BRICKS.defaultBlockState(), 14);
        level.setBlock(o.above(15), Blocks.BELL.defaultBlockState(), 2);
    }

    private static void placeGarden(ServerLevel level, BlockPos o) {
        ArchitectureBuilders.fillDisk(level, o, 4, Blocks.GRASS_BLOCK.defaultBlockState());
        for (int i = -3; i <= 3; i++) {
            level.setBlock(o.offset(i, 1, 0), Blocks.POPPY.defaultBlockState(), 2);
            level.setBlock(o.offset(0, 1, i), Blocks.DANDELION.defaultBlockState(), 2);
        }
    }

    private static void placeMudShrine(ServerLevel level, BlockPos o) {
        ArchitectureBuilders.fillDisk(level, o, 4, ModBlocks.BLACK_MUD.get().defaultBlockState());
        placeTower(level, o, ModBlocks.BLACK_MUD.get().defaultBlockState(), 8);
        level.setBlock(o.above(9), Blocks.CANDLE.defaultBlockState(), 2);
    }

    private static void placeMoonGate(ServerLevel level, BlockPos o) {
        for (int dy = 0; dy <= 5; dy++) {
            level.setBlock(o.offset(-2, dy, 0), Blocks.STONE_BRICKS.defaultBlockState(), 2);
            level.setBlock(o.offset(2, dy, 0), Blocks.STONE_BRICKS.defaultBlockState(), 2);
        }
        for (int dx = -1; dx <= 1; dx++) {
            level.setBlock(o.offset(dx, 5, 0), Blocks.STONE_BRICKS.defaultBlockState(), 2);
        }
        level.setBlock(o, Blocks.WATER.defaultBlockState(), 2);
        level.setBlock(o.above(), ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState(), 2);
    }
}
