package com.cocojenna.world;

import com.cocojenna.exploration.ExplorationMarkers;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 無明港 (Port of the Blind) — 盲水畔港口（設計書 9.5）.
 * Located west of the kingdom origin in blind_water_river biome area.
 */
public final class BlindPortGenerator {

    public static final int FLOOR_Y = 63;
    public static final BlockPos CENTER = new BlockPos(-384, FLOOR_Y, 256);
    private static final BlockPos MARKER = new BlockPos(-384, FLOOR_Y, 256);

    private BlindPortGenerator() {}

    public static void ensurePort(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            return;
        }
        if (level.getBlockState(MARKER).is(Blocks.DARK_OAK_PLANKS)) {
            return;
        }
        placePort(level);
    }

    private static void placePort(ServerLevel level) {
        int cx = CENTER.getX();
        int cz = CENTER.getZ();

        // Dock platform over blind water
        for (int x = -18; x <= 18; x++) {
            for (int z = -12; z <= 12; z++) {
                set(level, cx + x, FLOOR_Y, cz + z, Blocks.DARK_OAK_PLANKS.defaultBlockState());
            }
        }

        // Water basin south
        for (int x = -14; x <= 14; x++) {
            for (int z = 14; z <= 22; z++) {
                for (int y = FLOOR_Y; y >= FLOOR_Y - 2; y--) {
                    set(level, cx + x, y, cz + z, Blocks.WATER.defaultBlockState());
                }
            }
        }

        // Market stalls
        buildStall(level, cx - 8, cz - 4);
        buildStall(level, cx + 8, cz - 4);
        buildStall(level, cx, cz + 2);

        // Lantern posts
        for (int dx = -12; dx <= 12; dx += 6) {
            set(level, cx + dx, FLOOR_Y + 1, cz - 8, Blocks.CHAIN.defaultBlockState());
            set(level, cx + dx, FLOOR_Y + 2, cz - 8, Blocks.SEA_LANTERN.defaultBlockState());
        }

        set(level, cx, FLOOR_Y, cz - 6, ModBlocks.FOOD_BOWL.get().defaultBlockState());
        set(level, cx - 3, FLOOR_Y, cz, ModBlocks.DISTILLER.get().defaultBlockState());
        set(level, cx + 6, FLOOR_Y, cz - 2, ModBlocks.RYOKATANA_SHOP_STAND.get().defaultBlockState());
        set(level, cx - 10, FLOOR_Y, cz + 4, Blocks.CHEST.defaultBlockState());
        BlockEntity chestBe = level.getBlockEntity(new BlockPos(cx - 10, FLOOR_Y, cz + 4));
        if (chestBe instanceof RandomizableContainerBlockEntity chest) {
            chest.setLootTable(new ResourceLocation("cocojenna", "chests/blind_port"), level.random.nextLong());
        }
        set(level, cx + 2, FLOOR_Y - 1, cz + 8, ModBlocks.BLACK_MUD.get().defaultBlockState());
        set(level, cx - 6, FLOOR_Y, cz + 18, ModBlocks.UNDERCAT_BLIND_RIFT.get().defaultBlockState());

        // Moored boats (oak stairs as bows)
        set(level, cx - 5, FLOOR_Y + 1, cz + 16, Blocks.OAK_STAIRS.defaultBlockState());
        set(level, cx + 5, FLOOR_Y + 1, cz + 16, Blocks.OAK_STAIRS.defaultBlockState());

        set(level, cx, FLOOR_Y + 1, cz - 12, ModBlocks.MEMORY_LIGHTHOUSE.get().defaultBlockState());
        spawnPortNpcs(level, cx, cz);
        ExplorationMarkers.placeBlindPort(level, CENTER);
    }

    private static void spawnPortNpcs(ServerLevel level, int cx, int cz) {
        spawnNpc(level, ModEntities.WHITE_GLOVE.get(), cx - 12, cz - 6);
        spawnNpc(level, ModEntities.CHESHIRE.get(), cx + 10, cz + 6);
        BlackMudBossHelper.trySpawnBoss(level, ModEntities.BLIND_WATER_LORD.get(),
                new BlockPos(cx, FLOOR_Y + 1, cz + 10));
    }

    private static void spawnNpc(ServerLevel level, net.minecraft.world.entity.EntityType<?> type, int x, int z) {
        var npc = type.create(level);
        if (npc == null) return;
        npc.moveTo(x + 0.5, FLOOR_Y + 1, z + 0.5, 0, 0);
        if (npc instanceof net.minecraft.world.entity.Mob mob) {
            mob.setPersistenceRequired();
        }
        level.addFreshEntity(npc);
    }

    private static void buildStall(Level level, int cx, int cz) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                set(level, cx + x, FLOOR_Y, cz + z, Blocks.SPRUCE_PLANKS.defaultBlockState());
            }
        }
        set(level, cx, FLOOR_Y + 1, cz, Blocks.PURPLE_BANNER.defaultBlockState());
        set(level, cx, FLOOR_Y + 2, cz, Blocks.PURPLE_WOOL.defaultBlockState());
    }

    private static void set(Level level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }
}
