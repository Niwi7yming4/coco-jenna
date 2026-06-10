package com.cocojenna.network;

import com.cocojenna.endgame.MemoryTheaterManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 客戶端 → 伺服器：重播指定場景. */
public record MemoryTheaterReplayPacket(String sceneId) {

    public static void encode(MemoryTheaterReplayPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.sceneId);
    }

    public static MemoryTheaterReplayPacket decode(FriendlyByteBuf buf) {
        return new MemoryTheaterReplayPacket(buf.readUtf(128));
    }

    public static void handle(MemoryTheaterReplayPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                MemoryTheaterManager.replay(player, msg.sceneId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
