package com.cocojenna.economy;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import com.cocojenna.item.CatnipItem;
import com.cocojenna.undercat.UndercatFaction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

/** 貓薄荷每日市價與走私碼頭交易. */
public final class CatnipMarketManager {

    private CatnipMarketManager() {}

    public static int dailyPrice(CatnipQuality quality, long day) {
        float wave = 0.72f + ((day % 7) + (day / 7 % 5)) * 0.08f;
        float jitter = 0.9f + (day % 3) * 0.05f;
        return Math.max(2, (int) (quality.basePrice() * wave * jitter));
    }

    public static CatnipQuality rollHarvestQuality(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.06f) return CatnipQuality.LEGENDARY;
        if (roll < 0.28f) return CatnipQuality.RARE;
        return CatnipQuality.COMMON;
    }

    public static int countCatnip(ServerPlayer player, CatnipQuality quality) {
        int total = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (CatnipItem.isQuality(stack, quality)) total += stack.getCount();
        }
        return total;
    }

    public static boolean sellOne(ServerPlayer player, CatnipQuality quality, long day) {
        int slot = findSlot(player, quality);
        if (slot < 0) {
            player.displayClientMessage(Component.translatable("economy.cocojenna.catnip.none"), true);
            return false;
        }
        ItemStack stack = player.getInventory().getItem(slot);
        stack.shrink(1);
        int pay = dailyPrice(quality, day);
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.addShadowCoins(pay);
        bond.addUndercatRep(UndercatFaction.SMUGGLER_UNION, 1);
        if (quality == CatnipQuality.LEGENDARY) {
            bond.addUndercatRep(UndercatFaction.SMUGGLER_UNION, 2);
        }
        player.displayClientMessage(Component.translatable("economy.cocojenna.catnip.sold",
                Component.translatable(CatnipItem.qualityKey(quality)), pay), true);
        return true;
    }

    public static boolean sellStack(ServerPlayer player, CatnipQuality quality, long day) {
        int slot = findSlot(player, quality);
        if (slot < 0) {
            player.displayClientMessage(Component.translatable("economy.cocojenna.catnip.none"), true);
            return false;
        }
        ItemStack stack = player.getInventory().getItem(slot);
        int count = stack.getCount();
        stack.setCount(0);
        int pay = dailyPrice(quality, day) * count;
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.addShadowCoins(pay);
        bond.addUndercatRep(UndercatFaction.SMUGGLER_UNION, Math.min(8, 1 + count / 4));
        player.displayClientMessage(Component.translatable("economy.cocojenna.catnip.sold_stack",
                count, Component.translatable(CatnipItem.qualityKey(quality)), pay), true);
        return true;
    }

    private static int findSlot(ServerPlayer player, CatnipQuality quality) {
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.getItem() == ModItems.CATNIP_ITEM.get() && CatnipItem.isQuality(s, quality)) return i;
        }
        return -1;
    }
}
