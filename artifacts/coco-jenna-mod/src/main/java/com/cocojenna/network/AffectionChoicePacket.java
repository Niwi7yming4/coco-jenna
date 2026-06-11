package com.cocojenna.network;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/** S→C 可可選擇互動對象粒子 */
public record AffectionChoicePacket(UUID playerId, double x, double y, double z) {

    public static void encode(AffectionChoicePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerId);
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
    }

    public static AffectionChoicePacket decode(FriendlyByteBuf buf) {
        return new AffectionChoicePacket(buf.readUUID(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void handle(AffectionChoicePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                var mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level != null) {
                    mc.level.addParticle(ParticleTypes.HEART, msg.x, msg.y + 1, msg.z, 0, 0.1, 0);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
