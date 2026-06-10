package com.cocojenna.network;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.sequence.MoonCrossroadsManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 客戶端 → 伺服器：確認源力選擇. */
public record ConfirmForceSelectionPacket(String force) {

    public static void encode(ConfirmForceSelectionPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.force);
    }

    public static ConfirmForceSelectionPacket decode(FriendlyByteBuf buf) {
        return new ConfirmForceSelectionPacket(buf.readUtf(16));
    }

    public static void handle(ConfirmForceSelectionPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!MoonCrossroadsManager.confirmForce(player, pkt.force)) return;
            var bond = ModCapabilities.getOrDefault(player);
            ModNetwork.CHANNEL.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                    new SyncBondDataPacket(bond.serializeNBT()));
        });
        ctx.get().setPacketHandled(true);
    }
}
