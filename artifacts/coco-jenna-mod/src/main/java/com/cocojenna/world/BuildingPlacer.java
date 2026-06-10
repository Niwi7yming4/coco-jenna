package com.cocojenna.world;

import com.cocojenna.endgame.BuildingBlueprintCatalog;
import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;

/** 建築完工後在世界中放置結構. */
public final class BuildingPlacer {

    private BuildingPlacer() {}

    public static boolean place(ServerPlayer player, String buildingId) {
        BuildingBlueprintCatalog.Blueprint bp = BuildingBlueprintCatalog.get(buildingId);
        if (bp == null) return false;
        BlockPos anchor = player.blockPosition().offset(player.getDirection().getNormal().multiply(4));
        ServerLevel level = player.serverLevel();
        if (!level.dimension().equals(com.cocojenna.init.ModDimensions.CAT_KINGDOM)) {
            player.displayClientMessage(Component.translatable("build.cocojenna.need_kingdom"), true);
            return false;
        }
        switch (buildingId) {
            case "small_cat_house" -> ArchitectureBuilders.buildYarnHouse(
                    level, anchor.getX(), anchor.getY(), anchor.getZ(),
                    net.minecraft.world.level.material.MapColor.COLOR_ORANGE);
            case "ironpaw_forge_upgrade", "ironpaw_forge_master" -> placeForgeUpgrade(level, anchor);
            case "sanbana_sewing" -> placeSewing(level, anchor);
            case "distiller_station" -> ArchitectureBuilders.set(level, anchor, ModBlocks.DISTILLER.get().defaultBlockState());
            case "pure_light_tower_build" -> ArchitectureBuilders.set(level, anchor, ModBlocks.PURE_LIGHT_TOWER.get().defaultBlockState());
            case "memory_lighthouse_build" -> ArchitectureBuilders.set(level, anchor, ModBlocks.MEMORY_LIGHTHOUSE.get().defaultBlockState());
            case "cat_paradise" -> placeParadise(level, anchor);
            case "mine_entrance" -> placeMine(level, anchor);
            case "cat_library" -> placeLibrary(level, anchor);
            case "festival_stage" -> placeStage(level, anchor);
            case "hot_spring" -> placeHotSpring(level, anchor);
            case "puree_fountain" -> placeFountain(level, anchor);
            case "stargazer_tower" -> ArchitectureBuilders.buildScratchMonumentTower(level, anchor, 25);
            case "memory_monument_tier2" -> ArchitectureBuilders.buildScratchMonumentTower(level, anchor, 40);
            case "floating_platform" -> ArchitectureBuilders.fillDisk(level, anchor.above(8),
                    6, ModBlocks.VELVET_BLOCK.get().defaultBlockState());
            case "cat_school" -> placeSchool(level, anchor);
            case "market_square" -> placeMarket(level, anchor);
            case "open_air_theater", "memory_theater" -> placeTheater(level, anchor);
            default -> placeGeneric(level, anchor);
        }
        player.displayClientMessage(Component.translatable("build.cocojenna.placed", bp.name()), true);
        return true;
    }

    private static void placeForgeUpgrade(ServerLevel level, BlockPos o) {
        ArchitectureBuilders.fillDisk(level, o, 4, ModBlocks.STARDUST_BRICK.get().defaultBlockState());
        ArchitectureBuilders.set(level, o.above(), ModBlocks.IRONPAW_FORGE.get().defaultBlockState());
        ArchitectureBuilders.set(level, o.offset(2, 1, 0), ModBlocks.SOCKETING_TABLE.get().defaultBlockState());
    }

    private static void placeSewing(ServerLevel level, BlockPos o) {
        ArchitectureBuilders.buildYarnHouse(level, o.getX(), o.getY(), o.getZ(),
                net.minecraft.world.level.material.MapColor.COLOR_RED);
        ArchitectureBuilders.set(level, o.offset(1, 1, 0), ModBlocks.SCRATCHING_POST.get().defaultBlockState());
    }

    private static void placeParadise(ServerLevel level, BlockPos o) {
        ArchitectureBuilders.fillDisk(level, o, 6, ModBlocks.VELVET_CARPET.get().defaultBlockState());
        for (int a = 0; a < 6; a++) {
            double ang = a * Math.PI * 2 / 6;
            ArchitectureBuilders.set(level, o.offset((int) (Math.cos(ang) * 4), 1, (int) (Math.sin(ang) * 4)),
                    ModBlocks.CAT_CLIMB_PLATFORM.get().defaultBlockState());
        }
        ArchitectureBuilders.set(level, o.above(), ModBlocks.CAT_BED.get().defaultBlockState());
    }

    private static void placeMine(ServerLevel level, BlockPos o) {
        for (int y = 0; y < 4; y++) {
            ArchitectureBuilders.set(level, o.below(y), Blocks.LADDER.defaultBlockState());
        }
        ArchitectureBuilders.set(level, o, Blocks.OAK_TRAPDOOR.defaultBlockState());
    }

    private static void placeLibrary(ServerLevel level, BlockPos o) {
        ArchitectureBuilders.fillDisk(level, o, 5, ModBlocks.VELVET_PLANKS.get().defaultBlockState());
        for (int y = 1; y <= 4; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    if (Math.abs(x) == 4 || Math.abs(z) == 4) {
                        ArchitectureBuilders.set(level, o.offset(x, y, z), ModBlocks.WOVEN_WOOL.get().defaultBlockState());
                    }
                }
            }
        }
        ArchitectureBuilders.set(level, o.offset(0, 1, 0), ModBlocks.PICTURE_BOOK_STAND.get().defaultBlockState());
        ArchitectureBuilders.set(level, o.offset(2, 1, 0), Blocks.BOOKSHELF.defaultBlockState());
    }

    private static void placeStage(ServerLevel level, BlockPos o) {
        ArchitectureBuilders.fillDisk(level, o, 6, ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
        for (int x = -3; x <= 3; x++) {
            ArchitectureBuilders.set(level, o.offset(x, 1, -4), ModBlocks.CAT_SCRATCH_BOARD.get().defaultBlockState());
            ArchitectureBuilders.set(level, o.offset(x, 1, 4), ModBlocks.CAT_SCRATCH_BOARD.get().defaultBlockState());
        }
        ArchitectureBuilders.set(level, o.above(2), ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
    }

    private static void placeHotSpring(ServerLevel level, BlockPos o) {
        ArchitectureBuilders.fillDisk(level, o, 4, ModBlocks.STARDUST_BRICK.get().defaultBlockState());
        ArchitectureBuilders.fillDisk(level, o, 3, Blocks.WATER.defaultBlockState());
        ArchitectureBuilders.set(level, o.above(1), ModBlocks.NEON_MUSHROOM.get().defaultBlockState());
    }

    private static void placeFountain(ServerLevel level, BlockPos o) {
        ArchitectureBuilders.fillDisk(level, o, 3, ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
        ArchitectureBuilders.set(level, o.above(), Blocks.WATER.defaultBlockState());
        ArchitectureBuilders.set(level, o.above(2), ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());
    }

    private static void placeSchool(ServerLevel level, BlockPos o) {
        ArchitectureBuilders.buildYarnHouse(level, o.getX(), o.getY(), o.getZ(),
                net.minecraft.world.level.material.MapColor.COLOR_YELLOW);
        ArchitectureBuilders.set(level, o.offset(3, 1, 0), ModBlocks.CAT_CLIMB_PLATFORM.get().defaultBlockState());
    }

    private static void placeMarket(ServerLevel level, BlockPos o) {
        for (int x = -2; x <= 2; x++) {
            ArchitectureBuilders.set(level, o.offset(x, 0, 0), ModBlocks.RYOKATANA_SHOP_STAND.get().defaultBlockState());
        }
        ArchitectureBuilders.fillDisk(level, o, 4, ModBlocks.VELVET_CARPET.get().defaultBlockState());
    }

    private static void placeTheater(ServerLevel level, BlockPos o) {
        placeStage(level, o);
        for (int z = 2; z <= 5; z++) {
            ArchitectureBuilders.fillDisk(level, o.offset(0, 0, z), 5 - z / 2, ModBlocks.VELVET_CARPET.get().defaultBlockState());
        }
    }

    private static void placeGeneric(ServerLevel level, BlockPos o) {
        ArchitectureBuilders.fillDisk(level, o, 3, ModBlocks.VELVET_BLOCK.get().defaultBlockState());
        ArchitectureBuilders.set(level, o.above(), ModBlocks.CAT_CORE_ENGINEERING.get().defaultBlockState());
    }
}
