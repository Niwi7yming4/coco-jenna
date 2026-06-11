package com.cocojenna.network;

import com.cocojenna.kingdom.multiplayer.DecreeVoteManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** C→S 提案法令 */
public record DecreeProposalPacket(String proposal) {

    public static void encode(DecreeProposalPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.proposal);
    }

    public static DecreeProposalPacket decode(FriendlyByteBuf buf) {
        return new DecreeProposalPacket(buf.readUtf());
    }

    public static void handle(DecreeProposalPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> DecreeVoteManager.propose(player, msg.proposal()));
        ctx.get().setPacketHandled(true);
    }
}
