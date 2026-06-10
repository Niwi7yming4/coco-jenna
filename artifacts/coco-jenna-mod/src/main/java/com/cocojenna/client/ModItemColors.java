package com.cocojenna.client;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.init.ModItems;
import com.cocojenna.item.RyokatanaRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 自訂生怪蛋貼圖為全彩圖，關閉 ForgeSpawnEggItem 的底色／斑點染色。
 */
@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModItemColors {

    private ModItemColors() {}

    @SubscribeEvent
    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        RyokatanaRegistry.all().forEach((variantId, ro) -> {
            int tint = RyokatanaTintColors.colorFor(variantId);
            event.register((stack, layer) -> tint, ro.get());
        });

        for (RegistryObject<Item> ro : ModItems.ITEMS.getEntries()) {
            Item item = ro.get();
            var id = ForgeRegistries.ITEMS.getKey(item);
            if (id != null && CocoJennaMod.MOD_ID.equals(id.getNamespace())
                    && id.getPath().endsWith("_spawn_egg")) {
                event.register((stack, tint) -> 0xFFFFFFFF, item);
            }
        }
    }
}
