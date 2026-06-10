package com.cocojenna.shop;

import com.cocojenna.init.ModItems;
import com.cocojenna.item.RyokatanaRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/** 良快刀商店（設計書 1.5）— 以呼嚕硬幣購買. */
public final class RyokatanaShopOffers {

    public record Offer(String ryokatanaId, int coinCost) {}

    public static final List<Offer> OFFERS = List.of(
            new Offer("first_cry_beginner", 200),
            new Offer("velvet_warmth", 400),
            new Offer("sanhua_thread", 450),
            new Offer("gear_precision_2", 500),
            new Offer("moonlight_clear", 550),
            new Offer("dawn_hope", 600),
            new Offer("hibiscus_blood", 700),
            new Offer("dark_tide", 800),
            new Offer("gear_schedule", 900),
            new Offer("royal_glory", 1000)
    );

    private RyokatanaShopOffers() {}

    /** 雙幣購買 — 記憶碎片或呼嚕幣. */
    public static boolean tryPurchaseWithShards(net.minecraft.server.level.ServerPlayer player, int offerIndex) {
        if (offerIndex < 0 || offerIndex >= OFFERS.size()) return false;
        Offer offer = OFFERS.get(offerIndex);
        int shardCost = offer.coinCost / 50;
        if (countShards(player) >= shardCost && shardCost > 0) {
            removeShards(player, shardCost);
        } else if (!tryPurchase(player, offerIndex)) {
            return false;
        } else {
            return true;
        }
        var ro = RyokatanaRegistry.get(offer.ryokatanaId);
        if (ro == null) return false;
        ItemStack stack = new ItemStack(ro.get());
        if (!player.addItem(stack)) player.drop(stack, false);
        return true;
    }

    private static int countShards(net.minecraft.server.level.ServerPlayer player) {
        int total = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(ModItems.MEMORY_SHARD.get())) total += s.getCount();
        }
        return total;
    }

    private static void removeShards(net.minecraft.server.level.ServerPlayer player, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().items.size() && left > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (s.is(ModItems.MEMORY_SHARD.get())) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
    }

    public static boolean tryPurchase(net.minecraft.server.level.ServerPlayer player, int offerIndex) {
        if (offerIndex < 0 || offerIndex >= OFFERS.size()) return false;
        Offer offer = OFFERS.get(offerIndex);
        int coins = countCoins(player);
        if (coins < offer.coinCost) return false;
        removeCoins(player, offer.coinCost);
        var ro = RyokatanaRegistry.get(offer.ryokatanaId);
        if (ro == null) return false;
        ItemStack stack = new ItemStack(ro.get());
        if (!player.addItem(stack)) player.drop(stack, false);
        return true;
    }

    private static int countCoins(net.minecraft.server.level.ServerPlayer player) {
        int total = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.getItem() == ModItems.PURR_COIN.get()) total += s.getCount();
        }
        for (ItemStack s : player.getInventory().items) {
            if (s.getItem() == Items.GOLD_INGOT) total += s.getCount();
        }
        return total;
    }

    private static void removeCoins(net.minecraft.server.level.ServerPlayer player, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().items.size() && left > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (s.getItem() == ModItems.PURR_COIN.get() || s.getItem() == Items.GOLD_INGOT) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
    }
}
