package com.cocojenna.world;

import com.cocojenna.exploration.ExplorationMarkers;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** 月色小巷 — 中期區域 POI. */
public final class MoonAlleyGenerator {

    public static final BlockPos CENTER = new BlockPos(128, 64, -128);
    private static final BlockPos MARKER = new BlockPos(128, 64, -128);

    private MoonAlleyGenerator() {}

    public static void ensureAlley(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (level.getBlockState(MARKER).is(ModBlocks.MOONSTONE_BLOCK.get())) return;
        placeAlley(level);
    }

    private static void placeAlley(Level level) {
        int cx = CENTER.getX();
        int cz = CENTER.getZ();
        int y = CENTER.getY();

        ArchitectureBuilders.buildAlleySegment(level, new BlockPos(cx - 12, y, cz), 25, Direction.EAST);
        ArchitectureBuilders.buildAlleySegment(level, new BlockPos(cx, y, cz - 8), 17, Direction.SOUTH);

        set(level, cx, y, cz, ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState());
        set(level, cx - 8, y, cz + 2, Blocks.CHEST.defaultBlockState());
        BlockEntity chestBe = level.getBlockEntity(new BlockPos(cx - 8, y, cz + 2));
        if (chestBe instanceof RandomizableContainerBlockEntity chest) {
            chest.setLootTable(new ResourceLocation("cocojenna", "chests/moon_alley"), level.random.nextLong());
        }

        buildTeaHouse(level, cx + 6, y, cz);
        buildShopFront(level, cx - 6, y, cz - 2);
        ArchitectureBuilders.buildPortalGate(level, new BlockPos(cx, y, cz + 10), Direction.NORTH, "moon");

        BlackMudBossHelper.trySpawnBoss((ServerLevel) level, ModEntities.MOON_ALLEY_WRAITH.get(),
                new BlockPos(cx, y + 1, cz));
        if (level.getEntitiesOfClass(com.cocojenna.entity.BlackjackDealerEntity.class,
                new net.minecraft.world.phys.AABB(cx - 32, y - 4, cz - 32, cx + 32, y + 8, cz + 32)).isEmpty()) {
            var dealer = ModEntities.BLACKJACK_DEALER.get().create(level);
            if (dealer != null) {
                dealer.setPos(cx + 4, y + 1, cz);
                level.addFreshEntity(dealer);
            }
        }
        if (level instanceof ServerLevel server) {
            ExplorationMarkers.placeMoonAlley(server, CENTER);
        }
    }

    /** 仕女貓茶館 — 設計書 5.2 */
    private static void buildTeaHouse(Level level, int cx, int cy, int cz) {
        BlockState wall = ModBlocks.MOONSTONE_BRICK.get().defaultBlockState();
        BlockState glass = ModBlocks.PAWPRINT_GLASS.get().defaultBlockState();
        for (int x = -4; x <= 4; x++) {
            for (int z = -3; z <= 3; z++) {
                set(level, cx + x, cy, cz + z, ModBlocks.MOONSTONE_BLOCK.get().defaultBlockState());
            }
        }
        for (int y = 1; y <= 5; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -3; z <= 3; z++) {
                    boolean edge = Math.abs(x) == 4 || Math.abs(z) == 3;
                    if (edge) {
                        boolean window = y == 2 && (Math.abs(x) == 4 || Math.abs(z) == 3) && (x + z) % 2 == 0;
                        set(level, cx + x, cy + y, cz + z, window ? glass : wall);
                    }
                }
            }
        }
        for (int x = -3; x <= 3; x++) {
            for (int z = -2; z <= 2; z++) {
                set(level, cx + x, cy + 6, cz + z, ModBlocks.THATCH_ROOF.get().defaultBlockState());
            }
        }
        for (int x = -2; x <= 2; x += 2) {
            set(level, cx + x, cy + 1, cz, ModBlocks.VELVET_CARPET.get().defaultBlockState());
            set(level, cx + x, cy + 3, cz, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        }
        set(level, cx, cy + 1, cz + 3, Blocks.AIR.defaultBlockState());
        set(level, cx, cy + 2, cz + 3, Blocks.AIR.defaultBlockState());
        set(level, cx - 3, cy + 1, cz, ModBlocks.NEON_MUSHROOM_POT.get().defaultBlockState());
    }

    private static void buildShopFront(Level level, int cx, int cy, int cz) {
        for (int x = -2; x <= 2; x++) {
            for (int z = 0; z <= 2; z++) {
                set(level, cx + x, cy, cz + z, ModBlocks.MOONSTONE_BLOCK.get().defaultBlockState());
            }
        }
        for (int y = 1; y <= 3; y++) {
            set(level, cx - 2, cy + y, cz, ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
            set(level, cx + 2, cy + y, cz, ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
            set(level, cx, cy + y, cz + 2, ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
        }
        set(level, cx, cy + 1, cz + 1, ModBlocks.RYOKATANA_SHOP_STAND.get().defaultBlockState());
        set(level, cx, cy + 4, cz + 1, ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState());
    }

    private static void set(Level level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }
}
