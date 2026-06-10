package com.cocojenna.shop;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import com.cocojenna.item.RyokatanaRegistry;
import com.cocojenna.undercat.UndercatFaction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** 走私碼頭違禁品黑市 — 暗影幣購買（DLC 設計書）. */
public final class SmugglerBlackMarketOffers {

    public record Offer(String ryokatanaId, int shadowCost, int minRep) {}

    public static final List<Offer> OFFERS = List.of(
            new Offer("blind_water_stealth", 80, 10),
            new Offer("dark_tide", 120, 20),
            new Offer("neon_flash", 90, 15),
            new Offer("gear_schedule", 100, 25),
            new Offer("blind_water_core", 150, 40)
    );

    private SmugglerBlackMarketOffers() {}

    public static boolean tryPurchase(ServerPlayer player, int offerIndex) {
        if (offerIndex < 0 || offerIndex >= OFFERS.size()) return false;
        Offer offer = OFFERS.get(offerIndex);
        var bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatRep(UndercatFaction.SMUGGLER_UNION) < offer.minRep) {
            player.displayClientMessage(Component.translatable("shop.cocojenna.need_smuggler_rep"), true);
            return false;
        }
        if (bond.getShadowCoins() < offer.shadowCost) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.daily.need_coins"), true);
            return false;
        }
        bond.addShadowCoins(-offer.shadowCost);
        var ro = RyokatanaRegistry.get(offer.ryokatanaId);
        if (ro == null) return false;
        ItemStack stack = new ItemStack(ro.get());
        if (!player.addItem(stack)) player.drop(stack, false);
        bond.addUndercatRep(UndercatFaction.SMUGGLER_UNION, 3);
        player.displayClientMessage(Component.translatable("shop.cocojenna.purchased",
                Component.translatable("item.cocojenna.ryokatana_" + offer.ryokatanaId())), true);
        return true;
    }

    public static boolean tryBuyContraband(ServerPlayer player, String itemId) {
        int cost = switch (itemId) {
            case "tape_core" -> 60;
            case "shadow_coin_bundle" -> 40;
            case "legend_catnip_seed" -> 75;
            default -> -1;
        };
        if (cost < 0) return false;
        var bond = ModCapabilities.getOrDefault(player);
        if (bond.getShadowCoins() < cost) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.daily.need_coins"), true);
            return false;
        }
        bond.addShadowCoins(-cost);
        ItemStack stack = switch (itemId) {
            case "tape_core" -> new ItemStack(ModItems.TAPE_CORE.get());
            case "shadow_coin_bundle" -> new ItemStack(ModItems.SHADOW_COIN.get(), 25);
            case "legend_catnip_seed" -> new ItemStack(ModItems.LEGEND_CATNIP_SEED.get());
            default -> ItemStack.EMPTY;
        };
        if (stack.isEmpty()) return false;
        if (!player.addItem(stack)) player.drop(stack, false);
        player.displayClientMessage(Component.translatable("shop.cocojenna.smuggler_bought",
                Component.translatable("shop.cocojenna.contraband." + itemId)), true);
        return true;
    }
}
