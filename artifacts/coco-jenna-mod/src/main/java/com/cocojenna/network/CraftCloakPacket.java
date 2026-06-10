package com.cocojenna.network;

import com.cocojenna.cloak.CloakOrderSavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CraftCloakPacket {

    private final int cloakIndex;

    public CraftCloakPacket(int cloakIndex) {
        this.cloakIndex = cloakIndex;
    }

    public static void encode(CraftCloakPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.cloakIndex);
    }

    public static CraftCloakPacket decode(FriendlyByteBuf buf) {
        return new CraftCloakPacket(buf.readVarInt());
    }

    public static void handle(CraftCloakPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (pkt.cloakIndex < 0) {
                CloakOrderSavedData.deliverPending(player);
            } else {
                CloakOrderSavedData.placeOrder(player, pkt.cloakIndex);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
