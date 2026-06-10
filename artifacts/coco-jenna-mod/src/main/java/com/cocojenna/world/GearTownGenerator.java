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

/**
 * 齒輪鎮 (Gearpaw Town) — 工業龐克聚落（設計書 9.5）.
 * Located east of First Cry Village in first_cry_plains checkerboard region.
 */
public final class GearTownGenerator {

    public static final int FLOOR_Y = 64;
    public static final BlockPos CENTER = new BlockPos(384, FLOOR_Y, 256);
    private static final BlockPos MARKER = new BlockPos(384, FLOOR_Y, 256);

    private GearTownGenerator() {}

    public static void ensureTown(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            return;
        }
        if (level.getBlockState(MARKER).is(ModBlocks.IRONPAW_FORGE.get())) {
            return;
        }
        placeTown(level);
    }

    private static void placeTown(ServerLevel level) {
        int cx = CENTER.getX();
        int cz = CENTER.getZ();
        int radius = 22;
        BlockState ground = ModBlocks.STARDUST_BRICK.get().defaultBlockState();
        BlockState accent = ModBlocks.SPORE_METAL_BLOCK.get().defaultBlockState();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) {
                    continue;
                }
                BlockPos groundPos = new BlockPos(cx + x, FLOOR_Y, cz + z);
                level.setBlock(groundPos, ground, 2);
                if ((x + z) % 7 == 0) {
                    level.setBlock(groundPos, accent, 2);
                }
            }
        }
        ArchitectureBuilders.fillDisk(level, CENTER, 6, ModBlocks.SPORE_METAL_BLOCK.get().defaultBlockState());

        set(level, cx, FLOOR_Y, cz, ModBlocks.IRONPAW_FORGE.get().defaultBlockState());
        set(level, cx, FLOOR_Y + 1, cz, ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState());
        set(level, cx, FLOOR_Y + 2, cz, Blocks.LIGHTNING_ROD.defaultBlockState());

        set(level, cx - 5, FLOOR_Y, cz, ModBlocks.DISTILLER.get().defaultBlockState());
        set(level, cx + 5, FLOOR_Y, cz, ModBlocks.AROMA_DISTILLER.get().defaultBlockState());
        set(level, cx + 3, FLOOR_Y, cz, ModBlocks.SOCKETING_TABLE.get().defaultBlockState());
        set(level, cx - 9, FLOOR_Y, cz - 5, ModBlocks.RYOKATANA_SHOP_STAND.get().defaultBlockState());
        ProceduralDungeonBuilder.placeLootChest(level, cx - 7, FLOOR_Y + 1, cz - 3,
                new net.minecraft.resources.ResourceLocation("cocojenna", "chests/gear_town"));
        set(level, cx + 9, FLOOR_Y, cz + 5, ModBlocks.CAT_CORE_ENGINEERING.get().defaultBlockState());

        ArchitectureBuilders.buildGearShed(level, cx - 12, FLOOR_Y, cz - 10);
        ArchitectureBuilders.buildGearShed(level, cx + 12, FLOOR_Y, cz - 10);
        ArchitectureBuilders.buildGearShed(level, cx - 12, FLOOR_Y, cz + 10);
        ArchitectureBuilders.buildGearShed(level, cx + 12, FLOOR_Y, cz + 10);

        for (int i = -radius; i <= radius; i++) {
            BlockState pillar = ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState();
            set(level, cx + i, FLOOR_Y, cz - radius, ModBlocks.SPORE_METAL_BLOCK.get().defaultBlockState());
            set(level, cx + i, FLOOR_Y + 1, cz - radius, ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
            set(level, cx + i, FLOOR_Y, cz + radius, ModBlocks.SPORE_METAL_BLOCK.get().defaultBlockState());
            set(level, cx + i, FLOOR_Y + 1, cz + radius, ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
            set(level, cx - radius, FLOOR_Y, cz + i, ModBlocks.SPORE_METAL_BLOCK.get().defaultBlockState());
            set(level, cx - radius, FLOOR_Y + 1, cz + i, ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
            set(level, cx + radius, FLOOR_Y, cz + i, ModBlocks.SPORE_METAL_BLOCK.get().defaultBlockState());
            set(level, cx + radius, FLOOR_Y + 1, cz + i, ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
            if (i % 6 == 0) {
                set(level, cx + i, FLOOR_Y + 2, cz - radius, pillar);
                set(level, cx + i, FLOOR_Y + 2, cz + radius, pillar);
                set(level, cx - radius, FLOOR_Y + 2, cz + i, pillar);
                set(level, cx + radius, FLOOR_Y + 2, cz + i, pillar);
            }
        }

        scatterIndustry(level, cx, cz);
        ArchitectureBuilders.buildPortalGate(level, new BlockPos(cx, FLOOR_Y, cz - 16), Direction.SOUTH, "gear");
        set(level, cx, FLOOR_Y - 1, cz - 14, ModBlocks.UNDERCAT_GEAR_SHAFT.get().defaultBlockState());
        BlackMudBossHelper.trySpawnBoss(level, ModEntities.GEAR_OVERLORD.get(),
                new BlockPos(cx, FLOOR_Y + 1, cz + 12));
        ExplorationMarkers.placeGearTown(level, CENTER);
    }

    private static void scatterIndustry(Level level, int cx, int cz) {
        BlockPos[] pipes = {
                new BlockPos(cx + 7, FLOOR_Y, cz), new BlockPos(cx - 7, FLOOR_Y, cz),
                new BlockPos(cx, FLOOR_Y, cz + 7), new BlockPos(cx, FLOOR_Y, cz - 7)
        };
        for (BlockPos p : pipes) {
            set(level, p.getX(), p.getY(), p.getZ(), ModBlocks.SPORE_METAL_BLOCK.get().defaultBlockState());
            set(level, p.getX(), p.getY() + 1, p.getZ(), Blocks.CHAIN.defaultBlockState());
            set(level, p.getX(), p.getY() + 2, p.getZ(), ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState());
            set(level, p.getX(), p.getY() + 3, p.getZ(), Blocks.LIGHTNING_ROD.defaultBlockState());
        }
        set(level, cx + 4, FLOOR_Y, cz + 4, ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState());
        set(level, cx - 4, FLOOR_Y + 1, cz - 3, ModBlocks.TOY_BOX.get().defaultBlockState());
    }

    private static void set(Level level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }
}
