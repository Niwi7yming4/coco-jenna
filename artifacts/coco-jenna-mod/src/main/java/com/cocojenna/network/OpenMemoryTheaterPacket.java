package com.cocojenna.network;

import com.cocojenna.client.gui.MemoryTheaterScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** 伺服器 → 客戶端：開啟記憶劇場選單. */
public record OpenMemoryTheaterPacket(List<String> scenes) {

    public static void encode(OpenMemoryTheaterPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.scenes.size());
        for (String scene : msg.scenes) {
            buf.writeUtf(scene);
        }
    }

    public static OpenMemoryTheaterPacket decode(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        List<String> scenes = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            scenes.add(buf.readUtf());
        }
        return new OpenMemoryTheaterPacket(scenes);
    }

    public static void handle(OpenMemoryTheaterPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> MemoryTheaterScreen.open(msg.scenes));
        ctx.get().setPacketHandled(true);
    }
}
