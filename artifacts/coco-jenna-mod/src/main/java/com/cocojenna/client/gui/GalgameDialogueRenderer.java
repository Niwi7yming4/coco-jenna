package com.cocojenna.client.gui;

import com.cocojenna.dialogue.DialogueExpression;
import com.cocojenna.dialogue.DialogueLine;
import com.cocojenna.dialogue.PortraitSide;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** GAL 九切片框、背景、立繪動畫繪製. */
public final class GalgameDialogueRenderer {

    private static final int PORTRAIT_SIZE = 128;

    private GalgameDialogueRenderer() {}

    public static void drawBackground(GuiGraphics g, int width, int height, String backgroundId, float fade) {
        int overlay = Mth.clamp((int) (fade * 160), 0, 200);
        g.fill(0, 0, width, height, (overlay << 24));
        ResourceLocation bg = GalgameUiAssets.background(backgroundId);
        if (hasTexture(bg)) {
            int bw = GalgameUiAssets.BACKGROUND_W;
            int bh = GalgameUiAssets.BACKGROUND_H;
            float scale = Math.max((float) width / bw, (float) height / bh);
            int dw = (int) (bw * scale);
            int dh = (int) (bh * scale);
            int bx = (width - dw) / 2;
            int by = (height - dh) / 2;
            g.blit(bg, bx, by, dw, dh, 0, 0, bw, bh, bw, bh);
            g.fill(0, 0, width, height, (overlay << 24));
        } else {
            int tint = backgroundTint(backgroundId);
            g.fill(0, 0, width, height, (overlay << 24) | (tint & 0x00FFFFFF));
        }
    }

    public static void drawDialogBox(GuiGraphics g, int x, int y, int w, int h) {
        if (hasTexture(GalgameUiAssets.dialogBox())) {
            drawNineSlice(g, GalgameUiAssets.dialogBox(), x, y, w, h,
                    GalgameUiAssets.DIALOG_TEX, GalgameUiAssets.DIALOG_BORDER);
        } else {
            CocoJennaUi.drawPanel(g, x, y, w, h);
            g.fill(x, y, x + w, y + 2, CocoJennaUi.COL_ACCENT);
            g.fill(x, y + h - 2, x + w, y + h, CocoJennaUi.COL_ACCENT);
        }
    }

    public static void drawNamePlate(GuiGraphics g, Font font, int x, int y, Component speaker) {
        int textW = font.width(speaker);
        int plateW = Math.max(GalgameUiAssets.NAME_PLATE_W, textW + 20);
        if (hasTexture(GalgameUiAssets.namePlate())) {
            drawNineSlice(g, GalgameUiAssets.namePlate(), x, y, plateW, GalgameUiAssets.NAME_PLATE_H,
                    GalgameUiAssets.NAME_PLATE_W, 8);
        } else {
            g.fill(x, y, x + plateW, y + GalgameUiAssets.NAME_PLATE_H, CocoJennaUi.COL_ACCENT);
        }
        g.drawString(font, speaker, x + 10, y + 10, CocoJennaUi.COL_INK, false);
    }

    public static void drawPortrait(GuiGraphics g, DialogueLine line, int x, int y,
            int tickCounter, float fadeAlpha) {
        if (line.portraitSide() == PortraitSide.NONE) return;
        ResourceLocation tex = GuiTextures.portraitForLine(
                line.speakerKey(), line.portrait(), line.expression());
        int bob = (int) (Math.sin(tickCounter * 0.08) * 2);
        int alpha = Mth.clamp((int) (fadeAlpha * 255), 0, 255);
        int frame = CocoJennaUi.COL_FRAME | (alpha << 24);
        g.fill(x - 2, y - 2 + bob, x + PORTRAIT_SIZE + 2, y + PORTRAIT_SIZE + 2 + bob, frame);
        g.setColor(1f, 1f, 1f, fadeAlpha);
        g.blit(tex, x, y + bob, PORTRAIT_SIZE, PORTRAIT_SIZE,
                0, 0, CocoJennaUi.PORTRAIT_TEX_SIZE, CocoJennaUi.PORTRAIT_TEX_SIZE,
                CocoJennaUi.PORTRAIT_TEX_SIZE, CocoJennaUi.PORTRAIT_TEX_SIZE);
        g.setColor(1f, 1f, 1f, 1f);
    }

    public static void drawChoicePanel(GuiGraphics g, Font font, int x, int y, int w, int h,
            Component label, boolean hovered, boolean selected) {
        if (hasTexture(GalgameUiAssets.choicePanel())) {
            drawNineSlice(g, GalgameUiAssets.choicePanel(), x, y, w, h, 128, 12);
            if (hovered || selected) {
                g.fill(x + 2, y + 2, x + w - 2, y + h - 2, selected ? 0x44FFD700 : 0x33FFFFFF);
            }
        } else {
            CocoJennaUi.drawButton(g, font, x, y, w, h, label, hovered, true, true);
            return;
        }
        int color = hovered ? CocoJennaUi.COL_INK : CocoJennaUi.COL_INK_SOFT;
        g.drawString(font, label, x + 10, y + (h - 8) / 2, color, false);
    }

    public static void drawLogPanel(GuiGraphics g, Font font, int x, int y, int w, int h,
            java.util.List<String> entries) {
        g.fill(x, y, x + w, y + h, 0xDD1A1410);
        g.fill(x, y, x + w, y + 1, CocoJennaUi.COL_ACCENT);
        int ly = y + 6;
        for (int i = entries.size() - 1; i >= 0 && ly < y + h - 8; i--) {
            for (var line : font.split(Component.literal(entries.get(i)), w - 12)) {
                if (ly > y + h - 10) break;
                g.drawString(font, line, x + 6, ly, CocoJennaUi.COL_INK_SOFT, false);
                ly += 9;
            }
            ly += 2;
        }
    }

    public static void drawControlButton(GuiGraphics g, Font font, int x, int y, int w, int h,
            Component label, boolean active) {
        int bg = active ? CocoJennaUi.COL_ACCENT : 0xCC3A2A1A;
        g.fill(x, y, x + w, y + h, bg);
        g.drawString(font, label, x + 4, y + (h - 8) / 2,
                active ? CocoJennaUi.COL_INK : CocoJennaUi.COL_INK_SOFT, false);
    }

    private static void drawNineSlice(GuiGraphics g, ResourceLocation tex,
            int x, int y, int w, int h, int srcSize, int border) {
        int s = srcSize;
        int b = border;
        int innerW = w - b * 2;
        int innerH = h - b * 2;
        int innerSrc = s - b * 2;
        g.blit(tex, x, y, b, b, 0, 0, b, b, s, s);
        g.blit(tex, x + w - b, y, b, b, s - b, 0, b, b, s, s);
        g.blit(tex, x, y + h - b, b, b, 0, s - b, b, b, s, s);
        g.blit(tex, x + w - b, y + h - b, b, b, s - b, s - b, b, b, s, s);
        blitStretch(g, tex, x + b, y, innerW, b, b, 0, innerSrc, b, s, s);
        blitStretch(g, tex, x + b, y + h - b, innerW, b, b, s - b, innerSrc, b, s, s);
        blitStretch(g, tex, x, y + b, b, innerH, 0, b, b, innerSrc, s, s);
        blitStretch(g, tex, x + w - b, y + b, b, innerH, s - b, b, b, innerSrc, s, s);
        blitStretch(g, tex, x + b, y + b, innerW, innerH, b, b, innerSrc, innerSrc, s, s);
    }

    private static void blitStretch(GuiGraphics g, ResourceLocation tex,
            int dx, int dy, int dw, int dh, int sx, int sy, int sw, int sh, int texW, int texH) {
        g.blit(tex, dx, dy, dw, dh, sx, sy, sw, sh, texW, texH);
    }

    private static boolean hasTexture(ResourceLocation id) {
        return net.minecraft.client.Minecraft.getInstance().getResourceManager().getResource(id).isPresent();
    }

    private static int backgroundTint(String id) {
        return switch (id) {
            case "first_cry_sunset" -> 0xCCFFB6A0;
            case "moon_plaza" -> 0xCC8899CC;
            case "black_mud" -> 0xCC2A1A2A;
            case "mausoleum" -> 0xCC4A3A5A;
            case "paper_box" -> 0xCC8B6914;
            case "undercat" -> 0xCC1A1A3A;
            case "overworld" -> 0xCC6A8A6A;
            default -> 0xCC2A2030;
        };
    }
}
