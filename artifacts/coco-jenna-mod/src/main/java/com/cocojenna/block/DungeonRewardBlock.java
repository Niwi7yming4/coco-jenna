package com.cocojenna.block;

import com.cocojenna.exploration.DungeonRegistry;
import com.cocojenna.exploration.DungeonRewardHelper;
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

/** 地牢獎勵台 — 互動結算通關（設計書 3.2）. */
public class DungeonRewardBlock extends Block {

    public static final IntegerProperty DUNGEON_ID = IntegerProperty.create("dungeon", 0, 9);

    public DungeonRewardBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(DUNGEON_ID, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DUNGEON_ID);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            int idx = state.getValue(DUNGEON_ID);
            String id = com.cocojenna.world.DungeonGenerators.idAt(idx);
            if (!id.isEmpty()) {
                DungeonRegistry.get(id).ifPresent(def -> {
                    ExplorationManager.clearDungeon(sp, id);
                    DungeonRewardHelper.grant(sp, def.rewardKey());
                });
                level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
