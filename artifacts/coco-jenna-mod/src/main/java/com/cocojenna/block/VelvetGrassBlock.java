package com.cocojenna.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

/** 絨毛草 — 踩踏時散落絨毛粒子. */
public class VelvetGrassBlock extends BushBlock {

    public VelvetGrassBlock(Properties props) {
        super(props);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && level instanceof ServerLevel sl) {
            if (entity.tickCount % 4 == 0) {
                sl.sendParticles(ParticleTypes.FALLING_SPORE_BLOSSOM,
                        pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5,
                        2, 0.25, 0.1, 0.25, 0.01);
            }
        }
        super.stepOn(level, pos, state, entity);
    }
}
