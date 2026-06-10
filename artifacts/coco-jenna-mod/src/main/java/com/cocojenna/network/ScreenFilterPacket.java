package com.cocojenna.network;

import com.cocojenna.client.gui.ScreenEffectOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 伺服器 → 客戶端：畫面濾鏡效果. */
public class ScreenFilterPacket {

    public enum FilterType {
        NONE,
        BLACK_MUD,
        FULL_MOON,
        LOW_HEALTH,
        COCO_GUARD,
        AFTER_RAIN,
        ULTIMATE
    }

    private final FilterType filter;
    private final boolean enable;
    private final int duration;

    public ScreenFilterPacket(FilterType filter, boolean enable, int duration) {
        this.filter = filter;
        this.enable = enable;
        this.duration = duration;
    }

    public static void encode(ScreenFilterPacket pkt, FriendlyByteBuf buf) {
        buf.writeEnum(pkt.filter);
        buf.writeBoolean(pkt.enable);
        buf.writeVarInt(pkt.duration);
    }

    public static ScreenFilterPacket decode(FriendlyByteBuf buf) {
        return new ScreenFilterPacket(
                buf.readEnum(FilterType.class),
                buf.readBoolean(),
                buf.readVarInt());
    }

    public static void handle(ScreenFilterPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (!PacketAuthGuard.requireClientBound(ctx, "ScreenFilterPacket")) return;
        ctx.get().enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.player == null) return;
            if (pkt.filter == FilterType.LOW_HEALTH) {
                ScreenEffectOverlay.INSTANCE.setLowHealthWarning(pkt.enable);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
