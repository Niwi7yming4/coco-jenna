package com.cocojenna.block.entity;

import com.cocojenna.init.ModBlockEntities;
import com.cocojenna.init.ModItems;
import com.cocojenna.sequence.HiddenSequenceTriggers;
import com.cocojenna.world.inventory.DistillerMenu;
import com.cocojenna.world.recipe.DistillerRecipes;
import com.cocojenna.world.recipe.ProcessingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.BiFunction;

public class DistillerBlockEntity extends AbstractProcessingBlockEntity {

    public static void serverTick(Level level, BlockPos pos, BlockState state, DistillerBlockEntity entity) {
        AbstractProcessingBlockEntity.serverTick(level, pos, state, entity);
    }

    public DistillerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISTILLER.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.cocojenna.distiller");
    }

    @Override
    protected BiFunction<ItemStack, ItemStack, Optional<ProcessingRecipe>> recipeLookup() {
        return DistillerRecipes::find;
    }

    @Override
    protected boolean usesFuel() { return false; }

    @Override
    protected void afterRecipeFinished(ProcessingRecipe recipe) {
        if (level == null || level.isClientSide) return;
        if (!recipe.result().is(ModItems.HIBISCUS_TEAR.get())) return;
        AABB area = new AABB(worldPosition).inflate(16);
        for (ServerPlayer sp : level.getEntitiesOfClass(ServerPlayer.class, area)) {
            HiddenSequenceTriggers.onDistillHibiscusTear(sp);
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new DistillerMenu(id, inventory, this);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return createMenu(id, playerInventory);
    }
}
