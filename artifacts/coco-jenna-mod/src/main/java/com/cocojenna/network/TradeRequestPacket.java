package com.cocojenna.network;

import com.cocojenna.trade.PlayerTradeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record TradeRequestPacket(UUID fromId, String fromName) {

    public static void encode(TradeRequestPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.fromId);
        buf.writeUtf(msg.fromName);
    }

    public static TradeRequestPacket decode(FriendlyByteBuf buf) {
        return new TradeRequestPacket(buf.readUUID(), buf.readUtf());
    }

    public static void handle(TradeRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
