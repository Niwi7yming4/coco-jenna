package com.cocojenna.util;

import com.cocojenna.capability.BondData;
import com.cocojenna.item.SequenceBadgeItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Awards sequence badges when bond milestones are reached (設計書：序列系統).
 */
public final class SequenceUnlockHelper {

    private SequenceUnlockHelper() {}

    public static void checkMilestones(ServerPlayer player, BondData bond) {
        tryUnlock(player, bond, "a",
                bond.getCocoEmotionLevel().ordinal() >= BondData.EmotionLevel.BONDED.ordinal());
        tryUnlock(player, bond, "e",
                bond.getJennaEmotionLevel().ordinal() >= BondData.EmotionLevel.BONDED.ordinal());
        tryUnlock(player, bond, "c",
                bond.getCocoIndependence() >= 40f || bond.getJennaIndependence() >= 40f);
        tryUnlock(player, bond, "b", bond.getCocoAwakening() >= 5);
        tryUnlock(player, bond, "f", bond.getJennaAwakening() >= 5);
        tryUnlock(player, bond, "g", bond.getSisterBond() >= 80f);
        if (bond.isEndgameUnlocked()) {
            tryUnlock(player, bond, "d", true);
        }
    }

    private static void tryUnlock(ServerPlayer player, BondData bond, String id, boolean condition) {
        if (!condition || bond.hasSequence(id)) {
            return;
        }
        bond.unlockSequence(id);
        ItemStack badge = SequenceBadgeItem.of(id);
        if (!player.addItem(badge)) {
            player.drop(badge, false);
        }
        player.displayClientMessage(
                Component.translatable("cocojenna.sequence.unlocked", badge.getHoverName()), true);
    }
}
