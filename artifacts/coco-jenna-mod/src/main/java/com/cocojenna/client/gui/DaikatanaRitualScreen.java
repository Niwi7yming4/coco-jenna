package com.cocojenna.client.gui;

import com.cocojenna.memforge.DaikatanaRitual;
import com.cocojenna.memforge.DaikatanaRitualRecipe;
import com.cocojenna.network.DaikatanaRitualActionPacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenDaikatanaRitualPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
/** Ironpaw 風格大快刀鍛造儀式面板. */
public class DaikatanaRitualScreen extends Screen {

    private static final int PANEL_W = 280;
    private static final int PANEL_H = 220;
    private static final int ROW_H = 18;

    private static DaikatanaRitualScreen instance;

    private BlockPos altarPos = BlockPos.ZERO;
    private int phaseOrd = -1;
    private int recipeOrd = -1;
    private long phaseEndTick;
    private long levelTime;
    private int selectedOrd = 0;
    private int scroll;
    private boolean hoverStart;
    private boolean hoverQuench;

    public DaikatanaRitualScreen() {
        super(Component.translatable("gui.cocojenna.daikatana_ritual.title"));
    }

    public static void openOrUpdate(OpenDaikatanaRitualPacket pkt) {
        if (instance != null && instance.minecraft != null) {
            instance.apply(pkt);
            return;
        }
        var screen = new DaikatanaRitualScreen();
        screen.apply(pkt);
        net.minecraft.client.Minecraft.getInstance().setScreen(screen);
    }

    private void apply(OpenDaikatanaRitualPacket pkt) {
        altarPos = pkt.altarPos();
        phaseOrd = pkt.phaseOrd();
        recipeOrd = pkt.recipeOrd();
        phaseEndTick = pkt.phaseEndTick();
        levelTime = pkt.levelTime();
        if (recipeOrd >= 0) {
            selectedOrd = recipeOrd;
        }
    }

    @Override
    protected void init() {
        instance = this;
    }

    @Override
    public void onClose() {
        ModNetwork.CHANNEL.sendToServer(
                new DaikatanaRitualActionPacket(DaikatanaRitualActionPacket.Action.CLOSE, altarPos, -1));
        if (instance == this) {
            instance = null;
        }
        super.onClose();
    }

    private boolean ritualActive() {
        return phaseOrd >= 0;
    }

    private DaikatanaRitualRecipe selectedRecipe() {
        var all = DaikatanaRitualRecipe.values();
        if (selectedOrd < 0 || selectedOrd >= all.length) return all[0];
        return all[selectedOrd];
    }

    private float progress() {
        if (!ritualActive()) return 0f;
        DaikatanaRitual.Phase[] phases = DaikatanaRitual.Phase.values();
        DaikatanaRitual.Phase phase = phases[Math.max(0, Math.min(phaseOrd, phases.length - 1))];
        int total = phase == DaikatanaRitual.Phase.FORGING
                ? com.cocojenna.memforge.DaikatanaRitualManager.FORGE_TICKS
                : com.cocojenna.memforge.DaikatanaRitualManager.QUENCH_TICKS;
        long start = phaseEndTick - total;
        if (levelTime <= start) return 0f;
        return Math.min(1f, (levelTime - start) / (float) total);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int x = (width - PANEL_W) / 2;
        int y = (height - PANEL_H) / 2;

        CocoJennaUi.drawMachinePanel(g, font, x, y, PANEL_W, PANEL_H, title);

        int innerX = x + 10;
        int innerY = y + 28;
        int listW = 118;
        int detailX = innerX + listW + 8;
        int detailW = PANEL_W - listW - 28;

        g.drawString(font, Component.translatable("gui.cocojenna.daikatana_ritual.recipes"),
                innerX, innerY - 10, CocoJennaUi.COL_INK_SOFT, false);

        var recipes = DaikatanaRitualRecipe.values();
        int visible = 7;
        for (int i = 0; i < visible; i++) {
            int idx = scroll + i;
            if (idx >= recipes.length) break;
            DaikatanaRitualRecipe r = recipes[idx];
            int ry = innerY + i * ROW_H;
            boolean sel = idx == selectedOrd;
            boolean hov = mx >= innerX && mx < innerX + listW && my >= ry && my < ry + ROW_H - 2;
            CocoJennaUi.drawCardHover(g, innerX, ry, listW, ROW_H - 2, hov, sel);
            int color = r.hasMaterials(minecraft.player.getInventory()) ? CocoJennaUi.COL_INK : 0xFFAA4444;
            Component name = Component.translatable("item.cocojenna.daikatana_" + r.id);
            g.drawString(font, name, innerX + 4, ry + 4, color, false);
        }

        if (scroll > 0) {
            CocoJennaUi.drawButton(g, font, innerX + listW - 14, innerY - 2, 12, 12,
                    Component.literal("▲"), false, true, false);
        }
        if (scroll + visible < recipes.length) {
            CocoJennaUi.drawButton(g, font, innerX + listW - 14, innerY + visible * ROW_H - 14, 12, 12,
                    Component.literal("▼"), false, true, false);
        }

        DaikatanaRitualRecipe recipe = selectedRecipe();
        g.renderItem(new ItemStack(recipe.result), detailX, innerY);
        g.drawString(font, Component.translatable("item.cocojenna.daikatana_" + recipe.id),
                detailX + 22, innerY + 4, CocoJennaUi.COL_INK, false);

        int matY = innerY + 24;
        g.drawString(font, Component.translatable("gui.cocojenna.daikatana_ritual.materials"),
                detailX, matY, CocoJennaUi.COL_INK_SOFT, false);
        matY += 12;
        for (DaikatanaRitualRecipe.Mat m : recipe.materials) {
            if (matY > y + PANEL_H - 50) break;
            int have = countItem(m.item());
            int need = m.count();
            int col = have >= need ? CocoJennaUi.COL_INK : 0xFFCC4444;
            String line = itemName(m.item()) + "  " + have + "/" + need;
            g.drawString(font, line, detailX, matY, col, false);
            matY += 11;
        }

        if (ritualActive()) {
            DaikatanaRitual.Phase[] phases = DaikatanaRitual.Phase.values();
        DaikatanaRitual.Phase phase = phases[Math.max(0, Math.min(phaseOrd, phases.length - 1))];
            Component phaseLabel = phase == DaikatanaRitual.Phase.FORGING
                    ? Component.translatable("gui.cocojenna.daikatana_ritual.phase_forge")
                    : Component.translatable("gui.cocojenna.daikatana_ritual.phase_quench");
            g.drawString(font, phaseLabel, detailX, matY + 4, CocoJennaUi.COL_ACCENT_DK, false);
            CocoJennaUi.drawStatBar(g, font, detailX, matY + 16, detailW, Component.empty(),
                    progress() * 100f, 100f, 0xFFE8A040);
            g.drawString(font, Component.translatable("gui.cocojenna.daikatana_ritual.stay_near"),
                    detailX, matY + 36, CocoJennaUi.COL_INK_SOFT, false);
        }

        int btnY = y + PANEL_H - 28;
        int btnW = 72;
        int startX = x + PANEL_W / 2 - btnW - 6;
        int quenchX = x + PANEL_W / 2 + 6;

        boolean canStart = !ritualActive() && recipe.hasMaterials(minecraft.player.getInventory());
        hoverStart = mx >= startX && mx < startX + btnW && my >= btnY && my < btnY + 18;
        CocoJennaUi.drawButton(g, font, startX, btnY, btnW, 18,
                Component.translatable("gui.cocojenna.daikatana_ritual.start"),
                hoverStart, canStart, true);

        boolean quenchPhase = ritualActive() && phaseOrd == DaikatanaRitual.Phase.QUENCH.ordinal();
        hoverQuench = mx >= quenchX && mx < quenchX + btnW && my >= btnY && my < btnY + 18;
        CocoJennaUi.drawButton(g, font, quenchX, btnY, btnW, 18,
                Component.translatable("gui.cocojenna.daikatana_ritual.quench"),
                hoverQuench, quenchPhase, true);

        super.render(g, mx, my, partial);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button != 0) return super.mouseClicked(mx, my, button);

        int x = (width - PANEL_W) / 2;
        int y = (height - PANEL_H) / 2;
        int innerX = x + 10;
        int innerY = y + 28;
        int listW = 118;

        if (mx >= innerX + listW - 14 && mx < innerX + listW - 2) {
            if (my >= innerY - 2 && my < innerY + 10 && scroll > 0) {
                scroll--;
                return true;
            }
            if (my >= innerY + 7 * ROW_H - 14 && my < innerY + 7 * ROW_H - 2
                    && scroll + 7 < DaikatanaRitualRecipe.values().length) {
                scroll++;
                return true;
            }
        }

        for (int i = 0; i < 7; i++) {
            int idx = scroll + i;
            if (idx >= DaikatanaRitualRecipe.values().length) break;
            int ry = innerY + i * ROW_H;
            if (mx >= innerX && mx < innerX + listW && my >= ry && my < ry + ROW_H - 2) {
                selectedOrd = idx;
                return true;
            }
        }

        int btnY = y + PANEL_H - 28;
        int btnW = 72;
        if (hoverStart) {
            ModNetwork.CHANNEL.sendToServer(new DaikatanaRitualActionPacket(
                    DaikatanaRitualActionPacket.Action.START, altarPos, selectedOrd));
            return true;
        }
        if (hoverQuench) {
            ModNetwork.CHANNEL.sendToServer(new DaikatanaRitualActionPacket(
                    DaikatanaRitualActionPacket.Action.QUENCH, altarPos, -1));
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static int countItem(net.minecraft.world.item.Item item) {
        var inv = net.minecraft.client.Minecraft.getInstance().player.getInventory();
        int total = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            var stack = inv.getItem(i);
            if (stack.is(item)) total += stack.getCount();
        }
        return total;
    }

    private static String itemName(net.minecraft.world.item.Item item) {
        return new ItemStack(item).getHoverName().getString();
    }
}
