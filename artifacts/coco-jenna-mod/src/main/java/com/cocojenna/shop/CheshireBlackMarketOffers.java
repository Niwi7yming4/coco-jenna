package com.cocojenna.shop;

import com.cocojenna.init.ModItems;
import com.cocojenna.item.RyokatanaRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** 柴郡黑市 — 以黑泥殘骸購買良快刀（設計書 1.4 #31）. */
public final class CheshireBlackMarketOffers {

    public record Offer(String ryokatanaId, int remnantCost) {}

    public static final List<Offer> OFFERS = List.of(
            new Offer("screen_noise", 200),
            new Offer("neon_flash", 300),
            new Offer("paper_crow_ink", 450),
            new Offer("dark_tide", 650),
            new Offer("cheshire_grin", 500)
    );

    private CheshireBlackMarketOffers() {}

    public static boolean tryPurchase(net.minecraft.server.level.ServerPlayer player, int offerIndex) {
        if (offerIndex < 0 || offerIndex >= OFFERS.size()) return false;
        Offer offer = OFFERS.get(offerIndex);
        if (countRemnants(player) < offer.remnantCost) {
            player.displayClientMessage(Component.translatable("shop.cocojenna.not_enough_remnants"), true);
            return false;
        }
        removeRemnants(player, offer.remnantCost);
        var ro = RyokatanaRegistry.get(offer.ryokatanaId);
        if (ro == null) return false;
        ItemStack stack = new ItemStack(ro.get());
        if (!player.addItem(stack)) player.drop(stack, false);
        player.displayClientMessage(Component.translatable("shop.cocojenna.purchased",
                Component.translatable("item.cocojenna.ryokatana_" + offer.ryokatanaId())), true);
        return true;
    }

    private static int countRemnants(net.minecraft.server.level.ServerPlayer player) {
        int total = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.getItem() == ModItems.BLACK_MUD_REMNANT.get()) total += s.getCount();
        }
        return total;
    }

    private static void removeRemnants(net.minecraft.server.level.ServerPlayer player, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().items.size() && left > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (s.getItem() == ModItems.BLACK_MUD_REMNANT.get()) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
    }
}
