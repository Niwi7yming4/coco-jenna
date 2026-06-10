package com.cocojenna.client.gui;

import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SocketWeaponPacket;
import com.cocojenna.world.inventory.SocketingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SocketingScreen extends AbstractContainerScreen<SocketingMenu> {

    private boolean hoverSocket;

    public SocketingScreen(SocketingMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mx, int my) {
        CocoJennaUi.drawMachinePanel(g, font, leftPos, topPos, imageWidth, imageHeight, title);
        g.drawString(font, Component.translatable("socket.cocojenna.weapon_slot"),
                leftPos + 8, topPos + 28, CocoJennaUi.COL_INK_SOFT, false);
        g.drawString(font, Component.translatable("socket.cocojenna.gem_slot"),
                leftPos + 98, topPos + 28, CocoJennaUi.COL_INK_SOFT, false);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        super.render(g, mx, my, partial);

        int bx = leftPos + 52;
        int by = topPos + 58;
        int bw = 72;
        int bh = 18;
        hoverSocket = mx >= bx && mx < bx + bw && my >= by && my < by + bh;
        CocoJennaUi.drawButton(g, font, bx, by, bw, bh,
                Component.translatable("socket.cocojenna.button"), hoverSocket, true, true);
        renderTooltip(g, mx, my);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && hoverSocket) {
            ModNetwork.CHANNEL.sendToServer(new SocketWeaponPacket(
                    menu.getBlockEntity().getBlockPos()));
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }
}
