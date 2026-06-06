package com.cocojenna.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/**
 * 木天蓼炸彈 — 投擲後爆炸，對貓類敵人造成混亂效果（5 秒）。
 * 對玩家無傷害。爆炸範圍 3 格。
 */
public class SilvervineBombItem extends Item {

    public SilvervineBombItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        level.playSound(null, player.blockPosition(), SoundEvents.SNOWBALL_THROW,
                SoundSource.PLAYERS, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!level.isClientSide) {
            // 找爆炸點（玩家前方 8 格）
            var lookVec = player.getLookAngle();
            double ex = player.getX() + lookVec.x * 8;
            double ey = player.getY() + lookVec.y * 8 + 1.5;
            double ez = player.getZ() + lookVec.z * 8;

            // 範圍內的貓類敵人獲得混亂效果
            level.getEntitiesOfClass(LivingEntity.class,
                    new net.minecraft.world.phys.AABB(ex - 3, ey - 3, ez - 3, ex + 3, ey + 3, ez + 3),
                    e -> !(e instanceof Player))
            .forEach(e -> e.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.CONFUSION, 100, 1, false, true)));

            // 粒子效果
            if (level instanceof net.minecraft.server.level.ServerLevel sl) {
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                        ex, ey, ez, 20, 1.5, 1.5, 1.5, 0.1);
            }

            if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}
