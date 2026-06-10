package com.cocojenna.block;

import com.cocojenna.block.entity.SealPedestalBlockEntity;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class SealPedestalBlock extends BaseEntityBlock {

    public SealPedestalBlock(Properties props) { super(props); }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SealPedestalBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof SealPedestalBlockEntity pedestal)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);

        if (held.is(ModItems.HOLY_WATER.get()) || held.is(ModItems.HIBISCUS_TEAR.get())) {
            if (pedestal.tryRevive(player) && !player.isCreative()) held.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!held.isEmpty() && pedestal.placeSeal(held)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (held.isEmpty()) {
            ItemStack seal = pedestal.removeSeal();
            if (!seal.isEmpty() && !player.addItem(seal)) player.drop(seal, false);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
