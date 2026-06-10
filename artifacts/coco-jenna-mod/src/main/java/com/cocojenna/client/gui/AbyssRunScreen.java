package com.cocojenna.client.gui;

import com.cocojenna.network.AbyssRunActionPacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenAbyssRunPacket;
import com.cocojenna.sequence.PromotionCardCatalog;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** 深淵牌局戰場 UI. */
public class AbyssRunScreen extends Screen {

    private static final int CARD_W = 64;
    private static final int CARD_H = 88;

    private final OpenAbyssRunPacket state;
    private int hovered = -1;

    public AbyssRunScreen(OpenAbyssRunPacket state) {
        super(Component.translatable("gui.cocojenna.abyss.title"));
        this.state = state;
    }

    public static void open(OpenAbyssRunPacket state) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new AbyssRunScreen(state));
    }

    @Override
    protected void init() {
        clearWidgets();
        int cx = width / 2;
        int by = height - 36;
        if (state.shopPending()) {
            String[] keys = {"abyss.cocojenna.shop_heal", "abyss.cocojenna.shop_remove", "abyss.cocojenna.shop_buy"};
            for (int i = 0; i < 3; i++) {
                int idx = i;
                addRenderableWidget(new ParchmentButton(cx - 110 + i * 76, by - 40, 68, 18,
                        Component.translatable(keys[i]),
                        b -> ModNetwork.CHANNEL.sendToServer(
                                new AbyssRunActionPacket(AbyssRunActionPacket.Action.SHOP_BUY, idx))));
            }
        } else if (state.rewardPending() >= 0) {
            for (int i = 0; i < 3; i++) {
                int idx = i;
                addRenderableWidget(new ParchmentButton(cx - 110 + i * 76, by - 40, 68, 18,
                        Component.translatable("abyss.cocojenna.reward", i + 1),
                        b -> ModNetwork.CHANNEL.sendToServer(
                                new AbyssRunActionPacket(AbyssRunActionPacket.Action.PICK_REWARD, idx))));
            }
        } else {
            addRenderableWidget(new ParchmentButton(cx - 60, by, 56, 18,
                    Component.translatable("abyss.cocojenna.end_turn"),
                    b -> ModNetwork.CHANNEL.sendToServer(
                            new AbyssRunActionPacket(AbyssRunActionPacket.Action.END_TURN, 0))));
            addRenderableWidget(new ParchmentButton(cx + 8, by, 56, 18,
                    Component.translatable("abyss.cocojenna.leave"),
                    b -> {
                        ModNetwork.CHANNEL.sendToServer(
                                new AbyssRunActionPacket(AbyssRunActionPacket.Action.LEAVE, 0));
                        onClose();
                    }));
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int cx = width / 2;
        g.drawCenteredString(font, title, cx, 16, CocoJennaUi.COL_INK);
        g.drawCenteredString(font,
                Component.translatable("abyss.cocojenna.floor", state.floor()),
                cx, 30, CocoJennaUi.COL_INK_SOFT);

        g.drawString(font, Component.translatable("abyss.cocojenna.player_hp", state.playerHp(), state.playerBlock()),
                20, 52, CocoJennaUi.COL_INK);
        g.drawString(font, Component.translatable("abyss.cocojenna.energy", state.energy()),
                20, 64, CocoJennaUi.COL_INK);
        g.drawString(font, Component.translatable("abyss.cocojenna.enemy_hp", state.enemyHp(), state.enemyBlock()),
                width - 140, 52, CocoJennaUi.COL_INK);
        g.drawString(font, Component.translatable("abyss.cocojenna.intent", state.intentOrd()),
                width - 140, 64, CocoJennaUi.COL_INK_SOFT);

        int y = height / 2 + 10;
        int startX = cx - (state.hand().size() * (CARD_W + 8)) / 2;
        hovered = -1;
        for (int i = 0; i < state.hand().size(); i++) {
            int x = startX + i * (CARD_W + 8);
            boolean hov = mx >= x && mx < x + CARD_W && my >= y && my < y + CARD_H;
            if (hov) hovered = i;
            CocoJennaUi.drawCardHover(g, x, y, CARD_W, CARD_H, hov, false);
            String card = state.hand().get(i);
            var def = PromotionCardCatalog.get(card);
            String force = def.map(d -> d.force()).orElse("resonance");
            g.blit(GuiTextures.cardBack(5), x, y, 0, 0, CARD_W, CARD_H, CARD_W, CARD_H);
            g.blit(GuiTextures.cardFront(force), x + 4, y + 6, 0, 0, CARD_W - 8, CARD_H - 12,
                    CARD_W - 8, CARD_H - 12);
            g.drawCenteredString(font, PromotionCardCatalog.displayName(card).getString(),
                    x + CARD_W / 2, y + CARD_H + 4, CocoJennaUi.COL_INK_SOFT);
        }
        super.render(g, mx, my, partial);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (state.rewardPending() < 0 && button == 0 && hovered >= 0 && state.playerTurn()) {
            ModNetwork.CHANNEL.sendToServer(
                    new AbyssRunActionPacket(AbyssRunActionPacket.Action.PLAY_CARD, hovered));
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
