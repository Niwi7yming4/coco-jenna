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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/** 古代石碑 — 右鍵閱讀傳說（設計書 2.1）. */
public class AncientStoneTabletBlock extends Block {

    public static final IntegerProperty LORE = IntegerProperty.create("lore", 0, 31);

    public AncientStoneTabletBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(LORE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LORE);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            int lore = state.getValue(LORE);
            ExplorationManager.discoverLore(sp, lore);
            com.cocojenna.world.firstcry.FirstCryHiddenInteractionHandler.onTabletRead(sp, lore);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
