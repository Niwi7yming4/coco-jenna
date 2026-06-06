package com.cocojenna.block;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

/** 滿月祭壇 — 滿月時自動激活，玩家在激活狀態下放入材料可觸發特殊儀式。 */
public class FullMoonAltarBlock extends Block {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public FullMoonAltarBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public void randomTick(BlockState state, net.minecraft.server.level.ServerLevel level,
            BlockPos pos, net.minecraft.util.RandomSource random) {
        boolean isFullMoon = level.getMoonBrightness() >= 1.0f && level.isNight();
        boolean canSeeSky = level.canSeeSky(pos.above());
        boolean shouldBeActive = isFullMoon && canSeeSky;

        if (shouldBeActive != state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, shouldBeActive), 3);
            if (shouldBeActive) {
                level.playSound(null, pos, com.cocojenna.init.ModSounds.WORLD_FULL_MOON_FESTIVAL.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }
}
