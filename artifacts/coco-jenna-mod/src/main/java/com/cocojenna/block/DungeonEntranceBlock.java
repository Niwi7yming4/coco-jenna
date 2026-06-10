package com.cocojenna.block;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.exploration.DungeonRegistry;
import com.cocojenna.exploration.ExplorationManager;
import com.cocojenna.world.DungeonGenerators;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

/** 地牢入口 — 生成結構並傳送玩家（設計書 3.1）. */
public class DungeonEntranceBlock extends Block {

    public static final IntegerProperty DUNGEON = IntegerProperty.create("dungeon", 0, 9);
    public static final BooleanProperty CLEARED = BooleanProperty.create("cleared");

    public DungeonEntranceBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(DUNGEON, 0).setValue(CLEARED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DUNGEON, CLEARED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer sp && level instanceof ServerLevel sl) {
            int idx = state.getValue(DUNGEON);
            String id = DungeonGenerators.idAt(idx);
            if (id.isEmpty()) return InteractionResult.PASS;

            boolean cleared = state.getValue(CLEARED);
            int flag = DungeonRegistry.flag(id);
            if (flag != 0 && ModCapabilities.getOrDefault(sp).hasDungeonCleared(flag)) {
                cleared = true;
                if (!state.getValue(CLEARED)) {
                    level.setBlock(pos, state.setValue(CLEARED, true), 2);
                }
            }

            if (cleared) {
                player.displayClientMessage(
                        Component.translatable("explore.cocojenna.dungeon.cleared_entrance",
                                Component.translatable("explore.cocojenna.dungeon.name." + id)), true);
            } else {
                player.displayClientMessage(
                        Component.translatable("explore.cocojenna.dungeon.enter",
                                Component.translatable("explore.cocojenna.dungeon.name." + id)), false);
                ExplorationManager.logExploration(sp, "explore.cocojenna.journal.dungeon",
                        Component.translatable("explore.cocojenna.dungeon.name." + id).getString());
                player.displayClientMessage(Component.translatable("explore.cocojenna.dungeon.hint." + id), true);
            }

            BlockPos dest = DungeonGenerators.ensure(sl, id, sp);
            if (cleared) {
                player.displayClientMessage(
                        Component.translatable("explore.cocojenna.dungeon.boss_absent"), true);
            }
            sp.teleportTo(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
