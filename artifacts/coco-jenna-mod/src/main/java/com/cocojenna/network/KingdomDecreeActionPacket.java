package com.cocojenna.network;

import com.cocojenna.endgame.KingdomDecreeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record KingdomDecreeActionPacket(Action action, String decreeId, int slot) {

    public enum Action { ENACT, REVOKE }

    public static void encode(KingdomDecreeActionPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.action);
        buf.writeUtf(msg.decreeId);
        buf.writeVarInt(msg.slot);
    }

    public static KingdomDecreeActionPacket decode(FriendlyByteBuf buf) {
        return new KingdomDecreeActionPacket(buf.readEnum(Action.class), buf.readUtf(), buf.readVarInt());
    }

    public static void handle(KingdomDecreeActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> {
            switch (msg.action) {
                case ENACT -> KingdomDecreeManager.enact(player, msg.decreeId);
                case REVOKE -> KingdomDecreeManager.revoke(player, msg.slot);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
