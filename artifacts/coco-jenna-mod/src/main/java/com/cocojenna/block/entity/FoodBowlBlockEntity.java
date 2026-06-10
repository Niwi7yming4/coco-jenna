package com.cocojenna.block.entity;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class FoodBowlBlockEntity extends BlockEntity {

    private static final ResourceLocation CAT_FOOD_TAG =
            new ResourceLocation(CocoJennaMod.MOD_ID, "cat_food");

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    public FoodBowlBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FOOD_BOWL.get(), pos, state);
    }

    public boolean tryInsert(ItemStack stack) {
        if (!items.get(0).isEmpty()) return false;
        if (!stack.is(ForgeRegistries.ITEMS.tags().createTagKey(CAT_FOOD_TAG))) return false;
        items.set(0, stack.split(1));
        setChanged();
        return true;
    }

    public ItemStack takeFood() {
        if (items.get(0).isEmpty()) return ItemStack.EMPTY;
        ItemStack taken = items.get(0).copy();
        items.set(0, ItemStack.EMPTY);
        setChanged();
        return taken;
    }

    public boolean hasFood() { return !items.get(0).isEmpty(); }

    public ItemStack getFood() { return items.get(0); }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items, true);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.clear();
        ContainerHelper.loadAllItems(tag, items);
    }

    public boolean stillValid(Player player) {
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= 64.0;
    }
}
