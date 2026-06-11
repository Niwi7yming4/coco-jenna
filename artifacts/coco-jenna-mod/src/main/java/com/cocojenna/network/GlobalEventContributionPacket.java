package com.cocojenna.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record GlobalEventContributionPacket(int total) {

    public static void encode(GlobalEventContributionPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.total);
    }

    public static GlobalEventContributionPacket decode(FriendlyByteBuf buf) {
        return new GlobalEventContributionPacket(buf.readVarInt());
    }

    public static void handle(GlobalEventContributionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
