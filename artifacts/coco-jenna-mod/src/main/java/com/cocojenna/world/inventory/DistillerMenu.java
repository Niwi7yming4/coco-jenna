package com.cocojenna.world.inventory;

import com.cocojenna.block.entity.AbstractProcessingBlockEntity;
import com.cocojenna.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
public class DistillerMenu extends AbstractContainerMenu {

    private final AbstractProcessingBlockEntity blockEntity;
    private final ContainerData data;

    public DistillerMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, playerInv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public DistillerMenu(int id, Inventory playerInv, BlockEntity be) {
        super(ModMenuTypes.DISTILLER.get(), id);
        this.blockEntity = (AbstractProcessingBlockEntity) be;
        this.data = blockEntity.getData();
        checkContainerSize(blockEntity, 4);
        blockEntity.startOpen(playerInv.player);
        addDataSlots(data);

        addSlot(new Slot(blockEntity, 0, 56, 17));
        addSlot(new Slot(blockEntity, 1, 56, 53));
        addSlot(new Slot(blockEntity, 2, 8, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });
        addSlot(new Slot(blockEntity, 3, 116, 35));

        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < 4) {
                if (!moveItemStackTo(stack, 4, slots.size(), true)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stack, 0, 2, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }

    public int getProgress() { return data.get(0); }
    public int getMaxProgress() { return Math.max(1, data.get(1)); }
}
