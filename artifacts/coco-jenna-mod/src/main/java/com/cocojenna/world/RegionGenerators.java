package com.cocojenna.world;

import com.cocojenna.exploration.ExplorationMarkers;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** 其餘區域 POI（中央廣場、嚎風峽谷、迷宮、睡眠聖殿）. */
public final class RegionGenerators {

    public static final BlockPos CENTRAL_PLAZA = new BlockPos(256, 64, 0);
    public static final BlockPos HOWLING_GORGE = new BlockPos(512, 70, -512);
    public static final BlockPos LABYRINTH = new BlockPos(-512, 64, -512);
    public static final BlockPos SLEEP_SANCTUARY = new BlockPos(384, 80, -384);

    private RegionGenerators() {}

    public static void ensureAll(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        ensurePlaza(level);
        ensureGorge(level);
        ensureLabyrinth(level);
        ensureSanctuary(level);
    }

    private static void ensurePlaza(ServerLevel level) {
        if (level.getBlockState(CENTRAL_PLAZA).is(ModBlocks.CAT_SCRATCH_BOARD.get())) return;
        int cx = CENTRAL_PLAZA.getX();
        int cy = CENTRAL_PLAZA.getY();
        int cz = CENTRAL_PLAZA.getZ();
        int plazaR = 40;

        for (int x = -plazaR; x <= plazaR; x++) {
            for (int z = -plazaR; z <= plazaR; z++) {
                if (x * x + z * z > plazaR * plazaR) continue;
                BlockState ground = (x + z) % 2 == 0
                        ? ModBlocks.MOONSTONE_BRICK.get().defaultBlockState()
                        : ModBlocks.VELVET_GRASS.get().defaultBlockState();
                set(level, cx + x, cy, cz + z, ground);
            }
        }

        for (int a = 0; a < 5; a++) {
            double ang = a * Math.PI * 2 / 5;
            int px = cx + (int) (Math.cos(ang) * 8);
            int pz = cz + (int) (Math.sin(ang) * 8);
            set(level, px, cy, pz, ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
        }

        MonumentGrowthManager.ensureMonument(level);
        ArchitectureBuilders.buildAlphaObservatory(level, new BlockPos(cx, cy + 12, cz - 32));

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x * x + z * z <= 4) {
                    set(level, cx + x, cy, cz + z, Blocks.WATER.defaultBlockState());
                }
            }
        }
        set(level, cx, cy + 1, cz, ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());

        ArchitectureBuilders.buildPortalGate(level, new BlockPos(cx, cy, cz - 28), Direction.SOUTH, "moon");
        ArchitectureBuilders.buildPortalGate(level, new BlockPos(cx, cy, cz + 28), Direction.NORTH, "velvet");
        ArchitectureBuilders.buildPortalGate(level, new BlockPos(cx - 28, cy, cz), Direction.EAST, "gear");
        ArchitectureBuilders.buildPortalGate(level, new BlockPos(cx + 28, cy, cz), Direction.WEST, "village");
        set(level, cx - 20, cy + 1, cz + 20, ModBlocks.CAT_CORE_ENGINEERING.get().defaultBlockState());

        for (int a = 0; a < 8; a++) {
            double ang = a * Math.PI * 2 / 8;
            int bx = cx + (int) (Math.cos(ang) * 18);
            int bz = cz + (int) (Math.sin(ang) * 18);
            set(level, bx, cy + 1, bz, ModBlocks.VELVET_CARPET.get().defaultBlockState());
            set(level, bx + 1, cy + 1, bz, ModBlocks.VELVET_CARPET.get().defaultBlockState());
        }

        BlackMudBossHelper.trySpawnBoss(level, ModEntities.PLAZA_SENTINEL.get(),
                CENTRAL_PLAZA.above(3));
        BlackMudBossHelper.trySpawnBoss(level, ModEntities.THOUSAND_FACE_STITCHER.get(),
                CENTRAL_PLAZA.above(5));
        ExplorationMarkers.placeRegionHub(level, "central_plaza", CENTRAL_PLAZA);
    }

    private static void ensureGorge(ServerLevel level) {
        BlockPos m = HOWLING_GORGE;
        if (level.getBlockState(m).is(Blocks.DEEPSLATE_BRICKS)) return;
        for (int x = -8; x <= 8; x++) {
            for (int z = -2; z <= 2; z++) {
                set(level, m.getX() + x, m.getY(), m.getZ() + z, Blocks.DEEPSLATE_BRICKS.defaultBlockState());
            }
            set(level, m.getX() + x, m.getY() - 4, m.getZ(), Blocks.AIR.defaultBlockState());
            if (x % 3 == 0) {
                set(level, m.getX() + x, m.getY() + 1, m.getZ(), Blocks.CHAIN.defaultBlockState());
            }
        }
        set(level, m.getX(), m.getY() + 1, m.getZ(), ModBlocks.SALT_CRYSTAL.get().defaultBlockState());
        for (int y = 1; y <= 6; y++) {
            set(level, m.getX() - 8, m.getY() + y, m.getZ() - 2, Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState());
            set(level, m.getX() + 8, m.getY() + y, m.getZ() + 2, Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState());
        }
        BlackMudBossHelper.trySpawnBoss(level, ModEntities.HOWLING_SQUALL.get(), m.above());
        ExplorationMarkers.placeRegionHub(level, "howling_gorge", HOWLING_GORGE);
    }

    private static void ensureLabyrinth(ServerLevel level) {
        BlockPos m = LABYRINTH;
        if (level.getBlockState(m).is(Blocks.MOSSY_COBBLESTONE)) return;
        for (int x = -12; x <= 12; x++) {
            for (int z = -12; z <= 12; z++) {
                boolean wall = (x % 4 == 0 || z % 4 == 0) && (Math.abs(x) + Math.abs(z)) % 3 != 0;
                if (wall) {
                    set(level, m.getX() + x, m.getY(), m.getZ() + z, Blocks.MOSSY_COBBLESTONE.defaultBlockState());
                    if ((x + z) % 5 == 0) {
                        set(level, m.getX() + x, m.getY() + 1, m.getZ() + z, Blocks.MOSSY_COBBLESTONE.defaultBlockState());
                    }
                } else {
                    set(level, m.getX() + x, m.getY(), m.getZ() + z, Blocks.GRASS_BLOCK.defaultBlockState());
                }
            }
        }
        set(level, m.getX(), m.getY() + 1, m.getZ(), Blocks.END_ROD.defaultBlockState());
        BlackMudBossHelper.trySpawnBoss(level, ModEntities.ASHURA_PHANTOM.get(), m.above());
        ExplorationMarkers.placeRegionHub(level, "phantom_maze", LABYRINTH);
    }

    private static void ensureSanctuary(ServerLevel level) {
        BlockPos m = SLEEP_SANCTUARY;
        if (level.getBlockState(m).is(ModBlocks.STARDUST_BRICK.get())) return;
        int cx = m.getX();
        int cy = m.getY();
        int cz = m.getZ();

        fillPlatform(level, m, ModBlocks.STARDUST_BRICK.get().defaultBlockState(), 10);
        for (int y = 1; y <= 12; y++) {
            for (int x = -8; x <= 8; x++) {
                for (int z = -14; z <= 14; z++) {
                    boolean wall = Math.abs(x) == 8 || Math.abs(z) == 14;
                    if (wall) set(level, cx + x, cy + y, cz + z, ModBlocks.WOVEN_WOOL.get().defaultBlockState());
                }
            }
        }
        for (int y = 13; y <= 18; y++) {
            set(level, cx, cy + y, cz - 14, ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
            set(level, cx, cy + y, cz + 14, ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
            set(level, cx - 8, cy + y, cz, ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
            set(level, cx + 8, cy + y, cz, ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
        }
        set(level, cx, cy + 19, cz, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());

        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                if (x * x + z * z <= 16) {
                    set(level, cx + x, cy, cz + z, Blocks.WATER.defaultBlockState());
                }
            }
        }
        set(level, cx, cy + 1, cz, ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());
        set(level, cx, cy - 1, cz, ModBlocks.UNDERCAT_SANCTUARY_POOL.get().defaultBlockState());

        for (int side = -1; side <= 1; side += 2) {
            for (int z = -10; z <= 10; z += 5) {
                set(level, cx + side * 6, cy + 1, cz + z, ModBlocks.VELVET_CARPET.get().defaultBlockState());
                set(level, cx + side * 6, cy + 2, cz + z, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
            }
        }

        BlackMudBossHelper.trySpawnBoss(level, ModEntities.FALLEN_GENERAL.get(), m.above(2));
        ExplorationMarkers.placeRegionHub(level, "sleep_sanctuary", SLEEP_SANCTUARY);
    }

    private static void fillPlatform(Level level, BlockPos center, BlockState state, int r) {
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                if (x * x + z * z <= r * r) {
                    set(level, center.getX() + x, center.getY(), center.getZ() + z, state);
                }
            }
        }
    }

    private static void set(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, 3);
    }

    private static void set(Level level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }
}
