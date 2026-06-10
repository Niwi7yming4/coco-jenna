package com.cocojenna.network;

import com.cocojenna.cloak.CloakOrderSavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RepairCloakPacket {

    public RepairCloakPacket() {}

    public static void encode(RepairCloakPacket pkt, FriendlyByteBuf buf) {}

    public static RepairCloakPacket decode(FriendlyByteBuf buf) {
        return new RepairCloakPacket();
    }

    public static void handle(RepairCloakPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                CloakOrderSavedData.repairCloak(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
