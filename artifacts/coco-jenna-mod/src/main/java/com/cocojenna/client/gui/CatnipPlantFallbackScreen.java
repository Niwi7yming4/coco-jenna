package com.cocojenna.client.gui;

import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.WebUiActionPacket;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** 貓薄荷種植節奏小遊戲（空格／按鈕在綠環內點擊）. */
public class CatnipPlantFallbackScreen extends Screen {

    private static final int MAX_ROUNDS = 5;

    private final long seed;
    private final String sessionId;
    private int round;
    private int hits;
    private int misses;
    private float ring = 0.05f;
    private boolean growing = true;
    private boolean done;

    public CatnipPlantFallbackScreen(long seed) {
        super(Component.translatable("undercat.cocojenna.plant_title"));
        this.seed = seed;
        this.sessionId = "catnip_fb_" + System.nanoTime();
    }

    public static void open(long seed) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new CatnipPlantFallbackScreen(seed));
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(Component.translatable("undercat.cocojenna.plant_tap"),
                        b -> onTap())
                .bounds(width / 2 - 60, height / 2 + 50, 120, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> finish(false))
                .bounds(width / 2 - 50, height / 2 + 78, 100, 20).build());
    }

    @Override
    public void tick() {
        if (done) return;
        if (growing) {
            ring += 0.012f;
            if (ring >= 1f) {
                ring = 1f;
                growing = false;
            }
        }
    }

    private void onTap() {
        if (done) return;
        round++;
        if (ring > 0.55f && ring < 0.92f) hits++;
        else misses++;
        if (round >= MAX_ROUNDS) finish(true);
        else {
            ring = 0.05f;
            growing = true;
        }
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (!done && key == 32) {
            onTap();
            return true;
        }
        return super.keyPressed(key, scan, mods);
    }

    private void finish(boolean complete) {
        if (done) return;
        done = true;
        JsonObject msg = new JsonObject();
        msg.addProperty("action", complete ? "plant_complete" : "plant_abandon");
        if (complete) {
            msg.addProperty("hits", hits);
            msg.addProperty("misses", misses);
        }
        ModNetwork.CHANNEL.sendToServer(new WebUiActionPacket("catnip_plant", sessionId, msg.toString()));
        onClose();
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int cx = width / 2;
        int cy = height / 2 - 10;
        g.drawCenteredString(font, title, cx, cy - 70, CocoJennaUi.COL_INK);
        g.drawCenteredString(font,
                Component.translatable("undercat.cocojenna.plant_score", hits, misses, round, MAX_ROUNDS),
                cx, cy - 48, CocoJennaUi.COL_INK_SOFT);
        int radius = (int) (52 * ring);
        g.fill(cx - 58, cy - 58, cx + 58, cy + 58, 0x440D180D);
        g.renderOutline(cx - radius, cy - radius, radius * 2, radius * 2, 0xFF6FCF6F);
        g.fill(cx - 14, cy - 14, cx + 14, cy + 14, 0xFF3D6B3D);
        boolean sweet = ring > 0.55f && ring < 0.92f;
        g.drawCenteredString(font,
                Component.translatable(sweet ? "undercat.cocojenna.plant_sweet" : "undercat.cocojenna.plant_wait"),
                cx, cy + 72, sweet ? 0xFF88FF88 : CocoJennaUi.COL_INK_SOFT);
        super.render(g, mx, my, partial);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
