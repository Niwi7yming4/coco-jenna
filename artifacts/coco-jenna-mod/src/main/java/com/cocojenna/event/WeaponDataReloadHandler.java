package com.cocojenna.event;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.weapon.WeaponResonanceMaterials;
import com.cocojenna.weapon.WeaponSkillRegistry;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WeaponDataReloadHandler {

    private WeaponDataReloadHandler() {}

    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) {
        event.addListener(WeaponSkillRegistry.INSTANCE);
        event.addListener(WeaponResonanceMaterials.INSTANCE);
    }
}
