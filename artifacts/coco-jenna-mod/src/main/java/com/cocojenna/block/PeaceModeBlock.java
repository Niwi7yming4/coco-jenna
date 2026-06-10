package com.cocojenna.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.BiConsumer;

/** 雨後 DLC 互動方塊基底. */
public class PeaceModeBlock extends Block {

    private final BiConsumer<Player, BlockPos> onUse;

    public PeaceModeBlock(Properties props, BiConsumer<Player, BlockPos> onUse) {
        super(props);
        this.onUse = onUse;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            onUse.accept(player, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
