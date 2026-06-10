package com.cocojenna.network;

import com.cocojenna.client.web.WebUiRouter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenRiverVoyagePacket(long seed) {

    public static void encode(OpenRiverVoyagePacket msg, FriendlyByteBuf buf) {
        buf.writeLong(msg.seed);
    }

    public static OpenRiverVoyagePacket decode(FriendlyByteBuf buf) {
        return new OpenRiverVoyagePacket(buf.readLong());
    }

    public static void handle(OpenRiverVoyagePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> WebUiRouter.openRiverVoyage(msg.seed()));
        ctx.get().setPacketHandled(true);
    }
}
