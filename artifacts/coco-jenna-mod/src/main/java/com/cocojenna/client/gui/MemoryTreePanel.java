package com.cocojenna.client.gui;

import com.cocojenna.capability.BondData;
import com.cocojenna.exploration.LoreRegistry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** 記憶之樹 — 碎片里程碑與傳說分支視覺化. */
public final class MemoryTreePanel {

    private static final int[] MILESTONES = {1, 5, 10, 20, 30, 50};

    private MemoryTreePanel() {}

    public static void render(GuiGraphics g, Font font, BondData bond, int x, int y, int w, int h) {
        int total = bond.getMemoryShardsTotal();
        int trunkX = x + w / 2;
        int trunkTop = y + 8;
        int trunkBottom = y + h - 28;

        g.fill(trunkX - 2, trunkTop, trunkX + 2, trunkBottom, 0xFF5C3D2E);
        for (int i = 0; i < MILESTONES.length; i++) {
            int my = trunkTop + 12 + i * ((trunkBottom - trunkTop - 24) / Math.max(1, MILESTONES.length - 1));
            boolean lit = total >= MILESTONES[i];
            int nodeColor = lit ? 0xFFFFD700 : 0xFF888888;
            g.fill(trunkX - 5, my - 5, trunkX + 5, my + 5, nodeColor);
            if (lit) {
                g.fill(trunkX - 3, my - 3, trunkX + 3, my + 3, 0xFFFFF8DC);
            }
            int branchDir = i % 2 == 0 ? -1 : 1;
            int bx = trunkX + branchDir * (28 + i * 4);
            int branchColor = lit ? 0xFF8B6914 : 0xFF666666;
            drawBranch(g, trunkX, my, bx, my - 6, branchColor);
            if (lit) {
                g.drawString(font, String.valueOf(MILESTONES[i]), bx + (branchDir > 0 ? 4 : -16), my - 4,
                        CocoJennaUi.COL_INK_SOFT, false);
            }
        }

        int loreY = y + h - 22;
        int loreLit = 0;
        for (var entry : LoreRegistry.all()) {
            if (bond.hasLore(entry.id())) loreLit++;
        }
        g.drawString(font, Component.translatable("gui.cocojenna.memory_tree.lore_branches", loreLit,
                LoreRegistry.all().size()), x, loreY, CocoJennaUi.COL_INK, false);

        int leafX = x + 8;
        int leafY = y + 6;
        int shown = 0;
        for (var entry : LoreRegistry.all()) {
            if (!bond.hasLore(entry.id())) continue;
            if (shown >= 6) break;
            int lx = leafX + (shown % 3) * 52;
            int ly = leafY + (shown / 3) * 10;
            g.fill(lx, ly, lx + 6, ly + 6, 0xFF66BB66);
            shown++;
        }
        if (shown == 0) {
            g.drawString(font, Component.translatable("gui.cocojenna.memory_tree.empty"),
                    x + 4, leafY, CocoJennaUi.COL_INK_SOFT, false);
        }
    }

    private static void drawBranch(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        int steps = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
        for (int i = 0; i <= steps; i++) {
            int px = x1 + (x2 - x1) * i / Math.max(1, steps);
            int py = y1 + (y2 - y1) * i / Math.max(1, steps);
            g.fill(px, py, px + 2, py + 2, color);
        }
    }
}
