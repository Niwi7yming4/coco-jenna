package com.cocojenna.client;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.client.gui.AromaDistillerScreen;
import com.cocojenna.client.gui.CloakWeaverScreen;
import com.cocojenna.client.gui.DistillerScreen;
import com.cocojenna.client.gui.IronpawForgeScreen;
import com.cocojenna.client.gui.BlackjackGambleScreen;
import com.cocojenna.client.gui.CatnipMarketScreen;
import com.cocojenna.client.gui.CheshireBlackMarketScreen;
import com.cocojenna.client.gui.ReputationShopScreen;
import com.cocojenna.client.gui.RyokatanaShopScreen;
import com.cocojenna.client.gui.SocketingScreen;
import com.cocojenna.init.ModMenuTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModMenuScreens {

    @SubscribeEvent
    public static void register(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.DISTILLER.get(), DistillerScreen::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.AROMA_DISTILLER.get(), AromaDistillerScreen::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.IRONPAW_FORGE.get(), IronpawForgeScreen::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.SOCKETING_TABLE.get(), SocketingScreen::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.CLOAK_WEAVER.get(), CloakWeaverScreen::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.RYOKATANA_SHOP.get(), RyokatanaShopScreen::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.CHESHIRE_BLACK_MARKET.get(), CheshireBlackMarketScreen::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.BLACKJACK_GAMBLE.get(), BlackjackGambleScreen::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.CATNIP_MARKET.get(), CatnipMarketScreen::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.REPUTATION_SHOP.get(), ReputationShopScreen::new);
        });
    }
}
