package com.cocojenna.block;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 貓床 — 可可和珍奶的睡覺地點。放入絨毛可提升睡眠品質（設計書：功能方塊類）.
 */
public class CatBedBlock extends Block {

    public static final BooleanProperty COZY = BooleanProperty.create("cozy");

    public CatBedBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(COZY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COZY);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(ModItems.VELVET_FUR.get()) || state.getValue(COZY)) {
            if (state.getValue(COZY)) {
                player.displayClientMessage(
                        Component.translatable("block.cocojenna.cat_bed.cozy"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(COZY, true), 3);
            if (!player.isCreative()) {
                held.shrink(1);
            }
            player.displayClientMessage(
                    Component.translatable("block.cocojenna.cat_bed.prepared"), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** True when a cat is resting on this bed block. */
    public static boolean isCatOnBed(Level level, BlockPos bedPos) {
        BlockState state = level.getBlockState(bedPos);
        if (!state.is(ModBlocks.CAT_BED.get())) {
            return false;
        }
        return !level.getEntitiesOfClass(
                com.cocojenna.entity.AbstractCatEntity.class,
                net.minecraft.world.phys.AABB.ofSize(
                        net.minecraft.world.phys.Vec3.atCenterOf(bedPos), 1.2, 0.8, 1.2),
                com.cocojenna.entity.AbstractCatEntity::isSitting).isEmpty();
    }

    public static boolean isCozy(Level level, BlockPos bedPos) {
        return level.getBlockState(bedPos).getValue(COZY);
    }
}
