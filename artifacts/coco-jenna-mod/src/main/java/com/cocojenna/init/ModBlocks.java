package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CocoJennaMod.MOD_ID);

    // ══════════════════════════════════════════════════════════════════════
    // 功能方塊
    // ══════════════════════════════════════════════════════════════════════

    /** 蒸餾台 — 提煉朱槿花之淚、處理黑泥殘骸  🧪 */
    public static final RegistryObject<Block> DISTILLER = registerWithItem(
            "distiller",
            () -> new DistillerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5f)
                    .lightLevel(s -> 4)
                    .noOcclusion()));

    /** 香氛蒸餾台 — 製作香氛 🌸 */
    public static final RegistryObject<Block> AROMA_DISTILLER = registerWithItem(
            "aroma_distiller",
            () -> new AromaDistillerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .requiresCorrectToolForDrops()
                    .strength(3.0f)
                    .lightLevel(s -> 6)
                    .noOcclusion()));

    /** 貓床 — 貓咪睡覺點 🛏️ */
    public static final RegistryObject<Block> CAT_BED = registerWithItem(
            "cat_bed",
            () -> new CatBedBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOL)
                    .strength(0.5f)
                    .noOcclusion()));

    /** 食物碗 — 貓咪進食點 🍽️ */
    public static final RegistryObject<Block> FOOD_BOWL = registerWithItem(
            "food_bowl",
            () -> new FoodBowlBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(1.0f)
                    .noOcclusion()));

    /** 貓抓板 — 貓咪磨爪點 🐾 */
    public static final RegistryObject<Block> SCRATCHING_POST = registerWithItem(
            "scratching_post",
            () -> new ScratchingPostBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.5f)
                    .noOcclusion()));

    /** 記憶紀念碑（基礎層）— 記憶碎片展示 🗿 */
    public static final RegistryObject<Block> MEMORY_MONUMENT_BASE = registerWithItem(
            "memory_monument_base",
            () -> new MemoryMonumentBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .requiresCorrectToolForDrops()
                    .strength(5.0f, 10.0f)
                    .lightLevel(s -> 8)
                    .noOcclusion()));

    /** 記憶紀念碑（頂層）— 完成後發光 ✨ */
    public static final RegistryObject<Block> MEMORY_MONUMENT_TOP = registerWithItem(
            "memory_monument_top",
            () -> new MemoryMonumentBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .requiresCorrectToolForDrops()
                    .strength(5.0f, 10.0f)
                    .lightLevel(s -> 15)
                    .noOcclusion()));

    /** 傳送門框架 — 前往貓之國 🌀 */
    public static final RegistryObject<Block> CAT_KINGDOM_PORTAL_FRAME = registerWithItem(
            "cat_kingdom_portal_frame",
            () -> new CatKingdomPortalFrameBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .requiresCorrectToolForDrops()
                    .strength(50.0f, 2000.0f)
                    .lightLevel(s -> 11)));

    /** 貓之國傳送門（虛空方塊） 🔮 */
    public static final RegistryObject<Block> CAT_KINGDOM_PORTAL = BLOCKS.register(
            "cat_kingdom_portal",
            () -> new CatKingdomPortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .noLootTable()
                    .strength(-1.0f)
                    .lightLevel(s -> 11)));

    // ══════════════════════════════════════════════════════════════════════
    // 特殊土壤與植物方塊
    // ══════════════════════════════════════════════════════════════════════

    /** 星塵土壤 — 終局後大量出現 ⭐ */
    public static final RegistryObject<Block> STARDUST_SOIL = registerWithItem(
            "stardust_soil",
            () -> new StardustSoilBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(0.6f)
                    .lightLevel(s -> 3)));

    /** 黑泥 (Black Mud) — 侵蝕方塊 stage 2 蔓延 🌑 */
    public static final RegistryObject<Block> BLACK_MUD = registerWithItem(
            "black_mud",
            () -> new com.cocojenna.block.ErosionBlackMudBlock(2, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.3f)
                    .lightLevel(s -> 0)
                    .randomTicks()));

    /** 黑泥 stage 1 沾染 */
    public static final RegistryObject<Block> BLACK_MUD_STAGE1 = registerWithItem(
            "black_mud_stage1",
            () -> new com.cocojenna.block.ErosionBlackMudBlock(1, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.25f)
                    .lightLevel(s -> 0)
                    .randomTicks()));

    /** 黑泥 stage 3 吞噬 */
    public static final RegistryObject<Block> BLACK_MUD_STAGE3 = registerWithItem(
            "black_mud_stage3",
            () -> new com.cocojenna.block.ErosionBlackMudBlock(3, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.35f)
                    .lightLevel(s -> 0)
                    .randomTicks()));

    /** 黑泥 stage 4 深淵化 */
    public static final RegistryObject<Block> BLACK_MUD_STAGE4 = registerWithItem(
            "black_mud_stage4",
            () -> new com.cocojenna.block.ErosionBlackMudBlock(4, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.4f)
                    .lightLevel(s -> 1)
                    .randomTicks()));

    /** 朱槿花 — 在曾重傷倒下的地方生長 🌺 */
    public static final RegistryObject<Block> HIBISCUS_FLOWER = registerWithItem(
            "hibiscus_flower",
            () -> new HibiscusBlock(BlockBehaviour.Properties.copy(Blocks.POPPY)));

    /** 貓薄荷 🌿 */
    public static final RegistryObject<Block> CATNIP = registerWithItem(
            "catnip",
            () -> new CatnipBlock(BlockBehaviour.Properties.copy(Blocks.GRASS)));

    /** 忘憂霓虹菇 — 夜晚發光 🍄 */
    public static final RegistryObject<Block> NEON_MUSHROOM = registerWithItem(
            "neon_mushroom",
            () -> new NeonMushroomBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(0.2f)
                    .lightLevel(s -> 9)
                    .noOcclusion()));

    /** 絨毛草 — 可破壞獲得絨毛 🌾 */
    public static final RegistryObject<Block> VELVET_GRASS = registerWithItem(
            "velvet_grass",
            () -> new VelvetGrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS)));

    /** 棉花糖灌木 — 右鍵採收甜食 🍬 */
    public static final RegistryObject<Block> COTTON_CANDY_SHRUB = registerWithItem(
            "cotton_candy_shrub",
            () -> new CottonCandyShrubBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
                    .mapColor(MapColor.COLOR_PINK)
                    .noOcclusion()));

    /** 月光石簇 — Moon Alley 地面採集 🌙 */
    public static final RegistryObject<Block> MOONSTONE_CLUSTER = registerWithItem(
            "moonstone_cluster",
            () -> new MoonstoneClusterBlock(BlockBehaviour.Properties.copy(Blocks.GLOW_LICHEN)
                    .lightLevel(s -> 10)
                    .noOcclusion()));

    /** 鹽晶 — 嚎風峽谷岩壁採集 🧂 */
    public static final RegistryObject<Block> SALT_CRYSTAL = registerWithItem(
            "salt_crystal",
            () -> new SaltCrystalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(0.8f)
                    .lightLevel(s -> 2)
                    .noOcclusion()));

    /** 物料孢子樹果實掉落節點 🌳 */
    public static final RegistryObject<Block> SPORE_FRUIT_NODE = registerWithItem(
            "spore_fruit_node",
            () -> new SporeFruitNode(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.5f)
                    .noOcclusion()));

    /** 滿月祭壇 — 滿月時啟動 🌕 */
    public static final RegistryObject<Block> FULL_MOON_ALTAR = registerWithItem(
            "full_moon_altar",
            () -> new FullMoonAltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .requiresCorrectToolForDrops()
                    .strength(3.0f)
                    .lightLevel(s -> s.getValue(FullMoonAltarBlock.ACTIVE) ? 15 : 0)
                    .noOcclusion()));

    /** 月光三岔路試煉祭壇 */
    public static final RegistryObject<Block> MOON_TRIAL_ALTAR = registerWithItem(
            "moon_trial_altar",
            () -> new MoonTrialAltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .requiresCorrectToolForDrops()
                    .strength(2.5f)
                    .lightLevel(s -> 10)
                    .noOcclusion()));

    /** 封印物展示台 (Seal Pedestal) 🏺 */
    public static final RegistryObject<Block> SEAL_PEDESTAL = registerWithItem(
            "seal_pedestal",
            () -> new SealPedestalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0f)
                    .noOcclusion()));

    /** 鐵爪鍛造舖 — 傳說武器強化 🔨 */
    public static final RegistryObject<Block> IRONPAW_FORGE = registerWithItem(
            "ironpaw_forge",
            () -> new IronpawForgeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0f)
                    .lightLevel(s -> 4)
                    .noOcclusion()));

    /** 金色算盤珠鑲嵌台 */
    public static final RegistryObject<Block> SOCKETING_TABLE = registerWithItem(
            "socketing_table",
            () -> new SocketingTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .requiresCorrectToolForDrops()
                    .strength(4.0f)
                    .lightLevel(s -> 6)
                    .noOcclusion()));

    /** 良快刀商店攤位 */
    public static final RegistryObject<Block> RYOKATANA_SHOP_STAND = registerWithItem(
            "ryokatana_shop_stand",
            () -> new RyokatanaShopStandBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0f)
                    .noOcclusion()));

    /** 純淨呼嚕結晶方塊 — 記憶鑄造祭壇用 ✨ */
    public static final RegistryObject<Block> PURR_CRYSTAL_BLOCK = registerWithItem(
            "purr_crystal_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .requiresCorrectToolForDrops()
                    .strength(3.0f)
                    .lightLevel(s -> 8)));

    /** 月光石磚 🌙 */
    public static final RegistryObject<Block> MOONSTONE_BRICK = registerWithItem(
            "moonstone_brick",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0f)
                    .lightLevel(s -> 5)));

    /** 絨毛方塊 🧶 */
    public static final RegistryObject<Block> VELVET_BLOCK = registerWithItem(
            "velvet_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.8f)));

    // ── 設計書第六章：建築方塊 ──────────────────────────────────────────

    /** 絨毛木板 — 奶油色絨毛邊緣 */
    public static final RegistryObject<Block> VELVET_PLANKS = registerWithItem(
            "velvet_planks",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(1.2f)));

    /** 星塵石磚 — 深灰帶金色星點 */
    public static final RegistryObject<Block> STARDUST_BRICK = registerWithItem(
            "stardust_brick",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .requiresCorrectToolForDrops()
                    .strength(2.5f)
                    .lightLevel(s -> 2)));

    /** 編織羊毛 — 毛線編織紋理 */
    public static final RegistryObject<Block> WOVEN_WOOL = registerWithItem(
            "woven_wool",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .strength(0.6f)));

    /** 貓抓板材 — 米白爪痕紋理 */
    public static final RegistryObject<Block> CAT_SCRATCH_BOARD = registerWithItem(
            "cat_scratch_board",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.5f)));

    /** 絨毛地毯 — 超柔軟地面 */
    public static final RegistryObject<Block> VELVET_CARPET = registerWithItem(
            "velvet_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.2f)));

    /** 線球燈 — 暖金色懸掛光源 */
    public static final RegistryObject<Block> YARN_BALL_LAMP = registerWithItem(
            "yarn_ball_lamp",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .strength(0.5f)
                    .lightLevel(s -> 12)
                    .noOcclusion()));

    /** 月光石燈柱 — 藍白立柱光源 */
    public static final RegistryObject<Block> MOONSTONE_LAMP_POST = registerWithItem(
            "moonstone_lamp_post",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(2.0f)
                    .lightLevel(s -> 10)
                    .noOcclusion()));

    /** 絨毛藤蔓 — 可攀附牆面 */
    public static final RegistryObject<Block> VELVET_VINE = registerWithItem(
            "velvet_vine",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.3f)
                    .noOcclusion()));

    /** 貓跳台 — 多層攀爬平台 */
    public static final RegistryObject<Block> CAT_CLIMB_PLATFORM = registerWithItem(
            "cat_climb_platform",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f)
                    .noOcclusion()));

    /** 絨毛樹原木 — 絨毛森林／初啼村景觀 */
    public static final RegistryObject<Block> VELVET_TREE_LOG = registerWithItem(
            "velvet_tree_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)));

    /** 絨毛樹葉 — 柔軟樹冠 */
    public static final RegistryObject<Block> VELVET_TREE_LEAVES = registerWithItem(
            "velvet_tree_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)
                    .mapColor(MapColor.PLANT)
                    .strength(0.2f)
                    .sound(SoundType.GRASS)
                    .noOcclusion()));

    /** 茅草屋頂 — 初啼村／鄉村小屋 */
    public static final RegistryObject<Block> THATCH_ROOF = registerWithItem(
            "thatch_roof",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(0.4f)
                    .sound(SoundType.GRASS)));

    /** 爪印玻璃 — 月色小巷窗戶 */
    public static final RegistryObject<Block> PAWPRINT_GLASS = registerWithItem(
            "pawprint_glass",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.GLASS)
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(0.3f)
                    .noOcclusion()));

    /** 霓虹菇盆栽 — 室內裝飾光源 */
    public static final RegistryObject<Block> NEON_MUSHROOM_POT = registerWithItem(
            "neon_mushroom_pot",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(0.5f)
                    .lightLevel(s -> 9)
                    .noOcclusion()));

    /** 物料孢子金屬方塊 — 齒輪鎮工業建材 */
    public static final RegistryObject<Block> SPORE_METAL_BLOCK = registerWithItem(
            "spore_metal_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(4.0f)
                    .sound(SoundType.METAL)));

    /** 鹽方塊 — 嚎風峽谷／遺忘高塔 */
    public static final RegistryObject<Block> SALT_BLOCK = registerWithItem(
            "salt_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(1.5f)
                    .sound(SoundType.CALCITE)));

    /** 月光石方塊 — 月色小巷地面 */
    public static final RegistryObject<Block> MOONSTONE_BLOCK = registerWithItem(
            "moonstone_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0f)
                    .lightLevel(s -> 6)));

    /** 玩具箱 — 絨毛森林裝飾 */
    public static final RegistryObject<Block> TOY_BOX = registerWithItem(
            "toy_box",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    /** 暗影結晶方塊 */
    public static final RegistryObject<Block> SHADOW_CRYSTAL_BLOCK = registerWithItem(
            "shadow_crystal_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0f)));

    /** 記憶燈塔 — 每日淨化周圍黑泥 */
    public static final RegistryObject<Block> MEMORY_LIGHTHOUSE = registerWithItem(
            "memory_lighthouse",
            () -> new com.cocojenna.block.MemoryLighthouseBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .requiresCorrectToolForDrops()
                    .strength(4.0f)
                    .lightLevel(s -> 15)
                    .randomTicks()
                    .noOcclusion()));

    /** 純淨光塔 — 阻止黑泥蔓延 */
    public static final RegistryObject<Block> PURE_LIGHT_TOWER = registerWithItem(
            "pure_light_tower",
            () -> new com.cocojenna.block.PureLightTowerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .requiresCorrectToolForDrops()
                    .strength(5.0f)
                    .lightLevel(s -> 15)
                    .randomTicks()
                    .noOcclusion()));

    /** 祭壇基石 — 記憶鑄造祭壇用 */
    public static final RegistryObject<Block> ALTAR_FOUNDATION = registerWithItem(
            "altar_foundation",
            () -> new com.cocojenna.block.DaikatanaAltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
                    .strength(5.0f)
                    .lightLevel(s -> 3)));

    // ══════════════════════════════════════════════════════════════════════
    // 地下貓域 DLC「深淵與星光」
    // ══════════════════════════════════════════════════════════════════════

    public static final RegistryObject<Block> CARDBOARD_BLOCK = registerWithItem(
            "cardboard_block",
            () -> new CardboardStructuralBlock(false, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN).strength(0.4f).sound(SoundType.WOOL)));

    public static final RegistryObject<Block> REINFORCED_CARDBOARD = registerWithItem(
            "reinforced_cardboard",
            () -> new CardboardStructuralBlock(true, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN).strength(1.2f).sound(SoundType.WOOL)));

    public static final RegistryObject<Block> ROPE_NET = registerWithItem(
            "rope_net",
            () -> new RopeNetBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN).strength(0.3f).noOcclusion().sound(SoundType.WOOL)));

    public static final RegistryObject<Block> TAPE_BLOCK = registerWithItem(
            "tape_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.6f).sound(SoundType.SLIME_BLOCK)));

    public static final RegistryObject<Block> TAPE_TEMPLE = registerWithItem(
            "tape_temple",
            () -> new TapeTempleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE).strength(0.8f).lightLevel(s -> 10)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()));

    public static final RegistryObject<Block> NEON_MUSH_LAMP = registerWithItem(
            "neon_mush_lamp",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE).strength(0.3f).lightLevel(s -> 12)
                    .sound(SoundType.SHROOMLIGHT)
                    .noOcclusion()));

    public static final RegistryObject<Block> UNDERCAT_TREE_HOLE = registerWithItem(
            "undercat_tree_hole",
            () -> new com.cocojenna.block.UndercatTreeHoleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5f).noOcclusion()));

    public static final RegistryObject<Block> UNDERCAT_BLIND_RIFT = registerWithItem(
            "undercat_blind_rift",
            () -> new UndercatPassageBlock(com.cocojenna.undercat.UndercatEntrance.BLIND_PORT_RIFT,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE).strength(1.5f).lightLevel(s -> 6).noOcclusion()));

    public static final RegistryObject<Block> UNDERCAT_GEAR_SHAFT = registerWithItem(
            "undercat_gear_shaft",
            () -> new UndercatPassageBlock(com.cocojenna.undercat.UndercatEntrance.GEAR_MINE_SHAFT,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL).strength(2.0f).noOcclusion()));

    public static final RegistryObject<Block> UNDERCAT_LIGHTHOUSE_WELL = registerWithItem(
            "undercat_lighthouse_well",
            () -> new UndercatPassageBlock(com.cocojenna.undercat.UndercatEntrance.LIGHTHOUSE_WELL,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.GOLD).strength(1.5f).lightLevel(s -> 10).noOcclusion()));

    public static final RegistryObject<Block> UNDERCAT_SANCTUARY_POOL = registerWithItem(
            "undercat_sanctuary_pool",
            () -> new UndercatPassageBlock(com.cocojenna.undercat.UndercatEntrance.SANCTUARY_POOL,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_BLUE).strength(1.0f).noOcclusion()));

    public static final RegistryObject<Block> STARLIGHT_MARBLE = registerWithItem(
            "starlight_marble",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE).strength(2.0f)
                    .lightLevel(s -> 7).sound(SoundType.STONE)));

    public static final RegistryObject<Block> UNDERCAT_WAYSTONE = registerWithItem(
            "undercat_waystone",
            () -> new com.cocojenna.block.UndercatWaystoneBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE).strength(3.0f).lightLevel(s -> 10)
                    .sound(SoundType.AMETHYST)));

    // ══════════════════════════════════════════════════════════════════════
    // 雨後 DLC 方塊
    // ══════════════════════════════════════════════════════════════════════

    /** 法令頒布台 */
    public static final RegistryObject<Block> DECREE_PEDESTAL = registerPeaceBlock(
            "decree_pedestal", MapColor.GOLD, 3.0f, 6,
            (player, pos) -> {
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    com.cocojenna.endgame.KingdomDecreeManager.openTerminal(sp);
                }
            });

    /** 貓飯烹飪台 */
    public static final RegistryObject<Block> CAT_KITCHEN = registerPeaceBlock(
            "cat_kitchen", MapColor.COLOR_ORANGE, 2.5f, 4,
            (player, pos) -> {
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    com.cocojenna.network.ModNetwork.CHANNEL.send(
                            net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> sp),
                            new com.cocojenna.network.OpenCatKitchenPacket());
                }
            });

    /** 繪本編輯台 */
    public static final RegistryObject<Block> PICTURE_BOOK_STAND = registerPeaceBlock(
            "picture_book_stand", MapColor.COLOR_PINK, 2.0f, 5,
            (player, pos) -> {
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(sp);
                    com.cocojenna.network.ModNetwork.CHANNEL.send(
                            net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> sp),
                            new com.cocojenna.network.OpenPictureBookPacket(bond.serializeNBT()));
                }
            });

    /** 建築藍圖台 */
    public static final RegistryObject<Block> BLUEPRINT_TABLE = registerPeaceBlock(
            "blueprint_table", MapColor.WOOD, 2.5f, 0,
            (player, pos) -> {
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(sp);
                    com.cocojenna.network.ModNetwork.CHANNEL.send(
                            net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> sp),
                            new com.cocojenna.network.OpenKingdomTerminalPacket(bond.serializeNBT()));
                }
            });

    /** 貓咪核心工程 — 城鎮建設專用終端 🏗️ */
    public static final RegistryObject<Block> CAT_CORE_ENGINEERING = registerPeaceBlock(
            "cat_core_engineering", MapColor.GOLD, 3.5f, 8,
            (player, pos) -> {
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    com.cocojenna.endgame.BuildingManager.openCoreEngineering(sp);
                }
            });

    private static RegistryObject<Block> registerPeaceBlock(String name, MapColor color,
            float strength, int light,
            java.util.function.BiConsumer<net.minecraft.world.entity.player.Player, net.minecraft.core.BlockPos> onUse) {
        return registerWithItem(name, () -> new com.cocojenna.block.PeaceModeBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(color)
                        .strength(strength)
                        .lightLevel(s -> light)
                        .noOcclusion(),
                onUse));
    }

    // ── 探索／考古（設計書第二章）────────────────────────────────────────
    public static final RegistryObject<Block> ANCIENT_STONE_TABLET = registerWithItem(
            "ancient_stone_tablet",
            () -> new AncientStoneTabletBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(2.5f, 8.0f)
                    .lightLevel(s -> 6)));

    public static final RegistryObject<Block> MURAL_FRAGMENT = registerWithItem(
            "mural_fragment",
            () -> new MuralFragmentBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_PURPLE)
                    .strength(1.5f)
                    .noOcclusion()
                    .lightLevel(s -> 4)));

    public static final RegistryObject<Block> DUNGEON_ENTRANCE = registerWithItem(
            "dungeon_entrance",
            () -> new DungeonEntranceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(-1f, 3600000f)
                    .noOcclusion()));

    public static final RegistryObject<Block> DUNGEON_PUZZLE_HINT = registerWithItem(
            "dungeon_puzzle_hint",
            () -> new com.cocojenna.block.DungeonPuzzleHintBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(2.0f)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> DUNGEON_SEQUENCE_PLATE = registerWithItem(
            "dungeon_sequence_plate",
            () -> new com.cocojenna.block.DungeonSequencePlateBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(1.5f)
                    .lightLevel(s -> s.getValue(com.cocojenna.block.DungeonSequencePlateBlock.LIT) ? 10 : 0)
                    .noOcclusion()));

    public static final RegistryObject<Block> DUNGEON_REWARD = registerWithItem(
            "dungeon_reward",
            () -> new DungeonRewardBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .strength(2f)
                    .lightLevel(s -> 10)));

    public static final RegistryObject<Block> SUSPICIOUS_WALL = registerWithItem(
            "suspicious_wall",
            () -> new SuspiciousWallBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(1.5f)));

    // ══════════════════════════════════════════════════════════════════════
    // Helper
    // ══════════════════════════════════════════════════════════════════════

    private static RegistryObject<Block> registerWithItem(String name, Supplier<Block> block) {
        RegistryObject<Block> b = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name,
                () -> new BlockItem(b.get(), new Item.Properties()));
        return b;
    }
}
