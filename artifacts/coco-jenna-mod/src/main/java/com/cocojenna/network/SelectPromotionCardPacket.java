package com.cocojenna.network;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.sequence.SequencePromotionHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 客戶端 → 伺服器：選擇晉升卡牌. */
public class SelectPromotionCardPacket {

    private final int cardIndex;

    public SelectPromotionCardPacket(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public static void encode(SelectPromotionCardPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.cardIndex);
    }

    public static SelectPromotionCardPacket decode(FriendlyByteBuf buf) {
        return new SelectPromotionCardPacket(buf.readVarInt());
    }

    public static void handle(SelectPromotionCardPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            SequencePromotionHelper.confirmPromotion(
                    player, ModCapabilities.getOrDefault(player), pkt.cardIndex);
        });
        ctx.get().setPacketHandled(true);
    }
}
