package com.cocojenna.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public abstract class BaseMachineBlock<T extends BlockEntity> extends BaseEntityBlock {

    private final BiConsumer<Level, BlockPos> ticker;

    protected BaseMachineBlock(Properties props, BiConsumer<Level, BlockPos> ticker) {
        super(props);
        this.ticker = ticker;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof net.minecraft.world.MenuProvider menuProvider) {
                NetworkHooks.openScreen(serverPlayer, menuProvider, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public <E extends BlockEntity> BlockEntityTicker<E> getTicker(Level level, BlockState state,
            BlockEntityType<E> type) {
        if (level.isClientSide) return null;
        return (lvl, p, s, be) -> ticker.accept(lvl, p);
    }
}
