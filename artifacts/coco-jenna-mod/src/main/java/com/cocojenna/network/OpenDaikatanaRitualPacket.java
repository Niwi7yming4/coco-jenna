package com.cocojenna.network;

import com.cocojenna.client.gui.DaikatanaRitualScreen;
import com.cocojenna.memforge.DaikatanaRitual;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server → client: open or refresh daikatana ritual panel. */
public record OpenDaikatanaRitualPacket(
        BlockPos altarPos,
        int phaseOrd,
        int recipeOrd,
        long phaseEndTick,
        long levelTime) {

    public static void encode(OpenDaikatanaRitualPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.altarPos);
        buf.writeVarInt(msg.phaseOrd);
        buf.writeVarInt(msg.recipeOrd);
        buf.writeLong(msg.phaseEndTick);
        buf.writeLong(msg.levelTime);
    }

    public static OpenDaikatanaRitualPacket decode(FriendlyByteBuf buf) {
        return new OpenDaikatanaRitualPacket(
                buf.readBlockPos(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readLong(),
                buf.readLong());
    }

    public static void handle(OpenDaikatanaRitualPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DaikatanaRitualScreen.openOrUpdate(msg));
        ctx.get().setPacketHandled(true);
    }

    public float progress() {
        if (phaseOrd < 0 || recipeOrd < 0) return 0f;
        DaikatanaRitual.Phase[] phases = DaikatanaRitual.Phase.values();
        DaikatanaRitual.Phase phase = phases[Math.max(0, Math.min(phaseOrd, phases.length - 1))];
        int total = phase == DaikatanaRitual.Phase.FORGING
                ? com.cocojenna.memforge.DaikatanaRitualManager.FORGE_TICKS
                : com.cocojenna.memforge.DaikatanaRitualManager.QUENCH_TICKS;
        long start = phaseEndTick - total;
        if (levelTime <= start) return 0f;
        return Math.min(1f, (levelTime - start) / (float) total);
    }
}
