package com.cocojenna.shop;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkHooks;

/** 開啟商店 GUI（伺服器端）. */
public final class ShopOpener {

    private ShopOpener() {}

    public static void openCatnipMarket(ServerPlayer player) {
        long day = player.level().getDayTime() / 24000L;
        NetworkHooks.openScreen(player, new SimpleMenuProvider(
                (id, inv, p) -> {
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    CatnipMarketMenu.writePrices(buf, day);
                    return new CatnipMarketMenu(id, inv, buf);
                },
                Component.translatable("economy.cocojenna.catnip.title")), BlockPos.ZERO);
    }

    public static void openReputationShop(ServerPlayer player, BlockPos pos) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider(
                (id, inv, p) -> new ReputationShopMenu(id, inv),
                Component.translatable("shop.cocojenna.reputation.title")), pos);
    }
}
