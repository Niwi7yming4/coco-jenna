package com.cocojenna.network;

import com.cocojenna.player.PlayerActionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 客戶端 → 伺服器：V 鍵互動圓盤動作. */
public record CatRadialActionPacket(int action) {

    public static final int PET = 0;
    public static final int FEED = 1;
    public static final int GROOM = 2;
    public static final int PLAY = 3;
    public static final int FOLLOW = 4;

    public static void encode(CatRadialActionPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.action);
    }

    public static CatRadialActionPacket decode(FriendlyByteBuf buf) {
        return new CatRadialActionPacket(buf.readVarInt());
    }

    public static void handle(CatRadialActionPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (pkt.action < PET || pkt.action > FOLLOW) return;
            PlayerActionHandler.performRadialAction(player, pkt.action);
        });
        ctx.get().setPacketHandled(true);
    }
}
