package com.cocojenna.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record GlobalEventStartPacket(boolean purge, int target) {

    public static void encode(GlobalEventStartPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.purge);
        buf.writeVarInt(msg.target);
    }

    public static GlobalEventStartPacket decode(FriendlyByteBuf buf) {
        return new GlobalEventStartPacket(buf.readBoolean(), buf.readVarInt());
    }

    public static void handle(GlobalEventStartPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
