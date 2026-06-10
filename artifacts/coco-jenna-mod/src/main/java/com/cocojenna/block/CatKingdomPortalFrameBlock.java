package com.cocojenna.block;

import com.cocojenna.init.ModItems;
import com.cocojenna.world.portal.CatKingdomPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** 貓之國傳送門框架 — 滿月光譜右鍵點火，類似地獄傳送門。 */
public class CatKingdomPortalFrameBlock extends Block {

    public CatKingdomPortalFrameBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(ModItems.FULL_MOON_SPECTRUM.get())) {
            return InteractionResult.PASS;
        }
        if (CatKingdomPortalShape.tryIgnite(level, pos, player, hand)) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("message.cocojenna.portal_ignited"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide) {
            player.displayClientMessage(
                    Component.translatable("message.cocojenna.portal_invalid"), true);
        }
        return InteractionResult.FAIL;
    }
}
