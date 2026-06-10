package com.cocojenna.network;

import com.cocojenna.player.PlayerActionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client → server: V/M/N/B/dash 等快捷操作. */
public class PlayerActionPacket {

    public enum Action {
        OPEN_MEMORY_BOOK,
        TOGGLE_FOLLOW,
        RECALL_CATS,
        INTERACT_CAT,
        DASH,
        OPEN_KINGDOM_LEGACY
    }

    private final Action action;

    public PlayerActionPacket(Action action) {
        this.action = action;
    }

    public static void encode(PlayerActionPacket pkt, FriendlyByteBuf buf) {
        buf.writeEnum(pkt.action);
    }

    public static PlayerActionPacket decode(FriendlyByteBuf buf) {
        return new PlayerActionPacket(buf.readEnum(Action.class));
    }

    public static void handle(PlayerActionPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            switch (pkt.action) {
                case OPEN_MEMORY_BOOK -> PlayerActionHandler.openMemoryBook(player);
                case TOGGLE_FOLLOW -> PlayerActionHandler.toggleFollow(player);
                case RECALL_CATS -> PlayerActionHandler.recallCats(player);
                case INTERACT_CAT -> PlayerActionHandler.interactNearestCat(player);
                case DASH -> PlayerActionHandler.dash(player);
                case OPEN_KINGDOM_LEGACY -> com.cocojenna.endgame.KingdomDecreeManager.openLegacyTerminal(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
