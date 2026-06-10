package com.cocojenna.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** 伺服器 → 客戶端：開啟晉升三選一介面. */
public class OpenPromotionPacket {

    private final int fromTier;
    private final String force;
    private final List<String> cards;

    public OpenPromotionPacket(int fromTier, String force, List<String> cards) {
        this.fromTier = fromTier;
        this.force = force;
        this.cards = cards;
    }

    public static void encode(OpenPromotionPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.fromTier);
        buf.writeUtf(pkt.force);
        buf.writeVarInt(pkt.cards.size());
        for (String c : pkt.cards) buf.writeUtf(c);
    }

    public static OpenPromotionPacket decode(FriendlyByteBuf buf) {
        int tier = buf.readVarInt();
        String force = buf.readUtf();
        int n = buf.readVarInt();
        List<String> cards = new ArrayList<>();
        for (int i = 0; i < n; i++) cards.add(buf.readUtf());
        return new OpenPromotionPacket(tier, force, cards);
    }

    public static void handle(OpenPromotionPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                com.cocojenna.client.gui.SequencePromotionScreen.open(
                        pkt.fromTier, pkt.force, pkt.cards);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
