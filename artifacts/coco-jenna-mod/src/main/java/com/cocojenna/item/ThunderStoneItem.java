package com.cocojenna.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/**
 * 雷石 — 投擲後在落點呼叫閃電，觸電範圍 2 格，造成 8 傷害並短暫麻痺。
 * 對水中目標效果翻倍。
 */
public class ThunderStoneItem extends Item {

    public ThunderStoneItem(Properties props) {
        super(props.stacksTo(4));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            var look = player.getLookAngle();
            double tx = player.getX() + look.x * 10;
            double ty = player.getY() + look.y * 10;
            double tz = player.getZ() + look.z * 10;

            // 召喚閃電
            net.minecraft.world.entity.LightningBolt bolt =
                    net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(level);
            if (bolt != null) {
                bolt.moveTo(tx, ty, tz);
                bolt.setVisualOnly(false);
                level.addFreshEntity(bolt);
            }

            // 麻痺效果
            level.getEntitiesOfClass(LivingEntity.class,
                    new net.minecraft.world.phys.AABB(tx - 2, ty - 2, tz - 2, tx + 2, ty + 2, tz + 2),
                    e -> !(e instanceof Player))
            .forEach(e -> e.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 4, false, true)));

            if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}
