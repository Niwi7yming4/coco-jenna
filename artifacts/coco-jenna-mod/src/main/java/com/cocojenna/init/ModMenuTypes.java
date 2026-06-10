package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.cloak.CloakWeaverMenu;
import com.cocojenna.gamble.BlackjackGambleMenu;
import com.cocojenna.shop.CatnipMarketMenu;
import com.cocojenna.shop.CheshireBlackMarketMenu;
import com.cocojenna.shop.ReputationShopMenu;
import com.cocojenna.shop.RyokatanaShopMenu;
import com.cocojenna.world.inventory.AromaDistillerMenu;
import com.cocojenna.world.inventory.DistillerMenu;
import com.cocojenna.world.inventory.IronpawForgeMenu;
import com.cocojenna.world.inventory.SocketingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CocoJennaMod.MOD_ID);

    public static final RegistryObject<MenuType<DistillerMenu>> DISTILLER =
            MENUS.register("distiller", () -> IForgeMenuType.create(DistillerMenu::new));

    public static final RegistryObject<MenuType<AromaDistillerMenu>> AROMA_DISTILLER =
            MENUS.register("aroma_distiller", () -> IForgeMenuType.create(AromaDistillerMenu::new));

    public static final RegistryObject<MenuType<IronpawForgeMenu>> IRONPAW_FORGE =
            MENUS.register("ironpaw_forge", () -> IForgeMenuType.create(IronpawForgeMenu::new));

    public static final RegistryObject<MenuType<SocketingMenu>> SOCKETING_TABLE =
            MENUS.register("socketing_table", () -> IForgeMenuType.create(SocketingMenu::new));

    public static final RegistryObject<MenuType<CloakWeaverMenu>> CLOAK_WEAVER =
            MENUS.register("cloak_weaver", () -> IForgeMenuType.create(CloakWeaverMenu::new));

    public static final RegistryObject<MenuType<RyokatanaShopMenu>> RYOKATANA_SHOP =
            MENUS.register("ryokatana_shop", () -> IForgeMenuType.create(RyokatanaShopMenu::new));

    public static final RegistryObject<MenuType<CheshireBlackMarketMenu>> CHESHIRE_BLACK_MARKET =
            MENUS.register("cheshire_black_market", () -> IForgeMenuType.create(CheshireBlackMarketMenu::new));

    public static final RegistryObject<MenuType<BlackjackGambleMenu>> BLACKJACK_GAMBLE =
            MENUS.register("blackjack_gamble", () -> IForgeMenuType.create(BlackjackGambleMenu::new));

    public static final RegistryObject<MenuType<CatnipMarketMenu>> CATNIP_MARKET =
            MENUS.register("catnip_market", () -> IForgeMenuType.create(CatnipMarketMenu::new));

    public static final RegistryObject<MenuType<ReputationShopMenu>> REPUTATION_SHOP =
            MENUS.register("reputation_shop", () -> IForgeMenuType.create(ReputationShopMenu::new));
}
