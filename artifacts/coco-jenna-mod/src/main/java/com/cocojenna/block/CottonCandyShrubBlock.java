package com.cocojenna.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** 棉花糖灌木 — 右鍵採收甜食（設計書 §2.1）. */
public class CottonCandyShrubBlock extends BushBlock {

    public CottonCandyShrubBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        ItemStack stack = new ItemStack(Items.COOKIE);
        stack.setHoverName(net.minecraft.network.chat.Component.translatable("item.cocojenna.cotton_candy"));
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 0.8f, 1.2f);
        return InteractionResult.CONSUME;
    }
}
