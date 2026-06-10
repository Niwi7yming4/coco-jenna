package com.cocojenna.block;

import com.cocojenna.exploration.DungeonPuzzleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** 謎題提示石碑 — 可可／珍奶協助解讀序列（設計書 3.3）. */
public class DungeonPuzzleHintBlock extends Block {

    public DungeonPuzzleHintBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            DungeonPuzzleManager.tryRevealHint(sp, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
