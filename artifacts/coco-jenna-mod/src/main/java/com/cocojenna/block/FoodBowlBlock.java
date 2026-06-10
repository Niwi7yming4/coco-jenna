package com.cocojenna.block;

import com.cocojenna.block.entity.FoodBowlBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

public class FoodBowlBlock extends BaseEntityBlock {

    public FoodBowlBlock(Properties props) { super(props); }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FoodBowlBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (!(level.getBlockEntity(pos) instanceof FoodBowlBlockEntity bowl)) {
            return InteractionResult.PASS;
        }

        if (!held.isEmpty() && bowl.tryInsert(held)) {
            if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer sp) {
                com.cocojenna.quest.FirstCryQuestManager.onFoodBowlFilled(sp);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (held.isEmpty() && bowl.hasFood()) {
            ItemStack food = bowl.takeFood();
            if (!player.addItem(food)) player.drop(food, false);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (bowl.hasFood()) {
            player.displayClientMessage(
                    Component.translatable("block.cocojenna.food_bowl.has_food", bowl.getFood().getHoverName()),
                    true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
