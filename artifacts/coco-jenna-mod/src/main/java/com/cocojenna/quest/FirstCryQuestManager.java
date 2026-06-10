package com.cocojenna.quest;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.init.ModItems;
import com.cocojenna.util.MemoryShardUtil;
import com.cocojenna.util.SequenceUnlockHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * 初啼村新手任務線 — GALGAME 對話驅動.
 */
public final class FirstCryQuestManager {

    public static final int STAGE_NONE = 0;
    public static final int STAGE_MET_ELDER = 1;
    public static final int STAGE_FED_CATS = 2;
    public static final int STAGE_MET_SAMURAI = 3;
    public static final int STAGE_DUEL_DONE = 4;
    public static final int STAGE_COMPLETE = 5;

    private FirstCryQuestManager() {}

    public static void onElderTalk(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        int stage = bond.getFirstCryQuestStage();
        if (stage >= STAGE_COMPLETE) {
            player.displayClientMessage(Component.translatable("quest.cocojenna.first_cry.complete"), false);
            return;
        }
        if (stage == STAGE_DUEL_DONE) {
            DialogueManager.play(player, "first_cry_elder_reward");
            return;
        }
        if (stage < STAGE_MET_ELDER) {
            DialogueManager.play(player, "first_cry_elder_welcome");
        } else {
            DialogueManager.play(player, "first_cry_elder_hint");
        }
    }

    public static void onElderDialogueEnd(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getFirstCryQuestStage() < STAGE_MET_ELDER) {
            bond.setFirstCryQuestStage(STAGE_MET_ELDER);
        }
        TutorialGuideManager.grantAlphaShardIfNeeded(player);
    }

    public static void onRewardDialogueEnd(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getFirstCryQuestStage() == STAGE_DUEL_DONE) {
            completeQuest(player, bond);
        }
    }

    public static void onFoodBowlFilled(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getFirstCryQuestStage() == STAGE_MET_ELDER) {
            bond.setFirstCryQuestStage(STAGE_FED_CATS);
            player.displayClientMessage(Component.translatable("quest.cocojenna.first_cry.stage2"), true);
        }
    }

    public static void onSamuraiTalk(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        int stage = bond.getFirstCryQuestStage();
        if (stage == STAGE_FED_CATS) {
            DialogueManager.play(player, "first_cry_samurai_meet");
        } else if (stage >= STAGE_MET_SAMURAI && stage < STAGE_DUEL_DONE) {
            DialogueManager.play(player, "first_cry_samurai_duel");
        }
    }

    public static void onSamuraiDialogueEnd(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getFirstCryQuestStage() == STAGE_FED_CATS) {
            bond.setFirstCryQuestStage(STAGE_MET_SAMURAI);
        }
    }

    public static void onSamuraiDefeated(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getFirstCryQuestStage() >= STAGE_MET_SAMURAI
                && bond.getFirstCryQuestStage() < STAGE_COMPLETE) {
            bond.setFirstCryQuestStage(STAGE_DUEL_DONE);
            DialogueManager.play(player, "first_cry_samurai_defeat");
        }
    }

    private static void completeQuest(ServerPlayer player, BondData bond) {
        bond.setFirstCryQuestStage(STAGE_COMPLETE);

        ItemStack reward = new ItemStack(ModItems.RYOKATANA_RED_JADE.get());
        if (!player.addItem(reward)) {
            player.drop(reward, false);
        }
        ItemStack shard = MemoryShardUtil.create("first_cry_quest");
        if (!player.addItem(shard)) {
            player.drop(shard, false);
        }
        ItemStack book = new ItemStack(ModItems.SEQUENCE_MANUAL.get());
        if (!player.addItem(book)) {
            player.drop(book, false);
        }

        if (bond.getFelineTier() <= 0) bond.setFelineTier(9);
        bond.setGuardian(true);
        com.cocojenna.reputation.ReputationHelper.onQuestComplete(player, "first_cry");

        player.displayClientMessage(Component.translatable("quest.cocojenna.first_cry.reward"), true);
        SequenceUnlockHelper.checkMilestones(player, bond);
    }
}
