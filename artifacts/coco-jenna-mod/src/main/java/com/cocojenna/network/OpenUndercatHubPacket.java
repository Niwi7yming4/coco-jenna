package com.cocojenna.network;

import com.cocojenna.client.web.WebUiRouter;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenUndercatHubPacket(String json) {

    public static void encode(OpenUndercatHubPacket msg, FriendlyByteBuf buf) {
        LargePayload.writeUtf8(buf, msg.json);
    }

    public static OpenUndercatHubPacket decode(FriendlyByteBuf buf) {
        return new OpenUndercatHubPacket(LargePayload.readUtf8(buf));
    }

    public static void handle(OpenUndercatHubPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                WebUiRouter.openUndercatHub(JsonParser.parseString(msg.json).getAsJsonObject()));
        ctx.get().setPacketHandled(true);
    }
}
