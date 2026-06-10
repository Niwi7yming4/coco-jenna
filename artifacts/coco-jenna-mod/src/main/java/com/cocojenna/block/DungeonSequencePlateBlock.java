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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/** 地牢序列鎖踏板 — 按正確順序啟動機關（設計書 3.3）. */
public class DungeonSequencePlateBlock extends Block {

    public static final IntegerProperty SYMBOL = IntegerProperty.create("symbol", 0, 3);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public DungeonSequencePlateBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(SYMBOL, 0).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SYMBOL, LIT);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            DungeonPuzzleManager.onPlatePressed(level, sp, pos, state.getValue(SYMBOL));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
