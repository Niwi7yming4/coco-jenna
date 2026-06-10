package com.cocojenna.gamble;

import com.cocojenna.blackmud.BlackMudCorruptionManager;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import com.cocojenna.sequence.HiddenSequenceRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 命運博弈 — 三局兩勝，賭注為天氣與黑泥活性（設計書 11.6）. */
public final class BlackjackGambleManager {

    private static final long FATE_DURATION = 24000L * 3;
    private static final Map<UUID, Session> ACTIVE = new HashMap<>();

    public record Session(
            int matchRound,
            int playerMatchWins,
            int dealerMatchWins,
            int playerHand,
            int dealerHand,
            boolean dealerRevealed,
            boolean roundResolved
    ) {}

    private BlackjackGambleManager() {}

    public static Session getSession(ServerPlayer player) {
        return ACTIVE.get(player.getUUID());
    }

    public static void startMatch(ServerPlayer player) {
        if (ACTIVE.containsKey(player.getUUID())) return;
        ACTIVE.put(player.getUUID(), newSession(player, 1, 0, 0));
        player.displayClientMessage(Component.translatable("blackjack.cocojenna.match_start"), true);
    }

    public static void hit(ServerPlayer player) {
        Session s = ACTIVE.get(player.getUUID());
        if (s == null || s.roundResolved()) return;
        int next = s.playerHand() + drawCard(player);
        ACTIVE.put(player.getUUID(), new Session(s.matchRound(), s.playerMatchWins(), s.dealerMatchWins(),
                next, s.dealerHand(), s.dealerRevealed(), false));
        if (next > 21) {
            resolveRound(player, false);
        }
    }

    public static void stand(ServerPlayer player) {
        Session s = ACTIVE.get(player.getUUID());
        if (s == null || s.roundResolved()) return;
        int dealer = s.dealerHand();
        while (dealer < 17) {
            dealer += drawCard(player);
        }
        ACTIVE.put(player.getUUID(), new Session(s.matchRound(), s.playerMatchWins(), s.dealerMatchWins(),
                s.playerHand(), dealer, true, false));
        boolean playerWin = s.playerHand() <= 21 && (dealer > 21 || s.playerHand() > dealer);
        resolveRound(player, playerWin);
    }

    private static void resolveRound(ServerPlayer player, boolean playerWin) {
        Session s = ACTIVE.get(player.getUUID());
        if (s == null) return;
        int pWins = s.playerMatchWins() + (playerWin ? 1 : 0);
        int dWins = s.dealerMatchWins() + (playerWin ? 0 : 1);
        int round = s.matchRound();

        player.displayClientMessage(Component.translatable(
                playerWin ? "blackjack.cocojenna.round_win" : "blackjack.cocojenna.round_lose",
                s.playerHand(), s.dealerHand()), true);

        if (pWins >= 2 || dWins >= 2 || round >= 3) {
            finishMatch(player, pWins, dWins);
            return;
        }
        ACTIVE.put(player.getUUID(), newSession(player, round + 1, pWins, dWins));
    }

    private static Session newSession(ServerPlayer player, int round, int pWins, int dWins) {
        return new Session(round, pWins, dWins, drawCard(player) + drawCard(player), drawCard(player), false, false);
    }

    private static int drawCard(ServerPlayer player) {
        return 1 + player.getRandom().nextInt(10);
    }

    private static void finishMatch(ServerPlayer player, int pWins, int dWins) {
        ACTIVE.remove(player.getUUID());
        ServerLevel level = player.serverLevel();
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            ServerLevel ck = player.server.getLevel(ModDimensions.CAT_KINGDOM);
            if (ck != null) level = ck;
        }
        BlackjackGambleSavedData data = BlackjackGambleSavedData.get(level);
        long now = level.getGameTime();
        boolean playerWon = pWins > dWins;
        if (playerWon) {
            data.setFateBlessingUntil(now + FATE_DURATION);
            data.setPolarNightUntil(0);
            int total = data.addWin(player.getUUID());
            player.displayClientMessage(Component.translatable("blackjack.cocojenna.match_win", pWins, dWins)
                    .withStyle(ChatFormatting.GOLD), true);
            if (total == 3) {
                HiddenSequenceRegistry.tryUnlock(player, "cheshire_merchant");
            }
            if (total >= 5) {
                ItemStack chip = new ItemStack(ModItems.BLACKJACK_CHIP.get());
                if (!player.addItem(chip)) player.drop(chip, false);
                player.displayClientMessage(Component.translatable("blackjack.cocojenna.chip_reward"), true);
            }
        } else {
            data.setPolarNightUntil(now + FATE_DURATION);
            data.setFateBlessingUntil(0);
            player.displayClientMessage(Component.translatable("blackjack.cocojenna.match_lose", pWins, dWins)
                    .withStyle(ChatFormatting.DARK_RED), true);
        }
        BlackMudCorruptionManager.onBlackjackFateResolved(level, playerWon);
    }

    public static float blackMudSpreadMultiplier(ServerLevel level) {
        BlackjackGambleSavedData data = BlackjackGambleSavedData.get(level);
        long now = level.getGameTime();
        if (data.isFateBlessingActive(now)) return 0.5f;
        if (data.isPolarNightActive(now)) return 2.0f;
        return 1.0f;
    }

    public static boolean isPolarNightActive(ServerLevel level) {
        return BlackjackGambleSavedData.get(level).isPolarNightActive(level.getGameTime());
    }
}
