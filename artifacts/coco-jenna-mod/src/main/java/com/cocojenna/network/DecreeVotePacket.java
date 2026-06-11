package com.cocojenna.network;

import com.cocojenna.kingdom.multiplayer.DecreeVoteManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** C→S 投票 */
public record DecreeVotePacket(String voteId, boolean yes) {

    public static void encode(DecreeVotePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.voteId);
        buf.writeBoolean(msg.yes);
    }

    public static DecreeVotePacket decode(FriendlyByteBuf buf) {
        return new DecreeVotePacket(buf.readUtf(), buf.readBoolean());
    }

    public static void handle(DecreeVotePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> DecreeVoteManager.castVote(player, msg.voteId(), msg.yes()));
        ctx.get().setPacketHandled(true);
    }
}
