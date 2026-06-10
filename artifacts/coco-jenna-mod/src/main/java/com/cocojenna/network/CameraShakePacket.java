package com.cocojenna.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 伺服器 → 客戶端：螢幕震動. */
public class CameraShakePacket {

    private final float intensity;
    private final int durationTicks;

    public CameraShakePacket(float intensity, int durationTicks) {
        this.intensity = intensity;
        this.durationTicks = durationTicks;
    }

    public static void encode(CameraShakePacket pkt, FriendlyByteBuf buf) {
        buf.writeFloat(pkt.intensity);
        buf.writeVarInt(pkt.durationTicks);
    }

    public static CameraShakePacket decode(FriendlyByteBuf buf) {
        return new CameraShakePacket(buf.readFloat(), buf.readVarInt());
    }

    public static void handle(CameraShakePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (!PacketAuthGuard.requireClientBound(ctx, "CameraShakePacket")) return;
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null && pkt.durationTicks > 0) {
                // 震動由 ScreenEffectOverlay 擴充；此處僅確保封包可編譯處理
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
