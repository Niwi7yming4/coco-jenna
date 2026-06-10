package com.cocojenna.network;

import com.cocojenna.client.web.WebUiRouter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenCatnipPlantPacket(long seed) {

    public static void encode(OpenCatnipPlantPacket msg, FriendlyByteBuf buf) {
        buf.writeLong(msg.seed);
    }

    public static OpenCatnipPlantPacket decode(FriendlyByteBuf buf) {
        return new OpenCatnipPlantPacket(buf.readLong());
    }

    public static void handle(OpenCatnipPlantPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> WebUiRouter.openCatnipPlant(msg.seed()));
        ctx.get().setPacketHandled(true);
    }
}
