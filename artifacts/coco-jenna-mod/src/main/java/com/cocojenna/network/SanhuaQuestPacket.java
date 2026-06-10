package com.cocojenna.network;

import com.cocojenna.quest.SanhuaEternalCloakManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 客戶端 → 伺服器：三花永恆披風材料交付. */
public record SanhuaQuestPacket() {

    public static void encode(SanhuaQuestPacket msg, FriendlyByteBuf buf) {}

    public static SanhuaQuestPacket decode(FriendlyByteBuf buf) {
        return new SanhuaQuestPacket();
    }

    public static void handle(SanhuaQuestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                SanhuaEternalCloakManager.tryTurnInMaterials(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
