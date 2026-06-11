package com.cocojenna.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** S→C 同步個人好感度 */
public record PersonalAffectionSyncPacket(float coco, float jenna) {

    public static void encode(PersonalAffectionSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.coco);
        buf.writeFloat(msg.jenna);
    }

    public static PersonalAffectionSyncPacket decode(FriendlyByteBuf buf) {
        return new PersonalAffectionSyncPacket(buf.readFloat(), buf.readFloat());
    }

    public static void handle(PersonalAffectionSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
