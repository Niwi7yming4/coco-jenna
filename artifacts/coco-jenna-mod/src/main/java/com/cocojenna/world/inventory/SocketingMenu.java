package com.cocojenna.world.inventory;

import com.cocojenna.block.entity.SocketingTableBlockEntity;
import com.cocojenna.memforge.SocketHelper;
import com.cocojenna.memforge.WeaponEnhanceHelper;
import com.cocojenna.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SocketingMenu extends AbstractContainerMenu {

    private final SocketingTableBlockEntity blockEntity;

    public SocketingMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, playerInv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public SocketingMenu(int id, Inventory playerInv, BlockEntity be) {
        super(ModMenuTypes.SOCKETING_TABLE.get(), id);
        this.blockEntity = (SocketingTableBlockEntity) be;
        blockEntity.startOpen(playerInv.player);

        addSlot(new Slot(blockEntity, 0, 44, 36) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return WeaponEnhanceHelper.canEnhance(stack);
            }
        });
        addSlot(new Slot(blockEntity, 1, 98, 36));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public SocketingTableBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public void trySocket(Player player) {
        if (player.level().isClientSide) return;
        ItemStack weapon = blockEntity.getItem(0);
        ItemStack gem = blockEntity.getItem(1);
        if (weapon.isEmpty() || gem.isEmpty()) return;
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            SocketHelper.trySocket(sp, weapon, gem);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
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
