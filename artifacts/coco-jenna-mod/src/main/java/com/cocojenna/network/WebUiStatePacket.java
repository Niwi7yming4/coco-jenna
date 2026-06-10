package com.cocojenna.network;

import com.cocojenna.client.gui.KingdomHubFallbackScreen;
import com.cocojenna.client.gui.UndercatHubFallbackScreen;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server → client: push updated UI state to open vanilla screen. */
public record WebUiStatePacket(String sessionId, String json) {

    public static void encode(WebUiStatePacket msg, net.minecraft.network.FriendlyByteBuf buf) {
        buf.writeUtf(msg.sessionId, 64);
        LargePayload.writeUtf8(buf, msg.json);
    }

    public static WebUiStatePacket decode(net.minecraft.network.FriendlyByteBuf buf) {
        return new WebUiStatePacket(buf.readUtf(64), LargePayload.readUtf8(buf));
    }

    public static void handle(WebUiStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            JsonObject state = JsonParser.parseString(msg.json).getAsJsonObject();
            String type = state.has("type") ? state.get("type").getAsString() : "";
            if ("undercat".equals(type)) {
                var hub = UndercatHubFallbackScreen.current();
                if (hub != null) {
                    hub.updateState(state);
                    return;
                }
            }
            if ("kingdom".equals(type)) {
                var hub = KingdomHubFallbackScreen.current();
                if (hub != null) {
                    hub.updateState(state);
                    return;
                }
                KingdomHubFallbackScreen.open(state);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
