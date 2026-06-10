package com.cocojenna.item;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.sequence.MoonCrossroadsManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** 在月光祭壇使用以重置源力選擇. */
public class ForceResetScrollItem extends Item {

    public ForceResetScrollItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockState state = level.getBlockState(ctx.getClickedPos());
        if (!state.is(ModBlocks.FULL_MOON_ALTAR.get())
                && !state.is(ModBlocks.MOON_TRIAL_ALTAR.get())) {
            return InteractionResult.PASS;
        }
        Player player = ctx.getPlayer();
        if (level.isClientSide || !(player instanceof net.minecraft.server.level.ServerPlayer sp)) {
            return InteractionResult.SUCCESS;
        }
        if (MoonCrossroadsManager.tryResetForce(sp)) {
            if (!player.getAbilities().instabuild) {
                ctx.getItemInHand().shrink(1);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("force.cocojenna.reset.need_altar"), true);
        return InteractionResultHolder.fail(stack);
    }
}
