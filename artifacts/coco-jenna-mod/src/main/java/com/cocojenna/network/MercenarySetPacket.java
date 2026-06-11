package com.cocojenna.network;

import com.cocojenna.kingdom.multiplayer.MercenaryManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record MercenarySetPacket(int price) {

    public static void encode(MercenarySetPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.price);
    }

    public static MercenarySetPacket decode(FriendlyByteBuf buf) {
        return new MercenarySetPacket(buf.readVarInt());
    }

    public static void handle(MercenarySetPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> MercenaryManager.setProfile(player, msg.price()));
        ctx.get().setPacketHandled(true);
    }
}
