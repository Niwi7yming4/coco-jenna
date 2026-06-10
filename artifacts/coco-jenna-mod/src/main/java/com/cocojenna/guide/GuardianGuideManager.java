package com.cocojenna.guide;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 首次進入貓之國發放《守護者指南》. */
public final class GuardianGuideManager {

    private GuardianGuideManager() {}

    public static void onFirstEnterCatKingdom(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.hasReceivedGuardianGuide()) return;
        bond.setReceivedGuardianGuide(true);
        if (bond.getCatKingdomEnterDay() <= 0) {
            bond.setCatKingdomEnterDay(player.level().getDayTime() / 24000L);
        }

        ItemStack guide = new ItemStack(ModItems.GUARDIAN_GUIDE.get());
        if (!player.addItem(guide)) player.drop(guide, false);

        ItemStack hood = new ItemStack(ModItems.VELVET_BEGINNER_HELMET.get());
        if (!player.addItem(hood)) player.drop(hood, false);

        player.displayClientMessage(Component.translatable("guide.cocojenna.welcome"), false);
        player.displayClientMessage(Component.translatable("guide.cocojenna.open_hint"), true);
        com.cocojenna.quest.TutorialGuideManager.onFirstEnterCatKingdom(player);
        if (player.level() instanceof net.minecraft.server.level.ServerLevel sl) {
            com.cocojenna.exploration.DungeonEntranceRegistry.syncForPlayer(sl, player);
            com.cocojenna.exploration.DungeonWorldData.syncBossStateForPlayer(sl, player);
        }
    }
}
