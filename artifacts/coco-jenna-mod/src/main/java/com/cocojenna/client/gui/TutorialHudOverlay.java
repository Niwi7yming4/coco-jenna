package com.cocojenna.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/** 螢幕邊角輕量入門提示（類似進度浮動文字）. */
public final class TutorialHudOverlay {

    public static final TutorialHudOverlay INSTANCE = new TutorialHudOverlay();

    private Component currentHint = Component.empty();
    private long showUntilMs;

    private TutorialHudOverlay() {}

    public void show(Component hint) {
        currentHint = hint;
        showUntilMs = System.currentTimeMillis() + 6000L;
    }

    public void render(GuiGraphics graphics, int screenWidth, int screenHeight) {
        if (System.currentTimeMillis() > showUntilMs || currentHint.getString().isEmpty()) return;

        long remaining = showUntilMs - System.currentTimeMillis();
        float alpha = Mth.clamp(remaining / 1500f, 0f, 1f);
        int color = ((int) (alpha * 255) << 24) | 0xFFFFFF;

        int x = screenWidth - 12;
        int y = screenHeight / 4;
        int textWidth = Minecraft.getInstance().font.width(currentHint);
        graphics.drawString(Minecraft.getInstance().font, currentHint, x - textWidth, y, color, true);
    }
}
