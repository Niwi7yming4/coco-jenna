package com.cocojenna.network;

import com.cocojenna.combat.DistillCombatManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 客戶端 R 鍵 — 蒸餾打擊準備. */
public record DistillStrikePacket() {

    public static void encode(DistillStrikePacket msg, FriendlyByteBuf buf) {}

    public static DistillStrikePacket decode(FriendlyByteBuf buf) {
        return new DistillStrikePacket();
    }

    public static void handle(DistillStrikePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> DistillCombatManager.armDistillStrike(player));
        ctx.get().setPacketHandled(true);
    }
}
