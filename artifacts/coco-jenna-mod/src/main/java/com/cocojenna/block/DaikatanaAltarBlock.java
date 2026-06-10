package com.cocojenna.block;

import com.cocojenna.memforge.DaikatanaRitualManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** 祭壇基石 — 右鍵開啟大快刀鍛造儀式面板. */
public class DaikatanaAltarBlock extends Block {

    public DaikatanaAltarBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                               InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer sp) {
            DaikatanaRitualManager.openGui(sp, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
