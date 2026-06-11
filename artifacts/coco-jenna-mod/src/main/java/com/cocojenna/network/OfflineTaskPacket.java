package com.cocojenna.network;

import com.cocojenna.kingdom.multiplayer.OfflineTaskManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OfflineTaskPacket(String type, int param, long endTick) {

    public static void encode(OfflineTaskPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.type);
        buf.writeVarInt(msg.param);
        buf.writeLong(msg.endTick);
    }

    public static OfflineTaskPacket decode(FriendlyByteBuf buf) {
        return new OfflineTaskPacket(buf.readUtf(), buf.readVarInt(), buf.readLong());
    }

    public static void handle(OfflineTaskPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> OfflineTaskManager.setTask(player, msg.type(), msg.param(), msg.endTick()));
        ctx.get().setPacketHandled(true);
    }
}
