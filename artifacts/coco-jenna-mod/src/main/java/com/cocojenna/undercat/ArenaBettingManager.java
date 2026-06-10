package com.cocojenna.undercat;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 競技場下注與觀戰獎勵（批次 D）. */
public final class ArenaBettingManager {

    private ArenaBettingManager() {}

    public static boolean placeBet(ServerPlayer player, int amount) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() < 3) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.need_ch3"), true);
            return false;
        }
        if (amount < 10 || amount > 50) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.bet.range"), true);
            return false;
        }
        if (bond.getArenaBetAmount() > 0) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.bet.active"), true);
            return false;
        }
        if (bond.getShadowCoins() < amount) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.daily.need_coins"), true);
            return false;
        }
        bond.addShadowCoins(-amount);
        bond.setArenaBetAmount(amount);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.bet.placed", amount), true);
        UndercatQuestManager.openHub(player);
        return true;
    }

    public static void onGladiatorWin(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        int bet = bond.getArenaBetAmount();
        if (bet <= 0) return;
        int payout = bet * 2;
        bond.addShadowCoins(payout);
        bond.setArenaBetAmount(0);
        bond.addUndercatRep(UndercatFaction.ARENA_BROTHERHOOD, 5);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.bet.won", payout), true);
        tryCompleteArenaDaily(player);
    }

    public static void onGladiatorLoss(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getArenaBetAmount() <= 0) return;
        int lost = bond.getArenaBetAmount();
        bond.setArenaBetAmount(0);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.bet.lost", lost), true);
    }

    private static void tryCompleteArenaDaily(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!bond.isUndercatDailyDone()
                && UndercatDailyQuestManager.current(bond) == UndercatDailyQuest.ARENA_TIP
                && bond.getArenaBetAmount() == 0) {
            UndercatDailyQuestManager.complete(player);
        }
    }
}
