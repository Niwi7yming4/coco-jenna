package com.cocojenna.network;

import com.cocojenna.endgame.CookingManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CookFoodPacket(int recipeIndex) {

    public static void encode(CookFoodPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.recipeIndex);
    }

    public static CookFoodPacket decode(FriendlyByteBuf buf) {
        return new CookFoodPacket(buf.readVarInt());
    }

    public static void handle(CookFoodPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> CookingManager.cook(player, msg.recipeIndex()));
        ctx.get().setPacketHandled(true);
    }
}
