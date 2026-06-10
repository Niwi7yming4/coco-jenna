package com.cocojenna.block;

import com.cocojenna.block.entity.DistillerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class DistillerBlock extends BaseMachineBlock<DistillerBlockEntity> {

    public DistillerBlock(Properties props) {
        super(props, (level, pos) -> {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DistillerBlockEntity distiller) {
                DistillerBlockEntity.serverTick(level, pos, level.getBlockState(pos), distiller);
            }
        });
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DistillerBlockEntity(pos, state);
    }
}
