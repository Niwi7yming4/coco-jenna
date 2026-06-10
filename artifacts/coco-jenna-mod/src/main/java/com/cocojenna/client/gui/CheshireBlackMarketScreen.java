package com.cocojenna.client.gui;

import com.cocojenna.network.BuyCheshireMarketPacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.shop.CheshireBlackMarketMenu;
import com.cocojenna.shop.CheshireBlackMarketOffers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CheshireBlackMarketScreen extends AbstractContainerScreen<CheshireBlackMarketMenu> {

    private int hoveredOffer = -1;

    public CheshireBlackMarketScreen(CheshireBlackMarketMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageWidth = 220;
        imageHeight = 240;
        inventoryLabelY = imageHeight - 94;
    }

    private int offerX() {
        return leftPos + 10;
    }

    private int offerY(int idx) {
        return topPos + 26 + idx * 20;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mouseX, int mouseY) {
        CocoJennaUi.drawMachinePanel(g, font, leftPos, topPos, imageWidth, imageHeight, title);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partial);
        hoveredOffer = -1;
        for (int i = 0; i < CheshireBlackMarketOffers.OFFERS.size(); i++) {
            var offer = CheshireBlackMarketOffers.OFFERS.get(i);
            int x = offerX();
            int y = offerY(i);
            boolean hovered = mouseX >= x && mouseX < x + imageWidth - 20
                    && mouseY >= y && mouseY < y + 18;
            if (hovered) hoveredOffer = i;
            Component label = Component.translatable("item.cocojenna.ryokatana_" + offer.ryokatanaId())
                    .append(Component.translatable("shop.cocojenna.remnant_price", offer.remnantCost()));
            CocoJennaUi.drawButton(g, font, x, y, imageWidth - 20, 18, label, hovered, true, false);
        }
        renderTooltip(g, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredOffer >= 0) {
            ModNetwork.CHANNEL.sendToServer(new BuyCheshireMarketPacket(hoveredOffer));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
