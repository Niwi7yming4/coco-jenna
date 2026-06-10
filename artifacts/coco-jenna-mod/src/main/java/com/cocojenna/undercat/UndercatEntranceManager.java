package com.cocojenna.undercat;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 多入口傳送與首次演出. */
public final class UndercatEntranceManager {

    private UndercatEntranceManager() {}

    public static boolean tryEnter(ServerPlayer player, UndercatEntrance entrance, ItemStack held) {
        BondData bond = ModCapabilities.getOrDefault(player);

        if (entrance.needsHolyWater(bond, held)) {
            player.displayClientMessage(
                    Component.translatable("undercat.cocojenna.entrance.need_holy_water"), true);
            return false;
        }
        if (!entrance.canUse(bond)) {
            player.displayClientMessage(
                    Component.translatable("undercat.cocojenna.entrance.locked." + entrance.name().toLowerCase()),
                    true);
            return false;
        }

        if (bond.getUndercatChapter() <= 0 && entrance != UndercatEntrance.TREE_HOLE) {
            bond.setUndercatChapter(1);
            bond.setUndercatStage(0);
        }

        boolean first = !bond.hasSeenUndercatEntrance(entrance);
        if (first) {
            bond.markUndercatEntranceSeen(entrance);
            if (entrance.firstDialogueId != null) {
                DialogueManager.play(player, entrance.firstDialogueId);
            }
        }

        if (!isRegionUnlocked(bond, entrance.destination)) {
            UndercatQuestManager.unlockRegionProgress(bond, entrance.destination);
        }

        UndercatQuestManager.teleportToRegion(player, entrance.destination);
        player.displayClientMessage(Component.translatable(
                "undercat.cocojenna.entrance.arrived." + entrance.name().toLowerCase()), true);

        if (entrance == UndercatEntrance.TREE_HOLE) {
            bond.setUndercatStage(Math.max(1, bond.getUndercatStage()));
        }
        return true;
    }

    private static boolean isRegionUnlocked(BondData bond, UndercatRegion region) {
        return (bond.getUndercatRegions() & (1 << region.ordinal())) != 0;
    }
}
