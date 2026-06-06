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
    /** 貓薄荷（物品形式） 🟢 */
    public static final RegistryObject<Item> CATNIP_ITEM = simple("catnip_item");
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
    /** 暗影結晶 🟡 */
    public static final RegistryObject<Item> SHADOW_CRYSTAL = simple("shadow_crystal");
    /** 混沌結晶 🟡 */
    public static final RegistryObject<Item> CHAOS_CRYSTAL = simple("chaos_crystal");
    /** 盲水凝膠 🟡 */
    public static final RegistryObject<Item> BLIND_WATER_GEL = simple("blind_water_gel");
    /** 黑珍珠 🟡 */
    public static final RegistryObject<Item> BLACK_PEARL = simple("black_pearl");
    /** 記憶微粒 🟡 */
    public static final RegistryObject<Item> MEMORY_PARTICLE = simple("memory_particle");
    /** 純淨之淚 🟡 */
    public static final RegistryObject<Item> PURE_TEAR = simple("pure_tear");
    /** 深海珍珠 🟡 */
    public static final RegistryObject<Item> DEEP_SEA_PEARL = simple("deep_sea_pearl");

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

    // 良快刀（各 50 把，代表性清單）
    /** 爪痕・月影 🟡 */
    public static final RegistryObject<Item> RYOKATANA_MOON_SHADOW =
            ITEMS.register("ryokatana_moon_shadow", () -> new RyokatanaItem(Tiers.IRON, 1, -2.2f, defaultProp(), "moon_shadow"));
    /** 朱槿・泣血 🟡 */
    public static final RegistryObject<Item> RYOKATANA_HIBISCUS_BLOOD =
            ITEMS.register("ryokatana_hibiscus_blood", () -> new RyokatanaItem(Tiers.DIAMOND, 4, -2.4f, defaultProp(), "hibiscus_blood"));
    /** 黎明・希望 🟡 */
    public static final RegistryObject<Item> RYOKATANA_DAWN_HOPE =
            ITEMS.register("ryokatana_dawn_hope", () -> new RyokatanaItem(Tiers.DIAMOND, 5, -2.4f, defaultProp(), "dawn_hope"));
    /** 齒輪・精工 🟡 */
    public static final RegistryObject<Item> RYOKATANA_PRECISION_GEAR =
            ITEMS.register("ryokatana_precision_gear", () -> new RyokatanaItem(Tiers.IRON, 3, -2.6f, defaultProp(), "precision_gear"));
    /** 無明港・暗潮 🟡 */
    public static final RegistryObject<Item> RYOKATANA_DARK_TIDE =
            ITEMS.register("ryokatana_dark_tide", () -> new RyokatanaItem(Tiers.DIAMOND, 3, -2.0f, defaultProp(), "dark_tide"));

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
    /** 21 無上・絕對貓爪（最強武器 🔴） */
    public static final RegistryObject<Item> SUPREME_CAT_CLAW =
            ITEMS.register("supreme_cat_claw", () -> new SupremeCatClawItem(defaultProp().stacksTo(1).rarity(Rarity.EPIC)));

    // ══════════════════════════════════════════════════════════════════════
    // 裝備 — 披風、項圈、飾品
    // ══════════════════════════════════════════════════════════════════════

    /** 絨尾披風 🟡 */
    public static final RegistryObject<Item> VELVET_TAIL_CAPE =
            ITEMS.register("velvet_tail_cape", () -> new CatCloakItem(defaultProp().stacksTo(1).rarity(Rarity.RARE)));
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
    /** 影爪刷怪蛋（創造模式專用）*/
    public static final RegistryObject<Item> SHADOW_CLAW_SPAWN_EGG =
            ITEMS.register("shadow_claw_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.SHADOW_CLAW, 0x000000, 0x660088, defaultProp()));

    // ══════════════════════════════════════════════════════════════════════
    // Helper
    // ══════════════════════════════════════════════════════════════════════

    private static RegistryObject<Item> simple(String name) {
        return ITEMS.register(name, () -> new Item(defaultProp()));
    }

    private static Item.Properties defaultProp() {
        return new Item.Properties();
    }
}
