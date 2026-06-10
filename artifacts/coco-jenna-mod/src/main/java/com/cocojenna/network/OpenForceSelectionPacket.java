package com.cocojenna.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 伺服器 → 客戶端：開啟源力最終選擇介面. */
public class OpenForceSelectionPacket {

    public OpenForceSelectionPacket() {}

    public static void encode(OpenForceSelectionPacket pkt, FriendlyByteBuf buf) {}

    public static OpenForceSelectionPacket decode(FriendlyByteBuf buf) {
        return new OpenForceSelectionPacket();
    }

    public static void handle(OpenForceSelectionPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                com.cocojenna.client.gui.ForceSelectionScreen.open();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
