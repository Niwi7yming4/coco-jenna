package com.cocojenna.network;

import com.cocojenna.kingdom.multiplayer.KingdomAuthorityManager;
import com.cocojenna.kingdom.multiplayer.KingdomAuthoritySavedData;
import com.cocojenna.kingdom.multiplayer.KingdomRole;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/** C→S 任命/撤銷職位 */
public record RoleAssignPacket(UUID target, String roleName, boolean revoke) {

    public static void encode(RoleAssignPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.target);
        buf.writeUtf(msg.roleName);
        buf.writeBoolean(msg.revoke);
    }

    public static RoleAssignPacket decode(FriendlyByteBuf buf) {
        return new RoleAssignPacket(buf.readUUID(), buf.readUtf(), buf.readBoolean());
    }

    public static void handle(RoleAssignPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> {
            KingdomAuthoritySavedData auth = KingdomAuthoritySavedData.get(player.serverLevel());
            if (msg.revoke()) {
                auth.assignRole(player.getUUID(), msg.target(), KingdomRole.CITIZEN);
            } else {
                auth.assignRole(player.getUUID(), msg.target(), KingdomRole.valueOf(msg.roleName()));
            }
            KingdomAuthorityManager.syncToAll(player.serverLevel());
        });
        ctx.get().setPacketHandled(true);
    }
}
