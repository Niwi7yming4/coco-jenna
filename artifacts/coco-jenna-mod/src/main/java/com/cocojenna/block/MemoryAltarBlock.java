package com.cocojenna.block;

import com.cocojenna.init.ModItems;
import com.cocojenna.weapon.WeaponData;
import com.cocojenna.weapon.WeaponUnsealManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

/** 記憶祭壇 — 解封 stage 3→4 儀式（Wave 3）. */
public class MemoryAltarBlock extends Block {

    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");

    public MemoryAltarBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(CHARGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(ModItems.MEMORY_SHARD.get()) && !state.getValue(CHARGED)) {
            if (level.isClientSide) return InteractionResult.SUCCESS;
            if (!player.getAbilities().instabuild) held.shrink(3);
            level.setBlock(pos, state.setValue(CHARGED, true), 3);
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.CONSUME;
        }
        if (state.getValue(CHARGED) && WeaponData.isUnsealable(held) && player.isShiftKeyDown()) {
            if (level.isClientSide) return InteractionResult.SUCCESS;
            if (player instanceof ServerPlayer sp) {
                if (WeaponData.getStage(held) == com.cocojenna.weapon.WeaponAwakeningStage.ENLIGHTENED
                        && WeaponUnsealManager.tryAltarInfusion(sp, held, countMemoryShards(sp), false)) {
                    level.setBlock(pos, state.setValue(CHARGED, false), 3);
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.2F);
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    private static int countMemoryShards(ServerPlayer player) {
        int total = 0;
        for (var s : player.getInventory().items) {
            if (s.is(ModItems.MEMORY_SHARD.get())) total += s.getCount();
        }
        return total;
    }
}
