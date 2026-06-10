package com.cocojenna.world.ruin;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** 20 種程序遺跡 + NBT fallback 骨架. */
public final class ProceduralRuinBuilder {

    private ProceduralRuinBuilder() {}

    public static void build(ServerLevel level, BlockPos c, RuinMatrixRegistry ruin, RandomSource random) {
        switch (ruin) {
            case OUTPOST, WAR_RUINS -> buildWarRuins(level, c);
            case SCRATCHING_BARRICADE -> buildScratchingBarricade(level, c);
            case VELVET_TOWER -> buildVelvetTower(level, c);
            case MORTAR_POSITION -> buildMortarPosition(level, c);
            case FALLEN_HEROES_MONUMENT -> buildMonument(level, c);
            case FORGOTTEN_ALTAR -> buildAltar(level, c);
            case BLACK_MUD_CONTAMINATED_TEMPLE -> buildMudTemple(level, c);
            case MOON_SEALED_DUNGEON -> buildMoonDungeon(level, c);
            case FULL_MOON_STARGAZING_WELL -> buildStargazingWell(level, c);
            case MEMORY_STONE_CIRCLE -> buildStoneCircle(level, c);
            case HOLY_WATER_POTION_LAB -> buildPotionLab(level, c);
            case BLACK_MUD_FARM -> buildMudFarm(level, c);
            case CARDBOARD_REFUGEE_CAMP -> buildCardboardCamp(level, c);
            case CATNIP_SMUGGLING_GREENHOUSE -> buildGreenhouse(level, c);
            case ABANDONED_GEAR_WORKSHOP -> buildWorkshop(level, c);
            case BLIND_WATER_SEWER -> buildSewer(level, c);
            case IRONPAW_FORGE_RUINS -> buildForge(level, c);
            case ALIENATED_CAT_TREE_TOWER -> buildTreeTower(level, c);
            case VELVET_TREEHOUSE_REMAINS -> buildTreehouse(level, c);
            case ROYAL_LITTER_BOX_BASIN -> buildLitterBasin(level, c);
            case GIANT_YARN_BALL_NEST -> buildYarnNest(level, c);
            case STRAY_CAT_CANTEEN -> buildCanteen(level, c);
            case ABANDONED_TOY_VAULT -> buildToyVault(level, c);
            case CRASHED_VELVET_AIRSHIP -> buildAirship(level, c);
            case ABYSS_SEAL_ANCHOR -> buildAbyssAnchor(level, c);
            case GIANT_CAT_SKELETON_RUINS -> buildSkeleton(level, c);
            case TEMPORAL_RIFT_PORTAL -> buildRiftPortal(level, c);
            case GREAT_WALL_SEGMENT -> buildWallSegment(level, c);
            case PHANTOM_CLOCK_TOWER -> buildClockTower(level, c);
            case VELVET_ALTAR_RUINS -> buildVelvetAltar(level, c);
        }
        if (random.nextInt(3) == 0) {
            spawnMudMob(level, c, random);
        }
    }

    public static void fillLootChest(ServerLevel level, BlockPos chestPos, RuinMatrixRegistry ruin) {
        if (!level.getBlockState(chestPos).is(Blocks.CHEST)) {
            level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 2);
        }
        BlockEntity be = level.getBlockEntity(chestPos);
        if (be instanceof ChestBlockEntity chest) {
            chest.setLootTable(ruin.lootTable(), level.random.nextLong());
        }
    }

    private static void platform(ServerLevel level, BlockPos c, int r, BlockState floor) {
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                if (dx * dx + dz * dz <= r * r) {
                    level.setBlock(c.offset(dx, 0, dz), floor, 2);
                }
            }
        }
    }

    private static void buildWarRuins(ServerLevel level, BlockPos c) {
        platform(level, c, 4, Blocks.COBBLESTONE.defaultBlockState());
        level.setBlock(c.offset(0, 1, 0), Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 2);
        fillLootChest(level, c.offset(0, 1, 2), RuinMatrixRegistry.WAR_RUINS);
    }

    private static void buildScratchingBarricade(ServerLevel level, BlockPos c) {
        for (int i = -2; i <= 2; i++) {
            level.setBlock(c.offset(i, 0, 0), ModBlocks.SCRATCHING_POST.get().defaultBlockState(), 2);
            level.setBlock(c.offset(0, 0, i), ModBlocks.WOVEN_WOOL.get().defaultBlockState(), 2);
        }
        fillLootChest(level, c.above(), RuinMatrixRegistry.SCRATCHING_BARRICADE);
    }

    private static void buildVelvetTower(ServerLevel level, BlockPos c) {
        for (int dy = 0; dy < 8; dy++) {
            level.setBlock(c.offset(0, dy, 0), ModBlocks.VELVET_PLANKS.get().defaultBlockState(), 2);
            level.setBlock(c.offset(1, dy, 0), ModBlocks.VELVET_PLANKS.get().defaultBlockState(), 2);
            level.setBlock(c.offset(0, dy, 1), ModBlocks.VELVET_PLANKS.get().defaultBlockState(), 2);
        }
        fillLootChest(level, c.offset(0, 8, 0), RuinMatrixRegistry.VELVET_TOWER);
    }

    private static void buildMortarPosition(ServerLevel level, BlockPos c) {
        platform(level, c, 2, Blocks.STONE_BRICKS.defaultBlockState());
        level.setBlock(c.above(), Blocks.CAULDRON.defaultBlockState(), 2);
        fillLootChest(level, c.offset(2, 1, 0), RuinMatrixRegistry.MORTAR_POSITION);
    }

    private static void buildMonument(ServerLevel level, BlockPos c) {
        for (int dy = 0; dy < 4; dy++) {
            level.setBlock(c.offset(0, dy, 0), Blocks.STONE_BRICKS.defaultBlockState(), 2);
        }
        level.setBlock(c.offset(0, 4, 0), ModBlocks.MEMORY_MONUMENT_BASE.get().defaultBlockState(), 2);
        fillLootChest(level, c.offset(1, 1, 1), RuinMatrixRegistry.FALLEN_HEROES_MONUMENT);
    }

    private static void buildAltar(ServerLevel level, BlockPos c) {
        platform(level, c, 1, Blocks.STONE_BRICKS.defaultBlockState());
        level.setBlock(c.above(), ModBlocks.FULL_MOON_ALTAR.get().defaultBlockState(), 2);
        fillLootChest(level, c.offset(0, 1, 2), RuinMatrixRegistry.FORGOTTEN_ALTAR);
    }

    private static void buildMudTemple(ServerLevel level, BlockPos c) {
        platform(level, c, 3, Blocks.STONE_BRICKS.defaultBlockState());
        for (int dx = -1; dx <= 1; dx++) {
            level.setBlock(c.offset(dx, 1, 0), ModBlocks.BLACK_MUD.get().defaultBlockState(), 2);
        }
        fillLootChest(level, c.offset(0, 1, 2), RuinMatrixRegistry.BLACK_MUD_CONTAMINATED_TEMPLE);
    }

    private static void buildMoonDungeon(ServerLevel level, BlockPos c) {
        for (int dy = -2; dy <= 0; dy++) {
            platform(level, c.offset(0, dy, 0), 2, Blocks.DEEPSLATE_BRICKS.defaultBlockState());
        }
        level.setBlock(c, Blocks.IRON_DOOR.defaultBlockState(), 2);
        fillLootChest(level, c.below(), RuinMatrixRegistry.MOON_SEALED_DUNGEON);
    }

    private static void buildStargazingWell(ServerLevel level, BlockPos c) {
        level.setBlock(c, Blocks.WATER.defaultBlockState(), 2);
        level.setBlock(c.above(3), ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState(), 2);
        fillLootChest(level, c.offset(2, 0, 0), RuinMatrixRegistry.FULL_MOON_STARGAZING_WELL);
    }

    private static void buildStoneCircle(ServerLevel level, BlockPos c) {
        for (int a = 0; a < 8; a++) {
            double ang = a * Math.PI / 4;
            int px = (int) (Math.cos(ang) * 3);
            int pz = (int) (Math.sin(ang) * 3);
            level.setBlock(c.offset(px, 0, pz), ModBlocks.MOONSTONE_BRICK.get().defaultBlockState(), 2);
        }
        fillLootChest(level, c.above(), RuinMatrixRegistry.MEMORY_STONE_CIRCLE);
    }

    private static void buildPotionLab(ServerLevel level, BlockPos c) {
        platform(level, c, 2, Blocks.OAK_PLANKS.defaultBlockState());
        level.setBlock(c.above(), ModBlocks.DISTILLER.get().defaultBlockState(), 2);
        fillLootChest(level, c.offset(1, 1, 0), RuinMatrixRegistry.HOLY_WATER_POTION_LAB);
    }

    private static void buildMudFarm(ServerLevel level, BlockPos c) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                level.setBlock(c.offset(dx, 0, dz), Blocks.FARMLAND.defaultBlockState(), 2);
                if (level.random.nextBoolean()) {
                    level.setBlock(c.offset(dx, 1, dz), ModBlocks.BLACK_MUD.get().defaultBlockState(), 2);
                }
            }
        }
        fillLootChest(level, c.offset(0, 1, 4), RuinMatrixRegistry.BLACK_MUD_FARM);
    }

    private static void buildCardboardCamp(ServerLevel level, BlockPos c) {
        for (int i = 0; i < 3; i++) {
            level.setBlock(c.offset(i * 2, 0, 0), ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState(), 2);
            level.setBlock(c.offset(i * 2, 1, 0), ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState(), 2);
        }
        fillLootChest(level, c.offset(0, 0, 2), RuinMatrixRegistry.CARDBOARD_REFUGEE_CAMP);
    }

    private static void buildGreenhouse(ServerLevel level, BlockPos c) {
        platform(level, c, 2, Blocks.GLASS.defaultBlockState());
        level.setBlock(c.above(), ModBlocks.CATNIP.get().defaultBlockState(), 2);
        fillLootChest(level, c.offset(0, 0, 3), RuinMatrixRegistry.CATNIP_SMUGGLING_GREENHOUSE);
    }

    private static void buildWorkshop(ServerLevel level, BlockPos c) {
        level.setBlock(c, ModBlocks.CAT_CORE_ENGINEERING.get().defaultBlockState(), 2);
        level.setBlock(c.above(), Blocks.ANVIL.defaultBlockState(), 2);
        fillLootChest(level, c.offset(1, 0, 0), RuinMatrixRegistry.ABANDONED_GEAR_WORKSHOP);
    }

    private static void buildSewer(ServerLevel level, BlockPos c) {
        for (int dy = -3; dy <= 0; dy++) {
            level.setBlock(c.offset(0, dy, 0), Blocks.WATER.defaultBlockState(), 2);
        }
        fillLootChest(level, c.offset(1, -2, 0), RuinMatrixRegistry.BLIND_WATER_SEWER);
    }

    private static void buildForge(ServerLevel level, BlockPos c) {
        platform(level, c, 2, Blocks.STONE_BRICKS.defaultBlockState());
        level.setBlock(c.above(), Blocks.FURNACE.defaultBlockState(), 2);
        level.setBlock(c.offset(1, 1, 0), Blocks.ANVIL.defaultBlockState(), 2);
        fillLootChest(level, c.offset(0, 1, 2), RuinMatrixRegistry.IRONPAW_FORGE_RUINS);
    }

    private static void buildTreeTower(ServerLevel level, BlockPos c) {
        com.cocojenna.world.ArchitectureBuilders.buildVelvetTree(level, c, 10);
        fillLootChest(level, c.above(8), RuinMatrixRegistry.ALIENATED_CAT_TREE_TOWER);
    }

    private static void buildTreehouse(ServerLevel level, BlockPos c) {
        com.cocojenna.world.ArchitectureBuilders.buildYarnHouse(level, c.getX(), c.getY() + 6, c.getZ(),
                net.minecraft.world.level.material.MapColor.COLOR_PURPLE);
        fillLootChest(level, c.above(7), RuinMatrixRegistry.VELVET_TREEHOUSE_REMAINS);
    }

    private static void buildLitterBasin(ServerLevel level, BlockPos c) {
        platform(level, c, 3, ModBlocks.SALT_BLOCK.get().defaultBlockState());
        level.setBlock(c.above(), ModBlocks.SCRATCHING_POST.get().defaultBlockState(), 2);
        fillLootChest(level, c.offset(0, 1, 2), RuinMatrixRegistry.ROYAL_LITTER_BOX_BASIN);
    }

    private static void buildYarnNest(ServerLevel level, BlockPos c) {
        level.setBlock(c, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState(), 2);
        level.setBlock(c.above(), ModBlocks.WOVEN_WOOL.get().defaultBlockState(), 2);
        fillLootChest(level, c.offset(1, 0, 1), RuinMatrixRegistry.GIANT_YARN_BALL_NEST);
    }

    private static void buildCanteen(ServerLevel level, BlockPos c) {
        platform(level, c, 2, ModBlocks.VELVET_PLANKS.get().defaultBlockState());
        level.setBlock(c.above(), ModBlocks.FOOD_BOWL.get().defaultBlockState(), 2);
        fillLootChest(level, c.offset(0, 1, 0), RuinMatrixRegistry.STRAY_CAT_CANTEEN);
    }

    private static void buildToyVault(ServerLevel level, BlockPos c) {
        for (int dy = 0; dy < 3; dy++) {
            level.setBlock(c.offset(0, dy, 0), Blocks.IRON_BLOCK.defaultBlockState(), 2);
        }
        fillLootChest(level, c.offset(0, 1, 0), RuinMatrixRegistry.ABANDONED_TOY_VAULT);
    }

    private static void buildAirship(ServerLevel level, BlockPos c) {
        for (int dx = -3; dx <= 3; dx++) {
            level.setBlock(c.offset(dx, 2, 0), ModBlocks.VELVET_PLANKS.get().defaultBlockState(), 2);
        }
        level.setBlock(c.offset(0, 1, 0), ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState(), 2);
        fillLootChest(level, c.offset(0, 2, 1), RuinMatrixRegistry.CRASHED_VELVET_AIRSHIP);
    }

    private static void buildAbyssAnchor(ServerLevel level, BlockPos c) {
        for (int dy = 0; dy < 5; dy++) {
            level.setBlock(c.offset(0, dy, 0), Blocks.OBSIDIAN.defaultBlockState(), 2);
        }
        fillLootChest(level, c.above(5), RuinMatrixRegistry.ABYSS_SEAL_ANCHOR);
    }

    private static void buildSkeleton(ServerLevel level, BlockPos c) {
        for (int i = 0; i < 12; i++) {
            level.setBlock(c.offset(i - 6, 0, 0), Blocks.BONE_BLOCK.defaultBlockState(), 2);
        }
        fillLootChest(level, c.offset(0, 1, 2), RuinMatrixRegistry.GIANT_CAT_SKELETON_RUINS);
    }

    private static void buildRiftPortal(ServerLevel level, BlockPos c) {
        level.setBlock(c, ModBlocks.CAT_KINGDOM_PORTAL.get().defaultBlockState(), 2);
        fillLootChest(level, c.offset(1, 0, 0), RuinMatrixRegistry.TEMPORAL_RIFT_PORTAL);
    }

    private static void buildWallSegment(ServerLevel level, BlockPos c) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = 0; dy < 4; dy++) {
                level.setBlock(c.offset(dx, dy, 0), ModBlocks.STARDUST_BRICK.get().defaultBlockState(), 2);
            }
        }
        fillLootChest(level, c.offset(0, 1, 1), RuinMatrixRegistry.GREAT_WALL_SEGMENT);
    }

    private static void buildClockTower(ServerLevel level, BlockPos c) {
        for (int dy = 0; dy < 10; dy++) {
            level.setBlock(c.offset(0, dy, 0), ModBlocks.MOONSTONE_BRICK.get().defaultBlockState(), 2);
        }
        level.setBlock(c.offset(0, 10, 0), Blocks.REDSTONE_LAMP.defaultBlockState(), 2);
        fillLootChest(level, c.offset(1, 5, 0), RuinMatrixRegistry.PHANTOM_CLOCK_TOWER);
    }

    private static void buildVelvetAltar(ServerLevel level, BlockPos c) {
        platform(level, c, 2, ModBlocks.VELVET_PLANKS.get().defaultBlockState());
        level.setBlock(c.above(), ModBlocks.FULL_MOON_ALTAR.get().defaultBlockState(), 2);
        fillLootChest(level, c.offset(0, 1, 2), RuinMatrixRegistry.VELVET_ALTAR_RUINS);
    }

    private static void spawnMudMob(ServerLevel level, BlockPos c, RandomSource random) {
        var type = random.nextBoolean() ? ModEntities.MUD_GUARD.get() : ModEntities.MUD_FARMER.get();
        var mob = type.create(level);
        if (mob != null) {
            mob.moveTo(c.getX() + 0.5, c.getY() + 1, c.getZ() + 0.5, 0, 0);
            level.addFreshEntity(mob);
        }
    }
}
