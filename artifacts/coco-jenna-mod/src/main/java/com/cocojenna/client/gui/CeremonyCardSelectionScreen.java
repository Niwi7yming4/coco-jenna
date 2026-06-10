package com.cocojenna.client.gui;

import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SelectPromotionCardPacket;
import com.cocojenna.sequence.PromotionCardCatalog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import java.util.List;

/**
 * 儀式第四階段：啟示 — 3D浮空卡牌選擇
 * 
 * 設計書 2.1 第四階段：
 * - 三張卡牌以半透明光牌形式浮現（非純GUI）
 * - 玩家左右移動視角可以「瀏覽」三張卡牌
 * - 卡牌隨著視角靠近而放大
 * - 玩家看向某一張卡牌並按下右鍵（或Alt+點擊）來確認選擇
 */
public class CeremonyCardSelectionScreen extends Screen {

    private static final int CARD_W = 96;
    private static final int CARD_H = 128;
    private static final float CARD_SPACING = 140f;

    private final int fromTier;
    private final String force;
    private final List<String> cards;
    private int hoveredCard = -1;
    private float[] cardRotations = new float[3];
    private float[] cardScales = new float[3];
    private float[] cardFloatOffsets = new float[3]; // 浮動動畫偏移
    private int animTicks = 0;
    private boolean cardChosen = false;
    private int chosenCard = -1;
    private int dissolveTicks = 0;
    private float flyProgress = 0f;
    private float chosenFlyX = 0f;
    private float chosenFlyY = 0f;

    // 視角控制
    private float viewAngle = 0f; // -1 to 1
    private float targetViewAngle = 0f;

    public CeremonyCardSelectionScreen(int fromTier, String force, List<String> cards) {
        super(Component.translatable("promotion.cocojenna.title", fromTier - 1));
        this.fromTier = fromTier;
        this.force = force;
        this.cards = cards;

        // 初始化浮動偏移
        for (int i = 0; i < 3; i++) {
            cardFloatOffsets[i] = (float) (Math.random() * Math.PI * 2);
            cardRotations[i] = 0f;
            cardScales[i] = 1.0f;
        }
    }

    public static void open(int fromTier, String force, List<String> cards) {
        var mc = Minecraft.getInstance();
        mc.setScreen(new CeremonyCardSelectionScreen(fromTier, force, cards));
    }

    @Override
    public void tick() {
        super.tick();
        animTicks++;

        // 平滑視角移動
        viewAngle += (targetViewAngle - viewAngle) * 0.1f;

        // 更新卡牌旋轉和浮動
        for (int i = 0; i < 3; i++) {
            float baseAngle = (float) Math.sin(animTicks * 0.02 + cardFloatOffsets[i]) * 0.05f;
            cardRotations[i] = baseAngle + (i - 1) * viewAngle * 0.3f;

            // 懸浮動畫
            cardFloatOffsets[i] += 0.02f;

            // 根據懸浮計算縮放（靠近中間的卡牌放大）
            float distFromCenter = Math.abs(i - 1.0f);
            cardScales[i] = 1.0f - distFromCenter * 0.15f;
        }

        // 卡牌消散動畫（選擇後）
        if (cardChosen) {
            dissolveTicks++;
            flyProgress = Math.min(1f, flyProgress + 0.06f);
            if (dissolveTicks > 28) {
                onClose();
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        // 滾輪切換焦點
        if (delta > 0) targetViewAngle = Math.max(-1, targetViewAngle - 0.3f);
        else if (delta < 0) targetViewAngle = Math.min(1, targetViewAngle + 0.3f);
        return true;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && hoveredCard >= 0 && !cardChosen) {
            // 確認選擇
            cardChosen = true;
            chosenCard = hoveredCard;
            dissolveTicks = 0;
            flyProgress = 0f;
            chosenFlyX = (float) width / 2;
            chosenFlyY = (float) height / 2 - 60;

            ModNetwork.CHANNEL.sendToServer(new SelectPromotionCardPacket(hoveredCard));
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 左右方向鍵切換
        if (keyCode == 262) { // 右
            targetViewAngle = Math.min(1, targetViewAngle + 0.3f);
            return true;
        }
        if (keyCode == 263) { // 左
            targetViewAngle = Math.max(-1, targetViewAngle - 0.3f);
            return true;
        }
        // Enter確認
        if ((keyCode == 257 || keyCode == 335) && hoveredCard >= 0 && !cardChosen) {
            cardChosen = true;
            chosenCard = hoveredCard;
            dissolveTicks = 0;
            ModNetwork.CHANNEL.sendToServer(new SelectPromotionCardPacket(hoveredCard));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);

        int centerX = width / 2;
        int centerY = height / 2 - 20;

        // 背景光暈
        renderBackgroundGlow(g, centerX, centerY);

        // 標題
        g.drawCenteredString(font, Component.translatable("promotion.cocojenna.title", fromTier - 1),
            centerX, centerY - 96, 0xFFE8C4FF);
        g.drawCenteredString(font,
            Component.translatable("promotion.cocojenna.subtitle", fromTier, fromTier - 1),
            centerX, centerY - 82, 0xFFE8C4FF);
        g.drawCenteredString(font,
            Component.translatable("promotion.cocojenna.force." + (force.isEmpty() ? "resonance" : force)),
            centerX, centerY - 68, CocoJennaUi.COL_INK_SOFT);

        // 渲染三張浮空卡牌（3D效果）
        int hoveredIdx = -1;
        for (int i = 0; i < cards.size(); i++) {
            float baseCx = centerX + (i - 1) * CARD_SPACING + viewAngle * (i == 0 ? -30 : i == 2 ? 30 : 0);
            float baseCy = centerY + (float) Math.sin(animTicks * 0.05 + cardFloatOffsets[i]) * 8;
            float cx = baseCx;
            float cy = baseCy;
            if (cardChosen && i == chosenCard) {
                cx = baseCx + (chosenFlyX - baseCx) * flyProgress;
                cy = baseCy + (chosenFlyY - baseCy) * flyProgress;
            }

            float scale = cardChosen && i == chosenCard ? 1.2f + flyProgress * 0.5f : cardScales[i];

            // 計算滑鼠懸浮
            int cardX = (int)(cx - CARD_W * scale / 2);
            int cardY = (int)(cy - CARD_H * scale / 2);
            int cardW = (int)(CARD_W * scale);
            int cardH = (int)(CARD_H * scale);

            boolean hovered = mx >= cardX && mx < cardX + cardW && my >= cardY && my < cardY + cardH;
            if (hovered) hoveredIdx = i;

            // 渲染卡牌
            renderFloatingCard(g, cx, cy, i, scale, hovered, cardChosen && i == chosenCard);
            if (cardChosen && i == chosenCard) {
                renderFlyParticles(g, (int) cx, (int) cy, scale);
            }
        }
        hoveredCard = hoveredIdx;

        // 底部提示
        if (!cardChosen) {
            g.drawCenteredString(font,
                Component.translatable("promotion.cocojenna.pick_hint_3d"),
                centerX, centerY + CARD_H / 2 + 40, CocoJennaUi.COL_INK_SOFT);
        }

        super.render(g, mx, my, partial);
    }

    private void renderFlyParticles(GuiGraphics g, int cx, int cy, float scale) {
        int color = switch (force) {
            case "resonance" -> 0xCCFFD700;
            case "shadow" -> 0xCC9B30FF;
            case "chaos" -> 0xCCFF69B4;
            default -> 0xCCFFFFFF;
        };
        for (int p = 0; p < 8; p++) {
            float ang = (animTicks * 0.2f + p * 0.78f);
            int px = cx + (int) (Math.cos(ang) * (24 + scale * 20));
            int py = cy + (int) (Math.sin(ang) * (16 + scale * 12));
            g.fill(px, py, px + 3, py + 3, color);
        }
    }

    private void renderBackgroundGlow(GuiGraphics g, int cx, int cy) {
        // 根據途徑渲染背景光暈
        int color = switch (force) {
            case "resonance" -> 0x44FFD700;
            case "shadow"    -> 0x449B30FF;
            case "chaos"     -> 0x44FF69B4;
            default          -> 0x44FFFFFF;
        };
        int r = Math.min(width, height) / 3;
        g.fill(cx - r, cy - r, cx + r, cy + r, color);
    }

    private void renderFloatingCard(GuiGraphics g, float cx, float cy, int index, float scale, boolean hovered, boolean chosen) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, chosen ? Math.max(0, 1.0f - dissolveTicks / 20.0f) : 1.0f);

        int w = (int)(CARD_W * scale);
        int h = (int)(CARD_H * scale);
        int x = (int)(cx - w / 2f);
        int y = (int)(cy - h / 2f);

        // 卡牌影子
        g.fill(x + 4, y + 4, x + w + 4, y + h + 4, 0x44000000);

        // 卡牌背景
        int bgColor = chosen ? 0x88FFD700 :
            hovered ? 0xCCFFFFFF : 0xAA000000;
        int borderColor = chosen ? 0xFFFFD700 :
            hovered ? 0xCC66CCFF : 0x66443322;

        g.fill(x - 2, y - 2, x + w + 2, y + h + 2, borderColor);
        g.fill(x, y, x + w, y + h, bgColor);

        // 卡牌正面紋理
        ResourceLocation frontTex = new ResourceLocation(
            "cocojenna:textures/gui/card_front_" + (force.isEmpty() ? "resonance" : force) + ".png");
        g.blit(frontTex, x + 4, y + 4, w - 8, h - 8, 0, 0, 64, 64, 64, 64);

        // 卡牌名稱
        Component name = PromotionCardCatalog.displayName(cards.get(index));
        int nameColor = chosen ? 0xFFFFD700 : hovered ? 0xFFFFFFFF : 0xFFE8E0FF;
        g.drawCenteredString(font, name, (int)cx, y + h + 6, nameColor);

        // 光暈（懸浮或選擇時）
        if (hovered || chosen) {
            int glowColor = chosen ? 0x44FFD700 : 0x2266CCFF;
            int glowR = (int)(Math.max(w, h) * 0.6f);
            g.fill(x - glowR / 2 + w / 2, y - glowR / 2 + h / 2, x + glowR / 2 + w / 2, y + glowR / 2 + h / 2, glowColor);
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // 儀式進行中不能跳過
    }
}