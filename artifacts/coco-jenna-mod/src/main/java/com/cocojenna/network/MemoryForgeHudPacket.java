package com.cocojenna.network;

import com.cocojenna.client.gui.MemoryForgeHudOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MemoryForgeHudPacket {

    private final int phase;
    private final int ticksRemaining;
    private final float altarHpRatio;

    public MemoryForgeHudPacket(int phase, int ticksRemaining, float altarHpRatio) {
        this.phase = phase;
        this.ticksRemaining = ticksRemaining;
        this.altarHpRatio = altarHpRatio;
    }

    public static void encode(MemoryForgeHudPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.phase);
        buf.writeVarInt(pkt.ticksRemaining);
        buf.writeFloat(pkt.altarHpRatio);
    }

    public static MemoryForgeHudPacket decode(FriendlyByteBuf buf) {
        return new MemoryForgeHudPacket(buf.readVarInt(), buf.readVarInt(), buf.readFloat());
    }

    public static void handle(MemoryForgeHudPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (!PacketAuthGuard.requireClientBound(ctx, "MemoryForgeHudPacket")) return;
        ctx.get().enqueueWork(() -> {
            if (pkt.phase < 0) {
                MemoryForgeHudOverlay.INSTANCE.clear();
            } else {
                MemoryForgeHudOverlay.INSTANCE.update(pkt.phase, pkt.ticksRemaining, pkt.altarHpRatio);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
