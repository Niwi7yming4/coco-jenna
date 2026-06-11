package com.cocojenna.event;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.entity.*;
import com.cocojenna.init.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 在 Mod 事件匯流排上註冊所有實體屬性。
 */
@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeRegistrationHandler {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.COCO.get(),          CocoEntity.createAttributes().build());
        event.put(ModEntities.JENNA.get(),         JennaEntity.createAttributes().build());
        event.put(ModEntities.SAMURAI_CAT.get(),   SamuraiCatEntity.createAttributes().build());
        event.put(ModEntities.SUMO_CAT.get(),      SumoCatEntity.createAttributes().build());
        event.put(ModEntities.COURT_LADY_CAT.get(), CourtLadyCatEntity.createAttributes().build());
        event.put(ModEntities.MONK_CAT.get(),      MonkCatEntity.createAttributes().build());
        event.put(ModEntities.GENERAL_CAT.get(),   GeneralCatEntity.createAttributes().build());
        event.put(ModEntities.SHADOW_CLAW.get(),   ShadowClawEntity.createAttributes().build());
        event.put(ModEntities.FUR_BALL_SPIRIT.get(), FurBallSpiritEntity.createAttributes().build());
        event.put(ModEntities.VELVET_MOTH.get(),   VelvetMothEntity.createAttributes().build());
        event.put(ModEntities.SANHUA_WEAVER.get(), SanhuaWeaverEntity.createAttributes().build());
        event.put(ModEntities.HEAT_LEECH.get(),       HeatLeechEntity.createAttributes().build());
        event.put(ModEntities.FORGOTTEN_WISP.get(),   ForgottenWispEntity.createAttributes().build());
        event.put(ModEntities.WHISPERING_DOLL.get(),  WhisperingDollEntity.createAttributes().build());
        event.put(ModEntities.MEMORY_MOTH.get(),      MemoryMothEntity.createAttributes().build());
        event.put(ModEntities.MIMIC_CAT.get(),        MimicCatEntity.createAttributes().build());
        event.put(ModEntities.GRIEF_AMALGAM.get(),    GriefAmalgamEntity.createAttributes().build());
        event.put(ModEntities.BLIND_WATER_LORD.get(), BlindWaterLordEntity.createAttributes().build());
        event.put(ModEntities.FALLEN_VELVET.get(),    FallenVelvetEntity.createAttributes().build());
        event.put(ModEntities.PRIMAL_CHAOS.get(),     PrimalChaosEntity.createAttributes().build());
        event.put(ModEntities.CHESHIRE.get(),         CheshireEntity.createAttributes().build());
        event.put(ModEntities.WHITE_GLOVE.get(),      WhiteGloveEntity.createAttributes().build());
        event.put(ModEntities.ALPHA.get(),            AlphaEntity.createAttributes().build());
        event.put(ModEntities.BLACKJACK_DEALER.get(), BlackjackDealerEntity.createAttributes().build());
        event.put(ModEntities.FALLEN_GENERAL.get(),
                RegionalBossEntity.attributesFor(BlackMudBossEntity.BossKind.FALLEN_GENERAL).build());
        event.put(ModEntities.HOWLING_SQUALL.get(),
                RegionalBossEntity.attributesFor(BlackMudBossEntity.BossKind.HOWLING_SQUALL).build());
        event.put(ModEntities.ASHURA_PHANTOM.get(),
                RegionalBossEntity.attributesFor(BlackMudBossEntity.BossKind.ASHURA_PHANTOM).build());
        event.put(ModEntities.GEAR_OVERLORD.get(),
                RegionalBossEntity.attributesFor(BlackMudBossEntity.BossKind.GEAR_OVERLORD).build());
        event.put(ModEntities.MOON_ALLEY_WRAITH.get(),
                RegionalBossEntity.attributesFor(BlackMudBossEntity.BossKind.MOON_ALLEY_WRAITH).build());
        event.put(ModEntities.MOON_GUARDIAN.get(),
                RegionalBossEntity.attributesFor(BlackMudBossEntity.BossKind.MOON_GUARDIAN).build());
        event.put(ModEntities.PLAZA_SENTINEL.get(),
                RegionalBossEntity.attributesFor(BlackMudBossEntity.BossKind.PLAZA_SENTINEL).build());
        event.put(ModEntities.FIRST_CRY_WARDEN.get(),
                RegionalBossEntity.attributesFor(BlackMudBossEntity.BossKind.FIRST_CRY_WARDEN).build());
        event.put(ModEntities.THOUSAND_FACE_STITCHER.get(),
                ThousandFaceStitcherEntity.createAttributes().build());
        event.put(ModEntities.CORRUGATA_QUEEN.get(), CorrugataQueenEntity.createAttributes().build());
        event.put(ModEntities.TAPE_COLOSSUS.get(), TapeColossusEntity.createAttributes().build());
        event.put(ModEntities.UNDERCAT_HUB_NPC.get(), UndercatHubNpcEntity.createAttributes().build());
        event.put(ModEntities.ARENA_GLADIATOR.get(), ArenaGladiatorEntity.createAttributes().build());
        event.put(ModEntities.CATNIP_DRAGON.get(), CatnipDragonEntity.createAttributes().build());
        event.put(ModEntities.SILENCED_ONE.get(), SilencedOneEntity.createAttributes().build());
        event.put(ModEntities.BOX_GHOST.get(), BoxGhostEntity.createAttributes().build());
        event.put(ModEntities.BLIND_WATER_LEECH.get(), BlindWaterLeechEntity.createAttributes().build());
        event.put(ModEntities.GLITCH_CAT.get(), GlitchCatEntity.createAttributes().build());
        event.put(ModEntities.ORIGAMI_CROW.get(), OrigamiCrowEntity.createAttributes().build());
        event.put(ModEntities.WILD_CAT.get(), WildCatEntity.createAttributes().build());
        event.put(ModEntities.TREASURE_HUNTER.get(), TreasureHunterNpcEntity.createAttributes().build());
        event.put(ModEntities.WANDERING_SLUDGE.get(), WanderingSludgeEntity.createAttributes().build());
        event.put(ModEntities.MUD_FARMER.get(), MudFarmerEntity.createAttributes().build());
        event.put(ModEntities.MUD_GUARD.get(), MudGuardEntity.createAttributes().build());
        event.put(ModEntities.MUD_PRIEST.get(), MudPriestEntity.createAttributes().build());
        event.put(ModEntities.TOWN_NPC_COMPANION.get(), TownNpcCompanionEntity.createAttributes().build());
        event.put(ModEntities.KINGDOM_KITTEN.get(), KingdomKittenEntity.createAttributes().build());
        event.put(ModEntities.QIN_KEMU.get(), QinKemuEntity.createAttributes().build());
        event.put(ModEntities.A_FANG.get(), AFangEntity.createAttributes().build());
        event.put(ModEntities.LI_JIANG.get(), LiJiangEntity.createAttributes().build());
        event.put(ModEntities.LI_QINGZHAO_CAT.get(), LiQingzhaoCatEntity.createAttributes().build());
        event.put(ModEntities.PRACTICE_SCARECROW.get(), PracticeScarecrowEntity.createAttributes().build());
        event.put(ModEntities.GHOST_TARGET.get(), GhostTargetEntity.createAttributes().build());
        event.put(ModEntities.TRIAL_BALLOON.get(), TrialBalloonEntity.createAttributes().build());
        event.put(ModEntities.GRAY_WHISKER.get(),
                com.cocojenna.overworld.GrayWhiskerNpcEntity.createAttributes().build());
        event.put(ModEntities.OVERWORLD_CAT.get(),
                com.cocojenna.overworld.OverworldCatNpcEntity.createAttributes().build());
    }
}
