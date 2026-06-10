package com.cocojenna.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

/**
 * 黑泥 (Black Mud) — 侵蝕方塊。
 * 踩上去導致緩速，並隨時間擴散到相鄰方塊。
 */
public class BlackMudBlock extends Block {

    public BlackMudBlock(Properties props) { super(props); }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity living && !level.isClientSide) {
            // 施加黑泥第一階段效果
            living.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    com.cocojenna.init.ModEffects.BLACK_MUD_STAGE1.get(), 100, 0, false, true));
            // 緩速
            living.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, false));
        }
    }

    /** 黑泥蔓延：每 60 tick 嘗試擴散到相鄰方塊 */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (random.nextFloat() < 0.05f) {
            BlockPos target = pos.offset(
                    random.nextIntBetweenInclusive(-1, 1),
                    0,
                    random.nextIntBetweenInclusive(-1, 1));
            BlockState neighborState = level.getBlockState(target);
            // 只侵蝕普通土地（不侵蝕石頭等）
            if (neighborState.is(net.minecraft.tags.BlockTags.DIRT)) {
                level.setBlock(target, defaultBlockState(), 3);
                level.playSound(null, target, com.cocojenna.init.ModSounds.WORLD_BLACK_MUD_SPREAD.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.3f, 0.8f);
            }
        }
    }
}
