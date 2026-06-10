package com.cocojenna.network;

import com.cocojenna.economy.CatnipQuality;
import com.cocojenna.shop.CatnipMarketMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SellCatnipPacket {

    private final int qualityOrdinal;
    private final boolean wholeStack;

    public SellCatnipPacket(int qualityOrdinal, boolean wholeStack) {
        this.qualityOrdinal = qualityOrdinal;
        this.wholeStack = wholeStack;
    }

    public static void encode(SellCatnipPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.qualityOrdinal);
        buf.writeBoolean(pkt.wholeStack);
    }

    public static SellCatnipPacket decode(FriendlyByteBuf buf) {
        return new SellCatnipPacket(buf.readVarInt(), buf.readBoolean());
    }

    public static void handle(SellCatnipPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (player.containerMenu instanceof CatnipMarketMenu menu) {
                CatnipQuality q = CatnipQuality.fromOrdinal(pkt.qualityOrdinal);
                if (pkt.wholeStack) menu.sellStack(player, q);
                else menu.sellOne(player, q);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
