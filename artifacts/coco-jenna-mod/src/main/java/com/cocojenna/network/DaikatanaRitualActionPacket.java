package com.cocojenna.network;

import com.cocojenna.memforge.DaikatanaRitualManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client → server: ritual panel actions. */
public record DaikatanaRitualActionPacket(Action action, BlockPos altarPos, int recipeOrd) {

    public enum Action { START, QUENCH, CLOSE }

    public static void encode(DaikatanaRitualActionPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.action);
        buf.writeBlockPos(msg.altarPos);
        buf.writeVarInt(msg.recipeOrd);
    }

    public static DaikatanaRitualActionPacket decode(FriendlyByteBuf buf) {
        return new DaikatanaRitualActionPacket(buf.readEnum(Action.class), buf.readBlockPos(), buf.readVarInt());
    }

    public static void handle(DaikatanaRitualActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            switch (msg.action) {
                case START -> DaikatanaRitualManager.tryStartByOrdinal(player, msg.altarPos, msg.recipeOrd);
                case QUENCH -> DaikatanaRitualManager.tryQuenchFromGui(player, msg.altarPos);
                case CLOSE -> { }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
