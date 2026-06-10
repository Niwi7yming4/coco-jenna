package com.cocojenna.block;

import com.cocojenna.block.entity.SocketingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/** 鑲嵌台 — 鐵爪鍛造舖 Lv.2（設計書第八章）. */
public class SocketingTableBlock extends BaseMachineBlock<SocketingTableBlockEntity> {

    public SocketingTableBlock(Properties props) {
        super(props, (level, pos) -> {});
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SocketingTableBlockEntity(pos, state);
    }
}
