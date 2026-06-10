package com.cocojenna.client.gui;

import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.WebUiActionPacket;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Random;

/** 簡易暗河航行（舵位 QTE）. */
public class RiverVoyageFallbackScreen extends Screen {

    private final long seed;
    private final String sessionId;
    private int round;
    private int correct;
    private int obstacleLane = 1;
    private long roundStart;

    public RiverVoyageFallbackScreen(long seed) {
        super(Component.translatable("undercat.cocojenna.voyage_title"));
        this.seed = seed;
        this.sessionId = "river_fb_" + System.nanoTime();
    }

    public static void open(long seed) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new RiverVoyageFallbackScreen(seed));
    }

    @Override
    protected void init() {
        round = 0;
        correct = 0;
        nextRound();
        int cy = height / 2 + 40;
        addRenderableWidget(Button.builder(Component.literal("◀ 左舷"), b -> choose(0))
                .bounds(width / 2 - 160, cy, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("直行"), b -> choose(1))
                .bounds(width / 2 - 50, cy, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("右舷 ▶"), b -> choose(2))
                .bounds(width / 2 + 60, cy, 100, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> sendResult("abandon", 0, 0))
                .bounds(width / 2 - 50, cy + 36, 100, 20).build());
    }

    private void nextRound() {
        Random rng = new Random(seed + round * 31L);
        obstacleLane = rng.nextInt(3);
        roundStart = System.currentTimeMillis();
        round++;
    }

    private void choose(int lane) {
        if (lane != obstacleLane) {
            sendResult("fail", round * 200, 20);
            return;
        }
        correct++;
        if (round >= 5) {
            sendResult("success", 1000, 60);
            return;
        }
        nextRound();
    }

    private void sendResult(String outcome, int distance, int hull) {
        JsonObject msg = new JsonObject();
        msg.addProperty("action", "river_complete");
        msg.addProperty("outcome", outcome);
        msg.addProperty("distance", distance);
        msg.addProperty("hull", hull);
        msg.addProperty("durationMs", (int) Math.max(5000, System.currentTimeMillis() - roundStart + round * 2000L));
        msg.addProperty("fallback", true);
        msg.add("events", new JsonArray());
        ModNetwork.CHANNEL.sendToServer(new WebUiActionPacket("river", sessionId, msg.toString()));
        onClose();
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        g.drawCenteredString(font, title, width / 2, height / 2 - 60, 0xFF_E8D4FF);
        g.drawCenteredString(font,
                Component.translatable("undercat.cocojenna.voyage_fallback_hint", round, correct),
                width / 2, height / 2 - 30, 0xFF_CCB8E8);
        String[] lanes = {"← 障礙", "  ↑  ", "障礙 →"};
        for (int i = 0; i < 3; i++) {
            int x = width / 2 - 80 + i * 80;
            boolean hit = i == obstacleLane;
            g.fill(x - 30, height / 2 - 10, x + 30, height / 2 + 20, hit ? 0x88_FF4466 : 0x44_332244);
            g.drawCenteredString(font, lanes[i], x, height / 2, 0xFF_FFFFFF);
        }
        super.render(g, mx, my, partial);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
