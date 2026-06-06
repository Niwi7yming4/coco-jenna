package com.cocojenna.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.cocojenna.CocoJennaMod;
import net.minecraft.client.gui.GuiGraphics;

/**
 * 初晴事件客戶端特效渲染器。
 *
 * <p>效果序列（共 600 tick = 30 秒）：
 * <ol>
 *   <li>0‑100 tick：全畫面漸白（alpha 0 → 255）</li>
 *   <li>100‑300 tick：純白畫面，播放初晴音樂</li>
 *   <li>300‑400 tick：漸出（alpha 255 → 0）</li>
 *   <li>400‑600 tick：天空顏色由深藍轉為金橙色，維持 10 秒</li>
 * </ol>
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID, value = Dist.CLIENT)
public class FirstDawnRenderer {

    private static int effectTick = -1; // -1 = 未激活

    public static void startEffect() {
        effectTick = 0;
    }

    public static boolean isActive() {
        return effectTick >= 0;
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (!isActive()) return;

        effectTick++;
        if (effectTick > 600) {
            effectTick = -1;
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        int screenW = event.getWindow().getGuiScaledWidth();
        int screenH = event.getWindow().getGuiScaledHeight();

        int alpha;
        if (effectTick <= 100) {
            alpha = (int) (255 * effectTick / 100f);
        } else if (effectTick <= 300) {
            alpha = 255;
        } else if (effectTick <= 400) {
            alpha = (int) (255 * (1 - (effectTick - 300) / 100f));
        } else {
            return; // 後半段不渲染白色覆蓋
        }

        // 全畫面白色覆蓋
        int color = (alpha << 24) | 0xFFFFFF;
        graphics.fill(0, 0, screenW, screenH, color);

        // 中央文字（淡入淡出）
        if (effectTick > 150 && effectTick < 350) {
            int textAlpha = Math.min(255, (int) (255 * Math.sin(Math.PI * (effectTick - 150) / 200f)));
            String text = "初晴";
            int textW = net.minecraft.client.Minecraft.getInstance().font.width(text);
            graphics.drawString(
                    net.minecraft.client.Minecraft.getInstance().font,
                    net.minecraft.network.chat.Component.literal(text)
                            .withStyle(net.minecraft.ChatFormatting.GOLD),
                    (screenW - textW) / 2,
                    screenH / 2 - 10,
                    (textAlpha << 24) | 0xFFD700,
                    false);
        }
    }
}
