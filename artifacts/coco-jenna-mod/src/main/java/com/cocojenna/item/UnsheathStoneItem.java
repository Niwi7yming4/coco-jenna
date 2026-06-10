package com.cocojenna.item;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.swordbone.SwordBoneManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/** 卸劍石 — 安全卸下一柄劍骨，武器進入損壞狀態（設計書 1.7）. */
public class UnsheathStoneItem extends Item {

    public UnsheathStoneItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(player instanceof ServerPlayer sp)) return InteractionResultHolder.pass(stack);

        var bond = ModCapabilities.getOrDefault(sp);
        if (bond.getSwordBones().isEmpty()) {
            sp.displayClientMessage(Component.translatable("swordbone.cocojenna.no_bones"), true);
            return InteractionResultHolder.fail(stack);
        }
        int idx = bond.getSwordBones().size() - 1;
        if (SwordBoneManager.unsheath(sp, idx)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("item.cocojenna.unsheath_stone.desc"));
    }
}
