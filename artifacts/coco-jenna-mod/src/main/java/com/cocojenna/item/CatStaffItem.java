package com.cocojenna.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** 貓之杖 — 右鍵蓄力釋放魔法（線球杖：拉扯／衝擊） */
public class CatStaffItem extends Item {

    private static final int SKILL_COOLDOWN = 40;

    private final String variantId;

    public CatStaffItem(Properties props, String variantId) {
        super(props.stacksTo(1).durability(300));
        this.variantId = variantId;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return WeaponChargeHelper.startCharge(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        return WeaponChargeHelper.useOnBlock(ctx, this);
    }

    @Override
    public int getUseDuration(ItemStack stack) { return WeaponChargeHelper.USE_DURATION; }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return WeaponChargeHelper.bowAnim(); }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        WeaponChargeHelper.tickChargeFeedback(entity, stack,
                WeaponChargeHelper.chargeTicks(stack, remaining));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, net.minecraft.world.entity.LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        int charge = WeaponChargeHelper.chargeTicks(stack, timeLeft);
        if (!WeaponChargeHelper.chargeReady(charge)) {
            WeaponChargeHelper.notifyChargeTooShort(player);
            return;
        }
        if (!WeaponChargeHelper.tryConsumeCooldown(player, this, SKILL_COOLDOWN)) return;

        if (!level.isClientSide) {
            float power = 1f + Math.min(1.5f, (charge - WeaponChargeHelper.MIN_CHARGE_TICKS) / 30f);
            castStaff(player, level, power);
            if (!player.isCreative()) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            }
            WeaponChargeHelper.applyCooldown(player, this, SKILL_COOLDOWN);
        }
        WeaponChargeHelper.playReleaseSound(level, player);
    }

    private void castStaff(Player player, Level level, float power) {
        Vec3 look = player.getLookAngle().normalize();
        double range = 6.0 + power * 2.0;
        AABB box = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.5);

        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, t -> t != player && t.isAlive())) {
            Vec3 pull = player.position().subtract(e.position()).normalize().scale(0.35 + power * 0.15);
            e.setDeltaMovement(pull.x, 0.15, pull.z);
            e.hurt(level.damageSources().magic(), 4f + power * 3f);
            e.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 30, 0));
        }
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, box, ItemEntity::isAlive)) {
            Vec3 pull = player.position().subtract(item.position()).normalize().scale(0.5);
            item.setDeltaMovement(pull.x, 0.2, pull.z);
        }
        if (level instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.sendParticles(ParticleTypes.END_ROD,
                    player.getX() + look.x, player.getY() + 1.2, player.getZ() + look.z,
                    16, 0.3, 0.3, 0.3, 0.05);
        }
    }
}
