package com.cocojenna.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** 記憶鍛造儀式進度 HUD（階段／剩餘 tick／祭壇 HP）. */
public class MemoryForgeHudOverlay {

    public static final MemoryForgeHudOverlay INSTANCE = new MemoryForgeHudOverlay();

    private boolean active;
    private int phaseOrdinal;
    private int ticksRemaining;
    private float altarHpRatio = 1f;

    private MemoryForgeHudOverlay() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void update(int phaseOrdinal, int ticksRemaining, float altarHpRatio) {
        this.phaseOrdinal = phaseOrdinal;
        this.ticksRemaining = Math.max(0, ticksRemaining);
        this.altarHpRatio = Math.max(0f, Math.min(1f, altarHpRatio));
        this.active = phaseOrdinal >= 0 && phaseOrdinal < 4;
    }

    public void clear() {
        active = false;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type() || !active) return;
        var mc = Minecraft.getInstance();
        GuiGraphics g = event.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int y = mc.getWindow().getGuiScaledHeight() / 2 + 24;
        String phaseKey = switch (phaseOrdinal) {
            case 0 -> "ritual.cocojenna.phase.awaken";
            case 1 -> "ritual.cocojenna.phase.defend";
            case 2 -> "ritual.cocojenna.phase.inject";
            case 3 -> "ritual.cocojenna.phase.resonance";
            default -> "ritual.cocojenna.phase.awaken";
        };
        g.drawCenteredString(mc.font, Component.translatable(phaseKey), w / 2, y, 0xFFE8D4A8);
        int barW = 120;
        int barX = w / 2 - barW / 2;
        int barY = y + 12;
        g.fill(barX - 1, barY - 1, barX + barW + 1, barY + 7, 0xFF3A2A1A);
        int fill = (int) (barW * altarHpRatio);
        g.fill(barX, barY, barX + fill, barY + 6, 0xFF66CC88);
        g.drawCenteredString(mc.font,
                Component.translatable("ritual.cocojenna.hud_ticks", ticksRemaining / 20),
                w / 2, barY + 10, 0xFFCCCCCC);
    }
}
