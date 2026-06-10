package com.cocojenna.world.inventory;

import com.cocojenna.block.entity.IronpawForgeBlockEntity;
import com.cocojenna.memforge.WeaponEnhanceHelper;
import com.cocojenna.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class IronpawForgeMenu extends AbstractContainerMenu {

    private final IronpawForgeBlockEntity blockEntity;

    public IronpawForgeMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, playerInv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public IronpawForgeMenu(int id, Inventory playerInv, BlockEntity be) {
        super(ModMenuTypes.IRONPAW_FORGE.get(), id);
        this.blockEntity = (IronpawForgeBlockEntity) be;
        blockEntity.startOpen(playerInv.player);

        addSlot(new Slot(blockEntity, 0, 80, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return WeaponEnhanceHelper.canEnhance(stack);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public IronpawForgeBlockEntity getBlockEntity() { return blockEntity; }

    public void tryEnhance(Player player) {
        if (player.level().isClientSide) return;
        ItemStack weapon = blockEntity.getItem(0);
        if (weapon.isEmpty()) return;
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            WeaponEnhanceHelper.tryEnhance(sp, weapon);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        result = stack.copy();
        if (index == 0) {
            if (!moveItemStackTo(stack, 1, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 1, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        blockEntity.stopOpen(player);
    }
}
