package com.cocojenna.network;

import com.cocojenna.kingdom.multiplayer.MercenaryManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record MercenarySummonPacket(UUID ownerId) {

    public static void encode(MercenarySummonPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.ownerId);
    }

    public static MercenarySummonPacket decode(FriendlyByteBuf buf) {
        return new MercenarySummonPacket(buf.readUUID());
    }

    public static void handle(MercenarySummonPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> MercenaryManager.summon(player, msg.ownerId()));
        ctx.get().setPacketHandled(true);
    }
}
