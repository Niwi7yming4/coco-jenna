package com.cocojenna.undercat;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

/** 靜默圖書館三 room puzzle（壓力板 / lectern 序）. */
public final class SilentLibraryPuzzleManager {

    private static final String TAG = "cocojenna_silent_puzzle";

    private SilentLibraryPuzzleManager() {}

    public static int getProgress(ServerPlayer player) {
        return player.getPersistentData().getInt(TAG);
    }

    private static void setProgress(ServerPlayer player, int v) {
        player.getPersistentData().putInt(TAG, v);
    }

    public static void onPressurePlate(ServerPlayer player, BlockPos pos) {
        if (ModCapabilities.getOrDefault(player).getUndercatChapter() != 4) return;
        int p = getProgress(player);
        if (p == 0) {
            setProgress(player, 1);
            player.displayClientMessage(Component.translatable("undercat.cocojenna.puzzle.plate1"), true);
        } else if (p == 2) {
            setProgress(player, 3);
            player.displayClientMessage(Component.translatable("undercat.cocojenna.puzzle.plate2"), true);
            tryComplete(player);
        }
    }

    public static void onLectern(ServerPlayer player, BlockState state) {
        if (ModCapabilities.getOrDefault(player).getUndercatChapter() != 4) return;
        if (!state.is(Blocks.LECTERN)) return;
        int page = state.getValue(LecternBlock.HAS_BOOK) ? 1 : 0;
        int p = getProgress(player);
        if (p == 1 && page > 0) {
            setProgress(player, 2);
            player.displayClientMessage(Component.translatable("undercat.cocojenna.puzzle.lectern"), true);
        } else if (p == 3 && page > 0) {
            setProgress(player, 4);
            tryComplete(player);
        }
    }

    private static void tryComplete(ServerPlayer player) {
        if (getProgress(player) >= 4) {
            UndercatQuestManager.completeCommission(player, UndercatCommission.LIBRARY_PLATE);
            UndercatQuestManager.completeCommission(player, UndercatCommission.LIBRARY_LECTERN);
            UndercatQuestManager.completeCommission(player, UndercatCommission.LIBRARY_SEAL);
            DialogueManager.play(player, "undercat_ch4_trial");
            player.displayClientMessage(Component.translatable("undercat.cocojenna.puzzle.complete"), true);
        }
    }
}
