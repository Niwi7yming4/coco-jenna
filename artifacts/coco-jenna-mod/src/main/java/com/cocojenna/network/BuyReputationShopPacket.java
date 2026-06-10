package com.cocojenna.network;

import com.cocojenna.shop.ReputationShopOffers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BuyReputationShopPacket {

    private final int offerIndex;

    public BuyReputationShopPacket(int offerIndex) {
        this.offerIndex = offerIndex;
    }

    public static void encode(BuyReputationShopPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.offerIndex);
    }

    public static BuyReputationShopPacket decode(FriendlyByteBuf buf) {
        return new BuyReputationShopPacket(buf.readVarInt());
    }

    public static void handle(BuyReputationShopPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) ReputationShopOffers.tryPurchase(player, pkt.offerIndex);
        });
        ctx.get().setPacketHandled(true);
    }
}
