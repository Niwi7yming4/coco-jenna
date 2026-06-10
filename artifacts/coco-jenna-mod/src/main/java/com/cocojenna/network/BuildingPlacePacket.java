package com.cocojenna.network;

import com.cocojenna.endgame.BuildingManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BuildingPlacePacket(String buildingId) {

    public static void encode(BuildingPlacePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.buildingId);
    }

    public static BuildingPlacePacket decode(FriendlyByteBuf buf) {
        return new BuildingPlacePacket(buf.readUtf());
    }

    public static void handle(BuildingPlacePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> BuildingManager.place(player, msg.buildingId()));
        ctx.get().setPacketHandled(true);
    }
}
