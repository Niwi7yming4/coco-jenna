package com.cocojenna.network;

import com.cocojenna.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 伺服器 → 客戶端：印記／儀式狀態輕量更新. */
public class MarkUpdatePacket {

    private final int markLevel;
    private final String markForce;
    private final int ceremonyStage;
    private final int felineTier;

    public MarkUpdatePacket(int markLevel, String markForce, int ceremonyStage, int felineTier) {
        this.markLevel = markLevel;
        this.markForce = markForce == null ? "" : markForce;
        this.ceremonyStage = ceremonyStage;
        this.felineTier = felineTier;
    }

    public static void encode(MarkUpdatePacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.markLevel);
        buf.writeUtf(pkt.markForce);
        buf.writeVarInt(pkt.ceremonyStage);
        buf.writeVarInt(pkt.felineTier);
    }

    public static MarkUpdatePacket decode(FriendlyByteBuf buf) {
        return new MarkUpdatePacket(buf.readVarInt(), buf.readUtf(), buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(MarkUpdatePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (!PacketAuthGuard.requireClientBound(ctx, "MarkUpdatePacket")) return;
        ctx.get().enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player == null) return;
            ModCapabilities.get(player).ifPresent(bond -> {
                bond.setMarkLevel(pkt.markLevel);
                bond.setMarkForce(pkt.markForce);
                bond.setCeremonyStage(pkt.ceremonyStage);
                bond.setFelineTier(pkt.felineTier);
            });
            com.cocojenna.client.renderer.PromotionMarkRenderer.INSTANCE
                    .setPermanentMark(player, pkt.markForce, pkt.markLevel);
        });
        ctx.get().setPacketHandled(true);
    }
}
