package com.cocojenna.network;

import com.cocojenna.web.WebUiActionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client → server: player action from HTML/JS UI. */
public record WebUiActionPacket(String page, String sessionId, String json) {

    public static void encode(WebUiActionPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.page, 64);
        buf.writeUtf(msg.sessionId, 64);
        LargePayload.writeUtf8(buf, msg.json);
    }

    public static WebUiActionPacket decode(FriendlyByteBuf buf) {
        return new WebUiActionPacket(buf.readUtf(64), buf.readUtf(64), LargePayload.readUtf8(buf));
    }

    public static void handle(WebUiActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                WebUiActionHandler.handle(player, msg.page(), msg.sessionId(), msg.json());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
