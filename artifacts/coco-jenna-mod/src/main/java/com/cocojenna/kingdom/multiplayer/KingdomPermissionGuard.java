package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.init.ModDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/** 王國權限閘道 — 統一 check(player, Permission) */
public final class KingdomPermissionGuard {

    private KingdomPermissionGuard() {}

    public static boolean check(ServerPlayer player, Permission permission) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return true;
        ServerLevel level = player.serverLevel();
        KingdomAuthoritySavedData auth = KingdomAuthoritySavedData.get(level);
        if (player.server.getPlayerCount() <= 1) {
            auth.ensureSoloMonarch(player);
            return true;
        }
        auth.touchOnline(player);
        KingdomRole role = auth.getRole(player.getUUID());
        if (role == KingdomRole.MONARCH) return true;
        return role.defaultPermissions().contains(permission);
    }

    public static boolean require(ServerPlayer player, Permission permission) {
        if (check(player, permission)) return true;
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.no_permission",
                permission.name()), true);
        return false;
    }
}
