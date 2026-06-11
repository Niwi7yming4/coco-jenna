package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.item.*;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CocoJennaMod.MOD_ID);

    // ══════════════════════════════════════════════════════════════════════
    // 基礎材料 — 採集類
    // ══════════════════════════════════════════════════════════════════════

    /** 絨毛 🟢 */
    public static final RegistryObject<Item> VELVET_FUR = simple("velvet_fur");
    /** 月光石 🟢 */
    public static final RegistryObject<Item> MOONSTONE = simple("moonstone");
    /** 月光核心 — 月之守護者掉落 */
    public static final RegistryObject<Item> MOON_CORE =
            ITEMS.register("moon_core", () -> new MoonCoreItem(defaultProp().stacksTo(16).rarity(Rarity.EPIC)));
    /** 貓之魂戒 — MCA 求婚用 */
    public static final RegistryObject<Item> CAT_SOUL_RING =
            ITEMS.register("cat_soul_ring", () -> new Item(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    /** 初學者源力徽章 */
    public static final RegistryObject<Item> NOVICE_FORCE_BADGE =
            ITEMS.register("novice_force_badge", () -> new Item(defaultProp().stacksTo(1).rarity(Rarity.UNCOMMON)));
    /** 源力重置卷軸 */
    public static final RegistryObject<Item> FORCE_RESET_SCROLL =
            ITEMS.register("force_reset_scroll", () -> new com.cocojenna.item.ForceResetScrollItem(
                    defaultProp().stacksTo(1).rarity(Rarity.EPIC)));
    /** 暗影幣 — 地下貓域貨幣 */
    public static final RegistryObject<Item> SHADOW_COIN = simple("shadow_coin");
    /** 膠帶核心 — 膠帶巨像掉落 */
    public static final RegistryObject<Item> TAPE_CORE = simple("tape_core");
    /** 紙箱王國徽章 */
    public static final RegistryObject<Item> CARDBOARD_BADGE = simple("cardboard_badge");
    /** 傳說貓薄荷種子 */
    public static final RegistryObject<Item> LEGEND_CATNIP_SEED = simple("legend_catnip_seed");
    /** 被封印的記憶之書 */
    public static final RegistryObject<Item> SEALED_MEMORY_BOOK = simple("sealed_memory_book");
    /** 疤面的護符 */
    public static final RegistryObject<Item> SCARFACE_CHARM = simple("scarface_charm");
    /** 沉默修女的銀線 */
    public static final RegistryObject<Item> SILENCED_SILVER_THREAD = simple("silenced_silver_thread");
    /** 光與影的雙子星 */
    public static final RegistryObject<Item> TWIN_STAR_PENDANT = simple("twin_star_pendant");
    /** 木天蓼 🟡 */
    public static final RegistryObject<Item> SILVERVINE = simple("silvervine");
    /** 忘憂霓虹菇 🟡 */
    public static final RegistryObject<Item> NEON_MUSHROOM_ITEM = simple("neon_mushroom_item");
    /** 光纖神經藤（斷裂） 🟡 */
    public static final RegistryObject<Item> FIBER_VINE = simple("fiber_vine");
    /** 星塵土壤（物品形式） 🟢 */
    public static final RegistryObject<Item> STARDUST_SOIL_ITEM = simple("stardust_soil_item");
    /** 朱槿花（物品形式） 🟡 */
    public static final RegistryObject<Item> HIBISCUS_FLOWER_ITEM = simple("hibiscus_flower_item");
    /** 蒲公英絨 🟢 */
    public static final RegistryObject<Item> DANDELION_FLUFF = simple("dandelion_fluff");
    /** 貓薄荷（物品形式，帶品質） 🟢 */
    public static final RegistryObject<Item> CATNIP_ITEM =
            ITEMS.register("catnip_item", () -> new com.cocojenna.item.CatnipItem(defaultProp().stacksTo(64)));
    /** 滿月光譜 🟡 */
    public static final RegistryObject<Item> FULL_MOON_SPECTRUM =
            ITEMS.register("full_moon_spectrum", () -> new FullMoonSpectrumItem(defaultProp().stacksTo(16)));
    /** 盲水樣本 🟡 */
    public static final RegistryObject<Item> BLIND_WATER_SAMPLE =
            ITEMS.register("blind_water_sample", () -> new Item(defaultProp().stacksTo(16)));
    /** 鹽 🟢 */
    public static final RegistryObject<Item> SALT = simple("salt");
    /** 粗鹽結晶 🟢 */
    public static final RegistryObject<Item> COARSE_SALT = simple("coarse_salt");

    // ══════════════════════════════════════════════════════════════════════
    // 基礎材料 — 釣魚類
    // ══════════════════════════════════════════════════════════════════════

    /** 夜光魚 🟡 */
    public static final RegistryObject<Item> GLOW_FISH = simple("glow_fish");
    /** 深海魚 🟡 */
    public static final RegistryObject<Item> DEEP_SEA_FISH = simple("deep_sea_fish");
    /** 巨型青魚 🟡 */
    public static final RegistryObject<Item> GIANT_GREEN_FISH = simple("giant_green_fish");
    /** 蟹肉 🟡 */
    public static final RegistryObject<Item> CRAB_MEAT = simple("crab_meat");

    // ══════════════════════════════════════════════════════════════════════
    // 基礎材料 — 怪物掉落
    // ══════════════════════════════════════════════════════════════════════

    /** 黑泥殘骸 🟢 */
    public static final RegistryObject<Item> BLACK_MUD_REMNANT = simple("black_mud_remnant");
    /** 黑泥樣本 — 研究用採集物 */
    public static final RegistryObject<Item> BLACK_MUD_SAMPLE = ITEMS.register("black_mud_sample",
            () -> new Item(defaultProp().rarity(Rarity.UNCOMMON)));
    /** 暗影結晶 🟡 */
    public static final RegistryObject<Item> SHADOW_CRYSTAL = simple("shadow_crystal");
    /** 混沌結晶 🟡 */
    public static final RegistryObject<Item> CHAOS_CRYSTAL = simple("chaos_crystal");
    /** 盲水凝膠 🟡 */
    public static final RegistryObject<Item> BLIND_WATER_GEL = simple("blind_water_gel");
    /** 悲嘆凝膠 — 序列4 */
    public static final RegistryObject<Item> GRIEF_GEL = simple("grief_gel");
    /** 墮落核心 — 序列2 */
    public static final RegistryObject<Item> FALLEN_CORE = ITEMS.register("fallen_core",
            () -> new Item(defaultProp().rarity(Rarity.RARE)));
    /** 原始混沌碎片 — 序列1 */
    public static final RegistryObject<Item> PRIMAL_CHAOS_SHARD = ITEMS.register("primal_chaos_shard",
            () -> new Item(defaultProp().rarity(Rarity.EPIC)));
    /** 純淨之核 — 原始混沌淨化後的墓碑（設計書 卷六 §7.4） */
    public static final RegistryObject<Item> PRIMAL_CHAOS_CORE = ITEMS.register("primal_chaos_core",
            () -> new com.cocojenna.item.PrimalChaosCoreItem(defaultProp().rarity(Rarity.EPIC).stacksTo(1)));
    /** 記憶陶土 — 雨後終局建材 */
    public static final RegistryObject<Item> MEMORY_CLAY = simple("memory_clay");
    /** 悲傷遺物 */
    public static final RegistryObject<Item> WARRIOR_LAST_LETTER = ITEMS.register("warrior_last_letter",
            () -> new com.cocojenna.item.OverworldArtifactItem(defaultProp().rarity(Rarity.UNCOMMON).stacksTo(1),
                    com.cocojenna.item.OverworldArtifactItem.Kind.WARRIOR_LETTER));
    public static final RegistryObject<Item> FARMER_DIARY = ITEMS.register("farmer_diary",
            () -> new com.cocojenna.item.OverworldArtifactItem(defaultProp().rarity(Rarity.UNCOMMON).stacksTo(1),
                    com.cocojenna.item.OverworldArtifactItem.Kind.FARMER_DIARY));
    public static final RegistryObject<Item> OUTPOST_BADGE = ITEMS.register("outpost_badge",
            () -> new com.cocojenna.item.OverworldArtifactItem(defaultProp().rarity(Rarity.RARE).stacksTo(1),
                    com.cocojenna.item.OverworldArtifactItem.Kind.OUTPOST_BADGE));
    public static final RegistryObject<Item> GUARDIAN_BADGE = ITEMS.register("guardian_badge",
            () -> new com.cocojenna.item.OverworldArtifactItem(defaultProp().rarity(Rarity.RARE).stacksTo(1),
                    com.cocojenna.item.OverworldArtifactItem.Kind.GUARDIAN_BADGE));
    public static final RegistryObject<Item> UNSENT_LETTER = ITEMS.register("unsent_letter",
            () -> new com.cocojenna.item.BlackMudRelicItem(defaultProp().rarity(Rarity.UNCOMMON).stacksTo(1),
                    com.cocojenna.item.BlackMudRelicItem.RelicKind.UNSENT_LETTER));
    public static final RegistryObject<Item> RUSTED_BELL = ITEMS.register("rusted_bell",
            () -> new com.cocojenna.item.BlackMudRelicItem(defaultProp().rarity(Rarity.UNCOMMON).stacksTo(1),
                    com.cocojenna.item.BlackMudRelicItem.RelicKind.RUSTED_BELL));
    public static final RegistryObject<Item> HALF_SCARF = ITEMS.register("half_scarf",
            () -> new com.cocojenna.item.BlackMudRelicItem(defaultProp().rarity(Rarity.RARE).stacksTo(1),
                    com.cocojenna.item.BlackMudRelicItem.RelicKind.HALF_SCARF));
    public static final RegistryObject<Item> FADED_COLLAR = ITEMS.register("faded_collar",
            () -> new com.cocojenna.item.BlackMudRelicItem(defaultProp().rarity(Rarity.RARE).stacksTo(1),
                    com.cocojenna.item.BlackMudRelicItem.RelicKind.FADED_COLLAR));
    public static final RegistryObject<Item> PURE_DROP = ITEMS.register("pure_drop",
            () -> new com.cocojenna.item.BlackMudRelicItem(defaultProp().rarity(Rarity.EPIC).stacksTo(1),
                    com.cocojenna.item.BlackMudRelicItem.RelicKind.PURE_DROP));
    /** 黑珍珠 🟡 */
    public static final RegistryObject<Item> BLACK_PEARL = simple("black_pearl");
    /** 記憶微粒 🟡 — 100 個右鍵合成記憶碎片 */
    public static final RegistryObject<Item> MEMORY_PARTICLE = ITEMS.register("memory_particle",
            () -> new com.cocojenna.item.MemoryParticleItem(defaultProp()));
    /** 空白記憶卡 — 螢幕雜訊貓掉落 */
    public static final RegistryObject<Item> BLANK_MEMORY_CARD =
            ITEMS.register("blank_memory_card", () -> new Item(defaultProp().rarity(Rarity.UNCOMMON)));
    /** 廢稿摺紙 — 折紙鴉掉落 */
    public static final RegistryObject<Item> ORIGAMI_SCRAP = simple("origami_scrap");
    /** 純淨之淚 🟡 */
    public static final RegistryObject<Item> PURE_TEAR = simple("pure_tear");
    /** 深海珍珠 🟡 */
    public static final RegistryObject<Item> DEEP_SEA_PEARL = simple("deep_sea_pearl");
    /** 暴雨的傘骨 — 斑鳩掉落 */
    public static final RegistryObject<Item> SQUALL_UMBRELLA_BONE = simple("squall_umbrella_bone");
    /** 雷雲絨毛 */
    public static final RegistryObject<Item> STORM_CLOUD_FUR = simple("storm_cloud_fur");

    // ══════════════════════════════════════════════════════════════════════
    // 基礎材料 — 貓咪互動
    // ══════════════════════════════════════════════════════════════════════

    /** 可可的毛（黑） 🟡 */
    public static final RegistryObject<Item> COCO_FUR = simple("coco_fur");
    /** 珍奶的毛（玳瑁） 🟡 */
    public static final RegistryObject<Item> JENNA_FUR = simple("jenna_fur");
    /** 純淨的呼嚕結晶 🟡 */
    public static final RegistryObject<Item> PURR_CRYSTAL = simple("purr_crystal");
    /** 珍奶的舊鈴鐺 🔴 */
    public static final RegistryObject<Item> JENNAS_OLD_BELL =
            ITEMS.register("jennas_old_bell", () -> new UniqueItem(defaultProp().stacksTo(1).rarity(Rarity.EPIC)));
    /** 玩具啾啾聲（物品） 🟡 */
    public static final RegistryObject<Item> TOY_SQUEAK = simple("toy_squeak");
    /** 彩虹絨球 🟡 */
    public static final RegistryObject<Item> RAINBOW_YARN_BALL = simple("rainbow_yarn_ball");

    // ══════════════════════════════════════════════════════════════════════
    // 基礎材料 — 機械類
    // ══════════════════════════════════════════════════════════════════════

    /** 銅線 🟢 */
    public static final RegistryObject<Item> COPPER_WIRE = simple("copper_wire");
    /** 精密齒輪 🟡 */
    public static final RegistryObject<Item> PRECISION_GEAR = simple("precision_gear");
    /** 廢棄電路板 🟡 */
    public static final RegistryObject<Item> BROKEN_CIRCUIT = simple("broken_circuit");
    /** 物料孢子樹果實 🟢 */
    public static final RegistryObject<Item> SPORE_FRUIT = simple("spore_fruit");
    /** 生鏽鐵錠 🟢 */
    public static final RegistryObject<Item> RUSTY_IRON = simple("rusty_iron");
    /** 鏽蝕船錨 — 無上快刀儀式核心 ⚓ */
    public static final RegistryObject<Item> RUSTED_ANCHOR = simple("rusted_anchor");
    /** 完整塔羅牌組 🃏 */
    public static final RegistryObject<Item> TAROT_DECK = simple("tarot_deck");
    /** 黑傑克籌碼 🎰 */
    public static final RegistryObject<Item> BLACKJACK_CHIP = simple("blackjack_chip");
    /** 物料孢子粉 🟢 */
    public static final RegistryObject<Item> SPORE_POWDER = simple("spore_powder");

    // ══════════════════════════════════════════════════════════════════════
    // 消耗品 — 治療與淨化
    // ══════════════════════════════════════════════════════════════════════

    /** 聖水 🟢 */
    public static final RegistryObject<Item> HOLY_WATER =
            ITEMS.register("holy_water", () -> new HolyWaterItem(defaultProp().stacksTo(3)));
    /** 朱槿花之淚 🟡 */
    public static final RegistryObject<Item> HIBISCUS_TEAR =
            ITEMS.register("hibiscus_tear", () -> new PurifyItem(defaultProp().stacksTo(4), 3));
    /** 力量藥水（戰鬥輔助）🟢 */
    public static final RegistryObject<Item> STRENGTH_ELIXIR =
            ITEMS.register("strength_elixir", () -> new CombatElixirItem(defaultProp().stacksTo(8),
                    net.minecraft.world.effect.MobEffects.DAMAGE_BOOST, 3600));
    /** 速度藥水 🟢 */
    public static final RegistryObject<Item> SPEED_ELIXIR =
            ITEMS.register("speed_elixir", () -> new CombatElixirItem(defaultProp().stacksTo(8),
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 2400));
    /** 生命藥水 🟡 */
    public static final RegistryObject<Item> LIFE_ELIXIR =
            ITEMS.register("life_elixir", () -> new CombatElixirItem(defaultProp().stacksTo(4),
                    net.minecraft.world.effect.MobEffects.REGENERATION, 100, 20));
    /** 純淨治療液 🟡 */
    public static final RegistryObject<Item> PURE_CURE =
            ITEMS.register("pure_cure", () -> new PurifyItem(defaultProp().stacksTo(4), 3));

    // ══════════════════════════════════════════════════════════════════════
    // 消耗品 — 貓食
    // ══════════════════════════════════════════════════════════════════════

    /** 基礎魚肉泥 🟢 */
    public static final RegistryObject<Item> BASIC_FISH_PUREE =
            ITEMS.register("basic_fish_puree", () -> new CatFoodItem(defaultProp(), 3, 3));
    /** 高級魚罐頭 🟢 */
    public static final RegistryObject<Item> PREMIUM_FISH_CAN =
            ITEMS.register("premium_fish_can", () -> new CatFoodItem(defaultProp(), 4, 4));
    /** 夜光魚湯 🟡 */
    public static final RegistryObject<Item> GLOW_FISH_SOUP =
            ITEMS.register("glow_fish_soup", () -> new CatFoodItem(defaultProp().stacksTo(4), 2, 4));
    /** 蟹肉豪華拼盤 🟡 */
    public static final RegistryObject<Item> CRAB_DELUXE =
            ITEMS.register("crab_deluxe", () -> new CatFoodItem(defaultProp().stacksTo(4), 5, 5));
    /** 朱槿花魚生 🟡 */
    public static final RegistryObject<Item> HIBISCUS_SASHIMI =
            ITEMS.register("hibiscus_sashimi", () -> new CatFoodItem(defaultProp().stacksTo(4), 5, 3));
    /** 木天蓼餅乾 🟢 */
    public static final RegistryObject<Item> SILVERVINE_BISCUIT =
            ITEMS.register("silvervine_biscuit", () -> new CatFoodItem(defaultProp(), 1, 5));
    /** 深海燉飯 🟡 */
    public static final RegistryObject<Item> DEEP_SEA_RISOTTO =
            ITEMS.register("deep_sea_risotto", () -> new CatFoodItem(defaultProp().stacksTo(4), 3, 3));
    /** 可可專屬特餐 🟡 */
    public static final RegistryObject<Item> COCO_SPECIAL_MEAL =
            ITEMS.register("coco_special_meal", () -> new SpecialCatMealItem(defaultProp().stacksTo(1).rarity(Rarity.UNCOMMON), true));
    /** 珍奶專屬特餐 🟡 */
    public static final RegistryObject<Item> JENNA_SPECIAL_MEAL =
            ITEMS.register("jenna_special_meal", () -> new SpecialCatMealItem(defaultProp().stacksTo(1).rarity(Rarity.UNCOMMON), false));
    /** 雙子星分享餐 🟡 */
    public static final RegistryObject<Item> TWIN_STAR_MEAL =
            ITEMS.register("twin_star_meal", () -> new TwinStarMealItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));

    // ══════════════════════════════════════════════════════════════════════
    // 消耗品 — 戰鬥
    // ══════════════════════════════════════════════════════════════════════

    /** 木天蓼炸彈 🟢 */
    public static final RegistryObject<Item> SILVERVINE_BOMB =
            ITEMS.register("silvervine_bomb", () -> new SilvervineBombItem(defaultProp()));
    /** 貓鈴鐺（投擲版） 🟡 */
    public static final RegistryObject<Item> CAT_BELL_THROW =
            ITEMS.register("cat_bell_throw", () -> new CatBellThrowItem(defaultProp()));
    /** 雷石 🟡 */
    public static final RegistryObject<Item> THUNDER_STONE =
            ITEMS.register("thunder_stone", () -> new ThunderStoneItem(defaultProp()));
    /** 絨蛾鱗粉 🟡 */
    public static final RegistryObject<Item> MOTH_SCALE_POWDER = simple("moth_scale_powder");

    // ══════════════════════════════════════════════════════════════════════
    // 消耗品 — 特殊
    // ══════════════════════════════════════════════════════════════════════

    /** 九命貓薄荷 🔴 */
    public static final RegistryObject<Item> NINE_LIVES_CATNIP =
            ITEMS.register("nine_lives_catnip", () -> new NineLivesCatnipItem(defaultProp().stacksTo(3).rarity(Rarity.EPIC)));
    /** 薛丁格盲盒 🔴 */
    public static final RegistryObject<Item> SCHRODINGERS_BOX =
            ITEMS.register("schrodingers_box", () -> new SchrodingersBoxItem(defaultProp().stacksTo(1).rarity(Rarity.EPIC)));

    // ══════════════════════════════════════════════════════════════════════
    // 工具類
    // ══════════════════════════════════════════════════════════════════════

    /** 記憶之書 📖 */
    public static final RegistryObject<Item> MEMORY_BOOK =
            ITEMS.register("memory_book", () -> new MemoryBookItem(defaultProp().stacksTo(1)));
    /** 《守護者指南》— Patchouli 教學手冊 */
    public static final RegistryObject<Item> GUARDIAN_GUIDE =
            ITEMS.register("guardian_guide", () -> new GuardianGuideItem(defaultProp().stacksTo(1).rarity(Rarity.UNCOMMON)));
    /** 考古刷子 — 清除壁畫黑泥 */
    public static final RegistryObject<Item> ARCHAEOLOGY_BRUSH =
            ITEMS.register("archaeology_brush", () -> new ArchaeologyBrushItem(defaultProp().stacksTo(1)));
    /** 貓之國地圖殘頁 */
    public static final RegistryObject<Item> MAP_FRAGMENT =
            ITEMS.register("map_fragment", () -> new MapFragmentItem(defaultProp().stacksTo(16)));
    /** 卸劍石 — 卸下劍骨（設計書 1.7） */
    public static final RegistryObject<Item> UNSHEATH_STONE =
            ITEMS.register("unsheath_stone", () -> new UnsheathStoneItem(defaultProp().stacksTo(16)));

    // ── 防具：絨毛初心者 ─────────────────────────────────────────────────
    public static final RegistryObject<Item> VELVET_BEGINNER_HELMET = armor("velvet_beginner_helmet",
            com.cocojenna.armor.ModArmorMaterials.VELVET_BEGINNER, ArmorItem.Type.HELMET);
    public static final RegistryObject<Item> VELVET_BEGINNER_CHESTPLATE = armor("velvet_beginner_chestplate",
            com.cocojenna.armor.ModArmorMaterials.VELVET_BEGINNER, ArmorItem.Type.CHESTPLATE);
    public static final RegistryObject<Item> VELVET_BEGINNER_LEGGINGS = armor("velvet_beginner_leggings",
            com.cocojenna.armor.ModArmorMaterials.VELVET_BEGINNER, ArmorItem.Type.LEGGINGS);
    public static final RegistryObject<Item> VELVET_BEGINNER_BOOTS = armor("velvet_beginner_boots",
            com.cocojenna.armor.ModArmorMaterials.VELVET_BEGINNER, ArmorItem.Type.BOOTS);
    // ── 防具：月光巡禮者 ─────────────────────────────────────────────────
    public static final RegistryObject<Item> MOONLIGHT_HELMET = armor("moonlight_helmet",
            com.cocojenna.armor.ModArmorMaterials.MOONLIGHT, ArmorItem.Type.HELMET);
    public static final RegistryObject<Item> MOONLIGHT_CHESTPLATE = armor("moonlight_chestplate",
            com.cocojenna.armor.ModArmorMaterials.MOONLIGHT, ArmorItem.Type.CHESTPLATE);
    public static final RegistryObject<Item> MOONLIGHT_LEGGINGS = armor("moonlight_leggings",
            com.cocojenna.armor.ModArmorMaterials.MOONLIGHT, ArmorItem.Type.LEGGINGS);
    public static final RegistryObject<Item> MOONLIGHT_BOOTS = armor("moonlight_boots",
            com.cocojenna.armor.ModArmorMaterials.MOONLIGHT, ArmorItem.Type.BOOTS);
    /** 肉球印章 🟡 */
    public static final RegistryObject<Item> PAW_STAMP =
            ITEMS.register("paw_stamp", () -> new PawStampItem(defaultProp().stacksTo(4)));
    /** 逗貓棒 🟢 */
    public static final RegistryObject<Item> FEATHER_WAND =
            ITEMS.register("feather_wand", () -> new FeatherWandItem(defaultProp().stacksTo(1).durability(200)));
    /** 梳子（梳毛用）🟢 */
    public static final RegistryObject<Item> GROOMING_BRUSH =
            ITEMS.register("grooming_brush", () -> new GroomingBrushItem(defaultProp().stacksTo(1).durability(100)));
    /** 玻璃瓶（封印採集）🟢 */
    public static final RegistryObject<Item> GLASS_VIAL =
            ITEMS.register("glass_vial", () -> new GlassVialItem(defaultProp().stacksTo(16)));

    // ══════════════════════════════════════════════════════════════════════
    // 記憶碎片
    // ══════════════════════════════════════════════════════════════════════

    /** 記憶碎片（通用） 🟡 */
    public static final RegistryObject<Item> MEMORY_SHARD =
            ITEMS.register("memory_shard", () -> new MemoryShardItem(defaultProp().stacksTo(1).rarity(Rarity.UNCOMMON)));
    /** 可可的記憶碎片 🟡 */
    public static final RegistryObject<Item> COCO_MEMORY_SHARD =
            ITEMS.register("coco_memory_shard", () -> new MemoryShardItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    /** 珍奶的記憶碎片 🟡 */
    public static final RegistryObject<Item> JENNA_MEMORY_SHARD =
            ITEMS.register("jenna_memory_shard", () -> new MemoryShardItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));

    // ══════════════════════════════════════════════════════════════════════
    // 封印物
    // ══════════════════════════════════════════════════════════════════════

    /** 封印物（通用）💊 */
    public static final RegistryObject<Item> SEAL_ORB =
            ITEMS.register("seal_orb", () -> new SealOrbItem(defaultProp().stacksTo(1).rarity(Rarity.UNCOMMON)));
    /** 貓武士封印物 ⚔️ */
    public static final RegistryObject<Item> SAMURAI_SEAL =
            ITEMS.register("samurai_seal", () -> new SealOrbItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    /** 貓將軍封印物 👑 */
    public static final RegistryObject<Item> GENERAL_SEAL =
            ITEMS.register("general_seal", () -> new SealOrbItem(defaultProp().stacksTo(1).rarity(Rarity.EPIC)));

    // ══════════════════════════════════════════════════════════════════════
    // 基礎武器（良快刀 50 把，節選 25 把代表）
    // ══════════════════════════════════════════════════════════════════════

    /** 魚骨刃 🟢 */
    public static final RegistryObject<Item> FISH_BONE_BLADE =
            ITEMS.register("fish_bone_blade", () -> new CatSwordItem(Tiers.STONE, 1, -2.4f, defaultProp(), "fish_bone"));
    /** 線球杖 🟢 */
    public static final RegistryObject<Item> YARN_BALL_STAFF =
            ITEMS.register("yarn_ball_staff", () -> new CatStaffItem(defaultProp(), "yarn_ball"));
    /** 足跡短刃 🟢 */
    public static final RegistryObject<Item> PAWPRINT_DAGGER =
            ITEMS.register("pawprint_dagger", () -> new CatSwordItem(Tiers.IRON, 0, -2.0f, defaultProp(), "pawprint"));
    /** 貓鈴鐺（副手） 🟢 */
    public static final RegistryObject<Item> CAT_BELL_OFFHAND =
            ITEMS.register("cat_bell_offhand", () -> new CatBellOffhandItem(defaultProp()));

    // 良快刀 50 把 — RyokatanaRegistry 統一註冊
    public static RegistryObject<Item> RYOKATANA_MOON_SHADOW;
    public static RegistryObject<Item> RYOKATANA_HIBISCUS_BLOOD;
    public static RegistryObject<Item> RYOKATANA_DAWN_HOPE;
    public static RegistryObject<Item> RYOKATANA_PRECISION_GEAR;
    public static RegistryObject<Item> RYOKATANA_DARK_TIDE;
    public static RegistryObject<Item> RYOKATANA_RED_JADE;

    static {
        RyokatanaRegistry.registerAll(ITEMS);
        RYOKATANA_MOON_SHADOW = RyokatanaRegistry.get("moon_shadow");
        RYOKATANA_HIBISCUS_BLOOD = RyokatanaRegistry.get("hibiscus_blood");
        RYOKATANA_DAWN_HOPE = RyokatanaRegistry.get("dawn_hope");
        RYOKATANA_PRECISION_GEAR = RyokatanaRegistry.get("precision_gear");
        RYOKATANA_DARK_TIDE = RyokatanaRegistry.get("dark_tide");
        RYOKATANA_RED_JADE = RyokatanaRegistry.get("red_jade");
    }

    /** 金色算盤珠 — 鑲嵌台用 */
    public static final RegistryObject<Item> GOLDEN_ABACUS_BEAD = simple("golden_abacus_bead");
    /** 金湯匙獎盃 — 滿月料理大賽優勝裝飾品 */
    public static final RegistryObject<Item> GOLDEN_SPOON_TROPHY = ITEMS.register(
            "golden_spoon_trophy", () -> new Item(defaultProp().rarity(Rarity.UNCOMMON)));
    /** 鐵爪護符 — 強化失敗時防止降級 */
    public static final RegistryObject<Item> IRONPAW_CHARM = simple("ironpaw_charm");

    // ══════════════════════════════════════════════════════════════════════
    // 傳說武器（大快刀 21 把，全部實作）
    // ══════════════════════════════════════════════════════════════════════

    /** 01 虎鐵・將軍之刃 🟡 */
    public static final RegistryObject<Item> DAIKATANA_TIGER_IRON =
            ITEMS.register("daikatana_tiger_iron", () -> new DaikataItem(Tiers.NETHERITE, 8, -2.4f, defaultProp().rarity(Rarity.EPIC), "tiger_iron", DaikataItem.Skill.SUMMON_WARRIOR));
    /** 02 風切・第三門 🟡 */
    public static final RegistryObject<Item> DAIKATANA_WIND_CUT =
            ITEMS.register("daikatana_wind_cut", () -> new DaikataItem(Tiers.NETHERITE, 7, -2.2f, defaultProp().rarity(Rarity.EPIC), "wind_cut", DaikataItem.Skill.DASH_SLASH));
    /** 03 紫苑・幻影 🟡 */
    public static final RegistryObject<Item> DAIKATANA_PHANTOM =
            ITEMS.register("daikatana_phantom", () -> new DaikataItem(Tiers.NETHERITE, 5, -1.8f, defaultProp().rarity(Rarity.EPIC), "phantom", DaikataItem.Skill.PHANTOM_STEP));
    /** 04 玄德・壓制 🟡 */
    public static final RegistryObject<Item> DAIKATANA_SUPPRESSION =
            ITEMS.register("daikatana_suppression", () -> new DaikataItem(Tiers.NETHERITE, 3, -2.0f, defaultProp().rarity(Rarity.EPIC), "suppression", DaikataItem.Skill.STILL_DOMAIN));
    /** 05 力士・震擊 🟡 */
    public static final RegistryObject<Item> DAIKATANA_SHOCKWAVE =
            ITEMS.register("daikatana_shockwave", () -> new DaikataItem(Tiers.NETHERITE, 10, -3.0f, defaultProp().rarity(Rarity.EPIC), "shockwave", DaikataItem.Skill.GROUND_SLAM));
    /** 06 月牙・裂空 */
    public static final RegistryObject<Item> DAIKATANA_CRESCENT =
            ITEMS.register("daikatana_crescent", () -> new DaikataItem(Tiers.NETHERITE, 8, -2.4f, defaultProp().rarity(Rarity.EPIC), "crescent", DaikataItem.Skill.CRESCENT_SLASH));
    /** 06b 月光・裁決（月核催化） */
    public static final RegistryObject<Item> DAIKATANA_MOON_VERDICT =
            ITEMS.register("daikatana_moon_verdict", () -> new DaikataItem(Tiers.NETHERITE, 9, -2.5f, defaultProp().rarity(Rarity.EPIC), "moon_verdict", DaikataItem.Skill.CRESCENT_SLASH));
    /** 07 星圖・指北 */
    public static final RegistryObject<Item> DAIKATANA_STAR_MAP =
            ITEMS.register("daikatana_star_map", () -> new DaikataItem(Tiers.NETHERITE, 6, -2.0f, defaultProp().rarity(Rarity.EPIC), "star_map", DaikataItem.Skill.STAR_GUIDE));
    /** 08 盲水・溺淵 */
    public static final RegistryObject<Item> DAIKATANA_ABYSS =
            ITEMS.register("daikatana_abyss", () -> new DaikataItem(Tiers.NETHERITE, 9, -2.6f, defaultProp().rarity(Rarity.EPIC), "abyss", DaikataItem.Skill.WATER_PRISON));
    /** 09 霓虹・亂舞 */
    public static final RegistryObject<Item> DAIKATANA_NEON_DANCE =
            ITEMS.register("daikatana_neon_dance", () -> new DaikataItem(Tiers.NETHERITE, 7, -1.8f, defaultProp().rarity(Rarity.EPIC), "neon_dance", DaikataItem.Skill.NEON_BARRAGE));
    /** 10 齒輪王・機動 */
    public static final RegistryObject<Item> DAIKATANA_GEAR_KING =
            ITEMS.register("daikatana_gear_king", () -> new DaikataItem(Tiers.NETHERITE, 10, -2.8f, defaultProp().rarity(Rarity.EPIC), "gear_king", DaikataItem.Skill.MECHANICAL_FRENZY));
    /** 11 朱槿・泣血絕技 */
    public static final RegistryObject<Item> DAIKATANA_HIBISCUS_ULTIMATE =
            ITEMS.register("daikatana_hibiscus_ultimate", () -> new DaikataItem(Tiers.NETHERITE, 12, -2.4f, defaultProp().rarity(Rarity.EPIC), "hibiscus_ultimate", DaikataItem.Skill.BLOOD_TIDE));
    /** 12 嚎風・裂谷 */
    public static final RegistryObject<Item> DAIKATANA_HOWLING_GORGE =
            ITEMS.register("daikatana_howling_gorge", () -> new DaikataItem(Tiers.NETHERITE, 9, -2.4f, defaultProp().rarity(Rarity.EPIC), "howling_gorge", DaikataItem.Skill.GORGE_WIND));
    /** 13 初晴・黎明 */
    public static final RegistryObject<Item> DAIKATANA_FIRST_DAWN =
            ITEMS.register("daikatana_first_dawn", () -> new DaikataItem(Tiers.NETHERITE, 8, -2.2f, defaultProp().rarity(Rarity.EPIC), "first_dawn", DaikataItem.Skill.DAWN_BURST));
    /** 14 貓之國・王權 */
    public static final RegistryObject<Item> DAIKATANA_ROYAL_AUTHORITY =
            ITEMS.register("daikatana_royal_authority", () -> new DaikataItem(Tiers.NETHERITE, 11, -2.6f, defaultProp().rarity(Rarity.EPIC), "royal_authority", DaikataItem.Skill.ROYAL_DECREE));
    /** 15 影爪・仿 */
    public static final RegistryObject<Item> DAIKATANA_SHADOW_CLAW_IMITATION =
            ITEMS.register("daikatana_shadow_claw_imitation", () -> new DaikataItem(Tiers.NETHERITE, 13, -2.8f, defaultProp().rarity(Rarity.EPIC), "shadow_imitation", DaikataItem.Skill.SHADOW_STRIKE));
    /** 16 沉默・守護 */
    public static final RegistryObject<Item> DAIKATANA_SILENT_GUARD =
            ITEMS.register("daikatana_silent_guard", () -> new DaikataItem(Tiers.NETHERITE, 7, -2.0f, defaultProp().rarity(Rarity.EPIC), "silent_guard", DaikataItem.Skill.SILENT_SHIELD));
    /** 17 黃昏・終焉 */
    public static final RegistryObject<Item> DAIKATANA_DUSK_END =
            ITEMS.register("daikatana_dusk_end", () -> new DaikataItem(Tiers.NETHERITE, 10, -2.4f, defaultProp().rarity(Rarity.EPIC), "dusk_end", DaikataItem.Skill.TWILIGHT_SLASH));
    /** 18 白手套・契約 */
    public static final RegistryObject<Item> DAIKATANA_WHITE_GLOVE_CONTRACT =
            ITEMS.register("daikatana_white_glove_contract", () -> new DaikataItem(Tiers.NETHERITE, 6, -1.6f, defaultProp().rarity(Rarity.EPIC), "white_glove", DaikataItem.Skill.TRADE_ROUTE));
    /** 19 遺忘・高塔 */
    public static final RegistryObject<Item> DAIKATANA_FORGOTTEN_TOWER =
            ITEMS.register("daikatana_forgotten_tower", () -> new DaikataItem(Tiers.NETHERITE, 9, -2.6f, defaultProp().rarity(Rarity.EPIC), "forgotten_tower", DaikataItem.Skill.TOWER_COLLAPSE));
    /** 20 初啼・村魂 */
    public static final RegistryObject<Item> DAIKATANA_VILLAGE_SOUL =
            ITEMS.register("daikatana_village_soul", () -> new DaikataItem(Tiers.NETHERITE, 7, -2.0f, defaultProp().rarity(Rarity.EPIC), "village_soul", DaikataItem.Skill.VILLAGE_BOND));
    /** 21 暴雨・傘劍 */
    public static final RegistryObject<Item> DAIKATANA_STORM_UMBRELLA =
            ITEMS.register("daikatana_storm_umbrella", () -> new DaikataItem(Tiers.NETHERITE, 10, -2.4f, defaultProp().rarity(Rarity.EPIC), "storm_umbrella", DaikataItem.Skill.STORM_CALL));
    // ── 無上大快刀（記憶鑄造祭壇儀式）────────────────────────────────
    public static final RegistryObject<Item> MUSOU_SALMON_KING =
            ITEMS.register("musou_salmon_king", () -> new DaikataItem(Tiers.NETHERITE, 14, -2.6f,
                    defaultProp().stacksTo(1).rarity(Rarity.EPIC), "salmon_king", DaikataItem.Skill.GROUND_SLAM));
    public static final RegistryObject<Item> MUSOU_NIGHT_VERDICT =
            ITEMS.register("musou_night_verdict", () -> new DaikataItem(Tiers.NETHERITE, 15, -2.4f,
                    defaultProp().stacksTo(1).rarity(Rarity.EPIC), "night_verdict", DaikataItem.Skill.TWILIGHT_SLASH));
    public static final RegistryObject<Item> MUSOU_TOY_HAMMER =
            ITEMS.register("musou_toy_hammer", () -> new DaikataItem(Tiers.NETHERITE, 10, -2.0f,
                    defaultProp().stacksTo(1).rarity(Rarity.EPIC), "toy_hammer", DaikataItem.Skill.NEON_BARRAGE));
    public static final RegistryObject<Item> MUSOU_HIBISCUS_FALL =
            ITEMS.register("musou_hibiscus_fall", () -> new DaikataItem(Tiers.NETHERITE, 13, -2.4f,
                    defaultProp().stacksTo(1).rarity(Rarity.EPIC), "hibiscus_fall", DaikataItem.Skill.BLOOD_TIDE));
    public static final RegistryObject<Item> MUSOU_ABYSS_DEPTH =
            ITEMS.register("musou_abyss_depth", () -> new DaikataItem(Tiers.NETHERITE, 16, -2.8f,
                    defaultProp().stacksTo(1).rarity(Rarity.EPIC), "abyss_depth", DaikataItem.Skill.WATER_PRISON));
    public static final RegistryObject<Item> MUSOU_MAD_CARD =
            ITEMS.register("musou_mad_card", () -> new DaikataItem(Tiers.NETHERITE, 12, -2.2f,
                    defaultProp().stacksTo(1).rarity(Rarity.EPIC), "mad_card", DaikataItem.Skill.ROYAL_DECREE));

    /** 21 無上・絕對貓爪（最強武器 🔴） */
    public static final RegistryObject<Item> SUPREME_CAT_CLAW =
            ITEMS.register("supreme_cat_claw", () -> new SupremeCatClawItem(defaultProp().stacksTo(1).rarity(Rarity.EPIC)));

    // ══════════════════════════════════════════════════════════════════════
    // 裝備 — 披風、項圈、飾品
    // ══════════════════════════════════════════════════════════════════════

    /** 絨尾披風 🟡 */
    public static final RegistryObject<Item> VELVET_TAIL_CAPE =
            ITEMS.register("velvet_tail_cape", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CLOAK_ANTI_CORROSION =
            ITEMS.register("cloak_anti_corrosion", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CLOAK_MOONLIGHT =
            ITEMS.register("cloak_moonlight", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CLOAK_MEMORY =
            ITEMS.register("cloak_memory", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CLOAK_GUARDIAN =
            ITEMS.register("cloak_guardian", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CLOAK_WARM =
            ITEMS.register("cloak_warm", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> CLOAK_TRAVELER =
            ITEMS.register("cloak_traveler", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> CLOAK_THUNDER =
            ITEMS.register("cloak_thunder", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CLOAK_HIBISCUS =
            ITEMS.register("cloak_hibiscus", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CLOAK_PURR =
            ITEMS.register("cloak_purr", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CLOAK_ETERNAL =
            ITEMS.register("cloak_eternal", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> CLOAK_STARDUST =
            ITEMS.register("cloak_stardust", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CLOAK_CARDBOARD =
            ITEMS.register("cloak_cardboard", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SAVE_ANCHOR =
            ITEMS.register("save_anchor", () -> new SaveAnchorItem(defaultProp().stacksTo(16).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> MOONLIGHT_FOOTPRINT =
            ITEMS.register("moonlight_footprint", () -> new CatRingItem(defaultProp().stacksTo(1).rarity(Rarity.EPIC)));
    /** 月光項圈 🟡 */
    public static final RegistryObject<Item> MOONLIGHT_COLLAR =
            ITEMS.register("moonlight_collar", () -> new CatCollarItem(defaultProp().stacksTo(1).rarity(Rarity.UNCOMMON)));
    /** 星塵戒指 🟡 */
    public static final RegistryObject<Item> STARDUST_RING =
            ITEMS.register("stardust_ring", () -> new CatRingItem(defaultProp().stacksTo(1).rarity(Rarity.UNCOMMON)));

    // ══════════════════════════════════════════════════════════════════════
    // 序列道具
    // ══════════════════════════════════════════════════════════════════════

    /** 序列徽章（各序列解鎖憑證）🟡 */
    public static final RegistryObject<Item> SEQUENCE_BADGE =
            ITEMS.register("sequence_badge", () -> new SequenceBadgeItem(defaultProp().stacksTo(1)));
    public static final RegistryObject<Item> SEQUENCE_MANUAL =
            ITEMS.register("sequence_manual", () -> new SequenceManualItem(defaultProp().stacksTo(1)));

    // ══════════════════════════════════════════════════════════════════════
    // 特殊貨幣
    // ══════════════════════════════════════════════════════════════════════

    /** 呼嚕硬幣（貓之國通用貨幣）🟢 */
    public static final RegistryObject<Item> PURR_COIN = simple("purr_coin");
    /** 滿月幣（稀有貨幣）🟡 */
    public static final RegistryObject<Item> FULL_MOON_COIN = simple("full_moon_coin");

    // ══════════════════════════════════════════════════════════════════════
    // 刷怪蛋
    // ══════════════════════════════════════════════════════════════════════

    /** 可可刷怪蛋 */
    public static final RegistryObject<Item> COCO_SPAWN_EGG =
            ITEMS.register("coco_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.COCO, 0x111111, 0xFFD700, defaultProp()));
    /** 珍奶刷怪蛋 */
    public static final RegistryObject<Item> JENNA_SPAWN_EGG =
            ITEMS.register("jenna_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.JENNA, 0xCC8822, 0x222222, defaultProp()));
    /** 貓武士刷怪蛋 */
    public static final RegistryObject<Item> SAMURAI_CAT_SPAWN_EGG =
            ITEMS.register("samurai_cat_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.SAMURAI_CAT, 0x8B0000, 0xC0C0C0, defaultProp()));
    /** 相撲貓刷怪蛋 */
    public static final RegistryObject<Item> SUMO_CAT_SPAWN_EGG =
            ITEMS.register("sumo_cat_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.SUMO_CAT, 0xCC6644, 0xFFFFFF, defaultProp()));
    /** 貓僧刷怪蛋 */
    public static final RegistryObject<Item> MONK_CAT_SPAWN_EGG =
            ITEMS.register("monk_cat_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.MONK_CAT, 0x8B6914, 0x4A2810, defaultProp()));
    /** 貓將軍刷怪蛋 */
    public static final RegistryObject<Item> GENERAL_CAT_SPAWN_EGG =
            ITEMS.register("general_cat_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.GENERAL_CAT, 0xCC8822, 0x880022, defaultProp()));
    /** 影爪刷怪蛋（創造模式專用）*/
    public static final RegistryObject<Item> SHADOW_CLAW_SPAWN_EGG =
            ITEMS.register("shadow_claw_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.SHADOW_CLAW, 0x000000, 0x660088, defaultProp()));
    /** 三花子刷怪蛋 */
    public static final RegistryObject<Item> SANHUA_WEAVER_SPAWN_EGG =
            ITEMS.register("sanhua_weaver_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.SANHUA_WEAVER, 0xFFCCAA, 0xFFFFFF, defaultProp()));

    public static final RegistryObject<Item> HEAT_LEECH_SPAWN_EGG =
            ITEMS.register("heat_leech_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.HEAT_LEECH, 0x1A1A2E, 0x4488FF, defaultProp()));
    public static final RegistryObject<Item> FORGOTTEN_WISP_SPAWN_EGG =
            ITEMS.register("forgotten_wisp_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.FORGOTTEN_WISP, 0x888888, 0xCCCCCC, defaultProp()));
    public static final RegistryObject<Item> WHISPERING_DOLL_SPAWN_EGG =
            ITEMS.register("whispering_doll_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.WHISPERING_DOLL, 0x8B6914, 0x4A2810, defaultProp()));
    public static final RegistryObject<Item> MEMORY_MOTH_SPAWN_EGG =
            ITEMS.register("memory_moth_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.MEMORY_MOTH, 0x4B0082, 0x9932CC, defaultProp()));
    public static final RegistryObject<Item> MIMIC_CAT_SPAWN_EGG =
            ITEMS.register("mimic_cat_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.MIMIC_CAT, 0xCC8822, 0x222222, defaultProp()));

    public static final RegistryObject<Item> GRIEF_AMALGAM_SPAWN_EGG =
            ITEMS.register("grief_amalgam_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.GRIEF_AMALGAM, 0x4A0080, 0x220033, defaultProp()));
    public static final RegistryObject<Item> BLIND_WATER_LORD_SPAWN_EGG =
            ITEMS.register("blind_water_lord_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.BLIND_WATER_LORD, 0x001133, 0x000000, defaultProp()));
    public static final RegistryObject<Item> FALLEN_VELVET_SPAWN_EGG =
            ITEMS.register("fallen_velvet_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.FALLEN_VELVET, 0x880022, 0xFFD700, defaultProp()));
    public static final RegistryObject<Item> PRIMAL_CHAOS_SPAWN_EGG =
            ITEMS.register("primal_chaos_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.PRIMAL_CHAOS, 0x110000, 0xFF2200, defaultProp()));
    public static final RegistryObject<Item> CHESHIRE_SPAWN_EGG =
            ITEMS.register("cheshire_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.CHESHIRE, 0xCC8822, 0xFF88CC, defaultProp()));
    public static final RegistryObject<Item> WHITE_GLOVE_SPAWN_EGG =
            ITEMS.register("white_glove_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.WHITE_GLOVE, 0xEEEEEE, 0x333333, defaultProp()));
    public static final RegistryObject<Item> ALPHA_SPAWN_EGG =
            ITEMS.register("alpha_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.ALPHA, 0xAADDFF, 0xFFFFFF, defaultProp()));

    // ── 初啼村主支線 ─────────────────────────────────────────────────────
    public static final RegistryObject<Item> SOUL_DUST = simple("soul_dust");
    public static final RegistryObject<Item> FIRST_CRY_WHISKER = simple("first_cry_whisker");
    public static final RegistryObject<Item> DAMAGED_DIARY = simple("damaged_diary");
    public static final RegistryObject<Item> ANCIENT_CAT_PAW = simple("ancient_cat_paw");
    public static final RegistryObject<Item> PURIFIED_SALT = simple("purified_salt");

    // ── 秦可沐線物品 ─────────────────────────────────────────────────────
    public static final RegistryObject<Item> RED_PAPER = simple("red_paper");
    public static final RegistryObject<Item> MINT_MILK_CHOCOLATE = simple("mint_milk_chocolate");
    public static final RegistryObject<Item> PAPER_HAREM_DOLL = simple("paper_harem_doll");
    public static final RegistryObject<Item> TIGER_SEAL = simple("tiger_seal");
    public static final RegistryObject<Item> QIN_CHRONICLE = simple("qin_chronicle");
    public static final RegistryObject<Item> HIBISCUS_EARRING = simple("hibiscus_earring");
    public static final RegistryObject<Item> RED_PAPER_DAGGER = simple("red_paper_dagger");

    /** 孤勇者勳章 — Boss 單人模式 */
    public static final RegistryObject<Item> LONE_WOLF_MEDAL = ITEMS.register("lone_wolf_medal",
            () -> new Item(defaultProp().stacksTo(1).rarity(Rarity.EPIC)));

    // ══════════════════════════════════════════════════════════════════════
    // Helper
    // ══════════════════════════════════════════════════════════════════════

    private static RegistryObject<Item> simple(String name) {
        return ITEMS.register(name, () -> new Item(defaultProp()));
    }

    private static RegistryObject<Item> armor(String name, com.cocojenna.armor.ModArmorMaterials mat,
            ArmorItem.Type type) {
        return ITEMS.register(name, () -> new ArmorItem(mat, type, defaultProp()));
    }

    private static Item.Properties defaultProp() {
        return new Item.Properties();
    }
}
