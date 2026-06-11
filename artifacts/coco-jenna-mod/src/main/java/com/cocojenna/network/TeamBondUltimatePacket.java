package com.cocojenna.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/** S→C 團隊共鳴特效 */
public record TeamBondUltimatePacket(UUID leaderId, double x, double y, double z) {

    public static void encode(TeamBondUltimatePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.leaderId);
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
    }

    public static TeamBondUltimatePacket decode(FriendlyByteBuf buf) {
        return new TeamBondUltimatePacket(buf.readUUID(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void handle(TeamBondUltimatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
