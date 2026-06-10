package com.cocojenna.client.gui;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.BondSettingsPacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.sequence.FelineSequenceSkills;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** P 鍵 — 技能與裝備配置（設計書 §2.2）. */
public class SkillEquipmentScreen extends Screen {

    private static final int W = 340;
    private static final int H = 260;
    private final com.cocojenna.capability.BondData bond;
    private int hoveredSlot = -1;

    public SkillEquipmentScreen() {
        super(Component.translatable("gui.cocojenna.skill_equip.title"));
        this.bond = ModCapabilities.getOrDefault(net.minecraft.client.Minecraft.getInstance().player);
    }

    public static void open() {
        net.minecraft.client.Minecraft.getInstance().setScreen(new SkillEquipmentScreen());
    }

    @Override
    protected void init() {
        int left = (width - W) / 2;
        int top = (height - H) / 2;
        for (int i = 0; i < 3; i++) {
            int preset = i;
            addRenderableWidget(new ParchmentButton(left + 14 + i * 72, top + 52, 66, 16,
                    Component.translatable("gui.cocojenna.skill_equip.preset", i + 1),
                    b -> switchPreset(preset)));
        }
        addRenderableWidget(new ParchmentButton(left + 12, top + H - 28, 90, 18,
                Component.translatable("gui.cocojenna.skill_tree.open"),
                b -> SkillTreeScreen.open()));
        addRenderableWidget(new ParchmentButton(left + W - 102, top + H - 28, 90, 18,
                Component.translatable("gui.cocojenna.close"),
                b -> onClose()));
    }

    private void switchPreset(int preset) {
        bond.setActiveSkillPreset(preset);
        ModNetwork.CHANNEL.sendToServer(new BondSettingsPacket(BondSettingsPacket.SKILL_PRESET, preset));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int left = (width - W) / 2;
        int top = (height - H) / 2;
        CocoJennaUi.drawCatKingdomFrame(g, font, left, top, W, H, title);

        String force = bond.getFelineForce();
        if (force == null || force.isEmpty()) force = "?";
        g.drawString(font, Component.translatable("gui.cocojenna.skill_equip.force",
                Component.translatable("gui.cocojenna.force." + force)), left + 14, top + 30, CocoJennaUi.COL_INK);
        g.drawString(font, Component.translatable("gui.cocojenna.sequence_tier", bond.getFelineTier(), ""),
                left + 14, top + 42, CocoJennaUi.COL_INK_SOFT);

        int slots = FelineSequenceSkills.wheelSlotCount(Math.max(9, bond.getFelineTier()));
        int cols = Math.min(4, slots);
        int rows = (slots + cols - 1) / cols;
        int slotSize = 36;
        int gridX = left + 14;
        int gridY = top + 76;
        hoveredSlot = -1;
        for (int i = 0; i < slots; i++) {
            int col = i % cols;
            int row = i / cols;
            int sx = gridX + col * (slotSize + 6);
            int sy = gridY + row * (slotSize + 6);
            boolean sel = i == bond.getPreferredSkillSlot();
            boolean hov = mx >= sx && mx < sx + slotSize && my >= sy && my < sy + slotSize;
            if (hov) hoveredSlot = i;
            int bg = sel ? 0xFFFFE0B0 : hov ? 0xFFFFF0E8 : 0xFFE8D4B0;
            g.fill(sx, sy, sx + slotSize, sy + slotSize, CocoJennaUi.COL_FRAME);
            g.fill(sx + 1, sy + 1, sx + slotSize - 1, sy + slotSize - 1, bg);
            if (sel) {
                g.fill(sx + 2, sy + 2, sx + slotSize - 2, sy + 4, 0xFFFFD700);
            }
            String label = String.valueOf(i + 1);
            g.drawCenteredString(font, label, sx + slotSize / 2, sy + slotSize / 2 - 4, CocoJennaUi.COL_COFFEE);
        }

        int passiveY = gridY + rows * (slotSize + 6) + 8;
        g.drawString(font, Component.translatable("gui.cocojenna.skill_equip.passive"),
                left + 14, passiveY, CocoJennaUi.COL_ACCENT_DK);
        int shown = 0;
        for (String card : bond.getOwnedPromotionCards()) {
            if (shown >= 3) break;
            g.fill(left + 14, passiveY + 14 + shown * 18, left + W - 14, passiveY + 30 + shown * 18, 0xFFD8C8A8);
            g.drawString(font, card, left + 18, passiveY + 18 + shown * 18, CocoJennaUi.COL_INK);
            shown++;
        }
        if (shown == 0) {
            g.drawString(font, Component.translatable("gui.cocojenna.skill_equip.no_cards"),
                    left + 14, passiveY + 18, CocoJennaUi.COL_INK_SOFT);
        }

        g.drawCenteredString(font, Component.translatable("gui.cocojenna.skills.hint"),
                width / 2, top + H + 6, CocoJennaUi.COL_INK_SOFT);
        super.render(g, mx, my, partial);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && hoveredSlot >= 0) {
            bond.setPreferredSkillSlot(hoveredSlot);
            ModNetwork.CHANNEL.sendToServer(new BondSettingsPacket(BondSettingsPacket.SKILL_SLOT, hoveredSlot));
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
