package com.cocojenna.network;

import com.cocojenna.abyss.AbyssRunManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record AbyssRunActionPacket(Action action, int index) {

    public enum Action { START, PLAY_CARD, END_TURN, PICK_REWARD, SHOP_BUY, LEAVE }

    public static void encode(AbyssRunActionPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.action);
        buf.writeVarInt(msg.index);
    }

    public static AbyssRunActionPacket decode(FriendlyByteBuf buf) {
        return new AbyssRunActionPacket(buf.readEnum(Action.class), buf.readVarInt());
    }

    public static void handle(AbyssRunActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> {
            switch (msg.action) {
                case START -> AbyssRunManager.startRun(player);
                case PLAY_CARD -> AbyssRunManager.playCard(player, msg.index);
                case END_TURN -> AbyssRunManager.endTurn(player);
                case PICK_REWARD -> AbyssRunManager.pickReward(player, msg.index);
                case SHOP_BUY -> AbyssRunManager.shopAction(player, msg.index);
                case LEAVE -> AbyssRunManager.abandon(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
