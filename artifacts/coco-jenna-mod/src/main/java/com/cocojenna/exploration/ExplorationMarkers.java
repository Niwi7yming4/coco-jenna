package com.cocojenna.exploration;

import com.cocojenna.block.AncientStoneTabletBlock;
import com.cocojenna.block.DungeonEntranceBlock;
import com.cocojenna.block.MuralFragmentBlock;
import com.cocojenna.entity.WildCatEntity;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import com.cocojenna.world.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** 各區域傳說／隱藏牆／怪貓放置（設計書 2.1 / 4.2 / 5.1）. */
public final class ExplorationMarkers {

    private ExplorationMarkers() {}

    public static void placeFirstCry(ServerLevel level) {
        setTablet(level, -6, FirstCryVillageGenerator.FLOOR_Y, -10, 9);
        setMural(level, 6, FirstCryVillageGenerator.FLOOR_Y + 1, -12, 14, true);
        setDungeon(level, 2, FirstCryVillageGenerator.FLOOR_Y, 0, 0);
        setHidden(level, -4, FirstCryVillageGenerator.FLOOR_Y + 1, -6);
        spawnWild(level, WildCatType.SONG_CAT.id(), 8, FirstCryVillageGenerator.FLOOR_Y + 1, 12);
        spawnWild(level, WildCatType.MOTH_CAT.id(), -14, FirstCryVillageGenerator.FLOOR_Y + 1, -8);
    }

    public static void placeVelvetForest(ServerLevel level, BlockPos center) {
        setTablet(level, center.getX() - 10, center.getY(), center.getZ() + 4, 0);
        setTablet(level, center.getX() + 8, center.getY() + 1, center.getZ() - 6, 11);
        setHidden(level, center.getX() + 5, center.getY() + 1, center.getZ() + 8);
        spawnWild(level, WildCatType.MOTH_CAT.id(), center.getX() - 4, center.getY() + 1, center.getZ() - 3);
        spawnWild(level, WildCatType.STING_TAIL.id(), center.getX() + 6, center.getY() + 1, center.getZ() + 2);
    }

    public static void placeMoonAlley(ServerLevel level, BlockPos center) {
        setMural(level, center.getX() + 3, center.getY() + 1, center.getZ() - 4, 1, true);
        setHidden(level, center.getX() - 2, center.getY() + 1, center.getZ() + 5);
        spawnWild(level, WildCatType.MOON_TABBY.id(), center.getX() + 1, center.getY() + 1, center.getZ() + 1);
    }

    public static void placeGearTown(ServerLevel level, BlockPos center) {
        setTablet(level, center.getX() - 6, center.getY(), center.getZ() + 3, 2);
        setDungeon(level, center.getX() + 10, center.getY(), center.getZ() - 8, 3);
        spawnWild(level, WildCatType.BOX_LURKER.id(), center.getX() + 4, center.getY() + 1, center.getZ() + 2);
        spawnWild(level, WildCatType.RUST_GEAR.id(), center.getX() - 8, center.getY() + 1, center.getZ() - 4);
    }

    public static void placeBlindPort(ServerLevel level, BlockPos center) {
        setTablet(level, center.getX() + 5, center.getY(), center.getZ() - 3, 4);
        setMural(level, center.getX() - 4, center.getY() + 1, center.getZ() + 6, 12, true);
        setDungeon(level, center.getX(), center.getY() - 1, center.getZ() + 12, 5);
        spawnWild(level, WildCatType.BLIND_LIGHT_KEEPER.id(), center.getX() - 2, center.getY() + 1, center.getZ() + 4);
    }

    public static void placeDawnHighlands(ServerLevel level, BlockPos center) {
        setMural(level, center.getX() + 2, center.getY() + 1, center.getZ() - 2, 5, false);
        setTablet(level, center.getX() - 5, center.getY() + 2, center.getZ() + 4, 13);
        setDungeon(level, center.getX() + 8, center.getY(), center.getZ() + 8, 6);
        spawnWild(level, WildCatType.HONEY_CLAW.id(), center.getX() + 3, center.getY() + 1, center.getZ() + 5);
    }

    public static void placeRainbowCanyon(ServerLevel level, BlockPos center) {
        setTablet(level, center.getX(), center.getY() + 1, center.getZ(), 15);
        setHidden(level, center.getX() + 4, center.getY() + 1, center.getZ() - 3);
        spawnWild(level, WildCatType.DUNE_STALKER.id(), center.getX() - 2, center.getY() + 1, center.getZ() + 2);
    }

    public static void placeCatnipHighlands(ServerLevel level, BlockPos center) {
        setTablet(level, center.getX() + 3, center.getY(), center.getZ(), 16);
        setDungeon(level, center.getX() - 6, center.getY(), center.getZ() + 8, 14);
        spawnWild(level, WildCatType.HONEY_CLAW.id(), center.getX() - 1, center.getY() + 1, center.getZ() + 4);
    }

    public static void placeCardboardSlums(ServerLevel level, BlockPos center) {
        setMural(level, center.getX() - 2, center.getY() + 1, center.getZ(), 17, true);
        setDungeon(level, center.getX() + 8, center.getY(), center.getZ() - 4, 12);
        spawnWild(level, WildCatType.BOX_LURKER.id(), center.getX() + 2, center.getY() + 1, center.getZ() + 1);
    }

    public static void placeMoonlightBeach(ServerLevel level, BlockPos center) {
        setTablet(level, center.getX(), center.getY(), center.getZ() + 2, 18);
        setDungeon(level, center.getX() - 10, center.getY(), center.getZ() - 6, 13);
        spawnWild(level, WildCatType.MOON_TABBY.id(), center.getX() + 1, center.getY() + 1, center.getZ() - 1);
    }

    public static void placeStardustDesert(ServerLevel level, BlockPos center) {
        setTablet(level, center.getX() - 3, center.getY(), center.getZ() - 2, 19);
        setDungeon(level, center.getX() + 10, center.getY(), center.getZ() + 6, 10);
        spawnWild(level, WildCatType.DUNE_STALKER.id(), center.getX() + 4, center.getY() + 1, center.getZ() + 3);
    }

    public static void placeForgottenWastes(ServerLevel level, BlockPos center) {
        setMural(level, center.getX(), center.getY() + 1, center.getZ() - 4, 21, true);
        setDungeon(level, center.getX() - 8, center.getY(), center.getZ() + 10, 11);
        spawnWild(level, WildCatType.VOID_GAZER.id(), center.getX() + 5, center.getY() + 1, center.getZ());
    }

    public static void placeFirstCryPlains(ServerLevel level, BlockPos center) {
        setTablet(level, center.getX() - 6, center.getY(), center.getZ() - 4, 9);
        setMural(level, center.getX() + 4, center.getY() + 1, center.getZ() - 6, 14, true);
        setHidden(level, center.getX() - 2, center.getY() + 1, center.getZ() + 3);
        spawnWild(level, WildCatType.SONG_CAT.id(), center.getX() + 2, center.getY() + 1, center.getZ() + 5);
    }

    public static void placeHowlingGorge(ServerLevel level, BlockPos center) {
        setTablet(level, center.getX() - 4, center.getY() + 1, center.getZ() + 3, 6);
        setDungeon(level, center.getX() + 6, center.getY(), center.getZ() - 6, 7);
        spawnWild(level, WildCatType.STORM_RIDER.id(), center.getX(), center.getY() + 2, center.getZ());
        spawnWild(level, WildCatType.FROST_WHISKER.id(), center.getX() - 5, center.getY() + 2, center.getZ() - 2);
    }

    public static void placeBlindWaterRiver(ServerLevel level, BlockPos center) {
        setTablet(level, center.getX() + 5, center.getY(), center.getZ() - 3, 4);
        setMural(level, center.getX() - 4, center.getY() + 1, center.getZ() + 6, 12, true);
        setDungeon(level, center.getX(), center.getY() - 1, center.getZ() + 10, 5);
        spawnWild(level, WildCatType.BLIND_LIGHT_KEEPER.id(), center.getX() - 2, center.getY() + 1, center.getZ() + 4);
    }

    public static void placeForgottenTower(ServerLevel level, BlockPos center) {
        setTablet(level, center.getX(), center.getY() - 2, center.getZ() + 6, 8);
        setDungeon(level, center.getX() - 6, center.getY() - 4, center.getZ(), 9);
        spawnWild(level, WildCatType.TIME_SKIP.id(), center.getX() + 2, center.getY() - 1, center.getZ() - 2);
    }

    public static void placeRegionHub(ServerLevel level, String region, BlockPos center) {
        switch (region) {
            case "central_plaza" -> {
                setTablet(level, center.getX() + 4, center.getY(), center.getZ(), 10);
                setHidden(level, center.getX() - 3, center.getY() + 1, center.getZ() + 2);
            }
            case "sleep_sanctuary" -> {
                setMural(level, center.getX(), center.getY() + 2, center.getZ() - 5, 3, true);
                setDungeon(level, center.getX() + 8, center.getY(), center.getZ() - 2, 4);
            }
            case "howling_gorge" -> {
                setTablet(level, center.getX() - 4, center.getY() + 1, center.getZ() + 3, 6);
                setDungeon(level, center.getX() + 6, center.getY(), center.getZ() - 6, 7);
                spawnWild(level, WildCatType.STORM_RIDER.id(), center.getX(), center.getY() + 4, center.getZ());
                spawnWild(level, WildCatType.SALT_LIZARD.id(), center.getX() + 5, center.getY() + 1, center.getZ() + 4);
                spawnWild(level, WildCatType.FROST_WHISKER.id(), center.getX() - 6, center.getY() + 3, center.getZ() - 2);
            }
            case "phantom_maze" -> {
                setMural(level, center.getX() + 2, center.getY() + 1, center.getZ() + 2, 7, true);
                setDungeon(level, center.getX() - 8, center.getY(), center.getZ() - 8, 8);
                spawnWild(level, WildCatType.MIRAGE_SHADE.id(), center.getX() + 4, center.getY() + 1, center.getZ() - 4);
            }
            default -> { }
        }
    }

    private static void setTablet(Level level, int x, int y, int z, int loreId) {
        level.setBlock(new BlockPos(x, y, z),
                ModBlocks.ANCIENT_STONE_TABLET.get().defaultBlockState()
                        .setValue(AncientStoneTabletBlock.LORE, loreId), 2);
    }

    private static void setMural(Level level, int x, int y, int z, int loreId, boolean covered) {
        level.setBlock(new BlockPos(x, y, z),
                ModBlocks.MURAL_FRAGMENT.get().defaultBlockState()
                        .setValue(MuralFragmentBlock.LORE, loreId)
                        .setValue(MuralFragmentBlock.COVERED, covered), 2);
    }

    private static void setDungeon(Level level, int x, int y, int z, int dungeonId) {
        BlockPos pos = new BlockPos(x, y, z);
        level.setBlock(pos,
                ModBlocks.DUNGEON_ENTRANCE.get().defaultBlockState()
                        .setValue(DungeonEntranceBlock.DUNGEON, dungeonId)
                        .setValue(DungeonEntranceBlock.CLEARED, false), 2);
        if (level instanceof ServerLevel server) {
            DungeonEntranceRegistry.register(pos, dungeonId);
        }
    }

    private static void setHidden(Level level, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        level.setBlock(pos, ModBlocks.SUSPICIOUS_WALL.get().defaultBlockState(), 2);
        ExplorationGuideManager.registerHiddenWall(pos);
    }

    private static void spawnWild(ServerLevel level, int typeId, int x, int y, int z) {
        WildCatEntity cat = ModEntities.WILD_CAT.get().create(level);
        if (cat == null) return;
        cat.setCatTypeId(typeId);
        cat.moveTo(x + 0.5, y, z + 0.5, level.random.nextFloat() * 360f, 0f);
        level.addFreshEntity(cat);
    }
}
