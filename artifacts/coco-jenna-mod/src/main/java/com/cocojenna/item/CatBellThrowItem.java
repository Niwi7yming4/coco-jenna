package com.cocojenna.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** 投擲版貓鈴鐺 — 落地產生 3 秒持續鈴聲，分散範圍 5 格內敵人的注意力 */
public class CatBellThrowItem extends Item {

    public CatBellThrowItem(Properties props) {
        super(props.stacksTo(8));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_BELL.value(),
                SoundSource.PLAYERS, 1.0f, 1.2f);

        if (!level.isClientSide) {
            var look = player.getLookAngle();
            double tx = player.getX() + look.x * 6;
            double ty = player.getY() + look.y * 6;
            double tz = player.getZ() + look.z * 6;

            // 分散敵人注意力（讓他們轉向鈴鐺位置）
            level.getEntitiesOfClass(LivingEntity.class,
                    new net.minecraft.world.phys.AABB(tx - 5, ty - 5, tz - 5, tx + 5, ty + 5, tz + 5),
                    e -> !(e instanceof Player) && e.isAlive())
            .forEach(e -> e.lookAt(net.minecraft.world.entity.EntityAnchorArgument.Anchor.EYES,
                    new net.minecraft.world.phys.Vec3(tx, ty, tz)));

            if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}
