package com.cocojenna.client.gui;

import com.cocojenna.economy.CatnipQuality;
import com.cocojenna.item.CatnipItem;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SellCatnipPacket;
import com.cocojenna.shop.CatnipMarketMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CatnipMarketScreen extends AbstractContainerScreen<CatnipMarketMenu> {

    private int hoveredQuality = -1;
    private boolean hoveredStack;

    public CatnipMarketScreen(CatnipMarketMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageWidth = 220;
        imageHeight = 250;
        inventoryLabelY = imageHeight - 94;
    }

    private int rowY(int idx) {
        return topPos + 30 + idx * 44;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mouseX, int mouseY) {
        CocoJennaUi.drawMachinePanel(g, font, leftPos, topPos, imageWidth, imageHeight, title);
        g.drawString(font, Component.translatable("economy.cocojenna.catnip.day", menu.marketDay()), leftPos + 10, topPos + 16,
                CocoJennaUi.COL_INK_SOFT, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partial);
        hoveredQuality = -1;
        CatnipQuality[] qualities = CatnipQuality.values();
        for (int i = 0; i < qualities.length; i++) {
            CatnipQuality q = qualities[i];
            int x = leftPos + 10;
            int y = rowY(i);
            Component label = Component.translatable(CatnipItem.qualityKey(q))
                    .append(" · " + menu.price(q) + " ")
                    .append(Component.translatable("economy.cocojenna.catnip.coin_unit"));
            g.drawString(font, label, x, y, q.color(), false);
            boolean h1 = mouseX >= x && mouseX < x + 95 && mouseY >= y + 14 && mouseY < y + 32;
            boolean h2 = mouseX >= x + 100 && mouseX < x + 195 && mouseY >= y + 14 && mouseY < y + 32;
            if (h1 || h2) {
                hoveredQuality = i;
                hoveredStack = h2;
            }
            CocoJennaUi.drawButton(g, font, x, y + 14, 90, 18,
                    Component.translatable("economy.cocojenna.catnip.sell_one"), h1, true, false);
            CocoJennaUi.drawButton(g, font, x + 100, y + 14, 95, 18,
                    Component.translatable("economy.cocojenna.catnip.sell_stack"), h2, true, false);
        }
        renderTooltip(g, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredQuality >= 0) {
            ModNetwork.CHANNEL.sendToServer(new SellCatnipPacket(hoveredQuality, hoveredStack));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
