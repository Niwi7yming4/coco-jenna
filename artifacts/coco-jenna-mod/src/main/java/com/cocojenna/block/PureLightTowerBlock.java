package com.cocojenna.block;

import com.cocojenna.blackmud.BlackMudSavedData;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** 純淨光塔 — 半徑 64 格內阻止黑泥蔓延. */
public class PureLightTowerBlock extends Block {

    public PureLightTowerBlock(Properties props) {
        super(props);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        BlackMudSavedData.get(level).protectRadius(pos, 64);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }
}
