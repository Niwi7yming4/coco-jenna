package com.cocojenna.client.gui;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.BondSettingsPacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.sequence.FelineSequenceSkills;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** 技能設定面板 — /skills 或記憶之書開啟. */
public class SkillSettingsScreen extends Screen {

    private static final int PANEL_W = 220;
    private static final int PANEL_H = 178;

    private final com.cocojenna.capability.BondData bond;

    public SkillSettingsScreen() {
        super(Component.translatable("gui.cocojenna.skills.title"));
        this.bond = ModCapabilities.getOrDefault(net.minecraft.client.Minecraft.getInstance().player);
    }

    public static void open() {
        net.minecraft.client.Minecraft.getInstance().setScreen(new SkillSettingsScreen());
    }

    @Override
    protected void init() {
        int left = (width - PANEL_W) / 2;
        int top = (height - PANEL_H) / 2;
        int cx = left + 10;
        int cy = top + 52;
        addRenderableWidget(new ParchmentButton(cx, cy, PANEL_W - 20, 18,
                Component.translatable("gui.cocojenna.skills.slot", bond.getPreferredSkillSlot()),
                b -> cycleSlot()));
        cy += 24;
        addRenderableWidget(new ParchmentButton(cx, cy, PANEL_W - 20, 18,
                bond.isShowSkillCooldown()
                        ? Component.translatable("gui.cocojenna.skills.cooldown_on")
                        : Component.translatable("gui.cocojenna.skills.cooldown_off"),
                b -> toggleCooldown()));
        cy += 30;
        addRenderableWidget(new ParchmentButton(cx, cy, PANEL_W - 20, 18,
                Component.translatable("gui.cocojenna.skill_tree.open"),
                b -> SkillTreeScreen.open()));
        cy += 24;
        addRenderableWidget(new ParchmentButton(cx + (PANEL_W - 20 - 80) / 2, cy, 80, 18,
                Component.translatable("gui.cocojenna.close"),
                b -> onClose()));
    }

    private void cycleSlot() {
        int slots = FelineSequenceSkills.wheelSlotCount(bond.getFelineTier());
        int next = (bond.getPreferredSkillSlot() + 1) % Math.max(1, slots);
        bond.setPreferredSkillSlot(next);
        ModNetwork.CHANNEL.sendToServer(new BondSettingsPacket(BondSettingsPacket.SKILL_SLOT, next));
        init();
    }

    private void toggleCooldown() {
        boolean next = !bond.isShowSkillCooldown();
        bond.setShowSkillCooldown(next);
        ModNetwork.CHANNEL.sendToServer(new BondSettingsPacket(BondSettingsPacket.SHOW_COOLDOWN, next ? 1 : 0));
        init();
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int left = (width - PANEL_W) / 2;
        int top = (height - PANEL_H) / 2;
        CocoJennaUi.drawMachinePanel(g, font, left, top, PANEL_W, PANEL_H, title);

        String force = bond.getFelineForce();
        if (force == null || force.isEmpty()) force = "resonance";
        Component tierLine = Component.translatable("gui.cocojenna.sequence_tier",
                bond.getFelineTier(),
                Component.translatable("gui.cocojenna.force." + force));
        g.drawCenteredString(font, tierLine, width / 2, top + 30, CocoJennaUi.COL_INK_SOFT);

        super.render(g, mx, my, partial);

        g.drawCenteredString(font,
                Component.translatable("gui.cocojenna.skills.hint"),
                width / 2, top + PANEL_H + 8, CocoJennaUi.COL_INK_SOFT);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
