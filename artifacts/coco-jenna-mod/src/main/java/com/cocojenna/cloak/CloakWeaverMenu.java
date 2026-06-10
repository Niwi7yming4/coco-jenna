package com.cocojenna.cloak;

import com.cocojenna.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CloakWeaverMenu extends AbstractContainerMenu {

    public CloakWeaverMenu(int id, Inventory inv) {
        super(ModMenuTypes.CLOAK_WEAVER.get(), id);
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    public CloakWeaverMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv);
    }

    public void craft(Player player, int cloakIndex) {
        if (!(player instanceof net.minecraft.server.level.ServerPlayer sp)) return;
        CloakDefinition.byIndex(cloakIndex).ifPresent(def -> {
            ItemStack result = def.craft(sp);
            if (!result.isEmpty()) {
                if (!sp.addItem(result)) sp.drop(result, false);
                sp.displayClientMessage(Component.translatable("cloak.cocojenna.crafted",
                        Component.translatable("item.cocojenna." + def.itemId)), true);
            } else {
                sp.displayClientMessage(Component.translatable("cloak.cocojenna.missing_materials"), true);
            }
        });
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new net.minecraft.world.inventory.Slot(inv, col + row * 9 + 9, 8 + col * 18, 200 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int col = 0; col < 9; col++) {
            addSlot(new net.minecraft.world.inventory.Slot(inv, col, 8 + col * 18, 258));
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
