package com.cocojenna.client.gui;

import com.cocojenna.guide.AlphaExchangeManager;
import com.cocojenna.network.AlphaExchangePacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** 觀測者・阿爾法兌換所 UI. */
public class AlphaExchangeScreen extends Screen {

    private static final int W = 280;

    public AlphaExchangeScreen() {
        super(Component.translatable("alpha.cocojenna.exchange.title"));
    }

    public static void open() {
        net.minecraft.client.Minecraft.getInstance().setScreen(new AlphaExchangeScreen());
    }

    @Override
    protected void init() {
        clearWidgets();
        int left = (width - W) / 2;
        int top = 48;
        int row = 0;
        for (AlphaExchangeManager.Offer offer : AlphaExchangeManager.Offer.values()) {
            int y = top + row * 36;
            String id = offer.id();
            int cost = offer.shardCost();
            addRenderableWidget(new ParchmentButton(left, y, W, 30,
                    Component.translatable("alpha.cocojenna.offer_line." + id, cost),
                    b -> ModNetwork.CHANNEL.sendToServer(new AlphaExchangePacket(id))));
            row++;
        }
        addRenderableWidget(new ParchmentButton(left + W / 2 - 40, height - 34, 80, 18,
                Component.translatable("gui.cocojenna.close"), b -> onClose()));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int left = (width - W) / 2;
        CocoJennaUi.drawPanel(g, left - 10, 32, W + 20, 200);
        g.drawCenteredString(font, title, width / 2, 40, CocoJennaUi.COL_INK);
        g.drawCenteredString(font, Component.translatable("alpha.cocojenna.exchange.subtitle"),
                width / 2, 54, CocoJennaUi.COL_INK_SOFT);
        super.render(g, mx, my, partial);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
