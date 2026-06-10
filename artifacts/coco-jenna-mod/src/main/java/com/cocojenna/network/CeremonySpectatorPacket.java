package com.cocojenna.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 伺服器 → 旁觀者：儀式光效（粒子／聲波），不含 UI.
 */
public class CeremonySpectatorPacket {

    public enum SpectatorEffect { ALTAR_GLOW, RESONANCE_WAVE }

    private final SpectatorEffect effect;
    private final double x;
    private final double y;
    private final double z;
    private final String force;

    public CeremonySpectatorPacket(SpectatorEffect effect, double x, double y, double z, String force) {
        this.effect = effect;
        this.x = x;
        this.y = y;
        this.z = z;
        this.force = force == null ? "" : force;
    }

    public static void encode(CeremonySpectatorPacket pkt, FriendlyByteBuf buf) {
        buf.writeEnum(pkt.effect);
        buf.writeDouble(pkt.x);
        buf.writeDouble(pkt.y);
        buf.writeDouble(pkt.z);
        buf.writeUtf(pkt.force);
    }

    public static CeremonySpectatorPacket decode(FriendlyByteBuf buf) {
        return new CeremonySpectatorPacket(
                buf.readEnum(SpectatorEffect.class),
                buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readUtf());
    }

    public static void handle(CeremonySpectatorPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (!PacketAuthGuard.requireClientBound(ctx, "CeremonySpectatorPacket")) return;
        ctx.get().enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            if (level == null) return;
            switch (pkt.effect) {
                case ALTAR_GLOW -> level.addParticle(ParticleTypes.END_ROD,
                        pkt.x, pkt.y + 1.2, pkt.z, 0, 0.04, 0);
                case RESONANCE_WAVE -> {
                    for (int i = 0; i < 12; i++) {
                        double angle = i * Math.PI / 6;
                        level.addParticle(ParticleTypes.WITCH,
                                pkt.x + Math.cos(angle), pkt.y + 0.2, pkt.z + Math.sin(angle),
                                0, 0.02, 0);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
