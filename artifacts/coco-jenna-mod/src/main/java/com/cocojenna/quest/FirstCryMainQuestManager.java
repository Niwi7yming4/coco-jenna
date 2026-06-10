package com.cocojenna.quest;

import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.init.ModItems;
import com.cocojenna.reputation.ReputationHelper;
import com.cocojenna.world.FirstCryVillageGenerator;
import com.cocojenna.world.firstcry.HarborTravelManager;
import com.cocojenna.world.ruin.RuinMatrixSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 主線「初啼的呼喚」與支線「黑泥的秘密」. */
public final class FirstCryMainQuestManager {

    private FirstCryMainQuestManager() {}

    public static void onNpcTalk(ServerPlayer player, String npcId) {
        FirstCryProgress p = FirstCryProgress.get(player.serverLevel());
        switch (npcId) {
            case "ryokatsu" -> onRyokatsu(player, p);
            case "pagepaw" -> onPagepaw(player, p);
            default -> {}
        }
    }

    private static void onRyokatsu(ServerPlayer player, FirstCryProgress p) {
        switch (p.getCallingStage()) {
            case 0 -> {
                DialogueManager.play(player, "first_cry_calling_start");
                ItemStack paw = new ItemStack(ModItems.ANCIENT_CAT_PAW.get());
                if (!player.addItem(paw)) player.drop(paw, false);
                p.setCallingStage(1);
                FirstCryQuestHud.showCallingHint(player, 1);
            }
            case 1 -> FirstCryQuestHud.showCallingHint(player, 1);
            case 2 -> FirstCryQuestHud.showCallingHint(player, 2);
            case 3 -> {
                p.setCallingStage(4);
                DialogueManager.play(player, "first_cry_calling_pagepaw_hint");
                FirstCryQuestHud.showCallingHint(player, 4);
            }
            case 4 -> completeCalling(player, p);
            default -> player.displayClientMessage(
                    Component.translatable("quest.cocojenna.first_cry_calling.complete"), false);
        }
    }

    private static void onPagepaw(ServerPlayer player, FirstCryProgress p) {
        if (p.getCallingStage() == 3) {
            p.setCallingStage(4);
            markNearestRuin(player);
            player.displayClientMessage(Component.translatable("quest.cocojenna.first_cry_calling.dungeon_mark"), false);
            ReputationHelper.addRep(player, "first_cry", 10);
            FirstCryQuestHud.showCallingHint(player, 4);
        } else if (p.getCallingStage() == 4) {
            completeCalling(player, p);
        }
    }

    private static void markNearestRuin(ServerPlayer player) {
        RuinMatrixSavedData data = RuinMatrixSavedData.get(player.serverLevel());
        BlockPos nearest = data.nearestRuinTo(player.blockPosition(), 512);
        if (nearest != null) {
            player.getPersistentData().putLong("cocojenna_nearest_ruin", nearest.asLong());
            player.displayClientMessage(
                    Component.translatable("quest.cocojenna.first_cry_calling.ruin_hint",
                            nearest.getX(), nearest.getY(), nearest.getZ()), false);
        }
    }

    public static void onSacredTreePlace(ServerPlayer player) {
        FirstCryProgress p = FirstCryProgress.get(player.serverLevel());
        if (p.getCallingStage() != 1) return;
        BlockPos hole = FirstCryVillageGenerator.CENTER.below(2);
        if (player.blockPosition().distSqr(hole) > 16) return;
        p.setCallingStage(2);
        p.setSacredTreeBlessed(true);
        HarborTravelManager.unlockDefaultHarbor(player.serverLevel());
        com.cocojenna.quest.KingdomTutorialManager.onMonumentFound(player);
        player.displayClientMessage(Component.translatable("quest.cocojenna.first_cry_calling.tree"), false);
        FirstCryQuestHud.showCallingHint(player, 2);
    }

    public static void onCanopyTablet(ServerPlayer player) {
        FirstCryProgress p = FirstCryProgress.get(player.serverLevel());
        if (p.getCallingStage() == 2) {
            p.setCallingStage(3);
            player.displayClientMessage(Component.translatable("quest.cocojenna.first_cry_calling.fragment"), false);
            FirstCryQuestHud.showCallingHint(player, 3);
        }
    }

    public static void onMoonChamberOpened(ServerPlayer player) {
        player.displayClientMessage(Component.translatable("quest.cocojenna.first_cry_calling.moon_chamber"), false);
    }

    private static void completeCalling(ServerPlayer player, FirstCryProgress p) {
        p.setCallingStage(5);
        ItemStack whisker = new ItemStack(ModItems.FIRST_CRY_WHISKER.get());
        if (!player.addItem(whisker)) player.drop(whisker, false);
        ReputationHelper.addRep(player, "first_cry", 25);
        DialogueManager.play(player, "first_cry_calling_complete");
        FirstCryQuestHud.showCallingHint(player, 5);
    }

    public static void onDiaryFound(ServerPlayer player) {
        FirstCryProgress p = FirstCryProgress.get(player.serverLevel());
        if (p.getBlackMudStage() > 0) return;
        p.setBlackMudStage(1);
        DialogueManager.play(player, "black_mud_secret_start");
        FirstCryQuestHud.showBlackMudHint(player, p);
    }

    public static void onBlackMudPurify(ServerPlayer player) {
        FirstCryProgress p = FirstCryProgress.get(player.serverLevel());
        if (p.isBlackMudPurified()) return;
        if (!consumeItem(player, ModItems.PURIFIED_SALT.get(), 1)) return;
        com.cocojenna.world.firstcry.FirstCryBlackMudEventManager.onPurified(player.serverLevel());
        p.setBlackMudStage(5);
        DialogueManager.play(player, "black_mud_secret_complete");
        ReputationHelper.addRep(player, "first_cry", 15);
    }

    private static boolean consumeItem(ServerPlayer player, net.minecraft.world.item.Item item, int n) {
        int left = n;
        for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(item)) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
        return left == 0;
    }
}
