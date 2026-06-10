package com.cocojenna.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/** 羊皮紙風格按鈕，取代預設石頭材質。 */
public class ParchmentButton extends Button {

    private final boolean accent;

    public ParchmentButton(int x, int y, int w, int h, Component message, OnPress onPress) {
        this(x, y, w, h, message, onPress, false);
    }

    public ParchmentButton(int x, int y, int w, int h, Component message, OnPress onPress, boolean accent) {
        super(x, y, w, h, message, onPress, DEFAULT_NARRATION);
        this.accent = accent;
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
        CocoJennaUi.drawButton(g, Minecraft.getInstance().font,
                getX(), getY(), getWidth(), getHeight(),
                getMessage(), isHovered(), active, accent);
    }
}
