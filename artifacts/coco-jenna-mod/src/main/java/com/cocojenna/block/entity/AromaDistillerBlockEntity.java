package com.cocojenna.block.entity;

import com.cocojenna.init.ModBlockEntities;
import com.cocojenna.world.inventory.AromaDistillerMenu;
import com.cocojenna.world.recipe.AromaDistillerRecipes;
import com.cocojenna.world.recipe.ProcessingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.BiFunction;

public class AromaDistillerBlockEntity extends AbstractProcessingBlockEntity {

    public static void serverTick(Level level, BlockPos pos, BlockState state, AromaDistillerBlockEntity entity) {
        AbstractProcessingBlockEntity.serverTick(level, pos, state, entity);
    }

    public AromaDistillerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AROMA_DISTILLER.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.cocojenna.aroma_distiller");
    }

    @Override
    protected BiFunction<ItemStack, ItemStack, Optional<ProcessingRecipe>> recipeLookup() {
        return AromaDistillerRecipes::find;
    }

    @Override
    protected boolean usesFuel() { return true; }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new AromaDistillerMenu(id, inventory, this);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return createMenu(id, playerInventory);
    }
}
