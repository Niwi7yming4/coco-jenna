package com.cocojenna.block.entity;

import com.cocojenna.world.recipe.ProcessingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class AbstractProcessingBlockEntity extends BaseContainerBlockEntity {

    protected static final int SLOT_INPUT = 0;
    protected static final int SLOT_EXTRA = 1;
    protected static final int SLOT_FUEL = 2;
    protected static final int SLOT_OUTPUT = 3;
    protected static final int SLOT_COUNT = 4;

    protected final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> fuel;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
                case 2 -> fuel = value;
                default -> {}
            }
        }

        @Override
        public int getCount() { return 3; }
    };

    protected int progress;
    protected int maxProgress;
    protected int fuel;

    protected AbstractProcessingBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type,
            BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected abstract Component getDefaultName();

    protected abstract BiFunction<ItemStack, ItemStack, Optional<ProcessingRecipe>> recipeLookup();

    protected abstract boolean usesFuel();

    public static void serverTick(Level level, BlockPos pos, BlockState state,
            AbstractProcessingBlockEntity entity) {
        if (level.isClientSide) return;

        boolean wasWorking = entity.isWorking();
        if (entity.usesFuel() && entity.fuel <= 0) {
            entity.tryConsumeFuel();
        }

        if (entity.canProcess()) {
            if (entity.usesFuel() && entity.fuel > 0) entity.fuel--;
            entity.progress++;
            if (entity.progress >= entity.maxProgress) {
                entity.finishRecipe();
                entity.progress = 0;
            }
        } else {
            entity.progress = 0;
        }

        if (wasWorking != entity.isWorking()) {
            setChanged(level, pos, state);
        }
    }

    private boolean isWorking() {
        return progress > 0;
    }

    private void tryConsumeFuel() {
        ItemStack fuelStack = items.get(SLOT_FUEL);
        if (!fuelStack.isEmpty() && ProcessingRecipe.isFuel(fuelStack)) {
            fuel = 160;
            fuelStack.shrink(1);
        }
    }

    private boolean canProcess() {
        if (items.get(SLOT_OUTPUT).getCount() >= items.get(SLOT_OUTPUT).getMaxStackSize()) return false;
        Optional<ProcessingRecipe> recipe = recipeLookup().apply(items.get(SLOT_INPUT), items.get(SLOT_EXTRA));
        if (recipe.isEmpty()) return false;
        if (usesFuel() && fuel <= 0 && items.get(SLOT_FUEL).isEmpty()) return false;

        ProcessingRecipe r = recipe.get();
        maxProgress = r.cookTime();
        ItemStack result = r.result().copy();
        if (!items.get(SLOT_OUTPUT).isEmpty()
                && !ItemStack.isSameItemSameTags(items.get(SLOT_OUTPUT), result)) {
            return false;
        }
        return items.get(SLOT_OUTPUT).isEmpty()
                || items.get(SLOT_OUTPUT).getCount() + result.getCount() <= result.getMaxStackSize();
    }

    private void finishRecipe() {
        Optional<ProcessingRecipe> recipeOpt = recipeLookup().apply(items.get(SLOT_INPUT), items.get(SLOT_EXTRA));
        if (recipeOpt.isEmpty()) return;
        ProcessingRecipe recipe = recipeOpt.get();

        items.get(SLOT_INPUT).shrink(1);
        if (recipe.catalyst().isPresent() && !items.get(SLOT_EXTRA).isEmpty()) {
            items.get(SLOT_EXTRA).shrink(1);
        }

        ItemStack result = recipe.result().copy();
        if (items.get(SLOT_OUTPUT).isEmpty()) {
            items.set(SLOT_OUTPUT, result);
        } else {
            items.get(SLOT_OUTPUT).grow(result.getCount());
        }
        afterRecipeFinished(recipe);
        setChanged();
    }

    protected void afterRecipeFinished(ProcessingRecipe recipe) {}

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
        tag.putInt("Fuel", fuel);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.clear();
        ContainerHelper.loadAllItems(tag, items);
        progress = tag.getInt("Progress");
        maxProgress = tag.getInt("MaxProgress");
        fuel = tag.getInt("Fuel");
    }

    @Override
    public int getContainerSize() { return SLOT_COUNT; }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

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
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() { items.clear(); }

    public ContainerData getData() { return data; }
}
