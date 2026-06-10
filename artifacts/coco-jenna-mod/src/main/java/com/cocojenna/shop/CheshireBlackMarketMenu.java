package com.cocojenna.shop;

import com.cocojenna.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CheshireBlackMarketMenu extends AbstractContainerMenu {

    public CheshireBlackMarketMenu(int id, Inventory inv) {
        super(ModMenuTypes.CHESHIRE_BLACK_MARKET.get(), id);
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    public CheshireBlackMarketMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv);
    }

    public void buy(Player player, int offerIndex) {
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            CheshireBlackMarketOffers.tryPurchase(sp, offerIndex);
        }
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new net.minecraft.world.inventory.Slot(inv, col + row * 9 + 9, 8 + col * 18, 148 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int col = 0; col < 9; col++) {
            addSlot(new net.minecraft.world.inventory.Slot(inv, col, 8 + col * 18, 206));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
