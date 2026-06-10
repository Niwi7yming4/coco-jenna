package com.cocojenna.network;

import com.cocojenna.gamble.BlackjackGambleMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BlackjackGamblePacket {

    private final int actionOrdinal;

    public BlackjackGamblePacket(BlackjackGambleMenu.BlackjackGambleAction action) {
        this.actionOrdinal = action.ordinal();
    }

    public static void encode(BlackjackGamblePacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.actionOrdinal);
    }

    public static BlackjackGamblePacket decode(FriendlyByteBuf buf) {
        int ord = buf.readVarInt();
        var values = BlackjackGambleMenu.BlackjackGambleAction.values();
        var action = ord >= 0 && ord < values.length ? values[ord] : BlackjackGambleMenu.BlackjackGambleAction.START;
        return new BlackjackGamblePacket(action);
    }

    public static void handle(BlackjackGamblePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            AbstractContainerMenu menu = player.containerMenu;
            if (menu instanceof BlackjackGambleMenu gamble) {
                var values = BlackjackGambleMenu.BlackjackGambleAction.values();
                if (pkt.actionOrdinal >= 0 && pkt.actionOrdinal < values.length) {
                    gamble.action(values[pkt.actionOrdinal]);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
