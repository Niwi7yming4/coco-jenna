package com.cocojenna.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 存檔錨點 — 回溯區域時間一次. */
public class SaveAnchorItem extends Item {

    public SaveAnchorItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        if (player instanceof ServerPlayer sp && level instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.setDayTime(sl.getDayTime() - 24000L);
            int charges = stack.getOrCreateTag().getInt("AnchorCharges");
            if (charges <= 1) {
                stack.shrink(1);
            } else {
                stack.getTag().putInt("AnchorCharges", charges - 1);
            }
            sp.displayClientMessage(Component.translatable("item.cocojenna.save_anchor.used"), true);
        }
        return InteractionResultHolder.consume(stack);
    }
}
