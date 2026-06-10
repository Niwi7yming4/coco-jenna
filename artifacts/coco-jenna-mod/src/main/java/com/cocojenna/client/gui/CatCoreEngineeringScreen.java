package com.cocojenna.client.gui;

import com.cocojenna.capability.BondData;
import com.cocojenna.endgame.BuildingBlueprintCatalog;
import com.cocojenna.endgame.BuildingMaterialHelper;
import com.cocojenna.network.BuildingContributePacket;
import com.cocojenna.network.BuildingPlacePacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenMemoryTheaterRequestPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

/** 貓咪核心工程 — 城鎮建設專用 UI（設計書第七章）. */
public class CatCoreEngineeringScreen extends Screen {

    private static final int W = 380;
    private static final int H = 320;
    private static final int HEADER_H = 92;
    private static final int FOOTER_H = 44;
    private static final int ROW_H = 38;
    private static final int PAD_X = 14;

    private final BondData bond;
    private int scroll = 0;
    private int hovered = -1;

    public CatCoreEngineeringScreen(BondData bond) {
        super(Component.translatable("gui.cocojenna.cat_core_engineering.title"));
        this.bond = bond;
    }

    public static void open(BondData bond) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new CatCoreEngineeringScreen(bond));
    }

    private int panelLeft() { return (width - W) / 2; }

    private int panelTop() { return (height - H) / 2; }

    private int listTop() { return panelTop() + HEADER_H; }

    private int listBottom() { return panelTop() + H - FOOTER_H; }

    private int visibleRows() {
        return Math.max(1, (listBottom() - listTop()) / ROW_H);
    }

    @Override
    protected void init() {
        clearWidgets();
        int left = panelLeft();
        int top = panelTop();
        int footerY = top + H - FOOTER_H + 10;
        addRenderableWidget(new ParchmentButton(left + W - PAD_X - 84, footerY, 84, 20,
                Component.translatable("gui.cocojenna.cat_core_engineering.place"),
                b -> tryPlaceSelected()));
        addRenderableWidget(new ParchmentButton(left + PAD_X, footerY, 84, 20,
                Component.translatable("gui.cocojenna.cat_core_engineering.contribute"),
                b -> tryContributeSelected()));
        if (bond.isBuildingPlaced("memory_theater")) {
            addRenderableWidget(new ParchmentButton(left + W / 2 - 60, footerY - 26, 120, 20,
                    Component.translatable("memory_theater.cocojenna.open"),
                    b -> ModNetwork.CHANNEL.sendToServer(new OpenMemoryTheaterRequestPacket())));
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int left = panelLeft();
        int top = panelTop();
        CocoJennaUi.drawPanel(g, left, top, W, H);
        g.drawCenteredString(font, title, left + W / 2, top + 10, CocoJennaUi.COL_INK);

        int y = top + 28;
        g.drawString(font, Component.translatable("gui.cocojenna.building.creativity", bond.getBuildCreativity()),
                left + PAD_X, y, CocoJennaUi.COL_INK_SOFT);
        y += 14;
        g.drawString(font, Component.translatable("gui.cocojenna.cat_core_engineering.forge_level",
                bond.getIronpawForgeLevel()), left + PAD_X, y, CocoJennaUi.COL_ACCENT);
        y += 18;
        g.drawString(font, Component.translatable("gui.cocojenna.cat_core_engineering.hint"),
                left + PAD_X, y, CocoJennaUi.COL_INK_SOFT);

        int listTop = listTop();
        int listBottom = listBottom();
        g.fill(left + PAD_X, listTop - 4, left + W - PAD_X, listTop - 3, 0x44AA9988);

        hovered = -1;
        List<BuildingBlueprintCatalog.Blueprint> all = BuildingBlueprintCatalog.all();
        int rows = visibleRows();
        g.enableScissor(left + 6, listTop, left + W - 6, listBottom);
        for (int i = 0; i < rows; i++) {
            int idx = scroll + i;
            if (idx >= all.size()) break;
            var bp = all.get(idx);
            int rowY = listTop + i * ROW_H;
            int prog = bond.getBuildingProgress(bp.id());
            boolean placed = bond.isBuildingPlaced(bp.id());
            boolean done = prog >= bp.requiredProgress();
            boolean hov = mx >= left + 10 && mx < left + W - 10
                    && my >= rowY && my < rowY + ROW_H - 2;
            if (hov) hovered = idx;

            if (hov) {
                g.fill(left + 10, rowY, left + W - 10, rowY + ROW_H - 2, 0x33FFCCAA);
            }

            int col = hov ? CocoJennaUi.COL_ACCENT : CocoJennaUi.COL_INK_SOFT;
            String status = placed ? "✓" : (done ? "★" : prog + "/" + bp.requiredProgress());
            g.drawString(font, bp.name().getString() + "  " + status, left + PAD_X, rowY + 4, col);
            drawBomRow(g, bp, left + PAD_X + 6, rowY + 16, left + W - PAD_X);
        }
        g.disableScissor();

        int maxScroll = Math.max(0, all.size() - rows);
        if (maxScroll > 0) {
            String scrollHint = scroll + 1 + "–" + Math.min(scroll + rows, all.size()) + " / " + all.size();
            g.drawCenteredString(font, scrollHint, left + W / 2, listBottom + 4, CocoJennaUi.COL_INK_SOFT);
        }

        super.render(g, mx, my, partial);
    }

    private void drawBomRow(GuiGraphics g, BuildingBlueprintCatalog.Blueprint bp,
                            int x, int y, int rightEdge) {
        int iconX = x;
        int iconCount = 0;
        StringBuilder text = new StringBuilder();
        boolean allEnough = true;

        for (Map.Entry<String, Integer> entry : bp.bom().entrySet()) {
            Item item = BuildingMaterialHelper.resolve(entry.getKey());
            int need = entry.getValue();
            int have = minecraft.player != null ? minecraft.player.getInventory().countItem(item) : 0;
            if (have < need) allEnough = false;

            if (iconCount < 4 && iconX + 16 <= rightEdge) {
                g.renderItem(new ItemStack(item), iconX, y);
                iconX += 18;
                iconCount++;
            }

            if (!text.isEmpty()) text.append(" · ");
            text.append(have).append('/').append(need).append('×')
                    .append(new ItemStack(item).getHoverName().getString());
        }

        int textX = iconCount > 0 ? iconX + 2 : x;
        int textCol = allEnough ? 0xFF9999AA : 0xFFCC6666;
        g.drawString(font, trimBom(text.toString(), rightEdge - textX), textX, y + 4, textCol);
    }

    private String trimBom(String bom, int maxW) {
        if (font.width(bom) <= maxW) {
            return bom;
        }
        String ell = "…";
        String trimmed = bom;
        while (trimmed.length() > 1 && font.width(trimmed + ell) > maxW) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed + ell;
    }

    private void tryContributeSelected() {
        if (hovered < 0) return;
        String id = BuildingBlueprintCatalog.all().get(hovered).id();
        ModNetwork.CHANNEL.sendToServer(new BuildingContributePacket(id));
    }

    private void tryPlaceSelected() {
        if (hovered < 0) return;
        String id = BuildingBlueprintCatalog.all().get(hovered).id();
        ModNetwork.CHANNEL.sendToServer(new BuildingPlacePacket(id));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (hovered >= 0) {
            if (button == 0) {
                tryContributeSelected();
                return true;
            }
            if (button == 1) {
                tryPlaceSelected();
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        int max = Math.max(0, BuildingBlueprintCatalog.all().size() - visibleRows());
        scroll = Math.max(0, Math.min(max, scroll - (int) delta));
        return true;
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
