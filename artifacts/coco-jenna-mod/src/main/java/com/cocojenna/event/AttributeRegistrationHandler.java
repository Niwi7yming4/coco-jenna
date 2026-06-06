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
    }
}
