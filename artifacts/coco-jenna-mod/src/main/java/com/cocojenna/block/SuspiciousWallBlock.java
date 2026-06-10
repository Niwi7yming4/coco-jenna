package com.cocojenna.block;

import com.cocojenna.exploration.ExplorationGuideManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** 可疑牆壁 — 可可危險感知或珍奶好奇心可揭露隱藏通道（設計書 5.1）. */
public class SuspiciousWallBlock extends Block {

    public SuspiciousWallBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            if (ExplorationGuideManager.canRevealHidden(sp, pos) || sp.isShiftKeyDown()) {
                reveal(level, pos);
                ExplorationGuideManager.onHiddenRevealed(sp, pos);
                com.cocojenna.world.firstcry.FirstCryHiddenInteractionHandler.onSuspiciousWall(sp, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static void reveal(Level level, BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
    }
}
