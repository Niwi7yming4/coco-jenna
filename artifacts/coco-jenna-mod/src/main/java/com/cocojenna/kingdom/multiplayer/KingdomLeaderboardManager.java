package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.capability.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class KingdomLeaderboardManager {

    private KingdomLeaderboardManager() {}

    public static void show(ServerPlayer player) {
        KingdomLeaderboardSavedData lb = KingdomLeaderboardSavedData.get(player.serverLevel());
        player.displayClientMessage(Component.literal("§6—— 王國排行榜 ——"), false);
        showBoard(player, lb, KingdomLeaderboardSavedData.Board.ARENA, "競技場");
        showBoard(player, lb, KingdomLeaderboardSavedData.Board.CATNIP, "貓薄荷");
        showBoard(player, lb, KingdomLeaderboardSavedData.Board.MEMORY, "記憶");
        showBoard(player, lb, KingdomLeaderboardSavedData.Board.PURIFY, "淨化");
        showBoard(player, lb, KingdomLeaderboardSavedData.Board.SOLO_WOLF, "獨狼");
    }

    private static void showBoard(ServerPlayer player, KingdomLeaderboardSavedData lb,
                                  KingdomLeaderboardSavedData.Board board, String label) {
        var top = lb.top(board, 3);
        player.displayClientMessage(Component.literal("§e" + label + ":"), false);
        int rank = 1;
        for (var e : top) {
            player.displayClientMessage(Component.literal("  " + rank++ + ". " + e.getKey() + " — " + e.getValue()), false);
        }
    }

    public static void syncFromBond(ServerPlayer player) {
        var bond = ModCapabilities.getOrDefault(player);
        var mp = bond.getMultiplayerSection();
        KingdomLeaderboardSavedData lb = KingdomLeaderboardSavedData.get(player.serverLevel());
        lb.addScore(KingdomLeaderboardSavedData.Board.ARENA, player.getUUID(), mp.getArenaScore());
        lb.addScore(KingdomLeaderboardSavedData.Board.CATNIP, player.getUUID(), mp.getCatnipPlantStreak());
        lb.addScore(KingdomLeaderboardSavedData.Board.MEMORY, player.getUUID(), bond.getMemoryShardsTotal());
        lb.addScore(KingdomLeaderboardSavedData.Board.PURIFY, player.getUUID(), mp.getBlackMudPurified());
        int solo = (mp.isSoloBossClear() ? 100 : 0) + (mp.isSoloCeremonyFlawless() ? 50 : 0)
                + (mp.isSoloRuinClear() ? 30 : 0);
        lb.addScore(KingdomLeaderboardSavedData.Board.SOLO_WOLF, player.getUUID(), solo);
    }
}
