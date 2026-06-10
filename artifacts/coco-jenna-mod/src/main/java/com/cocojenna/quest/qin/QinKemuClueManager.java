package com.cocojenna.quest.qin;

import com.cocojenna.capability.BondData;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 秦可沐線索章 — 破碎村民/地表紅紙屑. */
public final class QinKemuClueManager {

    private QinKemuClueManager() {}

    public static void onRedPaperScrapFound(ServerPlayer player, BondData bond) {
        if (bond.getQinKemuQuestStage() > 0) return;
        bond.setQinKemuQuestStage(0);
        bond.addQinKemuFavor(2);
        player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.clue"), false);
    }

    public static void markSleepingChamber(ServerPlayer player, BlockPos pos) {
        player.getPersistentData().putLong("QinSleepingX", pos.getX());
        player.getPersistentData().putLong("QinSleepingZ", pos.getZ());
        player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.chamber_mark"), true);
    }

    public static void grantClueItem(ServerPlayer player) {
        ItemStack scrap = new ItemStack(ModItems.RED_PAPER.get());
        if (!player.addItem(scrap)) player.drop(scrap, false);
    }
}
