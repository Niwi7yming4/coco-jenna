package com.cocojenna.client.gui;

import com.cocojenna.capability.BondData;
import com.cocojenna.endgame.KingdomDecreeCatalog;
import com.cocojenna.endgame.BuildingBlueprintCatalog;
import com.cocojenna.endgame.kingdom.MpsTask;
import com.cocojenna.network.BuildingContributePacket;
import com.cocojenna.network.KingdomDecreeActionPacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.MpsSchedulePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** 王權大廳終端 — 王國狀態、法令、建造、月曆、繪本入口. */
public class KingdomTerminalScreen extends Screen {

    private static final int W = 360;
    private static final int H = 248;
    private static final int TAB_COUNT = 5;

    private final BondData bond;
    private int tab = 0;
    private int decreeScroll = 0;
    private int hoveredDecree = -1;
    private int hoveredBuilding = -1;
    private int hoveredMpsDay = -1;
    private int hoveredMpsBlock = -1;

    public KingdomTerminalScreen(BondData bond) {
        super(Component.translatable("gui.cocojenna.kingdom_terminal.title"));
        this.bond = bond;
    }

    public static void open(BondData bond) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new KingdomTerminalScreen(bond));
    }

    @Override
    protected void init() {
        clearWidgets();
        int left = (width - W) / 2;
        int top = (height - H) / 2;
        for (int i = 0; i < TAB_COUNT; i++) {
            int t = i;
            addRenderableWidget(new ParchmentButton(left + 6 + i * 70, top + 28, 66, 16,
                    Component.translatable("gui.cocojenna.kingdom_terminal.tab." + i),
                    b -> { tab = t; init(); }));
        }
        if (tab == 3) {
            addRenderableWidget(new ParchmentButton(left + 14, top + H - 34, 100, 16,
                    Component.translatable("gui.cocojenna.kingdom_terminal.mps_preset"),
                    b -> ModNetwork.CHANNEL.sendToServer(
                            new MpsSchedulePacket(MpsSchedulePacket.APPLY_PRESET, 0, 0, ""))));
            addRenderableWidget(new ParchmentButton(left + W - 114, top + H - 34, 100, 16,
                    Component.translatable("gui.cocojenna.kingdom_terminal.mps_run"),
                    b -> ModNetwork.CHANNEL.sendToServer(
                            new MpsSchedulePacket(MpsSchedulePacket.RUN_DAY, 0, 0, ""))));
        }
        if (tab == 4) {
            addRenderableWidget(new ParchmentButton(left + W / 2 - 60, top + H - 32, 120, 18,
                    Component.translatable("gui.cocojenna.picture_book.open"),
                    b -> PictureBookScreen.open(bond)));
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int left = (width - W) / 2;
        int top = (height - H) / 2;
        CocoJennaUi.drawPanel(g, left, top, W, H);
        g.drawCenteredString(font, title, left + W / 2, top + 10, CocoJennaUi.COL_INK);

        switch (tab) {
            case 0 -> renderStatus(g, left, top);
            case 1 -> renderDecrees(g, left, top, mx, my);
            case 2 -> renderBuilding(g, left, top, mx, my);
            case 3 -> renderCalendar(g, left, top, mx, my);
            case 4 -> renderExtras(g, left, top);
        }
        super.render(g, mx, my, partial);
    }

    private void renderStatus(GuiGraphics g, int left, int top) {
        int y = top + 54;
        g.drawString(font, Component.translatable("gui.cocojenna.kingdom.prosperity", bond.getKingdomProsperity()),
                left + 14, y, CocoJennaUi.COL_INK_SOFT);
        y += 14;
        g.drawString(font, Component.translatable("gui.cocojenna.kingdom.happiness", bond.getKingdomHappiness()),
                left + 14, y, CocoJennaUi.COL_INK_SOFT);
        y += 14;
        g.drawString(font, Component.translatable("gui.cocojenna.kingdom.stability", bond.getKingdomStability()),
                left + 14, y, CocoJennaUi.COL_INK_SOFT);
        y += 14;
        g.drawString(font, Component.translatable("gui.cocojenna.kingdom.reputation", bond.getKingdomReputation()),
                left + 14, y, CocoJennaUi.COL_INK_SOFT);
        y += 18;
        List<BondData.ActiveDecree> active = bond.getActiveDecrees();
        g.drawString(font, Component.translatable("gui.cocojenna.kingdom.active_decrees", active.size()),
                left + 14, y, CocoJennaUi.COL_INK);
        y += 14;
        for (int i = 0; i < active.size(); i++) {
            var d = KingdomDecreeCatalog.get(active.get(i).id());
            g.drawString(font, "• " + d.name().getString(), left + 20, y, CocoJennaUi.COL_INK_SOFT);
            y += 12;
        }
    }

    private void renderDecrees(GuiGraphics g, int left, int top, int mx, int my) {
        var all = KingdomDecreeCatalog.all();
        int y0 = top + 52;
        hoveredDecree = -1;
        for (int i = 0; i < 8; i++) {
            int idx = decreeScroll + i;
            if (idx >= all.size()) break;
            var def = all.get(idx);
            int y = y0 + i * 18;
            boolean hover = mx >= left + 12 && mx < left + W - 12 && my >= y && my < y + 16;
            if (hover) hoveredDecree = idx;
            int col = hover ? CocoJennaUi.COL_ACCENT : CocoJennaUi.COL_INK_SOFT;
            g.drawString(font, def.name().getString() + " (" + def.shardCost() + "◆)", left + 14, y, col);
        }
        if (hoveredDecree >= 0) {
            var def = all.get(hoveredDecree);
            g.drawString(font, def.desc(), left + 14, top + H - 44, 0xFFCCCCDD);
        }
    }

    private void renderBuilding(GuiGraphics g, int left, int top, int mx, int my) {
        int y = top + 52;
        hoveredBuilding = -1;
        g.drawString(font, Component.translatable("gui.cocojenna.building.creativity", bond.getBuildCreativity()),
                left + 14, y, CocoJennaUi.COL_INK);
        y += 16;
        var all = BuildingBlueprintCatalog.all();
        for (int i = 0; i < all.size(); i++) {
            var b = all.get(i);
            int prog = bond.getBuildingProgress(b.id());
            boolean hov = mx >= left + 12 && mx < left + W - 12 && my >= y && my < y + 14;
            if (hov) hoveredBuilding = i;
            g.drawString(font, b.name().getString() + " " + prog + "/" + b.requiredProgress(),
                    left + 14, y, hov ? CocoJennaUi.COL_ACCENT : CocoJennaUi.COL_INK_SOFT);
            y += 13;
            if (y > top + H - 24) break;
        }
        g.drawString(font, Component.translatable("gui.cocojenna.building.click_hint"),
                left + 14, top + H - 28, CocoJennaUi.COL_INK_SOFT);
    }

    private void renderCalendar(GuiGraphics g, int left, int top, int mx, int my) {
        hoveredMpsDay = -1;
        hoveredMpsBlock = -1;
        g.drawString(font, Component.translatable("gui.cocojenna.kingdom_terminal.mps_title",
                        bond.getMpsDayIndex() + 1, bond.getFestivalPrepDay(), bond.getFestivalPrepProgress()),
                left + 14, top + 50, CocoJennaUi.COL_INK);

        int gridX = left + 14;
        int gridY = top + 66;
        int cellW = 46;
        int cellH = 18;
        String[] blocks = {"morning", "afternoon", "dusk", "midnight"};
        for (int b = 0; b < 4; b++) {
            g.drawString(font, Component.translatable("kingdom.cocojenna.mps.block." + blocks[b]),
                    gridX, gridY + 14 + b * (cellH + 2), CocoJennaUi.COL_INK_SOFT);
        }
        for (int d = 0; d < 7; d++) {
            int dx = gridX + 52 + d * (cellW + 2);
            boolean today = d == bond.getMpsDayIndex();
            g.drawString(font, Component.translatable("gui.cocojenna.kingdom_terminal.mps_day", d + 1),
                    dx + 2, gridY, today ? CocoJennaUi.COL_ACCENT : CocoJennaUi.COL_INK_SOFT);
            for (int b = 0; b < 4; b++) {
                int cy = gridY + 14 + b * (cellH + 2);
                boolean hov = mx >= dx && mx < dx + cellW && my >= cy && my < cy + cellH;
                if (hov) {
                    hoveredMpsDay = d;
                    hoveredMpsBlock = b;
                }
                int bg = today ? 0xFFE8D8B0 : 0xFFD8C8A0;
                if (hov) bg = 0xFFFFF0D8;
                g.fill(dx, cy, dx + cellW, cy + cellH, CocoJennaUi.COL_FRAME);
                g.fill(dx + 1, cy + 1, dx + cellW - 1, cy + cellH - 1, bg);
                String taskId = bond.getMpsCell(d, b);
                MpsTask task = MpsTask.byId(taskId);
                String label = task.label().getString();
                if (font.width(label) > cellW - 4) {
                    label = label.substring(0, Math.min(4, label.length())) + "…";
                }
                g.drawString(font, label, dx + 3, cy + 5, CocoJennaUi.COL_INK);
            }
        }
        g.drawString(font, Component.translatable("gui.cocojenna.kingdom_terminal.mps_hint"),
                left + 14, top + H - 48, CocoJennaUi.COL_INK_SOFT);
    }

    private void renderExtras(GuiGraphics g, int left, int top) {
        g.drawString(font, Component.translatable("gui.cocojenna.kingdom.pages", bond.getPictureBookPages().size()),
                left + 14, top + 54, CocoJennaUi.COL_INK_SOFT);
        g.drawString(font, Component.translatable("gui.cocojenna.kingdom.cooking_hint"),
                left + 14, top + 72, CocoJennaUi.COL_INK_SOFT);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (tab == 1 && button == 0 && hoveredDecree >= 0) {
            String id = KingdomDecreeCatalog.all().get(hoveredDecree).id();
            ModNetwork.CHANNEL.sendToServer(new KingdomDecreeActionPacket(
                    KingdomDecreeActionPacket.Action.ENACT, id, 0));
            return true;
        }
        if (tab == 1 && button == 1 && !bond.getActiveDecrees().isEmpty()) {
            ModNetwork.CHANNEL.sendToServer(new KingdomDecreeActionPacket(
                    KingdomDecreeActionPacket.Action.REVOKE, "", 0));
            return true;
        }
        if (tab == 2 && button == 0 && hoveredBuilding >= 0) {
            String id = BuildingBlueprintCatalog.all().get(hoveredBuilding).id();
            ModNetwork.CHANNEL.sendToServer(new BuildingContributePacket(id));
            return true;
        }
        if (tab == 3 && button == 0 && hoveredMpsDay >= 0 && hoveredMpsBlock >= 0) {
            ModNetwork.CHANNEL.sendToServer(new MpsSchedulePacket(
                    MpsSchedulePacket.CYCLE_CELL, hoveredMpsDay, hoveredMpsBlock, ""));
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        if (tab == 1) {
            decreeScroll = Math.max(0, Math.min(KingdomDecreeCatalog.all().size() - 8,
                    decreeScroll - (int) delta));
            return true;
        }
        return super.mouseScrolled(mx, my, delta);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
