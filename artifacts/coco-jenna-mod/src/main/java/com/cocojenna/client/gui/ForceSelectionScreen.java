package com.cocojenna.client.gui;

import com.cocojenna.network.ConfirmForceSelectionPacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** 月光三岔路 — 源力最終選擇（不可逆）. */
public class ForceSelectionScreen extends Screen {

    private static final String[] FORCES = {"resonance", "shadow", "chaos"};
    private static final int[] COLORS = {0xFFFFD700, 0xFF9B59B6, 0xFFFFB7C5};
    private static final String[] ICONS = {"🌙", "🌑", "✨"};

    private int hovered = -1;

    public ForceSelectionScreen() {
        super(Component.translatable("gui.cocojenna.force_select.title"));
    }

    public static void open() {
        net.minecraft.client.Minecraft.getInstance().setScreen(new ForceSelectionScreen());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int panelW = 360;
        int panelH = 220;
        int left = (width - panelW) / 2;
        int top = (height - panelH) / 2;
        CocoJennaUi.drawCatKingdomFrame(g, font, left, top, panelW, panelH, title);
        g.drawCenteredString(font,
                Component.translatable("gui.cocojenna.force_select.subtitle"),
                width / 2, top + 28, CocoJennaUi.COL_COFFEE);
        hovered = -1;
        int cardW = 100;
        int cardH = 120;
        int gap = 12;
        int startX = left + (panelW - (cardW * 3 + gap * 2)) / 2;
        int cardY = top + 52;
        for (int i = 0; i < 3; i++) {
            int x = startX + i * (cardW + gap);
            boolean hov = mx >= x && mx < x + cardW && my >= cardY && my < cardY + cardH;
            if (hov) hovered = i;
            CocoJennaUi.drawForceCard(g, font, x, cardY, cardW, cardH,
                    FORCES[i], ICONS[i], COLORS[i], hov);
        }
        g.drawCenteredString(font,
                Component.translatable("gui.cocojenna.force_select.hint"),
                width / 2, top + panelH - 14, CocoJennaUi.COL_INK_SOFT);
        super.render(g, mx, my, partial);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && hovered >= 0 && hovered < FORCES.length) {
            ModNetwork.CHANNEL.sendToServer(new ConfirmForceSelectionPacket(FORCES[hovered]));
            onClose();
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
