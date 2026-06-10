package com.cocojenna.client.gui;

import com.cocojenna.network.MemoryTheaterReplayPacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** 記憶劇場 — 選擇並重播已解鎖場景. */
public class MemoryTheaterScreen extends Screen {

    private static final int W = 300;
    private static final int ROW = 22;

    private final List<String> scenes;

    public MemoryTheaterScreen(List<String> scenes) {
        super(Component.translatable("memory_theater.cocojenna.title"));
        this.scenes = scenes;
    }

    public static void open(List<String> scenes) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new MemoryTheaterScreen(scenes));
    }

    @Override
    protected void init() {
        clearWidgets();
        int left = (width - W) / 2;
        int top = 56;
        for (int i = 0; i < scenes.size(); i++) {
            String sceneId = scenes.get(i);
            int y = top + i * ROW;
            addRenderableWidget(new ParchmentButton(left, y, W, 18,
                    Component.translatable("memory_theater.cocojenna.scene." + sceneId),
                    b -> {
                        ModNetwork.CHANNEL.sendToServer(new MemoryTheaterReplayPacket(sceneId));
                        onClose();
                    }));
        }
        addRenderableWidget(new ParchmentButton(left + W / 2 - 40, height - 34, 80, 18,
                Component.translatable("gui.cocojenna.close"),
                b -> onClose()));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int left = (width - W) / 2;
        CocoJennaUi.drawPanel(g, left - 12, 28, W + 24, Math.min(height - 60, 56 + scenes.size() * ROW + 16));
        g.drawCenteredString(font, title, width / 2, 36, CocoJennaUi.COL_INK);
        g.drawCenteredString(font, Component.translatable("memory_theater.cocojenna.subtitle"),
                width / 2, 48, CocoJennaUi.COL_INK_SOFT);
        super.render(g, mx, my, partial);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
