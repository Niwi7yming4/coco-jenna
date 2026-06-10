package com.cocojenna.network;

import com.cocojenna.shop.RyokatanaShopOffers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BuyRyokatanaPacket {

    private final int offerIndex;

    public BuyRyokatanaPacket(int offerIndex) {
        this.offerIndex = offerIndex;
    }

    public static void encode(BuyRyokatanaPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.offerIndex);
    }

    public static BuyRyokatanaPacket decode(FriendlyByteBuf buf) {
        return new BuyRyokatanaPacket(buf.readVarInt());
    }

    public static void handle(BuyRyokatanaPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            RyokatanaShopOffers.tryPurchase(player, pkt.offerIndex);
        });
        ctx.get().setPacketHandled(true);
    }
}
