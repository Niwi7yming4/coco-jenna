package com.cocojenna.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenSkillSettingsPacket {

    public OpenSkillSettingsPacket() {}

    public static void encode(OpenSkillSettingsPacket pkt, FriendlyByteBuf buf) {}

    public static OpenSkillSettingsPacket decode(FriendlyByteBuf buf) {
        return new OpenSkillSettingsPacket();
    }

    public static void handle(OpenSkillSettingsPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                com.cocojenna.client.gui.SkillSettingsScreen.open();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
