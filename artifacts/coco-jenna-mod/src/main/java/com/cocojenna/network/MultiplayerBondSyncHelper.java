package com.cocojenna.network;

import com.cocojenna.blackmud.BlackMudSavedData;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/** 多人伺服器：登入時同步黑泥 chunk 與 per-player bond 快照. */
public final class MultiplayerBondSyncHelper {

    private MultiplayerBondSyncHelper() {}

    public static void onPlayerLogin(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        BondSyncCoordinator.syncFull(player, bond);

        if (player.server.isDedicatedServer() && player.server.getPlayerCount() > 1) {
            for (ServerLevel level : player.server.getAllLevels()) {
                if (level.dimension().equals(Level.OVERWORLD)
                        || level.dimension().equals(com.cocojenna.init.ModDimensions.CAT_KINGDOM)) {
                    BlackMudSavedData.get(level).setDirty();
                }
            }
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("multiplayer.cocojenna.bond_synced"),
                    true);
        }
    }

    public static void tick(ServerPlayer player) {
        if (!player.server.isDedicatedServer()) return;
        if (player.level().getGameTime() % 1200 != 0) return;
        BondSyncCoordinator.syncFull(player, ModCapabilities.getOrDefault(player));
    }
}
