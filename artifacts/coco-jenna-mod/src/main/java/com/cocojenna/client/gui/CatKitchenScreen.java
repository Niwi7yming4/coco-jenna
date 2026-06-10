package com.cocojenna.client.gui;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.CookingRecipeRegistry;
import com.cocojenna.network.CookFoodPacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** 貓之國料理台 UI（設計書 §2.3）. */
public class CatKitchenScreen extends Screen {

    private static final int W = 320;
    private static final int H = 220;
    private int scroll = 0;
    private int hovered = -1;

    public CatKitchenScreen() {
        super(Component.translatable("gui.cocojenna.cat_kitchen.title"));
    }

    public static void open() {
        net.minecraft.client.Minecraft.getInstance().setScreen(new CatKitchenScreen());
    }

    @Override
    protected void init() {
        int left = (width - W) / 2;
        int top = (height - H) / 2;
        addRenderableWidget(new ParchmentButton(left + W - 88, top + H - 26, 76, 18,
                Component.translatable("gui.cocojenna.close"),
                b -> onClose()));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int left = (width - W) / 2;
        int top = (height - H) / 2;
        CocoJennaUi.drawCatKingdomFrame(g, font, left, top, W, H, title);

        g.drawString(font, Component.translatable("gui.cocojenna.cat_kitchen.recipes"),
                left + 14, top + 30, CocoJennaUi.COL_ACCENT_DK);
        g.drawString(font, Component.translatable("gui.cocojenna.cat_kitchen.hint"),
                left + 160, top + 30, CocoJennaUi.COL_INK_SOFT);

        int previewX = left + 168;
        int previewY = top + 48;
        CocoJennaUi.drawInset(g, previewX, previewY, 140, 120);
        g.drawCenteredString(font, Component.translatable("gui.cocojenna.cat_kitchen.preview"),
                previewX + 70, previewY + 6, CocoJennaUi.COL_INK);

        hovered = -1;
        var recipes = CookingRecipeRegistry.all();
        var bond = ModCapabilities.getOrDefault(minecraft.player);
        for (int i = 0; i < 9; i++) {
            int idx = scroll + i;
            if (idx >= recipes.size()) break;
            var r = recipes.get(idx);
            int y = top + 46 + i * 18;
            boolean hov = mx >= left + 12 && mx < left + 154 && my >= y && my < y + 16;
            if (hov) hovered = idx;
            boolean locked = !r.isUnlocked(bond);
            int color = locked ? 0xFF999999 : hov ? CocoJennaUi.COL_WARM_PINK : CocoJennaUi.COL_INK_SOFT;
            boolean cocoPref = r.id().contains("coco") || r.id().contains("meat_puree");
            boolean jennaPref = r.id().contains("jenna") || r.id().contains("taste_of_memory");
            String pref = cocoPref ? "§8🐾" : jennaPref ? "§6🐾" : "  ";
            String line = pref + r.name().getString() + " ★" + r.preferenceStars() + (locked ? " 🔒" : "");
            g.drawString(font, line, left + 14, y, color);

            if (hov && !locked) {
                g.drawString(font, r.name(), previewX + 8, previewY + 28, CocoJennaUi.COL_INK);
                g.drawString(font, Component.translatable("gui.cocojenna.cat_kitchen.click_cook"),
                        previewX + 8, previewY + 100, CocoJennaUi.COL_INK_SOFT);
            } else if (hov && locked) {
                g.drawString(font, Component.translatable("cooking.cocojenna.locked"),
                        previewX + 8, previewY + 40, CocoJennaUi.COL_INK_SOFT);
            }
        }
        super.render(g, mx, my, partial);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && hovered >= 0) {
            ModNetwork.CHANNEL.sendToServer(new CookFoodPacket(hovered));
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        scroll = Math.max(0, Math.min(CookingRecipeRegistry.all().size() - 9, scroll - (int) delta));
        return true;
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
