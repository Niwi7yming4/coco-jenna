package com.cocojenna.world.portal;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Detects a vertical cat-kingdom portal frame and fills the interior. */
public final class CatKingdomPortalShape {

    private static final int MIN_WIDTH = 4;
    private static final int MIN_HEIGHT = 5;

    private final List<BlockPos> interior;

    private CatKingdomPortalShape(List<BlockPos> interior) {
        this.interior = interior;
    }

    public static boolean tryIgnite(Level level, BlockPos clickedFrame, @Nullable Player player,
            InteractionHand hand) {
        if (level.isClientSide) {
            return false;
        }

        Optional<CatKingdomPortalShape> shape = find(level, clickedFrame);
        if (shape.isEmpty()) {
            return false;
        }

        for (BlockPos pos : shape.get().interior) {
            level.setBlock(pos, ModBlocks.CAT_KINGDOM_PORTAL.get().defaultBlockState(), 18);
        }

        level.playSound(null, clickedFrame, SoundEvents.END_PORTAL_FRAME_FILL,
                SoundSource.BLOCKS, 1.0F, 1.0F);

        if (player != null && !player.getAbilities().instabuild) {
            ItemStack held = player.getItemInHand(hand);
            if (!held.isEmpty()) {
                held.shrink(1);
            }
        }
        return true;
    }

    public static Optional<CatKingdomPortalShape> find(Level level, BlockPos pos) {
        if (!isFrame(level, pos)) {
            return Optional.empty();
        }
        for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.Z, Direction.Axis.X}) {
            Optional<CatKingdomPortalShape> shape = tryAxis(level, pos, axis);
            if (shape.isPresent()) {
                return shape;
            }
        }
        return Optional.empty();
    }

    private static Optional<CatKingdomPortalShape> tryAxis(Level level, BlockPos pos, Direction.Axis axis) {
        Direction right = axis == Direction.Axis.Z ? Direction.EAST : Direction.SOUTH;

        BlockPos bottomLeft = pos;
        while (isFrame(level, bottomLeft.below())) {
            bottomLeft = bottomLeft.below();
        }
        while (isFrame(level, bottomLeft.relative(right.getOpposite()))) {
            bottomLeft = bottomLeft.relative(right.getOpposite());
        }

        if (!isFrame(level, bottomLeft)) {
            return Optional.empty();
        }

        int width = countFrames(level, bottomLeft, right);
        int height = countFrames(level, bottomLeft, Direction.UP);
        if (width < MIN_WIDTH || height < MIN_HEIGHT) {
            return Optional.empty();
        }

        if (!isFrame(level, bottomLeft.relative(right, width - 1))
                || !isFrame(level, bottomLeft.relative(Direction.UP, height - 1))
                || !isFrame(level, bottomLeft.relative(right, width - 1).relative(Direction.UP, height - 1))) {
            return Optional.empty();
        }

        for (int x = 0; x < width; x++) {
            if (!isFrame(level, bottomLeft.relative(right, x))
                    || !isFrame(level, bottomLeft.relative(right, x).relative(Direction.UP, height - 1))) {
                return Optional.empty();
            }
        }
        for (int y = 1; y < height - 1; y++) {
            if (!isFrame(level, bottomLeft.relative(Direction.UP, y))
                    || !isFrame(level, bottomLeft.relative(right, width - 1).relative(Direction.UP, y))) {
                return Optional.empty();
            }
        }

        List<BlockPos> inner = new ArrayList<>();
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                BlockPos innerPos = bottomLeft.relative(right, x).relative(Direction.UP, y);
                BlockState state = level.getBlockState(innerPos);
                if (!state.isAir() && !state.is(ModBlocks.CAT_KINGDOM_PORTAL.get())) {
                    return Optional.empty();
                }
                inner.add(innerPos);
            }
        }

        return inner.isEmpty() ? Optional.empty() : Optional.of(new CatKingdomPortalShape(inner));
    }

    private static int countFrames(Level level, BlockPos start, Direction direction) {
        int count = 0;
        BlockPos cursor = start;
        while (isFrame(level, cursor)) {
            count++;
            cursor = cursor.relative(direction);
        }
        return count;
    }

    private static boolean isFrame(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get());
    }
}
