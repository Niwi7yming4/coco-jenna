package com.cocojenna.client.gui;

import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.WebUiActionPacket;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/** 地下貓域 Hub — 原版 GUI. */
public class UndercatHubFallbackScreen extends Screen {

    @Nullable
    private static UndercatHubFallbackScreen current;

    private JsonObject state;
    private final String sessionId;

    public UndercatHubFallbackScreen(JsonObject state) {
        super(Component.translatable("undercat.cocojenna.hub_title"));
        this.state = state;
        this.sessionId = "undercat_fb_" + System.nanoTime();
    }

    public static void open(JsonObject state) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new UndercatHubFallbackScreen(state));
    }

    @Nullable
    public static UndercatHubFallbackScreen current() {
        return current;
    }

    public void updateState(JsonObject next) {
        this.state = next;
        init();
    }

    @Override
    protected void init() {
        current = this;
        clearWidgets();
        int cx = width / 2;
        int y = height - 92;
        int bw = 148;
        int gap = 6;

        if (state.get("chapter").getAsInt() == 0) {
            addBtn(cx - bw / 2, y, bw, Component.translatable("undercat.cocojenna.hub.accept"),
                    () -> action("accept_quest", null));
            y += 22 + gap;
        } else {
            if (!state.get("dailyDone").getAsBoolean() && state.get("dailyQuest").getAsInt() >= 0) {
                addBtn(cx - bw / 2, y, bw, Component.translatable("undercat.cocojenna.hub.daily"),
                        () -> action("complete_daily", null));
                y += 22 + gap;
            }
            if (state.get("chapter").getAsInt() >= 2) {
                addBtn(cx - bw / 2, y, bw, Component.translatable("undercat.cocojenna.hub.plant"),
                        () -> action("plant_catnip", null));
                y += 22 + gap;
            }
            if (state.get("chapter").getAsInt() >= 2 && state.get("stage").getAsInt() >= 3) {
                addBtn(cx - bw / 2, y, bw, Component.translatable("undercat.cocojenna.hub.voyage"),
                        () -> action("start_voyage", null));
                y += 22 + gap;
            }
            if (state.get("chapter").getAsInt() >= 3 && state.get("arenaBet").getAsInt() <= 0) {
                addBtn(cx - bw / 2, y, bw, Component.translatable("undercat.cocojenna.hub.arena_bet"),
                        () -> {
                            JsonObject p = new JsonObject();
                            p.addProperty("amount", 20);
                            action("arena_bet", p);
                        });
                y += 22 + gap;
            }
            if (state.get("chapter").getAsInt() == 5 && state.get("ending").getAsInt() == 0) {
                int half = 72;
                addBtn(cx - half - gap / 2, y, half, Component.translatable("undercat.cocojenna.hub.ending_coco"),
                        () -> chooseEnding(1));
                addBtn(cx + gap / 2, y, half, Component.translatable("undercat.cocojenna.hub.ending_jenna"),
                        () -> chooseEnding(2));
                y += 22 + gap;
                addBtn(cx - half - gap / 2, y, half, Component.translatable("undercat.cocojenna.hub.ending_symbiosis"),
                        () -> chooseEnding(3));
                addBtn(cx + gap / 2, y, half, Component.translatable("undercat.cocojenna.hub.ending_sacrifice"),
                        () -> chooseEnding(4));
                y += 22 + gap;
                addBtn(cx - bw / 2, y, bw, Component.translatable("undercat.cocojenna.hub.ending_secret"),
                        () -> chooseEnding(5));
                y += 22 + gap;
            }
            JsonArray regions = state.getAsJsonArray("regionList");
            for (int i = 0; i < regions.size() && i < 3; i++) {
                var r = regions.get(i).getAsJsonObject();
                if (!r.get("unlocked").getAsBoolean()) continue;
                String id = r.get("id").getAsString();
                addBtn(cx - bw / 2, y, bw,
                        Component.translatable("undercat.cocojenna.hub.teleport",
                                Component.translatable("undercat.cocojenna.region." + id.toLowerCase())),
                        () -> {
                            JsonObject p = new JsonObject();
                            p.addProperty("region", id);
                            action("teleport_region", p);
                        });
                y += 22 + gap;
            }
        }

        addBtn(cx - 50, height - 28, 100, Component.translatable("gui.done"), this::onClose);
    }

    private void addBtn(int x, int y, int w, Component label, Runnable onPress) {
        addRenderableWidget(new ParchmentButton(x, y, w, 18, label, b -> onPress.run()));
    }

    private void chooseEnding(int ending) {
        JsonObject p = new JsonObject();
        p.addProperty("ending", ending);
        action("choose_ending", p);
    }

    private void action(String name, @Nullable JsonObject payload) {
        JsonObject msg = payload != null ? payload : new JsonObject();
        msg.addProperty("action", name);
        ModNetwork.CHANNEL.sendToServer(new WebUiActionPacket("undercat", sessionId, msg.toString()));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int x = width / 2 - 150;
        int y = 28;
        CocoJennaUi.drawPanel(g, x, y, 300, height - 120);
        g.drawCenteredString(font, title, width / 2, y + 10, CocoJennaUi.COL_INK);
        int ty = y + 28;
        g.drawString(font, Component.translatable("undercat.cocojenna.hub.chapter",
                state.get("chapter").getAsInt(), state.get("stage").getAsInt()), x + 12, ty, CocoJennaUi.COL_INK);
        ty += 14;
        g.drawString(font, Component.translatable("undercat.cocojenna.hub.coins",
                state.get("shadowCoins").getAsInt()), x + 12, ty, CocoJennaUi.COL_ACCENT);
        ty += 14;
        if (state.get("chapter").getAsInt() > 0) {
            g.drawString(font, Component.translatable("undercat.cocojenna.hub.commissions",
                    state.get("commissionCount").getAsInt()), x + 12, ty, CocoJennaUi.COL_INK_SOFT);
            ty += 14;
            g.drawString(font, Component.translatable("undercat.cocojenna.hub.leeches",
                    state.get("leechKills").getAsInt()), x + 12, ty, CocoJennaUi.COL_INK_SOFT);
            ty += 18;
            JsonArray factions = state.getAsJsonArray("factions");
            for (int i = 0; i < factions.size() && i < 4; i++) {
                var f = factions.get(i).getAsJsonObject();
                g.drawString(font, factionLabel(f.get("id").getAsString()) + " "
                                + f.get("rep").getAsInt(),
                        x + 12, ty, CocoJennaUi.COL_INK_SOFT);
                ty += 12;
            }
        } else {
            g.drawString(font, Component.translatable("undercat.cocojenna.hub.intro"), x + 12, ty, CocoJennaUi.COL_INK_SOFT);
        }
        super.render(g, mx, my, partial);
    }

    private static String factionLabel(String id) {
        return switch (id) {
            case "CARDBOARD_KINGDOM" -> "紙箱";
            case "SMUGGLER_UNION" -> "走私";
            case "ARENA_BROTHERHOOD" -> "競技";
            case "SERVANT_CULT" -> "貓奴";
            case "SILENT_SISTERHOOD" -> "修女";
            default -> id;
        };
    }

    @Override
    public void onClose() {
        current = null;
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
