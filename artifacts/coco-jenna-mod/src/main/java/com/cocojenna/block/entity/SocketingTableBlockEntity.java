package com.cocojenna.block.entity;

import com.cocojenna.init.ModBlockEntities;
import com.cocojenna.world.inventory.SocketingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SocketingTableBlockEntity extends BaseContainerBlockEntity {

    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    public SocketingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOCKETING_TABLE.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.cocojenna.socketing_table");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new SocketingMenu(id, inventory, this);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return createMenu(id, playerInventory);
    }

    @Override
    public int getContainerSize() { return 2; }

    @Override
    public boolean isEmpty() { return items.get(0).isEmpty() && items.get(1).isEmpty(); }

    @Override
    public ItemStack getItem(int slot) { return items.get(slot); }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= 64;
    }

    @Override
    public void clearContent() { items.clear(); }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
    }
}
