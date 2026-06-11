package com.cocojenna.world;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.entity.UndercatHubNpcEntity;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import com.cocojenna.world.ruin.ModStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/** 紙箱貧民窟 POI. */
public final class CardboardSlumsGenerator {

    private static final ResourceLocation CASTLE_NBT =
            new ResourceLocation(CocoJennaMod.MOD_ID, "undercat/cardboard_castle");

    private CardboardSlumsGenerator() {}

    public static void build(ServerLevel level, BlockPos center) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        fillPad(level, cx, cy, cz, 28, ModBlocks.CARDBOARD_BLOCK.get());
        if (!placeCastleNbt(level, center)) {
            buildCastle(level, cx, cy, cz);
        }
        buildTapeTemple(level, cx - 18, cy, cz + 12);
        buildOrphanage(level, cx + 16, cy, cz - 14);
        buildMaze(level, cx + 22, cy, cz + 18);
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

    private static boolean placeCastleNbt(ServerLevel level, BlockPos origin) {
        try {
            StructureTemplate template = level.getStructureManager().getOrCreate(CASTLE_NBT);
            if (template.getSize().equals(Vec3i.ZERO)) return false;
            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .addProcessor(new ModStructureProcessor());
            template.placeInWorld(level, origin, origin, settings, level.random, Block.UPDATE_ALL);
            return true;
        } catch (Exception e) {
            CocoJennaMod.LOGGER.warn("cardboard_castle NBT failed: {}", e.toString());
            return false;
        }
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

    /** 3×3 紙箱房間迷宮（Wave 3）. */
    private static void buildMaze(ServerLevel level, int ox, int y, int oz) {
        int room = 5;
        for (int rx = 0; rx < 3; rx++) {
            for (int rz = 0; rz < 3; rz++) {
                int baseX = ox + rx * room;
                int baseZ = oz + rz * room;
                for (int dx = 0; dx < room; dx++) {
                    for (int dz = 0; dz < room; dz++) {
                        boolean wall = dx == 0 || dz == 0 || dx == room - 1 || dz == room - 1;
                        if (wall && (dx == room / 2 || dz == room / 2) && level.random.nextBoolean()) {
                            continue;
                        }
                        if (wall) {
                            level.setBlock(new BlockPos(baseX + dx, y + 1, baseZ + dz),
                                    ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState(), 2);
                        }
                    }
                }
                if (rx == 1 && rz == 1) {
                    level.setBlock(new BlockPos(baseX + 2, y + 1, baseZ + 2),
                            ModBlocks.SCRATCHING_POST.get().defaultBlockState(), 2);
                }
            }
        }
        var ghost = ModEntities.BOX_GHOST.get().create(level);
        if (ghost != null) {
            ghost.setPos(ox + 7, y + 1, oz + 7);
            ghost.finalizeSpawn(level, level.getCurrentDifficultyAt(new BlockPos(ox, y, oz)),
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
