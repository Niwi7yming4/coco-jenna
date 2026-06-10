package com.cocojenna.client.gui;

import com.cocojenna.world.inventory.AromaDistillerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AromaDistillerScreen extends AbstractContainerScreen<AromaDistillerMenu> {

    private static final ResourceLocation TEX =
            new ResourceLocation("minecraft", "textures/gui/container/furnace.png");

    public AromaDistillerScreen(AromaDistillerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(TEX, x, y, 0, 0, imageWidth, imageHeight);
        if (menu.getMaxProgress() > 0) {
            int progress = menu.getProgress() * 24 / menu.getMaxProgress();
            graphics.blit(TEX, x + 79, y + 34, 176, 14, progress + 1, 16);
        }
        if (menu.getFuel() > 0) {
            int fuel = menu.getFuel() * 13 / 160;
            graphics.blit(TEX, x + 56, y + 36 + 12 - fuel, 176, 12 - fuel, 14, fuel + 1);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        CocoJennaUi.drawHeader(graphics, font, leftPos, topPos, imageWidth, 18, title);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
