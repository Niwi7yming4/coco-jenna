package com.cocojenna.client.gui;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.BuyReputationShopPacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.shop.ReputationShopMenu;
import com.cocojenna.shop.ReputationShopOffers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ReputationShopScreen extends AbstractContainerScreen<ReputationShopMenu> {

    private int hoveredOffer = -1;

    public ReputationShopScreen(ReputationShopMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageWidth = 240;
        imageHeight = 260;
        inventoryLabelY = imageHeight - 94;
    }

    private int offerY(int idx) {
        return topPos + 24 + idx * 20;
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
        BondData bond = minecraft != null && minecraft.player != null
                ? ModCapabilities.getOrDefault(minecraft.player) : null;
        for (int i = 0; i < ReputationShopOffers.OFFERS.size(); i++) {
            var offer = ReputationShopOffers.OFFERS.get(i);
            int x = leftPos + 8;
            int y = offerY(i);
            boolean unlocked = bond != null && bond.getReputation(offer.region()) >= offer.repRequired();
            boolean owned = offer.oneTime() && bond != null && bond.hasPurchasedRepOffer(offer.id());
            boolean hovered = mouseX >= x && mouseX < x + imageWidth - 16 && mouseY >= y && mouseY < y + 18;
            if (hovered && unlocked && !owned) hoveredOffer = i;
            Component name = offer.kind() == ReputationShopOffers.Kind.RYOKATANA
                    ? Component.translatable("item.cocojenna.ryokatana_" + offer.itemKey())
                    : Component.translatable("item." + offer.itemKey().replace(':', '.'));
            String suffix = unlocked
                    ? (offer.coinCost() > 0 ? " · " + offer.coinCost() : (offer.oneTime() ? " · ★" : ""))
                    : " · 🔒" + offer.repRequired();
            Component label = name.copy().append(suffix);
            CocoJennaUi.drawButton(g, font, x, y, imageWidth - 16, 18, label, hovered, unlocked && !owned, owned);
        }
        renderTooltip(g, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredOffer >= 0) {
            ModNetwork.CHANNEL.sendToServer(new BuyReputationShopPacket(hoveredOffer));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
