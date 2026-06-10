package com.cocojenna.client.gui;

import com.cocojenna.network.ModNetwork;
import com.cocojenna.sequence.PromotionCardCatalog;
import com.cocojenna.network.SelectPromotionCardPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/** 序列晉升三選一卡牌介面. */
public class SequencePromotionScreen extends Screen {

    private static final int CARD_W = 72;
    private static final int CARD_H = 96;

    private final int fromTier;
    private final String force;
    private final List<String> cards;
    private int hoveredCard = -1;

    public SequencePromotionScreen(int fromTier, String force, List<String> cards) {
        super(Component.translatable("promotion.cocojenna.title", fromTier - 1));
        this.fromTier = fromTier;
        this.force = force;
        this.cards = cards;
    }

    public static void open(int fromTier, String force, List<String> cards) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        mc.setScreen(new SequencePromotionScreen(fromTier, force, cards));
    }

    private int cardStartX() {
        int gap = 16;
        int total = cards.size() * CARD_W + (cards.size() - 1) * gap;
        return (width - total) / 2;
    }

    private int cardY() {
        return height / 2 - 4;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int left = cardStartX();
        int y = cardY();
        int gap = 16;

        g.drawCenteredString(font, title, width / 2, height / 2 - 72, 0xFFE8C4FF);
        g.drawCenteredString(font,
                Component.translatable("promotion.cocojenna.subtitle", fromTier, fromTier - 1),
                width / 2, height / 2 - 58, 0xFFE8C4FF);
        g.drawCenteredString(font,
                Component.translatable("promotion.cocojenna.force." + (force.isEmpty() ? "resonance" : force)),
                width / 2, height / 2 - 44, CocoJennaUi.COL_INK_SOFT);

        ResourceLocation back = GuiTextures.cardBack(fromTier);
        ResourceLocation front = GuiTextures.cardFront(force);
        hoveredCard = -1;

        for (int i = 0; i < cards.size(); i++) {
            int bx = left + i * (CARD_W + gap);
            boolean hovered = mx >= bx && mx < bx + CARD_W && my >= y && my < y + CARD_H + 20;
            if (hovered) hoveredCard = i;
            CocoJennaUi.drawCardHover(g, bx, y, CARD_W, CARD_H, hovered, false);
            g.blit(back, bx, y, 0, 0, CARD_W, CARD_H, CARD_W, CARD_H);
            g.blit(front, bx + 6, y + 8, 0, 0, CARD_W - 12, CARD_H - 16, CARD_W - 12, CARD_H - 16);
            g.drawCenteredString(font, cardLabel(cards.get(i)), bx + CARD_W / 2, y + CARD_H + 6, 0xFFE8E0FF);
        }

        g.drawCenteredString(font,
                Component.translatable("promotion.cocojenna.pick_hint"),
                width / 2, y + CARD_H + 28, CocoJennaUi.COL_INK_SOFT);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && hoveredCard >= 0) {
            ModNetwork.CHANNEL.sendToServer(new SelectPromotionCardPacket(hoveredCard));
            onClose();
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    private static Component cardLabel(String cardId) {
        return PromotionCardCatalog.displayName(cardId);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
