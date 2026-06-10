package com.cocojenna.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 伺服器 → 客戶端：入門任務 HUD 提示. */
public class OnboardingHintPacket {

    private final String translationKey;
    private final String[] args;

    public OnboardingHintPacket(String translationKey, Object... args) {
        this.translationKey = translationKey;
        this.args = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            this.args[i] = String.valueOf(args[i]);
        }
    }

    public static void encode(OnboardingHintPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.translationKey);
        buf.writeVarInt(pkt.args.length);
        for (String arg : pkt.args) buf.writeUtf(arg);
    }

    public static OnboardingHintPacket decode(FriendlyByteBuf buf) {
        String key = buf.readUtf();
        int n = buf.readVarInt();
        String[] args = new String[n];
        for (int i = 0; i < n; i++) args[i] = buf.readUtf();
        return new OnboardingHintPacket(key, (Object[]) args);
    }

    public static void handle(OnboardingHintPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (!PacketAuthGuard.requireClientBound(ctx, "OnboardingHintPacket")) return;
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player == null) return;
            Component text = Component.translatable(pkt.translationKey, (Object[]) pkt.args);
            com.cocojenna.client.gui.TutorialHudOverlay.INSTANCE.show(text);
        });
        ctx.get().setPacketHandled(true);
    }
}
