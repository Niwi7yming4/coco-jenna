package com.cocojenna.client;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.client.gui.CocoJennaUi;
import com.cocojenna.network.CastSequenceSkillPacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.sequence.FelineSequenceSkills;
import com.cocojenna.sequence.MoonCrossroadsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Alt 技能圓盤 — 序列 9–7 四槽、6–3 八槽、2 以下十六槽.
 */
public final class SkillWheelOverlay {

    private static final int RADIUS = 86;
    private static final int INNER_RADIUS = 50;
    private static final int OUTER_RADIUS = 72;
    private static final int SLOT_SIZE = 38;
    private static int selectedSlot = -1;
    private static boolean wasOpen;

    private SkillWheelOverlay() {}

    public static void render(GuiGraphics g, int screenW, int screenH) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || ModKeyBindings.SKILL_WHEEL == null) return;
        if (com.cocojenna.integration.FallenAbyssLinkage.isFallenPathLockedClient()) return;
        if (!ModKeyBindings.SKILL_WHEEL.isDown()) {
            if (wasOpen && selectedSlot >= 0) {
                ModNetwork.CHANNEL.sendToServer(new CastSequenceSkillPacket(selectedSlot));
            }
            wasOpen = false;
            selectedSlot = -1;
            return;
        }

        var bond = ModCapabilities.getOrDefault(mc.player);
        if (!MoonCrossroadsManager.hasChosenForce(bond)) {
            g.drawCenteredString(mc.font,
                    Component.translatable("force.cocojenna.wheel_locked"),
                    screenW / 2, screenH / 2, CocoJennaUi.COL_WARM_PINK);
            return;
        }

        wasOpen = true;
        int cx = screenW / 2;
        int cy = screenH / 2;
        double mx = mc.mouseHandler.xpos() * screenW / mc.getWindow().getScreenWidth();
        double my = mc.mouseHandler.ypos() * screenH / mc.getWindow().getScreenHeight();

        CocoJennaUi.drawRadialBackdrop(g, cx, cy, RADIUS);
        int forceColor = forceAccent(bond.getFelineForce());
        drawRing(g, cx, cy, RADIUS - 2, forceColor);

        int slots = slotCount(mc);
        Component[] labels = skillLabels(mc, slots);
        selectedSlot = pickSlot(cx, cy, mx, my, slots);

        long now = mc.level != null ? mc.level.getGameTime() : 0;
        long cdUntil = bond.getFelineSkillCooldownUntil();
        boolean showCd = bond.isShowSkillCooldown();
        float cdFrac = showCd && now < cdUntil
                ? (cdUntil - now) / (float) cooldownTotal(mc) : 0f;

        for (int i = 0; i < slots; i++) {
            int ringRadius = slotRadius(slots, i);
            double angle = Math.toRadians(-90 + i * (360.0 / slots));
            int sx = cx + (int) (Math.cos(angle) * ringRadius);
            int sy = cy + (int) (Math.sin(angle) * ringRadius);
            boolean sel = i == selectedSlot;
            int bg = sel ? 0xFFFFE8B0 : 0xE6FFFFFF;
            g.fill(sx - SLOT_SIZE / 2, sy - SLOT_SIZE / 2,
                    sx + SLOT_SIZE / 2, sy + SLOT_SIZE / 2, CocoJennaUi.COL_FRAME);
            g.fill(sx - SLOT_SIZE / 2 + 1, sy - SLOT_SIZE / 2 + 1,
                    sx + SLOT_SIZE / 2 - 1, sy + SLOT_SIZE / 2 - 1, bg);
            if (showCd && cdFrac > 0 && i == bond.getPreferredSkillSlot()) {
                drawCooldownArc(g, sx, sy, SLOT_SIZE / 2 - 2, cdFrac, 0xAA5D3A3A);
            }
            if (sel) {
                drawRing(g, sx, sy, SLOT_SIZE / 2 + 2, 0xFFFFD700);
                Component tip = i < labels.length ? labels[i] : Component.literal("?");
                int tw = mc.font.width(tip);
                g.drawString(mc.font, tip, cx - tw / 2, cy + RADIUS + 6, CocoJennaUi.COL_WARM_PINK);
            }
            String shortLabel = String.valueOf(i + 1);
            g.drawCenteredString(mc.font, shortLabel, sx, sy - 4, CocoJennaUi.COL_COFFEE);
        }

        g.fill(cx - 16, cy - 16, cx + 16, cy + 16, 0xEE2A1A28);
        g.drawCenteredString(mc.font, Component.literal("🐾"), cx, cy - 5, forceColor);
        Component forceName = Component.translatable("gui.cocojenna.force." + bond.getFelineForce());
        g.drawCenteredString(mc.font, forceName, cx, cy + 6, 0xFFCCCCCC);
    }

    private static int forceAccent(String force) {
        return switch (force) {
            case "shadow" -> 0xFF9B59B6;
            case "chaos" -> 0xFFFFB7C5;
            default -> 0xFFFFD700;
        };
    }

    private static int slotCount(Minecraft mc) {
        int tier = ModCapabilities.getOrDefault(mc.player).getFelineTier();
        if (tier <= 0) tier = 9;
        return FelineSequenceSkills.wheelSlotCount(tier);
    }

    private static int slotRadius(int slots, int index) {
        if (slots <= 4) return 62;
        if (slots <= 8) return index < 4 ? INNER_RADIUS : OUTER_RADIUS;
        return index < 8 ? INNER_RADIUS : OUTER_RADIUS;
    }

    private static Component[] skillLabels(Minecraft mc, int slots) {
        String force = ModCapabilities.getOrDefault(mc.player).getFelineForce();
        String[] keys = switch (force) {
            case "shadow" -> new String[]{
                    "gui.cocojenna.wheel.shadow.0", "gui.cocojenna.wheel.shadow.1",
                    "gui.cocojenna.wheel.shadow.2", "gui.cocojenna.wheel.shadow.3",
                    "gui.cocojenna.wheel.shadow.4", "gui.cocojenna.wheel.shadow.5",
                    "gui.cocojenna.wheel.shadow.6", "gui.cocojenna.wheel.shadow.7"};
            case "chaos" -> new String[]{
                    "gui.cocojenna.wheel.chaos.0", "gui.cocojenna.wheel.chaos.1",
                    "gui.cocojenna.wheel.chaos.2", "gui.cocojenna.wheel.chaos.3",
                    "gui.cocojenna.wheel.chaos.4", "gui.cocojenna.wheel.chaos.5",
                    "gui.cocojenna.wheel.chaos.6", "gui.cocojenna.wheel.chaos.7"};
            default -> new String[]{
                    "gui.cocojenna.wheel.resonance.0", "gui.cocojenna.wheel.resonance.1",
                    "gui.cocojenna.wheel.resonance.2", "gui.cocojenna.wheel.resonance.3",
                    "gui.cocojenna.wheel.resonance.4", "gui.cocojenna.wheel.resonance.5",
                    "gui.cocojenna.wheel.resonance.6", "gui.cocojenna.wheel.resonance.7"};
        };
        Component[] out = new Component[Math.min(slots, keys.length)];
        for (int i = 0; i < out.length; i++) {
            out[i] = Component.translatable(keys[i]);
        }
        return out;
    }

    private static int pickSlot(int cx, int cy, double mx, double my, int slots) {
        double dx = mx - cx;
        double dy = my - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 20 || dist > RADIUS) return -1;
        double angle = Math.toDegrees(Math.atan2(dy, dx)) + 90;
        if (angle < 0) angle += 360;
        return (int) Math.round(angle / (360.0 / slots)) % slots;
    }

    private static int cooldownTotal(Minecraft mc) {
        int tier = ModCapabilities.getOrDefault(mc.player).getFelineTier();
        if (tier <= 0) tier = 9;
        return switch (tier) {
            case 9 -> 600;
            case 8 -> 900;
            case 7 -> 1200;
            case 6, 5 -> 1500;
            default -> 1800;
        };
    }

    private static void drawCooldownArc(GuiGraphics g, int cx, int cy, int r, float frac, int color) {
        int steps = (int) (frac * 48);
        for (int i = 0; i < steps; i++) {
            double rad = Math.toRadians(-90 + i * (360.0 / 48));
            int x = cx + (int) (Math.cos(rad) * r);
            int y = cy + (int) (Math.sin(rad) * r);
            g.fill(x, y, x + 2, y + 2, color);
        }
    }

    private static void drawRing(GuiGraphics g, int cx, int cy, int r, int color) {
        for (int a = 0; a < 360; a += 3) {
            double rad = Math.toRadians(a);
            int x = cx + (int) (Math.cos(rad) * r);
            int y = cy + (int) (Math.sin(rad) * r);
            g.fill(x, y, x + 2, y + 2, color);
        }
    }
}
