package com.cocojenna.world;

import com.cocojenna.block.DungeonRewardBlock;
import com.cocojenna.exploration.DungeonPuzzleManager;
import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/** 程序化地牢共用建造工具（設計書 3.2–3.3）. */
public final class ProceduralDungeonBuilder {

    private ProceduralDungeonBuilder() {}

    public record Theme(BlockState wall, BlockState floor, BlockState accent) {}

    public static List<int[]> layoutRooms(ServerLevel level, BlockPos anchor, int count) {
        List<int[]> rooms = new ArrayList<>();
        int ax = anchor.getX();
        int ay = anchor.getY();
        int az = anchor.getZ();
        rooms.add(new int[]{ax, ay, az});

        int x = ax;
        int z = az;
        Direction dir = Direction.SOUTH;
        for (int i = 1; i < count; i++) {
            if (i % 3 == 0 && level.random.nextBoolean()) {
                dir = dir.getClockWise();
            }
            x += dir.getStepX() * 10;
            z += dir.getStepZ() * 10;
            rooms.add(new int[]{x, ay, z});
        }
        return rooms;
    }

    public static void buildDungeon(ServerLevel level, String dungeonId, int dungeonIndex,
            BlockPos anchor, int minRooms, int maxRooms, Theme theme,
            @Nullable BlockPos ladderTarget, EntityType<? extends Mob> bossType,
            BiConsumer<ServerLevel, int[]> roomDecorator) {
        BlockState air = Blocks.AIR.defaultBlockState();
        int roomCount = minRooms + level.random.nextInt(Math.max(1, maxRooms - minRooms + 1));
        List<int[]> rooms = layoutRooms(level, anchor, roomCount);

        for (int[] r : rooms) {
            carveRoom(level, r[0], r[1], r[2], 4, 3, theme, air);
        }
        for (int i = 0; i < rooms.size() - 1; i++) {
            carveCorridor(level, rooms.get(i), rooms.get(i + 1), theme, air);
        }

        int[] entrance = rooms.get(0);
        carveRoom(level, entrance[0], entrance[1], entrance[2], 5, 4, theme, air);
        if (ladderTarget != null) {
            placeLadderShaft(level, entrance[0], entrance[1], entrance[2], ladderTarget.getY());
        }
        placeLootChest(level, entrance[0] + 2, entrance[1] + 1, entrance[2],
                new ResourceLocation("cocojenna", "chests/dungeon_common"));

        List<int[]> middle = rooms.subList(1, Math.max(1, rooms.size() - 1));
        ResourceLocation loot = new ResourceLocation("cocojenna", "chests/dungeon_" + dungeonId);

        if (middle.size() >= 2) {
            DungeonPuzzleManager.placePuzzleChain(level, dungeonIndex, middle,
                    rooms.get(rooms.size() - 1));
        }

        for (int i = 0; i < middle.size(); i++) {
            int[] r = middle.get(i);
            roomDecorator.accept(level, r);
            if (level.random.nextFloat() < 0.45f) {
                placeLootChest(level, r[0] + 1, r[1] + 1, r[2] - 1, loot);
            }
        }

        int[] boss = rooms.get(rooms.size() - 1);
        placeBossRoom(level, boss[0], boss[1], boss[2], theme, dungeonIndex, bossType);
    }

    public static void carveRoom(Level level, int cx, int cy, int cz, int halfW, int height,
            Theme theme, BlockState air) {
        for (int x = -halfW; x <= halfW; x++) {
            for (int z = -halfW; z <= halfW; z++) {
                for (int y = 0; y <= height; y++) {
                    boolean edge = x == -halfW || x == halfW || z == -halfW || z == halfW || y == 0 || y == height;
                    BlockState state = y == 0 ? theme.floor() : (edge ? theme.wall() : air);
                    ArchitectureBuilders.set(level, cx + x, cy + y, cz + z, state);
                }
            }
        }
        ArchitectureBuilders.set(level, cx, cy + 1, cz, theme.accent());
    }

    public static void carveCorridor(Level level, int[] a, int[] b, Theme theme, BlockState air) {
        int steps = Math.max(Math.abs(b[0] - a[0]), Math.abs(b[2] - a[2])) / 2;
        for (int i = 0; i <= steps; i++) {
            double t = steps == 0 ? 0 : (double) i / steps;
            int x = (int) (a[0] + (b[0] - a[0]) * t);
            int z = (int) (a[2] + (b[2] - a[2]) * t);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    ArchitectureBuilders.set(level, x + dx, a[1], z + dz, theme.floor());
                    ArchitectureBuilders.set(level, x + dx, a[1] + 1, z + dz, air);
                    ArchitectureBuilders.set(level, x + dx, a[1] + 2, z + dz, air);
                    if (Math.abs(dx) == 1 && Math.abs(dz) == 1) {
                        ArchitectureBuilders.set(level, x + dx, a[1] + 1, z + dz, theme.wall());
                    }
                }
            }
        }
    }

    public static void placeLadderShaft(ServerLevel level, int x, int y, int z, int surfaceY) {
        for (int ly = y + 1; ly < surfaceY; ly++) {
            ArchitectureBuilders.set(level, x, ly, z, Blocks.AIR.defaultBlockState());
            BlockState ladder = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.NORTH);
            ArchitectureBuilders.set(level, x + 1, ly, z, ladder);
        }
        for (int ly = surfaceY - 3; ly <= surfaceY; ly++) {
            ArchitectureBuilders.set(level, x, ly, z, Blocks.STONE_BRICKS.defaultBlockState());
        }
    }

    public static void placeLootChest(ServerLevel level, int x, int y, int z, ResourceLocation lootTable) {
        BlockPos pos = new BlockPos(x, y, z);
        ArchitectureBuilders.set(level, x, y, z, Blocks.CHEST.defaultBlockState());
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RandomizableContainerBlockEntity chest) {
            chest.setLootTable(lootTable, level.random.nextLong());
        }
    }

    public static void placeBossRoom(ServerLevel level, int cx, int cy, int cz,
            Theme theme, int dungeonIndex, EntityType<? extends Mob> bossType) {
        carveRoom(level, cx, cy, cz, 5, 4, theme, Blocks.AIR.defaultBlockState());
        ArchitectureBuilders.set(level, cx, cy + 1, cz,
                ModBlocks.DUNGEON_REWARD.get().defaultBlockState()
                        .setValue(DungeonRewardBlock.DUNGEON_ID, dungeonIndex));

        BlockPos anchor = new BlockPos(cx, cy, cz);
        com.cocojenna.exploration.DungeonWorldData.get(level).setBossAnchor(dungeonIndex, anchor);
        if (!com.cocojenna.exploration.DungeonWorldData.get(level).isBossDefeated(dungeonIndex)) {
            Mob boss = bossType.create(level);
            if (boss != null) {
                boss.moveTo(cx + 0.5, cy + 1, cz - 2.5, 0, 0);
                boss.setPersistenceRequired();
                level.addFreshEntity(boss);
            }
        }
    }
}
