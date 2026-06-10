package com.cocojenna.block;

import com.cocojenna.exploration.ExplorationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/** 壁畫殘片 — 刷子清除黑泥後顯露（設計書 2.1）. */
public class MuralFragmentBlock extends Block {

    public static final IntegerProperty LORE = IntegerProperty.create("lore", 0, 31);
    public static final BooleanProperty COVERED = BooleanProperty.create("covered");

    public MuralFragmentBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(LORE, 0).setValue(COVERED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LORE, COVERED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(COVERED)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            ExplorationManager.discoverLore(sp, state.getValue(LORE));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static BlockState uncover(BlockState state) {
        return state.setValue(COVERED, false);
    }
}
