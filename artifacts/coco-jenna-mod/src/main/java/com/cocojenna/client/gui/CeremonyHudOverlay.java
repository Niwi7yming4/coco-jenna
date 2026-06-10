package com.cocojenna.client.gui;

import com.cocojenna.sequence.PromotionCeremonyHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 晉升儀式HUD疊層
 * 顯示當前儀式階段、進度條、提示文字
 */
public class CeremonyHudOverlay {

    public static final CeremonyHudOverlay INSTANCE = new CeremonyHudOverlay();

    private int currentStage = 0;       // 0=NONE
    private int currentTier = 9;
    private int maxDuration = 0;
    private int elapsedTicks = 0;
    private boolean active = false;

    private static final String[] STAGE_KEYS = {
        "",
        "ceremony.cocojenna.stage.summoning",
        "ceremony.cocojenna.stage.sacrifice",
        "ceremony.cocojenna.stage.resonance",
        "ceremony.cocojenna.stage.revelation",
        "ceremony.cocojenna.stage.marking",
        "ceremony.cocojenna.stage.complete"
    };

    private static final int[] STAGE_COLORS = {
        0,                              // NONE
        0xFFFFD700,                     // SUMMONING 金色
        0xFFFF8C00,                     // SACRIFICE 橙色
        0xFF9B30FF,                     // RESONANCE 紫色
        0xFFFF69B4,                     // REVELATION 粉色
        0xFFFFD700,                     // MARKING 金色
        0xFF00FF88                      // COMPLETE 綠色
    };

    private CeremonyHudOverlay() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onStageChanged(int stage, int tier, int durationTicks) {
        currentStage = stage;
        currentTier = tier;
        active = stage > 0 && stage < 6;
        if (durationTicks > 0) {
            maxDuration = durationTicks;
            elapsedTicks = 0;
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type()) return;
        if (!active) return;

        var mc = Minecraft.getInstance();
        var g = event.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        elapsedTicks++;

        // 階段名稱
        if (currentStage > 0 && currentStage < STAGE_KEYS.length) {
            Component stageName = Component.translatable(STAGE_KEYS[currentStage], currentTier);
            g.drawCenteredString(mc.font, stageName, w / 2, h / 2 + 60, STAGE_COLORS[currentStage]);
        }

        // 進度條（如果有持續時間）
        if (maxDuration > 0) {
            float progress = Math.min(1.0f, (float) elapsedTicks / maxDuration);
            int barW = 200;
            int barH = 4;
            int barX = w / 2 - barW / 2;
            int barY = h / 2 + 76;

            // 背景
            g.fill(barX, barY, barX + barW, barY + barH, 0x44000000);
            // 進度
            int fillW = (int)(barW * progress);
            if (fillW > 0) {
                g.fill(barX, barY, barX + fillW, barY + barH, STAGE_COLORS[currentStage]);
            }
        }

        // 儀式提示（階段說明）
        if (currentStage == 1) {
            // 召喚階段提示
            g.drawCenteredString(mc.font,
                Component.translatable("ceremony.cocojenna.hint.summoning"),
                w / 2, h / 2 + 90, 0xAAFFFFFF);
        } else if (currentStage == 2) {
            // 獻祭階段提示
            g.drawCenteredString(mc.font,
                Component.translatable("ceremony.cocojenna.hint.sacrifice"),
                w / 2, h / 2 + 90, 0xAAFFFFFF);
        } else if (currentStage == 3) {
            // 共鳴階段提示
            g.drawCenteredString(mc.font,
                Component.translatable("ceremony.cocojenna.hint.resonance"),
                w / 2, h / 2 + 90, 0xAAFFFFFF);
        } else if (currentStage == 4) {
            // 啟示階段：卡牌選擇提示（由CeremonyCardSelectionScreen處理）
        } else if (currentStage == 5) {
            g.drawCenteredString(mc.font,
                Component.translatable("ceremony.cocojenna.hint.marking"),
                w / 2, h / 2 + 90, 0xAAFFFFFF);
        }
    }
}