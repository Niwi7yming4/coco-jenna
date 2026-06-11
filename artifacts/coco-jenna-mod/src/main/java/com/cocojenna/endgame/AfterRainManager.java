package com.cocojenna.endgame;

import com.cocojenna.blackmud.BlackMudSavedData;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

/** 初晴後世界狀態（《雨後的絨尾之鄉》基礎框架）. */
public final class AfterRainManager {

    private AfterRainManager() {}

    public static void onFirstDawn(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.triggerEndgame();

        ServerLevel level = player.serverLevel();
        if (level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            BlackMudSavedData data = BlackMudSavedData.get(level);
            data.setAfterRain(true);
            data.clearAllCorruption();
            data.setBlindWaterRainUntil(0);
            CatKingdomWorldData worldData = CatKingdomWorldData.get(level);
            worldData.registerFirstDawnCandidate(player);
            worldData.setAfterRain(true);
            com.cocojenna.kingdom.multiplayer.KingdomAuthorityManager.onFirstDawn(player);
        }

        for (ServerPlayer online : player.server.getPlayerList().getPlayers()) {
            BondData b = ModCapabilities.getOrDefault(online);
            if (!b.isEndgameUnlocked()) {
                b.triggerEndgame();
            }
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> online),
                    new SyncBondDataPacket(b.serializeNBT()));
            online.displayClientMessage(Component.translatable("endgame.cocojenna.after_rain_begin"), true);
        }
    }

    public static void setShadowClawEnding(ServerPlayer player, String ending) {
        if (player == null) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.setShadowClawEnding(ending);
        player.displayClientMessage(Component.translatable(
                "endgame.cocojenna.shadow_claw." + ending), true);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }

    public static boolean isAfterRain(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return false;
        CatKingdomWorldData world = CatKingdomWorldData.getIfPresent(level);
        if (world != null && world.isAfterRain()) return true;
        return BlackMudSavedData.get(level).isAfterRain();
    }
}
