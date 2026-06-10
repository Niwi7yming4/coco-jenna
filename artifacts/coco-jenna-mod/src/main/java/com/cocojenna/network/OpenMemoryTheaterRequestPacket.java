package com.cocojenna.network;

import com.cocojenna.endgame.MemoryTheaterManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 客戶端 → 伺服器：請求開啟記憶劇場選單. */
public record OpenMemoryTheaterRequestPacket() {

    public static void encode(OpenMemoryTheaterRequestPacket msg, FriendlyByteBuf buf) {}

    public static OpenMemoryTheaterRequestPacket decode(FriendlyByteBuf buf) {
        return new OpenMemoryTheaterRequestPacket();
    }

    public static void handle(OpenMemoryTheaterRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                MemoryTheaterManager.open(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
