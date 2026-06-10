package com.cocojenna.block;

import com.cocojenna.block.entity.AromaDistillerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class AromaDistillerBlock extends BaseMachineBlock<AromaDistillerBlockEntity> {

    public AromaDistillerBlock(Properties props) {
        super(props, (level, pos) -> {
            var be = level.getBlockEntity(pos);
            if (be instanceof AromaDistillerBlockEntity distiller) {
                AromaDistillerBlockEntity.serverTick(level, pos, level.getBlockState(pos), distiller);
            }
        });
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AromaDistillerBlockEntity(pos, state);
    }
}
