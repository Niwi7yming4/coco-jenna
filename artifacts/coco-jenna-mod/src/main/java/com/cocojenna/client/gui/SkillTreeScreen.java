package com.cocojenna.client.gui;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.BondSettingsPacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** 3×9 貓之序列技能樹（呼嚕／夜瞳／液態 × 序列 9–7，每格一個技能節點）. */
public class SkillTreeScreen extends Screen {

    private static final int W = 380;
    private static final int H = 268;
    private static final int GRID_LEFT = 52;
    private static final String[] FORCES = {"resonance", "shadow", "chaos"};
    private static final String[] FORCE_LABELS = {
            "gui.cocojenna.force.resonance",
            "gui.cocojenna.force.shadow",
            "gui.cocojenna.force.chaos"
    };
    private static final int[] TIERS = {9, 8, 7};

    private final BondData bond;
    private int hoveredCol = -1;
    private int hoveredRow = -1;

    public SkillTreeScreen(BondData bond) {
        super(Component.translatable("gui.cocojenna.skill_tree.title"));
        this.bond = bond;
    }

    public static void open() {
        var player = net.minecraft.client.Minecraft.getInstance().player;
        if (player == null) return;
        net.minecraft.client.Minecraft.getInstance().setScreen(
                new SkillTreeScreen(ModCapabilities.getOrDefault(player)));
    }

    @Override
    protected void init() {
        int left = (width - W) / 2;
        int top = (height - H) / 2;
        addRenderableWidget(new ParchmentButton(left + W - 90, top + H - 28, 80, 18,
                Component.translatable("gui.cocojenna.close"), b -> onClose()));
        addRenderableWidget(new ParchmentButton(left + 10, top + H - 28, 100, 18,
                Component.translatable("gui.cocojenna.skill_tree.settings"),
                b -> net.minecraft.client.Minecraft.getInstance().setScreen(new SkillSettingsScreen())));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int left = (width - W) / 2;
        int top = (height - H) / 2;
        CocoJennaUi.drawPanel(g, left, top, W, H);
        g.drawCenteredString(font, title, left + W / 2, top + 8, CocoJennaUi.COL_INK);

        String force = bond.getFelineForce();
        if (force == null || force.isEmpty()) force = "resonance";
        int tier = bond.getFelineTier();
        int forceIdx = indexOf(FORCES, force);

        g.drawString(font, Component.translatable("gui.cocojenna.skill_tree.force",
                Component.translatable("gui.cocojenna.force." + force)), left + 12, top + 24, CocoJennaUi.COL_ACCENT);
        g.drawString(font, Component.translatable("gui.cocojenna.sequence_tier", tier,
                Component.translatable("gui.cocojenna.force." + force)), left + 12, top + 38, CocoJennaUi.COL_INK_SOFT);

        int gridTop = top + 58;
        int cellW = 104;
        int cellH = 44;
        hoveredCol = -1;
        hoveredRow = -1;

        for (int c = 0; c < 3; c++) {
            g.drawCenteredString(font, Component.translatable(FORCE_LABELS[c]),
                    left + GRID_LEFT + c * cellW + cellW / 2, gridTop - 6, CocoJennaUi.COL_INK_SOFT);
        }

        for (int r = 0; r < TIERS.length; r++) {
            int seqTier = TIERS[r];
            int variant = 9 - seqTier;
            int rowY = gridTop + r * cellH;

            g.drawString(font, Component.translatable("gui.cocojenna.skill_tree.tier", seqTier),
                    left + 10, rowY + cellH / 2 - 4, CocoJennaUi.COL_INK_SOFT);

            for (int c = 0; c < 3; c++) {
                int x = left + GRID_LEFT + c * cellW;
                int y = rowY;
                boolean unlocked = tier <= seqTier;
                boolean active = c == forceIdx && unlocked;
                boolean selected = c == forceIdx && bond.getPreferredSkillSlot() == variant;
                boolean hov = mx >= x && mx < x + cellW - 6 && my >= y && my < y + cellH - 4;
                if (hov) {
                    hoveredCol = c;
                    hoveredRow = r;
                }

                int fill = active ? (selected ? 0x55FFCC88 : 0x33AA9988) : 0x22AAAAAA;
                g.fill(x, y, x + cellW - 6, y + cellH - 4, fill);
                CocoJennaUi.drawInset(g, x + 1, y + 1, cellW - 8, cellH - 6);

                ResourceLocation icon = skillIcon(FORCES[c], variant);
                g.blit(icon, x + 6, y + 8, 20, 20, 0, 0, 32, 32, 32, 32);
                if (!unlocked) {
                    g.fill(x + 6, y + 8, x + 26, y + 28, 0x88000000);
                }

                Component label = Component.translatable(
                        "gui.cocojenna.skill_tree.node." + FORCES[c] + "." + variant);
                int textCol = active
                        ? (selected ? CocoJennaUi.COL_ACCENT : CocoJennaUi.COL_INK)
                        : (unlocked ? 0xFF888899 : 0xFF666677);
                g.drawString(font, label, x + 30, y + 14, textCol);
            }
        }

        if (hoveredCol >= 0 && hoveredRow >= 0) {
            int variant = 9 - TIERS[hoveredRow];
            g.drawCenteredString(font, Component.translatable("gui.cocojenna.skill_tree.hint", variant),
                    left + W / 2, top + H - 44, CocoJennaUi.COL_INK_SOFT);
        }

        super.render(g, mx, my, partial);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && hoveredCol >= 0 && hoveredRow >= 0) {
            String force = FORCES[hoveredCol];
            if (force.equals(bond.getFelineForce())) {
                int slot = 9 - TIERS[hoveredRow];
                bond.setPreferredSkillSlot(slot);
                ModNetwork.CHANNEL.sendToServer(new BondSettingsPacket(BondSettingsPacket.SKILL_SLOT, slot));
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    private static ResourceLocation skillIcon(String force, int variant) {
        String tex = switch (force) {
            case "shadow" -> switch (variant) {
                case 0 -> "skill_whisper_step";
                case 1 -> "skill_shadow_tag";
                case 2 -> "skill_shadow_blade";
                default -> "skill_owl_eye";
            };
            case "chaos" -> switch (variant) {
                case 0 -> "skill_box_toss";
                case 1 -> "skill_dark_dash";
                case 2 -> "skill_confusion";
                default -> "skill_luck";
            };
            default -> switch (variant) {
                case 0 -> "skill_purr_heal";
                case 1 -> "skill_barrier";
                case 2 -> "skill_warm_aura";
                default -> "skill_soundwave";
            };
        };
        return new ResourceLocation(CocoJennaMod.MOD_ID, "textures/gui/skills/" + tex + ".png");
    }

    private static int indexOf(String[] arr, String v) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(v)) return i;
        }
        return 0;
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
