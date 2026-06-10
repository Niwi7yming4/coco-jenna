package com.cocojenna.block;

import com.cocojenna.undercat.UndercatQuestManager;
import com.cocojenna.undercat.UndercatRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** 地下貓域區域傳送石碑. */
public class UndercatWaystoneBlock extends Block {

    public UndercatWaystoneBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        UndercatRegion nearest = nearest(pos);
        UndercatQuestManager.openWaystoneMenu(sp, nearest);
        return InteractionResult.CONSUME;
    }

    private static UndercatRegion nearest(BlockPos pos) {
        UndercatRegion best = UndercatRegion.CARDBOARD_SLUMS;
        double bestDist = Double.MAX_VALUE;
        for (UndercatRegion region : UndercatRegion.values()) {
            double d = pos.distSqr(region.center);
            if (d < bestDist) {
                bestDist = d;
                best = region;
            }
        }
        return best;
    }
}
