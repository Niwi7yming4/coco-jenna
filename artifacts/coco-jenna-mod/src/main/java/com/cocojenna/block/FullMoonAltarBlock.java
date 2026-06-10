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

/** 滿月祭壇 — 滿月時自動激活，玩家在激活狀態下放入材料可觸發特殊儀式。 */
public class FullMoonAltarBlock extends Block {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public FullMoonAltarBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (state.getValue(ACTIVE) && held.getItem() instanceof com.cocojenna.item.DaikataItem
                && player.isShiftKeyDown()) {
            if (level.isClientSide) return InteractionResult.SUCCESS;
            if (player instanceof ServerPlayer sp) {
                com.cocojenna.weapon.WeaponMemoryTaskManager.onDaikataAltarInfusion(sp, held);
                return InteractionResult.CONSUME;
            }
        }
        if (state.getValue(ACTIVE) && WeaponData.isUnsealable(held) && player.isShiftKeyDown()) {
            if (level.isClientSide) return InteractionResult.SUCCESS;
            if (player instanceof ServerPlayer sp) {
                boolean fullMoonFree = level.isNight() && level.getMoonBrightness() >= 1.0f;
                int shards = fullMoonFree ? Integer.MAX_VALUE : countMemoryShards(sp);
                if (WeaponUnsealManager.tryAltarInfusion(sp, held, shards, fullMoonFree)) {
                    com.cocojenna.weapon.WeaponMemoryTaskManager.onFullMoonAltar(sp);
                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.FAIL;
        }
        if (!held.is(ModItems.GLASS_VIAL.get()) || !state.getValue(ACTIVE)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        ItemStack spectrum = new ItemStack(ModItems.FULL_MOON_SPECTRUM.get());
        if (!player.addItem(spectrum)) {
            player.drop(spectrum, false);
        }
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.2F);
        return InteractionResult.CONSUME;
    }

    private static int countMemoryShards(ServerPlayer player) {
        int total = 0;
        for (var s : player.getInventory().items) {
            if (s.is(ModItems.MEMORY_SHARD.get())) total += s.getCount();
        }
        return total;
    }

    @Override
    public void randomTick(BlockState state, net.minecraft.server.level.ServerLevel level,
            BlockPos pos, net.minecraft.util.RandomSource random) {
        boolean isFullMoon = level.getMoonBrightness() >= 1.0f && level.isNight();
        boolean canSeeSky = level.canSeeSky(pos.above());
        boolean shouldBeActive = isFullMoon && canSeeSky;

        if (shouldBeActive != state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, shouldBeActive), 3);
            if (shouldBeActive) {
                level.playSound(null, pos, com.cocojenna.init.ModSounds.WORLD_FULL_MOON_FESTIVAL.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }
}
