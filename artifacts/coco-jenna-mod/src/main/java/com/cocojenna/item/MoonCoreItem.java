package com.cocojenna.item;

import com.cocojenna.overworld.MoonCoreManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 月光核心 — 滿月夜或封印地牢觸發月之祝福（設計書 主世界再多點 §6.2）. */
public class MoonCoreItem extends Item {

    public MoonCoreItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            if (MoonCoreManager.tryActivate(sp, false)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResultHolder.consume(stack);
            }
        }
        return InteractionResultHolder.fail(stack);
    }
}
