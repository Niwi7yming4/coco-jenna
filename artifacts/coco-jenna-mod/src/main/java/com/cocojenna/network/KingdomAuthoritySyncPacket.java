package com.cocojenna.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** S→C 同步王國職位與投票 */
public record KingdomAuthoritySyncPacket(CompoundTag data) {

    public static void encode(KingdomAuthoritySyncPacket msg, FriendlyByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static KingdomAuthoritySyncPacket decode(FriendlyByteBuf buf) {
        return new KingdomAuthoritySyncPacket(buf.readNbt());
    }

    public static void handle(KingdomAuthoritySyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                ClientKingdomAuthorityCache.apply(msg.data);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
