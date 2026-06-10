package com.cocojenna.client.gui;

import com.cocojenna.cloak.CloakDefinition;
import com.cocojenna.cloak.CloakWeaverMenu;
import com.cocojenna.network.CraftCloakPacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.RepairCloakPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class CloakWeaverScreen extends AbstractContainerScreen<CloakWeaverMenu> {

    private static final int BTN_W = 98;
    private static final int BTN_H = 14;

    private final List<CloakDefinition> cloaks = CloakDefinition.basicAndAdvanced();
    private int hoveredCraft = -1;
    private int hoveredCollect;
    private int hoveredRepair;

    public CloakWeaverScreen(CloakWeaverMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageWidth = 220;
        imageHeight = 280;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mx, int my) {
        CocoJennaUi.drawMachinePanel(g, font, leftPos, topPos, imageWidth, imageHeight, title);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        super.render(g, mx, my, partial);

        hoveredCraft = -1;
        hoveredCollect = 0;
        hoveredRepair = 0;

        int col0 = leftPos + 8;
        int col1 = leftPos + imageWidth / 2 + 2;
        int startY = topPos + 26;
        for (int i = 0; i < cloaks.size(); i++) {
            int col = i % 2;
            int row = i / 2;
            int x = col == 0 ? col0 : col1;
            int y = startY + row * (BTN_H + 3);
            boolean hovered = mx >= x && mx < x + BTN_W && my >= y && my < y + BTN_H;
            if (hovered) hoveredCraft = i;
            Component label = Component.translatable("item.cocojenna." + cloaks.get(i).itemId);
            CocoJennaUi.drawButton(g, font, x, y, BTN_W, BTN_H, label, hovered, true, false);
        }

        int actionY = topPos + 26 + 5 * (BTN_H + 3) + 6;
        int half = (imageWidth - 20) / 2;
        int ax = leftPos + 8;
        boolean hCollect = mx >= ax && mx < ax + half && my >= actionY && my < actionY + BTN_H;
        boolean hRepair = mx >= ax + half + 4 && mx < ax + half * 2 + 4
                && my >= actionY && my < actionY + BTN_H;
        if (hCollect) hoveredCollect = 1;
        if (hRepair) hoveredRepair = 1;
        CocoJennaUi.drawButton(g, font, ax, actionY, half, BTN_H,
                Component.translatable("cloak.cocojenna.collect"), hCollect, true, true);
        CocoJennaUi.drawButton(g, font, ax + half + 4, actionY, half, BTN_H,
                Component.translatable("cloak.cocojenna.repair_button"), hRepair, true, false);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            if (hoveredCraft >= 0) {
                ModNetwork.CHANNEL.sendToServer(new CraftCloakPacket(hoveredCraft));
                return true;
            }
            if (hoveredCollect == 1) {
                ModNetwork.CHANNEL.sendToServer(new CraftCloakPacket(-1));
                return true;
            }
            if (hoveredRepair == 1) {
                ModNetwork.CHANNEL.sendToServer(new RepairCloakPacket());
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}
