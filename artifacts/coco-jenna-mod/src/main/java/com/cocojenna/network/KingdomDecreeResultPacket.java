package com.cocojenna.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** S→C 投票結果廣播 */
public record KingdomDecreeResultPacket(String voteId, String proposal, boolean passed, String reason) {

    public static void encode(KingdomDecreeResultPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.voteId);
        buf.writeUtf(msg.proposal);
        buf.writeBoolean(msg.passed);
        buf.writeUtf(msg.reason);
    }

    public static KingdomDecreeResultPacket decode(FriendlyByteBuf buf) {
        return new KingdomDecreeResultPacket(buf.readUtf(), buf.readUtf(), buf.readBoolean(), buf.readUtf());
    }

    public static void handle(KingdomDecreeResultPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                net.minecraft.client.Minecraft.getInstance().player.displayClientMessage(
                        Component.translatable(msg.passed
                                ? "kingdom.cocojenna.decree_passed"
                                : "kingdom.cocojenna.decree_failed", msg.proposal), true);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
