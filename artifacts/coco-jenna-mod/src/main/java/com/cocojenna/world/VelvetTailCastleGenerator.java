package com.cocojenna.world;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** 絨尾王座城堡 — 黎明高地童話宮殿（設計書第四章）. */
public final class VelvetTailCastleGenerator {

    public static final BlockPos CENTER = new BlockPos(448, 72, -448);
    private static final BlockPos MARKER = CENTER;

    private VelvetTailCastleGenerator() {}

    public static void ensureCastle(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (level.getBlockState(MARKER).is(ModBlocks.STARDUST_BRICK.get())) return;
        placeCastle(level);
    }

    private static void placeCastle(ServerLevel level) {
        int cx = CENTER.getX();
        int cy = CENTER.getY();
        int cz = CENTER.getZ();
        BlockState wall = ModBlocks.STARDUST_BRICK.get().defaultBlockState();
        BlockState velvet = ModBlocks.VELVET_VINE.get().defaultBlockState();
        BlockState floor = ModBlocks.MOONSTONE_BRICK.get().defaultBlockState();

        for (int x = -22; x <= 22; x++) {
            for (int z = -28; z <= 28; z++) {
                if (Math.abs(x) <= 20 && Math.abs(z) <= 26) {
                    set(level, cx + x, cy - 1, cz + z, ModBlocks.VELVET_GRASS.get().defaultBlockState());
                }
            }
        }

        for (int y = 0; y < 20; y++) {
            for (int x = -18; x <= 18; x++) {
                for (int z = -24; z <= 24; z++) {
                    boolean edge = Math.abs(x) == 18 || Math.abs(z) == 24;
                    if (edge) set(level, cx + x, cy + y, cz + z, wall);
                    if (edge && y % 4 == 0) set(level, cx + x, cy + y, cz + z, velvet);
                }
            }
        }

        for (int x = -14; x <= 14; x++) {
            for (int z = -18; z <= 18; z++) {
                set(level, cx + x, cy, cz + z, floor);
            }
        }

        for (int y = 1; y <= 14; y++) {
            for (int x = -13; x <= 13; x++) {
                for (int z = -17; z <= 17; z++) {
                    boolean edge = Math.abs(x) == 13 || Math.abs(z) == 17;
                    if (edge && y < 14) set(level, cx + x, cy + y, cz + z, wall);
                }
            }
        }

        placeThroneHall(level, cx, cy, cz, wall, floor);
        placeTwinBedroom(level, cx, cy, cz);
        placeLibrary(level, cx, cy, cz, wall);

        for (int y = 1; y <= 10; y++) {
            set(level, cx - 16, cy + y, cz, wall);
            set(level, cx + 16, cy + y, cz, wall);
            set(level, cx, cy + y, cz - 20, wall);
            set(level, cx, cy + y, cz + 20, wall);
        }
        for (int y = 11; y <= 14; y++) {
            set(level, cx, cy + y, cz, ModBlocks.WOVEN_WOOL.get().defaultBlockState());
        }
        set(level, cx, cy + 15, cz, ModBlocks.MEMORY_MONUMENT_TOP.get().defaultBlockState());

        for (int x = -8; x <= 8; x++) {
            set(level, cx + x, cy, cz + 24, Blocks.OAK_PLANKS.defaultBlockState());
        }
        for (int x = -10; x <= 10; x++) {
            for (int z = 26; z <= 32; z++) {
                if ((x + z) % 2 == 0) {
                    set(level, cx + x, cy, cz + z, ModBlocks.HIBISCUS_FLOWER.get().defaultBlockState());
                }
            }
        }

        for (int x = -12; x <= 12; x += 6) {
            for (int z = -14; z <= 14; z += 7) {
                ArchitectureBuilders.buildYarnHouse(level, cx + x, cy + 8, cz + z,
                        net.minecraft.world.level.material.MapColor.COLOR_LIGHT_BLUE);
            }
        }
        set(level, cx, cy + 1, cz + 24, Blocks.AIR.defaultBlockState());
        set(level, cx, cy + 2, cz + 24, Blocks.AIR.defaultBlockState());
        set(level, cx + 1, cy + 1, cz + 24, Blocks.AIR.defaultBlockState());
        set(level, cx + 1, cy + 2, cz + 24, Blocks.AIR.defaultBlockState());
    }

    private static void placeThroneHall(ServerLevel level, int cx, int cy, int cz,
            BlockState wall, BlockState floor) {
        for (int z = -6; z <= 2; z++) {
            set(level, cx, cy, cz + z, floor);
        }
        for (int x = -2; x <= 2; x++) {
            set(level, cx + x, cy + 1, cz - 8, ModBlocks.VELVET_CARPET.get().defaultBlockState());
        }
        set(level, cx - 3, cy + 1, cz - 8, ModBlocks.CAT_BED.get().defaultBlockState());
        set(level, cx + 3, cy + 1, cz - 8, ModBlocks.CAT_BED.get().defaultBlockState());
        set(level, cx, cy + 1, cz - 8, ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());
        for (int side = -1; side <= 1; side += 2) {
            for (int y = 2; y <= 5; y++) {
                set(level, cx + side * 12, cy + y, cz - 4, Blocks.PURPLE_WOOL.defaultBlockState());
                set(level, cx + side * 12, cy + y, cz, Blocks.ORANGE_WOOL.defaultBlockState());
            }
        }
        for (int x = -10; x <= 10; x += 5) {
            set(level, cx + x, cy + 1, cz + 2, ModBlocks.VELVET_CARPET.get().defaultBlockState());
        }
        set(level, cx, cy + 1, cz - 10, wall);
    }

    private static void placeTwinBedroom(ServerLevel level, int cx, int cy, int cz) {
        set(level, cx - 6, cy + 5, cz, ModBlocks.CAT_BED.get().defaultBlockState());
        set(level, cx + 6, cy + 5, cz, ModBlocks.CAT_BED.get().defaultBlockState());
        set(level, cx - 5, cy + 5, cz - 2, Blocks.BOOKSHELF.defaultBlockState());
        set(level, cx + 5, cy + 5, cz + 2, ModBlocks.SCRATCHING_POST.get().defaultBlockState());
        set(level, cx, cy + 5, cz, ModBlocks.VELVET_CARPET.get().defaultBlockState());
    }

    private static void placeLibrary(ServerLevel level, int cx, int cy, int cz, BlockState wall) {
        for (int y = 9; y <= 12; y++) {
            for (int x = -8; x <= 8; x++) {
                set(level, cx + x, cy + y, cz - 14, Blocks.BOOKSHELF.defaultBlockState());
                set(level, cx + x, cy + y, cz + 14, Blocks.BOOKSHELF.defaultBlockState());
            }
        }
        set(level, cx, cy + 10, cz, ModBlocks.PICTURE_BOOK_STAND.get().defaultBlockState());
        set(level, cx + 4, cy + 10, cz, ModBlocks.SEAL_PEDESTAL.get().defaultBlockState());
        set(level, cx - 4, cy + 10, cz, ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState());
        set(level, cx, cy + 11, cz, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
    }

    private static void set(Level level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }
}
