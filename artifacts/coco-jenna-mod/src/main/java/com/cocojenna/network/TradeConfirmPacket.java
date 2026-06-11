package com.cocojenna.network;

import com.cocojenna.trade.PlayerTradeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TradeConfirmPacket(ItemStack offer) {

    public static void encode(TradeConfirmPacket msg, FriendlyByteBuf buf) {
        buf.writeItem(msg.offer);
    }

    public static TradeConfirmPacket decode(FriendlyByteBuf buf) {
        return new TradeConfirmPacket(buf.readItem());
    }

    public static void handle(TradeConfirmPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> PlayerTradeManager.confirm(player, msg.offer()));
        ctx.get().setPacketHandled(true);
    }
}
