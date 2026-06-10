package com.cocojenna.world;

import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.exploration.ExplorationMarkers;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import com.cocojenna.util.MemoryShardUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * 初啼村 — 生命線球樹上的毛線小屋聚落（設計書第二章）.
 */
public final class FirstCryVillageGenerator {

    public static final int FLOOR_Y = 64;
    public static final BlockPos CENTER = new BlockPos(0, FLOOR_Y, 0);
    public static final BlockPos SPAWN = new BlockPos(0, FLOOR_Y + 1, -12);
    private static final BlockPos MARKER = new BlockPos(0, FLOOR_Y + 52, 0);

    private FirstCryVillageGenerator() {}

    public static BlockPos ensureVillage(ServerLevel level, @Nullable ServerPlayer player) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            return SPAWN;
        }
        if (!level.getBlockState(MARKER).is(ModBlocks.PURR_CRYSTAL_BLOCK.get())) {
            placeVillage(level);
            spawnVillageNpcs(level, player);
            if (player != null) {
                grantArrivalShard(player);
            }
            GearTownGenerator.ensureTown(level);
            BlindPortGenerator.ensurePort(level);
            DawnWeaverGenerator.ensureWeaver(level);
            MoonAlleyGenerator.ensureAlley(level);
            VelvetForestPoiGenerator.ensureForest(level);
            ForgottenTowerGenerator.ensureTower(level);
            RegionGenerators.ensureAll(level);
            VelvetTailCastleGenerator.ensureCastle(level);
        }
        return SPAWN;
    }

    private static void grantArrivalShard(ServerPlayer player) {
        var shard = MemoryShardUtil.create("first_cry_arrival");
        if (!player.addItem(shard)) {
            player.drop(shard, false);
        }
        player.displayClientMessage(
                Component.translatable("cocojenna.memory_shard.first_cry_arrival"), false);
    }

    private static void placeVillage(ServerLevel level) {
        int radius = 50;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) continue;
                BlockPos surface = new BlockPos(x, FLOOR_Y, z);
                level.setBlock(surface.below(), Blocks.DIRT.defaultBlockState(), 2);
                int distSq = x * x + z * z;
                if (distSq <= 12 * 12) {
                    level.setBlock(surface, ModBlocks.VELVET_GRASS.get().defaultBlockState(), 2);
                } else if (distSq <= radius * radius) {
                    level.setBlock(surface,
                            (x + z) % 3 == 0
                                    ? ModBlocks.VELVET_GRASS.get().defaultBlockState()
                                    : ModBlocks.STARDUST_SOIL.get().defaultBlockState(), 2);
                }
            }
        }

        com.cocojenna.world.firstcry.FirstCrySacredTreeBuilder.build(level);
        com.cocojenna.world.firstcry.FirstCrySacredTreeBuilder.buildBlackMudRuin(level);
        com.cocojenna.world.firstcry.FirstCryMayorHallBuilder.build(level);
        com.cocojenna.world.firstcry.FirstCryLibraryBuilder.build(level);
        com.cocojenna.world.firstcry.FirstCryMarketDistrictBuilder.build(level);
        com.cocojenna.world.firstcry.FirstCryKitchenMarketBuilder.build(level);
        com.cocojenna.world.firstcry.FirstCryMoonPlazaBuilder.build(level);
        com.cocojenna.world.firstcry.FirstCryHarborBuilder.build(level);
        com.cocojenna.world.firstcry.FirstCryInnBuilder.build(level);
        com.cocojenna.world.firstcry.FirstCryFarmBuilder.build(level);
        com.cocojenna.world.firstcry.FirstCryOuterRingBuilder.build(level);
        com.cocojenna.world.firstcry.FirstCryStructurePlacer.placeDistricts(level);

        buildReturnPortal(level, 0, FLOOR_Y, -42);
        scatterPlants(level);
        BlackMudBossHelper.trySpawnBoss(level, ModEntities.FIRST_CRY_WARDEN.get(),
                com.cocojenna.world.firstcry.FirstCryLayout.MOON_PLAZA.offset(12, 1, 0));
        ExplorationMarkers.placeFirstCry(level);
        MoonCrossroadsPlacer.place(level);
        for (int dy = 0; dy < 3; dy++) {
            set(level, 2, FLOOR_Y - 1 - dy, 0, Blocks.STONE_BRICKS.defaultBlockState());
        }
    }

    private static void spawnVillageNpcs(ServerLevel level, @Nullable ServerPlayer player) {
        spawnCat(level, ModEntities.COCO.get(), -14, 16, player);
        spawnCat(level, ModEntities.JENNA.get(), 12, -6, player);

        var samurai = ModEntities.SAMURAI_CAT.get().create(level);
        if (samurai != null) {
            samurai.moveTo(10.5, FLOOR_Y + 23, -4.5, 200.0F, 0.0F);
            level.addFreshEntity(samurai);
        }
        var hunter = ModEntities.TREASURE_HUNTER.get().create(level);
        if (hunter != null) {
            hunter.moveTo(4.5, FLOOR_Y + 1, 6.5, 90.0F, 0.0F);
            level.addFreshEntity(hunter);
        }
        var alpha = ModEntities.ALPHA.get().create(level);
        if (alpha != null) {
            alpha.moveTo(1.5, FLOOR_Y + 1, 3.5, 180.0F, 0.0F);
            level.addFreshEntity(alpha);
        }
        com.cocojenna.world.firstcry.FirstCryNpcSpawner.spawnAll(level);
    }

    private static void spawnCat(ServerLevel level, EntityType<? extends AbstractCatEntity> type,
            int x, int z, @Nullable ServerPlayer player) {
        var cat = type.create(level);
        if (cat == null) return;
        cat.moveTo(x + 0.5, FLOOR_Y + 1, z + 0.5, 0.0F, 0.0F);
        if (player != null && cat instanceof CocoEntity coco) {
            coco.setOwnerUUID(player.getUUID());
        } else if (player != null && cat instanceof JennaEntity jenna) {
            jenna.setOwnerUUID(player.getUUID());
        }
        level.addFreshEntity(cat);
    }

    private static void buildReturnPortal(ServerLevel level, int x, int y, int z) {
        for (int dy = 0; dy < 5; dy++) {
            set(level, x - 1, y + dy, z, frame());
            set(level, x + 2, y + dy, z, frame());
        }
        for (int dx = -1; dx <= 2; dx++) {
            set(level, x + dx, y, z, frame());
            set(level, x + dx, y + 4, z, frame());
        }
        set(level, x, y + 1, z, ModBlocks.CAT_KINGDOM_PORTAL.get().defaultBlockState());
        set(level, x + 1, y + 1, z, ModBlocks.CAT_KINGDOM_PORTAL.get().defaultBlockState());
        set(level, x, y + 2, z, ModBlocks.CAT_KINGDOM_PORTAL.get().defaultBlockState());
        set(level, x + 1, y + 2, z, ModBlocks.CAT_KINGDOM_PORTAL.get().defaultBlockState());
        set(level, x, y + 5, z, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        set(level, x + 1, y + 5, z, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
    }

    private static void scatterPlants(ServerLevel level) {
        BlockPos[] spots = {
                new BlockPos(6, FLOOR_Y, 8), new BlockPos(-8, FLOOR_Y, 10),
                new BlockPos(12, FLOOR_Y, -4), new BlockPos(-14, FLOOR_Y, -6),
                new BlockPos(5, FLOOR_Y, -10), new BlockPos(-6, FLOOR_Y, 14)
        };
        for (BlockPos pos : spots) {
            if (level.getBlockState(pos).isAir()) {
                level.setBlock(pos, ModBlocks.CATNIP.get().defaultBlockState(), 2);
            }
        }
        set(level, 8, FLOOR_Y, 4, ModBlocks.HIBISCUS_FLOWER.get().defaultBlockState());
        set(level, -9, FLOOR_Y, 5, ModBlocks.NEON_MUSHROOM.get().defaultBlockState());
    }

    private static BlockState frame() {
        return ModBlocks.WOVEN_WOOL.get().defaultBlockState();
    }

    private static void set(Level level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }
}
