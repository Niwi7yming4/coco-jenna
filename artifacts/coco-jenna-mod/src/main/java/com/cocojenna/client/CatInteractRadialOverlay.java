package com.cocojenna.client;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.client.gui.CocoJennaUi;
import com.cocojenna.network.CatRadialActionPacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** V 鍵環形互動選單 — 撫摸／餵食／梳毛／玩耍／跟隨. */
public final class CatInteractRadialOverlay {

    private static final int RADIUS = 64;
    private static final String[] ICONS = {"✋", "🐟", "🪮", "🪶", "👣"};
    private static final String[] KEYS = {
            "gui.cocojenna.radial.pet",
            "gui.cocojenna.radial.feed",
            "gui.cocojenna.radial.groom",
            "gui.cocojenna.radial.play",
            "gui.cocojenna.radial.follow"
    };

    private static int selected = -1;
    private static boolean wasOpen;

    private CatInteractRadialOverlay() {}

    public static void render(GuiGraphics g, int screenW, int screenH) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || ModKeyBindings.INTERACT_CAT == null) return;
        if (!ModKeyBindings.INTERACT_CAT.isDown()) {
            if (wasOpen && selected >= 0) {
                ModNetwork.CHANNEL.sendToServer(new CatRadialActionPacket(selected));
            }
            wasOpen = false;
            selected = -1;
            return;
        }
        wasOpen = true;
        int cx = screenW / 2;
        int cy = screenH / 2;
        double mx = mc.mouseHandler.xpos() * screenW / mc.getWindow().getScreenWidth();
        double my = mc.mouseHandler.ypos() * screenH / mc.getWindow().getScreenHeight();

        CocoJennaUi.drawRadialBackdrop(g, cx, cy, RADIUS);
        selected = pickSegment(cx, cy, mx, my, KEYS.length);

        var bond = ModCapabilities.getOrDefault(mc.player);
        for (int i = 0; i < KEYS.length; i++) {
            double angle = Math.toRadians(-90 + i * (360.0 / KEYS.length));
            int sx = cx + (int) (Math.cos(angle) * 48);
            int sy = cy + (int) (Math.sin(angle) * 48);
            boolean sel = i == selected;
            int size = 28;
            int bg = sel ? 0xFFFFB7C5 : 0xCCFFFFFF;
            g.fill(sx - size / 2, sy - size / 2, sx + size / 2, sy + size / 2, bg);
            if (sel) {
                g.fill(sx - size / 2 - 1, sy - size / 2 - 1, sx + size / 2 + 1, sy + size / 2 + 1, 0xFF5D3A3A);
            }
            g.drawCenteredString(mc.font, ICONS[i], sx, sy - 5, CocoJennaUi.COL_COFFEE);
            if (sel) {
                Component tip = Component.translatable(KEYS[i]);
                int tw = mc.font.width(tip);
                g.drawString(mc.font, tip, cx - tw / 2, cy + RADIUS + 8, CocoJennaUi.COL_WARM_PINK);
                float emo = i < 2 ? bond.getCocoEmotion() : bond.getJennaEmotion();
                g.drawCenteredString(mc.font,
                        Component.translatable("gui.cocojenna.radial.emotion", (int) emo),
                        cx, cy + RADIUS + 22, CocoJennaUi.COL_INK_SOFT);
            }
        }
        g.drawCenteredString(mc.font, Component.literal("🐾"), cx, cy - 4, 0xFFFFD700);
    }

    private static int pickSegment(int cx, int cy, double mx, double my, int segments) {
        double dx = mx - cx;
        double dy = my - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 14 || dist > RADIUS) return -1;
        double angle = Math.toDegrees(Math.atan2(dy, dx)) + 90;
        if (angle < 0) angle += 360;
        return (int) Math.round(angle / (360.0 / segments)) % segments;
    }
}
