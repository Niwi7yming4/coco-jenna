package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OfflineTaskPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class OfflineTaskManager {

    private OfflineTaskManager() {}

    public static void setTask(ServerPlayer player, String type, int param, long endTick) {
        var mp = ModCapabilities.getOrDefault(player).getMultiplayerSection();
        mp.setOfflineTaskType(type);
        mp.setOfflineTaskParam(param);
        mp.setOfflineTaskEndTime(endTick);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.offline_task_set", type), true);
    }

    public static void tickLogout(ServerPlayer player) {
        var mp = ModCapabilities.getOrDefault(player).getMultiplayerSection();
        if ("donor".equals(mp.getOfflineTaskType()) && mp.getOfflineTaskParam() > 0) {
            // 離線捐贈在每日 tick 處理
        }
    }

    public static void tickDaily(ServerPlayer player) {
        var bond = ModCapabilities.getOrDefault(player);
        var mp = bond.getMultiplayerSection();
        if (!"donor".equals(mp.getOfflineTaskType())) return;
        int amount = mp.getOfflineTaskParam();
        if (amount <= 0) return;
        boolean solo = player.server.getPlayerCount() <= 1;
        int reward = Math.max(1, amount / 2);
        if (solo) {
            reward = (int) Math.ceil(reward * 1.25);
            mp.addContributionPoints(reward);
            player.displayClientMessage(
                    Component.translatable("kingdom.cocojenna.offline_task_solo_reward", reward), true);
            return;
        }
        if (bond.getKingdomProsperity() >= amount) {
            bond.setKingdomProsperity(bond.getKingdomProsperity() - amount);
            bond.setKingdomProsperity(bond.getKingdomProsperity() + amount);
            mp.addContributionPoints(reward);
        }
    }
}
