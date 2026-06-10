package com.cocojenna.network;

import com.cocojenna.client.gui.CatKitchenScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenCatKitchenPacket() {

    public static void encode(OpenCatKitchenPacket msg, FriendlyByteBuf buf) {}

    public static OpenCatKitchenPacket decode(FriendlyByteBuf buf) {
        return new OpenCatKitchenPacket();
    }

    public static void handle(OpenCatKitchenPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(CatKitchenScreen::open);
        ctx.get().setPacketHandled(true);
    }
}
