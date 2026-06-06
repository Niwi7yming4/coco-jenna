package com.cocojenna.block;

import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** 朱槿花 — 在曾重傷倒下（HP ≤ 1）的位置會自然生長。 */
public class HibiscusBlock extends FlowerBlock {
    public HibiscusBlock(BlockBehaviour.Properties props) {
        super(MobEffects.REGENERATION, 7, props);
    }
}
