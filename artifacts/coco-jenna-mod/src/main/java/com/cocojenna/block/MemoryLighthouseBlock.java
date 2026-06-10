package com.cocojenna.block;

import com.cocojenna.blackmud.BlackMudCorruptionManager;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** 記憶燈塔 — 每日淨化周圍 32 格黑泥. */
public class MemoryLighthouseBlock extends Block {

    public MemoryLighthouseBlock(Properties props) {
        super(props);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM) || random.nextInt(2400) != 0) return;
        BlackMudCorruptionManager.purifyRegion(level, pos, 32, null);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }
}
