package com.cocojenna.world;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 十五座區域地牢程序生成（設計書 3.1–3.2）.
 * 每座 5–15 房，主題材質／裝飾／守護者各異。
 */
public final class DungeonGenerators {

    private static final String[] IDS = {
            "elder_cellar", "weaver_workshop", "moon_well", "rust_sewer", "forgotten_confessional",
            "saltwind_wreck", "watchtower_vault", "wind_cavern", "ashura_trial", "tower_prison",
            "stardust_tomb", "forgotten_vault", "cardboard_depth", "moonlight_grotto", "catnip_mine"
    };

    private record Spec(int index, BlockPos anchor, BlockPos marker, @Nullable BlockPos ladderSurface,
            int minRooms, int maxRooms, ProceduralDungeonBuilder.Theme theme,
            EntityType<? extends Mob> boss, BiConsumer<ServerLevel, int[]> decorator) {}

    private static final Map<String, Spec> SPECS = new LinkedHashMap<>();

    static {
        reg("elder_cellar", 0,
                new BlockPos(2, 28, 0), new BlockPos(2, 28, -48), new BlockPos(2, 64, 0),
                5, 15, theme(ModBlocks.MOONSTONE_BRICK.get().defaultBlockState(),
                        ModBlocks.WOVEN_WOOL.get().defaultBlockState(),
                        ModBlocks.YARN_BALL_LAMP.get().defaultBlockState()),
                ModEntities.FUR_BALL_SPIRIT.get(), DungeonGenerators::decorateElder);

        reg("weaver_workshop", 1,
                new BlockPos(-128, 24, -200), new BlockPos(-128, 24, -248), null,
                6, 12, theme(ModBlocks.VELVET_BLOCK.get().defaultBlockState(),
                        ModBlocks.STARDUST_SOIL.get().defaultBlockState(),
                        ModBlocks.VELVET_CARPET.get().defaultBlockState()),
                ModEntities.MEMORY_MOTH.get(), DungeonGenerators::decorateWeaver);

        reg("moon_well", 2,
                new BlockPos(128, 24, -128), new BlockPos(128, 24, -176), null,
                5, 14, theme(ModBlocks.MOONSTONE_BRICK.get().defaultBlockState(),
                        ModBlocks.MOONSTONE_BLOCK.get().defaultBlockState(),
                        ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState()),
                ModEntities.MOON_ALLEY_WRAITH.get(), DungeonGenerators::decorateMoon);

        reg("rust_sewer", 3,
                new BlockPos(384, 20, 256), new BlockPos(384, 20, 208), null,
                7, 15, theme(ModBlocks.SPORE_METAL_BLOCK.get().defaultBlockState(),
                        ModBlocks.STARDUST_BRICK.get().defaultBlockState(),
                        ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState()),
                ModEntities.GEAR_OVERLORD.get(), DungeonGenerators::decorateRust);

        reg("forgotten_confessional", 4,
                new BlockPos(384, 76, -384), new BlockPos(384, 76, -432), null,
                5, 11, theme(ModBlocks.WOVEN_WOOL.get().defaultBlockState(),
                        ModBlocks.STARDUST_BRICK.get().defaultBlockState(),
                        ModBlocks.YARN_BALL_LAMP.get().defaultBlockState()),
                ModEntities.SILENCED_ONE.get(), DungeonGenerators::decorateConfessional);

        reg("saltwind_wreck", 5,
                new BlockPos(-384, 16, 256), new BlockPos(-384, 16, 208), null,
                6, 13, theme(Blocks.DARK_OAK_PLANKS.defaultBlockState(),
                        Blocks.SPRUCE_PLANKS.defaultBlockState(),
                        Blocks.SEA_LANTERN.defaultBlockState()),
                ModEntities.BLIND_WATER_LEECH.get(), DungeonGenerators::decorateSaltwind);

        reg("watchtower_vault", 6,
                new BlockPos(256, 24, -384), new BlockPos(256, 24, -432), null,
                5, 12, theme(Blocks.SPRUCE_PLANKS.defaultBlockState(),
                        ModBlocks.SALT_BLOCK.get().defaultBlockState(),
                        ModBlocks.PURE_LIGHT_TOWER.get().defaultBlockState()),
                ModEntities.ORIGAMI_CROW.get(), DungeonGenerators::decorateWatchtower);

        reg("wind_cavern", 7,
                new BlockPos(512, 64, -512), new BlockPos(512, 64, -560), null,
                6, 14, theme(Blocks.DEEPSLATE_BRICKS.defaultBlockState(),
                        Blocks.DEEPSLATE_TILES.defaultBlockState(),
                        Blocks.CHAIN.defaultBlockState()),
                ModEntities.HOWLING_SQUALL.get(), DungeonGenerators::decorateWind);

        reg("ashura_trial", 8,
                new BlockPos(-512, 24, -512), new BlockPos(-512, 24, -560), null,
                8, 15, theme(Blocks.MOSSY_COBBLESTONE.defaultBlockState(),
                        Blocks.GRASS_BLOCK.defaultBlockState(),
                        Blocks.END_ROD.defaultBlockState()),
                ModEntities.ASHURA_PHANTOM.get(), DungeonGenerators::decorateAshura);

        reg("tower_prison", 9,
                new BlockPos(-256, 48, 512), new BlockPos(-256, 48, 464), null,
                5, 13, theme(ModBlocks.SHADOW_CRYSTAL_BLOCK.get().defaultBlockState(),
                        ModBlocks.SALT_BLOCK.get().defaultBlockState(),
                        ModBlocks.SEAL_PEDESTAL.get().defaultBlockState()),
                ModEntities.FALLEN_VELVET.get(), DungeonGenerators::decorateTower);

        reg("stardust_tomb", 10,
                new BlockPos(640, 20, 128), new BlockPos(640, 20, 80), null,
                6, 14, theme(ModBlocks.STARDUST_SOIL.get().defaultBlockState(),
                        ModBlocks.STARDUST_BRICK.get().defaultBlockState(),
                        ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState()),
                ModEntities.FORGOTTEN_WISP.get(), DungeonGenerators::decorateStardust);

        reg("forgotten_vault", 11,
                new BlockPos(-640, 16, -128), new BlockPos(-640, 16, -176), null,
                7, 15, theme(ModBlocks.SHADOW_CRYSTAL_BLOCK.get().defaultBlockState(),
                        ModBlocks.BLACK_MUD.get().defaultBlockState(),
                        ModBlocks.SEAL_PEDESTAL.get().defaultBlockState()),
                ModEntities.GRIEF_AMALGAM.get(), DungeonGenerators::decorateVault);

        reg("cardboard_depth", 12,
                new BlockPos(192, 18, 640), new BlockPos(192, 18, 592), null,
                5, 12, theme(Blocks.BROWN_WOOL.defaultBlockState(),
                        Blocks.CARVED_PUMPKIN.defaultBlockState(),
                        ModBlocks.TOY_BOX.get().defaultBlockState()),
                ModEntities.CORRUGATA_QUEEN.get(), DungeonGenerators::decorateCardboard);

        reg("moonlight_grotto", 13,
                new BlockPos(-192, 14, 640), new BlockPos(-192, 14, 592), null,
                6, 13, theme(ModBlocks.MOONSTONE_BRICK.get().defaultBlockState(),
                        Blocks.PRISMARINE.defaultBlockState(),
                        Blocks.SEA_LANTERN.defaultBlockState()),
                ModEntities.MOON_GUARDIAN.get(), DungeonGenerators::decorateGrotto);

        reg("catnip_mine", 14,
                new BlockPos(640, 22, -640), new BlockPos(640, 22, -688), null,
                6, 14, theme(Blocks.DEEPSLATE_BRICKS.defaultBlockState(),
                        ModBlocks.CATNIP.get().defaultBlockState(),
                        ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState()),
                ModEntities.CATNIP_DRAGON.get(), DungeonGenerators::decorateCatnip);
    }

    private DungeonGenerators() {}

    private static void reg(String id, int index, BlockPos anchor, BlockPos marker,
            @Nullable BlockPos ladderSurface, int minRooms, int maxRooms,
            ProceduralDungeonBuilder.Theme theme, EntityType<? extends Mob> boss,
            BiConsumer<ServerLevel, int[]> decorator) {
        SPECS.put(id, new Spec(index, anchor, marker, ladderSurface, minRooms, maxRooms, theme, boss, decorator));
    }

    private static ProceduralDungeonBuilder.Theme theme(BlockState wall, BlockState floor, BlockState accent) {
        return new ProceduralDungeonBuilder.Theme(wall, floor, accent);
    }

    public static BlockPos ensure(ServerLevel level, String id, @Nullable ServerPlayer player) {
        Spec spec = SPECS.get(id);
        if (spec == null) {
            return BlockPos.ZERO;
        }
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            return spec.anchor().above();
        }
        if (!level.getBlockState(spec.marker()).is(ModBlocks.PURR_CRYSTAL_BLOCK.get())) {
            ProceduralDungeonBuilder.buildDungeon(level, id, spec.index(), spec.anchor(),
                    spec.minRooms(), spec.maxRooms(), spec.theme(), spec.ladderSurface(),
                    spec.boss(), spec.decorator());
            level.setBlock(spec.marker(), ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState(), 2);
        }
        if (player != null) {
            com.cocojenna.exploration.DungeonWorldData.syncBossStateForPlayer(level, player);
        }
        return spec.anchor().above();
    }

    public static boolean isGenerated(ServerLevel level, String id) {
        Spec spec = SPECS.get(id);
        return spec != null && level.getBlockState(spec.marker()).is(ModBlocks.PURR_CRYSTAL_BLOCK.get());
    }

    public static int indexOf(String id) {
        Spec spec = SPECS.get(id);
        return spec == null ? -1 : spec.index();
    }

    public static String idAt(int index) {
        if (index < 0 || index >= IDS.length) return "";
        return IDS[index];
    }

    private static void decorateElder(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], ModBlocks.TOY_BOX.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.SCRATCHING_POST.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] + 3, ModBlocks.SOCKETING_TABLE.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 3, r[1] + 1, r[2] + 2, Blocks.BOOKSHELF.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 3, r[1] + 1, r[2] - 2, ModBlocks.ANCIENT_STONE_TABLET.get().defaultBlockState());
        placeRewardChest(level, r, "elder_cellar");
        maybeBlackMud(level, r);
    }

    private static void decorateWeaver(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] + 2, Blocks.LOOM.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], ModBlocks.CAT_BED.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.VELVET_CARPET.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 2, r[2], ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 3, r[1] + 1, r[2] + 1, ModBlocks.PICTURE_BOOK_STAND.get().defaultBlockState());
        placeRewardChest(level, r, "weaver_workshop");
        maybeBlackMud(level, r);
    }

    private static void decorateMoon(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] - 1, r[2], Blocks.WATER.defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] + 3, ModBlocks.FULL_MOON_ALTAR.get().defaultBlockState());
        placeRewardChest(level, r, "moon_well");
        maybeBlackMud(level, r);
    }

    private static void decorateRust(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] + 2, Blocks.CHAIN.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], ModBlocks.DISTILLER.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.SPORE_METAL_BLOCK.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 1, r[1] + 1, r[2] + 1, ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 1, r[1] + 1, r[2] - 1, Blocks.IRON_BARS.defaultBlockState());
        placeRewardChest(level, r, "rust_sewer");
        maybeBlackMud(level, r);
    }

    private static void placeRewardChest(ServerLevel level, int[] r, String dungeonId) {
        ProceduralDungeonBuilder.placeLootChest(level, r[0], r[1] + 1, r[2] + 3,
                new net.minecraft.resources.ResourceLocation("cocojenna", "chests/dungeon_" + dungeonId));
    }

    private static void placeRewardChest(ServerLevel level, int[] r) {
        placeRewardChest(level, r, "common");
    }

    private static void decorateConfessional(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] - 2, ModBlocks.CAT_BED.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.VELVET_CARPET.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], Blocks.CANDLE.defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] + 2, ModBlocks.MURAL_FRAGMENT.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 1, r[1] + 1, r[2] - 1, ModBlocks.SUSPICIOUS_WALL.get().defaultBlockState());
        placeRewardChest(level, r, "forgotten_confessional");
        maybeBlackMud(level, r);
    }

    private static void decorateSaltwind(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], Blocks.OAK_STAIRS.defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1], r[2] + 2, Blocks.WATER.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], Blocks.BARREL.defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 2, r[2], Blocks.CHAIN.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 1, r[1] + 1, r[2] - 1, Blocks.SEA_LANTERN.defaultBlockState());
        placeRewardChest(level, r, "saltwind_wreck");
        maybeBlackMud(level, r);
    }

    private static void decorateWatchtower(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] + 2, ModBlocks.SALT_CRYSTAL.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], Blocks.BARREL.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.PURE_LIGHT_TOWER.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 3, r[2], Blocks.LANTERN.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 1, r[1] + 1, r[2] - 1, Blocks.SCAFFOLDING.defaultBlockState());
        placeRewardChest(level, r, "watchtower_vault");
        maybeBlackMud(level, r);
    }

    private static void decorateWind(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0], r[1] + 2, r[2], Blocks.CHAIN.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.SALT_CRYSTAL.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], Blocks.CAMPFIRE.defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] + 2, Blocks.IRON_BARS.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 1, r[1] + 1, r[2] - 1, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        placeRewardChest(level, r, "wind_cavern");
        maybeBlackMud(level, r);
    }

    private static void decorateAshura(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], Blocks.MOSSY_COBBLESTONE.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2] + 2, Blocks.END_ROD.defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] - 2, ModBlocks.ANCIENT_STONE_TABLET.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 1, r[1] + 2, r[2], Blocks.SOUL_LANTERN.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 1, r[1] + 1, r[2] + 1, ModBlocks.CAT_BED.get().defaultBlockState());
        placeRewardChest(level, r, "ashura_trial");
        maybeBlackMud(level, r);
    }

    private static void decorateTower(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] - 2, ModBlocks.MEMORY_MONUMENT_BASE.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.BLACK_MUD.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], ModBlocks.SEAL_PEDESTAL.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 2, r[2], ModBlocks.SHADOW_CRYSTAL_BLOCK.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 1, r[1] + 1, r[2] + 1, Blocks.CANDLE.defaultBlockState());
        placeRewardChest(level, r, "tower_prison");
        maybeBlackMud(level, r);
    }

    private static void decorateStardust(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], ModBlocks.STARDUST_SOIL.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] + 2, ModBlocks.STARDUST_BRICK.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 1, r[1] + 2, r[2], Blocks.END_ROD.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 1, r[1] + 1, r[2] - 1, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        placeRewardChest(level, r, "stardust_tomb");
        maybeBlackMud(level, r);
    }

    private static void decorateVault(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] - 2, ModBlocks.SEAL_PEDESTAL.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.BLACK_MUD.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], ModBlocks.SUSPICIOUS_WALL.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 2, r[2], ModBlocks.MURAL_FRAGMENT.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 1, r[1] + 1, r[2] + 1, ModBlocks.ANCIENT_STONE_TABLET.get().defaultBlockState());
        placeRewardChest(level, r, "forgotten_vault");
        maybeBlackMud(level, r);
    }

    private static void decorateCardboard(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], Blocks.BROWN_WOOL.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.TOY_BOX.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] + 2, ModBlocks.CARDBOARD_BLOCK.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 1, r[1] + 2, r[2], ModBlocks.SCRATCHING_POST.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 1, r[1] + 1, r[2] - 1, ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState());
        placeRewardChest(level, r, "cardboard_depth");
        maybeBlackMud(level, r);
    }

    private static void decorateGrotto(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0], r[1], r[2] + 2, Blocks.WATER.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 2, r[2], Blocks.SEA_LANTERN.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 1, r[1] + 1, r[2] - 1, ModBlocks.FULL_MOON_ALTAR.get().defaultBlockState());
        placeRewardChest(level, r, "moonlight_grotto");
        maybeBlackMud(level, r);
    }

    private static void decorateCatnip(ServerLevel level, int[] r) {
        ArchitectureBuilders.set(level, r[0] - 2, r[1] + 1, r[2], ModBlocks.CATNIP.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 2, r[1] + 1, r[2], ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] + 2, Blocks.IRON_ORE.defaultBlockState());
        ArchitectureBuilders.set(level, r[0] + 1, r[1] + 2, r[2], ModBlocks.AROMA_DISTILLER.get().defaultBlockState());
        ArchitectureBuilders.set(level, r[0] - 1, r[1] + 1, r[2] - 1, ModBlocks.FOOD_BOWL.get().defaultBlockState());
        placeRewardChest(level, r, "catnip_mine");
        maybeBlackMud(level, r);
    }

    private static void maybeBlackMud(ServerLevel level, int[] r) {
        if (level.random.nextFloat() < 0.3f) {
            ArchitectureBuilders.set(level, r[0], r[1] + 1, r[2] + 2, ModBlocks.BLACK_MUD.get().defaultBlockState());
        }
    }
}
