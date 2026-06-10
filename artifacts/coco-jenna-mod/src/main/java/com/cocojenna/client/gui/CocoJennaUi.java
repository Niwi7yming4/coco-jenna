package com.cocojenna.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** 貓之國共用 GUI 繪製（羊皮紙 / 玫瑰金風格）. */
public final class CocoJennaUi {

    public static final int COL_INK      = 0xFF3D2E24;
    public static final int COL_INK_SOFT = 0xFF6B5344;
    public static final int COL_PARCHMENT = 0xFFF4E8D0;
    public static final int COL_PARCHMENT_DARK = 0xFFE8D4B0;
    public static final int COL_FRAME    = 0xFF5C3D2E;
    public static final int COL_FRAME_LT = 0xFF8B6914;
    public static final int COL_ACCENT   = 0xFFE8A0BF;
    public static final int COL_ACCENT_DK = 0xFFCC6B8A;
    public static final int COL_WARM_PINK = 0xFFFFB7C5;
    public static final int COL_COFFEE = 0xFF5D3A3A;

    private CocoJennaUi() {}

    public static void drawCatKingdomFrame(GuiGraphics g, Font font, int x, int y, int w, int h, Component title) {
        drawPanel(g, x, y, w, h);
        drawHeader(g, font, x, y, w, 22, title);
        drawPawCorners(g, x, y, w, h);
    }

    public static void drawPawCorners(GuiGraphics g, int x, int y, int w, int h) {
        int paw = COL_ACCENT_DK;
        g.fill(x + 4, y + 4, x + 8, y + 8, paw);
        g.fill(x + w - 8, y + 4, x + w - 4, y + 8, paw);
        g.fill(x + 4, y + h - 8, x + 8, y + h - 4, paw);
        g.fill(x + w - 8, y + h - 8, x + w - 4, y + h - 4, paw);
    }

    public static void drawRadialBackdrop(GuiGraphics g, int cx, int cy, int r) {
        for (int y = -r; y <= r; y++) {
            int w = (int) Math.sqrt(r * r - y * y);
            g.fill(cx - w, cy + y, cx + w, cy + y + 1, 0xCC1A1018);
        }
        for (int a = 0; a < 360; a += 4) {
            double rad = Math.toRadians(a);
            int px = cx + (int) (Math.cos(rad) * r);
            int py = cy + (int) (Math.sin(rad) * r);
            g.fill(px, py, px + 2, py + 2, COL_WARM_PINK);
        }
    }

    public static void drawForceCard(GuiGraphics g, Font font, int x, int y, int w, int h,
            String force, String icon, int accent, boolean hovered) {
        int border = hovered ? 0xFFFFD700 : COL_FRAME;
        g.fill(x, y, x + w, y + h, border);
        g.fill(x + 2, y + 2, x + w - 2, y + h - 2, hovered ? 0xFFFFF5F8 : COL_PARCHMENT);
        g.fill(x + 2, y + 2, x + w - 2, y + 24, accent & 0x88FFFFFF | 0x44000000);
        g.drawCenteredString(font, icon, x + w / 2, y + 8, COL_COFFEE);
        Component name = Component.translatable("gui.cocojenna.force." + force);
        g.drawCenteredString(font, name, x + w / 2, y + 36, COL_INK);
        Component bonus = Component.translatable("gui.cocojenna.force_select.bonus." + force);
        int ty = y + 52;
        for (String line : bonus.getString().split("\n")) {
            if (ty > y + h - 12) break;
            g.drawCenteredString(font, Component.literal(line), x + w / 2, ty, COL_INK_SOFT);
            ty += 10;
        }
    }

    public static void drawPanel(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x - 2, y - 2, x + w + 2, y + h + 2, 0x88000000);
        g.fill(x, y, x + w, y + h, COL_FRAME);
        g.fill(x + 1, y + 1, x + w - 1, y + 2, COL_FRAME_LT);
        g.fill(x + 1, y + 1, x + 2, y + h - 1, COL_FRAME_LT);
        g.fill(x + 3, y + 3, x + w - 3, y + h - 3, COL_PARCHMENT);
    }

    public static void drawInset(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, 0xFFD8C8A8);
        g.fill(x, y, x + w, y + 1, 0xFF9A8A6A);
        g.fill(x, y, x + 1, y + h, 0xFF9A8A6A);
        g.fill(x, y + h - 1, x + w, y + h, 0xFFF8F0E0);
        g.fill(x + w - 1, y, x + w, y + h, 0xFFF8F0E0);
    }

    public static void drawHeader(GuiGraphics g, Font font, int x, int y, int w, int h, Component title) {
        g.fill(x + 2, y + 2, x + w - 2, y + h, COL_ACCENT);
        g.fill(x + 4, y + 4, x + w - 4, y + h - 2, COL_PARCHMENT);
        int tw = font.width(title);
        g.drawString(font, title, x + (w - tw) / 2, y + (h - 8) / 2, COL_INK, false);
        g.fill(x + 8, y + h - 1, x + w - 8, y + h, COL_ACCENT_DK);
    }

    public static void drawButton(GuiGraphics g, Font font, int x, int y, int w, int h,
            Component label, boolean hovered, boolean enabled, boolean accent) {
        int border = enabled ? (accent ? COL_ACCENT_DK : COL_FRAME) : 0xFF888888;
        int bg = !enabled ? 0xFFBBBBBB
                : hovered ? (accent ? 0xFFFFD0E0 : 0xFFF0D8C8)
                : (accent ? COL_ACCENT : COL_PARCHMENT_DARK);
        g.fill(x, y, x + w, y + h, border);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, bg);
        int color = enabled ? COL_INK : COL_INK_SOFT;
        int lw = font.width(label);
        int lx = x + Math.max(4, (w - lw) / 2);
        g.drawString(font, label, lx, y + (h - 8) / 2, color, false);
    }

    public static void drawTab(GuiGraphics g, Font font, int x, int y, int w, int h,
            boolean selected, boolean hovered, ResourceLocation icon, Component label) {
        int bg = selected ? COL_ACCENT : hovered ? 0xFFF0D8C8 : COL_PARCHMENT_DARK;
        int border = selected ? COL_ACCENT_DK : 0xFF9A8A6A;
        g.fill(x, y, x + w, y + h, border);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, bg);
        if (selected) {
            g.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, COL_PARCHMENT);
        }
        g.blit(icon, x + 4, y + 3, 18, 18, 0, 0, 32, 32, 32, 32);
        int textX = x + 22;
        int maxTextW = w - 24;
        Component clipped = label;
        if (font.width(label) > maxTextW) {
            String raw = label.getString();
            while (raw.length() > 1 && font.width(raw + "…") > maxTextW) {
                raw = raw.substring(0, raw.length() - 1);
            }
            clipped = Component.literal(raw + "…");
        }
        g.drawString(font, clipped, textX, y + (h - 8) / 2, selected ? COL_INK : COL_INK_SOFT, false);
    }

    public static void drawStatBar(GuiGraphics g, Font font, int x, int y, int w,
            Component label, float value, float max, int fillColor) {
        g.drawString(font, label, x, y, COL_INK_SOFT, false);
        int barY = y + 11;
        int barH = 7;
        g.fill(x, barY, x + w, barY + barH, 0xFF5A4A3A);
        g.fill(x + 1, barY + 1, x + w - 1, barY + barH - 1, 0xFF3A2A1A);
        int fill = Math.max(0, (int) ((w - 2) * value / max));
        if (fill > 0) {
            g.fill(x + 1, barY + 1, x + 1 + fill, barY + barH - 1, fillColor);
            g.fill(x + 1, barY + 1, x + 1 + fill, barY + 2, brighten(fillColor));
        }
        String valText = String.format("%.0f", value);
        g.drawString(font, valText, x + w - font.width(valText), y, COL_INK, false);
    }

    public static void drawBondBridge(GuiGraphics g, Font font, int x, int y, int w, float bond) {
        int barH = 8;
        int barY = y + 12;
        g.drawString(font, Component.literal("\u2665"), x + w / 2 - 3, y + 1, COL_ACCENT_DK, false);
        g.fill(x, barY, x + w, barY + barH, 0xFF5A4A3A);
        g.fill(x + 1, barY + 1, x + w - 1, barY + barH - 1, 0xFF3A2A1A);
        int fill = Math.max(0, (int) ((w - 2) * bond / 100f));
        if (fill > 0) {
            g.fill(x + 1, barY + 1, x + 1 + fill, barY + barH - 1, 0xFFCC3366);
        }
        String pct = String.format("%.0f%%", bond);
        g.drawString(font, pct, x + w - font.width(pct), barY + barH + 3, COL_INK_SOFT, false);
    }

    /** GUI 立繪貼圖標準尺寸（與 tools 產出一致）. */
    public static final int PORTRAIT_TEX_SIZE = 128;

    public static void drawPortrait(GuiGraphics g, ResourceLocation tex, int x, int y, int size) {
        drawPortraitArt(g, tex, x, y, size);
    }

    public static void drawPortraitArt(GuiGraphics g, ResourceLocation tex, int x, int y, int size) {
        g.fill(x - 1, y - 1, x + size + 1, y + size + 1, COL_FRAME);
        int s = PORTRAIT_TEX_SIZE;
        g.blit(tex, x, y, size, size, 0, 0, s, s, s, s);
    }

    public static void drawMachinePanel(GuiGraphics g, Font font, int x, int y, int w, int h, Component title) {
        drawPanel(g, x, y, w, h);
        drawHeader(g, font, x, y, w, 20, title);
        drawInset(g, x + 6, y + 24, w - 12, h - 30);
    }

    public static void drawCardHover(GuiGraphics g, int x, int y, int w, int h, boolean hovered, boolean selected) {
        int color = selected ? 0xFFFFD700 : hovered ? 0xCCFFFFFF : 0x00000000;
        if (color != 0) {
            g.fill(x - 2, y - 2, x + w + 2, y + h + 2, color);
        }
    }

    private static int brighten(int color) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, ((color >> 16) & 0xFF) + 40);
        int gr = Math.min(255, ((color >> 8) & 0xFF) + 40);
        int b = Math.min(255, (color & 0xFF) + 40);
        return (a << 24) | (r << 16) | (gr << 8) | b;
    }
}
