package com.cocojenna.network;

import com.cocojenna.endgame.kingdom.CatLibraryManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 圖書館書架：0=上架繪本頁 1=閱讀書架格. */
public record LibraryShelfPacket(int action, int index) {

    public static void encode(LibraryShelfPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.action);
        buf.writeVarInt(msg.index);
    }

    public static LibraryShelfPacket decode(FriendlyByteBuf buf) {
        return new LibraryShelfPacket(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(LibraryShelfPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> {
            if (msg.action == 0) CatLibraryManager.shelvePage(player, msg.index);
            else CatLibraryManager.readShelf(player, msg.index);
        });
        ctx.get().setPacketHandled(true);
    }
}
