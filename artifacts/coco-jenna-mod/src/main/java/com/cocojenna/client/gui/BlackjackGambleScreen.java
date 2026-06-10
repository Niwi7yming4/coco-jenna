package com.cocojenna.client.gui;

import com.cocojenna.gamble.BlackjackGambleMenu;
import com.cocojenna.network.AbyssRunActionPacket;
import com.cocojenna.network.BlackjackGamblePacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BlackjackGambleScreen extends AbstractContainerScreen<BlackjackGambleMenu> {

    public BlackjackGambleScreen(BlackjackGambleMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageWidth = 220;
        imageHeight = 180;
    }

    @Override
    protected void init() {
        super.init();
        int cx = leftPos + 12;
        int cy = topPos + 100;
        addRenderableWidget(new ParchmentButton(cx, cy, 60, 18,
                Component.translatable("blackjack.cocojenna.btn_start"),
                b -> ModNetwork.CHANNEL.sendToServer(
                        new BlackjackGamblePacket(BlackjackGambleMenu.BlackjackGambleAction.START))));
        addRenderableWidget(new ParchmentButton(cx + 68, cy, 60, 18,
                Component.translatable("blackjack.cocojenna.btn_hit"),
                b -> ModNetwork.CHANNEL.sendToServer(
                        new BlackjackGamblePacket(BlackjackGambleMenu.BlackjackGambleAction.HIT))));
        addRenderableWidget(new ParchmentButton(cx + 136, cy, 60, 18,
                Component.translatable("blackjack.cocojenna.btn_stand"),
                b -> ModNetwork.CHANNEL.sendToServer(
                        new BlackjackGamblePacket(BlackjackGambleMenu.BlackjackGambleAction.STAND))));
        addRenderableWidget(new ParchmentButton(cx + 34, cy + 24, 152, 18,
                Component.translatable("abyss.cocojenna.btn_enter"),
                b -> ModNetwork.CHANNEL.sendToServer(
                        new AbyssRunActionPacket(AbyssRunActionPacket.Action.START, 0))));
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mouseX, int mouseY) {
        CocoJennaUi.drawMachinePanel(g, font, leftPos, topPos, imageWidth, imageHeight, title);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        var data = menu.getSyncData();
        int playerHand = data.get(0);
        int dealerHand = data.get(1);
        int round = data.get(2);
        int pWins = data.get(3);
        int dWins = data.get(4);
        g.drawString(font, Component.translatable("blackjack.cocojenna.hand_player", playerHand),
                14, 36, CocoJennaUi.COL_INK, false);
        g.drawString(font, Component.translatable("blackjack.cocojenna.hand_dealer", dealerHand),
                14, 52, CocoJennaUi.COL_INK, false);
        g.drawString(font, Component.translatable("blackjack.cocojenna.score", round, pWins, dWins),
                14, 72, CocoJennaUi.COL_INK_SOFT, false);
        g.drawString(font, Component.translatable("blackjack.cocojenna.hint"),
                14, 88, CocoJennaUi.COL_INK_SOFT, false);
    }
}
