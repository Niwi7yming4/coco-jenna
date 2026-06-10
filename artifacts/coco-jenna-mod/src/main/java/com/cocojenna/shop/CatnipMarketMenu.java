package com.cocojenna.shop;

import com.cocojenna.economy.CatnipMarketManager;
import com.cocojenna.economy.CatnipQuality;
import com.cocojenna.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CatnipMarketMenu extends AbstractContainerMenu {

    private final long marketDay;
    private final int[] prices = new int[3];

    public CatnipMarketMenu(int id, Inventory inv) {
        super(ModMenuTypes.CATNIP_MARKET.get(), id);
        marketDay = 0;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    public CatnipMarketMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        super(ModMenuTypes.CATNIP_MARKET.get(), id);
        marketDay = buf.readLong();
        for (int i = 0; i < 3; i++) prices[i] = buf.readVarInt();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    public static void writePrices(FriendlyByteBuf buf, long day) {
        buf.writeLong(day);
        for (CatnipQuality q : CatnipQuality.values()) {
            buf.writeVarInt(CatnipMarketManager.dailyPrice(q, day));
        }
    }

    public long marketDay() { return marketDay; }
    public int price(CatnipQuality q) { return prices[q.ordinal()]; }

    public void sellOne(Player player, CatnipQuality quality) {
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            CatnipMarketManager.sellOne(sp, quality, marketDay);
        }
    }

    public void sellStack(Player player, CatnipQuality quality) {
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            CatnipMarketManager.sellStack(sp, quality, marketDay);
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
