package com.cocojenna.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 伺服器 → 客戶端：打開記憶之書 GUI */
public class OpenMemoryBookPacket {

    public OpenMemoryBookPacket() {}

    public static void encode(OpenMemoryBookPacket packet, FriendlyByteBuf buf) {}

    public static OpenMemoryBookPacket decode(FriendlyByteBuf buf) {
        return new OpenMemoryBookPacket();
    }

    public static void handle(OpenMemoryBookPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                com.cocojenna.client.gui.MemoryBookScreen.open(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
