package com.cocojenna.network;

import com.cocojenna.client.gui.KingdomHubFallbackScreen;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenKingdomHubPacket(String json) {

    public static void encode(OpenKingdomHubPacket msg, FriendlyByteBuf buf) {
        LargePayload.writeUtf8(buf, msg.json);
    }

    public static OpenKingdomHubPacket decode(FriendlyByteBuf buf) {
        return new OpenKingdomHubPacket(LargePayload.readUtf8(buf));
    }

    public static void handle(OpenKingdomHubPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            JsonObject state = JsonParser.parseString(msg.json).getAsJsonObject();
            KingdomHubFallbackScreen.open(state);
        });
        ctx.get().setPacketHandled(true);
    }
}
