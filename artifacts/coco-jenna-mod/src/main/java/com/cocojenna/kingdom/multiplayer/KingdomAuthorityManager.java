package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.AfterRainManager;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.network.KingdomAuthoritySyncPacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

/** 王國內閣協調器 — 君主初始化、同步、Hub 狀態 */
public final class KingdomAuthorityManager {

    private KingdomAuthorityManager() {}

    public static void onFirstDawn(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        KingdomAuthoritySavedData auth = KingdomAuthoritySavedData.get(player.serverLevel());
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.isEndgameUnlocked() && auth.getMonarch() == null) {
            auth.ensureMonarch(player);
            syncToAll(player.serverLevel());
        }
    }

    public static void onPlayerLogin(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        KingdomAuthoritySavedData auth = KingdomAuthoritySavedData.get(player.serverLevel());
        auth.touchOnline(player);
        if (player.server.getPlayerCount() <= 1) auth.ensureSoloMonarch(player);
        syncToPlayer(player);
    }

    public static void syncToPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        KingdomAuthoritySavedData auth = KingdomAuthoritySavedData.get(player.serverLevel());
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new KingdomAuthoritySyncPacket(auth.toSyncTag()));
    }

    public static void syncToAll(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        KingdomAuthoritySavedData auth = KingdomAuthoritySavedData.get(level);
        KingdomAuthoritySyncPacket pkt = new KingdomAuthoritySyncPacket(auth.toSyncTag());
        for (ServerPlayer p : level.players()) {
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), pkt);
        }
    }

    public static void tickDaily(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (!AfterRainManager.isAfterRain(level)) return;
        KingdomAuthoritySavedData.get(level).tickDaily(level);
    }
}
