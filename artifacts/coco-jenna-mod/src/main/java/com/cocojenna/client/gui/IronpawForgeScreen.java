package com.cocojenna.client.gui;

import com.cocojenna.memforge.WeaponEnhanceHelper;
import com.cocojenna.network.IronpawForgeActionPacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.world.inventory.IronpawForgeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class IronpawForgeScreen extends AbstractContainerScreen<IronpawForgeMenu> {

    private static final ResourceLocation TEX =
            new ResourceLocation("minecraft", "textures/gui/container/anvil.png");

    private int hoverBtn = -1;

    public IronpawForgeScreen(IronpawForgeMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        CocoJennaUi.drawPanel(g, x, y, imageWidth, imageHeight);
        g.blit(TEX, x + 6, y + 22, 0, 0, imageWidth - 12, 56, imageWidth, 56);

        ItemStack weapon = menu.getBlockEntity().getItem(0);
        if (!weapon.isEmpty()) {
            int lvl = WeaponEnhanceHelper.getLevel(weapon);
            g.drawString(font, Component.translatable("forge.cocojenna.current_level", lvl),
                    x + 8, y + 8, 0xFFD700, false);
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partial);

        int bx = leftPos + 8;
        int by = topPos + 56;
        int bw = 72;
        int bh = 16;
        int gap = 4;
        String[] keys = {
                "forge.cocojenna.enhance_button",
                "forge.cocojenna.repair_button",
                "forge.cocojenna.repair_bone_button",
                "forge.cocojenna.buy_unsheath_button"
        };
        hoverBtn = -1;
        for (int i = 0; i < keys.length; i++) {
            int row = i / 2;
            int col = i % 2;
            int x = bx + col * (bw + gap);
            int y = by + row * (bh + gap);
            boolean hover = mouseX >= x && mouseX < x + bw && mouseY >= y && mouseY < y + bh;
            if (hover) hoverBtn = i;
            CocoJennaUi.drawButton(g, font, x, y, bw, bh,
                    Component.translatable(keys[i]), hover, true, true);
        }
        renderTooltip(g, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && hoverBtn >= 0) {
            int action = switch (hoverBtn) {
                case 0 -> IronpawForgeActionPacket.ENHANCE;
                case 1 -> IronpawForgeActionPacket.REPAIR;
                case 2 -> IronpawForgeActionPacket.REPAIR_BONES;
                default -> IronpawForgeActionPacket.BUY_UNSHEATH;
            };
            ModNetwork.CHANNEL.sendToServer(
                    new IronpawForgeActionPacket(menu.getBlockEntity().getBlockPos(), action));
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }
}
