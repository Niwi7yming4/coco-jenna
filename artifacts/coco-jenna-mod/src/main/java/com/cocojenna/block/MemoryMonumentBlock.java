package com.cocojenna.block;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.quest.FirstCryQuestManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** 記憶紀念碑 — 初啼村村長對話點；鑲嵌記憶碎片後觸發初晴事件。 */
public class MemoryMonumentBlock extends Block {

    public MemoryMonumentBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer sp
                && state.is(ModBlocks.MEMORY_MONUMENT_BASE.get())) {
            FirstCryQuestManager.onElderTalk(sp);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
