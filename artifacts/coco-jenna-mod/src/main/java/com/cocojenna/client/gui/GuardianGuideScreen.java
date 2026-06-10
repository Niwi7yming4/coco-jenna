package com.cocojenna.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** 《守護者指南》原版閱讀器（無 Patchouli 時；對應守護者.md 章節）. */
public class GuardianGuideScreen extends Screen {

    private static final String[] PAGES = {
            "guide.cocojenna.p.cover",
            "guide.cocojenna.p.coco",
            "guide.cocojenna.p.welcome",
            "guide.cocojenna.p.jenna",
            "guide.cocojenna.p.sisters",
            "guide.cocojenna.p.basics",
            "guide.cocojenna.p.alpha",
            "guide.cocojenna.p.first_cry",
            "guide.cocojenna.p.morning",
            "guide.cocojenna.p.bond",
            "guide.cocojenna.p.gather",
            "guide.cocojenna.p.mud",
            "guide.cocojenna.p.erosion",
            "guide.cocojenna.p.parasite",
            "guide.cocojenna.p.distill",
            "guide.cocojenna.p.bestiary",
            "guide.cocojenna.p.sequence",
            "guide.cocojenna.p.paths",
            "guide.cocojenna.p.wheel",
            "guide.cocojenna.p.map",
            "guide.cocojenna.p.memory",
            "guide.cocojenna.p.sister_bond",
            "guide.cocojenna.p.forge",
            "guide.cocojenna.p.town",
            "guide.cocojenna.p.four_mad",
            "guide.cocojenna.p.final",
            "guide.cocojenna.p.appendix"
    };

    private int page;

    public GuardianGuideScreen() {
        super(Component.translatable("guide.cocojenna.title"));
    }

    public static void open(Player player) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new GuardianGuideScreen());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        renderBackground(g);
        int x = width / 2 - 140;
        int y = 40;
        CocoJennaUi.drawPanel(g, x, y, 280, height - 80);
        g.drawCenteredString(font, title, width / 2, y + 8, CocoJennaUi.COL_INK);
        g.drawCenteredString(font, Component.translatable("guide.cocojenna.page", page + 1, PAGES.length),
                width / 2, y + 22, CocoJennaUi.COL_INK_SOFT);
        var lines = font.split(Component.translatable(PAGES[page]), 250);
        int ly = y + 40;
        for (var line : lines) {
            g.drawString(font, line, x + 16, ly, CocoJennaUi.COL_INK, false);
            ly += 10;
        }
        CocoJennaUi.drawButton(g, font, x + 20, height - 52, 80, 18,
                Component.translatable("guide.cocojenna.prev"), false, page > 0, false);
        CocoJennaUi.drawButton(g, font, x + 180, height - 52, 80, 18,
                Component.translatable("guide.cocojenna.next"), false, page < PAGES.length - 1, false);
        super.render(g, mouseX, mouseY, partial);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = width / 2 - 140;
        if (button == 0) {
            if (mouseY >= height - 52 && mouseY < height - 34) {
                if (mouseX >= x + 20 && mouseX < x + 100 && page > 0) {
                    page--;
                    return true;
                }
                if (mouseX >= x + 180 && mouseX < x + 260 && page < PAGES.length - 1) {
                    page++;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
