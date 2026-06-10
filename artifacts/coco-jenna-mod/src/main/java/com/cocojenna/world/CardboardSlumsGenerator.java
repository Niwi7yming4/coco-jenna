package com.cocojenna.world;

import com.cocojenna.entity.UndercatHubNpcEntity;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;

/** 紙箱貧民窟 POI. */
public final class CardboardSlumsGenerator {

    private CardboardSlumsGenerator() {}

    public static void build(ServerLevel level, BlockPos center) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        fillPad(level, cx, cy, cz, 28, ModBlocks.CARDBOARD_BLOCK.get());
        buildCastle(level, cx, cy, cz);
        buildTapeTemple(level, cx - 18, cy, cz + 12);
        buildOrphanage(level, cx + 16, cy, cz - 14);
        buildMazeCorner(level, cx + 22, cy, cz + 18);
        for (int i = 0; i < 10; i++) {
            int lx = cx - 24 + i * 5;
            level.setBlock(new BlockPos(lx, cy + 1, cz + 14),
                    ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState(), 2);
        }
        spawnNpc(level, UndercatHubNpcEntity.Role.CORRUGATA, cx, cy + 1, cz - 2);
        var boss = ModEntities.TAPE_COLOSSUS.get().create(level);
        if (boss != null) {
            boss.setPos(cx + 32, cy + 1, cz + 24);
            boss.finalizeSpawn(level, level.getCurrentDifficultyAt(center), MobSpawnType.STRUCTURE, null, null);
            level.addFreshEntity(boss);
        }
        level.setBlock(new BlockPos(cx, cy + 1, cz),
                ModBlocks.UNDERCAT_WAYSTONE.get().defaultBlockState(), 2);
    }

    private static void buildCastle(ServerLevel level, int cx, int cy, int cz) {
        for (int y = 1; y <= 7; y++) {
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    if (Math.abs(dx) == 4 || Math.abs(dz) == 4 || y == 7) {
                        level.setBlock(new BlockPos(cx + dx, cy + y, cz + dz),
                                ModBlocks.REINFORCED_CARDBOARD.get().defaultBlockState(), 2);
                    }
                }
            }
        }
        level.setBlock(new BlockPos(cx, cy + 1, cz),
                ModBlocks.ALTAR_FOUNDATION.get().defaultBlockState(), 2);
    }

    private static void buildTapeTemple(ServerLevel level, int x, int y, int z) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                level.setBlock(new BlockPos(x + dx, y + 1, z + dz),
                        ModBlocks.TAPE_BLOCK.get().defaultBlockState(), 2);
            }
        }
        level.setBlock(new BlockPos(x, y + 2, z), ModBlocks.TAPE_TEMPLE.get().defaultBlockState(), 2);
    }

    private static void buildOrphanage(ServerLevel level, int x, int y, int z) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                level.setBlock(new BlockPos(x + dx, y + 1, z + dz),
                        ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState(), 2);
            }
        }
    }

    private static void buildMazeCorner(ServerLevel level, int x, int y, int z) {
        for (int i = 0; i < 6; i++) {
            level.setBlock(new BlockPos(x + i, y + 1, z), ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState(), 2);
            level.setBlock(new BlockPos(x, y + 1, z + i), ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState(), 2);
        }
        var ghost = ModEntities.BOX_GHOST.get().create(level);
        if (ghost != null) {
            ghost.setPos(x + 3, y + 1, z + 3);
            ghost.finalizeSpawn(level, level.getCurrentDifficultyAt(new BlockPos(x, y, z)),
                    MobSpawnType.STRUCTURE, null, null);
            level.addFreshEntity(ghost);
        }
    }

    static void fillPad(ServerLevel level, int cx, int cy, int cz, int radius, net.minecraft.world.level.block.Block block) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -2; dy <= 0; dy++) {
                    level.setBlock(new BlockPos(cx + dx, cy + dy, cz + dz), block.defaultBlockState(), 2);
                }
            }
        }
    }

    static void spawnNpc(ServerLevel level, UndercatHubNpcEntity.Role role, double x, double y, double z) {
        var npc = ModEntities.UNDERCAT_HUB_NPC.get().create(level);
        if (npc != null) {
            npc.setRole(role);
            npc.setPos(x, y, z);
            npc.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(x, y, z)),
                    MobSpawnType.STRUCTURE, null, null);
            level.addFreshEntity(npc);
        }
    }
}
