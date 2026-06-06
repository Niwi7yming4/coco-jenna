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

    /** 封印物 (Sealed Entity — 戰鬥後凝結的敵人) 💊 */
    public static final RegistryObject<EntityType<SealedEntity>> SEALED_ENTITY =
            ENTITY_TYPES.register("sealed_entity",
                    () -> EntityType.Builder.<SealedEntity>of(SealedEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(6)
                            .build("sealed_entity"));
}
