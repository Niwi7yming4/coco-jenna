package com.cocojenna.shop;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import com.cocojenna.item.RyokatanaRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/** 聲望閾值商店（設計書 1.8 + 批次 A）. */
public final class ReputationShopOffers {

    public enum Kind { RYOKATANA, ITEM }

    public record Offer(String id, String region, int repRequired, int coinCost, Kind kind,
                        String itemKey, boolean oneTime) {}

    public static final List<Offer> OFFERS = List.of(
            new Offer("gear_precision", "gear_town", 30, 120, Kind.RYOKATANA, "gear_precision_2", false),
            new Offer("gear_schedule", "gear_town", 80, 0, Kind.RYOKATANA, "gear_schedule", true),
            new Offer("first_cry_blade", "first_cry", 30, 80, Kind.RYOKATANA, "first_cry_beginner", false),
            new Offer("blind_stealth", "blind_port", 40, 150, Kind.RYOKATANA, "blind_water_stealth", false),
            new Offer("dawn_hope", "dawn", 60, 200, Kind.RYOKATANA, "dawn_hope", false),
            new Offer("royal_glory", "royal", 90, 0, Kind.RYOKATANA, "royal_glory", true),
            new Offer("ironpaw_token", "gear_town", 50, 60, Kind.ITEM, "cocojenna:ironpaw_charm", true),
            new Offer("scarface_token", "blind_port", 70, 80, Kind.ITEM, "cocojenna:scarface_charm", true),
            new Offer("shadow_crystal", "royal", 60, 100, Kind.ITEM, "cocojenna:shadow_crystal", false)
    );

    private ReputationShopOffers() {}

    public static boolean isUnlocked(ServerPlayer player, Offer offer) {
        BondData bond = ModCapabilities.getOrDefault(player);
        return bond.getReputation(offer.region()) >= offer.repRequired();
    }

    public static boolean tryPurchase(ServerPlayer player, int offerIndex) {
        if (offerIndex < 0 || offerIndex >= OFFERS.size()) return false;
        Offer offer = OFFERS.get(offerIndex);
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getReputation(offer.region()) < offer.repRequired()) {
            player.displayClientMessage(Component.translatable("shop.cocojenna.rep_locked",
                    offer.repRequired(), Component.translatable("reputation.cocojenna." + offer.region())), true);
            return false;
        }
        if (offer.oneTime() && bond.hasPurchasedRepOffer(offer.id())) {
            player.displayClientMessage(Component.translatable("shop.cocojenna.rep_owned"), true);
            return false;
        }
        if (offer.coinCost() > 0 && countCoins(player) < offer.coinCost()) {
            player.displayClientMessage(Component.translatable("shop.cocojenna.not_enough_coins"), true);
            return false;
        }
        ItemStack reward = resolveReward(offer);
        if (reward.isEmpty()) return false;
        if (offer.coinCost() > 0) removeCoins(player, offer.coinCost());
        if (!player.addItem(reward)) player.drop(reward, false);
        if (offer.oneTime()) bond.markRepOfferPurchased(offer.id());
        player.displayClientMessage(Component.translatable("shop.cocojenna.rep_purchased", reward.getHoverName()), true);
        return true;
    }

    private static ItemStack resolveReward(Offer offer) {
        if (offer.kind() == Kind.RYOKATANA) {
            var ro = RyokatanaRegistry.get(offer.itemKey());
            return ro != null ? new ItemStack(ro.get()) : ItemStack.EMPTY;
        }
        Item item = ForgeRegistries.ITEMS.getValue(new net.minecraft.resources.ResourceLocation(offer.itemKey()));
        return item != null ? new ItemStack(item) : ItemStack.EMPTY;
    }

    private static int countCoins(ServerPlayer player) {
        int total = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.getItem() == ModItems.PURR_COIN.get()) total += s.getCount();
        }
        return total;
    }

    private static void removeCoins(ServerPlayer player, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().items.size() && left > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (s.getItem() == ModItems.PURR_COIN.get()) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
    }
}
