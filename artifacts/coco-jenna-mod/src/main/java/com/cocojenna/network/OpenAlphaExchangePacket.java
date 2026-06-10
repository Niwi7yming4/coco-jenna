package com.cocojenna.network;

import com.cocojenna.client.gui.AlphaExchangeScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 伺服器 → 客戶端：開啟阿爾法兌換所. */
public record OpenAlphaExchangePacket() {

    public static void encode(OpenAlphaExchangePacket msg, FriendlyByteBuf buf) {}

    public static OpenAlphaExchangePacket decode(FriendlyByteBuf buf) {
        return new OpenAlphaExchangePacket();
    }

    public static void handle(OpenAlphaExchangePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(AlphaExchangeScreen::open);
        ctx.get().setPacketHandled(true);
    }
}
