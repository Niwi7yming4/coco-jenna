package com.cocojenna.network;

import com.cocojenna.guide.AlphaExchangeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 客戶端 → 伺服器：阿爾法兌換. */
public record AlphaExchangePacket(String offerId) {

    public static void encode(AlphaExchangePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.offerId);
    }

    public static AlphaExchangePacket decode(FriendlyByteBuf buf) {
        return new AlphaExchangePacket(buf.readUtf(64));
    }

    public static void handle(AlphaExchangePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                AlphaExchangeManager.purchase(player, msg.offerId());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
