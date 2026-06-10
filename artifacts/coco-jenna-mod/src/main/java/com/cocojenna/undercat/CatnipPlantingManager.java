package com.cocojenna.undercat;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.economy.CatnipMarketManager;
import com.cocojenna.economy.CatnipQuality;
import com.cocojenna.init.ModItems;
import com.cocojenna.item.CatnipItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 貓薄荷種植小遊戲 — 節奏三連判定（批次 D）. */
public final class CatnipPlantingManager {

    private CatnipPlantingManager() {}

    public static void begin(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() < 2) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.need_ch2"), true);
            return;
        }
        com.cocojenna.network.ModNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new com.cocojenna.network.OpenCatnipPlantPacket(player.getRandom().nextLong()));
    }

    public static void finish(ServerPlayer player, int hits, int misses) {
        BondData bond = ModCapabilities.getOrDefault(player);
        int score = Math.max(0, hits - misses);
        bond.setCatnipPlantStreak(Math.max(bond.getCatnipPlantStreak(), score));

        CatnipQuality quality = CatnipQuality.COMMON;
        if (score >= 3) quality = CatnipMarketManager.rollHarvestQuality(player.getRandom());
        if (score >= 5) {
            quality = player.getRandom().nextFloat() < 0.15f
                    ? CatnipQuality.LEGENDARY : CatnipQuality.RARE;
        }
        if (score <= 0) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.plant.fail"), true);
            UndercatQuestManager.openHub(player);
            return;
        }

        int count = 1 + score / 2;
        ItemStack harvest = CatnipItem.createStack(quality, count);
        if (!player.addItem(harvest)) player.drop(harvest, false);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.plant.success",
                Component.translatable(CatnipItem.qualityKey(quality)), count), true);
        UndercatDailyQuestManager.onCatnipPlanted(player);
        UndercatQuestManager.openHub(player);
    }

    public static boolean plantWithSeed(ServerPlayer player) {
        int slot = -1;
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            if (player.getInventory().getItem(i).is(ModItems.LEGEND_CATNIP_SEED.get())) {
                slot = i;
                break;
            }
        }
        if (slot >= 0) {
            player.getInventory().getItem(slot).shrink(1);
        } else if (ModCapabilities.getOrDefault(player).getShadowCoins() < 10) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.plant.need_seed"), true);
            return false;
        } else {
            ModCapabilities.getOrDefault(player).addShadowCoins(-10);
        }
        begin(player);
        return true;
    }
}
