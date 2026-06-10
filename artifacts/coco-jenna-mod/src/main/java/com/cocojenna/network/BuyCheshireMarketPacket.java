package com.cocojenna.network;

import com.cocojenna.shop.CheshireBlackMarketOffers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BuyCheshireMarketPacket {

    private final int offerIndex;

    public BuyCheshireMarketPacket(int offerIndex) {
        this.offerIndex = offerIndex;
    }

    public static void encode(BuyCheshireMarketPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.offerIndex);
    }

    public static BuyCheshireMarketPacket decode(FriendlyByteBuf buf) {
        return new BuyCheshireMarketPacket(buf.readVarInt());
    }

    public static void handle(BuyCheshireMarketPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            CheshireBlackMarketOffers.tryPurchase(player, pkt.offerIndex);
        });
        ctx.get().setPacketHandled(true);
    }
}
