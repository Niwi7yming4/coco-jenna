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
                    .lightLevel(s -> 4)));

    /** 香氛蒸餾台 — 製作香氛 🌸 */
    public static final RegistryObject<Block> AROMA_DISTILLER = registerWithItem(
            "aroma_distiller",
            () -> new AromaDistillerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .requiresCorrectToolForDrops()
                    .strength(3.0f)
                    .lightLevel(s -> 6)));

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
                    .strength(1.5f)));

    /** 記憶紀念碑（基礎層）— 記憶碎片展示 🗿 */
    public static final RegistryObject<Block> MEMORY_MONUMENT_BASE = registerWithItem(
            "memory_monument_base",
            () -> new MemoryMonumentBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .requiresCorrectToolForDrops()
                    .strength(5.0f, 10.0f)
                    .lightLevel(s -> 8)));

    /** 記憶紀念碑（頂層）— 完成後發光 ✨ */
    public static final RegistryObject<Block> MEMORY_MONUMENT_TOP = registerWithItem(
            "memory_monument_top",
            () -> new MemoryMonumentBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .requiresCorrectToolForDrops()
                    .strength(5.0f, 10.0f)
                    .lightLevel(s -> 15)));

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
                    .noDrops()
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

    /** 黑泥 (Black Mud) — 侵蝕方塊 🌑 */
    public static final RegistryObject<Block> BLACK_MUD = registerWithItem(
            "black_mud",
            () -> new BlackMudBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.3f)
                    .lightLevel(s -> 0)));

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
                    .lightLevel(s -> s.getValue(FullMoonAltarBlock.ACTIVE) ? 15 : 0)));

    /** 封印物展示台 (Seal Pedestal) 🏺 */
    public static final RegistryObject<Block> SEAL_PEDESTAL = registerWithItem(
            "seal_pedestal",
            () -> new SealPedestalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0f)
                    .noOcclusion()));

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
