package com.cocojenna.network;

import com.cocojenna.sequence.RitualCoopManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/** C→S 儀式注入祝福 */
public record RitualAidPacket(UUID promoterId, int aidType) {

    public static void encode(RitualAidPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.promoterId);
        buf.writeVarInt(msg.aidType);
    }

    public static RitualAidPacket decode(FriendlyByteBuf buf) {
        return new RitualAidPacket(buf.readUUID(), buf.readVarInt());
    }

    public static void handle(RitualAidPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> {
            if (msg.aidType() == 0) RitualCoopManager.injectBlessing(player, msg.promoterId());
        });
        ctx.get().setPacketHandled(true);
    }
}
