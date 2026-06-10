package com.cocojenna.block;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** 紙箱方塊 — 屋頂連接與結構支撐判定. */
public class CardboardStructuralBlock extends Block {

    private final boolean reinforced;

    public CardboardStructuralBlock(boolean reinforced, Properties props) {
        super(props);
        this.reinforced = reinforced;
    }

    public boolean isReinforced() {
        return reinforced;
    }

    public static boolean hasRoofSupport(Level level, BlockPos pos) {
        int horizontal = 0;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos n = pos.relative(dir);
            if (isCardboard(level.getBlockState(n))) horizontal++;
        }
        if (horizontal >= 2) return true;
        BlockPos above = pos.above();
        if (isCardboard(level.getBlockState(above))) return true;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(dir)).is(ModBlocks.ROPE_NET.get())) return true;
        }
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    public static boolean isCardboard(BlockState state) {
        return state.is(ModBlocks.CARDBOARD_BLOCK.get()) || state.is(ModBlocks.REINFORCED_CARDBOARD.get());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        if (level.isClientSide || reinforced) return;
        if (!hasRoofSupport(level, pos) && !hasVerticalSupport(level, pos, 0)) {
            level.destroyBlock(pos, true);
        }
    }

    private static boolean hasVerticalSupport(Level level, BlockPos pos, int depth) {
        if (depth > 6) return false;
        BlockPos below = pos.below();
        BlockState under = level.getBlockState(below);
        if (under.isFaceSturdy(level, below, Direction.UP)) return true;
        if (under.is(ModBlocks.ROPE_NET.get())) return true;
        if (isCardboard(under)) return hasVerticalSupport(level, below, depth + 1);
        return false;
    }

    public static void collapseUnsupported(ServerLevel level, BlockPos pos) {
        if (!isCardboard(level.getBlockState(pos))) return;
        if (hasRoofSupport(level, pos) || hasVerticalSupport(level, pos, 0)) return;
        level.destroyBlock(pos, true);
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            collapseUnsupported(level, pos.relative(dir));
        }
        collapseUnsupported(level, pos.above());
    }
}
