package com.cocojenna.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 伺服器 → 客戶端：播放「初晴」終局事件特效 */
public class TriggerFirstDawnPacket {

    public TriggerFirstDawnPacket() {}

    public static void encode(TriggerFirstDawnPacket packet, FriendlyByteBuf buf) {}

    public static TriggerFirstDawnPacket decode(FriendlyByteBuf buf) {
        return new TriggerFirstDawnPacket();
    }

    public static void handle(TriggerFirstDawnPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 客戶端播放初晴音樂並觸發特殊天空顏色變化
            var mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.getSoundManager().play(
                        net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                                com.cocojenna.init.ModSounds.WORLD_FIRST_DAWN.get(), 1.0f));

                // 顯示全畫面漸白效果（由客戶端 tick 驅動）
                com.cocojenna.client.FirstDawnRenderer.startEffect();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
