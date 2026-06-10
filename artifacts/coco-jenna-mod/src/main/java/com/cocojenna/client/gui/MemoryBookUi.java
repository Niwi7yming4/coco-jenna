package com.cocojenna.client.gui;

import com.cocojenna.dialogue.Portrait;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** 記憶之書版面常數與繪製（委派至 {@link CocoJennaUi}）. */
final class MemoryBookUi {

    static final int PANEL_W = 360;
    static final int PANEL_H = 252;
    static final int SIDEBAR_W = 68;
    static final int TAB_H = 22;
    static final int TAB_W = SIDEBAR_W - 8;
    static final int TAB_GAP = 3;
    static final int HEADER_H = 22;
    static final int PAD = 12;

    static final int COL_INK = CocoJennaUi.COL_INK;
    static final int COL_INK_SOFT = CocoJennaUi.COL_INK_SOFT;
    static final int COL_PARCHMENT = CocoJennaUi.COL_PARCHMENT;
    static final int COL_PARCHMENT_DARK = CocoJennaUi.COL_PARCHMENT_DARK;
    static final int COL_FRAME = CocoJennaUi.COL_FRAME;
    static final int COL_FRAME_LT = CocoJennaUi.COL_FRAME_LT;
    static final int COL_ACCENT = CocoJennaUi.COL_ACCENT;
    static final int COL_ACCENT_DK = CocoJennaUi.COL_ACCENT_DK;

    static final ResourceLocation TEX_COCO = GuiTextures.portrait(Portrait.COCO);
    static final ResourceLocation TEX_JENNA = GuiTextures.portrait(Portrait.JENNA);

    private MemoryBookUi() {}

    static void drawPanel(GuiGraphics g, int x, int y, int w, int h) {
        CocoJennaUi.drawPanel(g, x, y, w, h);
        g.fill(x + SIDEBAR_W, y + HEADER_H + 4, x + w - PAD, y + h - PAD, COL_PARCHMENT_DARK);
        drawInset(g, x + SIDEBAR_W, y + HEADER_H + 4, w - SIDEBAR_W - PAD, h - HEADER_H - PAD - 4);
        g.fill(x + 4, y + HEADER_H + 4, x + SIDEBAR_W - 2, y + h - PAD, COL_PARCHMENT);
    }

    static void drawInset(GuiGraphics g, int x, int y, int w, int h) {
        CocoJennaUi.drawInset(g, x, y, w, h);
    }

    static void drawHeader(GuiGraphics g, Font font, int x, int y, int w, Component title) {
        CocoJennaUi.drawHeader(g, font, x, y, w, HEADER_H, title);
    }

    static void drawTab(GuiGraphics g, Font font, int x, int y, int w, int h,
            boolean selected, boolean hovered, ResourceLocation icon, Component label) {
        CocoJennaUi.drawTab(g, font, x, y, w, h, selected, hovered, icon, label);
    }

    static void drawStatBar(GuiGraphics g, Font font, int x, int y, int w,
            Component label, float value, float max, int fillColor) {
        CocoJennaUi.drawStatBar(g, font, x, y, w, label, value, max, fillColor);
    }

    static void drawBondBridge(GuiGraphics g, Font font, int x, int y, int w, float bond) {
        CocoJennaUi.drawBondBridge(g, font, x, y, w, bond);
    }

    static void drawPortrait(GuiGraphics g, ResourceLocation tex, int x, int y, int size) {
        CocoJennaUi.drawPortraitArt(g, tex, x, y, size);
    }
}
