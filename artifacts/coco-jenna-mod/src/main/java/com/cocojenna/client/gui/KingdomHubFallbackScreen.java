package com.cocojenna.client.gui;

import com.cocojenna.endgame.kingdom.MpsTask;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.MpsSchedulePacket;
import com.cocojenna.network.PlayerActionPacket;
import com.cocojenna.network.WebUiActionPacket;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/** 王國 Hub 原版 GUI — 含 MPS 月曆雙向同步與 NPC 互動. */
public class KingdomHubFallbackScreen extends Screen {

    @Nullable
    private static KingdomHubFallbackScreen current;

    private JsonObject state;
    private int tab;
    private int selectedNpc;
    private int hoveredMpsDay = -1;
    private int hoveredMpsBlock = -1;
    private final String sessionId = "kingdom";

    public KingdomHubFallbackScreen(JsonObject state) {
        super(Component.translatable("kingdom.cocojenna.hub.title"));
        this.state = state;
    }

    public static void open(JsonObject state) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new KingdomHubFallbackScreen(state));
    }

    @Nullable
    public static KingdomHubFallbackScreen current() {
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
        int y = 40;
        for (int i = 0; i < 4; i++) {
            int t = i;
            addRenderableWidget(new ParchmentButton(cx - 160 + i * 82, y, 78, 16,
                    Component.translatable("kingdom.cocojenna.tab." + i),
                    b -> { tab = t; init(); }));
        }
        if (tab == 1) {
            addRenderableWidget(new ParchmentButton(cx - 160, height - 36, 100, 18,
                    Component.translatable("gui.cocojenna.kingdom_terminal.mps_preset"),
                    b -> ModNetwork.CHANNEL.sendToServer(
                            new MpsSchedulePacket(MpsSchedulePacket.APPLY_PRESET, 0, 0, ""))));
            addRenderableWidget(new ParchmentButton(cx + 60, height - 36, 100, 18,
                    Component.translatable("gui.cocojenna.kingdom_terminal.mps_run"),
                    b -> ModNetwork.CHANNEL.sendToServer(
                            new MpsSchedulePacket(MpsSchedulePacket.RUN_DAY, 0, 0, ""))));
        }
        if (tab == 3) {
            JsonArray npcs = state.getAsJsonArray("npcs");
            if (!npcs.isEmpty()) {
                selectedNpc = Math.min(selectedNpc, npcs.size() - 1);
                addRenderableWidget(new ParchmentButton(cx - 160, height - 58, 50, 16,
                        Component.literal("◀"),
                        b -> { selectedNpc = (selectedNpc + npcs.size() - 1) % npcs.size(); init(); }));
                addRenderableWidget(new ParchmentButton(cx + 110, height - 58, 50, 16,
                        Component.literal("▶"),
                        b -> { selectedNpc = (selectedNpc + 1) % npcs.size(); init(); }));
                String npcId = npcs.get(selectedNpc).getAsJsonObject().get("id").getAsString();
                addRenderableWidget(new ParchmentButton(cx - 160, height - 36, 100, 18,
                        Component.translatable("kingdom.cocojenna.hub.gift"),
                        b -> hubAction("gift_npc", npcId)));
                addRenderableWidget(new ParchmentButton(cx - 50, height - 36, 100, 18,
                        Component.translatable("kingdom.cocojenna.hub.recruit"),
                        b -> hubAction("recruit_npc", npcId)));
                addRenderableWidget(new ParchmentButton(cx + 60, height - 36, 100, 18,
                        Component.translatable("kingdom.cocojenna.hub.story"),
                        b -> hubAction("read_story", npcId)));
            }
        }
        if (tab != 3) {
            addRenderableWidget(new ParchmentButton(cx - 70, height - 36, 140, 18,
                    Component.translatable("gui.cocojenna.kingdom_terminal.title"),
                    b -> ModNetwork.CHANNEL.sendToServer(
                            new PlayerActionPacket(PlayerActionPacket.Action.OPEN_KINGDOM_LEGACY))));
        }
    }

    private void hubAction(String action, String npcId) {
        JsonObject msg = new JsonObject();
        msg.addProperty("action", action);
        msg.addProperty("npc", npcId);
        ModNetwork.CHANNEL.sendToServer(new WebUiActionPacket("kingdom", sessionId, msg.toString()));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        g.drawCenteredString(font, title, width / 2, 16, CocoJennaUi.COL_INK);
        int y = 68;
        g.drawCenteredString(font, Component.translatable("kingdom.cocojenna.hub.stats",
                state.get("prosperity").getAsInt(), state.get("happiness").getAsInt()), width / 2, y, CocoJennaUi.COL_INK_SOFT);
        y += 20;
        switch (tab) {
            case 0 -> renderRecruit(g, y);
            case 1 -> renderMps(g, y, mx, my);
            case 2 -> renderFestival(g, y);
            case 3 -> renderNpc(g, y);
        }
        super.render(g, mx, my, partial);
    }

    private void renderRecruit(GuiGraphics g, int y) {
        JsonArray jobs = state.getAsJsonArray("jobs");
        for (int i = 0; i < jobs.size() && i < 6; i++) {
            var j = jobs.get(i).getAsJsonObject();
            g.drawString(font, j.get("zh").getAsString() + " " + j.get("assigned").getAsInt()
                    + "/" + j.get("cap").getAsInt(), width / 2 - 100, y, CocoJennaUi.COL_INK_SOFT);
            y += 12;
        }
    }

    private void renderMps(GuiGraphics g, int y, int mx, int my) {
        hoveredMpsDay = -1;
        hoveredMpsBlock = -1;
        int mpsDay = state.get("mpsDay").getAsInt();
        g.drawString(font, Component.translatable("gui.cocojenna.kingdom_terminal.mps_title",
                        mpsDay + 1, state.get("festivalDay").getAsInt(), state.get("festivalProgress").getAsInt()),
                width / 2 - 150, y, CocoJennaUi.COL_INK);
        y += 16;
        JsonArray schedule = state.getAsJsonArray("schedule");
        int gridX = width / 2 - 150;
        int cellW = 40;
        int cellH = 16;
        String[] blocks = {"morning", "afternoon", "dusk", "midnight"};
        for (int b = 0; b < 4; b++) {
            g.drawString(font, Component.translatable("kingdom.cocojenna.mps.block." + blocks[b]),
                    gridX, y + 12 + b * (cellH + 2), CocoJennaUi.COL_INK_SOFT);
        }
        for (int d = 0; d < 7; d++) {
            int dx = gridX + 48 + d * (cellW + 2);
            boolean today = d == mpsDay;
            g.drawString(font, Component.translatable("gui.cocojenna.kingdom_terminal.mps_day", d + 1),
                    dx + 2, y, today ? CocoJennaUi.COL_ACCENT : CocoJennaUi.COL_INK_SOFT);
            JsonArray day = schedule.get(d).getAsJsonArray();
            for (int b = 0; b < 4; b++) {
                int cy = y + 12 + b * (cellH + 2);
                boolean hov = mx >= dx && mx < dx + cellW && my >= cy && my < cy + cellH;
                if (hov) {
                    hoveredMpsDay = d;
                    hoveredMpsBlock = b;
                }
                int bg = today ? 0xFFE8D8B0 : 0xFFD8C8A0;
                if (hov) bg = 0xFFFFF0D8;
                g.fill(dx, cy, dx + cellW, cy + cellH, CocoJennaUi.COL_FRAME);
                g.fill(dx + 1, cy + 1, dx + cellW - 1, cy + cellH - 1, bg);
                String taskId = day.get(b).getAsString();
                String label = MpsTask.byId(taskId).label().getString();
                if (font.width(label) > cellW - 4) {
                    label = label.substring(0, Math.min(3, label.length())) + "…";
                }
                g.drawString(font, label, dx + 3, cy + 4, CocoJennaUi.COL_INK);
            }
        }
        g.drawString(font, Component.translatable("gui.cocojenna.kingdom_terminal.mps_hint"),
                width / 2 - 150, y + 88, CocoJennaUi.COL_INK_SOFT);
    }

    private void renderFestival(GuiGraphics g, int y) {
        g.drawString(font, Component.translatable("kingdom.cocojenna.festival_progress",
                state.get("festivalProgress").getAsInt(), state.get("festivalDay").getAsInt()),
                width / 2 - 100, y, CocoJennaUi.COL_ACCENT);
    }

    private void renderNpc(GuiGraphics g, int y) {
        JsonArray npcs = state.getAsJsonArray("npcs");
        for (int i = 0; i < npcs.size(); i++) {
            var n = npcs.get(i).getAsJsonObject();
            int color = i == selectedNpc ? CocoJennaUi.COL_ACCENT : CocoJennaUi.COL_INK_SOFT;
            String recruited = n.get("recruited").getAsBoolean() ? "✓" : "·";
            g.drawString(font, recruited + " " + n.get("name").getAsString()
                            + " ♥" + n.get("favor").getAsInt()
                            + " Ch." + n.get("story").getAsInt(),
                    width / 2 - 100, y, color);
            y += 12;
        }
        if (!npcs.isEmpty()) {
            g.drawString(font, Component.translatable("kingdom.cocojenna.hub.npc_hint"),
                    width / 2 - 100, height - 72, CocoJennaUi.COL_INK_SOFT);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (tab == 1 && button == 0 && hoveredMpsDay >= 0 && hoveredMpsBlock >= 0) {
            ModNetwork.CHANNEL.sendToServer(new MpsSchedulePacket(
                    MpsSchedulePacket.CYCLE_CELL, hoveredMpsDay, hoveredMpsBlock, ""));
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
