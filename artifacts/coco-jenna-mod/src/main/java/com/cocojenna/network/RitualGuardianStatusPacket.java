package com.cocojenna.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/** S→C 守護/祝福進度 */
public record RitualGuardianStatusPacket(UUID promoterId, int blessCount, int resonanceSlots) {

    public static void encode(RitualGuardianStatusPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.promoterId);
        buf.writeVarInt(msg.blessCount);
        buf.writeVarInt(msg.resonanceSlots);
    }

    public static RitualGuardianStatusPacket decode(FriendlyByteBuf buf) {
        return new RitualGuardianStatusPacket(buf.readUUID(), buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(RitualGuardianStatusPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
