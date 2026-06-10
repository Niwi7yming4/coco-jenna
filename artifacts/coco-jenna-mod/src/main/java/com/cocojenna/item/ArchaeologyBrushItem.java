package com.cocojenna.item;

import com.cocojenna.block.AncientStoneTabletBlock;
import com.cocojenna.block.MuralFragmentBlock;
import com.cocojenna.exploration.ExplorationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** 考古刷子 — 清除壁畫黑泥／拂去石碑塵土（設計書 2.1 / 7.1）. */
public class ArchaeologyBrushItem extends Item {

    public ArchaeologyBrushItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof MuralFragmentBlock) {
            if (!state.getValue(MuralFragmentBlock.COVERED)) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide) {
                level.setBlock(pos, MuralFragmentBlock.uncover(state), 2);
                playBrush(level, pos);
                if (ctx.getPlayer() instanceof ServerPlayer sp) {
                    ExplorationManager.discoverLore(sp, state.getValue(MuralFragmentBlock.LORE));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (state.getBlock() instanceof AncientStoneTabletBlock) {
            if (!level.isClientSide && ctx.getPlayer() instanceof ServerPlayer sp) {
                playBrush(level, pos);
                ExplorationManager.discoverLore(sp, state.getValue(AncientStoneTabletBlock.LORE));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    private static void playBrush(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 0.8f, 1.1f);
    }
}
