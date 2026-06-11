package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.GlobalEventContributionPacket;
import com.cocojenna.network.GlobalEventStartPacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public final class GlobalEventManager {

    private GlobalEventManager() {}

    public static void tickDaily(ServerLevel level) {
        KingdomEventSavedData ev = KingdomEventSavedData.get(level);
        long day = level.getDayTime() / 24000L;
        boolean wasActive = ev.isPurgeActive();
        ev.tickDay(level, day);
        if (ev.isPurgeActive() && !wasActive) {
            ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(),
                    new GlobalEventStartPacket(true, 5000));
            for (ServerPlayer p : level.players()) {
                p.displayClientMessage(Component.translatable("kingdom.cocojenna.purge_start"), true);
            }
        }
    }

    public static void onPurify(ServerPlayer player, int points) {
        KingdomEventSavedData ev = KingdomEventSavedData.get(player.serverLevel());
        int scaled = points;
        if (player.server.getPlayerCount() <= 1) {
            scaled = (int) Math.ceil(points * 1.5);
        }
        ev.addPurgeContribution(scaled);
        ModCapabilities.getOrDefault(player).getMultiplayerSection().addBlackMudPurified(points);
        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(),
                new GlobalEventContributionPacket(ev.getPurgeContribution()));
    }
}
