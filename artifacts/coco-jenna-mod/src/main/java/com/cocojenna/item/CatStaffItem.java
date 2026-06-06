package com.cocojenna.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** 貓之杖基礎類別 — 可蓄力釋放魔法攻擊 */
public class CatStaffItem extends Item {

    private final String variantId;

    public CatStaffItem(Properties props, String variantId) {
        super(props.stacksTo(1).durability(300));
        this.variantId = variantId;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack) { return 72000; }
    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }

    @Override
    public void releaseUsing(ItemStack stack, Level level, net.minecraft.world.entity.LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        int charge = getUseDuration(stack) - timeLeft;
        if (charge < 20) return;

        if (!level.isClientSide) {
            // 線球杖：向前方發射能量球
            var lookVec = player.getLookAngle();
            level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                    player.getBoundingBox().expandTowards(lookVec.scale(8)).inflate(1.5),
                    e -> e != player)
            .forEach(e -> e.hurt(level.damageSources().magic(), 6.0f));

            if (!player.isCreative()) stack.hurtAndBreak(1, player,
                    p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }
    }
}
