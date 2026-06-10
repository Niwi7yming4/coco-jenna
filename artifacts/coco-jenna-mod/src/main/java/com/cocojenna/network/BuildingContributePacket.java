package com.cocojenna.network;

import com.cocojenna.endgame.BuildingManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BuildingContributePacket(String buildingId) {

    public static void encode(BuildingContributePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.buildingId);
    }

    public static BuildingContributePacket decode(FriendlyByteBuf buf) {
        return new BuildingContributePacket(buf.readUtf());
    }

    public static void handle(BuildingContributePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> BuildingManager.contribute(player, msg.buildingId()));
        ctx.get().setPacketHandled(true);
    }
}
