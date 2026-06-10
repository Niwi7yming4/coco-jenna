package com.cocojenna.block;

import com.cocojenna.init.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;

/**
 * 黑泥侵蝕分級方塊（設計書 0=淨化 … 4=深淵化）.
 * 1=沾染 2=蔓延 3=吞噬 4=深淵化
 */
public class ErosionBlackMudBlock extends BlackMudBlock {

    private final int erosionTier;

    public ErosionBlackMudBlock(int erosionTier, Properties props) {
        super(props);
        this.erosionTier = erosionTier;
    }

    public int erosionTier() { return erosionTier; }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity living) || level.isClientSide) return;

        int slowAmp = switch (erosionTier) {
            case 1 -> 0;
            case 2 -> 1;
            case 3 -> 1;
            default -> 2;
        };
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, slowAmp, false, false));

        if (erosionTier >= 1) {
            living.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE1.get(), 120, 0, false, true));
        }
        if (erosionTier >= 3) {
            living.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE2.get(), 100, 0, false, true));
        }
        if (erosionTier >= 4 && level.getRandom().nextFloat() < 0.2f) {
            living.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE3.get(), 80, 0, false, true));
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        float chance = switch (erosionTier) {
            case 1 -> 0.02f;
            case 2 -> 0.05f;
            case 3 -> 0.08f;
            default -> 0.12f;
        };
        if (random.nextFloat() >= chance) return;

        BlockPos target = pos.offset(
                random.nextIntBetweenInclusive(-1, 1), 0,
                random.nextIntBetweenInclusive(-1, 1));
        BlockState neighborState = level.getBlockState(target);
        if (!neighborState.is(BlockTags.DIRT)) return;

        int spreadTier = Math.max(1, erosionTier - 1);
        level.setBlock(target, com.cocojenna.blackmud.BlackMudBlocks.blockStateForStage(spreadTier), 3);
        level.playSound(null, target, com.cocojenna.init.ModSounds.WORLD_BLACK_MUD_SPREAD.get(),
                net.minecraft.sounds.SoundSource.BLOCKS, 0.3f, 0.8f);
    }
}
