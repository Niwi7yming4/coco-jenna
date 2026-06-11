package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CocoJennaMod.MOD_ID);

    // ── 主角伴侶 ───────────────────────────────────────────────────────────

    /** 可可 — 黑貓・沉默的守護者 🐈‍⬛ */
    public static final RegistryObject<EntityType<CocoEntity>> COCO =
            ENTITY_TYPES.register("coco",
                    () -> EntityType.Builder.<CocoEntity>of(CocoEntity::new, MobCategory.CREATURE)
                            .sized(0.8f, 0.7f)
                            .clientTrackingRange(10)
                            .updateInterval(3)
                            .build("coco"));

    /** 珍奶 — 玳瑁貓・活潑的陪伴者 🐈 */
    public static final RegistryObject<EntityType<JennaEntity>> JENNA =
            ENTITY_TYPES.register("jenna",
                    () -> EntityType.Builder.<JennaEntity>of(JennaEntity::new, MobCategory.CREATURE)
                            .sized(0.7f, 0.6f)
                            .clientTrackingRange(10)
                            .updateInterval(3)
                            .build("jenna"));

    // ── 貓之國 NPC ─────────────────────────────────────────────────────────

    /** 貓武士 (Samurai Cat) ⚔️ */
    public static final RegistryObject<EntityType<SamuraiCatEntity>> SAMURAI_CAT =
            ENTITY_TYPES.register("samurai_cat",
                    () -> EntityType.Builder.<SamuraiCatEntity>of(SamuraiCatEntity::new, MobCategory.CREATURE)
                            .sized(0.8f, 1.8f)
                            .clientTrackingRange(8)
                            .build("samurai_cat"));

    /** 相撲貓 (Sumo Cat) 🏋️ */
    public static final RegistryObject<EntityType<SumoCatEntity>> SUMO_CAT =
            ENTITY_TYPES.register("sumo_cat",
                    () -> EntityType.Builder.<SumoCatEntity>of(SumoCatEntity::new, MobCategory.CREATURE)
                            .sized(1.4f, 1.6f)
                            .clientTrackingRange(8)
                            .build("sumo_cat"));

    /** 仕女貓 (Court Lady Cat) 💃 */
    public static final RegistryObject<EntityType<CourtLadyCatEntity>> COURT_LADY_CAT =
            ENTITY_TYPES.register("court_lady_cat",
                    () -> EntityType.Builder.<CourtLadyCatEntity>of(CourtLadyCatEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.6f)
                            .clientTrackingRange(8)
                            .build("court_lady_cat"));

    /** 貓僧 (Monk Cat) 🧘 */
    public static final RegistryObject<EntityType<MonkCatEntity>> MONK_CAT =
            ENTITY_TYPES.register("monk_cat",
                    () -> EntityType.Builder.<MonkCatEntity>of(MonkCatEntity::new, MobCategory.CREATURE)
                            .sized(0.7f, 1.7f)
                            .clientTrackingRange(8)
                            .build("monk_cat"));

    /** 貓將軍 (General Cat — Boss) 👑 */
    public static final RegistryObject<EntityType<GeneralCatEntity>> GENERAL_CAT =
            ENTITY_TYPES.register("general_cat",
                    () -> EntityType.Builder.<GeneralCatEntity>of(GeneralCatEntity::new, MobCategory.CREATURE)
                            .sized(1.2f, 2.2f)
                            .clientTrackingRange(10)
                            .build("general_cat"));

    /** 篡位者・影爪 (Shadow Claw — Final Boss) 🌑 */
    public static final RegistryObject<EntityType<ShadowClawEntity>> SHADOW_CLAW =
            ENTITY_TYPES.register("shadow_claw",
                    () -> EntityType.Builder.<ShadowClawEntity>of(ShadowClawEntity::new, MobCategory.MONSTER)
                            .sized(2.0f, 3.0f)
                            .clientTrackingRange(12)
                            .build("shadow_claw"));

    // ── 特殊生物 ───────────────────────────────────────────────────────────

    /** 毛球精靈 (Fur Ball Spirit) ☁️ */
    public static final RegistryObject<EntityType<FurBallSpiritEntity>> FUR_BALL_SPIRIT =
            ENTITY_TYPES.register("fur_ball_spirit",
                    () -> EntityType.Builder.<FurBallSpiritEntity>of(FurBallSpiritEntity::new, MobCategory.AMBIENT)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(6)
                            .build("fur_ball_spirit"));

    /** 絨蛾 (Velvet Moth) 🦋 */
    public static final RegistryObject<EntityType<VelvetMothEntity>> VELVET_MOTH =
            ENTITY_TYPES.register("velvet_moth",
                    () -> EntityType.Builder.<VelvetMothEntity>of(VelvetMothEntity::new, MobCategory.AMBIENT)
                            .sized(0.4f, 0.3f)
                            .clientTrackingRange(6)
                            .build("velvet_moth"));

    /** 記憶紡織娘・三花子 — 披風縫製 NPC */
    public static final RegistryObject<EntityType<SanhuaWeaverEntity>> SANHUA_WEAVER =
            ENTITY_TYPES.register("sanhua_weaver",
                    () -> EntityType.Builder.<SanhuaWeaverEntity>of(SanhuaWeaverEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.6f)
                            .clientTrackingRange(8)
                            .build("sanhua_weaver"));

    /** 封印物 (Sealed Entity — 戰鬥後凝結的敵人) 💊 */
    public static final RegistryObject<EntityType<SealedEntity>> SEALED_ENTITY =
            ENTITY_TYPES.register("sealed_entity",
                    () -> EntityType.Builder.<SealedEntity>of(SealedEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(6)
                            .build("sealed_entity"));

    // ── 黑泥怪物 ───────────────────────────────────────────────────────────

    public static final RegistryObject<EntityType<HeatLeechEntity>> HEAT_LEECH =
            ENTITY_TYPES.register("heat_leech",
                    () -> EntityType.Builder.<HeatLeechEntity>of(HeatLeechEntity::new, MobCategory.MONSTER)
                            .sized(0.7f, 1.2f).clientTrackingRange(8).build("heat_leech"));

    public static final RegistryObject<EntityType<ForgottenWispEntity>> FORGOTTEN_WISP =
            ENTITY_TYPES.register("forgotten_wisp",
                    () -> EntityType.Builder.<ForgottenWispEntity>of(ForgottenWispEntity::new, MobCategory.MONSTER)
                            .sized(0.5f, 0.5f).clientTrackingRange(8).build("forgotten_wisp"));

    public static final RegistryObject<EntityType<WhisperingDollEntity>> WHISPERING_DOLL =
            ENTITY_TYPES.register("whispering_doll",
                    () -> EntityType.Builder.<WhisperingDollEntity>of(WhisperingDollEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.4f).clientTrackingRange(8).build("whispering_doll"));

    public static final RegistryObject<EntityType<MemoryMothEntity>> MEMORY_MOTH =
            ENTITY_TYPES.register("memory_moth",
                    () -> EntityType.Builder.<MemoryMothEntity>of(MemoryMothEntity::new, MobCategory.MONSTER)
                            .sized(0.5f, 0.35f).clientTrackingRange(8).build("memory_moth"));

    public static final RegistryObject<EntityType<MimicCatEntity>> MIMIC_CAT =
            ENTITY_TYPES.register("mimic_cat",
                    () -> EntityType.Builder.<MimicCatEntity>of(MimicCatEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 0.7f).clientTrackingRange(8).build("mimic_cat"));

    public static final RegistryObject<EntityType<GriefAmalgamEntity>> GRIEF_AMALGAM =
            ENTITY_TYPES.register("grief_amalgam",
                    () -> EntityType.Builder.<GriefAmalgamEntity>of(GriefAmalgamEntity::new, MobCategory.MONSTER)
                            .sized(1.2f, 2.0f).clientTrackingRange(12).build("grief_amalgam"));

    public static final RegistryObject<EntityType<BlindWaterLordEntity>> BLIND_WATER_LORD =
            ENTITY_TYPES.register("blind_water_lord",
                    () -> EntityType.Builder.<BlindWaterLordEntity>of(BlindWaterLordEntity::new, MobCategory.MONSTER)
                            .sized(1.4f, 3.5f).clientTrackingRange(12).build("blind_water_lord"));

    public static final RegistryObject<EntityType<FallenVelvetEntity>> FALLEN_VELVET =
            ENTITY_TYPES.register("fallen_velvet",
                    () -> EntityType.Builder.<FallenVelvetEntity>of(FallenVelvetEntity::new, MobCategory.MONSTER)
                            .sized(1.0f, 2.2f).clientTrackingRange(12).build("fallen_velvet"));

    public static final RegistryObject<EntityType<PrimalChaosEntity>> PRIMAL_CHAOS =
            ENTITY_TYPES.register("primal_chaos",
                    () -> EntityType.Builder.<PrimalChaosEntity>of(PrimalChaosEntity::new, MobCategory.MONSTER)
                            .sized(2.0f, 3.0f).clientTrackingRange(14).build("primal_chaos"));

    public static final RegistryObject<EntityType<CheshireEntity>> CHESHIRE =
            ENTITY_TYPES.register("cheshire",
                    () -> EntityType.Builder.<CheshireEntity>of(CheshireEntity::new, MobCategory.CREATURE)
                            .sized(0.8f, 1.8f).clientTrackingRange(10).build("cheshire"));

    public static final RegistryObject<EntityType<WhiteGloveEntity>> WHITE_GLOVE =
            ENTITY_TYPES.register("white_glove",
                    () -> EntityType.Builder.<WhiteGloveEntity>of(WhiteGloveEntity::new, MobCategory.CREATURE)
                            .sized(0.8f, 1.9f).clientTrackingRange(10).build("white_glove"));

    public static final RegistryObject<EntityType<AlphaEntity>> ALPHA =
            ENTITY_TYPES.register("alpha",
                    () -> EntityType.Builder.<AlphaEntity>of(AlphaEntity::new, MobCategory.CREATURE)
                            .sized(0.7f, 1.8f).clientTrackingRange(12).build("alpha"));

    public static final RegistryObject<EntityType<BlackjackDealerEntity>> BLACKJACK_DEALER =
            ENTITY_TYPES.register("blackjack_dealer",
                    () -> EntityType.Builder.<BlackjackDealerEntity>of(BlackjackDealerEntity::new, MobCategory.CREATURE)
                            .sized(0.8f, 1.8f).clientTrackingRange(10).build("blackjack_dealer"));

    // ── 額外區域黑泥首領 ─────────────────────────────────────────────────

    public static final RegistryObject<EntityType<RegionalBossEntity>> FALLEN_GENERAL =
            registerRegionalBoss("fallen_general", BlackMudBossEntity.BossKind.FALLEN_GENERAL, 1.1f, 2.2f);

    public static final RegistryObject<EntityType<RegionalBossEntity>> HOWLING_SQUALL =
            registerRegionalBoss("howling_squall", BlackMudBossEntity.BossKind.HOWLING_SQUALL, 1.3f, 2.4f);

    public static final RegistryObject<EntityType<RegionalBossEntity>> ASHURA_PHANTOM =
            registerRegionalBoss("ashura_phantom", BlackMudBossEntity.BossKind.ASHURA_PHANTOM, 1.2f, 2.5f);

    public static final RegistryObject<EntityType<RegionalBossEntity>> GEAR_OVERLORD =
            registerRegionalBoss("gear_overlord", BlackMudBossEntity.BossKind.GEAR_OVERLORD, 1.5f, 2.8f);

    public static final RegistryObject<EntityType<RegionalBossEntity>> MOON_ALLEY_WRAITH =
            registerRegionalBoss("moon_alley_wraith", BlackMudBossEntity.BossKind.MOON_ALLEY_WRAITH, 1.2f, 2.3f);

    public static final RegistryObject<EntityType<RegionalBossEntity>> MOON_GUARDIAN =
            registerRegionalBoss("moon_guardian", BlackMudBossEntity.BossKind.MOON_GUARDIAN, 1.3f, 2.5f);

    public static final RegistryObject<EntityType<RegionalBossEntity>> PLAZA_SENTINEL =
            registerRegionalBoss("plaza_sentinel", BlackMudBossEntity.BossKind.PLAZA_SENTINEL, 1.3f, 2.6f);

    public static final RegistryObject<EntityType<RegionalBossEntity>> FIRST_CRY_WARDEN =
            registerRegionalBoss("first_cry_warden", BlackMudBossEntity.BossKind.FIRST_CRY_WARDEN, 1.0f, 2.0f);

    public static final RegistryObject<EntityType<ThousandFaceStitcherEntity>> THOUSAND_FACE_STITCHER =
            ENTITY_TYPES.register("thousand_face_stitcher",
                    () -> EntityType.Builder.<ThousandFaceStitcherEntity>of(
                                    ThousandFaceStitcherEntity::new, MobCategory.MONSTER)
                            .sized(1.6f, 3.2f).clientTrackingRange(16).build("thousand_face_stitcher"));

    /** 紙箱女王・瓦楞 */
    public static final RegistryObject<EntityType<CorrugataQueenEntity>> CORRUGATA_QUEEN =
            ENTITY_TYPES.register("corrugata_queen",
                    () -> EntityType.Builder.<CorrugataQueenEntity>of(CorrugataQueenEntity::new, MobCategory.CREATURE)
                            .sized(0.7f, 1.4f).clientTrackingRange(10).build("corrugata_queen"));

    /** 膠帶巨像 Boss */
    public static final RegistryObject<EntityType<TapeColossusEntity>> TAPE_COLOSSUS =
            ENTITY_TYPES.register("tape_colossus",
                    () -> EntityType.Builder.<TapeColossusEntity>of(TapeColossusEntity::new, MobCategory.MONSTER)
                            .sized(2.5f, 4.0f).clientTrackingRange(16).fireImmune().build("tape_colossus"));

    public static final RegistryObject<EntityType<UndercatHubNpcEntity>> UNDERCAT_HUB_NPC =
            ENTITY_TYPES.register("undercat_hub_npc",
                    () -> EntityType.Builder.<UndercatHubNpcEntity>of(UndercatHubNpcEntity::new, MobCategory.CREATURE)
                            .sized(0.7f, 1.4f).clientTrackingRange(10).build("undercat_hub_npc"));

    public static final RegistryObject<EntityType<ArenaGladiatorEntity>> ARENA_GLADIATOR =
            ENTITY_TYPES.register("arena_gladiator",
                    () -> EntityType.Builder.<ArenaGladiatorEntity>of(ArenaGladiatorEntity::new, MobCategory.MONSTER)
                            .sized(0.8f, 1.5f).clientTrackingRange(10).build("arena_gladiator"));

    public static final RegistryObject<EntityType<CatnipDragonEntity>> CATNIP_DRAGON =
            ENTITY_TYPES.register("catnip_dragon",
                    () -> EntityType.Builder.<CatnipDragonEntity>of(CatnipDragonEntity::new, MobCategory.MONSTER)
                            .sized(2.8f, 2.2f).clientTrackingRange(16).build("catnip_dragon"));

    public static final RegistryObject<EntityType<SilencedOneEntity>> SILENCED_ONE =
            ENTITY_TYPES.register("silenced_one",
                    () -> EntityType.Builder.<SilencedOneEntity>of(SilencedOneEntity::new, MobCategory.MONSTER)
                            .sized(0.9f, 2.0f).clientTrackingRange(12).build("silenced_one"));

    public static final RegistryObject<EntityType<BoxGhostEntity>> BOX_GHOST =
            ENTITY_TYPES.register("box_ghost",
                    () -> EntityType.Builder.<BoxGhostEntity>of(BoxGhostEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.2f).clientTrackingRange(8).build("box_ghost"));

    public static final RegistryObject<EntityType<BlindWaterLeechEntity>> BLIND_WATER_LEECH =
            ENTITY_TYPES.register("blind_water_leech",
                    () -> EntityType.Builder.<BlindWaterLeechEntity>of(BlindWaterLeechEntity::new, MobCategory.MONSTER)
                            .sized(0.5f, 0.4f).clientTrackingRange(8).build("blind_water_leech"));

    /** 螢幕雜訊貓 — 原生生物 §10.2 */
    public static final RegistryObject<EntityType<GlitchCatEntity>> GLITCH_CAT =
            ENTITY_TYPES.register("glitch_cat",
                    () -> EntityType.Builder.<GlitchCatEntity>of(GlitchCatEntity::new, MobCategory.CREATURE)
                            .sized(0.55f, 0.65f).clientTrackingRange(10).build("glitch_cat"));

    /** 未完稿・折紙鴉 — 原生生物 §10.2 */
    public static final RegistryObject<EntityType<OrigamiCrowEntity>> ORIGAMI_CROW =
            ENTITY_TYPES.register("origami_crow",
                    () -> EntityType.Builder.<OrigamiCrowEntity>of(OrigamiCrowEntity::new, MobCategory.MONSTER)
                            .sized(0.9f, 0.7f).clientTrackingRange(12).build("origami_crow"));

    public static final RegistryObject<EntityType<MemoryMothSlimeProjectile>> MEMORY_MOTH_SLIME =
            ENTITY_TYPES.register("memory_moth_slime",
                    () -> EntityType.Builder.<MemoryMothSlimeProjectile>of(MemoryMothSlimeProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("memory_moth_slime"));

    /** 怪貓貓（15 變體）— 設計書第四章 */
    public static final RegistryObject<EntityType<WildCatEntity>> WILD_CAT =
            ENTITY_TYPES.register("wild_cat",
                    () -> EntityType.Builder.<WildCatEntity>of(WildCatEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 0.7f).clientTrackingRange(10).build("wild_cat"));

    /** 寶藏獵人・鏽鼻 — 設計書 5.2 */
    public static final RegistryObject<EntityType<TreasureHunterNpcEntity>> TREASURE_HUNTER =
            ENTITY_TYPES.register("treasure_hunter",
                    () -> EntityType.Builder.<TreasureHunterNpcEntity>of(TreasureHunterNpcEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.8f).clientTrackingRange(8).build("treasure_hunter"));

    /** 灰鬚賢者 — 主世界滲透引導 */
    public static final RegistryObject<EntityType<com.cocojenna.overworld.GrayWhiskerNpcEntity>> GRAY_WHISKER =
            ENTITY_TYPES.register("gray_whisker",
                    () -> EntityType.Builder.<com.cocojenna.overworld.GrayWhiskerNpcEntity>of(
                                    com.cocojenna.overworld.GrayWhiskerNpcEntity::new, MobCategory.CREATURE)
                            .sized(0.7f, 0.8f).clientTrackingRange(10).build("gray_whisker"));

    /** 流浪黑泥 — 主世界夜晚生成 */
    public static final RegistryObject<EntityType<WanderingSludgeEntity>> WANDERING_SLUDGE =
            ENTITY_TYPES.register("wandering_sludge",
                    () -> EntityType.Builder.<WanderingSludgeEntity>of(WanderingSludgeEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 0.6f).clientTrackingRange(8).build("wandering_sludge"));

    /** 黑泥農夫 — 主世界腐化農場 */
    public static final RegistryObject<EntityType<MudFarmerEntity>> MUD_FARMER =
            ENTITY_TYPES.register("mud_farmer",
                    () -> EntityType.Builder.<MudFarmerEntity>of(MudFarmerEntity::new, MobCategory.MONSTER)
                            .sized(0.7f, 1.8f).clientTrackingRange(8).build("mud_farmer"));

    /** 黑泥守衛 — 汙染神殿 */
    public static final RegistryObject<EntityType<MudGuardEntity>> MUD_GUARD =
            ENTITY_TYPES.register("mud_guard",
                    () -> EntityType.Builder.<MudGuardEntity>of(MudGuardEntity::new, MobCategory.MONSTER)
                            .sized(0.8f, 1.9f).clientTrackingRange(8).build("mud_guard"));

    /** 黑泥祭司 — 汙染神殿祭壇 */
    public static final RegistryObject<EntityType<MudPriestEntity>> MUD_PRIEST =
            ENTITY_TYPES.register("mud_priest",
                    () -> EntityType.Builder.<MudPriestEntity>of(MudPriestEntity::new, MobCategory.MONSTER)
                            .sized(0.7f, 1.9f).clientTrackingRange(8).build("mud_priest"));

    public static final RegistryObject<EntityType<PracticeScarecrowEntity>> PRACTICE_SCARECROW =
            ENTITY_TYPES.register("practice_scarecrow",
                    () -> EntityType.Builder.<PracticeScarecrowEntity>of(PracticeScarecrowEntity::new, MobCategory.MISC)
                            .sized(0.6f, 1.8f).clientTrackingRange(8).build("practice_scarecrow"));

    public static final RegistryObject<EntityType<GhostTargetEntity>> GHOST_TARGET =
            ENTITY_TYPES.register("ghost_target",
                    () -> EntityType.Builder.<GhostTargetEntity>of(GhostTargetEntity::new, MobCategory.MISC)
                            .sized(0.4f, 0.4f).clientTrackingRange(8).build("ghost_target"));

    public static final RegistryObject<EntityType<TrialBalloonEntity>> TRIAL_BALLOON =
            ENTITY_TYPES.register("trial_balloon",
                    () -> EntityType.Builder.<TrialBalloonEntity>of(TrialBalloonEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f).clientTrackingRange(8).build("trial_balloon"));

    /** 雨後王國招募 NPC 伴侶實體 */
    public static final RegistryObject<EntityType<TownNpcCompanionEntity>> TOWN_NPC_COMPANION =
            ENTITY_TYPES.register("town_npc_companion",
                    () -> EntityType.Builder.<TownNpcCompanionEntity>of(TownNpcCompanionEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 0.7f).clientTrackingRange(10).build("town_npc_companion"));

    /** 王國幼貓 — 多人個人夥伴 */
    public static final RegistryObject<EntityType<KingdomKittenEntity>> KINGDOM_KITTEN =
            ENTITY_TYPES.register("kingdom_kitten",
                    () -> EntityType.Builder.<KingdomKittenEntity>of(KingdomKittenEntity::new, MobCategory.CREATURE)
                            .sized(0.4f, 0.5f).clientTrackingRange(8).build("kingdom_kitten"));

    /** 始皇貓 · 秦可沐 */
    public static final RegistryObject<EntityType<com.cocojenna.entity.QinKemuEntity>> QIN_KEMU =
            ENTITY_TYPES.register("qin_kemu",
                    () -> EntityType.Builder.<com.cocojenna.entity.QinKemuEntity>of(
                                    com.cocojenna.entity.QinKemuEntity::new, MobCategory.CREATURE)
                            .sized(0.7f, 0.7f).clientTrackingRange(10).build("qin_kemu"));

    public static final RegistryObject<EntityType<com.cocojenna.entity.AFangEntity>> A_FANG =
            ENTITY_TYPES.register("a_fang",
                    () -> EntityType.Builder.<com.cocojenna.entity.AFangEntity>of(
                                    com.cocojenna.entity.AFangEntity::new, MobCategory.CREATURE)
                            .sized(0.5f, 0.5f).clientTrackingRange(8).build("a_fang"));

    public static final RegistryObject<EntityType<com.cocojenna.entity.LiJiangEntity>> LI_JIANG =
            ENTITY_TYPES.register("li_jiang",
                    () -> EntityType.Builder.<com.cocojenna.entity.LiJiangEntity>of(
                                    com.cocojenna.entity.LiJiangEntity::new, MobCategory.CREATURE)
                            .sized(0.5f, 0.5f).clientTrackingRange(8).build("li_jiang"));

    public static final RegistryObject<EntityType<com.cocojenna.entity.LiQingzhaoCatEntity>> LI_QINGZHAO_CAT =
            ENTITY_TYPES.register("li_qingzhao_cat",
                    () -> EntityType.Builder.<com.cocojenna.entity.LiQingzhaoCatEntity>of(
                                    com.cocojenna.entity.LiQingzhaoCatEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 0.6f).clientTrackingRange(8).build("li_qingzhao_cat"));

    /** 主世界流亡貓 NPC */
    public static final RegistryObject<EntityType<com.cocojenna.overworld.OverworldCatNpcEntity>> OVERWORLD_CAT =
            ENTITY_TYPES.register("overworld_cat",
                    () -> EntityType.Builder.<com.cocojenna.overworld.OverworldCatNpcEntity>of(
                                    com.cocojenna.overworld.OverworldCatNpcEntity::new, MobCategory.CREATURE)
                            .sized(0.55f, 0.65f).clientTrackingRange(8).build("overworld_cat"));

    private static RegistryObject<EntityType<RegionalBossEntity>> registerRegionalBoss(
            String id, BlackMudBossEntity.BossKind kind, float width, float height) {
        return ENTITY_TYPES.register(id,
                () -> EntityType.Builder.<RegionalBossEntity>of(
                                (type, level) -> new RegionalBossEntity(type, level, kind),
                                MobCategory.MONSTER)
                        .sized(width, height).clientTrackingRange(12).build(id));
    }
}
