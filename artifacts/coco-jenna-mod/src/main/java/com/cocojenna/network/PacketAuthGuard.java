package com.cocojenna.network;

import com.cocojenna.CocoJennaMod;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 伺服器權威封包驗證 — 拒絕客戶端濫發特效封包. */
public final class PacketAuthGuard {

    private PacketAuthGuard() {}

    public static boolean requireClientBound(Supplier<NetworkEvent.Context> ctx, String packetName) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            return true;
        }
        ServerPlayer sender = ctx.get().getSender();
        if (sender != null) {
            CocoJennaMod.LOGGER.warn("Rejected unauthorized packet {} from {}", packetName, sender.getName().getString());
            sender.connection.disconnect(Component.translatable("disconnect.cocojenna.unauthorized_packet", packetName));
        }
        ctx.get().setPacketHandled(true);
        return false;
    }
}
