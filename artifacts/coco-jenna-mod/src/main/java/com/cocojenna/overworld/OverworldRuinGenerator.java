package com.cocojenna.overworld;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/** 主世界貓之國遺跡程序化生成. */
public final class OverworldRuinGenerator {

    private static final int RUIN_CHUNK_CHANCE = 4;

    private OverworldRuinGenerator() {}

    public static void trySeedChunkRuin(ServerLevel level, long chunkKey, BlockPos surfaceHint) {
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(level);
        if (data.isRuinChunkSeeded(chunkKey)) return;

        var random = net.minecraft.util.RandomSource.create(level.getSeed() ^ chunkKey ^ 0x5EEDCA771L);
        data.markRuinChunkSeeded(chunkKey);
        if (random.nextInt(100) >= RUIN_CHUNK_CHANCE) return;

        int x = surfaceHint.getX() + random.nextInt(16) - 8;
        int z = surfaceHint.getZ() + random.nextInt(16) - 8;
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        BlockPos center = new BlockPos(x, y, z);
        OverworldRuinType type;
        if (random.nextInt(400) == 0) {
            type = OverworldRuinType.MOON_SEAL;
        } else {
            type = OverworldRuinType.roll(random);
        }
        if (!data.canPlaceRuin(center, type)) return;
        build(level, data, center, type);
    }

    public static void ensureStarterOutpost(ServerLevel level, BlockPos near) {
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(level);
        if (data.isStarterOutpostPlaced()) return;

        int angle = level.random.nextInt(360);
        int dist = 180 + level.random.nextInt(280);
        int x = near.getX() + (int) (Math.cos(Math.toRadians(angle)) * dist);
        int z = near.getZ() + (int) (Math.sin(Math.toRadians(angle)) * dist);
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        build(level, data, new BlockPos(x, y, z), OverworldRuinType.SMUGGLER_OUTPOST);
        data.setStarterOutpostPlaced(true);
    }

    public static void build(ServerLevel level, OverworldPenetrationSavedData data,
            BlockPos center, OverworldRuinType type) {
        switch (type) {
            case WAR_RUIN -> buildWarRuin(level, data, center);
            case FORGOTTEN_ALTAR -> buildAltar(level, data, center);
            case MUD_FARM -> buildMudFarm(level, data, center);
            case SMUGGLER_OUTPOST -> buildOutpost(level, data, center);
            case CAT_BAR -> buildCatBar(level, data, center);
            case POLLUTED_TEMPLE -> buildPollutedTemple(level, data, center);
            case MOON_SEAL -> buildMoonSealDungeon(level, data, center);
        }
        data.putRuin(center, type);
    }

    private static void buildWarRuin(ServerLevel level, OverworldPenetrationSavedData data, BlockPos c) {
        BlockState stone = Blocks.COBBLESTONE.defaultBlockState();
        BlockState cracked = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (Math.abs(dx) + Math.abs(dz) > 6) continue;
                level.setBlock(c.offset(dx, 0, dz), stone, 2);
            }
        }
        level.setBlock(c.offset(0, 1, 0), cracked, 2);
        level.setBlock(c.offset(2, 1, 1), cracked, 2);
        level.setBlock(c.offset(-2, 1, -1), Blocks.BLACK_WOOL.defaultBlockState(), 2);
        BlockPos chest = c.offset(0, 1, 2);
        level.setBlock(chest, Blocks.CHEST.defaultBlockState(), 2);
        fillChest(level, chest, new ItemStack[]{
                new ItemStack(ModItems.MEMORY_SHARD.get()),
                new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 2),
                new ItemStack(ModItems.WARRIOR_LAST_LETTER.get()),
                RuinMapFragmentHelper.typedStack(RuinMapFragmentType.WAR_RUIN, 1)
        });
        spawnMobs(level, c, 1, 2);
    }

    private static void buildAltar(ServerLevel level, OverworldPenetrationSavedData data, BlockPos c) {
        BlockState brick = Blocks.STONE_BRICKS.defaultBlockState();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlock(c.offset(dx, 0, dz), brick, 2);
            }
        }
        level.setBlock(c, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 2);
        level.setBlock(c.above(), Blocks.CANDLE.defaultBlockState(), 2);
        level.setBlock(c.offset(-2, 1, 0), Blocks.CANDLE.defaultBlockState(), 2);
        level.setBlock(c.offset(2, 1, 0), Blocks.CANDLE.defaultBlockState(), 2);
        data.putTrace(c.above(), OverworldTraceType.MOON_PAW);
    }

    private static void buildMudFarm(ServerLevel level, OverworldPenetrationSavedData data, BlockPos c) {
        BlockState plank = Blocks.OAK_PLANKS.defaultBlockState();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                level.setBlock(c.offset(dx, 0, dz), Blocks.FARMLAND.defaultBlockState(), 2);
                if (level.random.nextBoolean()) {
                    level.setBlock(c.offset(dx, 1, dz), ModBlocks.BLACK_MUD.get().defaultBlockState(), 2);
                } else {
                    level.setBlock(c.offset(dx, 1, dz), Blocks.WHEAT.defaultBlockState(), 2);
                }
            }
        }
        for (int dy = 0; dy <= 3; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                boolean wall = Math.abs(dx) == 2 || dy == 0;
                if (wall) level.setBlock(c.offset(dx, dy, -5), plank, 2);
            }
        }
        BlockPos chest = c.offset(0, 1, -4);
        level.setBlock(chest, Blocks.CHEST.defaultBlockState(), 2);
        fillChest(level, chest, new ItemStack[]{
                new ItemStack(ModItems.BLACK_MUD_SAMPLE.get(), 2),
                new ItemStack(ModItems.FARMER_DIARY.get()),
                RuinMapFragmentHelper.typedStack(RuinMapFragmentType.MUD_FARM, 1),
                new ItemStack(Items.WHEAT, 8)
        });
        var farmer = ModEntities.MUD_FARMER.get().create(level);
        if (farmer != null) {
            farmer.setPos(c.getX() + 0.5, c.getY() + 1, c.getZ() + 0.5);
            level.addFreshEntity(farmer);
        }
        spawnMobs(level, c, 2, 3);
    }

    private static void buildOutpost(ServerLevel level, OverworldPenetrationSavedData data, BlockPos c) {
        BlockState log = Blocks.SPRUCE_LOG.defaultBlockState();
        BlockState plank = Blocks.SPRUCE_PLANKS.defaultBlockState();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                level.setBlock(c.offset(dx, 0, dz), plank, 2);
            }
        }
        for (int dy = 1; dy <= 2; dy++) {
            level.setBlock(c.offset(-2, dy, -2), log, 2);
            level.setBlock(c.offset(2, dy, -2), log, 2);
            level.setBlock(c.offset(-2, dy, 2), Blocks.AIR.defaultBlockState(), 2);
        }
        level.setBlock(c.offset(0, 1, 0), Blocks.CAMPFIRE.defaultBlockState(), 2);
        BlockPos chest = c.offset(1, 1, 1);
        level.setBlock(chest, Blocks.CHEST.defaultBlockState(), 2);
        fillChest(level, chest, new ItemStack[]{
                new ItemStack(ModItems.CATNIP_ITEM.get(), 3),
                new ItemStack(ModItems.MOONSTONE.get()),
                new ItemStack(ModItems.BLACK_MUD_SAMPLE.get()),
                new ItemStack(ModItems.OUTPOST_BADGE.get()),
                RuinMapFragmentHelper.typedStack(RuinMapFragmentType.SMUGGLER_OUTPOST, 1),
                new ItemStack(Items.WRITABLE_BOOK)
        });
        data.putTrace(c.offset(-1, 2, -2), OverworldTraceType.CAT_GRAFFITI);
    }

    private static void spawnMobs(ServerLevel level, BlockPos c, int min, int max) {
        int count = min + level.random.nextInt(max - min + 1);
        for (int i = 0; i < count; i++) {
            var mob = ModEntities.WANDERING_SLUDGE.get().create(level);
            if (mob == null) continue;
            mob.setPos(c.getX() + level.random.nextInt(7) - 3 + 0.5,
                    c.getY() + 1, c.getZ() + level.random.nextInt(7) - 3 + 0.5);
            level.addFreshEntity(mob);
        }
    }

    private static void buildCatBar(ServerLevel level, OverworldPenetrationSavedData data, BlockPos c) {
        level.setBlock(c, Blocks.OAK_TRAPDOOR.defaultBlockState(), 2);
        for (int dy = 1; dy <= 6; dy++) {
            level.setBlock(c.below(dy), Blocks.LADDER.defaultBlockState(), 2);
        }
        BlockPos room = c.below(7);
        BlockState plank = Blocks.DARK_OAK_PLANKS.defaultBlockState();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                level.setBlock(room.offset(dx, 0, dz), plank, 2);
                if (Math.abs(dx) == 4 || Math.abs(dz) == 4) {
                    for (int dy = 1; dy <= 3; dy++) {
                        level.setBlock(room.offset(dx, dy, dz), plank, 2);
                    }
                }
            }
        }
        level.setBlock(room, Blocks.BARREL.defaultBlockState(), 2);
        level.setBlock(room.offset(2, 1, 0), Blocks.CHEST.defaultBlockState(), 2);
        fillChest(level, room.offset(2, 1, 0), new ItemStack[]{
                RuinMapFragmentHelper.typedStack(RuinMapFragmentType.SMUGGLER_OUTPOST, 1),
                new ItemStack(ModItems.CATNIP_ITEM.get(), 5),
                new ItemStack(ModItems.MEMORY_SHARD.get())
        });
        spawnBarNpc(level, room.offset(-2, 1, 1), OverworldCatNpcEntity.Role.BARKEEP);
        spawnBarNpc(level, room.offset(1, 1, -2), OverworldCatNpcEntity.Role.WHISPERER);
        data.putTrace(room.offset(0, 2, -3), OverworldTraceType.CAT_GRAFFITI);
    }

    private static void buildPollutedTemple(ServerLevel level, OverworldPenetrationSavedData data, BlockPos c) {
        for (int dy = 0; dy >= -8; dy--) {
            level.setBlock(c.offset(0, dy, 0), Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 2);
        }
        BlockPos hall = c.below(9);
        BlockState brick = Blocks.STONE_BRICKS.defaultBlockState();
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -8; dz <= 2; dz++) {
                level.setBlock(hall.offset(dx, 0, dz), brick, 2);
                if (Math.abs(dx) == 5 || dz == -8 || dz == 2) {
                    level.setBlock(hall.offset(dx, 1, dz), brick, 2);
                    level.setBlock(hall.offset(dx, 2, dz), brick, 2);
                }
            }
        }
        level.setBlock(hall.offset(0, 0, -6), ModBlocks.BLACK_MUD.get().defaultBlockState(), 2);
        level.setBlock(hall.offset(0, 1, 4), Blocks.CHEST.defaultBlockState(), 2);
        fillChest(level, hall.offset(0, 1, 4), new ItemStack[]{
                RuinMapFragmentHelper.typedStack(RuinMapFragmentType.POLLUTED_TEMPLE, 1),
                new ItemStack(ModItems.MEMORY_SHARD.get(), 2),
                new ItemStack(ModItems.BLACK_MUD_SAMPLE.get(), 2)
        });
        spawnMobs(level, hall, 2, 3);
        var guard = ModEntities.MUD_GUARD.get().create(level);
        if (guard != null) {
            guard.setPos(hall.getX() + 0.5, hall.getY() + 1, hall.getZ() - 5.5);
            level.addFreshEntity(guard);
        }
        var priest = ModEntities.MUD_PRIEST.get().create(level);
        if (priest != null) {
            priest.setPos(hall.getX() + 0.5, hall.getY() + 1, hall.getZ() - 6.5);
            level.addFreshEntity(priest);
        }
    }

    private static void buildMoonSealDungeon(ServerLevel level, OverworldPenetrationSavedData data, BlockPos c) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (dx * dx + dz * dz <= 9) {
                    level.setBlock(c.offset(dx, -1, dz), Blocks.STONE_BRICKS.defaultBlockState(), 2);
                }
            }
        }
        level.setBlock(c, ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState(), 2);
        BlockPos shaft = c.below(1);
        for (int dy = 1; dy <= 12; dy++) {
            level.setBlock(shaft.below(dy), Blocks.LADDER.defaultBlockState(), 2);
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    level.setBlock(shaft.below(dy).offset(dx, 0, dz), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
                }
            }
        }
        BlockPos arena = shaft.below(13);
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                level.setBlock(arena.offset(dx, 0, dz), Blocks.DEEPSLATE_TILES.defaultBlockState(), 2);
            }
        }
        level.setBlock(arena.above(), Blocks.SEA_LANTERN.defaultBlockState(), 2);
        var boss = ModEntities.MOON_GUARDIAN.get().create(level);
        if (boss != null) {
            boss.setPos(arena.getX() + 0.5, arena.getY() + 1, arena.getZ() + 0.5);
            level.addFreshEntity(boss);
        }
        level.setBlock(arena.offset(3, 1, 0), Blocks.CHEST.defaultBlockState(), 2);
        fillChest(level, arena.offset(3, 1, 0), new ItemStack[]{
                RuinMapFragmentHelper.typedStack(RuinMapFragmentType.MOON_SEAL, 1),
                new ItemStack(ModItems.MOON_CORE.get(), 1),
                new ItemStack(ModItems.MEMORY_SHARD.get(), 3)
        });
    }

    private static void spawnBarNpc(ServerLevel level, BlockPos pos, OverworldCatNpcEntity.Role role) {
        var npc = ModEntities.OVERWORLD_CAT.get().create(level);
        if (npc == null) return;
        npc.setRole(role);
        npc.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        level.addFreshEntity(npc);
    }

    private static void fillChest(ServerLevel level, BlockPos pos, ItemStack[] loot) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ChestBlockEntity chest)) return;
        int slot = 0;
        for (ItemStack stack : loot) {
            if (stack.isEmpty()) continue;
            chest.setItem(slot++, stack);
        }
    }
}
