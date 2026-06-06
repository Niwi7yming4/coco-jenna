package com.cocojenna.item;

import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 肉球印章 🐾 — 讓可可或珍奶在物品上蓋上肉球印記。
 * 蓋章的武器獲得 +1 攻擊力，被蓋章的防具獲得 +1 護甲。
 * 每日使用上限 2 次（每隻貓一次）。
 */
public class PawStampItem extends Item {

    public PawStampItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));

        // 找附近的可可和珍奶施加印記
        boolean cocoStamped = level.getEntitiesOfClass(CocoEntity.class,
                player.getBoundingBox().inflate(3.0),
                c -> c.getOwnerUUID() != null && c.getOwnerUUID().equals(player.getUUID()))
                .stream().findFirst().map(coco -> {
                    player.displayClientMessage(
                            Component.translatable("item.cocojenna.paw_stamp.coco_stamped")
                                    .withStyle(ChatFormatting.GOLD), true);
                    return true;
                }).orElse(false);

        boolean jennaStamped = level.getEntitiesOfClass(JennaEntity.class,
                player.getBoundingBox().inflate(3.0),
                j -> j.getOwnerUUID() != null && j.getOwnerUUID().equals(player.getUUID()))
                .stream().findFirst().map(jenna -> {
                    player.displayClientMessage(
                            Component.translatable("item.cocojenna.paw_stamp.jenna_stamped")
                                    .withStyle(ChatFormatting.YELLOW), true);
                    return true;
                }).orElse(false);

        if (cocoStamped || jennaStamped) {
            if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.paw_stamp.tooltip")
                .withStyle(ChatFormatting.GRAY));
    }
}
